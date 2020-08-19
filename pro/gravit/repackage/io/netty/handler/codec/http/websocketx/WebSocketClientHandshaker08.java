/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketClientHandshaker08
/*     */   extends WebSocketClientHandshaker
/*     */ {
/*  43 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker08.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String expectedChallengeResponseString;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean allowExtensions;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean performMasking;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean allowMaskMismatch;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
/*  72 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false, 10000L);
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
/*     */   public WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
/* 103 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, 10000L);
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
/*     */   public WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
/* 136 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, false);
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
/*     */   WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/* 173 */     super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     
/* 175 */     this.allowExtensions = allowExtensions;
/* 176 */     this.performMasking = performMasking;
/* 177 */     this.allowMaskMismatch = allowMaskMismatch;
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
/*     */   protected FullHttpRequest newHandshakeRequest() {
/* 200 */     URI wsURL = uri();
/*     */ 
/*     */     
/* 203 */     byte[] nonce = WebSocketUtil.randomBytes(16);
/* 204 */     String key = WebSocketUtil.base64(nonce);
/*     */     
/* 206 */     String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/* 207 */     byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
/* 208 */     this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
/*     */     
/* 210 */     if (logger.isDebugEnabled()) {
/* 211 */       logger.debug("WebSocket version 08 client handshake key: {}, expected response: {}", key, this.expectedChallengeResponseString);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 217 */     DefaultFullHttpRequest defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, upgradeUrl(wsURL), Unpooled.EMPTY_BUFFER);
/*     */     
/* 219 */     HttpHeaders headers = defaultFullHttpRequest.headers();
/*     */     
/* 221 */     if (this.customHeaders != null) {
/* 222 */       headers.add(this.customHeaders);
/*     */     }
/*     */     
/* 225 */     headers.set((CharSequence)HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
/* 226 */       .set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
/* 227 */       .set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, key)
/* 228 */       .set((CharSequence)HttpHeaderNames.HOST, websocketHostValue(wsURL));
/*     */     
/* 230 */     if (!headers.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) {
/* 231 */       headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, websocketOriginValue(wsURL));
/*     */     }
/*     */     
/* 234 */     String expectedSubprotocol = expectedSubprotocol();
/* 235 */     if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
/* 236 */       headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
/*     */     }
/*     */     
/* 239 */     headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, "8");
/* 240 */     return (FullHttpRequest)defaultFullHttpRequest;
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
/*     */   protected void verify(FullHttpResponse response) {
/* 262 */     HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
/* 263 */     HttpHeaders headers = response.headers();
/*     */     
/* 265 */     if (!response.status().equals(status)) {
/* 266 */       throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
/*     */     }
/*     */     
/* 269 */     CharSequence upgrade = headers.get((CharSequence)HttpHeaderNames.UPGRADE);
/* 270 */     if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
/* 271 */       throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
/*     */     }
/*     */     
/* 274 */     if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, true)) {
/* 275 */       throw new WebSocketHandshakeException("Invalid handshake response connection: " + headers
/* 276 */           .get(HttpHeaderNames.CONNECTION));
/*     */     }
/*     */     
/* 279 */     CharSequence accept = headers.get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
/* 280 */     if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
/* 281 */       throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", new Object[] { accept, this.expectedChallengeResponseString }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 288 */     return new WebSocket08FrameDecoder(false, this.allowExtensions, maxFramePayloadLength(), this.allowMaskMismatch);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 293 */     return new WebSocket08FrameEncoder(this.performMasking);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker08 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 298 */     super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
/* 299 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientHandshaker08.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */