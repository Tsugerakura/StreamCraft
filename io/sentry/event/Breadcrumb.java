/*     */ package io.sentry.event;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class Breadcrumb
/*     */   implements Serializable
/*     */ {
/*     */   private final Type type;
/*     */   private final Date timestamp;
/*     */   private final Level level;
/*     */   private final String message;
/*     */   private final String category;
/*     */   private final Map<String, String> data;
/*     */   
/*     */   public enum Level
/*     */   {
/*  47 */     DEBUG("debug"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  52 */     INFO("info"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     WARNING("warning"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  62 */     ERROR("error"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     CRITICAL("critical");
/*     */ 
/*     */ 
/*     */     
/*     */     private final String value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Level(String value) {
/*  77 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getValue() {
/*  81 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Type
/*     */   {
/*  91 */     DEFAULT("default"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     HTTP("http"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     NAVIGATION("navigation"),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     USER("user");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final String value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Type(String value) {
/* 117 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 121 */       return this.value;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Breadcrumb(Type type, Date timestamp, Level level, String message, String category, Map<String, String> data) {
/* 138 */     if (timestamp == null) {
/* 139 */       timestamp = new Date();
/*     */     }
/*     */     
/* 142 */     if (message == null && (data == null || data.size() < 1)) {
/* 143 */       throw new IllegalArgumentException("one of 'message' or 'data' must be set");
/*     */     }
/*     */     
/* 146 */     this.type = type;
/* 147 */     this.timestamp = timestamp;
/* 148 */     this.level = level;
/* 149 */     this.message = message;
/* 150 */     this.category = category;
/* 151 */     this.data = data;
/*     */   }
/*     */   
/*     */   public Type getType() {
/* 155 */     return this.type;
/*     */   }
/*     */   
/*     */   public Date getTimestamp() {
/* 159 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   public Level getLevel() {
/* 163 */     return this.level;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 167 */     return this.message;
/*     */   }
/*     */   
/*     */   public String getCategory() {
/* 171 */     return this.category;
/*     */   }
/*     */   
/*     */   public Map<String, String> getData() {
/* 175 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 180 */     if (this == o) {
/* 181 */       return true;
/*     */     }
/* 183 */     if (o == null || getClass() != o.getClass()) {
/* 184 */       return false;
/*     */     }
/* 186 */     Breadcrumb that = (Breadcrumb)o;
/* 187 */     return (this.type == that.type && 
/* 188 */       Objects.equals(this.timestamp, that.timestamp) && this.level == that.level && 
/*     */       
/* 190 */       Objects.equals(this.message, that.message) && 
/* 191 */       Objects.equals(this.category, that.category) && 
/* 192 */       Objects.equals(this.data, that.data));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 197 */     return Objects.hash(new Object[] { this.type, this.timestamp, this.level, this.message, this.category, this.data });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\Breadcrumb.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */