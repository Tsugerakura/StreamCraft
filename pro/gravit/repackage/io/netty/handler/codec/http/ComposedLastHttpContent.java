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
/*     */ final class ComposedLastHttpContent
/*     */   implements LastHttpContent
/*     */ {
/*     */   private final HttpHeaders trailingHeaders;
/*     */   private DecoderResult result;
/*     */   
/*     */   ComposedLastHttpContent(HttpHeaders trailingHeaders) {
/*  28 */     this.trailingHeaders = trailingHeaders;
/*     */   }
/*     */   
/*     */   ComposedLastHttpContent(HttpHeaders trailingHeaders, DecoderResult result) {
/*  32 */     this(trailingHeaders);
/*  33 */     this.result = result;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders trailingHeaders() {
/*  38 */     return this.trailingHeaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent copy() {
/*  43 */     LastHttpContent content = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
/*  44 */     content.trailingHeaders().set(trailingHeaders());
/*  45 */     return content;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent duplicate() {
/*  50 */     return copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retainedDuplicate() {
/*  55 */     return copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent replace(ByteBuf content) {
/*  60 */     LastHttpContent dup = new DefaultLastHttpContent(content);
/*  61 */     dup.trailingHeaders().setAll(trailingHeaders());
/*  62 */     return dup;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retain(int increment) {
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retain() {
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent touch() {
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent touch(Object hint) {
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  87 */     return Unpooled.EMPTY_BUFFER;
/*     */   }
/*     */ 
/*     */   
/*     */   public DecoderResult decoderResult() {
/*  92 */     return this.result;
/*     */   }
/*     */ 
/*     */   
/*     */   public DecoderResult getDecoderResult() {
/*  97 */     return decoderResult();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDecoderResult(DecoderResult result) {
/* 102 */     this.result = result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/* 107 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 112 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 117 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\ComposedLastHttpContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */