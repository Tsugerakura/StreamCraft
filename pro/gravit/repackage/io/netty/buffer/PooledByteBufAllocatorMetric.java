/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ public final class PooledByteBufAllocatorMetric
/*     */   implements ByteBufAllocatorMetric
/*     */ {
/*     */   private final PooledByteBufAllocator allocator;
/*     */   
/*     */   PooledByteBufAllocatorMetric(PooledByteBufAllocator allocator) {
/*  31 */     this.allocator = allocator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int numHeapArenas() {
/*  38 */     return this.allocator.numHeapArenas();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int numDirectArenas() {
/*  45 */     return this.allocator.numDirectArenas();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PoolArenaMetric> heapArenas() {
/*  52 */     return this.allocator.heapArenas();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PoolArenaMetric> directArenas() {
/*  59 */     return this.allocator.directArenas();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int numThreadLocalCaches() {
/*  66 */     return this.allocator.numThreadLocalCaches();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int tinyCacheSize() {
/*  73 */     return this.allocator.tinyCacheSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int smallCacheSize() {
/*  80 */     return this.allocator.smallCacheSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int normalCacheSize() {
/*  87 */     return this.allocator.normalCacheSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int chunkSize() {
/*  94 */     return this.allocator.chunkSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public long usedHeapMemory() {
/*  99 */     return this.allocator.usedHeapMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public long usedDirectMemory() {
/* 104 */     return this.allocator.usedDirectMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     StringBuilder sb = new StringBuilder(256);
/* 110 */     sb.append(StringUtil.simpleClassName(this))
/* 111 */       .append("(usedHeapMemory: ").append(usedHeapMemory())
/* 112 */       .append("; usedDirectMemory: ").append(usedDirectMemory())
/* 113 */       .append("; numHeapArenas: ").append(numHeapArenas())
/* 114 */       .append("; numDirectArenas: ").append(numDirectArenas())
/* 115 */       .append("; tinyCacheSize: ").append(tinyCacheSize())
/* 116 */       .append("; smallCacheSize: ").append(smallCacheSize())
/* 117 */       .append("; normalCacheSize: ").append(normalCacheSize())
/* 118 */       .append("; numThreadLocalCaches: ").append(numThreadLocalCaches())
/* 119 */       .append("; chunkSize: ").append(chunkSize()).append(')');
/* 120 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PooledByteBufAllocatorMetric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */