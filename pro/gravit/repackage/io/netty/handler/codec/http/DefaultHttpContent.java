/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public class DefaultHttpContent
/*     */   extends DefaultHttpObject
/*     */   implements HttpContent
/*     */ {
/*     */   private final ByteBuf content;
/*     */   
/*     */   public DefaultHttpContent(ByteBuf content) {
/*  33 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  38 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent copy() {
/*  43 */     return replace(this.content.copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent duplicate() {
/*  48 */     return replace(this.content.duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent retainedDuplicate() {
/*  53 */     return replace(this.content.retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent replace(ByteBuf content) {
/*  58 */     return new DefaultHttpContent(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  63 */     return this.content.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent retain() {
/*  68 */     this.content.retain();
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent retain(int increment) {
/*  74 */     this.content.retain(increment);
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent touch() {
/*  80 */     this.content.touch();
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent touch(Object hint) {
/*  86 */     this.content.touch(hint);
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/*  92 */     return this.content.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/*  97 */     return this.content.release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 102 */     return StringUtil.simpleClassName(this) + "(data: " + 
/* 103 */       content() + ", decoderResult: " + decoderResult() + ')';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */