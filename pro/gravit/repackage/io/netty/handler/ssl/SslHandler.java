/*      */ package pro.gravit.repackage.io.netty.handler.ssl;
/*      */ 
/*      */ import java.net.SocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.DatagramChannel;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.ScheduledFuture;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLEngineResult;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*      */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*      */ import pro.gravit.repackage.io.netty.channel.AbstractCoalescingBufferQueue;
/*      */ import pro.gravit.repackage.io.netty.channel.Channel;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundInvoker;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelPromiseNotifier;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.DecoderException;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.UnsupportedMessageTypeException;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.DefaultPromise;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.ImmediateExecutor;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.PromiseNotifier;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.ScheduledFuture;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SslHandler
/*      */   extends ByteToMessageDecoder
/*      */   implements ChannelOutboundHandler
/*      */ {
/*  169 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
/*      */   
/*  171 */   private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
/*      */   
/*  173 */   private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
/*      */   private static final int MAX_PLAINTEXT_LENGTH = 16384;
/*      */   private volatile ChannelHandlerContext ctx;
/*      */   private final SSLEngine engine;
/*      */   private final SslEngineType engineType;
/*      */   private final Executor delegatedTaskExecutor;
/*      */   private final boolean jdkCompatibilityMode;
/*      */   
/*      */   private enum SslEngineType
/*      */   {
/*  183 */     TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR)
/*      */     {
/*      */       SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
/*      */         SSLEngineResult result;
/*  187 */         int nioBufferCount = in.nioBufferCount();
/*  188 */         int writerIndex = out.writerIndex();
/*      */         
/*  190 */         if (nioBufferCount > 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  196 */           ReferenceCountedOpenSslEngine opensslEngine = (ReferenceCountedOpenSslEngine)handler.engine;
/*      */           try {
/*  198 */             handler.singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out
/*  199 */                 .writableBytes());
/*  200 */             result = opensslEngine.unwrap(in.nioBuffers(readerIndex, len), handler.singleBuffer);
/*      */           } finally {
/*  202 */             handler.singleBuffer[0] = null;
/*      */           } 
/*      */         } else {
/*  205 */           result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler
/*  206 */               .toByteBuffer(out, writerIndex, out.writableBytes()));
/*      */         } 
/*  208 */         out.writerIndex(writerIndex + result.bytesProduced());
/*  209 */         return result;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
/*  215 */         return allocator.directBuffer(((ReferenceCountedOpenSslEngine)handler.engine)
/*  216 */             .calculateMaxLengthForWrap(pendingBytes, numComponents));
/*      */       }
/*      */ 
/*      */       
/*      */       int calculatePendingData(SslHandler handler, int guess) {
/*  221 */         int sslPending = ((ReferenceCountedOpenSslEngine)handler.engine).sslPending();
/*  222 */         return (sslPending > 0) ? sslPending : guess;
/*      */       }
/*      */ 
/*      */       
/*      */       boolean jdkCompatibilityMode(SSLEngine engine) {
/*  227 */         return ((ReferenceCountedOpenSslEngine)engine).jdkCompatibilityMode;
/*      */       }
/*      */     },
/*  230 */     CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR)
/*      */     {
/*      */       SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
/*      */         SSLEngineResult result;
/*  234 */         int nioBufferCount = in.nioBufferCount();
/*  235 */         int writerIndex = out.writerIndex();
/*      */         
/*  237 */         if (nioBufferCount > 1) {
/*      */ 
/*      */           
/*      */           try {
/*      */             
/*  242 */             handler.singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
/*  243 */             result = ((ConscryptAlpnSslEngine)handler.engine).unwrap(in
/*  244 */                 .nioBuffers(readerIndex, len), handler
/*  245 */                 .singleBuffer);
/*      */           } finally {
/*  247 */             handler.singleBuffer[0] = null;
/*      */           } 
/*      */         } else {
/*  250 */           result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler
/*  251 */               .toByteBuffer(out, writerIndex, out.writableBytes()));
/*      */         } 
/*  253 */         out.writerIndex(writerIndex + result.bytesProduced());
/*  254 */         return result;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
/*  260 */         return allocator.directBuffer((
/*  261 */             (ConscryptAlpnSslEngine)handler.engine).calculateOutNetBufSize(pendingBytes, numComponents));
/*      */       }
/*      */ 
/*      */       
/*      */       int calculatePendingData(SslHandler handler, int guess) {
/*  266 */         return guess;
/*      */       }
/*      */ 
/*      */       
/*      */       boolean jdkCompatibilityMode(SSLEngine engine) {
/*  271 */         return true;
/*      */       }
/*      */     },
/*  274 */     JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR)
/*      */     {
/*      */       SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException
/*      */       {
/*  278 */         int writerIndex = out.writerIndex();
/*  279 */         ByteBuffer inNioBuffer = SslHandler.toByteBuffer(in, readerIndex, len);
/*  280 */         int position = inNioBuffer.position();
/*  281 */         SSLEngineResult result = handler.engine.unwrap(inNioBuffer, SslHandler
/*  282 */             .toByteBuffer(out, writerIndex, out.writableBytes()));
/*  283 */         out.writerIndex(writerIndex + result.bytesProduced());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  291 */         if (result.bytesConsumed() == 0) {
/*  292 */           int consumed = inNioBuffer.position() - position;
/*  293 */           if (consumed != result.bytesConsumed())
/*      */           {
/*  295 */             return new SSLEngineResult(result
/*  296 */                 .getStatus(), result.getHandshakeStatus(), consumed, result.bytesProduced());
/*      */           }
/*      */         } 
/*  299 */         return result;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
/*  311 */         return allocator.heapBuffer(handler.engine.getSession().getPacketBufferSize());
/*      */       }
/*      */ 
/*      */       
/*      */       int calculatePendingData(SslHandler handler, int guess) {
/*  316 */         return guess;
/*      */       }
/*      */ 
/*      */       
/*      */       boolean jdkCompatibilityMode(SSLEngine engine) {
/*  321 */         return true;
/*      */       } };
/*      */     final boolean wantsDirectBuffer;
/*      */     
/*      */     static SslEngineType forEngine(SSLEngine engine) {
/*  326 */       return (engine instanceof ReferenceCountedOpenSslEngine) ? TCNATIVE : ((engine instanceof ConscryptAlpnSslEngine) ? CONSCRYPT : JDK);
/*      */     }
/*      */     final ByteToMessageDecoder.Cumulator cumulator;
/*      */     
/*      */     SslEngineType(boolean wantsDirectBuffer, ByteToMessageDecoder.Cumulator cumulator) {
/*  331 */       this.wantsDirectBuffer = wantsDirectBuffer;
/*  332 */       this.cumulator = cumulator;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract SSLEngineResult unwrap(SslHandler param1SslHandler, ByteBuf param1ByteBuf1, int param1Int1, int param1Int2, ByteBuf param1ByteBuf2) throws SSLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract int calculatePendingData(SslHandler param1SslHandler, int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract boolean jdkCompatibilityMode(SSLEngine param1SSLEngine);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     abstract ByteBuf allocateWrapBuffer(SslHandler param1SslHandler, ByteBufAllocator param1ByteBufAllocator, int param1Int1, int param1Int2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  377 */   private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
/*      */   
/*      */   private final boolean startTls;
/*      */   
/*      */   private boolean sentFirstMessage;
/*      */   private boolean flushedBeforeHandshake;
/*      */   private boolean readDuringHandshake;
/*      */   private boolean handshakeStarted;
/*      */   private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
/*  386 */   private Promise<Channel> handshakePromise = (Promise<Channel>)new LazyChannelPromise();
/*  387 */   private final LazyChannelPromise sslClosePromise = new LazyChannelPromise();
/*      */ 
/*      */   
/*      */   private boolean needsFlush;
/*      */ 
/*      */   
/*      */   private boolean outboundClosed;
/*      */ 
/*      */   
/*      */   private boolean closeNotify;
/*      */ 
/*      */   
/*      */   private boolean processTask;
/*      */ 
/*      */   
/*      */   private int packetLength;
/*      */ 
/*      */   
/*      */   private boolean firedChannelRead;
/*      */   
/*  407 */   private volatile long handshakeTimeoutMillis = 10000L;
/*  408 */   private volatile long closeNotifyFlushTimeoutMillis = 3000L;
/*      */   private volatile long closeNotifyReadTimeoutMillis;
/*  410 */   volatile int wrapDataSize = 16384;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler(SSLEngine engine) {
/*  418 */     this(engine, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler(SSLEngine engine, boolean startTls) {
/*  429 */     this(engine, startTls, (Executor)ImmediateExecutor.INSTANCE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
/*  440 */     this(engine, false, delegatedTaskExecutor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
/*  453 */     this.engine = (SSLEngine)ObjectUtil.checkNotNull(engine, "engine");
/*  454 */     this.delegatedTaskExecutor = (Executor)ObjectUtil.checkNotNull(delegatedTaskExecutor, "delegatedTaskExecutor");
/*  455 */     this.engineType = SslEngineType.forEngine(engine);
/*  456 */     this.startTls = startTls;
/*  457 */     this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(engine);
/*  458 */     setCumulator(this.engineType.cumulator);
/*      */   }
/*      */   
/*      */   public long getHandshakeTimeoutMillis() {
/*  462 */     return this.handshakeTimeoutMillis;
/*      */   }
/*      */   
/*      */   public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
/*  466 */     ObjectUtil.checkNotNull(unit, "unit");
/*  467 */     setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
/*      */   }
/*      */   
/*      */   public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
/*  471 */     if (handshakeTimeoutMillis < 0L) {
/*  472 */       throw new IllegalArgumentException("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)");
/*      */     }
/*      */     
/*  475 */     this.handshakeTimeoutMillis = handshakeTimeoutMillis;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setWrapDataSize(int wrapDataSize) {
/*  500 */     this.wrapDataSize = wrapDataSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public long getCloseNotifyTimeoutMillis() {
/*  508 */     return getCloseNotifyFlushTimeoutMillis();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
/*  516 */     setCloseNotifyFlushTimeout(closeNotifyTimeout, unit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
/*  524 */     setCloseNotifyFlushTimeoutMillis(closeNotifyFlushTimeoutMillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long getCloseNotifyFlushTimeoutMillis() {
/*  533 */     return this.closeNotifyFlushTimeoutMillis;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setCloseNotifyFlushTimeout(long closeNotifyFlushTimeout, TimeUnit unit) {
/*  542 */     setCloseNotifyFlushTimeoutMillis(unit.toMillis(closeNotifyFlushTimeout));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setCloseNotifyFlushTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
/*  549 */     if (closeNotifyFlushTimeoutMillis < 0L) {
/*  550 */       throw new IllegalArgumentException("closeNotifyFlushTimeoutMillis: " + closeNotifyFlushTimeoutMillis + " (expected: >= 0)");
/*      */     }
/*      */     
/*  553 */     this.closeNotifyFlushTimeoutMillis = closeNotifyFlushTimeoutMillis;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long getCloseNotifyReadTimeoutMillis() {
/*  562 */     return this.closeNotifyReadTimeoutMillis;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setCloseNotifyReadTimeout(long closeNotifyReadTimeout, TimeUnit unit) {
/*  571 */     setCloseNotifyReadTimeoutMillis(unit.toMillis(closeNotifyReadTimeout));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setCloseNotifyReadTimeoutMillis(long closeNotifyReadTimeoutMillis) {
/*  578 */     if (closeNotifyReadTimeoutMillis < 0L) {
/*  579 */       throw new IllegalArgumentException("closeNotifyReadTimeoutMillis: " + closeNotifyReadTimeoutMillis + " (expected: >= 0)");
/*      */     }
/*      */     
/*  582 */     this.closeNotifyReadTimeoutMillis = closeNotifyReadTimeoutMillis;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SSLEngine engine() {
/*  589 */     return this.engine;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String applicationProtocol() {
/*  598 */     SSLEngine engine = engine();
/*  599 */     if (!(engine instanceof ApplicationProtocolAccessor)) {
/*  600 */       return null;
/*      */     }
/*      */     
/*  603 */     return ((ApplicationProtocolAccessor)engine).getNegotiatedApplicationProtocol();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Future<Channel> handshakeFuture() {
/*  613 */     return (Future<Channel>)this.handshakePromise;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ChannelFuture close() {
/*  621 */     return closeOutbound();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ChannelFuture close(ChannelPromise promise) {
/*  629 */     return closeOutbound(promise);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ChannelFuture closeOutbound() {
/*  639 */     return closeOutbound(this.ctx.newPromise());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ChannelFuture closeOutbound(final ChannelPromise promise) {
/*  649 */     ChannelHandlerContext ctx = this.ctx;
/*  650 */     if (ctx.executor().inEventLoop()) {
/*  651 */       closeOutbound0(promise);
/*      */     } else {
/*  653 */       ctx.executor().execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  656 */               SslHandler.this.closeOutbound0(promise);
/*      */             }
/*      */           });
/*      */     } 
/*  660 */     return (ChannelFuture)promise;
/*      */   }
/*      */   
/*      */   private void closeOutbound0(ChannelPromise promise) {
/*  664 */     this.outboundClosed = true;
/*  665 */     this.engine.closeOutbound();
/*      */     try {
/*  667 */       flush(this.ctx, promise);
/*  668 */     } catch (Exception e) {
/*  669 */       if (!promise.tryFailure(e)) {
/*  670 */         logger.warn("{} flush() raised a masked exception.", this.ctx.channel(), e);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Future<Channel> sslCloseFuture() {
/*  683 */     return (Future<Channel>)this.sslClosePromise;
/*      */   }
/*      */ 
/*      */   
/*      */   public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
/*  688 */     if (!this.pendingUnencryptedWrites.isEmpty())
/*      */     {
/*  690 */       this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, (Throwable)new ChannelException("Pending write on removal of SslHandler"));
/*      */     }
/*      */     
/*  693 */     this.pendingUnencryptedWrites = null;
/*  694 */     if (this.engine instanceof ReferenceCounted) {
/*  695 */       ((ReferenceCounted)this.engine).release();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/*  701 */     ctx.bind(localAddress, promise);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/*  707 */     ctx.connect(remoteAddress, localAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/*  712 */     ctx.deregister(promise);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/*  718 */     closeOutboundAndChannel(ctx, promise, true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/*  724 */     closeOutboundAndChannel(ctx, promise, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public void read(ChannelHandlerContext ctx) throws Exception {
/*  729 */     if (!this.handshakePromise.isDone()) {
/*  730 */       this.readDuringHandshake = true;
/*      */     }
/*      */     
/*  733 */     ctx.read();
/*      */   }
/*      */   
/*      */   private static IllegalStateException newPendingWritesNullException() {
/*  737 */     return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
/*      */   }
/*      */ 
/*      */   
/*      */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/*  742 */     if (!(msg instanceof ByteBuf)) {
/*  743 */       UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(msg, new Class[] { ByteBuf.class });
/*  744 */       ReferenceCountUtil.safeRelease(msg);
/*  745 */       promise.setFailure((Throwable)exception);
/*  746 */     } else if (this.pendingUnencryptedWrites == null) {
/*  747 */       ReferenceCountUtil.safeRelease(msg);
/*  748 */       promise.setFailure(newPendingWritesNullException());
/*      */     } else {
/*  750 */       this.pendingUnencryptedWrites.add((ByteBuf)msg, promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void flush(ChannelHandlerContext ctx) throws Exception {
/*  758 */     if (this.startTls && !this.sentFirstMessage) {
/*  759 */       this.sentFirstMessage = true;
/*  760 */       this.pendingUnencryptedWrites.writeAndRemoveAll(ctx);
/*  761 */       forceFlush(ctx);
/*      */ 
/*      */       
/*  764 */       startHandshakeProcessing();
/*      */       
/*      */       return;
/*      */     } 
/*  768 */     if (this.processTask) {
/*      */       return;
/*      */     }
/*      */     
/*      */     try {
/*  773 */       wrapAndFlush(ctx);
/*  774 */     } catch (Throwable cause) {
/*  775 */       setHandshakeFailure(ctx, cause);
/*  776 */       PlatformDependent.throwException(cause);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void wrapAndFlush(ChannelHandlerContext ctx) throws SSLException {
/*  781 */     if (this.pendingUnencryptedWrites.isEmpty())
/*      */     {
/*      */ 
/*      */ 
/*      */       
/*  786 */       this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.newPromise());
/*      */     }
/*  788 */     if (!this.handshakePromise.isDone()) {
/*  789 */       this.flushedBeforeHandshake = true;
/*      */     }
/*      */     try {
/*  792 */       wrap(ctx, false);
/*      */     }
/*      */     finally {
/*      */       
/*  796 */       forceFlush(ctx);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
/*  802 */     ByteBuf out = null;
/*  803 */     ChannelPromise promise = null;
/*  804 */     ByteBufAllocator alloc = ctx.alloc();
/*  805 */     boolean needUnwrap = false;
/*  806 */     ByteBuf buf = null;
/*      */     try {
/*  808 */       int wrapDataSize = this.wrapDataSize;
/*      */ 
/*      */       
/*  811 */       while (!ctx.isRemoved()) {
/*  812 */         promise = ctx.newPromise();
/*      */ 
/*      */         
/*  815 */         buf = (wrapDataSize > 0) ? this.pendingUnencryptedWrites.remove(alloc, wrapDataSize, promise) : this.pendingUnencryptedWrites.removeFirst(promise);
/*  816 */         if (buf == null) {
/*      */           break;
/*      */         }
/*      */         
/*  820 */         if (out == null) {
/*  821 */           out = allocateOutNetBuf(ctx, buf.readableBytes(), buf.nioBufferCount());
/*      */         }
/*      */         
/*  824 */         SSLEngineResult result = wrap(alloc, this.engine, buf, out);
/*      */         
/*  826 */         if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
/*  827 */           buf.release();
/*  828 */           buf = null;
/*  829 */           SSLException exception = new SSLException("SSLEngine closed already");
/*  830 */           promise.tryFailure(exception);
/*  831 */           promise = null;
/*      */ 
/*      */           
/*  834 */           this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, exception);
/*      */           return;
/*      */         } 
/*  837 */         if (buf.isReadable()) {
/*  838 */           this.pendingUnencryptedWrites.addFirst(buf, promise);
/*      */ 
/*      */           
/*  841 */           promise = null;
/*      */         } else {
/*  843 */           buf.release();
/*      */         } 
/*  845 */         buf = null;
/*      */         
/*  847 */         switch (result.getHandshakeStatus()) {
/*      */           case BUFFER_OVERFLOW:
/*  849 */             if (!runDelegatedTasks(inUnwrap)) {
/*      */               break;
/*      */             }
/*      */             continue;
/*      */ 
/*      */           
/*      */           case CLOSED:
/*  856 */             setHandshakeSuccess();
/*      */           
/*      */           case null:
/*  859 */             setHandshakeSuccessIfStillHandshaking();
/*      */           
/*      */           case null:
/*  862 */             finishWrap(ctx, out, promise, inUnwrap, false);
/*  863 */             promise = null;
/*  864 */             out = null;
/*      */             continue;
/*      */           case null:
/*  867 */             needUnwrap = true;
/*      */             return;
/*      */         } 
/*  870 */         throw new IllegalStateException("Unknown handshake status: " + result
/*  871 */             .getHandshakeStatus());
/*      */       }
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/*  877 */       if (buf != null) {
/*  878 */         buf.release();
/*      */       }
/*  880 */       finishWrap(ctx, out, promise, inUnwrap, needUnwrap);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap, boolean needUnwrap) {
/*  886 */     if (out == null) {
/*  887 */       out = Unpooled.EMPTY_BUFFER;
/*  888 */     } else if (!out.isReadable()) {
/*  889 */       out.release();
/*  890 */       out = Unpooled.EMPTY_BUFFER;
/*      */     } 
/*      */     
/*  893 */     if (promise != null) {
/*  894 */       ctx.write(out, promise);
/*      */     } else {
/*  896 */       ctx.write(out);
/*      */     } 
/*      */     
/*  899 */     if (inUnwrap) {
/*  900 */       this.needsFlush = true;
/*      */     }
/*      */     
/*  903 */     if (needUnwrap)
/*      */     {
/*      */       
/*  906 */       readIfNeeded(ctx);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean wrapNonAppData(final ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
/*  917 */     ByteBuf out = null;
/*  918 */     ByteBufAllocator alloc = ctx.alloc();
/*      */ 
/*      */     
/*      */     try {
/*  922 */       while (!ctx.isRemoved()) {
/*  923 */         boolean bool; if (out == null)
/*      */         {
/*      */ 
/*      */           
/*  927 */           out = allocateOutNetBuf(ctx, 2048, 1);
/*      */         }
/*  929 */         SSLEngineResult result = wrap(alloc, this.engine, Unpooled.EMPTY_BUFFER, out);
/*      */         
/*  931 */         if (result.bytesProduced() > 0) {
/*  932 */           ctx.write(out).addListener((GenericFutureListener)new ChannelFutureListener()
/*      */               {
/*      */                 public void operationComplete(ChannelFuture future) {
/*  935 */                   Throwable cause = future.cause();
/*  936 */                   if (cause != null) {
/*  937 */                     SslHandler.this.setHandshakeFailureTransportFailure(ctx, cause);
/*      */                   }
/*      */                 }
/*      */               });
/*  941 */           if (inUnwrap) {
/*  942 */             this.needsFlush = true;
/*      */           }
/*  944 */           out = null;
/*      */         } 
/*      */         
/*  947 */         SSLEngineResult.HandshakeStatus status = result.getHandshakeStatus();
/*  948 */         switch (status) {
/*      */           case CLOSED:
/*  950 */             setHandshakeSuccess();
/*  951 */             bool = false; return bool;
/*      */           case BUFFER_OVERFLOW:
/*  953 */             if (!runDelegatedTasks(inUnwrap)) {
/*      */               break;
/*      */             }
/*      */             break;
/*      */ 
/*      */           
/*      */           case null:
/*  960 */             if (inUnwrap) {
/*      */ 
/*      */ 
/*      */               
/*  964 */               bool = false; return bool;
/*      */             } 
/*      */             
/*  967 */             unwrapNonAppData(ctx);
/*      */             break;
/*      */           case null:
/*      */             break;
/*      */           case null:
/*  972 */             setHandshakeSuccessIfStillHandshaking();
/*      */ 
/*      */             
/*  975 */             if (!inUnwrap) {
/*  976 */               unwrapNonAppData(ctx);
/*      */             }
/*  978 */             bool = true; return bool;
/*      */           default:
/*  980 */             throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  985 */         if (result.bytesProduced() == 0 && status != SSLEngineResult.HandshakeStatus.NEED_TASK) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  991 */         if (result.bytesConsumed() == 0 && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } finally {
/*  996 */       if (out != null) {
/*  997 */         out.release();
/*      */       }
/*      */     } 
/* 1000 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private SSLEngineResult wrap(ByteBufAllocator alloc, SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
/* 1005 */     ByteBuf newDirectIn = null; try {
/*      */       ByteBuffer[] in0; SSLEngineResult result;
/* 1007 */       int readerIndex = in.readerIndex();
/* 1008 */       int readableBytes = in.readableBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1013 */       if (in.isDirect() || !this.engineType.wantsDirectBuffer) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1018 */         if (!(in instanceof CompositeByteBuf) && in.nioBufferCount() == 1) {
/* 1019 */           in0 = this.singleBuffer;
/*      */ 
/*      */           
/* 1022 */           in0[0] = in.internalNioBuffer(readerIndex, readableBytes);
/*      */         } else {
/* 1024 */           in0 = in.nioBuffers();
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1030 */         newDirectIn = alloc.directBuffer(readableBytes);
/* 1031 */         newDirectIn.writeBytes(in, readerIndex, readableBytes);
/* 1032 */         in0 = this.singleBuffer;
/* 1033 */         in0[0] = newDirectIn.internalNioBuffer(newDirectIn.readerIndex(), readableBytes);
/*      */       } 
/*      */       
/*      */       while (true) {
/* 1037 */         ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
/* 1038 */         result = engine.wrap(in0, out0);
/* 1039 */         in.skipBytes(result.bytesConsumed());
/* 1040 */         out.writerIndex(out.writerIndex() + result.bytesProduced());
/*      */         
/* 1042 */         switch (result.getStatus()) {
/*      */           case BUFFER_OVERFLOW:
/* 1044 */             out.ensureWritable(engine.getSession().getPacketBufferSize()); continue;
/*      */         }  break;
/*      */       } 
/* 1047 */       return result;
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/* 1052 */       this.singleBuffer[0] = null;
/*      */       
/* 1054 */       if (newDirectIn != null) {
/* 1055 */         newDirectIn.release();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 1062 */     ClosedChannelException exception = new ClosedChannelException();
/*      */ 
/*      */     
/* 1065 */     setHandshakeFailure(ctx, exception, !this.outboundClosed, this.handshakeStarted, false);
/*      */ 
/*      */     
/* 1068 */     notifyClosePromise(exception);
/*      */     
/* 1070 */     super.channelInactive(ctx);
/*      */   }
/*      */ 
/*      */   
/*      */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 1075 */     if (ignoreException(cause)) {
/*      */ 
/*      */       
/* 1078 */       if (logger.isDebugEnabled()) {
/* 1079 */         logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", ctx
/*      */             
/* 1081 */             .channel(), cause);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1086 */       if (ctx.channel().isActive()) {
/* 1087 */         ctx.close();
/*      */       }
/*      */     } else {
/* 1090 */       ctx.fireExceptionCaught(cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean ignoreException(Throwable t) {
/* 1104 */     if (!(t instanceof SSLException) && t instanceof java.io.IOException && this.sslClosePromise.isDone()) {
/* 1105 */       String message = t.getMessage();
/*      */ 
/*      */ 
/*      */       
/* 1109 */       if (message != null && IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
/* 1110 */         return true;
/*      */       }
/*      */ 
/*      */       
/* 1114 */       StackTraceElement[] elements = t.getStackTrace();
/* 1115 */       for (StackTraceElement element : elements) {
/* 1116 */         String classname = element.getClassName();
/* 1117 */         String methodname = element.getMethodName();
/*      */ 
/*      */         
/* 1120 */         if (!classname.startsWith("pro.gravit.repackage.io.netty."))
/*      */         {
/*      */ 
/*      */ 
/*      */           
/* 1125 */           if ("read".equals(methodname)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1131 */             if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
/* 1132 */               return true;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             try {
/* 1139 */               Class<?> clazz = PlatformDependent.getClassLoader(getClass()).loadClass(classname);
/*      */               
/* 1141 */               if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class
/* 1142 */                 .isAssignableFrom(clazz)) {
/* 1143 */                 return true;
/*      */               }
/*      */ 
/*      */               
/* 1147 */               if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel"
/* 1148 */                 .equals(clazz.getSuperclass().getName())) {
/* 1149 */                 return true;
/*      */               }
/* 1151 */             } catch (Throwable cause) {
/* 1152 */               logger.debug("Unexpected exception while loading class {} classname {}", new Object[] {
/* 1153 */                     getClass(), classname, cause });
/*      */             } 
/*      */           }  } 
/*      */       } 
/*      */     } 
/* 1158 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEncrypted(ByteBuf buffer) {
/* 1174 */     if (buffer.readableBytes() < 5) {
/* 1175 */       throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
/*      */     }
/*      */     
/* 1178 */     return (SslUtils.getEncryptedPacketLength(buffer, buffer.readerIndex()) != -2);
/*      */   }
/*      */   
/*      */   private void decodeJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) throws NotSslRecordException {
/* 1182 */     int packetLength = this.packetLength;
/*      */     
/* 1184 */     if (packetLength > 0) {
/* 1185 */       if (in.readableBytes() < packetLength) {
/*      */         return;
/*      */       }
/*      */     } else {
/*      */       
/* 1190 */       int readableBytes = in.readableBytes();
/* 1191 */       if (readableBytes < 5) {
/*      */         return;
/*      */       }
/* 1194 */       packetLength = SslUtils.getEncryptedPacketLength(in, in.readerIndex());
/* 1195 */       if (packetLength == -2) {
/*      */ 
/*      */         
/* 1198 */         NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
/* 1199 */         in.skipBytes(in.readableBytes());
/*      */ 
/*      */ 
/*      */         
/* 1203 */         setHandshakeFailure(ctx, e);
/*      */         
/* 1205 */         throw e;
/*      */       } 
/* 1207 */       assert packetLength > 0;
/* 1208 */       if (packetLength > readableBytes) {
/*      */         
/* 1210 */         this.packetLength = packetLength;
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/* 1217 */     this.packetLength = 0;
/*      */     try {
/* 1219 */       int bytesConsumed = unwrap(ctx, in, in.readerIndex(), packetLength);
/* 1220 */       assert bytesConsumed == packetLength || this.engine.isInboundDone() : "we feed the SSLEngine a packets worth of data: " + packetLength + " but it only consumed: " + bytesConsumed;
/*      */ 
/*      */       
/* 1223 */       in.skipBytes(bytesConsumed);
/* 1224 */     } catch (Throwable cause) {
/* 1225 */       handleUnwrapThrowable(ctx, cause);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void decodeNonJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) {
/*      */     try {
/* 1231 */       in.skipBytes(unwrap(ctx, in, in.readerIndex(), in.readableBytes()));
/* 1232 */     } catch (Throwable cause) {
/* 1233 */       handleUnwrapThrowable(ctx, cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleUnwrapThrowable(ChannelHandlerContext ctx, Throwable cause) {
/*      */     try {
/* 1243 */       if (this.handshakePromise.tryFailure(cause)) {
/* 1244 */         ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1249 */       wrapAndFlush(ctx);
/* 1250 */     } catch (SSLException ex) {
/* 1251 */       logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", ex);
/*      */     }
/*      */     finally {
/*      */       
/* 1255 */       setHandshakeFailure(ctx, cause, true, false, true);
/*      */     } 
/* 1257 */     PlatformDependent.throwException(cause);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
/* 1262 */     if (this.processTask) {
/*      */       return;
/*      */     }
/* 1265 */     if (this.jdkCompatibilityMode) {
/* 1266 */       decodeJdkCompatible(ctx, in);
/*      */     } else {
/* 1268 */       decodeNonJdkCompatible(ctx, in);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 1274 */     channelReadComplete0(ctx);
/*      */   }
/*      */ 
/*      */   
/*      */   private void channelReadComplete0(ChannelHandlerContext ctx) {
/* 1279 */     discardSomeReadBytes();
/*      */     
/* 1281 */     flushIfNeeded(ctx);
/* 1282 */     readIfNeeded(ctx);
/*      */     
/* 1284 */     this.firedChannelRead = false;
/* 1285 */     ctx.fireChannelReadComplete();
/*      */   }
/*      */ 
/*      */   
/*      */   private void readIfNeeded(ChannelHandlerContext ctx) {
/* 1290 */     if (!ctx.channel().config().isAutoRead() && (!this.firedChannelRead || !this.handshakePromise.isDone()))
/*      */     {
/*      */       
/* 1293 */       ctx.read();
/*      */     }
/*      */   }
/*      */   
/*      */   private void flushIfNeeded(ChannelHandlerContext ctx) {
/* 1298 */     if (this.needsFlush) {
/* 1299 */       forceFlush(ctx);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
/* 1307 */     unwrap(ctx, Unpooled.EMPTY_BUFFER, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int unwrap(ChannelHandlerContext ctx, ByteBuf packet, int offset, int length) throws SSLException {
/* 1315 */     int originalLength = length;
/* 1316 */     boolean wrapLater = false;
/* 1317 */     boolean notifyClosure = false;
/* 1318 */     int overflowReadableBytes = -1;
/* 1319 */     ByteBuf decodeOut = allocate(ctx, length);
/*      */ 
/*      */     
/*      */     try {
/* 1323 */       while (!ctx.isRemoved()) {
/* 1324 */         int readableBytes, previousOverflowReadableBytes, bufferSize; SSLEngineResult result = this.engineType.unwrap(this, packet, offset, length, decodeOut);
/* 1325 */         SSLEngineResult.Status status = result.getStatus();
/* 1326 */         SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
/* 1327 */         int produced = result.bytesProduced();
/* 1328 */         int consumed = result.bytesConsumed();
/*      */ 
/*      */         
/* 1331 */         offset += consumed;
/* 1332 */         length -= consumed;
/*      */         
/* 1334 */         switch (status) {
/*      */           case BUFFER_OVERFLOW:
/* 1336 */             readableBytes = decodeOut.readableBytes();
/* 1337 */             previousOverflowReadableBytes = overflowReadableBytes;
/* 1338 */             overflowReadableBytes = readableBytes;
/* 1339 */             bufferSize = this.engine.getSession().getApplicationBufferSize() - readableBytes;
/* 1340 */             if (readableBytes > 0) {
/* 1341 */               this.firedChannelRead = true;
/* 1342 */               ctx.fireChannelRead(decodeOut);
/*      */ 
/*      */               
/* 1345 */               decodeOut = null;
/* 1346 */               if (bufferSize <= 0)
/*      */               {
/*      */ 
/*      */ 
/*      */                 
/* 1351 */                 bufferSize = this.engine.getSession().getApplicationBufferSize();
/*      */               }
/*      */             } else {
/*      */               
/* 1355 */               decodeOut.release();
/* 1356 */               decodeOut = null;
/*      */             } 
/* 1358 */             if (readableBytes == 0 && previousOverflowReadableBytes == 0)
/*      */             {
/*      */               
/* 1361 */               throw new IllegalStateException("Two consecutive overflows but no content was consumed. " + SSLSession.class
/* 1362 */                   .getSimpleName() + " getApplicationBufferSize: " + this.engine
/* 1363 */                   .getSession().getApplicationBufferSize() + " maybe too small.");
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1368 */             decodeOut = allocate(ctx, this.engineType.calculatePendingData(this, bufferSize));
/*      */             continue;
/*      */           
/*      */           case CLOSED:
/* 1372 */             notifyClosure = true;
/* 1373 */             overflowReadableBytes = -1;
/*      */             break;
/*      */           default:
/* 1376 */             overflowReadableBytes = -1;
/*      */             break;
/*      */         } 
/*      */         
/* 1380 */         switch (handshakeStatus) {
/*      */           case null:
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case null:
/* 1387 */             if (wrapNonAppData(ctx, true) && length == 0) {
/*      */               break;
/*      */             }
/*      */             break;
/*      */           case BUFFER_OVERFLOW:
/* 1392 */             if (!runDelegatedTasks(true)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1399 */               wrapLater = false;
/*      */               break;
/*      */             } 
/*      */             break;
/*      */           case CLOSED:
/* 1404 */             setHandshakeSuccess();
/* 1405 */             wrapLater = true;
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case null:
/* 1420 */             if (setHandshakeSuccessIfStillHandshaking()) {
/* 1421 */               wrapLater = true;
/*      */ 
/*      */               
/*      */               continue;
/*      */             } 
/*      */ 
/*      */             
/* 1428 */             if (length == 0) {
/*      */               break;
/*      */             }
/*      */             break;
/*      */           default:
/* 1433 */             throw new IllegalStateException("unknown handshake status: " + handshakeStatus);
/*      */         } 
/*      */         
/* 1436 */         if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW || (handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_TASK && consumed == 0 && produced == 0)) {
/*      */ 
/*      */           
/* 1439 */           if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)
/*      */           {
/*      */             
/* 1442 */             readIfNeeded(ctx);
/*      */           }
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */       
/* 1449 */       if (this.flushedBeforeHandshake && this.handshakePromise.isDone()) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1454 */         this.flushedBeforeHandshake = false;
/* 1455 */         wrapLater = true;
/*      */       } 
/*      */       
/* 1458 */       if (wrapLater) {
/* 1459 */         wrap(ctx, true);
/*      */       }
/*      */       
/* 1462 */       if (notifyClosure) {
/* 1463 */         notifyClosePromise((Throwable)null);
/*      */       }
/*      */     } finally {
/* 1466 */       if (decodeOut != null) {
/* 1467 */         if (decodeOut.isReadable()) {
/* 1468 */           this.firedChannelRead = true;
/*      */           
/* 1470 */           ctx.fireChannelRead(decodeOut);
/*      */         } else {
/* 1472 */           decodeOut.release();
/*      */         } 
/*      */       }
/*      */     } 
/* 1476 */     return originalLength - length;
/*      */   }
/*      */   
/*      */   private static ByteBuffer toByteBuffer(ByteBuf out, int index, int len) {
/* 1480 */     return (out.nioBufferCount() == 1) ? out.internalNioBuffer(index, len) : out
/* 1481 */       .nioBuffer(index, len);
/*      */   }
/*      */   
/*      */   private static boolean inEventLoop(Executor executor) {
/* 1485 */     return (executor instanceof EventExecutor && ((EventExecutor)executor).inEventLoop());
/*      */   }
/*      */   
/*      */   private static void runAllDelegatedTasks(SSLEngine engine) {
/*      */     while (true) {
/* 1490 */       Runnable task = engine.getDelegatedTask();
/* 1491 */       if (task == null) {
/*      */         return;
/*      */       }
/* 1494 */       task.run();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean runDelegatedTasks(boolean inUnwrap) {
/* 1506 */     if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE || inEventLoop(this.delegatedTaskExecutor)) {
/*      */       
/* 1508 */       runAllDelegatedTasks(this.engine);
/* 1509 */       return true;
/*      */     } 
/* 1511 */     executeDelegatedTasks(inUnwrap);
/* 1512 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private void executeDelegatedTasks(boolean inUnwrap) {
/* 1517 */     this.processTask = true;
/*      */     try {
/* 1519 */       this.delegatedTaskExecutor.execute(new SslTasksRunner(inUnwrap));
/* 1520 */     } catch (RejectedExecutionException e) {
/* 1521 */       this.processTask = false;
/* 1522 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final class SslTasksRunner
/*      */     implements Runnable
/*      */   {
/*      */     private final boolean inUnwrap;
/*      */ 
/*      */     
/*      */     SslTasksRunner(boolean inUnwrap) {
/* 1534 */       this.inUnwrap = inUnwrap;
/*      */     }
/*      */ 
/*      */     
/*      */     private void taskError(Throwable e) {
/* 1539 */       if (this.inUnwrap) {
/*      */ 
/*      */         
/*      */         try {
/*      */ 
/*      */           
/* 1545 */           SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
/* 1546 */         } catch (Throwable cause) {
/* 1547 */           safeExceptionCaught(cause);
/*      */         } 
/*      */       } else {
/* 1550 */         SslHandler.this.setHandshakeFailure(SslHandler.this.ctx, e);
/* 1551 */         SslHandler.this.forceFlush(SslHandler.this.ctx);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void safeExceptionCaught(Throwable cause) {
/*      */       try {
/* 1558 */         SslHandler.this.exceptionCaught(SslHandler.this.ctx, wrapIfNeeded(cause));
/* 1559 */       } catch (Throwable error) {
/* 1560 */         SslHandler.this.ctx.fireExceptionCaught(error);
/*      */       } 
/*      */     }
/*      */     
/*      */     private Throwable wrapIfNeeded(Throwable cause) {
/* 1565 */       if (!this.inUnwrap)
/*      */       {
/* 1567 */         return cause;
/*      */       }
/*      */ 
/*      */       
/* 1571 */       return (cause instanceof DecoderException) ? cause : (Throwable)new DecoderException(cause);
/*      */     }
/*      */     
/*      */     private void tryDecodeAgain() {
/*      */       try {
/* 1576 */         SslHandler.this.channelRead(SslHandler.this.ctx, Unpooled.EMPTY_BUFFER);
/* 1577 */       } catch (Throwable cause) {
/* 1578 */         safeExceptionCaught(cause);
/*      */       
/*      */       }
/*      */       finally {
/*      */         
/* 1583 */         SslHandler.this.channelReadComplete0(SslHandler.this.ctx);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void resumeOnEventExecutor() {
/* 1592 */       assert SslHandler.this.ctx.executor().inEventLoop();
/*      */       
/* 1594 */       SslHandler.this.processTask = false;
/*      */       
/*      */       try {
/* 1597 */         SSLEngineResult.HandshakeStatus status = SslHandler.this.engine.getHandshakeStatus();
/* 1598 */         switch (status) {
/*      */           
/*      */           case BUFFER_OVERFLOW:
/* 1601 */             SslHandler.this.executeDelegatedTasks(this.inUnwrap);
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case CLOSED:
/* 1607 */             SslHandler.this.setHandshakeSuccess();
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case null:
/* 1613 */             SslHandler.this.setHandshakeSuccessIfStillHandshaking();
/*      */ 
/*      */             
/*      */             try {
/* 1617 */               SslHandler.this.wrap(SslHandler.this.ctx, this.inUnwrap);
/* 1618 */             } catch (Throwable e) {
/* 1619 */               taskError(e);
/*      */               return;
/*      */             } 
/* 1622 */             if (this.inUnwrap)
/*      */             {
/*      */               
/* 1625 */               SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
/*      */             }
/*      */ 
/*      */             
/* 1629 */             SslHandler.this.forceFlush(SslHandler.this.ctx);
/*      */             
/* 1631 */             tryDecodeAgain();
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case null:
/*      */             try {
/* 1638 */               SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
/* 1639 */             } catch (SSLException e) {
/* 1640 */               SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
/*      */               return;
/*      */             } 
/* 1643 */             tryDecodeAgain();
/*      */             return;
/*      */ 
/*      */ 
/*      */           
/*      */           case null:
/*      */             try {
/* 1650 */               if (!SslHandler.this.wrapNonAppData(SslHandler.this.ctx, false) && this.inUnwrap)
/*      */               {
/*      */ 
/*      */ 
/*      */                 
/* 1655 */                 SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
/*      */               }
/*      */ 
/*      */               
/* 1659 */               SslHandler.this.forceFlush(SslHandler.this.ctx);
/* 1660 */             } catch (Throwable e) {
/* 1661 */               taskError(e);
/*      */               
/*      */               return;
/*      */             } 
/*      */             
/* 1666 */             tryDecodeAgain();
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1671 */         throw new AssertionError();
/*      */       }
/* 1673 */       catch (Throwable cause) {
/* 1674 */         safeExceptionCaught(cause);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/*      */       try {
/* 1681 */         SslHandler.runAllDelegatedTasks(SslHandler.this.engine);
/*      */ 
/*      */         
/* 1684 */         assert SslHandler.this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_TASK;
/*      */ 
/*      */         
/* 1687 */         SslHandler.this.ctx.executor().execute(new Runnable()
/*      */             {
/*      */               public void run() {
/* 1690 */                 SslHandler.SslTasksRunner.this.resumeOnEventExecutor();
/*      */               }
/*      */             });
/* 1693 */       } catch (Throwable cause) {
/* 1694 */         handleException(cause);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void handleException(final Throwable cause) {
/* 1699 */       if (SslHandler.this.ctx.executor().inEventLoop()) {
/* 1700 */         SslHandler.this.processTask = false;
/* 1701 */         safeExceptionCaught(cause);
/*      */       } else {
/*      */         try {
/* 1704 */           SslHandler.this.ctx.executor().execute(new Runnable()
/*      */               {
/*      */                 public void run() {
/* 1707 */                   SslHandler.this.processTask = false;
/* 1708 */                   SslHandler.SslTasksRunner.this.safeExceptionCaught(cause);
/*      */                 }
/*      */               });
/* 1711 */         } catch (RejectedExecutionException ignore) {
/* 1712 */           SslHandler.this.processTask = false;
/*      */ 
/*      */           
/* 1715 */           SslHandler.this.ctx.fireExceptionCaught(cause);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean setHandshakeSuccessIfStillHandshaking() {
/* 1729 */     if (!this.handshakePromise.isDone()) {
/* 1730 */       setHandshakeSuccess();
/* 1731 */       return true;
/*      */     } 
/* 1733 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHandshakeSuccess() {
/* 1740 */     this.handshakePromise.trySuccess(this.ctx.channel());
/*      */     
/* 1742 */     if (logger.isDebugEnabled()) {
/* 1743 */       logger.debug("{} HANDSHAKEN: {}", this.ctx.channel(), this.engine.getSession().getCipherSuite());
/*      */     }
/* 1745 */     this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
/*      */     
/* 1747 */     if (this.readDuringHandshake && !this.ctx.channel().config().isAutoRead()) {
/* 1748 */       this.readDuringHandshake = false;
/* 1749 */       this.ctx.read();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause) {
/* 1757 */     setHandshakeFailure(ctx, cause, true, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean closeInbound, boolean notify, boolean alwaysFlushAndClose) {
/*      */     try {
/* 1768 */       this.outboundClosed = true;
/* 1769 */       this.engine.closeOutbound();
/*      */       
/* 1771 */       if (closeInbound) {
/*      */         try {
/* 1773 */           this.engine.closeInbound();
/* 1774 */         } catch (SSLException e) {
/* 1775 */           if (logger.isDebugEnabled()) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1780 */             String msg = e.getMessage();
/* 1781 */             if (msg == null || (!msg.contains("possible truncation attack") && 
/* 1782 */               !msg.contains("closing inbound before receiving peer's close_notify"))) {
/* 1783 */               logger.debug("{} SSLEngine.closeInbound() raised an exception.", ctx.channel(), e);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       }
/* 1788 */       if (this.handshakePromise.tryFailure(cause) || alwaysFlushAndClose) {
/* 1789 */         SslUtils.handleHandshakeFailure(ctx, cause, notify);
/*      */       }
/*      */     } finally {
/*      */       
/* 1793 */       releaseAndFailAll(ctx, cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHandshakeFailureTransportFailure(ChannelHandlerContext ctx, Throwable cause) {
/*      */     try {
/* 1802 */       SSLException transportFailure = new SSLException("failure when writing TLS control frames", cause);
/* 1803 */       releaseAndFailAll(ctx, transportFailure);
/* 1804 */       if (this.handshakePromise.tryFailure(transportFailure)) {
/* 1805 */         ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(transportFailure));
/*      */       }
/*      */     } finally {
/* 1808 */       ctx.close();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void releaseAndFailAll(ChannelHandlerContext ctx, Throwable cause) {
/* 1813 */     if (this.pendingUnencryptedWrites != null) {
/* 1814 */       this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, cause);
/*      */     }
/*      */   }
/*      */   
/*      */   private void notifyClosePromise(Throwable cause) {
/* 1819 */     if (cause == null) {
/* 1820 */       if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
/* 1821 */         this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS);
/*      */       }
/*      */     }
/* 1824 */     else if (this.sslClosePromise.tryFailure(cause)) {
/* 1825 */       this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(cause));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void closeOutboundAndChannel(ChannelHandlerContext ctx, final ChannelPromise promise, boolean disconnect) throws Exception {
/* 1832 */     this.outboundClosed = true;
/* 1833 */     this.engine.closeOutbound();
/*      */     
/* 1835 */     if (!ctx.channel().isActive()) {
/* 1836 */       if (disconnect) {
/* 1837 */         ctx.disconnect(promise);
/*      */       } else {
/* 1839 */         ctx.close(promise);
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/* 1844 */     ChannelPromise closeNotifyPromise = ctx.newPromise();
/*      */     try {
/* 1846 */       flush(ctx, closeNotifyPromise);
/*      */     } finally {
/* 1848 */       if (!this.closeNotify) {
/* 1849 */         this.closeNotify = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1858 */         safeClose(ctx, (ChannelFuture)closeNotifyPromise, ctx.newPromise().addListener((GenericFutureListener)new ChannelPromiseNotifier(false, new ChannelPromise[] { promise })));
/*      */       }
/*      */       else {
/*      */         
/* 1862 */         this.sslClosePromise.addListener((GenericFutureListener)new FutureListener<Channel>()
/*      */             {
/*      */               public void operationComplete(Future<Channel> future) {
/* 1865 */                 promise.setSuccess();
/*      */               }
/*      */             });
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 1873 */     if (this.pendingUnencryptedWrites != null) {
/* 1874 */       this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, promise);
/*      */     } else {
/* 1876 */       promise.setFailure(newPendingWritesNullException());
/*      */     } 
/* 1878 */     flush(ctx);
/*      */   }
/*      */ 
/*      */   
/*      */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 1883 */     this.ctx = ctx;
/*      */     
/* 1885 */     this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue(ctx.channel(), 16);
/* 1886 */     if (ctx.channel().isActive()) {
/* 1887 */       startHandshakeProcessing();
/*      */     }
/*      */   }
/*      */   
/*      */   private void startHandshakeProcessing() {
/* 1892 */     if (!this.handshakeStarted) {
/* 1893 */       this.handshakeStarted = true;
/* 1894 */       if (this.engine.getUseClientMode())
/*      */       {
/*      */ 
/*      */         
/* 1898 */         handshake();
/*      */       }
/* 1900 */       applyHandshakeTimeout();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Future<Channel> renegotiate() {
/* 1908 */     ChannelHandlerContext ctx = this.ctx;
/* 1909 */     if (ctx == null) {
/* 1910 */       throw new IllegalStateException();
/*      */     }
/*      */     
/* 1913 */     return renegotiate(ctx.executor().newPromise());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Future<Channel> renegotiate(final Promise<Channel> promise) {
/* 1920 */     ObjectUtil.checkNotNull(promise, "promise");
/*      */     
/* 1922 */     ChannelHandlerContext ctx = this.ctx;
/* 1923 */     if (ctx == null) {
/* 1924 */       throw new IllegalStateException();
/*      */     }
/*      */     
/* 1927 */     EventExecutor executor = ctx.executor();
/* 1928 */     if (!executor.inEventLoop()) {
/* 1929 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/* 1932 */               SslHandler.this.renegotiateOnEventLoop(promise);
/*      */             }
/*      */           });
/* 1935 */       return (Future<Channel>)promise;
/*      */     } 
/*      */     
/* 1938 */     renegotiateOnEventLoop(promise);
/* 1939 */     return (Future<Channel>)promise;
/*      */   }
/*      */   
/*      */   private void renegotiateOnEventLoop(Promise<Channel> newHandshakePromise) {
/* 1943 */     Promise<Channel> oldHandshakePromise = this.handshakePromise;
/* 1944 */     if (!oldHandshakePromise.isDone()) {
/*      */ 
/*      */       
/* 1947 */       oldHandshakePromise.addListener((GenericFutureListener)new PromiseNotifier(new Promise[] { newHandshakePromise }));
/*      */     } else {
/* 1949 */       this.handshakePromise = newHandshakePromise;
/* 1950 */       handshake();
/* 1951 */       applyHandshakeTimeout();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handshake() {
/* 1959 */     if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1964 */     if (this.handshakePromise.isDone()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1975 */     ChannelHandlerContext ctx = this.ctx;
/*      */     try {
/* 1977 */       this.engine.beginHandshake();
/* 1978 */       wrapNonAppData(ctx, false);
/* 1979 */     } catch (Throwable e) {
/* 1980 */       setHandshakeFailure(ctx, e);
/*      */     } finally {
/* 1982 */       forceFlush(ctx);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void applyHandshakeTimeout() {
/* 1987 */     final Promise<Channel> localHandshakePromise = this.handshakePromise;
/*      */ 
/*      */     
/* 1990 */     long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
/* 1991 */     if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
/*      */       return;
/*      */     }
/*      */     
/* 1995 */     final ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable()
/*      */         {
/*      */           public void run() {
/* 1998 */             if (localHandshakePromise.isDone()) {
/*      */               return;
/*      */             }
/* 2001 */             SSLException exception = new SSLException("handshake timed out");
/*      */             try {
/* 2003 */               if (localHandshakePromise.tryFailure(exception)) {
/* 2004 */                 SslUtils.handleHandshakeFailure(SslHandler.this.ctx, exception, true);
/*      */               }
/*      */             } finally {
/* 2007 */               SslHandler.this.releaseAndFailAll(SslHandler.this.ctx, exception);
/*      */             } 
/*      */           }
/*      */         }handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
/*      */ 
/*      */     
/* 2013 */     localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Channel>()
/*      */         {
/*      */           public void operationComplete(Future<Channel> f) throws Exception {
/* 2016 */             timeoutFuture.cancel(false);
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private void forceFlush(ChannelHandlerContext ctx) {
/* 2022 */     this.needsFlush = false;
/* 2023 */     ctx.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/* 2031 */     if (!this.startTls) {
/* 2032 */       startHandshakeProcessing();
/*      */     }
/* 2034 */     ctx.fireChannelActive();
/*      */   }
/*      */ 
/*      */   
/*      */   private void safeClose(final ChannelHandlerContext ctx, final ChannelFuture flushFuture, final ChannelPromise promise) {
/*      */     final ScheduledFuture<?> timeoutFuture;
/* 2040 */     if (!ctx.channel().isActive()) {
/* 2041 */       ctx.close(promise);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 2046 */     if (!flushFuture.isDone()) {
/* 2047 */       long closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis;
/* 2048 */       if (closeNotifyTimeout > 0L) {
/*      */         
/* 2050 */         ScheduledFuture scheduledFuture = ctx.executor().schedule(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/* 2054 */                 if (!flushFuture.isDone()) {
/* 2055 */                   SslHandler.logger.warn("{} Last write attempt timed out; force-closing the connection.", ctx
/* 2056 */                       .channel());
/* 2057 */                   SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
/*      */                 } 
/*      */               }
/*      */             }closeNotifyTimeout, TimeUnit.MILLISECONDS);
/*      */       } else {
/* 2062 */         timeoutFuture = null;
/*      */       } 
/*      */     } else {
/* 2065 */       timeoutFuture = null;
/*      */     } 
/*      */ 
/*      */     
/* 2069 */     flushFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*      */         {
/*      */           public void operationComplete(ChannelFuture f) throws Exception
/*      */           {
/* 2073 */             if (timeoutFuture != null) {
/* 2074 */               timeoutFuture.cancel(false);
/*      */             }
/* 2076 */             final long closeNotifyReadTimeout = SslHandler.this.closeNotifyReadTimeoutMillis;
/* 2077 */             if (closeNotifyReadTimeout <= 0L) {
/*      */ 
/*      */               
/* 2080 */               SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
/*      */             } else {
/*      */               final ScheduledFuture<?> closeNotifyReadTimeoutFuture;
/*      */               
/* 2084 */               if (!SslHandler.this.sslClosePromise.isDone()) {
/* 2085 */                 ScheduledFuture scheduledFuture = ctx.executor().schedule(new Runnable()
/*      */                     {
/*      */                       public void run() {
/* 2088 */                         if (!SslHandler.this.sslClosePromise.isDone()) {
/* 2089 */                           SslHandler.logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", ctx
/*      */                               
/* 2091 */                               .channel(), Long.valueOf(closeNotifyReadTimeout));
/*      */ 
/*      */                           
/* 2094 */                           SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
/*      */                         } 
/*      */                       }
/*      */                     }closeNotifyReadTimeout, TimeUnit.MILLISECONDS);
/*      */               } else {
/* 2099 */                 closeNotifyReadTimeoutFuture = null;
/*      */               } 
/*      */ 
/*      */               
/* 2103 */               SslHandler.this.sslClosePromise.addListener((GenericFutureListener)new FutureListener<Channel>()
/*      */                   {
/*      */                     public void operationComplete(Future<Channel> future) throws Exception {
/* 2106 */                       if (closeNotifyReadTimeoutFuture != null) {
/* 2107 */                         closeNotifyReadTimeoutFuture.cancel(false);
/*      */                       }
/* 2109 */                       SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
/*      */                     }
/*      */                   });
/*      */             } 
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void addCloseListener(ChannelFuture future, ChannelPromise promise) {
/* 2124 */     future.addListener((GenericFutureListener)new ChannelPromiseNotifier(false, new ChannelPromise[] { promise }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
/* 2132 */     ByteBufAllocator alloc = ctx.alloc();
/* 2133 */     if (this.engineType.wantsDirectBuffer) {
/* 2134 */       return alloc.directBuffer(capacity);
/*      */     }
/* 2136 */     return alloc.buffer(capacity);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes, int numComponents) {
/* 2145 */     return this.engineType.allocateWrapBuffer(this, ctx.alloc(), pendingBytes, numComponents);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class SslHandlerCoalescingBufferQueue
/*      */     extends AbstractCoalescingBufferQueue
/*      */   {
/*      */     SslHandlerCoalescingBufferQueue(Channel channel, int initSize) {
/* 2156 */       super(channel, initSize);
/*      */     }
/*      */ 
/*      */     
/*      */     protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
/* 2161 */       int wrapDataSize = SslHandler.this.wrapDataSize;
/* 2162 */       if (cumulation instanceof CompositeByteBuf) {
/* 2163 */         CompositeByteBuf composite = (CompositeByteBuf)cumulation;
/* 2164 */         int numComponents = composite.numComponents();
/* 2165 */         if (numComponents == 0 || 
/* 2166 */           !SslHandler.attemptCopyToCumulation(composite.internalComponent(numComponents - 1), next, wrapDataSize)) {
/* 2167 */           composite.addComponent(true, next);
/*      */         }
/* 2169 */         return (ByteBuf)composite;
/*      */       } 
/* 2171 */       return SslHandler.attemptCopyToCumulation(cumulation, next, wrapDataSize) ? cumulation : 
/* 2172 */         copyAndCompose(alloc, cumulation, next);
/*      */     }
/*      */ 
/*      */     
/*      */     protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
/* 2177 */       if (first instanceof CompositeByteBuf) {
/* 2178 */         CompositeByteBuf composite = (CompositeByteBuf)first;
/* 2179 */         if (SslHandler.this.engineType.wantsDirectBuffer) {
/* 2180 */           first = allocator.directBuffer(composite.readableBytes());
/*      */         } else {
/* 2182 */           first = allocator.heapBuffer(composite.readableBytes());
/*      */         } 
/*      */         try {
/* 2185 */           first.writeBytes((ByteBuf)composite);
/* 2186 */         } catch (Throwable cause) {
/* 2187 */           first.release();
/* 2188 */           PlatformDependent.throwException(cause);
/*      */         } 
/* 2190 */         composite.release();
/*      */       } 
/* 2192 */       return first;
/*      */     }
/*      */ 
/*      */     
/*      */     protected ByteBuf removeEmptyValue() {
/* 2197 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
/* 2202 */     int inReadableBytes = next.readableBytes();
/* 2203 */     int cumulationCapacity = cumulation.capacity();
/* 2204 */     if (wrapDataSize - cumulation.readableBytes() >= inReadableBytes && ((cumulation
/*      */ 
/*      */ 
/*      */       
/* 2208 */       .isWritable(inReadableBytes) && cumulationCapacity >= wrapDataSize) || (cumulationCapacity < wrapDataSize && 
/*      */       
/* 2210 */       ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false))))) {
/* 2211 */       cumulation.writeBytes(next);
/* 2212 */       next.release();
/* 2213 */       return true;
/*      */     } 
/* 2215 */     return false;
/*      */   }
/*      */   
/*      */   private final class LazyChannelPromise extends DefaultPromise<Channel> {
/*      */     private LazyChannelPromise() {}
/*      */     
/*      */     protected EventExecutor executor() {
/* 2222 */       if (SslHandler.this.ctx == null) {
/* 2223 */         throw new IllegalStateException();
/*      */       }
/* 2225 */       return SslHandler.this.ctx.executor();
/*      */     }
/*      */ 
/*      */     
/*      */     protected void checkDeadLock() {
/* 2230 */       if (SslHandler.this.ctx == null) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2239 */       super.checkDeadLock();
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */