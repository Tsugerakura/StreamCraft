/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.reflect.TypeToken;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class TypeAdapterRuntimeTypeWrapper<T>
/*    */   extends TypeAdapter<T>
/*    */ {
/*    */   private final Gson context;
/*    */   private final TypeAdapter<T> delegate;
/*    */   private final Type type;
/*    */   
/*    */   TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
/* 34 */     this.context = context;
/* 35 */     this.delegate = delegate;
/* 36 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public T read(JsonReader in) throws IOException {
/* 41 */     return (T)this.delegate.read(in);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, T value) throws IOException {
/* 53 */     TypeAdapter<T> chosen = this.delegate;
/* 54 */     Type runtimeType = getRuntimeTypeIfMoreSpecific(this.type, value);
/* 55 */     if (runtimeType != this.type) {
/* 56 */       TypeAdapter<T> runtimeTypeAdapter = this.context.getAdapter(TypeToken.get(runtimeType));
/* 57 */       if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
/*    */         
/* 59 */         chosen = runtimeTypeAdapter;
/* 60 */       } else if (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
/*    */ 
/*    */         
/* 63 */         chosen = this.delegate;
/*    */       } else {
/*    */         
/* 66 */         chosen = runtimeTypeAdapter;
/*    */       } 
/*    */     } 
/* 69 */     chosen.write(out, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Type getRuntimeTypeIfMoreSpecific(Type<?> type, Object value) {
/* 76 */     if (value != null && (type == Object.class || type instanceof java.lang.reflect.TypeVariable || type instanceof Class))
/*    */     {
/* 78 */       type = value.getClass();
/*    */     }
/* 80 */     return type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\TypeAdapterRuntimeTypeWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */