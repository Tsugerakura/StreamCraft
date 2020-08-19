/*    */ package io.sentry.servlet;
/*    */ 
/*    */ import io.sentry.Sentry;
/*    */ import io.sentry.SentryClient;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletRequestEvent;
/*    */ import javax.servlet.ServletRequestListener;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SentryServletRequestListener
/*    */   implements ServletRequestListener
/*    */ {
/* 19 */   private static final Logger logger = LoggerFactory.getLogger(SentryServletRequestListener.class);
/*    */   
/* 21 */   private static final ThreadLocal<HttpServletRequest> THREAD_REQUEST = new ThreadLocal<>();
/*    */   
/*    */   public static HttpServletRequest getServletRequest() {
/* 24 */     return THREAD_REQUEST.get();
/*    */   }
/*    */ 
/*    */   
/*    */   public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
/* 29 */     THREAD_REQUEST.remove();
/*    */     
/*    */     try {
/* 32 */       SentryClient sentryClient = Sentry.getStoredClient();
/* 33 */       if (sentryClient != null) {
/* 34 */         sentryClient.clearContext();
/*    */       }
/* 36 */     } catch (RuntimeException e) {
/* 37 */       logger.error("Error clearing Context state.", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void requestInitialized(ServletRequestEvent servletRequestEvent) {
/* 43 */     ServletRequest servletRequest = servletRequestEvent.getServletRequest();
/* 44 */     if (servletRequest instanceof HttpServletRequest) {
/* 45 */       THREAD_REQUEST.set((HttpServletRequest)servletRequest);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static void reset() {
/* 55 */     THREAD_REQUEST.remove();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\servlet\SentryServletRequestListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */