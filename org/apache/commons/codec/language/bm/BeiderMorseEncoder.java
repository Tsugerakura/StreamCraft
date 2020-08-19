/*     */ package org.apache.commons.codec.language.bm;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeiderMorseEncoder
/*     */   implements StringEncoder
/*     */ {
/*  84 */   private PhoneticEngine engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
/*     */ 
/*     */   
/*     */   public Object encode(Object source) throws EncoderException {
/*  88 */     if (!(source instanceof String)) {
/*  89 */       throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
/*     */     }
/*  91 */     return encode((String)source);
/*     */   }
/*     */ 
/*     */   
/*     */   public String encode(String source) throws EncoderException {
/*  96 */     if (source == null) {
/*  97 */       return null;
/*     */     }
/*  99 */     return this.engine.encode(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NameType getNameType() {
/* 108 */     return this.engine.getNameType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleType getRuleType() {
/* 117 */     return this.engine.getRuleType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConcat() {
/* 126 */     return this.engine.isConcat();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConcat(boolean concat) {
/* 137 */     this
/*     */ 
/*     */       
/* 140 */       .engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat, this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNameType(NameType nameType) {
/* 151 */     this
/*     */ 
/*     */       
/* 154 */       .engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat(), this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRuleType(RuleType ruleType) {
/* 164 */     this
/*     */ 
/*     */       
/* 167 */       .engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat(), this.engine.getMaxPhonemes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxPhonemes(int maxPhonemes) {
/* 178 */     this
/*     */       
/* 180 */       .engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), this.engine.isConcat(), maxPhonemes);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\bm\BeiderMorseEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */