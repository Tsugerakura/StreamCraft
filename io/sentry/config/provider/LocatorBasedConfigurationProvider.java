/*    */ package io.sentry.config.provider;
/*    */ 
/*    */ import io.sentry.config.ResourceLoader;
/*    */ import io.sentry.config.location.ConfigurationResourceLocator;
/*    */ import java.io.IOException;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocatorBasedConfigurationProvider
/*    */   extends ResourceLoaderConfigurationProvider
/*    */ {
/*    */   public LocatorBasedConfigurationProvider(ResourceLoader rl, ConfigurationResourceLocator locator, Charset charset) throws IOException {
/* 24 */     super(rl, locator.getConfigurationResourcePath(), charset);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\LocatorBasedConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */