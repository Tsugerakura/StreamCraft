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
/*    */ public class SystemPropertiesConfigurationProvider
/*    */   implements ConfigurationProvider
/*    */ {
/*    */   public static final String DEFAULT_SYSTEM_PROPERTY_PREFIX = "sentry.";
/* 16 */   private static final Logger logger = LoggerFactory.getLogger(SystemPropertiesConfigurationProvider.class);
/*    */ 
/*    */   
/*    */   private final String prefix;
/*    */ 
/*    */ 
/*    */   
/*    */   public SystemPropertiesConfigurationProvider() {
/* 24 */     this("sentry.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SystemPropertiesConfigurationProvider(String systemPropertyPrefix) {
/* 34 */     this.prefix = systemPropertyPrefix;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getProperty(String key) {
/* 40 */     String ret = System.getProperty(this.prefix + key.toLowerCase());
/*    */     
/* 42 */     if (ret != null) {
/* 43 */       logger.debug("Found {}={} in Java System Properties.", key, ret);
/*    */     }
/*    */     
/* 46 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\SystemPropertiesConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */