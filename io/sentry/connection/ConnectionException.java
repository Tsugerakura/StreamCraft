/*    */ package io.sentry.connection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConnectionException
/*    */   extends RuntimeException
/*    */ {
/* 12 */   private Long recommendedLockdownTime = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 17 */   private Integer responseCode = null;
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionException() {}
/*    */ 
/*    */   
/*    */   public ConnectionException(String message, Throwable cause) {
/* 25 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public ConnectionException(String message, Throwable cause, Long recommendedLockdownTime, Integer responseCode) {
/* 29 */     super(message, cause);
/* 30 */     this.recommendedLockdownTime = recommendedLockdownTime;
/* 31 */     this.responseCode = responseCode;
/*    */   }
/*    */   
/*    */   public Long getRecommendedLockdownTime() {
/* 35 */     return this.recommendedLockdownTime;
/*    */   }
/*    */   
/*    */   public Integer getResponseCode() {
/* 39 */     return this.responseCode;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\ConnectionException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */