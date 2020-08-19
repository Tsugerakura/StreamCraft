/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ public class WebSocket08FrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */   implements WebSocketFrameDecoder
/*     */ {
/*     */   enum State
/*     */   {
/*  79 */     READING_FIRST,
/*  80 */     READING_SECOND,
/*  81 */     READING_SIZE,
/*  82 */     MASKING_KEY,
/*  83 */     PAYLOAD,
/*  84 */     CORRUPT;
/*     */   }
/*     */   
/*  87 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
/*     */   
/*     */   private static final byte OPCODE_CONT = 0;
/*     */   
/*     */   private static final byte OPCODE_TEXT = 1;
/*     */   
/*     */   private static final byte OPCODE_BINARY = 2;
/*     */   private static final byte OPCODE_CLOSE = 8;
/*     */   private static final byte OPCODE_PING = 9;
/*     */   private static final byte OPCODE_PONG = 10;
/*     */   private final WebSocketDecoderConfig config;
/*     */   private int fragmentedFramesCount;
/*     */   private boolean frameFinalFlag;
/*     */   private boolean frameMasked;
/*     */   private int frameRsv;
/*     */   private int frameOpcode;
/*     */   private long framePayloadLength;
/*     */   private byte[] maskingKey;
/*     */   private int framePayloadLen1;
/*     */   private boolean receivedClosingHandshake;
/* 107 */   private State state = State.READING_FIRST;
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
/*     */   public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
/* 122 */     this(expectMaskedFrames, allowExtensions, maxFramePayloadLength, false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
/* 142 */     this(WebSocketDecoderConfig.newBuilder()
/* 143 */         .expectMaskedFrames(expectMaskedFrames)
/* 144 */         .allowExtensions(allowExtensions)
/* 145 */         .maxFramePayloadLength(maxFramePayloadLength)
/* 146 */         .allowMaskMismatch(allowMaskMismatch)
/* 147 */         .build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocket08FrameDecoder(WebSocketDecoderConfig decoderConfig) {
/* 157 */     this.config = (WebSocketDecoderConfig)ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
/*     */   }
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*     */     byte b;
/*     */     ByteBuf payloadBuffer;
/* 163 */     if (this.receivedClosingHandshake) {
/* 164 */       in.skipBytes(actualReadableBytes());
/*     */       
/*     */       return;
/*     */     } 
/* 168 */     switch (this.state) {
/*     */       case READING_FIRST:
/* 170 */         if (!in.isReadable()) {
/*     */           return;
/*     */         }
/*     */         
/* 174 */         this.framePayloadLength = 0L;
/*     */ 
/*     */         
/* 177 */         b = in.readByte();
/* 178 */         this.frameFinalFlag = ((b & 0x80) != 0);
/* 179 */         this.frameRsv = (b & 0x70) >> 4;
/* 180 */         this.frameOpcode = b & 0xF;
/*     */         
/* 182 */         if (logger.isTraceEnabled()) {
/* 183 */           logger.trace("Decoding WebSocket Frame opCode={}", Integer.valueOf(this.frameOpcode));
/*     */         }
/*     */         
/* 186 */         this.state = State.READING_SECOND;
/*     */       case READING_SECOND:
/* 188 */         if (!in.isReadable()) {
/*     */           return;
/*     */         }
/*     */         
/* 192 */         b = in.readByte();
/* 193 */         this.frameMasked = ((b & 0x80) != 0);
/* 194 */         this.framePayloadLen1 = b & Byte.MAX_VALUE;
/*     */         
/* 196 */         if (this.frameRsv != 0 && !this.config.allowExtensions()) {
/* 197 */           protocolViolation(ctx, in, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
/*     */           
/*     */           return;
/*     */         } 
/* 201 */         if (!this.config.allowMaskMismatch() && this.config.expectMaskedFrames() != this.frameMasked) {
/* 202 */           protocolViolation(ctx, in, "received a frame that is not masked as expected");
/*     */           
/*     */           return;
/*     */         } 
/* 206 */         if (this.frameOpcode > 7) {
/*     */ 
/*     */           
/* 209 */           if (!this.frameFinalFlag) {
/* 210 */             protocolViolation(ctx, in, "fragmented control frame");
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/* 215 */           if (this.framePayloadLen1 > 125) {
/* 216 */             protocolViolation(ctx, in, "control frame with payload length > 125 octets");
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/* 221 */           if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
/*     */             
/* 223 */             protocolViolation(ctx, in, "control frame using reserved opcode " + this.frameOpcode);
/*     */ 
/*     */             
/*     */             return;
/*     */           } 
/*     */ 
/*     */           
/* 230 */           if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
/* 231 */             protocolViolation(ctx, in, "received close control frame with payload len 1");
/*     */             
/*     */             return;
/*     */           } 
/*     */         } else {
/* 236 */           if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
/*     */             
/* 238 */             protocolViolation(ctx, in, "data frame using reserved opcode " + this.frameOpcode);
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/* 243 */           if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
/* 244 */             protocolViolation(ctx, in, "received continuation data frame outside fragmented message");
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/* 249 */           if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
/* 250 */             protocolViolation(ctx, in, "received non-continuation data frame while inside fragmented message");
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/*     */         
/* 256 */         this.state = State.READING_SIZE;
/*     */ 
/*     */       
/*     */       case READING_SIZE:
/* 260 */         if (this.framePayloadLen1 == 126) {
/* 261 */           if (in.readableBytes() < 2) {
/*     */             return;
/*     */           }
/* 264 */           this.framePayloadLength = in.readUnsignedShort();
/* 265 */           if (this.framePayloadLength < 126L) {
/* 266 */             protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
/*     */             return;
/*     */           } 
/* 269 */         } else if (this.framePayloadLen1 == 127) {
/* 270 */           if (in.readableBytes() < 8) {
/*     */             return;
/*     */           }
/* 273 */           this.framePayloadLength = in.readLong();
/*     */ 
/*     */ 
/*     */           
/* 277 */           if (this.framePayloadLength < 65536L) {
/* 278 */             protocolViolation(ctx, in, "invalid data frame length (not using minimal length encoding)");
/*     */             return;
/*     */           } 
/*     */         } else {
/* 282 */           this.framePayloadLength = this.framePayloadLen1;
/*     */         } 
/*     */         
/* 285 */         if (this.framePayloadLength > this.config.maxFramePayloadLength()) {
/* 286 */           protocolViolation(ctx, in, WebSocketCloseStatus.MESSAGE_TOO_BIG, "Max frame length of " + this.config
/* 287 */               .maxFramePayloadLength() + " has been exceeded.");
/*     */           
/*     */           return;
/*     */         } 
/* 291 */         if (logger.isTraceEnabled()) {
/* 292 */           logger.trace("Decoding WebSocket Frame length={}", Long.valueOf(this.framePayloadLength));
/*     */         }
/*     */         
/* 295 */         this.state = State.MASKING_KEY;
/*     */       case MASKING_KEY:
/* 297 */         if (this.frameMasked) {
/* 298 */           if (in.readableBytes() < 4) {
/*     */             return;
/*     */           }
/* 301 */           if (this.maskingKey == null) {
/* 302 */             this.maskingKey = new byte[4];
/*     */           }
/* 304 */           in.readBytes(this.maskingKey);
/*     */         } 
/* 306 */         this.state = State.PAYLOAD;
/*     */       case PAYLOAD:
/* 308 */         if (in.readableBytes() < this.framePayloadLength) {
/*     */           return;
/*     */         }
/*     */         
/* 312 */         payloadBuffer = null;
/*     */         try {
/* 314 */           payloadBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toFrameLength(this.framePayloadLength));
/*     */ 
/*     */ 
/*     */           
/* 318 */           this.state = State.READING_FIRST;
/*     */ 
/*     */           
/* 321 */           if (this.frameMasked) {
/* 322 */             unmask(payloadBuffer);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 327 */           if (this.frameOpcode == 9) {
/* 328 */             out.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/* 329 */             payloadBuffer = null;
/*     */             return;
/*     */           } 
/* 332 */           if (this.frameOpcode == 10) {
/* 333 */             out.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/* 334 */             payloadBuffer = null;
/*     */             return;
/*     */           } 
/* 337 */           if (this.frameOpcode == 8) {
/* 338 */             this.receivedClosingHandshake = true;
/* 339 */             checkCloseFrameBody(ctx, payloadBuffer);
/* 340 */             out.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/* 341 */             payloadBuffer = null;
/*     */ 
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/* 347 */           if (this.frameFinalFlag) {
/*     */ 
/*     */             
/* 350 */             if (this.frameOpcode != 9) {
/* 351 */               this.fragmentedFramesCount = 0;
/*     */             }
/*     */           } else {
/*     */             
/* 355 */             this.fragmentedFramesCount++;
/*     */           } 
/*     */ 
/*     */           
/* 359 */           if (this.frameOpcode == 1) {
/* 360 */             out.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/* 361 */             payloadBuffer = null; return;
/*     */           } 
/* 363 */           if (this.frameOpcode == 2) {
/* 364 */             out.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/* 365 */             payloadBuffer = null; return;
/*     */           } 
/* 367 */           if (this.frameOpcode == 0) {
/* 368 */             out.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, payloadBuffer));
/*     */             
/* 370 */             payloadBuffer = null;
/*     */             return;
/*     */           } 
/* 373 */           throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
/*     */         }
/*     */         finally {
/*     */           
/* 377 */           if (payloadBuffer != null) {
/* 378 */             payloadBuffer.release();
/*     */           }
/*     */         } 
/*     */       case CORRUPT:
/* 382 */         if (in.isReadable())
/*     */         {
/*     */           
/* 385 */           in.readByte();
/*     */         }
/*     */         return;
/*     */     } 
/* 389 */     throw new Error("Shouldn't reach here.");
/*     */   }
/*     */ 
/*     */   
/*     */   private void unmask(ByteBuf frame) {
/* 394 */     int i = frame.readerIndex();
/* 395 */     int end = frame.writerIndex();
/*     */     
/* 397 */     ByteOrder order = frame.order();
/*     */ 
/*     */ 
/*     */     
/* 401 */     int intMask = (this.maskingKey[0] & 0xFF) << 24 | (this.maskingKey[1] & 0xFF) << 16 | (this.maskingKey[2] & 0xFF) << 8 | this.maskingKey[3] & 0xFF;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 408 */     if (order == ByteOrder.LITTLE_ENDIAN) {
/* 409 */       intMask = Integer.reverseBytes(intMask);
/*     */     }
/*     */     
/* 412 */     for (; i + 3 < end; i += 4) {
/* 413 */       int unmasked = frame.getInt(i) ^ intMask;
/* 414 */       frame.setInt(i, unmasked);
/*     */     } 
/* 416 */     for (; i < end; i++) {
/* 417 */       frame.setByte(i, frame.getByte(i) ^ this.maskingKey[i % 4]);
/*     */     }
/*     */   }
/*     */   
/*     */   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, String reason) {
/* 422 */     protocolViolation(ctx, in, WebSocketCloseStatus.PROTOCOL_ERROR, reason);
/*     */   }
/*     */   
/*     */   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, WebSocketCloseStatus status, String reason) {
/* 426 */     protocolViolation(ctx, in, new CorruptedWebSocketFrameException(status, reason));
/*     */   }
/*     */   
/*     */   private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, CorruptedWebSocketFrameException ex) {
/* 430 */     this.state = State.CORRUPT;
/* 431 */     int readableBytes = in.readableBytes();
/* 432 */     if (readableBytes > 0)
/*     */     {
/*     */       
/* 435 */       in.skipBytes(readableBytes);
/*     */     }
/* 437 */     if (ctx.channel().isActive() && this.config.closeOnProtocolViolation()) {
/*     */       Object closeMessage;
/* 439 */       if (this.receivedClosingHandshake) {
/* 440 */         closeMessage = Unpooled.EMPTY_BUFFER;
/*     */       } else {
/* 442 */         WebSocketCloseStatus closeStatus = ex.closeStatus();
/* 443 */         String reasonText = ex.getMessage();
/* 444 */         if (reasonText == null) {
/* 445 */           reasonText = closeStatus.reasonText();
/*     */         }
/* 447 */         closeMessage = new CloseWebSocketFrame(closeStatus, reasonText);
/*     */       } 
/* 449 */       ctx.writeAndFlush(closeMessage).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */     } 
/* 451 */     throw ex;
/*     */   }
/*     */   
/*     */   private static int toFrameLength(long l) {
/* 455 */     if (l > 2147483647L) {
/* 456 */       throw new TooLongFrameException("Length:" + l);
/*     */     }
/* 458 */     return (int)l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkCloseFrameBody(ChannelHandlerContext ctx, ByteBuf buffer) {
/* 465 */     if (buffer == null || !buffer.isReadable()) {
/*     */       return;
/*     */     }
/* 468 */     if (buffer.readableBytes() == 1) {
/* 469 */       protocolViolation(ctx, buffer, WebSocketCloseStatus.INVALID_PAYLOAD_DATA, "Invalid close frame body");
/*     */     }
/*     */ 
/*     */     
/* 473 */     int idx = buffer.readerIndex();
/* 474 */     buffer.readerIndex(0);
/*     */ 
/*     */     
/* 477 */     int statusCode = buffer.readShort();
/* 478 */     if (!WebSocketCloseStatus.isValidStatusCode(statusCode)) {
/* 479 */       protocolViolation(ctx, buffer, "Invalid close frame getStatus code: " + statusCode);
/*     */     }
/*     */ 
/*     */     
/* 483 */     if (buffer.isReadable()) {
/*     */       try {
/* 485 */         (new Utf8Validator()).check(buffer);
/* 486 */       } catch (CorruptedWebSocketFrameException ex) {
/* 487 */         protocolViolation(ctx, buffer, ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 492 */     buffer.readerIndex(idx);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocket08FrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */