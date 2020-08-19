/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonDeserializationContext;
/*     */ import com.google.gson.JsonDeserializer;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.JsonSerializationContext;
/*     */ import com.google.gson.JsonSerializer;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.internal.;
/*     */ import com.google.gson.internal.Streams;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TreeTypeAdapter<T>
/*     */   extends TypeAdapter<T>
/*     */ {
/*     */   private final JsonSerializer<T> serializer;
/*     */   private final JsonDeserializer<T> deserializer;
/*     */   final Gson gson;
/*     */   private final TypeToken<T> typeToken;
/*     */   private final TypeAdapterFactory skipPast;
/*  47 */   private final GsonContextImpl context = new GsonContextImpl();
/*     */ 
/*     */   
/*     */   private TypeAdapter<T> delegate;
/*     */ 
/*     */   
/*     */   public TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast) {
/*  54 */     this.serializer = serializer;
/*  55 */     this.deserializer = deserializer;
/*  56 */     this.gson = gson;
/*  57 */     this.typeToken = typeToken;
/*  58 */     this.skipPast = skipPast;
/*     */   }
/*     */   
/*     */   public T read(JsonReader in) throws IOException {
/*  62 */     if (this.deserializer == null) {
/*  63 */       return (T)delegate().read(in);
/*     */     }
/*  65 */     JsonElement value = Streams.parse(in);
/*  66 */     if (value.isJsonNull()) {
/*  67 */       return null;
/*     */     }
/*  69 */     return (T)this.deserializer.deserialize(value, this.typeToken.getType(), this.context);
/*     */   }
/*     */   
/*     */   public void write(JsonWriter out, T value) throws IOException {
/*  73 */     if (this.serializer == null) {
/*  74 */       delegate().write(out, value);
/*     */       return;
/*     */     } 
/*  77 */     if (value == null) {
/*  78 */       out.nullValue();
/*     */       return;
/*     */     } 
/*  81 */     JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.context);
/*  82 */     Streams.write(tree, out);
/*     */   }
/*     */   
/*     */   private TypeAdapter<T> delegate() {
/*  86 */     TypeAdapter<T> d = this.delegate;
/*  87 */     return (d != null) ? d : (this
/*     */       
/*  89 */       .delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeAdapterFactory newFactory(TypeToken<?> exactType, Object typeAdapter) {
/*  96 */     return new SingleTypeFactory(typeAdapter, exactType, false, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> exactType, Object typeAdapter) {
/* 106 */     boolean matchRawType = (exactType.getType() == exactType.getRawType());
/* 107 */     return new SingleTypeFactory(typeAdapter, exactType, matchRawType, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> hierarchyType, Object typeAdapter) {
/* 116 */     return new SingleTypeFactory(typeAdapter, null, false, hierarchyType);
/*     */   }
/*     */   
/*     */   private static final class SingleTypeFactory
/*     */     implements TypeAdapterFactory {
/*     */     private final TypeToken<?> exactType;
/*     */     private final boolean matchRawType;
/*     */     private final Class<?> hierarchyType;
/*     */     private final JsonSerializer<?> serializer;
/*     */     private final JsonDeserializer<?> deserializer;
/*     */     
/*     */     SingleTypeFactory(Object typeAdapter, TypeToken<?> exactType, boolean matchRawType, Class<?> hierarchyType) {
/* 128 */       this.serializer = (typeAdapter instanceof JsonSerializer) ? (JsonSerializer)typeAdapter : null;
/*     */ 
/*     */       
/* 131 */       this.deserializer = (typeAdapter instanceof JsonDeserializer) ? (JsonDeserializer)typeAdapter : null;
/*     */ 
/*     */       
/* 134 */       .Gson.Preconditions.checkArgument((this.serializer != null || this.deserializer != null));
/* 135 */       this.exactType = exactType;
/* 136 */       this.matchRawType = matchRawType;
/* 137 */       this.hierarchyType = hierarchyType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/* 145 */       boolean matches = (this.exactType != null) ? ((this.exactType.equals(type) || (this.matchRawType && this.exactType.getType() == type.getRawType()))) : this.hierarchyType.isAssignableFrom(type.getRawType());
/* 146 */       return matches ? new TreeTypeAdapter<T>((JsonSerializer)this.serializer, (JsonDeserializer)this.deserializer, gson, type, this) : null;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class GsonContextImpl
/*     */     implements JsonSerializationContext, JsonDeserializationContext {
/*     */     private GsonContextImpl() {}
/*     */     
/*     */     public JsonElement serialize(Object src) {
/* 155 */       return TreeTypeAdapter.this.gson.toJsonTree(src);
/*     */     }
/*     */     public JsonElement serialize(Object src, Type typeOfSrc) {
/* 158 */       return TreeTypeAdapter.this.gson.toJsonTree(src, typeOfSrc);
/*     */     }
/*     */     
/*     */     public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
/* 162 */       return (R)TreeTypeAdapter.this.gson.fromJson(json, typeOfT);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\TreeTypeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */