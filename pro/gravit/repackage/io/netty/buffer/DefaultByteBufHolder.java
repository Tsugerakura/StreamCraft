/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
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
/*     */ public class DefaultByteBufHolder
/*     */   implements ByteBufHolder
/*     */ {
/*     */   private final ByteBuf data;
/*     */   
/*     */   public DefaultByteBufHolder(ByteBuf data) {
/*  31 */     this.data = (ByteBuf)ObjectUtil.checkNotNull(data, "data");
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  36 */     if (this.data.refCnt() <= 0) {
/*  37 */       throw new IllegalReferenceCountException(this.data.refCnt());
/*     */     }
/*  39 */     return this.data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufHolder copy() {
/*  49 */     return replace(this.data.copy());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufHolder duplicate() {
/*  59 */     return replace(this.data.duplicate());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufHolder retainedDuplicate() {
/*  69 */     return replace(this.data.retainedDuplicate());
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
/*     */   public ByteBufHolder replace(ByteBuf content) {
/*  81 */     return new DefaultByteBufHolder(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  86 */     return this.data.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufHolder retain() {
/*  91 */     this.data.retain();
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufHolder retain(int increment) {
/*  97 */     this.data.retain(increment);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufHolder touch() {
/* 103 */     this.data.touch();
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufHolder touch(Object hint) {
/* 109 */     this.data.touch(hint);
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 115 */     return this.data.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 120 */     return this.data.release(decrement);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String contentToString() {
/* 128 */     return this.data.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 133 */     return StringUtil.simpleClassName(this) + '(' + contentToString() + ')';
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
/*     */   public boolean equals(Object o) {
/* 149 */     if (this == o) {
/* 150 */       return true;
/*     */     }
/* 152 */     if (o != null && getClass() == o.getClass()) {
/* 153 */       return this.data.equals(((DefaultByteBufHolder)o).data);
/*     */     }
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 160 */     return this.data.hashCode();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\DefaultByteBufHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */