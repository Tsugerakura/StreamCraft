/*     */ package org.apache.http.conn.util;
/*     */ 
/*     */ import java.net.IDN;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class PublicSuffixMatcher
/*     */ {
/*     */   private final Map<String, DomainType> rules;
/*     */   private final Map<String, DomainType> exceptions;
/*     */   
/*     */   public PublicSuffixMatcher(Collection<String> rules, Collection<String> exceptions) {
/*  56 */     this(DomainType.UNKNOWN, rules, exceptions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PublicSuffixMatcher(DomainType domainType, Collection<String> rules, Collection<String> exceptions) {
/*  64 */     Args.notNull(domainType, "Domain type");
/*  65 */     Args.notNull(rules, "Domain suffix rules");
/*  66 */     this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
/*  67 */     for (String rule : rules) {
/*  68 */       this.rules.put(rule, domainType);
/*     */     }
/*  70 */     this.exceptions = new ConcurrentHashMap<String, DomainType>();
/*  71 */     if (exceptions != null) {
/*  72 */       for (String exception : exceptions) {
/*  73 */         this.exceptions.put(exception, domainType);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PublicSuffixMatcher(Collection<PublicSuffixList> lists) {
/*  82 */     Args.notNull(lists, "Domain suffix lists");
/*  83 */     this.rules = new ConcurrentHashMap<String, DomainType>();
/*  84 */     this.exceptions = new ConcurrentHashMap<String, DomainType>();
/*  85 */     for (PublicSuffixList list : lists) {
/*  86 */       DomainType domainType = list.getType();
/*  87 */       List<String> rules = list.getRules();
/*  88 */       for (String rule : rules) {
/*  89 */         this.rules.put(rule, domainType);
/*     */       }
/*  91 */       List<String> exceptions = list.getExceptions();
/*  92 */       if (exceptions != null) {
/*  93 */         for (String exception : exceptions) {
/*  94 */           this.exceptions.put(exception, domainType);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean hasEntry(Map<String, DomainType> map, String rule, DomainType expectedType) {
/* 101 */     if (map == null) {
/* 102 */       return false;
/*     */     }
/* 104 */     DomainType domainType = map.get(rule);
/* 105 */     return (domainType == null) ? false : ((expectedType == null || domainType.equals(expectedType)));
/*     */   }
/*     */   
/*     */   private boolean hasRule(String rule, DomainType expectedType) {
/* 109 */     return hasEntry(this.rules, rule, expectedType);
/*     */   }
/*     */   
/*     */   private boolean hasException(String exception, DomainType expectedType) {
/* 113 */     return hasEntry(this.exceptions, exception, expectedType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDomainRoot(String domain) {
/* 124 */     return getDomainRoot(domain, null);
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
/*     */   public String getDomainRoot(String domain, DomainType expectedType) {
/* 138 */     if (domain == null) {
/* 139 */       return null;
/*     */     }
/* 141 */     if (domain.startsWith(".")) {
/* 142 */       return null;
/*     */     }
/* 144 */     String normalized = DnsUtils.normalize(domain);
/* 145 */     String segment = normalized;
/* 146 */     String result = null;
/* 147 */     while (segment != null) {
/*     */       
/* 149 */       String key = IDN.toUnicode(segment);
/* 150 */       if (hasException(key, expectedType)) {
/* 151 */         return segment;
/*     */       }
/* 153 */       if (hasRule(key, expectedType)) {
/* 154 */         return result;
/*     */       }
/*     */       
/* 157 */       int nextdot = segment.indexOf('.');
/* 158 */       String nextSegment = (nextdot != -1) ? segment.substring(nextdot + 1) : null;
/*     */       
/* 160 */       if (nextSegment != null && 
/* 161 */         hasRule("*." + IDN.toUnicode(nextSegment), expectedType)) {
/* 162 */         return result;
/*     */       }
/*     */       
/* 165 */       result = segment;
/* 166 */       segment = nextSegment;
/*     */     } 
/*     */ 
/*     */     
/* 170 */     if (expectedType == null || expectedType == DomainType.UNKNOWN) {
/* 171 */       return result;
/*     */     }
/*     */ 
/*     */     
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(String domain) {
/* 181 */     return matches(domain, null);
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
/*     */   public boolean matches(String domain, DomainType expectedType) {
/* 194 */     if (domain == null) {
/* 195 */       return false;
/*     */     }
/* 197 */     String domainRoot = getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
/*     */     
/* 199 */     return (domainRoot == null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\con\\util\PublicSuffixMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */