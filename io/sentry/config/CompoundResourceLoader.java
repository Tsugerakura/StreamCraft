/*    */ package io.sentry.config;
/*    */ 
/*    */ import io.sentry.util.Nullable;
/*    */ import java.io.InputStream;
/*    */ import java.util.Collection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompoundResourceLoader
/*    */   implements ResourceLoader
/*    */ {
/*    */   private final Collection<ResourceLoader> loaders;
/*    */   
/*    */   public CompoundResourceLoader(Collection<ResourceLoader> loaders) {
/* 21 */     this.loaders = loaders;
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
/*    */   @Nullable
/*    */   public InputStream getInputStream(String filepath) {
/* 34 */     for (ResourceLoader l : this.loaders) {
/* 35 */       InputStream is = l.getInputStream(filepath);
/* 36 */       if (is != null) {
/* 37 */         return is;
/*    */       }
/*    */     } 
/*    */     
/* 41 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\CompoundResourceLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */