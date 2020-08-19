/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpUtil;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
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
/*     */ public class SpdyHttpDecoder
/*     */   extends MessageToMessageDecoder<SpdyFrame>
/*     */ {
/*     */   private final boolean validateHeaders;
/*     */   private final int spdyVersion;
/*     */   private final int maxContentLength;
/*     */   private final Map<Integer, FullHttpMessage> messageMap;
/*     */   
/*     */   public SpdyHttpDecoder(SpdyVersion version, int maxContentLength) {
/*  64 */     this(version, maxContentLength, new HashMap<Integer, FullHttpMessage>(), true);
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
/*     */   public SpdyHttpDecoder(SpdyVersion version, int maxContentLength, boolean validateHeaders) {
/*  77 */     this(version, maxContentLength, new HashMap<Integer, FullHttpMessage>(), validateHeaders);
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
/*     */   protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap) {
/*  90 */     this(version, maxContentLength, messageMap, true);
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
/*     */   protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap, boolean validateHeaders) {
/* 105 */     this.spdyVersion = ((SpdyVersion)ObjectUtil.checkNotNull(version, "version")).getVersion();
/* 106 */     this.maxContentLength = ObjectUtil.checkPositive(maxContentLength, "maxContentLength");
/* 107 */     this.messageMap = messageMap;
/* 108 */     this.validateHeaders = validateHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 114 */     for (Map.Entry<Integer, FullHttpMessage> entry : this.messageMap.entrySet()) {
/* 115 */       ReferenceCountUtil.safeRelease(entry.getValue());
/*     */     }
/* 117 */     this.messageMap.clear();
/* 118 */     super.channelInactive(ctx);
/*     */   }
/*     */   
/*     */   protected FullHttpMessage putMessage(int streamId, FullHttpMessage message) {
/* 122 */     return this.messageMap.put(Integer.valueOf(streamId), message);
/*     */   }
/*     */   
/*     */   protected FullHttpMessage getMessage(int streamId) {
/* 126 */     return this.messageMap.get(Integer.valueOf(streamId));
/*     */   }
/*     */   
/*     */   protected FullHttpMessage removeMessage(int streamId) {
/* 130 */     return this.messageMap.remove(Integer.valueOf(streamId));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, SpdyFrame msg, List<Object> out) throws Exception {
/* 136 */     if (msg instanceof SpdySynStreamFrame) {
/*     */ 
/*     */       
/* 139 */       SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
/* 140 */       int streamId = spdySynStreamFrame.streamId();
/*     */       
/* 142 */       if (SpdyCodecUtil.isServerId(streamId)) {
/*     */         
/* 144 */         int associatedToStreamId = spdySynStreamFrame.associatedStreamId();
/*     */ 
/*     */ 
/*     */         
/* 148 */         if (associatedToStreamId == 0) {
/* 149 */           SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INVALID_STREAM);
/*     */           
/* 151 */           ctx.writeAndFlush(spdyRstStreamFrame);
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */ 
/*     */         
/* 158 */         if (spdySynStreamFrame.isLast()) {
/* 159 */           SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */           
/* 161 */           ctx.writeAndFlush(spdyRstStreamFrame);
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 167 */         if (spdySynStreamFrame.isTruncated()) {
/* 168 */           SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
/*     */           
/* 170 */           ctx.writeAndFlush(spdyRstStreamFrame);
/*     */           
/*     */           return;
/*     */         } 
/*     */         try {
/* 175 */           FullHttpRequest httpRequestWithEntity = createHttpRequest(spdySynStreamFrame, ctx.alloc());
/*     */ 
/*     */           
/* 178 */           httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, streamId);
/* 179 */           httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, associatedToStreamId);
/* 180 */           httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.PRIORITY, spdySynStreamFrame.priority());
/*     */           
/* 182 */           out.add(httpRequestWithEntity);
/*     */         }
/* 184 */         catch (Throwable ignored) {
/* 185 */           SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */           
/* 187 */           ctx.writeAndFlush(spdyRstStreamFrame);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 194 */         if (spdySynStreamFrame.isTruncated()) {
/* 195 */           SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
/* 196 */           spdySynReplyFrame.setLast(true);
/* 197 */           SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
/* 198 */           frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
/* 199 */           frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
/* 200 */           ctx.writeAndFlush(spdySynReplyFrame);
/*     */           
/*     */           return;
/*     */         } 
/*     */         try {
/* 205 */           FullHttpRequest httpRequestWithEntity = createHttpRequest(spdySynStreamFrame, ctx.alloc());
/*     */ 
/*     */           
/* 208 */           httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, streamId);
/*     */           
/* 210 */           if (spdySynStreamFrame.isLast()) {
/* 211 */             out.add(httpRequestWithEntity);
/*     */           } else {
/*     */             
/* 214 */             putMessage(streamId, (FullHttpMessage)httpRequestWithEntity);
/*     */           } 
/* 216 */         } catch (Throwable t) {
/*     */ 
/*     */ 
/*     */           
/* 220 */           SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
/* 221 */           spdySynReplyFrame.setLast(true);
/* 222 */           SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
/* 223 */           frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.BAD_REQUEST.code());
/* 224 */           frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
/* 225 */           ctx.writeAndFlush(spdySynReplyFrame);
/*     */         }
/*     */       
/*     */       } 
/* 229 */     } else if (msg instanceof SpdySynReplyFrame) {
/*     */       
/* 231 */       SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
/* 232 */       int streamId = spdySynReplyFrame.streamId();
/*     */ 
/*     */ 
/*     */       
/* 236 */       if (spdySynReplyFrame.isTruncated()) {
/* 237 */         SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
/*     */         
/* 239 */         ctx.writeAndFlush(spdyRstStreamFrame);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*     */       try {
/* 245 */         FullHttpResponse httpResponseWithEntity = createHttpResponse(spdySynReplyFrame, ctx.alloc(), this.validateHeaders);
/*     */ 
/*     */         
/* 248 */         httpResponseWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, streamId);
/*     */         
/* 250 */         if (spdySynReplyFrame.isLast()) {
/* 251 */           HttpUtil.setContentLength((HttpMessage)httpResponseWithEntity, 0L);
/* 252 */           out.add(httpResponseWithEntity);
/*     */         } else {
/*     */           
/* 255 */           putMessage(streamId, (FullHttpMessage)httpResponseWithEntity);
/*     */         } 
/* 257 */       } catch (Throwable t) {
/*     */ 
/*     */         
/* 260 */         SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */         
/* 262 */         ctx.writeAndFlush(spdyRstStreamFrame);
/*     */       }
/*     */     
/* 265 */     } else if (msg instanceof SpdyHeadersFrame) {
/*     */       FullHttpResponse fullHttpResponse;
/* 267 */       SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
/* 268 */       int streamId = spdyHeadersFrame.streamId();
/* 269 */       FullHttpMessage fullHttpMessage = getMessage(streamId);
/*     */       
/* 271 */       if (fullHttpMessage == null) {
/*     */         
/* 273 */         if (SpdyCodecUtil.isServerId(streamId)) {
/*     */ 
/*     */ 
/*     */           
/* 277 */           if (spdyHeadersFrame.isTruncated()) {
/* 278 */             SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
/*     */             
/* 280 */             ctx.writeAndFlush(spdyRstStreamFrame);
/*     */             
/*     */             return;
/*     */           } 
/*     */           try {
/* 285 */             fullHttpResponse = createHttpResponse(spdyHeadersFrame, ctx.alloc(), this.validateHeaders);
/*     */ 
/*     */             
/* 288 */             fullHttpResponse.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, streamId);
/*     */             
/* 290 */             if (spdyHeadersFrame.isLast()) {
/* 291 */               HttpUtil.setContentLength((HttpMessage)fullHttpResponse, 0L);
/* 292 */               out.add(fullHttpResponse);
/*     */             } else {
/*     */               
/* 295 */               putMessage(streamId, (FullHttpMessage)fullHttpResponse);
/*     */             } 
/* 297 */           } catch (Throwable t) {
/*     */ 
/*     */             
/* 300 */             SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */             
/* 302 */             ctx.writeAndFlush(spdyRstStreamFrame);
/*     */           } 
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 309 */       if (!spdyHeadersFrame.isTruncated()) {
/* 310 */         for (Map.Entry<CharSequence, CharSequence> e : (Iterable<Map.Entry<CharSequence, CharSequence>>)spdyHeadersFrame.headers()) {
/* 311 */           fullHttpResponse.headers().add(e.getKey(), e.getValue());
/*     */         }
/*     */       }
/*     */       
/* 315 */       if (spdyHeadersFrame.isLast()) {
/* 316 */         HttpUtil.setContentLength((HttpMessage)fullHttpResponse, fullHttpResponse.content().readableBytes());
/* 317 */         removeMessage(streamId);
/* 318 */         out.add(fullHttpResponse);
/*     */       }
/*     */     
/* 321 */     } else if (msg instanceof SpdyDataFrame) {
/*     */       
/* 323 */       SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
/* 324 */       int streamId = spdyDataFrame.streamId();
/* 325 */       FullHttpMessage fullHttpMessage = getMessage(streamId);
/*     */ 
/*     */       
/* 328 */       if (fullHttpMessage == null) {
/*     */         return;
/*     */       }
/*     */       
/* 332 */       ByteBuf content = fullHttpMessage.content();
/* 333 */       if (content.readableBytes() > this.maxContentLength - spdyDataFrame.content().readableBytes()) {
/* 334 */         removeMessage(streamId);
/* 335 */         throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
/*     */       } 
/*     */ 
/*     */       
/* 339 */       ByteBuf spdyDataFrameData = spdyDataFrame.content();
/* 340 */       int spdyDataFrameDataLen = spdyDataFrameData.readableBytes();
/* 341 */       content.writeBytes(spdyDataFrameData, spdyDataFrameData.readerIndex(), spdyDataFrameDataLen);
/*     */       
/* 343 */       if (spdyDataFrame.isLast()) {
/* 344 */         HttpUtil.setContentLength((HttpMessage)fullHttpMessage, content.readableBytes());
/* 345 */         removeMessage(streamId);
/* 346 */         out.add(fullHttpMessage);
/*     */       }
/*     */     
/* 349 */     } else if (msg instanceof SpdyRstStreamFrame) {
/*     */       
/* 351 */       SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
/* 352 */       int streamId = spdyRstStreamFrame.streamId();
/* 353 */       removeMessage(streamId);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static FullHttpRequest createHttpRequest(SpdyHeadersFrame requestFrame, ByteBufAllocator alloc) throws Exception {
/* 360 */     SpdyHeaders headers = requestFrame.headers();
/* 361 */     HttpMethod method = HttpMethod.valueOf(headers.getAsString((CharSequence)SpdyHeaders.HttpNames.METHOD));
/* 362 */     String url = headers.getAsString((CharSequence)SpdyHeaders.HttpNames.PATH);
/* 363 */     HttpVersion httpVersion = HttpVersion.valueOf(headers.getAsString((CharSequence)SpdyHeaders.HttpNames.VERSION));
/* 364 */     headers.remove(SpdyHeaders.HttpNames.METHOD);
/* 365 */     headers.remove(SpdyHeaders.HttpNames.PATH);
/* 366 */     headers.remove(SpdyHeaders.HttpNames.VERSION);
/*     */     
/* 368 */     boolean release = true;
/* 369 */     ByteBuf buffer = alloc.buffer();
/*     */     try {
/* 371 */       DefaultFullHttpRequest defaultFullHttpRequest = new DefaultFullHttpRequest(httpVersion, method, url, buffer);
/*     */ 
/*     */       
/* 374 */       headers.remove(SpdyHeaders.HttpNames.SCHEME);
/*     */ 
/*     */       
/* 377 */       CharSequence host = (CharSequence)headers.get(SpdyHeaders.HttpNames.HOST);
/* 378 */       headers.remove(SpdyHeaders.HttpNames.HOST);
/* 379 */       defaultFullHttpRequest.headers().set((CharSequence)HttpHeaderNames.HOST, host);
/*     */       
/* 381 */       for (Map.Entry<CharSequence, CharSequence> e : (Iterable<Map.Entry<CharSequence, CharSequence>>)requestFrame.headers()) {
/* 382 */         defaultFullHttpRequest.headers().add(e.getKey(), e.getValue());
/*     */       }
/*     */ 
/*     */       
/* 386 */       HttpUtil.setKeepAlive((HttpMessage)defaultFullHttpRequest, true);
/*     */ 
/*     */       
/* 389 */       defaultFullHttpRequest.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/* 390 */       release = false;
/* 391 */       return (FullHttpRequest)defaultFullHttpRequest;
/*     */     } finally {
/* 393 */       if (release) {
/* 394 */         buffer.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static FullHttpResponse createHttpResponse(SpdyHeadersFrame responseFrame, ByteBufAllocator alloc, boolean validateHeaders) throws Exception {
/* 403 */     SpdyHeaders headers = responseFrame.headers();
/* 404 */     HttpResponseStatus status = HttpResponseStatus.parseLine((CharSequence)headers.get(SpdyHeaders.HttpNames.STATUS));
/* 405 */     HttpVersion version = HttpVersion.valueOf(headers.getAsString((CharSequence)SpdyHeaders.HttpNames.VERSION));
/* 406 */     headers.remove(SpdyHeaders.HttpNames.STATUS);
/* 407 */     headers.remove(SpdyHeaders.HttpNames.VERSION);
/*     */     
/* 409 */     boolean release = true;
/* 410 */     ByteBuf buffer = alloc.buffer();
/*     */     try {
/* 412 */       DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(version, status, buffer, validateHeaders);
/* 413 */       for (Map.Entry<CharSequence, CharSequence> e : (Iterable<Map.Entry<CharSequence, CharSequence>>)responseFrame.headers()) {
/* 414 */         defaultFullHttpResponse.headers().add(e.getKey(), e.getValue());
/*     */       }
/*     */ 
/*     */       
/* 418 */       HttpUtil.setKeepAlive((HttpMessage)defaultFullHttpResponse, true);
/*     */ 
/*     */       
/* 421 */       defaultFullHttpResponse.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/* 422 */       defaultFullHttpResponse.headers().remove((CharSequence)HttpHeaderNames.TRAILER);
/*     */       
/* 424 */       release = false;
/* 425 */       return (FullHttpResponse)defaultFullHttpResponse;
/*     */     } finally {
/* 427 */       if (release)
/* 428 */         buffer.release(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHttpDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */