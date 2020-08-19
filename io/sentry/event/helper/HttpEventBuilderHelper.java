/*    */ package io.sentry.event.helper;
/*    */ 
/*    */ import io.sentry.event.EventBuilder;
/*    */ import io.sentry.event.interfaces.HttpInterface;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import io.sentry.event.interfaces.UserInterface;
/*    */ import io.sentry.servlet.SentryServletRequestListener;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpEventBuilderHelper
/*    */   implements EventBuilderHelper
/*    */ {
/*    */   private final RemoteAddressResolver remoteAddressResolver;
/*    */   
/*    */   public HttpEventBuilderHelper() {
/* 24 */     this.remoteAddressResolver = new BasicRemoteAddressResolver();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpEventBuilderHelper(RemoteAddressResolver remoteAddressResolver) {
/* 33 */     this.remoteAddressResolver = remoteAddressResolver;
/*    */   }
/*    */ 
/*    */   
/*    */   public void helpBuildingEvent(EventBuilder eventBuilder) {
/* 38 */     HttpServletRequest servletRequest = SentryServletRequestListener.getServletRequest();
/* 39 */     if (servletRequest == null) {
/*    */       return;
/*    */     }
/*    */     
/* 43 */     addHttpInterface(eventBuilder, servletRequest);
/* 44 */     addUserInterface(eventBuilder, servletRequest);
/*    */   }
/*    */   
/*    */   private void addHttpInterface(EventBuilder eventBuilder, HttpServletRequest servletRequest) {
/* 48 */     eventBuilder.withSentryInterface((SentryInterface)new HttpInterface(servletRequest, this.remoteAddressResolver), false);
/*    */   }
/*    */   
/*    */   private void addUserInterface(EventBuilder eventBuilder, HttpServletRequest servletRequest) {
/* 52 */     String username = null;
/* 53 */     if (servletRequest.getUserPrincipal() != null) {
/* 54 */       username = servletRequest.getUserPrincipal().getName();
/*    */     }
/*    */ 
/*    */     
/* 58 */     UserInterface userInterface = new UserInterface(null, username, this.remoteAddressResolver.getRemoteAddress(servletRequest), null);
/* 59 */     eventBuilder.withSentryInterface((SentryInterface)userInterface, false);
/*    */   }
/*    */   
/*    */   public RemoteAddressResolver getRemoteAddressResolver() {
/* 63 */     return this.remoteAddressResolver;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\helper\HttpEventBuilderHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */