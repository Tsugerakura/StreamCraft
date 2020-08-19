/*    */ package io.sentry.time;
/*    */ 
/*    */ import java.util.Date;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FixedClock
/*    */   implements Clock
/*    */ {
/*    */   private Date date;
/*    */   
/*    */   public FixedClock(Date date) {
/* 19 */     this.date = date;
/*    */   }
/*    */ 
/*    */   
/*    */   public long millis() {
/* 24 */     return this.date.getTime();
/*    */   }
/*    */ 
/*    */   
/*    */   public Date date() {
/* 29 */     return this.date;
/*    */   }
/*    */   
/*    */   public void setDate(Date date) {
/* 33 */     this.date = date;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void tick(long duration, TimeUnit unit) {
/* 43 */     this.date = new Date(this.date.getTime() + unit.toMillis(duration));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\time\FixedClock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */