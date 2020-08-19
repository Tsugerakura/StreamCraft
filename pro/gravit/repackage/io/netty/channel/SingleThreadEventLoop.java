/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorGroup;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.RejectedExecutionHandler;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.RejectedExecutionHandlers;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.SingleThreadEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SingleThreadEventLoop
/*     */   extends SingleThreadEventExecutor
/*     */   implements EventLoop
/*     */ {
/*  35 */   protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16, 
/*  36 */       SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.eventLoop.maxPendingTasks", 2147483647));
/*     */   
/*     */   private final Queue<Runnable> tailTasks;
/*     */   
/*     */   protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
/*  41 */     this(parent, threadFactory, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
/*     */   }
/*     */   
/*     */   protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp) {
/*  45 */     this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
/*  51 */     super(parent, threadFactory, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
/*  52 */     this.tailTasks = newTaskQueue(maxPendingTasks);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
/*  58 */     super(parent, executor, addTaskWakesUp, maxPendingTasks, rejectedExecutionHandler);
/*  59 */     this.tailTasks = newTaskQueue(maxPendingTasks);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, Queue<Runnable> tailTaskQueue, RejectedExecutionHandler rejectedExecutionHandler) {
/*  65 */     super(parent, executor, addTaskWakesUp, taskQueue, rejectedExecutionHandler);
/*  66 */     this.tailTasks = (Queue<Runnable>)ObjectUtil.checkNotNull(tailTaskQueue, "tailTaskQueue");
/*     */   }
/*     */ 
/*     */   
/*     */   public EventLoopGroup parent() {
/*  71 */     return (EventLoopGroup)super.parent();
/*     */   }
/*     */ 
/*     */   
/*     */   public EventLoop next() {
/*  76 */     return (EventLoop)super.next();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture register(Channel channel) {
/*  81 */     return register(new DefaultChannelPromise(channel, (EventExecutor)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture register(ChannelPromise promise) {
/*  86 */     ObjectUtil.checkNotNull(promise, "promise");
/*  87 */     promise.channel().unsafe().register(this, promise);
/*  88 */     return promise;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFuture register(Channel channel, ChannelPromise promise) {
/*  94 */     ObjectUtil.checkNotNull(promise, "promise");
/*  95 */     ObjectUtil.checkNotNull(channel, "channel");
/*  96 */     channel.unsafe().register(this, promise);
/*  97 */     return promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void executeAfterEventLoopIteration(Runnable task) {
/* 107 */     ObjectUtil.checkNotNull(task, "task");
/* 108 */     if (isShutdown()) {
/* 109 */       reject();
/*     */     }
/*     */     
/* 112 */     if (!this.tailTasks.offer(task)) {
/* 113 */       reject(task);
/*     */     }
/*     */     
/* 116 */     if (!(task instanceof pro.gravit.repackage.io.netty.util.concurrent.AbstractEventExecutor.LazyRunnable) && wakesUpForTask(task)) {
/* 117 */       wakeup(inEventLoop());
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
/*     */   final boolean removeAfterEventLoopIterationTask(Runnable task) {
/* 130 */     return this.tailTasks.remove(ObjectUtil.checkNotNull(task, "task"));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void afterRunningAllTasks() {
/* 135 */     runAllTasksFrom(this.tailTasks);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean hasTasks() {
/* 140 */     return (super.hasTasks() || !this.tailTasks.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public int pendingTasks() {
/* 145 */     return super.pendingTasks() + this.tailTasks.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int registeredChannels() {
/* 155 */     return -1;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\SingleThreadEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */