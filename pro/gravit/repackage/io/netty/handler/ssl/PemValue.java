/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ class PemValue
/*     */   extends AbstractReferenceCounted
/*     */   implements PemEncoded
/*     */ {
/*     */   private final ByteBuf content;
/*     */   private final boolean sensitive;
/*     */   
/*     */   PemValue(ByteBuf content, boolean sensitive) {
/*  38 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*  39 */     this.sensitive = sensitive;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSensitive() {
/*  44 */     return this.sensitive;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  49 */     int count = refCnt();
/*  50 */     if (count <= 0) {
/*  51 */       throw new IllegalReferenceCountException(count);
/*     */     }
/*     */     
/*  54 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue copy() {
/*  59 */     return replace(this.content.copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue duplicate() {
/*  64 */     return replace(this.content.duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue retainedDuplicate() {
/*  69 */     return replace(this.content.retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue replace(ByteBuf content) {
/*  74 */     return new PemValue(content, this.sensitive);
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue touch() {
/*  79 */     return (PemValue)super.touch();
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue touch(Object hint) {
/*  84 */     this.content.touch(hint);
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue retain() {
/*  90 */     return (PemValue)super.retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public PemValue retain(int increment) {
/*  95 */     return (PemValue)super.retain(increment);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 100 */     if (this.sensitive) {
/* 101 */       SslUtils.zeroout(this.content);
/*     */     }
/* 103 */     this.content.release();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\PemValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */