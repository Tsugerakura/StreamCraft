/*     */ package pro.gravit.repackage.io.netty.handler.traffic;
/*     */ 
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TrafficCounter
/*     */ {
/*  39 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long milliSecondFromNano() {
/*  45 */     return System.nanoTime() / 1000000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private final AtomicLong currentWrittenBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   private final AtomicLong currentReadBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long writingTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long readingTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private final AtomicLong cumulativeReadBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long lastCumulativeTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long lastWriteThroughput;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long lastReadThroughput;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   final AtomicLong lastTime = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long lastWrittenBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long lastReadBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long lastWritingTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long lastReadingTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   private final AtomicLong realWrittenBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long realWriteThroughput;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 131 */   final AtomicLong checkInterval = new AtomicLong(1000L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final AbstractTrafficShapingHandler trafficShapingHandler;
/*     */ 
/*     */ 
/*     */   
/*     */   final ScheduledExecutorService executor;
/*     */ 
/*     */ 
/*     */   
/*     */   Runnable monitor;
/*     */ 
/*     */ 
/*     */   
/*     */   volatile ScheduledFuture<?> scheduledFuture;
/*     */ 
/*     */ 
/*     */   
/*     */   volatile boolean monitorActive;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class TrafficMonitoringTask
/*     */     implements Runnable
/*     */   {
/*     */     private TrafficMonitoringTask() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 171 */       if (!TrafficCounter.this.monitorActive) {
/*     */         return;
/*     */       }
/* 174 */       TrafficCounter.this.resetAccounting(TrafficCounter.milliSecondFromNano());
/* 175 */       if (TrafficCounter.this.trafficShapingHandler != null) {
/* 176 */         TrafficCounter.this.trafficShapingHandler.doAccounting(TrafficCounter.this);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void start() {
/* 185 */     if (this.monitorActive) {
/*     */       return;
/*     */     }
/* 188 */     this.lastTime.set(milliSecondFromNano());
/* 189 */     long localCheckInterval = this.checkInterval.get();
/*     */     
/* 191 */     if (localCheckInterval > 0L && this.executor != null) {
/* 192 */       this.monitorActive = true;
/* 193 */       this.monitor = new TrafficMonitoringTask();
/* 194 */       this
/* 195 */         .scheduledFuture = this.executor.scheduleAtFixedRate(this.monitor, 0L, localCheckInterval, TimeUnit.MILLISECONDS);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void stop() {
/* 203 */     if (!this.monitorActive) {
/*     */       return;
/*     */     }
/* 206 */     this.monitorActive = false;
/* 207 */     resetAccounting(milliSecondFromNano());
/* 208 */     if (this.trafficShapingHandler != null) {
/* 209 */       this.trafficShapingHandler.doAccounting(this);
/*     */     }
/* 211 */     if (this.scheduledFuture != null) {
/* 212 */       this.scheduledFuture.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void resetAccounting(long newLastTime) {
/* 222 */     long interval = newLastTime - this.lastTime.getAndSet(newLastTime);
/* 223 */     if (interval == 0L) {
/*     */       return;
/*     */     }
/*     */     
/* 227 */     if (logger.isDebugEnabled() && interval > checkInterval() << 1L) {
/* 228 */       logger.debug("Acct schedule not ok: " + interval + " > 2*" + checkInterval() + " from " + this.name);
/*     */     }
/* 230 */     this.lastReadBytes = this.currentReadBytes.getAndSet(0L);
/* 231 */     this.lastWrittenBytes = this.currentWrittenBytes.getAndSet(0L);
/* 232 */     this.lastReadThroughput = this.lastReadBytes * 1000L / interval;
/*     */     
/* 234 */     this.lastWriteThroughput = this.lastWrittenBytes * 1000L / interval;
/*     */     
/* 236 */     this.realWriteThroughput = this.realWrittenBytes.getAndSet(0L) * 1000L / interval;
/* 237 */     this.lastWritingTime = Math.max(this.lastWritingTime, this.writingTime);
/* 238 */     this.lastReadingTime = Math.max(this.lastReadingTime, this.readingTime);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TrafficCounter(ScheduledExecutorService executor, String name, long checkInterval) {
/* 255 */     this.name = (String)ObjectUtil.checkNotNull(name, "name");
/* 256 */     this.trafficShapingHandler = null;
/* 257 */     this.executor = executor;
/*     */     
/* 259 */     init(checkInterval);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TrafficCounter(AbstractTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval) {
/* 280 */     if (trafficShapingHandler == null) {
/* 281 */       throw new IllegalArgumentException("trafficShapingHandler");
/*     */     }
/*     */     
/* 284 */     this.name = (String)ObjectUtil.checkNotNull(name, "name");
/* 285 */     this.trafficShapingHandler = trafficShapingHandler;
/* 286 */     this.executor = executor;
/*     */     
/* 288 */     init(checkInterval);
/*     */   }
/*     */ 
/*     */   
/*     */   private void init(long checkInterval) {
/* 293 */     this.lastCumulativeTime = System.currentTimeMillis();
/* 294 */     this.writingTime = milliSecondFromNano();
/* 295 */     this.readingTime = this.writingTime;
/* 296 */     this.lastWritingTime = this.writingTime;
/* 297 */     this.lastReadingTime = this.writingTime;
/* 298 */     configure(checkInterval);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configure(long newCheckInterval) {
/* 307 */     long newInterval = newCheckInterval / 10L * 10L;
/* 308 */     if (this.checkInterval.getAndSet(newInterval) != newInterval) {
/* 309 */       if (newInterval <= 0L) {
/* 310 */         stop();
/*     */         
/* 312 */         this.lastTime.set(milliSecondFromNano());
/*     */       } else {
/*     */         
/* 315 */         stop();
/* 316 */         start();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void bytesRecvFlowControl(long recv) {
/* 328 */     this.currentReadBytes.addAndGet(recv);
/* 329 */     this.cumulativeReadBytes.addAndGet(recv);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void bytesWriteFlowControl(long write) {
/* 339 */     this.currentWrittenBytes.addAndGet(write);
/* 340 */     this.cumulativeWrittenBytes.addAndGet(write);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void bytesRealWriteFlowControl(long write) {
/* 350 */     this.realWrittenBytes.addAndGet(write);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long checkInterval() {
/* 358 */     return this.checkInterval.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastReadThroughput() {
/* 365 */     return this.lastReadThroughput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastWriteThroughput() {
/* 372 */     return this.lastWriteThroughput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastReadBytes() {
/* 379 */     return this.lastReadBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastWrittenBytes() {
/* 386 */     return this.lastWrittenBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long currentReadBytes() {
/* 393 */     return this.currentReadBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long currentWrittenBytes() {
/* 400 */     return this.currentWrittenBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastTime() {
/* 407 */     return this.lastTime.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long cumulativeWrittenBytes() {
/* 414 */     return this.cumulativeWrittenBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long cumulativeReadBytes() {
/* 421 */     return this.cumulativeReadBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long lastCumulativeTime() {
/* 429 */     return this.lastCumulativeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AtomicLong getRealWrittenBytes() {
/* 436 */     return this.realWrittenBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRealWriteThroughput() {
/* 443 */     return this.realWriteThroughput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetCumulativeTime() {
/* 451 */     this.lastCumulativeTime = System.currentTimeMillis();
/* 452 */     this.cumulativeReadBytes.set(0L);
/* 453 */     this.cumulativeWrittenBytes.set(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/* 460 */     return this.name;
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
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long readTimeToWait(long size, long limitTraffic, long maxTime) {
/* 477 */     return readTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long readTimeToWait(long size, long limitTraffic, long maxTime, long now) {
/* 494 */     bytesRecvFlowControl(size);
/* 495 */     if (size == 0L || limitTraffic == 0L) {
/* 496 */       return 0L;
/*     */     }
/* 498 */     long lastTimeCheck = this.lastTime.get();
/* 499 */     long sum = this.currentReadBytes.get();
/* 500 */     long localReadingTime = this.readingTime;
/* 501 */     long lastRB = this.lastReadBytes;
/* 502 */     long interval = now - lastTimeCheck;
/* 503 */     long pastDelay = Math.max(this.lastReadingTime - lastTimeCheck, 0L);
/* 504 */     if (interval > 10L) {
/*     */       
/* 506 */       long l = sum * 1000L / limitTraffic - interval + pastDelay;
/* 507 */       if (l > 10L) {
/* 508 */         if (logger.isDebugEnabled()) {
/* 509 */           logger.debug("Time: " + l + ':' + sum + ':' + interval + ':' + pastDelay);
/*     */         }
/* 511 */         if (l > maxTime && now + l - localReadingTime > maxTime) {
/* 512 */           l = maxTime;
/*     */         }
/* 514 */         this.readingTime = Math.max(localReadingTime, now + l);
/* 515 */         return l;
/*     */       } 
/* 517 */       this.readingTime = Math.max(localReadingTime, now);
/* 518 */       return 0L;
/*     */     } 
/*     */     
/* 521 */     long lastsum = sum + lastRB;
/* 522 */     long lastinterval = interval + this.checkInterval.get();
/* 523 */     long time = lastsum * 1000L / limitTraffic - lastinterval + pastDelay;
/* 524 */     if (time > 10L) {
/* 525 */       if (logger.isDebugEnabled()) {
/* 526 */         logger.debug("Time: " + time + ':' + lastsum + ':' + lastinterval + ':' + pastDelay);
/*     */       }
/* 528 */       if (time > maxTime && now + time - localReadingTime > maxTime) {
/* 529 */         time = maxTime;
/*     */       }
/* 531 */       this.readingTime = Math.max(localReadingTime, now + time);
/* 532 */       return time;
/*     */     } 
/* 534 */     this.readingTime = Math.max(localReadingTime, now);
/* 535 */     return 0L;
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
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long writeTimeToWait(long size, long limitTraffic, long maxTime) {
/* 552 */     return writeTimeToWait(size, limitTraffic, maxTime, milliSecondFromNano());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long writeTimeToWait(long size, long limitTraffic, long maxTime, long now) {
/* 569 */     bytesWriteFlowControl(size);
/* 570 */     if (size == 0L || limitTraffic == 0L) {
/* 571 */       return 0L;
/*     */     }
/* 573 */     long lastTimeCheck = this.lastTime.get();
/* 574 */     long sum = this.currentWrittenBytes.get();
/* 575 */     long lastWB = this.lastWrittenBytes;
/* 576 */     long localWritingTime = this.writingTime;
/* 577 */     long pastDelay = Math.max(this.lastWritingTime - lastTimeCheck, 0L);
/* 578 */     long interval = now - lastTimeCheck;
/* 579 */     if (interval > 10L) {
/*     */       
/* 581 */       long l = sum * 1000L / limitTraffic - interval + pastDelay;
/* 582 */       if (l > 10L) {
/* 583 */         if (logger.isDebugEnabled()) {
/* 584 */           logger.debug("Time: " + l + ':' + sum + ':' + interval + ':' + pastDelay);
/*     */         }
/* 586 */         if (l > maxTime && now + l - localWritingTime > maxTime) {
/* 587 */           l = maxTime;
/*     */         }
/* 589 */         this.writingTime = Math.max(localWritingTime, now + l);
/* 590 */         return l;
/*     */       } 
/* 592 */       this.writingTime = Math.max(localWritingTime, now);
/* 593 */       return 0L;
/*     */     } 
/*     */     
/* 596 */     long lastsum = sum + lastWB;
/* 597 */     long lastinterval = interval + this.checkInterval.get();
/* 598 */     long time = lastsum * 1000L / limitTraffic - lastinterval + pastDelay;
/* 599 */     if (time > 10L) {
/* 600 */       if (logger.isDebugEnabled()) {
/* 601 */         logger.debug("Time: " + time + ':' + lastsum + ':' + lastinterval + ':' + pastDelay);
/*     */       }
/* 603 */       if (time > maxTime && now + time - localWritingTime > maxTime) {
/* 604 */         time = maxTime;
/*     */       }
/* 606 */       this.writingTime = Math.max(localWritingTime, now + time);
/* 607 */       return time;
/*     */     } 
/* 609 */     this.writingTime = Math.max(localWritingTime, now);
/* 610 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 615 */     return (new StringBuilder(165)).append("Monitor ").append(this.name)
/* 616 */       .append(" Current Speed Read: ").append(this.lastReadThroughput >> 10L).append(" KB/s, ")
/* 617 */       .append("Asked Write: ").append(this.lastWriteThroughput >> 10L).append(" KB/s, ")
/* 618 */       .append("Real Write: ").append(this.realWriteThroughput >> 10L).append(" KB/s, ")
/* 619 */       .append("Current Read: ").append(this.currentReadBytes.get() >> 10L).append(" KB, ")
/* 620 */       .append("Current asked Write: ").append(this.currentWrittenBytes.get() >> 10L).append(" KB, ")
/* 621 */       .append("Current real Write: ").append(this.realWrittenBytes.get() >> 10L).append(" KB").toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\traffic\TrafficCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */