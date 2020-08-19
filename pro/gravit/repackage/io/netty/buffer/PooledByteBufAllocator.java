/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.util.NettyRuntime;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ThreadExecutorMap;
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
/*     */ public class PooledByteBufAllocator
/*     */   extends AbstractByteBufAllocator
/*     */   implements ByteBufAllocatorMetricProvider
/*     */ {
/*  40 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
/*     */   
/*     */   private static final int DEFAULT_NUM_HEAP_ARENA;
/*     */   
/*     */   private static final int DEFAULT_NUM_DIRECT_ARENA;
/*     */   private static final int DEFAULT_PAGE_SIZE;
/*     */   private static final int DEFAULT_MAX_ORDER;
/*     */   private static final int DEFAULT_TINY_CACHE_SIZE;
/*     */   private static final int DEFAULT_SMALL_CACHE_SIZE;
/*     */   private static final int DEFAULT_NORMAL_CACHE_SIZE;
/*     */   private static final int DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
/*     */   private static final int DEFAULT_CACHE_TRIM_INTERVAL;
/*     */   private static final long DEFAULT_CACHE_TRIM_INTERVAL_MILLIS;
/*     */   private static final boolean DEFAULT_USE_CACHE_FOR_ALL_THREADS;
/*     */   private static final int DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT;
/*     */   static final int DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK;
/*     */   private static final int MIN_PAGE_SIZE = 4096;
/*     */   private static final int MAX_CHUNK_SIZE = 1073741824;
/*     */   
/*  59 */   private final Runnable trimTask = new Runnable()
/*     */     {
/*     */       public void run() {
/*  62 */         PooledByteBufAllocator.this.trimCurrentThreadCache();
/*     */       }
/*     */     };
/*     */   public static final PooledByteBufAllocator DEFAULT;
/*     */   static {
/*  67 */     int defaultPageSize = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.pageSize", 8192);
/*  68 */     Throwable pageSizeFallbackCause = null;
/*     */     try {
/*  70 */       validateAndCalculatePageShifts(defaultPageSize);
/*  71 */     } catch (Throwable t) {
/*  72 */       pageSizeFallbackCause = t;
/*  73 */       defaultPageSize = 8192;
/*     */     } 
/*  75 */     DEFAULT_PAGE_SIZE = defaultPageSize;
/*     */     
/*  77 */     int defaultMaxOrder = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.maxOrder", 11);
/*  78 */     Throwable maxOrderFallbackCause = null;
/*     */     try {
/*  80 */       validateAndCalculateChunkSize(DEFAULT_PAGE_SIZE, defaultMaxOrder);
/*  81 */     } catch (Throwable t) {
/*  82 */       maxOrderFallbackCause = t;
/*  83 */       defaultMaxOrder = 11;
/*     */     } 
/*  85 */     DEFAULT_MAX_ORDER = defaultMaxOrder;
/*     */ 
/*     */ 
/*     */     
/*  89 */     Runtime runtime = Runtime.getRuntime();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  98 */     int defaultMinNumArena = NettyRuntime.availableProcessors() * 2;
/*  99 */     int defaultChunkSize = DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER;
/* 100 */     DEFAULT_NUM_HEAP_ARENA = Math.max(0, 
/* 101 */         SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.numHeapArenas", 
/*     */           
/* 103 */           (int)Math.min(defaultMinNumArena, runtime
/*     */             
/* 105 */             .maxMemory() / defaultChunkSize / 2L / 3L)));
/* 106 */     DEFAULT_NUM_DIRECT_ARENA = Math.max(0, 
/* 107 */         SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.numDirectArenas", 
/*     */           
/* 109 */           (int)Math.min(defaultMinNumArena, 
/*     */             
/* 111 */             PlatformDependent.maxDirectMemory() / defaultChunkSize / 2L / 3L)));
/*     */ 
/*     */     
/* 114 */     DEFAULT_TINY_CACHE_SIZE = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.tinyCacheSize", 512);
/* 115 */     DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.smallCacheSize", 256);
/* 116 */     DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.normalCacheSize", 64);
/*     */ 
/*     */ 
/*     */     
/* 120 */     DEFAULT_MAX_CACHED_BUFFER_CAPACITY = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.maxCachedBufferCapacity", 32768);
/*     */ 
/*     */ 
/*     */     
/* 124 */     DEFAULT_CACHE_TRIM_INTERVAL = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.cacheTrimInterval", 8192);
/*     */ 
/*     */     
/* 127 */     DEFAULT_CACHE_TRIM_INTERVAL_MILLIS = SystemPropertyUtil.getLong("pro.gravit.repackage.io.netty.allocation.cacheTrimIntervalMillis", 0L);
/*     */ 
/*     */     
/* 130 */     DEFAULT_USE_CACHE_FOR_ALL_THREADS = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.allocator.useCacheForAllThreads", true);
/*     */ 
/*     */     
/* 133 */     DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.directMemoryCacheAlignment", 0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 138 */     DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.allocator.maxCachedByteBuffersPerChunk", 1023);
/*     */ 
/*     */     
/* 141 */     if (logger.isDebugEnabled()) {
/* 142 */       logger.debug("-Dio.netty.allocator.numHeapArenas: {}", Integer.valueOf(DEFAULT_NUM_HEAP_ARENA));
/* 143 */       logger.debug("-Dio.netty.allocator.numDirectArenas: {}", Integer.valueOf(DEFAULT_NUM_DIRECT_ARENA));
/* 144 */       if (pageSizeFallbackCause == null) {
/* 145 */         logger.debug("-Dio.netty.allocator.pageSize: {}", Integer.valueOf(DEFAULT_PAGE_SIZE));
/*     */       } else {
/* 147 */         logger.debug("-Dio.netty.allocator.pageSize: {}", Integer.valueOf(DEFAULT_PAGE_SIZE), pageSizeFallbackCause);
/*     */       } 
/* 149 */       if (maxOrderFallbackCause == null) {
/* 150 */         logger.debug("-Dio.netty.allocator.maxOrder: {}", Integer.valueOf(DEFAULT_MAX_ORDER));
/*     */       } else {
/* 152 */         logger.debug("-Dio.netty.allocator.maxOrder: {}", Integer.valueOf(DEFAULT_MAX_ORDER), maxOrderFallbackCause);
/*     */       } 
/* 154 */       logger.debug("-Dio.netty.allocator.chunkSize: {}", Integer.valueOf(DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER));
/* 155 */       logger.debug("-Dio.netty.allocator.tinyCacheSize: {}", Integer.valueOf(DEFAULT_TINY_CACHE_SIZE));
/* 156 */       logger.debug("-Dio.netty.allocator.smallCacheSize: {}", Integer.valueOf(DEFAULT_SMALL_CACHE_SIZE));
/* 157 */       logger.debug("-Dio.netty.allocator.normalCacheSize: {}", Integer.valueOf(DEFAULT_NORMAL_CACHE_SIZE));
/* 158 */       logger.debug("-Dio.netty.allocator.maxCachedBufferCapacity: {}", Integer.valueOf(DEFAULT_MAX_CACHED_BUFFER_CAPACITY));
/* 159 */       logger.debug("-Dio.netty.allocator.cacheTrimInterval: {}", Integer.valueOf(DEFAULT_CACHE_TRIM_INTERVAL));
/* 160 */       logger.debug("-Dio.netty.allocator.cacheTrimIntervalMillis: {}", Long.valueOf(DEFAULT_CACHE_TRIM_INTERVAL_MILLIS));
/* 161 */       logger.debug("-Dio.netty.allocator.useCacheForAllThreads: {}", Boolean.valueOf(DEFAULT_USE_CACHE_FOR_ALL_THREADS));
/* 162 */       logger.debug("-Dio.netty.allocator.maxCachedByteBuffersPerChunk: {}", 
/* 163 */           Integer.valueOf(DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 168 */     DEFAULT = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
/*     */   }
/*     */   private final PoolArena<byte[]>[] heapArenas;
/*     */   private final PoolArena<ByteBuffer>[] directArenas;
/*     */   private final int tinyCacheSize;
/*     */   private final int smallCacheSize;
/*     */   private final int normalCacheSize;
/*     */   private final List<PoolArenaMetric> heapArenaMetrics;
/*     */   private final List<PoolArenaMetric> directArenaMetrics;
/*     */   private final PoolThreadLocalCache threadCache;
/*     */   private final int chunkSize;
/*     */   private final PooledByteBufAllocatorMetric metric;
/*     */   
/*     */   public PooledByteBufAllocator() {
/* 182 */     this(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public PooledByteBufAllocator(boolean preferDirect) {
/* 187 */     this(preferDirect, DEFAULT_NUM_HEAP_ARENA, DEFAULT_NUM_DIRECT_ARENA, DEFAULT_PAGE_SIZE, DEFAULT_MAX_ORDER);
/*     */   }
/*     */ 
/*     */   
/*     */   public PooledByteBufAllocator(int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
/* 192 */     this(false, nHeapArena, nDirectArena, pageSize, maxOrder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
/* 201 */     this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, DEFAULT_TINY_CACHE_SIZE, DEFAULT_SMALL_CACHE_SIZE, DEFAULT_NORMAL_CACHE_SIZE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize) {
/* 212 */     this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, tinyCacheSize, smallCacheSize, normalCacheSize, DEFAULT_USE_CACHE_FOR_ALL_THREADS, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads) {
/* 220 */     this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, tinyCacheSize, smallCacheSize, normalCacheSize, useCacheForAllThreads, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads, int directMemoryCacheAlignment) {
/* 228 */     super(preferDirect);
/* 229 */     this.threadCache = new PoolThreadLocalCache(useCacheForAllThreads);
/* 230 */     this.tinyCacheSize = tinyCacheSize;
/* 231 */     this.smallCacheSize = smallCacheSize;
/* 232 */     this.normalCacheSize = normalCacheSize;
/* 233 */     this.chunkSize = validateAndCalculateChunkSize(pageSize, maxOrder);
/*     */     
/* 235 */     ObjectUtil.checkPositiveOrZero(nHeapArena, "nHeapArena");
/* 236 */     ObjectUtil.checkPositiveOrZero(nDirectArena, "nDirectArena");
/*     */     
/* 238 */     ObjectUtil.checkPositiveOrZero(directMemoryCacheAlignment, "directMemoryCacheAlignment");
/* 239 */     if (directMemoryCacheAlignment > 0 && !isDirectMemoryCacheAlignmentSupported()) {
/* 240 */       throw new IllegalArgumentException("directMemoryCacheAlignment is not supported");
/*     */     }
/*     */     
/* 243 */     if ((directMemoryCacheAlignment & -directMemoryCacheAlignment) != directMemoryCacheAlignment) {
/* 244 */       throw new IllegalArgumentException("directMemoryCacheAlignment: " + directMemoryCacheAlignment + " (expected: power of two)");
/*     */     }
/*     */ 
/*     */     
/* 248 */     int pageShifts = validateAndCalculatePageShifts(pageSize);
/*     */     
/* 250 */     if (nHeapArena > 0) {
/* 251 */       this.heapArenas = newArenaArray(nHeapArena);
/* 252 */       List<PoolArenaMetric> metrics = new ArrayList<PoolArenaMetric>(this.heapArenas.length);
/* 253 */       for (int i = 0; i < this.heapArenas.length; i++) {
/* 254 */         PoolArena.HeapArena arena = new PoolArena.HeapArena(this, pageSize, maxOrder, pageShifts, this.chunkSize, directMemoryCacheAlignment);
/*     */ 
/*     */         
/* 257 */         this.heapArenas[i] = arena;
/* 258 */         metrics.add(arena);
/*     */       } 
/* 260 */       this.heapArenaMetrics = Collections.unmodifiableList(metrics);
/*     */     } else {
/* 262 */       this.heapArenas = null;
/* 263 */       this.heapArenaMetrics = Collections.emptyList();
/*     */     } 
/*     */     
/* 266 */     if (nDirectArena > 0) {
/* 267 */       this.directArenas = newArenaArray(nDirectArena);
/* 268 */       List<PoolArenaMetric> metrics = new ArrayList<PoolArenaMetric>(this.directArenas.length);
/* 269 */       for (int i = 0; i < this.directArenas.length; i++) {
/* 270 */         PoolArena.DirectArena arena = new PoolArena.DirectArena(this, pageSize, maxOrder, pageShifts, this.chunkSize, directMemoryCacheAlignment);
/*     */         
/* 272 */         this.directArenas[i] = arena;
/* 273 */         metrics.add(arena);
/*     */       } 
/* 275 */       this.directArenaMetrics = Collections.unmodifiableList(metrics);
/*     */     } else {
/* 277 */       this.directArenas = null;
/* 278 */       this.directArenaMetrics = Collections.emptyList();
/*     */     } 
/* 280 */     this.metric = new PooledByteBufAllocatorMetric(this);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> PoolArena<T>[] newArenaArray(int size) {
/* 285 */     return (PoolArena<T>[])new PoolArena[size];
/*     */   }
/*     */   
/*     */   private static int validateAndCalculatePageShifts(int pageSize) {
/* 289 */     if (pageSize < 4096) {
/* 290 */       throw new IllegalArgumentException("pageSize: " + pageSize + " (expected: " + 'က' + ")");
/*     */     }
/*     */     
/* 293 */     if ((pageSize & pageSize - 1) != 0) {
/* 294 */       throw new IllegalArgumentException("pageSize: " + pageSize + " (expected: power of 2)");
/*     */     }
/*     */ 
/*     */     
/* 298 */     return 31 - Integer.numberOfLeadingZeros(pageSize);
/*     */   }
/*     */   
/*     */   private static int validateAndCalculateChunkSize(int pageSize, int maxOrder) {
/* 302 */     if (maxOrder > 14) {
/* 303 */       throw new IllegalArgumentException("maxOrder: " + maxOrder + " (expected: 0-14)");
/*     */     }
/*     */ 
/*     */     
/* 307 */     int chunkSize = pageSize;
/* 308 */     for (int i = maxOrder; i > 0; i--) {
/* 309 */       if (chunkSize > 536870912)
/* 310 */         throw new IllegalArgumentException(String.format("pageSize (%d) << maxOrder (%d) must not exceed %d", new Object[] {
/* 311 */                 Integer.valueOf(pageSize), Integer.valueOf(maxOrder), Integer.valueOf(1073741824)
/*     */               })); 
/* 313 */       chunkSize <<= 1;
/*     */     } 
/* 315 */     return chunkSize;
/*     */   }
/*     */   
/*     */   protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
/*     */     ByteBuf buf;
/* 320 */     PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
/* 321 */     PoolArena<byte[]> heapArena = cache.heapArena;
/*     */ 
/*     */     
/* 324 */     if (heapArena != null) {
/* 325 */       buf = heapArena.allocate(cache, initialCapacity, maxCapacity);
/*     */     } else {
/* 327 */       buf = PlatformDependent.hasUnsafe() ? new UnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity) : new UnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 332 */     return toLeakAwareBuffer(buf);
/*     */   }
/*     */   
/*     */   protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
/*     */     ByteBuf buf;
/* 337 */     PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
/* 338 */     PoolArena<ByteBuffer> directArena = cache.directArena;
/*     */ 
/*     */     
/* 341 */     if (directArena != null) {
/* 342 */       buf = directArena.allocate(cache, initialCapacity, maxCapacity);
/*     */     } else {
/*     */       
/* 345 */       buf = PlatformDependent.hasUnsafe() ? UnsafeByteBufUtil.newUnsafeDirectByteBuf(this, initialCapacity, maxCapacity) : new UnpooledDirectByteBuf(this, initialCapacity, maxCapacity);
/*     */     } 
/*     */ 
/*     */     
/* 349 */     return toLeakAwareBuffer(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultNumHeapArena() {
/* 356 */     return DEFAULT_NUM_HEAP_ARENA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultNumDirectArena() {
/* 363 */     return DEFAULT_NUM_DIRECT_ARENA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultPageSize() {
/* 370 */     return DEFAULT_PAGE_SIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultMaxOrder() {
/* 377 */     return DEFAULT_MAX_ORDER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean defaultUseCacheForAllThreads() {
/* 384 */     return DEFAULT_USE_CACHE_FOR_ALL_THREADS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean defaultPreferDirect() {
/* 391 */     return PlatformDependent.directBufferPreferred();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultTinyCacheSize() {
/* 398 */     return DEFAULT_TINY_CACHE_SIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultSmallCacheSize() {
/* 405 */     return DEFAULT_SMALL_CACHE_SIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int defaultNormalCacheSize() {
/* 412 */     return DEFAULT_NORMAL_CACHE_SIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isDirectMemoryCacheAlignmentSupported() {
/* 419 */     return PlatformDependent.hasUnsafe();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectBufferPooled() {
/* 424 */     return (this.directArenas != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasThreadLocalCache() {
/* 433 */     return this.threadCache.isSet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void freeThreadLocalCache() {
/* 441 */     this.threadCache.remove();
/*     */   }
/*     */   
/*     */   final class PoolThreadLocalCache extends FastThreadLocal<PoolThreadCache> {
/*     */     private final boolean useCacheForAllThreads;
/*     */     
/*     */     PoolThreadLocalCache(boolean useCacheForAllThreads) {
/* 448 */       this.useCacheForAllThreads = useCacheForAllThreads;
/*     */     }
/*     */ 
/*     */     
/*     */     protected synchronized PoolThreadCache initialValue() {
/* 453 */       PoolArena<byte[]> heapArena = (PoolArena)leastUsedArena((PoolArena[])PooledByteBufAllocator.this.heapArenas);
/* 454 */       PoolArena<ByteBuffer> directArena = leastUsedArena(PooledByteBufAllocator.this.directArenas);
/*     */       
/* 456 */       Thread current = Thread.currentThread();
/* 457 */       if (this.useCacheForAllThreads || current instanceof pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocalThread) {
/*     */ 
/*     */         
/* 460 */         PoolThreadCache cache = new PoolThreadCache(heapArena, directArena, PooledByteBufAllocator.this.tinyCacheSize, PooledByteBufAllocator.this.smallCacheSize, PooledByteBufAllocator.this.normalCacheSize, PooledByteBufAllocator.DEFAULT_MAX_CACHED_BUFFER_CAPACITY, PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL);
/*     */         
/* 462 */         if (PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL_MILLIS > 0L) {
/* 463 */           EventExecutor executor = ThreadExecutorMap.currentExecutor();
/* 464 */           if (executor != null) {
/* 465 */             executor.scheduleAtFixedRate(PooledByteBufAllocator.this.trimTask, PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL_MILLIS, PooledByteBufAllocator
/* 466 */                 .DEFAULT_CACHE_TRIM_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
/*     */           }
/*     */         } 
/* 469 */         return cache;
/*     */       } 
/*     */       
/* 472 */       return new PoolThreadCache(heapArena, directArena, 0, 0, 0, 0, 0);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void onRemoval(PoolThreadCache threadCache) {
/* 477 */       threadCache.free(false);
/*     */     }
/*     */     
/*     */     private <T> PoolArena<T> leastUsedArena(PoolArena<T>[] arenas) {
/* 481 */       if (arenas == null || arenas.length == 0) {
/* 482 */         return null;
/*     */       }
/*     */       
/* 485 */       PoolArena<T> minArena = arenas[0];
/* 486 */       for (int i = 1; i < arenas.length; i++) {
/* 487 */         PoolArena<T> arena = arenas[i];
/* 488 */         if (arena.numThreadCaches.get() < minArena.numThreadCaches.get()) {
/* 489 */           minArena = arena;
/*     */         }
/*     */       } 
/*     */       
/* 493 */       return minArena;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public PooledByteBufAllocatorMetric metric() {
/* 499 */     return this.metric;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int numHeapArenas() {
/* 509 */     return this.heapArenaMetrics.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int numDirectArenas() {
/* 519 */     return this.directArenaMetrics.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List<PoolArenaMetric> heapArenas() {
/* 529 */     return this.heapArenaMetrics;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List<PoolArenaMetric> directArenas() {
/* 539 */     return this.directArenaMetrics;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int numThreadLocalCaches() {
/* 549 */     PoolArena<?>[] arenas = (this.heapArenas != null) ? (PoolArena<?>[])this.heapArenas : (PoolArena<?>[])this.directArenas;
/* 550 */     if (arenas == null) {
/* 551 */       return 0;
/*     */     }
/*     */     
/* 554 */     int total = 0;
/* 555 */     for (PoolArena<?> arena : arenas) {
/* 556 */       total += arena.numThreadCaches.get();
/*     */     }
/*     */     
/* 559 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int tinyCacheSize() {
/* 569 */     return this.tinyCacheSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int smallCacheSize() {
/* 579 */     return this.smallCacheSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int normalCacheSize() {
/* 589 */     return this.normalCacheSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final int chunkSize() {
/* 599 */     return this.chunkSize;
/*     */   }
/*     */   
/*     */   final long usedHeapMemory() {
/* 603 */     return usedMemory((PoolArena<?>[])this.heapArenas);
/*     */   }
/*     */   
/*     */   final long usedDirectMemory() {
/* 607 */     return usedMemory((PoolArena<?>[])this.directArenas);
/*     */   }
/*     */   
/*     */   private static long usedMemory(PoolArena<?>[] arenas) {
/* 611 */     if (arenas == null) {
/* 612 */       return -1L;
/*     */     }
/* 614 */     long used = 0L;
/* 615 */     for (PoolArena<?> arena : arenas) {
/* 616 */       used += arena.numActiveBytes();
/* 617 */       if (used < 0L) {
/* 618 */         return Long.MAX_VALUE;
/*     */       }
/*     */     } 
/* 621 */     return used;
/*     */   }
/*     */   
/*     */   final PoolThreadCache threadCache() {
/* 625 */     PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
/* 626 */     assert cache != null;
/* 627 */     return cache;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean trimCurrentThreadCache() {
/* 637 */     PoolThreadCache cache = (PoolThreadCache)this.threadCache.getIfExists();
/* 638 */     if (cache != null) {
/* 639 */       cache.trim();
/* 640 */       return true;
/*     */     } 
/* 642 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dumpStats() {
/* 650 */     int heapArenasLen = (this.heapArenas == null) ? 0 : this.heapArenas.length;
/*     */ 
/*     */ 
/*     */     
/* 654 */     StringBuilder buf = (new StringBuilder(512)).append(heapArenasLen).append(" heap arena(s):").append(StringUtil.NEWLINE);
/* 655 */     if (heapArenasLen > 0) {
/* 656 */       for (PoolArena<byte[]> a : this.heapArenas) {
/* 657 */         buf.append(a);
/*     */       }
/*     */     }
/*     */     
/* 661 */     int directArenasLen = (this.directArenas == null) ? 0 : this.directArenas.length;
/*     */     
/* 663 */     buf.append(directArenasLen)
/* 664 */       .append(" direct arena(s):")
/* 665 */       .append(StringUtil.NEWLINE);
/* 666 */     if (directArenasLen > 0) {
/* 667 */       for (PoolArena<ByteBuffer> a : this.directArenas) {
/* 668 */         buf.append(a);
/*     */       }
/*     */     }
/*     */     
/* 672 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */