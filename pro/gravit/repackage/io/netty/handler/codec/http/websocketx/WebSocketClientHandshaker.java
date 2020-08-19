/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.SimpleChannelInboundHandler;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpClientCodec;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContentDecompressor;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectAggregator;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequestEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpScheme;
/*     */ import pro.gravit.repackage.io.netty.util.NetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
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
/*     */ public abstract class WebSocketClientHandshaker
/*     */ {
/*  52 */   private static final String HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
/*  53 */   private static final String HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
/*     */   
/*     */   protected static final int DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS = 10000;
/*     */   
/*     */   private final URI uri;
/*     */   
/*     */   private final WebSocketVersion version;
/*     */   
/*     */   private volatile boolean handshakeComplete;
/*  62 */   private volatile long forceCloseTimeoutMillis = 10000L;
/*     */ 
/*     */   
/*     */   private volatile int forceCloseInit;
/*     */   
/*  67 */   private static final AtomicIntegerFieldUpdater<WebSocketClientHandshaker> FORCE_CLOSE_INIT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(WebSocketClientHandshaker.class, "forceCloseInit");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean forceCloseComplete;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String expectedSubprotocol;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile String actualSubprotocol;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final HttpHeaders customHeaders;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int maxFramePayloadLength;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean absoluteUpgradeUrl;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength) {
/*  98 */     this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, 10000L);
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
/*     */   protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis) {
/* 121 */     this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, false);
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
/*     */   protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/* 147 */     this.uri = uri;
/* 148 */     this.version = version;
/* 149 */     this.expectedSubprotocol = subprotocol;
/* 150 */     this.customHeaders = customHeaders;
/* 151 */     this.maxFramePayloadLength = maxFramePayloadLength;
/* 152 */     this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/* 153 */     this.absoluteUpgradeUrl = absoluteUpgradeUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI uri() {
/* 160 */     return this.uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketVersion version() {
/* 167 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxFramePayloadLength() {
/* 174 */     return this.maxFramePayloadLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isHandshakeComplete() {
/* 181 */     return this.handshakeComplete;
/*     */   }
/*     */   
/*     */   private void setHandshakeComplete() {
/* 185 */     this.handshakeComplete = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String expectedSubprotocol() {
/* 192 */     return this.expectedSubprotocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String actualSubprotocol() {
/* 200 */     return this.actualSubprotocol;
/*     */   }
/*     */   
/*     */   private void setActualSubprotocol(String actualSubprotocol) {
/* 204 */     this.actualSubprotocol = actualSubprotocol;
/*     */   }
/*     */   
/*     */   public long forceCloseTimeoutMillis() {
/* 208 */     return this.forceCloseTimeoutMillis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isForceCloseComplete() {
/* 216 */     return this.forceCloseComplete;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 226 */     this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture handshake(Channel channel) {
/* 237 */     ObjectUtil.checkNotNull(channel, "channel");
/* 238 */     return handshake(channel, channel.newPromise());
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
/*     */   public final ChannelFuture handshake(Channel channel, final ChannelPromise promise) {
/* 250 */     ChannelPipeline pipeline = channel.pipeline();
/* 251 */     HttpResponseDecoder decoder = (HttpResponseDecoder)pipeline.get(HttpResponseDecoder.class);
/* 252 */     if (decoder == null) {
/* 253 */       HttpClientCodec codec = (HttpClientCodec)pipeline.get(HttpClientCodec.class);
/* 254 */       if (codec == null) {
/* 255 */         promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
/*     */         
/* 257 */         return (ChannelFuture)promise;
/*     */       } 
/*     */     } 
/*     */     
/* 261 */     FullHttpRequest request = newHandshakeRequest();
/*     */     
/* 263 */     channel.writeAndFlush(request).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) {
/* 266 */             if (future.isSuccess()) {
/* 267 */               ChannelPipeline p = future.channel().pipeline();
/* 268 */               ChannelHandlerContext ctx = p.context(HttpRequestEncoder.class);
/* 269 */               if (ctx == null) {
/* 270 */                 ctx = p.context(HttpClientCodec.class);
/*     */               }
/* 272 */               if (ctx == null) {
/* 273 */                 promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec"));
/*     */                 
/*     */                 return;
/*     */               } 
/* 277 */               p.addAfter(ctx.name(), "ws-encoder", (ChannelHandler)WebSocketClientHandshaker.this.newWebSocketEncoder());
/*     */               
/* 279 */               promise.setSuccess();
/*     */             } else {
/* 281 */               promise.setFailure(future.cause());
/*     */             } 
/*     */           }
/*     */         });
/* 285 */     return (ChannelFuture)promise;
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
/*     */   public final void finishHandshake(Channel channel, FullHttpResponse response) {
/* 302 */     verify(response);
/*     */ 
/*     */ 
/*     */     
/* 306 */     String receivedProtocol = response.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
/* 307 */     receivedProtocol = (receivedProtocol != null) ? receivedProtocol.trim() : null;
/* 308 */     String expectedProtocol = (this.expectedSubprotocol != null) ? this.expectedSubprotocol : "";
/* 309 */     boolean protocolValid = false;
/*     */     
/* 311 */     if (expectedProtocol.isEmpty() && receivedProtocol == null) {
/*     */       
/* 313 */       protocolValid = true;
/* 314 */       setActualSubprotocol(this.expectedSubprotocol);
/* 315 */     } else if (!expectedProtocol.isEmpty() && receivedProtocol != null && !receivedProtocol.isEmpty()) {
/*     */       
/* 317 */       for (String protocol : expectedProtocol.split(",")) {
/* 318 */         if (protocol.trim().equals(receivedProtocol)) {
/* 319 */           protocolValid = true;
/* 320 */           setActualSubprotocol(receivedProtocol);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 326 */     if (!protocolValid) {
/* 327 */       throw new WebSocketHandshakeException(String.format("Invalid subprotocol. Actual: %s. Expected one of: %s", new Object[] { receivedProtocol, this.expectedSubprotocol }));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 332 */     setHandshakeComplete();
/*     */     
/* 334 */     final ChannelPipeline p = channel.pipeline();
/*     */     
/* 336 */     HttpContentDecompressor decompressor = (HttpContentDecompressor)p.get(HttpContentDecompressor.class);
/* 337 */     if (decompressor != null) {
/* 338 */       p.remove((ChannelHandler)decompressor);
/*     */     }
/*     */ 
/*     */     
/* 342 */     HttpObjectAggregator aggregator = (HttpObjectAggregator)p.get(HttpObjectAggregator.class);
/* 343 */     if (aggregator != null) {
/* 344 */       p.remove((ChannelHandler)aggregator);
/*     */     }
/*     */     
/* 347 */     ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
/* 348 */     if (ctx == null) {
/* 349 */       ctx = p.context(HttpClientCodec.class);
/* 350 */       if (ctx == null) {
/* 351 */         throw new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec");
/*     */       }
/*     */       
/* 354 */       final HttpClientCodec codec = (HttpClientCodec)ctx.handler();
/*     */       
/* 356 */       codec.removeOutboundHandler();
/*     */       
/* 358 */       p.addAfter(ctx.name(), "ws-decoder", (ChannelHandler)newWebsocketDecoder());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 363 */       channel.eventLoop().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 366 */               p.remove((ChannelHandler)codec);
/*     */             }
/*     */           });
/*     */     } else {
/* 370 */       if (p.get(HttpRequestEncoder.class) != null)
/*     */       {
/* 372 */         p.remove(HttpRequestEncoder.class);
/*     */       }
/* 374 */       final ChannelHandlerContext context = ctx;
/* 375 */       p.addAfter(context.name(), "ws-decoder", (ChannelHandler)newWebsocketDecoder());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 380 */       channel.eventLoop().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 383 */               p.remove(context.handler());
/*     */             }
/*     */           });
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
/*     */   public final ChannelFuture processHandshake(Channel channel, HttpResponse response) {
/* 400 */     return processHandshake(channel, response, channel.newPromise());
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
/*     */   public final ChannelFuture processHandshake(final Channel channel, HttpResponse response, final ChannelPromise promise) {
/* 417 */     if (response instanceof FullHttpResponse) {
/*     */       try {
/* 419 */         finishHandshake(channel, (FullHttpResponse)response);
/* 420 */         promise.setSuccess();
/* 421 */       } catch (Throwable cause) {
/* 422 */         promise.setFailure(cause);
/*     */       } 
/*     */     } else {
/* 425 */       ChannelPipeline p = channel.pipeline();
/* 426 */       ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
/* 427 */       if (ctx == null) {
/* 428 */         ctx = p.context(HttpClientCodec.class);
/* 429 */         if (ctx == null) {
/* 430 */           return (ChannelFuture)promise.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 438 */       String aggregatorName = "httpAggregator";
/* 439 */       p.addAfter(ctx.name(), aggregatorName, (ChannelHandler)new HttpObjectAggregator(8192));
/* 440 */       p.addAfter(aggregatorName, "handshaker", (ChannelHandler)new SimpleChannelInboundHandler<FullHttpResponse>()
/*     */           {
/*     */             protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception
/*     */             {
/* 444 */               ctx.pipeline().remove((ChannelHandler)this);
/*     */               try {
/* 446 */                 WebSocketClientHandshaker.this.finishHandshake(channel, msg);
/* 447 */                 promise.setSuccess();
/* 448 */               } catch (Throwable cause) {
/* 449 */                 promise.setFailure(cause);
/*     */               } 
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 456 */               ctx.pipeline().remove((ChannelHandler)this);
/* 457 */               promise.setFailure(cause);
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 463 */               if (!promise.isDone()) {
/* 464 */                 promise.tryFailure(new ClosedChannelException());
/*     */               }
/* 466 */               ctx.fireChannelInactive();
/*     */             }
/*     */           });
/*     */       try {
/* 470 */         ctx.fireChannelRead(ReferenceCountUtil.retain(response));
/* 471 */       } catch (Throwable cause) {
/* 472 */         promise.setFailure(cause);
/*     */       } 
/*     */     } 
/* 475 */     return (ChannelFuture)promise;
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
/*     */   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
/* 502 */     ObjectUtil.checkNotNull(channel, "channel");
/* 503 */     return close(channel, frame, channel.newPromise());
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
/*     */   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
/* 517 */     ObjectUtil.checkNotNull(channel, "channel");
/* 518 */     channel.writeAndFlush(frame, promise);
/* 519 */     applyForceCloseTimeout(channel, (ChannelFuture)promise);
/* 520 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void applyForceCloseTimeout(final Channel channel, ChannelFuture flushFuture) {
/* 524 */     final long forceCloseTimeoutMillis = this.forceCloseTimeoutMillis;
/* 525 */     final WebSocketClientHandshaker handshaker = this;
/* 526 */     if (forceCloseTimeoutMillis <= 0L || !channel.isActive() || this.forceCloseInit != 0) {
/*     */       return;
/*     */     }
/*     */     
/* 530 */     flushFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           
/*     */           public void operationComplete(ChannelFuture future) throws Exception
/*     */           {
/* 537 */             if (future.isSuccess() && channel.isActive() && WebSocketClientHandshaker
/* 538 */               .FORCE_CLOSE_INIT_UPDATER.compareAndSet(handshaker, 0, 1)) {
/* 539 */               final ScheduledFuture forceCloseFuture = channel.eventLoop().schedule(new Runnable()
/*     */                   {
/*     */                     public void run() {
/* 542 */                       if (channel.isActive()) {
/* 543 */                         channel.close();
/* 544 */                         WebSocketClientHandshaker.this.forceCloseComplete = true;
/*     */                       } 
/*     */                     }
/*     */                   },  forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
/*     */               
/* 549 */               channel.closeFuture().addListener((GenericFutureListener)new ChannelFutureListener()
/*     */                   {
/*     */                     public void operationComplete(ChannelFuture future) throws Exception {
/* 552 */                       forceCloseFuture.cancel(false);
/*     */                     }
/*     */                   });
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String upgradeUrl(URI wsURL) {
/* 564 */     if (this.absoluteUpgradeUrl) {
/* 565 */       return wsURL.toString();
/*     */     }
/*     */     
/* 568 */     String path = wsURL.getRawPath();
/* 569 */     String query = wsURL.getRawQuery();
/* 570 */     if (query != null && !query.isEmpty()) {
/* 571 */       path = path + '?' + query;
/*     */     }
/*     */     
/* 574 */     return (path == null || path.isEmpty()) ? "/" : path;
/*     */   }
/*     */   
/*     */   static CharSequence websocketHostValue(URI wsURL) {
/* 578 */     int port = wsURL.getPort();
/* 579 */     if (port == -1) {
/* 580 */       return wsURL.getHost();
/*     */     }
/* 582 */     String host = wsURL.getHost();
/* 583 */     String scheme = wsURL.getScheme();
/* 584 */     if (port == HttpScheme.HTTP.port()) {
/* 585 */       return (HttpScheme.HTTP.name().contentEquals(scheme) || WebSocketScheme.WS
/* 586 */         .name().contentEquals(scheme)) ? host : 
/* 587 */         NetUtil.toSocketAddressString(host, port);
/*     */     }
/* 589 */     if (port == HttpScheme.HTTPS.port()) {
/* 590 */       return (HttpScheme.HTTPS.name().contentEquals(scheme) || WebSocketScheme.WSS
/* 591 */         .name().contentEquals(scheme)) ? host : 
/* 592 */         NetUtil.toSocketAddressString(host, port);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 597 */     return NetUtil.toSocketAddressString(host, port);
/*     */   } static CharSequence websocketOriginValue(URI wsURL) {
/*     */     String schemePrefix;
/*     */     int defaultPort;
/* 601 */     String scheme = wsURL.getScheme();
/*     */     
/* 603 */     int port = wsURL.getPort();
/*     */     
/* 605 */     if (WebSocketScheme.WSS.name().contentEquals(scheme) || HttpScheme.HTTPS
/* 606 */       .name().contentEquals(scheme) || (scheme == null && port == WebSocketScheme.WSS
/* 607 */       .port())) {
/*     */       
/* 609 */       schemePrefix = HTTPS_SCHEME_PREFIX;
/* 610 */       defaultPort = WebSocketScheme.WSS.port();
/*     */     } else {
/* 612 */       schemePrefix = HTTP_SCHEME_PREFIX;
/* 613 */       defaultPort = WebSocketScheme.WS.port();
/*     */     } 
/*     */ 
/*     */     
/* 617 */     String host = wsURL.getHost().toLowerCase(Locale.US);
/*     */     
/* 619 */     if (port != defaultPort && port != -1)
/*     */     {
/*     */       
/* 622 */       return schemePrefix + NetUtil.toSocketAddressString(host, port);
/*     */     }
/* 624 */     return schemePrefix + host;
/*     */   }
/*     */   
/*     */   protected abstract FullHttpRequest newHandshakeRequest();
/*     */   
/*     */   protected abstract void verify(FullHttpResponse paramFullHttpResponse);
/*     */   
/*     */   protected abstract WebSocketFrameDecoder newWebsocketDecoder();
/*     */   
/*     */   protected abstract WebSocketFrameEncoder newWebSocketEncoder();
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */