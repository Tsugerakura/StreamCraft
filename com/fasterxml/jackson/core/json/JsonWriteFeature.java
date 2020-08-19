/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.FormatFeature;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
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
/*     */ public enum JsonWriteFeature
/*     */   implements FormatFeature
/*     */ {
/*  26 */   QUOTE_FIELD_NAMES(true, JsonGenerator.Feature.QUOTE_FIELD_NAMES),
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
/*  41 */   WRITE_NAN_AS_STRINGS(true, JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS),
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
/*  60 */   WRITE_NUMBERS_AS_STRINGS(false, JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS),
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
/*  78 */   ESCAPE_NON_ASCII(false, JsonGenerator.Feature.ESCAPE_NON_ASCII);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean _defaultState;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _mask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final JsonGenerator.Feature _mappedFeature;
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
/*     */   public static int collectDefaults() {
/* 120 */     int flags = 0;
/* 121 */     for (JsonWriteFeature f : values()) {
/* 122 */       if (f.enabledByDefault()) {
/* 123 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 126 */     return flags;
/*     */   }
/*     */ 
/*     */   
/*     */   JsonWriteFeature(boolean defaultState, JsonGenerator.Feature mapTo) {
/* 131 */     this._defaultState = defaultState;
/* 132 */     this._mask = 1 << ordinal();
/* 133 */     this._mappedFeature = mapTo;
/*     */   }
/*     */   
/*     */   public boolean enabledByDefault() {
/* 137 */     return this._defaultState;
/*     */   } public int getMask() {
/* 139 */     return this._mask;
/*     */   } public boolean enabledIn(int flags) {
/* 141 */     return ((flags & this._mask) != 0);
/*     */   } public JsonGenerator.Feature mappedFeature() {
/* 143 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\JsonWriteFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */