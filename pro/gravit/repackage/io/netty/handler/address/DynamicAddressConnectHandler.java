/*    */ package pro.gravit.repackage.io.netty.handler.address;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandlerAdapter;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*    */ public abstract class DynamicAddressConnectHandler
/*    */   extends ChannelOutboundHandlerAdapter
/*    */ {
/*    */   public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/*    */     SocketAddress remote, local;
/*    */     try {
/* 43 */       remote = remoteAddress(remoteAddress, localAddress);
/* 44 */       local = localAddress(remoteAddress, localAddress);
/* 45 */     } catch (Exception e) {
/* 46 */       promise.setFailure(e);
/*    */       return;
/*    */     } 
/* 49 */     ctx.connect(remote, local, promise).addListener((GenericFutureListener)new ChannelFutureListener()
/*    */         {
/*    */           public void operationComplete(ChannelFuture future) {
/* 52 */             if (future.isSuccess())
/*    */             {
/*    */               
/* 55 */               future.channel().pipeline().remove((ChannelHandler)DynamicAddressConnectHandler.this);
/*    */             }
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected SocketAddress localAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 69 */     return localAddress;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected SocketAddress remoteAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 80 */     return remoteAddress;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\address\DynamicAddressConnectHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */