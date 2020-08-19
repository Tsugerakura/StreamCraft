/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ByteToMessageDecoder
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*  80 */   public static final Cumulator MERGE_CUMULATOR = new Cumulator()
/*     */     {
/*     */       public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
/*  83 */         if (!cumulation.isReadable() && in.isContiguous()) {
/*     */           
/*  85 */           cumulation.release();
/*  86 */           return in;
/*     */         } 
/*     */         try {
/*  89 */           int required = in.readableBytes();
/*  90 */           if (required > cumulation.maxWritableBytes() || (required > cumulation
/*  91 */             .maxFastWritableBytes() && cumulation.refCnt() > 1) || cumulation
/*  92 */             .isReadOnly())
/*     */           {
/*     */ 
/*     */ 
/*     */             
/*  97 */             return ByteToMessageDecoder.expandCumulation(alloc, cumulation, in);
/*     */           }
/*  99 */           cumulation.writeBytes(in, in.readerIndex(), required);
/* 100 */           in.readerIndex(in.writerIndex());
/* 101 */           return cumulation;
/*     */         }
/*     */         finally {
/*     */           
/* 105 */           in.release();
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final Cumulator COMPOSITE_CUMULATOR = new Cumulator()
/*     */     {
/*     */       public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
/* 118 */         if (!cumulation.isReadable()) {
/* 119 */           cumulation.release();
/* 120 */           return in;
/*     */         } 
/* 122 */         CompositeByteBuf composite = null;
/*     */         try {
/* 124 */           if (cumulation instanceof CompositeByteBuf && cumulation.refCnt() == 1) {
/* 125 */             composite = (CompositeByteBuf)cumulation;
/*     */ 
/*     */             
/* 128 */             if (composite.writerIndex() != composite.capacity()) {
/* 129 */               composite.capacity(composite.writerIndex());
/*     */             }
/*     */           } else {
/* 132 */             composite = alloc.compositeBuffer(2147483647).addFlattenedComponents(true, cumulation);
/*     */           } 
/* 134 */           composite.addFlattenedComponents(true, in);
/* 135 */           in = null;
/* 136 */           return (ByteBuf)composite;
/*     */         } finally {
/* 138 */           if (in != null) {
/*     */             
/* 140 */             in.release();
/*     */             
/* 142 */             if (composite != null && composite != cumulation) {
/* 143 */               composite.release();
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private static final byte STATE_INIT = 0;
/*     */   private static final byte STATE_CALLING_CHILD_DECODE = 1;
/*     */   private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
/*     */   ByteBuf cumulation;
/* 155 */   private Cumulator cumulator = MERGE_CUMULATOR;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean singleDecode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean first;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean firedChannelRead;
/*     */ 
/*     */ 
/*     */   
/* 173 */   private byte decodeState = 0;
/* 174 */   private int discardAfterReads = 16;
/*     */   private int numReads;
/*     */   
/*     */   protected ByteToMessageDecoder() {
/* 178 */     ensureNotSharable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSingleDecode(boolean singleDecode) {
/* 188 */     this.singleDecode = singleDecode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSingleDecode() {
/* 198 */     return this.singleDecode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCumulator(Cumulator cumulator) {
/* 205 */     this.cumulator = (Cumulator)ObjectUtil.checkNotNull(cumulator, "cumulator");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDiscardAfterReads(int discardAfterReads) {
/* 213 */     ObjectUtil.checkPositive(discardAfterReads, "discardAfterReads");
/* 214 */     this.discardAfterReads = discardAfterReads;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int actualReadableBytes() {
/* 224 */     return internalBuffer().readableBytes();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuf internalBuffer() {
/* 233 */     if (this.cumulation != null) {
/* 234 */       return this.cumulation;
/*     */     }
/* 236 */     return Unpooled.EMPTY_BUFFER;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 242 */     if (this.decodeState == 1) {
/* 243 */       this.decodeState = 2;
/*     */       return;
/*     */     } 
/* 246 */     ByteBuf buf = this.cumulation;
/* 247 */     if (buf != null) {
/*     */       
/* 249 */       this.cumulation = null;
/* 250 */       this.numReads = 0;
/* 251 */       int readable = buf.readableBytes();
/* 252 */       if (readable > 0) {
/* 253 */         ctx.fireChannelRead(buf);
/* 254 */         ctx.fireChannelReadComplete();
/*     */       } else {
/* 256 */         buf.release();
/*     */       } 
/*     */     } 
/* 259 */     handlerRemoved0(ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 270 */     if (msg instanceof ByteBuf) {
/* 271 */       CodecOutputList out = CodecOutputList.newInstance();
/*     */       try {
/* 273 */         this.first = (this.cumulation == null);
/* 274 */         this.cumulation = this.cumulator.cumulate(ctx.alloc(), this.first ? Unpooled.EMPTY_BUFFER : this.cumulation, (ByteBuf)msg);
/*     */         
/* 276 */         callDecode(ctx, this.cumulation, out);
/* 277 */       } catch (DecoderException e) {
/* 278 */         throw e;
/* 279 */       } catch (Exception e) {
/* 280 */         throw new DecoderException(e);
/*     */       } finally {
/* 282 */         if (this.cumulation != null && !this.cumulation.isReadable()) {
/* 283 */           this.numReads = 0;
/* 284 */           this.cumulation.release();
/* 285 */           this.cumulation = null;
/* 286 */         } else if (++this.numReads >= this.discardAfterReads) {
/*     */ 
/*     */           
/* 289 */           this.numReads = 0;
/* 290 */           discardSomeReadBytes();
/*     */         } 
/*     */         
/* 293 */         int size = out.size();
/* 294 */         this.firedChannelRead |= out.insertSinceRecycled();
/* 295 */         fireChannelRead(ctx, out, size);
/* 296 */         out.recycle();
/*     */       } 
/*     */     } else {
/* 299 */       ctx.fireChannelRead(msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void fireChannelRead(ChannelHandlerContext ctx, List<Object> msgs, int numElements) {
/* 307 */     if (msgs instanceof CodecOutputList) {
/* 308 */       fireChannelRead(ctx, (CodecOutputList)msgs, numElements);
/*     */     } else {
/* 310 */       for (int i = 0; i < numElements; i++) {
/* 311 */         ctx.fireChannelRead(msgs.get(i));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void fireChannelRead(ChannelHandlerContext ctx, CodecOutputList msgs, int numElements) {
/* 320 */     for (int i = 0; i < numElements; i++) {
/* 321 */       ctx.fireChannelRead(msgs.getUnsafe(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 327 */     this.numReads = 0;
/* 328 */     discardSomeReadBytes();
/* 329 */     if (!this.firedChannelRead && !ctx.channel().config().isAutoRead()) {
/* 330 */       ctx.read();
/*     */     }
/* 332 */     this.firedChannelRead = false;
/* 333 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */   
/*     */   protected final void discardSomeReadBytes() {
/* 337 */     if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 345 */       this.cumulation.discardSomeReadBytes();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 351 */     channelInputClosed(ctx, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 356 */     if (evt instanceof pro.gravit.repackage.io.netty.channel.socket.ChannelInputShutdownEvent)
/*     */     {
/*     */ 
/*     */       
/* 360 */       channelInputClosed(ctx, false);
/*     */     }
/* 362 */     super.userEventTriggered(ctx, evt);
/*     */   }
/*     */   
/*     */   private void channelInputClosed(ChannelHandlerContext ctx, boolean callChannelInactive) {
/* 366 */     CodecOutputList out = CodecOutputList.newInstance();
/*     */     try {
/* 368 */       channelInputClosed(ctx, out);
/* 369 */     } catch (DecoderException e) {
/* 370 */       throw e;
/* 371 */     } catch (Exception e) {
/* 372 */       throw new DecoderException(e);
/*     */     } finally {
/*     */       try {
/* 375 */         if (this.cumulation != null) {
/* 376 */           this.cumulation.release();
/* 377 */           this.cumulation = null;
/*     */         } 
/* 379 */         int size = out.size();
/* 380 */         fireChannelRead(ctx, out, size);
/* 381 */         if (size > 0)
/*     */         {
/* 383 */           ctx.fireChannelReadComplete();
/*     */         }
/* 385 */         if (callChannelInactive) {
/* 386 */           ctx.fireChannelInactive();
/*     */         }
/*     */       } finally {
/*     */         
/* 390 */         out.recycle();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
/* 400 */     if (this.cumulation != null) {
/* 401 */       callDecode(ctx, this.cumulation, out);
/* 402 */       decodeLast(ctx, this.cumulation, out);
/*     */     } else {
/* 404 */       decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
/*     */     } 
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
/*     */   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
/*     */     try {
/* 418 */       while (in.isReadable()) {
/* 419 */         int outSize = out.size();
/*     */         
/* 421 */         if (outSize > 0) {
/* 422 */           fireChannelRead(ctx, out, outSize);
/* 423 */           out.clear();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 430 */           if (ctx.isRemoved()) {
/*     */             break;
/*     */           }
/* 433 */           outSize = 0;
/*     */         } 
/*     */         
/* 436 */         int oldInputLength = in.readableBytes();
/* 437 */         decodeRemovalReentryProtection(ctx, in, out);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 443 */         if (ctx.isRemoved()) {
/*     */           break;
/*     */         }
/*     */         
/* 447 */         if (outSize == out.size()) {
/* 448 */           if (oldInputLength == in.readableBytes()) {
/*     */             break;
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 455 */         if (oldInputLength == in.readableBytes()) {
/* 456 */           throw new DecoderException(
/* 457 */               StringUtil.simpleClassName(getClass()) + ".decode() did not read anything but decoded a message.");
/*     */         }
/*     */ 
/*     */         
/* 461 */         if (isSingleDecode()) {
/*     */           break;
/*     */         }
/*     */       } 
/* 465 */     } catch (DecoderException e) {
/* 466 */       throw e;
/* 467 */     } catch (Exception cause) {
/* 468 */       throw new DecoderException(cause);
/*     */     } 
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
/*     */   protected abstract void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, List<Object> paramList) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 496 */     this.decodeState = 1;
/*     */     try {
/* 498 */       decode(ctx, in, out);
/*     */     } finally {
/* 500 */       boolean removePending = (this.decodeState == 2);
/* 501 */       this.decodeState = 0;
/* 502 */       if (removePending) {
/* 503 */         fireChannelRead(ctx, out, out.size());
/* 504 */         out.clear();
/* 505 */         handlerRemoved(ctx);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 518 */     if (in.isReadable())
/*     */     {
/*     */       
/* 521 */       decodeRemovalReentryProtection(ctx, in, out);
/*     */     }
/*     */   }
/*     */   
/*     */   static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf oldCumulation, ByteBuf in) {
/* 526 */     int oldBytes = oldCumulation.readableBytes();
/* 527 */     int newBytes = in.readableBytes();
/* 528 */     int totalBytes = oldBytes + newBytes;
/* 529 */     ByteBuf newCumulation = alloc.buffer(alloc.calculateNewCapacity(totalBytes, 2147483647));
/* 530 */     ByteBuf toRelease = newCumulation;
/*     */     
/*     */     try {
/* 533 */       newCumulation.setBytes(0, oldCumulation, oldCumulation.readerIndex(), oldBytes)
/* 534 */         .setBytes(oldBytes, in, in.readerIndex(), newBytes)
/* 535 */         .writerIndex(totalBytes);
/* 536 */       in.readerIndex(in.writerIndex());
/* 537 */       toRelease = oldCumulation;
/* 538 */       return newCumulation;
/*     */     } finally {
/* 540 */       toRelease.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface Cumulator {
/*     */     ByteBuf cumulate(ByteBufAllocator param1ByteBufAllocator, ByteBuf param1ByteBuf1, ByteBuf param1ByteBuf2);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\ByteToMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */