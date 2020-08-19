/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Nysiis
/*     */   implements StringEncoder
/*     */ {
/*  72 */   private static final char[] CHARS_A = new char[] { 'A' };
/*  73 */   private static final char[] CHARS_AF = new char[] { 'A', 'F' };
/*  74 */   private static final char[] CHARS_C = new char[] { 'C' };
/*  75 */   private static final char[] CHARS_FF = new char[] { 'F', 'F' };
/*  76 */   private static final char[] CHARS_G = new char[] { 'G' };
/*  77 */   private static final char[] CHARS_N = new char[] { 'N' };
/*  78 */   private static final char[] CHARS_NN = new char[] { 'N', 'N' };
/*  79 */   private static final char[] CHARS_S = new char[] { 'S' };
/*  80 */   private static final char[] CHARS_SSS = new char[] { 'S', 'S', 'S' };
/*     */   
/*  82 */   private static final Pattern PAT_MAC = Pattern.compile("^MAC");
/*  83 */   private static final Pattern PAT_KN = Pattern.compile("^KN");
/*  84 */   private static final Pattern PAT_K = Pattern.compile("^K");
/*  85 */   private static final Pattern PAT_PH_PF = Pattern.compile("^(PH|PF)");
/*  86 */   private static final Pattern PAT_SCH = Pattern.compile("^SCH");
/*  87 */   private static final Pattern PAT_EE_IE = Pattern.compile("(EE|IE)$");
/*  88 */   private static final Pattern PAT_DT_ETC = Pattern.compile("(DT|RT|RD|NT|ND)$");
/*     */ 
/*     */   
/*     */   private static final char SPACE = ' ';
/*     */ 
/*     */   
/*     */   private static final int TRUE_LENGTH = 6;
/*     */ 
/*     */   
/*     */   private final boolean strict;
/*     */ 
/*     */   
/*     */   private static boolean isVowel(char c) {
/* 101 */     return (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U');
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
/*     */   private static char[] transcodeRemaining(char prev, char curr, char next, char aNext) {
/* 120 */     if (curr == 'E' && next == 'V') {
/* 121 */       return CHARS_AF;
/*     */     }
/*     */ 
/*     */     
/* 125 */     if (isVowel(curr)) {
/* 126 */       return CHARS_A;
/*     */     }
/*     */ 
/*     */     
/* 130 */     if (curr == 'Q')
/* 131 */       return CHARS_G; 
/* 132 */     if (curr == 'Z')
/* 133 */       return CHARS_S; 
/* 134 */     if (curr == 'M') {
/* 135 */       return CHARS_N;
/*     */     }
/*     */ 
/*     */     
/* 139 */     if (curr == 'K') {
/* 140 */       if (next == 'N') {
/* 141 */         return CHARS_NN;
/*     */       }
/* 143 */       return CHARS_C;
/*     */     } 
/*     */ 
/*     */     
/* 147 */     if (curr == 'S' && next == 'C' && aNext == 'H') {
/* 148 */       return CHARS_SSS;
/*     */     }
/*     */ 
/*     */     
/* 152 */     if (curr == 'P' && next == 'H') {
/* 153 */       return CHARS_FF;
/*     */     }
/*     */ 
/*     */     
/* 157 */     if (curr == 'H' && (!isVowel(prev) || !isVowel(next))) {
/* 158 */       return new char[] { prev };
/*     */     }
/*     */ 
/*     */     
/* 162 */     if (curr == 'W' && isVowel(prev)) {
/* 163 */       return new char[] { prev };
/*     */     }
/*     */     
/* 166 */     return new char[] { curr };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Nysiis() {
/* 177 */     this(true);
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
/*     */   public Nysiis(boolean strict) {
/* 192 */     this.strict = strict;
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 210 */     if (!(obj instanceof String)) {
/* 211 */       throw new EncoderException("Parameter supplied to Nysiis encode is not of type java.lang.String");
/*     */     }
/* 213 */     return nysiis((String)obj);
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
/*     */   public String encode(String str) {
/* 227 */     return nysiis(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStrict() {
/* 236 */     return this.strict;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nysiis(String str) {
/* 247 */     if (str == null) {
/* 248 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 252 */     str = SoundexUtils.clean(str);
/*     */     
/* 254 */     if (str.length() == 0) {
/* 255 */       return str;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 260 */     str = PAT_MAC.matcher(str).replaceFirst("MCC");
/* 261 */     str = PAT_KN.matcher(str).replaceFirst("NN");
/* 262 */     str = PAT_K.matcher(str).replaceFirst("C");
/* 263 */     str = PAT_PH_PF.matcher(str).replaceFirst("FF");
/* 264 */     str = PAT_SCH.matcher(str).replaceFirst("SSS");
/*     */ 
/*     */ 
/*     */     
/* 268 */     str = PAT_EE_IE.matcher(str).replaceFirst("Y");
/* 269 */     str = PAT_DT_ETC.matcher(str).replaceFirst("D");
/*     */ 
/*     */     
/* 272 */     StringBuilder key = new StringBuilder(str.length());
/* 273 */     key.append(str.charAt(0));
/*     */ 
/*     */     
/* 276 */     char[] chars = str.toCharArray();
/* 277 */     int len = chars.length;
/*     */     
/* 279 */     for (int i = 1; i < len; i++) {
/* 280 */       char next = (i < len - 1) ? chars[i + 1] : ' ';
/* 281 */       char aNext = (i < len - 2) ? chars[i + 2] : ' ';
/* 282 */       char[] transcoded = transcodeRemaining(chars[i - 1], chars[i], next, aNext);
/* 283 */       System.arraycopy(transcoded, 0, chars, i, transcoded.length);
/*     */ 
/*     */       
/* 286 */       if (chars[i] != chars[i - 1]) {
/* 287 */         key.append(chars[i]);
/*     */       }
/*     */     } 
/*     */     
/* 291 */     if (key.length() > 1) {
/* 292 */       char lastChar = key.charAt(key.length() - 1);
/*     */ 
/*     */       
/* 295 */       if (lastChar == 'S') {
/* 296 */         key.deleteCharAt(key.length() - 1);
/* 297 */         lastChar = key.charAt(key.length() - 1);
/*     */       } 
/*     */       
/* 300 */       if (key.length() > 2) {
/* 301 */         char last2Char = key.charAt(key.length() - 2);
/*     */         
/* 303 */         if (last2Char == 'A' && lastChar == 'Y') {
/* 304 */           key.deleteCharAt(key.length() - 2);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 309 */       if (lastChar == 'A') {
/* 310 */         key.deleteCharAt(key.length() - 1);
/*     */       }
/*     */     } 
/*     */     
/* 314 */     String string = key.toString();
/* 315 */     return isStrict() ? string.substring(0, Math.min(6, string.length())) : string;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\Nysiis.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */