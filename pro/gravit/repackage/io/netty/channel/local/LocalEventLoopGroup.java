/*    */ package pro.gravit.repackage.io.netty.channel.local;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import pro.gravit.repackage.io.netty.channel.DefaultEventLoopGroup;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class LocalEventLoopGroup
/*    */   extends DefaultEventLoopGroup
/*    */ {
/*    */   public LocalEventLoopGroup() {}
/*    */   
/*    */   public LocalEventLoopGroup(int nThreads) {
/* 39 */     super(nThreads);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LocalEventLoopGroup(ThreadFactory threadFactory) {
/* 48 */     super(0, threadFactory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LocalEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
/* 58 */     super(nThreads, threadFactory);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\local\LocalEventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */