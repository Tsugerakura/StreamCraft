/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.ScheduledFuture;
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
/*     */ class WebSocketClientProtocolHandshakeHandler
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*     */   private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
/*     */   private final WebSocketClientHandshaker handshaker;
/*     */   private final long handshakeTimeoutMillis;
/*     */   private ChannelHandlerContext ctx;
/*     */   private ChannelPromise handshakePromise;
/*     */   
/*     */   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker) {
/*  41 */     this(handshaker, 10000L);
/*     */   }
/*     */   
/*     */   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker, long handshakeTimeoutMillis) {
/*  45 */     this.handshaker = handshaker;
/*  46 */     this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/*  51 */     this.ctx = ctx;
/*  52 */     this.handshakePromise = ctx.newPromise();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(final ChannelHandlerContext ctx) throws Exception {
/*  57 */     super.channelActive(ctx);
/*  58 */     this.handshaker.handshake(ctx.channel()).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/*  61 */             if (!future.isSuccess()) {
/*  62 */               WebSocketClientProtocolHandshakeHandler.this.handshakePromise.tryFailure(future.cause());
/*  63 */               ctx.fireExceptionCaught(future.cause());
/*     */             } else {
/*  65 */               ctx.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/*  70 */     applyHandshakeTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  75 */     if (!(msg instanceof FullHttpResponse)) {
/*  76 */       ctx.fireChannelRead(msg);
/*     */       
/*     */       return;
/*     */     } 
/*  80 */     FullHttpResponse response = (FullHttpResponse)msg;
/*     */     try {
/*  82 */       if (!this.handshaker.isHandshakeComplete()) {
/*  83 */         this.handshaker.finishHandshake(ctx.channel(), response);
/*  84 */         this.handshakePromise.trySuccess();
/*  85 */         ctx.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
/*     */         
/*  87 */         ctx.pipeline().remove((ChannelHandler)this);
/*     */         return;
/*     */       } 
/*  90 */       throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
/*     */     } finally {
/*  92 */       response.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void applyHandshakeTimeout() {
/*  97 */     final ChannelPromise localHandshakePromise = this.handshakePromise;
/*  98 */     if (this.handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
/*     */       return;
/*     */     }
/*     */     
/* 102 */     final ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable()
/*     */         {
/*     */           public void run() {
/* 105 */             if (localHandshakePromise.isDone()) {
/*     */               return;
/*     */             }
/*     */             
/* 109 */             if (localHandshakePromise.tryFailure(new WebSocketHandshakeException("handshake timed out"))) {
/* 110 */               WebSocketClientProtocolHandshakeHandler.this.ctx.flush()
/* 111 */                 .fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT)
/* 112 */                 .close();
/*     */             }
/*     */           }
/*     */         },  this.handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
/*     */ 
/*     */     
/* 118 */     localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Void>()
/*     */         {
/*     */           public void operationComplete(Future<Void> f) throws Exception {
/* 121 */             timeoutFuture.cancel(false);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ChannelFuture getHandshakeFuture() {
/* 132 */     return (ChannelFuture)this.handshakePromise;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientProtocolHandshakeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */