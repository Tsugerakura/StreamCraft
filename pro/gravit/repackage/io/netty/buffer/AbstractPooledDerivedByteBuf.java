/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AbstractPooledDerivedByteBuf
/*     */   extends AbstractReferenceCountedByteBuf
/*     */ {
/*     */   private final ObjectPool.Handle<AbstractPooledDerivedByteBuf> recyclerHandle;
/*     */   private AbstractByteBuf rootParent;
/*     */   private ByteBuf parent;
/*     */   
/*     */   AbstractPooledDerivedByteBuf(ObjectPool.Handle<? extends AbstractPooledDerivedByteBuf> recyclerHandle) {
/*  42 */     super(0);
/*  43 */     this.recyclerHandle = (ObjectPool.Handle)recyclerHandle;
/*     */   }
/*     */ 
/*     */   
/*     */   final void parent(ByteBuf newParent) {
/*  48 */     assert newParent instanceof SimpleLeakAwareByteBuf;
/*  49 */     this.parent = newParent;
/*     */   }
/*     */ 
/*     */   
/*     */   public final AbstractByteBuf unwrap() {
/*  54 */     return this.rootParent;
/*     */   }
/*     */ 
/*     */   
/*     */   final <U extends AbstractPooledDerivedByteBuf> U init(AbstractByteBuf unwrapped, ByteBuf wrapped, int readerIndex, int writerIndex, int maxCapacity) {
/*  59 */     wrapped.retain();
/*  60 */     this.parent = wrapped;
/*  61 */     this.rootParent = unwrapped;
/*     */     
/*     */     try {
/*  64 */       maxCapacity(maxCapacity);
/*  65 */       setIndex0(readerIndex, writerIndex);
/*  66 */       resetRefCnt();
/*     */ 
/*     */       
/*  69 */       AbstractPooledDerivedByteBuf abstractPooledDerivedByteBuf = this;
/*  70 */       wrapped = null;
/*  71 */       return (U)abstractPooledDerivedByteBuf;
/*     */     } finally {
/*  73 */       if (wrapped != null) {
/*  74 */         this.parent = this.rootParent = null;
/*  75 */         wrapped.release();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void deallocate() {
/*  85 */     ByteBuf parent = this.parent;
/*  86 */     this.recyclerHandle.recycle(this);
/*  87 */     parent.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBufAllocator alloc() {
/*  92 */     return unwrap().alloc();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final ByteOrder order() {
/*  98 */     return unwrap().order();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 103 */     return unwrap().isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isDirect() {
/* 108 */     return unwrap().isDirect();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasArray() {
/* 113 */     return unwrap().hasArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] array() {
/* 118 */     return unwrap().array();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMemoryAddress() {
/* 123 */     return unwrap().hasMemoryAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isContiguous() {
/* 128 */     return unwrap().isContiguous();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int nioBufferCount() {
/* 133 */     return unwrap().nioBufferCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuffer internalNioBuffer(int index, int length) {
/* 138 */     return nioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retainedSlice() {
/* 143 */     int index = readerIndex();
/* 144 */     return retainedSlice(index, writerIndex() - index);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/* 149 */     ensureAccessible();
/*     */     
/* 151 */     return new PooledNonRetainedSlicedByteBuf(this, unwrap(), index, length);
/*     */   }
/*     */   
/*     */   final ByteBuf duplicate0() {
/* 155 */     ensureAccessible();
/*     */     
/* 157 */     return new PooledNonRetainedDuplicateByteBuf(this, unwrap());
/*     */   }
/*     */   
/*     */   private static final class PooledNonRetainedDuplicateByteBuf extends UnpooledDuplicatedByteBuf {
/*     */     private final ReferenceCounted referenceCountDelegate;
/*     */     
/*     */     PooledNonRetainedDuplicateByteBuf(ReferenceCounted referenceCountDelegate, AbstractByteBuf buffer) {
/* 164 */       super(buffer);
/* 165 */       this.referenceCountDelegate = referenceCountDelegate;
/*     */     }
/*     */ 
/*     */     
/*     */     int refCnt0() {
/* 170 */       return this.referenceCountDelegate.refCnt();
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf retain0() {
/* 175 */       this.referenceCountDelegate.retain();
/* 176 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf retain0(int increment) {
/* 181 */       this.referenceCountDelegate.retain(increment);
/* 182 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf touch0() {
/* 187 */       this.referenceCountDelegate.touch();
/* 188 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf touch0(Object hint) {
/* 193 */       this.referenceCountDelegate.touch(hint);
/* 194 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean release0() {
/* 199 */       return this.referenceCountDelegate.release();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean release0(int decrement) {
/* 204 */       return this.referenceCountDelegate.release(decrement);
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf duplicate() {
/* 209 */       ensureAccessible();
/* 210 */       return new PooledNonRetainedDuplicateByteBuf(this.referenceCountDelegate, this);
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf retainedDuplicate() {
/* 215 */       return PooledDuplicatedByteBuf.newInstance(unwrap(), this, readerIndex(), writerIndex());
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf slice(int index, int length) {
/* 220 */       checkIndex(index, length);
/* 221 */       return new AbstractPooledDerivedByteBuf.PooledNonRetainedSlicedByteBuf(this.referenceCountDelegate, unwrap(), index, length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBuf retainedSlice() {
/* 227 */       return retainedSlice(readerIndex(), capacity());
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf retainedSlice(int index, int length) {
/* 232 */       return PooledSlicedByteBuf.newInstance(unwrap(), this, index, length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class PooledNonRetainedSlicedByteBuf
/*     */     extends UnpooledSlicedByteBuf {
/*     */     private final ReferenceCounted referenceCountDelegate;
/*     */     
/*     */     PooledNonRetainedSlicedByteBuf(ReferenceCounted referenceCountDelegate, AbstractByteBuf buffer, int index, int length) {
/* 241 */       super(buffer, index, length);
/* 242 */       this.referenceCountDelegate = referenceCountDelegate;
/*     */     }
/*     */ 
/*     */     
/*     */     int refCnt0() {
/* 247 */       return this.referenceCountDelegate.refCnt();
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf retain0() {
/* 252 */       this.referenceCountDelegate.retain();
/* 253 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf retain0(int increment) {
/* 258 */       this.referenceCountDelegate.retain(increment);
/* 259 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf touch0() {
/* 264 */       this.referenceCountDelegate.touch();
/* 265 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuf touch0(Object hint) {
/* 270 */       this.referenceCountDelegate.touch(hint);
/* 271 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean release0() {
/* 276 */       return this.referenceCountDelegate.release();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean release0(int decrement) {
/* 281 */       return this.referenceCountDelegate.release(decrement);
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf duplicate() {
/* 286 */       ensureAccessible();
/* 287 */       return (new AbstractPooledDerivedByteBuf.PooledNonRetainedDuplicateByteBuf(this.referenceCountDelegate, unwrap()))
/* 288 */         .setIndex(idx(readerIndex()), idx(writerIndex()));
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf retainedDuplicate() {
/* 293 */       return PooledDuplicatedByteBuf.newInstance(unwrap(), this, idx(readerIndex()), idx(writerIndex()));
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf slice(int index, int length) {
/* 298 */       checkIndex(index, length);
/* 299 */       return new PooledNonRetainedSlicedByteBuf(this.referenceCountDelegate, unwrap(), idx(index), length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBuf retainedSlice() {
/* 305 */       return retainedSlice(0, capacity());
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf retainedSlice(int index, int length) {
/* 310 */       return PooledSlicedByteBuf.newInstance(unwrap(), this, idx(index), length);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractPooledDerivedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */