/*     */ package com.fasterxml.jackson.core;
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
/*     */ public enum StreamWriteFeature
/*     */ {
/*  30 */   AUTO_CLOSE_TARGET(JsonGenerator.Feature.AUTO_CLOSE_TARGET),
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
/*  43 */   AUTO_CLOSE_CONTENT(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT),
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
/*  56 */   FLUSH_PASSED_TO_STREAM(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM),
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
/*  72 */   WRITE_BIGDECIMAL_AS_PLAIN(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN),
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
/*  89 */   STRICT_DUPLICATE_DETECTION(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION),
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
/* 109 */   IGNORE_UNKNOWN(JsonGenerator.Feature.IGNORE_UNKNOWN);
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean _defaultState;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _mask;
/*     */ 
/*     */ 
/*     */   
/*     */   private final JsonGenerator.Feature _mappedFeature;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   StreamWriteFeature(JsonGenerator.Feature mappedTo) {
/* 127 */     this._mappedFeature = mappedTo;
/* 128 */     this._mask = mappedTo.getMask();
/* 129 */     this._defaultState = mappedTo.enabledByDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int collectDefaults() {
/* 138 */     int flags = 0;
/* 139 */     for (StreamWriteFeature f : values()) {
/* 140 */       if (f.enabledByDefault()) {
/* 141 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 144 */     return flags;
/*     */   }
/*     */   
/* 147 */   public boolean enabledByDefault() { return this._defaultState; }
/* 148 */   public boolean enabledIn(int flags) { return ((flags & this._mask) != 0); } public int getMask() {
/* 149 */     return this._mask;
/*     */   } public JsonGenerator.Feature mappedFeature() {
/* 151 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\StreamWriteFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */