/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.nio.channels.ScatteringByteChannel;
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
/*     */ abstract class PooledByteBuf<T>
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*     */   private final ObjectPool.Handle<PooledByteBuf<T>> recyclerHandle;
/*     */   protected PoolChunk<T> chunk;
/*     */   protected long handle;
/*     */   protected T memory;
/*     */   protected int offset;
/*     */   protected int length;
/*     */   int maxLength;
/*     */   PoolThreadCache cache;
/*     */   ByteBuffer tmpNioBuf;
/*     */   private ByteBufAllocator allocator;
/*     */   
/*     */   protected PooledByteBuf(ObjectPool.Handle<? extends PooledByteBuf<T>> recyclerHandle, int maxCapacity) {
/*  45 */     super(maxCapacity);
/*  46 */     this.recyclerHandle = (ObjectPool.Handle)recyclerHandle;
/*     */   }
/*     */ 
/*     */   
/*     */   void init(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
/*  51 */     init0(chunk, nioBuffer, handle, offset, length, maxLength, cache);
/*     */   }
/*     */   
/*     */   void initUnpooled(PoolChunk<T> chunk, int length) {
/*  55 */     init0(chunk, (ByteBuffer)null, 0L, chunk.offset, length, length, (PoolThreadCache)null);
/*     */   }
/*     */ 
/*     */   
/*     */   private void init0(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
/*  60 */     assert handle >= 0L;
/*  61 */     assert chunk != null;
/*     */     
/*  63 */     this.chunk = chunk;
/*  64 */     this.memory = chunk.memory;
/*  65 */     this.tmpNioBuf = nioBuffer;
/*  66 */     this.allocator = chunk.arena.parent;
/*  67 */     this.cache = cache;
/*  68 */     this.handle = handle;
/*  69 */     this.offset = offset;
/*  70 */     this.length = length;
/*  71 */     this.maxLength = maxLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void reuse(int maxCapacity) {
/*  78 */     maxCapacity(maxCapacity);
/*  79 */     resetRefCnt();
/*  80 */     setIndex0(0, 0);
/*  81 */     discardMarks();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int capacity() {
/*  86 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxFastWritableBytes() {
/*  91 */     return Math.min(this.maxLength, maxCapacity()) - this.writerIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf capacity(int newCapacity) {
/*  96 */     if (newCapacity == this.length) {
/*  97 */       ensureAccessible();
/*  98 */       return this;
/*     */     } 
/* 100 */     checkNewCapacity(newCapacity);
/* 101 */     if (!this.chunk.unpooled)
/*     */     {
/* 103 */       if (newCapacity > this.length) {
/* 104 */         if (newCapacity <= this.maxLength) {
/* 105 */           this.length = newCapacity;
/* 106 */           return this;
/*     */         } 
/* 108 */       } else if (newCapacity > this.maxLength >>> 1 && (this.maxLength > 512 || newCapacity > this.maxLength - 16)) {
/*     */ 
/*     */         
/* 111 */         this.length = newCapacity;
/* 112 */         trimIndicesToCapacity(newCapacity);
/* 113 */         return this;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 118 */     this.chunk.arena.reallocate(this, newCapacity, true);
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBufAllocator alloc() {
/* 124 */     return this.allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteOrder order() {
/* 129 */     return ByteOrder.BIG_ENDIAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf unwrap() {
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retainedDuplicate() {
/* 139 */     return PooledDuplicatedByteBuf.newInstance(this, this, readerIndex(), writerIndex());
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retainedSlice() {
/* 144 */     int index = readerIndex();
/* 145 */     return retainedSlice(index, writerIndex() - index);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retainedSlice(int index, int length) {
/* 150 */     return PooledSlicedByteBuf.newInstance(this, this, index, length);
/*     */   }
/*     */   
/*     */   protected final ByteBuffer internalNioBuffer() {
/* 154 */     ByteBuffer tmpNioBuf = this.tmpNioBuf;
/* 155 */     if (tmpNioBuf == null) {
/* 156 */       this.tmpNioBuf = tmpNioBuf = newInternalNioBuffer(this.memory);
/*     */     } else {
/* 158 */       tmpNioBuf.clear();
/*     */     } 
/* 160 */     return tmpNioBuf;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract ByteBuffer newInternalNioBuffer(T paramT);
/*     */   
/*     */   protected final void deallocate() {
/* 167 */     if (this.handle >= 0L) {
/* 168 */       long handle = this.handle;
/* 169 */       this.handle = -1L;
/* 170 */       this.memory = null;
/* 171 */       this.chunk.arena.free(this.chunk, this.tmpNioBuf, handle, this.maxLength, this.cache);
/* 172 */       this.tmpNioBuf = null;
/* 173 */       this.chunk = null;
/* 174 */       recycle();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void recycle() {
/* 179 */     this.recyclerHandle.recycle(this);
/*     */   }
/*     */   
/*     */   protected final int idx(int index) {
/* 183 */     return this.offset + index;
/*     */   }
/*     */   
/*     */   final ByteBuffer _internalNioBuffer(int index, int length, boolean duplicate) {
/* 187 */     index = idx(index);
/* 188 */     ByteBuffer buffer = duplicate ? newInternalNioBuffer(this.memory) : internalNioBuffer();
/* 189 */     buffer.limit(index + length).position(index);
/* 190 */     return buffer;
/*     */   }
/*     */   
/*     */   ByteBuffer duplicateInternalNioBuffer(int index, int length) {
/* 194 */     checkIndex(index, length);
/* 195 */     return _internalNioBuffer(index, length, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuffer internalNioBuffer(int index, int length) {
/* 200 */     checkIndex(index, length);
/* 201 */     return _internalNioBuffer(index, length, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int nioBufferCount() {
/* 206 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuffer nioBuffer(int index, int length) {
/* 211 */     return duplicateInternalNioBuffer(index, length).slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuffer[] nioBuffers(int index, int length) {
/* 216 */     return new ByteBuffer[] { nioBuffer(index, length) };
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isContiguous() {
/* 221 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 226 */     return out.write(duplicateInternalNioBuffer(index, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readBytes(GatheringByteChannel out, int length) throws IOException {
/* 231 */     checkReadableBytes(length);
/* 232 */     int readBytes = out.write(_internalNioBuffer(this.readerIndex, length, false));
/* 233 */     this.readerIndex += readBytes;
/* 234 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 239 */     return out.write(duplicateInternalNioBuffer(index, length), position);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readBytes(FileChannel out, long position, int length) throws IOException {
/* 244 */     checkReadableBytes(length);
/* 245 */     int readBytes = out.write(_internalNioBuffer(this.readerIndex, length, false), position);
/* 246 */     this.readerIndex += readBytes;
/* 247 */     return readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/*     */     try {
/* 253 */       return in.read(internalNioBuffer(index, length));
/* 254 */     } catch (ClosedChannelException ignored) {
/* 255 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/*     */     try {
/* 262 */       return in.read(internalNioBuffer(index, length), position);
/* 263 */     } catch (ClosedChannelException ignored) {
/* 264 */       return -1;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */