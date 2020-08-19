/*    */ package io.sentry.dsn;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InvalidDsnException
/*    */   extends RuntimeException
/*    */ {
/*    */   public InvalidDsnException() {}
/*    */   
/*    */   public InvalidDsnException(String message) {
/* 15 */     super(message);
/*    */   }
/*    */   
/*    */   public InvalidDsnException(String message, Throwable cause) {
/* 19 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public InvalidDsnException(Throwable cause) {
/* 23 */     super(cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\dsn\InvalidDsnException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */