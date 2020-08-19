/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*    */ 
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
/*    */ class PerFrameDeflateEncoder
/*    */   extends DeflateEncoder
/*    */ {
/*    */   PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
/* 38 */     super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
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
/*    */   PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
/* 51 */     super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/* 56 */     if (!super.acceptOutboundMessage(msg)) {
/* 57 */       return false;
/*    */     }
/*    */     
/* 60 */     WebSocketFrame wsFrame = (WebSocketFrame)msg;
/* 61 */     if (extensionEncoderFilter().mustSkip(wsFrame)) {
/* 62 */       return false;
/*    */     }
/*    */     
/* 65 */     return ((msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame) && wsFrame
/*    */       
/* 67 */       .content().readableBytes() > 0 && (wsFrame
/* 68 */       .rsv() & 0x4) == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int rsv(WebSocketFrame msg) {
/* 73 */     return msg.rsv() | 0x4;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean removeFrameTail(WebSocketFrame msg) {
/* 78 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerFrameDeflateEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */