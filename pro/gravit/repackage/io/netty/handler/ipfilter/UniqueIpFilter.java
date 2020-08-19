/*    */ package pro.gravit.repackage.io.netty.handler.ipfilter;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.SocketAddress;
/*    */ import java.util.Set;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ConcurrentSet;
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
/*    */ @Sharable
/*    */ public class UniqueIpFilter
/*    */   extends AbstractRemoteAddressFilter<InetSocketAddress>
/*    */ {
/* 36 */   private final Set<InetAddress> connected = (Set<InetAddress>)new ConcurrentSet();
/*    */ 
/*    */   
/*    */   protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
/* 40 */     final InetAddress remoteIp = remoteAddress.getAddress();
/* 41 */     if (!this.connected.add(remoteIp)) {
/* 42 */       return false;
/*    */     }
/* 44 */     ctx.channel().closeFuture().addListener((GenericFutureListener)new ChannelFutureListener()
/*    */         {
/*    */           public void operationComplete(ChannelFuture future) throws Exception {
/* 47 */             UniqueIpFilter.this.connected.remove(remoteIp);
/*    */           }
/*    */         });
/* 50 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ipfilter\UniqueIpFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */