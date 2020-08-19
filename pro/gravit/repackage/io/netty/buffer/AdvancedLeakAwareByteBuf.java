/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ final class AdvancedLeakAwareByteBuf
/*     */   extends SimpleLeakAwareByteBuf
/*     */ {
/*     */   private static final String PROP_ACQUIRE_AND_RELEASE_ONLY = "pro.gravit.repackage.io.netty.leakDetection.acquireAndReleaseOnly";
/*     */   private static final boolean ACQUIRE_AND_RELEASE_ONLY;
/*  41 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AdvancedLeakAwareByteBuf.class);
/*     */   
/*     */   static {
/*  44 */     ACQUIRE_AND_RELEASE_ONLY = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.leakDetection.acquireAndReleaseOnly", false);
/*     */     
/*  46 */     if (logger.isDebugEnabled()) {
/*  47 */       logger.debug("-D{}: {}", "pro.gravit.repackage.io.netty.leakDetection.acquireAndReleaseOnly", Boolean.valueOf(ACQUIRE_AND_RELEASE_ONLY));
/*     */     }
/*     */     
/*  50 */     ResourceLeakDetector.addExclusions(AdvancedLeakAwareByteBuf.class, new String[] { "touch", "recordLeakNonRefCountingOperation" });
/*     */   }
/*     */ 
/*     */   
/*     */   AdvancedLeakAwareByteBuf(ByteBuf buf, ResourceLeakTracker<ByteBuf> leak) {
/*  55 */     super(buf, leak);
/*     */   }
/*     */   
/*     */   AdvancedLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
/*  59 */     super(wrapped, trackedByteBuf, leak);
/*     */   }
/*     */   
/*     */   static void recordLeakNonRefCountingOperation(ResourceLeakTracker<ByteBuf> leak) {
/*  63 */     if (!ACQUIRE_AND_RELEASE_ONLY) {
/*  64 */       leak.record();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf order(ByteOrder endianness) {
/*  70 */     recordLeakNonRefCountingOperation(this.leak);
/*  71 */     return super.order(endianness);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice() {
/*  76 */     recordLeakNonRefCountingOperation(this.leak);
/*  77 */     return super.slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/*  82 */     recordLeakNonRefCountingOperation(this.leak);
/*  83 */     return super.slice(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice() {
/*  88 */     recordLeakNonRefCountingOperation(this.leak);
/*  89 */     return super.retainedSlice();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice(int index, int length) {
/*  94 */     recordLeakNonRefCountingOperation(this.leak);
/*  95 */     return super.retainedSlice(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedDuplicate() {
/* 100 */     recordLeakNonRefCountingOperation(this.leak);
/* 101 */     return super.retainedDuplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readRetainedSlice(int length) {
/* 106 */     recordLeakNonRefCountingOperation(this.leak);
/* 107 */     return super.readRetainedSlice(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/* 112 */     recordLeakNonRefCountingOperation(this.leak);
/* 113 */     return super.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readSlice(int length) {
/* 118 */     recordLeakNonRefCountingOperation(this.leak);
/* 119 */     return super.readSlice(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf discardReadBytes() {
/* 124 */     recordLeakNonRefCountingOperation(this.leak);
/* 125 */     return super.discardReadBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf discardSomeReadBytes() {
/* 130 */     recordLeakNonRefCountingOperation(this.leak);
/* 131 */     return super.discardSomeReadBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ensureWritable(int minWritableBytes) {
/* 136 */     recordLeakNonRefCountingOperation(this.leak);
/* 137 */     return super.ensureWritable(minWritableBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public int ensureWritable(int minWritableBytes, boolean force) {
/* 142 */     recordLeakNonRefCountingOperation(this.leak);
/* 143 */     return super.ensureWritable(minWritableBytes, force);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean(int index) {
/* 148 */     recordLeakNonRefCountingOperation(this.leak);
/* 149 */     return super.getBoolean(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 154 */     recordLeakNonRefCountingOperation(this.leak);
/* 155 */     return super.getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getUnsignedByte(int index) {
/* 160 */     recordLeakNonRefCountingOperation(this.leak);
/* 161 */     return super.getUnsignedByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 166 */     recordLeakNonRefCountingOperation(this.leak);
/* 167 */     return super.getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedShort(int index) {
/* 172 */     recordLeakNonRefCountingOperation(this.leak);
/* 173 */     return super.getUnsignedShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMedium(int index) {
/* 178 */     recordLeakNonRefCountingOperation(this.leak);
/* 179 */     return super.getMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 184 */     recordLeakNonRefCountingOperation(this.leak);
/* 185 */     return super.getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 190 */     recordLeakNonRefCountingOperation(this.leak);
/* 191 */     return super.getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUnsignedInt(int index) {
/* 196 */     recordLeakNonRefCountingOperation(this.leak);
/* 197 */     return super.getUnsignedInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 202 */     recordLeakNonRefCountingOperation(this.leak);
/* 203 */     return super.getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public char getChar(int index) {
/* 208 */     recordLeakNonRefCountingOperation(this.leak);
/* 209 */     return super.getChar(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat(int index) {
/* 214 */     recordLeakNonRefCountingOperation(this.leak);
/* 215 */     return super.getFloat(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(int index) {
/* 220 */     recordLeakNonRefCountingOperation(this.leak);
/* 221 */     return super.getDouble(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst) {
/* 226 */     recordLeakNonRefCountingOperation(this.leak);
/* 227 */     return super.getBytes(index, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
/* 232 */     recordLeakNonRefCountingOperation(this.leak);
/* 233 */     return super.getBytes(index, dst, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 238 */     recordLeakNonRefCountingOperation(this.leak);
/* 239 */     return super.getBytes(index, dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst) {
/* 244 */     recordLeakNonRefCountingOperation(this.leak);
/* 245 */     return super.getBytes(index, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 250 */     recordLeakNonRefCountingOperation(this.leak);
/* 251 */     return super.getBytes(index, dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 256 */     recordLeakNonRefCountingOperation(this.leak);
/* 257 */     return super.getBytes(index, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 262 */     recordLeakNonRefCountingOperation(this.leak);
/* 263 */     return super.getBytes(index, out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 268 */     recordLeakNonRefCountingOperation(this.leak);
/* 269 */     return super.getBytes(index, out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/* 274 */     recordLeakNonRefCountingOperation(this.leak);
/* 275 */     return super.getCharSequence(index, length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBoolean(int index, boolean value) {
/* 280 */     recordLeakNonRefCountingOperation(this.leak);
/* 281 */     return super.setBoolean(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 286 */     recordLeakNonRefCountingOperation(this.leak);
/* 287 */     return super.setByte(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 292 */     recordLeakNonRefCountingOperation(this.leak);
/* 293 */     return super.setShort(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 298 */     recordLeakNonRefCountingOperation(this.leak);
/* 299 */     return super.setMedium(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 304 */     recordLeakNonRefCountingOperation(this.leak);
/* 305 */     return super.setInt(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 310 */     recordLeakNonRefCountingOperation(this.leak);
/* 311 */     return super.setLong(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setChar(int index, int value) {
/* 316 */     recordLeakNonRefCountingOperation(this.leak);
/* 317 */     return super.setChar(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setFloat(int index, float value) {
/* 322 */     recordLeakNonRefCountingOperation(this.leak);
/* 323 */     return super.setFloat(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setDouble(int index, double value) {
/* 328 */     recordLeakNonRefCountingOperation(this.leak);
/* 329 */     return super.setDouble(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src) {
/* 334 */     recordLeakNonRefCountingOperation(this.leak);
/* 335 */     return super.setBytes(index, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int length) {
/* 340 */     recordLeakNonRefCountingOperation(this.leak);
/* 341 */     return super.setBytes(index, src, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 346 */     recordLeakNonRefCountingOperation(this.leak);
/* 347 */     return super.setBytes(index, src, srcIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src) {
/* 352 */     recordLeakNonRefCountingOperation(this.leak);
/* 353 */     return super.setBytes(index, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 358 */     recordLeakNonRefCountingOperation(this.leak);
/* 359 */     return super.setBytes(index, src, srcIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 364 */     recordLeakNonRefCountingOperation(this.leak);
/* 365 */     return super.setBytes(index, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 370 */     recordLeakNonRefCountingOperation(this.leak);
/* 371 */     return super.setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 376 */     recordLeakNonRefCountingOperation(this.leak);
/* 377 */     return super.setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setZero(int index, int length) {
/* 382 */     recordLeakNonRefCountingOperation(this.leak);
/* 383 */     return super.setZero(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/* 388 */     recordLeakNonRefCountingOperation(this.leak);
/* 389 */     return super.setCharSequence(index, sequence, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readBoolean() {
/* 394 */     recordLeakNonRefCountingOperation(this.leak);
/* 395 */     return super.readBoolean();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte() {
/* 400 */     recordLeakNonRefCountingOperation(this.leak);
/* 401 */     return super.readByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readUnsignedByte() {
/* 406 */     recordLeakNonRefCountingOperation(this.leak);
/* 407 */     return super.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort() {
/* 412 */     recordLeakNonRefCountingOperation(this.leak);
/* 413 */     return super.readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedShort() {
/* 418 */     recordLeakNonRefCountingOperation(this.leak);
/* 419 */     return super.readUnsignedShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readMedium() {
/* 424 */     recordLeakNonRefCountingOperation(this.leak);
/* 425 */     return super.readMedium();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedMedium() {
/* 430 */     recordLeakNonRefCountingOperation(this.leak);
/* 431 */     return super.readUnsignedMedium();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt() {
/* 436 */     recordLeakNonRefCountingOperation(this.leak);
/* 437 */     return super.readInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readUnsignedInt() {
/* 442 */     recordLeakNonRefCountingOperation(this.leak);
/* 443 */     return super.readUnsignedInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong() {
/* 448 */     recordLeakNonRefCountingOperation(this.leak);
/* 449 */     return super.readLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public char readChar() {
/* 454 */     recordLeakNonRefCountingOperation(this.leak);
/* 455 */     return super.readChar();
/*     */   }
/*     */ 
/*     */   
/*     */   public float readFloat() {
/* 460 */     recordLeakNonRefCountingOperation(this.leak);
/* 461 */     return super.readFloat();
/*     */   }
/*     */ 
/*     */   
/*     */   public double readDouble() {
/* 466 */     recordLeakNonRefCountingOperation(this.leak);
/* 467 */     return super.readDouble();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(int length) {
/* 472 */     recordLeakNonRefCountingOperation(this.leak);
/* 473 */     return super.readBytes(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuf dst) {
/* 478 */     recordLeakNonRefCountingOperation(this.leak);
/* 479 */     return super.readBytes(dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuf dst, int length) {
/* 484 */     recordLeakNonRefCountingOperation(this.leak);
/* 485 */     return super.readBytes(dst, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/* 490 */     recordLeakNonRefCountingOperation(this.leak);
/* 491 */     return super.readBytes(dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(byte[] dst) {
/* 496 */     recordLeakNonRefCountingOperation(this.leak);
/* 497 */     return super.readBytes(dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/* 502 */     recordLeakNonRefCountingOperation(this.leak);
/* 503 */     return super.readBytes(dst, dstIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuffer dst) {
/* 508 */     recordLeakNonRefCountingOperation(this.leak);
/* 509 */     return super.readBytes(dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
/* 514 */     recordLeakNonRefCountingOperation(this.leak);
/* 515 */     return super.readBytes(out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/* 520 */     recordLeakNonRefCountingOperation(this.leak);
/* 521 */     return super.readBytes(out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence readCharSequence(int length, Charset charset) {
/* 526 */     recordLeakNonRefCountingOperation(this.leak);
/* 527 */     return super.readCharSequence(length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf skipBytes(int length) {
/* 532 */     recordLeakNonRefCountingOperation(this.leak);
/* 533 */     return super.skipBytes(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBoolean(boolean value) {
/* 538 */     recordLeakNonRefCountingOperation(this.leak);
/* 539 */     return super.writeBoolean(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeByte(int value) {
/* 544 */     recordLeakNonRefCountingOperation(this.leak);
/* 545 */     return super.writeByte(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeShort(int value) {
/* 550 */     recordLeakNonRefCountingOperation(this.leak);
/* 551 */     return super.writeShort(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeMedium(int value) {
/* 556 */     recordLeakNonRefCountingOperation(this.leak);
/* 557 */     return super.writeMedium(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeInt(int value) {
/* 562 */     recordLeakNonRefCountingOperation(this.leak);
/* 563 */     return super.writeInt(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeLong(long value) {
/* 568 */     recordLeakNonRefCountingOperation(this.leak);
/* 569 */     return super.writeLong(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeChar(int value) {
/* 574 */     recordLeakNonRefCountingOperation(this.leak);
/* 575 */     return super.writeChar(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeFloat(float value) {
/* 580 */     recordLeakNonRefCountingOperation(this.leak);
/* 581 */     return super.writeFloat(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeDouble(double value) {
/* 586 */     recordLeakNonRefCountingOperation(this.leak);
/* 587 */     return super.writeDouble(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(ByteBuf src) {
/* 592 */     recordLeakNonRefCountingOperation(this.leak);
/* 593 */     return super.writeBytes(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(ByteBuf src, int length) {
/* 598 */     recordLeakNonRefCountingOperation(this.leak);
/* 599 */     return super.writeBytes(src, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/* 604 */     recordLeakNonRefCountingOperation(this.leak);
/* 605 */     return super.writeBytes(src, srcIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(byte[] src) {
/* 610 */     recordLeakNonRefCountingOperation(this.leak);
/* 611 */     return super.writeBytes(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/* 616 */     recordLeakNonRefCountingOperation(this.leak);
/* 617 */     return super.writeBytes(src, srcIndex, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeBytes(ByteBuffer src) {
/* 622 */     recordLeakNonRefCountingOperation(this.leak);
/* 623 */     return super.writeBytes(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeBytes(InputStream in, int length) throws IOException {
/* 628 */     recordLeakNonRefCountingOperation(this.leak);
/* 629 */     return super.writeBytes(in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
/* 634 */     recordLeakNonRefCountingOperation(this.leak);
/* 635 */     return super.writeBytes(in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeZero(int length) {
/* 640 */     recordLeakNonRefCountingOperation(this.leak);
/* 641 */     return super.writeZero(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int indexOf(int fromIndex, int toIndex, byte value) {
/* 646 */     recordLeakNonRefCountingOperation(this.leak);
/* 647 */     return super.indexOf(fromIndex, toIndex, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int bytesBefore(byte value) {
/* 652 */     recordLeakNonRefCountingOperation(this.leak);
/* 653 */     return super.bytesBefore(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int bytesBefore(int length, byte value) {
/* 658 */     recordLeakNonRefCountingOperation(this.leak);
/* 659 */     return super.bytesBefore(length, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int bytesBefore(int index, int length, byte value) {
/* 664 */     recordLeakNonRefCountingOperation(this.leak);
/* 665 */     return super.bytesBefore(index, length, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(ByteProcessor processor) {
/* 670 */     recordLeakNonRefCountingOperation(this.leak);
/* 671 */     return super.forEachByte(processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 676 */     recordLeakNonRefCountingOperation(this.leak);
/* 677 */     return super.forEachByte(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(ByteProcessor processor) {
/* 682 */     recordLeakNonRefCountingOperation(this.leak);
/* 683 */     return super.forEachByteDesc(processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 688 */     recordLeakNonRefCountingOperation(this.leak);
/* 689 */     return super.forEachByteDesc(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy() {
/* 694 */     recordLeakNonRefCountingOperation(this.leak);
/* 695 */     return super.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 700 */     recordLeakNonRefCountingOperation(this.leak);
/* 701 */     return super.copy(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 706 */     recordLeakNonRefCountingOperation(this.leak);
/* 707 */     return super.nioBufferCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer() {
/* 712 */     recordLeakNonRefCountingOperation(this.leak);
/* 713 */     return super.nioBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 718 */     recordLeakNonRefCountingOperation(this.leak);
/* 719 */     return super.nioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers() {
/* 724 */     recordLeakNonRefCountingOperation(this.leak);
/* 725 */     return super.nioBuffers();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 730 */     recordLeakNonRefCountingOperation(this.leak);
/* 731 */     return super.nioBuffers(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 736 */     recordLeakNonRefCountingOperation(this.leak);
/* 737 */     return super.internalNioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString(Charset charset) {
/* 742 */     recordLeakNonRefCountingOperation(this.leak);
/* 743 */     return super.toString(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString(int index, int length, Charset charset) {
/* 748 */     recordLeakNonRefCountingOperation(this.leak);
/* 749 */     return super.toString(index, length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/* 754 */     recordLeakNonRefCountingOperation(this.leak);
/* 755 */     return super.capacity(newCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 760 */     recordLeakNonRefCountingOperation(this.leak);
/* 761 */     return super.getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedShortLE(int index) {
/* 766 */     recordLeakNonRefCountingOperation(this.leak);
/* 767 */     return super.getUnsignedShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMediumLE(int index) {
/* 772 */     recordLeakNonRefCountingOperation(this.leak);
/* 773 */     return super.getMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 778 */     recordLeakNonRefCountingOperation(this.leak);
/* 779 */     return super.getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 784 */     recordLeakNonRefCountingOperation(this.leak);
/* 785 */     return super.getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUnsignedIntLE(int index) {
/* 790 */     recordLeakNonRefCountingOperation(this.leak);
/* 791 */     return super.getUnsignedIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 796 */     recordLeakNonRefCountingOperation(this.leak);
/* 797 */     return super.getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 802 */     recordLeakNonRefCountingOperation(this.leak);
/* 803 */     return super.setShortLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 808 */     recordLeakNonRefCountingOperation(this.leak);
/* 809 */     return super.setIntLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 814 */     recordLeakNonRefCountingOperation(this.leak);
/* 815 */     return super.setMediumLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 820 */     recordLeakNonRefCountingOperation(this.leak);
/* 821 */     return super.setLongLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShortLE() {
/* 826 */     recordLeakNonRefCountingOperation(this.leak);
/* 827 */     return super.readShortLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedShortLE() {
/* 832 */     recordLeakNonRefCountingOperation(this.leak);
/* 833 */     return super.readUnsignedShortLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readMediumLE() {
/* 838 */     recordLeakNonRefCountingOperation(this.leak);
/* 839 */     return super.readMediumLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedMediumLE() {
/* 844 */     recordLeakNonRefCountingOperation(this.leak);
/* 845 */     return super.readUnsignedMediumLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readIntLE() {
/* 850 */     recordLeakNonRefCountingOperation(this.leak);
/* 851 */     return super.readIntLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readUnsignedIntLE() {
/* 856 */     recordLeakNonRefCountingOperation(this.leak);
/* 857 */     return super.readUnsignedIntLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLongLE() {
/* 862 */     recordLeakNonRefCountingOperation(this.leak);
/* 863 */     return super.readLongLE();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeShortLE(int value) {
/* 868 */     recordLeakNonRefCountingOperation(this.leak);
/* 869 */     return super.writeShortLE(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeMediumLE(int value) {
/* 874 */     recordLeakNonRefCountingOperation(this.leak);
/* 875 */     return super.writeMediumLE(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeIntLE(int value) {
/* 880 */     recordLeakNonRefCountingOperation(this.leak);
/* 881 */     return super.writeIntLE(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeLongLE(long value) {
/* 886 */     recordLeakNonRefCountingOperation(this.leak);
/* 887 */     return super.writeLongLE(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/* 892 */     recordLeakNonRefCountingOperation(this.leak);
/* 893 */     return super.writeCharSequence(sequence, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 898 */     recordLeakNonRefCountingOperation(this.leak);
/* 899 */     return super.getBytes(index, out, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 904 */     recordLeakNonRefCountingOperation(this.leak);
/* 905 */     return super.setBytes(index, in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/* 910 */     recordLeakNonRefCountingOperation(this.leak);
/* 911 */     return super.readBytes(out, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeBytes(FileChannel in, long position, int length) throws IOException {
/* 916 */     recordLeakNonRefCountingOperation(this.leak);
/* 917 */     return super.writeBytes(in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf asReadOnly() {
/* 922 */     recordLeakNonRefCountingOperation(this.leak);
/* 923 */     return super.asReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain() {
/* 928 */     this.leak.record();
/* 929 */     return super.retain();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain(int increment) {
/* 934 */     this.leak.record();
/* 935 */     return super.retain(increment);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 940 */     this.leak.record();
/* 941 */     return super.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 946 */     this.leak.record();
/* 947 */     return super.release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch() {
/* 952 */     this.leak.record();
/* 953 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch(Object hint) {
/* 958 */     this.leak.record(hint);
/* 959 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
/* 965 */     return new AdvancedLeakAwareByteBuf(buf, trackedByteBuf, leakTracker);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AdvancedLeakAwareByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */