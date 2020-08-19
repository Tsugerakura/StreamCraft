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
/*    */ 
/*    */ 
/*    */ public class DefaultSpdyRstStreamFrame
/*    */   extends DefaultSpdyStreamFrame
/*    */   implements SpdyRstStreamFrame
/*    */ {
/*    */   private SpdyStreamStatus status;
/*    */   
/*    */   public DefaultSpdyRstStreamFrame(int streamId, int statusCode) {
/* 35 */     this(streamId, SpdyStreamStatus.valueOf(statusCode));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultSpdyRstStreamFrame(int streamId, SpdyStreamStatus status) {
/* 45 */     super(streamId);
/* 46 */     setStatus(status);
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyRstStreamFrame setStreamId(int streamId) {
/* 51 */     super.setStreamId(streamId);
/* 52 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyRstStreamFrame setLast(boolean last) {
/* 57 */     super.setLast(last);
/* 58 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyStreamStatus status() {
/* 63 */     return this.status;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyRstStreamFrame setStatus(SpdyStreamStatus status) {
/* 68 */     this.status = status;
/* 69 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return 
/* 75 */       StringUtil.simpleClassName(this) + StringUtil.NEWLINE + 
/* 76 */       "--> Stream-ID = " + 
/*    */       
/* 78 */       streamId() + StringUtil.NEWLINE + 
/* 79 */       "--> Status: " + 
/*    */       
/* 81 */       status();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyRstStreamFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */