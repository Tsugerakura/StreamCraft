/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PromiseNotificationUtil;
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
/*     */ public final class DelegatingChannelPromiseNotifier
/*     */   implements ChannelPromise, ChannelFutureListener
/*     */ {
/*  34 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DelegatingChannelPromiseNotifier.class);
/*     */   private final ChannelPromise delegate;
/*     */   private final boolean logNotifyFailure;
/*     */   
/*     */   public DelegatingChannelPromiseNotifier(ChannelPromise delegate) {
/*  39 */     this(delegate, !(delegate instanceof VoidChannelPromise));
/*     */   }
/*     */   
/*     */   public DelegatingChannelPromiseNotifier(ChannelPromise delegate, boolean logNotifyFailure) {
/*  43 */     this.delegate = (ChannelPromise)ObjectUtil.checkNotNull(delegate, "delegate");
/*  44 */     this.logNotifyFailure = logNotifyFailure;
/*     */   }
/*     */ 
/*     */   
/*     */   public void operationComplete(ChannelFuture future) throws Exception {
/*  49 */     InternalLogger internalLogger = this.logNotifyFailure ? logger : null;
/*  50 */     if (future.isSuccess()) {
/*  51 */       Void result = (Void)future.get();
/*  52 */       PromiseNotificationUtil.trySuccess(this.delegate, result, internalLogger);
/*  53 */     } else if (future.isCancelled()) {
/*  54 */       PromiseNotificationUtil.tryCancel(this.delegate, internalLogger);
/*     */     } else {
/*  56 */       Throwable cause = future.cause();
/*  57 */       PromiseNotificationUtil.tryFailure(this.delegate, cause, internalLogger);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Channel channel() {
/*  63 */     return this.delegate.channel();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setSuccess(Void result) {
/*  68 */     this.delegate.setSuccess(result);
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setSuccess() {
/*  74 */     this.delegate.setSuccess();
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess() {
/*  80 */     return this.delegate.trySuccess();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess(Void result) {
/*  85 */     return this.delegate.trySuccess(result);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setFailure(Throwable cause) {
/*  90 */     this.delegate.setFailure(cause);
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  96 */     this.delegate.addListener(listener);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 102 */     this.delegate.addListeners(listeners);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/* 108 */     this.delegate.removeListener(listener);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 114 */     this.delegate.removeListeners(listeners);
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryFailure(Throwable cause) {
/* 120 */     return this.delegate.tryFailure(cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setUncancellable() {
/* 125 */     return this.delegate.setUncancellable();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise await() throws InterruptedException {
/* 130 */     this.delegate.await();
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise awaitUninterruptibly() {
/* 136 */     this.delegate.awaitUninterruptibly();
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVoid() {
/* 142 */     return this.delegate.isVoid();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise unvoid() {
/* 147 */     return isVoid() ? new DelegatingChannelPromiseNotifier(this.delegate.unvoid()) : this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
/* 152 */     return this.delegate.await(timeout, unit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeoutMillis) throws InterruptedException {
/* 157 */     return this.delegate.await(timeoutMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
/* 162 */     return this.delegate.awaitUninterruptibly(timeout, unit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeoutMillis) {
/* 167 */     return this.delegate.awaitUninterruptibly(timeoutMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public Void getNow() {
/* 172 */     return (Void)this.delegate.getNow();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean cancel(boolean mayInterruptIfRunning) {
/* 177 */     return this.delegate.cancel(mayInterruptIfRunning);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancelled() {
/* 182 */     return this.delegate.isCancelled();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDone() {
/* 187 */     return this.delegate.isDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public Void get() throws InterruptedException, ExecutionException {
/* 192 */     return (Void)this.delegate.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
/* 197 */     return (Void)this.delegate.get(timeout, unit);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise sync() throws InterruptedException {
/* 202 */     this.delegate.sync();
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise syncUninterruptibly() {
/* 208 */     this.delegate.syncUninterruptibly();
/* 209 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSuccess() {
/* 214 */     return this.delegate.isSuccess();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancellable() {
/* 219 */     return this.delegate.isCancellable();
/*     */   }
/*     */ 
/*     */   
/*     */   public Throwable cause() {
/* 224 */     return this.delegate.cause();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DelegatingChannelPromiseNotifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */