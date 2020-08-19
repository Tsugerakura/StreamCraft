/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ 
/*     */ public class UnpooledHeapByteBuf
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*     */   private final ByteBufAllocator alloc;
/*     */   byte[] array;
/*     */   private ByteBuffer tmpNioBuf;
/*     */   
/*     */   public UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/*  51 */     super(maxCapacity);
/*     */     
/*  53 */     if (initialCapacity > maxCapacity) {
/*  54 */       throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[] {
/*  55 */               Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)
/*     */             }));
/*     */     }
/*  58 */     this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull(alloc, "alloc");
/*  59 */     setArray(allocateArray(initialCapacity));
/*  60 */     setIndex(0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity) {
/*  70 */     super(maxCapacity);
/*     */     
/*  72 */     ObjectUtil.checkNotNull(alloc, "alloc");
/*  73 */     ObjectUtil.checkNotNull(initialArray, "initialArray");
/*  74 */     if (initialArray.length > maxCapacity) {
/*  75 */       throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[] {
/*  76 */               Integer.valueOf(initialArray.length), Integer.valueOf(maxCapacity)
/*     */             }));
/*     */     }
/*  79 */     this.alloc = alloc;
/*  80 */     setArray(initialArray);
/*  81 */     setIndex(0, initialArray.length);
/*     */   }
/*     */   
/*     */   protected byte[] allocateArray(int initialCapacity) {
/*  85 */     return new byte[initialCapacity];
/*     */   }
/*     */ 
/*     */   
/*     */   protected void freeArray(byte[] array) {}
/*     */ 
/*     */   
/*     */   private void setArray(byte[] initialArray) {
/*  93 */     this.array = initialArray;
/*  94 */     this.tmpNioBuf = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/*  99 */     return this.alloc;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteOrder order() {
/* 104 */     return ByteOrder.BIG_ENDIAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 114 */     return this.array.length;
/*     */   }
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*     */     int bytesToCopy;
/* 119 */     checkNewCapacity(newCapacity);
/* 120 */     byte[] oldArray = this.array;
/* 121 */     int oldCapacity = oldArray.length;
/* 122 */     if (newCapacity == oldCapacity) {
/* 123 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 127 */     if (newCapacity > oldCapacity) {
/* 128 */       bytesToCopy = oldCapacity;
/*     */     } else {
/* 130 */       trimIndicesToCapacity(newCapacity);
/* 131 */       bytesToCopy = newCapacity;
/*     */     } 
/* 133 */     byte[] newArray = allocateArray(newCapacity);
/* 134 */     System.arraycopy(oldArray, 0, newArray, 0, bytesToCopy);
/* 135 */     setArray(newArray);
/* 136 */     freeArray(oldArray);
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 147 */     ensureAccessible();
/* 148 */     return this.array;
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 153 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 163 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 168 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 169 */     if (dst.hasMemoryAddress()) {
/* 170 */       PlatformDependent.copyMemory(this.array, index, dst.memoryAddress() + dstIndex, length);
/* 171 */     } else if (dst.hasArray()) {
/* 172 */       getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
/*     */     } else {
/* 174 */       dst.setBytes(dstIndex, this.array, index, length);
/*     */     } 
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 181 */     checkDstIndex(index, length, dstIndex, dst.length);
/* 182 */     System.arraycopy(this.array, index, dst, dstIndex, length);
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 188 */     ensureAccessible();
/* 189 */     dst.put(this.array, index, dst.remaining());
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 195 */     ensureAccessible();
/* 196 */     out.write(this.array, index, length);
/* 197 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 202 */     ensureAccessible();
/* 203 */     return getBytes(index, out, length, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 208 */     ensureAccessible();
/* 209 */     return getBytes(index, out, position, length, false);
/*     */   }
/*     */   private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
/*     */     ByteBuffer tmpBuf;
/* 213 */     ensureAccessible();
/*     */     
/* 215 */     if (internal) {
/* 216 */       tmpBuf = internalNioBuffer();
/*     */     } else {
/* 218 */       tmpBuf = ByteBuffer.wrap(this.array);
/*     */     } 
/* 220 */     return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length));
/*     */   }
/*     */   
/*     */   private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
/* 224 */     ensureAccessible();
/* 225 */     ByteBuffer tmpBuf = internal ? internalNioBuffer() : ByteBuffer.wrap(this.array);
/* 226 */     return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length), position);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/* 231 */     checkReadableBytes(length);
/* 232 */     int readBytes = getBytes(this.readerIndex, out, length, true);
/* 233 */     this.readerIndex += readBytes;
/* 234 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/* 239 */     checkReadableBytes(length);
/* 240 */     int readBytes = getBytes(this.readerIndex, out, position, length, true);
/* 241 */     this.readerIndex += readBytes;
/* 242 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 247 */     checkSrcIndex(index, length, srcIndex, src.capacity());
/* 248 */     if (src.hasMemoryAddress()) {
/* 249 */       PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, this.array, index, length);
/* 250 */     } else if (src.hasArray()) {
/* 251 */       setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
/*     */     } else {
/* 253 */       src.getBytes(srcIndex, this.array, index, length);
/*     */     } 
/* 255 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 260 */     checkSrcIndex(index, length, srcIndex, src.length);
/* 261 */     System.arraycopy(src, srcIndex, this.array, index, length);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 267 */     ensureAccessible();
/* 268 */     src.get(this.array, index, src.remaining());
/* 269 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 274 */     ensureAccessible();
/* 275 */     return in.read(this.array, index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 280 */     ensureAccessible();
/*     */     try {
/* 282 */       return in.read((ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length));
/* 283 */     } catch (ClosedChannelException ignored) {
/* 284 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 290 */     ensureAccessible();
/*     */     try {
/* 292 */       return in.read((ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length), position);
/* 293 */     } catch (ClosedChannelException ignored) {
/* 294 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 300 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 305 */     ensureAccessible();
/* 306 */     return ByteBuffer.wrap(this.array, index, length).slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 311 */     return new ByteBuffer[] { nioBuffer(index, length) };
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 316 */     checkIndex(index, length);
/* 317 */     return (ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isContiguous() {
/* 322 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 327 */     ensureAccessible();
/* 328 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 333 */     return HeapByteBufUtil.getByte(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 338 */     ensureAccessible();
/* 339 */     return _getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 344 */     return HeapByteBufUtil.getShort(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 349 */     ensureAccessible();
/* 350 */     return _getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 355 */     return HeapByteBufUtil.getShortLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 360 */     ensureAccessible();
/* 361 */     return _getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 366 */     return HeapByteBufUtil.getUnsignedMedium(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 371 */     ensureAccessible();
/* 372 */     return _getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 377 */     return HeapByteBufUtil.getUnsignedMediumLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 382 */     ensureAccessible();
/* 383 */     return _getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 388 */     return HeapByteBufUtil.getInt(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 393 */     ensureAccessible();
/* 394 */     return _getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 399 */     return HeapByteBufUtil.getIntLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 404 */     ensureAccessible();
/* 405 */     return _getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 410 */     return HeapByteBufUtil.getLong(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 415 */     ensureAccessible();
/* 416 */     return _getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 421 */     return HeapByteBufUtil.getLongLE(this.array, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 426 */     ensureAccessible();
/* 427 */     _setByte(index, value);
/* 428 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 433 */     HeapByteBufUtil.setByte(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 438 */     ensureAccessible();
/* 439 */     _setShort(index, value);
/* 440 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 445 */     HeapByteBufUtil.setShort(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 450 */     ensureAccessible();
/* 451 */     _setShortLE(index, value);
/* 452 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 457 */     HeapByteBufUtil.setShortLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 462 */     ensureAccessible();
/* 463 */     _setMedium(index, value);
/* 464 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 469 */     HeapByteBufUtil.setMedium(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 474 */     ensureAccessible();
/* 475 */     _setMediumLE(index, value);
/* 476 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 481 */     HeapByteBufUtil.setMediumLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 486 */     ensureAccessible();
/* 487 */     _setInt(index, value);
/* 488 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 493 */     HeapByteBufUtil.setInt(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 498 */     ensureAccessible();
/* 499 */     _setIntLE(index, value);
/* 500 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 505 */     HeapByteBufUtil.setIntLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 510 */     ensureAccessible();
/* 511 */     _setLong(index, value);
/* 512 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 517 */     HeapByteBufUtil.setLong(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 522 */     ensureAccessible();
/* 523 */     _setLongLE(index, value);
/* 524 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 529 */     HeapByteBufUtil.setLongLE(this.array, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 534 */     checkIndex(index, length);
/* 535 */     return alloc().heapBuffer(length, maxCapacity()).writeBytes(this.array, index, length);
/*     */   }
/*     */   
/*     */   private ByteBuffer internalNioBuffer() {
/* 539 */     ByteBuffer tmpNioBuf = this.tmpNioBuf;
/* 540 */     if (tmpNioBuf == null) {
/* 541 */       this.tmpNioBuf = tmpNioBuf = ByteBuffer.wrap(this.array);
/*     */     }
/* 543 */     return tmpNioBuf;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 548 */     freeArray(this.array);
/* 549 */     this.array = EmptyArrays.EMPTY_BYTES;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/* 554 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledHeapByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */