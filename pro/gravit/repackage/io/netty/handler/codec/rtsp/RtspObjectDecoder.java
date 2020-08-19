/*    */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectDecoder;
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
/*    */ @Deprecated
/*    */ public abstract class RtspObjectDecoder
/*    */   extends HttpObjectDecoder
/*    */ {
/*    */   protected RtspObjectDecoder() {
/* 62 */     this(4096, 8192, 8192);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
/* 69 */     super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false);
/*    */   }
/*    */ 
/*    */   
/*    */   protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
/* 74 */     super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false, validateHeaders);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
/* 81 */     boolean empty = super.isContentAlwaysEmpty(msg);
/* 82 */     if (empty) {
/* 83 */       return true;
/*    */     }
/* 85 */     if (!msg.headers().contains((CharSequence)RtspHeaderNames.CONTENT_LENGTH)) {
/* 86 */       return true;
/*    */     }
/* 88 */     return empty;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspObjectDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */