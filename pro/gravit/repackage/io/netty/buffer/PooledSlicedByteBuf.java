/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
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
/*     */ final class PooledSlicedByteBuf
/*     */   extends AbstractPooledDerivedByteBuf
/*     */ {
/*  36 */   private static final ObjectPool<PooledSlicedByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledSlicedByteBuf>()
/*     */       {
/*     */         public PooledSlicedByteBuf newObject(ObjectPool.Handle<PooledSlicedByteBuf> handle)
/*     */         {
/*  40 */           return new PooledSlicedByteBuf(handle);
/*     */         }
/*     */       });
/*     */ 
/*     */   
/*     */   static PooledSlicedByteBuf newInstance(AbstractByteBuf unwrapped, ByteBuf wrapped, int index, int length) {
/*  46 */     AbstractUnpooledSlicedByteBuf.checkSliceOutOfBounds(index, length, unwrapped);
/*  47 */     return newInstance0(unwrapped, wrapped, index, length);
/*     */   }
/*     */   int adjustment;
/*     */   
/*     */   private static PooledSlicedByteBuf newInstance0(AbstractByteBuf unwrapped, ByteBuf wrapped, int adjustment, int length) {
/*  52 */     PooledSlicedByteBuf slice = (PooledSlicedByteBuf)RECYCLER.get();
/*  53 */     slice.init(unwrapped, wrapped, 0, length, length);
/*  54 */     slice.discardMarks();
/*  55 */     slice.adjustment = adjustment;
/*     */     
/*  57 */     return slice;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private PooledSlicedByteBuf(ObjectPool.Handle<PooledSlicedByteBuf> handle) {
/*  63 */     super((ObjectPool.Handle)handle);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/*  68 */     return maxCapacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*  73 */     throw new UnsupportedOperationException("sliced buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/*  78 */     return idx(unwrap().arrayOffset());
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/*  83 */     return unwrap().memoryAddress() + this.adjustment;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/*  88 */     checkIndex0(index, length);
/*  89 */     return unwrap().nioBuffer(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  94 */     checkIndex0(index, length);
/*  95 */     return unwrap().nioBuffers(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 100 */     checkIndex0(index, length);
/* 101 */     return unwrap().copy(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/* 106 */     checkIndex0(index, length);
/* 107 */     return super.slice(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice(int index, int length) {
/* 112 */     checkIndex0(index, length);
/* 113 */     return newInstance0(unwrap(), this, idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/* 118 */     return duplicate0().setIndex(idx(readerIndex()), idx(writerIndex()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedDuplicate() {
/* 123 */     return PooledDuplicatedByteBuf.newInstance(unwrap(), this, idx(readerIndex()), idx(writerIndex()));
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 128 */     checkIndex0(index, 1);
/* 129 */     return unwrap().getByte(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 134 */     return unwrap()._getByte(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 139 */     checkIndex0(index, 2);
/* 140 */     return unwrap().getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 145 */     return unwrap()._getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 150 */     checkIndex0(index, 2);
/* 151 */     return unwrap().getShortLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 156 */     return unwrap()._getShortLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 161 */     checkIndex0(index, 3);
/* 162 */     return unwrap().getUnsignedMedium(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 167 */     return unwrap()._getUnsignedMedium(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 172 */     checkIndex0(index, 3);
/* 173 */     return unwrap().getUnsignedMediumLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 178 */     return unwrap()._getUnsignedMediumLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 183 */     checkIndex0(index, 4);
/* 184 */     return unwrap().getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 189 */     return unwrap()._getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 194 */     checkIndex0(index, 4);
/* 195 */     return unwrap().getIntLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 200 */     return unwrap()._getIntLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 205 */     checkIndex0(index, 8);
/* 206 */     return unwrap().getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 211 */     return unwrap()._getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 216 */     checkIndex0(index, 8);
/* 217 */     return unwrap().getLongLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 222 */     return unwrap()._getLongLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 227 */     checkIndex0(index, length);
/* 228 */     unwrap().getBytes(idx(index), dst, dstIndex, length);
/* 229 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 234 */     checkIndex0(index, length);
/* 235 */     unwrap().getBytes(idx(index), dst, dstIndex, length);
/* 236 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 241 */     checkIndex0(index, dst.remaining());
/* 242 */     unwrap().getBytes(idx(index), dst);
/* 243 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 248 */     checkIndex0(index, 1);
/* 249 */     unwrap().setByte(idx(index), value);
/* 250 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 255 */     unwrap()._setByte(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 260 */     checkIndex0(index, 2);
/* 261 */     unwrap().setShort(idx(index), value);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 267 */     unwrap()._setShort(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 272 */     checkIndex0(index, 2);
/* 273 */     unwrap().setShortLE(idx(index), value);
/* 274 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 279 */     unwrap()._setShortLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 284 */     checkIndex0(index, 3);
/* 285 */     unwrap().setMedium(idx(index), value);
/* 286 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 291 */     unwrap()._setMedium(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 296 */     checkIndex0(index, 3);
/* 297 */     unwrap().setMediumLE(idx(index), value);
/* 298 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 303 */     unwrap()._setMediumLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 308 */     checkIndex0(index, 4);
/* 309 */     unwrap().setInt(idx(index), value);
/* 310 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 315 */     unwrap()._setInt(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 320 */     checkIndex0(index, 4);
/* 321 */     unwrap().setIntLE(idx(index), value);
/* 322 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 327 */     unwrap()._setIntLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 332 */     checkIndex0(index, 8);
/* 333 */     unwrap().setLong(idx(index), value);
/* 334 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 339 */     unwrap()._setLong(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 344 */     checkIndex0(index, 8);
/* 345 */     unwrap().setLongLE(idx(index), value);
/* 346 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 351 */     unwrap().setLongLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 356 */     checkIndex0(index, length);
/* 357 */     unwrap().setBytes(idx(index), src, srcIndex, length);
/* 358 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 363 */     checkIndex0(index, length);
/* 364 */     unwrap().setBytes(idx(index), src, srcIndex, length);
/* 365 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 370 */     checkIndex0(index, src.remaining());
/* 371 */     unwrap().setBytes(idx(index), src);
/* 372 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 378 */     checkIndex0(index, length);
/* 379 */     unwrap().getBytes(idx(index), out, length);
/* 380 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 386 */     checkIndex0(index, length);
/* 387 */     return unwrap().getBytes(idx(index), out, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 393 */     checkIndex0(index, length);
/* 394 */     return unwrap().getBytes(idx(index), out, position, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 400 */     checkIndex0(index, length);
/* 401 */     return unwrap().setBytes(idx(index), in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 407 */     checkIndex0(index, length);
/* 408 */     return unwrap().setBytes(idx(index), in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 414 */     checkIndex0(index, length);
/* 415 */     return unwrap().setBytes(idx(index), in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 420 */     checkIndex0(index, length);
/* 421 */     int ret = unwrap().forEachByte(idx(index), length, processor);
/* 422 */     if (ret < this.adjustment) {
/* 423 */       return -1;
/*     */     }
/* 425 */     return ret - this.adjustment;
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 430 */     checkIndex0(index, length);
/* 431 */     int ret = unwrap().forEachByteDesc(idx(index), length, processor);
/* 432 */     if (ret < this.adjustment) {
/* 433 */       return -1;
/*     */     }
/* 435 */     return ret - this.adjustment;
/*     */   }
/*     */   
/*     */   private int idx(int index) {
/* 439 */     return index + this.adjustment;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledSlicedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */