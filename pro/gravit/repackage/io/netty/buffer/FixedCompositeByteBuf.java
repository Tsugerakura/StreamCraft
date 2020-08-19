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
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.RecyclableArrayList;
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
/*     */ final class FixedCompositeByteBuf
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*  37 */   private static final ByteBuf[] EMPTY = new ByteBuf[] { Unpooled.EMPTY_BUFFER };
/*     */   private final int nioBufferCount;
/*     */   private final int capacity;
/*     */   private final ByteBufAllocator allocator;
/*     */   private final ByteOrder order;
/*     */   private final ByteBuf[] buffers;
/*     */   private final boolean direct;
/*     */   
/*     */   FixedCompositeByteBuf(ByteBufAllocator allocator, ByteBuf... buffers) {
/*  46 */     super(2147483647);
/*  47 */     if (buffers.length == 0) {
/*  48 */       this.buffers = EMPTY;
/*  49 */       this.order = ByteOrder.BIG_ENDIAN;
/*  50 */       this.nioBufferCount = 1;
/*  51 */       this.capacity = 0;
/*  52 */       this.direct = false;
/*     */     } else {
/*  54 */       ByteBuf b = buffers[0];
/*  55 */       this.buffers = buffers;
/*  56 */       boolean direct = true;
/*  57 */       int nioBufferCount = b.nioBufferCount();
/*  58 */       int capacity = b.readableBytes();
/*  59 */       this.order = b.order();
/*  60 */       for (int i = 1; i < buffers.length; i++) {
/*  61 */         b = buffers[i];
/*  62 */         if (buffers[i].order() != this.order) {
/*  63 */           throw new IllegalArgumentException("All ByteBufs need to have same ByteOrder");
/*     */         }
/*  65 */         nioBufferCount += b.nioBufferCount();
/*  66 */         capacity += b.readableBytes();
/*  67 */         if (!b.isDirect()) {
/*  68 */           direct = false;
/*     */         }
/*     */       } 
/*  71 */       this.nioBufferCount = nioBufferCount;
/*  72 */       this.capacity = capacity;
/*  73 */       this.direct = direct;
/*     */     } 
/*  75 */     setIndex(0, capacity());
/*  76 */     this.allocator = allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable(int size) {
/*  86 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf discardReadBytes() {
/*  91 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/*  96 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 101 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 106 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 111 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 116 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 121 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 126 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 131 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 136 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 141 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 146 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 151 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 156 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 161 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 166 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 171 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 176 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) {
/* 181 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) {
/* 186 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) {
/* 191 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 196 */     return this.capacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxCapacity() {
/* 201 */     return this.capacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/* 206 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/* 211 */     return this.allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteOrder order() {
/* 216 */     return this.order;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 226 */     return this.direct;
/*     */   }
/*     */   
/*     */   private Component findComponent(int index) {
/* 230 */     int readable = 0;
/* 231 */     for (int i = 0; i < this.buffers.length; i++) {
/* 232 */       Component comp = null;
/* 233 */       ByteBuf b = this.buffers[i];
/* 234 */       if (b instanceof Component) {
/* 235 */         comp = (Component)b;
/* 236 */         b = comp.buf;
/*     */       } 
/* 238 */       readable += b.readableBytes();
/* 239 */       if (index < readable) {
/* 240 */         if (comp == null) {
/*     */ 
/*     */           
/* 243 */           comp = new Component(i, readable - b.readableBytes(), b);
/* 244 */           this.buffers[i] = comp;
/*     */         } 
/* 246 */         return comp;
/*     */       } 
/*     */     } 
/* 249 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuf buffer(int i) {
/* 256 */     ByteBuf b = this.buffers[i];
/* 257 */     return (b instanceof Component) ? ((Component)b).buf : b;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 262 */     return _getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 267 */     Component c = findComponent(index);
/* 268 */     return c.buf.getByte(index - c.offset);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 273 */     Component c = findComponent(index);
/* 274 */     if (index + 2 <= c.endOffset)
/* 275 */       return c.buf.getShort(index - c.offset); 
/* 276 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 277 */       return (short)((_getByte(index) & 0xFF) << 8 | _getByte(index + 1) & 0xFF);
/*     */     }
/* 279 */     return (short)(_getByte(index) & 0xFF | (_getByte(index + 1) & 0xFF) << 8);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 285 */     Component c = findComponent(index);
/* 286 */     if (index + 2 <= c.endOffset)
/* 287 */       return c.buf.getShortLE(index - c.offset); 
/* 288 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 289 */       return (short)(_getByte(index) & 0xFF | (_getByte(index + 1) & 0xFF) << 8);
/*     */     }
/* 291 */     return (short)((_getByte(index) & 0xFF) << 8 | _getByte(index + 1) & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 297 */     Component c = findComponent(index);
/* 298 */     if (index + 3 <= c.endOffset)
/* 299 */       return c.buf.getUnsignedMedium(index - c.offset); 
/* 300 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 301 */       return (_getShort(index) & 0xFFFF) << 8 | _getByte(index + 2) & 0xFF;
/*     */     }
/* 303 */     return _getShort(index) & 0xFFFF | (_getByte(index + 2) & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 309 */     Component c = findComponent(index);
/* 310 */     if (index + 3 <= c.endOffset)
/* 311 */       return c.buf.getUnsignedMediumLE(index - c.offset); 
/* 312 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 313 */       return _getShortLE(index) & 0xFFFF | (_getByte(index + 2) & 0xFF) << 16;
/*     */     }
/* 315 */     return (_getShortLE(index) & 0xFFFF) << 8 | _getByte(index + 2) & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 321 */     Component c = findComponent(index);
/* 322 */     if (index + 4 <= c.endOffset)
/* 323 */       return c.buf.getInt(index - c.offset); 
/* 324 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 325 */       return (_getShort(index) & 0xFFFF) << 16 | _getShort(index + 2) & 0xFFFF;
/*     */     }
/* 327 */     return _getShort(index) & 0xFFFF | (_getShort(index + 2) & 0xFFFF) << 16;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 333 */     Component c = findComponent(index);
/* 334 */     if (index + 4 <= c.endOffset)
/* 335 */       return c.buf.getIntLE(index - c.offset); 
/* 336 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 337 */       return _getShortLE(index) & 0xFFFF | (_getShortLE(index + 2) & 0xFFFF) << 16;
/*     */     }
/* 339 */     return (_getShortLE(index) & 0xFFFF) << 16 | _getShortLE(index + 2) & 0xFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 345 */     Component c = findComponent(index);
/* 346 */     if (index + 8 <= c.endOffset)
/* 347 */       return c.buf.getLong(index - c.offset); 
/* 348 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 349 */       return (_getInt(index) & 0xFFFFFFFFL) << 32L | _getInt(index + 4) & 0xFFFFFFFFL;
/*     */     }
/* 351 */     return _getInt(index) & 0xFFFFFFFFL | (_getInt(index + 4) & 0xFFFFFFFFL) << 32L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 357 */     Component c = findComponent(index);
/* 358 */     if (index + 8 <= c.endOffset)
/* 359 */       return c.buf.getLongLE(index - c.offset); 
/* 360 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 361 */       return _getIntLE(index) & 0xFFFFFFFFL | (_getIntLE(index + 4) & 0xFFFFFFFFL) << 32L;
/*     */     }
/* 363 */     return (_getIntLE(index) & 0xFFFFFFFFL) << 32L | _getIntLE(index + 4) & 0xFFFFFFFFL;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 369 */     checkDstIndex(index, length, dstIndex, dst.length);
/* 370 */     if (length == 0) {
/* 371 */       return this;
/*     */     }
/*     */     
/* 374 */     Component c = findComponent(index);
/* 375 */     int i = c.index;
/* 376 */     int adjustment = c.offset;
/* 377 */     ByteBuf s = c.buf;
/*     */     while (true) {
/* 379 */       int localLength = Math.min(length, s.readableBytes() - index - adjustment);
/* 380 */       s.getBytes(index - adjustment, dst, dstIndex, localLength);
/* 381 */       index += localLength;
/* 382 */       dstIndex += localLength;
/* 383 */       length -= localLength;
/* 384 */       adjustment += s.readableBytes();
/* 385 */       if (length <= 0) {
/*     */         break;
/*     */       }
/* 388 */       s = buffer(++i);
/*     */     } 
/* 390 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 395 */     int limit = dst.limit();
/* 396 */     int length = dst.remaining();
/*     */     
/* 398 */     checkIndex(index, length);
/* 399 */     if (length == 0) {
/* 400 */       return this;
/*     */     }
/*     */     
/*     */     try {
/* 404 */       Component c = findComponent(index);
/* 405 */       int i = c.index;
/* 406 */       int adjustment = c.offset;
/* 407 */       ByteBuf s = c.buf;
/*     */       while (true) {
/* 409 */         int localLength = Math.min(length, s.readableBytes() - index - adjustment);
/* 410 */         dst.limit(dst.position() + localLength);
/* 411 */         s.getBytes(index - adjustment, dst);
/* 412 */         index += localLength;
/* 413 */         length -= localLength;
/* 414 */         adjustment += s.readableBytes();
/* 415 */         if (length <= 0) {
/*     */           break;
/*     */         }
/* 418 */         s = buffer(++i);
/*     */       } 
/*     */     } finally {
/* 421 */       dst.limit(limit);
/*     */     } 
/* 423 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 428 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 429 */     if (length == 0) {
/* 430 */       return this;
/*     */     }
/*     */     
/* 433 */     Component c = findComponent(index);
/* 434 */     int i = c.index;
/* 435 */     int adjustment = c.offset;
/* 436 */     ByteBuf s = c.buf;
/*     */     while (true) {
/* 438 */       int localLength = Math.min(length, s.readableBytes() - index - adjustment);
/* 439 */       s.getBytes(index - adjustment, dst, dstIndex, localLength);
/* 440 */       index += localLength;
/* 441 */       dstIndex += localLength;
/* 442 */       length -= localLength;
/* 443 */       adjustment += s.readableBytes();
/* 444 */       if (length <= 0) {
/*     */         break;
/*     */       }
/* 447 */       s = buffer(++i);
/*     */     } 
/* 449 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 455 */     int count = nioBufferCount();
/* 456 */     if (count == 1) {
/* 457 */       return out.write(internalNioBuffer(index, length));
/*     */     }
/* 459 */     long writtenBytes = out.write(nioBuffers(index, length));
/* 460 */     if (writtenBytes > 2147483647L) {
/* 461 */       return Integer.MAX_VALUE;
/*     */     }
/* 463 */     return (int)writtenBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 471 */     int count = nioBufferCount();
/* 472 */     if (count == 1) {
/* 473 */       return out.write(internalNioBuffer(index, length), position);
/*     */     }
/* 475 */     long writtenBytes = 0L;
/* 476 */     for (ByteBuffer buf : nioBuffers(index, length)) {
/* 477 */       writtenBytes += out.write(buf, position + writtenBytes);
/*     */     }
/* 479 */     if (writtenBytes > 2147483647L) {
/* 480 */       return Integer.MAX_VALUE;
/*     */     }
/* 482 */     return (int)writtenBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 489 */     checkIndex(index, length);
/* 490 */     if (length == 0) {
/* 491 */       return this;
/*     */     }
/*     */     
/* 494 */     Component c = findComponent(index);
/* 495 */     int i = c.index;
/* 496 */     int adjustment = c.offset;
/* 497 */     ByteBuf s = c.buf;
/*     */     while (true) {
/* 499 */       int localLength = Math.min(length, s.readableBytes() - index - adjustment);
/* 500 */       s.getBytes(index - adjustment, out, localLength);
/* 501 */       index += localLength;
/* 502 */       length -= localLength;
/* 503 */       adjustment += s.readableBytes();
/* 504 */       if (length <= 0) {
/*     */         break;
/*     */       }
/* 507 */       s = buffer(++i);
/*     */     } 
/* 509 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 514 */     checkIndex(index, length);
/* 515 */     boolean release = true;
/* 516 */     ByteBuf buf = alloc().buffer(length);
/*     */     try {
/* 518 */       buf.writeBytes(this, index, length);
/* 519 */       release = false;
/* 520 */       return buf;
/*     */     } finally {
/* 522 */       if (release) {
/* 523 */         buf.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 530 */     return this.nioBufferCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 535 */     checkIndex(index, length);
/* 536 */     if (this.buffers.length == 1) {
/* 537 */       ByteBuf buf = buffer(0);
/* 538 */       if (buf.nioBufferCount() == 1) {
/* 539 */         return buf.nioBuffer(index, length);
/*     */       }
/*     */     } 
/* 542 */     ByteBuffer merged = ByteBuffer.allocate(length).order(order());
/* 543 */     ByteBuffer[] buffers = nioBuffers(index, length);
/*     */ 
/*     */     
/* 546 */     for (int i = 0; i < buffers.length; i++) {
/* 547 */       merged.put(buffers[i]);
/*     */     }
/*     */     
/* 550 */     merged.flip();
/* 551 */     return merged;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 556 */     if (this.buffers.length == 1) {
/* 557 */       return buffer(0).internalNioBuffer(index, length);
/*     */     }
/* 559 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 564 */     checkIndex(index, length);
/* 565 */     if (length == 0) {
/* 566 */       return EmptyArrays.EMPTY_BYTE_BUFFERS;
/*     */     }
/*     */     
/* 569 */     RecyclableArrayList array = RecyclableArrayList.newInstance(this.buffers.length);
/*     */     try {
/* 571 */       Component c = findComponent(index);
/* 572 */       int i = c.index;
/* 573 */       int adjustment = c.offset;
/* 574 */       ByteBuf s = c.buf;
/*     */       while (true) {
/* 576 */         int localLength = Math.min(length, s.readableBytes() - index - adjustment);
/* 577 */         switch (s.nioBufferCount()) {
/*     */           case 0:
/* 579 */             throw new UnsupportedOperationException();
/*     */           case 1:
/* 581 */             array.add(s.nioBuffer(index - adjustment, localLength));
/*     */             break;
/*     */           default:
/* 584 */             Collections.addAll((Collection<? super ByteBuffer>)array, s.nioBuffers(index - adjustment, localLength));
/*     */             break;
/*     */         } 
/* 587 */         index += localLength;
/* 588 */         length -= localLength;
/* 589 */         adjustment += s.readableBytes();
/* 590 */         if (length <= 0) {
/*     */           break;
/*     */         }
/* 593 */         s = buffer(++i);
/*     */       } 
/*     */       
/* 596 */       return (ByteBuffer[])array.toArray((Object[])new ByteBuffer[0]);
/*     */     } finally {
/* 598 */       array.recycle();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 604 */     switch (this.buffers.length) {
/*     */       case 0:
/* 606 */         return true;
/*     */       case 1:
/* 608 */         return buffer(0).hasArray();
/*     */     } 
/* 610 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 616 */     switch (this.buffers.length) {
/*     */       case 0:
/* 618 */         return EmptyArrays.EMPTY_BYTES;
/*     */       case 1:
/* 620 */         return buffer(0).array();
/*     */     } 
/* 622 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 628 */     switch (this.buffers.length) {
/*     */       case 0:
/* 630 */         return 0;
/*     */       case 1:
/* 632 */         return buffer(0).arrayOffset();
/*     */     } 
/* 634 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 640 */     switch (this.buffers.length) {
/*     */       case 0:
/* 642 */         return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
/*     */       case 1:
/* 644 */         return buffer(0).hasMemoryAddress();
/*     */     } 
/* 646 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 652 */     switch (this.buffers.length) {
/*     */       case 0:
/* 654 */         return Unpooled.EMPTY_BUFFER.memoryAddress();
/*     */       case 1:
/* 656 */         return buffer(0).memoryAddress();
/*     */     } 
/* 658 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 664 */     for (int i = 0; i < this.buffers.length; i++) {
/* 665 */       buffer(i).release();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 671 */     String result = super.toString();
/* 672 */     result = result.substring(0, result.length() - 1);
/* 673 */     return result + ", components=" + this.buffers.length + ')';
/*     */   }
/*     */   
/*     */   private static final class Component extends WrappedByteBuf {
/*     */     private final int index;
/*     */     private final int offset;
/*     */     private final int endOffset;
/*     */     
/*     */     Component(int index, int offset, ByteBuf buf) {
/* 682 */       super(buf);
/* 683 */       this.index = index;
/* 684 */       this.offset = offset;
/* 685 */       this.endOffset = offset + buf.readableBytes();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\FixedCompositeByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */