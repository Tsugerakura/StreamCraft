/*     */ package pro.gravit.repackage.io.netty.channel.group;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class VoidChannelGroupFuture
/*     */   implements ChannelGroupFuture
/*     */ {
/*  29 */   private static final Iterator<ChannelFuture> EMPTY = Collections.<ChannelFuture>emptyList().iterator();
/*     */   private final ChannelGroup group;
/*     */   
/*     */   VoidChannelGroupFuture(ChannelGroup group) {
/*  33 */     this.group = group;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroup group() {
/*  38 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture find(Channel channel) {
/*  43 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSuccess() {
/*  48 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupException cause() {
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPartialSuccess() {
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPartialFailure() {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  68 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  73 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/*  78 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/*  83 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture await() {
/*  88 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture awaitUninterruptibly() {
/*  93 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture syncUninterruptibly() {
/*  98 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture sync() {
/* 103 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<ChannelFuture> iterator() {
/* 108 */     return EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancellable() {
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeout, TimeUnit unit) {
/* 118 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean await(long timeoutMillis) {
/* 123 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
/* 128 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitUninterruptibly(long timeoutMillis) {
/* 133 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public Void getNow() {
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean cancel(boolean mayInterruptIfRunning) {
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCancelled() {
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDone() {
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Void get() {
/* 163 */     throw reject();
/*     */   }
/*     */ 
/*     */   
/*     */   public Void get(long timeout, TimeUnit unit) {
/* 168 */     throw reject();
/*     */   }
/*     */   
/*     */   private static RuntimeException reject() {
/* 172 */     return new IllegalStateException("void future");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\group\VoidChannelGroupFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */