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
/*    */ public class DefaultSpdyPingFrame
/*    */   implements SpdyPingFrame
/*    */ {
/*    */   private int id;
/*    */   
/*    */   public DefaultSpdyPingFrame(int id) {
/* 33 */     setId(id);
/*    */   }
/*    */ 
/*    */   
/*    */   public int id() {
/* 38 */     return this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public SpdyPingFrame setId(int id) {
/* 43 */     this.id = id;
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return 
/* 50 */       StringUtil.simpleClassName(this) + StringUtil.NEWLINE + 
/* 51 */       "--> ID = " + 
/*    */       
/* 53 */       id();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyPingFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */