/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ class ThreadLocalBufferManager
/*     */ {
/*  26 */   private final Object RELEASE_LOCK = new Object();
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
/*  38 */   private final Map<SoftReference<BufferRecycler>, Boolean> _trackedRecyclers = new ConcurrentHashMap<SoftReference<BufferRecycler>, Boolean>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   private final ReferenceQueue<BufferRecycler> _refQueue = new ReferenceQueue<BufferRecycler>();
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
/*     */   public static ThreadLocalBufferManager instance() {
/*  57 */     return ThreadLocalBufferManagerHolder.manager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int releaseBuffers() {
/*  67 */     synchronized (this.RELEASE_LOCK) {
/*  68 */       int count = 0;
/*     */       
/*  70 */       removeSoftRefsClearedByGc();
/*  71 */       for (SoftReference<BufferRecycler> ref : this._trackedRecyclers.keySet()) {
/*  72 */         ref.clear();
/*  73 */         count++;
/*     */       } 
/*  75 */       this._trackedRecyclers.clear();
/*  76 */       return count;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SoftReference<BufferRecycler> wrapAndTrack(BufferRecycler br) {
/*  82 */     SoftReference<BufferRecycler> newRef = new SoftReference<BufferRecycler>(br, this._refQueue);
/*     */     
/*  84 */     this._trackedRecyclers.put(newRef, Boolean.valueOf(true));
/*     */     
/*  86 */     removeSoftRefsClearedByGc();
/*  87 */     return newRef;
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
/*     */   private void removeSoftRefsClearedByGc() {
/*     */     SoftReference<?> clearedSoftRef;
/* 103 */     while ((clearedSoftRef = (SoftReference)this._refQueue.poll()) != null)
/*     */     {
/* 105 */       this._trackedRecyclers.remove(clearedSoftRef);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ThreadLocalBufferManagerHolder
/*     */   {
/* 114 */     static final ThreadLocalBufferManager manager = new ThreadLocalBufferManager();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\ThreadLocalBufferManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */