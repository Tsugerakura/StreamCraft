/*     */ package pro.gravit.repackage.io.netty.buffer;
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
/*     */ final class PoolSubpage<T>
/*     */   implements PoolSubpageMetric
/*     */ {
/*     */   final PoolChunk<T> chunk;
/*     */   private final int memoryMapIdx;
/*     */   private final int runOffset;
/*     */   private final int pageSize;
/*     */   private final long[] bitmap;
/*     */   PoolSubpage<T> prev;
/*     */   PoolSubpage<T> next;
/*     */   boolean doNotDestroy;
/*     */   int elemSize;
/*     */   private int maxNumElems;
/*     */   private int bitmapLength;
/*     */   private int nextAvail;
/*     */   private int numAvail;
/*     */   
/*     */   PoolSubpage(int pageSize) {
/*  42 */     this.chunk = null;
/*  43 */     this.memoryMapIdx = -1;
/*  44 */     this.runOffset = -1;
/*  45 */     this.elemSize = -1;
/*  46 */     this.pageSize = pageSize;
/*  47 */     this.bitmap = null;
/*     */   }
/*     */   
/*     */   PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int memoryMapIdx, int runOffset, int pageSize, int elemSize) {
/*  51 */     this.chunk = chunk;
/*  52 */     this.memoryMapIdx = memoryMapIdx;
/*  53 */     this.runOffset = runOffset;
/*  54 */     this.pageSize = pageSize;
/*  55 */     this.bitmap = new long[pageSize >>> 10];
/*  56 */     init(head, elemSize);
/*     */   }
/*     */   
/*     */   void init(PoolSubpage<T> head, int elemSize) {
/*  60 */     this.doNotDestroy = true;
/*  61 */     this.elemSize = elemSize;
/*  62 */     if (elemSize != 0) {
/*  63 */       this.maxNumElems = this.numAvail = this.pageSize / elemSize;
/*  64 */       this.nextAvail = 0;
/*  65 */       this.bitmapLength = this.maxNumElems >>> 6;
/*  66 */       if ((this.maxNumElems & 0x3F) != 0) {
/*  67 */         this.bitmapLength++;
/*     */       }
/*     */       
/*  70 */       for (int i = 0; i < this.bitmapLength; i++) {
/*  71 */         this.bitmap[i] = 0L;
/*     */       }
/*     */     } 
/*  74 */     addToPool(head);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long allocate() {
/*  81 */     if (this.elemSize == 0) {
/*  82 */       return toHandle(0);
/*     */     }
/*     */     
/*  85 */     if (this.numAvail == 0 || !this.doNotDestroy) {
/*  86 */       return -1L;
/*     */     }
/*     */     
/*  89 */     int bitmapIdx = getNextAvail();
/*  90 */     int q = bitmapIdx >>> 6;
/*  91 */     int r = bitmapIdx & 0x3F;
/*  92 */     assert (this.bitmap[q] >>> r & 0x1L) == 0L;
/*  93 */     this.bitmap[q] = this.bitmap[q] | 1L << r;
/*     */     
/*  95 */     if (--this.numAvail == 0) {
/*  96 */       removeFromPool();
/*     */     }
/*     */     
/*  99 */     return toHandle(bitmapIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean free(PoolSubpage<T> head, int bitmapIdx) {
/* 107 */     if (this.elemSize == 0) {
/* 108 */       return true;
/*     */     }
/* 110 */     int q = bitmapIdx >>> 6;
/* 111 */     int r = bitmapIdx & 0x3F;
/* 112 */     assert (this.bitmap[q] >>> r & 0x1L) != 0L;
/* 113 */     this.bitmap[q] = this.bitmap[q] ^ 1L << r;
/*     */     
/* 115 */     setNextAvail(bitmapIdx);
/*     */     
/* 117 */     if (this.numAvail++ == 0) {
/* 118 */       addToPool(head);
/* 119 */       return true;
/*     */     } 
/*     */     
/* 122 */     if (this.numAvail != this.maxNumElems) {
/* 123 */       return true;
/*     */     }
/*     */     
/* 126 */     if (this.prev == this.next)
/*     */     {
/* 128 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 132 */     this.doNotDestroy = false;
/* 133 */     removeFromPool();
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addToPool(PoolSubpage<T> head) {
/* 139 */     assert this.prev == null && this.next == null;
/* 140 */     this.prev = head;
/* 141 */     this.next = head.next;
/* 142 */     this.next.prev = this;
/* 143 */     head.next = this;
/*     */   }
/*     */   
/*     */   private void removeFromPool() {
/* 147 */     assert this.prev != null && this.next != null;
/* 148 */     this.prev.next = this.next;
/* 149 */     this.next.prev = this.prev;
/* 150 */     this.next = null;
/* 151 */     this.prev = null;
/*     */   }
/*     */   
/*     */   private void setNextAvail(int bitmapIdx) {
/* 155 */     this.nextAvail = bitmapIdx;
/*     */   }
/*     */   
/*     */   private int getNextAvail() {
/* 159 */     int nextAvail = this.nextAvail;
/* 160 */     if (nextAvail >= 0) {
/* 161 */       this.nextAvail = -1;
/* 162 */       return nextAvail;
/*     */     } 
/* 164 */     return findNextAvail();
/*     */   }
/*     */   
/*     */   private int findNextAvail() {
/* 168 */     long[] bitmap = this.bitmap;
/* 169 */     int bitmapLength = this.bitmapLength;
/* 170 */     for (int i = 0; i < bitmapLength; i++) {
/* 171 */       long bits = bitmap[i];
/* 172 */       if ((bits ^ 0xFFFFFFFFFFFFFFFFL) != 0L) {
/* 173 */         return findNextAvail0(i, bits);
/*     */       }
/*     */     } 
/* 176 */     return -1;
/*     */   }
/*     */   
/*     */   private int findNextAvail0(int i, long bits) {
/* 180 */     int maxNumElems = this.maxNumElems;
/* 181 */     int baseVal = i << 6;
/*     */     
/* 183 */     for (int j = 0; j < 64; j++) {
/* 184 */       if ((bits & 0x1L) == 0L) {
/* 185 */         int val = baseVal | j;
/* 186 */         if (val < maxNumElems) {
/* 187 */           return val;
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/* 192 */       bits >>>= 1L;
/*     */     } 
/* 194 */     return -1;
/*     */   }
/*     */   
/*     */   private long toHandle(int bitmapIdx) {
/* 198 */     return 0x4000000000000000L | bitmapIdx << 32L | this.memoryMapIdx;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     boolean doNotDestroy;
/*     */     int maxNumElems;
/*     */     int numAvail;
/*     */     int elemSize;
/* 207 */     if (this.chunk == null) {
/*     */       
/* 209 */       doNotDestroy = true;
/* 210 */       maxNumElems = 0;
/* 211 */       numAvail = 0;
/* 212 */       elemSize = -1;
/*     */     } else {
/* 214 */       synchronized (this.chunk.arena) {
/* 215 */         if (!this.doNotDestroy) {
/* 216 */           doNotDestroy = false;
/*     */           
/* 218 */           maxNumElems = numAvail = elemSize = -1;
/*     */         } else {
/* 220 */           doNotDestroy = true;
/* 221 */           maxNumElems = this.maxNumElems;
/* 222 */           numAvail = this.numAvail;
/* 223 */           elemSize = this.elemSize;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 228 */     if (!doNotDestroy) {
/* 229 */       return "(" + this.memoryMapIdx + ": not in use)";
/*     */     }
/*     */     
/* 232 */     return "(" + this.memoryMapIdx + ": " + (maxNumElems - numAvail) + '/' + maxNumElems + ", offset: " + this.runOffset + ", length: " + this.pageSize + ", elemSize: " + elemSize + ')';
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxNumElements() {
/* 238 */     if (this.chunk == null)
/*     */     {
/* 240 */       return 0;
/*     */     }
/*     */     
/* 243 */     synchronized (this.chunk.arena) {
/* 244 */       return this.maxNumElems;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int numAvailable() {
/* 250 */     if (this.chunk == null)
/*     */     {
/* 252 */       return 0;
/*     */     }
/*     */     
/* 255 */     synchronized (this.chunk.arena) {
/* 256 */       return this.numAvail;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int elementSize() {
/* 262 */     if (this.chunk == null)
/*     */     {
/* 264 */       return -1;
/*     */     }
/*     */     
/* 267 */     synchronized (this.chunk.arena) {
/* 268 */       return this.elemSize;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int pageSize() {
/* 274 */     return this.pageSize;
/*     */   }
/*     */   
/*     */   void destroy() {
/* 278 */     if (this.chunk != null)
/* 279 */       this.chunk.destroy(); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolSubpage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */