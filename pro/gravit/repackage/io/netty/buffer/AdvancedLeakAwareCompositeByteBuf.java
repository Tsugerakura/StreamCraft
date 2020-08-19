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
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
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
/*      */ final class AdvancedLeakAwareCompositeByteBuf
/*      */   extends SimpleLeakAwareCompositeByteBuf
/*      */ {
/*      */   AdvancedLeakAwareCompositeByteBuf(CompositeByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
/*   39 */     super(wrapped, leak);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf order(ByteOrder endianness) {
/*   44 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   45 */     return super.order(endianness);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice() {
/*   50 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   51 */     return super.slice();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice() {
/*   56 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   57 */     return super.retainedSlice();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice(int index, int length) {
/*   62 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   63 */     return super.slice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice(int index, int length) {
/*   68 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   69 */     return super.retainedSlice(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf duplicate() {
/*   74 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   75 */     return super.duplicate();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedDuplicate() {
/*   80 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   81 */     return super.retainedDuplicate();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readSlice(int length) {
/*   86 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   87 */     return super.readSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readRetainedSlice(int length) {
/*   92 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   93 */     return super.readRetainedSlice(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf asReadOnly() {
/*   98 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*   99 */     return super.asReadOnly();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*  104 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  105 */     return super.isReadOnly();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadBytes() {
/*  110 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  111 */     return super.discardReadBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardSomeReadBytes() {
/*  116 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  117 */     return super.discardSomeReadBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf ensureWritable(int minWritableBytes) {
/*  122 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  123 */     return super.ensureWritable(minWritableBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  128 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  129 */     return super.ensureWritable(minWritableBytes, force);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int index) {
/*  134 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  135 */     return super.getBoolean(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  140 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  141 */     return super.getByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getUnsignedByte(int index) {
/*  146 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  147 */     return super.getUnsignedByte(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(int index) {
/*  152 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  153 */     return super.getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShort(int index) {
/*  158 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  159 */     return super.getUnsignedShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMedium(int index) {
/*  164 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  165 */     return super.getMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMedium(int index) {
/*  170 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  171 */     return super.getUnsignedMedium(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(int index) {
/*  176 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  177 */     return super.getInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedInt(int index) {
/*  182 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  183 */     return super.getUnsignedInt(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(int index) {
/*  188 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  189 */     return super.getLong(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public char getChar(int index) {
/*  194 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  195 */     return super.getChar(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(int index) {
/*  200 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  201 */     return super.getFloat(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(int index) {
/*  206 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  207 */     return super.getDouble(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst) {
/*  212 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  213 */     return super.getBytes(index, dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
/*  218 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  219 */     return super.getBytes(index, dst, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  224 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  225 */     return super.getBytes(index, dst, dstIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst) {
/*  230 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  231 */     return super.getBytes(index, dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/*  236 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  237 */     return super.getBytes(index, dst, dstIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
/*  242 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  243 */     return super.getBytes(index, dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/*  248 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  249 */     return super.getBytes(index, out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/*  254 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  255 */     return super.getBytes(index, out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/*  260 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  261 */     return super.getCharSequence(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBoolean(int index, boolean value) {
/*  266 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  267 */     return super.setBoolean(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setByte(int index, int value) {
/*  272 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  273 */     return super.setByte(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setShort(int index, int value) {
/*  278 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  279 */     return super.setShort(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setMedium(int index, int value) {
/*  284 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  285 */     return super.setMedium(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setInt(int index, int value) {
/*  290 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  291 */     return super.setInt(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setLong(int index, long value) {
/*  296 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  297 */     return super.setLong(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setChar(int index, int value) {
/*  302 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  303 */     return super.setChar(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setFloat(int index, float value) {
/*  308 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  309 */     return super.setFloat(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setDouble(int index, double value) {
/*  314 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  315 */     return super.setDouble(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src) {
/*  320 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  321 */     return super.setBytes(index, src);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
/*  326 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  327 */     return super.setBytes(index, src, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/*  332 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  333 */     return super.setBytes(index, src, srcIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src) {
/*  338 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  339 */     return super.setBytes(index, src);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/*  344 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  345 */     return super.setBytes(index, src, srcIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuffer src) {
/*  350 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  351 */     return super.setBytes(index, src);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, InputStream in, int length) throws IOException {
/*  356 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  357 */     return super.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/*  362 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  363 */     return super.setBytes(index, in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setZero(int index, int length) {
/*  368 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  369 */     return super.setZero(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean readBoolean() {
/*  374 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  375 */     return super.readBoolean();
/*      */   }
/*      */ 
/*      */   
/*      */   public byte readByte() {
/*  380 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  381 */     return super.readByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readUnsignedByte() {
/*  386 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  387 */     return super.readUnsignedByte();
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShort() {
/*  392 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  393 */     return super.readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShort() {
/*  398 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  399 */     return super.readUnsignedShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMedium() {
/*  404 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  405 */     return super.readMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMedium() {
/*  410 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  411 */     return super.readUnsignedMedium();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt() {
/*  416 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  417 */     return super.readInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedInt() {
/*  422 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  423 */     return super.readUnsignedInt();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLong() {
/*  428 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  429 */     return super.readLong();
/*      */   }
/*      */ 
/*      */   
/*      */   public char readChar() {
/*  434 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  435 */     return super.readChar();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() {
/*  440 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  441 */     return super.readFloat();
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() {
/*  446 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  447 */     return super.readDouble();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(int length) {
/*  452 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  453 */     return super.readBytes(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst) {
/*  458 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  459 */     return super.readBytes(dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int length) {
/*  464 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  465 */     return super.readBytes(dst, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/*  470 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  471 */     return super.readBytes(dst, dstIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst) {
/*  476 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  477 */     return super.readBytes(dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/*  482 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  483 */     return super.readBytes(dst, dstIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuffer dst) {
/*  488 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  489 */     return super.readBytes(dst);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
/*  494 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  495 */     return super.readBytes(out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/*  500 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  501 */     return super.readBytes(out, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence readCharSequence(int length, Charset charset) {
/*  506 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  507 */     return super.readCharSequence(length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf skipBytes(int length) {
/*  512 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  513 */     return super.skipBytes(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBoolean(boolean value) {
/*  518 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  519 */     return super.writeBoolean(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeByte(int value) {
/*  524 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  525 */     return super.writeByte(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeShort(int value) {
/*  530 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  531 */     return super.writeShort(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeMedium(int value) {
/*  536 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  537 */     return super.writeMedium(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeInt(int value) {
/*  542 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  543 */     return super.writeInt(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeLong(long value) {
/*  548 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  549 */     return super.writeLong(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeChar(int value) {
/*  554 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  555 */     return super.writeChar(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeFloat(float value) {
/*  560 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  561 */     return super.writeFloat(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeDouble(double value) {
/*  566 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  567 */     return super.writeDouble(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src) {
/*  572 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  573 */     return super.writeBytes(src);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int length) {
/*  578 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  579 */     return super.writeBytes(src, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/*  584 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  585 */     return super.writeBytes(src, srcIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src) {
/*  590 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  591 */     return super.writeBytes(src);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/*  596 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  597 */     return super.writeBytes(src, srcIndex, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuffer src) {
/*  602 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  603 */     return super.writeBytes(src);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(InputStream in, int length) throws IOException {
/*  608 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  609 */     return super.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
/*  614 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  615 */     return super.writeBytes(in, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeZero(int length) {
/*  620 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  621 */     return super.writeZero(length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/*  626 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  627 */     return super.writeCharSequence(sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int indexOf(int fromIndex, int toIndex, byte value) {
/*  632 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  633 */     return super.indexOf(fromIndex, toIndex, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(byte value) {
/*  638 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  639 */     return super.bytesBefore(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int length, byte value) {
/*  644 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  645 */     return super.bytesBefore(length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int index, int length, byte value) {
/*  650 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  651 */     return super.bytesBefore(index, length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(ByteProcessor processor) {
/*  656 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  657 */     return super.forEachByte(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(int index, int length, ByteProcessor processor) {
/*  662 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  663 */     return super.forEachByte(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(ByteProcessor processor) {
/*  668 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  669 */     return super.forEachByteDesc(processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/*  674 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  675 */     return super.forEachByteDesc(index, length, processor);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy() {
/*  680 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  681 */     return super.copy();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy(int index, int length) {
/*  686 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  687 */     return super.copy(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int nioBufferCount() {
/*  692 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  693 */     return super.nioBufferCount();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer() {
/*  698 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  699 */     return super.nioBuffer();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer(int index, int length) {
/*  704 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  705 */     return super.nioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/*  710 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  711 */     return super.nioBuffers();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers(int index, int length) {
/*  716 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  717 */     return super.nioBuffers(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer internalNioBuffer(int index, int length) {
/*  722 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  723 */     return super.internalNioBuffer(index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(Charset charset) {
/*  728 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  729 */     return super.toString(charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(int index, int length, Charset charset) {
/*  734 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  735 */     return super.toString(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf capacity(int newCapacity) {
/*  740 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  741 */     return super.capacity(newCapacity);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShortLE(int index) {
/*  746 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  747 */     return super.getShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShortLE(int index) {
/*  752 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  753 */     return super.getUnsignedShortLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMediumLE(int index) {
/*  758 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  759 */     return super.getUnsignedMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMediumLE(int index) {
/*  764 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  765 */     return super.getMediumLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntLE(int index) {
/*  770 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  771 */     return super.getIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedIntLE(int index) {
/*  776 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  777 */     return super.getUnsignedIntLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLongLE(int index) {
/*  782 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  783 */     return super.getLongLE(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShortLE(int index, int value) {
/*  788 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  789 */     return super.setShortLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMediumLE(int index, int value) {
/*  794 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  795 */     return super.setMediumLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIntLE(int index, int value) {
/*  800 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  801 */     return super.setIntLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLongLE(int index, long value) {
/*  806 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  807 */     return super.setLongLE(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/*  812 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  813 */     return super.setCharSequence(index, sequence, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShortLE() {
/*  818 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  819 */     return super.readShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShortLE() {
/*  824 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  825 */     return super.readUnsignedShortLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMediumLE() {
/*  830 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  831 */     return super.readMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMediumLE() {
/*  836 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  837 */     return super.readUnsignedMediumLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readIntLE() {
/*  842 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  843 */     return super.readIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedIntLE() {
/*  848 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  849 */     return super.readUnsignedIntLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLongLE() {
/*  854 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  855 */     return super.readLongLE();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShortLE(int value) {
/*  860 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  861 */     return super.writeShortLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMediumLE(int value) {
/*  866 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  867 */     return super.writeMediumLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeIntLE(int value) {
/*  872 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  873 */     return super.writeIntLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLongLE(long value) {
/*  878 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  879 */     return super.writeLongLE(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(ByteBuf buffer) {
/*  884 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  885 */     return super.addComponent(buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(ByteBuf... buffers) {
/*  890 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  891 */     return super.addComponents(buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
/*  896 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  897 */     return super.addComponents(buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
/*  902 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  903 */     return super.addComponent(cIndex, buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, ByteBuf... buffers) {
/*  908 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  909 */     return super.addComponents(cIndex, buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
/*  914 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  915 */     return super.addComponents(cIndex, buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
/*  920 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  921 */     return super.addComponent(increaseWriterIndex, buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf... buffers) {
/*  926 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  927 */     return super.addComponents(increaseWriterIndex, buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
/*  932 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  933 */     return super.addComponents(increaseWriterIndex, buffers);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
/*  938 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  939 */     return super.addComponent(increaseWriterIndex, cIndex, buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
/*  944 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  945 */     return super.addFlattenedComponents(increaseWriterIndex, buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponent(int cIndex) {
/*  950 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  951 */     return super.removeComponent(cIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
/*  956 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  957 */     return super.removeComponents(cIndex, numComponents);
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator<ByteBuf> iterator() {
/*  962 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  963 */     return super.iterator();
/*      */   }
/*      */ 
/*      */   
/*      */   public List<ByteBuf> decompose(int offset, int length) {
/*  968 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  969 */     return super.decompose(offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate() {
/*  974 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  975 */     return super.consolidate();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadComponents() {
/*  980 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  981 */     return super.discardReadComponents();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate(int cIndex, int numComponents) {
/*  986 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  987 */     return super.consolidate(cIndex, numComponents);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/*  992 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  993 */     return super.getBytes(index, out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/*  998 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/*  999 */     return super.setBytes(index, in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/* 1004 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/* 1005 */     return super.readBytes(out, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(FileChannel in, long position, int length) throws IOException {
/* 1010 */     AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
/* 1011 */     return super.writeBytes(in, position, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain() {
/* 1016 */     this.leak.record();
/* 1017 */     return super.retain();
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain(int increment) {
/* 1022 */     this.leak.record();
/* 1023 */     return super.retain(increment);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release() {
/* 1028 */     this.leak.record();
/* 1029 */     return super.release();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean release(int decrement) {
/* 1034 */     this.leak.record();
/* 1035 */     return super.release(decrement);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch() {
/* 1040 */     this.leak.record();
/* 1041 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch(Object hint) {
/* 1046 */     this.leak.record(hint);
/* 1047 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
/* 1053 */     return new AdvancedLeakAwareByteBuf(wrapped, trackedByteBuf, leakTracker);
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AdvancedLeakAwareCompositeByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */