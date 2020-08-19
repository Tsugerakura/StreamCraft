/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketServerProtocolHandler
/*     */   extends WebSocketProtocolHandler
/*     */ {
/*     */   public enum ServerHandshakeStateEvent
/*     */   {
/*  68 */     HANDSHAKE_COMPLETE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     HANDSHAKE_TIMEOUT;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class HandshakeComplete
/*     */   {
/*     */     private final String requestUri;
/*     */     
/*     */     private final HttpHeaders requestHeaders;
/*     */     private final String selectedSubprotocol;
/*     */     
/*     */     HandshakeComplete(String requestUri, HttpHeaders requestHeaders, String selectedSubprotocol) {
/*  86 */       this.requestUri = requestUri;
/*  87 */       this.requestHeaders = requestHeaders;
/*  88 */       this.selectedSubprotocol = selectedSubprotocol;
/*     */     }
/*     */     
/*     */     public String requestUri() {
/*  92 */       return this.requestUri;
/*     */     }
/*     */     
/*     */     public HttpHeaders requestHeaders() {
/*  96 */       return this.requestHeaders;
/*     */     }
/*     */     
/*     */     public String selectedSubprotocol() {
/* 100 */       return this.selectedSubprotocol;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 105 */   private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final WebSocketServerProtocolConfig serverConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(WebSocketServerProtocolConfig serverConfig) {
/* 116 */     super(((WebSocketServerProtocolConfig)ObjectUtil.checkNotNull(serverConfig, "serverConfig")).dropPongFrames());
/* 117 */     this.serverConfig = serverConfig;
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath) {
/* 121 */     this(websocketPath, WebSocketServerProtocolConfig.DEFAULT.handshakeTimeoutMillis());
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, long handshakeTimeoutMillis) {
/* 125 */     this(websocketPath, false, handshakeTimeoutMillis);
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith) {
/* 129 */     this(websocketPath, checkStartsWith, WebSocketServerProtocolConfig.DEFAULT.handshakeTimeoutMillis());
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith, long handshakeTimeoutMillis) {
/* 133 */     this(websocketPath, (String)null, false, 65536, false, checkStartsWith, handshakeTimeoutMillis);
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols) {
/* 137 */     this(websocketPath, subprotocols, WebSocketServerProtocolConfig.DEFAULT.handshakeTimeoutMillis());
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, long handshakeTimeoutMillis) {
/* 141 */     this(websocketPath, subprotocols, false, handshakeTimeoutMillis);
/*     */   }
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions) {
/* 145 */     this(websocketPath, subprotocols, allowExtensions, WebSocketServerProtocolConfig.DEFAULT.handshakeTimeoutMillis());
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, long handshakeTimeoutMillis) {
/* 150 */     this(websocketPath, subprotocols, allowExtensions, 65536, handshakeTimeoutMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize) {
/* 155 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, WebSocketServerProtocolConfig.DEFAULT.handshakeTimeoutMillis());
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, long handshakeTimeoutMillis) {
/* 160 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, false, handshakeTimeoutMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
/* 165 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, WebSocketServerProtocolConfig.DEFAULT
/* 166 */         .handshakeTimeoutMillis());
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, long handshakeTimeoutMillis) {
/* 171 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, false, handshakeTimeoutMillis);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith) {
/* 177 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, WebSocketServerProtocolConfig.DEFAULT
/* 178 */         .handshakeTimeoutMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, long handshakeTimeoutMillis) {
/* 184 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, true, handshakeTimeoutMillis);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, boolean dropPongFrames) {
/* 191 */     this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, dropPongFrames, WebSocketServerProtocolConfig.DEFAULT
/* 192 */         .handshakeTimeoutMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, boolean dropPongFrames, long handshakeTimeoutMillis) {
/* 198 */     this(websocketPath, subprotocols, checkStartsWith, dropPongFrames, handshakeTimeoutMillis, 
/* 199 */         WebSocketDecoderConfig.newBuilder()
/* 200 */         .maxFramePayloadLength(maxFrameSize)
/* 201 */         .allowMaskMismatch(allowMaskMismatch)
/* 202 */         .allowExtensions(allowExtensions)
/* 203 */         .build());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean checkStartsWith, boolean dropPongFrames, long handshakeTimeoutMillis, WebSocketDecoderConfig decoderConfig) {
/* 209 */     this(WebSocketServerProtocolConfig.newBuilder()
/* 210 */         .websocketPath(websocketPath)
/* 211 */         .subprotocols(subprotocols)
/* 212 */         .checkStartsWith(checkStartsWith)
/* 213 */         .handshakeTimeoutMillis(handshakeTimeoutMillis)
/* 214 */         .dropPongFrames(dropPongFrames)
/* 215 */         .decoderConfig(decoderConfig)
/* 216 */         .build());
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) {
/* 221 */     ChannelPipeline cp = ctx.pipeline();
/* 222 */     if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null)
/*     */     {
/* 224 */       cp.addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), (ChannelHandler)new WebSocketServerProtocolHandshakeHandler(this.serverConfig));
/*     */     }
/*     */     
/* 227 */     if (this.serverConfig.decoderConfig().withUTF8Validator() && cp.get(Utf8FrameValidator.class) == null)
/*     */     {
/* 229 */       cp.addBefore(ctx.name(), Utf8FrameValidator.class.getName(), (ChannelHandler)new Utf8FrameValidator());
/*     */     }
/*     */     
/* 232 */     if (this.serverConfig.sendCloseFrame() != null) {
/* 233 */       cp.addBefore(ctx.name(), WebSocketCloseFrameHandler.class.getName(), (ChannelHandler)new WebSocketCloseFrameHandler(this.serverConfig
/* 234 */             .sendCloseFrame(), this.serverConfig.forceCloseTimeoutMillis()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
/* 240 */     if (this.serverConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
/* 241 */       WebSocketServerHandshaker handshaker = getHandshaker(ctx.channel());
/* 242 */       if (handshaker != null) {
/* 243 */         frame.retain();
/* 244 */         handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame);
/*     */       } else {
/* 246 */         ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */       } 
/*     */       return;
/*     */     } 
/* 250 */     super.decode(ctx, frame, out);
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 255 */     if (cause instanceof WebSocketHandshakeException) {
/*     */       
/* 257 */       DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
/* 258 */       ctx.channel().writeAndFlush(defaultFullHttpResponse).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */     } else {
/* 260 */       ctx.fireExceptionCaught(cause);
/* 261 */       ctx.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   static WebSocketServerHandshaker getHandshaker(Channel channel) {
/* 266 */     return (WebSocketServerHandshaker)channel.attr(HANDSHAKER_ATTR_KEY).get();
/*     */   }
/*     */   
/*     */   static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
/* 270 */     channel.attr(HANDSHAKER_ATTR_KEY).set(handshaker);
/*     */   }
/*     */   
/*     */   static ChannelHandler forbiddenHttpRequestResponder() {
/* 274 */     return (ChannelHandler)new ChannelInboundHandlerAdapter()
/*     */       {
/*     */         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 277 */           if (msg instanceof FullHttpRequest) {
/* 278 */             ((FullHttpRequest)msg).release();
/*     */             
/* 280 */             DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0));
/* 281 */             ctx.channel().writeAndFlush(defaultFullHttpResponse);
/*     */           } else {
/* 283 */             ctx.fireChannelRead(msg);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerProtocolHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */