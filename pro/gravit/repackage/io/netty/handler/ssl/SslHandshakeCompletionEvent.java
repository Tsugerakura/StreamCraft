/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
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
/*    */ public final class SslHandshakeCompletionEvent
/*    */   extends SslCompletionEvent
/*    */ {
/* 25 */   public static final SslHandshakeCompletionEvent SUCCESS = new SslHandshakeCompletionEvent();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private SslHandshakeCompletionEvent() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SslHandshakeCompletionEvent(Throwable cause) {
/* 37 */     super(cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslHandshakeCompletionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */