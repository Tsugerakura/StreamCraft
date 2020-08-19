/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
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
/*     */ public class WebSocketClientProtocolHandler
/*     */   extends WebSocketProtocolHandler
/*     */ {
/*     */   private final WebSocketClientHandshaker handshaker;
/*     */   private final WebSocketClientProtocolConfig clientConfig;
/*     */   
/*     */   public WebSocketClientHandshaker handshaker() {
/*  51 */     return this.handshaker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ClientHandshakeStateEvent
/*     */   {
/*  61 */     HANDSHAKE_TIMEOUT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     HANDSHAKE_ISSUED,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     HANDSHAKE_COMPLETE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientProtocolHandler(WebSocketClientProtocolConfig clientConfig) {
/*  81 */     super(((WebSocketClientProtocolConfig)ObjectUtil.checkNotNull(clientConfig, "clientConfig")).dropPongFrames());
/*  82 */     this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(clientConfig
/*  83 */         .webSocketUri(), clientConfig
/*  84 */         .version(), clientConfig
/*  85 */         .subprotocol(), clientConfig
/*  86 */         .allowExtensions(), clientConfig
/*  87 */         .customHeaders(), clientConfig
/*  88 */         .maxFramePayloadLength(), clientConfig
/*  89 */         .performMasking(), clientConfig
/*  90 */         .allowMaskMismatch(), clientConfig
/*  91 */         .forceCloseTimeoutMillis(), clientConfig
/*  92 */         .absoluteUpgradeUrl());
/*     */     
/*  94 */     this.clientConfig = clientConfig;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, boolean performMasking, boolean allowMaskMismatch) {
/* 125 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, performMasking, allowMaskMismatch, WebSocketClientProtocolConfig.DEFAULT
/* 126 */         .handshakeTimeoutMillis());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, boolean performMasking, boolean allowMaskMismatch, long handshakeTimeoutMillis) {
/* 160 */     this(WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch), handleCloseFrames, handshakeTimeoutMillis);
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
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames) {
/* 186 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, WebSocketClientProtocolConfig.DEFAULT
/* 187 */         .handshakeTimeoutMillis());
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
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, long handshakeTimeoutMillis) {
/* 213 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, WebSocketClientProtocolConfig.DEFAULT
/* 214 */         .performMasking(), WebSocketClientProtocolConfig.DEFAULT.allowMaskMismatch(), handshakeTimeoutMillis);
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
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
/* 235 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, WebSocketClientProtocolConfig.DEFAULT
/* 236 */         .handshakeTimeoutMillis());
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
/*     */   public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, long handshakeTimeoutMillis) {
/* 260 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, WebSocketClientProtocolConfig.DEFAULT
/* 261 */         .handleCloseFrames(), handshakeTimeoutMillis);
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
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames) {
/* 274 */     this(handshaker, handleCloseFrames, WebSocketClientProtocolConfig.DEFAULT.handshakeTimeoutMillis());
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
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, long handshakeTimeoutMillis) {
/* 291 */     this(handshaker, handleCloseFrames, WebSocketClientProtocolConfig.DEFAULT.dropPongFrames(), handshakeTimeoutMillis);
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
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, boolean dropPongFrames) {
/* 307 */     this(handshaker, handleCloseFrames, dropPongFrames, WebSocketClientProtocolConfig.DEFAULT.handshakeTimeoutMillis());
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
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, boolean dropPongFrames, long handshakeTimeoutMillis) {
/* 326 */     super(dropPongFrames);
/* 327 */     this.handshaker = handshaker;
/* 328 */     this
/*     */ 
/*     */       
/* 331 */       .clientConfig = WebSocketClientProtocolConfig.newBuilder().handleCloseFrames(handleCloseFrames).handshakeTimeoutMillis(handshakeTimeoutMillis).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker) {
/* 342 */     this(handshaker, WebSocketClientProtocolConfig.DEFAULT.handshakeTimeoutMillis());
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
/*     */   public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, long handshakeTimeoutMillis) {
/* 356 */     this(handshaker, WebSocketClientProtocolConfig.DEFAULT.handleCloseFrames(), handshakeTimeoutMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
/* 361 */     if (this.clientConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
/* 362 */       ctx.close();
/*     */       return;
/*     */     } 
/* 365 */     super.decode(ctx, frame, out);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) {
/* 370 */     ChannelPipeline cp = ctx.pipeline();
/* 371 */     if (cp.get(WebSocketClientProtocolHandshakeHandler.class) == null)
/*     */     {
/* 373 */       ctx.pipeline().addBefore(ctx.name(), WebSocketClientProtocolHandshakeHandler.class.getName(), (ChannelHandler)new WebSocketClientProtocolHandshakeHandler(this.handshaker, this.clientConfig
/* 374 */             .handshakeTimeoutMillis()));
/*     */     }
/* 376 */     if (cp.get(Utf8FrameValidator.class) == null)
/*     */     {
/* 378 */       ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(), (ChannelHandler)new Utf8FrameValidator());
/*     */     }
/*     */     
/* 381 */     if (this.clientConfig.sendCloseFrame() != null)
/* 382 */       cp.addBefore(ctx.name(), WebSocketCloseFrameHandler.class.getName(), (ChannelHandler)new WebSocketCloseFrameHandler(this.clientConfig
/* 383 */             .sendCloseFrame(), this.clientConfig.forceCloseTimeoutMillis())); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientProtocolHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */