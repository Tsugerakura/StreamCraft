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
/*    */ public class EnvironmentBasedLocator
/*    */   implements ConfigurationResourceLocator
/*    */ {
/*    */   public static final String DEFAULT_ENV_VAR_NAME = "SENTRY_PROPERTIES_FILE";
/*    */   private final String envVarName;
/*    */   
/*    */   public EnvironmentBasedLocator() {
/* 20 */     this("SENTRY_PROPERTIES_FILE");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EnvironmentBasedLocator(String envVarName) {
/* 29 */     this.envVarName = envVarName;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getConfigurationResourcePath() {
/* 35 */     return System.getenv(this.envVarName);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\location\EnvironmentBasedLocator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */