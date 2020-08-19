/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.UnsupportedMessageTypeException;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
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
/*     */ public class SpdyFrameCodec
/*     */   extends ByteToMessageDecoder
/*     */   implements SpdyFrameDecoderDelegate, ChannelOutboundHandler
/*     */ {
/*  37 */   private static final SpdyProtocolException INVALID_FRAME = new SpdyProtocolException("Received invalid frame");
/*     */ 
/*     */   
/*     */   private final SpdyFrameDecoder spdyFrameDecoder;
/*     */ 
/*     */   
/*     */   private final SpdyFrameEncoder spdyFrameEncoder;
/*     */   
/*     */   private final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder;
/*     */   
/*     */   private final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder;
/*     */   
/*     */   private SpdyHeadersFrame spdyHeadersFrame;
/*     */   
/*     */   private SpdySettingsFrame spdySettingsFrame;
/*     */   
/*     */   private ChannelHandlerContext ctx;
/*     */   
/*     */   private boolean read;
/*     */   
/*     */   private final boolean validateHeaders;
/*     */ 
/*     */   
/*     */   public SpdyFrameCodec(SpdyVersion version) {
/*  61 */     this(version, true);
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
/*     */   public SpdyFrameCodec(SpdyVersion version, boolean validateHeaders) {
/*  73 */     this(version, 8192, 16384, 6, 15, 8, validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpdyFrameCodec(SpdyVersion version, int maxChunkSize, int maxHeaderSize, int compressionLevel, int windowBits, int memLevel) {
/*  83 */     this(version, maxChunkSize, maxHeaderSize, compressionLevel, windowBits, memLevel, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpdyFrameCodec(SpdyVersion version, int maxChunkSize, int maxHeaderSize, int compressionLevel, int windowBits, int memLevel, boolean validateHeaders) {
/*  93 */     this(version, maxChunkSize, 
/*  94 */         SpdyHeaderBlockDecoder.newInstance(version, maxHeaderSize), 
/*  95 */         SpdyHeaderBlockEncoder.newInstance(version, compressionLevel, windowBits, memLevel), validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SpdyFrameCodec(SpdyVersion version, int maxChunkSize, SpdyHeaderBlockDecoder spdyHeaderBlockDecoder, SpdyHeaderBlockEncoder spdyHeaderBlockEncoder, boolean validateHeaders) {
/* 101 */     this.spdyFrameDecoder = new SpdyFrameDecoder(version, this, maxChunkSize);
/* 102 */     this.spdyFrameEncoder = new SpdyFrameEncoder(version);
/* 103 */     this.spdyHeaderBlockDecoder = spdyHeaderBlockDecoder;
/* 104 */     this.spdyHeaderBlockEncoder = spdyHeaderBlockEncoder;
/* 105 */     this.validateHeaders = validateHeaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 110 */     super.handlerAdded(ctx);
/* 111 */     this.ctx = ctx;
/* 112 */     ctx.channel().closeFuture().addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/* 115 */             SpdyFrameCodec.this.spdyHeaderBlockDecoder.end();
/* 116 */             SpdyFrameCodec.this.spdyHeaderBlockEncoder.end();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 123 */     this.spdyFrameDecoder.decode(in);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 128 */     if (!this.read && 
/* 129 */       !ctx.channel().config().isAutoRead()) {
/* 130 */       ctx.read();
/*     */     }
/*     */     
/* 133 */     this.read = false;
/* 134 */     super.channelReadComplete(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 139 */     ctx.bind(localAddress, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 145 */     ctx.connect(remoteAddress, localAddress, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 150 */     ctx.disconnect(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 155 */     ctx.close(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 160 */     ctx.deregister(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 165 */     ctx.read();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 170 */     ctx.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 177 */     if (msg instanceof SpdyDataFrame) {
/*     */       
/* 179 */       SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
/* 180 */       ByteBuf frame = this.spdyFrameEncoder.encodeDataFrame(ctx
/* 181 */           .alloc(), spdyDataFrame
/* 182 */           .streamId(), spdyDataFrame
/* 183 */           .isLast(), spdyDataFrame
/* 184 */           .content());
/*     */       
/* 186 */       spdyDataFrame.release();
/* 187 */       ctx.write(frame, promise);
/*     */     }
/* 189 */     else if (msg instanceof SpdySynStreamFrame) {
/*     */       ByteBuf frame;
/* 191 */       SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
/* 192 */       ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode(ctx.alloc(), spdySynStreamFrame);
/*     */       try {
/* 194 */         frame = this.spdyFrameEncoder.encodeSynStreamFrame(ctx
/* 195 */             .alloc(), spdySynStreamFrame
/* 196 */             .streamId(), spdySynStreamFrame
/* 197 */             .associatedStreamId(), spdySynStreamFrame
/* 198 */             .priority(), spdySynStreamFrame
/* 199 */             .isLast(), spdySynStreamFrame
/* 200 */             .isUnidirectional(), headerBlock);
/*     */       }
/*     */       finally {
/*     */         
/* 204 */         headerBlock.release();
/*     */       } 
/* 206 */       ctx.write(frame, promise);
/*     */     }
/* 208 */     else if (msg instanceof SpdySynReplyFrame) {
/*     */       ByteBuf frame;
/* 210 */       SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
/* 211 */       ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode(ctx.alloc(), spdySynReplyFrame);
/*     */       try {
/* 213 */         frame = this.spdyFrameEncoder.encodeSynReplyFrame(ctx
/* 214 */             .alloc(), spdySynReplyFrame
/* 215 */             .streamId(), spdySynReplyFrame
/* 216 */             .isLast(), headerBlock);
/*     */       }
/*     */       finally {
/*     */         
/* 220 */         headerBlock.release();
/*     */       } 
/* 222 */       ctx.write(frame, promise);
/*     */     }
/* 224 */     else if (msg instanceof SpdyRstStreamFrame) {
/*     */       
/* 226 */       SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
/* 227 */       ByteBuf frame = this.spdyFrameEncoder.encodeRstStreamFrame(ctx
/* 228 */           .alloc(), spdyRstStreamFrame
/* 229 */           .streamId(), spdyRstStreamFrame
/* 230 */           .status().code());
/*     */       
/* 232 */       ctx.write(frame, promise);
/*     */     }
/* 234 */     else if (msg instanceof SpdySettingsFrame) {
/*     */       
/* 236 */       SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
/* 237 */       ByteBuf frame = this.spdyFrameEncoder.encodeSettingsFrame(ctx
/* 238 */           .alloc(), spdySettingsFrame);
/*     */ 
/*     */       
/* 241 */       ctx.write(frame, promise);
/*     */     }
/* 243 */     else if (msg instanceof SpdyPingFrame) {
/*     */       
/* 245 */       SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
/* 246 */       ByteBuf frame = this.spdyFrameEncoder.encodePingFrame(ctx
/* 247 */           .alloc(), spdyPingFrame
/* 248 */           .id());
/*     */       
/* 250 */       ctx.write(frame, promise);
/*     */     }
/* 252 */     else if (msg instanceof SpdyGoAwayFrame) {
/*     */       
/* 254 */       SpdyGoAwayFrame spdyGoAwayFrame = (SpdyGoAwayFrame)msg;
/* 255 */       ByteBuf frame = this.spdyFrameEncoder.encodeGoAwayFrame(ctx
/* 256 */           .alloc(), spdyGoAwayFrame
/* 257 */           .lastGoodStreamId(), spdyGoAwayFrame
/* 258 */           .status().code());
/*     */       
/* 260 */       ctx.write(frame, promise);
/*     */     }
/* 262 */     else if (msg instanceof SpdyHeadersFrame) {
/*     */       ByteBuf frame;
/* 264 */       SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
/* 265 */       ByteBuf headerBlock = this.spdyHeaderBlockEncoder.encode(ctx.alloc(), spdyHeadersFrame);
/*     */       try {
/* 267 */         frame = this.spdyFrameEncoder.encodeHeadersFrame(ctx
/* 268 */             .alloc(), spdyHeadersFrame
/* 269 */             .streamId(), spdyHeadersFrame
/* 270 */             .isLast(), headerBlock);
/*     */       }
/*     */       finally {
/*     */         
/* 274 */         headerBlock.release();
/*     */       } 
/* 276 */       ctx.write(frame, promise);
/*     */     }
/* 278 */     else if (msg instanceof SpdyWindowUpdateFrame) {
/*     */       
/* 280 */       SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
/* 281 */       ByteBuf frame = this.spdyFrameEncoder.encodeWindowUpdateFrame(ctx
/* 282 */           .alloc(), spdyWindowUpdateFrame
/* 283 */           .streamId(), spdyWindowUpdateFrame
/* 284 */           .deltaWindowSize());
/*     */       
/* 286 */       ctx.write(frame, promise);
/*     */     } else {
/* 288 */       throw new UnsupportedMessageTypeException(msg, new Class[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void readDataFrame(int streamId, boolean last, ByteBuf data) {
/* 294 */     this.read = true;
/*     */     
/* 296 */     SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(streamId, data);
/* 297 */     spdyDataFrame.setLast(last);
/* 298 */     this.ctx.fireChannelRead(spdyDataFrame);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void readSynStreamFrame(int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional) {
/* 304 */     SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority, this.validateHeaders);
/*     */     
/* 306 */     spdySynStreamFrame.setLast(last);
/* 307 */     spdySynStreamFrame.setUnidirectional(unidirectional);
/* 308 */     this.spdyHeadersFrame = spdySynStreamFrame;
/*     */   }
/*     */ 
/*     */   
/*     */   public void readSynReplyFrame(int streamId, boolean last) {
/* 313 */     SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId, this.validateHeaders);
/* 314 */     spdySynReplyFrame.setLast(last);
/* 315 */     this.spdyHeadersFrame = spdySynReplyFrame;
/*     */   }
/*     */ 
/*     */   
/*     */   public void readRstStreamFrame(int streamId, int statusCode) {
/* 320 */     this.read = true;
/*     */     
/* 322 */     SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, statusCode);
/* 323 */     this.ctx.fireChannelRead(spdyRstStreamFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readSettingsFrame(boolean clearPersisted) {
/* 328 */     this.read = true;
/*     */     
/* 330 */     this.spdySettingsFrame = new DefaultSpdySettingsFrame();
/* 331 */     this.spdySettingsFrame.setClearPreviouslyPersistedSettings(clearPersisted);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readSetting(int id, int value, boolean persistValue, boolean persisted) {
/* 336 */     this.spdySettingsFrame.setValue(id, value, persistValue, persisted);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readSettingsEnd() {
/* 341 */     this.read = true;
/*     */     
/* 343 */     Object frame = this.spdySettingsFrame;
/* 344 */     this.spdySettingsFrame = null;
/* 345 */     this.ctx.fireChannelRead(frame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readPingFrame(int id) {
/* 350 */     this.read = true;
/*     */     
/* 352 */     SpdyPingFrame spdyPingFrame = new DefaultSpdyPingFrame(id);
/* 353 */     this.ctx.fireChannelRead(spdyPingFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readGoAwayFrame(int lastGoodStreamId, int statusCode) {
/* 358 */     this.read = true;
/*     */     
/* 360 */     SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(lastGoodStreamId, statusCode);
/* 361 */     this.ctx.fireChannelRead(spdyGoAwayFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readHeadersFrame(int streamId, boolean last) {
/* 366 */     this.spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId, this.validateHeaders);
/* 367 */     this.spdyHeadersFrame.setLast(last);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readWindowUpdateFrame(int streamId, int deltaWindowSize) {
/* 372 */     this.read = true;
/*     */     
/* 374 */     SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(streamId, deltaWindowSize);
/* 375 */     this.ctx.fireChannelRead(spdyWindowUpdateFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readHeaderBlock(ByteBuf headerBlock) {
/*     */     try {
/* 381 */       this.spdyHeaderBlockDecoder.decode(this.ctx.alloc(), headerBlock, this.spdyHeadersFrame);
/* 382 */     } catch (Exception e) {
/* 383 */       this.ctx.fireExceptionCaught(e);
/*     */     } finally {
/* 385 */       headerBlock.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void readHeaderBlockEnd() {
/* 391 */     Object frame = null;
/*     */     try {
/* 393 */       this.spdyHeaderBlockDecoder.endHeaderBlock(this.spdyHeadersFrame);
/* 394 */       frame = this.spdyHeadersFrame;
/* 395 */       this.spdyHeadersFrame = null;
/* 396 */     } catch (Exception e) {
/* 397 */       this.ctx.fireExceptionCaught(e);
/*     */     } 
/* 399 */     if (frame != null) {
/* 400 */       this.read = true;
/*     */       
/* 402 */       this.ctx.fireChannelRead(frame);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFrameError(String message) {
/* 408 */     this.ctx.fireExceptionCaught(INVALID_FRAME);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyFrameCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */