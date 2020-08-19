/*     */ package io.sentry;
/*     */ 
/*     */ import io.sentry.config.Lookup;
/*     */ import io.sentry.config.ResourceLoader;
/*     */ import io.sentry.dsn.Dsn;
/*     */ import io.sentry.util.Nullable;
/*     */ import io.sentry.util.Objects;
/*     */ import io.sentry.util.Util;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SentryOptions
/*     */ {
/*  16 */   private static final Logger logger = LoggerFactory.getLogger(SentryOptions.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Lookup lookup;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SentryClientFactory sentryClientFactory;
/*     */ 
/*     */ 
/*     */   
/*     */   private String dsn;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private ResourceLoader resourceLoader;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SentryOptions(Lookup lookup, @Nullable String dsn, @Nullable SentryClientFactory sentryClientFactory) {
/*  41 */     this.lookup = (Lookup)Objects.requireNonNull(lookup, "lookup");
/*  42 */     this.dsn = resolveDsn(lookup, dsn);
/*  43 */     this
/*  44 */       .sentryClientFactory = (sentryClientFactory == null) ? SentryClientFactory.instantiateFrom(this.lookup, this.dsn) : sentryClientFactory;
/*     */     
/*  46 */     this.resourceLoader = null;
/*     */     
/*  48 */     if (this.sentryClientFactory == null) {
/*  49 */       logger.error("Failed to find a Sentry client factory in the provided configuration. Will continue with a dummy implementation that will send no data.");
/*     */ 
/*     */       
/*  52 */       this.sentryClientFactory = new InvalidSentryClientFactory();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryOptions from(Lookup lookup) {
/*  64 */     return from(lookup, null, null);
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
/*     */   public static SentryOptions from(Lookup lookup, @Nullable String dsn) {
/*  76 */     return from(lookup, dsn, null);
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
/*     */   public static SentryOptions from(Lookup lookup, @Nullable String dsn, @Nullable SentryClientFactory factory) {
/*  89 */     return new SentryOptions(lookup, dsn, factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryOptions defaults() {
/* 100 */     return defaults(null);
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
/*     */   public static SentryOptions defaults(@Nullable String dsn) {
/* 116 */     return from(Lookup.getDefault(), dsn, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SentryClientFactory getSentryClientFactory() {
/* 124 */     return this.sentryClientFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSentryClientFactory(@Nullable SentryClientFactory clientFactory) {
/* 132 */     this
/* 133 */       .sentryClientFactory = (clientFactory == null) ? SentryClientFactory.instantiateFrom(getLookup(), getDsn()) : clientFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDsn() {
/* 142 */     return this.dsn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDsn(@Nullable String dsn) {
/* 150 */     this.dsn = resolveDsn(getLookup(), dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lookup getLookup() {
/* 158 */     return this.lookup;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLookup(Lookup lookup) {
/* 166 */     this.lookup = (Lookup)Objects.requireNonNull(lookup);
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
/*     */   @Deprecated
/*     */   public ResourceLoader getResourceLoader() {
/* 182 */     return this.resourceLoader;
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
/*     */   @Deprecated
/*     */   public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
/* 199 */     this.resourceLoader = resourceLoader;
/*     */   }
/*     */   
/*     */   private static String resolveDsn(Lookup lookup, @Nullable String dsn) {
/*     */     try {
/* 204 */       if (Util.isNullOrEmpty(dsn)) {
/* 205 */         dsn = Dsn.dsnFrom(lookup);
/*     */       }
/*     */       
/* 208 */       return dsn;
/* 209 */     } catch (RuntimeException e) {
/* 210 */       logger.error("Error creating valid DSN from: '{}'.", dsn, e);
/* 211 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class InvalidSentryClientFactory
/*     */     extends SentryClientFactory
/*     */   {
/*     */     private InvalidSentryClientFactory() {
/* 221 */       super(SentryOptions.this.getLookup());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SentryClient createSentryClient(Dsn newDsn) {
/* 232 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\SentryOptions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */