/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DateFormatter;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class CookieDecoder
/*     */ {
/*  53 */   private final InternalLogger logger = InternalLoggerFactory.getInstance(getClass());
/*     */   
/*  55 */   private static final CookieDecoder STRICT = new CookieDecoder(true);
/*     */   
/*  57 */   private static final CookieDecoder LAX = new CookieDecoder(false);
/*     */   
/*     */   private static final String COMMENT = "Comment";
/*     */   
/*     */   private static final String COMMENTURL = "CommentURL";
/*     */   
/*     */   private static final String DISCARD = "Discard";
/*     */   
/*     */   private static final String PORT = "Port";
/*     */   
/*     */   private static final String VERSION = "Version";
/*     */   
/*     */   private final boolean strict;
/*     */   
/*     */   public static Set<Cookie> decode(String header) {
/*  72 */     return decode(header, true);
/*     */   }
/*     */   
/*     */   public static Set<Cookie> decode(String header, boolean strict) {
/*  76 */     return (strict ? STRICT : LAX).doDecode(header);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Set<Cookie> doDecode(String header) {
/*     */     int i;
/*  85 */     List<String> names = new ArrayList<String>(8);
/*  86 */     List<String> values = new ArrayList<String>(8);
/*  87 */     extractKeyValuePairs(header, names, values);
/*     */     
/*  89 */     if (names.isEmpty()) {
/*  90 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*  94 */     int version = 0;
/*     */ 
/*     */ 
/*     */     
/*  98 */     if (((String)names.get(0)).equalsIgnoreCase("Version")) {
/*     */       try {
/* 100 */         version = Integer.parseInt(values.get(0));
/* 101 */       } catch (NumberFormatException numberFormatException) {}
/*     */ 
/*     */       
/* 104 */       i = 1;
/*     */     } else {
/* 106 */       i = 0;
/*     */     } 
/*     */     
/* 109 */     if (names.size() <= i)
/*     */     {
/* 111 */       return Collections.emptySet();
/*     */     }
/*     */     
/* 114 */     Set<Cookie> cookies = new TreeSet<Cookie>();
/* 115 */     for (; i < names.size(); i++) {
/* 116 */       String name = names.get(i);
/* 117 */       String value = values.get(i);
/* 118 */       if (value == null) {
/* 119 */         value = "";
/*     */       }
/*     */       
/* 122 */       Cookie c = initCookie(name, value);
/*     */       
/* 124 */       if (c == null) {
/*     */         break;
/*     */       }
/*     */       
/* 128 */       boolean discard = false;
/* 129 */       boolean secure = false;
/* 130 */       boolean httpOnly = false;
/* 131 */       String comment = null;
/* 132 */       String commentURL = null;
/* 133 */       String domain = null;
/* 134 */       String path = null;
/* 135 */       long maxAge = Long.MIN_VALUE;
/* 136 */       List<Integer> ports = new ArrayList<Integer>(2);
/*     */       
/* 138 */       for (int j = i + 1; j < names.size(); j++, i++) {
/* 139 */         name = names.get(j);
/* 140 */         value = values.get(j);
/*     */         
/* 142 */         if ("Discard".equalsIgnoreCase(name)) {
/* 143 */           discard = true;
/* 144 */         } else if ("Secure".equalsIgnoreCase(name)) {
/* 145 */           secure = true;
/* 146 */         } else if ("HTTPOnly".equalsIgnoreCase(name)) {
/* 147 */           httpOnly = true;
/* 148 */         } else if ("Comment".equalsIgnoreCase(name)) {
/* 149 */           comment = value;
/* 150 */         } else if ("CommentURL".equalsIgnoreCase(name)) {
/* 151 */           commentURL = value;
/* 152 */         } else if ("Domain".equalsIgnoreCase(name)) {
/* 153 */           domain = value;
/* 154 */         } else if ("Path".equalsIgnoreCase(name)) {
/* 155 */           path = value;
/* 156 */         } else if ("Expires".equalsIgnoreCase(name)) {
/* 157 */           Date date = DateFormatter.parseHttpDate(value);
/* 158 */           if (date != null) {
/* 159 */             long maxAgeMillis = date.getTime() - System.currentTimeMillis();
/* 160 */             maxAge = maxAgeMillis / 1000L + ((maxAgeMillis % 1000L != 0L) ? 1L : 0L);
/*     */           } 
/* 162 */         } else if ("Max-Age".equalsIgnoreCase(name)) {
/* 163 */           maxAge = Integer.parseInt(value);
/* 164 */         } else if ("Version".equalsIgnoreCase(name)) {
/* 165 */           version = Integer.parseInt(value);
/* 166 */         } else if ("Port".equalsIgnoreCase(name)) {
/* 167 */           String[] portList = value.split(",");
/* 168 */           for (String s1 : portList) {
/*     */             try {
/* 170 */               ports.add(Integer.valueOf(s1));
/* 171 */             } catch (NumberFormatException numberFormatException) {}
/*     */           } 
/*     */         } else {
/*     */           break;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 180 */       c.setVersion(version);
/* 181 */       c.setMaxAge(maxAge);
/* 182 */       c.setPath(path);
/* 183 */       c.setDomain(domain);
/* 184 */       c.setSecure(secure);
/* 185 */       c.setHttpOnly(httpOnly);
/* 186 */       if (version > 0) {
/* 187 */         c.setComment(comment);
/*     */       }
/* 189 */       if (version > 1) {
/* 190 */         c.setCommentUrl(commentURL);
/* 191 */         c.setPorts(ports);
/* 192 */         c.setDiscard(discard);
/*     */       } 
/*     */       
/* 195 */       cookies.add(c);
/*     */     } 
/*     */     
/* 198 */     return cookies;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void extractKeyValuePairs(String header, List<String> names, List<String> values) {
/* 203 */     int headerLen = header.length();
/* 204 */     int i = 0;
/*     */ 
/*     */ 
/*     */     
/* 208 */     label63: while (i != headerLen) {
/*     */ 
/*     */       
/* 211 */       switch (header.charAt(i)) { case '\t': case '\n': case '\013': case '\f': case '\r': case ' ':
/*     */         case ',':
/*     */         case ';':
/* 214 */           i++;
/*     */           continue; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 222 */       label58: while (i != headerLen) {
/*     */ 
/*     */         
/* 225 */         if (header.charAt(i) == '$') {
/* 226 */           i++;
/*     */ 
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 235 */         if (i == headerLen) {
/* 236 */           String name = null;
/* 237 */           String value = null; continue label63;
/*     */         } 
/* 239 */         int newNameStart = i;
/*     */         break label58;
/*     */       } 
/*     */       break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CookieDecoder(boolean strict) {
/* 325 */     this.strict = strict;
/*     */   }
/*     */   
/*     */   private DefaultCookie initCookie(String name, String value) {
/* 329 */     if (name == null || name.length() == 0) {
/* 330 */       this.logger.debug("Skipping cookie with null name");
/* 331 */       return null;
/*     */     } 
/*     */     
/* 334 */     if (value == null) {
/* 335 */       this.logger.debug("Skipping cookie with null value");
/* 336 */       return null;
/*     */     } 
/*     */     
/* 339 */     CharSequence unwrappedValue = CookieUtil.unwrapValue(value);
/* 340 */     if (unwrappedValue == null) {
/* 341 */       this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", unwrappedValue);
/*     */       
/* 343 */       return null;
/*     */     } 
/*     */     
/*     */     int invalidOctetPos;
/* 347 */     if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
/* 348 */       if (this.logger.isDebugEnabled()) {
/* 349 */         this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", name, 
/* 350 */             Character.valueOf(name.charAt(invalidOctetPos)));
/*     */       }
/* 352 */       return null;
/*     */     } 
/*     */     
/* 355 */     boolean wrap = (unwrappedValue.length() != value.length());
/*     */     
/* 357 */     if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
/* 358 */       if (this.logger.isDebugEnabled()) {
/* 359 */         this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", unwrappedValue, 
/* 360 */             Character.valueOf(unwrappedValue.charAt(invalidOctetPos)));
/*     */       }
/* 362 */       return null;
/*     */     } 
/*     */     
/* 365 */     DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
/* 366 */     cookie.setWrap(wrap);
/* 367 */     return cookie;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\CookieDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */