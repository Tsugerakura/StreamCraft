/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.AddressedEnvelope;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramPacket;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ 
/*     */ public class DatagramPacketEncoder<M>
/*     */   extends MessageToMessageEncoder<AddressedEnvelope<M, InetSocketAddress>>
/*     */ {
/*     */   private final MessageToMessageEncoder<? super M> encoder;
/*     */   
/*     */   public DatagramPacketEncoder(MessageToMessageEncoder<? super M> encoder) {
/*  57 */     this.encoder = (MessageToMessageEncoder<? super M>)ObjectUtil.checkNotNull(encoder, "encoder");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/*  62 */     if (super.acceptOutboundMessage(msg)) {
/*     */       
/*  64 */       AddressedEnvelope envelope = (AddressedEnvelope)msg;
/*  65 */       return (this.encoder.acceptOutboundMessage(envelope.content()) && (envelope
/*  66 */         .sender() instanceof InetSocketAddress || envelope.sender() == null) && envelope
/*  67 */         .recipient() instanceof InetSocketAddress);
/*     */     } 
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<M, InetSocketAddress> msg, List<Object> out) throws Exception {
/*  75 */     assert out.isEmpty();
/*     */     
/*  77 */     this.encoder.encode(ctx, (M)msg.content(), out);
/*  78 */     if (out.size() != 1) {
/*  79 */       throw new EncoderException(
/*  80 */           StringUtil.simpleClassName(this.encoder) + " must produce only one message.");
/*     */     }
/*  82 */     Object content = out.get(0);
/*  83 */     if (content instanceof ByteBuf) {
/*     */       
/*  85 */       out.set(0, new DatagramPacket((ByteBuf)content, (InetSocketAddress)msg.recipient(), (InetSocketAddress)msg.sender()));
/*     */     } else {
/*  87 */       throw new EncoderException(
/*  88 */           StringUtil.simpleClassName(this.encoder) + " must produce only ByteBuf.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/*  94 */     this.encoder.bind(ctx, localAddress, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 101 */     this.encoder.connect(ctx, remoteAddress, localAddress, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 106 */     this.encoder.disconnect(ctx, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 111 */     this.encoder.close(ctx, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 116 */     this.encoder.deregister(ctx, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 121 */     this.encoder.read(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 126 */     this.encoder.flush(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 131 */     this.encoder.handlerAdded(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 136 */     this.encoder.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 141 */     this.encoder.exceptionCaught(ctx, cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSharable() {
/* 146 */     return this.encoder.isSharable();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DatagramPacketEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */