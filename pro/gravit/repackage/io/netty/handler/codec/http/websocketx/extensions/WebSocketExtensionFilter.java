/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketFrame;
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
/*    */ 
/*    */ public interface WebSocketExtensionFilter
/*    */ {
/* 30 */   public static final WebSocketExtensionFilter NEVER_SKIP = new WebSocketExtensionFilter()
/*    */     {
/*    */       public boolean mustSkip(WebSocketFrame frame) {
/* 33 */         return false;
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 41 */   public static final WebSocketExtensionFilter ALWAYS_SKIP = new WebSocketExtensionFilter()
/*    */     {
/*    */       public boolean mustSkip(WebSocketFrame frame) {
/* 44 */         return true;
/*    */       }
/*    */     };
/*    */   
/*    */   boolean mustSkip(WebSocketFrame paramWebSocketFrame);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\WebSocketExtensionFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */