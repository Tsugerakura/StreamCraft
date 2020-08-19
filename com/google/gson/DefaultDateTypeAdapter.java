/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.JavaVersion;
/*     */ import com.google.gson.internal.PreJava9DateFormatProvider;
/*     */ import com.google.gson.internal.bind.util.ISO8601Utils;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.sql.Date;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DefaultDateTypeAdapter
/*     */   extends TypeAdapter<Date>
/*     */ {
/*     */   private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
/*     */   private final Class<? extends Date> dateType;
/*  54 */   private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();
/*     */   
/*     */   DefaultDateTypeAdapter(Class<? extends Date> dateType) {
/*  57 */     this.dateType = verifyDateType(dateType);
/*  58 */     this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2, Locale.US));
/*  59 */     if (!Locale.getDefault().equals(Locale.US)) {
/*  60 */       this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2));
/*     */     }
/*  62 */     if (JavaVersion.isJava9OrLater()) {
/*  63 */       this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(2, 2));
/*     */     }
/*     */   }
/*     */   
/*     */   DefaultDateTypeAdapter(Class<? extends Date> dateType, String datePattern) {
/*  68 */     this.dateType = verifyDateType(dateType);
/*  69 */     this.dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
/*  70 */     if (!Locale.getDefault().equals(Locale.US)) {
/*  71 */       this.dateFormats.add(new SimpleDateFormat(datePattern));
/*     */     }
/*     */   }
/*     */   
/*     */   DefaultDateTypeAdapter(Class<? extends Date> dateType, int style) {
/*  76 */     this.dateType = verifyDateType(dateType);
/*  77 */     this.dateFormats.add(DateFormat.getDateInstance(style, Locale.US));
/*  78 */     if (!Locale.getDefault().equals(Locale.US)) {
/*  79 */       this.dateFormats.add(DateFormat.getDateInstance(style));
/*     */     }
/*  81 */     if (JavaVersion.isJava9OrLater()) {
/*  82 */       this.dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(style));
/*     */     }
/*     */   }
/*     */   
/*     */   public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
/*  87 */     this(Date.class, dateStyle, timeStyle);
/*     */   }
/*     */   
/*     */   public DefaultDateTypeAdapter(Class<? extends Date> dateType, int dateStyle, int timeStyle) {
/*  91 */     this.dateType = verifyDateType(dateType);
/*  92 */     this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
/*  93 */     if (!Locale.getDefault().equals(Locale.US)) {
/*  94 */       this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
/*     */     }
/*  96 */     if (JavaVersion.isJava9OrLater()) {
/*  97 */       this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(dateStyle, timeStyle));
/*     */     }
/*     */   }
/*     */   
/*     */   private static Class<? extends Date> verifyDateType(Class<? extends Date> dateType) {
/* 102 */     if (dateType != Date.class && dateType != Date.class && dateType != Timestamp.class) {
/* 103 */       throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + Date.class + " but was " + dateType);
/*     */     }
/* 105 */     return dateType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(JsonWriter out, Date value) throws IOException {
/* 112 */     if (value == null) {
/* 113 */       out.nullValue();
/*     */       return;
/*     */     } 
/* 116 */     synchronized (this.dateFormats) {
/* 117 */       String dateFormatAsString = ((DateFormat)this.dateFormats.get(0)).format(value);
/* 118 */       out.value(dateFormatAsString);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Date read(JsonReader in) throws IOException {
/* 124 */     if (in.peek() == JsonToken.NULL) {
/* 125 */       in.nextNull();
/* 126 */       return null;
/*     */     } 
/* 128 */     Date date = deserializeToDate(in.nextString());
/* 129 */     if (this.dateType == Date.class)
/* 130 */       return date; 
/* 131 */     if (this.dateType == Timestamp.class)
/* 132 */       return new Timestamp(date.getTime()); 
/* 133 */     if (this.dateType == Date.class) {
/* 134 */       return new Date(date.getTime());
/*     */     }
/*     */     
/* 137 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   
/*     */   private Date deserializeToDate(String s) {
/* 142 */     synchronized (this.dateFormats) {
/* 143 */       for (DateFormat dateFormat : this.dateFormats) {
/*     */         try {
/* 145 */           return dateFormat.parse(s);
/* 146 */         } catch (ParseException parseException) {}
/*     */       } 
/*     */       try {
/* 149 */         return ISO8601Utils.parse(s, new ParsePosition(0));
/* 150 */       } catch (ParseException e) {
/* 151 */         throw new JsonSyntaxException(s, e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 158 */     DateFormat defaultFormat = this.dateFormats.get(0);
/* 159 */     if (defaultFormat instanceof SimpleDateFormat) {
/* 160 */       return "DefaultDateTypeAdapter(" + ((SimpleDateFormat)defaultFormat).toPattern() + ')';
/*     */     }
/* 162 */     return "DefaultDateTypeAdapter(" + defaultFormat.getClass().getSimpleName() + ')';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\DefaultDateTypeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */