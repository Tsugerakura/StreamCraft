/*    */ package io.sentry.config;
/*    */ 
/*    */ import javax.naming.Context;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.NoInitialContextException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public final class JndiLookup
/*    */ {
/*    */   private static final String JNDI_PREFIX = "java:comp/env/sentry/";
/* 22 */   private static final Logger logger = LoggerFactory.getLogger(JndiLookup.class);
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
/*    */   public static String jndiLookup(String key) {
/* 35 */     String value = null;
/*    */     try {
/* 37 */       Context c = new InitialContext();
/* 38 */       value = (String)c.lookup("java:comp/env/sentry/" + key);
/* 39 */     } catch (NoInitialContextException e) {
/* 40 */       logger.trace("JNDI not configured for Sentry (NoInitialContextEx)");
/* 41 */     } catch (NamingException e) {
/* 42 */       logger.trace("No /sentry/" + key + " in JNDI");
/* 43 */     } catch (RuntimeException e) {
/* 44 */       logger.warn("Odd RuntimeException while testing for JNDI", e);
/*    */     } 
/* 46 */     return value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\JndiLookup.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */