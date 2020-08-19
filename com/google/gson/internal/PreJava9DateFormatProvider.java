/*    */ package com.google.gson.internal;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.SimpleDateFormat;
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
/*    */ public class PreJava9DateFormatProvider
/*    */ {
/*    */   public static DateFormat getUSDateFormat(int style) {
/* 31 */     return new SimpleDateFormat(getDateFormatPattern(style), Locale.US);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DateFormat getUSDateTimeFormat(int dateStyle, int timeStyle) {
/* 39 */     String pattern = getDatePartOfDateTimePattern(dateStyle) + " " + getTimePartOfDateTimePattern(timeStyle);
/* 40 */     return new SimpleDateFormat(pattern, Locale.US);
/*    */   }
/*    */   
/*    */   private static String getDateFormatPattern(int style) {
/* 44 */     switch (style) {
/*    */       case 3:
/* 46 */         return "M/d/yy";
/*    */       case 2:
/* 48 */         return "MMM d, y";
/*    */       case 1:
/* 50 */         return "MMMM d, y";
/*    */       case 0:
/* 52 */         return "EEEE, MMMM d, y";
/*    */     } 
/* 54 */     throw new IllegalArgumentException("Unknown DateFormat style: " + style);
/*    */   }
/*    */ 
/*    */   
/*    */   private static String getDatePartOfDateTimePattern(int dateStyle) {
/* 59 */     switch (dateStyle) {
/*    */       case 3:
/* 61 */         return "M/d/yy";
/*    */       case 2:
/* 63 */         return "MMM d, yyyy";
/*    */       case 1:
/* 65 */         return "MMMM d, yyyy";
/*    */       case 0:
/* 67 */         return "EEEE, MMMM d, yyyy";
/*    */     } 
/* 69 */     throw new IllegalArgumentException("Unknown DateFormat style: " + dateStyle);
/*    */   }
/*    */ 
/*    */   
/*    */   private static String getTimePartOfDateTimePattern(int timeStyle) {
/* 74 */     switch (timeStyle) {
/*    */       case 3:
/* 76 */         return "h:mm a";
/*    */       case 2:
/* 78 */         return "h:mm:ss a";
/*    */       case 0:
/*    */       case 1:
/* 81 */         return "h:mm:ss a z";
/*    */     } 
/* 83 */     throw new IllegalArgumentException("Unknown DateFormat style: " + timeStyle);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\PreJava9DateFormatProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */