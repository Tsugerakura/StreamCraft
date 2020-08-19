/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.handler.codec.CorruptedFrameException;
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
/*    */ public final class CorruptedWebSocketFrameException
/*    */   extends CorruptedFrameException
/*    */ {
/*    */   private static final long serialVersionUID = 3918055132492988338L;
/*    */   private final WebSocketCloseStatus closeStatus;
/*    */   
/*    */   public CorruptedWebSocketFrameException() {
/* 35 */     this(WebSocketCloseStatus.PROTOCOL_ERROR, null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message, Throwable cause) {
/* 42 */     super((message == null) ? status.reasonText() : message, cause);
/* 43 */     this.closeStatus = status;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message) {
/* 50 */     this(status, message, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, Throwable cause) {
/* 57 */     this(status, null, cause);
/*    */   }
/*    */   
/*    */   public WebSocketCloseStatus closeStatus() {
/* 61 */     return this.closeStatus;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\CorruptedWebSocketFrameException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */