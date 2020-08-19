/*    */ package io.sentry.config.provider;
/*    */ 
/*    */ import io.sentry.config.ResourceLoader;
/*    */ import io.sentry.util.Nullable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Properties;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResourceLoaderConfigurationProvider
/*    */   implements ConfigurationProvider
/*    */ {
/* 20 */   private static final Logger logger = LoggerFactory.getLogger(ResourceLoaderConfigurationProvider.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private final Properties properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResourceLoaderConfigurationProvider(ResourceLoader rl, @Nullable String filePath, Charset charset) throws IOException {
/* 34 */     this.properties = loadProperties(rl, filePath, charset);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private static Properties loadProperties(ResourceLoader rl, @Nullable String filePath, Charset charset) throws IOException {
/* 40 */     if (filePath == null) {
/* 41 */       return null;
/*    */     }
/*    */     
/* 44 */     InputStream is = rl.getInputStream(filePath);
/*    */     
/* 46 */     if (is == null) {
/* 47 */       return null;
/*    */     }
/*    */     
/* 50 */     try (InputStreamReader rdr = new InputStreamReader(is, charset)) {
/* 51 */       Properties props = new Properties();
/* 52 */       props.load(rdr);
/* 53 */       return props;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getProperty(String key) {
/* 59 */     if (this.properties == null) {
/* 60 */       return null;
/*    */     }
/*    */     
/* 63 */     String ret = this.properties.getProperty(key);
/*    */     
/* 65 */     if (ret != null) {
/* 66 */       logger.debug("Found {}={} in properties file.", key, ret);
/*    */     }
/*    */     
/* 69 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\ResourceLoaderConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */