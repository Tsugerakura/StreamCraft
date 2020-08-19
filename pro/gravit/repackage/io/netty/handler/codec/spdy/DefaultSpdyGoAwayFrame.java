/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ public class DefaultSpdyGoAwayFrame
/*    */   implements SpdyGoAwayFrame
/*    */ {
/*    */   private int lastGoodStreamId;
/*    */   private SpdySessionStatus status;
/*    */   
/*    */   public DefaultSpdyGoAwayFrame(int lastGoodStreamId) {
/* 36 */     this(lastGoodStreamId, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultSpdyGoAwayFrame(int lastGoodStreamId, int statusCode) {
/* 46 */     this(lastGoodStreamId, SpdySessionStatus.valueOf(statusCode));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultSpdyGoAwayFrame(int lastGoodStreamId, SpdySessionStatus status) {
/* 56 */     setLastGoodStreamId(lastGoodStreamId);
/* 57 */     setStatus(status);
/*    */   }
/*    */ 
/*    */   
/*    */   public int lastGoodStreamId() {
/* 62 */     return this.lastGoodStreamId;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyGoAwayFrame setLastGoodStreamId(int lastGoodStreamId) {
/* 67 */     ObjectUtil.checkPositiveOrZero(lastGoodStreamId, "lastGoodStreamId");
/* 68 */     this.lastGoodStreamId = lastGoodStreamId;
/* 69 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdySessionStatus status() {
/* 74 */     return this.status;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyGoAwayFrame setStatus(SpdySessionStatus status) {
/* 79 */     this.status = status;
/* 80 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return 
/* 86 */       StringUtil.simpleClassName(this) + StringUtil.NEWLINE + 
/* 87 */       "--> Last-good-stream-ID = " + 
/*    */       
/* 89 */       lastGoodStreamId() + StringUtil.NEWLINE + 
/* 90 */       "--> Status: " + 
/*    */       
/* 92 */       status();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyGoAwayFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */