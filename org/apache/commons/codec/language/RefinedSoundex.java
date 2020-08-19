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
/*     */ public class RefinedSoundex
/*     */   implements StringEncoder
/*     */ {
/*     */   public static final String US_ENGLISH_MAPPING_STRING = "01360240043788015936020505";
/*  58 */   private static final char[] US_ENGLISH_MAPPING = "01360240043788015936020505".toCharArray();
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
/*  71 */   public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RefinedSoundex() {
/*  78 */     this.soundexMapping = US_ENGLISH_MAPPING;
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
/*     */   public RefinedSoundex(char[] mapping) {
/*  91 */     this.soundexMapping = new char[mapping.length];
/*  92 */     System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
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
/*     */   public RefinedSoundex(String mapping) {
/* 104 */     this.soundexMapping = mapping.toCharArray();
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
/*     */   public int difference(String s1, String s2) throws EncoderException {
/* 130 */     return SoundexUtils.difference(this, s1, s2);
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
/* 148 */     if (!(obj instanceof String)) {
/* 149 */       throw new EncoderException("Parameter supplied to RefinedSoundex encode is not of type java.lang.String");
/*     */     }
/* 151 */     return soundex((String)obj);
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
/*     */   public String encode(String str) {
/* 163 */     return soundex(str);
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
/*     */   char getMappingCode(char c) {
/* 176 */     if (!Character.isLetter(c)) {
/* 177 */       return Character.MIN_VALUE;
/*     */     }
/* 179 */     return this.soundexMapping[Character.toUpperCase(c) - 65];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String soundex(String str) {
/* 190 */     if (str == null) {
/* 191 */       return null;
/*     */     }
/* 193 */     str = SoundexUtils.clean(str);
/* 194 */     if (str.length() == 0) {
/* 195 */       return str;
/*     */     }
/*     */     
/* 198 */     StringBuilder sBuf = new StringBuilder();
/* 199 */     sBuf.append(str.charAt(0));
/*     */ 
/*     */     
/* 202 */     char last = '*';
/*     */     
/* 204 */     for (int i = 0; i < str.length(); i++) {
/*     */       
/* 206 */       char current = getMappingCode(str.charAt(i));
/* 207 */       if (current != last) {
/*     */         
/* 209 */         if (current != '\000') {
/* 210 */           sBuf.append(current);
/*     */         }
/*     */         
/* 213 */         last = current;
/*     */       } 
/*     */     } 
/*     */     
/* 217 */     return sBuf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\RefinedSoundex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */