/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.CompleteFuture;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
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
/*     */ abstract class CompleteChannelFuture
/*     */   extends CompleteFuture<Void>
/*     */   implements ChannelFuture
/*     */ {
/*     */   private final Channel channel;
/*     */   
/*     */   protected CompleteChannelFuture(Channel channel, EventExecutor executor) {
/*  38 */     super(executor);
/*  39 */     this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
/*     */   }
/*     */ 
/*     */   
/*     */   protected EventExecutor executor() {
/*  44 */     EventExecutor e = super.executor();
/*  45 */     if (e == null) {
/*  46 */       return (EventExecutor)channel().eventLoop();
/*     */     }
/*  48 */     return e;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  54 */     super.addListener(listener);
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  60 */     super.addListeners((GenericFutureListener[])listeners);
/*  61 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  66 */     super.removeListener(listener);
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  72 */     super.removeListeners((GenericFutureListener[])listeners);
/*  73 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture syncUninterruptibly() {
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture sync() throws InterruptedException {
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture await() throws InterruptedException {
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture awaitUninterruptibly() {
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Channel channel() {
/*  98 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public Void getNow() {
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVoid() {
/* 108 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\CompleteChannelFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */