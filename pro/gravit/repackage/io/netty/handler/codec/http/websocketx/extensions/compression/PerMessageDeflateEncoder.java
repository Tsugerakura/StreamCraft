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
/*    */ 
/*    */ 
/*    */ class PerMessageDeflateEncoder
/*    */   extends DeflateEncoder
/*    */ {
/*    */   private boolean compressing;
/*    */   
/*    */   PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
/* 43 */     super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
/*    */   }
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
/*    */   PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
/* 56 */     super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/* 61 */     if (!super.acceptOutboundMessage(msg)) {
/* 62 */       return false;
/*    */     }
/*    */     
/* 65 */     WebSocketFrame wsFrame = (WebSocketFrame)msg;
/* 66 */     if (extensionEncoderFilter().mustSkip(wsFrame)) {
/* 67 */       if (this.compressing) {
/* 68 */         throw new IllegalStateException("Cannot skip per message deflate encoder, compression in progress");
/*    */       }
/* 70 */       return false;
/*    */     } 
/*    */     
/* 73 */     return (((wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) && (wsFrame
/* 74 */       .rsv() & 0x4) == 0) || (wsFrame instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame && this.compressing));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected int rsv(WebSocketFrame msg) {
/* 80 */     return (msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) ? (msg
/* 81 */       .rsv() | 0x4) : msg.rsv();
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean removeFrameTail(WebSocketFrame msg) {
/* 86 */     return msg.isFinalFragment();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
/* 92 */     super.encode(ctx, msg, out);
/*    */     
/* 94 */     if (msg.isFinalFragment()) {
/* 95 */       this.compressing = false;
/* 96 */     } else if (msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame) {
/* 97 */       this.compressing = true;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerMessageDeflateEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */