/*    */ package com.fasterxml.jackson.core.util;
/*    */ 
/*    */ import java.lang.ref.SoftReference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BufferRecyclers
/*    */ {
/*    */   public static final String SYSTEM_PROPERTY_TRACK_REUSABLE_BUFFERS = "com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers";
/* 36 */   private static final ThreadLocalBufferManager _bufferRecyclerTracker = "true".equals(System.getProperty("com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers")) ? 
/* 37 */     ThreadLocalBufferManager.instance() : null;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 52 */   protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static BufferRecycler getBufferRecycler() {
/* 60 */     SoftReference<BufferRecycler> ref = _recyclerRef.get();
/* 61 */     BufferRecycler br = (ref == null) ? null : ref.get();
/*    */     
/* 63 */     if (br == null) {
/* 64 */       br = new BufferRecycler();
/* 65 */       if (_bufferRecyclerTracker != null) {
/* 66 */         ref = _bufferRecyclerTracker.wrapAndTrack(br);
/*    */       } else {
/* 68 */         ref = new SoftReference<BufferRecycler>(br);
/*    */       } 
/* 70 */       _recyclerRef.set(ref);
/*    */     } 
/* 72 */     return br;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int releaseBuffers() {
/* 89 */     if (_bufferRecyclerTracker != null) {
/* 90 */       return _bufferRecyclerTracker.releaseBuffers();
/*    */     }
/* 92 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\BufferRecyclers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */