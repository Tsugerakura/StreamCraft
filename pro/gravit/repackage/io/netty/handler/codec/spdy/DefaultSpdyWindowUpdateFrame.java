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
/*    */ 
/*    */ 
/*    */ public class DefaultSpdyWindowUpdateFrame
/*    */   implements SpdyWindowUpdateFrame
/*    */ {
/*    */   private int streamId;
/*    */   private int deltaWindowSize;
/*    */   
/*    */   public DefaultSpdyWindowUpdateFrame(int streamId, int deltaWindowSize) {
/* 38 */     setStreamId(streamId);
/* 39 */     setDeltaWindowSize(deltaWindowSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public int streamId() {
/* 44 */     return this.streamId;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyWindowUpdateFrame setStreamId(int streamId) {
/* 49 */     ObjectUtil.checkPositiveOrZero(streamId, "streamId");
/* 50 */     this.streamId = streamId;
/* 51 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public int deltaWindowSize() {
/* 56 */     return this.deltaWindowSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyWindowUpdateFrame setDeltaWindowSize(int deltaWindowSize) {
/* 61 */     ObjectUtil.checkPositive(deltaWindowSize, "deltaWindowSize");
/* 62 */     this.deltaWindowSize = deltaWindowSize;
/* 63 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return 
/* 69 */       StringUtil.simpleClassName(this) + StringUtil.NEWLINE + 
/* 70 */       "--> Stream-ID = " + 
/*    */       
/* 72 */       streamId() + StringUtil.NEWLINE + 
/* 73 */       "--> Delta-Window-Size = " + 
/*    */       
/* 75 */       deltaWindowSize();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyWindowUpdateFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */