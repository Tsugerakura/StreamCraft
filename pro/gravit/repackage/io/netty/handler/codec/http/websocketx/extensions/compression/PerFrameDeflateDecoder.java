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
/*    */ class PerFrameDeflateDecoder
/*    */   extends DeflateDecoder
/*    */ {
/*    */   PerFrameDeflateDecoder(boolean noContext) {
/* 36 */     super(noContext, WebSocketExtensionFilter.NEVER_SKIP);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   PerFrameDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
/* 46 */     super(noContext, extensionDecoderFilter);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean acceptInboundMessage(Object msg) throws Exception {
/* 51 */     if (!super.acceptInboundMessage(msg)) {
/* 52 */       return false;
/*    */     }
/*    */     
/* 55 */     WebSocketFrame wsFrame = (WebSocketFrame)msg;
/* 56 */     if (extensionDecoderFilter().mustSkip(wsFrame)) {
/* 57 */       return false;
/*    */     }
/*    */     
/* 60 */     return ((msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame || msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame) && (wsFrame
/*    */       
/* 62 */       .rsv() & 0x4) > 0);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int newRsv(WebSocketFrame msg) {
/* 67 */     return msg.rsv() ^ 0x4;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean appendFrameTail(WebSocketFrame msg) {
/* 72 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerFrameDeflateDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */