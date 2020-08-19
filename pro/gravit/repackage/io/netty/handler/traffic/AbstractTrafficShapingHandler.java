/*     */ package pro.gravit.repackage.io.netty.handler.traffic;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.Attribute;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
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
/*     */ public abstract class AbstractTrafficShapingHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  51 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long DEFAULT_CHECK_INTERVAL = 1000L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long DEFAULT_MAX_TIME = 15000L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final long DEFAULT_MAX_SIZE = 4194304L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final long MINIMAL_WAIT = 10L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TrafficCounter trafficCounter;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long writeLimit;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile long readLimit;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   protected volatile long maxTime = 15000L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   protected volatile long checkInterval = 1000L;
/*     */ 
/*     */   
/* 100 */   static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
/* 101 */   static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class
/* 102 */       .getName() + ".REOPEN_TASK");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   volatile long maxWriteDelay = 4000L;
/*     */ 
/*     */ 
/*     */   
/* 111 */   volatile long maxWriteSize = 4194304L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int userDefinedWritabilityIndex;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setTrafficCounter(TrafficCounter newTrafficCounter) {
/* 139 */     this.trafficCounter = newTrafficCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int userDefinedWritabilityIndex() {
/* 150 */     return 1;
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
/*     */   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
/* 166 */     if (maxTime <= 0L) {
/* 167 */       throw new IllegalArgumentException("maxTime must be positive");
/*     */     }
/*     */     
/* 170 */     this.userDefinedWritabilityIndex = userDefinedWritabilityIndex();
/* 171 */     this.writeLimit = writeLimit;
/* 172 */     this.readLimit = readLimit;
/* 173 */     this.checkInterval = checkInterval;
/* 174 */     this.maxTime = maxTime;
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
/*     */   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
/* 188 */     this(writeLimit, readLimit, checkInterval, 15000L);
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
/*     */   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit) {
/* 201 */     this(writeLimit, readLimit, 1000L, 15000L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractTrafficShapingHandler() {
/* 209 */     this(0L, 0L, 1000L, 15000L);
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
/*     */   protected AbstractTrafficShapingHandler(long checkInterval) {
/* 221 */     this(0L, 0L, checkInterval, 15000L);
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
/*     */   public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
/* 238 */     configure(newWriteLimit, newReadLimit);
/* 239 */     configure(newCheckInterval);
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
/*     */   public void configure(long newWriteLimit, long newReadLimit) {
/* 254 */     this.writeLimit = newWriteLimit;
/* 255 */     this.readLimit = newReadLimit;
/* 256 */     if (this.trafficCounter != null) {
/* 257 */       this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configure(long newCheckInterval) {
/* 267 */     this.checkInterval = newCheckInterval;
/* 268 */     if (this.trafficCounter != null) {
/* 269 */       this.trafficCounter.configure(this.checkInterval);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteLimit() {
/* 277 */     return this.writeLimit;
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
/*     */   public void setWriteLimit(long writeLimit) {
/* 290 */     this.writeLimit = writeLimit;
/* 291 */     if (this.trafficCounter != null) {
/* 292 */       this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadLimit() {
/* 300 */     return this.readLimit;
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
/*     */   public void setReadLimit(long readLimit) {
/* 313 */     this.readLimit = readLimit;
/* 314 */     if (this.trafficCounter != null) {
/* 315 */       this.trafficCounter.resetAccounting(TrafficCounter.milliSecondFromNano());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCheckInterval() {
/* 323 */     return this.checkInterval;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCheckInterval(long checkInterval) {
/* 330 */     this.checkInterval = checkInterval;
/* 331 */     if (this.trafficCounter != null) {
/* 332 */       this.trafficCounter.configure(checkInterval);
/*     */     }
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
/*     */   public void setMaxTimeWait(long maxTime) {
/* 348 */     if (maxTime <= 0L) {
/* 349 */       throw new IllegalArgumentException("maxTime must be positive");
/*     */     }
/* 351 */     this.maxTime = maxTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxTimeWait() {
/* 358 */     return this.maxTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxWriteDelay() {
/* 365 */     return this.maxWriteDelay;
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
/*     */   public void setMaxWriteDelay(long maxWriteDelay) {
/* 379 */     if (maxWriteDelay <= 0L) {
/* 380 */       throw new IllegalArgumentException("maxWriteDelay must be positive");
/*     */     }
/* 382 */     this.maxWriteDelay = maxWriteDelay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxWriteSize() {
/* 389 */     return this.maxWriteSize;
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
/*     */   public void setMaxWriteSize(long maxWriteSize) {
/* 405 */     this.maxWriteSize = maxWriteSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doAccounting(TrafficCounter counter) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ReopenReadTimerTask
/*     */     implements Runnable
/*     */   {
/*     */     final ChannelHandlerContext ctx;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ReopenReadTimerTask(ChannelHandlerContext ctx) {
/* 425 */       this.ctx = ctx;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 430 */       Channel channel = this.ctx.channel();
/* 431 */       ChannelConfig config = channel.config();
/* 432 */       if (!config.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
/*     */ 
/*     */         
/* 435 */         if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
/* 436 */           AbstractTrafficShapingHandler.logger.debug("Not unsuspend: " + config.isAutoRead() + ':' + 
/* 437 */               AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
/*     */         }
/* 439 */         channel.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
/*     */       } else {
/*     */         
/* 442 */         if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
/* 443 */           if (config.isAutoRead() && !AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
/* 444 */             if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
/* 445 */               AbstractTrafficShapingHandler.logger.debug("Unsuspend: " + config.isAutoRead() + ':' + 
/* 446 */                   AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
/*     */             }
/*     */           }
/* 449 */           else if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
/* 450 */             AbstractTrafficShapingHandler.logger.debug("Normal unsuspend: " + config.isAutoRead() + ':' + 
/* 451 */                 AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
/*     */           } 
/*     */         }
/*     */         
/* 455 */         channel.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
/* 456 */         config.setAutoRead(true);
/* 457 */         channel.read();
/*     */       } 
/* 459 */       if (AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
/* 460 */         AbstractTrafficShapingHandler.logger.debug("Unsuspend final status => " + config.isAutoRead() + ':' + 
/* 461 */             AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void releaseReadSuspended(ChannelHandlerContext ctx) {
/* 470 */     Channel channel = ctx.channel();
/* 471 */     channel.attr(READ_SUSPENDED).set(Boolean.valueOf(false));
/* 472 */     channel.config().setAutoRead(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 477 */     long size = calculateSize(msg);
/* 478 */     long now = TrafficCounter.milliSecondFromNano();
/* 479 */     if (size > 0L) {
/*     */       
/* 481 */       long wait = this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime, now);
/* 482 */       wait = checkWaitReadTime(ctx, wait, now);
/* 483 */       if (wait >= 10L) {
/*     */ 
/*     */         
/* 486 */         Channel channel = ctx.channel();
/* 487 */         ChannelConfig config = channel.config();
/* 488 */         if (logger.isDebugEnabled()) {
/* 489 */           logger.debug("Read suspend: " + wait + ':' + config.isAutoRead() + ':' + 
/* 490 */               isHandlerActive(ctx));
/*     */         }
/* 492 */         if (config.isAutoRead() && isHandlerActive(ctx)) {
/* 493 */           config.setAutoRead(false);
/* 494 */           channel.attr(READ_SUSPENDED).set(Boolean.valueOf(true));
/*     */ 
/*     */           
/* 497 */           Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
/* 498 */           Runnable reopenTask = (Runnable)attr.get();
/* 499 */           if (reopenTask == null) {
/* 500 */             reopenTask = new ReopenReadTimerTask(ctx);
/* 501 */             attr.set(reopenTask);
/*     */           } 
/* 503 */           ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
/* 504 */           if (logger.isDebugEnabled()) {
/* 505 */             logger.debug("Suspend final status => " + config.isAutoRead() + ':' + 
/* 506 */                 isHandlerActive(ctx) + " will reopened at: " + wait);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 511 */     informReadOperation(ctx, now);
/* 512 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
/* 523 */     return wait;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void informReadOperation(ChannelHandlerContext ctx, long now) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
/* 535 */     Boolean suspended = (Boolean)ctx.channel().attr(READ_SUSPENDED).get();
/* 536 */     return (suspended == null || Boolean.FALSE.equals(suspended));
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) {
/* 541 */     if (isHandlerActive(ctx))
/*     */     {
/* 543 */       ctx.read();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 550 */     long size = calculateSize(msg);
/* 551 */     long now = TrafficCounter.milliSecondFromNano();
/* 552 */     if (size > 0L) {
/*     */       
/* 554 */       long wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime, now);
/* 555 */       if (wait >= 10L) {
/* 556 */         if (logger.isDebugEnabled()) {
/* 557 */           logger.debug("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + 
/* 558 */               isHandlerActive(ctx));
/*     */         }
/* 560 */         submitWrite(ctx, msg, size, wait, now, promise);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 565 */     submitWrite(ctx, msg, size, 0L, now, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void submitWrite(ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise) {
/* 571 */     submitWrite(ctx, msg, calculateSize(msg), delay, 
/* 572 */         TrafficCounter.milliSecondFromNano(), promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/* 580 */     setUserDefinedWritability(ctx, true);
/* 581 */     super.channelRegistered(ctx);
/*     */   }
/*     */   
/*     */   void setUserDefinedWritability(ChannelHandlerContext ctx, boolean writable) {
/* 585 */     ChannelOutboundBuffer cob = ctx.channel().unsafe().outboundBuffer();
/* 586 */     if (cob != null) {
/* 587 */       cob.setUserDefinedWritability(this.userDefinedWritabilityIndex, writable);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkWriteSuspend(ChannelHandlerContext ctx, long delay, long queueSize) {
/* 598 */     if (queueSize > this.maxWriteSize || delay > this.maxWriteDelay) {
/* 599 */       setUserDefinedWritability(ctx, false);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void releaseWriteSuspended(ChannelHandlerContext ctx) {
/* 606 */     setUserDefinedWritability(ctx, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TrafficCounter trafficCounter() {
/* 614 */     return this.trafficCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 625 */     StringBuilder builder = (new StringBuilder(290)).append("TrafficShaping with Write Limit: ").append(this.writeLimit).append(" Read Limit: ").append(this.readLimit).append(" CheckInterval: ").append(this.checkInterval).append(" maxDelay: ").append(this.maxWriteDelay).append(" maxSize: ").append(this.maxWriteSize).append(" and Counter: ");
/* 626 */     if (this.trafficCounter != null) {
/* 627 */       builder.append(this.trafficCounter);
/*     */     } else {
/* 629 */       builder.append("none");
/*     */     } 
/* 631 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long calculateSize(Object msg) {
/* 642 */     if (msg instanceof ByteBuf) {
/* 643 */       return ((ByteBuf)msg).readableBytes();
/*     */     }
/* 645 */     if (msg instanceof ByteBufHolder) {
/* 646 */       return ((ByteBufHolder)msg).content().readableBytes();
/*     */     }
/* 648 */     return -1L;
/*     */   }
/*     */   
/*     */   abstract void submitWrite(ChannelHandlerContext paramChannelHandlerContext, Object paramObject, long paramLong1, long paramLong2, long paramLong3, ChannelPromise paramChannelPromise);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\traffic\AbstractTrafficShapingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */