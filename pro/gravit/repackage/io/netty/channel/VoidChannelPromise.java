/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.AbstractFuture;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class VoidChannelPromise
/*     */   extends AbstractFuture<Void>
/*     */   implements ChannelPromise
/*     */ {
/*     */   private final Channel channel;
/*     */   private final ChannelFutureListener fireExceptionListener;
/*     */   
/*     */   public VoidChannelPromise(Channel channel, boolean fireException) {
/*  39 */     ObjectUtil.checkNotNull(channel, "channel");
/*  40 */     this.channel = channel;
/*  41 */     if (fireException) {
/*  42 */       this.fireExceptionListener = new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/*  45 */             Throwable cause = future.cause();
/*  46 */             if (cause != null) {
/*  47 */               VoidChannelPromise.this.fireException0(cause);
/*     */             }
/*     */           }
/*     */         };
/*     */     } else {
/*  52 */       this.fireExceptionListener = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  58 */     fail();
/*  59 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  64 */     fail();
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise await() throws InterruptedException {
/*  82 */     if (Thread.interrupted()) {
/*  83 */       throw new InterruptedException();
/*     */     }
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeout, TimeUnit unit) {
/*  90 */     fail();
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeoutMillis) {
/*  96 */     fail();
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise awaitUninterruptibly() {
/* 102 */     fail();
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
/* 108 */     fail();
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeoutMillis) {
/* 114 */     fail();
/* 115 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Channel channel() {
/* 120 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDone() {
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSuccess() {
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setUncancellable() {
/* 135 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancellable() {
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancelled() {
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Throwable cause() {
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise sync() {
/* 155 */     fail();
/* 156 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise syncUninterruptibly() {
/* 161 */     fail();
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise setFailure(Throwable cause) {
/* 167 */     fireException0(cause);
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise setSuccess() {
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryFailure(Throwable cause) {
/* 178 */     fireException0(cause);
/* 179 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean cancel(boolean mayInterruptIfRunning) {
/* 189 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess() {
/* 194 */     return false;
/*     */   }
/*     */   
/*     */   private static void fail() {
/* 198 */     throw new IllegalStateException("void future");
/*     */   }
/*     */ 
/*     */   
/*     */   public VoidChannelPromise setSuccess(Void result) {
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess(Void result) {
/* 208 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Void getNow() {
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise unvoid() {
/* 218 */     ChannelPromise promise = new DefaultChannelPromise(this.channel);
/* 219 */     if (this.fireExceptionListener != null) {
/* 220 */       promise.addListener(this.fireExceptionListener);
/*     */     }
/* 222 */     return promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVoid() {
/* 227 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fireException0(Throwable cause) {
/* 235 */     if (this.fireExceptionListener != null && this.channel.isRegistered())
/* 236 */       this.channel.pipeline().fireExceptionCaught(cause); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\VoidChannelPromise.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */