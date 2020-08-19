/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
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
/*    */ public abstract class SslCompletionEvent
/*    */ {
/*    */   private final Throwable cause;
/*    */   
/*    */   SslCompletionEvent() {
/* 25 */     this.cause = null;
/*    */   }
/*    */   
/*    */   SslCompletionEvent(Throwable cause) {
/* 29 */     this.cause = (Throwable)ObjectUtil.checkNotNull(cause, "cause");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final boolean isSuccess() {
/* 36 */     return (this.cause == null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Throwable cause() {
/* 44 */     return this.cause;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     Throwable cause = cause();
/* 50 */     return (cause == null) ? (getClass().getSimpleName() + "(SUCCESS)") : (
/* 51 */       getClass().getSimpleName() + '(' + cause + ')');
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslCompletionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */