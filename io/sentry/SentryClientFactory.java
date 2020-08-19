/*     */ package io.sentry;
/*     */ 
/*     */ import io.sentry.config.Lookup;
/*     */ import io.sentry.dsn.Dsn;
/*     */ import io.sentry.util.Nullable;
/*     */ import io.sentry.util.Objects;
/*     */ import io.sentry.util.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SentryClientFactory
/*     */ {
/*  22 */   private static final Logger logger = LoggerFactory.getLogger(SentryClientFactory.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Lookup lookup;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SentryClientFactory(Lookup lookup) {
/*  35 */     this.lookup = (Lookup)Objects.requireNonNull(lookup);
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
/*     */   protected SentryClientFactory() {
/*  47 */     this(Lookup.getDefault());
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
/*     */   @Nullable
/*     */   public static SentryClient sentryClient() {
/*  60 */     return sentryClient(null, null);
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
/*     */   @Nullable
/*     */   public static SentryClient sentryClient(@Nullable String dsn) {
/*  74 */     return sentryClient(dsn, null);
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
/*     */   @Nullable
/*     */   public static SentryClient sentryClient(@Nullable String dsn, @Nullable SentryClientFactory sentryClientFactory) {
/*  89 */     Lookup lookup = Lookup.getDefault();
/*  90 */     String realDsn = dsnOrLookedUp(dsn, lookup);
/*     */     
/*  92 */     SentryClientFactory factory = (sentryClientFactory == null) ? instantiateFrom(lookup, realDsn) : sentryClientFactory;
/*     */     
/*  94 */     return (factory == null) ? null : factory.createClient(realDsn);
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
/*     */   @Nullable
/*     */   public static SentryClientFactory instantiateFrom(Lookup lookup, @Nullable String dsn) {
/*     */     SentryClientFactory sentryClientFactory;
/* 114 */     Dsn realDsn = new Dsn(dsnOrLookedUp(dsn, lookup));
/*     */ 
/*     */ 
/*     */     
/* 118 */     String sentryClientFactoryName = lookup.get("factory", realDsn);
/* 119 */     if (Util.isNullOrEmpty(sentryClientFactoryName)) {
/*     */       
/* 121 */       sentryClientFactory = new DefaultSentryClientFactory(lookup);
/*     */     } else {
/*     */       
/*     */       try {
/* 125 */         Class<?> factoryClass = Class.forName(sentryClientFactoryName);
/*     */         
/* 127 */         Constructor<?> ctor = null;
/*     */         try {
/* 129 */           ctor = factoryClass.getConstructor(new Class[] { Lookup.class });
/* 130 */           sentryClientFactory = (SentryClientFactory)ctor.newInstance(new Object[] { lookup });
/* 131 */         } catch (NoSuchMethodException e) {
/* 132 */           sentryClientFactory = (SentryClientFactory)factoryClass.newInstance();
/* 133 */         } catch (InvocationTargetException e) {
/* 134 */           logger.warn("Failed to instantiate SentryClientFactory using " + ctor + ". Falling back to using the default constructor, if any.");
/*     */           
/* 136 */           sentryClientFactory = (SentryClientFactory)factoryClass.newInstance();
/*     */         } 
/* 138 */       } catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
/* 139 */         logger.error("Error creating SentryClient using factory class: '" + sentryClientFactoryName + "'.", e);
/*     */         
/* 141 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 145 */     return sentryClientFactory;
/*     */   }
/*     */   
/*     */   private static String dsnOrLookedUp(@Nullable String dsn, Lookup lookup) {
/* 149 */     if (dsn == null) {
/* 150 */       dsn = Dsn.dsnFrom(lookup);
/*     */     }
/*     */     
/* 153 */     return dsn;
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
/*     */   public abstract SentryClient createSentryClient(Dsn paramDsn);
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
/*     */   public SentryClient createClient(@Nullable String dsn) {
/* 177 */     Dsn realDsn = new Dsn((dsn == null) ? Dsn.dsnFrom(this.lookup) : dsn);
/* 178 */     return createSentryClient(realDsn);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 183 */     return "SentryClientFactory{name='" + 
/* 184 */       getClass().getName() + '\'' + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\SentryClientFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */