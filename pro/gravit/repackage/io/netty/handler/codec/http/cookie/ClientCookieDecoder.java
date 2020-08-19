/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
/*     */ import java.util.Date;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DateFormatter;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ClientCookieDecoder
/*     */   extends CookieDecoder
/*     */ {
/*  38 */   public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);
/*     */   
/*     */   private ClientCookieDecoder(boolean strict) {
/*  46 */     super(strict);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cookie decode(String header) {
/*  55 */     int headerLen = ((String)ObjectUtil.checkNotNull(header, "header")).length();
/*     */     
/*  57 */     if (headerLen == 0) {
/*  58 */       return null;
/*     */     }
/*     */     
/*  61 */     CookieBuilder cookieBuilder = null;
/*     */     
/*  63 */     int i = 0;
/*     */ 
/*     */ 
/*     */     
/*  67 */     while (i != headerLen) {
/*     */       int nameEnd, valueBegin, valueEnd;
/*     */       
/*  70 */       char c = header.charAt(i);
/*  71 */       if (c == ',') {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/*  76 */       if (c == '\t' || c == '\n' || c == '\013' || c == '\f' || c == '\r' || c == ' ' || c == ';') {
/*     */         
/*  78 */         i++;
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*  84 */       int nameBegin = i;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       while (true) {
/*  90 */         char curChar = header.charAt(i);
/*  91 */         if (curChar == ';') {
/*     */           
/*  93 */           nameEnd = i;
/*  94 */           valueBegin = valueEnd = -1;
/*     */           break;
/*     */         } 
/*  97 */         if (curChar == '=') {
/*     */           
/*  99 */           nameEnd = i;
/* 100 */           i++;
/* 101 */           if (i == headerLen) {
/*     */             
/* 103 */             int k = 0, j = k;
/*     */             
/*     */             break;
/*     */           } 
/* 107 */           valueBegin = i;
/*     */           
/* 109 */           int semiPos = header.indexOf(';', i);
/* 110 */           valueEnd = i = (semiPos > 0) ? semiPos : headerLen;
/*     */           break;
/*     */         } 
/* 113 */         i++;
/*     */ 
/*     */         
/* 116 */         if (i == headerLen) {
/*     */           
/* 118 */           nameEnd = headerLen;
/* 119 */           valueBegin = valueEnd = -1;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 124 */       if (valueEnd > 0 && header.charAt(valueEnd - 1) == ',')
/*     */       {
/* 126 */         valueEnd--;
/*     */       }
/*     */       
/* 129 */       if (cookieBuilder == null) {
/*     */         
/* 131 */         DefaultCookie cookie = initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
/*     */         
/* 133 */         if (cookie == null) {
/* 134 */           return null;
/*     */         }
/*     */         
/* 137 */         cookieBuilder = new CookieBuilder(cookie, header);
/*     */         continue;
/*     */       } 
/* 140 */       cookieBuilder.appendAttribute(nameBegin, nameEnd, valueBegin, valueEnd);
/*     */     } 
/*     */     
/* 143 */     return (cookieBuilder != null) ? cookieBuilder.cookie() : null;
/*     */   }
/*     */   
/*     */   private static class CookieBuilder
/*     */   {
/*     */     private final String header;
/*     */     private final DefaultCookie cookie;
/*     */     private String domain;
/*     */     private String path;
/* 152 */     private long maxAge = Long.MIN_VALUE;
/*     */     private int expiresStart;
/*     */     private int expiresEnd;
/*     */     private boolean secure;
/*     */     private boolean httpOnly;
/*     */     
/*     */     CookieBuilder(DefaultCookie cookie, String header) {
/* 159 */       this.cookie = cookie;
/* 160 */       this.header = header;
/*     */     }
/*     */ 
/*     */     
/*     */     private long mergeMaxAgeAndExpires() {
/* 165 */       if (this.maxAge != Long.MIN_VALUE)
/* 166 */         return this.maxAge; 
/* 167 */       if (isValueDefined(this.expiresStart, this.expiresEnd)) {
/* 168 */         Date expiresDate = DateFormatter.parseHttpDate(this.header, this.expiresStart, this.expiresEnd);
/* 169 */         if (expiresDate != null) {
/* 170 */           long maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis();
/* 171 */           return maxAgeMillis / 1000L + ((maxAgeMillis % 1000L != 0L) ? 1L : 0L);
/*     */         } 
/*     */       } 
/* 174 */       return Long.MIN_VALUE;
/*     */     }
/*     */     
/*     */     Cookie cookie() {
/* 178 */       this.cookie.setDomain(this.domain);
/* 179 */       this.cookie.setPath(this.path);
/* 180 */       this.cookie.setMaxAge(mergeMaxAgeAndExpires());
/* 181 */       this.cookie.setSecure(this.secure);
/* 182 */       this.cookie.setHttpOnly(this.httpOnly);
/* 183 */       return this.cookie;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void appendAttribute(int keyStart, int keyEnd, int valueStart, int valueEnd) {
/* 200 */       int length = keyEnd - keyStart;
/*     */       
/* 202 */       if (length == 4) {
/* 203 */         parse4(keyStart, valueStart, valueEnd);
/* 204 */       } else if (length == 6) {
/* 205 */         parse6(keyStart, valueStart, valueEnd);
/* 206 */       } else if (length == 7) {
/* 207 */         parse7(keyStart, valueStart, valueEnd);
/* 208 */       } else if (length == 8) {
/* 209 */         parse8(keyStart);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void parse4(int nameStart, int valueStart, int valueEnd) {
/* 214 */       if (this.header.regionMatches(true, nameStart, "Path", 0, 4)) {
/* 215 */         this.path = computeValue(valueStart, valueEnd);
/*     */       }
/*     */     }
/*     */     
/*     */     private void parse6(int nameStart, int valueStart, int valueEnd) {
/* 220 */       if (this.header.regionMatches(true, nameStart, "Domain", 0, 5)) {
/* 221 */         this.domain = computeValue(valueStart, valueEnd);
/* 222 */       } else if (this.header.regionMatches(true, nameStart, "Secure", 0, 5)) {
/* 223 */         this.secure = true;
/*     */       } 
/*     */     }
/*     */     
/*     */     private void setMaxAge(String value) {
/*     */       try {
/* 229 */         this.maxAge = Math.max(Long.parseLong(value), 0L);
/* 230 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void parse7(int nameStart, int valueStart, int valueEnd) {
/* 236 */       if (this.header.regionMatches(true, nameStart, "Expires", 0, 7)) {
/* 237 */         this.expiresStart = valueStart;
/* 238 */         this.expiresEnd = valueEnd;
/* 239 */       } else if (this.header.regionMatches(true, nameStart, "Max-Age", 0, 7)) {
/* 240 */         setMaxAge(computeValue(valueStart, valueEnd));
/*     */       } 
/*     */     }
/*     */     
/*     */     private void parse8(int nameStart) {
/* 245 */       if (this.header.regionMatches(true, nameStart, "HTTPOnly", 0, 8)) {
/* 246 */         this.httpOnly = true;
/*     */       }
/*     */     }
/*     */     
/*     */     private static boolean isValueDefined(int valueStart, int valueEnd) {
/* 251 */       return (valueStart != -1 && valueStart != valueEnd);
/*     */     }
/*     */     
/*     */     private String computeValue(int valueStart, int valueEnd) {
/* 255 */       return isValueDefined(valueStart, valueEnd) ? this.header.substring(valueStart, valueEnd) : null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\ClientCookieDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */