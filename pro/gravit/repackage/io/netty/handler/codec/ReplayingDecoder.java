/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.util.Signal;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ReplayingDecoder<S>
/*     */   extends ByteToMessageDecoder
/*     */ {
/* 270 */   static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class, "REPLAY");
/*     */   
/* 272 */   private final ReplayingDecoderByteBuf replayable = new ReplayingDecoderByteBuf();
/*     */   private S state;
/* 274 */   private int checkpoint = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ReplayingDecoder() {
/* 280 */     this((S)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ReplayingDecoder(S initialState) {
/* 287 */     this.state = initialState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkpoint() {
/* 294 */     this.checkpoint = internalBuffer().readerIndex();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkpoint(S state) {
/* 302 */     checkpoint();
/* 303 */     state(state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected S state() {
/* 311 */     return this.state;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected S state(S newState) {
/* 319 */     S oldState = this.state;
/* 320 */     this.state = newState;
/* 321 */     return oldState;
/*     */   }
/*     */ 
/*     */   
/*     */   final void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
/*     */     try {
/* 327 */       this.replayable.terminate();
/* 328 */       if (this.cumulation != null) {
/* 329 */         callDecode(ctx, internalBuffer(), out);
/*     */       } else {
/* 331 */         this.replayable.setCumulation(Unpooled.EMPTY_BUFFER);
/*     */       } 
/* 333 */       decodeLast(ctx, this.replayable, out);
/* 334 */     } catch (Signal replay) {
/*     */       
/* 336 */       replay.expect(REPLAY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
/* 342 */     this.replayable.setCumulation(in);
/*     */     try {
/* 344 */       while (in.isReadable()) {
/* 345 */         int oldReaderIndex = this.checkpoint = in.readerIndex();
/* 346 */         int outSize = out.size();
/*     */         
/* 348 */         if (outSize > 0) {
/* 349 */           fireChannelRead(ctx, out, outSize);
/* 350 */           out.clear();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 357 */           if (ctx.isRemoved()) {
/*     */             break;
/*     */           }
/* 360 */           outSize = 0;
/*     */         } 
/*     */         
/* 363 */         S oldState = this.state;
/* 364 */         int oldInputLength = in.readableBytes();
/*     */         try {
/* 366 */           decodeRemovalReentryProtection(ctx, this.replayable, out);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 372 */           if (ctx.isRemoved()) {
/*     */             break;
/*     */           }
/*     */           
/* 376 */           if (outSize == out.size()) {
/* 377 */             if (oldInputLength == in.readableBytes() && oldState == this.state) {
/* 378 */               throw new DecoderException(
/* 379 */                   StringUtil.simpleClassName(getClass()) + ".decode() must consume the inbound data or change its state if it did not decode anything.");
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             continue;
/*     */           } 
/* 387 */         } catch (Signal replay) {
/* 388 */           replay.expect(REPLAY);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 394 */           if (ctx.isRemoved()) {
/*     */             break;
/*     */           }
/*     */ 
/*     */           
/* 399 */           int checkpoint = this.checkpoint;
/* 400 */           if (checkpoint >= 0) {
/* 401 */             in.readerIndex(checkpoint);
/*     */           }
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */ 
/*     */         
/* 409 */         if (oldReaderIndex == in.readerIndex() && oldState == this.state) {
/* 410 */           throw new DecoderException(
/* 411 */               StringUtil.simpleClassName(getClass()) + ".decode() method must consume the inbound data or change its state if it decoded something.");
/*     */         }
/*     */         
/* 414 */         if (isSingleDecode()) {
/*     */           break;
/*     */         }
/*     */       } 
/* 418 */     } catch (DecoderException e) {
/* 419 */       throw e;
/* 420 */     } catch (Exception cause) {
/* 421 */       throw new DecoderException(cause);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\ReplayingDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */