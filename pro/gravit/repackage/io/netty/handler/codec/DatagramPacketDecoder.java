/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramPacket;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class DatagramPacketDecoder
/*     */   extends MessageToMessageDecoder<DatagramPacket>
/*     */ {
/*     */   private final MessageToMessageDecoder<ByteBuf> decoder;
/*     */   
/*     */   public DatagramPacketDecoder(MessageToMessageDecoder<ByteBuf> decoder) {
/*  46 */     this.decoder = (MessageToMessageDecoder<ByteBuf>)ObjectUtil.checkNotNull(decoder, "decoder");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean acceptInboundMessage(Object msg) throws Exception {
/*  51 */     if (msg instanceof DatagramPacket) {
/*  52 */       return this.decoder.acceptInboundMessage(((DatagramPacket)msg).content());
/*     */     }
/*  54 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
/*  59 */     this.decoder.decode(ctx, msg.content(), out);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/*  64 */     this.decoder.channelRegistered(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
/*  69 */     this.decoder.channelUnregistered(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/*  74 */     this.decoder.channelActive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/*  79 */     this.decoder.channelInactive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/*  84 */     this.decoder.channelReadComplete(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/*  89 */     this.decoder.userEventTriggered(ctx, evt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/*  94 */     this.decoder.channelWritabilityChanged(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/*  99 */     this.decoder.exceptionCaught(ctx, cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 104 */     this.decoder.handlerAdded(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 109 */     this.decoder.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSharable() {
/* 114 */     return this.decoder.isSharable();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DatagramPacketDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */