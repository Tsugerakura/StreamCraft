/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageDecoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class WebSocketProtocolHandler
/*    */   extends MessageToMessageDecoder<WebSocketFrame>
/*    */ {
/*    */   private final boolean dropPongFrames;
/*    */   
/*    */   WebSocketProtocolHandler() {
/* 32 */     this(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   WebSocketProtocolHandler(boolean dropPongFrames) {
/* 43 */     this.dropPongFrames = dropPongFrames;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
/* 48 */     if (frame instanceof PingWebSocketFrame) {
/* 49 */       frame.content().retain();
/* 50 */       ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
/* 51 */       readIfNeeded(ctx);
/*    */       return;
/*    */     } 
/* 54 */     if (frame instanceof PongWebSocketFrame && this.dropPongFrames) {
/* 55 */       readIfNeeded(ctx);
/*    */       
/*    */       return;
/*    */     } 
/* 59 */     out.add(frame.retain());
/*    */   }
/*    */   
/*    */   private static void readIfNeeded(ChannelHandlerContext ctx) {
/* 63 */     if (!ctx.channel().config().isAutoRead()) {
/* 64 */       ctx.read();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 70 */     ctx.fireExceptionCaught(cause);
/* 71 */     ctx.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketProtocolHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */