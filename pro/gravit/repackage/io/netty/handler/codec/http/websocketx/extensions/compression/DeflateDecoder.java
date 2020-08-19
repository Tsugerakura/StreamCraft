/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
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
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
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
/*     */ abstract class DeflateDecoder
/*     */   extends WebSocketExtensionDecoder
/*     */ {
/*  43 */   static final ByteBuf FRAME_TAIL = Unpooled.unreleasableBuffer(
/*  44 */       Unpooled.wrappedBuffer(new byte[] { 0, 0, -1, -1
/*  45 */         })).asReadOnly();
/*     */   
/*  47 */   static final ByteBuf EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer(
/*  48 */       Unpooled.wrappedBuffer(new byte[] { 0
/*  49 */         })).asReadOnly();
/*     */ 
/*     */   
/*     */   private final boolean noContext;
/*     */ 
/*     */   
/*     */   private final WebSocketExtensionFilter extensionDecoderFilter;
/*     */ 
/*     */   
/*     */   private EmbeddedChannel decoder;
/*     */ 
/*     */ 
/*     */   
/*     */   DeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
/*  63 */     this.noContext = noContext;
/*  64 */     this.extensionDecoderFilter = (WebSocketExtensionFilter)ObjectUtil.checkNotNull(extensionDecoderFilter, "extensionDecoderFilter");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketExtensionFilter extensionDecoderFilter() {
/*  71 */     return this.extensionDecoderFilter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
/*     */     ContinuationWebSocketFrame continuationWebSocketFrame;
/*  80 */     ByteBuf decompressedContent = decompressContent(ctx, msg);
/*     */ 
/*     */     
/*  83 */     if (msg instanceof TextWebSocketFrame) {
/*  84 */       TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg.isFinalFragment(), newRsv(msg), decompressedContent);
/*  85 */     } else if (msg instanceof BinaryWebSocketFrame) {
/*  86 */       BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(msg.isFinalFragment(), newRsv(msg), decompressedContent);
/*  87 */     } else if (msg instanceof ContinuationWebSocketFrame) {
/*  88 */       continuationWebSocketFrame = new ContinuationWebSocketFrame(msg.isFinalFragment(), newRsv(msg), decompressedContent);
/*     */     } else {
/*  90 */       throw new CodecException("unexpected frame type: " + msg.getClass().getName());
/*     */     } 
/*     */     
/*  93 */     out.add(continuationWebSocketFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/*  98 */     cleanup();
/*  99 */     super.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 104 */     cleanup();
/* 105 */     super.channelInactive(ctx);
/*     */   }
/*     */   
/*     */   private ByteBuf decompressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
/* 109 */     if (this.decoder == null) {
/* 110 */       if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
/* 111 */         throw new CodecException("unexpected initial frame type: " + msg.getClass().getName());
/*     */       }
/* 113 */       this.decoder = new EmbeddedChannel(new ChannelHandler[] { (ChannelHandler)ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE) });
/*     */     } 
/*     */     
/* 116 */     boolean readable = msg.content().isReadable();
/* 117 */     boolean emptyDeflateBlock = EMPTY_DEFLATE_BLOCK.equals(msg.content());
/*     */     
/* 119 */     this.decoder.writeInbound(new Object[] { msg.content().retain() });
/* 120 */     if (appendFrameTail(msg)) {
/* 121 */       this.decoder.writeInbound(new Object[] { FRAME_TAIL.duplicate() });
/*     */     }
/*     */     
/* 124 */     CompositeByteBuf compositeDecompressedContent = ctx.alloc().compositeBuffer();
/*     */     while (true) {
/* 126 */       ByteBuf partUncompressedContent = (ByteBuf)this.decoder.readInbound();
/* 127 */       if (partUncompressedContent == null) {
/*     */         break;
/*     */       }
/* 130 */       if (!partUncompressedContent.isReadable()) {
/* 131 */         partUncompressedContent.release();
/*     */         continue;
/*     */       } 
/* 134 */       compositeDecompressedContent.addComponent(true, partUncompressedContent);
/*     */     } 
/*     */ 
/*     */     
/* 138 */     if (!emptyDeflateBlock && readable && compositeDecompressedContent.numComponents() <= 0)
/*     */     {
/*     */       
/* 141 */       if (!(msg instanceof ContinuationWebSocketFrame)) {
/* 142 */         compositeDecompressedContent.release();
/* 143 */         throw new CodecException("cannot read uncompressed buffer");
/*     */       } 
/*     */     }
/*     */     
/* 147 */     if (msg.isFinalFragment() && this.noContext) {
/* 148 */       cleanup();
/*     */     }
/*     */     
/* 151 */     return (ByteBuf)compositeDecompressedContent;
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 155 */     if (this.decoder != null) {
/*     */       
/* 157 */       this.decoder.finishAndReleaseAll();
/* 158 */       this.decoder = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract boolean appendFrameTail(WebSocketFrame paramWebSocketFrame);
/*     */   
/*     */   protected abstract int newRsv(WebSocketFrame paramWebSocketFrame);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\DeflateDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */