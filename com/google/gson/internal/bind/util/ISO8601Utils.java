/*     */ package com.google.gson.internal.bind.util;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
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
/*     */ public class ISO8601Utils
/*     */ {
/*     */   private static final String UTC_ID = "UTC";
/*  30 */   private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
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
/*     */   public static String format(Date date) {
/*  45 */     return format(date, false, TIMEZONE_UTC);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date date, boolean millis) {
/*  56 */     return format(date, millis, TIMEZONE_UTC);
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
/*     */   public static String format(Date date, boolean millis, TimeZone tz) {
/*  68 */     Calendar calendar = new GregorianCalendar(tz, Locale.US);
/*  69 */     calendar.setTime(date);
/*     */ 
/*     */     
/*  72 */     int capacity = "yyyy-MM-ddThh:mm:ss".length();
/*  73 */     capacity += millis ? ".sss".length() : 0;
/*  74 */     capacity += (tz.getRawOffset() == 0) ? "Z".length() : "+hh:mm".length();
/*  75 */     StringBuilder formatted = new StringBuilder(capacity);
/*     */     
/*  77 */     padInt(formatted, calendar.get(1), "yyyy".length());
/*  78 */     formatted.append('-');
/*  79 */     padInt(formatted, calendar.get(2) + 1, "MM".length());
/*  80 */     formatted.append('-');
/*  81 */     padInt(formatted, calendar.get(5), "dd".length());
/*  82 */     formatted.append('T');
/*  83 */     padInt(formatted, calendar.get(11), "hh".length());
/*  84 */     formatted.append(':');
/*  85 */     padInt(formatted, calendar.get(12), "mm".length());
/*  86 */     formatted.append(':');
/*  87 */     padInt(formatted, calendar.get(13), "ss".length());
/*  88 */     if (millis) {
/*  89 */       formatted.append('.');
/*  90 */       padInt(formatted, calendar.get(14), "sss".length());
/*     */     } 
/*     */     
/*  93 */     int offset = tz.getOffset(calendar.getTimeInMillis());
/*  94 */     if (offset != 0) {
/*  95 */       int hours = Math.abs(offset / 60000 / 60);
/*  96 */       int minutes = Math.abs(offset / 60000 % 60);
/*  97 */       formatted.append((offset < 0) ? 45 : 43);
/*  98 */       padInt(formatted, hours, "hh".length());
/*  99 */       formatted.append(':');
/* 100 */       padInt(formatted, minutes, "mm".length());
/*     */     } else {
/* 102 */       formatted.append('Z');
/*     */     } 
/*     */     
/* 105 */     return formatted.toString();
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
/*     */   
/*     */   public static Date parse(String date, ParsePosition pos) throws ParseException {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore_2
/*     */     //   2: aload_1
/*     */     //   3: invokevirtual getIndex : ()I
/*     */     //   6: istore_3
/*     */     //   7: aload_0
/*     */     //   8: iload_3
/*     */     //   9: iinc #3, 4
/*     */     //   12: iload_3
/*     */     //   13: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   16: istore #4
/*     */     //   18: aload_0
/*     */     //   19: iload_3
/*     */     //   20: bipush #45
/*     */     //   22: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   25: ifeq -> 31
/*     */     //   28: iinc #3, 1
/*     */     //   31: aload_0
/*     */     //   32: iload_3
/*     */     //   33: iinc #3, 2
/*     */     //   36: iload_3
/*     */     //   37: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   40: istore #5
/*     */     //   42: aload_0
/*     */     //   43: iload_3
/*     */     //   44: bipush #45
/*     */     //   46: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   49: ifeq -> 55
/*     */     //   52: iinc #3, 1
/*     */     //   55: aload_0
/*     */     //   56: iload_3
/*     */     //   57: iinc #3, 2
/*     */     //   60: iload_3
/*     */     //   61: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   64: istore #6
/*     */     //   66: iconst_0
/*     */     //   67: istore #7
/*     */     //   69: iconst_0
/*     */     //   70: istore #8
/*     */     //   72: iconst_0
/*     */     //   73: istore #9
/*     */     //   75: iconst_0
/*     */     //   76: istore #10
/*     */     //   78: aload_0
/*     */     //   79: iload_3
/*     */     //   80: bipush #84
/*     */     //   82: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   85: istore #11
/*     */     //   87: iload #11
/*     */     //   89: ifne -> 128
/*     */     //   92: aload_0
/*     */     //   93: invokevirtual length : ()I
/*     */     //   96: iload_3
/*     */     //   97: if_icmpgt -> 128
/*     */     //   100: new java/util/GregorianCalendar
/*     */     //   103: dup
/*     */     //   104: iload #4
/*     */     //   106: iload #5
/*     */     //   108: iconst_1
/*     */     //   109: isub
/*     */     //   110: iload #6
/*     */     //   112: invokespecial <init> : (III)V
/*     */     //   115: astore #12
/*     */     //   117: aload_1
/*     */     //   118: iload_3
/*     */     //   119: invokevirtual setIndex : (I)V
/*     */     //   122: aload #12
/*     */     //   124: invokevirtual getTime : ()Ljava/util/Date;
/*     */     //   127: areturn
/*     */     //   128: iload #11
/*     */     //   130: ifeq -> 347
/*     */     //   133: aload_0
/*     */     //   134: iinc #3, 1
/*     */     //   137: iload_3
/*     */     //   138: iinc #3, 2
/*     */     //   141: iload_3
/*     */     //   142: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   145: istore #7
/*     */     //   147: aload_0
/*     */     //   148: iload_3
/*     */     //   149: bipush #58
/*     */     //   151: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   154: ifeq -> 160
/*     */     //   157: iinc #3, 1
/*     */     //   160: aload_0
/*     */     //   161: iload_3
/*     */     //   162: iinc #3, 2
/*     */     //   165: iload_3
/*     */     //   166: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   169: istore #8
/*     */     //   171: aload_0
/*     */     //   172: iload_3
/*     */     //   173: bipush #58
/*     */     //   175: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   178: ifeq -> 184
/*     */     //   181: iinc #3, 1
/*     */     //   184: aload_0
/*     */     //   185: invokevirtual length : ()I
/*     */     //   188: iload_3
/*     */     //   189: if_icmple -> 347
/*     */     //   192: aload_0
/*     */     //   193: iload_3
/*     */     //   194: invokevirtual charAt : (I)C
/*     */     //   197: istore #12
/*     */     //   199: iload #12
/*     */     //   201: bipush #90
/*     */     //   203: if_icmpeq -> 347
/*     */     //   206: iload #12
/*     */     //   208: bipush #43
/*     */     //   210: if_icmpeq -> 347
/*     */     //   213: iload #12
/*     */     //   215: bipush #45
/*     */     //   217: if_icmpeq -> 347
/*     */     //   220: aload_0
/*     */     //   221: iload_3
/*     */     //   222: iinc #3, 2
/*     */     //   225: iload_3
/*     */     //   226: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   229: istore #9
/*     */     //   231: iload #9
/*     */     //   233: bipush #59
/*     */     //   235: if_icmple -> 249
/*     */     //   238: iload #9
/*     */     //   240: bipush #63
/*     */     //   242: if_icmpge -> 249
/*     */     //   245: bipush #59
/*     */     //   247: istore #9
/*     */     //   249: aload_0
/*     */     //   250: iload_3
/*     */     //   251: bipush #46
/*     */     //   253: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*     */     //   256: ifeq -> 347
/*     */     //   259: iinc #3, 1
/*     */     //   262: aload_0
/*     */     //   263: iload_3
/*     */     //   264: iconst_1
/*     */     //   265: iadd
/*     */     //   266: invokestatic indexOfNonDigit : (Ljava/lang/String;I)I
/*     */     //   269: istore #13
/*     */     //   271: iload #13
/*     */     //   273: iload_3
/*     */     //   274: iconst_3
/*     */     //   275: iadd
/*     */     //   276: invokestatic min : (II)I
/*     */     //   279: istore #14
/*     */     //   281: aload_0
/*     */     //   282: iload_3
/*     */     //   283: iload #14
/*     */     //   285: invokestatic parseInt : (Ljava/lang/String;II)I
/*     */     //   288: istore #15
/*     */     //   290: iload #14
/*     */     //   292: iload_3
/*     */     //   293: isub
/*     */     //   294: lookupswitch default -> 340, 1 -> 330, 2 -> 320
/*     */     //   320: iload #15
/*     */     //   322: bipush #10
/*     */     //   324: imul
/*     */     //   325: istore #10
/*     */     //   327: goto -> 344
/*     */     //   330: iload #15
/*     */     //   332: bipush #100
/*     */     //   334: imul
/*     */     //   335: istore #10
/*     */     //   337: goto -> 344
/*     */     //   340: iload #15
/*     */     //   342: istore #10
/*     */     //   344: iload #13
/*     */     //   346: istore_3
/*     */     //   347: aload_0
/*     */     //   348: invokevirtual length : ()I
/*     */     //   351: iload_3
/*     */     //   352: if_icmpgt -> 365
/*     */     //   355: new java/lang/IllegalArgumentException
/*     */     //   358: dup
/*     */     //   359: ldc 'No time zone indicator'
/*     */     //   361: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   364: athrow
/*     */     //   365: aconst_null
/*     */     //   366: astore #12
/*     */     //   368: aload_0
/*     */     //   369: iload_3
/*     */     //   370: invokevirtual charAt : (I)C
/*     */     //   373: istore #13
/*     */     //   375: iload #13
/*     */     //   377: bipush #90
/*     */     //   379: if_icmpne -> 393
/*     */     //   382: getstatic com/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
/*     */     //   385: astore #12
/*     */     //   387: iinc #3, 1
/*     */     //   390: goto -> 630
/*     */     //   393: iload #13
/*     */     //   395: bipush #43
/*     */     //   397: if_icmpeq -> 407
/*     */     //   400: iload #13
/*     */     //   402: bipush #45
/*     */     //   404: if_icmpne -> 597
/*     */     //   407: aload_0
/*     */     //   408: iload_3
/*     */     //   409: invokevirtual substring : (I)Ljava/lang/String;
/*     */     //   412: astore #14
/*     */     //   414: aload #14
/*     */     //   416: invokevirtual length : ()I
/*     */     //   419: iconst_5
/*     */     //   420: if_icmplt -> 428
/*     */     //   423: aload #14
/*     */     //   425: goto -> 448
/*     */     //   428: new java/lang/StringBuilder
/*     */     //   431: dup
/*     */     //   432: invokespecial <init> : ()V
/*     */     //   435: aload #14
/*     */     //   437: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   440: ldc '00'
/*     */     //   442: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   445: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   448: astore #14
/*     */     //   450: iload_3
/*     */     //   451: aload #14
/*     */     //   453: invokevirtual length : ()I
/*     */     //   456: iadd
/*     */     //   457: istore_3
/*     */     //   458: ldc '+0000'
/*     */     //   460: aload #14
/*     */     //   462: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   465: ifne -> 478
/*     */     //   468: ldc '+00:00'
/*     */     //   470: aload #14
/*     */     //   472: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   475: ifeq -> 486
/*     */     //   478: getstatic com/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
/*     */     //   481: astore #12
/*     */     //   483: goto -> 594
/*     */     //   486: new java/lang/StringBuilder
/*     */     //   489: dup
/*     */     //   490: invokespecial <init> : ()V
/*     */     //   493: ldc 'GMT'
/*     */     //   495: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   498: aload #14
/*     */     //   500: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   503: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   506: astore #15
/*     */     //   508: aload #15
/*     */     //   510: invokestatic getTimeZone : (Ljava/lang/String;)Ljava/util/TimeZone;
/*     */     //   513: astore #12
/*     */     //   515: aload #12
/*     */     //   517: invokevirtual getID : ()Ljava/lang/String;
/*     */     //   520: astore #16
/*     */     //   522: aload #16
/*     */     //   524: aload #15
/*     */     //   526: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   529: ifne -> 594
/*     */     //   532: aload #16
/*     */     //   534: ldc ':'
/*     */     //   536: ldc ''
/*     */     //   538: invokevirtual replace : (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*     */     //   541: astore #17
/*     */     //   543: aload #17
/*     */     //   545: aload #15
/*     */     //   547: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   550: ifne -> 594
/*     */     //   553: new java/lang/IndexOutOfBoundsException
/*     */     //   556: dup
/*     */     //   557: new java/lang/StringBuilder
/*     */     //   560: dup
/*     */     //   561: invokespecial <init> : ()V
/*     */     //   564: ldc 'Mismatching time zone indicator: '
/*     */     //   566: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   569: aload #15
/*     */     //   571: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   574: ldc ' given, resolves to '
/*     */     //   576: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   579: aload #12
/*     */     //   581: invokevirtual getID : ()Ljava/lang/String;
/*     */     //   584: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   587: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   590: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   593: athrow
/*     */     //   594: goto -> 630
/*     */     //   597: new java/lang/IndexOutOfBoundsException
/*     */     //   600: dup
/*     */     //   601: new java/lang/StringBuilder
/*     */     //   604: dup
/*     */     //   605: invokespecial <init> : ()V
/*     */     //   608: ldc 'Invalid time zone indicator ''
/*     */     //   610: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   613: iload #13
/*     */     //   615: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   618: ldc '''
/*     */     //   620: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   623: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   626: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   629: athrow
/*     */     //   630: new java/util/GregorianCalendar
/*     */     //   633: dup
/*     */     //   634: aload #12
/*     */     //   636: invokespecial <init> : (Ljava/util/TimeZone;)V
/*     */     //   639: astore #14
/*     */     //   641: aload #14
/*     */     //   643: iconst_0
/*     */     //   644: invokevirtual setLenient : (Z)V
/*     */     //   647: aload #14
/*     */     //   649: iconst_1
/*     */     //   650: iload #4
/*     */     //   652: invokevirtual set : (II)V
/*     */     //   655: aload #14
/*     */     //   657: iconst_2
/*     */     //   658: iload #5
/*     */     //   660: iconst_1
/*     */     //   661: isub
/*     */     //   662: invokevirtual set : (II)V
/*     */     //   665: aload #14
/*     */     //   667: iconst_5
/*     */     //   668: iload #6
/*     */     //   670: invokevirtual set : (II)V
/*     */     //   673: aload #14
/*     */     //   675: bipush #11
/*     */     //   677: iload #7
/*     */     //   679: invokevirtual set : (II)V
/*     */     //   682: aload #14
/*     */     //   684: bipush #12
/*     */     //   686: iload #8
/*     */     //   688: invokevirtual set : (II)V
/*     */     //   691: aload #14
/*     */     //   693: bipush #13
/*     */     //   695: iload #9
/*     */     //   697: invokevirtual set : (II)V
/*     */     //   700: aload #14
/*     */     //   702: bipush #14
/*     */     //   704: iload #10
/*     */     //   706: invokevirtual set : (II)V
/*     */     //   709: aload_1
/*     */     //   710: iload_3
/*     */     //   711: invokevirtual setIndex : (I)V
/*     */     //   714: aload #14
/*     */     //   716: invokevirtual getTime : ()Ljava/util/Date;
/*     */     //   719: areturn
/*     */     //   720: astore_3
/*     */     //   721: aload_3
/*     */     //   722: astore_2
/*     */     //   723: goto -> 735
/*     */     //   726: astore_3
/*     */     //   727: aload_3
/*     */     //   728: astore_2
/*     */     //   729: goto -> 735
/*     */     //   732: astore_3
/*     */     //   733: aload_3
/*     */     //   734: astore_2
/*     */     //   735: aload_0
/*     */     //   736: ifnonnull -> 743
/*     */     //   739: aconst_null
/*     */     //   740: goto -> 767
/*     */     //   743: new java/lang/StringBuilder
/*     */     //   746: dup
/*     */     //   747: invokespecial <init> : ()V
/*     */     //   750: bipush #34
/*     */     //   752: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   755: aload_0
/*     */     //   756: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   759: bipush #34
/*     */     //   761: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   764: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   767: astore_3
/*     */     //   768: aload_2
/*     */     //   769: invokevirtual getMessage : ()Ljava/lang/String;
/*     */     //   772: astore #4
/*     */     //   774: aload #4
/*     */     //   776: ifnull -> 787
/*     */     //   779: aload #4
/*     */     //   781: invokevirtual isEmpty : ()Z
/*     */     //   784: ifeq -> 819
/*     */     //   787: new java/lang/StringBuilder
/*     */     //   790: dup
/*     */     //   791: invokespecial <init> : ()V
/*     */     //   794: ldc '('
/*     */     //   796: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   799: aload_2
/*     */     //   800: invokevirtual getClass : ()Ljava/lang/Class;
/*     */     //   803: invokevirtual getName : ()Ljava/lang/String;
/*     */     //   806: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   809: ldc ')'
/*     */     //   811: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   814: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   817: astore #4
/*     */     //   819: new java/text/ParseException
/*     */     //   822: dup
/*     */     //   823: new java/lang/StringBuilder
/*     */     //   826: dup
/*     */     //   827: invokespecial <init> : ()V
/*     */     //   830: ldc 'Failed to parse date ['
/*     */     //   832: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   835: aload_3
/*     */     //   836: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   839: ldc_w ']: '
/*     */     //   842: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   845: aload #4
/*     */     //   847: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   850: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   853: aload_1
/*     */     //   854: invokevirtual getIndex : ()I
/*     */     //   857: invokespecial <init> : (Ljava/lang/String;I)V
/*     */     //   860: astore #5
/*     */     //   862: aload #5
/*     */     //   864: aload_2
/*     */     //   865: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*     */     //   868: pop
/*     */     //   869: aload #5
/*     */     //   871: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #124	-> 0
/*     */     //   #126	-> 2
/*     */     //   #129	-> 7
/*     */     //   #130	-> 18
/*     */     //   #131	-> 28
/*     */     //   #135	-> 31
/*     */     //   #136	-> 42
/*     */     //   #137	-> 52
/*     */     //   #141	-> 55
/*     */     //   #143	-> 66
/*     */     //   #144	-> 69
/*     */     //   #145	-> 72
/*     */     //   #146	-> 75
/*     */     //   #149	-> 78
/*     */     //   #151	-> 87
/*     */     //   #152	-> 100
/*     */     //   #154	-> 117
/*     */     //   #155	-> 122
/*     */     //   #158	-> 128
/*     */     //   #161	-> 133
/*     */     //   #162	-> 147
/*     */     //   #163	-> 157
/*     */     //   #166	-> 160
/*     */     //   #167	-> 171
/*     */     //   #168	-> 181
/*     */     //   #171	-> 184
/*     */     //   #172	-> 192
/*     */     //   #173	-> 199
/*     */     //   #174	-> 220
/*     */     //   #175	-> 231
/*     */     //   #177	-> 249
/*     */     //   #178	-> 259
/*     */     //   #179	-> 262
/*     */     //   #180	-> 271
/*     */     //   #181	-> 281
/*     */     //   #183	-> 290
/*     */     //   #185	-> 320
/*     */     //   #186	-> 327
/*     */     //   #188	-> 330
/*     */     //   #189	-> 337
/*     */     //   #191	-> 340
/*     */     //   #193	-> 344
/*     */     //   #200	-> 347
/*     */     //   #201	-> 355
/*     */     //   #204	-> 365
/*     */     //   #205	-> 368
/*     */     //   #207	-> 375
/*     */     //   #208	-> 382
/*     */     //   #209	-> 387
/*     */     //   #210	-> 393
/*     */     //   #211	-> 407
/*     */     //   #214	-> 414
/*     */     //   #216	-> 450
/*     */     //   #218	-> 458
/*     */     //   #219	-> 478
/*     */     //   #225	-> 486
/*     */     //   #228	-> 508
/*     */     //   #230	-> 515
/*     */     //   #231	-> 522
/*     */     //   #237	-> 532
/*     */     //   #238	-> 543
/*     */     //   #239	-> 553
/*     */     //   #240	-> 581
/*     */     //   #244	-> 594
/*     */     //   #245	-> 597
/*     */     //   #248	-> 630
/*     */     //   #249	-> 641
/*     */     //   #250	-> 647
/*     */     //   #251	-> 655
/*     */     //   #252	-> 665
/*     */     //   #253	-> 673
/*     */     //   #254	-> 682
/*     */     //   #255	-> 691
/*     */     //   #256	-> 700
/*     */     //   #258	-> 709
/*     */     //   #259	-> 714
/*     */     //   #262	-> 720
/*     */     //   #263	-> 721
/*     */     //   #268	-> 723
/*     */     //   #264	-> 726
/*     */     //   #265	-> 727
/*     */     //   #268	-> 729
/*     */     //   #266	-> 732
/*     */     //   #267	-> 733
/*     */     //   #269	-> 735
/*     */     //   #270	-> 768
/*     */     //   #271	-> 774
/*     */     //   #272	-> 787
/*     */     //   #274	-> 819
/*     */     //   #275	-> 862
/*     */     //   #276	-> 869
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   117	11	12	calendar	Ljava/util/Calendar;
/*     */     //   271	76	13	endOffset	I
/*     */     //   281	66	14	parseEndOffset	I
/*     */     //   290	57	15	fraction	I
/*     */     //   199	148	12	c	C
/*     */     //   543	51	17	cleaned	Ljava/lang/String;
/*     */     //   508	86	15	timezoneId	Ljava/lang/String;
/*     */     //   522	72	16	act	Ljava/lang/String;
/*     */     //   414	180	14	timezoneOffset	Ljava/lang/String;
/*     */     //   7	713	3	offset	I
/*     */     //   18	702	4	year	I
/*     */     //   42	678	5	month	I
/*     */     //   66	654	6	day	I
/*     */     //   69	651	7	hour	I
/*     */     //   72	648	8	minutes	I
/*     */     //   75	645	9	seconds	I
/*     */     //   78	642	10	milliseconds	I
/*     */     //   87	633	11	hasT	Z
/*     */     //   368	352	12	timezone	Ljava/util/TimeZone;
/*     */     //   375	345	13	timezoneIndicator	C
/*     */     //   641	79	14	calendar	Ljava/util/Calendar;
/*     */     //   721	2	3	e	Ljava/lang/IndexOutOfBoundsException;
/*     */     //   727	2	3	e	Ljava/lang/NumberFormatException;
/*     */     //   733	2	3	e	Ljava/lang/IllegalArgumentException;
/*     */     //   0	872	0	date	Ljava/lang/String;
/*     */     //   0	872	1	pos	Ljava/text/ParsePosition;
/*     */     //   2	870	2	fail	Ljava/lang/Exception;
/*     */     //   768	104	3	input	Ljava/lang/String;
/*     */     //   774	98	4	msg	Ljava/lang/String;
/*     */     //   862	10	5	ex	Ljava/text/ParseException;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   2	127	720	java/lang/IndexOutOfBoundsException
/*     */     //   2	127	726	java/lang/NumberFormatException
/*     */     //   2	127	732	java/lang/IllegalArgumentException
/*     */     //   128	719	720	java/lang/IndexOutOfBoundsException
/*     */     //   128	719	726	java/lang/NumberFormatException
/*     */     //   128	719	732	java/lang/IllegalArgumentException
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
/*     */   private static boolean checkOffset(String value, int offset, char expected) {
/* 288 */     return (offset < value.length() && value.charAt(offset) == expected);
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
/*     */   private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
/* 301 */     if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
/* 302 */       throw new NumberFormatException(value);
/*     */     }
/*     */     
/* 305 */     int i = beginIndex;
/* 306 */     int result = 0;
/*     */     
/* 308 */     if (i < endIndex) {
/* 309 */       int digit = Character.digit(value.charAt(i++), 10);
/* 310 */       if (digit < 0) {
/* 311 */         throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
/*     */       }
/* 313 */       result = -digit;
/*     */     } 
/* 315 */     while (i < endIndex) {
/* 316 */       int digit = Character.digit(value.charAt(i++), 10);
/* 317 */       if (digit < 0) {
/* 318 */         throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
/*     */       }
/* 320 */       result *= 10;
/* 321 */       result -= digit;
/*     */     } 
/* 323 */     return -result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void padInt(StringBuilder buffer, int value, int length) {
/* 334 */     String strValue = Integer.toString(value);
/* 335 */     for (int i = length - strValue.length(); i > 0; i--) {
/* 336 */       buffer.append('0');
/*     */     }
/* 338 */     buffer.append(strValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int indexOfNonDigit(String string, int offset) {
/* 345 */     for (int i = offset; i < string.length(); i++) {
/* 346 */       char c = string.charAt(i);
/* 347 */       if (c < '0' || c > '9') return i; 
/*     */     } 
/* 349 */     return string.length();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bin\\util\ISO8601Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */