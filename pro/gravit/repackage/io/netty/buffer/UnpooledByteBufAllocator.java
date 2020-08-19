/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.util.internal.LongCounter;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public final class UnpooledByteBufAllocator
/*     */   extends AbstractByteBufAllocator
/*     */   implements ByteBufAllocatorMetricProvider
/*     */ {
/*  29 */   private final UnpooledByteBufAllocatorMetric metric = new UnpooledByteBufAllocatorMetric();
/*     */ 
/*     */   
/*     */   private final boolean disableLeakDetector;
/*     */   
/*     */   private final boolean noCleaner;
/*     */   
/*  36 */   public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(
/*  37 */       PlatformDependent.directBufferPreferred());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnpooledByteBufAllocator(boolean preferDirect) {
/*  46 */     this(preferDirect, false);
/*     */   }
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
/*     */   public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector) {
/*  59 */     this(preferDirect, disableLeakDetector, PlatformDependent.useDirectBufferNoCleaner());
/*     */   }
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
/*     */   public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector, boolean tryNoCleaner) {
/*  74 */     super(preferDirect);
/*  75 */     this.disableLeakDetector = disableLeakDetector;
/*  76 */     this
/*  77 */       .noCleaner = (tryNoCleaner && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor());
/*     */   }
/*     */ 
/*     */   
/*     */   protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
/*  82 */     return PlatformDependent.hasUnsafe() ? new InstrumentedUnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity) : new InstrumentedUnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
/*     */     ByteBuf buf;
/*  90 */     if (PlatformDependent.hasUnsafe()) {
/*  91 */       buf = this.noCleaner ? new InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(this, initialCapacity, maxCapacity) : new InstrumentedUnpooledUnsafeDirectByteBuf(this, initialCapacity, maxCapacity);
/*     */     } else {
/*     */       
/*  94 */       buf = new InstrumentedUnpooledDirectByteBuf(this, initialCapacity, maxCapacity);
/*     */     } 
/*  96 */     return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
/* 101 */     CompositeByteBuf buf = new CompositeByteBuf(this, false, maxNumComponents);
/* 102 */     return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
/* 107 */     CompositeByteBuf buf = new CompositeByteBuf(this, true, maxNumComponents);
/* 108 */     return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectBufferPooled() {
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocatorMetric metric() {
/* 118 */     return this.metric;
/*     */   }
/*     */   
/*     */   void incrementDirect(int amount) {
/* 122 */     this.metric.directCounter.add(amount);
/*     */   }
/*     */   
/*     */   void decrementDirect(int amount) {
/* 126 */     this.metric.directCounter.add(-amount);
/*     */   }
/*     */   
/*     */   void incrementHeap(int amount) {
/* 130 */     this.metric.heapCounter.add(amount);
/*     */   }
/*     */   
/*     */   void decrementHeap(int amount) {
/* 134 */     this.metric.heapCounter.add(-amount);
/*     */   }
/*     */   
/*     */   private static final class InstrumentedUnpooledUnsafeHeapByteBuf extends UnpooledUnsafeHeapByteBuf {
/*     */     InstrumentedUnpooledUnsafeHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 139 */       super(alloc, initialCapacity, maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected byte[] allocateArray(int initialCapacity) {
/* 144 */       byte[] bytes = super.allocateArray(initialCapacity);
/* 145 */       ((UnpooledByteBufAllocator)alloc()).incrementHeap(bytes.length);
/* 146 */       return bytes;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void freeArray(byte[] array) {
/* 151 */       int length = array.length;
/* 152 */       super.freeArray(array);
/* 153 */       ((UnpooledByteBufAllocator)alloc()).decrementHeap(length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class InstrumentedUnpooledHeapByteBuf extends UnpooledHeapByteBuf {
/*     */     InstrumentedUnpooledHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 159 */       super(alloc, initialCapacity, maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected byte[] allocateArray(int initialCapacity) {
/* 164 */       byte[] bytes = super.allocateArray(initialCapacity);
/* 165 */       ((UnpooledByteBufAllocator)alloc()).incrementHeap(bytes.length);
/* 166 */       return bytes;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void freeArray(byte[] array) {
/* 171 */       int length = array.length;
/* 172 */       super.freeArray(array);
/* 173 */       ((UnpooledByteBufAllocator)alloc()).decrementHeap(length);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf
/*     */     extends UnpooledUnsafeNoCleanerDirectByteBuf
/*     */   {
/*     */     InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 181 */       super(alloc, initialCapacity, maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected ByteBuffer allocateDirect(int initialCapacity) {
/* 186 */       ByteBuffer buffer = super.allocateDirect(initialCapacity);
/* 187 */       ((UnpooledByteBufAllocator)alloc()).incrementDirect(buffer.capacity());
/* 188 */       return buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     ByteBuffer reallocateDirect(ByteBuffer oldBuffer, int initialCapacity) {
/* 193 */       int capacity = oldBuffer.capacity();
/* 194 */       ByteBuffer buffer = super.reallocateDirect(oldBuffer, initialCapacity);
/* 195 */       ((UnpooledByteBufAllocator)alloc()).incrementDirect(buffer.capacity() - capacity);
/* 196 */       return buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void freeDirect(ByteBuffer buffer) {
/* 201 */       int capacity = buffer.capacity();
/* 202 */       super.freeDirect(buffer);
/* 203 */       ((UnpooledByteBufAllocator)alloc()).decrementDirect(capacity);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class InstrumentedUnpooledUnsafeDirectByteBuf
/*     */     extends UnpooledUnsafeDirectByteBuf {
/*     */     InstrumentedUnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 210 */       super(alloc, initialCapacity, maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected ByteBuffer allocateDirect(int initialCapacity) {
/* 215 */       ByteBuffer buffer = super.allocateDirect(initialCapacity);
/* 216 */       ((UnpooledByteBufAllocator)alloc()).incrementDirect(buffer.capacity());
/* 217 */       return buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void freeDirect(ByteBuffer buffer) {
/* 222 */       int capacity = buffer.capacity();
/* 223 */       super.freeDirect(buffer);
/* 224 */       ((UnpooledByteBufAllocator)alloc()).decrementDirect(capacity);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class InstrumentedUnpooledDirectByteBuf
/*     */     extends UnpooledDirectByteBuf {
/*     */     InstrumentedUnpooledDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 231 */       super(alloc, initialCapacity, maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected ByteBuffer allocateDirect(int initialCapacity) {
/* 236 */       ByteBuffer buffer = super.allocateDirect(initialCapacity);
/* 237 */       ((UnpooledByteBufAllocator)alloc()).incrementDirect(buffer.capacity());
/* 238 */       return buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void freeDirect(ByteBuffer buffer) {
/* 243 */       int capacity = buffer.capacity();
/* 244 */       super.freeDirect(buffer);
/* 245 */       ((UnpooledByteBufAllocator)alloc()).decrementDirect(capacity);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class UnpooledByteBufAllocatorMetric implements ByteBufAllocatorMetric {
/* 250 */     final LongCounter directCounter = PlatformDependent.newLongCounter();
/* 251 */     final LongCounter heapCounter = PlatformDependent.newLongCounter();
/*     */ 
/*     */     
/*     */     public long usedHeapMemory() {
/* 255 */       return this.heapCounter.value();
/*     */     }
/*     */ 
/*     */     
/*     */     public long usedDirectMemory() {
/* 260 */       return this.directCounter.value();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 265 */       return StringUtil.simpleClassName(this) + "(usedHeapMemory: " + 
/* 266 */         usedHeapMemory() + "; usedDirectMemory: " + usedDirectMemory() + ')';
/*     */     }
/*     */     
/*     */     private UnpooledByteBufAllocatorMetric() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */