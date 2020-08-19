/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
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
/*     */ final class PooledUnsafeHeapByteBuf
/*     */   extends PooledHeapByteBuf
/*     */ {
/*  25 */   private static final ObjectPool<PooledUnsafeHeapByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledUnsafeHeapByteBuf>()
/*     */       {
/*     */         public PooledUnsafeHeapByteBuf newObject(ObjectPool.Handle<PooledUnsafeHeapByteBuf> handle)
/*     */         {
/*  29 */           return new PooledUnsafeHeapByteBuf(handle, 0);
/*     */         }
/*     */       });
/*     */   
/*     */   static PooledUnsafeHeapByteBuf newUnsafeInstance(int maxCapacity) {
/*  34 */     PooledUnsafeHeapByteBuf buf = (PooledUnsafeHeapByteBuf)RECYCLER.get();
/*  35 */     buf.reuse(maxCapacity);
/*  36 */     return buf;
/*     */   }
/*     */   
/*     */   private PooledUnsafeHeapByteBuf(ObjectPool.Handle<PooledUnsafeHeapByteBuf> recyclerHandle, int maxCapacity) {
/*  40 */     super((ObjectPool.Handle)recyclerHandle, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte _getByte(int index) {
/*  45 */     return UnsafeByteBufUtil.getByte(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShort(int index) {
/*  50 */     return UnsafeByteBufUtil.getShort(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected short _getShortLE(int index) {
/*  55 */     return UnsafeByteBufUtil.getShortLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMedium(int index) {
/*  60 */     return UnsafeByteBufUtil.getUnsignedMedium(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getUnsignedMediumLE(int index) {
/*  65 */     return UnsafeByteBufUtil.getUnsignedMediumLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getInt(int index) {
/*  70 */     return UnsafeByteBufUtil.getInt(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int _getIntLE(int index) {
/*  75 */     return UnsafeByteBufUtil.getIntLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLong(int index) {
/*  80 */     return UnsafeByteBufUtil.getLong(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected long _getLongLE(int index) {
/*  85 */     return UnsafeByteBufUtil.getLongLE(this.memory, idx(index));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setByte(int index, int value) {
/*  90 */     UnsafeByteBufUtil.setByte(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShort(int index, int value) {
/*  95 */     UnsafeByteBufUtil.setShort(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setShortLE(int index, int value) {
/* 100 */     UnsafeByteBufUtil.setShortLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMedium(int index, int value) {
/* 105 */     UnsafeByteBufUtil.setMedium(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setMediumLE(int index, int value) {
/* 110 */     UnsafeByteBufUtil.setMediumLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setInt(int index, int value) {
/* 115 */     UnsafeByteBufUtil.setInt(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setIntLE(int index, int value) {
/* 120 */     UnsafeByteBufUtil.setIntLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLong(int index, long value) {
/* 125 */     UnsafeByteBufUtil.setLong(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _setLongLE(int index, long value) {
/* 130 */     UnsafeByteBufUtil.setLongLE(this.memory, idx(index), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf setZero(int index, int length) {
/* 135 */     if (PlatformDependent.javaVersion() >= 7) {
/* 136 */       checkIndex(index, length);
/*     */       
/* 138 */       UnsafeByteBufUtil.setZero(this.memory, idx(index), length);
/* 139 */       return this;
/*     */     } 
/* 141 */     return super.setZero(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf writeZero(int length) {
/* 146 */     if (PlatformDependent.javaVersion() >= 7) {
/*     */       
/* 148 */       ensureWritable(length);
/* 149 */       int wIndex = this.writerIndex;
/* 150 */       UnsafeByteBufUtil.setZero(this.memory, idx(wIndex), length);
/* 151 */       this.writerIndex = wIndex + length;
/* 152 */       return this;
/*     */     } 
/* 154 */     return super.writeZero(length);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected SwappedByteBuf newSwappedByteBuf() {
/* 160 */     if (PlatformDependent.isUnaligned())
/*     */     {
/* 162 */       return new UnsafeHeapSwappedByteBuf(this);
/*     */     }
/* 164 */     return super.newSwappedByteBuf();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledUnsafeHeapByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */