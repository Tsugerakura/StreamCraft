/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageAggregator;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpObjectAggregator
/*     */   extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage>
/*     */ {
/*  89 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
/*  90 */   private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
/*     */   
/*  92 */   private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
/*     */   
/*  94 */   private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
/*     */   
/*  96 */   private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
/*     */   private final boolean closeOnExpectationFailed;
/*     */   
/*     */   static {
/* 100 */     EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Integer.valueOf(0));
/* 101 */     TOO_LARGE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Integer.valueOf(0));
/*     */     
/* 103 */     TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Integer.valueOf(0));
/* 104 */     TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
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
/*     */   public HttpObjectAggregator(int maxContentLength) {
/* 116 */     this(maxContentLength, false);
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
/*     */   public HttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
/* 129 */     super(maxContentLength);
/* 130 */     this.closeOnExpectationFailed = closeOnExpectationFailed;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isStartMessage(HttpObject msg) throws Exception {
/* 135 */     return msg instanceof HttpMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isContentMessage(HttpObject msg) throws Exception {
/* 140 */     return msg instanceof HttpContent;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isLastContentMessage(HttpContent msg) throws Exception {
/* 145 */     return msg instanceof LastHttpContent;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isAggregated(HttpObject msg) throws Exception {
/* 150 */     return msg instanceof FullHttpMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isContentLengthInvalid(HttpMessage start, int maxContentLength) {
/*     */     try {
/* 156 */       return (HttpUtil.getContentLength(start, -1L) > maxContentLength);
/* 157 */     } catch (NumberFormatException e) {
/* 158 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Object continueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
/* 163 */     if (HttpUtil.isUnsupportedExpectation(start)) {
/*     */       
/* 165 */       pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
/* 166 */       return EXPECTATION_FAILED.retainedDuplicate();
/* 167 */     }  if (HttpUtil.is100ContinueExpected(start)) {
/*     */       
/* 169 */       if (HttpUtil.getContentLength(start, -1L) <= maxContentLength) {
/* 170 */         return CONTINUE.retainedDuplicate();
/*     */       }
/* 172 */       pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
/* 173 */       return TOO_LARGE.retainedDuplicate();
/*     */     } 
/*     */     
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object newContinueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
/* 181 */     Object response = continueResponse(start, maxContentLength, pipeline);
/*     */ 
/*     */     
/* 184 */     if (response != null) {
/* 185 */       start.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
/*     */     }
/* 187 */     return response;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean closeAfterContinueResponse(Object msg) {
/* 192 */     return (this.closeOnExpectationFailed && ignoreContentAfterContinueResponse(msg));
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean ignoreContentAfterContinueResponse(Object msg) {
/* 197 */     if (msg instanceof HttpResponse) {
/* 198 */       HttpResponse httpResponse = (HttpResponse)msg;
/* 199 */       return httpResponse.status().codeClass().equals(HttpStatusClass.CLIENT_ERROR);
/*     */     } 
/* 201 */     return false;
/*     */   }
/*     */   
/*     */   protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
/*     */     AggregatedFullHttpMessage ret;
/* 206 */     assert !(start instanceof FullHttpMessage);
/*     */     
/* 208 */     HttpUtil.setTransferEncodingChunked(start, false);
/*     */ 
/*     */     
/* 211 */     if (start instanceof HttpRequest) {
/* 212 */       ret = new AggregatedFullHttpRequest((HttpRequest)start, content, null);
/* 213 */     } else if (start instanceof HttpResponse) {
/* 214 */       ret = new AggregatedFullHttpResponse((HttpResponse)start, content, null);
/*     */     } else {
/* 216 */       throw new Error();
/*     */     } 
/* 218 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void aggregate(FullHttpMessage aggregated, HttpContent content) throws Exception {
/* 223 */     if (content instanceof LastHttpContent)
/*     */     {
/* 225 */       ((AggregatedFullHttpMessage)aggregated).setTrailingHeaders(((LastHttpContent)content).trailingHeaders());
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
/*     */   protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
/* 237 */     if (!HttpUtil.isContentLengthSet(aggregated)) {
/* 238 */       aggregated.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, 
/*     */           
/* 240 */           String.valueOf(aggregated.content().readableBytes()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleOversizedMessage(final ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
/* 246 */     if (oversized instanceof HttpRequest) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 251 */       if (oversized instanceof FullHttpMessage || (
/* 252 */         !HttpUtil.is100ContinueExpected(oversized) && !HttpUtil.isKeepAlive(oversized))) {
/* 253 */         ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
/* 254 */         future.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 257 */                 if (!future.isSuccess()) {
/* 258 */                   HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
/*     */                 }
/* 260 */                 ctx.close();
/*     */               }
/*     */             });
/*     */       } else {
/* 264 */         ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 267 */                 if (!future.isSuccess()) {
/* 268 */                   HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
/* 269 */                   ctx.close();
/*     */                 }  }
/*     */             });
/*     */       } 
/*     */     } else {
/* 274 */       if (oversized instanceof HttpResponse) {
/* 275 */         ctx.close();
/* 276 */         throw new TooLongFrameException("Response entity too large: " + oversized);
/*     */       } 
/* 278 */       throw new IllegalStateException();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static abstract class AggregatedFullHttpMessage implements FullHttpMessage {
/*     */     protected final HttpMessage message;
/*     */     private final ByteBuf content;
/*     */     private HttpHeaders trailingHeaders;
/*     */     
/*     */     AggregatedFullHttpMessage(HttpMessage message, ByteBuf content, HttpHeaders trailingHeaders) {
/* 288 */       this.message = message;
/* 289 */       this.content = content;
/* 290 */       this.trailingHeaders = trailingHeaders;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders trailingHeaders() {
/* 295 */       HttpHeaders trailingHeaders = this.trailingHeaders;
/* 296 */       if (trailingHeaders == null) {
/* 297 */         return EmptyHttpHeaders.INSTANCE;
/*     */       }
/* 299 */       return trailingHeaders;
/*     */     }
/*     */ 
/*     */     
/*     */     void setTrailingHeaders(HttpHeaders trailingHeaders) {
/* 304 */       this.trailingHeaders = trailingHeaders;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpVersion getProtocolVersion() {
/* 309 */       return this.message.protocolVersion();
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpVersion protocolVersion() {
/* 314 */       return this.message.protocolVersion();
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpMessage setProtocolVersion(HttpVersion version) {
/* 319 */       this.message.setProtocolVersion(version);
/* 320 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders headers() {
/* 325 */       return this.message.headers();
/*     */     }
/*     */ 
/*     */     
/*     */     public DecoderResult decoderResult() {
/* 330 */       return this.message.decoderResult();
/*     */     }
/*     */ 
/*     */     
/*     */     public DecoderResult getDecoderResult() {
/* 335 */       return this.message.decoderResult();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setDecoderResult(DecoderResult result) {
/* 340 */       this.message.setDecoderResult(result);
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf content() {
/* 345 */       return this.content;
/*     */     }
/*     */ 
/*     */     
/*     */     public int refCnt() {
/* 350 */       return this.content.refCnt();
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpMessage retain() {
/* 355 */       this.content.retain();
/* 356 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpMessage retain(int increment) {
/* 361 */       this.content.retain(increment);
/* 362 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpMessage touch(Object hint) {
/* 367 */       this.content.touch(hint);
/* 368 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpMessage touch() {
/* 373 */       this.content.touch();
/* 374 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean release() {
/* 379 */       return this.content.release();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean release(int decrement) {
/* 384 */       return this.content.release(decrement);
/*     */     }
/*     */ 
/*     */     
/*     */     public abstract FullHttpMessage copy();
/*     */     
/*     */     public abstract FullHttpMessage duplicate();
/*     */     
/*     */     public abstract FullHttpMessage retainedDuplicate();
/*     */   }
/*     */   
/*     */   private static final class AggregatedFullHttpRequest
/*     */     extends AggregatedFullHttpMessage
/*     */     implements FullHttpRequest
/*     */   {
/*     */     AggregatedFullHttpRequest(HttpRequest request, ByteBuf content, HttpHeaders trailingHeaders) {
/* 400 */       super(request, content, trailingHeaders);
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest copy() {
/* 405 */       return replace(content().copy());
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest duplicate() {
/* 410 */       return replace(content().duplicate());
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest retainedDuplicate() {
/* 415 */       return replace(content().retainedDuplicate());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public FullHttpRequest replace(ByteBuf content) {
/* 421 */       DefaultFullHttpRequest dup = new DefaultFullHttpRequest(protocolVersion(), method(), uri(), content, headers().copy(), trailingHeaders().copy());
/* 422 */       dup.setDecoderResult(decoderResult());
/* 423 */       return dup;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest retain(int increment) {
/* 428 */       super.retain(increment);
/* 429 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest retain() {
/* 434 */       super.retain();
/* 435 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest touch() {
/* 440 */       super.touch();
/* 441 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest touch(Object hint) {
/* 446 */       super.touch(hint);
/* 447 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest setMethod(HttpMethod method) {
/* 452 */       ((HttpRequest)this.message).setMethod(method);
/* 453 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest setUri(String uri) {
/* 458 */       ((HttpRequest)this.message).setUri(uri);
/* 459 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpMethod getMethod() {
/* 464 */       return ((HttpRequest)this.message).method();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getUri() {
/* 469 */       return ((HttpRequest)this.message).uri();
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpMethod method() {
/* 474 */       return getMethod();
/*     */     }
/*     */ 
/*     */     
/*     */     public String uri() {
/* 479 */       return getUri();
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpRequest setProtocolVersion(HttpVersion version) {
/* 484 */       super.setProtocolVersion(version);
/* 485 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 490 */       return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AggregatedFullHttpResponse
/*     */     extends AggregatedFullHttpMessage
/*     */     implements FullHttpResponse {
/*     */     AggregatedFullHttpResponse(HttpResponse message, ByteBuf content, HttpHeaders trailingHeaders) {
/* 498 */       super(message, content, trailingHeaders);
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse copy() {
/* 503 */       return replace(content().copy());
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse duplicate() {
/* 508 */       return replace(content().duplicate());
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse retainedDuplicate() {
/* 513 */       return replace(content().retainedDuplicate());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public FullHttpResponse replace(ByteBuf content) {
/* 519 */       DefaultFullHttpResponse dup = new DefaultFullHttpResponse(getProtocolVersion(), getStatus(), content, headers().copy(), trailingHeaders().copy());
/* 520 */       dup.setDecoderResult(decoderResult());
/* 521 */       return dup;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse setStatus(HttpResponseStatus status) {
/* 526 */       ((HttpResponse)this.message).setStatus(status);
/* 527 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpResponseStatus getStatus() {
/* 532 */       return ((HttpResponse)this.message).status();
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpResponseStatus status() {
/* 537 */       return getStatus();
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse setProtocolVersion(HttpVersion version) {
/* 542 */       super.setProtocolVersion(version);
/* 543 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse retain(int increment) {
/* 548 */       super.retain(increment);
/* 549 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse retain() {
/* 554 */       super.retain();
/* 555 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse touch(Object hint) {
/* 560 */       super.touch(hint);
/* 561 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public FullHttpResponse touch() {
/* 566 */       super.touch();
/* 567 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 572 */       return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpObjectAggregator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */