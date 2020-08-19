/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
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
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketClientHandshaker00
/*     */   extends WebSocketClientHandshaker
/*     */ {
/*  46 */   private static final AsciiString WEBSOCKET = AsciiString.cached("WebSocket");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuf expectedChallengeResponseBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength) {
/*  67 */     this(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, 10000L);
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
/*     */   public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis) {
/*  91 */     this(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, false);
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
/*     */   WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/* 117 */     super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
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
/*     */   protected FullHttpRequest newHandshakeRequest() {
/* 142 */     int spaces1 = WebSocketUtil.randomNumber(1, 12);
/* 143 */     int spaces2 = WebSocketUtil.randomNumber(1, 12);
/*     */     
/* 145 */     int max1 = Integer.MAX_VALUE / spaces1;
/* 146 */     int max2 = Integer.MAX_VALUE / spaces2;
/*     */     
/* 148 */     int number1 = WebSocketUtil.randomNumber(0, max1);
/* 149 */     int number2 = WebSocketUtil.randomNumber(0, max2);
/*     */     
/* 151 */     int product1 = number1 * spaces1;
/* 152 */     int product2 = number2 * spaces2;
/*     */     
/* 154 */     String key1 = Integer.toString(product1);
/* 155 */     String key2 = Integer.toString(product2);
/*     */     
/* 157 */     key1 = insertRandomCharacters(key1);
/* 158 */     key2 = insertRandomCharacters(key2);
/*     */     
/* 160 */     key1 = insertSpaces(key1, spaces1);
/* 161 */     key2 = insertSpaces(key2, spaces2);
/*     */     
/* 163 */     byte[] key3 = WebSocketUtil.randomBytes(8);
/*     */     
/* 165 */     ByteBuffer buffer = ByteBuffer.allocate(4);
/* 166 */     buffer.putInt(number1);
/* 167 */     byte[] number1Array = buffer.array();
/* 168 */     buffer = ByteBuffer.allocate(4);
/* 169 */     buffer.putInt(number2);
/* 170 */     byte[] number2Array = buffer.array();
/*     */     
/* 172 */     byte[] challenge = new byte[16];
/* 173 */     System.arraycopy(number1Array, 0, challenge, 0, 4);
/* 174 */     System.arraycopy(number2Array, 0, challenge, 4, 4);
/* 175 */     System.arraycopy(key3, 0, challenge, 8, 8);
/* 176 */     this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer(WebSocketUtil.md5(challenge));
/*     */     
/* 178 */     URI wsURL = uri();
/*     */ 
/*     */ 
/*     */     
/* 182 */     DefaultFullHttpRequest defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, upgradeUrl(wsURL), Unpooled.wrappedBuffer(key3));
/* 183 */     HttpHeaders headers = defaultFullHttpRequest.headers();
/*     */     
/* 185 */     if (this.customHeaders != null) {
/* 186 */       headers.add(this.customHeaders);
/*     */     }
/*     */     
/* 189 */     headers.set((CharSequence)HttpHeaderNames.UPGRADE, WEBSOCKET)
/* 190 */       .set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
/* 191 */       .set((CharSequence)HttpHeaderNames.HOST, websocketHostValue(wsURL))
/* 192 */       .set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1, key1)
/* 193 */       .set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2, key2);
/*     */     
/* 195 */     if (!headers.contains((CharSequence)HttpHeaderNames.ORIGIN)) {
/* 196 */       headers.set((CharSequence)HttpHeaderNames.ORIGIN, websocketOriginValue(wsURL));
/*     */     }
/*     */     
/* 199 */     String expectedSubprotocol = expectedSubprotocol();
/* 200 */     if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
/* 201 */       headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 206 */     headers.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Integer.valueOf(key3.length));
/* 207 */     return (FullHttpRequest)defaultFullHttpRequest;
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
/*     */   protected void verify(FullHttpResponse response) {
/* 232 */     if (!response.status().equals(HttpResponseStatus.SWITCHING_PROTOCOLS)) {
/* 233 */       throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
/*     */     }
/*     */     
/* 236 */     HttpHeaders headers = response.headers();
/*     */     
/* 238 */     CharSequence upgrade = headers.get((CharSequence)HttpHeaderNames.UPGRADE);
/* 239 */     if (!WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
/* 240 */       throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
/*     */     }
/*     */ 
/*     */     
/* 244 */     if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, true)) {
/* 245 */       throw new WebSocketHandshakeException("Invalid handshake response connection: " + headers
/* 246 */           .get(HttpHeaderNames.CONNECTION));
/*     */     }
/*     */     
/* 249 */     ByteBuf challenge = response.content();
/* 250 */     if (!challenge.equals(this.expectedChallengeResponseBytes)) {
/* 251 */       throw new WebSocketHandshakeException("Invalid challenge");
/*     */     }
/*     */   }
/*     */   
/*     */   private static String insertRandomCharacters(String key) {
/* 256 */     int count = WebSocketUtil.randomNumber(1, 12);
/*     */     
/* 258 */     char[] randomChars = new char[count];
/* 259 */     int randCount = 0;
/* 260 */     while (randCount < count) {
/* 261 */       int rand = (int)(Math.random() * 126.0D + 33.0D);
/* 262 */       if ((33 < rand && rand < 47) || (58 < rand && rand < 126)) {
/* 263 */         randomChars[randCount] = (char)rand;
/* 264 */         randCount++;
/*     */       } 
/*     */     } 
/*     */     
/* 268 */     for (int i = 0; i < count; i++) {
/* 269 */       int split = WebSocketUtil.randomNumber(0, key.length());
/* 270 */       String part1 = key.substring(0, split);
/* 271 */       String part2 = key.substring(split);
/* 272 */       key = part1 + randomChars[i] + part2;
/*     */     } 
/*     */     
/* 275 */     return key;
/*     */   }
/*     */   
/*     */   private static String insertSpaces(String key, int spaces) {
/* 279 */     for (int i = 0; i < spaces; i++) {
/* 280 */       int split = WebSocketUtil.randomNumber(1, key.length() - 1);
/* 281 */       String part1 = key.substring(0, split);
/* 282 */       String part2 = key.substring(split);
/* 283 */       key = part1 + ' ' + part2;
/*     */     } 
/*     */     
/* 286 */     return key;
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 291 */     return new WebSocket00FrameDecoder(maxFramePayloadLength());
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 296 */     return new WebSocket00FrameEncoder();
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketClientHandshaker00 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 301 */     super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
/* 302 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientHandshaker00.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */