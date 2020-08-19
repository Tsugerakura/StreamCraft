/*     */ package io.sentry.event;
/*     */ 
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import io.sentry.event.interfaces.SentryStackTraceElement;
/*     */ import io.sentry.time.Clock;
/*     */ import io.sentry.time.SystemClock;
/*     */ import java.net.InetAddress;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Checksum;
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
/*     */ 
/*     */ 
/*     */ public class EventBuilder
/*     */ {
/*     */   public static final String DEFAULT_PLATFORM = "java";
/*     */   public static final String DEFAULT_HOSTNAME = "unavailable";
/*  41 */   public static final long HOSTNAME_CACHE_DURATION = TimeUnit.HOURS.toMillis(5L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   static final HostnameCache HOSTNAME_CACHE = new HostnameCache(HOSTNAME_CACHE_DURATION);
/*     */   private final Event event;
/*     */   private boolean alreadyBuilt = false;
/*  50 */   private Set<String> sdkIntegrations = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder() {
/*  58 */     this(UUID.randomUUID());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder(UUID eventId) {
/*  67 */     this.event = new Event(eventId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String calculateChecksum(String string) {
/*  77 */     byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
/*  78 */     Checksum checksum = new CRC32();
/*  79 */     checksum.update(bytes, 0, bytes.length);
/*  80 */     return Long.toHexString(checksum.getValue()).toUpperCase();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void autoSetMissingValues() {
/*  88 */     if (this.event.getTimestamp() == null) {
/*  89 */       this.event.setTimestamp(new Date());
/*     */     }
/*     */ 
/*     */     
/*  93 */     if (this.event.getPlatform() == null) {
/*  94 */       this.event.setPlatform("java");
/*     */     }
/*     */ 
/*     */     
/*  98 */     if (this.event.getSdk() == null) {
/*  99 */       this.event.setSdk(new Sdk("sentry-java", "1.7.30-7a445", this.sdkIntegrations));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 104 */     if (this.event.getServerName() == null) {
/* 105 */       this.event.setServerName(HOSTNAME_CACHE.getHostname());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void makeImmutable() {
/* 114 */     this.event.setTags(Collections.unmodifiableMap(this.event.getTags()));
/*     */ 
/*     */     
/* 117 */     this.event.setBreadcrumbs(Collections.unmodifiableList(this.event.getBreadcrumbs()));
/*     */ 
/*     */     
/* 120 */     Map<String, Map<String, Object>> tempContexts = new HashMap<>();
/* 121 */     for (Map.Entry<String, Map<String, Object>> contextEntry : this.event.getContexts().entrySet()) {
/* 122 */       tempContexts.put(contextEntry.getKey(), Collections.unmodifiableMap(contextEntry.getValue()));
/*     */     }
/* 124 */     this.event.setContexts(Collections.unmodifiableMap(tempContexts));
/*     */ 
/*     */     
/* 127 */     this.event.setExtra(Collections.unmodifiableMap(this.event.getExtra()));
/*     */ 
/*     */     
/* 130 */     this.event.setSentryInterfaces(Collections.unmodifiableMap(this.event.getSentryInterfaces()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withMessage(String message) {
/* 140 */     this.event.setMessage(message);
/* 141 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withTimestamp(Date timestamp) {
/* 151 */     this.event.setTimestamp(timestamp);
/* 152 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withLevel(Event.Level level) {
/* 162 */     this.event.setLevel(level);
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withRelease(String release) {
/* 173 */     this.event.setRelease(release);
/* 174 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withDist(String dist) {
/* 184 */     this.event.setDist(dist);
/* 185 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withEnvironment(String environment) {
/* 195 */     this.event.setEnvironment(environment);
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withLogger(String logger) {
/* 206 */     this.event.setLogger(logger);
/* 207 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withPlatform(String platform) {
/* 217 */     this.event.setPlatform(platform);
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withSdkIntegration(String integration) {
/* 228 */     this.sdkIntegrations.add(integration);
/* 229 */     return this;
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
/*     */   public EventBuilder withCulprit(SentryStackTraceElement frame) {
/* 243 */     return withCulprit(buildCulpritString(frame.getModule(), frame.getFunction(), frame
/* 244 */           .getFileName(), frame.getLineno()));
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
/*     */   @Deprecated
/*     */   public EventBuilder withCulprit(StackTraceElement frame) {
/* 257 */     return withCulprit(buildCulpritString(frame.getClassName(), frame.getMethodName(), frame
/* 258 */           .getFileName(), frame.getLineNumber()));
/*     */   }
/*     */   
/*     */   private String buildCulpritString(String className, String methodName, String fileName, int lineNumber) {
/* 262 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 264 */     sb.append(className)
/* 265 */       .append(".")
/* 266 */       .append(methodName);
/*     */     
/* 268 */     if (fileName != null && !fileName.isEmpty()) {
/* 269 */       sb.append("(").append(fileName);
/* 270 */       if (lineNumber >= 0) {
/* 271 */         sb.append(":").append(lineNumber);
/*     */       }
/* 273 */       sb.append(")");
/*     */     } 
/*     */     
/* 276 */     return sb.toString();
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
/*     */   public EventBuilder withCulprit(String culprit) {
/* 288 */     this.event.setCulprit(culprit);
/* 289 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withTransaction(String transaction) {
/* 299 */     this.event.setTransaction(transaction);
/* 300 */     return this;
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
/*     */   public EventBuilder withTag(String tagKey, String tagValue) {
/* 313 */     this.event.getTags().put(tagKey, tagValue);
/* 314 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withBreadcrumbs(List<Breadcrumb> breadcrumbs) {
/* 324 */     this.event.setBreadcrumbs(breadcrumbs);
/* 325 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withContexts(Map<String, Map<String, Object>> contexts) {
/* 335 */     this.event.setContexts(contexts);
/* 336 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withServerName(String serverName) {
/* 346 */     this.event.setServerName(serverName);
/* 347 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withExtra(String extraName, Object extraValue) {
/* 358 */     this.event.getExtra().put(extraName, extraValue);
/* 359 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withFingerprint(String... fingerprint) {
/* 369 */     List<String> list = new ArrayList<>(fingerprint.length);
/* 370 */     Collections.addAll(list, fingerprint);
/* 371 */     this.event.setFingerprint(list);
/* 372 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withFingerprint(List<String> fingerprint) {
/* 382 */     this.event.setFingerprint(fingerprint);
/* 383 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EventBuilder withChecksumFor(String contentToChecksum) {
/* 393 */     return withChecksum(calculateChecksum(contentToChecksum));
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
/*     */   public EventBuilder withChecksum(String checksum) {
/* 405 */     this.event.setChecksum(checksum);
/* 406 */     return this;
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
/*     */   public EventBuilder withSentryInterface(SentryInterface sentryInterface) {
/* 419 */     return withSentryInterface(sentryInterface, true);
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
/*     */   public EventBuilder withSentryInterface(SentryInterface sentryInterface, boolean replace) {
/* 434 */     if (replace || !this.event.getSentryInterfaces().containsKey(sentryInterface.getInterfaceName())) {
/* 435 */       this.event.getSentryInterfaces().put(sentryInterface.getInterfaceName(), sentryInterface);
/*     */     }
/* 437 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Event build() {
/* 448 */     if (this.alreadyBuilt) {
/* 449 */       throw new IllegalStateException("A message can't be built twice");
/*     */     }
/*     */     
/* 452 */     autoSetMissingValues();
/* 453 */     makeImmutable();
/*     */ 
/*     */     
/* 456 */     this.alreadyBuilt = true;
/* 457 */     return this.event;
/*     */   }
/*     */   
/*     */   public Event getEvent() {
/* 461 */     return this.event;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 466 */     return "EventBuilder{event=" + this.event + ", alreadyBuilt=" + this.alreadyBuilt + '}';
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
/*     */   static final class HostnameCache
/*     */   {
/* 487 */     static final long GET_HOSTNAME_TIMEOUT = TimeUnit.SECONDS.toMillis(1L);
/* 488 */     private static final Logger logger = LoggerFactory.getLogger(HostnameCache.class);
/*     */ 
/*     */ 
/*     */     
/*     */     final long cacheDuration;
/*     */ 
/*     */ 
/*     */     
/* 496 */     volatile String hostname = "unavailable";
/*     */ 
/*     */ 
/*     */     
/*     */     volatile long expirationTimestamp;
/*     */ 
/*     */ 
/*     */     
/* 504 */     private AtomicBoolean updateRunning = new AtomicBoolean(false);
/*     */     
/*     */     private final Clock clock;
/*     */     private final Callable<InetAddress> getLocalhost;
/*     */     
/*     */     private HostnameCache(long cacheDuration) {
/* 510 */       this(cacheDuration, (Clock)new SystemClock(), new Callable<InetAddress>()
/*     */           {
/*     */             public InetAddress call() throws Exception {
/* 513 */               return InetAddress.getLocalHost();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     HostnameCache(long cacheDuration, Clock clock, Callable<InetAddress> getLocalhost) {
/* 526 */       this.cacheDuration = cacheDuration;
/* 527 */       this.clock = clock;
/* 528 */       this.getLocalhost = getLocalhost;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     String getHostname() {
/* 539 */       if (this.expirationTimestamp < this.clock.millis() && this.updateRunning
/* 540 */         .compareAndSet(false, true)) {
/* 541 */         updateCache();
/*     */       }
/*     */       
/* 544 */       return this.hostname;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void updateCache() {
/* 551 */       Callable<Void> hostRetriever = new Callable<Void>()
/*     */         {
/*     */           public Void call() throws Exception {
/*     */             try {
/* 555 */               EventBuilder.HostnameCache.this.hostname = ((InetAddress)EventBuilder.HostnameCache.this.getLocalhost.call()).getCanonicalHostName();
/* 556 */               EventBuilder.HostnameCache.this.expirationTimestamp = EventBuilder.HostnameCache.this.clock.millis() + EventBuilder.HostnameCache.this.cacheDuration;
/*     */             } finally {
/* 558 */               EventBuilder.HostnameCache.this.updateRunning.set(false);
/*     */             } 
/*     */             
/* 561 */             return null;
/*     */           }
/*     */         };
/*     */       
/*     */       try {
/* 566 */         logger.debug("Updating the hostname cache");
/* 567 */         FutureTask<Void> futureTask = new FutureTask<>(hostRetriever);
/* 568 */         (new Thread(futureTask)).start();
/* 569 */         futureTask.get(GET_HOSTNAME_TIMEOUT, TimeUnit.MILLISECONDS);
/* 570 */       } catch (InterruptedException e) {
/* 571 */         Thread.currentThread().interrupt();
/* 572 */         handleCacheUpdateFailure(e);
/* 573 */       } catch (ExecutionException|java.util.concurrent.TimeoutException|RuntimeException e) {
/* 574 */         handleCacheUpdateFailure(e);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void handleCacheUpdateFailure(Exception failure) {
/* 579 */       this.expirationTimestamp = this.clock.millis() + TimeUnit.SECONDS.toMillis(1L);
/* 580 */       logger.debug("Localhost hostname lookup failed, keeping the value '{}'. If this persists it may mean your DNS is incorrectly configured and you may want to hardcode your server name: https://docs.sentry.io/clients/java/config/", this.hostname, failure);
/*     */     }
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
/*     */     void reset(long newExpirationTimestamp) {
/* 593 */       this.hostname = "unavailable";
/* 594 */       this.expirationTimestamp = newExpirationTimestamp;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\EventBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */