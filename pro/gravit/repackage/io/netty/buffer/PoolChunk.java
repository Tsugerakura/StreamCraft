/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
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
/*     */ final class PoolChunk<T>
/*     */   implements PoolChunkMetric
/*     */ {
/*     */   private static final int INTEGER_SIZE_MINUS_ONE = 31;
/*     */   final PoolArena<T> arena;
/*     */   final T memory;
/*     */   final boolean unpooled;
/*     */   final int offset;
/*     */   private final byte[] memoryMap;
/*     */   private final byte[] depthMap;
/*     */   private final PoolSubpage<T>[] subpages;
/*     */   private final int subpageOverflowMask;
/*     */   private final int pageSize;
/*     */   private final int pageShifts;
/*     */   private final int maxOrder;
/*     */   private final int chunkSize;
/*     */   private final int log2ChunkSize;
/*     */   private final int maxSubpageAllocs;
/*     */   private final byte unusable;
/*     */   private final Deque<ByteBuffer> cachedNioBuffers;
/*     */   private int freeBytes;
/*     */   PoolChunkList<T> parent;
/*     */   PoolChunk<T> prev;
/*     */   PoolChunk<T> next;
/*     */   
/*     */   PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize, int offset) {
/* 145 */     this.unpooled = false;
/* 146 */     this.arena = arena;
/* 147 */     this.memory = memory;
/* 148 */     this.pageSize = pageSize;
/* 149 */     this.pageShifts = pageShifts;
/* 150 */     this.maxOrder = maxOrder;
/* 151 */     this.chunkSize = chunkSize;
/* 152 */     this.offset = offset;
/* 153 */     this.unusable = (byte)(maxOrder + 1);
/* 154 */     this.log2ChunkSize = log2(chunkSize);
/* 155 */     this.subpageOverflowMask = pageSize - 1 ^ 0xFFFFFFFF;
/* 156 */     this.freeBytes = chunkSize;
/*     */     
/* 158 */     assert maxOrder < 30 : "maxOrder should be < 30, but is: " + maxOrder;
/* 159 */     this.maxSubpageAllocs = 1 << maxOrder;
/*     */ 
/*     */     
/* 162 */     this.memoryMap = new byte[this.maxSubpageAllocs << 1];
/* 163 */     this.depthMap = new byte[this.memoryMap.length];
/* 164 */     int memoryMapIndex = 1;
/* 165 */     for (int d = 0; d <= maxOrder; d++) {
/* 166 */       int depth = 1 << d;
/* 167 */       for (int p = 0; p < depth; p++) {
/*     */         
/* 169 */         this.memoryMap[memoryMapIndex] = (byte)d;
/* 170 */         this.depthMap[memoryMapIndex] = (byte)d;
/* 171 */         memoryMapIndex++;
/*     */       } 
/*     */     } 
/*     */     
/* 175 */     this.subpages = newSubpageArray(this.maxSubpageAllocs);
/* 176 */     this.cachedNioBuffers = new ArrayDeque<ByteBuffer>(8);
/*     */   }
/*     */ 
/*     */   
/*     */   PoolChunk(PoolArena<T> arena, T memory, int size, int offset) {
/* 181 */     this.unpooled = true;
/* 182 */     this.arena = arena;
/* 183 */     this.memory = memory;
/* 184 */     this.offset = offset;
/* 185 */     this.memoryMap = null;
/* 186 */     this.depthMap = null;
/* 187 */     this.subpages = null;
/* 188 */     this.subpageOverflowMask = 0;
/* 189 */     this.pageSize = 0;
/* 190 */     this.pageShifts = 0;
/* 191 */     this.maxOrder = 0;
/* 192 */     this.unusable = (byte)(this.maxOrder + 1);
/* 193 */     this.chunkSize = size;
/* 194 */     this.log2ChunkSize = log2(this.chunkSize);
/* 195 */     this.maxSubpageAllocs = 0;
/* 196 */     this.cachedNioBuffers = null;
/*     */   }
/*     */ 
/*     */   
/*     */   private PoolSubpage<T>[] newSubpageArray(int size) {
/* 201 */     return (PoolSubpage<T>[])new PoolSubpage[size];
/*     */   }
/*     */ 
/*     */   
/*     */   public int usage() {
/*     */     int freeBytes;
/* 207 */     synchronized (this.arena) {
/* 208 */       freeBytes = this.freeBytes;
/*     */     } 
/* 210 */     return usage(freeBytes);
/*     */   }
/*     */   
/*     */   private int usage(int freeBytes) {
/* 214 */     if (freeBytes == 0) {
/* 215 */       return 100;
/*     */     }
/*     */     
/* 218 */     int freePercentage = (int)(freeBytes * 100L / this.chunkSize);
/* 219 */     if (freePercentage == 0) {
/* 220 */       return 99;
/*     */     }
/* 222 */     return 100 - freePercentage;
/*     */   }
/*     */   
/*     */   boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
/*     */     long handle;
/* 227 */     if ((normCapacity & this.subpageOverflowMask) != 0) {
/* 228 */       handle = allocateRun(normCapacity);
/*     */     } else {
/* 230 */       handle = allocateSubpage(normCapacity);
/*     */     } 
/*     */     
/* 233 */     if (handle < 0L) {
/* 234 */       return false;
/*     */     }
/* 236 */     ByteBuffer nioBuffer = (this.cachedNioBuffers != null) ? this.cachedNioBuffers.pollLast() : null;
/* 237 */     initBuf(buf, nioBuffer, handle, reqCapacity);
/* 238 */     return true;
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
/*     */   private void updateParentsAlloc(int id) {
/* 250 */     while (id > 1) {
/* 251 */       int parentId = id >>> 1;
/* 252 */       byte val1 = value(id);
/* 253 */       byte val2 = value(id ^ 0x1);
/* 254 */       byte val = (val1 < val2) ? val1 : val2;
/* 255 */       setValue(parentId, val);
/* 256 */       id = parentId;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateParentsFree(int id) {
/* 268 */     int logChild = depth(id) + 1;
/* 269 */     while (id > 1) {
/* 270 */       int parentId = id >>> 1;
/* 271 */       byte val1 = value(id);
/* 272 */       byte val2 = value(id ^ 0x1);
/* 273 */       logChild--;
/*     */       
/* 275 */       if (val1 == logChild && val2 == logChild) {
/* 276 */         setValue(parentId, (byte)(logChild - 1));
/*     */       } else {
/* 278 */         byte val = (val1 < val2) ? val1 : val2;
/* 279 */         setValue(parentId, val);
/*     */       } 
/*     */       
/* 282 */       id = parentId;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int allocateNode(int d) {
/* 294 */     int id = 1;
/* 295 */     int initial = -(1 << d);
/* 296 */     byte val = value(id);
/* 297 */     if (val > d) {
/* 298 */       return -1;
/*     */     }
/* 300 */     while (val < d || (id & initial) == 0) {
/* 301 */       id <<= 1;
/* 302 */       val = value(id);
/* 303 */       if (val > d) {
/* 304 */         id ^= 0x1;
/* 305 */         val = value(id);
/*     */       } 
/*     */     } 
/* 308 */     byte value = value(id);
/* 309 */     assert value == d && (id & initial) == 1 << d : String.format("val = %d, id & initial = %d, d = %d", new Object[] {
/* 310 */           Byte.valueOf(value), Integer.valueOf(id & initial), Integer.valueOf(d) });
/* 311 */     setValue(id, this.unusable);
/* 312 */     updateParentsAlloc(id);
/* 313 */     return id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long allocateRun(int normCapacity) {
/* 323 */     int d = this.maxOrder - log2(normCapacity) - this.pageShifts;
/* 324 */     int id = allocateNode(d);
/* 325 */     if (id < 0) {
/* 326 */       return id;
/*     */     }
/* 328 */     this.freeBytes -= runLength(id);
/* 329 */     return id;
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
/*     */   private long allocateSubpage(int normCapacity) {
/* 342 */     PoolSubpage<T> head = this.arena.findSubpagePoolHead(normCapacity);
/* 343 */     int d = this.maxOrder;
/* 344 */     synchronized (head) {
/* 345 */       int id = allocateNode(d);
/* 346 */       if (id < 0) {
/* 347 */         return id;
/*     */       }
/*     */       
/* 350 */       PoolSubpage<T>[] subpages = this.subpages;
/* 351 */       int pageSize = this.pageSize;
/*     */       
/* 353 */       this.freeBytes -= pageSize;
/*     */       
/* 355 */       int subpageIdx = subpageIdx(id);
/* 356 */       PoolSubpage<T> subpage = subpages[subpageIdx];
/* 357 */       if (subpage == null) {
/* 358 */         subpage = new PoolSubpage<T>(head, this, id, runOffset(id), pageSize, normCapacity);
/* 359 */         subpages[subpageIdx] = subpage;
/*     */       } else {
/* 361 */         subpage.init(head, normCapacity);
/*     */       } 
/* 363 */       return subpage.allocate();
/*     */     } 
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
/*     */   void free(long handle, ByteBuffer nioBuffer) {
/* 376 */     int memoryMapIdx = memoryMapIdx(handle);
/* 377 */     int bitmapIdx = bitmapIdx(handle);
/*     */     
/* 379 */     if (bitmapIdx != 0) {
/* 380 */       PoolSubpage<T> subpage = this.subpages[subpageIdx(memoryMapIdx)];
/* 381 */       assert subpage != null && subpage.doNotDestroy;
/*     */ 
/*     */ 
/*     */       
/* 385 */       PoolSubpage<T> head = this.arena.findSubpagePoolHead(subpage.elemSize);
/* 386 */       synchronized (head) {
/* 387 */         if (subpage.free(head, bitmapIdx & 0x3FFFFFFF)) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/* 392 */     this.freeBytes += runLength(memoryMapIdx);
/* 393 */     setValue(memoryMapIdx, depth(memoryMapIdx));
/* 394 */     updateParentsFree(memoryMapIdx);
/*     */     
/* 396 */     if (nioBuffer != null && this.cachedNioBuffers != null && this.cachedNioBuffers
/* 397 */       .size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
/* 398 */       this.cachedNioBuffers.offer(nioBuffer);
/*     */     }
/*     */   }
/*     */   
/*     */   void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
/* 403 */     int memoryMapIdx = memoryMapIdx(handle);
/* 404 */     int bitmapIdx = bitmapIdx(handle);
/* 405 */     if (bitmapIdx == 0) {
/* 406 */       byte val = value(memoryMapIdx);
/* 407 */       assert val == this.unusable : String.valueOf(val);
/* 408 */       buf.init(this, nioBuffer, handle, runOffset(memoryMapIdx) + this.offset, reqCapacity, 
/* 409 */           runLength(memoryMapIdx), this.arena.parent.threadCache());
/*     */     } else {
/* 411 */       initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx, reqCapacity);
/*     */     } 
/*     */   }
/*     */   
/*     */   void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
/* 416 */     initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx(handle), reqCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   private void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int bitmapIdx, int reqCapacity) {
/* 421 */     assert bitmapIdx != 0;
/*     */     
/* 423 */     int memoryMapIdx = memoryMapIdx(handle);
/*     */     
/* 425 */     PoolSubpage<T> subpage = this.subpages[subpageIdx(memoryMapIdx)];
/* 426 */     assert subpage.doNotDestroy;
/* 427 */     assert reqCapacity <= subpage.elemSize;
/*     */     
/* 429 */     buf.init(this, nioBuffer, handle, 
/*     */         
/* 431 */         runOffset(memoryMapIdx) + (bitmapIdx & 0x3FFFFFFF) * subpage.elemSize + this.offset, reqCapacity, subpage.elemSize, this.arena.parent
/* 432 */         .threadCache());
/*     */   }
/*     */   
/*     */   private byte value(int id) {
/* 436 */     return this.memoryMap[id];
/*     */   }
/*     */   
/*     */   private void setValue(int id, byte val) {
/* 440 */     this.memoryMap[id] = val;
/*     */   }
/*     */   
/*     */   private byte depth(int id) {
/* 444 */     return this.depthMap[id];
/*     */   }
/*     */ 
/*     */   
/*     */   private static int log2(int val) {
/* 449 */     return 31 - Integer.numberOfLeadingZeros(val);
/*     */   }
/*     */ 
/*     */   
/*     */   private int runLength(int id) {
/* 454 */     return 1 << this.log2ChunkSize - depth(id);
/*     */   }
/*     */ 
/*     */   
/*     */   private int runOffset(int id) {
/* 459 */     int shift = id ^ 1 << depth(id);
/* 460 */     return shift * runLength(id);
/*     */   }
/*     */   
/*     */   private int subpageIdx(int memoryMapIdx) {
/* 464 */     return memoryMapIdx ^ this.maxSubpageAllocs;
/*     */   }
/*     */   
/*     */   private static int memoryMapIdx(long handle) {
/* 468 */     return (int)handle;
/*     */   }
/*     */   
/*     */   private static int bitmapIdx(long handle) {
/* 472 */     return (int)(handle >>> 32L);
/*     */   }
/*     */ 
/*     */   
/*     */   public int chunkSize() {
/* 477 */     return this.chunkSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public int freeBytes() {
/* 482 */     synchronized (this.arena) {
/* 483 */       return this.freeBytes;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     int freeBytes;
/* 490 */     synchronized (this.arena) {
/* 491 */       freeBytes = this.freeBytes;
/*     */     } 
/*     */     
/* 494 */     return "Chunk(" + 
/*     */       
/* 496 */       Integer.toHexString(System.identityHashCode(this)) + ": " + 
/*     */       
/* 498 */       usage(freeBytes) + "%, " + (
/* 499 */       this.chunkSize - freeBytes) + 
/* 500 */       '/' + 
/* 501 */       this.chunkSize + 
/* 502 */       ')';
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void destroy() {
/* 508 */     this.arena.destroyChunk(this);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */