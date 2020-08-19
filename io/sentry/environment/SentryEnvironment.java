/*    */ package io.sentry.environment;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
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
/*    */ public final class SentryEnvironment
/*    */ {
/*    */   public static final String SDK_NAME = "sentry-java";
/*    */   public static final String SDK_VERSION = "1.7.30-7a445";
/*    */   
/* 26 */   static final ThreadLocal<AtomicInteger> SENTRY_THREAD = new ThreadLocal<AtomicInteger>()
/*    */     {
/*    */       protected AtomicInteger initialValue() {
/* 29 */         return new AtomicInteger();
/*    */       }
/*    */     };
/* 32 */   private static final Logger logger = LoggerFactory.getLogger(SentryEnvironment.class);
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
/*    */   public static void startManagingThread() {
/*    */     try {
/* 54 */       if (isManagingThread()) {
/* 55 */         logger.warn("Thread already managed by Sentry");
/*    */       }
/*    */     } finally {
/* 58 */       ((AtomicInteger)SENTRY_THREAD.get()).incrementAndGet();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void stopManagingThread() {
/*    */     try {
/* 69 */       if (!isManagingThread()) {
/*    */         
/* 71 */         startManagingThread();
/* 72 */         logger.warn("Thread not yet managed by Sentry");
/*    */       } 
/*    */     } finally {
/* 75 */       if (((AtomicInteger)SENTRY_THREAD.get()).decrementAndGet() == 0)
/*    */       {
/*    */         
/* 78 */         SENTRY_THREAD.remove();
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isManagingThread() {
/* 89 */     return (((AtomicInteger)SENTRY_THREAD.get()).get() > 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getSentryName() {
/* 98 */     return "sentry-java/1.7.30-7a445";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\environment\SentryEnvironment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */