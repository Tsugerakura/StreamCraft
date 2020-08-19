/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.;
/*     */ import com.google.gson.internal.Excluder;
/*     */ import com.google.gson.internal.bind.TreeTypeAdapter;
/*     */ import com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Type;
/*     */ import java.sql.Date;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public final class GsonBuilder
/*     */ {
/*  79 */   private Excluder excluder = Excluder.DEFAULT;
/*  80 */   private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
/*  81 */   private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
/*  82 */   private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<Type, InstanceCreator<?>>();
/*     */   
/*  84 */   private final List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
/*     */   
/*  86 */   private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<TypeAdapterFactory>();
/*     */   private boolean serializeNulls = false;
/*     */   private String datePattern;
/*  89 */   private int dateStyle = 2;
/*  90 */   private int timeStyle = 2;
/*     */ 
/*     */   
/*     */   private boolean complexMapKeySerialization = false;
/*     */ 
/*     */   
/*     */   private boolean serializeSpecialFloatingPointValues = false;
/*     */ 
/*     */   
/*     */   private boolean escapeHtmlChars = true;
/*     */ 
/*     */   
/*     */   private boolean prettyPrinting = false;
/*     */ 
/*     */   
/*     */   private boolean generateNonExecutableJson = false;
/*     */   
/*     */   private boolean lenient = false;
/*     */ 
/*     */   
/*     */   public GsonBuilder() {}
/*     */ 
/*     */   
/*     */   GsonBuilder(Gson gson) {
/* 114 */     this.excluder = gson.excluder;
/* 115 */     this.fieldNamingPolicy = gson.fieldNamingStrategy;
/* 116 */     this.instanceCreators.putAll(gson.instanceCreators);
/* 117 */     this.serializeNulls = gson.serializeNulls;
/* 118 */     this.complexMapKeySerialization = gson.complexMapKeySerialization;
/* 119 */     this.generateNonExecutableJson = gson.generateNonExecutableJson;
/* 120 */     this.escapeHtmlChars = gson.htmlSafe;
/* 121 */     this.prettyPrinting = gson.prettyPrinting;
/* 122 */     this.lenient = gson.lenient;
/* 123 */     this.serializeSpecialFloatingPointValues = gson.serializeSpecialFloatingPointValues;
/* 124 */     this.longSerializationPolicy = gson.longSerializationPolicy;
/* 125 */     this.datePattern = gson.datePattern;
/* 126 */     this.dateStyle = gson.dateStyle;
/* 127 */     this.timeStyle = gson.timeStyle;
/* 128 */     this.factories.addAll(gson.builderFactories);
/* 129 */     this.hierarchyFactories.addAll(gson.builderHierarchyFactories);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder setVersion(double ignoreVersionsAfter) {
/* 140 */     this.excluder = this.excluder.withVersion(ignoreVersionsAfter);
/* 141 */     return this;
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
/*     */   public GsonBuilder excludeFieldsWithModifiers(int... modifiers) {
/* 156 */     this.excluder = this.excluder.withModifiers(modifiers);
/* 157 */     return this;
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
/*     */   public GsonBuilder generateNonExecutableJson() {
/* 170 */     this.generateNonExecutableJson = true;
/* 171 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
/* 181 */     this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
/* 182 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder serializeNulls() {
/* 193 */     this.serializeNulls = true;
/* 194 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder enableComplexMapKeySerialization() {
/* 274 */     this.complexMapKeySerialization = true;
/* 275 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder disableInnerClassSerialization() {
/* 285 */     this.excluder = this.excluder.disableInnerClassSerialization();
/* 286 */     return this;
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
/*     */   public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
/* 298 */     this.longSerializationPolicy = serializationPolicy;
/* 299 */     return this;
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
/*     */   public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
/* 311 */     this.fieldNamingPolicy = namingConvention;
/* 312 */     return this;
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
/*     */   public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
/* 324 */     this.fieldNamingPolicy = fieldNamingStrategy;
/* 325 */     return this;
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
/*     */   public GsonBuilder setExclusionStrategies(ExclusionStrategy... strategies) {
/* 339 */     for (ExclusionStrategy strategy : strategies) {
/* 340 */       this.excluder = this.excluder.withExclusionStrategy(strategy, true, true);
/*     */     }
/* 342 */     return this;
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
/*     */   public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
/* 358 */     this.excluder = this.excluder.withExclusionStrategy(strategy, true, false);
/* 359 */     return this;
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
/*     */   public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
/* 375 */     this.excluder = this.excluder.withExclusionStrategy(strategy, false, true);
/* 376 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder setPrettyPrinting() {
/* 386 */     this.prettyPrinting = true;
/* 387 */     return this;
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
/*     */   public GsonBuilder setLenient() {
/* 399 */     this.lenient = true;
/* 400 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder disableHtmlEscaping() {
/* 411 */     this.escapeHtmlChars = false;
/* 412 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder setDateFormat(String pattern) {
/* 433 */     this.datePattern = pattern;
/* 434 */     return this;
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
/*     */ 
/*     */   
/*     */   public GsonBuilder setDateFormat(int style) {
/* 452 */     this.dateStyle = style;
/* 453 */     this.datePattern = null;
/* 454 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder setDateFormat(int dateStyle, int timeStyle) {
/* 473 */     this.dateStyle = dateStyle;
/* 474 */     this.timeStyle = timeStyle;
/* 475 */     this.datePattern = null;
/* 476 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
/* 497 */     .Gson.Preconditions.checkArgument((typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof InstanceCreator || typeAdapter instanceof TypeAdapter));
/*     */ 
/*     */ 
/*     */     
/* 501 */     if (typeAdapter instanceof InstanceCreator) {
/* 502 */       this.instanceCreators.put(type, (InstanceCreator)typeAdapter);
/*     */     }
/* 504 */     if (typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer) {
/* 505 */       TypeToken<?> typeToken = TypeToken.get(type);
/* 506 */       this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, typeAdapter));
/*     */     } 
/* 508 */     if (typeAdapter instanceof TypeAdapter) {
/* 509 */       this.factories.add(TypeAdapters.newFactory(TypeToken.get(type), (TypeAdapter)typeAdapter));
/*     */     }
/* 511 */     return this;
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
/*     */   public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
/* 523 */     this.factories.add(factory);
/* 524 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
/* 543 */     .Gson.Preconditions.checkArgument((typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof TypeAdapter));
/*     */ 
/*     */     
/* 546 */     if (typeAdapter instanceof JsonDeserializer || typeAdapter instanceof JsonSerializer) {
/* 547 */       this.hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(baseType, typeAdapter));
/*     */     }
/* 549 */     if (typeAdapter instanceof TypeAdapter) {
/* 550 */       this.factories.add(TypeAdapters.newTypeHierarchyFactory(baseType, (TypeAdapter)typeAdapter));
/*     */     }
/* 552 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GsonBuilder serializeSpecialFloatingPointValues() {
/* 576 */     this.serializeSpecialFloatingPointValues = true;
/* 577 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Gson create() {
/* 587 */     List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>(this.factories.size() + this.hierarchyFactories.size() + 3);
/* 588 */     factories.addAll(this.factories);
/* 589 */     Collections.reverse(factories);
/*     */     
/* 591 */     List<TypeAdapterFactory> hierarchyFactories = new ArrayList<TypeAdapterFactory>(this.hierarchyFactories);
/* 592 */     Collections.reverse(hierarchyFactories);
/* 593 */     factories.addAll(hierarchyFactories);
/*     */     
/* 595 */     addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, factories);
/*     */     
/* 597 */     return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.prettyPrinting, this.lenient, this.serializeSpecialFloatingPointValues, this.longSerializationPolicy, this.datePattern, this.dateStyle, this.timeStyle, this.factories, this.hierarchyFactories, factories);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle, List<TypeAdapterFactory> factories) {
/*     */     DefaultDateTypeAdapter dateTypeAdapter;
/*     */     TypeAdapter<Timestamp> timestampTypeAdapter;
/*     */     TypeAdapter<Date> javaSqlDateTypeAdapter;
/* 611 */     if (datePattern != null && !"".equals(datePattern.trim())) {
/* 612 */       dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, datePattern);
/* 613 */       timestampTypeAdapter = new DefaultDateTypeAdapter((Class)Timestamp.class, datePattern);
/* 614 */       javaSqlDateTypeAdapter = new DefaultDateTypeAdapter((Class)Date.class, datePattern);
/* 615 */     } else if (dateStyle != 2 && timeStyle != 2) {
/* 616 */       dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle, timeStyle);
/* 617 */       timestampTypeAdapter = new DefaultDateTypeAdapter((Class)Timestamp.class, dateStyle, timeStyle);
/* 618 */       javaSqlDateTypeAdapter = new DefaultDateTypeAdapter((Class)Date.class, dateStyle, timeStyle);
/*     */     } else {
/*     */       return;
/*     */     } 
/*     */     
/* 623 */     factories.add(TypeAdapters.newFactory(Date.class, dateTypeAdapter));
/* 624 */     factories.add(TypeAdapters.newFactory(Timestamp.class, timestampTypeAdapter));
/* 625 */     factories.add(TypeAdapters.newFactory(Date.class, javaSqlDateTypeAdapter));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\GsonBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */