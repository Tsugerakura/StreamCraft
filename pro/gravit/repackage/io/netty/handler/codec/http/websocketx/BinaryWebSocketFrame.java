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
/*    */ public class BinaryWebSocketFrame
/*    */   extends WebSocketFrame
/*    */ {
/*    */   public BinaryWebSocketFrame() {
/* 30 */     super(Unpooled.buffer(0));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame(ByteBuf binaryData) {
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
/*    */   public BinaryWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
/* 54 */     super(finalFragment, rsv, binaryData);
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame copy() {
/* 59 */     return (BinaryWebSocketFrame)super.copy();
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame duplicate() {
/* 64 */     return (BinaryWebSocketFrame)super.duplicate();
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame retainedDuplicate() {
/* 69 */     return (BinaryWebSocketFrame)super.retainedDuplicate();
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame replace(ByteBuf content) {
/* 74 */     return new BinaryWebSocketFrame(isFinalFragment(), rsv(), content);
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame retain() {
/* 79 */     super.retain();
/* 80 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame retain(int increment) {
/* 85 */     super.retain(increment);
/* 86 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame touch() {
/* 91 */     super.touch();
/* 92 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryWebSocketFrame touch(Object hint) {
/* 97 */     super.touch(hint);
/* 98 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\BinaryWebSocketFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */