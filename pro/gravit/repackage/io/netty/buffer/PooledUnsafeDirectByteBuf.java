/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
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
/*     */ final class PooledUnsafeDirectByteBuf
/*     */   extends PooledByteBuf<ByteBuffer>
/*     */ {
/*  30 */   private static final ObjectPool<PooledUnsafeDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledUnsafeDirectByteBuf>()
/*     */       {
/*     */         public PooledUnsafeDirectByteBuf newObject(ObjectPool.Handle<PooledUnsafeDirectByteBuf> handle)
/*     */         {
/*  34 */           return new PooledUnsafeDirectByteBuf(handle, 0);
/*     */         }
/*     */       });
/*     */   
/*     */   static PooledUnsafeDirectByteBuf newInstance(int maxCapacity) {
/*  39 */     PooledUnsafeDirectByteBuf buf = (PooledUnsafeDirectByteBuf)RECYCLER.get();
/*  40 */     buf.reuse(maxCapacity);
/*  41 */     return buf;
/*     */   }
/*     */   
/*     */   private long memoryAddress;
/*     */   
/*     */   private PooledUnsafeDirectByteBuf(ObjectPool.Handle<PooledUnsafeDirectByteBuf> recyclerHandle, int maxCapacity) {
/*  47 */     super((ObjectPool.Handle)recyclerHandle, maxCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void init(PoolChunk<ByteBuffer> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
/*  53 */     super.init(chunk, nioBuffer, handle, offset, length, maxLength, cache);
/*  54 */     initMemoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   void initUnpooled(PoolChunk<ByteBuffer> chunk, int length) {
/*  59 */     super.initUnpooled(chunk, length);
/*  60 */     initMemoryAddress();
/*     */   }
/*     */   
/*     */   private void initMemoryAddress() {
/*  64 */     this.memoryAddress = PlatformDependent.directBufferAddress(this.memory) + this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
/*  69 */     return memory.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/*  74 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  79 */     return UnsafeByteBufUtil.getByte(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  84 */     return UnsafeByteBufUtil.getShort(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  89 */     return UnsafeByteBufUtil.getShortLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  94 */     return UnsafeByteBufUtil.getUnsignedMedium(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  99 */     return UnsafeByteBufUtil.getUnsignedMediumLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/* 104 */     return UnsafeByteBufUtil.getInt(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/* 109 */     return UnsafeByteBufUtil.getIntLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 114 */     return UnsafeByteBufUtil.getLong(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 119 */     return UnsafeByteBufUtil.getLongLE(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 124 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 130 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst, dstIndex, length);
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 136 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, dst);
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 142 */     UnsafeByteBufUtil.getBytes(this, addr(index), index, out, length);
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 148 */     UnsafeByteBufUtil.setByte(addr(index), (byte)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 153 */     UnsafeByteBufUtil.setShort(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 158 */     UnsafeByteBufUtil.setShortLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 163 */     UnsafeByteBufUtil.setMedium(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 168 */     UnsafeByteBufUtil.setMediumLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 173 */     UnsafeByteBufUtil.setInt(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 178 */     UnsafeByteBufUtil.setIntLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 183 */     UnsafeByteBufUtil.setLong(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 188 */     UnsafeByteBufUtil.setLongLE(addr(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 193 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src, srcIndex, length);
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 199 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src, srcIndex, length);
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 205 */     UnsafeByteBufUtil.setBytes(this, addr(index), index, src);
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 211 */     return UnsafeByteBufUtil.setBytes(this, addr(index), index, in, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 216 */     return UnsafeByteBufUtil.copy(this, addr(index), index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 221 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 226 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 231 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 236 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 241 */     ensureAccessible();
/* 242 */     return this.memoryAddress;
/*     */   }
/*     */   
/*     */   private long addr(int index) {
/* 246 */     return this.memoryAddress + index;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SwappedByteBuf newSwappedByteBuf() {
/* 251 */     if (PlatformDependent.isUnaligned())
/*     */     {
/* 253 */       return new UnsafeDirectSwappedByteBuf(this);
/*     */     }
/* 255 */     return super.newSwappedByteBuf();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setZero(int index, int length) {
/* 260 */     checkIndex(index, length);
/* 261 */     UnsafeByteBufUtil.setZero(addr(index), length);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeZero(int length) {
/* 267 */     ensureWritable(length);
/* 268 */     int wIndex = this.writerIndex;
/* 269 */     UnsafeByteBufUtil.setZero(addr(wIndex), length);
/* 270 */     this.writerIndex = wIndex + length;
/* 271 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledUnsafeDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */