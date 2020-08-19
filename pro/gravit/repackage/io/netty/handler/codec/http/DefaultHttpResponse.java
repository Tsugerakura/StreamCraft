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
/*     */ public class DefaultHttpResponse
/*     */   extends DefaultHttpMessage
/*     */   implements HttpResponse
/*     */ {
/*     */   private HttpResponseStatus status;
/*     */   
/*     */   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status) {
/*  36 */     this(version, status, true, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
/*  47 */     this(version, status, validateHeaders, false);
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
/*     */   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
/*  64 */     super(version, validateHeaders, singleFieldHeaders);
/*  65 */     this.status = (HttpResponseStatus)ObjectUtil.checkNotNull(status, "status");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, HttpHeaders headers) {
/*  76 */     super(version, headers);
/*  77 */     this.status = (HttpResponseStatus)ObjectUtil.checkNotNull(status, "status");
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpResponseStatus getStatus() {
/*  83 */     return status();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponseStatus status() {
/*  88 */     return this.status;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse setStatus(HttpResponseStatus status) {
/*  93 */     this.status = (HttpResponseStatus)ObjectUtil.checkNotNull(status, "status");
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse setProtocolVersion(HttpVersion version) {
/*  99 */     super.setProtocolVersion(version);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 105 */     return HttpMessageUtil.appendResponse(new StringBuilder(256), this).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 110 */     int result = 1;
/* 111 */     result = 31 * result + this.status.hashCode();
/* 112 */     result = 31 * result + super.hashCode();
/* 113 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 118 */     if (!(o instanceof DefaultHttpResponse)) {
/* 119 */       return false;
/*     */     }
/*     */     
/* 122 */     DefaultHttpResponse other = (DefaultHttpResponse)o;
/*     */     
/* 124 */     return (this.status.equals(other.status()) && super.equals(o));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */