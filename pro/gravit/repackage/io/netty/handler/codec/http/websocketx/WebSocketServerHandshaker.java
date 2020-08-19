/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContentCompressor;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectAggregator;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequestDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpServerCodec;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public abstract class WebSocketServerHandshaker
/*     */ {
/*  49 */   protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String uri;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] subprotocols;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final WebSocketVersion version;
/*     */ 
/*     */ 
/*     */   
/*     */   private final WebSocketDecoderConfig decoderConfig;
/*     */ 
/*     */ 
/*     */   
/*     */   private String selectedSubprotocol;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SUB_PROTOCOL_WILDCARD = "*";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, int maxFramePayloadLength) {
/*  82 */     this(version, uri, subprotocols, WebSocketDecoderConfig.newBuilder()
/*  83 */         .maxFramePayloadLength(maxFramePayloadLength)
/*  84 */         .build());
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
/*     */   protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, WebSocketDecoderConfig decoderConfig) {
/* 102 */     this.version = version;
/* 103 */     this.uri = uri;
/* 104 */     if (subprotocols != null) {
/* 105 */       String[] subprotocolArray = subprotocols.split(",");
/* 106 */       for (int i = 0; i < subprotocolArray.length; i++) {
/* 107 */         subprotocolArray[i] = subprotocolArray[i].trim();
/*     */       }
/* 109 */       this.subprotocols = subprotocolArray;
/*     */     } else {
/* 111 */       this.subprotocols = EmptyArrays.EMPTY_STRINGS;
/*     */     } 
/* 113 */     this.decoderConfig = (WebSocketDecoderConfig)ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String uri() {
/* 120 */     return this.uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> subprotocols() {
/* 127 */     Set<String> ret = new LinkedHashSet<String>();
/* 128 */     Collections.addAll(ret, this.subprotocols);
/* 129 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketVersion version() {
/* 136 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxFramePayloadLength() {
/* 145 */     return this.decoderConfig.maxFramePayloadLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketDecoderConfig decoderConfig() {
/* 154 */     return this.decoderConfig;
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
/*     */   public ChannelFuture handshake(Channel channel, FullHttpRequest req) {
/* 169 */     return handshake(channel, req, (HttpHeaders)null, channel.newPromise());
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
/*     */   public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, final ChannelPromise promise) {
/*     */     final String encoderName;
/* 191 */     if (logger.isDebugEnabled()) {
/* 192 */       logger.debug("{} WebSocket version {} server handshake", channel, version());
/*     */     }
/* 194 */     FullHttpResponse response = newHandshakeResponse(req, responseHeaders);
/* 195 */     ChannelPipeline p = channel.pipeline();
/* 196 */     if (p.get(HttpObjectAggregator.class) != null) {
/* 197 */       p.remove(HttpObjectAggregator.class);
/*     */     }
/* 199 */     if (p.get(HttpContentCompressor.class) != null) {
/* 200 */       p.remove(HttpContentCompressor.class);
/*     */     }
/* 202 */     ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
/*     */     
/* 204 */     if (ctx == null) {
/*     */       
/* 206 */       ctx = p.context(HttpServerCodec.class);
/* 207 */       if (ctx == null) {
/* 208 */         promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
/*     */         
/* 210 */         return (ChannelFuture)promise;
/*     */       } 
/* 212 */       p.addBefore(ctx.name(), "wsencoder", (ChannelHandler)newWebSocketEncoder());
/* 213 */       p.addBefore(ctx.name(), "wsdecoder", (ChannelHandler)newWebsocketDecoder());
/* 214 */       encoderName = ctx.name();
/*     */     } else {
/* 216 */       p.replace(ctx.name(), "wsdecoder", (ChannelHandler)newWebsocketDecoder());
/*     */       
/* 218 */       encoderName = p.context(HttpResponseEncoder.class).name();
/* 219 */       p.addBefore(encoderName, "wsencoder", (ChannelHandler)newWebSocketEncoder());
/*     */     } 
/* 221 */     channel.writeAndFlush(response).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/* 224 */             if (future.isSuccess()) {
/* 225 */               ChannelPipeline p = future.channel().pipeline();
/* 226 */               p.remove(encoderName);
/* 227 */               promise.setSuccess();
/*     */             } else {
/* 229 */               promise.setFailure(future.cause());
/*     */             } 
/*     */           }
/*     */         });
/* 233 */     return (ChannelFuture)promise;
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
/*     */   public ChannelFuture handshake(Channel channel, HttpRequest req) {
/* 248 */     return handshake(channel, req, (HttpHeaders)null, channel.newPromise());
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
/*     */   public final ChannelFuture handshake(final Channel channel, HttpRequest req, final HttpHeaders responseHeaders, final ChannelPromise promise) {
/* 270 */     if (req instanceof FullHttpRequest) {
/* 271 */       return handshake(channel, (FullHttpRequest)req, responseHeaders, promise);
/*     */     }
/* 273 */     if (logger.isDebugEnabled()) {
/* 274 */       logger.debug("{} WebSocket version {} server handshake", channel, version());
/*     */     }
/* 276 */     ChannelPipeline p = channel.pipeline();
/* 277 */     ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
/* 278 */     if (ctx == null) {
/*     */       
/* 280 */       ctx = p.context(HttpServerCodec.class);
/* 281 */       if (ctx == null) {
/* 282 */         promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
/*     */         
/* 284 */         return (ChannelFuture)promise;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 291 */     String aggregatorName = "httpAggregator";
/* 292 */     p.addAfter(ctx.name(), aggregatorName, (ChannelHandler)new HttpObjectAggregator(8192));
/* 293 */     p.addAfter(aggregatorName, "handshaker", (ChannelHandler)new SimpleChannelInboundHandler<FullHttpRequest>()
/*     */         {
/*     */           protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception
/*     */           {
/* 297 */             ctx.pipeline().remove((ChannelHandler)this);
/* 298 */             WebSocketServerHandshaker.this.handshake(channel, msg, responseHeaders, promise);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 304 */             ctx.pipeline().remove((ChannelHandler)this);
/* 305 */             promise.tryFailure(cause);
/* 306 */             ctx.fireExceptionCaught(cause);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 312 */             if (!promise.isDone()) {
/* 313 */               promise.tryFailure(new ClosedChannelException());
/*     */             }
/* 315 */             ctx.fireChannelInactive();
/*     */           }
/*     */         });
/*     */     try {
/* 319 */       ctx.fireChannelRead(ReferenceCountUtil.retain(req));
/* 320 */     } catch (Throwable cause) {
/* 321 */       promise.setFailure(cause);
/*     */     } 
/* 323 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest paramFullHttpRequest, HttpHeaders paramHttpHeaders);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
/* 340 */     ObjectUtil.checkNotNull(channel, "channel");
/* 341 */     return close(channel, frame, channel.newPromise());
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
/* 355 */     ObjectUtil.checkNotNull(channel, "channel");
/* 356 */     return channel.writeAndFlush(frame, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String selectSubprotocol(String requestedSubprotocols) {
/* 367 */     if (requestedSubprotocols == null || this.subprotocols.length == 0) {
/* 368 */       return null;
/*     */     }
/*     */     
/* 371 */     String[] requestedSubprotocolArray = requestedSubprotocols.split(",");
/* 372 */     for (String p : requestedSubprotocolArray) {
/* 373 */       String requestedSubprotocol = p.trim();
/*     */       
/* 375 */       for (String supportedSubprotocol : this.subprotocols) {
/* 376 */         if ("*".equals(supportedSubprotocol) || requestedSubprotocol
/* 377 */           .equals(supportedSubprotocol)) {
/* 378 */           this.selectedSubprotocol = requestedSubprotocol;
/* 379 */           return requestedSubprotocol;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 385 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String selectedSubprotocol() {
/* 395 */     return this.selectedSubprotocol;
/*     */   }
/*     */   
/*     */   protected abstract WebSocketFrameDecoder newWebsocketDecoder();
/*     */   
/*     */   protected abstract WebSocketFrameEncoder newWebSocketEncoder();
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */