/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
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
/*     */ 
/*     */ public class UnpooledUnsafeDirectByteBuf
/*     */   extends UnpooledDirectByteBuf
/*     */ {
/*     */   long memoryAddress;
/*     */   
/*     */   public UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/*  41 */     super(alloc, initialCapacity, maxCapacity);
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
/*     */ 
/*     */   
/*     */   protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
/*  59 */     super(alloc, initialBuffer, maxCapacity, false, true);
/*     */   }
/*     */   
/*     */   UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree) {
/*  63 */     super(alloc, initialBuffer, maxCapacity, doFree, false);
/*     */   }
/*     */ 
/*     */   
/*     */   final void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
/*  68 */     super.setByteBuffer(buffer, tryFree);
/*  69 */     this.memoryAddress = PlatformDependent.directBufferAddress(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/*  74 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/*  79 */     ensureAccessible();
/*  80 */     return this.memoryAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/*  85 */     checkIndex(index);
/*  86 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  91 */     return UnsafeByteBufUtil.getByte(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/*  96 */     checkIndex(index, 2);
/*  97 */     return _getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 102 */     return UnsafeByteBufUtil.getShort(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 107 */     return UnsafeByteBufUtil.getShortLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 112 */     checkIndex(index, 3);
/* 113 */     return _getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 118 */     return UnsafeByteBufUtil.getUnsignedMedium(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 123 */     return UnsafeByteBufUtil.getUnsignedMediumLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 128 */     checkIndex(index, 4);
/* 129 */     return _getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 134 */     return UnsafeByteBufUtil.getInt(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 139 */     return UnsafeByteBufUtil.getIntLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 144 */     checkIndex(index, 8);
/* 145 */     return _getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 150 */     return UnsafeByteBufUtil.getLong(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 155 */     return UnsafeByteBufUtil.getLongLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 160 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
/* 166 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   void getBytes(int index, ByteBuffer dst, boolean internal) {
/* 171 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 176 */     checkIndex(index);
/* 177 */     _setByte(index, value);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 183 */     UnsafeByteBufUtil.setByte(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 188 */     checkIndex(index, 2);
/* 189 */     _setShort(index, value);
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 195 */     UnsafeByteBufUtil.setShort(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 200 */     UnsafeByteBufUtil.setShortLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 205 */     checkIndex(index, 3);
/* 206 */     _setMedium(index, value);
/* 207 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 212 */     UnsafeByteBufUtil.setMedium(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 217 */     UnsafeByteBufUtil.setMediumLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 222 */     checkIndex(index, 4);
/* 223 */     _setInt(index, value);
/* 224 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 229 */     UnsafeByteBufUtil.setInt(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 234 */     UnsafeByteBufUtil.setIntLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 239 */     checkIndex(index, 8);
/* 240 */     _setLong(index, value);
/* 241 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 246 */     UnsafeByteBufUtil.setLong(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 251 */     UnsafeByteBufUtil.setLongLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 256 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src, srcIndex, length);
/* 257 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 262 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src, srcIndex, length);
/* 263 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 268 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src);
/* 269 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
/* 274 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 279 */     return UnsafeByteBufUtil.setBytes(this, addr(index), index, in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 284 */     return UnsafeByteBufUtil.copy(this, addr(index), index, length);
/*     */   }
/*     */   
/*     */   final long addr(int index) {
/* 288 */     return this.memoryAddress + index;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SwappedByteBuf newSwappedByteBuf() {
/* 293 */     if (PlatformDependent.isUnaligned())
/*     */     {
/* 295 */       return new UnsafeDirectSwappedByteBuf(this);
/*     */     }
/* 297 */     return super.newSwappedByteBuf();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setZero(int index, int length) {
/* 302 */     checkIndex(index, length);
/* 303 */     UnsafeByteBufUtil.setZero(addr(index), length);
/* 304 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeZero(int length) {
/* 309 */     ensureWritable(length);
/* 310 */     int wIndex = this.writerIndex;
/* 311 */     UnsafeByteBufUtil.setZero(addr(wIndex), length);
/* 312 */     this.writerIndex = wIndex + length;
/* 313 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledUnsafeDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */