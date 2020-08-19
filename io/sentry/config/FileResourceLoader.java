/*    */ package io.sentry.config;
/*    */ 
/*    */ import io.sentry.util.Nullable;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.InputStream;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileResourceLoader
/*    */   implements ResourceLoader
/*    */ {
/* 16 */   private static final Logger logger = LoggerFactory.getLogger(FileResourceLoader.class);
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public InputStream getInputStream(String filepath) {
/* 21 */     File f = new File(filepath);
/* 22 */     if (f.isFile() && f.canRead()) {
/*    */       try {
/* 24 */         return new FileInputStream(f);
/* 25 */       } catch (FileNotFoundException e) {
/* 26 */         logger.debug("Configuration file {} could not be found even though we just checked it can be read...", filepath);
/*    */       } 
/*    */     } else {
/*    */       
/* 30 */       logger.debug("The configuration file {} (which resolves to absolute path {}) doesn't exist, is not a file or is not readable.", f, f
/* 31 */           .getAbsolutePath());
/*    */     } 
/*    */     
/* 34 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\FileResourceLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */