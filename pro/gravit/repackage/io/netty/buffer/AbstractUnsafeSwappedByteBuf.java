/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteOrder;
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
/*     */ abstract class AbstractUnsafeSwappedByteBuf
/*     */   extends SwappedByteBuf
/*     */ {
/*     */   private final boolean nativeByteOrder;
/*     */   private final AbstractByteBuf wrapped;
/*     */   
/*     */   AbstractUnsafeSwappedByteBuf(AbstractByteBuf buf) {
/*  32 */     super(buf);
/*  33 */     assert PlatformDependent.isUnaligned();
/*  34 */     this.wrapped = buf;
/*  35 */     this.nativeByteOrder = (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER == ((order() == ByteOrder.BIG_ENDIAN)));
/*     */   }
/*     */ 
/*     */   
/*     */   public final long getLong(int index) {
/*  40 */     this.wrapped.checkIndex(index, 8);
/*  41 */     long v = _getLong(this.wrapped, index);
/*  42 */     return this.nativeByteOrder ? v : Long.reverseBytes(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final float getFloat(int index) {
/*  47 */     return Float.intBitsToFloat(getInt(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public final double getDouble(int index) {
/*  52 */     return Double.longBitsToDouble(getLong(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public final char getChar(int index) {
/*  57 */     return (char)getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public final long getUnsignedInt(int index) {
/*  62 */     return getInt(index) & 0xFFFFFFFFL;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getInt(int index) {
/*  67 */     this.wrapped.checkIndex(index, 4);
/*  68 */     int v = _getInt(this.wrapped, index);
/*  69 */     return this.nativeByteOrder ? v : Integer.reverseBytes(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getUnsignedShort(int index) {
/*  74 */     return getShort(index) & 0xFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public final short getShort(int index) {
/*  79 */     this.wrapped.checkIndex(index, 2);
/*  80 */     short v = _getShort(this.wrapped, index);
/*  81 */     return this.nativeByteOrder ? v : Short.reverseBytes(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setShort(int index, int value) {
/*  86 */     this.wrapped.checkIndex(index, 2);
/*  87 */     _setShort(this.wrapped, index, this.nativeByteOrder ? (short)value : Short.reverseBytes((short)value));
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setInt(int index, int value) {
/*  93 */     this.wrapped.checkIndex(index, 4);
/*  94 */     _setInt(this.wrapped, index, this.nativeByteOrder ? value : Integer.reverseBytes(value));
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setLong(int index, long value) {
/* 100 */     this.wrapped.checkIndex(index, 8);
/* 101 */     _setLong(this.wrapped, index, this.nativeByteOrder ? value : Long.reverseBytes(value));
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setChar(int index, int value) {
/* 107 */     setShort(index, value);
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setFloat(int index, float value) {
/* 113 */     setInt(index, Float.floatToRawIntBits(value));
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setDouble(int index, double value) {
/* 119 */     setLong(index, Double.doubleToRawLongBits(value));
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeShort(int value) {
/* 125 */     this.wrapped.ensureWritable0(2);
/* 126 */     _setShort(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? (short)value : Short.reverseBytes((short)value));
/* 127 */     this.wrapped.writerIndex += 2;
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeInt(int value) {
/* 133 */     this.wrapped.ensureWritable0(4);
/* 134 */     _setInt(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? value : Integer.reverseBytes(value));
/* 135 */     this.wrapped.writerIndex += 4;
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeLong(long value) {
/* 141 */     this.wrapped.ensureWritable0(8);
/* 142 */     _setLong(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? value : Long.reverseBytes(value));
/* 143 */     this.wrapped.writerIndex += 8;
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeChar(int value) {
/* 149 */     writeShort(value);
/* 150 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeFloat(float value) {
/* 155 */     writeInt(Float.floatToRawIntBits(value));
/* 156 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf writeDouble(double value) {
/* 161 */     writeLong(Double.doubleToRawLongBits(value));
/* 162 */     return this;
/*     */   }
/*     */   
/*     */   protected abstract short _getShort(AbstractByteBuf paramAbstractByteBuf, int paramInt);
/*     */   
/*     */   protected abstract int _getInt(AbstractByteBuf paramAbstractByteBuf, int paramInt);
/*     */   
/*     */   protected abstract long _getLong(AbstractByteBuf paramAbstractByteBuf, int paramInt);
/*     */   
/*     */   protected abstract void _setShort(AbstractByteBuf paramAbstractByteBuf, int paramInt, short paramShort);
/*     */   
/*     */   protected abstract void _setInt(AbstractByteBuf paramAbstractByteBuf, int paramInt1, int paramInt2);
/*     */   
/*     */   protected abstract void _setLong(AbstractByteBuf paramAbstractByteBuf, int paramInt, long paramLong);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractUnsafeSwappedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */