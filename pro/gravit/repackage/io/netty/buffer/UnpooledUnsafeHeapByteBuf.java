/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public class UnpooledUnsafeHeapByteBuf
/*     */   extends UnpooledHeapByteBuf
/*     */ {
/*     */   public UnpooledUnsafeHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/*  34 */     super(alloc, initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] allocateArray(int initialCapacity) {
/*  39 */     return PlatformDependent.allocateUninitializedArray(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/*  44 */     checkIndex(index);
/*  45 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  50 */     return UnsafeByteBufUtil.getByte(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/*  55 */     checkIndex(index, 2);
/*  56 */     return _getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  61 */     return UnsafeByteBufUtil.getShort(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/*  66 */     checkIndex(index, 2);
/*  67 */     return _getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  72 */     return UnsafeByteBufUtil.getShortLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/*  77 */     checkIndex(index, 3);
/*  78 */     return _getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  83 */     return UnsafeByteBufUtil.getUnsignedMedium(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/*  88 */     checkIndex(index, 3);
/*  89 */     return _getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  94 */     return UnsafeByteBufUtil.getUnsignedMediumLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/*  99 */     checkIndex(index, 4);
/* 100 */     return _getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 105 */     return UnsafeByteBufUtil.getInt(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 110 */     checkIndex(index, 4);
/* 111 */     return _getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 116 */     return UnsafeByteBufUtil.getIntLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 121 */     checkIndex(index, 8);
/* 122 */     return _getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 127 */     return UnsafeByteBufUtil.getLong(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 132 */     checkIndex(index, 8);
/* 133 */     return _getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 138 */     return UnsafeByteBufUtil.getLongLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 143 */     checkIndex(index);
/* 144 */     _setByte(index, value);
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 150 */     UnsafeByteBufUtil.setByte(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 155 */     checkIndex(index, 2);
/* 156 */     _setShort(index, value);
/* 157 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 162 */     UnsafeByteBufUtil.setShort(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 167 */     checkIndex(index, 2);
/* 168 */     _setShortLE(index, value);
/* 169 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 174 */     UnsafeByteBufUtil.setShortLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 179 */     checkIndex(index, 3);
/* 180 */     _setMedium(index, value);
/* 181 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 186 */     UnsafeByteBufUtil.setMedium(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 191 */     checkIndex(index, 3);
/* 192 */     _setMediumLE(index, value);
/* 193 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 198 */     UnsafeByteBufUtil.setMediumLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 203 */     checkIndex(index, 4);
/* 204 */     _setInt(index, value);
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 210 */     UnsafeByteBufUtil.setInt(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 215 */     checkIndex(index, 4);
/* 216 */     _setIntLE(index, value);
/* 217 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 222 */     UnsafeByteBufUtil.setIntLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 227 */     checkIndex(index, 8);
/* 228 */     _setLong(index, value);
/* 229 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 234 */     UnsafeByteBufUtil.setLong(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 239 */     checkIndex(index, 8);
/* 240 */     _setLongLE(index, value);
/* 241 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 246 */     UnsafeByteBufUtil.setLongLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setZero(int index, int length) {
/* 251 */     if (PlatformDependent.javaVersion() >= 7) {
/*     */       
/* 253 */       checkIndex(index, length);
/* 254 */       UnsafeByteBufUtil.setZero(this.array, index, length);
/* 255 */       return this;
/*     */     } 
/* 257 */     return super.setZero(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeZero(int length) {
/* 262 */     if (PlatformDependent.javaVersion() >= 7) {
/*     */       
/* 264 */       ensureWritable(length);
/* 265 */       int wIndex = this.writerIndex;
/* 266 */       UnsafeByteBufUtil.setZero(this.array, wIndex, length);
/* 267 */       this.writerIndex = wIndex + length;
/* 268 */       return this;
/*     */     } 
/* 270 */     return super.writeZero(length);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected SwappedByteBuf newSwappedByteBuf() {
/* 276 */     if (PlatformDependent.isUnaligned())
/*     */     {
/* 278 */       return new UnsafeHeapSwappedByteBuf(this);
/*     */     }
/* 280 */     return super.newSwappedByteBuf();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledUnsafeHeapByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */