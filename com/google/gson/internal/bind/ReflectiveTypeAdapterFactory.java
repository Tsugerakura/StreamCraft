/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.FieldNamingStrategy;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.JsonAdapter;
/*     */ import com.google.gson.annotations.SerializedName;
/*     */ import com.google.gson.internal.;
/*     */ import com.google.gson.internal.ConstructorConstructor;
/*     */ import com.google.gson.internal.Excluder;
/*     */ import com.google.gson.internal.ObjectConstructor;
/*     */ import com.google.gson.internal.Primitives;
/*     */ import com.google.gson.internal.reflect.ReflectionAccessor;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public final class ReflectiveTypeAdapterFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final FieldNamingStrategy fieldNamingPolicy;
/*     */   private final Excluder excluder;
/*     */   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
/*  53 */   private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
/*     */ 
/*     */ 
/*     */   
/*     */   public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
/*  58 */     this.constructorConstructor = constructorConstructor;
/*  59 */     this.fieldNamingPolicy = fieldNamingPolicy;
/*  60 */     this.excluder = excluder;
/*  61 */     this.jsonAdapterFactory = jsonAdapterFactory;
/*     */   }
/*     */   
/*     */   public boolean excludeField(Field f, boolean serialize) {
/*  65 */     return excludeField(f, serialize, this.excluder);
/*     */   }
/*     */   
/*     */   static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
/*  69 */     return (!excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize));
/*     */   }
/*     */ 
/*     */   
/*     */   private List<String> getFieldNames(Field f) {
/*  74 */     SerializedName annotation = f.<SerializedName>getAnnotation(SerializedName.class);
/*  75 */     if (annotation == null) {
/*  76 */       String name = this.fieldNamingPolicy.translateName(f);
/*  77 */       return Collections.singletonList(name);
/*     */     } 
/*     */     
/*  80 */     String serializedName = annotation.value();
/*  81 */     String[] alternates = annotation.alternate();
/*  82 */     if (alternates.length == 0) {
/*  83 */       return Collections.singletonList(serializedName);
/*     */     }
/*     */     
/*  86 */     List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
/*  87 */     fieldNames.add(serializedName);
/*  88 */     for (String alternate : alternates) {
/*  89 */       fieldNames.add(alternate);
/*     */     }
/*  91 */     return fieldNames;
/*     */   }
/*     */   
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/*  95 */     Class<? super T> raw = type.getRawType();
/*     */     
/*  97 */     if (!Object.class.isAssignableFrom(raw)) {
/*  98 */       return null;
/*     */     }
/*     */     
/* 101 */     ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
/* 102 */     return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private BoundField createBoundField(final Gson context, final Field field, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
/* 108 */     final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
/*     */     
/* 110 */     JsonAdapter annotation = field.<JsonAdapter>getAnnotation(JsonAdapter.class);
/* 111 */     TypeAdapter<?> mapped = null;
/* 112 */     if (annotation != null) {
/* 113 */       mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation);
/*     */     }
/*     */     
/* 116 */     final boolean jsonAdapterPresent = (mapped != null);
/* 117 */     if (mapped == null) mapped = context.getAdapter(fieldType);
/*     */     
/* 119 */     final TypeAdapter<?> typeAdapter = mapped;
/* 120 */     return new BoundField(name, serialize, deserialize)
/*     */       {
/*     */         void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException
/*     */         {
/* 124 */           Object fieldValue = field.get(value);
/*     */           
/* 126 */           TypeAdapter t = jsonAdapterPresent ? typeAdapter : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
/* 127 */           t.write(writer, fieldValue);
/*     */         }
/*     */         
/*     */         void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
/* 131 */           Object fieldValue = typeAdapter.read(reader);
/* 132 */           if (fieldValue != null || !isPrimitive)
/* 133 */             field.set(value, fieldValue); 
/*     */         }
/*     */         
/*     */         public boolean writeField(Object value) throws IOException, IllegalAccessException {
/* 137 */           if (!this.serialized) return false; 
/* 138 */           Object fieldValue = field.get(value);
/* 139 */           return (fieldValue != value);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
/* 145 */     Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
/* 146 */     if (raw.isInterface()) {
/* 147 */       return result;
/*     */     }
/*     */     
/* 150 */     Type declaredType = type.getType();
/* 151 */     while (raw != Object.class) {
/* 152 */       Field[] fields = raw.getDeclaredFields();
/* 153 */       for (Field field : fields) {
/* 154 */         boolean serialize = excludeField(field, true);
/* 155 */         boolean deserialize = excludeField(field, false);
/* 156 */         if (serialize || deserialize) {
/*     */ 
/*     */           
/* 159 */           this.accessor.makeAccessible(field);
/* 160 */           Type fieldType = .Gson.Types.resolve(type.getType(), raw, field.getGenericType());
/* 161 */           List<String> fieldNames = getFieldNames(field);
/* 162 */           BoundField previous = null;
/* 163 */           for (int i = 0, size = fieldNames.size(); i < size; i++) {
/* 164 */             String name = fieldNames.get(i);
/* 165 */             if (i != 0) serialize = false; 
/* 166 */             BoundField boundField = createBoundField(context, field, name, 
/* 167 */                 TypeToken.get(fieldType), serialize, deserialize);
/* 168 */             BoundField replaced = result.put(name, boundField);
/* 169 */             if (previous == null) previous = replaced; 
/*     */           } 
/* 171 */           if (previous != null) {
/* 172 */             throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
/*     */           }
/*     */         } 
/*     */       } 
/* 176 */       type = TypeToken.get(.Gson.Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
/* 177 */       raw = type.getRawType();
/*     */     } 
/* 179 */     return result;
/*     */   }
/*     */   
/*     */   static abstract class BoundField {
/*     */     final String name;
/*     */     final boolean serialized;
/*     */     final boolean deserialized;
/*     */     
/*     */     protected BoundField(String name, boolean serialized, boolean deserialized) {
/* 188 */       this.name = name;
/* 189 */       this.serialized = serialized;
/* 190 */       this.deserialized = deserialized;
/*     */     }
/*     */     
/*     */     abstract boolean writeField(Object param1Object) throws IOException, IllegalAccessException;
/*     */     
/*     */     abstract void write(JsonWriter param1JsonWriter, Object param1Object) throws IOException, IllegalAccessException;
/*     */     
/*     */     abstract void read(JsonReader param1JsonReader, Object param1Object) throws IOException, IllegalAccessException; }
/*     */   
/*     */   public static final class Adapter<T> extends TypeAdapter<T> { private final ObjectConstructor<T> constructor;
/*     */     
/*     */     Adapter(ObjectConstructor<T> constructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
/* 202 */       this.constructor = constructor;
/* 203 */       this.boundFields = boundFields;
/*     */     }
/*     */     private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
/*     */     public T read(JsonReader in) throws IOException {
/* 207 */       if (in.peek() == JsonToken.NULL) {
/* 208 */         in.nextNull();
/* 209 */         return null;
/*     */       } 
/*     */       
/* 212 */       T instance = (T)this.constructor.construct();
/*     */       
/*     */       try {
/* 215 */         in.beginObject();
/* 216 */         while (in.hasNext()) {
/* 217 */           String name = in.nextName();
/* 218 */           ReflectiveTypeAdapterFactory.BoundField field = this.boundFields.get(name);
/* 219 */           if (field == null || !field.deserialized) {
/* 220 */             in.skipValue(); continue;
/*     */           } 
/* 222 */           field.read(in, instance);
/*     */         }
/*     */       
/* 225 */       } catch (IllegalStateException e) {
/* 226 */         throw new JsonSyntaxException(e);
/* 227 */       } catch (IllegalAccessException e) {
/* 228 */         throw new AssertionError(e);
/*     */       } 
/* 230 */       in.endObject();
/* 231 */       return instance;
/*     */     }
/*     */     
/*     */     public void write(JsonWriter out, T value) throws IOException {
/* 235 */       if (value == null) {
/* 236 */         out.nullValue();
/*     */         
/*     */         return;
/*     */       } 
/* 240 */       out.beginObject();
/*     */       try {
/* 242 */         for (ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values()) {
/* 243 */           if (boundField.writeField(value)) {
/* 244 */             out.name(boundField.name);
/* 245 */             boundField.write(out, value);
/*     */           } 
/*     */         } 
/* 248 */       } catch (IllegalAccessException e) {
/* 249 */         throw new AssertionError(e);
/*     */       } 
/* 251 */       out.endObject();
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\ReflectiveTypeAdapterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */