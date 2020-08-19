/*     */ package org.apache.commons.codec.language;
/*     */ 
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
/*     */ public class Soundex
/*     */   implements StringEncoder
/*     */ {
/*     */   public static final char SILENT_MARKER = '-';
/*     */   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
/*  68 */   private static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final Soundex US_ENGLISH = new Soundex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final Soundex US_ENGLISH_SIMPLIFIED = new Soundex("01230120022455012623010202", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   public static final Soundex US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 115 */   private int maxLength = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final char[] soundexMapping;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean specialCaseHW;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Soundex() {
/* 140 */     this.soundexMapping = US_ENGLISH_MAPPING;
/* 141 */     this.specialCaseHW = true;
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
/*     */   public Soundex(char[] mapping) {
/* 157 */     this.soundexMapping = new char[mapping.length];
/* 158 */     System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
/* 159 */     this.specialCaseHW = !hasMarker(this.soundexMapping);
/*     */   }
/*     */   
/*     */   private boolean hasMarker(char[] mapping) {
/* 163 */     for (char ch : mapping) {
/* 164 */       if (ch == '-') {
/* 165 */         return true;
/*     */       }
/*     */     } 
/* 168 */     return false;
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
/*     */   public Soundex(String mapping) {
/* 182 */     this.soundexMapping = mapping.toCharArray();
/* 183 */     this.specialCaseHW = !hasMarker(this.soundexMapping);
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
/*     */   public Soundex(String mapping, boolean specialCaseHW) {
/* 196 */     this.soundexMapping = mapping.toCharArray();
/* 197 */     this.specialCaseHW = specialCaseHW;
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
/*     */   public int difference(String s1, String s2) throws EncoderException {
/* 220 */     return SoundexUtils.difference(this, s1, s2);
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
/* 238 */     if (!(obj instanceof String)) {
/* 239 */       throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
/*     */     }
/* 241 */     return soundex((String)obj);
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
/* 255 */     return soundex(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getMaxLength() {
/* 266 */     return this.maxLength;
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
/*     */   private char map(char ch) {
/* 279 */     int index = ch - 65;
/* 280 */     if (index < 0 || index >= this.soundexMapping.length) {
/* 281 */       throw new IllegalArgumentException("The character is not mapped: " + ch + " (index=" + index + ")");
/*     */     }
/* 283 */     return this.soundexMapping[index];
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
/*     */   public void setMaxLength(int maxLength) {
/* 295 */     this.maxLength = maxLength;
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
/*     */   public String soundex(String str) {
/* 308 */     if (str == null) {
/* 309 */       return null;
/*     */     }
/* 311 */     str = SoundexUtils.clean(str);
/* 312 */     if (str.length() == 0) {
/* 313 */       return str;
/*     */     }
/* 315 */     char[] out = { '0', '0', '0', '0' };
/* 316 */     int count = 0;
/* 317 */     char first = str.charAt(0);
/* 318 */     out[count++] = first;
/* 319 */     char lastDigit = map(first);
/* 320 */     for (int i = 1; i < str.length() && count < out.length; i++) {
/* 321 */       char ch = str.charAt(i);
/* 322 */       if (!this.specialCaseHW || (ch != 'H' && ch != 'W')) {
/*     */ 
/*     */         
/* 325 */         char digit = map(ch);
/* 326 */         if (digit != '-')
/*     */         
/*     */         { 
/* 329 */           if (digit != '0' && digit != lastDigit) {
/* 330 */             out[count++] = digit;
/*     */           }
/* 332 */           lastDigit = digit; } 
/*     */       } 
/* 334 */     }  return new String(out);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\Soundex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */