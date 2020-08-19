/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.embedded.EmbeddedChannel;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CodecException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibCodecFactory;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibWrapper;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
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
/*     */ abstract class DeflateEncoder
/*     */   extends WebSocketExtensionEncoder
/*     */ {
/*     */   private final int compressionLevel;
/*     */   private final int windowSize;
/*     */   private final boolean noContext;
/*     */   private final WebSocketExtensionFilter extensionEncoderFilter;
/*     */   private EmbeddedChannel encoder;
/*     */   
/*     */   DeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
/*  59 */     this.compressionLevel = compressionLevel;
/*  60 */     this.windowSize = windowSize;
/*  61 */     this.noContext = noContext;
/*  62 */     this.extensionEncoderFilter = (WebSocketExtensionFilter)ObjectUtil.checkNotNull(extensionEncoderFilter, "extensionEncoderFilter");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketExtensionFilter extensionEncoderFilter() {
/*  69 */     return this.extensionEncoderFilter;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
/*     */     ByteBuf compressedContent;
/*     */     ContinuationWebSocketFrame continuationWebSocketFrame;
/*  87 */     if (msg.content().isReadable()) {
/*  88 */       compressedContent = compressContent(ctx, msg);
/*  89 */     } else if (msg.isFinalFragment()) {
/*     */ 
/*     */       
/*  92 */       compressedContent = PerMessageDeflateDecoder.EMPTY_DEFLATE_BLOCK.duplicate();
/*     */     } else {
/*  94 */       throw new CodecException("cannot compress content buffer");
/*     */     } 
/*     */ 
/*     */     
/*  98 */     if (msg instanceof TextWebSocketFrame) {
/*  99 */       TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent);
/* 100 */     } else if (msg instanceof BinaryWebSocketFrame) {
/* 101 */       BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent);
/* 102 */     } else if (msg instanceof ContinuationWebSocketFrame) {
/* 103 */       continuationWebSocketFrame = new ContinuationWebSocketFrame(msg.isFinalFragment(), rsv(msg), compressedContent);
/*     */     } else {
/* 105 */       throw new CodecException("unexpected frame type: " + msg.getClass().getName());
/*     */     } 
/*     */     
/* 108 */     out.add(continuationWebSocketFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 113 */     cleanup();
/* 114 */     super.handlerRemoved(ctx);
/*     */   }
/*     */   private ByteBuf compressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
/*     */     CompositeByteBuf compositeByteBuf1;
/* 118 */     if (this.encoder == null) {
/* 119 */       this.encoder = new EmbeddedChannel(new ChannelHandler[] { (ChannelHandler)ZlibCodecFactory.newZlibEncoder(ZlibWrapper.NONE, this.compressionLevel, this.windowSize, 8) });
/*     */     }
/*     */ 
/*     */     
/* 123 */     this.encoder.writeOutbound(new Object[] { msg.content().retain() });
/*     */     
/* 125 */     CompositeByteBuf fullCompressedContent = ctx.alloc().compositeBuffer();
/*     */     while (true) {
/* 127 */       ByteBuf partCompressedContent = (ByteBuf)this.encoder.readOutbound();
/* 128 */       if (partCompressedContent == null) {
/*     */         break;
/*     */       }
/* 131 */       if (!partCompressedContent.isReadable()) {
/* 132 */         partCompressedContent.release();
/*     */         continue;
/*     */       } 
/* 135 */       fullCompressedContent.addComponent(true, partCompressedContent);
/*     */     } 
/*     */     
/* 138 */     if (fullCompressedContent.numComponents() <= 0) {
/* 139 */       fullCompressedContent.release();
/* 140 */       throw new CodecException("cannot read compressed buffer");
/*     */     } 
/*     */     
/* 143 */     if (msg.isFinalFragment() && this.noContext) {
/* 144 */       cleanup();
/*     */     }
/*     */ 
/*     */     
/* 148 */     if (removeFrameTail(msg)) {
/* 149 */       int realLength = fullCompressedContent.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.readableBytes();
/* 150 */       ByteBuf compressedContent = fullCompressedContent.slice(0, realLength);
/*     */     } else {
/* 152 */       compositeByteBuf1 = fullCompressedContent;
/*     */     } 
/*     */     
/* 155 */     return (ByteBuf)compositeByteBuf1;
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 159 */     if (this.encoder != null) {
/*     */       
/* 161 */       this.encoder.finishAndReleaseAll();
/* 162 */       this.encoder = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract int rsv(WebSocketFrame paramWebSocketFrame);
/*     */   
/*     */   protected abstract boolean removeFrameTail(WebSocketFrame paramWebSocketFrame);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\DeflateEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */