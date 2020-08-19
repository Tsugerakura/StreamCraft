/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ServerCookieDecoder
/*     */   extends CookieDecoder
/*     */ {
/*     */   private static final String RFC2965_VERSION = "$Version";
/*     */   private static final String RFC2965_PATH = "$Path";
/*     */   private static final String RFC2965_DOMAIN = "$Domain";
/*     */   private static final String RFC2965_PORT = "$Port";
/*  51 */   public static final ServerCookieDecoder STRICT = new ServerCookieDecoder(true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final ServerCookieDecoder LAX = new ServerCookieDecoder(false);
/*     */   
/*     */   private ServerCookieDecoder(boolean strict) {
/*  59 */     super(strict);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> decodeAll(String header) {
/*  69 */     List<Cookie> cookies = new ArrayList<Cookie>();
/*  70 */     decode(cookies, header);
/*  71 */     return Collections.unmodifiableList(cookies);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Cookie> decode(String header) {
/*  80 */     Set<Cookie> cookies = new TreeSet<Cookie>();
/*  81 */     decode(cookies, header);
/*  82 */     return cookies;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void decode(Collection<? super Cookie> cookies, String header) {
/*  91 */     int headerLen = ((String)ObjectUtil.checkNotNull(header, "header")).length();
/*     */     
/*  93 */     if (headerLen == 0) {
/*     */       return;
/*     */     }
/*     */     
/*  97 */     int i = 0;
/*     */     
/*  99 */     boolean rfc2965Style = false;
/* 100 */     if (header.regionMatches(true, 0, "$Version", 0, "$Version".length())) {
/*     */       
/* 102 */       i = header.indexOf(';') + 1;
/* 103 */       rfc2965Style = true;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     while (i != headerLen) {
/*     */       int nameEnd, valueBegin, valueEnd;
/*     */       
/* 113 */       char c = header.charAt(i);
/* 114 */       if (c == '\t' || c == '\n' || c == '\013' || c == '\f' || c == '\r' || c == ' ' || c == ',' || c == ';') {
/*     */         
/* 116 */         i++;
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 122 */       int nameBegin = i;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       while (true) {
/* 129 */         char curChar = header.charAt(i);
/* 130 */         if (curChar == ';') {
/*     */           
/* 132 */           nameEnd = i;
/* 133 */           valueBegin = valueEnd = -1;
/*     */           break;
/*     */         } 
/* 136 */         if (curChar == '=') {
/*     */           
/* 138 */           nameEnd = i;
/* 139 */           i++;
/* 140 */           if (i == headerLen) {
/*     */             
/* 142 */             int k = 0, j = k;
/*     */             
/*     */             break;
/*     */           } 
/* 146 */           valueBegin = i;
/*     */           
/* 148 */           int semiPos = header.indexOf(';', i);
/* 149 */           valueEnd = i = (semiPos > 0) ? semiPos : headerLen;
/*     */           break;
/*     */         } 
/* 152 */         i++;
/*     */ 
/*     */         
/* 155 */         if (i == headerLen) {
/*     */           
/* 157 */           nameEnd = headerLen;
/* 158 */           valueBegin = valueEnd = -1;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 163 */       if (rfc2965Style && (header.regionMatches(nameBegin, "$Path", 0, "$Path".length()) || header
/* 164 */         .regionMatches(nameBegin, "$Domain", 0, "$Domain".length()) || header
/* 165 */         .regionMatches(nameBegin, "$Port", 0, "$Port".length()))) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 171 */       DefaultCookie cookie = initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
/* 172 */       if (cookie != null)
/* 173 */         cookies.add(cookie); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\ServerCookieDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */