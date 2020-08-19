/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Base64Variants
/*     */ {
/*     */   static final String STD_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
/*  34 */   public static final Base64Variant MIME = new Base64Variant("MIME", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", true, '=', 76);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final Base64Variant MIME_NO_LINEFEEDS = new Base64Variant(MIME, "MIME-NO-LINEFEEDS", 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final Base64Variant PEM = new Base64Variant(MIME, "PEM", true, '=', 64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Base64Variant MODIFIED_FOR_URL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  67 */     StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
/*     */     
/*  69 */     sb.setCharAt(sb.indexOf("+"), '-');
/*  70 */     sb.setCharAt(sb.indexOf("/"), '_');
/*     */     
/*  72 */     MODIFIED_FOR_URL = new Base64Variant("MODIFIED-FOR-URL", sb.toString(), false, false, 2147483647);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Base64Variant getDefaultVariant() {
/*  82 */     return MIME_NO_LINEFEEDS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Base64Variant valueOf(String name) throws IllegalArgumentException {
/*  90 */     if (MIME._name.equals(name)) {
/*  91 */       return MIME;
/*     */     }
/*  93 */     if (MIME_NO_LINEFEEDS._name.equals(name)) {
/*  94 */       return MIME_NO_LINEFEEDS;
/*     */     }
/*  96 */     if (PEM._name.equals(name)) {
/*  97 */       return PEM;
/*     */     }
/*  99 */     if (MODIFIED_FOR_URL._name.equals(name)) {
/* 100 */       return MODIFIED_FOR_URL;
/*     */     }
/* 102 */     if (name == null) {
/* 103 */       name = "<null>";
/*     */     } else {
/* 105 */       name = "'" + name + "'";
/*     */     } 
/* 107 */     throw new IllegalArgumentException("No Base64Variant with name " + name);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\Base64Variants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */