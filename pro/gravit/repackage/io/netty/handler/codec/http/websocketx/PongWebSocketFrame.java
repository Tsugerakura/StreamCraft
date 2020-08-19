/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*    */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
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
/*    */ public class PongWebSocketFrame
/*    */   extends WebSocketFrame
/*    */ {
/*    */   public PongWebSocketFrame() {
/* 30 */     super(Unpooled.buffer(0));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame(ByteBuf binaryData) {
/* 40 */     super(binaryData);
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
/*    */   public PongWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
/* 54 */     super(finalFragment, rsv, binaryData);
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame copy() {
/* 59 */     return (PongWebSocketFrame)super.copy();
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame duplicate() {
/* 64 */     return (PongWebSocketFrame)super.duplicate();
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame retainedDuplicate() {
/* 69 */     return (PongWebSocketFrame)super.retainedDuplicate();
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame replace(ByteBuf content) {
/* 74 */     return new PongWebSocketFrame(isFinalFragment(), rsv(), content);
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame retain() {
/* 79 */     super.retain();
/* 80 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame retain(int increment) {
/* 85 */     super.retain(increment);
/* 86 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame touch() {
/* 91 */     super.touch();
/* 92 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public PongWebSocketFrame touch(Object hint) {
/* 97 */     super.touch(hint);
/* 98 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\PongWebSocketFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */