/*    */ package io.sentry.event.interfaces;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ExceptionMechanismThrowable
/*    */   extends Throwable
/*    */ {
/*    */   private final ExceptionMechanism exceptionMechanism;
/*    */   private final Throwable throwable;
/*    */   
/*    */   public ExceptionMechanismThrowable(ExceptionMechanism mechanism, Throwable throwable) {
/* 17 */     this.exceptionMechanism = mechanism;
/* 18 */     this.throwable = throwable;
/*    */   }
/*    */   
/*    */   public ExceptionMechanism getExceptionMechanism() {
/* 22 */     return this.exceptionMechanism;
/*    */   }
/*    */   
/*    */   public Throwable getThrowable() {
/* 26 */     return this.throwable;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\ExceptionMechanismThrowable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */