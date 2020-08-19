/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketServerHandshaker07
/*     */   extends WebSocketServerHandshaker
/*     */ {
/*     */   public static final String WEBSOCKET_07_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/*     */   
/*     */   public WebSocketServerHandshaker07(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
/*  56 */     this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
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
/*     */   public WebSocketServerHandshaker07(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
/*  79 */     this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder()
/*  80 */         .allowExtensions(allowExtensions)
/*  81 */         .maxFramePayloadLength(maxFramePayloadLength)
/*  82 */         .allowMaskMismatch(allowMaskMismatch)
/*  83 */         .build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerHandshaker07(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
/*  93 */     super(WebSocketVersion.V07, webSocketURL, subprotocols, decoderConfig);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
/* 131 */     CharSequence key = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY);
/* 132 */     if (key == null) {
/* 133 */       throw new WebSocketHandshakeException("not a WebSocket request: missing key");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 138 */     DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, req.content().alloc().buffer(0));
/*     */     
/* 140 */     if (headers != null) {
/* 141 */       defaultFullHttpResponse.headers().add(headers);
/*     */     }
/*     */     
/* 144 */     String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/* 145 */     byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
/* 146 */     String accept = WebSocketUtil.base64(sha1);
/*     */     
/* 148 */     if (logger.isDebugEnabled()) {
/* 149 */       logger.debug("WebSocket version 07 server handshake key: {}, response: {}.", key, accept);
/*     */     }
/*     */     
/* 152 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET);
/* 153 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
/* 154 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, accept);
/*     */     
/* 156 */     String subprotocols = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
/* 157 */     if (subprotocols != null) {
/* 158 */       String selectedSubprotocol = selectSubprotocol(subprotocols);
/* 159 */       if (selectedSubprotocol == null) {
/* 160 */         if (logger.isDebugEnabled()) {
/* 161 */           logger.debug("Requested subprotocol(s) not supported: {}", subprotocols);
/*     */         }
/*     */       } else {
/* 164 */         defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
/*     */       } 
/*     */     } 
/* 167 */     return (FullHttpResponse)defaultFullHttpResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 172 */     return new WebSocket07FrameDecoder(decoderConfig());
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 177 */     return new WebSocket07FrameEncoder(false);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerHandshaker07.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */