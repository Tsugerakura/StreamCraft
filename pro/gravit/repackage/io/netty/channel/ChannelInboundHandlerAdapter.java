/*     */ package pro.gravit.repackage.io.netty.channel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChannelInboundHandlerAdapter
/*     */   extends ChannelHandlerAdapter
/*     */   implements ChannelInboundHandler
/*     */ {
/*     */   @Skip
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/*  45 */     ctx.fireChannelRegistered();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
/*  57 */     ctx.fireChannelUnregistered();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/*  69 */     ctx.fireChannelActive();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/*  81 */     ctx.fireChannelInactive();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  93 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 105 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 117 */     ctx.fireUserEventTriggered(evt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Skip
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/* 129 */     ctx.fireChannelWritabilityChanged();
/*     */   }
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
/*     */   @Skip
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 143 */     ctx.fireExceptionCaught(cause);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelInboundHandlerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */