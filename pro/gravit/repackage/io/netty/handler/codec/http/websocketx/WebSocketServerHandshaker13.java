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
/*     */ public class WebSocketServerHandshaker13
/*     */   extends WebSocketServerHandshaker
/*     */ {
/*     */   public static final String WEBSOCKET_13_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/*     */   
/*     */   public WebSocketServerHandshaker13(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
/*  55 */     this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
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
/*     */   public WebSocketServerHandshaker13(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
/*  78 */     this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder()
/*  79 */         .allowExtensions(allowExtensions)
/*  80 */         .maxFramePayloadLength(maxFramePayloadLength)
/*  81 */         .allowMaskMismatch(allowMaskMismatch)
/*  82 */         .build());
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
/*     */   public WebSocketServerHandshaker13(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
/*  98 */     super(WebSocketVersion.V13, webSocketURL, subprotocols, decoderConfig);
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
/*     */   
/*     */   protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
/* 137 */     CharSequence key = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY);
/* 138 */     if (key == null) {
/* 139 */       throw new WebSocketHandshakeException("not a WebSocket request: missing key");
/*     */     }
/*     */ 
/*     */     
/* 143 */     DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, req.content().alloc().buffer(0));
/* 144 */     if (headers != null) {
/* 145 */       defaultFullHttpResponse.headers().add(headers);
/*     */     }
/*     */     
/* 148 */     String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/* 149 */     byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
/* 150 */     String accept = WebSocketUtil.base64(sha1);
/*     */     
/* 152 */     if (logger.isDebugEnabled()) {
/* 153 */       logger.debug("WebSocket version 13 server handshake key: {}, response: {}", key, accept);
/*     */     }
/*     */     
/* 156 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET);
/* 157 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
/* 158 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, accept);
/*     */     
/* 160 */     String subprotocols = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
/* 161 */     if (subprotocols != null) {
/* 162 */       String selectedSubprotocol = selectSubprotocol(subprotocols);
/* 163 */       if (selectedSubprotocol == null) {
/* 164 */         if (logger.isDebugEnabled()) {
/* 165 */           logger.debug("Requested subprotocol(s) not supported: {}", subprotocols);
/*     */         }
/*     */       } else {
/* 168 */         defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
/*     */       } 
/*     */     } 
/* 171 */     return (FullHttpResponse)defaultFullHttpResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 176 */     return new WebSocket13FrameDecoder(decoderConfig());
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 181 */     return new WebSocket13FrameEncoder(false);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerHandshaker13.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */