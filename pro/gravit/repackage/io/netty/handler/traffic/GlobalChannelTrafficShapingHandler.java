/*     */ package pro.gravit.repackage.io.netty.handler.traffic;
/*     */ 
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.Attribute;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ @Sharable
/*     */ public class GlobalChannelTrafficShapingHandler
/*     */   extends AbstractTrafficShapingHandler
/*     */ {
/*  89 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
/*     */ 
/*     */ 
/*     */   
/*  93 */   final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   private final AtomicLong queuesSize = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 108 */   private final AtomicLong cumulativeReadBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 114 */   volatile long maxGlobalWriteSize = 419430400L;
/*     */   
/*     */   private volatile long writeChannelLimit;
/*     */   
/*     */   private volatile long readChannelLimit;
/*     */   
/*     */   private static final float DEFAULT_DEVIATION = 0.1F;
/*     */   
/*     */   private static final float MAX_DEVIATION = 0.4F;
/*     */   
/*     */   private static final float DEFAULT_SLOWDOWN = 0.4F;
/*     */   
/*     */   private static final float DEFAULT_ACCELERATION = -0.1F;
/*     */   
/*     */   private volatile float maxDeviation;
/*     */   
/*     */   private volatile float accelerationFactor;
/*     */   
/*     */   private volatile float slowDownFactor;
/*     */   
/*     */   private volatile boolean readDeviationActive;
/*     */   
/*     */   private volatile boolean writeDeviationActive;
/*     */ 
/*     */   
/*     */   static final class PerChannel
/*     */   {
/*     */     ArrayDeque<GlobalChannelTrafficShapingHandler.ToSend> messagesQueue;
/*     */     TrafficCounter channelTrafficCounter;
/*     */     long queueSize;
/*     */     long lastWriteTimestamp;
/*     */     long lastReadTimestamp;
/*     */   }
/*     */   
/*     */   void createGlobalTrafficCounter(ScheduledExecutorService executor) {
/* 149 */     setMaxDeviation(0.1F, 0.4F, -0.1F);
/* 150 */     if (executor == null) {
/* 151 */       throw new IllegalArgumentException("Executor must not be null");
/*     */     }
/* 153 */     TrafficCounter tc = new GlobalChannelTrafficCounter(this, executor, "GlobalChannelTC", this.checkInterval);
/* 154 */     setTrafficCounter(tc);
/* 155 */     tc.start();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int userDefinedWritabilityIndex() {
/* 160 */     return 3;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval, long maxTime) {
/* 186 */     super(writeGlobalLimit, readGlobalLimit, checkInterval, maxTime);
/* 187 */     createGlobalTrafficCounter(executor);
/* 188 */     this.writeChannelLimit = writeChannelLimit;
/* 189 */     this.readChannelLimit = readChannelLimit;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval) {
/* 213 */     super(writeGlobalLimit, readGlobalLimit, checkInterval);
/* 214 */     this.writeChannelLimit = writeChannelLimit;
/* 215 */     this.readChannelLimit = readChannelLimit;
/* 216 */     createGlobalTrafficCounter(executor);
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
/*     */   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit) {
/* 236 */     super(writeGlobalLimit, readGlobalLimit);
/* 237 */     this.writeChannelLimit = writeChannelLimit;
/* 238 */     this.readChannelLimit = readChannelLimit;
/* 239 */     createGlobalTrafficCounter(executor);
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
/*     */   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
/* 252 */     super(checkInterval);
/* 253 */     createGlobalTrafficCounter(executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor) {
/* 263 */     createGlobalTrafficCounter(executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float maxDeviation() {
/* 270 */     return this.maxDeviation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float accelerationFactor() {
/* 277 */     return this.accelerationFactor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float slowDownFactor() {
/* 284 */     return this.slowDownFactor;
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
/*     */   public void setMaxDeviation(float maxDeviation, float slowDownFactor, float accelerationFactor) {
/* 299 */     if (maxDeviation > 0.4F) {
/* 300 */       throw new IllegalArgumentException("maxDeviation must be <= 0.4");
/*     */     }
/* 302 */     if (slowDownFactor < 0.0F) {
/* 303 */       throw new IllegalArgumentException("slowDownFactor must be >= 0");
/*     */     }
/* 305 */     if (accelerationFactor > 0.0F) {
/* 306 */       throw new IllegalArgumentException("accelerationFactor must be <= 0");
/*     */     }
/* 308 */     this.maxDeviation = maxDeviation;
/* 309 */     this.accelerationFactor = 1.0F + accelerationFactor;
/* 310 */     this.slowDownFactor = 1.0F + slowDownFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   private void computeDeviationCumulativeBytes() {
/* 315 */     long maxWrittenBytes = 0L;
/* 316 */     long maxReadBytes = 0L;
/* 317 */     long minWrittenBytes = Long.MAX_VALUE;
/* 318 */     long minReadBytes = Long.MAX_VALUE;
/* 319 */     for (PerChannel perChannel : this.channelQueues.values()) {
/* 320 */       long value = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
/* 321 */       if (maxWrittenBytes < value) {
/* 322 */         maxWrittenBytes = value;
/*     */       }
/* 324 */       if (minWrittenBytes > value) {
/* 325 */         minWrittenBytes = value;
/*     */       }
/* 327 */       value = perChannel.channelTrafficCounter.cumulativeReadBytes();
/* 328 */       if (maxReadBytes < value) {
/* 329 */         maxReadBytes = value;
/*     */       }
/* 331 */       if (minReadBytes > value) {
/* 332 */         minReadBytes = value;
/*     */       }
/*     */     } 
/* 335 */     boolean multiple = (this.channelQueues.size() > 1);
/* 336 */     this.readDeviationActive = (multiple && minReadBytes < maxReadBytes / 2L);
/* 337 */     this.writeDeviationActive = (multiple && minWrittenBytes < maxWrittenBytes / 2L);
/* 338 */     this.cumulativeWrittenBytes.set(maxWrittenBytes);
/* 339 */     this.cumulativeReadBytes.set(maxReadBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doAccounting(TrafficCounter counter) {
/* 344 */     computeDeviationCumulativeBytes();
/* 345 */     super.doAccounting(counter);
/*     */   }
/*     */   
/*     */   private long computeBalancedWait(float maxLocal, float maxGlobal, long wait) {
/* 349 */     if (maxGlobal == 0.0F)
/*     */     {
/* 351 */       return wait;
/*     */     }
/* 353 */     float ratio = maxLocal / maxGlobal;
/*     */     
/* 355 */     if (ratio > this.maxDeviation) {
/* 356 */       if (ratio < 1.0F - this.maxDeviation) {
/* 357 */         return wait;
/*     */       }
/* 359 */       ratio = this.slowDownFactor;
/* 360 */       if (wait < 10L) {
/* 361 */         wait = 10L;
/*     */       }
/*     */     } else {
/*     */       
/* 365 */       ratio = this.accelerationFactor;
/*     */     } 
/* 367 */     return (long)((float)wait * ratio);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxGlobalWriteSize() {
/* 374 */     return this.maxGlobalWriteSize;
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
/*     */   public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
/* 388 */     if (maxGlobalWriteSize <= 0L) {
/* 389 */       throw new IllegalArgumentException("maxGlobalWriteSize must be positive");
/*     */     }
/* 391 */     this.maxGlobalWriteSize = maxGlobalWriteSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long queuesSize() {
/* 398 */     return this.queuesSize.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configureChannel(long newWriteLimit, long newReadLimit) {
/* 406 */     this.writeChannelLimit = newWriteLimit;
/* 407 */     this.readChannelLimit = newReadLimit;
/* 408 */     long now = TrafficCounter.milliSecondFromNano();
/* 409 */     for (PerChannel perChannel : this.channelQueues.values()) {
/* 410 */       perChannel.channelTrafficCounter.resetAccounting(now);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteChannelLimit() {
/* 418 */     return this.writeChannelLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteChannelLimit(long writeLimit) {
/* 425 */     this.writeChannelLimit = writeLimit;
/* 426 */     long now = TrafficCounter.milliSecondFromNano();
/* 427 */     for (PerChannel perChannel : this.channelQueues.values()) {
/* 428 */       perChannel.channelTrafficCounter.resetAccounting(now);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadChannelLimit() {
/* 436 */     return this.readChannelLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadChannelLimit(long readLimit) {
/* 443 */     this.readChannelLimit = readLimit;
/* 444 */     long now = TrafficCounter.milliSecondFromNano();
/* 445 */     for (PerChannel perChannel : this.channelQueues.values()) {
/* 446 */       perChannel.channelTrafficCounter.resetAccounting(now);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void release() {
/* 454 */     this.trafficCounter.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
/* 459 */     Channel channel = ctx.channel();
/* 460 */     Integer key = Integer.valueOf(channel.hashCode());
/* 461 */     PerChannel perChannel = this.channelQueues.get(key);
/* 462 */     if (perChannel == null) {
/* 463 */       perChannel = new PerChannel();
/* 464 */       perChannel.messagesQueue = new ArrayDeque<ToSend>();
/*     */       
/* 466 */       perChannel
/* 467 */         .channelTrafficCounter = new TrafficCounter(this, null, "ChannelTC" + ctx.channel().hashCode(), this.checkInterval);
/* 468 */       perChannel.queueSize = 0L;
/* 469 */       perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
/* 470 */       perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
/* 471 */       this.channelQueues.put(key, perChannel);
/*     */     } 
/* 473 */     return perChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 478 */     getOrSetPerChannel(ctx);
/* 479 */     this.trafficCounter.resetCumulativeTime();
/* 480 */     super.handlerAdded(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 485 */     this.trafficCounter.resetCumulativeTime();
/* 486 */     Channel channel = ctx.channel();
/* 487 */     Integer key = Integer.valueOf(channel.hashCode());
/* 488 */     PerChannel perChannel = this.channelQueues.remove(key);
/* 489 */     if (perChannel != null)
/*     */     {
/* 491 */       synchronized (perChannel) {
/* 492 */         if (channel.isActive()) {
/* 493 */           for (ToSend toSend : perChannel.messagesQueue) {
/* 494 */             long size = calculateSize(toSend.toSend);
/* 495 */             this.trafficCounter.bytesRealWriteFlowControl(size);
/* 496 */             perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
/* 497 */             perChannel.queueSize -= size;
/* 498 */             this.queuesSize.addAndGet(-size);
/* 499 */             ctx.write(toSend.toSend, toSend.promise);
/*     */           } 
/*     */         } else {
/* 502 */           this.queuesSize.addAndGet(-perChannel.queueSize);
/* 503 */           for (ToSend toSend : perChannel.messagesQueue) {
/* 504 */             if (toSend.toSend instanceof ByteBuf) {
/* 505 */               ((ByteBuf)toSend.toSend).release();
/*     */             }
/*     */           } 
/*     */         } 
/* 509 */         perChannel.messagesQueue.clear();
/*     */       } 
/*     */     }
/* 512 */     releaseWriteSuspended(ctx);
/* 513 */     releaseReadSuspended(ctx);
/* 514 */     super.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 519 */     long size = calculateSize(msg);
/* 520 */     long now = TrafficCounter.milliSecondFromNano();
/* 521 */     if (size > 0L) {
/*     */       
/* 523 */       long waitGlobal = this.trafficCounter.readTimeToWait(size, getReadLimit(), this.maxTime, now);
/* 524 */       Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 525 */       PerChannel perChannel = this.channelQueues.get(key);
/* 526 */       long wait = 0L;
/* 527 */       if (perChannel != null) {
/* 528 */         wait = perChannel.channelTrafficCounter.readTimeToWait(size, this.readChannelLimit, this.maxTime, now);
/* 529 */         if (this.readDeviationActive) {
/*     */ 
/*     */           
/* 532 */           long maxLocalRead = perChannel.channelTrafficCounter.cumulativeReadBytes();
/* 533 */           long maxGlobalRead = this.cumulativeReadBytes.get();
/* 534 */           if (maxLocalRead <= 0L) {
/* 535 */             maxLocalRead = 0L;
/*     */           }
/* 537 */           if (maxGlobalRead < maxLocalRead) {
/* 538 */             maxGlobalRead = maxLocalRead;
/*     */           }
/* 540 */           wait = computeBalancedWait((float)maxLocalRead, (float)maxGlobalRead, wait);
/*     */         } 
/*     */       } 
/* 543 */       if (wait < waitGlobal) {
/* 544 */         wait = waitGlobal;
/*     */       }
/* 546 */       wait = checkWaitReadTime(ctx, wait, now);
/* 547 */       if (wait >= 10L) {
/*     */ 
/*     */         
/* 550 */         Channel channel = ctx.channel();
/* 551 */         ChannelConfig config = channel.config();
/* 552 */         if (logger.isDebugEnabled()) {
/* 553 */           logger.debug("Read Suspend: " + wait + ':' + config.isAutoRead() + ':' + 
/* 554 */               isHandlerActive(ctx));
/*     */         }
/* 556 */         if (config.isAutoRead() && isHandlerActive(ctx)) {
/* 557 */           config.setAutoRead(false);
/* 558 */           channel.attr(READ_SUSPENDED).set(Boolean.valueOf(true));
/*     */ 
/*     */           
/* 561 */           Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
/* 562 */           Runnable reopenTask = (Runnable)attr.get();
/* 563 */           if (reopenTask == null) {
/* 564 */             reopenTask = new AbstractTrafficShapingHandler.ReopenReadTimerTask(ctx);
/* 565 */             attr.set(reopenTask);
/*     */           } 
/* 567 */           ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
/* 568 */           if (logger.isDebugEnabled()) {
/* 569 */             logger.debug("Suspend final status => " + config.isAutoRead() + ':' + 
/* 570 */                 isHandlerActive(ctx) + " will reopened at: " + wait);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 575 */     informReadOperation(ctx, now);
/* 576 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
/* 581 */     Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 582 */     PerChannel perChannel = this.channelQueues.get(key);
/* 583 */     if (perChannel != null && 
/* 584 */       wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
/* 585 */       wait = this.maxTime;
/*     */     }
/*     */     
/* 588 */     return wait;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void informReadOperation(ChannelHandlerContext ctx, long now) {
/* 593 */     Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 594 */     PerChannel perChannel = this.channelQueues.get(key);
/* 595 */     if (perChannel != null)
/* 596 */       perChannel.lastReadTimestamp = now; 
/*     */   }
/*     */   
/*     */   private static final class ToSend
/*     */   {
/*     */     final long relativeTimeAction;
/*     */     final Object toSend;
/*     */     final ChannelPromise promise;
/*     */     final long size;
/*     */     
/*     */     private ToSend(long delay, Object toSend, long size, ChannelPromise promise) {
/* 607 */       this.relativeTimeAction = delay;
/* 608 */       this.toSend = toSend;
/* 609 */       this.size = size;
/* 610 */       this.promise = promise;
/*     */     }
/*     */   }
/*     */   
/*     */   protected long maximumCumulativeWrittenBytes() {
/* 615 */     return this.cumulativeWrittenBytes.get();
/*     */   }
/*     */   
/*     */   protected long maximumCumulativeReadBytes() {
/* 619 */     return this.cumulativeReadBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<TrafficCounter> channelTrafficCounters() {
/* 627 */     return new AbstractCollection<TrafficCounter>()
/*     */       {
/*     */         public Iterator<TrafficCounter> iterator() {
/* 630 */           return new Iterator<TrafficCounter>() {
/* 631 */               final Iterator<GlobalChannelTrafficShapingHandler.PerChannel> iter = GlobalChannelTrafficShapingHandler.this.channelQueues.values().iterator();
/*     */               
/*     */               public boolean hasNext() {
/* 634 */                 return this.iter.hasNext();
/*     */               }
/*     */               
/*     */               public TrafficCounter next() {
/* 638 */                 return ((GlobalChannelTrafficShapingHandler.PerChannel)this.iter.next()).channelTrafficCounter;
/*     */               }
/*     */               
/*     */               public void remove() {
/* 642 */                 throw new UnsupportedOperationException();
/*     */               }
/*     */             };
/*     */         }
/*     */         
/*     */         public int size() {
/* 648 */           return GlobalChannelTrafficShapingHandler.this.channelQueues.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 656 */     long size = calculateSize(msg);
/* 657 */     long now = TrafficCounter.milliSecondFromNano();
/* 658 */     if (size > 0L) {
/*     */       
/* 660 */       long waitGlobal = this.trafficCounter.writeTimeToWait(size, getWriteLimit(), this.maxTime, now);
/* 661 */       Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 662 */       PerChannel perChannel = this.channelQueues.get(key);
/* 663 */       long wait = 0L;
/* 664 */       if (perChannel != null) {
/* 665 */         wait = perChannel.channelTrafficCounter.writeTimeToWait(size, this.writeChannelLimit, this.maxTime, now);
/* 666 */         if (this.writeDeviationActive) {
/*     */ 
/*     */           
/* 669 */           long maxLocalWrite = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
/* 670 */           long maxGlobalWrite = this.cumulativeWrittenBytes.get();
/* 671 */           if (maxLocalWrite <= 0L) {
/* 672 */             maxLocalWrite = 0L;
/*     */           }
/* 674 */           if (maxGlobalWrite < maxLocalWrite) {
/* 675 */             maxGlobalWrite = maxLocalWrite;
/*     */           }
/* 677 */           wait = computeBalancedWait((float)maxLocalWrite, (float)maxGlobalWrite, wait);
/*     */         } 
/*     */       } 
/* 680 */       if (wait < waitGlobal) {
/* 681 */         wait = waitGlobal;
/*     */       }
/* 683 */       if (wait >= 10L) {
/* 684 */         if (logger.isDebugEnabled()) {
/* 685 */           logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + 
/* 686 */               isHandlerActive(ctx));
/*     */         }
/* 688 */         submitWrite(ctx, msg, size, wait, now, promise);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 693 */     submitWrite(ctx, msg, size, 0L, now, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void submitWrite(final ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
/*     */     ToSend newToSend;
/* 700 */     Channel channel = ctx.channel();
/* 701 */     Integer key = Integer.valueOf(channel.hashCode());
/* 702 */     PerChannel perChannel = this.channelQueues.get(key);
/* 703 */     if (perChannel == null)
/*     */     {
/*     */       
/* 706 */       perChannel = getOrSetPerChannel(ctx);
/*     */     }
/*     */     
/* 709 */     long delay = writedelay;
/* 710 */     boolean globalSizeExceeded = false;
/*     */     
/* 712 */     synchronized (perChannel) {
/* 713 */       if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
/* 714 */         this.trafficCounter.bytesRealWriteFlowControl(size);
/* 715 */         perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
/* 716 */         ctx.write(msg, promise);
/* 717 */         perChannel.lastWriteTimestamp = now;
/*     */         return;
/*     */       } 
/* 720 */       if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
/* 721 */         delay = this.maxTime;
/*     */       }
/* 723 */       newToSend = new ToSend(delay + now, msg, size, promise);
/* 724 */       perChannel.messagesQueue.addLast(newToSend);
/* 725 */       perChannel.queueSize += size;
/* 726 */       this.queuesSize.addAndGet(size);
/* 727 */       checkWriteSuspend(ctx, delay, perChannel.queueSize);
/* 728 */       if (this.queuesSize.get() > this.maxGlobalWriteSize) {
/* 729 */         globalSizeExceeded = true;
/*     */       }
/*     */     } 
/* 732 */     if (globalSizeExceeded) {
/* 733 */       setUserDefinedWritability(ctx, false);
/*     */     }
/* 735 */     final long futureNow = newToSend.relativeTimeAction;
/* 736 */     final PerChannel forSchedule = perChannel;
/* 737 */     ctx.executor().schedule(new Runnable()
/*     */         {
/*     */           public void run() {
/* 740 */             GlobalChannelTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
/*     */           }
/*     */         }delay, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) {
/* 747 */     synchronized (perChannel) {
/* 748 */       ToSend newToSend = perChannel.messagesQueue.pollFirst();
/* 749 */       for (; newToSend != null; newToSend = perChannel.messagesQueue.pollFirst()) {
/* 750 */         if (newToSend.relativeTimeAction <= now) {
/* 751 */           long size = newToSend.size;
/* 752 */           this.trafficCounter.bytesRealWriteFlowControl(size);
/* 753 */           perChannel.channelTrafficCounter.bytesRealWriteFlowControl(size);
/* 754 */           perChannel.queueSize -= size;
/* 755 */           this.queuesSize.addAndGet(-size);
/* 756 */           ctx.write(newToSend.toSend, newToSend.promise);
/* 757 */           perChannel.lastWriteTimestamp = now;
/*     */         } else {
/* 759 */           perChannel.messagesQueue.addFirst(newToSend);
/*     */           break;
/*     */         } 
/*     */       } 
/* 763 */       if (perChannel.messagesQueue.isEmpty()) {
/* 764 */         releaseWriteSuspended(ctx);
/*     */       }
/*     */     } 
/* 767 */     ctx.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 772 */     return (new StringBuilder(340)).append(super.toString())
/* 773 */       .append(" Write Channel Limit: ").append(this.writeChannelLimit)
/* 774 */       .append(" Read Channel Limit: ").append(this.readChannelLimit).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\traffic\GlobalChannelTrafficShapingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */