/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ public final class NumberOutput
/*     */ {
/*   5 */   private static int MILLION = 1000000;
/*   6 */   private static int BILLION = 1000000000;
/*   7 */   private static long BILLION_L = 1000000000L;
/*     */   
/*   9 */   private static long MIN_INT_AS_LONG = -2147483648L;
/*  10 */   private static long MAX_INT_AS_LONG = 2147483647L;
/*     */   
/*  12 */   static final String SMALLEST_INT = String.valueOf(-2147483648);
/*  13 */   static final String SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   private static final int[] TRIPLET_TO_CHARS = new int[1000];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  27 */     int fullIx = 0;
/*  28 */     for (int i1 = 0; i1 < 10; i1++) {
/*  29 */       for (int i2 = 0; i2 < 10; i2++) {
/*  30 */         for (int i3 = 0; i3 < 10; i3++) {
/*  31 */           int enc = i1 + 48 << 16 | i2 + 48 << 8 | i3 + 48;
/*     */ 
/*     */           
/*  34 */           TRIPLET_TO_CHARS[fullIx++] = enc;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*  40 */   private static final String[] sSmallIntStrs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
/*     */ 
/*     */   
/*  43 */   private static final String[] sSmallIntStrs2 = new String[] { "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10" };
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
/*     */   public static int outputInt(int v, char[] b, int off) {
/*  58 */     if (v < 0) {
/*  59 */       if (v == Integer.MIN_VALUE)
/*     */       {
/*     */         
/*  62 */         return _outputSmallestI(b, off);
/*     */       }
/*  64 */       b[off++] = '-';
/*  65 */       v = -v;
/*     */     } 
/*     */     
/*  68 */     if (v < MILLION) {
/*  69 */       if (v < 1000) {
/*  70 */         if (v < 10) {
/*  71 */           b[off] = (char)(48 + v);
/*  72 */           return off + 1;
/*     */         } 
/*  74 */         return _leading3(v, b, off);
/*     */       } 
/*  76 */       int i = v / 1000;
/*  77 */       v -= i * 1000;
/*  78 */       off = _leading3(i, b, off);
/*  79 */       off = _full3(v, b, off);
/*  80 */       return off;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  88 */     if (v >= BILLION) {
/*  89 */       v -= BILLION;
/*  90 */       if (v >= BILLION) {
/*  91 */         v -= BILLION;
/*  92 */         b[off++] = '2';
/*     */       } else {
/*  94 */         b[off++] = '1';
/*     */       } 
/*  96 */       return _outputFullBillion(v, b, off);
/*     */     } 
/*  98 */     int newValue = v / 1000;
/*  99 */     int ones = v - newValue * 1000;
/* 100 */     v = newValue;
/* 101 */     newValue /= 1000;
/* 102 */     int thousands = v - newValue * 1000;
/*     */     
/* 104 */     off = _leading3(newValue, b, off);
/* 105 */     off = _full3(thousands, b, off);
/* 106 */     return _full3(ones, b, off);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int outputInt(int v, byte[] b, int off) {
/* 111 */     if (v < 0) {
/* 112 */       if (v == Integer.MIN_VALUE) {
/* 113 */         return _outputSmallestI(b, off);
/*     */       }
/* 115 */       b[off++] = 45;
/* 116 */       v = -v;
/*     */     } 
/*     */     
/* 119 */     if (v < MILLION) {
/* 120 */       if (v < 1000) {
/* 121 */         if (v < 10) {
/* 122 */           b[off++] = (byte)(48 + v);
/*     */         } else {
/* 124 */           off = _leading3(v, b, off);
/*     */         } 
/*     */       } else {
/* 127 */         int i = v / 1000;
/* 128 */         v -= i * 1000;
/* 129 */         off = _leading3(i, b, off);
/* 130 */         off = _full3(v, b, off);
/*     */       } 
/* 132 */       return off;
/*     */     } 
/* 134 */     if (v >= BILLION) {
/* 135 */       v -= BILLION;
/* 136 */       if (v >= BILLION) {
/* 137 */         v -= BILLION;
/* 138 */         b[off++] = 50;
/*     */       } else {
/* 140 */         b[off++] = 49;
/*     */       } 
/* 142 */       return _outputFullBillion(v, b, off);
/*     */     } 
/* 144 */     int newValue = v / 1000;
/* 145 */     int ones = v - newValue * 1000;
/* 146 */     v = newValue;
/* 147 */     newValue /= 1000;
/* 148 */     int thousands = v - newValue * 1000;
/* 149 */     off = _leading3(newValue, b, off);
/* 150 */     off = _full3(thousands, b, off);
/* 151 */     return _full3(ones, b, off);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int outputLong(long v, char[] b, int off) {
/* 160 */     if (v < 0L) {
/* 161 */       if (v > MIN_INT_AS_LONG) {
/* 162 */         return outputInt((int)v, b, off);
/*     */       }
/* 164 */       if (v == Long.MIN_VALUE) {
/* 165 */         return _outputSmallestL(b, off);
/*     */       }
/* 167 */       b[off++] = '-';
/* 168 */       v = -v;
/*     */     }
/* 170 */     else if (v <= MAX_INT_AS_LONG) {
/* 171 */       return outputInt((int)v, b, off);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 176 */     long upper = v / BILLION_L;
/* 177 */     v -= upper * BILLION_L;
/*     */ 
/*     */     
/* 180 */     if (upper < BILLION_L) {
/* 181 */       off = _outputUptoBillion((int)upper, b, off);
/*     */     } else {
/*     */       
/* 184 */       long hi = upper / BILLION_L;
/* 185 */       upper -= hi * BILLION_L;
/* 186 */       off = _leading3((int)hi, b, off);
/* 187 */       off = _outputFullBillion((int)upper, b, off);
/*     */     } 
/* 189 */     return _outputFullBillion((int)v, b, off);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int outputLong(long v, byte[] b, int off) {
/* 194 */     if (v < 0L) {
/* 195 */       if (v > MIN_INT_AS_LONG) {
/* 196 */         return outputInt((int)v, b, off);
/*     */       }
/* 198 */       if (v == Long.MIN_VALUE) {
/* 199 */         return _outputSmallestL(b, off);
/*     */       }
/* 201 */       b[off++] = 45;
/* 202 */       v = -v;
/*     */     }
/* 204 */     else if (v <= MAX_INT_AS_LONG) {
/* 205 */       return outputInt((int)v, b, off);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 210 */     long upper = v / BILLION_L;
/* 211 */     v -= upper * BILLION_L;
/*     */ 
/*     */     
/* 214 */     if (upper < BILLION_L) {
/* 215 */       off = _outputUptoBillion((int)upper, b, off);
/*     */     } else {
/*     */       
/* 218 */       long hi = upper / BILLION_L;
/* 219 */       upper -= hi * BILLION_L;
/* 220 */       off = _leading3((int)hi, b, off);
/* 221 */       off = _outputFullBillion((int)upper, b, off);
/*     */     } 
/* 223 */     return _outputFullBillion((int)v, b, off);
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
/*     */   public static String toString(int v) {
/* 238 */     if (v < sSmallIntStrs.length) {
/* 239 */       if (v >= 0) {
/* 240 */         return sSmallIntStrs[v];
/*     */       }
/* 242 */       int v2 = -v - 1;
/* 243 */       if (v2 < sSmallIntStrs2.length) {
/* 244 */         return sSmallIntStrs2[v2];
/*     */       }
/*     */     } 
/* 247 */     return Integer.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(long v) {
/* 251 */     if (v <= 2147483647L && v >= -2147483648L) {
/* 252 */       return toString((int)v);
/*     */     }
/* 254 */     return Long.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(double v) {
/* 258 */     return Double.toString(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(float v) {
/* 265 */     return Float.toString(v);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean notFinite(double value) {
/* 284 */     return (Double.isNaN(value) || Double.isInfinite(value));
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
/*     */   public static boolean notFinite(float value) {
/* 297 */     return (Float.isNaN(value) || Float.isInfinite(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int _outputUptoBillion(int v, char[] b, int off) {
/* 308 */     if (v < MILLION) {
/* 309 */       if (v < 1000) {
/* 310 */         return _leading3(v, b, off);
/*     */       }
/* 312 */       int i = v / 1000;
/* 313 */       int j = v - i * 1000;
/* 314 */       return _outputUptoMillion(b, off, i, j);
/*     */     } 
/* 316 */     int thousands = v / 1000;
/* 317 */     int ones = v - thousands * 1000;
/* 318 */     int millions = thousands / 1000;
/* 319 */     thousands -= millions * 1000;
/*     */     
/* 321 */     off = _leading3(millions, b, off);
/*     */     
/* 323 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 324 */     b[off++] = (char)(enc >> 16);
/* 325 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 326 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 328 */     enc = TRIPLET_TO_CHARS[ones];
/* 329 */     b[off++] = (char)(enc >> 16);
/* 330 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 331 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 333 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputFullBillion(int v, char[] b, int off) {
/* 338 */     int thousands = v / 1000;
/* 339 */     int ones = v - thousands * 1000;
/* 340 */     int millions = thousands / 1000;
/*     */     
/* 342 */     int enc = TRIPLET_TO_CHARS[millions];
/* 343 */     b[off++] = (char)(enc >> 16);
/* 344 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 345 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 347 */     thousands -= millions * 1000;
/* 348 */     enc = TRIPLET_TO_CHARS[thousands];
/* 349 */     b[off++] = (char)(enc >> 16);
/* 350 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 351 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 353 */     enc = TRIPLET_TO_CHARS[ones];
/* 354 */     b[off++] = (char)(enc >> 16);
/* 355 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 356 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 358 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoBillion(int v, byte[] b, int off) {
/* 363 */     if (v < MILLION) {
/* 364 */       if (v < 1000) {
/* 365 */         return _leading3(v, b, off);
/*     */       }
/* 367 */       int i = v / 1000;
/* 368 */       int j = v - i * 1000;
/* 369 */       return _outputUptoMillion(b, off, i, j);
/*     */     } 
/* 371 */     int thousands = v / 1000;
/* 372 */     int ones = v - thousands * 1000;
/* 373 */     int millions = thousands / 1000;
/* 374 */     thousands -= millions * 1000;
/*     */     
/* 376 */     off = _leading3(millions, b, off);
/*     */     
/* 378 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 379 */     b[off++] = (byte)(enc >> 16);
/* 380 */     b[off++] = (byte)(enc >> 8);
/* 381 */     b[off++] = (byte)enc;
/*     */     
/* 383 */     enc = TRIPLET_TO_CHARS[ones];
/* 384 */     b[off++] = (byte)(enc >> 16);
/* 385 */     b[off++] = (byte)(enc >> 8);
/* 386 */     b[off++] = (byte)enc;
/*     */     
/* 388 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputFullBillion(int v, byte[] b, int off) {
/* 393 */     int thousands = v / 1000;
/* 394 */     int ones = v - thousands * 1000;
/* 395 */     int millions = thousands / 1000;
/* 396 */     thousands -= millions * 1000;
/*     */     
/* 398 */     int enc = TRIPLET_TO_CHARS[millions];
/* 399 */     b[off++] = (byte)(enc >> 16);
/* 400 */     b[off++] = (byte)(enc >> 8);
/* 401 */     b[off++] = (byte)enc;
/*     */     
/* 403 */     enc = TRIPLET_TO_CHARS[thousands];
/* 404 */     b[off++] = (byte)(enc >> 16);
/* 405 */     b[off++] = (byte)(enc >> 8);
/* 406 */     b[off++] = (byte)enc;
/*     */     
/* 408 */     enc = TRIPLET_TO_CHARS[ones];
/* 409 */     b[off++] = (byte)(enc >> 16);
/* 410 */     b[off++] = (byte)(enc >> 8);
/* 411 */     b[off++] = (byte)enc;
/*     */     
/* 413 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoMillion(char[] b, int off, int thousands, int ones) {
/* 418 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 419 */     if (thousands > 9) {
/* 420 */       if (thousands > 99) {
/* 421 */         b[off++] = (char)(enc >> 16);
/*     */       }
/* 423 */       b[off++] = (char)(enc >> 8 & 0x7F);
/*     */     } 
/* 425 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 427 */     enc = TRIPLET_TO_CHARS[ones];
/* 428 */     b[off++] = (char)(enc >> 16);
/* 429 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 430 */     b[off++] = (char)(enc & 0x7F);
/* 431 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoMillion(byte[] b, int off, int thousands, int ones) {
/* 436 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 437 */     if (thousands > 9) {
/* 438 */       if (thousands > 99) {
/* 439 */         b[off++] = (byte)(enc >> 16);
/*     */       }
/* 441 */       b[off++] = (byte)(enc >> 8);
/*     */     } 
/* 443 */     b[off++] = (byte)enc;
/*     */     
/* 445 */     enc = TRIPLET_TO_CHARS[ones];
/* 446 */     b[off++] = (byte)(enc >> 16);
/* 447 */     b[off++] = (byte)(enc >> 8);
/* 448 */     b[off++] = (byte)enc;
/* 449 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _leading3(int t, char[] b, int off) {
/* 454 */     int enc = TRIPLET_TO_CHARS[t];
/* 455 */     if (t > 9) {
/* 456 */       if (t > 99) {
/* 457 */         b[off++] = (char)(enc >> 16);
/*     */       }
/* 459 */       b[off++] = (char)(enc >> 8 & 0x7F);
/*     */     } 
/* 461 */     b[off++] = (char)(enc & 0x7F);
/* 462 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _leading3(int t, byte[] b, int off) {
/* 467 */     int enc = TRIPLET_TO_CHARS[t];
/* 468 */     if (t > 9) {
/* 469 */       if (t > 99) {
/* 470 */         b[off++] = (byte)(enc >> 16);
/*     */       }
/* 472 */       b[off++] = (byte)(enc >> 8);
/*     */     } 
/* 474 */     b[off++] = (byte)enc;
/* 475 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _full3(int t, char[] b, int off) {
/* 480 */     int enc = TRIPLET_TO_CHARS[t];
/* 481 */     b[off++] = (char)(enc >> 16);
/* 482 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 483 */     b[off++] = (char)(enc & 0x7F);
/* 484 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _full3(int t, byte[] b, int off) {
/* 489 */     int enc = TRIPLET_TO_CHARS[t];
/* 490 */     b[off++] = (byte)(enc >> 16);
/* 491 */     b[off++] = (byte)(enc >> 8);
/* 492 */     b[off++] = (byte)enc;
/* 493 */     return off;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int _outputSmallestL(char[] b, int off) {
/* 500 */     int len = SMALLEST_LONG.length();
/* 501 */     SMALLEST_LONG.getChars(0, len, b, off);
/* 502 */     return off + len;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestL(byte[] b, int off) {
/* 507 */     int len = SMALLEST_LONG.length();
/* 508 */     for (int i = 0; i < len; i++) {
/* 509 */       b[off++] = (byte)SMALLEST_LONG.charAt(i);
/*     */     }
/* 511 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestI(char[] b, int off) {
/* 516 */     int len = SMALLEST_INT.length();
/* 517 */     SMALLEST_INT.getChars(0, len, b, off);
/* 518 */     return off + len;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestI(byte[] b, int off) {
/* 523 */     int len = SMALLEST_INT.length();
/* 524 */     for (int i = 0; i < len; i++) {
/* 525 */       b[off++] = (byte)SMALLEST_INT.charAt(i);
/*     */     }
/* 527 */     return off;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\io\NumberOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */