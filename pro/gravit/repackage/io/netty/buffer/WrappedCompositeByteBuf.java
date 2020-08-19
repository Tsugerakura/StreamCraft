/*      */ package pro.gravit.repackage.io.netty.buffer;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.GatheringByteChannel;
/*      */ import java.nio.channels.ScatteringByteChannel;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
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
/*      */ class WrappedCompositeByteBuf
/*      */   extends CompositeByteBuf
/*      */ {
/*      */   private final CompositeByteBuf wrapped;
/*      */   
/*      */   WrappedCompositeByteBuf(CompositeByteBuf wrapped) {
/*   37 */     super(wrapped.alloc());
/*   38 */     this.wrapped = wrapped;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release() {
/*   43 */     return this.wrapped.release();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release(int decrement) {
/*   48 */     return this.wrapped.release(decrement);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int maxCapacity() {
/*   53 */     return this.wrapped.maxCapacity();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int readerIndex() {
/*   58 */     return this.wrapped.readerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int writerIndex() {
/*   63 */     return this.wrapped.writerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isReadable() {
/*   68 */     return this.wrapped.isReadable();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isReadable(int numBytes) {
/*   73 */     return this.wrapped.isReadable(numBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isWritable() {
/*   78 */     return this.wrapped.isWritable();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isWritable(int numBytes) {
/*   83 */     return this.wrapped.isWritable(numBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int readableBytes() {
/*   88 */     return this.wrapped.readableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int writableBytes() {
/*   93 */     return this.wrapped.writableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int maxWritableBytes() {
/*   98 */     return this.wrapped.maxWritableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxFastWritableBytes() {
/*  103 */     return this.wrapped.maxFastWritableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  108 */     return this.wrapped.ensureWritable(minWritableBytes, force);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf order(ByteOrder endianness) {
/*  113 */     return this.wrapped.order(endianness);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int index) {
/*  118 */     return this.wrapped.getBoolean(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getUnsignedByte(int index) {
/*  123 */     return this.wrapped.getUnsignedByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(int index) {
/*  128 */     return this.wrapped.getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShortLE(int index) {
/*  133 */     return this.wrapped.getShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShort(int index) {
/*  138 */     return this.wrapped.getUnsignedShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShortLE(int index) {
/*  143 */     return this.wrapped.getUnsignedShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMedium(int index) {
/*  148 */     return this.wrapped.getUnsignedMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMediumLE(int index) {
/*  153 */     return this.wrapped.getUnsignedMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMedium(int index) {
/*  158 */     return this.wrapped.getMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMediumLE(int index) {
/*  163 */     return this.wrapped.getMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(int index) {
/*  168 */     return this.wrapped.getInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntLE(int index) {
/*  173 */     return this.wrapped.getIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedInt(int index) {
/*  178 */     return this.wrapped.getUnsignedInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedIntLE(int index) {
/*  183 */     return this.wrapped.getUnsignedIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(int index) {
/*  188 */     return this.wrapped.getLong(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLongLE(int index) {
/*  193 */     return this.wrapped.getLongLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public char getChar(int index) {
/*  198 */     return this.wrapped.getChar(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(int index) {
/*  203 */     return this.wrapped.getFloat(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(int index) {
/*  208 */     return this.wrapped.getDouble(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShortLE(int index, int value) {
/*  213 */     return this.wrapped.setShortLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMediumLE(int index, int value) {
/*  218 */     return this.wrapped.setMediumLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIntLE(int index, int value) {
/*  223 */     return this.wrapped.setIntLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLongLE(int index, long value) {
/*  228 */     return this.wrapped.setLongLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte readByte() {
/*  233 */     return this.wrapped.readByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean readBoolean() {
/*  238 */     return this.wrapped.readBoolean();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readUnsignedByte() {
/*  243 */     return this.wrapped.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShort() {
/*  248 */     return this.wrapped.readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShortLE() {
/*  253 */     return this.wrapped.readShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShort() {
/*  258 */     return this.wrapped.readUnsignedShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShortLE() {
/*  263 */     return this.wrapped.readUnsignedShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMedium() {
/*  268 */     return this.wrapped.readMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMediumLE() {
/*  273 */     return this.wrapped.readMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMedium() {
/*  278 */     return this.wrapped.readUnsignedMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMediumLE() {
/*  283 */     return this.wrapped.readUnsignedMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt() {
/*  288 */     return this.wrapped.readInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readIntLE() {
/*  293 */     return this.wrapped.readIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedInt() {
/*  298 */     return this.wrapped.readUnsignedInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedIntLE() {
/*  303 */     return this.wrapped.readUnsignedIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLong() {
/*  308 */     return this.wrapped.readLong();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLongLE() {
/*  313 */     return this.wrapped.readLongLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public char readChar() {
/*  318 */     return this.wrapped.readChar();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() {
/*  323 */     return this.wrapped.readFloat();
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() {
/*  328 */     return this.wrapped.readDouble();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(int length) {
/*  333 */     return this.wrapped.readBytes(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice() {
/*  338 */     return this.wrapped.slice();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice() {
/*  343 */     return this.wrapped.retainedSlice();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice(int index, int length) {
/*  348 */     return this.wrapped.slice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice(int index, int length) {
/*  353 */     return this.wrapped.retainedSlice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer() {
/*  358 */     return this.wrapped.nioBuffer();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(Charset charset) {
/*  363 */     return this.wrapped.toString(charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(int index, int length, Charset charset) {
/*  368 */     return this.wrapped.toString(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int indexOf(int fromIndex, int toIndex, byte value) {
/*  373 */     return this.wrapped.indexOf(fromIndex, toIndex, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(byte value) {
/*  378 */     return this.wrapped.bytesBefore(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int length, byte value) {
/*  383 */     return this.wrapped.bytesBefore(length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int index, int length, byte value) {
/*  388 */     return this.wrapped.bytesBefore(index, length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(ByteProcessor processor) {
/*  393 */     return this.wrapped.forEachByte(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(int index, int length, ByteProcessor processor) {
/*  398 */     return this.wrapped.forEachByte(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(ByteProcessor processor) {
/*  403 */     return this.wrapped.forEachByteDesc(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/*  408 */     return this.wrapped.forEachByteDesc(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int hashCode() {
/*  413 */     return this.wrapped.hashCode();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean equals(Object o) {
/*  418 */     return this.wrapped.equals(o);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int compareTo(ByteBuf that) {
/*  423 */     return this.wrapped.compareTo(that);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int refCnt() {
/*  428 */     return this.wrapped.refCnt();
/*      */   }
/*      */ 
/*      */   
/*      */   final boolean isAccessible() {
/*  433 */     return this.wrapped.isAccessible();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf duplicate() {
/*  438 */     return this.wrapped.duplicate();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedDuplicate() {
/*  443 */     return this.wrapped.retainedDuplicate();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readSlice(int length) {
/*  448 */     return this.wrapped.readSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readRetainedSlice(int length) {
/*  453 */     return this.wrapped.readRetainedSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/*  458 */     return this.wrapped.readBytes(out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShortLE(int value) {
/*  463 */     return this.wrapped.writeShortLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMediumLE(int value) {
/*  468 */     return this.wrapped.writeMediumLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeIntLE(int value) {
/*  473 */     return this.wrapped.writeIntLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLongLE(long value) {
/*  478 */     return this.wrapped.writeLongLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(InputStream in, int length) throws IOException {
/*  483 */     return this.wrapped.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
/*  488 */     return this.wrapped.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy() {
/*  493 */     return this.wrapped.copy();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(ByteBuf buffer) {
/*  498 */     this.wrapped.addComponent(buffer);
/*  499 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(ByteBuf... buffers) {
/*  504 */     this.wrapped.addComponents(buffers);
/*  505 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
/*  510 */     this.wrapped.addComponents(buffers);
/*  511 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
/*  516 */     this.wrapped.addComponent(cIndex, buffer);
/*  517 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, ByteBuf... buffers) {
/*  522 */     this.wrapped.addComponents(cIndex, buffers);
/*  523 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
/*  528 */     this.wrapped.addComponents(cIndex, buffers);
/*  529 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
/*  534 */     this.wrapped.addComponent(increaseWriterIndex, buffer);
/*  535 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf... buffers) {
/*  540 */     this.wrapped.addComponents(increaseWriterIndex, buffers);
/*  541 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
/*  546 */     this.wrapped.addComponents(increaseWriterIndex, buffers);
/*  547 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
/*  552 */     this.wrapped.addComponent(increaseWriterIndex, cIndex, buffer);
/*  553 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
/*  558 */     this.wrapped.addFlattenedComponents(increaseWriterIndex, buffer);
/*  559 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponent(int cIndex) {
/*  564 */     this.wrapped.removeComponent(cIndex);
/*  565 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
/*  570 */     this.wrapped.removeComponents(cIndex, numComponents);
/*  571 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator<ByteBuf> iterator() {
/*  576 */     return this.wrapped.iterator();
/*      */   }
/*      */ 
/*      */   
/*      */   public List<ByteBuf> decompose(int offset, int length) {
/*  581 */     return this.wrapped.decompose(offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isDirect() {
/*  586 */     return this.wrapped.isDirect();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean hasArray() {
/*  591 */     return this.wrapped.hasArray();
/*      */   }
/*      */ 
/*      */   
/*      */   public final byte[] array() {
/*  596 */     return this.wrapped.array();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int arrayOffset() {
/*  601 */     return this.wrapped.arrayOffset();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean hasMemoryAddress() {
/*  606 */     return this.wrapped.hasMemoryAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public final long memoryAddress() {
/*  611 */     return this.wrapped.memoryAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int capacity() {
/*  616 */     return this.wrapped.capacity();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf capacity(int newCapacity) {
/*  621 */     this.wrapped.capacity(newCapacity);
/*  622 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBufAllocator alloc() {
/*  627 */     return this.wrapped.alloc();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteOrder order() {
/*  632 */     return this.wrapped.order();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int numComponents() {
/*  637 */     return this.wrapped.numComponents();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int maxNumComponents() {
/*  642 */     return this.wrapped.maxNumComponents();
/*      */   }
/*      */ 
/*      */   
/*      */   public final int toComponentIndex(int offset) {
/*  647 */     return this.wrapped.toComponentIndex(offset);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int toByteIndex(int cIndex) {
/*  652 */     return this.wrapped.toByteIndex(cIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  657 */     return this.wrapped.getByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final byte _getByte(int index) {
/*  662 */     return this.wrapped._getByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final short _getShort(int index) {
/*  667 */     return this.wrapped._getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final short _getShortLE(int index) {
/*  672 */     return this.wrapped._getShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _getUnsignedMedium(int index) {
/*  677 */     return this.wrapped._getUnsignedMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _getUnsignedMediumLE(int index) {
/*  682 */     return this.wrapped._getUnsignedMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _getInt(int index) {
/*  687 */     return this.wrapped._getInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _getIntLE(int index) {
/*  692 */     return this.wrapped._getIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final long _getLong(int index) {
/*  697 */     return this.wrapped._getLong(index);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final long _getLongLE(int index) {
/*  702 */     return this.wrapped._getLongLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/*  707 */     this.wrapped.getBytes(index, dst, dstIndex, length);
/*  708 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
/*  713 */     this.wrapped.getBytes(index, dst);
/*  714 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  719 */     this.wrapped.getBytes(index, dst, dstIndex, length);
/*  720 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/*  725 */     return this.wrapped.getBytes(index, out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/*  730 */     this.wrapped.getBytes(index, out, length);
/*  731 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setByte(int index, int value) {
/*  736 */     this.wrapped.setByte(index, value);
/*  737 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setByte(int index, int value) {
/*  742 */     this.wrapped._setByte(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setShort(int index, int value) {
/*  747 */     this.wrapped.setShort(index, value);
/*  748 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setShort(int index, int value) {
/*  753 */     this.wrapped._setShort(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setShortLE(int index, int value) {
/*  758 */     this.wrapped._setShortLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setMedium(int index, int value) {
/*  763 */     this.wrapped.setMedium(index, value);
/*  764 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setMedium(int index, int value) {
/*  769 */     this.wrapped._setMedium(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setMediumLE(int index, int value) {
/*  774 */     this.wrapped._setMediumLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setInt(int index, int value) {
/*  779 */     this.wrapped.setInt(index, value);
/*  780 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setInt(int index, int value) {
/*  785 */     this.wrapped._setInt(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setIntLE(int index, int value) {
/*  790 */     this.wrapped._setIntLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setLong(int index, long value) {
/*  795 */     this.wrapped.setLong(index, value);
/*  796 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setLong(int index, long value) {
/*  801 */     this.wrapped._setLong(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _setLongLE(int index, long value) {
/*  806 */     this.wrapped._setLongLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/*  811 */     this.wrapped.setBytes(index, src, srcIndex, length);
/*  812 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuffer src) {
/*  817 */     this.wrapped.setBytes(index, src);
/*  818 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/*  823 */     this.wrapped.setBytes(index, src, srcIndex, length);
/*  824 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, InputStream in, int length) throws IOException {
/*  829 */     return this.wrapped.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/*  834 */     return this.wrapped.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy(int index, int length) {
/*  839 */     return this.wrapped.copy(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBuf component(int cIndex) {
/*  844 */     return this.wrapped.component(cIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBuf componentAtOffset(int offset) {
/*  849 */     return this.wrapped.componentAtOffset(offset);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBuf internalComponent(int cIndex) {
/*  854 */     return this.wrapped.internalComponent(cIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBuf internalComponentAtOffset(int offset) {
/*  859 */     return this.wrapped.internalComponentAtOffset(offset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int nioBufferCount() {
/*  864 */     return this.wrapped.nioBufferCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer internalNioBuffer(int index, int length) {
/*  869 */     return this.wrapped.internalNioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer(int index, int length) {
/*  874 */     return this.wrapped.nioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  879 */     return this.wrapped.nioBuffers(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate() {
/*  884 */     this.wrapped.consolidate();
/*  885 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate(int cIndex, int numComponents) {
/*  890 */     this.wrapped.consolidate(cIndex, numComponents);
/*  891 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadComponents() {
/*  896 */     this.wrapped.discardReadComponents();
/*  897 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadBytes() {
/*  902 */     this.wrapped.discardReadBytes();
/*  903 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final String toString() {
/*  908 */     return this.wrapped.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf readerIndex(int readerIndex) {
/*  913 */     this.wrapped.readerIndex(readerIndex);
/*  914 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf writerIndex(int writerIndex) {
/*  919 */     this.wrapped.writerIndex(writerIndex);
/*  920 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
/*  925 */     this.wrapped.setIndex(readerIndex, writerIndex);
/*  926 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf clear() {
/*  931 */     this.wrapped.clear();
/*  932 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf markReaderIndex() {
/*  937 */     this.wrapped.markReaderIndex();
/*  938 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf resetReaderIndex() {
/*  943 */     this.wrapped.resetReaderIndex();
/*  944 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf markWriterIndex() {
/*  949 */     this.wrapped.markWriterIndex();
/*  950 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final CompositeByteBuf resetWriterIndex() {
/*  955 */     this.wrapped.resetWriterIndex();
/*  956 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf ensureWritable(int minWritableBytes) {
/*  961 */     this.wrapped.ensureWritable(minWritableBytes);
/*  962 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst) {
/*  967 */     this.wrapped.getBytes(index, dst);
/*  968 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
/*  973 */     this.wrapped.getBytes(index, dst, length);
/*  974 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst) {
/*  979 */     this.wrapped.getBytes(index, dst);
/*  980 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBoolean(int index, boolean value) {
/*  985 */     this.wrapped.setBoolean(index, value);
/*  986 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setChar(int index, int value) {
/*  991 */     this.wrapped.setChar(index, value);
/*  992 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setFloat(int index, float value) {
/*  997 */     this.wrapped.setFloat(index, value);
/*  998 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setDouble(int index, double value) {
/* 1003 */     this.wrapped.setDouble(index, value);
/* 1004 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src) {
/* 1009 */     this.wrapped.setBytes(index, src);
/* 1010 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
/* 1015 */     this.wrapped.setBytes(index, src, length);
/* 1016 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src) {
/* 1021 */     this.wrapped.setBytes(index, src);
/* 1022 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setZero(int index, int length) {
/* 1027 */     this.wrapped.setZero(index, length);
/* 1028 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst) {
/* 1033 */     this.wrapped.readBytes(dst);
/* 1034 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int length) {
/* 1039 */     this.wrapped.readBytes(dst, length);
/* 1040 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/* 1045 */     this.wrapped.readBytes(dst, dstIndex, length);
/* 1046 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst) {
/* 1051 */     this.wrapped.readBytes(dst);
/* 1052 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/* 1057 */     this.wrapped.readBytes(dst, dstIndex, length);
/* 1058 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuffer dst) {
/* 1063 */     this.wrapped.readBytes(dst);
/* 1064 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
/* 1069 */     this.wrapped.readBytes(out, length);
/* 1070 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 1075 */     return this.wrapped.getBytes(index, out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 1080 */     return this.wrapped.setBytes(index, in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/* 1085 */     return this.wrapped.isReadOnly();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf asReadOnly() {
/* 1090 */     return this.wrapped.asReadOnly();
/*      */   }
/*      */ 
/*      */   
/*      */   protected SwappedByteBuf newSwappedByteBuf() {
/* 1095 */     return this.wrapped.newSwappedByteBuf();
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/* 1100 */     return this.wrapped.getCharSequence(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence readCharSequence(int length, Charset charset) {
/* 1105 */     return this.wrapped.readCharSequence(length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/* 1110 */     return this.wrapped.setCharSequence(index, sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/* 1115 */     return this.wrapped.readBytes(out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(FileChannel in, long position, int length) throws IOException {
/* 1120 */     return this.wrapped.writeBytes(in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/* 1125 */     return this.wrapped.writeCharSequence(sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf skipBytes(int length) {
/* 1130 */     this.wrapped.skipBytes(length);
/* 1131 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBoolean(boolean value) {
/* 1136 */     this.wrapped.writeBoolean(value);
/* 1137 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeByte(int value) {
/* 1142 */     this.wrapped.writeByte(value);
/* 1143 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeShort(int value) {
/* 1148 */     this.wrapped.writeShort(value);
/* 1149 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeMedium(int value) {
/* 1154 */     this.wrapped.writeMedium(value);
/* 1155 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeInt(int value) {
/* 1160 */     this.wrapped.writeInt(value);
/* 1161 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeLong(long value) {
/* 1166 */     this.wrapped.writeLong(value);
/* 1167 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeChar(int value) {
/* 1172 */     this.wrapped.writeChar(value);
/* 1173 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeFloat(float value) {
/* 1178 */     this.wrapped.writeFloat(value);
/* 1179 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeDouble(double value) {
/* 1184 */     this.wrapped.writeDouble(value);
/* 1185 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src) {
/* 1190 */     this.wrapped.writeBytes(src);
/* 1191 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int length) {
/* 1196 */     this.wrapped.writeBytes(src, length);
/* 1197 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/* 1202 */     this.wrapped.writeBytes(src, srcIndex, length);
/* 1203 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src) {
/* 1208 */     this.wrapped.writeBytes(src);
/* 1209 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/* 1214 */     this.wrapped.writeBytes(src, srcIndex, length);
/* 1215 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuffer src) {
/* 1220 */     this.wrapped.writeBytes(src);
/* 1221 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeZero(int length) {
/* 1226 */     this.wrapped.writeZero(length);
/* 1227 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain(int increment) {
/* 1232 */     this.wrapped.retain(increment);
/* 1233 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain() {
/* 1238 */     this.wrapped.retain();
/* 1239 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch() {
/* 1244 */     this.wrapped.touch();
/* 1245 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch(Object hint) {
/* 1250 */     this.wrapped.touch(hint);
/* 1251 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/* 1256 */     return this.wrapped.nioBuffers();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardSomeReadBytes() {
/* 1261 */     this.wrapped.discardSomeReadBytes();
/* 1262 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void deallocate() {
/* 1267 */     this.wrapped.deallocate();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ByteBuf unwrap() {
/* 1272 */     return this.wrapped;
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\WrappedCompositeByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */