/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.TimeZone;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DateFormatter
/*     */ {
/*  48 */   private static final BitSet DELIMITERS = new BitSet();
/*     */   static {
/*  50 */     DELIMITERS.set(9); char c;
/*  51 */     for (c = ' '; c <= '/'; c = (char)(c + 1)) {
/*  52 */       DELIMITERS.set(c);
/*     */     }
/*  54 */     for (c = ';'; c <= '@'; c = (char)(c + 1)) {
/*  55 */       DELIMITERS.set(c);
/*     */     }
/*  57 */     for (c = '['; c <= '`'; c = (char)(c + 1)) {
/*  58 */       DELIMITERS.set(c);
/*     */     }
/*  60 */     for (c = '{'; c <= '~'; c = (char)(c + 1)) {
/*  61 */       DELIMITERS.set(c);
/*     */     }
/*     */   }
/*     */   
/*  65 */   private static final String[] DAY_OF_WEEK_TO_SHORT_NAME = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
/*     */ 
/*     */   
/*  68 */   private static final String[] CALENDAR_MONTH_TO_SHORT_NAME = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
/*     */ 
/*     */   
/*  71 */   private static final FastThreadLocal<DateFormatter> INSTANCES = new FastThreadLocal<DateFormatter>()
/*     */     {
/*     */       protected DateFormatter initialValue()
/*     */       {
/*  75 */         return new DateFormatter();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parseHttpDate(CharSequence txt) {
/*  85 */     return parseHttpDate(txt, 0, txt.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parseHttpDate(CharSequence txt, int start, int end) {
/*  96 */     int length = end - start;
/*  97 */     if (length == 0)
/*  98 */       return null; 
/*  99 */     if (length < 0)
/* 100 */       throw new IllegalArgumentException("Can't have end < start"); 
/* 101 */     if (length > 64) {
/* 102 */       throw new IllegalArgumentException("Can't parse more than 64 chars,looks like a user error or a malformed header");
/*     */     }
/*     */     
/* 105 */     return formatter().parse0((CharSequence)ObjectUtil.checkNotNull(txt, "txt"), start, end);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date date) {
/* 114 */     return formatter().format0((Date)ObjectUtil.checkNotNull(date, "date"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder append(Date date, StringBuilder sb) {
/* 124 */     return formatter().append0((Date)ObjectUtil.checkNotNull(date, "date"), (StringBuilder)ObjectUtil.checkNotNull(sb, "sb"));
/*     */   }
/*     */   
/*     */   private static DateFormatter formatter() {
/* 128 */     DateFormatter formatter = (DateFormatter)INSTANCES.get();
/* 129 */     formatter.reset();
/* 130 */     return formatter;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isDelim(char c) {
/* 135 */     return DELIMITERS.get(c);
/*     */   }
/*     */   
/*     */   private static boolean isDigit(char c) {
/* 139 */     return (c >= '0' && c <= '9');
/*     */   }
/*     */   
/*     */   private static int getNumericalValue(char c) {
/* 143 */     return c - 48;
/*     */   }
/*     */   
/* 146 */   private final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
/* 147 */   private final StringBuilder sb = new StringBuilder(29);
/*     */   private boolean timeFound;
/*     */   private int hours;
/*     */   private int minutes;
/*     */   private int seconds;
/*     */   private boolean dayOfMonthFound;
/*     */   private int dayOfMonth;
/*     */   private boolean monthFound;
/*     */   private int month;
/*     */   private boolean yearFound;
/*     */   private int year;
/*     */   
/*     */   private DateFormatter() {
/* 160 */     reset();
/*     */   }
/*     */   
/*     */   public void reset() {
/* 164 */     this.timeFound = false;
/* 165 */     this.hours = -1;
/* 166 */     this.minutes = -1;
/* 167 */     this.seconds = -1;
/* 168 */     this.dayOfMonthFound = false;
/* 169 */     this.dayOfMonth = -1;
/* 170 */     this.monthFound = false;
/* 171 */     this.month = -1;
/* 172 */     this.yearFound = false;
/* 173 */     this.year = -1;
/* 174 */     this.cal.clear();
/* 175 */     this.sb.setLength(0);
/*     */   }
/*     */   
/*     */   private boolean tryParseTime(CharSequence txt, int tokenStart, int tokenEnd) {
/* 179 */     int len = tokenEnd - tokenStart;
/*     */ 
/*     */     
/* 182 */     if (len < 5 || len > 8) {
/* 183 */       return false;
/*     */     }
/*     */     
/* 186 */     int localHours = -1;
/* 187 */     int localMinutes = -1;
/* 188 */     int localSeconds = -1;
/* 189 */     int currentPartNumber = 0;
/* 190 */     int currentPartValue = 0;
/* 191 */     int numDigits = 0;
/*     */     
/* 193 */     for (int i = tokenStart; i < tokenEnd; i++) {
/* 194 */       char c = txt.charAt(i);
/* 195 */       if (isDigit(c)) {
/* 196 */         currentPartValue = currentPartValue * 10 + getNumericalValue(c);
/* 197 */         if (++numDigits > 2) {
/* 198 */           return false;
/*     */         }
/* 200 */       } else if (c == ':') {
/* 201 */         if (numDigits == 0)
/*     */         {
/* 203 */           return false;
/*     */         }
/* 205 */         switch (currentPartNumber) {
/*     */           
/*     */           case 0:
/* 208 */             localHours = currentPartValue;
/*     */             break;
/*     */           
/*     */           case 1:
/* 212 */             localMinutes = currentPartValue;
/*     */             break;
/*     */           
/*     */           default:
/* 216 */             return false;
/*     */         } 
/* 218 */         currentPartValue = 0;
/* 219 */         currentPartNumber++;
/* 220 */         numDigits = 0;
/*     */       } else {
/*     */         
/* 223 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 227 */     if (numDigits > 0)
/*     */     {
/* 229 */       localSeconds = currentPartValue;
/*     */     }
/*     */     
/* 232 */     if (localHours >= 0 && localMinutes >= 0 && localSeconds >= 0) {
/* 233 */       this.hours = localHours;
/* 234 */       this.minutes = localMinutes;
/* 235 */       this.seconds = localSeconds;
/* 236 */       return true;
/*     */     } 
/*     */     
/* 239 */     return false;
/*     */   }
/*     */   
/*     */   private boolean tryParseDayOfMonth(CharSequence txt, int tokenStart, int tokenEnd) {
/* 243 */     int len = tokenEnd - tokenStart;
/*     */     
/* 245 */     if (len == 1) {
/* 246 */       char c0 = txt.charAt(tokenStart);
/* 247 */       if (isDigit(c0)) {
/* 248 */         this.dayOfMonth = getNumericalValue(c0);
/* 249 */         return true;
/*     */       }
/*     */     
/* 252 */     } else if (len == 2) {
/* 253 */       char c0 = txt.charAt(tokenStart);
/* 254 */       char c1 = txt.charAt(tokenStart + 1);
/* 255 */       if (isDigit(c0) && isDigit(c1)) {
/* 256 */         this.dayOfMonth = getNumericalValue(c0) * 10 + getNumericalValue(c1);
/* 257 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 261 */     return false;
/*     */   }
/*     */   
/*     */   private boolean tryParseMonth(CharSequence txt, int tokenStart, int tokenEnd) {
/* 265 */     int len = tokenEnd - tokenStart;
/*     */     
/* 267 */     if (len != 3) {
/* 268 */       return false;
/*     */     }
/*     */     
/* 271 */     char monthChar1 = AsciiString.toLowerCase(txt.charAt(tokenStart));
/* 272 */     char monthChar2 = AsciiString.toLowerCase(txt.charAt(tokenStart + 1));
/* 273 */     char monthChar3 = AsciiString.toLowerCase(txt.charAt(tokenStart + 2));
/*     */     
/* 275 */     if (monthChar1 == 'j' && monthChar2 == 'a' && monthChar3 == 'n') {
/* 276 */       this.month = 0;
/* 277 */     } else if (monthChar1 == 'f' && monthChar2 == 'e' && monthChar3 == 'b') {
/* 278 */       this.month = 1;
/* 279 */     } else if (monthChar1 == 'm' && monthChar2 == 'a' && monthChar3 == 'r') {
/* 280 */       this.month = 2;
/* 281 */     } else if (monthChar1 == 'a' && monthChar2 == 'p' && monthChar3 == 'r') {
/* 282 */       this.month = 3;
/* 283 */     } else if (monthChar1 == 'm' && monthChar2 == 'a' && monthChar3 == 'y') {
/* 284 */       this.month = 4;
/* 285 */     } else if (monthChar1 == 'j' && monthChar2 == 'u' && monthChar3 == 'n') {
/* 286 */       this.month = 5;
/* 287 */     } else if (monthChar1 == 'j' && monthChar2 == 'u' && monthChar3 == 'l') {
/* 288 */       this.month = 6;
/* 289 */     } else if (monthChar1 == 'a' && monthChar2 == 'u' && monthChar3 == 'g') {
/* 290 */       this.month = 7;
/* 291 */     } else if (monthChar1 == 's' && monthChar2 == 'e' && monthChar3 == 'p') {
/* 292 */       this.month = 8;
/* 293 */     } else if (monthChar1 == 'o' && monthChar2 == 'c' && monthChar3 == 't') {
/* 294 */       this.month = 9;
/* 295 */     } else if (monthChar1 == 'n' && monthChar2 == 'o' && monthChar3 == 'v') {
/* 296 */       this.month = 10;
/* 297 */     } else if (monthChar1 == 'd' && monthChar2 == 'e' && monthChar3 == 'c') {
/* 298 */       this.month = 11;
/*     */     } else {
/* 300 */       return false;
/*     */     } 
/*     */     
/* 303 */     return true;
/*     */   }
/*     */   
/*     */   private boolean tryParseYear(CharSequence txt, int tokenStart, int tokenEnd) {
/* 307 */     int len = tokenEnd - tokenStart;
/*     */     
/* 309 */     if (len == 2) {
/* 310 */       char c0 = txt.charAt(tokenStart);
/* 311 */       char c1 = txt.charAt(tokenStart + 1);
/* 312 */       if (isDigit(c0) && isDigit(c1)) {
/* 313 */         this.year = getNumericalValue(c0) * 10 + getNumericalValue(c1);
/* 314 */         return true;
/*     */       }
/*     */     
/* 317 */     } else if (len == 4) {
/* 318 */       char c0 = txt.charAt(tokenStart);
/* 319 */       char c1 = txt.charAt(tokenStart + 1);
/* 320 */       char c2 = txt.charAt(tokenStart + 2);
/* 321 */       char c3 = txt.charAt(tokenStart + 3);
/* 322 */       if (isDigit(c0) && isDigit(c1) && isDigit(c2) && isDigit(c3)) {
/* 323 */         this
/*     */ 
/*     */           
/* 326 */           .year = getNumericalValue(c0) * 1000 + getNumericalValue(c1) * 100 + getNumericalValue(c2) * 10 + getNumericalValue(c3);
/* 327 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 331 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean parseToken(CharSequence txt, int tokenStart, int tokenEnd) {
/* 336 */     if (!this.timeFound) {
/* 337 */       this.timeFound = tryParseTime(txt, tokenStart, tokenEnd);
/* 338 */       if (this.timeFound) {
/* 339 */         return (this.dayOfMonthFound && this.monthFound && this.yearFound);
/*     */       }
/*     */     } 
/*     */     
/* 343 */     if (!this.dayOfMonthFound) {
/* 344 */       this.dayOfMonthFound = tryParseDayOfMonth(txt, tokenStart, tokenEnd);
/* 345 */       if (this.dayOfMonthFound) {
/* 346 */         return (this.timeFound && this.monthFound && this.yearFound);
/*     */       }
/*     */     } 
/*     */     
/* 350 */     if (!this.monthFound) {
/* 351 */       this.monthFound = tryParseMonth(txt, tokenStart, tokenEnd);
/* 352 */       if (this.monthFound) {
/* 353 */         return (this.timeFound && this.dayOfMonthFound && this.yearFound);
/*     */       }
/*     */     } 
/*     */     
/* 357 */     if (!this.yearFound) {
/* 358 */       this.yearFound = tryParseYear(txt, tokenStart, tokenEnd);
/*     */     }
/* 360 */     return (this.timeFound && this.dayOfMonthFound && this.monthFound && this.yearFound);
/*     */   }
/*     */   
/*     */   private Date parse0(CharSequence txt, int start, int end) {
/* 364 */     boolean allPartsFound = parse1(txt, start, end);
/* 365 */     return (allPartsFound && normalizeAndValidate()) ? computeDate() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean parse1(CharSequence txt, int start, int end) {
/* 370 */     int tokenStart = -1;
/*     */     
/* 372 */     for (int i = start; i < end; i++) {
/* 373 */       char c = txt.charAt(i);
/*     */       
/* 375 */       if (isDelim(c)) {
/* 376 */         if (tokenStart != -1) {
/*     */           
/* 378 */           if (parseToken(txt, tokenStart, i)) {
/* 379 */             return true;
/*     */           }
/* 381 */           tokenStart = -1;
/*     */         } 
/* 383 */       } else if (tokenStart == -1) {
/*     */         
/* 385 */         tokenStart = i;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 390 */     return (tokenStart != -1 && parseToken(txt, tokenStart, txt.length()));
/*     */   }
/*     */   
/*     */   private boolean normalizeAndValidate() {
/* 394 */     if (this.dayOfMonth < 1 || this.dayOfMonth > 31 || this.hours > 23 || this.minutes > 59 || this.seconds > 59)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 399 */       return false;
/*     */     }
/*     */     
/* 402 */     if (this.year >= 70 && this.year <= 99) {
/* 403 */       this.year += 1900;
/* 404 */     } else if (this.year >= 0 && this.year < 70) {
/* 405 */       this.year += 2000;
/* 406 */     } else if (this.year < 1601) {
/*     */       
/* 408 */       return false;
/*     */     } 
/* 410 */     return true;
/*     */   }
/*     */   
/*     */   private Date computeDate() {
/* 414 */     this.cal.set(5, this.dayOfMonth);
/* 415 */     this.cal.set(2, this.month);
/* 416 */     this.cal.set(1, this.year);
/* 417 */     this.cal.set(11, this.hours);
/* 418 */     this.cal.set(12, this.minutes);
/* 419 */     this.cal.set(13, this.seconds);
/* 420 */     return this.cal.getTime();
/*     */   }
/*     */   
/*     */   private String format0(Date date) {
/* 424 */     append0(date, this.sb);
/* 425 */     return this.sb.toString();
/*     */   }
/*     */   
/*     */   private StringBuilder append0(Date date, StringBuilder sb) {
/* 429 */     this.cal.setTime(date);
/*     */     
/* 431 */     sb.append(DAY_OF_WEEK_TO_SHORT_NAME[this.cal.get(7) - 1]).append(", ");
/* 432 */     sb.append(this.cal.get(5)).append(' ');
/* 433 */     sb.append(CALENDAR_MONTH_TO_SHORT_NAME[this.cal.get(2)]).append(' ');
/* 434 */     sb.append(this.cal.get(1)).append(' ');
/* 435 */     appendZeroLeftPadded(this.cal.get(11), sb).append(':');
/* 436 */     appendZeroLeftPadded(this.cal.get(12), sb).append(':');
/* 437 */     return appendZeroLeftPadded(this.cal.get(13), sb).append(" GMT");
/*     */   }
/*     */   
/*     */   private static StringBuilder appendZeroLeftPadded(int value, StringBuilder sb) {
/* 441 */     if (value < 10) {
/* 442 */       sb.append('0');
/*     */     }
/* 444 */     return sb.append(value);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DateFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */