/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ public final class Unpooled
/*     */ {
/*  74 */   private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);
/*     */   
/*     */   static {
/*  92 */     assert EMPTY_BUFFER instanceof EmptyByteBuf : "EMPTY_BUFFER must be an EmptyByteBuf.";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf buffer() {
/* 100 */     return ALLOC.heapBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf directBuffer() {
/* 108 */     return ALLOC.directBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf buffer(int initialCapacity) {
/* 117 */     return ALLOC.heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf directBuffer(int initialCapacity) {
/* 126 */     return ALLOC.directBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
/* 136 */     return ALLOC.heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
/* 146 */     return ALLOC.directBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(byte[] array) {
/* 155 */     if (array.length == 0) {
/* 156 */       return EMPTY_BUFFER;
/*     */     }
/* 158 */     return new UnpooledHeapByteBuf(ALLOC, array, array.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(byte[] array, int offset, int length) {
/* 167 */     if (length == 0) {
/* 168 */       return EMPTY_BUFFER;
/*     */     }
/*     */     
/* 171 */     if (offset == 0 && length == array.length) {
/* 172 */       return wrappedBuffer(array);
/*     */     }
/*     */     
/* 175 */     return wrappedBuffer(array).slice(offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(ByteBuffer buffer) {
/* 184 */     if (!buffer.hasRemaining()) {
/* 185 */       return EMPTY_BUFFER;
/*     */     }
/* 187 */     if (!buffer.isDirect() && buffer.hasArray())
/* 188 */       return wrappedBuffer(buffer
/* 189 */           .array(), buffer
/* 190 */           .arrayOffset() + buffer.position(), buffer
/* 191 */           .remaining()).order(buffer.order()); 
/* 192 */     if (PlatformDependent.hasUnsafe()) {
/* 193 */       if (buffer.isReadOnly()) {
/* 194 */         if (buffer.isDirect()) {
/* 195 */           return new ReadOnlyUnsafeDirectByteBuf(ALLOC, buffer);
/*     */         }
/* 197 */         return new ReadOnlyByteBufferBuf(ALLOC, buffer);
/*     */       } 
/*     */       
/* 200 */       return new UnpooledUnsafeDirectByteBuf(ALLOC, buffer, buffer.remaining());
/*     */     } 
/*     */     
/* 203 */     if (buffer.isReadOnly()) {
/* 204 */       return new ReadOnlyByteBufferBuf(ALLOC, buffer);
/*     */     }
/* 206 */     return new UnpooledDirectByteBuf(ALLOC, buffer, buffer.remaining());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(long memoryAddress, int size, boolean doFree) {
/* 216 */     return new WrappedUnpooledUnsafeDirectByteBuf(ALLOC, memoryAddress, size, doFree);
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
/*     */   public static ByteBuf wrappedBuffer(ByteBuf buffer) {
/* 228 */     if (buffer.isReadable()) {
/* 229 */       return buffer.slice();
/*     */     }
/* 231 */     buffer.release();
/* 232 */     return EMPTY_BUFFER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(byte[]... arrays) {
/* 242 */     return wrappedBuffer(arrays.length, arrays);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(ByteBuf... buffers) {
/* 253 */     return wrappedBuffer(buffers.length, buffers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedBuffer(ByteBuffer... buffers) {
/* 262 */     return wrappedBuffer(buffers.length, buffers);
/*     */   }
/*     */   
/*     */   static <T> ByteBuf wrappedBuffer(int maxNumComponents, CompositeByteBuf.ByteWrapper<T> wrapper, T[] array) {
/* 266 */     switch (array.length) {
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
/*     */       case 0:
/* 286 */         return EMPTY_BUFFER;
/*     */       case 1:
/*     */         if (!wrapper.isEmpty(array[0]))
/*     */           return wrapper.wrap(array[0]); 
/*     */     }  for (int i = 0, len = array.length; i < len; i++) {
/*     */       T bytes = array[i]; if (bytes == null)
/*     */         return EMPTY_BUFFER; 
/*     */       if (!wrapper.isEmpty(bytes))
/*     */         return new CompositeByteBuf(ALLOC, false, maxNumComponents, wrapper, array, i); 
/* 295 */     }  } public static ByteBuf wrappedBuffer(int maxNumComponents, byte[]... arrays) { return wrappedBuffer(maxNumComponents, (CompositeByteBuf.ByteWrapper)CompositeByteBuf.BYTE_ARRAY_WRAPPER, arrays); }
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
/*     */   public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuf... buffers) {
/*     */     ByteBuf buffer;
/* 308 */     switch (buffers.length) {
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
/*     */       case 0:
/* 329 */         return EMPTY_BUFFER;
/*     */       case 1:
/*     */         buffer = buffers[0]; if (buffer.isReadable())
/*     */           return wrappedBuffer(buffer.order(BIG_ENDIAN));  buffer.release();
/*     */     }  for (int i = 0; i < buffers.length; i++) {
/*     */       ByteBuf buf = buffers[i];
/*     */       if (buf.isReadable())
/*     */         return new CompositeByteBuf(ALLOC, false, maxNumComponents, buffers, i); 
/*     */       buf.release();
/* 338 */     }  } public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuffer... buffers) { return wrappedBuffer(maxNumComponents, CompositeByteBuf.BYTE_BUFFER_WRAPPER, buffers); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompositeByteBuf compositeBuffer() {
/* 345 */     return compositeBuffer(16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompositeByteBuf compositeBuffer(int maxNumComponents) {
/* 352 */     return new CompositeByteBuf(ALLOC, false, maxNumComponents);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(byte[] array) {
/* 361 */     if (array.length == 0) {
/* 362 */       return EMPTY_BUFFER;
/*     */     }
/* 364 */     return wrappedBuffer((byte[])array.clone());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(byte[] array, int offset, int length) {
/* 374 */     if (length == 0) {
/* 375 */       return EMPTY_BUFFER;
/*     */     }
/* 377 */     byte[] copy = PlatformDependent.allocateUninitializedArray(length);
/* 378 */     System.arraycopy(array, offset, copy, 0, length);
/* 379 */     return wrappedBuffer(copy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(ByteBuffer buffer) {
/* 389 */     int length = buffer.remaining();
/* 390 */     if (length == 0) {
/* 391 */       return EMPTY_BUFFER;
/*     */     }
/* 393 */     byte[] copy = PlatformDependent.allocateUninitializedArray(length);
/*     */ 
/*     */     
/* 396 */     ByteBuffer duplicate = buffer.duplicate();
/* 397 */     duplicate.get(copy);
/* 398 */     return wrappedBuffer(copy).order(duplicate.order());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(ByteBuf buffer) {
/* 408 */     int readable = buffer.readableBytes();
/* 409 */     if (readable > 0) {
/* 410 */       ByteBuf copy = buffer(readable);
/* 411 */       copy.writeBytes(buffer, buffer.readerIndex(), readable);
/* 412 */       return copy;
/*     */     } 
/* 414 */     return EMPTY_BUFFER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(byte[]... arrays) {
/* 425 */     switch (arrays.length) {
/*     */       case 0:
/* 427 */         return EMPTY_BUFFER;
/*     */       case 1:
/* 429 */         if ((arrays[0]).length == 0) {
/* 430 */           return EMPTY_BUFFER;
/*     */         }
/* 432 */         return copiedBuffer(arrays[0]);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 437 */     int length = 0;
/* 438 */     for (byte[] a : arrays) {
/* 439 */       if (Integer.MAX_VALUE - length < a.length) {
/* 440 */         throw new IllegalArgumentException("The total length of the specified arrays is too big.");
/*     */       }
/*     */       
/* 443 */       length += a.length;
/*     */     } 
/*     */     
/* 446 */     if (length == 0) {
/* 447 */       return EMPTY_BUFFER;
/*     */     }
/*     */     
/* 450 */     byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
/* 451 */     for (int i = 0, j = 0; i < arrays.length; i++) {
/* 452 */       byte[] a = arrays[i];
/* 453 */       System.arraycopy(a, 0, mergedArray, j, a.length);
/* 454 */       j += a.length;
/*     */     } 
/*     */     
/* 457 */     return wrappedBuffer(mergedArray);
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
/*     */   public static ByteBuf copiedBuffer(ByteBuf... buffers) {
/* 471 */     switch (buffers.length) {
/*     */       case 0:
/* 473 */         return EMPTY_BUFFER;
/*     */       case 1:
/* 475 */         return copiedBuffer(buffers[0]);
/*     */     } 
/*     */ 
/*     */     
/* 479 */     ByteOrder order = null;
/* 480 */     int length = 0;
/* 481 */     for (ByteBuf b : buffers) {
/* 482 */       int bLen = b.readableBytes();
/* 483 */       if (bLen > 0) {
/*     */ 
/*     */         
/* 486 */         if (Integer.MAX_VALUE - length < bLen) {
/* 487 */           throw new IllegalArgumentException("The total length of the specified buffers is too big.");
/*     */         }
/*     */         
/* 490 */         length += bLen;
/* 491 */         if (order != null) {
/* 492 */           if (!order.equals(b.order())) {
/* 493 */             throw new IllegalArgumentException("inconsistent byte order");
/*     */           }
/*     */         } else {
/* 496 */           order = b.order();
/*     */         } 
/*     */       } 
/*     */     } 
/* 500 */     if (length == 0) {
/* 501 */       return EMPTY_BUFFER;
/*     */     }
/*     */     
/* 504 */     byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
/* 505 */     for (int i = 0, j = 0; i < buffers.length; i++) {
/* 506 */       ByteBuf b = buffers[i];
/* 507 */       int bLen = b.readableBytes();
/* 508 */       b.getBytes(b.readerIndex(), mergedArray, j, bLen);
/* 509 */       j += bLen;
/*     */     } 
/*     */     
/* 512 */     return wrappedBuffer(mergedArray).order(order);
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
/*     */   public static ByteBuf copiedBuffer(ByteBuffer... buffers) {
/* 526 */     switch (buffers.length) {
/*     */       case 0:
/* 528 */         return EMPTY_BUFFER;
/*     */       case 1:
/* 530 */         return copiedBuffer(buffers[0]);
/*     */     } 
/*     */ 
/*     */     
/* 534 */     ByteOrder order = null;
/* 535 */     int length = 0;
/* 536 */     for (ByteBuffer b : buffers) {
/* 537 */       int bLen = b.remaining();
/* 538 */       if (bLen > 0) {
/*     */ 
/*     */         
/* 541 */         if (Integer.MAX_VALUE - length < bLen) {
/* 542 */           throw new IllegalArgumentException("The total length of the specified buffers is too big.");
/*     */         }
/*     */         
/* 545 */         length += bLen;
/* 546 */         if (order != null) {
/* 547 */           if (!order.equals(b.order())) {
/* 548 */             throw new IllegalArgumentException("inconsistent byte order");
/*     */           }
/*     */         } else {
/* 551 */           order = b.order();
/*     */         } 
/*     */       } 
/*     */     } 
/* 555 */     if (length == 0) {
/* 556 */       return EMPTY_BUFFER;
/*     */     }
/*     */     
/* 559 */     byte[] mergedArray = PlatformDependent.allocateUninitializedArray(length);
/* 560 */     for (int i = 0, j = 0; i < buffers.length; i++) {
/*     */ 
/*     */       
/* 563 */       ByteBuffer b = buffers[i].duplicate();
/* 564 */       int bLen = b.remaining();
/* 565 */       b.get(mergedArray, j, bLen);
/* 566 */       j += bLen;
/*     */     } 
/*     */     
/* 569 */     return wrappedBuffer(mergedArray).order(order);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(CharSequence string, Charset charset) {
/* 579 */     ObjectUtil.checkNotNull(string, "string");
/*     */     
/* 581 */     if (string instanceof CharBuffer) {
/* 582 */       return copiedBuffer((CharBuffer)string, charset);
/*     */     }
/*     */     
/* 585 */     return copiedBuffer(CharBuffer.wrap(string), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(CharSequence string, int offset, int length, Charset charset) {
/* 596 */     ObjectUtil.checkNotNull(string, "string");
/* 597 */     if (length == 0) {
/* 598 */       return EMPTY_BUFFER;
/*     */     }
/*     */     
/* 601 */     if (string instanceof CharBuffer) {
/* 602 */       CharBuffer buf = (CharBuffer)string;
/* 603 */       if (buf.hasArray()) {
/* 604 */         return copiedBuffer(buf
/* 605 */             .array(), buf
/* 606 */             .arrayOffset() + buf.position() + offset, length, charset);
/*     */       }
/*     */ 
/*     */       
/* 610 */       buf = buf.slice();
/* 611 */       buf.limit(length);
/* 612 */       buf.position(offset);
/* 613 */       return copiedBuffer(buf, charset);
/*     */     } 
/*     */     
/* 616 */     return copiedBuffer(CharBuffer.wrap(string, offset, offset + length), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(char[] array, Charset charset) {
/* 626 */     ObjectUtil.checkNotNull(array, "array");
/* 627 */     return copiedBuffer(array, 0, array.length, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copiedBuffer(char[] array, int offset, int length, Charset charset) {
/* 637 */     ObjectUtil.checkNotNull(array, "array");
/* 638 */     if (length == 0) {
/* 639 */       return EMPTY_BUFFER;
/*     */     }
/* 641 */     return copiedBuffer(CharBuffer.wrap(array, offset, length), charset);
/*     */   }
/*     */   
/*     */   private static ByteBuf copiedBuffer(CharBuffer buffer, Charset charset) {
/* 645 */     return ByteBufUtil.encodeString0(ALLOC, true, buffer, charset, 0);
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
/*     */   @Deprecated
/*     */   public static ByteBuf unmodifiableBuffer(ByteBuf buffer) {
/* 658 */     ByteOrder endianness = buffer.order();
/* 659 */     if (endianness == BIG_ENDIAN) {
/* 660 */       return new ReadOnlyByteBuf(buffer);
/*     */     }
/*     */     
/* 663 */     return (new ReadOnlyByteBuf(buffer.order(BIG_ENDIAN))).order(LITTLE_ENDIAN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyInt(int value) {
/* 670 */     ByteBuf buf = buffer(4);
/* 671 */     buf.writeInt(value);
/* 672 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyInt(int... values) {
/* 679 */     if (values == null || values.length == 0) {
/* 680 */       return EMPTY_BUFFER;
/*     */     }
/* 682 */     ByteBuf buffer = buffer(values.length * 4);
/* 683 */     for (int v : values) {
/* 684 */       buffer.writeInt(v);
/*     */     }
/* 686 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyShort(int value) {
/* 693 */     ByteBuf buf = buffer(2);
/* 694 */     buf.writeShort(value);
/* 695 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyShort(short... values) {
/* 702 */     if (values == null || values.length == 0) {
/* 703 */       return EMPTY_BUFFER;
/*     */     }
/* 705 */     ByteBuf buffer = buffer(values.length * 2);
/* 706 */     for (int v : values) {
/* 707 */       buffer.writeShort(v);
/*     */     }
/* 709 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyShort(int... values) {
/* 716 */     if (values == null || values.length == 0) {
/* 717 */       return EMPTY_BUFFER;
/*     */     }
/* 719 */     ByteBuf buffer = buffer(values.length * 2);
/* 720 */     for (int v : values) {
/* 721 */       buffer.writeShort(v);
/*     */     }
/* 723 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyMedium(int value) {
/* 730 */     ByteBuf buf = buffer(3);
/* 731 */     buf.writeMedium(value);
/* 732 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyMedium(int... values) {
/* 739 */     if (values == null || values.length == 0) {
/* 740 */       return EMPTY_BUFFER;
/*     */     }
/* 742 */     ByteBuf buffer = buffer(values.length * 3);
/* 743 */     for (int v : values) {
/* 744 */       buffer.writeMedium(v);
/*     */     }
/* 746 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyLong(long value) {
/* 753 */     ByteBuf buf = buffer(8);
/* 754 */     buf.writeLong(value);
/* 755 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyLong(long... values) {
/* 762 */     if (values == null || values.length == 0) {
/* 763 */       return EMPTY_BUFFER;
/*     */     }
/* 765 */     ByteBuf buffer = buffer(values.length * 8);
/* 766 */     for (long v : values) {
/* 767 */       buffer.writeLong(v);
/*     */     }
/* 769 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyBoolean(boolean value) {
/* 776 */     ByteBuf buf = buffer(1);
/* 777 */     buf.writeBoolean(value);
/* 778 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyBoolean(boolean... values) {
/* 785 */     if (values == null || values.length == 0) {
/* 786 */       return EMPTY_BUFFER;
/*     */     }
/* 788 */     ByteBuf buffer = buffer(values.length);
/* 789 */     for (boolean v : values) {
/* 790 */       buffer.writeBoolean(v);
/*     */     }
/* 792 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyFloat(float value) {
/* 799 */     ByteBuf buf = buffer(4);
/* 800 */     buf.writeFloat(value);
/* 801 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyFloat(float... values) {
/* 808 */     if (values == null || values.length == 0) {
/* 809 */       return EMPTY_BUFFER;
/*     */     }
/* 811 */     ByteBuf buffer = buffer(values.length * 4);
/* 812 */     for (float v : values) {
/* 813 */       buffer.writeFloat(v);
/*     */     }
/* 815 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyDouble(double value) {
/* 822 */     ByteBuf buf = buffer(8);
/* 823 */     buf.writeDouble(value);
/* 824 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf copyDouble(double... values) {
/* 831 */     if (values == null || values.length == 0) {
/* 832 */       return EMPTY_BUFFER;
/*     */     }
/* 834 */     ByteBuf buffer = buffer(values.length * 8);
/* 835 */     for (double v : values) {
/* 836 */       buffer.writeDouble(v);
/*     */     }
/* 838 */     return buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf unreleasableBuffer(ByteBuf buf) {
/* 845 */     return new UnreleasableByteBuf(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static ByteBuf unmodifiableBuffer(ByteBuf... buffers) {
/* 856 */     return wrappedUnmodifiableBuffer(true, buffers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf wrappedUnmodifiableBuffer(ByteBuf... buffers) {
/* 866 */     return wrappedUnmodifiableBuffer(false, buffers);
/*     */   }
/*     */   
/*     */   private static ByteBuf wrappedUnmodifiableBuffer(boolean copy, ByteBuf... buffers) {
/* 870 */     switch (buffers.length) {
/*     */       case 0:
/* 872 */         return EMPTY_BUFFER;
/*     */       case 1:
/* 874 */         return buffers[0].asReadOnly();
/*     */     } 
/* 876 */     if (copy) {
/* 877 */       buffers = Arrays.<ByteBuf, ByteBuf>copyOf(buffers, buffers.length, ByteBuf[].class);
/*     */     }
/* 879 */     return new FixedCompositeByteBuf(ALLOC, buffers);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\Unpooled.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */