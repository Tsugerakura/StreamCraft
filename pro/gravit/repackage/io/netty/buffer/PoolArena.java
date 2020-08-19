/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.LongCounter;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ 
/*     */ abstract class PoolArena<T> implements PoolArenaMetric {
/*     */   static final int numTinySubpagePools = 32;
/*     */   final PooledByteBufAllocator parent;
/*     */   private final int maxOrder;
/*     */   final int pageSize;
/*     */   final int pageShifts;
/*     */   final int chunkSize;
/*     */   final int subpageOverflowMask;
/*     */   final int numSmallSubpagePools;
/*     */   final int directMemoryCacheAlignment;
/*     */   final int directMemoryCacheAlignmentMask;
/*     */   private final PoolSubpage<T>[] tinySubpagePools;
/*     */   private final PoolSubpage<T>[] smallSubpagePools;
/*     */   private final PoolChunkList<T> q050;
/*     */   private final PoolChunkList<T> q025;
/*     */   private final PoolChunkList<T> q000;
/*     */   private final PoolChunkList<T> qInit;
/*     */   private final PoolChunkList<T> q075;
/*     */   private final PoolChunkList<T> q100;
/*     */   private final List<PoolChunkListMetric> chunkListMetrics;
/*     */   private long allocationsNormal;
/*  33 */   static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
/*     */   
/*     */   enum SizeClass {
/*  36 */     Tiny,
/*  37 */     Small,
/*  38 */     Normal;
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
/*  68 */   private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
/*  69 */   private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
/*  70 */   private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
/*  71 */   private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
/*     */   
/*     */   private long deallocationsTiny;
/*     */   
/*     */   private long deallocationsSmall;
/*     */   
/*     */   private long deallocationsNormal;
/*  78 */   private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
/*     */ 
/*     */   
/*  81 */   final AtomicInteger numThreadCaches = new AtomicInteger();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int cacheAlignment) {
/*  88 */     this.parent = parent;
/*  89 */     this.pageSize = pageSize;
/*  90 */     this.maxOrder = maxOrder;
/*  91 */     this.pageShifts = pageShifts;
/*  92 */     this.chunkSize = chunkSize;
/*  93 */     this.directMemoryCacheAlignment = cacheAlignment;
/*  94 */     this.directMemoryCacheAlignmentMask = cacheAlignment - 1;
/*  95 */     this.subpageOverflowMask = pageSize - 1 ^ 0xFFFFFFFF;
/*  96 */     this.tinySubpagePools = newSubpagePoolArray(32); int i;
/*  97 */     for (i = 0; i < this.tinySubpagePools.length; i++) {
/*  98 */       this.tinySubpagePools[i] = newSubpagePoolHead(pageSize);
/*     */     }
/*     */     
/* 101 */     this.numSmallSubpagePools = pageShifts - 9;
/* 102 */     this.smallSubpagePools = newSubpagePoolArray(this.numSmallSubpagePools);
/* 103 */     for (i = 0; i < this.smallSubpagePools.length; i++) {
/* 104 */       this.smallSubpagePools[i] = newSubpagePoolHead(pageSize);
/*     */     }
/*     */     
/* 107 */     this.q100 = new PoolChunkList<T>(this, null, 100, 2147483647, chunkSize);
/* 108 */     this.q075 = new PoolChunkList<T>(this, this.q100, 75, 100, chunkSize);
/* 109 */     this.q050 = new PoolChunkList<T>(this, this.q075, 50, 100, chunkSize);
/* 110 */     this.q025 = new PoolChunkList<T>(this, this.q050, 25, 75, chunkSize);
/* 111 */     this.q000 = new PoolChunkList<T>(this, this.q025, 1, 50, chunkSize);
/* 112 */     this.qInit = new PoolChunkList<T>(this, this.q000, -2147483648, 25, chunkSize);
/*     */     
/* 114 */     this.q100.prevList(this.q075);
/* 115 */     this.q075.prevList(this.q050);
/* 116 */     this.q050.prevList(this.q025);
/* 117 */     this.q025.prevList(this.q000);
/* 118 */     this.q000.prevList(null);
/* 119 */     this.qInit.prevList(this.qInit);
/*     */     
/* 121 */     List<PoolChunkListMetric> metrics = new ArrayList<PoolChunkListMetric>(6);
/* 122 */     metrics.add(this.qInit);
/* 123 */     metrics.add(this.q000);
/* 124 */     metrics.add(this.q025);
/* 125 */     metrics.add(this.q050);
/* 126 */     metrics.add(this.q075);
/* 127 */     metrics.add(this.q100);
/* 128 */     this.chunkListMetrics = Collections.unmodifiableList(metrics);
/*     */   }
/*     */   
/*     */   private PoolSubpage<T> newSubpagePoolHead(int pageSize) {
/* 132 */     PoolSubpage<T> head = new PoolSubpage<T>(pageSize);
/* 133 */     head.prev = head;
/* 134 */     head.next = head;
/* 135 */     return head;
/*     */   }
/*     */ 
/*     */   
/*     */   private PoolSubpage<T>[] newSubpagePoolArray(int size) {
/* 140 */     return (PoolSubpage<T>[])new PoolSubpage[size];
/*     */   }
/*     */   
/*     */   abstract boolean isDirect();
/*     */   
/*     */   PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
/* 146 */     PooledByteBuf<T> buf = newByteBuf(maxCapacity);
/* 147 */     allocate(cache, buf, reqCapacity);
/* 148 */     return buf;
/*     */   }
/*     */   
/*     */   static int tinyIdx(int normCapacity) {
/* 152 */     return normCapacity >>> 4;
/*     */   }
/*     */   
/*     */   static int smallIdx(int normCapacity) {
/* 156 */     int tableIdx = 0;
/* 157 */     int i = normCapacity >>> 10;
/* 158 */     while (i != 0) {
/* 159 */       i >>>= 1;
/* 160 */       tableIdx++;
/*     */     } 
/* 162 */     return tableIdx;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isTinyOrSmall(int normCapacity) {
/* 167 */     return ((normCapacity & this.subpageOverflowMask) == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean isTiny(int normCapacity) {
/* 172 */     return ((normCapacity & 0xFFFFFE00) == 0);
/*     */   }
/*     */   
/*     */   private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
/* 176 */     int normCapacity = normalizeCapacity(reqCapacity);
/* 177 */     if (isTinyOrSmall(normCapacity)) {
/*     */       int tableIdx;
/*     */       PoolSubpage<T>[] table;
/* 180 */       boolean tiny = isTiny(normCapacity);
/* 181 */       if (tiny) {
/* 182 */         if (cache.allocateTiny(this, buf, reqCapacity, normCapacity)) {
/*     */           return;
/*     */         }
/*     */         
/* 186 */         tableIdx = tinyIdx(normCapacity);
/* 187 */         table = this.tinySubpagePools;
/*     */       } else {
/* 189 */         if (cache.allocateSmall(this, buf, reqCapacity, normCapacity)) {
/*     */           return;
/*     */         }
/*     */         
/* 193 */         tableIdx = smallIdx(normCapacity);
/* 194 */         table = this.smallSubpagePools;
/*     */       } 
/*     */       
/* 197 */       PoolSubpage<T> head = table[tableIdx];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 203 */       synchronized (head) {
/* 204 */         PoolSubpage<T> s = head.next;
/* 205 */         if (s != head) {
/* 206 */           assert s.doNotDestroy && s.elemSize == normCapacity;
/* 207 */           long handle = s.allocate();
/* 208 */           assert handle >= 0L;
/* 209 */           s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity);
/* 210 */           incTinySmallAllocation(tiny);
/*     */           return;
/*     */         } 
/*     */       } 
/* 214 */       synchronized (this) {
/* 215 */         allocateNormal(buf, reqCapacity, normCapacity);
/*     */       } 
/*     */       
/* 218 */       incTinySmallAllocation(tiny);
/*     */       return;
/*     */     } 
/* 221 */     if (normCapacity <= this.chunkSize) {
/* 222 */       if (cache.allocateNormal(this, buf, reqCapacity, normCapacity)) {
/*     */         return;
/*     */       }
/*     */       
/* 226 */       synchronized (this) {
/* 227 */         allocateNormal(buf, reqCapacity, normCapacity);
/* 228 */         this.allocationsNormal++;
/*     */       } 
/*     */     } else {
/*     */       
/* 232 */       allocateHuge(buf, reqCapacity);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
/* 238 */     if (this.q050.allocate(buf, reqCapacity, normCapacity) || this.q025.allocate(buf, reqCapacity, normCapacity) || this.q000
/* 239 */       .allocate(buf, reqCapacity, normCapacity) || this.qInit.allocate(buf, reqCapacity, normCapacity) || this.q075
/* 240 */       .allocate(buf, reqCapacity, normCapacity)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 245 */     PoolChunk<T> c = newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
/* 246 */     boolean success = c.allocate(buf, reqCapacity, normCapacity);
/* 247 */     assert success;
/* 248 */     this.qInit.add(c);
/*     */   }
/*     */   
/*     */   private void incTinySmallAllocation(boolean tiny) {
/* 252 */     if (tiny) {
/* 253 */       this.allocationsTiny.increment();
/*     */     } else {
/* 255 */       this.allocationsSmall.increment();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
/* 260 */     PoolChunk<T> chunk = newUnpooledChunk(reqCapacity);
/* 261 */     this.activeBytesHuge.add(chunk.chunkSize());
/* 262 */     buf.initUnpooled(chunk, reqCapacity);
/* 263 */     this.allocationsHuge.increment();
/*     */   }
/*     */   
/*     */   void free(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolThreadCache cache) {
/* 267 */     if (chunk.unpooled) {
/* 268 */       int size = chunk.chunkSize();
/* 269 */       destroyChunk(chunk);
/* 270 */       this.activeBytesHuge.add(-size);
/* 271 */       this.deallocationsHuge.increment();
/*     */     } else {
/* 273 */       SizeClass sizeClass = sizeClass(normCapacity);
/* 274 */       if (cache != null && cache.add(this, chunk, nioBuffer, handle, normCapacity, sizeClass)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 279 */       freeChunk(chunk, handle, sizeClass, nioBuffer, false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private SizeClass sizeClass(int normCapacity) {
/* 284 */     if (!isTinyOrSmall(normCapacity)) {
/* 285 */       return SizeClass.Normal;
/*     */     }
/* 287 */     return isTiny(normCapacity) ? SizeClass.Tiny : SizeClass.Small;
/*     */   }
/*     */   
/*     */   void freeChunk(PoolChunk<T> chunk, long handle, SizeClass sizeClass, ByteBuffer nioBuffer, boolean finalizer) {
/*     */     boolean destroyChunk;
/* 292 */     synchronized (this) {
/*     */ 
/*     */       
/* 295 */       if (!finalizer) {
/* 296 */         switch (sizeClass) {
/*     */           case Normal:
/* 298 */             this.deallocationsNormal++;
/*     */             break;
/*     */           case Small:
/* 301 */             this.deallocationsSmall++;
/*     */             break;
/*     */           case Tiny:
/* 304 */             this.deallocationsTiny++;
/*     */             break;
/*     */           default:
/* 307 */             throw new Error();
/*     */         } 
/*     */       }
/* 310 */       destroyChunk = !chunk.parent.free(chunk, handle, nioBuffer);
/*     */     } 
/* 312 */     if (destroyChunk)
/*     */     {
/* 314 */       destroyChunk(chunk);
/*     */     }
/*     */   }
/*     */   
/*     */   PoolSubpage<T> findSubpagePoolHead(int elemSize) {
/*     */     int tableIdx;
/*     */     PoolSubpage<T>[] table;
/* 321 */     if (isTiny(elemSize)) {
/* 322 */       tableIdx = elemSize >>> 4;
/* 323 */       table = this.tinySubpagePools;
/*     */     } else {
/* 325 */       tableIdx = 0;
/* 326 */       elemSize >>>= 10;
/* 327 */       while (elemSize != 0) {
/* 328 */         elemSize >>>= 1;
/* 329 */         tableIdx++;
/*     */       } 
/* 331 */       table = this.smallSubpagePools;
/*     */     } 
/*     */     
/* 334 */     return table[tableIdx];
/*     */   }
/*     */   
/*     */   int normalizeCapacity(int reqCapacity) {
/* 338 */     ObjectUtil.checkPositiveOrZero(reqCapacity, "reqCapacity");
/*     */     
/* 340 */     if (reqCapacity >= this.chunkSize) {
/* 341 */       return (this.directMemoryCacheAlignment == 0) ? reqCapacity : alignCapacity(reqCapacity);
/*     */     }
/*     */     
/* 344 */     if (!isTiny(reqCapacity)) {
/*     */ 
/*     */       
/* 347 */       int normalizedCapacity = reqCapacity;
/* 348 */       normalizedCapacity--;
/* 349 */       normalizedCapacity |= normalizedCapacity >>> 1;
/* 350 */       normalizedCapacity |= normalizedCapacity >>> 2;
/* 351 */       normalizedCapacity |= normalizedCapacity >>> 4;
/* 352 */       normalizedCapacity |= normalizedCapacity >>> 8;
/* 353 */       normalizedCapacity |= normalizedCapacity >>> 16;
/* 354 */       normalizedCapacity++;
/*     */       
/* 356 */       if (normalizedCapacity < 0) {
/* 357 */         normalizedCapacity >>>= 1;
/*     */       }
/* 359 */       assert this.directMemoryCacheAlignment == 0 || (normalizedCapacity & this.directMemoryCacheAlignmentMask) == 0;
/*     */       
/* 361 */       return normalizedCapacity;
/*     */     } 
/*     */     
/* 364 */     if (this.directMemoryCacheAlignment > 0) {
/* 365 */       return alignCapacity(reqCapacity);
/*     */     }
/*     */ 
/*     */     
/* 369 */     if ((reqCapacity & 0xF) == 0) {
/* 370 */       return reqCapacity;
/*     */     }
/*     */     
/* 373 */     return (reqCapacity & 0xFFFFFFF0) + 16;
/*     */   }
/*     */   
/*     */   int alignCapacity(int reqCapacity) {
/* 377 */     int delta = reqCapacity & this.directMemoryCacheAlignmentMask;
/* 378 */     return (delta == 0) ? reqCapacity : (reqCapacity + this.directMemoryCacheAlignment - delta);
/*     */   }
/*     */   void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
/*     */     int bytesToCopy;
/* 382 */     assert newCapacity >= 0 && newCapacity <= buf.maxCapacity();
/*     */     
/* 384 */     int oldCapacity = buf.length;
/* 385 */     if (oldCapacity == newCapacity) {
/*     */       return;
/*     */     }
/*     */     
/* 389 */     PoolChunk<T> oldChunk = buf.chunk;
/* 390 */     ByteBuffer oldNioBuffer = buf.tmpNioBuf;
/* 391 */     long oldHandle = buf.handle;
/* 392 */     T oldMemory = buf.memory;
/* 393 */     int oldOffset = buf.offset;
/* 394 */     int oldMaxLength = buf.maxLength;
/*     */ 
/*     */     
/* 397 */     allocate(this.parent.threadCache(), buf, newCapacity);
/*     */     
/* 399 */     if (newCapacity > oldCapacity) {
/* 400 */       bytesToCopy = oldCapacity;
/*     */     } else {
/* 402 */       buf.trimIndicesToCapacity(newCapacity);
/* 403 */       bytesToCopy = newCapacity;
/*     */     } 
/* 405 */     memoryCopy(oldMemory, oldOffset, buf, bytesToCopy);
/* 406 */     if (freeOldMemory) {
/* 407 */       free(oldChunk, oldNioBuffer, oldHandle, oldMaxLength, buf.cache);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int numThreadCaches() {
/* 413 */     return this.numThreadCaches.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public int numTinySubpages() {
/* 418 */     return this.tinySubpagePools.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int numSmallSubpages() {
/* 423 */     return this.smallSubpagePools.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int numChunkLists() {
/* 428 */     return this.chunkListMetrics.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PoolSubpageMetric> tinySubpages() {
/* 433 */     return subPageMetricList((PoolSubpage<?>[])this.tinySubpagePools);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PoolSubpageMetric> smallSubpages() {
/* 438 */     return subPageMetricList((PoolSubpage<?>[])this.smallSubpagePools);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PoolChunkListMetric> chunkLists() {
/* 443 */     return this.chunkListMetrics;
/*     */   }
/*     */   
/*     */   private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
/* 447 */     List<PoolSubpageMetric> metrics = new ArrayList<PoolSubpageMetric>();
/* 448 */     for (PoolSubpage<?> head : pages) {
/* 449 */       if (head.next != head) {
/*     */ 
/*     */         
/* 452 */         PoolSubpage<?> s = head.next;
/*     */         do {
/* 454 */           metrics.add(s);
/* 455 */           s = s.next;
/* 456 */         } while (s != head);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 461 */     return metrics;
/*     */   }
/*     */ 
/*     */   
/*     */   public long numAllocations() {
/*     */     long allocsNormal;
/* 467 */     synchronized (this) {
/* 468 */       allocsNormal = this.allocationsNormal;
/*     */     } 
/* 470 */     return this.allocationsTiny.value() + this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public long numTinyAllocations() {
/* 475 */     return this.allocationsTiny.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public long numSmallAllocations() {
/* 480 */     return this.allocationsSmall.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long numNormalAllocations() {
/* 485 */     return this.allocationsNormal;
/*     */   }
/*     */ 
/*     */   
/*     */   public long numDeallocations() {
/*     */     long deallocs;
/* 491 */     synchronized (this) {
/* 492 */       deallocs = this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal;
/*     */     } 
/* 494 */     return deallocs + this.deallocationsHuge.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long numTinyDeallocations() {
/* 499 */     return this.deallocationsTiny;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long numSmallDeallocations() {
/* 504 */     return this.deallocationsSmall;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized long numNormalDeallocations() {
/* 509 */     return this.deallocationsNormal;
/*     */   }
/*     */ 
/*     */   
/*     */   public long numHugeAllocations() {
/* 514 */     return this.allocationsHuge.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public long numHugeDeallocations() {
/* 519 */     return this.deallocationsHuge.value();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long numActiveAllocations() {
/* 525 */     long val = this.allocationsTiny.value() + this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
/* 526 */     synchronized (this) {
/* 527 */       val += this.allocationsNormal - this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal;
/*     */     } 
/* 529 */     return Math.max(val, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long numActiveTinyAllocations() {
/* 534 */     return Math.max(numTinyAllocations() - numTinyDeallocations(), 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long numActiveSmallAllocations() {
/* 539 */     return Math.max(numSmallAllocations() - numSmallDeallocations(), 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long numActiveNormalAllocations() {
/*     */     long val;
/* 545 */     synchronized (this) {
/* 546 */       val = this.allocationsNormal - this.deallocationsNormal;
/*     */     } 
/* 548 */     return Math.max(val, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long numActiveHugeAllocations() {
/* 553 */     return Math.max(numHugeAllocations() - numHugeDeallocations(), 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public long numActiveBytes() {
/* 558 */     long val = this.activeBytesHuge.value();
/* 559 */     synchronized (this) {
/* 560 */       for (int i = 0; i < this.chunkListMetrics.size(); i++) {
/* 561 */         for (PoolChunkMetric m : this.chunkListMetrics.get(i)) {
/* 562 */           val += m.chunkSize();
/*     */         }
/*     */       } 
/*     */     } 
/* 566 */     return Math.max(0L, val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PoolChunk<T> newChunk(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PoolChunk<T> newUnpooledChunk(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PooledByteBuf<T> newByteBuf(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void memoryCopy(T paramT, int paramInt1, PooledByteBuf<T> paramPooledByteBuf, int paramInt2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void destroyChunk(PoolChunk<T> paramPoolChunk);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String toString() {
/* 602 */     StringBuilder buf = (new StringBuilder()).append("Chunk(s) at 0~25%:").append(StringUtil.NEWLINE).append(this.qInit).append(StringUtil.NEWLINE).append("Chunk(s) at 0~50%:").append(StringUtil.NEWLINE).append(this.q000).append(StringUtil.NEWLINE).append("Chunk(s) at 25~75%:").append(StringUtil.NEWLINE).append(this.q025).append(StringUtil.NEWLINE).append("Chunk(s) at 50~100%:").append(StringUtil.NEWLINE).append(this.q050).append(StringUtil.NEWLINE).append("Chunk(s) at 75~100%:").append(StringUtil.NEWLINE).append(this.q075).append(StringUtil.NEWLINE).append("Chunk(s) at 100%:").append(StringUtil.NEWLINE).append(this.q100).append(StringUtil.NEWLINE).append("tiny subpages:");
/* 603 */     appendPoolSubPages(buf, (PoolSubpage<?>[])this.tinySubpagePools);
/* 604 */     buf.append(StringUtil.NEWLINE)
/* 605 */       .append("small subpages:");
/* 606 */     appendPoolSubPages(buf, (PoolSubpage<?>[])this.smallSubpagePools);
/* 607 */     buf.append(StringUtil.NEWLINE);
/*     */     
/* 609 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static void appendPoolSubPages(StringBuilder buf, PoolSubpage<?>[] subpages) {
/* 613 */     for (int i = 0; i < subpages.length; i++) {
/* 614 */       PoolSubpage<?> head = subpages[i];
/* 615 */       if (head.next != head) {
/*     */ 
/*     */ 
/*     */         
/* 619 */         buf.append(StringUtil.NEWLINE)
/* 620 */           .append(i)
/* 621 */           .append(": ");
/* 622 */         PoolSubpage<?> s = head.next;
/*     */         do {
/* 624 */           buf.append(s);
/* 625 */           s = s.next;
/* 626 */         } while (s != head);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void finalize() throws Throwable {
/*     */     try {
/* 636 */       super.finalize();
/*     */     } finally {
/* 638 */       destroyPoolSubPages((PoolSubpage<?>[])this.smallSubpagePools);
/* 639 */       destroyPoolSubPages((PoolSubpage<?>[])this.tinySubpagePools);
/* 640 */       destroyPoolChunkLists((PoolChunkList<T>[])new PoolChunkList[] { this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100 });
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
/* 645 */     for (PoolSubpage<?> page : pages) {
/* 646 */       page.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */   private void destroyPoolChunkLists(PoolChunkList<T>... chunkLists) {
/* 651 */     for (PoolChunkList<T> chunkList : chunkLists) {
/* 652 */       chunkList.destroy(this);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class HeapArena
/*     */     extends PoolArena<byte[]>
/*     */   {
/*     */     HeapArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
/* 660 */       super(parent, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);
/*     */     }
/*     */ 
/*     */     
/*     */     private static byte[] newByteArray(int size) {
/* 665 */       return PlatformDependent.allocateUninitializedArray(size);
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isDirect() {
/* 670 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     protected PoolChunk<byte[]> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
/* 675 */       return (PoolChunk)new PoolChunk<byte>(this, newByteArray(chunkSize), pageSize, maxOrder, pageShifts, chunkSize, 0);
/*     */     }
/*     */ 
/*     */     
/*     */     protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
/* 680 */       return (PoolChunk)new PoolChunk<byte>(this, newByteArray(capacity), capacity, 0);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void destroyChunk(PoolChunk<byte[]> chunk) {}
/*     */ 
/*     */ 
/*     */     
/*     */     protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
/* 690 */       return HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity) : 
/* 691 */         PooledHeapByteBuf.newInstance(maxCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void memoryCopy(byte[] src, int srcOffset, PooledByteBuf<byte[]> dst, int length) {
/* 696 */       if (length == 0) {
/*     */         return;
/*     */       }
/*     */       
/* 700 */       System.arraycopy(src, srcOffset, dst.memory, dst.offset, length);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class DirectArena
/*     */     extends PoolArena<ByteBuffer>
/*     */   {
/*     */     DirectArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
/* 708 */       super(parent, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isDirect() {
/* 714 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int offsetCacheLine(ByteBuffer memory) {
/* 722 */       int remainder = HAS_UNSAFE ? (int)(PlatformDependent.directBufferAddress(memory) & this.directMemoryCacheAlignmentMask) : 0;
/*     */ 
/*     */ 
/*     */       
/* 726 */       return this.directMemoryCacheAlignment - remainder;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
/* 732 */       if (this.directMemoryCacheAlignment == 0) {
/* 733 */         return new PoolChunk<ByteBuffer>(this, 
/* 734 */             allocateDirect(chunkSize), pageSize, maxOrder, pageShifts, chunkSize, 0);
/*     */       }
/*     */       
/* 737 */       ByteBuffer memory = allocateDirect(chunkSize + this.directMemoryCacheAlignment);
/*     */       
/* 739 */       return new PoolChunk<ByteBuffer>(this, memory, pageSize, maxOrder, pageShifts, chunkSize, 
/*     */           
/* 741 */           offsetCacheLine(memory));
/*     */     }
/*     */ 
/*     */     
/*     */     protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
/* 746 */       if (this.directMemoryCacheAlignment == 0) {
/* 747 */         return new PoolChunk<ByteBuffer>(this, 
/* 748 */             allocateDirect(capacity), capacity, 0);
/*     */       }
/* 750 */       ByteBuffer memory = allocateDirect(capacity + this.directMemoryCacheAlignment);
/*     */       
/* 752 */       return new PoolChunk<ByteBuffer>(this, memory, capacity, 
/* 753 */           offsetCacheLine(memory));
/*     */     }
/*     */     
/*     */     private static ByteBuffer allocateDirect(int capacity) {
/* 757 */       return PlatformDependent.useDirectBufferNoCleaner() ? 
/* 758 */         PlatformDependent.allocateDirectNoCleaner(capacity) : ByteBuffer.allocateDirect(capacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
/* 763 */       if (PlatformDependent.useDirectBufferNoCleaner()) {
/* 764 */         PlatformDependent.freeDirectNoCleaner((ByteBuffer)chunk.memory);
/*     */       } else {
/* 766 */         PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.memory);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
/* 772 */       if (HAS_UNSAFE) {
/* 773 */         return PooledUnsafeDirectByteBuf.newInstance(maxCapacity);
/*     */       }
/* 775 */       return PooledDirectByteBuf.newInstance(maxCapacity);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void memoryCopy(ByteBuffer src, int srcOffset, PooledByteBuf<ByteBuffer> dstBuf, int length) {
/* 781 */       if (length == 0) {
/*     */         return;
/*     */       }
/*     */       
/* 785 */       if (HAS_UNSAFE) {
/* 786 */         PlatformDependent.copyMemory(
/* 787 */             PlatformDependent.directBufferAddress(src) + srcOffset, 
/* 788 */             PlatformDependent.directBufferAddress((ByteBuffer)dstBuf.memory) + dstBuf.offset, length);
/*     */       } else {
/*     */         
/* 791 */         src = src.duplicate();
/* 792 */         ByteBuffer dst = dstBuf.internalNioBuffer();
/* 793 */         src.position(srcOffset).limit(srcOffset + length);
/* 794 */         dst.position(dstBuf.offset);
/* 795 */         dst.put(src);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolArena.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */