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
/*     */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
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
/*     */ abstract class AbstractUnpooledSlicedByteBuf
/*     */   extends AbstractDerivedByteBuf
/*     */ {
/*     */   private final ByteBuf buffer;
/*     */   private final int adjustment;
/*     */   
/*     */   AbstractUnpooledSlicedByteBuf(ByteBuf buffer, int index, int length) {
/*  37 */     super(length);
/*  38 */     checkSliceOutOfBounds(index, length, buffer);
/*     */     
/*  40 */     if (buffer instanceof AbstractUnpooledSlicedByteBuf) {
/*  41 */       this.buffer = ((AbstractUnpooledSlicedByteBuf)buffer).buffer;
/*  42 */       ((AbstractUnpooledSlicedByteBuf)buffer).adjustment += index;
/*  43 */     } else if (buffer instanceof DuplicatedByteBuf) {
/*  44 */       this.buffer = buffer.unwrap();
/*  45 */       this.adjustment = index;
/*     */     } else {
/*  47 */       this.buffer = buffer;
/*  48 */       this.adjustment = index;
/*     */     } 
/*     */     
/*  51 */     initLength(length);
/*  52 */     writerIndex(length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void initLength(int length) {}
/*     */ 
/*     */ 
/*     */   
/*     */   int length() {
/*  63 */     return capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/*  68 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/*  73 */     return unwrap().alloc();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteOrder order() {
/*  79 */     return unwrap().order();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/*  84 */     return unwrap().isDirect();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*  89 */     throw new UnsupportedOperationException("sliced buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/*  94 */     return unwrap().hasArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/*  99 */     return unwrap().array();
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 104 */     return idx(unwrap().arrayOffset());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 109 */     return unwrap().hasMemoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 114 */     return unwrap().memoryAddress() + this.adjustment;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 119 */     checkIndex0(index, 1);
/* 120 */     return unwrap().getByte(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 125 */     return unwrap().getByte(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 130 */     checkIndex0(index, 2);
/* 131 */     return unwrap().getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 136 */     return unwrap().getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 141 */     checkIndex0(index, 2);
/* 142 */     return unwrap().getShortLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 147 */     return unwrap().getShortLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 152 */     checkIndex0(index, 3);
/* 153 */     return unwrap().getUnsignedMedium(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 158 */     return unwrap().getUnsignedMedium(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 163 */     checkIndex0(index, 3);
/* 164 */     return unwrap().getUnsignedMediumLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 169 */     return unwrap().getUnsignedMediumLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 174 */     checkIndex0(index, 4);
/* 175 */     return unwrap().getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 180 */     return unwrap().getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 185 */     checkIndex0(index, 4);
/* 186 */     return unwrap().getIntLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 191 */     return unwrap().getIntLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 196 */     checkIndex0(index, 8);
/* 197 */     return unwrap().getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 202 */     return unwrap().getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 207 */     checkIndex0(index, 8);
/* 208 */     return unwrap().getLongLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 213 */     return unwrap().getLongLE(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/* 218 */     return unwrap().duplicate().setIndex(idx(readerIndex()), idx(writerIndex()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 223 */     checkIndex0(index, length);
/* 224 */     return unwrap().copy(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/* 229 */     checkIndex0(index, length);
/* 230 */     return unwrap().slice(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 235 */     checkIndex0(index, length);
/* 236 */     unwrap().getBytes(idx(index), dst, dstIndex, length);
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 242 */     checkIndex0(index, length);
/* 243 */     unwrap().getBytes(idx(index), dst, dstIndex, length);
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 249 */     checkIndex0(index, dst.remaining());
/* 250 */     unwrap().getBytes(idx(index), dst);
/* 251 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 256 */     checkIndex0(index, 1);
/* 257 */     unwrap().setByte(idx(index), value);
/* 258 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/* 263 */     checkIndex0(index, length);
/* 264 */     return unwrap().getCharSequence(idx(index), length, charset);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 269 */     unwrap().setByte(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 274 */     checkIndex0(index, 2);
/* 275 */     unwrap().setShort(idx(index), value);
/* 276 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 281 */     unwrap().setShort(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 286 */     checkIndex0(index, 2);
/* 287 */     unwrap().setShortLE(idx(index), value);
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 293 */     unwrap().setShortLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 298 */     checkIndex0(index, 3);
/* 299 */     unwrap().setMedium(idx(index), value);
/* 300 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 305 */     unwrap().setMedium(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 310 */     checkIndex0(index, 3);
/* 311 */     unwrap().setMediumLE(idx(index), value);
/* 312 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 317 */     unwrap().setMediumLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 322 */     checkIndex0(index, 4);
/* 323 */     unwrap().setInt(idx(index), value);
/* 324 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 329 */     unwrap().setInt(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 334 */     checkIndex0(index, 4);
/* 335 */     unwrap().setIntLE(idx(index), value);
/* 336 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 341 */     unwrap().setIntLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 346 */     checkIndex0(index, 8);
/* 347 */     unwrap().setLong(idx(index), value);
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 353 */     unwrap().setLong(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 358 */     checkIndex0(index, 8);
/* 359 */     unwrap().setLongLE(idx(index), value);
/* 360 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 365 */     unwrap().setLongLE(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 370 */     checkIndex0(index, length);
/* 371 */     unwrap().setBytes(idx(index), src, srcIndex, length);
/* 372 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 377 */     checkIndex0(index, length);
/* 378 */     unwrap().setBytes(idx(index), src, srcIndex, length);
/* 379 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 384 */     checkIndex0(index, src.remaining());
/* 385 */     unwrap().setBytes(idx(index), src);
/* 386 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 391 */     checkIndex0(index, length);
/* 392 */     unwrap().getBytes(idx(index), out, length);
/* 393 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 398 */     checkIndex0(index, length);
/* 399 */     return unwrap().getBytes(idx(index), out, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 404 */     checkIndex0(index, length);
/* 405 */     return unwrap().getBytes(idx(index), out, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 410 */     checkIndex0(index, length);
/* 411 */     return unwrap().setBytes(idx(index), in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 416 */     checkIndex0(index, length);
/* 417 */     return unwrap().setBytes(idx(index), in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 422 */     checkIndex0(index, length);
/* 423 */     return unwrap().setBytes(idx(index), in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 428 */     return unwrap().nioBufferCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 433 */     checkIndex0(index, length);
/* 434 */     return unwrap().nioBuffer(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 439 */     checkIndex0(index, length);
/* 440 */     return unwrap().nioBuffers(idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 445 */     checkIndex0(index, length);
/* 446 */     int ret = unwrap().forEachByte(idx(index), length, processor);
/* 447 */     if (ret >= this.adjustment) {
/* 448 */       return ret - this.adjustment;
/*     */     }
/* 450 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 456 */     checkIndex0(index, length);
/* 457 */     int ret = unwrap().forEachByteDesc(idx(index), length, processor);
/* 458 */     if (ret >= this.adjustment) {
/* 459 */       return ret - this.adjustment;
/*     */     }
/* 461 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int idx(int index) {
/* 469 */     return index + this.adjustment;
/*     */   }
/*     */   
/*     */   static void checkSliceOutOfBounds(int index, int length, ByteBuf buffer) {
/* 473 */     if (MathUtil.isOutOfBounds(index, length, buffer.capacity()))
/* 474 */       throw new IndexOutOfBoundsException(buffer + ".slice(" + index + ", " + length + ')'); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractUnpooledSlicedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */