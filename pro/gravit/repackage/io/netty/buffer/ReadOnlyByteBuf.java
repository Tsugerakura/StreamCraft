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
/*     */ public class ReadOnlyByteBuf
/*     */   extends AbstractDerivedByteBuf
/*     */ {
/*     */   private final ByteBuf buffer;
/*     */   
/*     */   public ReadOnlyByteBuf(ByteBuf buffer) {
/*  43 */     super(buffer.maxCapacity());
/*     */     
/*  45 */     if (buffer instanceof ReadOnlyByteBuf || buffer instanceof DuplicatedByteBuf) {
/*  46 */       this.buffer = buffer.unwrap();
/*     */     } else {
/*  48 */       this.buffer = buffer;
/*     */     } 
/*  50 */     setIndex(buffer.readerIndex(), buffer.writerIndex());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/*  55 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable(int numBytes) {
/*  65 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  70 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ensureWritable(int minWritableBytes) {
/*  75 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf unwrap() {
/*  80 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator alloc() {
/*  85 */     return unwrap().alloc();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteOrder order() {
/*  91 */     return unwrap().order();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/*  96 */     return unwrap().isDirect();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 106 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 111 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 116 */     return unwrap().hasMemoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 121 */     return unwrap().memoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf discardReadBytes() {
/* 126 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 131 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 136 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 141 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setByte(int index, int value) {
/* 146 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 151 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShort(int index, int value) {
/* 156 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 161 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setShortLE(int index, int value) {
/* 166 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 171 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMedium(int index, int value) {
/* 176 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 181 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setMediumLE(int index, int value) {
/* 186 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 191 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setInt(int index, int value) {
/* 196 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 201 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setIntLE(int index, int value) {
/* 206 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 211 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLong(int index, long value) {
/* 216 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 221 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setLongLE(int index, long value) {
/* 226 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 231 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) {
/* 236 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, ScatteringByteChannel in, int length) {
/* 241 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, FileChannel in, long position, int length) {
/* 246 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 252 */     return unwrap().getBytes(index, out, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 258 */     return unwrap().getBytes(index, out, position, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 264 */     unwrap().getBytes(index, out, length);
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 270 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 271 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 276 */     unwrap().getBytes(index, dst, dstIndex, length);
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 282 */     unwrap().getBytes(index, dst);
/* 283 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/* 288 */     return new ReadOnlyByteBuf(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 293 */     return unwrap().copy(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/* 298 */     return Unpooled.unmodifiableBuffer(unwrap().slice(index, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int index) {
/* 303 */     return unwrap().getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/* 308 */     return unwrap().getByte(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int index) {
/* 313 */     return unwrap().getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/* 318 */     return unwrap().getShort(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortLE(int index) {
/* 323 */     return unwrap().getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/* 328 */     return unwrap().getShortLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMedium(int index) {
/* 333 */     return unwrap().getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/* 338 */     return unwrap().getUnsignedMedium(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getUnsignedMediumLE(int index) {
/* 343 */     return unwrap().getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/* 348 */     return unwrap().getUnsignedMediumLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 353 */     return unwrap().getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 358 */     return unwrap().getInt(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntLE(int index) {
/* 363 */     return unwrap().getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 368 */     return unwrap().getIntLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int index) {
/* 373 */     return unwrap().getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 378 */     return unwrap().getLong(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongLE(int index) {
/* 383 */     return unwrap().getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 388 */     return unwrap().getLongLE(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 393 */     return unwrap().nioBufferCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 398 */     return unwrap().nioBuffer(index, length).asReadOnlyBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 403 */     return unwrap().nioBuffers(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 408 */     return unwrap().forEachByte(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 413 */     return unwrap().forEachByteDesc(index, length, processor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 418 */     return unwrap().capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf capacity(int newCapacity) {
/* 423 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf asReadOnly() {
/* 428 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ReadOnlyByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */