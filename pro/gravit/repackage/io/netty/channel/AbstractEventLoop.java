/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.AbstractEventExecutor;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorGroup;
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
/*    */ public abstract class AbstractEventLoop
/*    */   extends AbstractEventExecutor
/*    */   implements EventLoop
/*    */ {
/*    */   protected AbstractEventLoop() {}
/*    */   
/*    */   protected AbstractEventLoop(EventLoopGroup parent) {
/* 29 */     super(parent);
/*    */   }
/*    */ 
/*    */   
/*    */   public EventLoopGroup parent() {
/* 34 */     return (EventLoopGroup)super.parent();
/*    */   }
/*    */ 
/*    */   
/*    */   public EventLoop next() {
/* 39 */     return (EventLoop)super.next();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AbstractEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */