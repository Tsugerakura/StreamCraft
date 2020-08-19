/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.ReadOnlyBufferException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
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
/*     */ 
/*     */ 
/*     */ class ReadOnlyByteBufferBuf
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*     */   protected final ByteBuffer buffer;
/*     */   private final ByteBufAllocator allocator;
/*     */   private ByteBuffer tmpNioBuf;
/*     */   
/*     */   ReadOnlyByteBufferBuf(ByteBufAllocator allocator, ByteBuffer buffer) {
/*  41 */     super(buffer.remaining());
/*  42 */     if (!buffer.isReadOnly()) {
/*  43 */       throw new IllegalArgumentException("must be a readonly buffer: " + StringUtil.simpleClassName(buffer));
/*     */     }
/*     */     
/*  46 */     this.allocator = allocator;
/*  47 */     this.buffer = buffer.slice().order(ByteOrder.BIG_ENDIAN);
/*  48 */     writerIndex(this.buffer.limit());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {}
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/*  56 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable(int numBytes) {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ensureWritable(int minWritableBytes) {
/*  66 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  71 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/*  76 */     ensureAccessible();
/*  77 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  82 */     return this.buffer.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/*  87 */     ensureAccessible();
/*  88 */     return _getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  93 */     return this.buffer.getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/*  98 */     ensureAccessible();
/*  99 */     return _getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 104 */     return ByteBufUtil.swapShort(this.buffer.getShort(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 109 */     ensureAccessible();
/* 110 */     return _getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 115 */     return (getByte(index) & 0xFF) << 16 | (
/* 116 */       getByte(index + 1) & 0xFF) << 8 | 
/* 117 */       getByte(index + 2) & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 122 */     ensureAccessible();
/* 123 */     return _getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 128 */     return getByte(index) & 0xFF | (
/* 129 */       getByte(index + 1) & 0xFF) << 8 | (
/* 130 */       getByte(index + 2) & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 135 */     ensureAccessible();
/* 136 */     return _getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 141 */     return this.buffer.getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 146 */     ensureAccessible();
/* 147 */     return _getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 152 */     return ByteBufUtil.swapInt(this.buffer.getInt(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 157 */     ensureAccessible();
/* 158 */     return _getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 163 */     return this.buffer.getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 168 */     ensureAccessible();
/* 169 */     return _getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 174 */     return ByteBufUtil.swapLong(this.buffer.getLong(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 179 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 180 */     if (dst.hasArray()) {
/* 181 */       getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
/* 182 */     } else if (dst.nioBufferCount() > 0) {
/* 183 */       for (ByteBuffer bb : dst.nioBuffers(dstIndex, length)) {
/* 184 */         int bbLen = bb.remaining();
/* 185 */         getBytes(index, bb);
/* 186 */         index += bbLen;
/*     */       } 
/*     */     } else {
/* 189 */       dst.setBytes(dstIndex, this, index, length);
/*     */     } 
/* 191 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 196 */     checkDstIndex(index, length, dstIndex, dst.length);
/*     */     
/* 198 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 199 */     tmpBuf.clear().position(index).limit(index + length);
/* 200 */     tmpBuf.get(dst, dstIndex, length);
/* 201 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 206 */     checkIndex(index, dst.remaining());
/*     */     
/* 208 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 209 */     tmpBuf.clear().position(index).limit(index + dst.remaining());
/* 210 */     dst.put(tmpBuf);
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 216 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 221 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 226 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 231 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 236 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 241 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 246 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 251 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 256 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 261 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 266 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 271 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 276 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 281 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 286 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 291 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 296 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 301 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 306 */     return maxCapacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/* 311 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/* 316 */     return this.allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteOrder order() {
/* 321 */     return ByteOrder.BIG_ENDIAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/* 326 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 331 */     return this.buffer.isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 336 */     return this.buffer.isDirect();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 341 */     ensureAccessible();
/* 342 */     if (length == 0) {
/* 343 */       return this;
/*     */     }
/*     */     
/* 346 */     if (this.buffer.hasArray()) {
/* 347 */       out.write(this.buffer.array(), index + this.buffer.arrayOffset(), length);
/*     */     } else {
/* 349 */       byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
/* 350 */       ByteBuffer tmpBuf = internalNioBuffer();
/* 351 */       tmpBuf.clear().position(index);
/* 352 */       tmpBuf.get(tmp, 0, length);
/* 353 */       out.write(tmp, 0, length);
/*     */     } 
/* 355 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 360 */     ensureAccessible();
/* 361 */     if (length == 0) {
/* 362 */       return 0;
/*     */     }
/*     */     
/* 365 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 366 */     tmpBuf.clear().position(index).limit(index + length);
/* 367 */     return out.write(tmpBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 372 */     ensureAccessible();
/* 373 */     if (length == 0) {
/* 374 */       return 0;
/*     */     }
/*     */     
/* 377 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 378 */     tmpBuf.clear().position(index).limit(index + length);
/* 379 */     return out.write(tmpBuf, position);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 384 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 389 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 394 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 399 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 404 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 409 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */   
/*     */   protected final ByteBuffer internalNioBuffer() {
/* 413 */     ByteBuffer tmpNioBuf = this.tmpNioBuf;
/* 414 */     if (tmpNioBuf == null) {
/* 415 */       this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
/*     */     }
/* 417 */     return tmpNioBuf;
/*     */   }
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/*     */     ByteBuffer src;
/* 422 */     ensureAccessible();
/*     */     
/*     */     try {
/* 425 */       src = (ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length);
/* 426 */     } catch (IllegalArgumentException ignored) {
/* 427 */       throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
/*     */     } 
/*     */     
/* 430 */     ByteBuf dst = src.isDirect() ? alloc().directBuffer(length) : alloc().heapBuffer(length);
/* 431 */     dst.writeBytes(src);
/* 432 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 437 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 442 */     return new ByteBuffer[] { nioBuffer(index, length) };
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 447 */     checkIndex(index, length);
/* 448 */     return (ByteBuffer)this.buffer.duplicate().position(index).limit(index + length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 453 */     ensureAccessible();
/* 454 */     return (ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isContiguous() {
/* 459 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 464 */     return this.buffer.hasArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 469 */     return this.buffer.array();
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 474 */     return this.buffer.arrayOffset();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 479 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 484 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ReadOnlyByteBufferBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */