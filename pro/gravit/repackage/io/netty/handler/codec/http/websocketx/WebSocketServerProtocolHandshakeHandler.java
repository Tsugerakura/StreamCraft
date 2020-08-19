/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpUtil;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ import pro.gravit.repackage.io.netty.handler.ssl.SslHandler;
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
/*     */ 
/*     */ 
/*     */ class WebSocketServerProtocolHandshakeHandler
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*     */   private final WebSocketServerProtocolConfig serverConfig;
/*     */   private ChannelHandlerContext ctx;
/*     */   private ChannelPromise handshakePromise;
/*     */   
/*     */   WebSocketServerProtocolHandshakeHandler(WebSocketServerProtocolConfig serverConfig) {
/*  52 */     this.serverConfig = (WebSocketServerProtocolConfig)ObjectUtil.checkNotNull(serverConfig, "serverConfig");
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/*  57 */     this.ctx = ctx;
/*  58 */     this.handshakePromise = ctx.newPromise();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
/*  63 */     final FullHttpRequest req = (FullHttpRequest)msg;
/*  64 */     if (isNotWebSocketPath(req)) {
/*  65 */       ctx.fireChannelRead(msg);
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/*  70 */       if (!HttpMethod.GET.equals(req.method())) {
/*  71 */         sendHttpResponse(ctx, (HttpRequest)req, (HttpResponse)new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0)));
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*  77 */       WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), (HttpRequest)req, this.serverConfig.websocketPath()), this.serverConfig.subprotocols(), this.serverConfig.decoderConfig());
/*  78 */       final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker((HttpRequest)req);
/*  79 */       final ChannelPromise localHandshakePromise = this.handshakePromise;
/*  80 */       if (handshaker == null) {
/*  81 */         WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/*  88 */         WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
/*  89 */         ctx.pipeline().replace((ChannelHandler)this, "WS403Responder", 
/*  90 */             WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
/*     */         
/*  92 */         ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
/*  93 */         handshakeFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/*  96 */                 if (!future.isSuccess()) {
/*  97 */                   localHandshakePromise.tryFailure(future.cause());
/*  98 */                   ctx.fireExceptionCaught(future.cause());
/*     */                 } else {
/* 100 */                   localHandshakePromise.trySuccess();
/*     */                   
/* 102 */                   ctx.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
/*     */                   
/* 104 */                   ctx.fireUserEventTriggered(new WebSocketServerProtocolHandler.HandshakeComplete(req
/*     */                         
/* 106 */                         .uri(), req.headers(), handshaker.selectedSubprotocol()));
/*     */                 } 
/*     */               }
/*     */             });
/* 110 */         applyHandshakeTimeout();
/*     */       } 
/*     */     } finally {
/* 113 */       req.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isNotWebSocketPath(FullHttpRequest req) {
/* 118 */     String websocketPath = this.serverConfig.websocketPath();
/* 119 */     return this.serverConfig.checkStartsWith() ? (!req.uri().startsWith(websocketPath)) : (!req.uri().equals(websocketPath));
/*     */   }
/*     */   
/*     */   private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
/* 123 */     ChannelFuture f = ctx.channel().writeAndFlush(res);
/* 124 */     if (!HttpUtil.isKeepAlive((HttpMessage)req) || res.status().code() != 200) {
/* 125 */       f.addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
/* 130 */     String protocol = "ws";
/* 131 */     if (cp.get(SslHandler.class) != null)
/*     */     {
/* 133 */       protocol = "wss";
/*     */     }
/* 135 */     String host = req.headers().get((CharSequence)HttpHeaderNames.HOST);
/* 136 */     return protocol + "://" + host + path;
/*     */   }
/*     */   
/*     */   private void applyHandshakeTimeout() {
/* 140 */     final ChannelPromise localHandshakePromise = this.handshakePromise;
/* 141 */     long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
/* 142 */     if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
/*     */       return;
/*     */     }
/*     */     
/* 146 */     final ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable()
/*     */         {
/*     */           public void run() {
/* 149 */             if (!localHandshakePromise.isDone() && localHandshakePromise
/* 150 */               .tryFailure(new WebSocketHandshakeException("handshake timed out"))) {
/* 151 */               WebSocketServerProtocolHandshakeHandler.this.ctx.flush()
/* 152 */                 .fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT)
/* 153 */                 .close();
/*     */             }
/*     */           }
/*     */         },  handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
/*     */ 
/*     */     
/* 159 */     localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Void>()
/*     */         {
/*     */           public void operationComplete(Future<Void> f) throws Exception {
/* 162 */             timeoutFuture.cancel(false);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerProtocolHandshakeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */