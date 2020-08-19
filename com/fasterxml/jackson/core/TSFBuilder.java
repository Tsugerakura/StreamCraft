/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.InputDecorator;
/*     */ import com.fasterxml.jackson.core.io.OutputDecorator;
/*     */ import com.fasterxml.jackson.core.json.JsonReadFeature;
/*     */ import com.fasterxml.jackson.core.json.JsonWriteFeature;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>>
/*     */ {
/*  26 */   protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = JsonFactory.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _factoryFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _streamReadFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _streamWriteFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InputDecorator _inputDecorator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OutputDecorator _outputDecorator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TSFBuilder() {
/*  86 */     this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*  87 */     this._streamReadFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*  88 */     this._streamWriteFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*  89 */     this._inputDecorator = null;
/*  90 */     this._outputDecorator = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected TSFBuilder(JsonFactory base) {
/*  95 */     this(base._factoryFeatures, base._parserFeatures, base._generatorFeatures);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TSFBuilder(int factoryFeatures, int parserFeatures, int generatorFeatures) {
/* 102 */     this._factoryFeatures = factoryFeatures;
/* 103 */     this._streamReadFeatures = parserFeatures;
/* 104 */     this._streamWriteFeatures = generatorFeatures;
/*     */   }
/*     */ 
/*     */   
/*     */   public int factoryFeaturesMask() {
/* 109 */     return this._factoryFeatures; }
/* 110 */   public int streamReadFeatures() { return this._streamReadFeatures; } public int streamWriteFeatures() {
/* 111 */     return this._streamWriteFeatures;
/*     */   }
/* 113 */   public InputDecorator inputDecorator() { return this._inputDecorator; } public OutputDecorator outputDecorator() {
/* 114 */     return this._outputDecorator;
/*     */   }
/*     */ 
/*     */   
/*     */   public B enable(JsonFactory.Feature f) {
/* 119 */     this._factoryFeatures |= f.getMask();
/* 120 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(JsonFactory.Feature f) {
/* 124 */     this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 125 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(JsonFactory.Feature f, boolean state) {
/* 129 */     return state ? enable(f) : disable(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(StreamReadFeature f) {
/* 135 */     this._streamReadFeatures |= f.mappedFeature().getMask();
/* 136 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(StreamReadFeature first, StreamReadFeature... other) {
/* 140 */     this._streamReadFeatures |= first.mappedFeature().getMask();
/* 141 */     for (StreamReadFeature f : other) {
/* 142 */       this._streamReadFeatures |= f.mappedFeature().getMask();
/*     */     }
/* 144 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamReadFeature f) {
/* 148 */     this._streamReadFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 149 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamReadFeature first, StreamReadFeature... other) {
/* 153 */     this._streamReadFeatures &= first.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 154 */     for (StreamReadFeature f : other) {
/* 155 */       this._streamReadFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 157 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamReadFeature f, boolean state) {
/* 161 */     return state ? enable(f) : disable(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(StreamWriteFeature f) {
/* 167 */     this._streamWriteFeatures |= f.mappedFeature().getMask();
/* 168 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
/* 172 */     this._streamWriteFeatures |= first.mappedFeature().getMask();
/* 173 */     for (StreamWriteFeature f : other) {
/* 174 */       this._streamWriteFeatures |= f.mappedFeature().getMask();
/*     */     }
/* 176 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamWriteFeature f) {
/* 180 */     this._streamWriteFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 181 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
/* 185 */     this._streamWriteFeatures &= first.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 186 */     for (StreamWriteFeature f : other) {
/* 187 */       this._streamWriteFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 189 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamWriteFeature f, boolean state) {
/* 193 */     return state ? enable(f) : disable(f);
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
/*     */   public B enable(JsonReadFeature f) {
/* 206 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B enable(JsonReadFeature first, JsonReadFeature... other) {
/* 210 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B disable(JsonReadFeature f) {
/* 214 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B disable(JsonReadFeature first, JsonReadFeature... other) {
/* 218 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B configure(JsonReadFeature f, boolean state) {
/* 222 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   private B _failNonJSON(Object feature) {
/* 226 */     throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature
/* 227 */         .toString() + " not supported for non-JSON backend");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(JsonWriteFeature f) {
/* 233 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B enable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 237 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B disable(JsonWriteFeature f) {
/* 241 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B disable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 245 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B configure(JsonWriteFeature f, boolean state) {
/* 249 */     return _failNonJSON(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B inputDecorator(InputDecorator dec) {
/* 255 */     this._inputDecorator = dec;
/* 256 */     return _this();
/*     */   }
/*     */   
/*     */   public B outputDecorator(OutputDecorator dec) {
/* 260 */     this._outputDecorator = dec;
/* 261 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract F build();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final B _this() {
/* 274 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _legacyEnable(JsonParser.Feature f) {
/* 279 */     this._streamReadFeatures |= f.getMask();
/*     */   }
/*     */   protected void _legacyDisable(JsonParser.Feature f) {
/* 282 */     this._streamReadFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*     */   }
/*     */   
/*     */   protected void _legacyEnable(JsonGenerator.Feature f) {
/* 286 */     this._streamWriteFeatures |= f.getMask();
/*     */   }
/*     */   protected void _legacyDisable(JsonGenerator.Feature f) {
/* 289 */     this._streamWriteFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\TSFBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */