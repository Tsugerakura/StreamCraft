/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ class SimpleLeakAwareByteBuf
/*     */   extends WrappedByteBuf
/*     */ {
/*     */   private final ByteBuf trackedByteBuf;
/*     */   final ResourceLeakTracker<ByteBuf> leak;
/*     */   
/*     */   SimpleLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
/*  36 */     super(wrapped);
/*  37 */     this.trackedByteBuf = (ByteBuf)ObjectUtil.checkNotNull(trackedByteBuf, "trackedByteBuf");
/*  38 */     this.leak = (ResourceLeakTracker<ByteBuf>)ObjectUtil.checkNotNull(leak, "leak");
/*     */   }
/*     */   
/*     */   SimpleLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
/*  42 */     this(wrapped, wrapped, leak);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice() {
/*  47 */     return newSharedLeakAwareByteBuf(super.slice());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice() {
/*  52 */     return unwrappedDerived(super.retainedSlice());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedSlice(int index, int length) {
/*  57 */     return unwrappedDerived(super.retainedSlice(index, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retainedDuplicate() {
/*  62 */     return unwrappedDerived(super.retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readRetainedSlice(int length) {
/*  67 */     return unwrappedDerived(super.readRetainedSlice(length));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf slice(int index, int length) {
/*  72 */     return newSharedLeakAwareByteBuf(super.slice(index, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf duplicate() {
/*  77 */     return newSharedLeakAwareByteBuf(super.duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readSlice(int length) {
/*  82 */     return newSharedLeakAwareByteBuf(super.readSlice(length));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf asReadOnly() {
/*  87 */     return newSharedLeakAwareByteBuf(super.asReadOnly());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch() {
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch(Object hint) {
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 102 */     if (super.release()) {
/* 103 */       closeLeak();
/* 104 */       return true;
/*     */     } 
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 111 */     if (super.release(decrement)) {
/* 112 */       closeLeak();
/* 113 */       return true;
/*     */     } 
/* 115 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeLeak() {
/* 121 */     boolean closed = this.leak.close(this.trackedByteBuf);
/* 122 */     assert closed;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf order(ByteOrder endianness) {
/* 127 */     if (order() == endianness) {
/* 128 */       return this;
/*     */     }
/* 130 */     return newSharedLeakAwareByteBuf(super.order(endianness));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuf unwrappedDerived(ByteBuf derived) {
/* 137 */     ByteBuf unwrappedDerived = unwrapSwapped(derived);
/*     */     
/* 139 */     if (unwrappedDerived instanceof AbstractPooledDerivedByteBuf) {
/*     */       
/* 141 */       ((AbstractPooledDerivedByteBuf)unwrappedDerived).parent(this);
/*     */       
/* 143 */       ResourceLeakTracker<ByteBuf> newLeak = AbstractByteBuf.leakDetector.track(derived);
/* 144 */       if (newLeak == null)
/*     */       {
/* 146 */         return derived;
/*     */       }
/* 148 */       return newLeakAwareByteBuf(derived, newLeak);
/*     */     } 
/* 150 */     return newSharedLeakAwareByteBuf(derived);
/*     */   }
/*     */ 
/*     */   
/*     */   private static ByteBuf unwrapSwapped(ByteBuf buf) {
/* 155 */     if (buf instanceof SwappedByteBuf) {
/*     */       do {
/* 157 */         buf = buf.unwrap();
/* 158 */       } while (buf instanceof SwappedByteBuf);
/*     */       
/* 160 */       return buf;
/*     */     } 
/* 162 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   private SimpleLeakAwareByteBuf newSharedLeakAwareByteBuf(ByteBuf wrapped) {
/* 167 */     return newLeakAwareByteBuf(wrapped, this.trackedByteBuf, this.leak);
/*     */   }
/*     */ 
/*     */   
/*     */   private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leakTracker) {
/* 172 */     return newLeakAwareByteBuf(wrapped, wrapped, leakTracker);
/*     */   }
/*     */ 
/*     */   
/*     */   protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
/* 177 */     return new SimpleLeakAwareByteBuf(buf, trackedByteBuf, leakTracker);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\SimpleLeakAwareByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */