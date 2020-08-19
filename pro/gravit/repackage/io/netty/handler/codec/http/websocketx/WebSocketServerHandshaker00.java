/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketServerHandshaker00
/*     */   extends WebSocketServerHandshaker
/*     */ {
/*  47 */   private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
/*  48 */   private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, int maxFramePayloadLength) {
/*  63 */     this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder()
/*  64 */         .maxFramePayloadLength(maxFramePayloadLength)
/*  65 */         .build());
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
/*     */   public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
/*  80 */     super(WebSocketVersion.V00, webSocketURL, subprotocols, decoderConfig);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
/* 127 */     if (!req.headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, true) || 
/* 128 */       !HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(req.headers().get((CharSequence)HttpHeaderNames.UPGRADE))) {
/* 129 */       throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 134 */     boolean isHixie76 = (req.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1) && req.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2));
/*     */     
/* 136 */     String origin = req.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
/*     */     
/* 138 */     if (origin == null && !isHixie76) {
/* 139 */       throw new WebSocketHandshakeException("Missing origin header, got only " + req.headers().names());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"), req.content().alloc().buffer(0));
/* 146 */     if (headers != null) {
/* 147 */       defaultFullHttpResponse.headers().add(headers);
/*     */     }
/*     */     
/* 150 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET);
/* 151 */     defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
/*     */ 
/*     */     
/* 154 */     if (isHixie76) {
/*     */       
/* 156 */       defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, origin);
/* 157 */       defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION, uri());
/*     */       
/* 159 */       String subprotocols = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
/* 160 */       if (subprotocols != null) {
/* 161 */         String selectedSubprotocol = selectSubprotocol(subprotocols);
/* 162 */         if (selectedSubprotocol == null) {
/* 163 */           if (logger.isDebugEnabled()) {
/* 164 */             logger.debug("Requested subprotocol(s) not supported: {}", subprotocols);
/*     */           }
/*     */         } else {
/* 167 */           defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 172 */       String key1 = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1);
/* 173 */       String key2 = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2);
/*     */       
/* 175 */       int a = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key1).replaceAll("")) / BEGINNING_SPACE.matcher(key1).replaceAll("").length());
/*     */       
/* 177 */       int b = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(key2).replaceAll("")) / BEGINNING_SPACE.matcher(key2).replaceAll("").length());
/* 178 */       long c = req.content().readLong();
/* 179 */       ByteBuf input = Unpooled.wrappedBuffer(new byte[16]).setIndex(0, 0);
/* 180 */       input.writeInt(a);
/* 181 */       input.writeInt(b);
/* 182 */       input.writeLong(c);
/* 183 */       defaultFullHttpResponse.content().writeBytes(WebSocketUtil.md5(input.array()));
/*     */     } else {
/*     */       
/* 186 */       defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_ORIGIN, origin);
/* 187 */       defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_LOCATION, uri());
/*     */       
/* 189 */       String protocol = req.headers().get((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL);
/* 190 */       if (protocol != null) {
/* 191 */         defaultFullHttpResponse.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL, selectSubprotocol(protocol));
/*     */       }
/*     */     } 
/* 194 */     return (FullHttpResponse)defaultFullHttpResponse;
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
/*     */   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
/* 207 */     return channel.writeAndFlush(frame, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameDecoder newWebsocketDecoder() {
/* 212 */     return new WebSocket00FrameDecoder(decoderConfig());
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketFrameEncoder newWebSocketEncoder() {
/* 217 */     return new WebSocket00FrameEncoder();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerHandshaker00.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */