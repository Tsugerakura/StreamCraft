/*    */ package io.sentry.servlet;
/*    */ 
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContainerInitializer;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SentryServletContainerInitializer
/*    */   implements ServletContainerInitializer
/*    */ {
/*    */   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
/* 14 */     ctx.addListener(SentryServletRequestListener.class);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\servlet\SentryServletContainerInitializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */