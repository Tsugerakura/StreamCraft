/*    */ package io.sentry.time;
/*    */ 
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SystemClock
/*    */   implements Clock
/*    */ {
/*    */   public long millis() {
/* 12 */     return System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   
/*    */   public Date date() {
/* 17 */     return new Date();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\time\SystemClock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */