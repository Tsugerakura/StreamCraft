/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.JsonSyntaxException;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.TypeAdapterFactory;
/*    */ import com.google.gson.internal.JavaVersion;
/*    */ import com.google.gson.internal.PreJava9DateFormatProvider;
/*    */ import com.google.gson.internal.bind.util.ISO8601Utils;
/*    */ import com.google.gson.reflect.TypeToken;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.text.DateFormat;
/*    */ import java.text.ParseException;
/*    */ import java.text.ParsePosition;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ import java.util.Locale;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DateTypeAdapter
/*    */   extends TypeAdapter<Date>
/*    */ {
/* 47 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*    */     {
/*    */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 50 */         return (typeToken.getRawType() == Date.class) ? new DateTypeAdapter() : null;
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 58 */   private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();
/*    */   
/*    */   public DateTypeAdapter() {
/* 61 */     this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2, Locale.US));
/* 62 */     if (!Locale.getDefault().equals(Locale.US)) {
/* 63 */       this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2));
/*    */     }
/* 65 */     if (JavaVersion.isJava9OrLater()) {
/* 66 */       this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(2, 2));
/*    */     }
/*    */   }
/*    */   
/*    */   public Date read(JsonReader in) throws IOException {
/* 71 */     if (in.peek() == JsonToken.NULL) {
/* 72 */       in.nextNull();
/* 73 */       return null;
/*    */     } 
/* 75 */     return deserializeToDate(in.nextString());
/*    */   }
/*    */   
/*    */   private synchronized Date deserializeToDate(String json) {
/* 79 */     for (DateFormat dateFormat : this.dateFormats) {
/*    */       try {
/* 81 */         return dateFormat.parse(json);
/* 82 */       } catch (ParseException parseException) {}
/*    */     } 
/*    */     try {
/* 85 */       return ISO8601Utils.parse(json, new ParsePosition(0));
/* 86 */     } catch (ParseException e) {
/* 87 */       throw new JsonSyntaxException(json, e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public synchronized void write(JsonWriter out, Date value) throws IOException {
/* 92 */     if (value == null) {
/* 93 */       out.nullValue();
/*    */       return;
/*    */     } 
/* 96 */     String dateFormatAsString = ((DateFormat)this.dateFormats.get(0)).format(value);
/* 97 */     out.value(dateFormatAsString);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\DateTypeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */