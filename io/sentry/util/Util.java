/*     */ package io.sentry.util;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Util
/*     */ {
/*     */   public static boolean equals(Object a, Object b) {
/*  38 */     return (a == b || (a != null && a.equals(b)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNullOrEmpty(String string) {
/*  48 */     return (string == null || string.length() == 0);
/*     */   }
/*     */   
/*     */   private static Map<String, String> parseCsv(String inputString, String typeName) {
/*  52 */     if (isNullOrEmpty(inputString)) {
/*  53 */       return Collections.emptyMap();
/*     */     }
/*     */     
/*  56 */     String[] entries = inputString.split(",");
/*  57 */     Map<String, String> map = new LinkedHashMap<>();
/*  58 */     for (String entry : entries) {
/*  59 */       String[] split = entry.split(":");
/*  60 */       if (split.length != 2) {
/*  61 */         throw new IllegalArgumentException("Invalid " + typeName + " entry: " + entry);
/*     */       }
/*  63 */       map.put(split[0], split[1]);
/*     */     } 
/*  65 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> parseTags(String tagsString) {
/*  75 */     return parseCsv(tagsString, "tags");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> parseExtra(String extrasString) {
/*  85 */     return parseCsv(extrasString, "extras");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Set<String> parseExtraTags(String extraTagsString) {
/*  97 */     return parseMdcTags(extraTagsString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<String> parseMdcTags(String mdcTagsString) {
/* 107 */     if (isNullOrEmpty(mdcTagsString)) {
/* 108 */       return Collections.emptySet();
/*     */     }
/*     */     
/* 111 */     return new HashSet<>(Arrays.asList(mdcTagsString.split(",")));
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
/*     */   public static Integer parseInteger(String value, Integer defaultValue) {
/* 124 */     if (isNullOrEmpty(value)) {
/* 125 */       return defaultValue;
/*     */     }
/* 127 */     return Integer.valueOf(Integer.parseInt(value));
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
/*     */   public static Long parseLong(String value, Long defaultValue) {
/* 139 */     if (isNullOrEmpty(value)) {
/* 140 */       return defaultValue;
/*     */     }
/* 142 */     return Long.valueOf(Long.parseLong(value));
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
/*     */   public static Double parseDouble(String value, Double defaultValue) {
/* 154 */     if (isNullOrEmpty(value)) {
/* 155 */       return defaultValue;
/*     */     }
/* 157 */     return Double.valueOf(Double.parseDouble(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String trimString(String string, int maxMessageLength) {
/* 168 */     if (string == null)
/* 169 */       return null; 
/* 170 */     if (string.length() > maxMessageLength)
/*     */     {
/* 172 */       return string.substring(0, maxMessageLength - 3) + "...";
/*     */     }
/*     */     
/* 175 */     return string;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean safelyRemoveShutdownHook(Thread shutDownHook) {
/*     */     try {
/* 186 */       return Runtime.getRuntime().removeShutdownHook(shutDownHook);
/* 187 */     } catch (IllegalStateException e) {
/*     */       
/* 189 */       if (e.getMessage().equals("Shutdown in progress"))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 196 */         return false;
/*     */       }
/*     */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeQuietly(Closeable closeable) {
/* 205 */     if (closeable != null)
/*     */       try {
/* 207 */         closeable.close();
/* 208 */       } catch (IOException|RuntimeException iOException) {} 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentr\\util\Util.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */