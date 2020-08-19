/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class CookieUtil
/*     */ {
/*  25 */   private static final BitSet VALID_COOKIE_NAME_OCTETS = validCookieNameOctets();
/*     */   
/*  27 */   private static final BitSet VALID_COOKIE_VALUE_OCTETS = validCookieValueOctets();
/*     */   
/*  29 */   private static final BitSet VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS = validCookieAttributeValueOctets();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitSet validCookieNameOctets() {
/*  37 */     BitSet bits = new BitSet();
/*  38 */     for (int i = 32; i < 127; i++) {
/*  39 */       bits.set(i);
/*     */     }
/*  41 */     int[] separators = { 40, 41, 60, 62, 64, 44, 59, 58, 92, 34, 47, 91, 93, 63, 61, 123, 125, 32, 9 };
/*     */     
/*  43 */     for (int separator : separators) {
/*  44 */       bits.set(separator, false);
/*     */     }
/*  46 */     return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitSet validCookieValueOctets() {
/*  52 */     BitSet bits = new BitSet();
/*  53 */     bits.set(33); int i;
/*  54 */     for (i = 35; i <= 43; i++) {
/*  55 */       bits.set(i);
/*     */     }
/*  57 */     for (i = 45; i <= 58; i++) {
/*  58 */       bits.set(i);
/*     */     }
/*  60 */     for (i = 60; i <= 91; i++) {
/*  61 */       bits.set(i);
/*     */     }
/*  63 */     for (i = 93; i <= 126; i++) {
/*  64 */       bits.set(i);
/*     */     }
/*  66 */     return bits;
/*     */   }
/*     */ 
/*     */   
/*     */   private static BitSet validCookieAttributeValueOctets() {
/*  71 */     BitSet bits = new BitSet();
/*  72 */     for (int i = 32; i < 127; i++) {
/*  73 */       bits.set(i);
/*     */     }
/*  75 */     bits.set(59, false);
/*  76 */     return bits;
/*     */   }
/*     */   
/*     */   static StringBuilder stringBuilder() {
/*  80 */     return InternalThreadLocalMap.get().stringBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String stripTrailingSeparatorOrNull(StringBuilder buf) {
/*  88 */     return (buf.length() == 0) ? null : stripTrailingSeparator(buf);
/*     */   }
/*     */   
/*     */   static String stripTrailingSeparator(StringBuilder buf) {
/*  92 */     if (buf.length() > 0) {
/*  93 */       buf.setLength(buf.length() - 2);
/*     */     }
/*  95 */     return buf.toString();
/*     */   }
/*     */   
/*     */   static void add(StringBuilder sb, String name, long val) {
/*  99 */     sb.append(name);
/* 100 */     sb.append('=');
/* 101 */     sb.append(val);
/* 102 */     sb.append(';');
/* 103 */     sb.append(' ');
/*     */   }
/*     */   
/*     */   static void add(StringBuilder sb, String name, String val) {
/* 107 */     sb.append(name);
/* 108 */     sb.append('=');
/* 109 */     sb.append(val);
/* 110 */     sb.append(';');
/* 111 */     sb.append(' ');
/*     */   }
/*     */   
/*     */   static void add(StringBuilder sb, String name) {
/* 115 */     sb.append(name);
/* 116 */     sb.append(';');
/* 117 */     sb.append(' ');
/*     */   }
/*     */   
/*     */   static void addQuoted(StringBuilder sb, String name, String val) {
/* 121 */     if (val == null) {
/* 122 */       val = "";
/*     */     }
/*     */     
/* 125 */     sb.append(name);
/* 126 */     sb.append('=');
/* 127 */     sb.append('"');
/* 128 */     sb.append(val);
/* 129 */     sb.append('"');
/* 130 */     sb.append(';');
/* 131 */     sb.append(' ');
/*     */   }
/*     */   
/*     */   static int firstInvalidCookieNameOctet(CharSequence cs) {
/* 135 */     return firstInvalidOctet(cs, VALID_COOKIE_NAME_OCTETS);
/*     */   }
/*     */   
/*     */   static int firstInvalidCookieValueOctet(CharSequence cs) {
/* 139 */     return firstInvalidOctet(cs, VALID_COOKIE_VALUE_OCTETS);
/*     */   }
/*     */   
/*     */   static int firstInvalidOctet(CharSequence cs, BitSet bits) {
/* 143 */     for (int i = 0; i < cs.length(); i++) {
/* 144 */       char c = cs.charAt(i);
/* 145 */       if (!bits.get(c)) {
/* 146 */         return i;
/*     */       }
/*     */     } 
/* 149 */     return -1;
/*     */   }
/*     */   
/*     */   static CharSequence unwrapValue(CharSequence cs) {
/* 153 */     int len = cs.length();
/* 154 */     if (len > 0 && cs.charAt(0) == '"') {
/* 155 */       if (len >= 2 && cs.charAt(len - 1) == '"')
/*     */       {
/* 157 */         return (len == 2) ? "" : cs.subSequence(1, len - 1);
/*     */       }
/* 159 */       return null;
/*     */     } 
/*     */     
/* 162 */     return cs;
/*     */   }
/*     */   
/*     */   static String validateAttributeValue(String name, String value) {
/* 166 */     if (value == null) {
/* 167 */       return null;
/*     */     }
/* 169 */     value = value.trim();
/* 170 */     if (value.isEmpty()) {
/* 171 */       return null;
/*     */     }
/* 173 */     int i = firstInvalidOctet(value, VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS);
/* 174 */     if (i != -1) {
/* 175 */       throw new IllegalArgumentException(name + " contains the prohibited characters: " + value.charAt(i));
/*     */     }
/* 177 */     return value;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\CookieUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */