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
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
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
/*     */ @Deprecated
/*     */ public class DuplicatedByteBuf
/*     */   extends AbstractDerivedByteBuf
/*     */ {
/*     */   private final ByteBuf buffer;
/*     */   
/*     */   public DuplicatedByteBuf(ByteBuf buffer) {
/*  42 */     this(buffer, buffer.readerIndex(), buffer.writerIndex());
/*     */   }
/*     */   
/*     */   DuplicatedByteBuf(ByteBuf buffer, int readerIndex, int writerIndex) {
/*  46 */     super(buffer.maxCapacity());
/*     */     
/*  48 */     if (buffer instanceof DuplicatedByteBuf) {
/*  49 */       this.buffer = ((DuplicatedByteBuf)buffer).buffer;
/*  50 */     } else if (buffer instanceof AbstractPooledDerivedByteBuf) {
/*  51 */       this.buffer = buffer.unwrap();
/*     */     } else {
/*  53 */       this.buffer = buffer;
/*     */     } 
/*     */     
/*  56 */     setIndex(readerIndex, writerIndex);
/*  57 */     markReaderIndex();
/*  58 */     markWriterIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/*  63 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/*  68 */     return unwrap().alloc();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteOrder order() {
/*  74 */     return unwrap().order();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/*  79 */     return unwrap().isDirect();
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/*  84 */     return unwrap().capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/*  89 */     unwrap().capacity(newCapacity);
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/*  95 */     return unwrap().hasArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 100 */     return unwrap().array();
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 105 */     return unwrap().arrayOffset();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 110 */     return unwrap().hasMemoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 115 */     return unwrap().memoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 120 */     return unwrap().getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 125 */     return unwrap().getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 130 */     return unwrap().getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 135 */     return unwrap().getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 140 */     return unwrap().getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 145 */     return unwrap().getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 150 */     return unwrap().getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 155 */     return unwrap().getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 160 */     return unwrap().getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 165 */     return unwrap().getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 170 */     return unwrap().getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 175 */     return unwrap().getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 180 */     return unwrap().getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 185 */     return unwrap().getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 190 */     return unwrap().getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 195 */     return unwrap().getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 200 */     return unwrap().getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 205 */     return unwrap().getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 210 */     return unwrap().copy(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/* 215 */     return unwrap().slice(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 220 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 221 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 226 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 232 */     unwrap().getBytes(index, dst);
/* 233 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 238 */     unwrap().setByte(index, value);
/* 239 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 244 */     unwrap().setByte(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 249 */     unwrap().setShort(index, value);
/* 250 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 255 */     unwrap().setShort(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 260 */     unwrap().setShortLE(index, value);
/* 261 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 266 */     unwrap().setShortLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 271 */     unwrap().setMedium(index, value);
/* 272 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 277 */     unwrap().setMedium(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 282 */     unwrap().setMediumLE(index, value);
/* 283 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 288 */     unwrap().setMediumLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 293 */     unwrap().setInt(index, value);
/* 294 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 299 */     unwrap().setInt(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 304 */     unwrap().setIntLE(index, value);
/* 305 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 310 */     unwrap().setIntLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 315 */     unwrap().setLong(index, value);
/* 316 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 321 */     unwrap().setLong(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 326 */     unwrap().setLongLE(index, value);
/* 327 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 332 */     unwrap().setLongLE(index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 337 */     unwrap().setBytes(index, src, srcIndex, length);
/* 338 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 343 */     unwrap().setBytes(index, src, srcIndex, length);
/* 344 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 349 */     unwrap().setBytes(index, src);
/* 350 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 356 */     unwrap().getBytes(index, out, length);
/* 357 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 363 */     return unwrap().getBytes(index, out, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 369 */     return unwrap().getBytes(index, out, position, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 375 */     return unwrap().setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 381 */     return unwrap().setBytes(index, in, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 387 */     return unwrap().setBytes(index, in, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 392 */     return unwrap().nioBufferCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 397 */     return unwrap().nioBuffers(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 402 */     return unwrap().forEachByte(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 407 */     return unwrap().forEachByteDesc(index, length, processor);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\DuplicatedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */