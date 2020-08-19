/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Rule
/*     */ {
/*     */   public static final class Phoneme
/*     */     implements PhonemeExpr
/*     */   {
/*  85 */     public static final Comparator<Phoneme> COMPARATOR = new Comparator<Phoneme>()
/*     */       {
/*     */         public int compare(Rule.Phoneme o1, Rule.Phoneme o2) {
/*  88 */           for (int i = 0; i < o1.phonemeText.length(); i++) {
/*  89 */             if (i >= o2.phonemeText.length()) {
/*  90 */               return 1;
/*     */             }
/*  92 */             int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
/*  93 */             if (c != 0) {
/*  94 */               return c;
/*     */             }
/*     */           } 
/*     */           
/*  98 */           if (o1.phonemeText.length() < o2.phonemeText.length()) {
/*  99 */             return -1;
/*     */           }
/*     */           
/* 102 */           return 0;
/*     */         }
/*     */       };
/*     */     
/*     */     private final StringBuilder phonemeText;
/*     */     private final Languages.LanguageSet languages;
/*     */     
/*     */     public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages) {
/* 110 */       this.phonemeText = new StringBuilder(phonemeText);
/* 111 */       this.languages = languages;
/*     */     }
/*     */     
/*     */     public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight) {
/* 115 */       this(phonemeLeft.phonemeText, phonemeLeft.languages);
/* 116 */       this.phonemeText.append(phonemeRight.phonemeText);
/*     */     }
/*     */     
/*     */     public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight, Languages.LanguageSet languages) {
/* 120 */       this(phonemeLeft.phonemeText, languages);
/* 121 */       this.phonemeText.append(phonemeRight.phonemeText);
/*     */     }
/*     */     
/*     */     public Phoneme append(CharSequence str) {
/* 125 */       this.phonemeText.append(str);
/* 126 */       return this;
/*     */     }
/*     */     
/*     */     public Languages.LanguageSet getLanguages() {
/* 130 */       return this.languages;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterable<Phoneme> getPhonemes() {
/* 135 */       return Collections.singleton(this);
/*     */     }
/*     */     
/*     */     public CharSequence getPhonemeText() {
/* 139 */       return this.phonemeText;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Phoneme join(Phoneme right) {
/* 151 */       return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages
/* 152 */           .restrictTo(right.languages));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Phoneme mergeWithLanguage(Languages.LanguageSet lang) {
/* 163 */       return new Phoneme(this.phonemeText.toString(), this.languages.merge(lang));
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 168 */       return this.phonemeText.toString() + "[" + this.languages + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class PhonemeList
/*     */     implements PhonemeExpr
/*     */   {
/*     */     private final List<Rule.Phoneme> phonemes;
/*     */ 
/*     */     
/*     */     public PhonemeList(List<Rule.Phoneme> phonemes) {
/* 180 */       this.phonemes = phonemes;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<Rule.Phoneme> getPhonemes() {
/* 185 */       return this.phonemes;
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
/* 196 */   public static final RPattern ALL_STRINGS_RMATCHER = new RPattern()
/*     */     {
/*     */       public boolean isMatch(CharSequence input) {
/* 199 */         return true;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public static final String ALL = "ALL";
/*     */   
/*     */   private static final String DOUBLE_QUOTE = "\"";
/*     */   
/*     */   private static final String HASH_INCLUDE = "#include";
/* 209 */   private static final Map<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>> RULES = new EnumMap<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>>(NameType.class); private final RPattern lContext;
/*     */   private final String pattern;
/*     */   
/*     */   static {
/* 213 */     for (NameType s : NameType.values()) {
/* 214 */       Map<RuleType, Map<String, Map<String, List<Rule>>>> rts = new EnumMap<RuleType, Map<String, Map<String, List<Rule>>>>(RuleType.class);
/*     */ 
/*     */       
/* 217 */       for (RuleType rt : RuleType.values()) {
/* 218 */         Map<String, Map<String, List<Rule>>> rs = new HashMap<String, Map<String, List<Rule>>>();
/*     */         
/* 220 */         Languages ls = Languages.getInstance(s);
/* 221 */         for (String l : ls.getLanguages()) {
/* 222 */           Scanner scanner = createScanner(s, rt, l);
/*     */           try {
/* 224 */             rs.put(l, parseRules(scanner, createResourceName(s, rt, l)));
/* 225 */           } catch (IllegalStateException e) {
/* 226 */             throw new IllegalStateException("Problem processing " + createResourceName(s, rt, l), e);
/*     */           } finally {
/* 228 */             scanner.close();
/*     */           } 
/*     */         } 
/* 231 */         if (!rt.equals(RuleType.RULES)) {
/* 232 */           Scanner scanner = createScanner(s, rt, "common");
/*     */           try {
/* 234 */             rs.put("common", parseRules(scanner, createResourceName(s, rt, "common")));
/*     */           } finally {
/* 236 */             scanner.close();
/*     */           } 
/*     */         } 
/*     */         
/* 240 */         rts.put(rt, Collections.unmodifiableMap(rs));
/*     */       } 
/*     */       
/* 243 */       RULES.put(s, Collections.unmodifiableMap(rts));
/*     */     } 
/*     */   }
/*     */   private final PhonemeExpr phoneme; private final RPattern rContext;
/*     */   private static boolean contains(CharSequence chars, char input) {
/* 248 */     for (int i = 0; i < chars.length(); i++) {
/* 249 */       if (chars.charAt(i) == input) {
/* 250 */         return true;
/*     */       }
/*     */     } 
/* 253 */     return false;
/*     */   }
/*     */   
/*     */   private static String createResourceName(NameType nameType, RuleType rt, String lang) {
/* 257 */     return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", new Object[] { nameType
/* 258 */           .getName(), rt.getName(), lang });
/*     */   }
/*     */   
/*     */   private static Scanner createScanner(NameType nameType, RuleType rt, String lang) {
/* 262 */     String resName = createResourceName(nameType, rt, lang);
/* 263 */     InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
/*     */     
/* 265 */     if (rulesIS == null) {
/* 266 */       throw new IllegalArgumentException("Unable to load resource: " + resName);
/*     */     }
/*     */     
/* 269 */     return new Scanner(rulesIS, "UTF-8");
/*     */   }
/*     */   
/*     */   private static Scanner createScanner(String lang) {
/* 273 */     String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", new Object[] { lang });
/* 274 */     InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
/*     */     
/* 276 */     if (rulesIS == null) {
/* 277 */       throw new IllegalArgumentException("Unable to load resource: " + resName);
/*     */     }
/*     */     
/* 280 */     return new Scanner(rulesIS, "UTF-8");
/*     */   }
/*     */   
/*     */   private static boolean endsWith(CharSequence input, CharSequence suffix) {
/* 284 */     if (suffix.length() > input.length()) {
/* 285 */       return false;
/*     */     }
/* 287 */     for (int i = input.length() - 1, j = suffix.length() - 1; j >= 0; i--, j--) {
/* 288 */       if (input.charAt(i) != suffix.charAt(j)) {
/* 289 */         return false;
/*     */       }
/*     */     } 
/* 292 */     return true;
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
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
/* 308 */     Map<String, List<Rule>> ruleMap = getInstanceMap(nameType, rt, langs);
/* 309 */     List<Rule> allRules = new ArrayList<Rule>();
/* 310 */     for (List<Rule> rules : ruleMap.values()) {
/* 311 */       allRules.addAll(rules);
/*     */     }
/* 313 */     return allRules;
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
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, String lang) {
/* 328 */     return getInstance(nameType, rt, Languages.LanguageSet.from(new HashSet<String>(Arrays.asList(new String[] { lang }))));
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
/*     */   public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
/* 345 */     return langs.isSingleton() ? getInstanceMap(nameType, rt, langs.getAny()) : 
/* 346 */       getInstanceMap(nameType, rt, "any");
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
/*     */   public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, String lang) {
/* 363 */     Map<String, List<Rule>> rules = (Map<String, List<Rule>>)((Map)((Map)RULES.get(nameType)).get(rt)).get(lang);
/*     */     
/* 365 */     if (rules == null) {
/* 366 */       throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", new Object[] { nameType
/* 367 */               .getName(), rt.getName(), lang }));
/*     */     }
/*     */     
/* 370 */     return rules;
/*     */   }
/*     */   
/*     */   private static Phoneme parsePhoneme(String ph) {
/* 374 */     int open = ph.indexOf("[");
/* 375 */     if (open >= 0) {
/* 376 */       if (!ph.endsWith("]")) {
/* 377 */         throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
/*     */       }
/* 379 */       String before = ph.substring(0, open);
/* 380 */       String in = ph.substring(open + 1, ph.length() - 1);
/* 381 */       Set<String> langs = new HashSet<String>(Arrays.asList(in.split("[+]")));
/*     */       
/* 383 */       return new Phoneme(before, Languages.LanguageSet.from(langs));
/*     */     } 
/* 385 */     return new Phoneme(ph, Languages.ANY_LANGUAGE);
/*     */   }
/*     */   
/*     */   private static PhonemeExpr parsePhonemeExpr(String ph) {
/* 389 */     if (ph.startsWith("(")) {
/* 390 */       if (!ph.endsWith(")")) {
/* 391 */         throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
/*     */       }
/*     */       
/* 394 */       List<Phoneme> phs = new ArrayList<Phoneme>();
/* 395 */       String body = ph.substring(1, ph.length() - 1);
/* 396 */       for (String part : body.split("[|]")) {
/* 397 */         phs.add(parsePhoneme(part));
/*     */       }
/* 399 */       if (body.startsWith("|") || body.endsWith("|")) {
/* 400 */         phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
/*     */       }
/*     */       
/* 403 */       return new PhonemeList(phs);
/*     */     } 
/* 405 */     return parsePhoneme(ph);
/*     */   }
/*     */   
/*     */   private static Map<String, List<Rule>> parseRules(Scanner scanner, final String location) {
/* 409 */     Map<String, List<Rule>> lines = new HashMap<String, List<Rule>>();
/* 410 */     int currentLine = 0;
/*     */     
/* 412 */     boolean inMultilineComment = false;
/* 413 */     while (scanner.hasNextLine()) {
/* 414 */       currentLine++;
/* 415 */       String rawLine = scanner.nextLine();
/* 416 */       String line = rawLine;
/*     */       
/* 418 */       if (inMultilineComment) {
/* 419 */         if (line.endsWith("*/"))
/* 420 */           inMultilineComment = false; 
/*     */         continue;
/*     */       } 
/* 423 */       if (line.startsWith("/*")) {
/* 424 */         inMultilineComment = true;
/*     */         continue;
/*     */       } 
/* 427 */       int cmtI = line.indexOf("//");
/* 428 */       if (cmtI >= 0) {
/* 429 */         line = line.substring(0, cmtI);
/*     */       }
/*     */ 
/*     */       
/* 433 */       line = line.trim();
/*     */       
/* 435 */       if (line.length() == 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 439 */       if (line.startsWith("#include")) {
/*     */         
/* 441 */         String incl = line.substring("#include".length()).trim();
/* 442 */         if (incl.contains(" ")) {
/* 443 */           throw new IllegalArgumentException("Malformed import statement '" + rawLine + "' in " + location);
/*     */         }
/*     */         
/* 446 */         Scanner hashIncludeScanner = createScanner(incl);
/*     */         try {
/* 448 */           lines.putAll(parseRules(hashIncludeScanner, location + "->" + incl));
/*     */         } finally {
/* 450 */           hashIncludeScanner.close();
/*     */         } 
/*     */         continue;
/*     */       } 
/* 454 */       String[] parts = line.split("\\s+");
/* 455 */       if (parts.length != 4) {
/* 456 */         throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
/*     */       }
/*     */       
/*     */       try {
/* 460 */         final String pat = stripQuotes(parts[0]);
/* 461 */         final String lCon = stripQuotes(parts[1]);
/* 462 */         final String rCon = stripQuotes(parts[2]);
/* 463 */         PhonemeExpr ph = parsePhonemeExpr(stripQuotes(parts[3]));
/* 464 */         final int cLine = currentLine;
/* 465 */         Rule r = new Rule(pat, lCon, rCon, ph) {
/* 466 */             private final int myLine = cLine;
/* 467 */             private final String loc = location;
/*     */ 
/*     */             
/*     */             public String toString() {
/* 471 */               StringBuilder sb = new StringBuilder();
/* 472 */               sb.append("Rule");
/* 473 */               sb.append("{line=").append(this.myLine);
/* 474 */               sb.append(", loc='").append(this.loc).append('\'');
/* 475 */               sb.append(", pat='").append(pat).append('\'');
/* 476 */               sb.append(", lcon='").append(lCon).append('\'');
/* 477 */               sb.append(", rcon='").append(rCon).append('\'');
/* 478 */               sb.append('}');
/* 479 */               return sb.toString();
/*     */             }
/*     */           };
/* 482 */         String patternKey = r.pattern.substring(0, 1);
/* 483 */         List<Rule> rules = lines.get(patternKey);
/* 484 */         if (rules == null) {
/* 485 */           rules = new ArrayList<Rule>();
/* 486 */           lines.put(patternKey, rules);
/*     */         } 
/* 488 */         rules.add(r);
/* 489 */       } catch (IllegalArgumentException e) {
/* 490 */         throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 498 */     return lines;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static RPattern pattern(final String regex) {
/* 509 */     boolean startsWith = regex.startsWith("^");
/* 510 */     boolean endsWith = regex.endsWith("$");
/* 511 */     final String content = regex.substring(startsWith ? 1 : 0, endsWith ? (regex.length() - 1) : regex.length());
/* 512 */     boolean boxes = content.contains("[");
/*     */     
/* 514 */     if (!boxes) {
/* 515 */       if (startsWith && endsWith) {
/*     */         
/* 517 */         if (content.length() == 0)
/*     */         {
/* 519 */           return new RPattern()
/*     */             {
/*     */               public boolean isMatch(CharSequence input) {
/* 522 */                 return (input.length() == 0);
/*     */               }
/*     */             };
/*     */         }
/* 526 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 529 */               return input.equals(content); }
/*     */           };
/*     */       } 
/* 532 */       if ((startsWith || endsWith) && content.length() == 0)
/*     */       {
/* 534 */         return ALL_STRINGS_RMATCHER; } 
/* 535 */       if (startsWith)
/*     */       {
/* 537 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 540 */               return Rule.startsWith(input, content);
/*     */             }
/*     */           }; } 
/* 543 */       if (endsWith)
/*     */       {
/* 545 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 548 */               return Rule.endsWith(input, content);
/*     */             }
/*     */           };
/*     */       }
/*     */     } else {
/* 553 */       boolean startsWithBox = content.startsWith("[");
/* 554 */       boolean endsWithBox = content.endsWith("]");
/*     */       
/* 556 */       if (startsWithBox && endsWithBox) {
/* 557 */         String boxContent = content.substring(1, content.length() - 1);
/* 558 */         if (!boxContent.contains("[")) {
/*     */           
/* 560 */           boolean negate = boxContent.startsWith("^");
/* 561 */           if (negate) {
/* 562 */             boxContent = boxContent.substring(1);
/*     */           }
/* 564 */           final String bContent = boxContent;
/* 565 */           final boolean shouldMatch = !negate;
/*     */           
/* 567 */           if (startsWith && endsWith)
/*     */           {
/* 569 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 572 */                   return (input.length() == 1 && Rule.contains(bContent, input.charAt(0)) == shouldMatch);
/*     */                 }
/*     */               }; } 
/* 575 */           if (startsWith)
/*     */           {
/* 577 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 580 */                   return (input.length() > 0 && Rule.contains(bContent, input.charAt(0)) == shouldMatch);
/*     */                 }
/*     */               }; } 
/* 583 */           if (endsWith)
/*     */           {
/* 585 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 588 */                   return (input.length() > 0 && Rule
/* 589 */                     .contains(bContent, input.charAt(input.length() - 1)) == shouldMatch);
/*     */                 }
/*     */               };
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 597 */     return new RPattern() {
/* 598 */         Pattern pattern = Pattern.compile(regex);
/*     */ 
/*     */         
/*     */         public boolean isMatch(CharSequence input) {
/* 602 */           Matcher matcher = this.pattern.matcher(input);
/* 603 */           return matcher.find();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static boolean startsWith(CharSequence input, CharSequence prefix) {
/* 609 */     if (prefix.length() > input.length()) {
/* 610 */       return false;
/*     */     }
/* 612 */     for (int i = 0; i < prefix.length(); i++) {
/* 613 */       if (input.charAt(i) != prefix.charAt(i)) {
/* 614 */         return false;
/*     */       }
/*     */     } 
/* 617 */     return true;
/*     */   }
/*     */   
/*     */   private static String stripQuotes(String str) {
/* 621 */     if (str.startsWith("\"")) {
/* 622 */       str = str.substring(1);
/*     */     }
/*     */     
/* 625 */     if (str.endsWith("\"")) {
/* 626 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 629 */     return str;
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
/*     */   public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme) {
/* 653 */     this.pattern = pattern;
/* 654 */     this.lContext = pattern(lContext + "$");
/* 655 */     this.rContext = pattern("^" + rContext);
/* 656 */     this.phoneme = phoneme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RPattern getLContext() {
/* 665 */     return this.lContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/* 674 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PhonemeExpr getPhoneme() {
/* 683 */     return this.phoneme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RPattern getRContext() {
/* 692 */     return this.rContext;
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
/*     */   public boolean patternAndContextMatches(CharSequence input, int i) {
/* 707 */     if (i < 0) {
/* 708 */       throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
/*     */     }
/*     */     
/* 711 */     int patternLength = this.pattern.length();
/* 712 */     int ipl = i + patternLength;
/*     */     
/* 714 */     if (ipl > input.length())
/*     */     {
/* 716 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 721 */     if (!input.subSequence(i, ipl).equals(this.pattern))
/* 722 */       return false; 
/* 723 */     if (!this.rContext.isMatch(input.subSequence(ipl, input.length()))) {
/* 724 */       return false;
/*     */     }
/* 726 */     return this.lContext.isMatch(input.subSequence(0, i));
/*     */   }
/*     */   
/*     */   public static interface RPattern {
/*     */     boolean isMatch(CharSequence param1CharSequence);
/*     */   }
/*     */   
/*     */   public static interface PhonemeExpr {
/*     */     Iterable<Rule.Phoneme> getPhonemes();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\language\bm\Rule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */