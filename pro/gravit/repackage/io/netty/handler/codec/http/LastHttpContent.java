/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
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
/*     */ public interface LastHttpContent
/*     */   extends HttpContent
/*     */ {
/*  30 */   public static final LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent()
/*     */     {
/*     */       public ByteBuf content()
/*     */       {
/*  34 */         return Unpooled.EMPTY_BUFFER;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent copy() {
/*  39 */         return EMPTY_LAST_CONTENT;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent duplicate() {
/*  44 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent replace(ByteBuf content) {
/*  49 */         return new DefaultLastHttpContent(content);
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent retainedDuplicate() {
/*  54 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public HttpHeaders trailingHeaders() {
/*  59 */         return EmptyHttpHeaders.INSTANCE;
/*     */       }
/*     */ 
/*     */       
/*     */       public DecoderResult decoderResult() {
/*  64 */         return DecoderResult.SUCCESS;
/*     */       }
/*     */ 
/*     */       
/*     */       @Deprecated
/*     */       public DecoderResult getDecoderResult() {
/*  70 */         return decoderResult();
/*     */       }
/*     */ 
/*     */       
/*     */       public void setDecoderResult(DecoderResult result) {
/*  75 */         throw new UnsupportedOperationException("read only");
/*     */       }
/*     */ 
/*     */       
/*     */       public int refCnt() {
/*  80 */         return 1;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent retain() {
/*  85 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent retain(int increment) {
/*  90 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent touch() {
/*  95 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public LastHttpContent touch(Object hint) {
/* 100 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean release() {
/* 105 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean release(int decrement) {
/* 110 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 115 */         return "EmptyLastHttpContent";
/*     */       }
/*     */     };
/*     */   
/*     */   HttpHeaders trailingHeaders();
/*     */   
/*     */   LastHttpContent copy();
/*     */   
/*     */   LastHttpContent duplicate();
/*     */   
/*     */   LastHttpContent retainedDuplicate();
/*     */   
/*     */   LastHttpContent replace(ByteBuf paramByteBuf);
/*     */   
/*     */   LastHttpContent retain(int paramInt);
/*     */   
/*     */   LastHttpContent retain();
/*     */   
/*     */   LastHttpContent touch();
/*     */   
/*     */   LastHttpContent touch(Object paramObject);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\LastHttpContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */