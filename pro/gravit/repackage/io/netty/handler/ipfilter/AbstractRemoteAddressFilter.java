/*     */ package pro.gravit.repackage.io.netty.handler.ipfilter;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractRemoteAddressFilter<T extends SocketAddress>
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/*  42 */     handleNewChannel(ctx);
/*  43 */     ctx.fireChannelRegistered();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/*  48 */     if (!handleNewChannel(ctx)) {
/*  49 */       throw new IllegalStateException("cannot determine to accept or reject a channel: " + ctx.channel());
/*     */     }
/*  51 */     ctx.fireChannelActive();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean handleNewChannel(ChannelHandlerContext ctx) throws Exception {
/*  57 */     SocketAddress socketAddress = ctx.channel().remoteAddress();
/*     */ 
/*     */     
/*  60 */     if (socketAddress == null) {
/*  61 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  66 */     ctx.pipeline().remove((ChannelHandler)this);
/*     */     
/*  68 */     if (accept(ctx, (T)socketAddress)) {
/*  69 */       channelAccepted(ctx, (T)socketAddress);
/*     */     } else {
/*  71 */       ChannelFuture rejectedFuture = channelRejected(ctx, (T)socketAddress);
/*  72 */       if (rejectedFuture != null) {
/*  73 */         rejectedFuture.addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */       } else {
/*  75 */         ctx.close();
/*     */       } 
/*     */     } 
/*     */     
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean accept(ChannelHandlerContext paramChannelHandlerContext, T paramT) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void channelAccepted(ChannelHandlerContext ctx, T remoteAddress) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ChannelFuture channelRejected(ChannelHandlerContext ctx, T remoteAddress) {
/* 107 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ipfilter\AbstractRemoteAddressFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */