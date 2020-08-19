/*     */ package io.sentry.config;
/*     */ 
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.config.location.CompoundResourceLocator;
/*     */ import io.sentry.config.location.ConfigurationResourceLocator;
/*     */ import io.sentry.config.location.EnvironmentBasedLocator;
/*     */ import io.sentry.config.location.StaticFileLocator;
/*     */ import io.sentry.config.location.SystemPropertiesBasedLocator;
/*     */ import io.sentry.config.provider.CompoundConfigurationProvider;
/*     */ import io.sentry.config.provider.ConfigurationProvider;
/*     */ import io.sentry.config.provider.EnvironmentConfigurationProvider;
/*     */ import io.sentry.config.provider.JndiConfigurationProvider;
/*     */ import io.sentry.config.provider.JndiSupport;
/*     */ import io.sentry.config.provider.LocatorBasedConfigurationProvider;
/*     */ import io.sentry.config.provider.SystemPropertiesConfigurationProvider;
/*     */ import io.sentry.dsn.Dsn;
/*     */ import io.sentry.util.Nullable;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public final class Lookup
/*     */ {
/*  37 */   private static final Logger logger = LoggerFactory.getLogger(Lookup.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ConfigurationProvider highPriorityProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ConfigurationProvider lowPriorityProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lookup(ConfigurationProvider highPriorityProvider, ConfigurationProvider lowPriorityProvider) {
/*  53 */     this.highPriorityProvider = highPriorityProvider;
/*  54 */     this.lowPriorityProvider = lowPriorityProvider;
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
/*     */   public static Lookup getDefault() {
/*  72 */     return new Lookup((ConfigurationProvider)new CompoundConfigurationProvider(
/*     */           
/*  74 */           getDefaultHighPriorityConfigurationProviders(Collections.emptyList())), (ConfigurationProvider)new CompoundConfigurationProvider(
/*     */ 
/*     */           
/*  77 */           getDefaultLowPriorityConfigurationProviders(Collections.emptyList())));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Lookup getDefaultWithAdditionalProviders(Collection<ConfigurationProvider> highPriorityProviders, Collection<ConfigurationProvider> lowPriorityProviders) {
/* 102 */     return new Lookup((ConfigurationProvider)new CompoundConfigurationProvider(
/* 103 */           getDefaultHighPriorityConfigurationProviders(highPriorityProviders)), (ConfigurationProvider)new CompoundConfigurationProvider(
/* 104 */           getDefaultLowPriorityConfigurationProviders(lowPriorityProviders)));
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<ConfigurationResourceLocator> getDefaultResourceLocators() {
/* 109 */     return Arrays.asList(new ConfigurationResourceLocator[] { (ConfigurationResourceLocator)new SystemPropertiesBasedLocator(), (ConfigurationResourceLocator)new EnvironmentBasedLocator(), (ConfigurationResourceLocator)new StaticFileLocator() });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<ConfigurationProvider> getDefaultHighPriorityConfigurationProviders(Collection<ConfigurationProvider> additionalProviders) {
/* 115 */     boolean jndiPresent = JndiSupport.isAvailable();
/*     */ 
/*     */     
/* 118 */     int providersCount = jndiPresent ? (3 + additionalProviders.size()) : (2 + additionalProviders.size());
/*     */     
/* 120 */     List<ConfigurationProvider> providers = new ArrayList<>(providersCount);
/* 121 */     providers.addAll(additionalProviders);
/*     */     
/* 123 */     if (jndiPresent) {
/* 124 */       providers.add(new JndiConfigurationProvider());
/*     */     }
/*     */     
/* 127 */     providers.add(new SystemPropertiesConfigurationProvider());
/* 128 */     providers.add(new EnvironmentConfigurationProvider());
/*     */     
/* 130 */     return providers;
/*     */   }
/*     */   
/*     */   private static List<ResourceLoader> getDefaultResourceLoaders() {
/* 134 */     ResourceLoader sentryLoader = Sentry.getResourceLoader();
/*     */     
/* 136 */     return (sentryLoader == null) ? 
/* 137 */       Arrays.<ResourceLoader>asList(new ResourceLoader[] { new FileResourceLoader(), new ContextClassLoaderResourceLoader()
/* 138 */         }) : Arrays.<ResourceLoader>asList(new ResourceLoader[] { new FileResourceLoader(), sentryLoader, new ContextClassLoaderResourceLoader() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<ConfigurationProvider> getDefaultLowPriorityConfigurationProviders(Collection<ConfigurationProvider> additionalProviders) {
/* 145 */     List<ConfigurationProvider> providers = new ArrayList<>(additionalProviders.size());
/* 146 */     providers.addAll(additionalProviders);
/*     */     
/*     */     try {
/* 149 */       providers.add(new LocatorBasedConfigurationProvider(new CompoundResourceLoader(getDefaultResourceLoaders()), (ConfigurationResourceLocator)new CompoundResourceLocator(
/* 150 */               getDefaultResourceLocators()), Charset.defaultCharset()));
/* 151 */     } catch (IOException e) {
/* 152 */       logger.debug("Failed to instantiate resource locator-based configuration provider.", e);
/*     */     } 
/* 154 */     return providers;
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
/*     */   @Deprecated
/*     */   public static String lookup(String key) {
/* 168 */     return lookup(key, null);
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
/*     */   @Deprecated
/*     */   public static String lookup(String key, Dsn dsn) {
/* 188 */     return getDefault().get(key, dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String get(String key) {
/* 199 */     return get(key, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String get(String key, @Nullable Dsn dsn) {
/* 211 */     String val = this.highPriorityProvider.getProperty(key);
/*     */     
/* 213 */     if (val == null && dsn != null) {
/* 214 */       val = (String)dsn.getOptions().get(key);
/* 215 */       if (val != null) {
/* 216 */         logger.debug("Found {}={} in DSN.", key, val);
/*     */       }
/*     */     } 
/*     */     
/* 220 */     if (val == null) {
/* 221 */       val = this.lowPriorityProvider.getProperty(key);
/*     */     }
/*     */     
/* 224 */     return (val == null) ? null : val.trim();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\Lookup.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */