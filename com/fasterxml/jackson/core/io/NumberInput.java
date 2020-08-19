/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NumberInput
/*     */ {
/*     */   public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
/*     */   static final long L_BILLION = 1000000000L;
/*  18 */   static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
/*  19 */   static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInt(char[] ch, int off, int len) {
/*  30 */     int num = ch[off + len - 1] - 48;
/*     */     
/*  32 */     switch (len) {
/*     */       case 9:
/*  34 */         num += (ch[off++] - 48) * 100000000;
/*     */       case 8:
/*  36 */         num += (ch[off++] - 48) * 10000000;
/*     */       case 7:
/*  38 */         num += (ch[off++] - 48) * 1000000;
/*     */       case 6:
/*  40 */         num += (ch[off++] - 48) * 100000;
/*     */       case 5:
/*  42 */         num += (ch[off++] - 48) * 10000;
/*     */       case 4:
/*  44 */         num += (ch[off++] - 48) * 1000;
/*     */       case 3:
/*  46 */         num += (ch[off++] - 48) * 100;
/*     */       case 2:
/*  48 */         num += (ch[off] - 48) * 10; break;
/*     */     } 
/*  50 */     return num;
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
/*     */   public static int parseInt(String s) {
/*  63 */     char c = s.charAt(0);
/*  64 */     int len = s.length();
/*  65 */     boolean neg = (c == '-');
/*  66 */     int offset = 1;
/*     */ 
/*     */     
/*  69 */     if (neg) {
/*  70 */       if (len == 1 || len > 10) {
/*  71 */         return Integer.parseInt(s);
/*     */       }
/*  73 */       c = s.charAt(offset++);
/*     */     }
/*  75 */     else if (len > 9) {
/*  76 */       return Integer.parseInt(s);
/*     */     } 
/*     */     
/*  79 */     if (c > '9' || c < '0') {
/*  80 */       return Integer.parseInt(s);
/*     */     }
/*  82 */     int num = c - 48;
/*  83 */     if (offset < len) {
/*  84 */       c = s.charAt(offset++);
/*  85 */       if (c > '9' || c < '0') {
/*  86 */         return Integer.parseInt(s);
/*     */       }
/*  88 */       num = num * 10 + c - 48;
/*  89 */       if (offset < len) {
/*  90 */         c = s.charAt(offset++);
/*  91 */         if (c > '9' || c < '0') {
/*  92 */           return Integer.parseInt(s);
/*     */         }
/*  94 */         num = num * 10 + c - 48;
/*     */         
/*  96 */         if (offset < len) {
/*     */           do {
/*  98 */             c = s.charAt(offset++);
/*  99 */             if (c > '9' || c < '0') {
/* 100 */               return Integer.parseInt(s);
/*     */             }
/* 102 */             num = num * 10 + c - 48;
/* 103 */           } while (offset < len);
/*     */         }
/*     */       } 
/*     */     } 
/* 107 */     return neg ? -num : num;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(char[] ch, int off, int len) {
/* 113 */     int len1 = len - 9;
/* 114 */     long val = parseInt(ch, off, len1) * 1000000000L;
/* 115 */     return val + parseInt(ch, off + len1, 9);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(String s) {
/* 123 */     int length = s.length();
/* 124 */     if (length <= 9) {
/* 125 */       return parseInt(s);
/*     */     }
/*     */     
/* 128 */     return Long.parseLong(s);
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
/*     */   public static boolean inLongRange(char[] ch, int off, int len, boolean negative) {
/* 143 */     String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 144 */     int cmpLen = cmpStr.length();
/* 145 */     if (len < cmpLen) return true; 
/* 146 */     if (len > cmpLen) return false;
/*     */     
/* 148 */     for (int i = 0; i < cmpLen; i++) {
/* 149 */       int diff = ch[off + i] - cmpStr.charAt(i);
/* 150 */       if (diff != 0) {
/* 151 */         return (diff < 0);
/*     */       }
/*     */     } 
/* 154 */     return true;
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
/*     */   public static boolean inLongRange(String s, boolean negative) {
/* 166 */     String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 167 */     int cmpLen = cmp.length();
/* 168 */     int alen = s.length();
/* 169 */     if (alen < cmpLen) return true; 
/* 170 */     if (alen > cmpLen) return false;
/*     */ 
/*     */     
/* 173 */     for (int i = 0; i < cmpLen; i++) {
/* 174 */       int diff = s.charAt(i) - cmp.charAt(i);
/* 175 */       if (diff != 0) {
/* 176 */         return (diff < 0);
/*     */       }
/*     */     } 
/* 179 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int parseAsInt(String s, int def) {
/* 184 */     if (s == null) {
/* 185 */       return def;
/*     */     }
/* 187 */     s = s.trim();
/* 188 */     int len = s.length();
/* 189 */     if (len == 0) {
/* 190 */       return def;
/*     */     }
/*     */     
/* 193 */     int i = 0;
/* 194 */     if (i < len) {
/* 195 */       char c = s.charAt(0);
/* 196 */       if (c == '+') {
/* 197 */         s = s.substring(1);
/* 198 */         len = s.length();
/* 199 */       } else if (c == '-') {
/* 200 */         i++;
/*     */       } 
/*     */     } 
/* 203 */     for (; i < len; i++) {
/* 204 */       char c = s.charAt(i);
/*     */       
/* 206 */       if (c > '9' || c < '0') {
/*     */         try {
/* 208 */           return (int)parseDouble(s);
/* 209 */         } catch (NumberFormatException e) {
/* 210 */           return def;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     try {
/* 215 */       return Integer.parseInt(s);
/* 216 */     } catch (NumberFormatException numberFormatException) {
/* 217 */       return def;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static long parseAsLong(String s, long def) {
/* 222 */     if (s == null) {
/* 223 */       return def;
/*     */     }
/* 225 */     s = s.trim();
/* 226 */     int len = s.length();
/* 227 */     if (len == 0) {
/* 228 */       return def;
/*     */     }
/*     */     
/* 231 */     int i = 0;
/* 232 */     if (i < len) {
/* 233 */       char c = s.charAt(0);
/* 234 */       if (c == '+') {
/* 235 */         s = s.substring(1);
/* 236 */         len = s.length();
/* 237 */       } else if (c == '-') {
/* 238 */         i++;
/*     */       } 
/*     */     } 
/* 241 */     for (; i < len; i++) {
/* 242 */       char c = s.charAt(i);
/*     */       
/* 244 */       if (c > '9' || c < '0') {
/*     */         try {
/* 246 */           return (long)parseDouble(s);
/* 247 */         } catch (NumberFormatException e) {
/* 248 */           return def;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     try {
/* 253 */       return Long.parseLong(s);
/* 254 */     } catch (NumberFormatException numberFormatException) {
/* 255 */       return def;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static double parseAsDouble(String s, double def) {
/* 260 */     if (s == null) return def; 
/* 261 */     s = s.trim();
/* 262 */     int len = s.length();
/* 263 */     if (len == 0) {
/* 264 */       return def;
/*     */     }
/*     */     try {
/* 267 */       return parseDouble(s);
/* 268 */     } catch (NumberFormatException numberFormatException) {
/* 269 */       return def;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double parseDouble(String s) throws NumberFormatException {
/* 277 */     if ("2.2250738585072012e-308".equals(s)) {
/* 278 */       return Double.MIN_VALUE;
/*     */     }
/* 280 */     return Double.parseDouble(s);
/*     */   }
/*     */   public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
/*     */     
/* 284 */     try { return new BigDecimal(s); } catch (NumberFormatException e)
/* 285 */     { throw _badBD(s); }
/*     */   
/*     */   }
/*     */   
/*     */   public static BigDecimal parseBigDecimal(char[] b) throws NumberFormatException {
/* 290 */     return parseBigDecimal(b, 0, b.length);
/*     */   }
/*     */   public static BigDecimal parseBigDecimal(char[] b, int off, int len) throws NumberFormatException {
/*     */     
/* 294 */     try { return new BigDecimal(b, off, len); } catch (NumberFormatException e)
/* 295 */     { throw _badBD(new String(b, off, len)); }
/*     */   
/*     */   }
/*     */   
/*     */   private static NumberFormatException _badBD(String s) {
/* 300 */     return new NumberFormatException("Value \"" + s + "\" can not be represented as BigDecimal");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\io\NumberInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */