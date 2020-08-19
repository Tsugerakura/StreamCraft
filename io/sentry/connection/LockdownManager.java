/*     */ package io.sentry.connection;
/*     */ 
/*     */ import io.sentry.time.Clock;
/*     */ import io.sentry.time.SystemClock;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LockdownManager
/*     */ {
/*  17 */   public static final long DEFAULT_MAX_LOCKDOWN_TIME = TimeUnit.MINUTES.toMillis(5L);
/*     */ 
/*     */ 
/*     */   
/*  21 */   public static final long DEFAULT_BASE_LOCKDOWN_TIME = TimeUnit.SECONDS.toMillis(1L);
/*     */ 
/*     */ 
/*     */   
/*  25 */   private long maxLockdownTime = DEFAULT_MAX_LOCKDOWN_TIME;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private long baseLockdownTime = DEFAULT_BASE_LOCKDOWN_TIME;
/*     */ 
/*     */ 
/*     */   
/*  35 */   private long lockdownTime = 0L;
/*     */ 
/*     */ 
/*     */   
/*  39 */   private Date lockdownStartTime = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Clock clock;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LockdownManager() {
/*  49 */     this((Clock)new SystemClock());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LockdownManager(Clock clock) {
/*  58 */     this.clock = clock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean isLockedDown() {
/*  67 */     return (this.lockdownStartTime != null && this.clock.millis() - this.lockdownStartTime.getTime() < this.lockdownTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void unlock() {
/*  74 */     this.lockdownTime = 0L;
/*  75 */     this.lockdownStartTime = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean lockdown(ConnectionException connectionException) {
/*  88 */     if (isLockedDown()) {
/*  89 */       return false;
/*     */     }
/*     */     
/*  92 */     if (connectionException != null && connectionException.getRecommendedLockdownTime() != null) {
/*  93 */       this.lockdownTime = connectionException.getRecommendedLockdownTime().longValue();
/*  94 */     } else if (this.lockdownTime != 0L) {
/*  95 */       this.lockdownTime *= 2L;
/*     */     } else {
/*  97 */       this.lockdownTime = this.baseLockdownTime;
/*     */     } 
/*     */     
/* 100 */     this.lockdownTime = Math.min(this.maxLockdownTime, this.lockdownTime);
/* 101 */     this.lockdownStartTime = this.clock.date();
/*     */     
/* 103 */     return true;
/*     */   }
/*     */   
/*     */   public synchronized void setBaseLockdownTime(long baseLockdownTime) {
/* 107 */     this.baseLockdownTime = baseLockdownTime;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxLockdownTime(long maxLockdownTime) {
/* 111 */     this.maxLockdownTime = maxLockdownTime;
/*     */   }
/*     */ 
/*     */   
/*     */   long getLockdownTime() {
/* 116 */     return this.lockdownTime;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\LockdownManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */