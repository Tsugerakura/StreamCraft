/*     */ package pro.gravit.repackage.io.netty.channel.nio;
/*     */ 
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultSelectStrategyFactory;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopTaskQueueFactory;
/*     */ import pro.gravit.repackage.io.netty.channel.MultithreadEventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.SelectStrategyFactory;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorChooserFactory;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.RejectedExecutionHandler;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.RejectedExecutionHandlers;
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
/*     */ public class NioEventLoopGroup
/*     */   extends MultithreadEventLoopGroup
/*     */ {
/*     */   public NioEventLoopGroup() {
/*  44 */     this(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads) {
/*  52 */     this(nThreads, (Executor)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(ThreadFactory threadFactory) {
/*  60 */     this(0, threadFactory, SelectorProvider.provider());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
/*  68 */     this(nThreads, threadFactory, SelectorProvider.provider());
/*     */   }
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor) {
/*  72 */     this(nThreads, executor, SelectorProvider.provider());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
/*  81 */     this(nThreads, threadFactory, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
/*  86 */     super(nThreads, threadFactory, new Object[] { selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject() });
/*     */   }
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider) {
/*  91 */     this(nThreads, executor, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
/*  96 */     super(nThreads, executor, new Object[] { selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject() });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
/* 102 */     super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, 
/* 103 */           RejectedExecutionHandlers.reject() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler) {
/* 110 */     super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, rejectedExecutionHandler });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory) {
/* 118 */     super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, rejectedExecutionHandler, taskQueueFactory });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIoRatio(int ioRatio) {
/* 127 */     for (EventExecutor e : this) {
/* 128 */       ((NioEventLoop)e).setIoRatio(ioRatio);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rebuildSelectors() {
/* 137 */     for (EventExecutor e : this) {
/* 138 */       ((NioEventLoop)e).rebuildSelector();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected EventLoop newChild(Executor executor, Object... args) throws Exception {
/* 144 */     EventLoopTaskQueueFactory queueFactory = (args.length == 4) ? (EventLoopTaskQueueFactory)args[3] : null;
/* 145 */     return (EventLoop)new NioEventLoop(this, executor, (SelectorProvider)args[0], ((SelectStrategyFactory)args[1])
/* 146 */         .newSelectStrategy(), (RejectedExecutionHandler)args[2], queueFactory);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\NioEventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */