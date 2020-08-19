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
/*     */ public class WebSocketClientHandshaker13
/*     */   extends WebSocketClientHandshaker
/*     */ {
/*  43 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker13.class);
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
/*     */   public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
/*  72 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
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
/*     */   public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
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
/*     */   
/*     */   public WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
/* 137 */     this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, false);
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
/*     */   WebSocketClientHandshaker13(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/* 174 */     super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     
/* 176 */     this.allowExtensions = allowExtensions;
/* 177 */     this.performMasking = performMasking;
/* 178 */     this.allowMaskMismatch = allowMaskMismatch;
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
/* 201 */     URI wsURL = uri();
/*     */ 
/*     */     
/* 204 */     byte[] nonce = WebSocketUtil.randomBytes(16);
/* 205 */     String key = WebSocketUtil.base64(nonce);
/*     */     
/* 207 */     String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/* 208 */     byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
/* 209 */     this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
/*     */     
/* 211 */     if (logger.isDebugEnabled()) {
/* 212 */       logger.debug("WebSocket version 13 client handshake key: {}, expected response: {}", key, this.expectedChallengeResponseString);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 218 */     DefaultFullHttpRequest defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, upgradeUrl(wsURL), Unpooled.EMPTY_BUFFER);
/*     */     
/* 220 */     HttpHeaders headers = defaultFullHttpRequest.headers();
/*     */     
/* 222 */     if (this.customHeaders != null) {
/* 223 */       headers.add(this.customHeaders);
/*     */     }
/*     */     
/* 226 */     headers.set((CharSequence)HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
/* 227 */       .set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
/* 228 */       .set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, key)
/* 229 */       .set((CharSequence)HttpHeaderNames.HOST, websocketHostValue(wsURL));
/*     */     
/* 231 */     if (!headers.contains((CharSequence)HttpHeaderNames.ORIGIN)) {
/* 232 */       headers.set((CharSequence)HttpHeaderNames.ORIGIN, websocketOriginValue(wsURL));
/*     */     }
/*     */     
/* 235 */     String expectedSubprotocol = expectedSubprotocol();
/* 236 */     if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
/* 237 */       headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
/*     */     }
/*     */     
/* 240 */     headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, "13");
/* 241 */     return (FullHttpRequest)defaultFullHttpRequest;
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
/* 263 */     HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
/* 264 */     HttpHeaders headers = response.headers();
/*     */     
/* 266 */     if (!response.status().equals(status)) {
/* 267 */       throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
/*     */     }
/*     */     
/* 270 */     CharSequence upgrade = headers.get((CharSequence)HttpHeaderNames.UPGRADE);
/* 271 */     if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
/* 272 */       throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
/*     */     }
/*     */     
/* 275 */     if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, true)) {
/* 276 */       throw new WebSocketHandshakeException("Invalid handshake response connection: " + headers
/* 277 */           .get(HttpHeaderNames.CONNECTION));
/*     */     }
/*     */     
/* 280 */     CharSequence accept = headers.get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
/* 281 */     if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
/* 282 */       throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", new Object[] { accept, this.expectedChallengeResponseString }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 289 */     return new WebSocket13FrameDecoder(false, this.allowExtensions, maxFramePayloadLength(), this.allowMaskMismatch);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 294 */     return new WebSocket13FrameEncoder(this.performMasking);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker13 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 299 */     super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
/* 300 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientHandshaker13.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */