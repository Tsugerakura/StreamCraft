/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import com.google.gson.ExclusionStrategy;
/*     */ import com.google.gson.FieldAttributes;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.Expose;
/*     */ import com.google.gson.annotations.Since;
/*     */ import com.google.gson.annotations.Until;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Excluder
/*     */   implements TypeAdapterFactory, Cloneable
/*     */ {
/*     */   private static final double IGNORE_VERSIONS = -1.0D;
/*  52 */   public static final Excluder DEFAULT = new Excluder();
/*     */   
/*  54 */   private double version = -1.0D;
/*  55 */   private int modifiers = 136;
/*     */   private boolean serializeInnerClasses = true;
/*     */   private boolean requireExpose;
/*  58 */   private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
/*  59 */   private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();
/*     */   
/*     */   protected Excluder clone() {
/*     */     try {
/*  63 */       return (Excluder)super.clone();
/*  64 */     } catch (CloneNotSupportedException e) {
/*  65 */       throw new AssertionError(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Excluder withVersion(double ignoreVersionsAfter) {
/*  70 */     Excluder result = clone();
/*  71 */     result.version = ignoreVersionsAfter;
/*  72 */     return result;
/*     */   }
/*     */   
/*     */   public Excluder withModifiers(int... modifiers) {
/*  76 */     Excluder result = clone();
/*  77 */     result.modifiers = 0;
/*  78 */     for (int modifier : modifiers) {
/*  79 */       result.modifiers |= modifier;
/*     */     }
/*  81 */     return result;
/*     */   }
/*     */   
/*     */   public Excluder disableInnerClassSerialization() {
/*  85 */     Excluder result = clone();
/*  86 */     result.serializeInnerClasses = false;
/*  87 */     return result;
/*     */   }
/*     */   
/*     */   public Excluder excludeFieldsWithoutExposeAnnotation() {
/*  91 */     Excluder result = clone();
/*  92 */     result.requireExpose = true;
/*  93 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
/*  98 */     Excluder result = clone();
/*  99 */     if (serialization) {
/* 100 */       result.serializationStrategies = new ArrayList<ExclusionStrategy>(this.serializationStrategies);
/* 101 */       result.serializationStrategies.add(exclusionStrategy);
/*     */     } 
/* 103 */     if (deserialization) {
/* 104 */       result.deserializationStrategies = new ArrayList<ExclusionStrategy>(this.deserializationStrategies);
/*     */       
/* 106 */       result.deserializationStrategies.add(exclusionStrategy);
/*     */     } 
/* 108 */     return result;
/*     */   }
/*     */   
/*     */   public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
/* 112 */     Class<?> rawType = type.getRawType();
/* 113 */     boolean excludeClass = excludeClassChecks(rawType);
/*     */     
/* 115 */     final boolean skipSerialize = (excludeClass || excludeClassInStrategy(rawType, true));
/* 116 */     final boolean skipDeserialize = (excludeClass || excludeClassInStrategy(rawType, false));
/*     */     
/* 118 */     if (!skipSerialize && !skipDeserialize) {
/* 119 */       return null;
/*     */     }
/*     */     
/* 122 */     return new TypeAdapter<T>()
/*     */       {
/*     */         private TypeAdapter<T> delegate;
/*     */         
/*     */         public T read(JsonReader in) throws IOException {
/* 127 */           if (skipDeserialize) {
/* 128 */             in.skipValue();
/* 129 */             return null;
/*     */           } 
/* 131 */           return (T)delegate().read(in);
/*     */         }
/*     */         
/*     */         public void write(JsonWriter out, T value) throws IOException {
/* 135 */           if (skipSerialize) {
/* 136 */             out.nullValue();
/*     */             return;
/*     */           } 
/* 139 */           delegate().write(out, value);
/*     */         }
/*     */         
/*     */         private TypeAdapter<T> delegate() {
/* 143 */           TypeAdapter<T> d = this.delegate;
/* 144 */           return (d != null) ? d : (this
/*     */             
/* 146 */             .delegate = gson.getDelegateAdapter(Excluder.this, type));
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public boolean excludeField(Field field, boolean serialize) {
/* 152 */     if ((this.modifiers & field.getModifiers()) != 0) {
/* 153 */       return true;
/*     */     }
/*     */     
/* 156 */     if (this.version != -1.0D && 
/* 157 */       !isValidVersion(field.<Since>getAnnotation(Since.class), field.<Until>getAnnotation(Until.class))) {
/* 158 */       return true;
/*     */     }
/*     */     
/* 161 */     if (field.isSynthetic()) {
/* 162 */       return true;
/*     */     }
/*     */     
/* 165 */     if (this.requireExpose) {
/* 166 */       Expose annotation = field.<Expose>getAnnotation(Expose.class);
/* 167 */       if (annotation == null || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
/* 168 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 172 */     if (!this.serializeInnerClasses && isInnerClass(field.getType())) {
/* 173 */       return true;
/*     */     }
/*     */     
/* 176 */     if (isAnonymousOrLocal(field.getType())) {
/* 177 */       return true;
/*     */     }
/*     */     
/* 180 */     List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
/* 181 */     if (!list.isEmpty()) {
/* 182 */       FieldAttributes fieldAttributes = new FieldAttributes(field);
/* 183 */       for (ExclusionStrategy exclusionStrategy : list) {
/* 184 */         if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
/* 185 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     return false;
/*     */   }
/*     */   
/*     */   private boolean excludeClassChecks(Class<?> clazz) {
/* 194 */     if (this.version != -1.0D && !isValidVersion(clazz.<Since>getAnnotation(Since.class), clazz.<Until>getAnnotation(Until.class))) {
/* 195 */       return true;
/*     */     }
/*     */     
/* 198 */     if (!this.serializeInnerClasses && isInnerClass(clazz)) {
/* 199 */       return true;
/*     */     }
/*     */     
/* 202 */     if (isAnonymousOrLocal(clazz)) {
/* 203 */       return true;
/*     */     }
/*     */     
/* 206 */     return false;
/*     */   }
/*     */   
/*     */   public boolean excludeClass(Class<?> clazz, boolean serialize) {
/* 210 */     return (excludeClassChecks(clazz) || 
/* 211 */       excludeClassInStrategy(clazz, serialize));
/*     */   }
/*     */   
/*     */   private boolean excludeClassInStrategy(Class<?> clazz, boolean serialize) {
/* 215 */     List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
/* 216 */     for (ExclusionStrategy exclusionStrategy : list) {
/* 217 */       if (exclusionStrategy.shouldSkipClass(clazz)) {
/* 218 */         return true;
/*     */       }
/*     */     } 
/* 221 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isAnonymousOrLocal(Class<?> clazz) {
/* 225 */     return (!Enum.class.isAssignableFrom(clazz) && (clazz
/* 226 */       .isAnonymousClass() || clazz.isLocalClass()));
/*     */   }
/*     */   
/*     */   private boolean isInnerClass(Class<?> clazz) {
/* 230 */     return (clazz.isMemberClass() && !isStatic(clazz));
/*     */   }
/*     */   
/*     */   private boolean isStatic(Class<?> clazz) {
/* 234 */     return ((clazz.getModifiers() & 0x8) != 0);
/*     */   }
/*     */   
/*     */   private boolean isValidVersion(Since since, Until until) {
/* 238 */     return (isValidSince(since) && isValidUntil(until));
/*     */   }
/*     */   
/*     */   private boolean isValidSince(Since annotation) {
/* 242 */     if (annotation != null) {
/* 243 */       double annotationVersion = annotation.value();
/* 244 */       if (annotationVersion > this.version) {
/* 245 */         return false;
/*     */       }
/*     */     } 
/* 248 */     return true;
/*     */   }
/*     */   
/*     */   private boolean isValidUntil(Until annotation) {
/* 252 */     if (annotation != null) {
/* 253 */       double annotationVersion = annotation.value();
/* 254 */       if (annotationVersion <= this.version) {
/* 255 */         return false;
/*     */       }
/*     */     } 
/* 258 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\Excluder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */