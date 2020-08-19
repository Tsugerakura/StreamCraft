/*     */ package io.sentry.connection;
/*     */ 
/*     */ import io.sentry.environment.SentryEnvironment;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.marshaller.Marshaller;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpConnection
/*     */   extends AbstractConnection
/*     */ {
/*     */   public static final int HTTP_TOO_MANY_REQUESTS = 429;
/*  31 */   private static final Charset UTF_8 = Charset.forName("UTF-8");
/*  32 */   private static final Logger logger = LoggerFactory.getLogger(HttpConnection.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String USER_AGENT = "User-Agent";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String SENTRY_AUTH = "X-Sentry-Auth";
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final int DEFAULT_CONNECTION_TIMEOUT = (int)TimeUnit.SECONDS.toMillis(1L);
/*     */ 
/*     */ 
/*     */   
/*  48 */   private static final int DEFAULT_READ_TIMEOUT = (int)TimeUnit.SECONDS.toMillis(5L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final HostnameVerifier NAIVE_VERIFIER = new HostnameVerifier()
/*     */     {
/*     */       public boolean verify(String hostname, SSLSession sslSession) {
/*  56 */         return true;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final URL sentryUrl;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Proxy proxy;
/*     */ 
/*     */ 
/*     */   
/*     */   private EventSampler eventSampler;
/*     */ 
/*     */ 
/*     */   
/*     */   private Marshaller marshaller;
/*     */ 
/*     */   
/*  78 */   private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
/*     */ 
/*     */ 
/*     */   
/*  82 */   private int readTimeout = DEFAULT_READ_TIMEOUT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bypassSecurity = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection(URL sentryUrl, String publicKey, String secretKey, Proxy proxy, EventSampler eventSampler) {
/* 100 */     super(publicKey, secretKey);
/* 101 */     this.sentryUrl = sentryUrl;
/* 102 */     this.proxy = proxy;
/* 103 */     this.eventSampler = eventSampler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getSentryApiUrl(URI sentryUri, String projectId) {
/*     */     try {
/* 115 */       String url = sentryUri.toString() + "api/" + projectId + "/store/";
/* 116 */       return new URL(url);
/* 117 */     } catch (MalformedURLException e) {
/* 118 */       throw new IllegalArgumentException("Couldn't build a valid URL from the Sentry API.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpURLConnection getConnection() {
/*     */     try {
/*     */       HttpURLConnection connection;
/* 130 */       if (this.proxy != null) {
/* 131 */         connection = (HttpURLConnection)this.sentryUrl.openConnection(this.proxy);
/*     */       } else {
/* 133 */         connection = (HttpURLConnection)this.sentryUrl.openConnection();
/*     */       } 
/*     */       
/* 136 */       if (this.bypassSecurity && connection instanceof HttpsURLConnection) {
/* 137 */         ((HttpsURLConnection)connection).setHostnameVerifier(NAIVE_VERIFIER);
/*     */       }
/* 139 */       connection.setRequestMethod("POST");
/* 140 */       connection.setDoOutput(true);
/* 141 */       connection.setConnectTimeout(this.connectionTimeout);
/* 142 */       connection.setReadTimeout(this.readTimeout);
/* 143 */       connection.setRequestProperty("User-Agent", SentryEnvironment.getSentryName());
/* 144 */       connection.setRequestProperty("X-Sentry-Auth", getAuthHeader());
/*     */       
/* 146 */       if (this.marshaller.getContentType() != null) {
/* 147 */         connection.setRequestProperty("Content-Type", this.marshaller.getContentType());
/*     */       }
/*     */       
/* 150 */       if (this.marshaller.getContentEncoding() != null) {
/* 151 */         connection.setRequestProperty("Content-Encoding", this.marshaller.getContentEncoding());
/*     */       }
/*     */       
/* 154 */       return connection;
/* 155 */     } catch (IOException e) {
/* 156 */       throw new IllegalStateException("Couldn't set up a connection to the Sentry server.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doSend(Event event) throws ConnectionException {
/* 162 */     if (this.eventSampler != null && !this.eventSampler.shouldSendEvent(event)) {
/*     */       return;
/*     */     }
/*     */     
/* 166 */     HttpURLConnection connection = getConnection();
/*     */     try {
/* 168 */       connection.connect();
/* 169 */       OutputStream outputStream = connection.getOutputStream();
/* 170 */       this.marshaller.marshall(event, outputStream);
/* 171 */       outputStream.close();
/* 172 */       connection.getInputStream().close();
/* 173 */     } catch (IOException e) {
/* 174 */       Long retryAfterMs = null;
/* 175 */       String retryAfterHeader = connection.getHeaderField("Retry-After");
/* 176 */       if (retryAfterHeader != null) {
/*     */         
/*     */         try {
/*     */           
/* 180 */           retryAfterMs = Long.valueOf((long)(Double.parseDouble(retryAfterHeader) * 1000.0D));
/*     */         }
/* 182 */         catch (NumberFormatException numberFormatException) {}
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 188 */       Integer responseCode = null;
/*     */       try {
/* 190 */         responseCode = Integer.valueOf(connection.getResponseCode());
/* 191 */         if (responseCode.intValue() == 403) {
/* 192 */           logger.debug("Event '" + event.getId() + "' was rejected by the Sentry server due to a filter."); return;
/*     */         } 
/* 194 */         if (responseCode.intValue() == 429)
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 199 */           throw new TooManyRequestsException("Too many requests to Sentry: https://docs.sentry.io/learn/quotas/", e, retryAfterMs, responseCode);
/*     */         
/*     */         }
/*     */       }
/* 203 */       catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */       
/* 207 */       String errorMessage = null;
/* 208 */       InputStream errorStream = connection.getErrorStream();
/* 209 */       if (errorStream != null) {
/* 210 */         errorMessage = getErrorMessageFromStream(errorStream);
/*     */       }
/* 212 */       if (null == errorMessage || errorMessage.isEmpty()) {
/* 213 */         errorMessage = "An exception occurred while submitting the event to the Sentry server.";
/*     */       }
/*     */       
/* 216 */       throw new ConnectionException(errorMessage, e, retryAfterMs, responseCode);
/*     */     } finally {
/* 218 */       connection.disconnect();
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getErrorMessageFromStream(InputStream errorStream) {
/* 223 */     BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, UTF_8));
/* 224 */     StringBuilder sb = new StringBuilder();
/*     */ 
/*     */     
/*     */     try {
/* 228 */       boolean first = true; String line;
/* 229 */       while ((line = reader.readLine()) != null) {
/* 230 */         if (!first) {
/* 231 */           sb.append("\n");
/*     */         }
/* 233 */         sb.append(line);
/* 234 */         first = false;
/*     */       } 
/* 236 */     } catch (IOException|RuntimeException e2) {
/* 237 */       logger.error("Exception while reading the error message from the connection.", e2);
/*     */     } 
/* 239 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setTimeout(int timeout) {
/* 251 */     this.connectionTimeout = timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectionTimeout(int timeout) {
/* 261 */     this.connectionTimeout = timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(int timeout) {
/* 271 */     this.readTimeout = timeout;
/*     */   }
/*     */   
/*     */   public void setMarshaller(Marshaller marshaller) {
/* 275 */     this.marshaller = marshaller;
/*     */   }
/*     */   
/*     */   public void setBypassSecurity(boolean bypassSecurity) {
/* 279 */     this.bypassSecurity = bypassSecurity;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\HttpConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */