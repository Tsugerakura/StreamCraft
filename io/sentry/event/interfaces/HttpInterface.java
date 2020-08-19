/*     */ package io.sentry.event.interfaces;
/*     */ 
/*     */ import io.sentry.event.helper.BasicRemoteAddressResolver;
/*     */ import io.sentry.event.helper.RemoteAddressResolver;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpInterface
/*     */   implements SentryInterface
/*     */ {
/*     */   public static final String HTTP_INTERFACE = "sentry.interfaces.Http";
/*     */   private final String requestUrl;
/*     */   private final String method;
/*     */   private final Map<String, Collection<String>> parameters;
/*     */   private final String queryString;
/*     */   private final Map<String, String> cookies;
/*     */   private final String remoteAddr;
/*     */   private final String serverName;
/*     */   private final int serverPort;
/*     */   private final String localAddr;
/*     */   private final String localName;
/*     */   private final int localPort;
/*     */   private final String protocol;
/*     */   private final boolean secure;
/*     */   private final boolean asyncStarted;
/*     */   private final String authType;
/*     */   private final String remoteUser;
/*     */   private final Map<String, Collection<String>> headers;
/*     */   private final String body;
/*     */   
/*     */   public HttpInterface(HttpServletRequest request) {
/*  43 */     this(request, (RemoteAddressResolver)new BasicRemoteAddressResolver());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpInterface(HttpServletRequest request, RemoteAddressResolver remoteAddressResolver) {
/*  53 */     this(request, remoteAddressResolver, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpInterface(HttpServletRequest request, RemoteAddressResolver remoteAddressResolver, String body) {
/*  64 */     this.requestUrl = request.getRequestURL().toString();
/*  65 */     this.method = request.getMethod();
/*  66 */     this.parameters = new HashMap<>();
/*  67 */     for (Map.Entry<String, String[]> parameterMapEntry : (Iterable<Map.Entry<String, String[]>>)request.getParameterMap().entrySet()) {
/*  68 */       this.parameters.put(parameterMapEntry.getKey(), Arrays.asList((Object[])parameterMapEntry.getValue()));
/*     */     }
/*  70 */     this.queryString = request.getQueryString();
/*  71 */     if (request.getCookies() != null) {
/*  72 */       this.cookies = new HashMap<>();
/*  73 */       for (Cookie cookie : request.getCookies()) {
/*  74 */         this.cookies.put(cookie.getName(), cookie.getValue());
/*     */       }
/*     */     } else {
/*  77 */       this.cookies = Collections.emptyMap();
/*     */     } 
/*  79 */     this.remoteAddr = remoteAddressResolver.getRemoteAddress(request);
/*  80 */     this.serverName = request.getServerName();
/*  81 */     this.serverPort = request.getServerPort();
/*  82 */     this.localAddr = request.getLocalAddr();
/*  83 */     this.localName = request.getLocalName();
/*  84 */     this.localPort = request.getLocalPort();
/*  85 */     this.protocol = request.getProtocol();
/*  86 */     this.secure = request.isSecure();
/*  87 */     this.asyncStarted = request.isAsyncStarted();
/*  88 */     this.authType = request.getAuthType();
/*  89 */     this.remoteUser = request.getRemoteUser();
/*  90 */     this.headers = new HashMap<>();
/*  91 */     for (String headerName : Collections.list(request.getHeaderNames())) {
/*  92 */       this.headers.put(headerName, Collections.list(request.getHeaders(headerName)));
/*     */     }
/*  94 */     this.body = body;
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
/*     */   public HttpInterface(String requestUrl, String method, Map<String, Collection<String>> parameters, String queryString, Map<String, String> cookies, String remoteAddr, String serverName, int serverPort, String localAddr, String localName, int localPort, String protocol, boolean secure, boolean asyncStarted, String authType, String remoteUser, Map<String, Collection<String>> headers, String body) {
/* 139 */     this.requestUrl = requestUrl;
/* 140 */     this.method = method;
/* 141 */     this.parameters = parameters;
/* 142 */     this.queryString = queryString;
/* 143 */     this.cookies = cookies;
/* 144 */     this.remoteAddr = remoteAddr;
/* 145 */     this.serverName = serverName;
/* 146 */     this.serverPort = serverPort;
/* 147 */     this.localAddr = localAddr;
/* 148 */     this.localName = localName;
/* 149 */     this.localPort = localPort;
/* 150 */     this.protocol = protocol;
/* 151 */     this.secure = secure;
/* 152 */     this.asyncStarted = asyncStarted;
/* 153 */     this.authType = authType;
/* 154 */     this.remoteUser = remoteUser;
/* 155 */     this.headers = headers;
/* 156 */     this.body = body;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getInterfaceName() {
/* 162 */     return "sentry.interfaces.Http";
/*     */   }
/*     */   
/*     */   public String getRequestUrl() {
/* 166 */     return this.requestUrl;
/*     */   }
/*     */   
/*     */   public String getMethod() {
/* 170 */     return this.method;
/*     */   }
/*     */   
/*     */   public Map<String, Collection<String>> getParameters() {
/* 174 */     return Collections.unmodifiableMap(this.parameters);
/*     */   }
/*     */   
/*     */   public String getQueryString() {
/* 178 */     return this.queryString;
/*     */   }
/*     */   
/*     */   public Map<String, String> getCookies() {
/* 182 */     return this.cookies;
/*     */   }
/*     */   
/*     */   public String getRemoteAddr() {
/* 186 */     return this.remoteAddr;
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 190 */     return this.serverName;
/*     */   }
/*     */   
/*     */   public int getServerPort() {
/* 194 */     return this.serverPort;
/*     */   }
/*     */   
/*     */   public String getLocalAddr() {
/* 198 */     return this.localAddr;
/*     */   }
/*     */   
/*     */   public String getLocalName() {
/* 202 */     return this.localName;
/*     */   }
/*     */   
/*     */   public int getLocalPort() {
/* 206 */     return this.localPort;
/*     */   }
/*     */   
/*     */   public String getProtocol() {
/* 210 */     return this.protocol;
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/* 214 */     return this.secure;
/*     */   }
/*     */   
/*     */   public boolean isAsyncStarted() {
/* 218 */     return this.asyncStarted;
/*     */   }
/*     */   
/*     */   public String getAuthType() {
/* 222 */     return this.authType;
/*     */   }
/*     */   
/*     */   public String getRemoteUser() {
/* 226 */     return this.remoteUser;
/*     */   }
/*     */   
/*     */   public String getBody() {
/* 230 */     return this.body;
/*     */   }
/*     */   
/*     */   public Map<String, Collection<String>> getHeaders() {
/* 234 */     return Collections.unmodifiableMap(this.headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 239 */     return "HttpInterface{requestUrl='" + this.requestUrl + '\'' + ", method='" + this.method + '\'' + ", queryString='" + this.queryString + '\'' + ", parameters=" + this.parameters + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 249 */     if (this == o) {
/* 250 */       return true;
/*     */     }
/* 252 */     if (o == null || getClass() != o.getClass()) {
/* 253 */       return false;
/*     */     }
/*     */     
/* 256 */     HttpInterface that = (HttpInterface)o;
/*     */     
/* 258 */     if (this.asyncStarted != that.asyncStarted) {
/* 259 */       return false;
/*     */     }
/* 261 */     if (this.localPort != that.localPort) {
/* 262 */       return false;
/*     */     }
/* 264 */     if (this.secure != that.secure) {
/* 265 */       return false;
/*     */     }
/* 267 */     if (this.serverPort != that.serverPort) {
/* 268 */       return false;
/*     */     }
/* 270 */     if ((this.authType != null) ? !this.authType.equals(that.authType) : (that.authType != null)) {
/* 271 */       return false;
/*     */     }
/* 273 */     if (!this.cookies.equals(that.cookies)) {
/* 274 */       return false;
/*     */     }
/* 276 */     if (!this.headers.equals(that.headers)) {
/* 277 */       return false;
/*     */     }
/* 279 */     if ((this.localAddr != null) ? !this.localAddr.equals(that.localAddr) : (that.localAddr != null)) {
/* 280 */       return false;
/*     */     }
/* 282 */     if ((this.localName != null) ? !this.localName.equals(that.localName) : (that.localName != null)) {
/* 283 */       return false;
/*     */     }
/* 285 */     if ((this.method != null) ? !this.method.equals(that.method) : (that.method != null)) {
/* 286 */       return false;
/*     */     }
/* 288 */     if (!this.parameters.equals(that.parameters)) {
/* 289 */       return false;
/*     */     }
/* 291 */     if ((this.protocol != null) ? !this.protocol.equals(that.protocol) : (that.protocol != null)) {
/* 292 */       return false;
/*     */     }
/* 294 */     if ((this.queryString != null) ? !this.queryString.equals(that.queryString) : (that.queryString != null)) {
/* 295 */       return false;
/*     */     }
/* 297 */     if ((this.remoteAddr != null) ? !this.remoteAddr.equals(that.remoteAddr) : (that.remoteAddr != null)) {
/* 298 */       return false;
/*     */     }
/* 300 */     if ((this.remoteUser != null) ? !this.remoteUser.equals(that.remoteUser) : (that.remoteUser != null)) {
/* 301 */       return false;
/*     */     }
/* 303 */     if (!this.requestUrl.equals(that.requestUrl)) {
/* 304 */       return false;
/*     */     }
/* 306 */     if ((this.serverName != null) ? !this.serverName.equals(that.serverName) : (that.serverName != null)) {
/* 307 */       return false;
/*     */     }
/* 309 */     if ((this.body != null) ? !this.body.equals(that.body) : (that.body != null)) {
/* 310 */       return false;
/*     */     }
/*     */     
/* 313 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 318 */     int result = this.requestUrl.hashCode();
/* 319 */     result = 31 * result + ((this.method != null) ? this.method.hashCode() : 0);
/* 320 */     result = 31 * result + this.parameters.hashCode();
/* 321 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\HttpInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */