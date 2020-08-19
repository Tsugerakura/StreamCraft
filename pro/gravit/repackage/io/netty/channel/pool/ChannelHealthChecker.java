/*    */ package pro.gravit.repackage.io.netty.channel.pool;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.Channel;
/*    */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
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
/*    */ public interface ChannelHealthChecker
/*    */ {
/* 32 */   public static final ChannelHealthChecker ACTIVE = new ChannelHealthChecker()
/*    */     {
/*    */       public Future<Boolean> isHealthy(Channel channel) {
/* 35 */         EventLoop loop = channel.eventLoop();
/* 36 */         return channel.isActive() ? loop.newSucceededFuture(Boolean.TRUE) : loop.newSucceededFuture(Boolean.FALSE);
/*    */       }
/*    */     };
/*    */   
/*    */   Future<Boolean> isHealthy(Channel paramChannel);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\ChannelHealthChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */