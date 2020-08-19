/*     */ package pro.gravit.repackage.io.netty.handler.traffic;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public class GlobalTrafficShapingHandler
/*     */   extends AbstractTrafficShapingHandler
/*     */ {
/*  83 */   private final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   private final AtomicLong queuesSize = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   long maxGlobalWriteSize = 419430400L;
/*     */ 
/*     */   
/*     */   private static final class PerChannel
/*     */   {
/*     */     ArrayDeque<GlobalTrafficShapingHandler.ToSend> messagesQueue;
/*     */     long queueSize;
/*     */     long lastWriteTimestamp;
/*     */     long lastReadTimestamp;
/*     */     
/*     */     private PerChannel() {}
/*     */   }
/*     */   
/*     */   void createGlobalTrafficCounter(ScheduledExecutorService executor) {
/* 108 */     TrafficCounter tc = new TrafficCounter(this, (ScheduledExecutorService)ObjectUtil.checkNotNull(executor, "executor"), "GlobalTC", this.checkInterval);
/*     */ 
/*     */ 
/*     */     
/* 112 */     setTrafficCounter(tc);
/* 113 */     tc.start();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int userDefinedWritabilityIndex() {
/* 118 */     return 2;
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
/*     */   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval, long maxTime) {
/* 138 */     super(writeLimit, readLimit, checkInterval, maxTime);
/* 139 */     createGlobalTrafficCounter(executor);
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
/*     */   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval) {
/* 158 */     super(writeLimit, readLimit, checkInterval);
/* 159 */     createGlobalTrafficCounter(executor);
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
/*     */   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit) {
/* 175 */     super(writeLimit, readLimit);
/* 176 */     createGlobalTrafficCounter(executor);
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
/*     */   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
/* 190 */     super(checkInterval);
/* 191 */     createGlobalTrafficCounter(executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GlobalTrafficShapingHandler(EventExecutor executor) {
/* 202 */     createGlobalTrafficCounter((ScheduledExecutorService)executor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxGlobalWriteSize() {
/* 209 */     return this.maxGlobalWriteSize;
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
/*     */   public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
/* 224 */     this.maxGlobalWriteSize = maxGlobalWriteSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long queuesSize() {
/* 231 */     return this.queuesSize.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void release() {
/* 238 */     this.trafficCounter.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
/* 243 */     Channel channel = ctx.channel();
/* 244 */     Integer key = Integer.valueOf(channel.hashCode());
/* 245 */     PerChannel perChannel = this.channelQueues.get(key);
/* 246 */     if (perChannel == null) {
/* 247 */       perChannel = new PerChannel();
/* 248 */       perChannel.messagesQueue = new ArrayDeque<ToSend>();
/* 249 */       perChannel.queueSize = 0L;
/* 250 */       perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
/* 251 */       perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp;
/* 252 */       this.channelQueues.put(key, perChannel);
/*     */     } 
/* 254 */     return perChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 259 */     getOrSetPerChannel(ctx);
/* 260 */     super.handlerAdded(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 265 */     Channel channel = ctx.channel();
/* 266 */     Integer key = Integer.valueOf(channel.hashCode());
/* 267 */     PerChannel perChannel = this.channelQueues.remove(key);
/* 268 */     if (perChannel != null)
/*     */     {
/* 270 */       synchronized (perChannel) {
/* 271 */         if (channel.isActive()) {
/* 272 */           for (ToSend toSend : perChannel.messagesQueue) {
/* 273 */             long size = calculateSize(toSend.toSend);
/* 274 */             this.trafficCounter.bytesRealWriteFlowControl(size);
/* 275 */             perChannel.queueSize -= size;
/* 276 */             this.queuesSize.addAndGet(-size);
/* 277 */             ctx.write(toSend.toSend, toSend.promise);
/*     */           } 
/*     */         } else {
/* 280 */           this.queuesSize.addAndGet(-perChannel.queueSize);
/* 281 */           for (ToSend toSend : perChannel.messagesQueue) {
/* 282 */             if (toSend.toSend instanceof ByteBuf) {
/* 283 */               ((ByteBuf)toSend.toSend).release();
/*     */             }
/*     */           } 
/*     */         } 
/* 287 */         perChannel.messagesQueue.clear();
/*     */       } 
/*     */     }
/* 290 */     releaseWriteSuspended(ctx);
/* 291 */     releaseReadSuspended(ctx);
/* 292 */     super.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
/* 297 */     Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 298 */     PerChannel perChannel = this.channelQueues.get(key);
/* 299 */     if (perChannel != null && 
/* 300 */       wait > this.maxTime && now + wait - perChannel.lastReadTimestamp > this.maxTime) {
/* 301 */       wait = this.maxTime;
/*     */     }
/*     */     
/* 304 */     return wait;
/*     */   }
/*     */ 
/*     */   
/*     */   void informReadOperation(ChannelHandlerContext ctx, long now) {
/* 309 */     Integer key = Integer.valueOf(ctx.channel().hashCode());
/* 310 */     PerChannel perChannel = this.channelQueues.get(key);
/* 311 */     if (perChannel != null)
/* 312 */       perChannel.lastReadTimestamp = now; 
/*     */   }
/*     */   
/*     */   private static final class ToSend
/*     */   {
/*     */     final long relativeTimeAction;
/*     */     final Object toSend;
/*     */     final long size;
/*     */     final ChannelPromise promise;
/*     */     
/*     */     private ToSend(long delay, Object toSend, long size, ChannelPromise promise) {
/* 323 */       this.relativeTimeAction = delay;
/* 324 */       this.toSend = toSend;
/* 325 */       this.size = size;
/* 326 */       this.promise = promise;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void submitWrite(final ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
/*     */     ToSend newToSend;
/* 334 */     Channel channel = ctx.channel();
/* 335 */     Integer key = Integer.valueOf(channel.hashCode());
/* 336 */     PerChannel perChannel = this.channelQueues.get(key);
/* 337 */     if (perChannel == null)
/*     */     {
/*     */       
/* 340 */       perChannel = getOrSetPerChannel(ctx);
/*     */     }
/*     */     
/* 343 */     long delay = writedelay;
/* 344 */     boolean globalSizeExceeded = false;
/*     */     
/* 346 */     synchronized (perChannel) {
/* 347 */       if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
/* 348 */         this.trafficCounter.bytesRealWriteFlowControl(size);
/* 349 */         ctx.write(msg, promise);
/* 350 */         perChannel.lastWriteTimestamp = now;
/*     */         return;
/*     */       } 
/* 353 */       if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
/* 354 */         delay = this.maxTime;
/*     */       }
/* 356 */       newToSend = new ToSend(delay + now, msg, size, promise);
/* 357 */       perChannel.messagesQueue.addLast(newToSend);
/* 358 */       perChannel.queueSize += size;
/* 359 */       this.queuesSize.addAndGet(size);
/* 360 */       checkWriteSuspend(ctx, delay, perChannel.queueSize);
/* 361 */       if (this.queuesSize.get() > this.maxGlobalWriteSize) {
/* 362 */         globalSizeExceeded = true;
/*     */       }
/*     */     } 
/* 365 */     if (globalSizeExceeded) {
/* 366 */       setUserDefinedWritability(ctx, false);
/*     */     }
/* 368 */     final long futureNow = newToSend.relativeTimeAction;
/* 369 */     final PerChannel forSchedule = perChannel;
/* 370 */     ctx.executor().schedule(new Runnable()
/*     */         {
/*     */           public void run() {
/* 373 */             GlobalTrafficShapingHandler.this.sendAllValid(ctx, forSchedule, futureNow);
/*     */           }
/*     */         }delay, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) {
/* 380 */     synchronized (perChannel) {
/* 381 */       ToSend newToSend = perChannel.messagesQueue.pollFirst();
/* 382 */       for (; newToSend != null; newToSend = perChannel.messagesQueue.pollFirst()) {
/* 383 */         if (newToSend.relativeTimeAction <= now) {
/* 384 */           long size = newToSend.size;
/* 385 */           this.trafficCounter.bytesRealWriteFlowControl(size);
/* 386 */           perChannel.queueSize -= size;
/* 387 */           this.queuesSize.addAndGet(-size);
/* 388 */           ctx.write(newToSend.toSend, newToSend.promise);
/* 389 */           perChannel.lastWriteTimestamp = now;
/*     */         } else {
/* 391 */           perChannel.messagesQueue.addFirst(newToSend);
/*     */           break;
/*     */         } 
/*     */       } 
/* 395 */       if (perChannel.messagesQueue.isEmpty()) {
/* 396 */         releaseWriteSuspended(ctx);
/*     */       }
/*     */     } 
/* 399 */     ctx.flush();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\traffic\GlobalTrafficShapingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */