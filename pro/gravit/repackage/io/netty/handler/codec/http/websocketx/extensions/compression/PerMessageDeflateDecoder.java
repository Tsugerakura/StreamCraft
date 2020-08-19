/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketFrame;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class PerMessageDeflateDecoder
/*    */   extends DeflateDecoder
/*    */ {
/*    */   private boolean compressing;
/*    */   
/*    */   PerMessageDeflateDecoder(boolean noContext) {
/* 41 */     super(noContext, WebSocketExtensionFilter.NEVER_SKIP);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   PerMessageDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
/* 51 */     super(noContext, extensionDecoderFilter);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean acceptInboundMessage(Object msg) throws Exception {
/* 56 */     if (!super.acceptInboundMessage(msg)) {
/* 57 */       return false;
/*    */     }
/*    */     
/* 60 */     WebSocketFrame wsFrame = (WebSocketFrame)msg;
/* 61 */     if (extensionDecoderFilter().mustSkip(wsFrame)) {
/* 62 */       if (this.compressing) {
/* 63 */         throw new IllegalStateException("Cannot skip per message deflate decoder, compression in progress");
/*    */       }
/* 65 */       return false;
/*    */     } 
/*    */     
/* 68 */     return (((wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) && (wsFrame
/* 69 */       .rsv() & 0x4) > 0) || (wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame && this.compressing));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected int newRsv(WebSocketFrame msg) {
/* 75 */     return ((msg.rsv() & 0x4) > 0) ? (msg
/* 76 */       .rsv() ^ 0x4) : msg.rsv();
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean appendFrameTail(WebSocketFrame msg) {
/* 81 */     return msg.isFinalFragment();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
/* 87 */     super.decode(ctx, msg, out);
/*    */     
/* 89 */     if (msg.isFinalFragment()) {
/* 90 */       this.compressing = false;
/* 91 */     } else if (msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) {
/* 92 */       this.compressing = true;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerMessageDeflateDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */