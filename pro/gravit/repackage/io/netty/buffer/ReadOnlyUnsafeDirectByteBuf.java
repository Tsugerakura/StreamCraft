/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ 
/*     */ 
/*     */ final class ReadOnlyUnsafeDirectByteBuf
/*     */   extends ReadOnlyByteBufferBuf
/*     */ {
/*     */   private final long memoryAddress;
/*     */   
/*     */   ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator allocator, ByteBuffer byteBuffer) {
/*  32 */     super(allocator, byteBuffer);
/*     */ 
/*     */     
/*  35 */     this.memoryAddress = PlatformDependent.directBufferAddress(this.buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  40 */     return UnsafeByteBufUtil.getByte(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  45 */     return UnsafeByteBufUtil.getShort(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  50 */     return UnsafeByteBufUtil.getUnsignedMedium(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  55 */     return UnsafeByteBufUtil.getInt(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/*  60 */     return UnsafeByteBufUtil.getLong(addr(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/*  65 */     checkIndex(index, length);
/*  66 */     ObjectUtil.checkNotNull(dst, "dst");
/*  67 */     if (dstIndex < 0 || dstIndex > dst.capacity() - length) {
/*  68 */       throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
/*     */     }
/*     */     
/*  71 */     if (dst.hasMemoryAddress()) {
/*  72 */       PlatformDependent.copyMemory(addr(index), dst.memoryAddress() + dstIndex, length);
/*  73 */     } else if (dst.hasArray()) {
/*  74 */       PlatformDependent.copyMemory(addr(index), dst.array(), dst.arrayOffset() + dstIndex, length);
/*     */     } else {
/*  76 */       dst.setBytes(dstIndex, this, index, length);
/*     */     } 
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/*  83 */     checkIndex(index, length);
/*  84 */     ObjectUtil.checkNotNull(dst, "dst");
/*  85 */     if (dstIndex < 0 || dstIndex > dst.length - length) {
/*  86 */       throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", new Object[] {
/*  87 */               Integer.valueOf(dstIndex), Integer.valueOf(length), Integer.valueOf(dst.length)
/*     */             }));
/*     */     }
/*  90 */     if (length != 0) {
/*  91 */       PlatformDependent.copyMemory(addr(index), dst, dstIndex, length);
/*     */     }
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf copy(int index, int length) {
/*  98 */     checkIndex(index, length);
/*  99 */     ByteBuf copy = alloc().directBuffer(length, maxCapacity());
/* 100 */     if (length != 0) {
/* 101 */       if (copy.hasMemoryAddress()) {
/* 102 */         PlatformDependent.copyMemory(addr(index), copy.memoryAddress(), length);
/* 103 */         copy.setIndex(0, length);
/*     */       } else {
/* 105 */         copy.writeBytes(this, index, length);
/*     */       } 
/*     */     }
/* 108 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 113 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long memoryAddress() {
/* 118 */     return this.memoryAddress;
/*     */   }
/*     */   
/*     */   private long addr(int index) {
/* 122 */     return this.memoryAddress + index;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ReadOnlyUnsafeDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */