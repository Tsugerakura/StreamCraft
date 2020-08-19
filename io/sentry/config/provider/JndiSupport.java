/*    */ package io.sentry.config.provider;
/*    */ 
/*    */ import io.sentry.dsn.Dsn;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class JndiSupport
/*    */ {
/* 11 */   private static final Logger logger = LoggerFactory.getLogger(JndiSupport.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isAvailable() {
/*    */     try {
/* 22 */       Class.forName("javax.naming.InitialContext", false, Dsn.class.getClassLoader());
/* 23 */       return true;
/* 24 */     } catch (ClassNotFoundException|NoClassDefFoundError e) {
/* 25 */       logger.trace("JNDI is not available: " + e.getMessage());
/* 26 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\JndiSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */