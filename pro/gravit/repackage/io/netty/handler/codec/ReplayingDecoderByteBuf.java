/*      */ package pro.gravit.repackage.io.netty.handler.codec;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.GatheringByteChannel;
/*      */ import java.nio.channels.ScatteringByteChannel;
/*      */ import java.nio.charset.Charset;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.buffer.SwappedByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.Signal;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class ReplayingDecoderByteBuf
/*      */   extends ByteBuf
/*      */ {
/*   41 */   private static final Signal REPLAY = ReplayingDecoder.REPLAY;
/*      */   
/*      */   private ByteBuf buffer;
/*      */   
/*      */   private boolean terminated;
/*      */   private SwappedByteBuf swapped;
/*   47 */   static final ReplayingDecoderByteBuf EMPTY_BUFFER = new ReplayingDecoderByteBuf(Unpooled.EMPTY_BUFFER);
/*      */   
/*      */   static {
/*   50 */     EMPTY_BUFFER.terminate();
/*      */   }
/*      */   
/*      */   ReplayingDecoderByteBuf() {}
/*      */   
/*      */   ReplayingDecoderByteBuf(ByteBuf buffer) {
/*   56 */     setCumulation(buffer);
/*      */   }
/*      */   
/*      */   void setCumulation(ByteBuf buffer) {
/*   60 */     this.buffer = buffer;
/*      */   }
/*      */   
/*      */   void terminate() {
/*   64 */     this.terminated = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int capacity() {
/*   69 */     if (this.terminated) {
/*   70 */       return this.buffer.capacity();
/*      */     }
/*   72 */     return Integer.MAX_VALUE;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf capacity(int newCapacity) {
/*   78 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxCapacity() {
/*   83 */     return capacity();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBufAllocator alloc() {
/*   88 */     return this.buffer.alloc();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*   93 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf asReadOnly() {
/*   99 */     return Unpooled.unmodifiableBuffer(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isDirect() {
/*  104 */     return this.buffer.isDirect();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasArray() {
/*  109 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte[] array() {
/*  114 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   
/*      */   public int arrayOffset() {
/*  119 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasMemoryAddress() {
/*  124 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public long memoryAddress() {
/*  129 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf clear() {
/*  134 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  139 */     return (this == obj);
/*      */   }
/*      */ 
/*      */   
/*      */   public int compareTo(ByteBuf buffer) {
/*  144 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy() {
/*  149 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy(int index, int length) {
/*  154 */     checkIndex(index, length);
/*  155 */     return this.buffer.copy(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardReadBytes() {
/*  160 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf ensureWritable(int writableBytes) {
/*  165 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  170 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf duplicate() {
/*  175 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedDuplicate() {
/*  180 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int index) {
/*  185 */     checkIndex(index, 1);
/*  186 */     return this.buffer.getBoolean(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  191 */     checkIndex(index, 1);
/*  192 */     return this.buffer.getByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getUnsignedByte(int index) {
/*  197 */     checkIndex(index, 1);
/*  198 */     return this.buffer.getUnsignedByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/*  203 */     checkIndex(index, length);
/*  204 */     this.buffer.getBytes(index, dst, dstIndex, length);
/*  205 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, byte[] dst) {
/*  210 */     checkIndex(index, dst.length);
/*  211 */     this.buffer.getBytes(index, dst);
/*  212 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/*  217 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  222 */     checkIndex(index, length);
/*  223 */     this.buffer.getBytes(index, dst, dstIndex, length);
/*  224 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
/*  229 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst) {
/*  234 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, GatheringByteChannel out, int length) {
/*  239 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, FileChannel out, long position, int length) {
/*  244 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, OutputStream out, int length) {
/*  249 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(int index) {
/*  254 */     checkIndex(index, 4);
/*  255 */     return this.buffer.getInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntLE(int index) {
/*  260 */     checkIndex(index, 4);
/*  261 */     return this.buffer.getIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedInt(int index) {
/*  266 */     checkIndex(index, 4);
/*  267 */     return this.buffer.getUnsignedInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedIntLE(int index) {
/*  272 */     checkIndex(index, 4);
/*  273 */     return this.buffer.getUnsignedIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(int index) {
/*  278 */     checkIndex(index, 8);
/*  279 */     return this.buffer.getLong(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLongLE(int index) {
/*  284 */     checkIndex(index, 8);
/*  285 */     return this.buffer.getLongLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMedium(int index) {
/*  290 */     checkIndex(index, 3);
/*  291 */     return this.buffer.getMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMediumLE(int index) {
/*  296 */     checkIndex(index, 3);
/*  297 */     return this.buffer.getMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMedium(int index) {
/*  302 */     checkIndex(index, 3);
/*  303 */     return this.buffer.getUnsignedMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMediumLE(int index) {
/*  308 */     checkIndex(index, 3);
/*  309 */     return this.buffer.getUnsignedMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(int index) {
/*  314 */     checkIndex(index, 2);
/*  315 */     return this.buffer.getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShortLE(int index) {
/*  320 */     checkIndex(index, 2);
/*  321 */     return this.buffer.getShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShort(int index) {
/*  326 */     checkIndex(index, 2);
/*  327 */     return this.buffer.getUnsignedShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShortLE(int index) {
/*  332 */     checkIndex(index, 2);
/*  333 */     return this.buffer.getUnsignedShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public char getChar(int index) {
/*  338 */     checkIndex(index, 2);
/*  339 */     return this.buffer.getChar(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(int index) {
/*  344 */     checkIndex(index, 4);
/*  345 */     return this.buffer.getFloat(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(int index) {
/*  350 */     checkIndex(index, 8);
/*  351 */     return this.buffer.getDouble(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/*  356 */     checkIndex(index, length);
/*  357 */     return this.buffer.getCharSequence(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  362 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int indexOf(int fromIndex, int toIndex, byte value) {
/*  367 */     if (fromIndex == toIndex) {
/*  368 */       return -1;
/*      */     }
/*      */     
/*  371 */     if (Math.max(fromIndex, toIndex) > this.buffer.writerIndex()) {
/*  372 */       throw REPLAY;
/*      */     }
/*      */     
/*  375 */     return this.buffer.indexOf(fromIndex, toIndex, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(byte value) {
/*  380 */     int bytes = this.buffer.bytesBefore(value);
/*  381 */     if (bytes < 0) {
/*  382 */       throw REPLAY;
/*      */     }
/*  384 */     return bytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int length, byte value) {
/*  389 */     return bytesBefore(this.buffer.readerIndex(), length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int index, int length, byte value) {
/*  394 */     int writerIndex = this.buffer.writerIndex();
/*  395 */     if (index >= writerIndex) {
/*  396 */       throw REPLAY;
/*      */     }
/*      */     
/*  399 */     if (index <= writerIndex - length) {
/*  400 */       return this.buffer.bytesBefore(index, length, value);
/*      */     }
/*      */     
/*  403 */     int res = this.buffer.bytesBefore(index, writerIndex - index, value);
/*  404 */     if (res < 0) {
/*  405 */       throw REPLAY;
/*      */     }
/*  407 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int forEachByte(ByteProcessor processor) {
/*  413 */     int ret = this.buffer.forEachByte(processor);
/*  414 */     if (ret < 0) {
/*  415 */       throw REPLAY;
/*      */     }
/*  417 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int forEachByte(int index, int length, ByteProcessor processor) {
/*  423 */     int writerIndex = this.buffer.writerIndex();
/*  424 */     if (index >= writerIndex) {
/*  425 */       throw REPLAY;
/*      */     }
/*      */     
/*  428 */     if (index <= writerIndex - length) {
/*  429 */       return this.buffer.forEachByte(index, length, processor);
/*      */     }
/*      */     
/*  432 */     int ret = this.buffer.forEachByte(index, writerIndex - index, processor);
/*  433 */     if (ret < 0) {
/*  434 */       throw REPLAY;
/*      */     }
/*  436 */     return ret;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(ByteProcessor processor) {
/*  442 */     if (this.terminated) {
/*  443 */       return this.buffer.forEachByteDesc(processor);
/*      */     }
/*  445 */     throw reject();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/*  451 */     if (index + length > this.buffer.writerIndex()) {
/*  452 */       throw REPLAY;
/*      */     }
/*      */     
/*  455 */     return this.buffer.forEachByteDesc(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markReaderIndex() {
/*  460 */     this.buffer.markReaderIndex();
/*  461 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markWriterIndex() {
/*  466 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteOrder order() {
/*  471 */     return this.buffer.order();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf order(ByteOrder endianness) {
/*  476 */     if (ObjectUtil.checkNotNull(endianness, "endianness") == order()) {
/*  477 */       return this;
/*      */     }
/*      */     
/*  480 */     SwappedByteBuf swapped = this.swapped;
/*  481 */     if (swapped == null) {
/*  482 */       this.swapped = swapped = new SwappedByteBuf(this);
/*      */     }
/*  484 */     return (ByteBuf)swapped;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable() {
/*  489 */     return (!this.terminated || this.buffer.isReadable());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable(int size) {
/*  494 */     return (!this.terminated || this.buffer.isReadable(size));
/*      */   }
/*      */ 
/*      */   
/*      */   public int readableBytes() {
/*  499 */     if (this.terminated) {
/*  500 */       return this.buffer.readableBytes();
/*      */     }
/*  502 */     return Integer.MAX_VALUE - this.buffer.readerIndex();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean readBoolean() {
/*  508 */     checkReadableBytes(1);
/*  509 */     return this.buffer.readBoolean();
/*      */   }
/*      */ 
/*      */   
/*      */   public byte readByte() {
/*  514 */     checkReadableBytes(1);
/*  515 */     return this.buffer.readByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readUnsignedByte() {
/*  520 */     checkReadableBytes(1);
/*  521 */     return this.buffer.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/*  526 */     checkReadableBytes(length);
/*  527 */     this.buffer.readBytes(dst, dstIndex, length);
/*  528 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst) {
/*  533 */     checkReadableBytes(dst.length);
/*  534 */     this.buffer.readBytes(dst);
/*  535 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuffer dst) {
/*  540 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/*  545 */     checkReadableBytes(length);
/*  546 */     this.buffer.readBytes(dst, dstIndex, length);
/*  547 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int length) {
/*  552 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst) {
/*  557 */     checkReadableBytes(dst.writableBytes());
/*  558 */     this.buffer.readBytes(dst);
/*  559 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(GatheringByteChannel out, int length) {
/*  564 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(FileChannel out, long position, int length) {
/*  569 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(int length) {
/*  574 */     checkReadableBytes(length);
/*  575 */     return this.buffer.readBytes(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readSlice(int length) {
/*  580 */     checkReadableBytes(length);
/*  581 */     return this.buffer.readSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readRetainedSlice(int length) {
/*  586 */     checkReadableBytes(length);
/*  587 */     return this.buffer.readRetainedSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(OutputStream out, int length) {
/*  592 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readerIndex() {
/*  597 */     return this.buffer.readerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readerIndex(int readerIndex) {
/*  602 */     this.buffer.readerIndex(readerIndex);
/*  603 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt() {
/*  608 */     checkReadableBytes(4);
/*  609 */     return this.buffer.readInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readIntLE() {
/*  614 */     checkReadableBytes(4);
/*  615 */     return this.buffer.readIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedInt() {
/*  620 */     checkReadableBytes(4);
/*  621 */     return this.buffer.readUnsignedInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedIntLE() {
/*  626 */     checkReadableBytes(4);
/*  627 */     return this.buffer.readUnsignedIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLong() {
/*  632 */     checkReadableBytes(8);
/*  633 */     return this.buffer.readLong();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLongLE() {
/*  638 */     checkReadableBytes(8);
/*  639 */     return this.buffer.readLongLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMedium() {
/*  644 */     checkReadableBytes(3);
/*  645 */     return this.buffer.readMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMediumLE() {
/*  650 */     checkReadableBytes(3);
/*  651 */     return this.buffer.readMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMedium() {
/*  656 */     checkReadableBytes(3);
/*  657 */     return this.buffer.readUnsignedMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMediumLE() {
/*  662 */     checkReadableBytes(3);
/*  663 */     return this.buffer.readUnsignedMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShort() {
/*  668 */     checkReadableBytes(2);
/*  669 */     return this.buffer.readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShortLE() {
/*  674 */     checkReadableBytes(2);
/*  675 */     return this.buffer.readShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShort() {
/*  680 */     checkReadableBytes(2);
/*  681 */     return this.buffer.readUnsignedShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShortLE() {
/*  686 */     checkReadableBytes(2);
/*  687 */     return this.buffer.readUnsignedShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public char readChar() {
/*  692 */     checkReadableBytes(2);
/*  693 */     return this.buffer.readChar();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() {
/*  698 */     checkReadableBytes(4);
/*  699 */     return this.buffer.readFloat();
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() {
/*  704 */     checkReadableBytes(8);
/*  705 */     return this.buffer.readDouble();
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence readCharSequence(int length, Charset charset) {
/*  710 */     checkReadableBytes(length);
/*  711 */     return this.buffer.readCharSequence(length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetReaderIndex() {
/*  716 */     this.buffer.resetReaderIndex();
/*  717 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetWriterIndex() {
/*  722 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBoolean(int index, boolean value) {
/*  727 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setByte(int index, int value) {
/*  732 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/*  737 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, byte[] src) {
/*  742 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuffer src) {
/*  747 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/*  752 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src, int length) {
/*  757 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src) {
/*  762 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, InputStream in, int length) {
/*  767 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setZero(int index, int length) {
/*  772 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, ScatteringByteChannel in, int length) {
/*  777 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, FileChannel in, long position, int length) {
/*  782 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIndex(int readerIndex, int writerIndex) {
/*  787 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setInt(int index, int value) {
/*  792 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIntLE(int index, int value) {
/*  797 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLong(int index, long value) {
/*  802 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLongLE(int index, long value) {
/*  807 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMedium(int index, int value) {
/*  812 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMediumLE(int index, int value) {
/*  817 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShort(int index, int value) {
/*  822 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShortLE(int index, int value) {
/*  827 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setChar(int index, int value) {
/*  832 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setFloat(int index, float value) {
/*  837 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setDouble(int index, double value) {
/*  842 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf skipBytes(int length) {
/*  847 */     checkReadableBytes(length);
/*  848 */     this.buffer.skipBytes(length);
/*  849 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice() {
/*  854 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice() {
/*  859 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice(int index, int length) {
/*  864 */     checkIndex(index, length);
/*  865 */     return this.buffer.slice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice(int index, int length) {
/*  870 */     checkIndex(index, length);
/*  871 */     return this.buffer.slice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int nioBufferCount() {
/*  876 */     return this.buffer.nioBufferCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer() {
/*  881 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer(int index, int length) {
/*  886 */     checkIndex(index, length);
/*  887 */     return this.buffer.nioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/*  892 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  897 */     checkIndex(index, length);
/*  898 */     return this.buffer.nioBuffers(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer internalNioBuffer(int index, int length) {
/*  903 */     checkIndex(index, length);
/*  904 */     return this.buffer.internalNioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(int index, int length, Charset charset) {
/*  909 */     checkIndex(index, length);
/*  910 */     return this.buffer.toString(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(Charset charsetName) {
/*  915 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/*  920 */     return StringUtil.simpleClassName(this) + '(' + "ridx=" + 
/*      */       
/*  922 */       readerIndex() + ", widx=" + 
/*      */ 
/*      */       
/*  925 */       writerIndex() + ')';
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWritable() {
/*  931 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable(int size) {
/*  936 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writableBytes() {
/*  941 */     return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxWritableBytes() {
/*  946 */     return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBoolean(boolean value) {
/*  951 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeByte(int value) {
/*  956 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/*  961 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src) {
/*  966 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuffer src) {
/*  971 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/*  976 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int length) {
/*  981 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src) {
/*  986 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(InputStream in, int length) {
/*  991 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(ScatteringByteChannel in, int length) {
/*  996 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(FileChannel in, long position, int length) {
/* 1001 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeInt(int value) {
/* 1006 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeIntLE(int value) {
/* 1011 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLong(long value) {
/* 1016 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLongLE(long value) {
/* 1021 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMedium(int value) {
/* 1026 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMediumLE(int value) {
/* 1031 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeZero(int length) {
/* 1036 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writerIndex() {
/* 1041 */     return this.buffer.writerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writerIndex(int writerIndex) {
/* 1046 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShort(int value) {
/* 1051 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShortLE(int value) {
/* 1056 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeChar(int value) {
/* 1061 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeFloat(float value) {
/* 1066 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeDouble(double value) {
/* 1071 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/* 1076 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/* 1081 */     throw reject();
/*      */   }
/*      */   
/*      */   private void checkIndex(int index, int length) {
/* 1085 */     if (index + length > this.buffer.writerIndex()) {
/* 1086 */       throw REPLAY;
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkReadableBytes(int readableBytes) {
/* 1091 */     if (this.buffer.readableBytes() < readableBytes) {
/* 1092 */       throw REPLAY;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardSomeReadBytes() {
/* 1098 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public int refCnt() {
/* 1103 */     return this.buffer.refCnt();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retain() {
/* 1108 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retain(int increment) {
/* 1113 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf touch() {
/* 1118 */     this.buffer.touch();
/* 1119 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf touch(Object hint) {
/* 1124 */     this.buffer.touch(hint);
/* 1125 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release() {
/* 1130 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release(int decrement) {
/* 1135 */     throw reject();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf unwrap() {
/* 1140 */     throw reject();
/*      */   }
/*      */   
/*      */   private static UnsupportedOperationException reject() {
/* 1144 */     return new UnsupportedOperationException("not a replayable operation");
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\ReplayingDecoderByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */