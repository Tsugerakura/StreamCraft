/*    */ package io.sentry.config.location;
/*    */ 
/*    */ import io.sentry.util.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SystemPropertiesBasedLocator
/*    */   implements ConfigurationResourceLocator
/*    */ {
/*    */   public static final String DEFAULT_PROPERTY_NAME = "sentry.properties.file";
/*    */   private final String propertyName;
/*    */   
/*    */   public SystemPropertiesBasedLocator() {
/* 20 */     this("sentry.properties.file");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SystemPropertiesBasedLocator(String propertyName) {
/* 29 */     this.propertyName = propertyName;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getConfigurationResourcePath() {
/* 35 */     return System.getProperty(this.propertyName);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\location\SystemPropertiesBasedLocator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */