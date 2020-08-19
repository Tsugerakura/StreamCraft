/*    */ package io.sentry.config;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContextClassLoaderResourceLoader
/*    */   implements ResourceLoader
/*    */ {
/*    */   public InputStream getInputStream(String filepath) {
/* 12 */     ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
/* 13 */     if (classLoader == null) {
/* 14 */       classLoader = ClassLoader.getSystemClassLoader();
/*    */     }
/* 16 */     return classLoader.getResourceAsStream(filepath);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\ContextClassLoaderResourceLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */