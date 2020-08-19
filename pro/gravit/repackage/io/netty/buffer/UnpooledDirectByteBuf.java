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
/*     */ 
/*     */ 
/*     */ public class UnpooledDirectByteBuf
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*     */   private final ByteBufAllocator alloc;
/*     */   ByteBuffer buffer;
/*     */   private ByteBuffer tmpNioBuf;
/*     */   private int capacity;
/*     */   private boolean doNotFree;
/*     */   
/*     */   public UnpooledDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/*  54 */     super(maxCapacity);
/*  55 */     ObjectUtil.checkNotNull(alloc, "alloc");
/*  56 */     ObjectUtil.checkPositiveOrZero(initialCapacity, "initialCapacity");
/*  57 */     ObjectUtil.checkPositiveOrZero(maxCapacity, "maxCapacity");
/*  58 */     if (initialCapacity > maxCapacity) {
/*  59 */       throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[] {
/*  60 */               Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)
/*     */             }));
/*     */     }
/*  63 */     this.alloc = alloc;
/*  64 */     setByteBuffer(allocateDirect(initialCapacity), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
/*  73 */     this(alloc, initialBuffer, maxCapacity, false, true);
/*     */   }
/*     */ 
/*     */   
/*     */   UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree, boolean slice) {
/*  78 */     super(maxCapacity);
/*  79 */     ObjectUtil.checkNotNull(alloc, "alloc");
/*  80 */     ObjectUtil.checkNotNull(initialBuffer, "initialBuffer");
/*  81 */     if (!initialBuffer.isDirect()) {
/*  82 */       throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
/*     */     }
/*  84 */     if (initialBuffer.isReadOnly()) {
/*  85 */       throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
/*     */     }
/*     */     
/*  88 */     int initialCapacity = initialBuffer.remaining();
/*  89 */     if (initialCapacity > maxCapacity) {
/*  90 */       throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[] {
/*  91 */               Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)
/*     */             }));
/*     */     }
/*  94 */     this.alloc = alloc;
/*  95 */     this.doNotFree = !doFree;
/*  96 */     setByteBuffer((slice ? initialBuffer.slice() : initialBuffer).order(ByteOrder.BIG_ENDIAN), false);
/*  97 */     writerIndex(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuffer allocateDirect(int initialCapacity) {
/* 104 */     return ByteBuffer.allocateDirect(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void freeDirect(ByteBuffer buffer) {
/* 111 */     PlatformDependent.freeDirectBuffer(buffer);
/*     */   }
/*     */   
/*     */   void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
/* 115 */     if (tryFree) {
/* 116 */       ByteBuffer oldBuffer = this.buffer;
/* 117 */       if (oldBuffer != null) {
/* 118 */         if (this.doNotFree) {
/* 119 */           this.doNotFree = false;
/*     */         } else {
/* 121 */           freeDirect(oldBuffer);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 126 */     this.buffer = buffer;
/* 127 */     this.tmpNioBuf = null;
/* 128 */     this.capacity = buffer.remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 138 */     return this.capacity;
/*     */   }
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*     */     int bytesToCopy;
/* 143 */     checkNewCapacity(newCapacity);
/* 144 */     int oldCapacity = this.capacity;
/* 145 */     if (newCapacity == oldCapacity) {
/* 146 */       return this;
/*     */     }
/*     */     
/* 149 */     if (newCapacity > oldCapacity) {
/* 150 */       bytesToCopy = oldCapacity;
/*     */     } else {
/* 152 */       trimIndicesToCapacity(newCapacity);
/* 153 */       bytesToCopy = newCapacity;
/*     */     } 
/* 155 */     ByteBuffer oldBuffer = this.buffer;
/* 156 */     ByteBuffer newBuffer = allocateDirect(newCapacity);
/* 157 */     oldBuffer.position(0).limit(bytesToCopy);
/* 158 */     newBuffer.position(0).limit(bytesToCopy);
/* 159 */     newBuffer.put(oldBuffer).clear();
/* 160 */     setByteBuffer(newBuffer, true);
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/* 166 */     return this.alloc;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteOrder order() {
/* 171 */     return ByteOrder.BIG_ENDIAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 176 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 181 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 186 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 196 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 201 */     ensureAccessible();
/* 202 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 207 */     return this.buffer.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 212 */     ensureAccessible();
/* 213 */     return _getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 218 */     return this.buffer.getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 223 */     return ByteBufUtil.swapShort(this.buffer.getShort(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 228 */     ensureAccessible();
/* 229 */     return _getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 234 */     return (getByte(index) & 0xFF) << 16 | (
/* 235 */       getByte(index + 1) & 0xFF) << 8 | 
/* 236 */       getByte(index + 2) & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 241 */     return getByte(index) & 0xFF | (
/* 242 */       getByte(index + 1) & 0xFF) << 8 | (
/* 243 */       getByte(index + 2) & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 248 */     ensureAccessible();
/* 249 */     return _getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 254 */     return this.buffer.getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 259 */     return ByteBufUtil.swapInt(this.buffer.getInt(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 264 */     ensureAccessible();
/* 265 */     return _getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 270 */     return this.buffer.getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 275 */     return ByteBufUtil.swapLong(this.buffer.getLong(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 280 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 281 */     if (dst.hasArray()) {
/* 282 */       getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
/* 283 */     } else if (dst.nioBufferCount() > 0) {
/* 284 */       for (ByteBuffer bb : dst.nioBuffers(dstIndex, length)) {
/* 285 */         int bbLen = bb.remaining();
/* 286 */         getBytes(index, bb);
/* 287 */         index += bbLen;
/*     */       } 
/*     */     } else {
/* 290 */       dst.setBytes(dstIndex, this, index, length);
/*     */     } 
/* 292 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 297 */     getBytes(index, dst, dstIndex, length, false);
/* 298 */     return this;
/*     */   }
/*     */   void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
/*     */     ByteBuffer tmpBuf;
/* 302 */     checkDstIndex(index, length, dstIndex, dst.length);
/*     */ 
/*     */     
/* 305 */     if (internal) {
/* 306 */       tmpBuf = internalNioBuffer();
/*     */     } else {
/* 308 */       tmpBuf = this.buffer.duplicate();
/*     */     } 
/* 310 */     tmpBuf.clear().position(index).limit(index + length);
/* 311 */     tmpBuf.get(dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/* 316 */     checkReadableBytes(length);
/* 317 */     getBytes(this.readerIndex, dst, dstIndex, length, true);
/* 318 */     this.readerIndex += length;
/* 319 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 324 */     getBytes(index, dst, false);
/* 325 */     return this;
/*     */   }
/*     */   void getBytes(int index, ByteBuffer dst, boolean internal) {
/*     */     ByteBuffer tmpBuf;
/* 329 */     checkIndex(index, dst.remaining());
/*     */ 
/*     */     
/* 332 */     if (internal) {
/* 333 */       tmpBuf = internalNioBuffer();
/*     */     } else {
/* 335 */       tmpBuf = this.buffer.duplicate();
/*     */     } 
/* 337 */     tmpBuf.clear().position(index).limit(index + dst.remaining());
/* 338 */     dst.put(tmpBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuffer dst) {
/* 343 */     int length = dst.remaining();
/* 344 */     checkReadableBytes(length);
/* 345 */     getBytes(this.readerIndex, dst, true);
/* 346 */     this.readerIndex += length;
/* 347 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 352 */     ensureAccessible();
/* 353 */     _setByte(index, value);
/* 354 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 359 */     this.buffer.put(index, (byte)value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 364 */     ensureAccessible();
/* 365 */     _setShort(index, value);
/* 366 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 371 */     this.buffer.putShort(index, (short)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 376 */     this.buffer.putShort(index, ByteBufUtil.swapShort((short)value));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 381 */     ensureAccessible();
/* 382 */     _setMedium(index, value);
/* 383 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 388 */     setByte(index, (byte)(value >>> 16));
/* 389 */     setByte(index + 1, (byte)(value >>> 8));
/* 390 */     setByte(index + 2, (byte)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 395 */     setByte(index, (byte)value);
/* 396 */     setByte(index + 1, (byte)(value >>> 8));
/* 397 */     setByte(index + 2, (byte)(value >>> 16));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 402 */     ensureAccessible();
/* 403 */     _setInt(index, value);
/* 404 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 409 */     this.buffer.putInt(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 414 */     this.buffer.putInt(index, ByteBufUtil.swapInt(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 419 */     ensureAccessible();
/* 420 */     _setLong(index, value);
/* 421 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 426 */     this.buffer.putLong(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 431 */     this.buffer.putLong(index, ByteBufUtil.swapLong(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 436 */     checkSrcIndex(index, length, srcIndex, src.capacity());
/* 437 */     if (src.nioBufferCount() > 0) {
/* 438 */       for (ByteBuffer bb : src.nioBuffers(srcIndex, length)) {
/* 439 */         int bbLen = bb.remaining();
/* 440 */         setBytes(index, bb);
/* 441 */         index += bbLen;
/*     */       } 
/*     */     } else {
/* 444 */       src.getBytes(srcIndex, this, index, length);
/*     */     } 
/* 446 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 451 */     checkSrcIndex(index, length, srcIndex, src.length);
/* 452 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 453 */     tmpBuf.clear().position(index).limit(index + length);
/* 454 */     tmpBuf.put(src, srcIndex, length);
/* 455 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 460 */     ensureAccessible();
/* 461 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 462 */     if (src == tmpBuf) {
/* 463 */       src = src.duplicate();
/*     */     }
/*     */     
/* 466 */     tmpBuf.clear().position(index).limit(index + src.remaining());
/* 467 */     tmpBuf.put(src);
/* 468 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 473 */     getBytes(index, out, length, false);
/* 474 */     return this;
/*     */   }
/*     */   
/*     */   void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
/* 478 */     ensureAccessible();
/* 479 */     if (length == 0) {
/*     */       return;
/*     */     }
/* 482 */     ByteBufUtil.readBytes(alloc(), internal ? internalNioBuffer() : this.buffer.duplicate(), index, length, out);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
/* 487 */     checkReadableBytes(length);
/* 488 */     getBytes(this.readerIndex, out, length, true);
/* 489 */     this.readerIndex += length;
/* 490 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 495 */     return getBytes(index, out, length, false);
/*     */   }
/*     */   private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
/*     */     ByteBuffer tmpBuf;
/* 499 */     ensureAccessible();
/* 500 */     if (length == 0) {
/* 501 */       return 0;
/*     */     }
/*     */ 
/*     */     
/* 505 */     if (internal) {
/* 506 */       tmpBuf = internalNioBuffer();
/*     */     } else {
/* 508 */       tmpBuf = this.buffer.duplicate();
/*     */     } 
/* 510 */     tmpBuf.clear().position(index).limit(index + length);
/* 511 */     return out.write(tmpBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 516 */     return getBytes(index, out, position, length, false);
/*     */   }
/*     */   
/*     */   private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
/* 520 */     ensureAccessible();
/* 521 */     if (length == 0) {
/* 522 */       return 0;
/*     */     }
/*     */     
/* 525 */     ByteBuffer tmpBuf = internal ? internalNioBuffer() : this.buffer.duplicate();
/* 526 */     tmpBuf.clear().position(index).limit(index + length);
/* 527 */     return out.write(tmpBuf, position);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/* 532 */     checkReadableBytes(length);
/* 533 */     int readBytes = getBytes(this.readerIndex, out, length, true);
/* 534 */     this.readerIndex += readBytes;
/* 535 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/* 540 */     checkReadableBytes(length);
/* 541 */     int readBytes = getBytes(this.readerIndex, out, position, length, true);
/* 542 */     this.readerIndex += readBytes;
/* 543 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 548 */     ensureAccessible();
/* 549 */     if (this.buffer.hasArray()) {
/* 550 */       return in.read(this.buffer.array(), this.buffer.arrayOffset() + index, length);
/*     */     }
/* 552 */     byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
/* 553 */     int readBytes = in.read(tmp, 0, length);
/* 554 */     if (readBytes <= 0) {
/* 555 */       return readBytes;
/*     */     }
/* 557 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 558 */     tmpBuf.clear().position(index);
/* 559 */     tmpBuf.put(tmp, 0, readBytes);
/* 560 */     return readBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 566 */     ensureAccessible();
/* 567 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 568 */     tmpBuf.clear().position(index).limit(index + length);
/*     */     try {
/* 570 */       return in.read(tmpBuf);
/* 571 */     } catch (ClosedChannelException ignored) {
/* 572 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 578 */     ensureAccessible();
/* 579 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 580 */     tmpBuf.clear().position(index).limit(index + length);
/*     */     try {
/* 582 */       return in.read(tmpBuf, position);
/* 583 */     } catch (ClosedChannelException ignored) {
/* 584 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 590 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 595 */     return new ByteBuffer[] { nioBuffer(index, length) };
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isContiguous() {
/* 600 */     return true;
/*     */   }
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/*     */     ByteBuffer src;
/* 605 */     ensureAccessible();
/*     */     
/*     */     try {
/* 608 */       src = (ByteBuffer)this.buffer.duplicate().clear().position(index).limit(index + length);
/* 609 */     } catch (IllegalArgumentException ignored) {
/* 610 */       throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
/*     */     } 
/*     */     
/* 613 */     return alloc().directBuffer(length, maxCapacity()).writeBytes(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 618 */     checkIndex(index, length);
/* 619 */     return (ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length);
/*     */   }
/*     */   
/*     */   private ByteBuffer internalNioBuffer() {
/* 623 */     ByteBuffer tmpNioBuf = this.tmpNioBuf;
/* 624 */     if (tmpNioBuf == null) {
/* 625 */       this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
/*     */     }
/* 627 */     return tmpNioBuf;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 632 */     checkIndex(index, length);
/* 633 */     return ((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).slice();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 638 */     ByteBuffer buffer = this.buffer;
/* 639 */     if (buffer == null) {
/*     */       return;
/*     */     }
/*     */     
/* 643 */     this.buffer = null;
/*     */     
/* 645 */     if (!this.doNotFree) {
/* 646 */       freeDirect(buffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/* 652 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */