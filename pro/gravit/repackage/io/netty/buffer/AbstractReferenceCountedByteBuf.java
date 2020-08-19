/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ReferenceCountUpdater;
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
/*     */ public abstract class AbstractReferenceCountedByteBuf
/*     */   extends AbstractByteBuf
/*     */ {
/*  28 */   private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, "refCnt");
/*     */   
/*  30 */   private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");
/*     */   
/*  32 */   private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>()
/*     */     {
/*     */       protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater()
/*     */       {
/*  36 */         return AbstractReferenceCountedByteBuf.AIF_UPDATER;
/*     */       }
/*     */       
/*     */       protected long unsafeOffset() {
/*  40 */         return AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*  45 */   private volatile int refCnt = updater
/*  46 */     .initialValue();
/*     */   
/*     */   protected AbstractReferenceCountedByteBuf(int maxCapacity) {
/*  49 */     super(maxCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isAccessible() {
/*  56 */     return updater.isLiveNonVolatile(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  61 */     return updater.refCnt(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setRefCnt(int refCnt) {
/*  68 */     updater.setRefCnt(this, refCnt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void resetRefCnt() {
/*  75 */     updater.resetRefCnt(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain() {
/*  80 */     return (ByteBuf)updater.retain(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf retain(int increment) {
/*  85 */     return (ByteBuf)updater.retain(this, increment);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch() {
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf touch(Object hint) {
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 100 */     return handleRelease(updater.release(this));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 105 */     return handleRelease(updater.release(this, decrement));
/*     */   }
/*     */   
/*     */   private boolean handleRelease(boolean result) {
/* 109 */     if (result) {
/* 110 */       deallocate();
/*     */     }
/* 112 */     return result;
/*     */   }
/*     */   
/*     */   protected abstract void deallocate();
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractReferenceCountedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */