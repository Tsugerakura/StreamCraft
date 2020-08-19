/*     */ package io.sentry.jvmti;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public final class FrameCache
/*     */ {
/*  10 */   private static Set<String> appPackages = new HashSet<>();
/*     */   
/*  12 */   private static ThreadLocal<WeakHashMap<Throwable, Frame[]>> cache = new ThreadLocal<WeakHashMap<Throwable, Frame[]>>()
/*     */     {
/*     */       protected WeakHashMap<Throwable, Frame[]> initialValue()
/*     */       {
/*  16 */         return (WeakHashMap)new WeakHashMap<>();
/*     */       }
/*     */     };
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
/*     */   public static void add(Throwable throwable, Frame[] frames) {
/*  34 */     Map<Throwable, Frame[]> weakMap = (Map<Throwable, Frame[]>)cache.get();
/*  35 */     weakMap.put(throwable, frames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Frame[] get(Throwable throwable) {
/*  45 */     Map<Throwable, Frame[]> weakMap = (Map<Throwable, Frame[]>)cache.get();
/*  46 */     return weakMap.get(throwable);
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
/*     */   public static boolean shouldCacheThrowable(Throwable throwable, int numFrames) {
/*  60 */     if (appPackages.isEmpty()) {
/*  61 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     Map<Throwable, Frame[]> weakMap = (Map<Throwable, Frame[]>)cache.get();
/*  69 */     Frame[] existing = weakMap.get(throwable);
/*  70 */     if (existing != null && numFrames <= existing.length) {
/*  71 */       return false;
/*     */     }
/*     */ 
/*     */     
/*  75 */     for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
/*  76 */       for (String appFrame : appPackages) {
/*  77 */         if (stackTraceElement.getClassName().startsWith(appFrame)) {
/*  78 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     return false;
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
/*     */   public static void addAppPackage(String newAppPackage) {
/*  97 */     appPackages.add(newAppPackage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void reset() {
/* 105 */     ((WeakHashMap)cache.get()).clear();
/* 106 */     appPackages.clear();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\jvmti\FrameCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */