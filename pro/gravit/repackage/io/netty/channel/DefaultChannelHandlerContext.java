/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
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
/*    */ final class DefaultChannelHandlerContext
/*    */   extends AbstractChannelHandlerContext
/*    */ {
/*    */   private final ChannelHandler handler;
/*    */   
/*    */   DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
/* 26 */     super(pipeline, executor, name, (Class)handler.getClass());
/* 27 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelHandler handler() {
/* 32 */     return this.handler;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultChannelHandlerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */