/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
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
/*    */ public final class WebSocketScheme
/*    */ {
/* 28 */   public static final WebSocketScheme WS = new WebSocketScheme(80, "ws");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 33 */   public static final WebSocketScheme WSS = new WebSocketScheme(443, "wss");
/*    */   
/*    */   private final int port;
/*    */   private final AsciiString name;
/*    */   
/*    */   private WebSocketScheme(int port, String name) {
/* 39 */     this.port = port;
/* 40 */     this.name = AsciiString.cached(name);
/*    */   }
/*    */   
/*    */   public AsciiString name() {
/* 44 */     return this.name;
/*    */   }
/*    */   
/*    */   public int port() {
/* 48 */     return this.port;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 53 */     if (!(o instanceof WebSocketScheme)) {
/* 54 */       return false;
/*    */     }
/* 56 */     WebSocketScheme other = (WebSocketScheme)o;
/* 57 */     return (other.port() == this.port && other.name().equals(this.name));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return this.port * 31 + this.name.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return this.name.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */