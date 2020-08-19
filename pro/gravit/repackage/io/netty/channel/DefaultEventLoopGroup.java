/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
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
/*    */ 
/*    */ 
/*    */ public class DefaultEventLoopGroup
/*    */   extends MultithreadEventLoopGroup
/*    */ {
/*    */   public DefaultEventLoopGroup() {
/* 30 */     this(0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultEventLoopGroup(int nThreads) {
/* 39 */     this(nThreads, (ThreadFactory)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultEventLoopGroup(ThreadFactory threadFactory) {
/* 48 */     this(0, threadFactory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
/* 58 */     super(nThreads, threadFactory, new Object[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultEventLoopGroup(int nThreads, Executor executor) {
/* 68 */     super(nThreads, executor, new Object[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   protected EventLoop newChild(Executor executor, Object... args) throws Exception {
/* 73 */     return new DefaultEventLoop(this, executor);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultEventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */