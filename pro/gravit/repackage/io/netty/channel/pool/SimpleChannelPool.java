/*     */ package pro.gravit.repackage.io.netty.channel.pool;
/*     */ 
/*     */ import java.util.Deque;
/*     */ import java.util.concurrent.Callable;
/*     */ import pro.gravit.repackage.io.netty.bootstrap.Bootstrap;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInitializer;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GlobalEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
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
/*     */ public class SimpleChannelPool
/*     */   implements ChannelPool
/*     */ {
/*  45 */   private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("pro.gravit.repackage.io.netty.channel.pool.SimpleChannelPool");
/*  46 */   private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
/*     */   
/*     */   private final ChannelPoolHandler handler;
/*     */   
/*     */   private final ChannelHealthChecker healthCheck;
/*     */   
/*     */   private final Bootstrap bootstrap;
/*     */   
/*     */   private final boolean releaseHealthCheck;
/*     */   
/*     */   private final boolean lastRecentUsed;
/*     */ 
/*     */   
/*     */   public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
/*  60 */     this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
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
/*     */   public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck) {
/*  72 */     this(bootstrap, handler, healthCheck, true);
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
/*     */   public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
/*  87 */     this(bootstrap, handler, healthCheck, releaseHealthCheck, true);
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
/*     */   public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck, boolean lastRecentUsed) {
/* 103 */     this.handler = (ChannelPoolHandler)ObjectUtil.checkNotNull(handler, "handler");
/* 104 */     this.healthCheck = (ChannelHealthChecker)ObjectUtil.checkNotNull(healthCheck, "healthCheck");
/* 105 */     this.releaseHealthCheck = releaseHealthCheck;
/*     */     
/* 107 */     this.bootstrap = ((Bootstrap)ObjectUtil.checkNotNull(bootstrap, "bootstrap")).clone();
/* 108 */     this.bootstrap.handler((ChannelHandler)new ChannelInitializer<Channel>()
/*     */         {
/*     */           protected void initChannel(Channel ch) throws Exception {
/* 111 */             assert ch.eventLoop().inEventLoop();
/* 112 */             handler.channelCreated(ch);
/*     */           }
/*     */         });
/* 115 */     this.lastRecentUsed = lastRecentUsed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Bootstrap bootstrap() {
/* 124 */     return this.bootstrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ChannelPoolHandler handler() {
/* 133 */     return this.handler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ChannelHealthChecker healthChecker() {
/* 142 */     return this.healthCheck;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean releaseHealthCheck() {
/* 152 */     return this.releaseHealthCheck;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Future<Channel> acquire() {
/* 157 */     return acquire(this.bootstrap.config().group().next().newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<Channel> acquire(Promise<Channel> promise) {
/* 162 */     return acquireHealthyFromPoolOrNew((Promise<Channel>)ObjectUtil.checkNotNull(promise, "promise"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
/*     */     try {
/* 172 */       final Channel ch = pollChannel();
/* 173 */       if (ch == null) {
/*     */         
/* 175 */         Bootstrap bs = this.bootstrap.clone();
/* 176 */         bs.attr(POOL_KEY, this);
/* 177 */         ChannelFuture f = connectChannel(bs);
/* 178 */         if (f.isDone()) {
/* 179 */           notifyConnect(f, promise);
/*     */         } else {
/* 181 */           f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */               {
/*     */                 public void operationComplete(ChannelFuture future) throws Exception {
/* 184 */                   SimpleChannelPool.this.notifyConnect(future, promise);
/*     */                 }
/*     */               });
/*     */         } 
/* 188 */         return (Future<Channel>)promise;
/*     */       } 
/* 190 */       EventLoop loop = ch.eventLoop();
/* 191 */       if (loop.inEventLoop()) {
/* 192 */         doHealthCheck(ch, promise);
/*     */       } else {
/* 194 */         loop.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 197 */                 SimpleChannelPool.this.doHealthCheck(ch, promise);
/*     */               }
/*     */             });
/*     */       } 
/* 201 */     } catch (Throwable cause) {
/* 202 */       promise.tryFailure(cause);
/*     */     } 
/* 204 */     return (Future<Channel>)promise;
/*     */   }
/*     */   
/*     */   private void notifyConnect(ChannelFuture future, Promise<Channel> promise) throws Exception {
/* 208 */     if (future.isSuccess()) {
/* 209 */       Channel channel = future.channel();
/* 210 */       this.handler.channelAcquired(channel);
/* 211 */       if (!promise.trySuccess(channel))
/*     */       {
/* 213 */         release(channel);
/*     */       }
/*     */     } else {
/* 216 */       promise.tryFailure(future.cause());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doHealthCheck(final Channel ch, final Promise<Channel> promise) {
/* 221 */     assert ch.eventLoop().inEventLoop();
/*     */     
/* 223 */     Future<Boolean> f = this.healthCheck.isHealthy(ch);
/* 224 */     if (f.isDone()) {
/* 225 */       notifyHealthCheck(f, ch, promise);
/*     */     } else {
/* 227 */       f.addListener((GenericFutureListener)new FutureListener<Boolean>()
/*     */           {
/*     */             public void operationComplete(Future<Boolean> future) throws Exception {
/* 230 */               SimpleChannelPool.this.notifyHealthCheck(future, ch, promise);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   private void notifyHealthCheck(Future<Boolean> future, Channel ch, Promise<Channel> promise) {
/* 237 */     assert ch.eventLoop().inEventLoop();
/*     */     
/* 239 */     if (future.isSuccess()) {
/* 240 */       if (((Boolean)future.getNow()).booleanValue()) {
/*     */         try {
/* 242 */           ch.attr(POOL_KEY).set(this);
/* 243 */           this.handler.channelAcquired(ch);
/* 244 */           promise.setSuccess(ch);
/* 245 */         } catch (Throwable cause) {
/* 246 */           closeAndFail(ch, cause, promise);
/*     */         } 
/*     */       } else {
/* 249 */         closeChannel(ch);
/* 250 */         acquireHealthyFromPoolOrNew(promise);
/*     */       } 
/*     */     } else {
/* 253 */       closeChannel(ch);
/* 254 */       acquireHealthyFromPoolOrNew(promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ChannelFuture connectChannel(Bootstrap bs) {
/* 265 */     return bs.connect();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Future<Void> release(Channel channel) {
/* 270 */     return release(channel, channel.eventLoop().newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<Void> release(final Channel channel, final Promise<Void> promise) {
/* 275 */     ObjectUtil.checkNotNull(channel, "channel");
/* 276 */     ObjectUtil.checkNotNull(promise, "promise");
/*     */     try {
/* 278 */       EventLoop loop = channel.eventLoop();
/* 279 */       if (loop.inEventLoop()) {
/* 280 */         doReleaseChannel(channel, promise);
/*     */       } else {
/* 282 */         loop.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 285 */                 SimpleChannelPool.this.doReleaseChannel(channel, promise);
/*     */               }
/*     */             });
/*     */       } 
/* 289 */     } catch (Throwable cause) {
/* 290 */       closeAndFail(channel, cause, promise);
/*     */     } 
/* 292 */     return (Future<Void>)promise;
/*     */   }
/*     */   
/*     */   private void doReleaseChannel(Channel channel, Promise<Void> promise) {
/* 296 */     assert channel.eventLoop().inEventLoop();
/*     */     
/* 298 */     if (channel.attr(POOL_KEY).getAndSet(null) != this) {
/* 299 */       closeAndFail(channel, new IllegalArgumentException("Channel " + channel + " was not acquired from this ChannelPool"), promise);
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */         
/* 306 */         if (this.releaseHealthCheck) {
/* 307 */           doHealthCheckOnRelease(channel, promise);
/*     */         } else {
/* 309 */           releaseAndOffer(channel, promise);
/*     */         } 
/* 311 */       } catch (Throwable cause) {
/* 312 */         closeAndFail(channel, cause, promise);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doHealthCheckOnRelease(final Channel channel, final Promise<Void> promise) throws Exception {
/* 318 */     final Future<Boolean> f = this.healthCheck.isHealthy(channel);
/* 319 */     if (f.isDone()) {
/* 320 */       releaseAndOfferIfHealthy(channel, promise, f);
/*     */     } else {
/* 322 */       f.addListener((GenericFutureListener)new FutureListener<Boolean>()
/*     */           {
/*     */             public void operationComplete(Future<Boolean> future) throws Exception {
/* 325 */               SimpleChannelPool.this.releaseAndOfferIfHealthy(channel, promise, f);
/*     */             }
/*     */           });
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
/*     */   private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) throws Exception {
/* 340 */     if (((Boolean)future.getNow()).booleanValue()) {
/* 341 */       releaseAndOffer(channel, promise);
/*     */     } else {
/* 343 */       this.handler.channelReleased(channel);
/* 344 */       promise.setSuccess(null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
/* 349 */     if (offerChannel(channel)) {
/* 350 */       this.handler.channelReleased(channel);
/* 351 */       promise.setSuccess(null);
/*     */     } else {
/* 353 */       closeAndFail(channel, new IllegalStateException("ChannelPool full")
/*     */           {
/*     */             public synchronized Throwable fillInStackTrace() {
/* 356 */               return this;
/*     */             }
/*     */           }promise);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void closeChannel(Channel channel) {
/* 363 */     channel.attr(POOL_KEY).getAndSet(null);
/* 364 */     channel.close();
/*     */   }
/*     */   
/*     */   private void closeAndFail(Channel channel, Throwable cause, Promise<?> promise) {
/* 368 */     closeChannel(channel);
/* 369 */     promise.tryFailure(cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Channel pollChannel() {
/* 380 */     return this.lastRecentUsed ? this.deque.pollLast() : this.deque.pollFirst();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean offerChannel(Channel channel) {
/* 391 */     return this.deque.offer(channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     while (true) {
/* 397 */       Channel channel = pollChannel();
/* 398 */       if (channel == null) {
/*     */         break;
/*     */       }
/*     */       
/* 402 */       channel.close().awaitUninterruptibly();
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
/* 413 */     return GlobalEventExecutor.INSTANCE.submit(new Callable<Void>()
/*     */         {
/*     */           public Void call() throws Exception {
/* 416 */             SimpleChannelPool.this.close();
/* 417 */             return null;
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\SimpleChannelPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */