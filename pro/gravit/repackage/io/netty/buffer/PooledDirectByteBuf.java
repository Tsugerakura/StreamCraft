/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
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
/*     */ final class PooledDirectByteBuf
/*     */   extends PooledByteBuf<ByteBuffer>
/*     */ {
/*  30 */   private static final ObjectPool<PooledDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledDirectByteBuf>()
/*     */       {
/*     */         public PooledDirectByteBuf newObject(ObjectPool.Handle<PooledDirectByteBuf> handle)
/*     */         {
/*  34 */           return new PooledDirectByteBuf(handle, 0);
/*     */         }
/*     */       });
/*     */   
/*     */   static PooledDirectByteBuf newInstance(int maxCapacity) {
/*  39 */     PooledDirectByteBuf buf = (PooledDirectByteBuf)RECYCLER.get();
/*  40 */     buf.reuse(maxCapacity);
/*  41 */     return buf;
/*     */   }
/*     */   
/*     */   private PooledDirectByteBuf(ObjectPool.Handle<PooledDirectByteBuf> recyclerHandle, int maxCapacity) {
/*  45 */     super((ObjectPool.Handle)recyclerHandle, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
/*  50 */     return memory.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/*  55 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  60 */     return this.memory.get(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  65 */     return this.memory.getShort(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  70 */     return ByteBufUtil.swapShort(_getShort(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  75 */     index = idx(index);
/*  76 */     return (this.memory.get(index) & 0xFF) << 16 | (this.memory
/*  77 */       .get(index + 1) & 0xFF) << 8 | this.memory
/*  78 */       .get(index + 2) & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  83 */     index = idx(index);
/*  84 */     return this.memory.get(index) & 0xFF | (this.memory
/*  85 */       .get(index + 1) & 0xFF) << 8 | (this.memory
/*  86 */       .get(index + 2) & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  91 */     return this.memory.getInt(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/*  96 */     return ByteBufUtil.swapInt(_getInt(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/* 101 */     return this.memory.getLong(idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/* 106 */     return ByteBufUtil.swapLong(_getLong(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 111 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 112 */     if (dst.hasArray()) {
/* 113 */       getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
/* 114 */     } else if (dst.nioBufferCount() > 0) {
/* 115 */       for (ByteBuffer bb : dst.nioBuffers(dstIndex, length)) {
/* 116 */         int bbLen = bb.remaining();
/* 117 */         getBytes(index, bb);
/* 118 */         index += bbLen;
/*     */       } 
/*     */     } else {
/* 121 */       dst.setBytes(dstIndex, this, index, length);
/*     */     } 
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 128 */     checkDstIndex(index, length, dstIndex, dst.length);
/* 129 */     _internalNioBuffer(index, length, true).get(dst, dstIndex, length);
/* 130 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/* 135 */     checkDstIndex(length, dstIndex, dst.length);
/* 136 */     _internalNioBuffer(this.readerIndex, length, false).get(dst, dstIndex, length);
/* 137 */     this.readerIndex += length;
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuffer dst) {
/* 143 */     dst.put(duplicateInternalNioBuffer(index, dst.remaining()));
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(ByteBuffer dst) {
/* 149 */     int length = dst.remaining();
/* 150 */     checkReadableBytes(length);
/* 151 */     dst.put(_internalNioBuffer(this.readerIndex, length, false));
/* 152 */     this.readerIndex += length;
/* 153 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 158 */     getBytes(index, out, length, false);
/* 159 */     return this;
/*     */   }
/*     */   
/*     */   private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
/* 163 */     checkIndex(index, length);
/* 164 */     if (length == 0) {
/*     */       return;
/*     */     }
/* 167 */     ByteBufUtil.readBytes(alloc(), internal ? internalNioBuffer() : this.memory.duplicate(), idx(index), length, out);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
/* 172 */     checkReadableBytes(length);
/* 173 */     getBytes(this.readerIndex, out, length, true);
/* 174 */     this.readerIndex += length;
/* 175 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 180 */     this.memory.put(idx(index), (byte)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 185 */     this.memory.putShort(idx(index), (short)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 190 */     _setShort(index, ByteBufUtil.swapShort((short)value));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 195 */     index = idx(index);
/* 196 */     this.memory.put(index, (byte)(value >>> 16));
/* 197 */     this.memory.put(index + 1, (byte)(value >>> 8));
/* 198 */     this.memory.put(index + 2, (byte)value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 203 */     index = idx(index);
/* 204 */     this.memory.put(index, (byte)value);
/* 205 */     this.memory.put(index + 1, (byte)(value >>> 8));
/* 206 */     this.memory.put(index + 2, (byte)(value >>> 16));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 211 */     this.memory.putInt(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 216 */     _setInt(index, ByteBufUtil.swapInt(value));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 221 */     this.memory.putLong(idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 226 */     _setLong(index, ByteBufUtil.swapLong(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 231 */     checkSrcIndex(index, length, srcIndex, src.capacity());
/* 232 */     if (src.hasArray()) {
/* 233 */       setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
/* 234 */     } else if (src.nioBufferCount() > 0) {
/* 235 */       for (ByteBuffer bb : src.nioBuffers(srcIndex, length)) {
/* 236 */         int bbLen = bb.remaining();
/* 237 */         setBytes(index, bb);
/* 238 */         index += bbLen;
/*     */       } 
/*     */     } else {
/* 241 */       src.getBytes(srcIndex, this, index, length);
/*     */     } 
/* 243 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 248 */     checkSrcIndex(index, length, srcIndex, src.length);
/* 249 */     _internalNioBuffer(index, length, false).put(src, srcIndex, length);
/* 250 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setBytes(int index, ByteBuffer src) {
/* 255 */     int length = src.remaining();
/* 256 */     checkIndex(index, length);
/* 257 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 258 */     if (src == tmpBuf) {
/* 259 */       src = src.duplicate();
/*     */     }
/*     */     
/* 262 */     index = idx(index);
/* 263 */     tmpBuf.limit(index + length).position(index);
/* 264 */     tmpBuf.put(src);
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 270 */     checkIndex(index, length);
/* 271 */     byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
/* 272 */     int readBytes = in.read(tmp, 0, length);
/* 273 */     if (readBytes <= 0) {
/* 274 */       return readBytes;
/*     */     }
/* 276 */     ByteBuffer tmpBuf = internalNioBuffer();
/* 277 */     tmpBuf.position(idx(index));
/* 278 */     tmpBuf.put(tmp, 0, readBytes);
/* 279 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/* 284 */     checkIndex(index, length);
/* 285 */     ByteBuf copy = alloc().directBuffer(length, maxCapacity());
/* 286 */     return copy.writeBytes(this, index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 291 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 296 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public int arrayOffset() {
/* 301 */     throw new UnsupportedOperationException("direct buffer");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 306 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 311 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */