/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
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
/*     */ public class DaitchMokotoffSoundex
/*     */   implements StringEncoder
/*     */ {
/*     */   private static final String COMMENT = "//";
/*     */   private static final String DOUBLE_QUOTE = "\"";
/*     */   private static final String MULTILINE_COMMENT_END = "*/";
/*     */   private static final String MULTILINE_COMMENT_START = "/*";
/*     */   private static final String RESOURCE_FILE = "org/apache/commons/codec/language/dmrules.txt";
/*     */   private static final int MAX_LENGTH = 6;
/*     */   
/*     */   private static final class Branch
/*     */   {
/*  83 */     private final StringBuilder builder = new StringBuilder();
/*  84 */     private String lastReplacement = null;
/*  85 */     private String cachedString = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Branch createBranch() {
/*  94 */       Branch branch = new Branch();
/*  95 */       branch.builder.append(toString());
/*  96 */       branch.lastReplacement = this.lastReplacement;
/*  97 */       return branch;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 102 */       if (this == other) {
/* 103 */         return true;
/*     */       }
/* 105 */       if (!(other instanceof Branch)) {
/* 106 */         return false;
/*     */       }
/*     */       
/* 109 */       return toString().equals(((Branch)other).toString());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void finish() {
/* 116 */       while (this.builder.length() < 6) {
/* 117 */         this.builder.append('0');
/* 118 */         this.cachedString = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 124 */       return toString().hashCode();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void processNextReplacement(String replacement, boolean forceAppend) {
/* 136 */       boolean append = (this.lastReplacement == null || !this.lastReplacement.endsWith(replacement) || forceAppend);
/*     */       
/* 138 */       if (append && this.builder.length() < 6) {
/* 139 */         this.builder.append(replacement);
/*     */         
/* 141 */         if (this.builder.length() > 6) {
/* 142 */           this.builder.delete(6, this.builder.length());
/*     */         }
/* 144 */         this.cachedString = null;
/*     */       } 
/*     */       
/* 147 */       this.lastReplacement = replacement;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 152 */       if (this.cachedString == null) {
/* 153 */         this.cachedString = this.builder.toString();
/*     */       }
/* 155 */       return this.cachedString;
/*     */     }
/*     */ 
/*     */     
/*     */     private Branch() {}
/*     */   }
/*     */   
/*     */   private static final class Rule
/*     */   {
/*     */     private final String pattern;
/*     */     private final String[] replacementAtStart;
/*     */     private final String[] replacementBeforeVowel;
/*     */     private final String[] replacementDefault;
/*     */     
/*     */     protected Rule(String pattern, String replacementAtStart, String replacementBeforeVowel, String replacementDefault) {
/* 170 */       this.pattern = pattern;
/* 171 */       this.replacementAtStart = replacementAtStart.split("\\|");
/* 172 */       this.replacementBeforeVowel = replacementBeforeVowel.split("\\|");
/* 173 */       this.replacementDefault = replacementDefault.split("\\|");
/*     */     }
/*     */     
/*     */     public int getPatternLength() {
/* 177 */       return this.pattern.length();
/*     */     }
/*     */     
/*     */     public String[] getReplacements(String context, boolean atStart) {
/* 181 */       if (atStart) {
/* 182 */         return this.replacementAtStart;
/*     */       }
/*     */       
/* 185 */       int nextIndex = getPatternLength();
/* 186 */       boolean nextCharIsVowel = (nextIndex < context.length()) ? isVowel(context.charAt(nextIndex)) : false;
/* 187 */       if (nextCharIsVowel) {
/* 188 */         return this.replacementBeforeVowel;
/*     */       }
/*     */       
/* 191 */       return this.replacementDefault;
/*     */     }
/*     */     
/*     */     private boolean isVowel(char ch) {
/* 195 */       return (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u');
/*     */     }
/*     */     
/*     */     public boolean matches(String context) {
/* 199 */       return context.startsWith(this.pattern);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 204 */       return String.format("%s=(%s,%s,%s)", new Object[] { this.pattern, Arrays.asList(this.replacementAtStart), 
/* 205 */             Arrays.asList(this.replacementBeforeVowel), Arrays.asList(this.replacementDefault) });
/*     */     }
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
/* 223 */   private static final Map<Character, List<Rule>> RULES = new HashMap<Character, List<Rule>>();
/*     */ 
/*     */   
/* 226 */   private static final Map<Character, Character> FOLDINGS = new HashMap<Character, Character>(); private final boolean folding;
/*     */   
/*     */   static {
/* 229 */     InputStream rulesIS = DaitchMokotoffSoundex.class.getClassLoader().getResourceAsStream("org/apache/commons/codec/language/dmrules.txt");
/* 230 */     if (rulesIS == null) {
/* 231 */       throw new IllegalArgumentException("Unable to load resource: org/apache/commons/codec/language/dmrules.txt");
/*     */     }
/*     */     
/* 234 */     Scanner scanner = new Scanner(rulesIS, "UTF-8");
/*     */     try {
/* 236 */       parseRules(scanner, "org/apache/commons/codec/language/dmrules.txt", RULES, FOLDINGS);
/*     */     } finally {
/* 238 */       scanner.close();
/*     */     } 
/*     */ 
/*     */     
/* 242 */     for (Map.Entry<Character, List<Rule>> rule : RULES.entrySet()) {
/* 243 */       List<Rule> ruleList = rule.getValue();
/* 244 */       Collections.sort(ruleList, new Comparator<Rule>()
/*     */           {
/*     */             public int compare(DaitchMokotoffSoundex.Rule rule1, DaitchMokotoffSoundex.Rule rule2) {
/* 247 */               return rule2.getPatternLength() - rule1.getPatternLength();
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseRules(Scanner scanner, String location, Map<Character, List<Rule>> ruleMapping, Map<Character, Character> asciiFoldings) {
/* 255 */     int currentLine = 0;
/* 256 */     boolean inMultilineComment = false;
/*     */     
/* 258 */     while (scanner.hasNextLine()) {
/* 259 */       currentLine++;
/* 260 */       String rawLine = scanner.nextLine();
/* 261 */       String line = rawLine;
/*     */       
/* 263 */       if (inMultilineComment) {
/* 264 */         if (line.endsWith("*/")) {
/* 265 */           inMultilineComment = false;
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/* 270 */       if (line.startsWith("/*")) {
/* 271 */         inMultilineComment = true;
/*     */         continue;
/*     */       } 
/* 274 */       int cmtI = line.indexOf("//");
/* 275 */       if (cmtI >= 0) {
/* 276 */         line = line.substring(0, cmtI);
/*     */       }
/*     */ 
/*     */       
/* 280 */       line = line.trim();
/*     */       
/* 282 */       if (line.length() == 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 286 */       if (line.contains("=")) {
/*     */         
/* 288 */         String[] arrayOfString = line.split("=");
/* 289 */         if (arrayOfString.length != 2) {
/* 290 */           throw new IllegalArgumentException("Malformed folding statement split into " + arrayOfString.length + " parts: " + rawLine + " in " + location);
/*     */         }
/*     */         
/* 293 */         String leftCharacter = arrayOfString[0];
/* 294 */         String rightCharacter = arrayOfString[1];
/*     */         
/* 296 */         if (leftCharacter.length() != 1 || rightCharacter.length() != 1) {
/* 297 */           throw new IllegalArgumentException("Malformed folding statement - patterns are not single characters: " + rawLine + " in " + location);
/*     */         }
/*     */ 
/*     */         
/* 301 */         asciiFoldings.put(Character.valueOf(leftCharacter.charAt(0)), Character.valueOf(rightCharacter.charAt(0)));
/*     */         continue;
/*     */       } 
/* 304 */       String[] parts = line.split("\\s+");
/* 305 */       if (parts.length != 4) {
/* 306 */         throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
/*     */       }
/*     */       
/*     */       try {
/* 310 */         String pattern = stripQuotes(parts[0]);
/* 311 */         String replacement1 = stripQuotes(parts[1]);
/* 312 */         String replacement2 = stripQuotes(parts[2]);
/* 313 */         String replacement3 = stripQuotes(parts[3]);
/*     */         
/* 315 */         Rule r = new Rule(pattern, replacement1, replacement2, replacement3);
/* 316 */         char patternKey = r.pattern.charAt(0);
/* 317 */         List<Rule> rules = ruleMapping.get(Character.valueOf(patternKey));
/* 318 */         if (rules == null) {
/* 319 */           rules = new ArrayList<Rule>();
/* 320 */           ruleMapping.put(Character.valueOf(patternKey), rules);
/*     */         } 
/* 322 */         rules.add(r);
/* 323 */       } catch (IllegalArgumentException e) {
/* 324 */         throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String stripQuotes(String str) {
/* 333 */     if (str.startsWith("\"")) {
/* 334 */       str = str.substring(1);
/*     */     }
/*     */     
/* 337 */     if (str.endsWith("\"")) {
/* 338 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 341 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaitchMokotoffSoundex() {
/* 351 */     this(true);
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
/*     */   public DaitchMokotoffSoundex(boolean folding) {
/* 365 */     this.folding = folding;
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
/*     */   private String cleanup(String input) {
/* 379 */     StringBuilder sb = new StringBuilder();
/* 380 */     for (char ch : input.toCharArray()) {
/* 381 */       if (!Character.isWhitespace(ch)) {
/*     */ 
/*     */ 
/*     */         
/* 385 */         ch = Character.toLowerCase(ch);
/* 386 */         if (this.folding && FOLDINGS.containsKey(Character.valueOf(ch))) {
/* 387 */           ch = ((Character)FOLDINGS.get(Character.valueOf(ch))).charValue();
/*     */         }
/* 389 */         sb.append(ch);
/*     */       } 
/* 391 */     }  return sb.toString();
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 414 */     if (!(obj instanceof String)) {
/* 415 */       throw new EncoderException("Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
/*     */     }
/*     */     
/* 418 */     return encode((String)obj);
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
/*     */   public String encode(String source) {
/* 434 */     if (source == null) {
/* 435 */       return null;
/*     */     }
/* 437 */     return soundex(source, false)[0];
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
/*     */   public String soundex(String source) {
/* 464 */     String[] branches = soundex(source, true);
/* 465 */     StringBuilder sb = new StringBuilder();
/* 466 */     int index = 0;
/* 467 */     for (String branch : branches) {
/* 468 */       sb.append(branch);
/* 469 */       if (++index < branches.length) {
/* 470 */         sb.append('|');
/*     */       }
/*     */     } 
/* 473 */     return sb.toString();
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
/*     */   private String[] soundex(String source, boolean branching) {
/* 487 */     if (source == null) {
/* 488 */       return null;
/*     */     }
/*     */     
/* 491 */     String input = cleanup(source);
/*     */     
/* 493 */     Set<Branch> currentBranches = new LinkedHashSet<Branch>();
/* 494 */     currentBranches.add(new Branch());
/*     */     
/* 496 */     char lastChar = Character.MIN_VALUE;
/* 497 */     for (int index = 0; index < input.length(); index++) {
/* 498 */       char ch = input.charAt(index);
/*     */ 
/*     */       
/* 501 */       if (!Character.isWhitespace(ch)) {
/*     */ 
/*     */ 
/*     */         
/* 505 */         String inputContext = input.substring(index);
/* 506 */         List<Rule> rules = RULES.get(Character.valueOf(ch));
/* 507 */         if (rules != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 513 */           List<Branch> nextBranches = branching ? new ArrayList<Branch>() : Collections.EMPTY_LIST;
/*     */           
/* 515 */           for (Rule rule : rules) {
/* 516 */             if (rule.matches(inputContext)) {
/* 517 */               if (branching) {
/* 518 */                 nextBranches.clear();
/*     */               }
/* 520 */               String[] replacements = rule.getReplacements(inputContext, (lastChar == '\000'));
/* 521 */               boolean branchingRequired = (replacements.length > 1 && branching);
/*     */               
/* 523 */               for (Branch branch : currentBranches) {
/* 524 */                 String[] arrayOfString; int j; byte b; for (arrayOfString = replacements, j = arrayOfString.length, b = 0; b < j; ) { String nextReplacement = arrayOfString[b];
/*     */                   
/* 526 */                   Branch nextBranch = branchingRequired ? branch.createBranch() : branch;
/*     */ 
/*     */                   
/* 529 */                   boolean force = ((lastChar == 'm' && ch == 'n') || (lastChar == 'n' && ch == 'm'));
/*     */                   
/* 531 */                   nextBranch.processNextReplacement(nextReplacement, force);
/*     */                   
/* 533 */                   if (branching) {
/* 534 */                     nextBranches.add(nextBranch);
/*     */                     
/*     */                     b++;
/*     */                   }  }
/*     */               
/*     */               } 
/*     */               
/* 541 */               if (branching) {
/* 542 */                 currentBranches.clear();
/* 543 */                 currentBranches.addAll(nextBranches);
/*     */               } 
/* 545 */               index += rule.getPatternLength() - 1;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/* 550 */           lastChar = ch;
/*     */         } 
/*     */       } 
/* 553 */     }  String[] result = new String[currentBranches.size()];
/* 554 */     int i = 0;
/* 555 */     for (Branch branch : currentBranches) {
/* 556 */       branch.finish();
/* 557 */       result[i++] = branch.toString();
/*     */     } 
/*     */     
/* 560 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\DaitchMokotoffSoundex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */