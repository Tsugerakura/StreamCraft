/*     */ package io.sentry.event;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class BreadcrumbBuilder
/*     */ {
/*     */   private Breadcrumb.Type type;
/*     */   private Date timestamp;
/*     */   private Breadcrumb.Level level;
/*     */   private String message;
/*     */   private String category;
/*     */   private Map<String, String> data;
/*     */   
/*     */   public BreadcrumbBuilder setType(Breadcrumb.Type newType) {
/*  26 */     this.type = newType;
/*  27 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder setTimestamp(Date newTimestamp) {
/*  37 */     this.timestamp = new Date(newTimestamp.getTime());
/*  38 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder setLevel(Breadcrumb.Level newLevel) {
/*  48 */     this.level = newLevel;
/*  49 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder setMessage(String newMessage) {
/*  60 */     this.message = newMessage;
/*  61 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder setCategory(String newCategory) {
/*  71 */     this.category = newCategory;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder setData(Map<String, String> newData) {
/*  83 */     this.data = newData;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BreadcrumbBuilder withData(String name, String value) {
/*  95 */     if (this.data == null) {
/*  96 */       this.data = new HashMap<>();
/*     */     }
/*     */     
/*  99 */     this.data.put(name, value);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Breadcrumb build() {
/* 109 */     return new Breadcrumb(this.type, this.timestamp, this.level, this.message, this.category, this.data);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\BreadcrumbBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */