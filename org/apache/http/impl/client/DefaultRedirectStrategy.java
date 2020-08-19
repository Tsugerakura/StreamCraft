/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.CircularRedirectException;
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.HttpGet;
/*     */ import org.apache.http.client.methods.HttpHead;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.methods.RequestBuilder;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultRedirectStrategy
/*     */   implements RedirectStrategy
/*     */ {
/*  75 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
/*     */ 
/*     */   
/*  83 */   public static final DefaultRedirectStrategy INSTANCE = new DefaultRedirectStrategy();
/*     */   
/*     */   private final String[] redirectMethods;
/*     */   
/*     */   public DefaultRedirectStrategy() {
/*  88 */     this(new String[] { "GET", "HEAD" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultRedirectStrategy(String[] redirectMethods) {
/* 102 */     String[] tmp = (String[])redirectMethods.clone();
/* 103 */     Arrays.sort((Object[])tmp);
/* 104 */     this.redirectMethods = tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 112 */     Args.notNull(request, "HTTP request");
/* 113 */     Args.notNull(response, "HTTP response");
/*     */     
/* 115 */     int statusCode = response.getStatusLine().getStatusCode();
/* 116 */     String method = request.getRequestLine().getMethod();
/* 117 */     Header locationHeader = response.getFirstHeader("location");
/* 118 */     switch (statusCode) {
/*     */       case 302:
/* 120 */         return (isRedirectable(method) && locationHeader != null);
/*     */       case 301:
/*     */       case 307:
/* 123 */         return isRedirectable(method);
/*     */       case 303:
/* 125 */         return true;
/*     */     } 
/* 127 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 135 */     Args.notNull(request, "HTTP request");
/* 136 */     Args.notNull(response, "HTTP response");
/* 137 */     Args.notNull(context, "HTTP context");
/*     */     
/* 139 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */ 
/*     */     
/* 142 */     Header locationHeader = response.getFirstHeader("location");
/* 143 */     if (locationHeader == null)
/*     */     {
/* 145 */       throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
/*     */     }
/*     */ 
/*     */     
/* 149 */     String location = locationHeader.getValue();
/* 150 */     if (this.log.isDebugEnabled()) {
/* 151 */       this.log.debug("Redirect requested to location '" + location + "'");
/*     */     }
/*     */     
/* 154 */     RequestConfig config = clientContext.getRequestConfig();
/*     */     
/* 156 */     URI uri = createLocationURI(location);
/*     */     
/*     */     try {
/* 159 */       if (config.isNormalizeUri()) {
/* 160 */         uri = URIUtils.normalizeSyntax(uri);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 165 */       if (!uri.isAbsolute()) {
/* 166 */         if (!config.isRelativeRedirectsAllowed()) {
/* 167 */           throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
/*     */         }
/*     */ 
/*     */         
/* 171 */         HttpHost target = clientContext.getTargetHost();
/* 172 */         Asserts.notNull(target, "Target host");
/* 173 */         URI requestURI = new URI(request.getRequestLine().getUri());
/* 174 */         URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, config.isNormalizeUri() ? URIUtils.NORMALIZE : URIUtils.NO_FLAGS);
/*     */         
/* 176 */         uri = URIUtils.resolve(absoluteRequestURI, uri);
/*     */       } 
/* 178 */     } catch (URISyntaxException ex) {
/* 179 */       throw new ProtocolException(ex.getMessage(), ex);
/*     */     } 
/*     */     
/* 182 */     RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
/*     */     
/* 184 */     if (redirectLocations == null) {
/* 185 */       redirectLocations = new RedirectLocations();
/* 186 */       context.setAttribute("http.protocol.redirect-locations", redirectLocations);
/*     */     } 
/* 188 */     if (!config.isCircularRedirectsAllowed() && 
/* 189 */       redirectLocations.contains(uri)) {
/* 190 */       throw new CircularRedirectException("Circular redirect to '" + uri + "'");
/*     */     }
/*     */     
/* 193 */     redirectLocations.add(uri);
/* 194 */     return uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected URI createLocationURI(String location) throws ProtocolException {
/*     */     try {
/* 202 */       return new URI(location);
/* 203 */     } catch (URISyntaxException ex) {
/* 204 */       throw new ProtocolException("Invalid redirect URI: " + location, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isRedirectable(String method) {
/* 212 */     return (Arrays.binarySearch((Object[])this.redirectMethods, method) >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 220 */     URI uri = getLocationURI(request, response, context);
/* 221 */     String method = request.getRequestLine().getMethod();
/* 222 */     if (method.equalsIgnoreCase("HEAD"))
/* 223 */       return (HttpUriRequest)new HttpHead(uri); 
/* 224 */     if (method.equalsIgnoreCase("GET")) {
/* 225 */       return (HttpUriRequest)new HttpGet(uri);
/*     */     }
/* 227 */     int status = response.getStatusLine().getStatusCode();
/* 228 */     return (status == 307) ? RequestBuilder.copy(request).setUri(uri).build() : (HttpUriRequest)new HttpGet(uri);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\client\DefaultRedirectStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */