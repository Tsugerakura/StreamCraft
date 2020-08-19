/*    */ package io.sentry.event.helper;
/*    */ 
/*    */ import io.sentry.SentryClient;
/*    */ import io.sentry.context.Context;
/*    */ import io.sentry.event.Breadcrumb;
/*    */ import io.sentry.event.EventBuilder;
/*    */ import io.sentry.event.User;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import io.sentry.event.interfaces.UserInterface;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class ContextBuilderHelper
/*    */   implements EventBuilderHelper
/*    */ {
/*    */   private SentryClient sentryClient;
/*    */   
/*    */   public ContextBuilderHelper(SentryClient sentryClient) {
/* 30 */     this.sentryClient = sentryClient;
/*    */   }
/*    */ 
/*    */   
/*    */   public void helpBuildingEvent(EventBuilder eventBuilder) {
/* 35 */     Context context = this.sentryClient.getContext();
/*    */     
/* 37 */     List<Breadcrumb> breadcrumbs = context.getBreadcrumbs();
/* 38 */     if (!breadcrumbs.isEmpty()) {
/* 39 */       eventBuilder.withBreadcrumbs(breadcrumbs);
/*    */     }
/*    */     
/* 42 */     if (context.getHttp() != null) {
/* 43 */       eventBuilder.withSentryInterface((SentryInterface)context.getHttp());
/*    */     }
/*    */     
/* 46 */     if (context.getUser() != null) {
/* 47 */       eventBuilder.withSentryInterface((SentryInterface)fromUser(context.getUser()));
/*    */     }
/*    */     
/* 50 */     Map<String, String> tags = context.getTags();
/* 51 */     if (!tags.isEmpty()) {
/* 52 */       for (Map.Entry<String, String> entry : tags.entrySet()) {
/* 53 */         eventBuilder.withTag(entry.getKey(), entry.getValue());
/*    */       }
/*    */     }
/*    */     
/* 57 */     Map<String, Object> extra = context.getExtra();
/* 58 */     if (!extra.isEmpty()) {
/* 59 */       for (Map.Entry<String, Object> entry : extra.entrySet()) {
/* 60 */         eventBuilder.withExtra(entry.getKey(), entry.getValue());
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private UserInterface fromUser(User user) {
/* 71 */     return new UserInterface(user.getId(), user.getUsername(), user.getIpAddress(), user
/* 72 */         .getEmail(), user.getData());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\helper\ContextBuilderHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */