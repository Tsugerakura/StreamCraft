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
/*     */ class PooledHeapByteBuf
/*     */   extends PooledByteBuf<byte[]>
/*     */ {
/*  29 */   private static final ObjectPool<PooledHeapByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledHeapByteBuf>()
/*     */       {
/*     */         public PooledHeapByteBuf newObject(ObjectPool.Handle<PooledHeapByteBuf> handle)
/*     */         {
/*  33 */           return new PooledHeapByteBuf(handle, 0);
/*     */         }
/*     */       });
/*     */   
/*     */   static PooledHeapByteBuf newInstance(int maxCapacity) {
/*  38 */     PooledHeapByteBuf buf = (PooledHeapByteBuf)RECYCLER.get();
/*  39 */     buf.reuse(maxCapacity);
/*  40 */     return buf;
/*     */   }
/*     */   
/*     */   PooledHeapByteBuf(ObjectPool.Handle<? extends PooledHeapByteBuf> recyclerHandle, int maxCapacity) {
/*  44 */     super((ObjectPool.Handle)recyclerHandle, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isDirect() {
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  54 */     return HeapByteBufUtil.getByte(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  59 */     return HeapByteBufUtil.getShort(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  64 */     return HeapByteBufUtil.getShortLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  69 */     return HeapByteBufUtil.getUnsignedMedium(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  74 */     return HeapByteBufUtil.getUnsignedMediumLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  79 */     return HeapByteBufUtil.getInt(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/*  84 */     return HeapByteBufUtil.getIntLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/*  89 */     return HeapByteBufUtil.getLong(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/*  94 */     return HeapByteBufUtil.getLongLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  99 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 100 */     if (dst.hasMemoryAddress()) {
/* 101 */       PlatformDependent.copyMemory(this.memory, idx(index), dst.memoryAddress() + dstIndex, length);
/* 102 */     } else if (dst.hasArray()) {
/* 103 */       getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
/*     */     } else {
/* 105 */       dst.setBytes(dstIndex, this.memory, idx(index), length);
/*     */     } 
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 112 */     checkDstIndex(index, length, dstIndex, dst.length);
/* 113 */     System.arraycopy(this.memory, idx(index), dst, dstIndex, length);
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf getBytes(int index, ByteBuffer dst) {
/* 119 */     int length = dst.remaining();
/* 120 */     checkIndex(index, length);
/* 121 */     dst.put(this.memory, idx(index), length);
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 127 */     checkIndex(index, length);
/* 128 */     out.write(this.memory, idx(index), length);
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/* 134 */     HeapByteBufUtil.setByte(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/* 139 */     HeapByteBufUtil.setShort(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 144 */     HeapByteBufUtil.setShortLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 149 */     HeapByteBufUtil.setMedium(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 154 */     HeapByteBufUtil.setMediumLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 159 */     HeapByteBufUtil.setInt(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 164 */     HeapByteBufUtil.setIntLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 169 */     HeapByteBufUtil.setLong(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 174 */     HeapByteBufUtil.setLongLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 179 */     checkSrcIndex(index, length, srcIndex, src.capacity());
/* 180 */     if (src.hasMemoryAddress()) {
/* 181 */       PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, this.memory, idx(index), length);
/* 182 */     } else if (src.hasArray()) {
/* 183 */       setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
/*     */     } else {
/* 185 */       src.getBytes(srcIndex, this.memory, idx(index), length);
/*     */     } 
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 192 */     checkSrcIndex(index, length, srcIndex, src.length);
/* 193 */     System.arraycopy(src, srcIndex, this.memory, idx(index), length);
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf setBytes(int index, ByteBuffer src) {
/* 199 */     int length = src.remaining();
/* 200 */     checkIndex(index, length);
/* 201 */     src.get(this.memory, idx(index), length);
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int setBytes(int index, InputStream in, int length) throws IOException {
/* 207 */     checkIndex(index, length);
/* 208 */     return in.read(this.memory, idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf copy(int index, int length) {
/* 213 */     checkIndex(index, length);
/* 214 */     ByteBuf copy = alloc().heapBuffer(length, maxCapacity());
/* 215 */     return copy.writeBytes(this.memory, idx(index), length);
/*     */   }
/*     */ 
/*     */   
/*     */   final ByteBuffer duplicateInternalNioBuffer(int index, int length) {
/* 220 */     checkIndex(index, length);
/* 221 */     return ByteBuffer.wrap(this.memory, idx(index), length).slice();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean hasArray() {
/* 226 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final byte[] array() {
/* 231 */     ensureAccessible();
/* 232 */     return this.memory;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int arrayOffset() {
/* 237 */     return this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean hasMemoryAddress() {
/* 242 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final long memoryAddress() {
/* 247 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected final ByteBuffer newInternalNioBuffer(byte[] memory) {
/* 252 */     return ByteBuffer.wrap(memory);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledHeapByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */