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
/*     */ public class CloseWebSocketFrame
/*     */   extends WebSocketFrame
/*     */ {
/*     */   public CloseWebSocketFrame() {
/*  32 */     super(Unpooled.buffer(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame(WebSocketCloseStatus status) {
/*  43 */     this(status.code(), status.reasonText());
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
/*     */   public CloseWebSocketFrame(WebSocketCloseStatus status, String reasonText) {
/*  56 */     this(status.code(), reasonText);
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
/*     */   public CloseWebSocketFrame(int statusCode, String reasonText) {
/*  69 */     this(true, 0, statusCode, reasonText);
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
/*     */   public CloseWebSocketFrame(boolean finalFragment, int rsv) {
/*  81 */     this(finalFragment, rsv, Unpooled.buffer(0));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame(boolean finalFragment, int rsv, int statusCode, String reasonText) {
/*  98 */     super(finalFragment, rsv, newBinaryData(statusCode, reasonText));
/*     */   }
/*     */   
/*     */   private static ByteBuf newBinaryData(int statusCode, String reasonText) {
/* 102 */     if (reasonText == null) {
/* 103 */       reasonText = "";
/*     */     }
/*     */     
/* 106 */     ByteBuf binaryData = Unpooled.buffer(2 + reasonText.length());
/* 107 */     binaryData.writeShort(statusCode);
/* 108 */     if (!reasonText.isEmpty()) {
/* 109 */       binaryData.writeCharSequence(reasonText, CharsetUtil.UTF_8);
/*     */     }
/*     */     
/* 112 */     binaryData.readerIndex(0);
/* 113 */     return binaryData;
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
/*     */   public CloseWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
/* 127 */     super(finalFragment, rsv, binaryData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int statusCode() {
/* 135 */     ByteBuf binaryData = content();
/* 136 */     if (binaryData == null || binaryData.capacity() == 0) {
/* 137 */       return -1;
/*     */     }
/*     */     
/* 140 */     binaryData.readerIndex(0);
/* 141 */     return binaryData.getShort(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String reasonText() {
/* 149 */     ByteBuf binaryData = content();
/* 150 */     if (binaryData == null || binaryData.capacity() <= 2) {
/* 151 */       return "";
/*     */     }
/*     */     
/* 154 */     binaryData.readerIndex(2);
/* 155 */     String reasonText = binaryData.toString(CharsetUtil.UTF_8);
/* 156 */     binaryData.readerIndex(0);
/*     */     
/* 158 */     return reasonText;
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame copy() {
/* 163 */     return (CloseWebSocketFrame)super.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame duplicate() {
/* 168 */     return (CloseWebSocketFrame)super.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame retainedDuplicate() {
/* 173 */     return (CloseWebSocketFrame)super.retainedDuplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame replace(ByteBuf content) {
/* 178 */     return new CloseWebSocketFrame(isFinalFragment(), rsv(), content);
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame retain() {
/* 183 */     super.retain();
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame retain(int increment) {
/* 189 */     super.retain(increment);
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame touch() {
/* 195 */     super.touch();
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseWebSocketFrame touch(Object hint) {
/* 201 */     super.touch(hint);
/* 202 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\CloseWebSocketFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */