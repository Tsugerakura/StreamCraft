/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*     */ public class LengthFieldBasedFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private final ByteOrder byteOrder;
/*     */   private final int maxFrameLength;
/*     */   private final int lengthFieldOffset;
/*     */   private final int lengthFieldLength;
/*     */   private final int lengthFieldEndOffset;
/*     */   private final int lengthAdjustment;
/*     */   private final int initialBytesToStrip;
/*     */   private final boolean failFast;
/*     */   private boolean discardingTooLongFrame;
/*     */   private long tooLongFrameLength;
/*     */   private long bytesToDiscard;
/*     */   
/*     */   public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
/* 216 */     this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
/* 239 */     this(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, true);
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
/*     */   public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
/* 271 */     this(ByteOrder.BIG_ENDIAN, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
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
/*     */   public LengthFieldBasedFrameDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
/* 305 */     this.byteOrder = (ByteOrder)ObjectUtil.checkNotNull(byteOrder, "byteOrder");
/*     */     
/* 307 */     ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
/*     */     
/* 309 */     ObjectUtil.checkPositiveOrZero(lengthFieldOffset, "lengthFieldOffset");
/*     */     
/* 311 */     ObjectUtil.checkPositiveOrZero(initialBytesToStrip, "initialBytesToStrip");
/*     */     
/* 313 */     if (lengthFieldOffset > maxFrameLength - lengthFieldLength) {
/* 314 */       throw new IllegalArgumentException("maxFrameLength (" + maxFrameLength + ") must be equal to or greater than lengthFieldOffset (" + lengthFieldOffset + ") + lengthFieldLength (" + lengthFieldLength + ").");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 321 */     this.maxFrameLength = maxFrameLength;
/* 322 */     this.lengthFieldOffset = lengthFieldOffset;
/* 323 */     this.lengthFieldLength = lengthFieldLength;
/* 324 */     this.lengthAdjustment = lengthAdjustment;
/* 325 */     this.lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
/* 326 */     this.initialBytesToStrip = initialBytesToStrip;
/* 327 */     this.failFast = failFast;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 332 */     Object decoded = decode(ctx, in);
/* 333 */     if (decoded != null) {
/* 334 */       out.add(decoded);
/*     */     }
/*     */   }
/*     */   
/*     */   private void discardingTooLongFrame(ByteBuf in) {
/* 339 */     long bytesToDiscard = this.bytesToDiscard;
/* 340 */     int localBytesToDiscard = (int)Math.min(bytesToDiscard, in.readableBytes());
/* 341 */     in.skipBytes(localBytesToDiscard);
/* 342 */     bytesToDiscard -= localBytesToDiscard;
/* 343 */     this.bytesToDiscard = bytesToDiscard;
/*     */     
/* 345 */     failIfNecessary(false);
/*     */   }
/*     */   
/*     */   private static void failOnNegativeLengthField(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
/* 349 */     in.skipBytes(lengthFieldEndOffset);
/* 350 */     throw new CorruptedFrameException("negative pre-adjustment length field: " + frameLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void failOnFrameLengthLessThanLengthFieldEndOffset(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
/* 357 */     in.skipBytes(lengthFieldEndOffset);
/* 358 */     throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less than lengthFieldEndOffset: " + lengthFieldEndOffset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void exceededFrameLength(ByteBuf in, long frameLength) {
/* 364 */     long discard = frameLength - in.readableBytes();
/* 365 */     this.tooLongFrameLength = frameLength;
/*     */     
/* 367 */     if (discard < 0L) {
/*     */       
/* 369 */       in.skipBytes((int)frameLength);
/*     */     } else {
/*     */       
/* 372 */       this.discardingTooLongFrame = true;
/* 373 */       this.bytesToDiscard = discard;
/* 374 */       in.skipBytes(in.readableBytes());
/*     */     } 
/* 376 */     failIfNecessary(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void failOnFrameLengthLessThanInitialBytesToStrip(ByteBuf in, long frameLength, int initialBytesToStrip) {
/* 382 */     in.skipBytes((int)frameLength);
/* 383 */     throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less than initialBytesToStrip: " + initialBytesToStrip);
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
/*     */   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
/* 397 */     if (this.discardingTooLongFrame) {
/* 398 */       discardingTooLongFrame(in);
/*     */     }
/*     */     
/* 401 */     if (in.readableBytes() < this.lengthFieldEndOffset) {
/* 402 */       return null;
/*     */     }
/*     */     
/* 405 */     int actualLengthFieldOffset = in.readerIndex() + this.lengthFieldOffset;
/* 406 */     long frameLength = getUnadjustedFrameLength(in, actualLengthFieldOffset, this.lengthFieldLength, this.byteOrder);
/*     */     
/* 408 */     if (frameLength < 0L) {
/* 409 */       failOnNegativeLengthField(in, frameLength, this.lengthFieldEndOffset);
/*     */     }
/*     */     
/* 412 */     frameLength += (this.lengthAdjustment + this.lengthFieldEndOffset);
/*     */     
/* 414 */     if (frameLength < this.lengthFieldEndOffset) {
/* 415 */       failOnFrameLengthLessThanLengthFieldEndOffset(in, frameLength, this.lengthFieldEndOffset);
/*     */     }
/*     */     
/* 418 */     if (frameLength > this.maxFrameLength) {
/* 419 */       exceededFrameLength(in, frameLength);
/* 420 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 424 */     int frameLengthInt = (int)frameLength;
/* 425 */     if (in.readableBytes() < frameLengthInt) {
/* 426 */       return null;
/*     */     }
/*     */     
/* 429 */     if (this.initialBytesToStrip > frameLengthInt) {
/* 430 */       failOnFrameLengthLessThanInitialBytesToStrip(in, frameLength, this.initialBytesToStrip);
/*     */     }
/* 432 */     in.skipBytes(this.initialBytesToStrip);
/*     */ 
/*     */     
/* 435 */     int readerIndex = in.readerIndex();
/* 436 */     int actualFrameLength = frameLengthInt - this.initialBytesToStrip;
/* 437 */     ByteBuf frame = extractFrame(ctx, in, readerIndex, actualFrameLength);
/* 438 */     in.readerIndex(readerIndex + actualFrameLength);
/* 439 */     return frame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
/*     */     long frameLength;
/* 451 */     buf = buf.order(order);
/*     */     
/* 453 */     switch (length) {
/*     */       case 1:
/* 455 */         frameLength = buf.getUnsignedByte(offset);
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
/* 473 */         return frameLength;case 2: frameLength = buf.getUnsignedShort(offset); return frameLength;case 3: frameLength = buf.getUnsignedMedium(offset); return frameLength;case 4: frameLength = buf.getUnsignedInt(offset); return frameLength;case 8: frameLength = buf.getLong(offset); return frameLength;
/*     */     } 
/*     */     throw new DecoderException("unsupported lengthFieldLength: " + this.lengthFieldLength + " (expected: 1, 2, 3, 4, or 8)");
/*     */   } private void failIfNecessary(boolean firstDetectionOfTooLongFrame) {
/* 477 */     if (this.bytesToDiscard == 0L) {
/*     */ 
/*     */       
/* 480 */       long tooLongFrameLength = this.tooLongFrameLength;
/* 481 */       this.tooLongFrameLength = 0L;
/* 482 */       this.discardingTooLongFrame = false;
/* 483 */       if (!this.failFast || firstDetectionOfTooLongFrame) {
/* 484 */         fail(tooLongFrameLength);
/*     */       
/*     */       }
/*     */     }
/* 488 */     else if (this.failFast && firstDetectionOfTooLongFrame) {
/* 489 */       fail(this.tooLongFrameLength);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
/* 498 */     return buffer.retainedSlice(index, length);
/*     */   }
/*     */   
/*     */   private void fail(long frameLength) {
/* 502 */     if (frameLength > 0L) {
/* 503 */       throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
/*     */     }
/*     */ 
/*     */     
/* 507 */     throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\LengthFieldBasedFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */