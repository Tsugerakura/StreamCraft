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
/*    */ 
/*    */ public final class SniCompletionEvent
/*    */   extends SslCompletionEvent
/*    */ {
/*    */   private final String hostname;
/*    */   
/*    */   SniCompletionEvent(String hostname) {
/* 29 */     this.hostname = hostname;
/*    */   }
/*    */   
/*    */   SniCompletionEvent(String hostname, Throwable cause) {
/* 33 */     super(cause);
/* 34 */     this.hostname = hostname;
/*    */   }
/*    */   
/*    */   SniCompletionEvent(Throwable cause) {
/* 38 */     this(null, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String hostname() {
/* 45 */     return this.hostname;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     Throwable cause = cause();
/* 51 */     return (cause == null) ? (getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")") : (
/* 52 */       getClass().getSimpleName() + '(' + cause + ')');
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SniCompletionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */