/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.DefaultPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultChannelPromise
/*     */   extends DefaultPromise<Void>
/*     */   implements ChannelPromise, ChannelFlushPromiseNotifier.FlushCheckpoint
/*     */ {
/*     */   private final Channel channel;
/*     */   private long checkpoint;
/*     */   
/*     */   public DefaultChannelPromise(Channel channel) {
/*  42 */     this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultChannelPromise(Channel channel, EventExecutor executor) {
/*  52 */     super(executor);
/*  53 */     this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
/*     */   }
/*     */ 
/*     */   
/*     */   protected EventExecutor executor() {
/*  58 */     EventExecutor e = super.executor();
/*  59 */     if (e == null) {
/*  60 */       return (EventExecutor)channel().eventLoop();
/*     */     }
/*  62 */     return e;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Channel channel() {
/*  68 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setSuccess() {
/*  73 */     return setSuccess((Void)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setSuccess(Void result) {
/*  78 */     super.setSuccess(result);
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess() {
/*  84 */     return trySuccess(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise setFailure(Throwable cause) {
/*  89 */     super.setFailure(cause);
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  95 */     super.addListener(listener);
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 101 */     super.addListeners((GenericFutureListener[])listeners);
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/* 107 */     super.removeListener(listener);
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 113 */     super.removeListeners((GenericFutureListener[])listeners);
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise sync() throws InterruptedException {
/* 119 */     super.sync();
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise syncUninterruptibly() {
/* 125 */     super.syncUninterruptibly();
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise await() throws InterruptedException {
/* 131 */     super.await();
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise awaitUninterruptibly() {
/* 137 */     super.awaitUninterruptibly();
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public long flushCheckpoint() {
/* 143 */     return this.checkpoint;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flushCheckpoint(long checkpoint) {
/* 148 */     this.checkpoint = checkpoint;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise promise() {
/* 153 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void checkDeadLock() {
/* 158 */     if (channel().isRegistered()) {
/* 159 */       super.checkDeadLock();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelPromise unvoid() {
/* 165 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVoid() {
/* 170 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultChannelPromise.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */