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
/*     */ final class PooledDuplicatedByteBuf
/*     */   extends AbstractPooledDerivedByteBuf
/*     */ {
/*  34 */   private static final ObjectPool<PooledDuplicatedByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledDuplicatedByteBuf>()
/*     */       {
/*     */         public PooledDuplicatedByteBuf newObject(ObjectPool.Handle<PooledDuplicatedByteBuf> handle)
/*     */         {
/*  38 */           return new PooledDuplicatedByteBuf(handle);
/*     */         }
/*     */       });
/*     */ 
/*     */   
/*     */   static PooledDuplicatedByteBuf newInstance(AbstractByteBuf unwrapped, ByteBuf wrapped, int readerIndex, int writerIndex) {
/*  44 */     PooledDuplicatedByteBuf duplicate = (PooledDuplicatedByteBuf)RECYCLER.get();
/*  45 */     duplicate.init(unwrapped, wrapped, readerIndex, writerIndex, unwrapped.maxCapacity());
/*  46 */     duplicate.markReaderIndex();
/*  47 */     duplicate.markWriterIndex();
/*     */     
/*  49 */     return duplicate;
/*     */   }
/*     */   
/*     */   private PooledDuplicatedByteBuf(ObjectPool.Handle<PooledDuplicatedByteBuf> handle) {
/*  53 */     super((ObjectPool.Handle)handle);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/*  58 */     return unwrap().capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*  63 */     unwrap().capacity(newCapacity);
/*  64 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/*  69 */     return unwrap().arrayOffset();
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/*  74 */     return unwrap().memoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/*  79 */     return unwrap().nioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  84 */     return unwrap().nioBuffers(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/*  89 */     return unwrap().copy(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice(int index, int length) {
/*  94 */     return PooledSlicedByteBuf.newInstance(unwrap(), this, index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/*  99 */     return duplicate0().setIndex(readerIndex(), writerIndex());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedDuplicate() {
/* 104 */     return newInstance(unwrap(), this, readerIndex(), writerIndex());
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 109 */     return unwrap().getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 114 */     return unwrap()._getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 119 */     return unwrap().getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 124 */     return unwrap()._getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 129 */     return unwrap().getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 134 */     return unwrap()._getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 139 */     return unwrap().getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 144 */     return unwrap()._getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 149 */     return unwrap().getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 154 */     return unwrap()._getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 159 */     return unwrap().getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 164 */     return unwrap()._getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 169 */     return unwrap().getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 174 */     return unwrap()._getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 179 */     return unwrap().getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 184 */     return unwrap()._getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 189 */     return unwrap().getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 194 */     return unwrap()._getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 199 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 205 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 211 */     unwrap().getBytes(index, dst);
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 217 */     unwrap().setByte(index, value);
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 223 */     unwrap()._setByte(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 228 */     unwrap().setShort(index, value);
/* 229 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 234 */     unwrap()._setShort(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 239 */     unwrap().setShortLE(index, value);
/* 240 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 245 */     unwrap()._setShortLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 250 */     unwrap().setMedium(index, value);
/* 251 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 256 */     unwrap()._setMedium(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 261 */     unwrap().setMediumLE(index, value);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 267 */     unwrap()._setMediumLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 272 */     unwrap().setInt(index, value);
/* 273 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 278 */     unwrap()._setInt(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 283 */     unwrap().setIntLE(index, value);
/* 284 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 289 */     unwrap()._setIntLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 294 */     unwrap().setLong(index, value);
/* 295 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 300 */     unwrap()._setLong(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 305 */     unwrap().setLongLE(index, value);
/* 306 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 311 */     unwrap().setLongLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 316 */     unwrap().setBytes(index, src, srcIndex, length);
/* 317 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 322 */     unwrap().setBytes(index, src, srcIndex, length);
/* 323 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 328 */     unwrap().setBytes(index, src);
/* 329 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 335 */     unwrap().getBytes(index, out, length);
/* 336 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 342 */     return unwrap().getBytes(index, out, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 348 */     return unwrap().getBytes(index, out, position, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 354 */     return unwrap().setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 360 */     return unwrap().setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 366 */     return unwrap().setBytes(index, in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 371 */     return unwrap().forEachByte(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 376 */     return unwrap().forEachByteDesc(index, length, processor);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledDuplicatedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */