/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class DefaultHttpRequest
/*     */   extends DefaultHttpMessage
/*     */   implements HttpRequest
/*     */ {
/*     */   private static final int HASH_CODE_PRIME = 31;
/*     */   private HttpMethod method;
/*     */   private String uri;
/*     */   
/*     */   public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
/*  38 */     this(httpVersion, method, uri, true);
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
/*     */   public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
/*  50 */     super(httpVersion, validateHeaders, false);
/*  51 */     this.method = (HttpMethod)ObjectUtil.checkNotNull(method, "method");
/*  52 */     this.uri = (String)ObjectUtil.checkNotNull(uri, "uri");
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
/*     */   public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, HttpHeaders headers) {
/*  64 */     super(httpVersion, headers);
/*  65 */     this.method = (HttpMethod)ObjectUtil.checkNotNull(method, "method");
/*  66 */     this.uri = (String)ObjectUtil.checkNotNull(uri, "uri");
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpMethod getMethod() {
/*  72 */     return method();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpMethod method() {
/*  77 */     return this.method;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getUri() {
/*  83 */     return uri();
/*     */   }
/*     */ 
/*     */   
/*     */   public String uri() {
/*  88 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRequest setMethod(HttpMethod method) {
/*  93 */     this.method = (HttpMethod)ObjectUtil.checkNotNull(method, "method");
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRequest setUri(String uri) {
/*  99 */     this.uri = (String)ObjectUtil.checkNotNull(uri, "uri");
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRequest setProtocolVersion(HttpVersion version) {
/* 105 */     super.setProtocolVersion(version);
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 111 */     int result = 1;
/* 112 */     result = 31 * result + this.method.hashCode();
/* 113 */     result = 31 * result + this.uri.hashCode();
/* 114 */     result = 31 * result + super.hashCode();
/* 115 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 120 */     if (!(o instanceof DefaultHttpRequest)) {
/* 121 */       return false;
/*     */     }
/*     */     
/* 124 */     DefaultHttpRequest other = (DefaultHttpRequest)o;
/*     */     
/* 126 */     return (method().equals(other.method()) && 
/* 127 */       uri().equalsIgnoreCase(other.uri()) && super
/* 128 */       .equals(o));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 133 */     return HttpMessageUtil.appendRequest(new StringBuilder(256), this).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */