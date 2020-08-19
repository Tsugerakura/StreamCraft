/*    */ package io.sentry.config.location;
/*    */ 
/*    */ import io.sentry.util.Nullable;
/*    */ import java.util.Collection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompoundResourceLocator
/*    */   implements ConfigurationResourceLocator
/*    */ {
/*    */   private final Collection<ConfigurationResourceLocator> locators;
/*    */   
/*    */   public CompoundResourceLocator(Collection<ConfigurationResourceLocator> locators) {
/* 18 */     this.locators = locators;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getConfigurationResourcePath() {
/* 30 */     for (ConfigurationResourceLocator l : this.locators) {
/* 31 */       String path = l.getConfigurationResourcePath();
/* 32 */       if (path != null) {
/* 33 */         return path;
/*    */       }
/*    */     } 
/*    */     
/* 37 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\location\CompoundResourceLocator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */