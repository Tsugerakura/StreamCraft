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
/*    */ public final class SslCloseCompletionEvent
/*    */   extends SslCompletionEvent
/*    */ {
/* 23 */   public static final SslCloseCompletionEvent SUCCESS = new SslCloseCompletionEvent();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private SslCloseCompletionEvent() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SslCloseCompletionEvent(Throwable cause) {
/* 35 */     super(cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslCloseCompletionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */