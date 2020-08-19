/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ final class PoolChunkList<T>
/*     */   implements PoolChunkListMetric
/*     */ {
/*  31 */   private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.<PoolChunkMetric>emptyList().iterator();
/*     */   
/*     */   private final PoolArena<T> arena;
/*     */   
/*     */   private final PoolChunkList<T> nextList;
/*     */   
/*     */   private final int minUsage;
/*     */   
/*     */   private final int maxUsage;
/*     */   
/*     */   private final int maxCapacity;
/*     */   private PoolChunk<T> head;
/*     */   private PoolChunkList<T> prevList;
/*     */   
/*     */   PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
/*  46 */     assert minUsage <= maxUsage;
/*  47 */     this.arena = arena;
/*  48 */     this.nextList = nextList;
/*  49 */     this.minUsage = minUsage;
/*  50 */     this.maxUsage = maxUsage;
/*  51 */     this.maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int calculateMaxCapacity(int minUsage, int chunkSize) {
/*  59 */     minUsage = minUsage0(minUsage);
/*     */     
/*  61 */     if (minUsage == 100)
/*     */     {
/*  63 */       return 0;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     return (int)(chunkSize * (100L - minUsage) / 100L);
/*     */   }
/*     */   
/*     */   void prevList(PoolChunkList<T> prevList) {
/*  75 */     assert this.prevList == null;
/*  76 */     this.prevList = prevList;
/*     */   }
/*     */   
/*     */   boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
/*  80 */     if (normCapacity > this.maxCapacity)
/*     */     {
/*     */       
/*  83 */       return false;
/*     */     }
/*     */     
/*  86 */     for (PoolChunk<T> cur = this.head; cur != null; cur = cur.next) {
/*  87 */       if (cur.allocate(buf, reqCapacity, normCapacity)) {
/*  88 */         if (cur.usage() >= this.maxUsage) {
/*  89 */           remove(cur);
/*  90 */           this.nextList.add(cur);
/*     */         } 
/*  92 */         return true;
/*     */       } 
/*     */     } 
/*  95 */     return false;
/*     */   }
/*     */   
/*     */   boolean free(PoolChunk<T> chunk, long handle, ByteBuffer nioBuffer) {
/*  99 */     chunk.free(handle, nioBuffer);
/* 100 */     if (chunk.usage() < this.minUsage) {
/* 101 */       remove(chunk);
/*     */       
/* 103 */       return move0(chunk);
/*     */     } 
/* 105 */     return true;
/*     */   }
/*     */   
/*     */   private boolean move(PoolChunk<T> chunk) {
/* 109 */     assert chunk.usage() < this.maxUsage;
/*     */     
/* 111 */     if (chunk.usage() < this.minUsage)
/*     */     {
/* 113 */       return move0(chunk);
/*     */     }
/*     */ 
/*     */     
/* 117 */     add0(chunk);
/* 118 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean move0(PoolChunk<T> chunk) {
/* 126 */     if (this.prevList == null) {
/*     */ 
/*     */       
/* 129 */       assert chunk.usage() == 0;
/* 130 */       return false;
/*     */     } 
/* 132 */     return this.prevList.move(chunk);
/*     */   }
/*     */   
/*     */   void add(PoolChunk<T> chunk) {
/* 136 */     if (chunk.usage() >= this.maxUsage) {
/* 137 */       this.nextList.add(chunk);
/*     */       return;
/*     */     } 
/* 140 */     add0(chunk);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void add0(PoolChunk<T> chunk) {
/* 147 */     chunk.parent = this;
/* 148 */     if (this.head == null) {
/* 149 */       this.head = chunk;
/* 150 */       chunk.prev = null;
/* 151 */       chunk.next = null;
/*     */     } else {
/* 153 */       chunk.prev = null;
/* 154 */       chunk.next = this.head;
/* 155 */       this.head.prev = chunk;
/* 156 */       this.head = chunk;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void remove(PoolChunk<T> cur) {
/* 161 */     if (cur == this.head) {
/* 162 */       this.head = cur.next;
/* 163 */       if (this.head != null) {
/* 164 */         this.head.prev = null;
/*     */       }
/*     */     } else {
/* 167 */       PoolChunk<T> next = cur.next;
/* 168 */       cur.prev.next = next;
/* 169 */       if (next != null) {
/* 170 */         next.prev = cur.prev;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int minUsage() {
/* 177 */     return minUsage0(this.minUsage);
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxUsage() {
/* 182 */     return Math.min(this.maxUsage, 100);
/*     */   }
/*     */   
/*     */   private static int minUsage0(int value) {
/* 186 */     return Math.max(1, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<PoolChunkMetric> iterator() {
/* 191 */     synchronized (this.arena) {
/* 192 */       if (this.head == null) {
/* 193 */         return EMPTY_METRICS;
/*     */       }
/* 195 */       List<PoolChunkMetric> metrics = new ArrayList<PoolChunkMetric>();
/* 196 */       PoolChunk<T> cur = this.head; do {
/* 197 */         metrics.add(cur);
/* 198 */         cur = cur.next;
/* 199 */       } while (cur != null);
/*     */ 
/*     */ 
/*     */       
/* 203 */       return metrics.iterator();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 209 */     StringBuilder buf = new StringBuilder();
/* 210 */     synchronized (this.arena) {
/* 211 */       if (this.head == null) {
/* 212 */         return "none";
/*     */       }
/*     */       
/* 215 */       PoolChunk<T> cur = this.head; while (true) {
/* 216 */         buf.append(cur);
/* 217 */         cur = cur.next;
/* 218 */         if (cur == null) {
/*     */           break;
/*     */         }
/* 221 */         buf.append(StringUtil.NEWLINE);
/*     */       } 
/*     */     } 
/* 224 */     return buf.toString();
/*     */   }
/*     */   
/*     */   void destroy(PoolArena<T> arena) {
/* 228 */     PoolChunk<T> chunk = this.head;
/* 229 */     while (chunk != null) {
/* 230 */       arena.destroyChunk(chunk);
/* 231 */       chunk = chunk.next;
/*     */     } 
/* 233 */     this.head = null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolChunkList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */