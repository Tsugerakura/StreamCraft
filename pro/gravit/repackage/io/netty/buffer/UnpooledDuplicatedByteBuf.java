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
/*     */ class UnpooledDuplicatedByteBuf
/*     */   extends DuplicatedByteBuf
/*     */ {
/*     */   UnpooledDuplicatedByteBuf(AbstractByteBuf buffer) {
/*  24 */     super(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractByteBuf unwrap() {
/*  29 */     return (AbstractByteBuf)super.unwrap();
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  34 */     return unwrap()._getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  39 */     return unwrap()._getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  44 */     return unwrap()._getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  49 */     return unwrap()._getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  54 */     return unwrap()._getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  59 */     return unwrap()._getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/*  64 */     return unwrap()._getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/*  69 */     return unwrap()._getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/*  74 */     return unwrap()._getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/*  79 */     unwrap()._setByte(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/*  84 */     unwrap()._setShort(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/*  89 */     unwrap()._setShortLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/*  94 */     unwrap()._setMedium(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/*  99 */     unwrap()._setMediumLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 104 */     unwrap()._setInt(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 109 */     unwrap()._setIntLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 114 */     unwrap()._setLong(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 119 */     unwrap()._setLongLE(index, value);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledDuplicatedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */