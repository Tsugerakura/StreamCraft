/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ThrowableUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpdySessionHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  37 */   private static final SpdyProtocolException PROTOCOL_EXCEPTION = (SpdyProtocolException)ThrowableUtil.unknownStackTrace(
/*  38 */       SpdyProtocolException.newStatic(null), SpdySessionHandler.class, "handleOutboundMessage(...)");
/*  39 */   private static final SpdyProtocolException STREAM_CLOSED = (SpdyProtocolException)ThrowableUtil.unknownStackTrace(
/*  40 */       SpdyProtocolException.newStatic("Stream closed"), SpdySessionHandler.class, "removeStream(...)");
/*     */   
/*     */   private static final int DEFAULT_WINDOW_SIZE = 65536;
/*  43 */   private int initialSendWindowSize = 65536;
/*  44 */   private int initialReceiveWindowSize = 65536;
/*  45 */   private volatile int initialSessionReceiveWindowSize = 65536;
/*     */   
/*  47 */   private final SpdySession spdySession = new SpdySession(this.initialSendWindowSize, this.initialReceiveWindowSize);
/*     */   
/*     */   private int lastGoodStreamId;
/*     */   private static final int DEFAULT_MAX_CONCURRENT_STREAMS = 2147483647;
/*  51 */   private int remoteConcurrentStreams = Integer.MAX_VALUE;
/*  52 */   private int localConcurrentStreams = Integer.MAX_VALUE;
/*     */   
/*  54 */   private final AtomicInteger pings = new AtomicInteger();
/*     */ 
/*     */   
/*     */   private boolean sentGoAwayFrame;
/*     */ 
/*     */   
/*     */   private boolean receivedGoAwayFrame;
/*     */ 
/*     */   
/*     */   private ChannelFutureListener closeSessionFutureListener;
/*     */ 
/*     */   
/*     */   private final boolean server;
/*     */ 
/*     */   
/*     */   private final int minorVersion;
/*     */ 
/*     */ 
/*     */   
/*     */   public SpdySessionHandler(SpdyVersion version, boolean server) {
/*  74 */     this.minorVersion = ((SpdyVersion)ObjectUtil.checkNotNull(version, "version")).getMinorVersion();
/*  75 */     this.server = server;
/*     */   }
/*     */   
/*     */   public void setSessionReceiveWindowSize(int sessionReceiveWindowSize) {
/*  79 */     ObjectUtil.checkPositiveOrZero(sessionReceiveWindowSize, "sessionReceiveWindowSize");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  86 */     this.initialSessionReceiveWindowSize = sessionReceiveWindowSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  91 */     if (msg instanceof SpdyDataFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 115 */       SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
/* 116 */       int streamId = spdyDataFrame.streamId();
/*     */       
/* 118 */       int deltaWindowSize = -1 * spdyDataFrame.content().readableBytes();
/*     */       
/* 120 */       int newSessionWindowSize = this.spdySession.updateReceiveWindowSize(0, deltaWindowSize);
/*     */ 
/*     */       
/* 123 */       if (newSessionWindowSize < 0) {
/* 124 */         issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 129 */       if (newSessionWindowSize <= this.initialSessionReceiveWindowSize / 2) {
/* 130 */         int sessionDeltaWindowSize = this.initialSessionReceiveWindowSize - newSessionWindowSize;
/* 131 */         this.spdySession.updateReceiveWindowSize(0, sessionDeltaWindowSize);
/* 132 */         SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(0, sessionDeltaWindowSize);
/*     */         
/* 134 */         ctx.writeAndFlush(spdyWindowUpdateFrame);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 139 */       if (!this.spdySession.isActiveStream(streamId)) {
/* 140 */         spdyDataFrame.release();
/* 141 */         if (streamId <= this.lastGoodStreamId) {
/* 142 */           issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/* 143 */         } else if (!this.sentGoAwayFrame) {
/* 144 */           issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
/*     */         } 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 151 */       if (this.spdySession.isRemoteSideClosed(streamId)) {
/* 152 */         spdyDataFrame.release();
/* 153 */         issueStreamError(ctx, streamId, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 158 */       if (!isRemoteInitiatedId(streamId) && !this.spdySession.hasReceivedReply(streamId)) {
/* 159 */         spdyDataFrame.release();
/* 160 */         issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 171 */       int newWindowSize = this.spdySession.updateReceiveWindowSize(streamId, deltaWindowSize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 178 */       if (newWindowSize < this.spdySession.getReceiveWindowSizeLowerBound(streamId)) {
/* 179 */         spdyDataFrame.release();
/* 180 */         issueStreamError(ctx, streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 186 */       if (newWindowSize < 0) {
/* 187 */         while (spdyDataFrame.content().readableBytes() > this.initialReceiveWindowSize) {
/*     */           
/* 189 */           SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId, spdyDataFrame.content().readRetainedSlice(this.initialReceiveWindowSize));
/* 190 */           ctx.writeAndFlush(partialDataFrame);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 195 */       if (newWindowSize <= this.initialReceiveWindowSize / 2 && !spdyDataFrame.isLast()) {
/* 196 */         int streamDeltaWindowSize = this.initialReceiveWindowSize - newWindowSize;
/* 197 */         this.spdySession.updateReceiveWindowSize(streamId, streamDeltaWindowSize);
/* 198 */         SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(streamId, streamDeltaWindowSize);
/*     */         
/* 200 */         ctx.writeAndFlush(spdyWindowUpdateFrame);
/*     */       } 
/*     */ 
/*     */       
/* 204 */       if (spdyDataFrame.isLast()) {
/* 205 */         halfCloseStream(streamId, true, ctx.newSucceededFuture());
/*     */       }
/*     */     }
/* 208 */     else if (msg instanceof SpdySynStreamFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 224 */       SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
/* 225 */       int streamId = spdySynStreamFrame.streamId();
/*     */ 
/*     */       
/* 228 */       if (spdySynStreamFrame.isInvalid() || 
/* 229 */         !isRemoteInitiatedId(streamId) || this.spdySession
/* 230 */         .isActiveStream(streamId)) {
/* 231 */         issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 236 */       if (streamId <= this.lastGoodStreamId) {
/* 237 */         issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 242 */       byte priority = spdySynStreamFrame.priority();
/* 243 */       boolean remoteSideClosed = spdySynStreamFrame.isLast();
/* 244 */       boolean localSideClosed = spdySynStreamFrame.isUnidirectional();
/* 245 */       if (!acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
/* 246 */         issueStreamError(ctx, streamId, SpdyStreamStatus.REFUSED_STREAM);
/*     */         
/*     */         return;
/*     */       } 
/* 250 */     } else if (msg instanceof SpdySynReplyFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 259 */       SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
/* 260 */       int streamId = spdySynReplyFrame.streamId();
/*     */ 
/*     */       
/* 263 */       if (spdySynReplyFrame.isInvalid() || 
/* 264 */         isRemoteInitiatedId(streamId) || this.spdySession
/* 265 */         .isRemoteSideClosed(streamId)) {
/* 266 */         issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 271 */       if (this.spdySession.hasReceivedReply(streamId)) {
/* 272 */         issueStreamError(ctx, streamId, SpdyStreamStatus.STREAM_IN_USE);
/*     */         
/*     */         return;
/*     */       } 
/* 276 */       this.spdySession.receivedReply(streamId);
/*     */ 
/*     */       
/* 279 */       if (spdySynReplyFrame.isLast()) {
/* 280 */         halfCloseStream(streamId, true, ctx.newSucceededFuture());
/*     */       }
/*     */     }
/* 283 */     else if (msg instanceof SpdyRstStreamFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 294 */       SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
/* 295 */       removeStream(spdyRstStreamFrame.streamId(), ctx.newSucceededFuture());
/*     */     }
/* 297 */     else if (msg instanceof SpdySettingsFrame) {
/*     */       
/* 299 */       SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
/*     */       
/* 301 */       int settingsMinorVersion = spdySettingsFrame.getValue(0);
/* 302 */       if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
/*     */         
/* 304 */         issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 309 */       int newConcurrentStreams = spdySettingsFrame.getValue(4);
/* 310 */       if (newConcurrentStreams >= 0) {
/* 311 */         this.remoteConcurrentStreams = newConcurrentStreams;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 317 */       if (spdySettingsFrame.isPersisted(7)) {
/* 318 */         spdySettingsFrame.removeValue(7);
/*     */       }
/* 320 */       spdySettingsFrame.setPersistValue(7, false);
/*     */ 
/*     */       
/* 323 */       int newInitialWindowSize = spdySettingsFrame.getValue(7);
/* 324 */       if (newInitialWindowSize >= 0) {
/* 325 */         updateInitialSendWindowSize(newInitialWindowSize);
/*     */       }
/*     */     }
/* 328 */     else if (msg instanceof SpdyPingFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 339 */       SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
/*     */       
/* 341 */       if (isRemoteInitiatedId(spdyPingFrame.id())) {
/* 342 */         ctx.writeAndFlush(spdyPingFrame);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 347 */       if (this.pings.get() == 0) {
/*     */         return;
/*     */       }
/* 350 */       this.pings.getAndDecrement();
/*     */     }
/* 352 */     else if (msg instanceof SpdyGoAwayFrame) {
/*     */       
/* 354 */       this.receivedGoAwayFrame = true;
/*     */     }
/* 356 */     else if (msg instanceof SpdyHeadersFrame) {
/*     */       
/* 358 */       SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
/* 359 */       int streamId = spdyHeadersFrame.streamId();
/*     */ 
/*     */       
/* 362 */       if (spdyHeadersFrame.isInvalid()) {
/* 363 */         issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
/*     */         
/*     */         return;
/*     */       } 
/* 367 */       if (this.spdySession.isRemoteSideClosed(streamId)) {
/* 368 */         issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 373 */       if (spdyHeadersFrame.isLast()) {
/* 374 */         halfCloseStream(streamId, true, ctx.newSucceededFuture());
/*     */       }
/*     */     }
/* 377 */     else if (msg instanceof SpdyWindowUpdateFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 389 */       SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
/* 390 */       int streamId = spdyWindowUpdateFrame.streamId();
/* 391 */       int deltaWindowSize = spdyWindowUpdateFrame.deltaWindowSize();
/*     */ 
/*     */       
/* 394 */       if (streamId != 0 && this.spdySession.isLocalSideClosed(streamId)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 399 */       if (this.spdySession.getSendWindowSize(streamId) > Integer.MAX_VALUE - deltaWindowSize) {
/* 400 */         if (streamId == 0) {
/* 401 */           issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
/*     */         } else {
/* 403 */           issueStreamError(ctx, streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 408 */       updateSendWindowSize(ctx, streamId, deltaWindowSize);
/*     */     } 
/*     */     
/* 411 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 416 */     for (Integer streamId : this.spdySession.activeStreams().keySet()) {
/* 417 */       removeStream(streamId.intValue(), ctx.newSucceededFuture());
/*     */     }
/* 419 */     ctx.fireChannelInactive();
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 424 */     if (cause instanceof SpdyProtocolException) {
/* 425 */       issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
/*     */     }
/*     */     
/* 428 */     ctx.fireExceptionCaught(cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 433 */     sendGoAwayFrame(ctx, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 438 */     if (msg instanceof SpdyDataFrame || msg instanceof SpdySynStreamFrame || msg instanceof SpdySynReplyFrame || msg instanceof SpdyRstStreamFrame || msg instanceof SpdySettingsFrame || msg instanceof SpdyPingFrame || msg instanceof SpdyGoAwayFrame || msg instanceof SpdyHeadersFrame || msg instanceof SpdyWindowUpdateFrame) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 448 */       handleOutboundMessage(ctx, msg, promise);
/*     */     } else {
/* 450 */       ctx.write(msg, promise);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleOutboundMessage(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 455 */     if (msg instanceof SpdyDataFrame) {
/*     */       
/* 457 */       SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
/* 458 */       int streamId = spdyDataFrame.streamId();
/*     */ 
/*     */       
/* 461 */       if (this.spdySession.isLocalSideClosed(streamId)) {
/* 462 */         spdyDataFrame.release();
/* 463 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 480 */       int dataLength = spdyDataFrame.content().readableBytes();
/* 481 */       int sendWindowSize = this.spdySession.getSendWindowSize(streamId);
/* 482 */       int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
/* 483 */       sendWindowSize = Math.min(sendWindowSize, sessionSendWindowSize);
/*     */       
/* 485 */       if (sendWindowSize <= 0) {
/*     */         
/* 487 */         this.spdySession.putPendingWrite(streamId, new SpdySession.PendingWrite(spdyDataFrame, promise)); return;
/*     */       } 
/* 489 */       if (sendWindowSize < dataLength) {
/*     */         
/* 491 */         this.spdySession.updateSendWindowSize(streamId, -1 * sendWindowSize);
/* 492 */         this.spdySession.updateSendWindowSize(0, -1 * sendWindowSize);
/*     */ 
/*     */ 
/*     */         
/* 496 */         SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId, spdyDataFrame.content().readRetainedSlice(sendWindowSize));
/*     */ 
/*     */         
/* 499 */         this.spdySession.putPendingWrite(streamId, new SpdySession.PendingWrite(spdyDataFrame, promise));
/*     */ 
/*     */ 
/*     */         
/* 503 */         final ChannelHandlerContext context = ctx;
/* 504 */         ctx.write(partialDataFrame).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 507 */                 if (!future.isSuccess()) {
/* 508 */                   SpdySessionHandler.this.issueSessionError(context, SpdySessionStatus.INTERNAL_ERROR);
/*     */                 }
/*     */               }
/*     */             });
/*     */         
/*     */         return;
/*     */       } 
/* 515 */       this.spdySession.updateSendWindowSize(streamId, -1 * dataLength);
/* 516 */       this.spdySession.updateSendWindowSize(0, -1 * dataLength);
/*     */ 
/*     */ 
/*     */       
/* 520 */       final ChannelHandlerContext context = ctx;
/* 521 */       promise.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture future) throws Exception {
/* 524 */               if (!future.isSuccess()) {
/* 525 */                 SpdySessionHandler.this.issueSessionError(context, SpdySessionStatus.INTERNAL_ERROR);
/*     */               }
/*     */             }
/*     */           });
/*     */ 
/*     */ 
/*     */       
/* 532 */       if (spdyDataFrame.isLast()) {
/* 533 */         halfCloseStream(streamId, false, (ChannelFuture)promise);
/*     */       }
/*     */     }
/* 536 */     else if (msg instanceof SpdySynStreamFrame) {
/*     */       
/* 538 */       SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
/* 539 */       int streamId = spdySynStreamFrame.streamId();
/*     */       
/* 541 */       if (isRemoteInitiatedId(streamId)) {
/* 542 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         
/*     */         return;
/*     */       } 
/* 546 */       byte priority = spdySynStreamFrame.priority();
/* 547 */       boolean remoteSideClosed = spdySynStreamFrame.isUnidirectional();
/* 548 */       boolean localSideClosed = spdySynStreamFrame.isLast();
/* 549 */       if (!acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
/* 550 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         
/*     */         return;
/*     */       } 
/* 554 */     } else if (msg instanceof SpdySynReplyFrame) {
/*     */       
/* 556 */       SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
/* 557 */       int streamId = spdySynReplyFrame.streamId();
/*     */ 
/*     */       
/* 560 */       if (!isRemoteInitiatedId(streamId) || this.spdySession.isLocalSideClosed(streamId)) {
/* 561 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 566 */       if (spdySynReplyFrame.isLast()) {
/* 567 */         halfCloseStream(streamId, false, (ChannelFuture)promise);
/*     */       }
/*     */     }
/* 570 */     else if (msg instanceof SpdyRstStreamFrame) {
/*     */       
/* 572 */       SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
/* 573 */       removeStream(spdyRstStreamFrame.streamId(), (ChannelFuture)promise);
/*     */     }
/* 575 */     else if (msg instanceof SpdySettingsFrame) {
/*     */       
/* 577 */       SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
/*     */       
/* 579 */       int settingsMinorVersion = spdySettingsFrame.getValue(0);
/* 580 */       if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
/*     */         
/* 582 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 587 */       int newConcurrentStreams = spdySettingsFrame.getValue(4);
/* 588 */       if (newConcurrentStreams >= 0) {
/* 589 */         this.localConcurrentStreams = newConcurrentStreams;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 595 */       if (spdySettingsFrame.isPersisted(7)) {
/* 596 */         spdySettingsFrame.removeValue(7);
/*     */       }
/* 598 */       spdySettingsFrame.setPersistValue(7, false);
/*     */ 
/*     */       
/* 601 */       int newInitialWindowSize = spdySettingsFrame.getValue(7);
/* 602 */       if (newInitialWindowSize >= 0) {
/* 603 */         updateInitialReceiveWindowSize(newInitialWindowSize);
/*     */       }
/*     */     }
/* 606 */     else if (msg instanceof SpdyPingFrame) {
/*     */       
/* 608 */       SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
/* 609 */       if (isRemoteInitiatedId(spdyPingFrame.id())) {
/* 610 */         ctx.fireExceptionCaught(new IllegalArgumentException("invalid PING ID: " + spdyPingFrame
/* 611 */               .id()));
/*     */         return;
/*     */       } 
/* 614 */       this.pings.getAndIncrement();
/*     */     } else {
/* 616 */       if (msg instanceof SpdyGoAwayFrame) {
/*     */ 
/*     */ 
/*     */         
/* 620 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         return;
/*     */       } 
/* 623 */       if (msg instanceof SpdyHeadersFrame) {
/*     */         
/* 625 */         SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
/* 626 */         int streamId = spdyHeadersFrame.streamId();
/*     */ 
/*     */         
/* 629 */         if (this.spdySession.isLocalSideClosed(streamId)) {
/* 630 */           promise.setFailure(PROTOCOL_EXCEPTION);
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 635 */         if (spdyHeadersFrame.isLast()) {
/* 636 */           halfCloseStream(streamId, false, (ChannelFuture)promise);
/*     */         }
/*     */       }
/* 639 */       else if (msg instanceof SpdyWindowUpdateFrame) {
/*     */ 
/*     */         
/* 642 */         promise.setFailure(PROTOCOL_EXCEPTION);
/*     */         return;
/*     */       } 
/*     */     } 
/* 646 */     ctx.write(msg, promise);
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
/*     */   private void issueSessionError(ChannelHandlerContext ctx, SpdySessionStatus status) {
/* 661 */     sendGoAwayFrame(ctx, status).addListener((GenericFutureListener)new ClosingChannelFutureListener(ctx, ctx.newPromise()));
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
/*     */   private void issueStreamError(ChannelHandlerContext ctx, int streamId, SpdyStreamStatus status) {
/* 676 */     boolean fireChannelRead = !this.spdySession.isRemoteSideClosed(streamId);
/* 677 */     ChannelPromise promise = ctx.newPromise();
/* 678 */     removeStream(streamId, (ChannelFuture)promise);
/*     */     
/* 680 */     SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, status);
/* 681 */     ctx.writeAndFlush(spdyRstStreamFrame, promise);
/* 682 */     if (fireChannelRead) {
/* 683 */       ctx.fireChannelRead(spdyRstStreamFrame);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isRemoteInitiatedId(int id) {
/* 692 */     boolean serverId = SpdyCodecUtil.isServerId(id);
/* 693 */     return ((this.server && !serverId) || (!this.server && serverId));
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateInitialSendWindowSize(int newInitialWindowSize) {
/* 698 */     int deltaWindowSize = newInitialWindowSize - this.initialSendWindowSize;
/* 699 */     this.initialSendWindowSize = newInitialWindowSize;
/* 700 */     this.spdySession.updateAllSendWindowSizes(deltaWindowSize);
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateInitialReceiveWindowSize(int newInitialWindowSize) {
/* 705 */     int deltaWindowSize = newInitialWindowSize - this.initialReceiveWindowSize;
/* 706 */     this.initialReceiveWindowSize = newInitialWindowSize;
/* 707 */     this.spdySession.updateAllReceiveWindowSizes(deltaWindowSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed) {
/* 714 */     if (this.receivedGoAwayFrame || this.sentGoAwayFrame) {
/* 715 */       return false;
/*     */     }
/*     */     
/* 718 */     boolean remote = isRemoteInitiatedId(streamId);
/* 719 */     int maxConcurrentStreams = remote ? this.localConcurrentStreams : this.remoteConcurrentStreams;
/* 720 */     if (this.spdySession.numActiveStreams(remote) >= maxConcurrentStreams) {
/* 721 */       return false;
/*     */     }
/* 723 */     this.spdySession.acceptStream(streamId, priority, remoteSideClosed, localSideClosed, this.initialSendWindowSize, this.initialReceiveWindowSize, remote);
/*     */ 
/*     */     
/* 726 */     if (remote) {
/* 727 */       this.lastGoodStreamId = streamId;
/*     */     }
/* 729 */     return true;
/*     */   }
/*     */   
/*     */   private void halfCloseStream(int streamId, boolean remote, ChannelFuture future) {
/* 733 */     if (remote) {
/* 734 */       this.spdySession.closeRemoteSide(streamId, isRemoteInitiatedId(streamId));
/*     */     } else {
/* 736 */       this.spdySession.closeLocalSide(streamId, isRemoteInitiatedId(streamId));
/*     */     } 
/* 738 */     if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
/* 739 */       future.addListener((GenericFutureListener)this.closeSessionFutureListener);
/*     */     }
/*     */   }
/*     */   
/*     */   private void removeStream(int streamId, ChannelFuture future) {
/* 744 */     this.spdySession.removeStream(streamId, STREAM_CLOSED, isRemoteInitiatedId(streamId));
/*     */     
/* 746 */     if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
/* 747 */       future.addListener((GenericFutureListener)this.closeSessionFutureListener);
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateSendWindowSize(final ChannelHandlerContext ctx, int streamId, int deltaWindowSize) {
/* 752 */     this.spdySession.updateSendWindowSize(streamId, deltaWindowSize);
/*     */ 
/*     */     
/*     */     while (true) {
/* 756 */       SpdySession.PendingWrite pendingWrite = this.spdySession.getPendingWrite(streamId);
/* 757 */       if (pendingWrite == null) {
/*     */         return;
/*     */       }
/*     */       
/* 761 */       SpdyDataFrame spdyDataFrame = pendingWrite.spdyDataFrame;
/* 762 */       int dataFrameSize = spdyDataFrame.content().readableBytes();
/* 763 */       int writeStreamId = spdyDataFrame.streamId();
/* 764 */       int sendWindowSize = this.spdySession.getSendWindowSize(writeStreamId);
/* 765 */       int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
/* 766 */       sendWindowSize = Math.min(sendWindowSize, sessionSendWindowSize);
/*     */       
/* 768 */       if (sendWindowSize <= 0)
/*     */         return; 
/* 770 */       if (sendWindowSize < dataFrameSize) {
/*     */         
/* 772 */         this.spdySession.updateSendWindowSize(writeStreamId, -1 * sendWindowSize);
/* 773 */         this.spdySession.updateSendWindowSize(0, -1 * sendWindowSize);
/*     */ 
/*     */ 
/*     */         
/* 777 */         SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(writeStreamId, spdyDataFrame.content().readRetainedSlice(sendWindowSize));
/*     */ 
/*     */ 
/*     */         
/* 781 */         ctx.writeAndFlush(partialDataFrame).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 784 */                 if (!future.isSuccess()) {
/* 785 */                   SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
/*     */                 }
/*     */               }
/*     */             });
/*     */         continue;
/*     */       } 
/* 791 */       this.spdySession.removePendingWrite(writeStreamId);
/* 792 */       this.spdySession.updateSendWindowSize(writeStreamId, -1 * dataFrameSize);
/* 793 */       this.spdySession.updateSendWindowSize(0, -1 * dataFrameSize);
/*     */ 
/*     */       
/* 796 */       if (spdyDataFrame.isLast()) {
/* 797 */         halfCloseStream(writeStreamId, false, (ChannelFuture)pendingWrite.promise);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 802 */       ctx.writeAndFlush(spdyDataFrame, pendingWrite.promise).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture future) throws Exception {
/* 805 */               if (!future.isSuccess()) {
/* 806 */                 SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
/*     */               }
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendGoAwayFrame(ChannelHandlerContext ctx, ChannelPromise future) {
/* 816 */     if (!ctx.channel().isActive()) {
/* 817 */       ctx.close(future);
/*     */       
/*     */       return;
/*     */     } 
/* 821 */     ChannelFuture f = sendGoAwayFrame(ctx, SpdySessionStatus.OK);
/* 822 */     if (this.spdySession.noActiveStreams()) {
/* 823 */       f.addListener((GenericFutureListener)new ClosingChannelFutureListener(ctx, future));
/*     */     } else {
/* 825 */       this.closeSessionFutureListener = new ClosingChannelFutureListener(ctx, future);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelFuture sendGoAwayFrame(ChannelHandlerContext ctx, SpdySessionStatus status) {
/* 832 */     if (!this.sentGoAwayFrame) {
/* 833 */       this.sentGoAwayFrame = true;
/* 834 */       SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, status);
/* 835 */       return ctx.writeAndFlush(spdyGoAwayFrame);
/*     */     } 
/* 837 */     return ctx.newSucceededFuture();
/*     */   }
/*     */   
/*     */   private static final class ClosingChannelFutureListener
/*     */     implements ChannelFutureListener {
/*     */     private final ChannelHandlerContext ctx;
/*     */     private final ChannelPromise promise;
/*     */     
/*     */     ClosingChannelFutureListener(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 846 */       this.ctx = ctx;
/* 847 */       this.promise = promise;
/*     */     }
/*     */ 
/*     */     
/*     */     public void operationComplete(ChannelFuture sentGoAwayFuture) throws Exception {
/* 852 */       this.ctx.close(this.promise);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdySessionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */