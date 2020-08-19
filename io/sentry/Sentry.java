/*     */ package io.sentry;
/*     */ 
/*     */ import io.sentry.config.Lookup;
/*     */ import io.sentry.config.ResourceLoader;
/*     */ import io.sentry.context.Context;
/*     */ import io.sentry.event.Breadcrumb;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.event.EventBuilder;
/*     */ import io.sentry.event.User;
/*     */ import io.sentry.util.Nullable;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Sentry
/*     */ {
/*  19 */   private static final Logger logger = LoggerFactory.getLogger(Sentry.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  25 */   private static final Object STORED_CLIENT_ACCESS = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private static SentryClient storedClient = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private static ResourceLoader resourceLoader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient init() {
/*  61 */     return init((String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient init(@Nullable SentryClientFactory sentryClientFactory) {
/*  76 */     return init(SentryOptions.from(Lookup.getDefault(), null, sentryClientFactory));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient init(@Nullable String dsn) {
/*  91 */     return init(SentryOptions.defaults(dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient init(@Nullable String dsn, @Nullable SentryClientFactory sentryClientFactory) {
/* 110 */     SentryOptions options = SentryOptions.defaults(dsn);
/* 111 */     options.setSentryClientFactory(sentryClientFactory);
/* 112 */     return init(options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient init(SentryOptions sentryOptions) {
/* 134 */     resourceLoader = sentryOptions.getResourceLoader();
/*     */ 
/*     */ 
/*     */     
/* 138 */     SentryClient client = sentryOptions.getSentryClientFactory().createClient(sentryOptions.getDsn());
/* 139 */     setStoredClient(client);
/* 140 */     return client;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInitialized() {
/* 152 */     return (storedClient != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryClient getStoredClient() {
/* 163 */     synchronized (STORED_CLIENT_ACCESS) {
/* 164 */       if (isInitialized()) {
/* 165 */         return storedClient;
/*     */       }
/*     */       
/* 168 */       init(SentryOptions.defaults());
/*     */     } 
/*     */     
/* 171 */     return storedClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static ResourceLoader getResourceLoader() {
/* 183 */     return resourceLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Context getContext() {
/* 192 */     return getStoredClient().getContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearContext() {
/* 199 */     getStoredClient().clearContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setStoredClient(SentryClient client) {
/* 208 */     synchronized (STORED_CLIENT_ACCESS) {
/* 209 */       if (isInitialized()) {
/* 210 */         logger.warn("Overwriting statically stored SentryClient instance {} with {}.", storedClient, client);
/*     */       }
/*     */       
/* 213 */       storedClient = client;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void capture(Event event) {
/* 223 */     getStoredClient().sendEvent(event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void capture(Throwable throwable) {
/* 235 */     getStoredClient().sendException(throwable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void capture(String message) {
/* 246 */     getStoredClient().sendMessage(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void capture(EventBuilder eventBuilder) {
/* 256 */     getStoredClient().sendEvent(eventBuilder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void record(Breadcrumb breadcrumb) {
/* 267 */     getStoredClient().getContext().recordBreadcrumb(breadcrumb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void setUser(User user) {
/* 278 */     getStoredClient().getContext().setUser(user);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close() {
/* 285 */     synchronized (STORED_CLIENT_ACCESS) {
/* 286 */       if (!isInitialized()) {
/*     */         return;
/*     */       }
/*     */       
/* 290 */       storedClient.closeConnection();
/* 291 */       storedClient = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\Sentry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */