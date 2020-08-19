/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DefaultHttpMessage
/*    */   extends DefaultHttpObject
/*    */   implements HttpMessage
/*    */ {
/*    */   private static final int HASH_CODE_PRIME = 31;
/*    */   private HttpVersion version;
/*    */   private final HttpHeaders headers;
/*    */   
/*    */   protected DefaultHttpMessage(HttpVersion version) {
/* 34 */     this(version, true, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected DefaultHttpMessage(HttpVersion version, boolean validateHeaders, boolean singleFieldHeaders) {
/* 41 */     this(version, singleFieldHeaders ? new CombinedHttpHeaders(validateHeaders) : new DefaultHttpHeaders(validateHeaders));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected DefaultHttpMessage(HttpVersion version, HttpHeaders headers) {
/* 50 */     this.version = (HttpVersion)ObjectUtil.checkNotNull(version, "version");
/* 51 */     this.headers = (HttpHeaders)ObjectUtil.checkNotNull(headers, "headers");
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpHeaders headers() {
/* 56 */     return this.headers;
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public HttpVersion getProtocolVersion() {
/* 62 */     return protocolVersion();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpVersion protocolVersion() {
/* 67 */     return this.version;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 72 */     int result = 1;
/* 73 */     result = 31 * result + this.headers.hashCode();
/* 74 */     result = 31 * result + this.version.hashCode();
/* 75 */     result = 31 * result + super.hashCode();
/* 76 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 81 */     if (!(o instanceof DefaultHttpMessage)) {
/* 82 */       return false;
/*    */     }
/*    */     
/* 85 */     DefaultHttpMessage other = (DefaultHttpMessage)o;
/*    */     
/* 87 */     return (headers().equals(other.headers()) && 
/* 88 */       protocolVersion().equals(other.protocolVersion()) && super
/* 89 */       .equals(o));
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpMessage setProtocolVersion(HttpVersion version) {
/* 94 */     this.version = (HttpVersion)ObjectUtil.checkNotNull(version, "version");
/* 95 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */