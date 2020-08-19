/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ public abstract class MessageAggregator<I, S, C extends ByteBufHolder, O extends ByteBufHolder>
/*     */   extends MessageToMessageDecoder<I>
/*     */ {
/*     */   private static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
/*     */   private final int maxContentLength;
/*     */   private O currentMessage;
/*     */   private boolean handlingOversizedMessage;
/*  61 */   private int maxCumulationBufferComponents = 1024;
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelHandlerContext ctx;
/*     */ 
/*     */   
/*     */   private ChannelFutureListener continueResponseWriteListener;
/*     */ 
/*     */   
/*     */   private boolean aggregating;
/*     */ 
/*     */ 
/*     */   
/*     */   protected MessageAggregator(int maxContentLength) {
/*  76 */     validateMaxContentLength(maxContentLength);
/*  77 */     this.maxContentLength = maxContentLength;
/*     */   }
/*     */   
/*     */   protected MessageAggregator(int maxContentLength, Class<? extends I> inboundMessageType) {
/*  81 */     super(inboundMessageType);
/*  82 */     validateMaxContentLength(maxContentLength);
/*  83 */     this.maxContentLength = maxContentLength;
/*     */   }
/*     */   
/*     */   private static void validateMaxContentLength(int maxContentLength) {
/*  87 */     ObjectUtil.checkPositiveOrZero(maxContentLength, "maxContentLength");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean acceptInboundMessage(Object msg) throws Exception {
/*  93 */     if (!super.acceptInboundMessage(msg)) {
/*  94 */       return false;
/*     */     }
/*     */ 
/*     */     
/*  98 */     I in = (I)msg;
/*     */     
/* 100 */     if (isAggregated(in)) {
/* 101 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 106 */     if (isStartMessage(in)) {
/* 107 */       this.aggregating = true;
/* 108 */       return true;
/* 109 */     }  if (this.aggregating && isContentMessage(in)) {
/* 110 */       return true;
/*     */     }
/*     */     
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isStartMessage(I paramI) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isContentMessage(I paramI) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isLastContentMessage(C paramC) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isAggregated(I paramI) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int maxContentLength() {
/* 157 */     return this.maxContentLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int maxCumulationBufferComponents() {
/* 167 */     return this.maxCumulationBufferComponents;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setMaxCumulationBufferComponents(int maxCumulationBufferComponents) {
/* 178 */     if (maxCumulationBufferComponents < 2) {
/* 179 */       throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 184 */     if (this.ctx == null) {
/* 185 */       this.maxCumulationBufferComponents = maxCumulationBufferComponents;
/*     */     } else {
/* 187 */       throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final boolean isHandlingOversizedMessage() {
/* 197 */     return this.handlingOversizedMessage;
/*     */   }
/*     */   
/*     */   protected final ChannelHandlerContext ctx() {
/* 201 */     if (this.ctx == null) {
/* 202 */       throw new IllegalStateException("not added to a pipeline yet");
/*     */     }
/* 204 */     return this.ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(final ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception {
/* 209 */     assert this.aggregating;
/*     */     
/* 211 */     if (isStartMessage(msg)) {
/* 212 */       this.handlingOversizedMessage = false;
/* 213 */       if (this.currentMessage != null) {
/* 214 */         this.currentMessage.release();
/* 215 */         this.currentMessage = null;
/* 216 */         throw new MessageAggregationException();
/*     */       } 
/*     */ 
/*     */       
/* 220 */       I i = msg;
/*     */ 
/*     */ 
/*     */       
/* 224 */       Object continueResponse = newContinueResponse((S)i, this.maxContentLength, ctx.pipeline());
/* 225 */       if (continueResponse != null) {
/*     */         
/* 227 */         ChannelFutureListener listener = this.continueResponseWriteListener;
/* 228 */         if (listener == null) {
/* 229 */           this.continueResponseWriteListener = listener = new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 232 */                 if (!future.isSuccess()) {
/* 233 */                   ctx.fireExceptionCaught(future.cause());
/*     */                 }
/*     */               }
/*     */             };
/*     */         }
/*     */ 
/*     */         
/* 240 */         boolean closeAfterWrite = closeAfterContinueResponse(continueResponse);
/* 241 */         this.handlingOversizedMessage = ignoreContentAfterContinueResponse(continueResponse);
/*     */         
/* 243 */         ChannelFuture future = ctx.writeAndFlush(continueResponse).addListener((GenericFutureListener)listener);
/*     */         
/* 245 */         if (closeAfterWrite) {
/* 246 */           future.addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */           return;
/*     */         } 
/* 249 */         if (this.handlingOversizedMessage) {
/*     */           return;
/*     */         }
/* 252 */       } else if (isContentLengthInvalid((S)i, this.maxContentLength)) {
/*     */         
/* 254 */         invokeHandleOversizedMessage(ctx, (S)i);
/*     */         
/*     */         return;
/*     */       } 
/* 258 */       if (i instanceof DecoderResultProvider && !((DecoderResultProvider)i).decoderResult().isSuccess()) {
/*     */         O aggregated;
/* 260 */         if (i instanceof ByteBufHolder) {
/* 261 */           aggregated = beginAggregation((S)i, ((ByteBufHolder)i).content().retain());
/*     */         } else {
/* 263 */           aggregated = beginAggregation((S)i, Unpooled.EMPTY_BUFFER);
/*     */         } 
/* 265 */         finishAggregation0(aggregated);
/* 266 */         out.add(aggregated);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 271 */       CompositeByteBuf content = ctx.alloc().compositeBuffer(this.maxCumulationBufferComponents);
/* 272 */       if (i instanceof ByteBufHolder) {
/* 273 */         appendPartialContent(content, ((ByteBufHolder)i).content());
/*     */       }
/* 275 */       this.currentMessage = beginAggregation((S)i, (ByteBuf)content);
/* 276 */     } else if (isContentMessage(msg)) {
/* 277 */       boolean last; if (this.currentMessage == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 284 */       CompositeByteBuf content = (CompositeByteBuf)this.currentMessage.content();
/*     */ 
/*     */       
/* 287 */       ByteBufHolder byteBufHolder = (ByteBufHolder)msg;
/*     */       
/* 289 */       if (content.readableBytes() > this.maxContentLength - byteBufHolder.content().readableBytes()) {
/*     */ 
/*     */         
/* 292 */         O o = this.currentMessage;
/* 293 */         invokeHandleOversizedMessage(ctx, (S)o);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 298 */       appendPartialContent(content, byteBufHolder.content());
/*     */ 
/*     */       
/* 301 */       aggregate(this.currentMessage, (C)byteBufHolder);
/*     */ 
/*     */       
/* 304 */       if (byteBufHolder instanceof DecoderResultProvider) {
/* 305 */         DecoderResult decoderResult = ((DecoderResultProvider)byteBufHolder).decoderResult();
/* 306 */         if (!decoderResult.isSuccess()) {
/* 307 */           if (this.currentMessage instanceof DecoderResultProvider) {
/* 308 */             ((DecoderResultProvider)this.currentMessage).setDecoderResult(
/* 309 */                 DecoderResult.failure(decoderResult.cause()));
/*     */           }
/* 311 */           last = true;
/*     */         } else {
/* 313 */           last = isLastContentMessage((C)byteBufHolder);
/*     */         } 
/*     */       } else {
/* 316 */         last = isLastContentMessage((C)byteBufHolder);
/*     */       } 
/*     */       
/* 319 */       if (last) {
/* 320 */         finishAggregation0(this.currentMessage);
/*     */ 
/*     */         
/* 323 */         out.add(this.currentMessage);
/* 324 */         this.currentMessage = null;
/*     */       } 
/*     */     } else {
/* 327 */       throw new MessageAggregationException();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void appendPartialContent(CompositeByteBuf content, ByteBuf partialContent) {
/* 332 */     if (partialContent.isReadable()) {
/* 333 */       content.addComponent(true, partialContent.retain());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isContentLengthInvalid(S paramS, int paramInt) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Object newContinueResponse(S paramS, int paramInt, ChannelPipeline paramChannelPipeline) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean closeAfterContinueResponse(Object paramObject) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean ignoreContentAfterContinueResponse(Object paramObject) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract O beginAggregation(S paramS, ByteBuf paramByteBuf) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void aggregate(O aggregated, C content) throws Exception {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void finishAggregation0(O aggregated) throws Exception {
/* 391 */     this.aggregating = false;
/* 392 */     finishAggregation(aggregated);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finishAggregation(O aggregated) throws Exception {}
/*     */ 
/*     */   
/*     */   private void invokeHandleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
/* 401 */     this.handlingOversizedMessage = true;
/* 402 */     this.currentMessage = null;
/*     */     try {
/* 404 */       handleOversizedMessage(ctx, oversized);
/*     */     } finally {
/*     */       
/* 407 */       ReferenceCountUtil.release(oversized);
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
/*     */   protected void handleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
/* 419 */     ctx.fireExceptionCaught(new TooLongFrameException("content length exceeded " + 
/* 420 */           maxContentLength() + " bytes."));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 428 */     if (this.currentMessage != null && !ctx.channel().config().isAutoRead()) {
/* 429 */       ctx.read();
/*     */     }
/* 431 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/*     */     try {
/* 438 */       super.channelInactive(ctx);
/*     */     } finally {
/* 440 */       releaseCurrentMessage();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 446 */     this.ctx = ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/*     */     try {
/* 452 */       super.handlerRemoved(ctx);
/*     */     }
/*     */     finally {
/*     */       
/* 456 */       releaseCurrentMessage();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void releaseCurrentMessage() {
/* 461 */     if (this.currentMessage != null) {
/* 462 */       this.currentMessage.release();
/* 463 */       this.currentMessage = null;
/* 464 */       this.handlingOversizedMessage = false;
/* 465 */       this.aggregating = false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\MessageAggregator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */