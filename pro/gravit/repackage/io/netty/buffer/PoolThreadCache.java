/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ 
/*     */ 
/*     */ final class PoolThreadCache
/*     */ {
/*  44 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
/*     */   
/*     */   final PoolArena<byte[]> heapArena;
/*     */   
/*     */   final PoolArena<ByteBuffer> directArena;
/*     */   
/*     */   private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
/*     */   
/*     */   private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
/*     */   
/*     */   private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
/*     */   private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
/*     */   private final MemoryRegionCache<byte[]>[] normalHeapCaches;
/*     */   private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
/*     */   private final int numShiftsNormalDirect;
/*     */   private final int numShiftsNormalHeap;
/*     */   private final int freeSweepAllocationThreshold;
/*  61 */   private final AtomicBoolean freed = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */   
/*     */   private int allocations;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
/*  71 */     ObjectUtil.checkPositiveOrZero(maxCachedBufferCapacity, "maxCachedBufferCapacity");
/*  72 */     this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
/*  73 */     this.heapArena = heapArena;
/*  74 */     this.directArena = directArena;
/*  75 */     if (directArena != null) {
/*  76 */       this.tinySubPageDirectCaches = createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
/*     */       
/*  78 */       this.smallSubPageDirectCaches = createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
/*     */ 
/*     */       
/*  81 */       this.numShiftsNormalDirect = log2(directArena.pageSize);
/*  82 */       this.normalDirectCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
/*     */ 
/*     */       
/*  85 */       directArena.numThreadCaches.getAndIncrement();
/*     */     } else {
/*     */       
/*  88 */       this.tinySubPageDirectCaches = null;
/*  89 */       this.smallSubPageDirectCaches = null;
/*  90 */       this.normalDirectCaches = null;
/*  91 */       this.numShiftsNormalDirect = -1;
/*     */     } 
/*  93 */     if (heapArena != null) {
/*     */       
/*  95 */       this.tinySubPageHeapCaches = createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
/*     */       
/*  97 */       this.smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
/*     */ 
/*     */       
/* 100 */       this.numShiftsNormalHeap = log2(heapArena.pageSize);
/* 101 */       this.normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, (PoolArena)heapArena);
/*     */ 
/*     */       
/* 104 */       heapArena.numThreadCaches.getAndIncrement();
/*     */     } else {
/*     */       
/* 107 */       this.tinySubPageHeapCaches = null;
/* 108 */       this.smallSubPageHeapCaches = null;
/* 109 */       this.normalHeapCaches = null;
/* 110 */       this.numShiftsNormalHeap = -1;
/*     */     } 
/*     */ 
/*     */     
/* 114 */     if ((this.tinySubPageDirectCaches != null || this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.tinySubPageHeapCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1)
/*     */     {
/*     */       
/* 117 */       throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches, PoolArena.SizeClass sizeClass) {
/* 124 */     if (cacheSize > 0 && numCaches > 0) {
/*     */       
/* 126 */       MemoryRegionCache[] arrayOfMemoryRegionCache = new MemoryRegionCache[numCaches];
/* 127 */       for (int i = 0; i < arrayOfMemoryRegionCache.length; i++)
/*     */       {
/* 129 */         arrayOfMemoryRegionCache[i] = new SubPageMemoryRegionCache(cacheSize, sizeClass);
/*     */       }
/* 131 */       return (MemoryRegionCache<T>[])arrayOfMemoryRegionCache;
/*     */     } 
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
/* 139 */     if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
/* 140 */       int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
/* 141 */       int arraySize = Math.max(1, log2(max / area.pageSize) + 1);
/*     */ 
/*     */       
/* 144 */       MemoryRegionCache[] arrayOfMemoryRegionCache = new MemoryRegionCache[arraySize];
/* 145 */       for (int i = 0; i < arrayOfMemoryRegionCache.length; i++) {
/* 146 */         arrayOfMemoryRegionCache[i] = new NormalMemoryRegionCache(cacheSize);
/*     */       }
/* 148 */       return (MemoryRegionCache<T>[])arrayOfMemoryRegionCache;
/*     */     } 
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int log2(int val) {
/* 155 */     int res = 0;
/* 156 */     while (val > 1) {
/* 157 */       val >>= 1;
/* 158 */       res++;
/*     */     } 
/* 160 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
/* 167 */     return allocate(cacheForTiny(area, normCapacity), buf, reqCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
/* 174 */     return allocate(cacheForSmall(area, normCapacity), buf, reqCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
/* 181 */     return allocate(cacheForNormal(area, normCapacity), buf, reqCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf<?> buf, int reqCapacity) {
/* 186 */     if (cache == null)
/*     */     {
/* 188 */       return false;
/*     */     }
/* 190 */     boolean allocated = cache.allocate(buf, reqCapacity);
/* 191 */     if (++this.allocations >= this.freeSweepAllocationThreshold) {
/* 192 */       this.allocations = 0;
/* 193 */       trim();
/*     */     } 
/* 195 */     return allocated;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean add(PoolArena<?> area, PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
/* 205 */     MemoryRegionCache<?> cache = cache(area, normCapacity, sizeClass);
/* 206 */     if (cache == null) {
/* 207 */       return false;
/*     */     }
/* 209 */     return cache.add(chunk, nioBuffer, handle);
/*     */   }
/*     */   
/*     */   private MemoryRegionCache<?> cache(PoolArena<?> area, int normCapacity, PoolArena.SizeClass sizeClass) {
/* 213 */     switch (sizeClass) {
/*     */       case Normal:
/* 215 */         return cacheForNormal(area, normCapacity);
/*     */       case Small:
/* 217 */         return cacheForSmall(area, normCapacity);
/*     */       case Tiny:
/* 219 */         return cacheForTiny(area, normCapacity);
/*     */     } 
/* 221 */     throw new Error();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 229 */       super.finalize();
/*     */     } finally {
/* 231 */       free(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void free(boolean finalizer) {
/* 241 */     if (this.freed.compareAndSet(false, true)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 247 */       int numFreed = free((MemoryRegionCache<?>[])this.tinySubPageDirectCaches, finalizer) + free((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, finalizer) + free((MemoryRegionCache<?>[])this.normalDirectCaches, finalizer) + free((MemoryRegionCache<?>[])this.tinySubPageHeapCaches, finalizer) + free((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, finalizer) + free((MemoryRegionCache<?>[])this.normalHeapCaches, finalizer);
/*     */       
/* 249 */       if (numFreed > 0 && logger.isDebugEnabled()) {
/* 250 */         logger.debug("Freed {} thread-local buffer(s) from thread: {}", Integer.valueOf(numFreed), 
/* 251 */             Thread.currentThread().getName());
/*     */       }
/*     */       
/* 254 */       if (this.directArena != null) {
/* 255 */         this.directArena.numThreadCaches.getAndDecrement();
/*     */       }
/*     */       
/* 258 */       if (this.heapArena != null) {
/* 259 */         this.heapArena.numThreadCaches.getAndDecrement();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
/* 265 */     if (caches == null) {
/* 266 */       return 0;
/*     */     }
/*     */     
/* 269 */     int numFreed = 0;
/* 270 */     for (MemoryRegionCache<?> c : caches) {
/* 271 */       numFreed += free(c, finalizer);
/*     */     }
/* 273 */     return numFreed;
/*     */   }
/*     */   
/*     */   private static int free(MemoryRegionCache<?> cache, boolean finalizer) {
/* 277 */     if (cache == null) {
/* 278 */       return 0;
/*     */     }
/* 280 */     return cache.free(finalizer);
/*     */   }
/*     */   
/*     */   void trim() {
/* 284 */     trim((MemoryRegionCache<?>[])this.tinySubPageDirectCaches);
/* 285 */     trim((MemoryRegionCache<?>[])this.smallSubPageDirectCaches);
/* 286 */     trim((MemoryRegionCache<?>[])this.normalDirectCaches);
/* 287 */     trim((MemoryRegionCache<?>[])this.tinySubPageHeapCaches);
/* 288 */     trim((MemoryRegionCache<?>[])this.smallSubPageHeapCaches);
/* 289 */     trim((MemoryRegionCache<?>[])this.normalHeapCaches);
/*     */   }
/*     */   
/*     */   private static void trim(MemoryRegionCache<?>[] caches) {
/* 293 */     if (caches == null) {
/*     */       return;
/*     */     }
/* 296 */     for (MemoryRegionCache<?> c : caches) {
/* 297 */       trim(c);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void trim(MemoryRegionCache<?> cache) {
/* 302 */     if (cache == null) {
/*     */       return;
/*     */     }
/* 305 */     cache.trim();
/*     */   }
/*     */   
/*     */   private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
/* 309 */     int idx = PoolArena.tinyIdx(normCapacity);
/* 310 */     if (area.isDirect()) {
/* 311 */       return cache((MemoryRegionCache<?>[])this.tinySubPageDirectCaches, idx);
/*     */     }
/* 313 */     return cache((MemoryRegionCache<?>[])this.tinySubPageHeapCaches, idx);
/*     */   }
/*     */   
/*     */   private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int normCapacity) {
/* 317 */     int idx = PoolArena.smallIdx(normCapacity);
/* 318 */     if (area.isDirect()) {
/* 319 */       return cache((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, idx);
/*     */     }
/* 321 */     return cache((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, idx);
/*     */   }
/*     */   
/*     */   private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int normCapacity) {
/* 325 */     if (area.isDirect()) {
/* 326 */       int i = log2(normCapacity >> this.numShiftsNormalDirect);
/* 327 */       return cache((MemoryRegionCache<?>[])this.normalDirectCaches, i);
/*     */     } 
/* 329 */     int idx = log2(normCapacity >> this.numShiftsNormalHeap);
/* 330 */     return cache((MemoryRegionCache<?>[])this.normalHeapCaches, idx);
/*     */   }
/*     */   
/*     */   private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int idx) {
/* 334 */     if (cache == null || idx > cache.length - 1) {
/* 335 */       return null;
/*     */     }
/* 337 */     return cache[idx];
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class SubPageMemoryRegionCache<T>
/*     */     extends MemoryRegionCache<T>
/*     */   {
/*     */     SubPageMemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
/* 345 */       super(size, sizeClass);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity) {
/* 351 */       chunk.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class NormalMemoryRegionCache<T>
/*     */     extends MemoryRegionCache<T>
/*     */   {
/*     */     NormalMemoryRegionCache(int size) {
/* 360 */       super(size, PoolArena.SizeClass.Normal);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity) {
/* 366 */       chunk.initBuf(buf, nioBuffer, handle, reqCapacity);
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class MemoryRegionCache<T> {
/*     */     private final int size;
/*     */     private final Queue<Entry<T>> queue;
/*     */     private final PoolArena.SizeClass sizeClass;
/*     */     private int allocations;
/*     */     
/*     */     MemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
/* 377 */       this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
/* 378 */       this.queue = PlatformDependent.newFixedMpscQueue(this.size);
/* 379 */       this.sizeClass = sizeClass;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected abstract void initBuf(PoolChunk<T> param1PoolChunk, ByteBuffer param1ByteBuffer, long param1Long, PooledByteBuf<T> param1PooledByteBuf, int param1Int);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean add(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle) {
/* 393 */       Entry<T> entry = newEntry(chunk, nioBuffer, handle);
/* 394 */       boolean queued = this.queue.offer(entry);
/* 395 */       if (!queued)
/*     */       {
/* 397 */         entry.recycle();
/*     */       }
/*     */       
/* 400 */       return queued;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity) {
/* 407 */       Entry<T> entry = this.queue.poll();
/* 408 */       if (entry == null) {
/* 409 */         return false;
/*     */       }
/* 411 */       initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity);
/* 412 */       entry.recycle();
/*     */ 
/*     */       
/* 415 */       this.allocations++;
/* 416 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final int free(boolean finalizer) {
/* 423 */       return free(2147483647, finalizer);
/*     */     }
/*     */     
/*     */     private int free(int max, boolean finalizer) {
/* 427 */       int numFreed = 0;
/* 428 */       for (; numFreed < max; numFreed++) {
/* 429 */         Entry<T> entry = this.queue.poll();
/* 430 */         if (entry != null) {
/* 431 */           freeEntry(entry, finalizer);
/*     */         } else {
/*     */           
/* 434 */           return numFreed;
/*     */         } 
/*     */       } 
/* 437 */       return numFreed;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final void trim() {
/* 444 */       int free = this.size - this.allocations;
/* 445 */       this.allocations = 0;
/*     */ 
/*     */       
/* 448 */       if (free > 0) {
/* 449 */         free(free, false);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void freeEntry(Entry entry, boolean finalizer) {
/* 455 */       PoolChunk chunk = entry.chunk;
/* 456 */       long handle = entry.handle;
/* 457 */       ByteBuffer nioBuffer = entry.nioBuffer;
/*     */       
/* 459 */       if (!finalizer)
/*     */       {
/*     */         
/* 462 */         entry.recycle();
/*     */       }
/*     */       
/* 465 */       chunk.arena.freeChunk(chunk, handle, this.sizeClass, nioBuffer, finalizer);
/*     */     }
/*     */     
/*     */     static final class Entry<T> {
/*     */       final ObjectPool.Handle<Entry<?>> recyclerHandle;
/*     */       PoolChunk<T> chunk;
/*     */       ByteBuffer nioBuffer;
/* 472 */       long handle = -1L;
/*     */       
/*     */       Entry(ObjectPool.Handle<Entry<?>> recyclerHandle) {
/* 475 */         this.recyclerHandle = recyclerHandle;
/*     */       }
/*     */       
/*     */       void recycle() {
/* 479 */         this.chunk = null;
/* 480 */         this.nioBuffer = null;
/* 481 */         this.handle = -1L;
/* 482 */         this.recyclerHandle.recycle(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private static Entry newEntry(PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle) {
/* 488 */       Entry entry = (Entry)RECYCLER.get();
/* 489 */       entry.chunk = chunk;
/* 490 */       entry.nioBuffer = nioBuffer;
/* 491 */       entry.handle = handle;
/* 492 */       return entry;
/*     */     }
/*     */ 
/*     */     
/* 496 */     private static final ObjectPool<Entry> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<Entry>()
/*     */         {
/*     */           public PoolThreadCache.MemoryRegionCache.Entry newObject(ObjectPool.Handle<PoolThreadCache.MemoryRegionCache.Entry> handle)
/*     */           {
/* 500 */             return new PoolThreadCache.MemoryRegionCache.Entry((ObjectPool.Handle)handle);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolThreadCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */