/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteOrder;
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
/*     */ final class UnreleasableByteBuf
/*     */   extends WrappedByteBuf
/*     */ {
/*     */   private SwappedByteBuf swappedBuf;
/*     */   
/*     */   UnreleasableByteBuf(ByteBuf buf) {
/*  31 */     super((buf instanceof UnreleasableByteBuf) ? buf.unwrap() : buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf order(ByteOrder endianness) {
/*  36 */     if (ObjectUtil.checkNotNull(endianness, "endianness") == order()) {
/*  37 */       return this;
/*     */     }
/*     */     
/*  40 */     SwappedByteBuf swappedBuf = this.swappedBuf;
/*  41 */     if (swappedBuf == null) {
/*  42 */       this.swappedBuf = swappedBuf = new SwappedByteBuf(this);
/*     */     }
/*  44 */     return swappedBuf;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf asReadOnly() {
/*  49 */     return this.buf.isReadOnly() ? this : new UnreleasableByteBuf(this.buf.asReadOnly());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readSlice(int length) {
/*  54 */     return new UnreleasableByteBuf(this.buf.readSlice(length));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf readRetainedSlice(int length) {
/*  62 */     return readSlice(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice() {
/*  67 */     return new UnreleasableByteBuf(this.buf.slice());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice() {
/*  75 */     return slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/*  80 */     return new UnreleasableByteBuf(this.buf.slice(index, length));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice(int index, int length) {
/*  88 */     return slice(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/*  93 */     return new UnreleasableByteBuf(this.buf.duplicate());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf retainedDuplicate() {
/* 101 */     return duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain(int increment) {
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain() {
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch() {
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch(Object hint) {
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 126 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 131 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnreleasableByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */