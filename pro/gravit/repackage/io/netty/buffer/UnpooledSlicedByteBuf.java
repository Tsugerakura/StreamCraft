/*     */ package pro.gravit.repackage.io.netty.buffer;
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
/*     */ class UnpooledSlicedByteBuf
/*     */   extends AbstractUnpooledSlicedByteBuf
/*     */ {
/*     */   UnpooledSlicedByteBuf(AbstractByteBuf buffer, int index, int length) {
/*  24 */     super(buffer, index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/*  29 */     return maxCapacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractByteBuf unwrap() {
/*  34 */     return (AbstractByteBuf)super.unwrap();
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  39 */     return unwrap()._getByte(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  44 */     return unwrap()._getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  49 */     return unwrap()._getShortLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  54 */     return unwrap()._getUnsignedMedium(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  59 */     return unwrap()._getUnsignedMediumLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  64 */     return unwrap()._getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/*  69 */     return unwrap()._getIntLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/*  74 */     return unwrap()._getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/*  79 */     return unwrap()._getLongLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/*  84 */     unwrap()._setByte(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/*  89 */     unwrap()._setShort(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/*  94 */     unwrap()._setShortLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/*  99 */     unwrap()._setMedium(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 104 */     unwrap()._setMediumLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 109 */     unwrap()._setInt(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 114 */     unwrap()._setIntLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 119 */     unwrap()._setLong(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 124 */     unwrap()._setLongLE(idx(index), value);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledSlicedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */