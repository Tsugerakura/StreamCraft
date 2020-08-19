/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.FormatFeature;
/*     */ import com.fasterxml.jackson.core.JsonParser;
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
/*     */ public enum JsonReadFeature
/*     */   implements FormatFeature
/*     */ {
/*  28 */   ALLOW_JAVA_COMMENTS(false, JsonParser.Feature.ALLOW_COMMENTS),
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
/*  42 */   ALLOW_YAML_COMMENTS(false, JsonParser.Feature.ALLOW_YAML_COMMENTS),
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
/*  57 */   ALLOW_SINGLE_QUOTES(false, JsonParser.Feature.ALLOW_SINGLE_QUOTES),
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
/*  68 */   ALLOW_UNQUOTED_FIELD_NAMES(false, JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES),
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
/*  81 */   ALLOW_UNESCAPED_CONTROL_CHARS(false, JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS),
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
/*  93 */   ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER(false, JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER),
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
/* 108 */   ALLOW_LEADING_ZEROS_FOR_NUMBERS(false, JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS),
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
/* 129 */   ALLOW_NON_NUMERIC_NUMBERS(false, JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS),
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
/* 148 */   ALLOW_MISSING_VALUES(false, JsonParser.Feature.ALLOW_MISSING_VALUES),
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
/* 170 */   ALLOW_TRAILING_COMMA(false, JsonParser.Feature.ALLOW_TRAILING_COMMA);
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
/*     */   public static int collectDefaults() {
/* 189 */     int flags = 0;
/* 190 */     for (JsonReadFeature f : values()) {
/* 191 */       if (f.enabledByDefault()) {
/* 192 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 195 */     return flags;
/*     */   }
/*     */ 
/*     */   
/*     */   JsonReadFeature(boolean defaultState, JsonParser.Feature mapTo) {
/* 200 */     this._defaultState = defaultState;
/* 201 */     this._mask = 1 << ordinal();
/* 202 */     this._mappedFeature = mapTo;
/*     */   }
/*     */   
/*     */   public boolean enabledByDefault() {
/* 206 */     return this._defaultState;
/*     */   } public int getMask() {
/* 208 */     return this._mask;
/*     */   } public boolean enabledIn(int flags) {
/* 210 */     return ((flags & this._mask) != 0);
/*     */   } public JsonParser.Feature mappedFeature() {
/* 212 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\JsonReadFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */