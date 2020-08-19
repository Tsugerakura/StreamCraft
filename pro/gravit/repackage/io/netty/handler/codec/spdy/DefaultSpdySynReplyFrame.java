/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*    */ public class DefaultSpdySynReplyFrame
/*    */   extends DefaultSpdyHeadersFrame
/*    */   implements SpdySynReplyFrame
/*    */ {
/*    */   public DefaultSpdySynReplyFrame(int streamId) {
/* 31 */     super(streamId);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultSpdySynReplyFrame(int streamId, boolean validateHeaders) {
/* 41 */     super(streamId, validateHeaders);
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdySynReplyFrame setStreamId(int streamId) {
/* 46 */     super.setStreamId(streamId);
/* 47 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdySynReplyFrame setLast(boolean last) {
/* 52 */     super.setLast(last);
/* 53 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdySynReplyFrame setInvalid() {
/* 58 */     super.setInvalid();
/* 59 */     return this;
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
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append("(last: ").append(isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(streamId()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
/* 75 */     appendHeaders(buf);
/*    */ 
/*    */     
/* 78 */     buf.setLength(buf.length() - StringUtil.NEWLINE.length());
/* 79 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdySynReplyFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */