/*     */ package pro.gravit.launcher.request.websockets;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.SimpleChannelInboundHandler;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketClientHandler
/*     */   extends SimpleChannelInboundHandler<Object>
/*     */ {
/*     */   private final WebSocketClientHandshaker handshaker;
/*     */   private final ClientJSONPoint clientJSONPoint;
/*     */   private ChannelPromise handshakeFuture;
/*     */   
/*     */   public WebSocketClientHandler(WebSocketClientHandshaker handshaker, ClientJSONPoint clientJSONPoint) {
/*  28 */     this.handshaker = handshaker;
/*  29 */     this.clientJSONPoint = clientJSONPoint;
/*     */   }
/*     */   
/*     */   public ChannelFuture handshakeFuture() {
/*  33 */     return (ChannelFuture)this.handshakeFuture;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/*  38 */     this.handshakeFuture = ctx.newPromise();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/*  43 */     this.handshaker.handshake(ctx.channel());
/*  44 */     this.clientJSONPoint.onOpen();
/*  45 */     ctx.executor().scheduleWithFixedDelay(() -> ctx.channel().writeAndFlush(new PingWebSocketFrame()), 20L, 20L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/*  53 */     this.clientJSONPoint.onDisconnect();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  58 */     Channel ch = ctx.channel();
/*  59 */     if (!this.handshaker.isHandshakeComplete()) {
/*     */       
/*  61 */       this.handshaker.finishHandshake(ch, (FullHttpResponse)msg);
/*  62 */       this.handshakeFuture.setSuccess();
/*     */       
/*     */       return;
/*     */     } 
/*  66 */     if (msg instanceof FullHttpResponse) {
/*  67 */       FullHttpResponse response = (FullHttpResponse)msg;
/*  68 */       throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response
/*  69 */           .content().toString(CharsetUtil.UTF_8) + ')');
/*     */     } 
/*     */     
/*  72 */     WebSocketFrame frame = (WebSocketFrame)msg;
/*  73 */     if (frame instanceof TextWebSocketFrame) {
/*  74 */       TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
/*  75 */       this.clientJSONPoint.onMessage(textFrame.text());
/*  76 */       if (LogHelper.isDevEnabled()) {
/*  77 */         LogHelper.dev("Message: %s", new Object[] { textFrame.text() });
/*     */       
/*     */       }
/*     */     }
/*  81 */     else if (frame instanceof PingWebSocketFrame) {
/*  82 */       frame.content().retain();
/*  83 */       ch.writeAndFlush(new PongWebSocketFrame(frame.content()), ch.voidPromise());
/*     */     }
/*  85 */     else if (!(frame instanceof PongWebSocketFrame)) {
/*  86 */       if (frame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.CloseWebSocketFrame) {
/*  87 */         ch.close();
/*  88 */       } else if (frame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) {
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/*  97 */     LogHelper.error(cause);
/*     */     
/*  99 */     if (!this.handshakeFuture.isDone()) {
/* 100 */       this.handshakeFuture.setFailure(cause);
/*     */     }
/*     */     
/* 103 */     ctx.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\websockets\WebSocketClientHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */