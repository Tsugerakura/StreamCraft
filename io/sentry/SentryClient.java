/*     */ package io.sentry;
/*     */ 
/*     */ import io.sentry.connection.Connection;
/*     */ import io.sentry.connection.EventSendCallback;
/*     */ import io.sentry.connection.LockedDownException;
/*     */ import io.sentry.context.Context;
/*     */ import io.sentry.context.ContextManager;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.event.EventBuilder;
/*     */ import io.sentry.event.helper.EventBuilderHelper;
/*     */ import io.sentry.event.helper.ShouldSendEventCallback;
/*     */ import io.sentry.event.interfaces.ExceptionInterface;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ public class SentryClient
/*     */ {
/*  29 */   private static final Logger logger = LoggerFactory.getLogger(SentryClient.class);
/*     */   
/*  31 */   private static final Logger lockdownLogger = LoggerFactory.getLogger(SentryClient.class.getName() + ".lockdown");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String release;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String dist;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String environment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String serverName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   protected Map<String, String> tags = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*  66 */   protected Set<String> mdcTags = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   protected Map<String, Object> extra = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*  76 */   private final Set<ShouldSendEventCallback> shouldSendEventCallbacks = new HashSet<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final Connection connection;
/*     */ 
/*     */ 
/*     */   
/*  84 */   private final List<EventBuilderHelper> builderHelpers = new CopyOnWriteArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ContextManager contextManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SentryUncaughtExceptionHandler uncaughtExceptionHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SentryClient(Connection connection, ContextManager contextManager) {
/* 105 */     this.connection = connection;
/* 106 */     this.contextManager = contextManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void runBuilderHelpers(EventBuilder eventBuilder) {
/* 116 */     for (EventBuilderHelper builderHelper : this.builderHelpers) {
/* 117 */       builderHelper.helpBuildingEvent(eventBuilder);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendEvent(Event event) {
/* 127 */     if (event == null) {
/*     */       return;
/*     */     }
/*     */     
/* 131 */     for (ShouldSendEventCallback shouldSendEventCallback : this.shouldSendEventCallbacks) {
/* 132 */       if (!shouldSendEventCallback.shouldSend(event)) {
/* 133 */         logger.trace("Not sending Event because of ShouldSendEventCallback: {}", shouldSendEventCallback);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     try {
/* 139 */       this.connection.send(event);
/* 140 */     } catch (LockedDownException|io.sentry.connection.TooManyRequestsException e) {
/* 141 */       logger.debug("Dropping an Event due to lockdown: " + event);
/* 142 */     } catch (RuntimeException e) {
/* 143 */       logger.error("An exception occurred while sending the event to Sentry.", e);
/*     */     } finally {
/* 145 */       getContext().setLastEventId(event.getId());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendEvent(EventBuilder eventBuilder) {
/* 155 */     if (eventBuilder == null) {
/*     */       return;
/*     */     }
/* 158 */     Event event = buildEvent(eventBuilder);
/* 159 */     sendEvent(event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Event buildEvent(EventBuilder eventBuilder) {
/* 168 */     Event workingEvent = eventBuilder.getEvent();
/*     */     
/* 170 */     if (!Util.isNullOrEmpty(this.release) && workingEvent.getRelease() == null) {
/* 171 */       eventBuilder.withRelease(this.release.trim());
/* 172 */       if (!Util.isNullOrEmpty(this.dist)) {
/* 173 */         eventBuilder.withDist(this.dist.trim());
/*     */       }
/*     */     } 
/*     */     
/* 177 */     if (!Util.isNullOrEmpty(this.environment) && workingEvent.getEnvironment() == null) {
/* 178 */       eventBuilder.withEnvironment(this.environment.trim());
/*     */     }
/*     */     
/* 181 */     if (!Util.isNullOrEmpty(this.serverName) && workingEvent.getServerName() == null) {
/* 182 */       eventBuilder.withServerName(this.serverName.trim());
/*     */     }
/*     */     
/* 185 */     for (Map.Entry<String, String> tagEntry : this.tags.entrySet()) {
/* 186 */       Map<String, String> workingTags = workingEvent.getTags();
/* 187 */       String currentValue = workingTags.put(tagEntry.getKey(), tagEntry.getValue());
/*     */       
/* 189 */       if (currentValue != null) {
/* 190 */         workingTags.put(tagEntry.getKey(), currentValue);
/*     */       }
/*     */     } 
/*     */     
/* 194 */     for (Map.Entry<String, Object> extraEntry : this.extra.entrySet()) {
/* 195 */       Map<String, Object> workingExtra = workingEvent.getExtra();
/* 196 */       Object currentValue = workingExtra.put(extraEntry.getKey(), extraEntry.getValue());
/*     */       
/* 198 */       if (currentValue != null) {
/* 199 */         workingExtra.put(extraEntry.getKey(), currentValue);
/*     */       }
/*     */     } 
/*     */     
/* 203 */     runBuilderHelpers(eventBuilder);
/* 204 */     return eventBuilder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendMessage(String message) {
/* 215 */     if (message == null) {
/*     */       return;
/*     */     }
/*     */     
/* 219 */     EventBuilder eventBuilder = (new EventBuilder()).withMessage(message).withLevel(Event.Level.INFO);
/* 220 */     sendEvent(eventBuilder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendException(Throwable throwable) {
/* 231 */     if (throwable == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 237 */     EventBuilder eventBuilder = (new EventBuilder()).withMessage(throwable.getMessage()).withLevel(Event.Level.ERROR).withSentryInterface((SentryInterface)new ExceptionInterface(throwable));
/* 238 */     sendEvent(eventBuilder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeBuilderHelper(EventBuilderHelper builderHelper) {
/* 247 */     logger.debug("Removing '{}' from the list of builder helpers.", builderHelper);
/* 248 */     this.builderHelpers.remove(builderHelper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addBuilderHelper(EventBuilderHelper builderHelper) {
/* 257 */     logger.debug("Adding '{}' to the list of builder helpers.", builderHelper);
/* 258 */     this.builderHelpers.add(builderHelper);
/*     */   }
/*     */   
/*     */   public List<EventBuilderHelper> getBuilderHelpers() {
/* 262 */     return Collections.unmodifiableList(this.builderHelpers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeConnection() {
/* 269 */     if (this.uncaughtExceptionHandler != null) {
/* 270 */       this.uncaughtExceptionHandler.disable();
/*     */     }
/*     */     
/*     */     try {
/* 274 */       this.connection.close();
/* 275 */     } catch (IOException e) {
/* 276 */       throw new RuntimeException("Couldn't close the Sentry connection", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearContext() {
/* 284 */     this.contextManager.clear();
/*     */   }
/*     */   
/*     */   public Context getContext() {
/* 288 */     return this.contextManager.getContext();
/*     */   }
/*     */   
/*     */   public String getRelease() {
/* 292 */     return this.release;
/*     */   }
/*     */   
/*     */   public String getDist() {
/* 296 */     return this.dist;
/*     */   }
/*     */   
/*     */   public String getEnvironment() {
/* 300 */     return this.environment;
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 304 */     return this.serverName;
/*     */   }
/*     */   
/*     */   public Map<String, String> getTags() {
/* 308 */     return Collections.unmodifiableMap(this.tags);
/*     */   }
/*     */   
/*     */   public Set<String> getMdcTags() {
/* 312 */     return Collections.unmodifiableSet(this.mdcTags);
/*     */   }
/*     */   
/*     */   public Map<String, Object> getExtra() {
/* 316 */     return this.extra;
/*     */   }
/*     */   
/*     */   public void setRelease(String release) {
/* 320 */     this.release = release;
/*     */   }
/*     */   
/*     */   public void setDist(String dist) {
/* 324 */     this.dist = dist;
/*     */   }
/*     */   
/*     */   public void setEnvironment(String environment) {
/* 328 */     this.environment = environment;
/*     */   }
/*     */   
/*     */   public void setServerName(String serverName) {
/* 332 */     this.serverName = serverName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTag(String name, String value) {
/* 342 */     this.tags.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTags(Map<String, String> tags) {
/* 351 */     if (tags == null) {
/* 352 */       this.tags = new HashMap<>();
/*     */     } else {
/* 354 */       this.tags = tags;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setExtraTags(Set<String> extraTags) {
/* 366 */     setMdcTags(extraTags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMdcTags(Set<String> mdcTags) {
/* 375 */     if (mdcTags == null) {
/* 376 */       this.mdcTags = new HashSet<>();
/*     */     } else {
/* 378 */       this.mdcTags = mdcTags;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void addExtraTag(String extraName) {
/* 390 */     addMdcTag(extraName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMdcTag(String tagName) {
/* 399 */     this.mdcTags.add(tagName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExtra(String name, Object value) {
/* 409 */     this.extra.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExtra(Map<String, Object> extra) {
/* 418 */     if (extra == null) {
/* 419 */       this.extra = new HashMap<>();
/*     */     } else {
/* 421 */       this.extra = extra;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEventSendCallback(EventSendCallback eventSendCallback) {
/* 431 */     this.connection.addEventSendCallback(eventSendCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addShouldSendEventCallback(ShouldSendEventCallback shouldSendEventCallback) {
/* 440 */     this.shouldSendEventCallbacks.add(shouldSendEventCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Connection getConnection() {
/* 450 */     return this.connection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setupUncaughtExceptionHandler() {
/* 458 */     this.uncaughtExceptionHandler = SentryUncaughtExceptionHandler.setup();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 463 */     return "SentryClient{release='" + this.release + '\'' + ", dist='" + this.dist + '\'' + ", environment='" + this.environment + '\'' + ", serverName='" + this.serverName + '\'' + ", tags=" + this.tags + ", mdcTags=" + this.mdcTags + ", extra=" + this.extra + ", connection=" + this.connection + ", builderHelpers=" + this.builderHelpers + ", contextManager=" + this.contextManager + ", uncaughtExceptionHandler=" + this.uncaughtExceptionHandler + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\SentryClient.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */