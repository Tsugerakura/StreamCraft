/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import pro.gravit.repackage.io.netty.util.NettyRuntime;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.DefaultThreadFactory;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorChooserFactory;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.MultithreadEventExecutorGroup;
/*    */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*    */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*    */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class MultithreadEventLoopGroup
/*    */   extends MultithreadEventExecutorGroup
/*    */   implements EventLoopGroup
/*    */ {
/* 35 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.eventLoopThreads", 
/* 41 */         NettyRuntime.availableProcessors() * 2));
/*    */   static {
/* 43 */     if (logger.isDebugEnabled()) {
/* 44 */       logger.debug("-Dio.netty.eventLoopThreads: {}", Integer.valueOf(DEFAULT_EVENT_LOOP_THREADS));
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
/* 52 */     super((nThreads == 0) ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
/* 59 */     super((nThreads == 0) ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected MultithreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object... args) {
/* 68 */     super((nThreads == 0) ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, chooserFactory, args);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ThreadFactory newDefaultThreadFactory() {
/* 73 */     return (ThreadFactory)new DefaultThreadFactory(getClass(), 10);
/*    */   }
/*    */ 
/*    */   
/*    */   public EventLoop next() {
/* 78 */     return (EventLoop)super.next();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChannelFuture register(Channel channel) {
/* 86 */     return next().register(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelFuture register(ChannelPromise promise) {
/* 91 */     return next().register(promise);
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public ChannelFuture register(Channel channel, ChannelPromise promise) {
/* 97 */     return next().register(channel, promise);
/*    */   }
/*    */   
/*    */   protected abstract EventLoop newChild(Executor paramExecutor, Object... paramVarArgs) throws Exception;
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\MultithreadEventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */