/*    */ package io.sentry.config.provider;
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
/*    */ 
/*    */ public class CompoundConfigurationProvider
/*    */   implements ConfigurationProvider
/*    */ {
/*    */   private final Collection<ConfigurationProvider> providers;
/*    */   
/*    */   public CompoundConfigurationProvider(Collection<ConfigurationProvider> providers) {
/* 19 */     this.providers = providers;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getProperty(String key) {
/* 25 */     for (ConfigurationProvider p : this.providers) {
/* 26 */       String val = p.getProperty(key);
/* 27 */       if (val != null) {
/* 28 */         return val;
/*    */       }
/*    */     } 
/*    */     
/* 32 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\CompoundConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */