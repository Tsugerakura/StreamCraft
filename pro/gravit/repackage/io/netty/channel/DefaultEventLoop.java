/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.DefaultThreadFactory;
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
/*    */ public class DefaultEventLoop
/*    */   extends SingleThreadEventLoop
/*    */ {
/*    */   public DefaultEventLoop() {
/* 26 */     this((EventLoopGroup)null);
/*    */   }
/*    */   
/*    */   public DefaultEventLoop(ThreadFactory threadFactory) {
/* 30 */     this((EventLoopGroup)null, threadFactory);
/*    */   }
/*    */   
/*    */   public DefaultEventLoop(Executor executor) {
/* 34 */     this((EventLoopGroup)null, executor);
/*    */   }
/*    */   
/*    */   public DefaultEventLoop(EventLoopGroup parent) {
/* 38 */     this(parent, (ThreadFactory)new DefaultThreadFactory(DefaultEventLoop.class));
/*    */   }
/*    */   
/*    */   public DefaultEventLoop(EventLoopGroup parent, ThreadFactory threadFactory) {
/* 42 */     super(parent, threadFactory, true);
/*    */   }
/*    */   
/*    */   public DefaultEventLoop(EventLoopGroup parent, Executor executor) {
/* 46 */     super(parent, executor, true);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void run() {
/*    */     do {
/* 52 */       Runnable task = takeTask();
/* 53 */       if (task == null)
/* 54 */         continue;  task.run();
/* 55 */       updateLastExecutionTime();
/*    */     
/*    */     }
/* 58 */     while (!confirmShutdown());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */