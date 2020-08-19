/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpUtil;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
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
/*     */ public class WebSocketServerHandshakerFactory
/*     */ {
/*     */   private final String webSocketURL;
/*     */   private final String subprotocols;
/*     */   private final WebSocketDecoderConfig decoderConfig;
/*     */   
/*     */   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
/*  55 */     this(webSocketURL, subprotocols, allowExtensions, 65536);
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
/*     */   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
/*  75 */     this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
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
/*     */   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
/*  98 */     this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder()
/*  99 */         .allowExtensions(allowExtensions)
/* 100 */         .maxFramePayloadLength(maxFramePayloadLength)
/* 101 */         .allowMaskMismatch(allowMaskMismatch)
/* 102 */         .build());
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
/*     */   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
/* 118 */     this.webSocketURL = webSocketURL;
/* 119 */     this.subprotocols = subprotocols;
/* 120 */     this.decoderConfig = (WebSocketDecoderConfig)ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
/* 131 */     CharSequence version = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION);
/* 132 */     if (version != null) {
/* 133 */       if (version.equals(WebSocketVersion.V13.toHttpHeaderValue()))
/*     */       {
/* 135 */         return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.decoderConfig);
/*     */       }
/* 137 */       if (version.equals(WebSocketVersion.V08.toHttpHeaderValue()))
/*     */       {
/* 139 */         return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.decoderConfig);
/*     */       }
/* 141 */       if (version.equals(WebSocketVersion.V07.toHttpHeaderValue()))
/*     */       {
/* 143 */         return new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.decoderConfig);
/*     */       }
/*     */       
/* 146 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 150 */     return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.decoderConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
/* 159 */     sendUnsupportedVersionResponse(channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel) {
/* 166 */     return sendUnsupportedVersionResponse(channel, channel.newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise) {
/* 175 */     DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED, channel.alloc().buffer(0));
/* 176 */     defaultFullHttpResponse.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
/* 177 */     HttpUtil.setContentLength((HttpMessage)defaultFullHttpResponse, 0L);
/* 178 */     return channel.writeAndFlush(defaultFullHttpResponse, promise);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerHandshakerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */