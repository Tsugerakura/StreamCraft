/*     */ package pro.gravit.repackage.io.netty.channel.pool;
/*     */ 
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.bootstrap.Bootstrap;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GlobalEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
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
/*     */ public class FixedChannelPool
/*     */   extends SimpleChannelPool
/*     */ {
/*     */   private final EventExecutor executor;
/*     */   private final long acquireTimeoutNanos;
/*     */   private final Runnable timeoutTask;
/*     */   
/*     */   public enum AcquireTimeoutAction
/*     */   {
/*  46 */     NEW,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     FAIL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   private final Queue<AcquireTask> pendingAcquireQueue = new ArrayDeque<AcquireTask>();
/*     */   private final int maxConnections;
/*     */   private final int maxPendingAcquires;
/*  63 */   private final AtomicInteger acquiredChannelCount = new AtomicInteger();
/*     */ 
/*     */ 
/*     */   
/*     */   private int pendingAcquireCount;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean closed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections) {
/*  77 */     this(bootstrap, handler, maxConnections, 2147483647);
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
/*     */   public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections, int maxPendingAcquires) {
/*  93 */     this(bootstrap, handler, ChannelHealthChecker.ACTIVE, null, -1L, maxConnections, maxPendingAcquires);
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
/*     */   public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires) {
/* 118 */     this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, true);
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
/*     */   public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck) {
/* 145 */     this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, releaseHealthCheck, true);
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
/*     */ 
/*     */   
/*     */   public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck, boolean lastRecentUsed) {
/* 175 */     super(bootstrap, handler, healthCheck, releaseHealthCheck, lastRecentUsed);
/* 176 */     if (maxConnections < 1) {
/* 177 */       throw new IllegalArgumentException("maxConnections: " + maxConnections + " (expected: >= 1)");
/*     */     }
/* 179 */     if (maxPendingAcquires < 1) {
/* 180 */       throw new IllegalArgumentException("maxPendingAcquires: " + maxPendingAcquires + " (expected: >= 1)");
/*     */     }
/* 182 */     if (action == null && acquireTimeoutMillis == -1L)
/* 183 */     { this.timeoutTask = null;
/* 184 */       this.acquireTimeoutNanos = -1L; }
/* 185 */     else { if (action == null && acquireTimeoutMillis != -1L)
/* 186 */         throw new NullPointerException("action"); 
/* 187 */       if (action != null && acquireTimeoutMillis < 0L) {
/* 188 */         throw new IllegalArgumentException("acquireTimeoutMillis: " + acquireTimeoutMillis + " (expected: >= 0)");
/*     */       }
/* 190 */       this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(acquireTimeoutMillis);
/* 191 */       switch (action) {
/*     */         case FAIL:
/* 193 */           this.timeoutTask = new TimeoutTask()
/*     */             {
/*     */               public void onTimeout(FixedChannelPool.AcquireTask task)
/*     */               {
/* 197 */                 task.promise.setFailure(new TimeoutException("Acquire operation took longer then configured maximum time")
/*     */                     {
/*     */                       public synchronized Throwable fillInStackTrace()
/*     */                       {
/* 201 */                         return this;
/*     */                       }
/*     */                     });
/*     */               }
/*     */             };
/*     */           break;
/*     */         case NEW:
/* 208 */           this.timeoutTask = new TimeoutTask()
/*     */             {
/*     */               
/*     */               public void onTimeout(FixedChannelPool.AcquireTask task)
/*     */               {
/* 213 */                 task.acquired();
/*     */                 
/* 215 */                 FixedChannelPool.this.acquire(task.promise);
/*     */               }
/*     */             };
/*     */           break;
/*     */         default:
/* 220 */           throw new Error();
/*     */       }  }
/*     */     
/* 223 */     this.executor = (EventExecutor)bootstrap.config().group().next();
/* 224 */     this.maxConnections = maxConnections;
/* 225 */     this.maxPendingAcquires = maxPendingAcquires;
/*     */   }
/*     */ 
/*     */   
/*     */   public int acquiredChannelCount() {
/* 230 */     return this.acquiredChannelCount.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<Channel> acquire(final Promise<Channel> promise) {
/*     */     try {
/* 236 */       if (this.executor.inEventLoop()) {
/* 237 */         acquire0(promise);
/*     */       } else {
/* 239 */         this.executor.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 242 */                 FixedChannelPool.this.acquire0(promise);
/*     */               }
/*     */             });
/*     */       } 
/* 246 */     } catch (Throwable cause) {
/* 247 */       promise.setFailure(cause);
/*     */     } 
/* 249 */     return (Future<Channel>)promise;
/*     */   }
/*     */   
/*     */   private void acquire0(Promise<Channel> promise) {
/* 253 */     assert this.executor.inEventLoop();
/*     */     
/* 255 */     if (this.closed) {
/* 256 */       promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
/*     */       return;
/*     */     } 
/* 259 */     if (this.acquiredChannelCount.get() < this.maxConnections) {
/* 260 */       assert this.acquiredChannelCount.get() >= 0;
/*     */ 
/*     */ 
/*     */       
/* 264 */       Promise<Channel> p = this.executor.newPromise();
/* 265 */       AcquireListener l = new AcquireListener(promise);
/* 266 */       l.acquired();
/* 267 */       p.addListener((GenericFutureListener)l);
/* 268 */       super.acquire(p);
/*     */     } else {
/* 270 */       if (this.pendingAcquireCount >= this.maxPendingAcquires) {
/* 271 */         tooManyOutstanding(promise);
/*     */       } else {
/* 273 */         AcquireTask task = new AcquireTask(promise);
/* 274 */         if (this.pendingAcquireQueue.offer(task)) {
/* 275 */           this.pendingAcquireCount++;
/*     */           
/* 277 */           if (this.timeoutTask != null) {
/* 278 */             task.timeoutFuture = (ScheduledFuture<?>)this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
/*     */           }
/*     */         } else {
/* 281 */           tooManyOutstanding(promise);
/*     */         } 
/*     */       } 
/*     */       
/* 285 */       assert this.pendingAcquireCount > 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void tooManyOutstanding(Promise<?> promise) {
/* 290 */     promise.setFailure(new IllegalStateException("Too many outstanding acquire operations"));
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<Void> release(final Channel channel, final Promise<Void> promise) {
/* 295 */     ObjectUtil.checkNotNull(promise, "promise");
/* 296 */     Promise<Void> p = this.executor.newPromise();
/* 297 */     super.release(channel, p.addListener((GenericFutureListener)new FutureListener<Void>()
/*     */           {
/*     */             public void operationComplete(Future<Void> future) throws Exception
/*     */             {
/* 301 */               assert FixedChannelPool.this.executor.inEventLoop();
/*     */               
/* 303 */               if (FixedChannelPool.this.closed) {
/*     */                 
/* 305 */                 channel.close();
/* 306 */                 promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
/*     */                 
/*     */                 return;
/*     */               } 
/* 310 */               if (future.isSuccess()) {
/* 311 */                 FixedChannelPool.this.decrementAndRunTaskQueue();
/* 312 */                 promise.setSuccess(null);
/*     */               } else {
/* 314 */                 Throwable cause = future.cause();
/*     */                 
/* 316 */                 if (!(cause instanceof IllegalArgumentException)) {
/* 317 */                   FixedChannelPool.this.decrementAndRunTaskQueue();
/*     */                 }
/* 319 */                 promise.setFailure(future.cause());
/*     */               } 
/*     */             }
/*     */           }));
/* 323 */     return (Future<Void>)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   private void decrementAndRunTaskQueue() {
/* 328 */     int currentCount = this.acquiredChannelCount.decrementAndGet();
/* 329 */     assert currentCount >= 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     runTaskQueue();
/*     */   }
/*     */   
/*     */   private void runTaskQueue() {
/* 339 */     while (this.acquiredChannelCount.get() < this.maxConnections) {
/* 340 */       AcquireTask task = this.pendingAcquireQueue.poll();
/* 341 */       if (task == null) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 346 */       ScheduledFuture<?> timeoutFuture = task.timeoutFuture;
/* 347 */       if (timeoutFuture != null) {
/* 348 */         timeoutFuture.cancel(false);
/*     */       }
/*     */       
/* 351 */       this.pendingAcquireCount--;
/* 352 */       task.acquired();
/*     */       
/* 354 */       super.acquire(task.promise);
/*     */     } 
/*     */ 
/*     */     
/* 358 */     assert this.pendingAcquireCount >= 0;
/* 359 */     assert this.acquiredChannelCount.get() >= 0;
/*     */   }
/*     */   
/*     */   private final class AcquireTask
/*     */     extends AcquireListener {
/*     */     final Promise<Channel> promise;
/* 365 */     final long expireNanoTime = System.nanoTime() + FixedChannelPool.this.acquireTimeoutNanos;
/*     */     ScheduledFuture<?> timeoutFuture;
/*     */     
/*     */     AcquireTask(Promise<Channel> promise) {
/* 369 */       super(promise);
/*     */ 
/*     */       
/* 372 */       this.promise = FixedChannelPool.this.executor.newPromise().addListener((GenericFutureListener)this);
/*     */     } }
/*     */   
/*     */   private abstract class TimeoutTask implements Runnable {
/*     */     private TimeoutTask() {}
/*     */     
/*     */     public final void run() {
/* 379 */       assert FixedChannelPool.this.executor.inEventLoop();
/* 380 */       long nanoTime = System.nanoTime();
/*     */       while (true) {
/* 382 */         FixedChannelPool.AcquireTask task = FixedChannelPool.this.pendingAcquireQueue.peek();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 387 */         if (task == null || nanoTime - task.expireNanoTime < 0L) {
/*     */           break;
/*     */         }
/* 390 */         FixedChannelPool.this.pendingAcquireQueue.remove();
/*     */         
/* 392 */         --FixedChannelPool.this.pendingAcquireCount;
/* 393 */         onTimeout(task);
/*     */       } 
/*     */     }
/*     */     
/*     */     public abstract void onTimeout(FixedChannelPool.AcquireTask param1AcquireTask);
/*     */   }
/*     */   
/*     */   private class AcquireListener implements FutureListener<Channel> {
/*     */     private final Promise<Channel> originalPromise;
/*     */     protected boolean acquired;
/*     */     
/*     */     AcquireListener(Promise<Channel> originalPromise) {
/* 405 */       this.originalPromise = originalPromise;
/*     */     }
/*     */ 
/*     */     
/*     */     public void operationComplete(Future<Channel> future) throws Exception {
/* 410 */       assert FixedChannelPool.this.executor.inEventLoop();
/*     */       
/* 412 */       if (FixedChannelPool.this.closed) {
/* 413 */         if (future.isSuccess())
/*     */         {
/* 415 */           ((Channel)future.getNow()).close();
/*     */         }
/* 417 */         this.originalPromise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
/*     */         
/*     */         return;
/*     */       } 
/* 421 */       if (future.isSuccess()) {
/* 422 */         this.originalPromise.setSuccess(future.getNow());
/*     */       } else {
/* 424 */         if (this.acquired) {
/* 425 */           FixedChannelPool.this.decrementAndRunTaskQueue();
/*     */         } else {
/* 427 */           FixedChannelPool.this.runTaskQueue();
/*     */         } 
/*     */         
/* 430 */         this.originalPromise.setFailure(future.cause());
/*     */       } 
/*     */     }
/*     */     
/*     */     public void acquired() {
/* 435 */       if (this.acquired) {
/*     */         return;
/*     */       }
/* 438 */       FixedChannelPool.this.acquiredChannelCount.incrementAndGet();
/* 439 */       this.acquired = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 446 */       closeAsync().await();
/* 447 */     } catch (InterruptedException e) {
/* 448 */       Thread.currentThread().interrupt();
/* 449 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Future<Void> closeAsync() {
/* 460 */     if (this.executor.inEventLoop()) {
/* 461 */       return close0();
/*     */     }
/* 463 */     final Promise<Void> closeComplete = this.executor.newPromise();
/* 464 */     this.executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 467 */             FixedChannelPool.this.close0().addListener((GenericFutureListener)new FutureListener<Void>()
/*     */                 {
/*     */                   public void operationComplete(Future<Void> f) throws Exception {
/* 470 */                     if (f.isSuccess()) {
/* 471 */                       closeComplete.setSuccess(null);
/*     */                     } else {
/* 473 */                       closeComplete.setFailure(f.cause());
/*     */                     } 
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/* 479 */     return (Future<Void>)closeComplete;
/*     */   }
/*     */ 
/*     */   
/*     */   private Future<Void> close0() {
/* 484 */     assert this.executor.inEventLoop();
/*     */     
/* 486 */     if (!this.closed) {
/* 487 */       this.closed = true;
/*     */       while (true) {
/* 489 */         AcquireTask task = this.pendingAcquireQueue.poll();
/* 490 */         if (task == null) {
/*     */           break;
/*     */         }
/* 493 */         ScheduledFuture<?> f = task.timeoutFuture;
/* 494 */         if (f != null) {
/* 495 */           f.cancel(false);
/*     */         }
/* 497 */         task.promise.setFailure(new ClosedChannelException());
/*     */       } 
/* 499 */       this.acquiredChannelCount.set(0);
/* 500 */       this.pendingAcquireCount = 0;
/*     */ 
/*     */ 
/*     */       
/* 504 */       return GlobalEventExecutor.INSTANCE.submit(new Callable<Void>()
/*     */           {
/*     */             public Void call() throws Exception {
/* 507 */               FixedChannelPool.this.close();
/* 508 */               return null;
/*     */             }
/*     */           });
/*     */     } 
/*     */     
/* 513 */     return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\FixedChannelPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */