/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContinuationWebSocketFrame
/*     */   extends WebSocketFrame
/*     */ {
/*     */   public ContinuationWebSocketFrame() {
/*  32 */     this(Unpooled.buffer(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame(ByteBuf binaryData) {
/*  42 */     super(binaryData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
/*  56 */     super(finalFragment, rsv, binaryData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame(boolean finalFragment, int rsv, String text) {
/*  70 */     this(finalFragment, rsv, fromText(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String text() {
/*  77 */     return content().toString(CharsetUtil.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ByteBuf fromText(String text) {
/*  87 */     if (text == null || text.isEmpty()) {
/*  88 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/*  90 */     return Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame copy() {
/*  96 */     return (ContinuationWebSocketFrame)super.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame duplicate() {
/* 101 */     return (ContinuationWebSocketFrame)super.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame retainedDuplicate() {
/* 106 */     return (ContinuationWebSocketFrame)super.retainedDuplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame replace(ByteBuf content) {
/* 111 */     return new ContinuationWebSocketFrame(isFinalFragment(), rsv(), content);
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame retain() {
/* 116 */     super.retain();
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame retain(int increment) {
/* 122 */     super.retain(increment);
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame touch() {
/* 128 */     super.touch();
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ContinuationWebSocketFrame touch(Object hint) {
/* 134 */     super.touch(hint);
/* 135 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\ContinuationWebSocketFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */