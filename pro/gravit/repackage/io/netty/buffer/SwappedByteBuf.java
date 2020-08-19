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
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ @Deprecated
/*      */ public class SwappedByteBuf
/*      */   extends ByteBuf
/*      */ {
/*      */   private final ByteBuf buf;
/*      */   private final ByteOrder order;
/*      */   
/*      */   public SwappedByteBuf(ByteBuf buf) {
/*   44 */     this.buf = (ByteBuf)ObjectUtil.checkNotNull(buf, "buf");
/*   45 */     if (buf.order() == ByteOrder.BIG_ENDIAN) {
/*   46 */       this.order = ByteOrder.LITTLE_ENDIAN;
/*      */     } else {
/*   48 */       this.order = ByteOrder.BIG_ENDIAN;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteOrder order() {
/*   54 */     return this.order;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf order(ByteOrder endianness) {
/*   59 */     if (ObjectUtil.checkNotNull(endianness, "endianness") == this.order) {
/*   60 */       return this;
/*      */     }
/*   62 */     return this.buf;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf unwrap() {
/*   67 */     return this.buf;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBufAllocator alloc() {
/*   72 */     return this.buf.alloc();
/*      */   }
/*      */ 
/*      */   
/*      */   public int capacity() {
/*   77 */     return this.buf.capacity();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf capacity(int newCapacity) {
/*   82 */     this.buf.capacity(newCapacity);
/*   83 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxCapacity() {
/*   88 */     return this.buf.maxCapacity();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*   93 */     return this.buf.isReadOnly();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf asReadOnly() {
/*   98 */     return Unpooled.unmodifiableBuffer(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isDirect() {
/*  103 */     return this.buf.isDirect();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readerIndex() {
/*  108 */     return this.buf.readerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readerIndex(int readerIndex) {
/*  113 */     this.buf.readerIndex(readerIndex);
/*  114 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writerIndex() {
/*  119 */     return this.buf.writerIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writerIndex(int writerIndex) {
/*  124 */     this.buf.writerIndex(writerIndex);
/*  125 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIndex(int readerIndex, int writerIndex) {
/*  130 */     this.buf.setIndex(readerIndex, writerIndex);
/*  131 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readableBytes() {
/*  136 */     return this.buf.readableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int writableBytes() {
/*  141 */     return this.buf.writableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxWritableBytes() {
/*  146 */     return this.buf.maxWritableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxFastWritableBytes() {
/*  151 */     return this.buf.maxFastWritableBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable() {
/*  156 */     return this.buf.isReadable();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable(int size) {
/*  161 */     return this.buf.isReadable(size);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable() {
/*  166 */     return this.buf.isWritable();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable(int size) {
/*  171 */     return this.buf.isWritable(size);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf clear() {
/*  176 */     this.buf.clear();
/*  177 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markReaderIndex() {
/*  182 */     this.buf.markReaderIndex();
/*  183 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetReaderIndex() {
/*  188 */     this.buf.resetReaderIndex();
/*  189 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markWriterIndex() {
/*  194 */     this.buf.markWriterIndex();
/*  195 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetWriterIndex() {
/*  200 */     this.buf.resetWriterIndex();
/*  201 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardReadBytes() {
/*  206 */     this.buf.discardReadBytes();
/*  207 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardSomeReadBytes() {
/*  212 */     this.buf.discardSomeReadBytes();
/*  213 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf ensureWritable(int writableBytes) {
/*  218 */     this.buf.ensureWritable(writableBytes);
/*  219 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  224 */     return this.buf.ensureWritable(minWritableBytes, force);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int index) {
/*  229 */     return this.buf.getBoolean(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  234 */     return this.buf.getByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getUnsignedByte(int index) {
/*  239 */     return this.buf.getUnsignedByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(int index) {
/*  244 */     return ByteBufUtil.swapShort(this.buf.getShort(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShortLE(int index) {
/*  249 */     return this.buf.getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShort(int index) {
/*  254 */     return getShort(index) & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShortLE(int index) {
/*  259 */     return getShortLE(index) & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMedium(int index) {
/*  264 */     return ByteBufUtil.swapMedium(this.buf.getMedium(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMediumLE(int index) {
/*  269 */     return this.buf.getMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMedium(int index) {
/*  274 */     return getMedium(index) & 0xFFFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMediumLE(int index) {
/*  279 */     return getMediumLE(index) & 0xFFFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(int index) {
/*  284 */     return ByteBufUtil.swapInt(this.buf.getInt(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntLE(int index) {
/*  289 */     return this.buf.getInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedInt(int index) {
/*  294 */     return getInt(index) & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedIntLE(int index) {
/*  299 */     return getIntLE(index) & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(int index) {
/*  304 */     return ByteBufUtil.swapLong(this.buf.getLong(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLongLE(int index) {
/*  309 */     return this.buf.getLong(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public char getChar(int index) {
/*  314 */     return (char)getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(int index) {
/*  319 */     return Float.intBitsToFloat(getInt(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(int index) {
/*  324 */     return Double.longBitsToDouble(getLong(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst) {
/*  329 */     this.buf.getBytes(index, dst);
/*  330 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
/*  335 */     this.buf.getBytes(index, dst, length);
/*  336 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  341 */     this.buf.getBytes(index, dst, dstIndex, length);
/*  342 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, byte[] dst) {
/*  347 */     this.buf.getBytes(index, dst);
/*  348 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/*  353 */     this.buf.getBytes(index, dst, dstIndex, length);
/*  354 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/*  359 */     this.buf.getBytes(index, dst);
/*  360 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/*  365 */     this.buf.getBytes(index, out, length);
/*  366 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/*  371 */     return this.buf.getBytes(index, out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/*  376 */     return this.buf.getBytes(index, out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/*  381 */     return this.buf.getCharSequence(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBoolean(int index, boolean value) {
/*  386 */     this.buf.setBoolean(index, value);
/*  387 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setByte(int index, int value) {
/*  392 */     this.buf.setByte(index, value);
/*  393 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShort(int index, int value) {
/*  398 */     this.buf.setShort(index, ByteBufUtil.swapShort((short)value));
/*  399 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShortLE(int index, int value) {
/*  404 */     this.buf.setShort(index, (short)value);
/*  405 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMedium(int index, int value) {
/*  410 */     this.buf.setMedium(index, ByteBufUtil.swapMedium(value));
/*  411 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMediumLE(int index, int value) {
/*  416 */     this.buf.setMedium(index, value);
/*  417 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setInt(int index, int value) {
/*  422 */     this.buf.setInt(index, ByteBufUtil.swapInt(value));
/*  423 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIntLE(int index, int value) {
/*  428 */     this.buf.setInt(index, value);
/*  429 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLong(int index, long value) {
/*  434 */     this.buf.setLong(index, ByteBufUtil.swapLong(value));
/*  435 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLongLE(int index, long value) {
/*  440 */     this.buf.setLong(index, value);
/*  441 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setChar(int index, int value) {
/*  446 */     setShort(index, value);
/*  447 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setFloat(int index, float value) {
/*  452 */     setInt(index, Float.floatToRawIntBits(value));
/*  453 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setDouble(int index, double value) {
/*  458 */     setLong(index, Double.doubleToRawLongBits(value));
/*  459 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src) {
/*  464 */     this.buf.setBytes(index, src);
/*  465 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src, int length) {
/*  470 */     this.buf.setBytes(index, src, length);
/*  471 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/*  476 */     this.buf.setBytes(index, src, srcIndex, length);
/*  477 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, byte[] src) {
/*  482 */     this.buf.setBytes(index, src);
/*  483 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/*  488 */     this.buf.setBytes(index, src, srcIndex, length);
/*  489 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuffer src) {
/*  494 */     this.buf.setBytes(index, src);
/*  495 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, InputStream in, int length) throws IOException {
/*  500 */     return this.buf.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/*  505 */     return this.buf.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/*  510 */     return this.buf.setBytes(index, in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setZero(int index, int length) {
/*  515 */     this.buf.setZero(index, length);
/*  516 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/*  521 */     return this.buf.setCharSequence(index, sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean readBoolean() {
/*  526 */     return this.buf.readBoolean();
/*      */   }
/*      */ 
/*      */   
/*      */   public byte readByte() {
/*  531 */     return this.buf.readByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readUnsignedByte() {
/*  536 */     return this.buf.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShort() {
/*  541 */     return ByteBufUtil.swapShort(this.buf.readShort());
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShortLE() {
/*  546 */     return this.buf.readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShort() {
/*  551 */     return readShort() & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShortLE() {
/*  556 */     return readShortLE() & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMedium() {
/*  561 */     return ByteBufUtil.swapMedium(this.buf.readMedium());
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMediumLE() {
/*  566 */     return this.buf.readMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMedium() {
/*  571 */     return readMedium() & 0xFFFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMediumLE() {
/*  576 */     return readMediumLE() & 0xFFFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt() {
/*  581 */     return ByteBufUtil.swapInt(this.buf.readInt());
/*      */   }
/*      */ 
/*      */   
/*      */   public int readIntLE() {
/*  586 */     return this.buf.readInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedInt() {
/*  591 */     return readInt() & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedIntLE() {
/*  596 */     return readIntLE() & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLong() {
/*  601 */     return ByteBufUtil.swapLong(this.buf.readLong());
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLongLE() {
/*  606 */     return this.buf.readLong();
/*      */   }
/*      */ 
/*      */   
/*      */   public char readChar() {
/*  611 */     return (char)readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() {
/*  616 */     return Float.intBitsToFloat(readInt());
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() {
/*  621 */     return Double.longBitsToDouble(readLong());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(int length) {
/*  626 */     return this.buf.readBytes(length).order(order());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readSlice(int length) {
/*  631 */     return this.buf.readSlice(length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readRetainedSlice(int length) {
/*  636 */     return this.buf.readRetainedSlice(length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst) {
/*  641 */     this.buf.readBytes(dst);
/*  642 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int length) {
/*  647 */     this.buf.readBytes(dst, length);
/*  648 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/*  653 */     this.buf.readBytes(dst, dstIndex, length);
/*  654 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst) {
/*  659 */     this.buf.readBytes(dst);
/*  660 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/*  665 */     this.buf.readBytes(dst, dstIndex, length);
/*  666 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuffer dst) {
/*  671 */     this.buf.readBytes(dst);
/*  672 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
/*  677 */     this.buf.readBytes(out, length);
/*  678 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/*  683 */     return this.buf.readBytes(out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/*  688 */     return this.buf.readBytes(out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence readCharSequence(int length, Charset charset) {
/*  693 */     return this.buf.readCharSequence(length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf skipBytes(int length) {
/*  698 */     this.buf.skipBytes(length);
/*  699 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBoolean(boolean value) {
/*  704 */     this.buf.writeBoolean(value);
/*  705 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeByte(int value) {
/*  710 */     this.buf.writeByte(value);
/*  711 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShort(int value) {
/*  716 */     this.buf.writeShort(ByteBufUtil.swapShort((short)value));
/*  717 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShortLE(int value) {
/*  722 */     this.buf.writeShort((short)value);
/*  723 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMedium(int value) {
/*  728 */     this.buf.writeMedium(ByteBufUtil.swapMedium(value));
/*  729 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMediumLE(int value) {
/*  734 */     this.buf.writeMedium(value);
/*  735 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeInt(int value) {
/*  740 */     this.buf.writeInt(ByteBufUtil.swapInt(value));
/*  741 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeIntLE(int value) {
/*  746 */     this.buf.writeInt(value);
/*  747 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLong(long value) {
/*  752 */     this.buf.writeLong(ByteBufUtil.swapLong(value));
/*  753 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLongLE(long value) {
/*  758 */     this.buf.writeLong(value);
/*  759 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeChar(int value) {
/*  764 */     writeShort(value);
/*  765 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeFloat(float value) {
/*  770 */     writeInt(Float.floatToRawIntBits(value));
/*  771 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeDouble(double value) {
/*  776 */     writeLong(Double.doubleToRawLongBits(value));
/*  777 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src) {
/*  782 */     this.buf.writeBytes(src);
/*  783 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int length) {
/*  788 */     this.buf.writeBytes(src, length);
/*  789 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/*  794 */     this.buf.writeBytes(src, srcIndex, length);
/*  795 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src) {
/*  800 */     this.buf.writeBytes(src);
/*  801 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/*  806 */     this.buf.writeBytes(src, srcIndex, length);
/*  807 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuffer src) {
/*  812 */     this.buf.writeBytes(src);
/*  813 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(InputStream in, int length) throws IOException {
/*  818 */     return this.buf.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
/*  823 */     return this.buf.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(FileChannel in, long position, int length) throws IOException {
/*  828 */     return this.buf.writeBytes(in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeZero(int length) {
/*  833 */     this.buf.writeZero(length);
/*  834 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/*  839 */     return this.buf.writeCharSequence(sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int indexOf(int fromIndex, int toIndex, byte value) {
/*  844 */     return this.buf.indexOf(fromIndex, toIndex, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(byte value) {
/*  849 */     return this.buf.bytesBefore(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int length, byte value) {
/*  854 */     return this.buf.bytesBefore(length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int index, int length, byte value) {
/*  859 */     return this.buf.bytesBefore(index, length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(ByteProcessor processor) {
/*  864 */     return this.buf.forEachByte(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(int index, int length, ByteProcessor processor) {
/*  869 */     return this.buf.forEachByte(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(ByteProcessor processor) {
/*  874 */     return this.buf.forEachByteDesc(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/*  879 */     return this.buf.forEachByteDesc(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy() {
/*  884 */     return this.buf.copy().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy(int index, int length) {
/*  889 */     return this.buf.copy(index, length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice() {
/*  894 */     return this.buf.slice().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice() {
/*  899 */     return this.buf.retainedSlice().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice(int index, int length) {
/*  904 */     return this.buf.slice(index, length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice(int index, int length) {
/*  909 */     return this.buf.retainedSlice(index, length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf duplicate() {
/*  914 */     return this.buf.duplicate().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedDuplicate() {
/*  919 */     return this.buf.retainedDuplicate().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public int nioBufferCount() {
/*  924 */     return this.buf.nioBufferCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer() {
/*  929 */     return this.buf.nioBuffer().order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer(int index, int length) {
/*  934 */     return this.buf.nioBuffer(index, length).order(this.order);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer internalNioBuffer(int index, int length) {
/*  939 */     return nioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/*  944 */     ByteBuffer[] nioBuffers = this.buf.nioBuffers();
/*  945 */     for (int i = 0; i < nioBuffers.length; i++) {
/*  946 */       nioBuffers[i] = nioBuffers[i].order(this.order);
/*      */     }
/*  948 */     return nioBuffers;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  953 */     ByteBuffer[] nioBuffers = this.buf.nioBuffers(index, length);
/*  954 */     for (int i = 0; i < nioBuffers.length; i++) {
/*  955 */       nioBuffers[i] = nioBuffers[i].order(this.order);
/*      */     }
/*  957 */     return nioBuffers;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasArray() {
/*  962 */     return this.buf.hasArray();
/*      */   }
/*      */ 
/*      */   
/*      */   public byte[] array() {
/*  967 */     return this.buf.array();
/*      */   }
/*      */ 
/*      */   
/*      */   public int arrayOffset() {
/*  972 */     return this.buf.arrayOffset();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasMemoryAddress() {
/*  977 */     return this.buf.hasMemoryAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isContiguous() {
/*  982 */     return this.buf.isContiguous();
/*      */   }
/*      */ 
/*      */   
/*      */   public long memoryAddress() {
/*  987 */     return this.buf.memoryAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(Charset charset) {
/*  992 */     return this.buf.toString(charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(int index, int length, Charset charset) {
/*  997 */     return this.buf.toString(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int refCnt() {
/* 1002 */     return this.buf.refCnt();
/*      */   }
/*      */ 
/*      */   
/*      */   final boolean isAccessible() {
/* 1007 */     return this.buf.isAccessible();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retain() {
/* 1012 */     this.buf.retain();
/* 1013 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retain(int increment) {
/* 1018 */     this.buf.retain(increment);
/* 1019 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf touch() {
/* 1024 */     this.buf.touch();
/* 1025 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf touch(Object hint) {
/* 1030 */     this.buf.touch(hint);
/* 1031 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release() {
/* 1036 */     return this.buf.release();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release(int decrement) {
/* 1041 */     return this.buf.release(decrement);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1046 */     return this.buf.hashCode();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/* 1051 */     if (this == obj) {
/* 1052 */       return true;
/*      */     }
/* 1054 */     if (obj instanceof ByteBuf) {
/* 1055 */       return ByteBufUtil.equals(this, (ByteBuf)obj);
/*      */     }
/* 1057 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public int compareTo(ByteBuf buffer) {
/* 1062 */     return ByteBufUtil.compare(this, buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1067 */     return "Swapped(" + this.buf + ')';
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\SwappedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */