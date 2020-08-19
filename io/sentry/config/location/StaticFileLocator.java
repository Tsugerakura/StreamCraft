/*    */ package io.sentry.config.location;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticFileLocator
/*    */   implements ConfigurationResourceLocator
/*    */ {
/*    */   public static final String DEFAULT_FILE_PATH = "sentry.properties";
/*    */   private final String path;
/*    */   
/*    */   public StaticFileLocator() {
/* 18 */     this("sentry.properties");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StaticFileLocator(String filePath) {
/* 27 */     this.path = filePath;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getConfigurationResourcePath() {
/* 32 */     return this.path;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\location\StaticFileLocator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */