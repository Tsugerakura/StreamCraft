/*    */ package io.sentry.config.provider;
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
/*    */ 
/*    */ public class JndiConfigurationProvider
/*    */   implements ConfigurationProvider
/*    */ {
/*    */   public static final String DEFAULT_JNDI_PREFIX = "java:comp/env/sentry/";
/* 23 */   private static final Logger logger = LoggerFactory.getLogger(JndiConfigurationProvider.class);
/*    */ 
/*    */   
/*    */   private final String prefix;
/*    */ 
/*    */   
/*    */   private final JndiContextProvider contextProvider;
/*    */ 
/*    */ 
/*    */   
/*    */   public JndiConfigurationProvider() {
/* 34 */     this("java:comp/env/sentry/", new JndiContextProvider()
/*    */         {
/*    */           public Context getContext() throws NamingException {
/* 37 */             return new InitialContext();
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static interface JndiContextProvider
/*    */   {
/*    */     Context getContext() throws NamingException;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public JndiConfigurationProvider(String jndiNamePrefix, JndiContextProvider contextProvider) {
/* 53 */     this.prefix = jndiNamePrefix;
/* 54 */     this.contextProvider = contextProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getProperty(String key) {
/* 59 */     String value = null;
/*    */     try {
/* 61 */       Context ctx = this.contextProvider.getContext();
/* 62 */       value = (String)ctx.lookup(this.prefix + key);
/* 63 */     } catch (NoInitialContextException e) {
/* 64 */       logger.trace("JNDI not configured for Sentry (NoInitialContextEx)");
/* 65 */     } catch (NamingException e) {
/* 66 */       logger.trace("No " + this.prefix + key + " in JNDI");
/* 67 */     } catch (RuntimeException e) {
/* 68 */       logger.warn("Odd RuntimeException while testing for JNDI", e);
/*    */     } 
/* 70 */     return value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\JndiConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */