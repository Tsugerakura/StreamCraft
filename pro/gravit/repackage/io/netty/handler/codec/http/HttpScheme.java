/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.AsciiString;
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
/*    */ 
/*    */ 
/*    */ public final class HttpScheme
/*    */ {
/* 29 */   public static final HttpScheme HTTP = new HttpScheme(80, "http");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 34 */   public static final HttpScheme HTTPS = new HttpScheme(443, "https");
/*    */   
/*    */   private final int port;
/*    */   private final AsciiString name;
/*    */   
/*    */   private HttpScheme(int port, String name) {
/* 40 */     this.port = port;
/* 41 */     this.name = AsciiString.cached(name);
/*    */   }
/*    */   
/*    */   public AsciiString name() {
/* 45 */     return this.name;
/*    */   }
/*    */   
/*    */   public int port() {
/* 49 */     return this.port;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 54 */     if (!(o instanceof HttpScheme)) {
/* 55 */       return false;
/*    */     }
/* 57 */     HttpScheme other = (HttpScheme)o;
/* 58 */     return (other.port() == this.port && other.name().equals(this.name));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 63 */     return this.port * 31 + this.name.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return this.name.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */