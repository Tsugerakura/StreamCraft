/*     */ package io.sentry.context;
/*     */ 
/*     */ import io.sentry.event.Breadcrumb;
/*     */ import io.sentry.event.User;
/*     */ import io.sentry.event.interfaces.HttpInterface;
/*     */ import io.sentry.util.CircularFifoQueue;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Context
/*     */   implements Serializable
/*     */ {
/*     */   private static final int DEFAULT_BREADCRUMB_LIMIT = 100;
/*     */   private final int breadcrumbLimit;
/*     */   private volatile UUID lastEventId;
/*     */   private volatile CircularFifoQueue<Breadcrumb> breadcrumbs;
/*     */   private volatile User user;
/*     */   private volatile Map<String, String> tags;
/*     */   private volatile Map<String, Object> extra;
/*     */   private volatile HttpInterface http;
/*     */   
/*     */   public Context() {
/*  56 */     this(100);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Context(int breadcrumbLimit) {
/*  65 */     this.breadcrumbLimit = breadcrumbLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clear() {
/*  72 */     setLastEventId(null);
/*  73 */     clearBreadcrumbs();
/*  74 */     clearUser();
/*  75 */     clearTags();
/*  76 */     clearExtra();
/*  77 */     clearHttp();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized List<Breadcrumb> getBreadcrumbs() {
/*  86 */     if (this.breadcrumbs == null || this.breadcrumbs.isEmpty()) {
/*  87 */       return Collections.emptyList();
/*     */     }
/*     */     
/*  90 */     List<Breadcrumb> copyList = new ArrayList<>(this.breadcrumbs.size());
/*  91 */     copyList.addAll((Collection<? extends Breadcrumb>)this.breadcrumbs);
/*  92 */     return copyList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Map<String, String> getTags() {
/* 101 */     if (this.tags == null || this.tags.isEmpty()) {
/* 102 */       return Collections.emptyMap();
/*     */     }
/*     */     
/* 105 */     return new HashMap<>(this.tags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Map<String, Object> getExtra() {
/* 114 */     if (this.extra == null || this.extra.isEmpty()) {
/* 115 */       return Collections.emptyMap();
/*     */     }
/*     */     
/* 118 */     return new HashMap<>(this.extra);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addTag(String name, String value) {
/* 128 */     if (this.tags == null) {
/* 129 */       this.tags = new HashMap<>();
/*     */     }
/*     */     
/* 132 */     this.tags.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeTag(String name) {
/* 141 */     if (this.tags == null) {
/*     */       return;
/*     */     }
/*     */     
/* 145 */     this.tags.remove(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clearTags() {
/* 152 */     this.tags = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addExtra(String name, Object value) {
/* 162 */     if (this.extra == null) {
/* 163 */       this.extra = new HashMap<>();
/*     */     }
/*     */     
/* 166 */     this.extra.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeExtra(String name) {
/* 175 */     if (this.extra == null) {
/*     */       return;
/*     */     }
/*     */     
/* 179 */     this.extra.remove(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clearExtra() {
/* 186 */     this.extra = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setHttp(HttpInterface http) {
/* 196 */     this.http = http;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized HttpInterface getHttp() {
/* 205 */     return this.http;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clearHttp() {
/* 212 */     this.http = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void recordBreadcrumb(Breadcrumb breadcrumb) {
/* 221 */     if (this.breadcrumbs == null) {
/* 222 */       this.breadcrumbs = new CircularFifoQueue(this.breadcrumbLimit);
/*     */     }
/*     */     
/* 225 */     this.breadcrumbs.add(breadcrumb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void clearBreadcrumbs() {
/* 232 */     this.breadcrumbs = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastEventId(UUID id) {
/* 241 */     this.lastEventId = id;
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
/*     */   public UUID getLastEventId() {
/* 254 */     return this.lastEventId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUser(User user) {
/* 263 */     this.user = user;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearUser() {
/* 270 */     setUser(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public User getUser() {
/* 279 */     return this.user;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\context\Context.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */