/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
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
/*     */ final class HttpPostBodyUtil
/*     */ {
/*     */   public static final int chunkSize = 8096;
/*     */   public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
/*     */   public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";
/*     */   
/*     */   public enum TransferEncodingMechanism
/*     */   {
/*  49 */     BIT7("7bit"),
/*     */ 
/*     */ 
/*     */     
/*  53 */     BIT8("8bit"),
/*     */ 
/*     */ 
/*     */     
/*  57 */     BINARY("binary");
/*     */     
/*     */     private final String value;
/*     */     
/*     */     TransferEncodingMechanism(String value) {
/*  62 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String value() {
/*  66 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  71 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class SeekAheadOptimize
/*     */   {
/*     */     byte[] bytes;
/*     */ 
/*     */     
/*     */     int readerIndex;
/*     */     
/*     */     int pos;
/*     */     
/*     */     int origPos;
/*     */     
/*     */     int limit;
/*     */     
/*     */     ByteBuf buffer;
/*     */ 
/*     */     
/*     */     SeekAheadOptimize(ByteBuf buffer) {
/*  94 */       if (!buffer.hasArray()) {
/*  95 */         throw new IllegalArgumentException("buffer hasn't backing byte array");
/*     */       }
/*  97 */       this.buffer = buffer;
/*  98 */       this.bytes = buffer.array();
/*  99 */       this.readerIndex = buffer.readerIndex();
/* 100 */       this.origPos = this.pos = buffer.arrayOffset() + this.readerIndex;
/* 101 */       this.limit = buffer.arrayOffset() + buffer.writerIndex();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void setReadPosition(int minus) {
/* 110 */       this.pos -= minus;
/* 111 */       this.readerIndex = getReadPosition(this.pos);
/* 112 */       this.buffer.readerIndex(this.readerIndex);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int getReadPosition(int index) {
/* 121 */       return index - this.origPos + this.readerIndex;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int findNonWhitespace(String sb, int offset) {
/*     */     int result;
/* 131 */     for (result = offset; result < sb.length() && 
/* 132 */       Character.isWhitespace(sb.charAt(result)); result++);
/*     */ 
/*     */ 
/*     */     
/* 136 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int findEndOfString(String sb) {
/*     */     int result;
/* 145 */     for (result = sb.length(); result > 0 && 
/* 146 */       Character.isWhitespace(sb.charAt(result - 1)); result--);
/*     */ 
/*     */ 
/*     */     
/* 150 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpPostBodyUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */