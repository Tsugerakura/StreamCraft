/*     */ package pro.gravit.repackage.io.netty.channel.embedded;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.AbstractScheduledEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorGroup;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
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
/*     */ final class EmbeddedEventLoop
/*     */   extends AbstractScheduledEventExecutor
/*     */   implements EventLoop
/*     */ {
/*  34 */   private final Queue<Runnable> tasks = new ArrayDeque<Runnable>(2);
/*     */ 
/*     */   
/*     */   public EventLoopGroup parent() {
/*  38 */     return (EventLoopGroup)super.parent();
/*     */   }
/*     */ 
/*     */   
/*     */   public EventLoop next() {
/*  43 */     return (EventLoop)super.next();
/*     */   }
/*     */ 
/*     */   
/*     */   public void execute(Runnable command) {
/*  48 */     this.tasks.add(ObjectUtil.checkNotNull(command, "command"));
/*     */   }
/*     */   
/*     */   void runTasks() {
/*     */     while (true) {
/*  53 */       Runnable task = this.tasks.poll();
/*  54 */       if (task == null) {
/*     */         break;
/*     */       }
/*     */       
/*  58 */       task.run();
/*     */     } 
/*     */   }
/*     */   
/*     */   long runScheduledTasks() {
/*  63 */     long time = AbstractScheduledEventExecutor.nanoTime();
/*     */     while (true) {
/*  65 */       Runnable task = pollScheduledTask(time);
/*  66 */       if (task == null) {
/*  67 */         return nextScheduledTaskNano();
/*     */       }
/*     */       
/*  70 */       task.run();
/*     */     } 
/*     */   }
/*     */   
/*     */   long nextScheduledTask() {
/*  75 */     return nextScheduledTaskNano();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void cancelScheduledTasks() {
/*  80 */     super.cancelScheduledTasks();
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
/*  85 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<?> terminationFuture() {
/*  90 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void shutdown() {
/*  96 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isShuttingDown() {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isShutdown() {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTerminated() {
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit) {
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture register(Channel channel) {
/* 121 */     return register((ChannelPromise)new DefaultChannelPromise(channel, (EventExecutor)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture register(ChannelPromise promise) {
/* 126 */     ObjectUtil.checkNotNull(promise, "promise");
/* 127 */     promise.channel().unsafe().register(this, promise);
/* 128 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFuture register(Channel channel, ChannelPromise promise) {
/* 134 */     channel.unsafe().register(this, promise);
/* 135 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inEventLoop() {
/* 140 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inEventLoop(Thread thread) {
/* 145 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\embedded\EmbeddedEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */