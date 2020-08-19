/*    */ package io.sentry.connection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TooManyRequestsException
/*    */   extends ConnectionException
/*    */ {
/*    */   public TooManyRequestsException(String message, Throwable cause, Long recommendedLockdownTime, Integer responseCode) {
/* 14 */     super(message, cause, recommendedLockdownTime, responseCode);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\TooManyRequestsException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */