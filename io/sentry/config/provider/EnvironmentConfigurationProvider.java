/*    */ package io.sentry.config.provider;
/*    */ 
/*    */ import io.sentry.util.Nullable;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnvironmentConfigurationProvider
/*    */   implements ConfigurationProvider
/*    */ {
/*    */   public static final String DEFAULT_ENV_VAR_PREFIX = "SENTRY_";
/* 16 */   private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfigurationProvider.class);
/*    */ 
/*    */   
/*    */   private final String prefix;
/*    */ 
/*    */ 
/*    */   
/*    */   public EnvironmentConfigurationProvider() {
/* 24 */     this("SENTRY_");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EnvironmentConfigurationProvider(String envVarPrefix) {
/* 33 */     this.prefix = envVarPrefix;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getProperty(String key) {
/* 39 */     String ret = System.getenv(this.prefix + key.replace(".", "_").toUpperCase());
/*    */     
/* 41 */     if (ret != null) {
/* 42 */       logger.debug("Found {}={} in System Environment Variables.", key, ret);
/*    */     }
/*    */     
/* 45 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\EnvironmentConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */