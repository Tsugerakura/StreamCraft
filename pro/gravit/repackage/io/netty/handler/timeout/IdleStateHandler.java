/*     */ package pro.gravit.repackage.io.netty.handler.timeout;
/*     */ 
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IdleStateHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/* 100 */   private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
/*     */ 
/*     */   
/* 103 */   private final ChannelFutureListener writeListener = new ChannelFutureListener()
/*     */     {
/*     */       public void operationComplete(ChannelFuture future) throws Exception {
/* 106 */         IdleStateHandler.this.lastWriteTime = IdleStateHandler.this.ticksInNanos();
/* 107 */         IdleStateHandler.this.firstWriterIdleEvent = IdleStateHandler.this.firstAllIdleEvent = true;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean observeOutput;
/*     */ 
/*     */   
/*     */   private final long readerIdleTimeNanos;
/*     */ 
/*     */   
/*     */   private final long writerIdleTimeNanos;
/*     */ 
/*     */   
/*     */   private final long allIdleTimeNanos;
/*     */ 
/*     */   
/*     */   private ScheduledFuture<?> readerIdleTimeout;
/*     */ 
/*     */   
/*     */   private long lastReadTime;
/*     */   
/*     */   private boolean firstReaderIdleEvent = true;
/*     */   
/*     */   private ScheduledFuture<?> writerIdleTimeout;
/*     */   
/*     */   private long lastWriteTime;
/*     */   
/*     */   private boolean firstWriterIdleEvent = true;
/*     */   
/*     */   private ScheduledFuture<?> allIdleTimeout;
/*     */   
/*     */   private boolean firstAllIdleEvent = true;
/*     */   
/*     */   private byte state;
/*     */   
/*     */   private boolean reading;
/*     */   
/*     */   private long lastChangeCheckTimeStamp;
/*     */   
/*     */   private int lastMessageHashCode;
/*     */   
/*     */   private long lastPendingWriteBytes;
/*     */   
/*     */   private long lastFlushProgress;
/*     */ 
/*     */   
/*     */   public IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
/* 156 */     this(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
/* 166 */     this(false, readerIdleTime, writerIdleTime, allIdleTime, unit);
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
/*     */ 
/*     */   
/*     */   public IdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
/* 194 */     ObjectUtil.checkNotNull(unit, "unit");
/*     */     
/* 196 */     this.observeOutput = observeOutput;
/*     */     
/* 198 */     if (readerIdleTime <= 0L) {
/* 199 */       this.readerIdleTimeNanos = 0L;
/*     */     } else {
/* 201 */       this.readerIdleTimeNanos = Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
/*     */     } 
/* 203 */     if (writerIdleTime <= 0L) {
/* 204 */       this.writerIdleTimeNanos = 0L;
/*     */     } else {
/* 206 */       this.writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
/*     */     } 
/* 208 */     if (allIdleTime <= 0L) {
/* 209 */       this.allIdleTimeNanos = 0L;
/*     */     } else {
/* 211 */       this.allIdleTimeNanos = Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReaderIdleTimeInMillis() {
/* 220 */     return TimeUnit.NANOSECONDS.toMillis(this.readerIdleTimeNanos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriterIdleTimeInMillis() {
/* 228 */     return TimeUnit.NANOSECONDS.toMillis(this.writerIdleTimeNanos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getAllIdleTimeInMillis() {
/* 236 */     return TimeUnit.NANOSECONDS.toMillis(this.allIdleTimeNanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 241 */     if (ctx.channel().isActive() && ctx.channel().isRegistered())
/*     */     {
/*     */       
/* 244 */       initialize(ctx);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 253 */     destroy();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/* 259 */     if (ctx.channel().isActive()) {
/* 260 */       initialize(ctx);
/*     */     }
/* 262 */     super.channelRegistered(ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/* 270 */     initialize(ctx);
/* 271 */     super.channelActive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 276 */     destroy();
/* 277 */     super.channelInactive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 282 */     if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
/* 283 */       this.reading = true;
/* 284 */       this.firstReaderIdleEvent = this.firstAllIdleEvent = true;
/*     */     } 
/* 286 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 291 */     if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
/* 292 */       this.lastReadTime = ticksInNanos();
/* 293 */       this.reading = false;
/*     */     } 
/* 295 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 301 */     if (this.writerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
/* 302 */       ctx.write(msg, promise.unvoid()).addListener((GenericFutureListener)this.writeListener);
/*     */     } else {
/* 304 */       ctx.write(msg, promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void initialize(ChannelHandlerContext ctx) {
/* 311 */     switch (this.state) {
/*     */       case 1:
/*     */       case 2:
/*     */         return;
/*     */     } 
/*     */     
/* 317 */     this.state = 1;
/* 318 */     initOutputChanged(ctx);
/*     */     
/* 320 */     this.lastReadTime = this.lastWriteTime = ticksInNanos();
/* 321 */     if (this.readerIdleTimeNanos > 0L) {
/* 322 */       this.readerIdleTimeout = schedule(ctx, new ReaderIdleTimeoutTask(ctx), this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */     }
/*     */     
/* 325 */     if (this.writerIdleTimeNanos > 0L) {
/* 326 */       this.writerIdleTimeout = schedule(ctx, new WriterIdleTimeoutTask(ctx), this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */     }
/*     */     
/* 329 */     if (this.allIdleTimeNanos > 0L) {
/* 330 */       this.allIdleTimeout = schedule(ctx, new AllIdleTimeoutTask(ctx), this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long ticksInNanos() {
/* 339 */     return System.nanoTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
/* 346 */     return (ScheduledFuture<?>)ctx.executor().schedule(task, delay, unit);
/*     */   }
/*     */   
/*     */   private void destroy() {
/* 350 */     this.state = 2;
/*     */     
/* 352 */     if (this.readerIdleTimeout != null) {
/* 353 */       this.readerIdleTimeout.cancel(false);
/* 354 */       this.readerIdleTimeout = null;
/*     */     } 
/* 356 */     if (this.writerIdleTimeout != null) {
/* 357 */       this.writerIdleTimeout.cancel(false);
/* 358 */       this.writerIdleTimeout = null;
/*     */     } 
/* 360 */     if (this.allIdleTimeout != null) {
/* 361 */       this.allIdleTimeout.cancel(false);
/* 362 */       this.allIdleTimeout = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
/* 371 */     ctx.fireUserEventTriggered(evt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
/* 378 */     switch (state) {
/*     */       case ALL_IDLE:
/* 380 */         return first ? IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : IdleStateEvent.ALL_IDLE_STATE_EVENT;
/*     */       case READER_IDLE:
/* 382 */         return first ? IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : IdleStateEvent.READER_IDLE_STATE_EVENT;
/*     */       case WRITER_IDLE:
/* 384 */         return first ? IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : IdleStateEvent.WRITER_IDLE_STATE_EVENT;
/*     */     } 
/* 386 */     throw new IllegalArgumentException("Unhandled: state=" + state + ", first=" + first);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initOutputChanged(ChannelHandlerContext ctx) {
/* 394 */     if (this.observeOutput) {
/* 395 */       Channel channel = ctx.channel();
/* 396 */       Channel.Unsafe unsafe = channel.unsafe();
/* 397 */       ChannelOutboundBuffer buf = unsafe.outboundBuffer();
/*     */       
/* 399 */       if (buf != null) {
/* 400 */         this.lastMessageHashCode = System.identityHashCode(buf.current());
/* 401 */         this.lastPendingWriteBytes = buf.totalPendingWriteBytes();
/* 402 */         this.lastFlushProgress = buf.currentProgress();
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
/*     */   
/*     */   private boolean hasOutputChanged(ChannelHandlerContext ctx, boolean first) {
/* 415 */     if (this.observeOutput) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 422 */       if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
/* 423 */         this.lastChangeCheckTimeStamp = this.lastWriteTime;
/*     */ 
/*     */         
/* 426 */         if (!first) {
/* 427 */           return true;
/*     */         }
/*     */       } 
/*     */       
/* 431 */       Channel channel = ctx.channel();
/* 432 */       Channel.Unsafe unsafe = channel.unsafe();
/* 433 */       ChannelOutboundBuffer buf = unsafe.outboundBuffer();
/*     */       
/* 435 */       if (buf != null) {
/* 436 */         int messageHashCode = System.identityHashCode(buf.current());
/* 437 */         long pendingWriteBytes = buf.totalPendingWriteBytes();
/*     */         
/* 439 */         if (messageHashCode != this.lastMessageHashCode || pendingWriteBytes != this.lastPendingWriteBytes) {
/* 440 */           this.lastMessageHashCode = messageHashCode;
/* 441 */           this.lastPendingWriteBytes = pendingWriteBytes;
/*     */           
/* 443 */           if (!first) {
/* 444 */             return true;
/*     */           }
/*     */         } 
/*     */         
/* 448 */         long flushProgress = buf.currentProgress();
/* 449 */         if (flushProgress != this.lastFlushProgress) {
/* 450 */           this.lastFlushProgress = flushProgress;
/*     */           
/* 452 */           if (!first) {
/* 453 */             return true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 459 */     return false;
/*     */   }
/*     */   
/*     */   private static abstract class AbstractIdleTask
/*     */     implements Runnable {
/*     */     private final ChannelHandlerContext ctx;
/*     */     
/*     */     AbstractIdleTask(ChannelHandlerContext ctx) {
/* 467 */       this.ctx = ctx;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 472 */       if (!this.ctx.channel().isOpen()) {
/*     */         return;
/*     */       }
/*     */       
/* 476 */       run(this.ctx);
/*     */     }
/*     */     
/*     */     protected abstract void run(ChannelHandlerContext param1ChannelHandlerContext);
/*     */   }
/*     */   
/*     */   private final class ReaderIdleTimeoutTask
/*     */     extends AbstractIdleTask {
/*     */     ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
/* 485 */       super(ctx);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void run(ChannelHandlerContext ctx) {
/* 490 */       long nextDelay = IdleStateHandler.this.readerIdleTimeNanos;
/* 491 */       if (!IdleStateHandler.this.reading) {
/* 492 */         nextDelay -= IdleStateHandler.this.ticksInNanos() - IdleStateHandler.this.lastReadTime;
/*     */       }
/*     */       
/* 495 */       if (nextDelay <= 0L) {
/*     */         
/* 497 */         IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */         
/* 499 */         boolean first = IdleStateHandler.this.firstReaderIdleEvent;
/* 500 */         IdleStateHandler.this.firstReaderIdleEvent = false;
/*     */         
/*     */         try {
/* 503 */           IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.READER_IDLE, first);
/* 504 */           IdleStateHandler.this.channelIdle(ctx, event);
/* 505 */         } catch (Throwable t) {
/* 506 */           ctx.fireExceptionCaught(t);
/*     */         } 
/*     */       } else {
/*     */         
/* 510 */         IdleStateHandler.this.readerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private final class WriterIdleTimeoutTask
/*     */     extends AbstractIdleTask {
/*     */     WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
/* 518 */       super(ctx);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void run(ChannelHandlerContext ctx) {
/* 524 */       long lastWriteTime = IdleStateHandler.this.lastWriteTime;
/* 525 */       long nextDelay = IdleStateHandler.this.writerIdleTimeNanos - IdleStateHandler.this.ticksInNanos() - lastWriteTime;
/* 526 */       if (nextDelay <= 0L) {
/*     */         
/* 528 */         IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */         
/* 530 */         boolean first = IdleStateHandler.this.firstWriterIdleEvent;
/* 531 */         IdleStateHandler.this.firstWriterIdleEvent = false;
/*     */         
/*     */         try {
/* 534 */           if (IdleStateHandler.this.hasOutputChanged(ctx, first)) {
/*     */             return;
/*     */           }
/*     */           
/* 538 */           IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.WRITER_IDLE, first);
/* 539 */           IdleStateHandler.this.channelIdle(ctx, event);
/* 540 */         } catch (Throwable t) {
/* 541 */           ctx.fireExceptionCaught(t);
/*     */         } 
/*     */       } else {
/*     */         
/* 545 */         IdleStateHandler.this.writerIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private final class AllIdleTimeoutTask
/*     */     extends AbstractIdleTask {
/*     */     AllIdleTimeoutTask(ChannelHandlerContext ctx) {
/* 553 */       super(ctx);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void run(ChannelHandlerContext ctx) {
/* 559 */       long nextDelay = IdleStateHandler.this.allIdleTimeNanos;
/* 560 */       if (!IdleStateHandler.this.reading) {
/* 561 */         nextDelay -= IdleStateHandler.this.ticksInNanos() - Math.max(IdleStateHandler.this.lastReadTime, IdleStateHandler.this.lastWriteTime);
/*     */       }
/* 563 */       if (nextDelay <= 0L) {
/*     */ 
/*     */         
/* 566 */         IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(ctx, this, IdleStateHandler.this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
/*     */         
/* 568 */         boolean first = IdleStateHandler.this.firstAllIdleEvent;
/* 569 */         IdleStateHandler.this.firstAllIdleEvent = false;
/*     */         
/*     */         try {
/* 572 */           if (IdleStateHandler.this.hasOutputChanged(ctx, first)) {
/*     */             return;
/*     */           }
/*     */           
/* 576 */           IdleStateEvent event = IdleStateHandler.this.newIdleStateEvent(IdleState.ALL_IDLE, first);
/* 577 */           IdleStateHandler.this.channelIdle(ctx, event);
/* 578 */         } catch (Throwable t) {
/* 579 */           ctx.fireExceptionCaught(t);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 584 */         IdleStateHandler.this.allIdleTimeout = IdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\IdleStateHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */