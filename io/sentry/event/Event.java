/*     */ package io.sentry.event;
/*     */ 
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Event
/*     */   implements Serializable
/*     */ {
/*  35 */   private static final Logger _logger = LoggerFactory.getLogger(Event.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final UUID id;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String message;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Date timestamp;
/*     */ 
/*     */ 
/*     */   
/*     */   private Level level;
/*     */ 
/*     */ 
/*     */   
/*     */   private String logger;
/*     */ 
/*     */ 
/*     */   
/*     */   private String platform;
/*     */ 
/*     */ 
/*     */   
/*     */   private Sdk sdk;
/*     */ 
/*     */ 
/*     */   
/*     */   private String culprit;
/*     */ 
/*     */ 
/*     */   
/*     */   private String transaction;
/*     */ 
/*     */ 
/*     */   
/*  78 */   private Map<String, String> tags = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*  82 */   private List<Breadcrumb> breadcrumbs = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*  86 */   private Map<String, Map<String, Object>> contexts = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String release;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String dist;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String environment;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String serverName;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   private transient Map<String, Object> extra = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<String> fingerprint;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String checksum;
/*     */ 
/*     */ 
/*     */   
/* 125 */   private Map<String, SentryInterface> sentryInterfaces = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Event(UUID id) {
/* 133 */     if (id == null) {
/* 134 */       throw new IllegalArgumentException("The id can't be null");
/*     */     }
/* 136 */     this.id = id;
/*     */   }
/*     */   
/*     */   public UUID getId() {
/* 140 */     return this.id;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 144 */     return this.message;
/*     */   }
/*     */   
/*     */   void setMessage(String message) {
/* 148 */     this.message = message;
/*     */   }
/*     */   
/*     */   public Date getTimestamp() {
/* 152 */     return (this.timestamp != null) ? (Date)this.timestamp.clone() : null;
/*     */   }
/*     */   
/*     */   void setTimestamp(Date timestamp) {
/* 156 */     this.timestamp = timestamp;
/*     */   }
/*     */   
/*     */   public Level getLevel() {
/* 160 */     return this.level;
/*     */   }
/*     */   
/*     */   void setLevel(Level level) {
/* 164 */     this.level = level;
/*     */   }
/*     */   
/*     */   public String getLogger() {
/* 168 */     return this.logger;
/*     */   }
/*     */   
/*     */   void setLogger(String logger) {
/* 172 */     this.logger = logger;
/*     */   }
/*     */   
/*     */   public String getPlatform() {
/* 176 */     return this.platform;
/*     */   }
/*     */   
/*     */   void setPlatform(String platform) {
/* 180 */     this.platform = platform;
/*     */   }
/*     */   
/*     */   public Sdk getSdk() {
/* 184 */     return this.sdk;
/*     */   }
/*     */   
/*     */   public void setSdk(Sdk sdk) {
/* 188 */     this.sdk = sdk;
/*     */   }
/*     */   
/*     */   public String getCulprit() {
/* 192 */     return this.culprit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   void setCulprit(String culprit) {
/* 203 */     this.culprit = culprit;
/*     */   }
/*     */   
/*     */   public String getTransaction() {
/* 207 */     return this.transaction;
/*     */   }
/*     */   
/*     */   void setTransaction(String transaction) {
/* 211 */     this.transaction = transaction;
/*     */   }
/*     */   
/*     */   public List<Breadcrumb> getBreadcrumbs() {
/* 215 */     return this.breadcrumbs;
/*     */   }
/*     */   
/*     */   void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
/* 219 */     this.breadcrumbs = breadcrumbs;
/*     */   }
/*     */   
/*     */   public Map<String, Map<String, Object>> getContexts() {
/* 223 */     return this.contexts;
/*     */   }
/*     */   
/*     */   public void setContexts(Map<String, Map<String, Object>> contexts) {
/* 227 */     this.contexts = contexts;
/*     */   }
/*     */   
/*     */   public Map<String, String> getTags() {
/* 231 */     return this.tags;
/*     */   }
/*     */   
/*     */   void setTags(Map<String, String> tags) {
/* 235 */     this.tags = tags;
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 239 */     return this.serverName;
/*     */   }
/*     */   
/*     */   void setServerName(String serverName) {
/* 243 */     this.serverName = serverName;
/*     */   }
/*     */   
/*     */   public String getRelease() {
/* 247 */     return this.release;
/*     */   }
/*     */   
/*     */   void setRelease(String release) {
/* 251 */     this.release = release;
/*     */   }
/*     */   
/*     */   public String getDist() {
/* 255 */     return this.dist;
/*     */   }
/*     */   
/*     */   public void setDist(String dist) {
/* 259 */     this.dist = dist;
/*     */   }
/*     */   
/*     */   public String getEnvironment() {
/* 263 */     return this.environment;
/*     */   }
/*     */   
/*     */   void setEnvironment(String environment) {
/* 267 */     this.environment = environment;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, Object> getExtra() {
/* 272 */     if (this.extra == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 277 */       this.extra = new HashMap<>();
/* 278 */       _logger.warn("`extra` field was null, deserialization must not have been called, please check your ProGuard (or other obfuscation) configuration.");
/*     */     } 
/*     */ 
/*     */     
/* 282 */     return this.extra;
/*     */   }
/*     */ 
/*     */   
/*     */   void setExtra(Map<String, Object> extra) {
/* 287 */     this.extra = extra;
/*     */   }
/*     */   
/*     */   public List<String> getFingerprint() {
/* 291 */     return this.fingerprint;
/*     */   }
/*     */   
/*     */   public void setFingerprint(List<String> fingerprint) {
/* 295 */     this.fingerprint = fingerprint;
/*     */   }
/*     */   
/*     */   public String getChecksum() {
/* 299 */     return this.checksum;
/*     */   }
/*     */   
/*     */   void setChecksum(String checksum) {
/* 303 */     this.checksum = checksum;
/*     */   }
/*     */   
/*     */   public Map<String, SentryInterface> getSentryInterfaces() {
/* 307 */     return this.sentryInterfaces;
/*     */   }
/*     */   
/*     */   void setSentryInterfaces(Map<String, SentryInterface> sentryInterfaces) {
/* 311 */     this.sentryInterfaces = sentryInterfaces;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 317 */     stream.defaultReadObject();
/* 318 */     this.extra = (Map<String, Object>)stream.readObject();
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream stream) throws IOException {
/* 323 */     stream.defaultWriteObject();
/* 324 */     stream.writeObject(convertToSerializable(this.extra));
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
/*     */   private static HashMap<String, ? super Serializable> convertToSerializable(Map<String, Object> objectMap) {
/* 338 */     HashMap<String, ? super Serializable> serializableMap = new HashMap<>(objectMap.size());
/* 339 */     for (Map.Entry<String, Object> objectEntry : objectMap.entrySet()) {
/* 340 */       if (objectEntry.getValue() == null) {
/* 341 */         serializableMap.put(objectEntry.getKey(), (String)null); continue;
/* 342 */       }  if (objectEntry.getValue() instanceof Serializable) {
/* 343 */         serializableMap.put(objectEntry.getKey(), (Serializable)objectEntry.getValue()); continue;
/*     */       } 
/* 345 */       serializableMap.put(objectEntry.getKey(), objectEntry.getValue().toString());
/*     */     } 
/*     */     
/* 348 */     return serializableMap;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 354 */     if (this == o) {
/* 355 */       return true;
/*     */     }
/* 357 */     if (o == null || getClass() != o.getClass()) {
/* 358 */       return false;
/*     */     }
/*     */     
/* 361 */     return this.id.equals(((Event)o).id);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 366 */     return this.id.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 371 */     return "Event{level=" + this.level + ", message='" + this.message + '\'' + ", logger='" + this.logger + '\'' + '}';
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
/*     */   public enum Level
/*     */   {
/* 385 */     FATAL,
/*     */ 
/*     */ 
/*     */     
/* 389 */     ERROR,
/*     */ 
/*     */ 
/*     */     
/* 393 */     WARNING,
/*     */ 
/*     */ 
/*     */     
/* 397 */     INFO,
/*     */ 
/*     */ 
/*     */     
/* 401 */     DEBUG;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\Event.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */