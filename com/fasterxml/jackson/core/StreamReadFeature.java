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
/*     */ public enum StreamReadFeature
/*     */ {
/*  28 */   AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE),
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
/*  46 */   STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION),
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
/*  68 */   IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED),
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
/*  89 */   INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
/*     */ 
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
/*     */   private final JsonParser.Feature _mappedFeature;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   StreamReadFeature(JsonParser.Feature mapTo) {
/* 108 */     this._mappedFeature = mapTo;
/* 109 */     this._mask = mapTo.getMask();
/* 110 */     this._defaultState = mapTo.enabledByDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int collectDefaults() {
/* 119 */     int flags = 0;
/* 120 */     for (StreamReadFeature f : values()) {
/* 121 */       if (f.enabledByDefault()) {
/* 122 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 125 */     return flags;
/*     */   }
/*     */   
/* 128 */   public boolean enabledByDefault() { return this._defaultState; }
/* 129 */   public boolean enabledIn(int flags) { return ((flags & this._mask) != 0); } public int getMask() {
/* 130 */     return this._mask;
/*     */   } public JsonParser.Feature mappedFeature() {
/* 132 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\StreamReadFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */