/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ClientCookieEncoder
/*     */   extends CookieEncoder
/*     */ {
/*  53 */   public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
/*     */   
/*     */   private ClientCookieEncoder(boolean strict) {
/*  62 */     super(strict);
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
/*     */   public String encode(String name, String value) {
/*  75 */     return encode(new DefaultCookie(name, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(Cookie cookie) {
/*  85 */     StringBuilder buf = CookieUtil.stringBuilder();
/*  86 */     encode(buf, (Cookie)ObjectUtil.checkNotNull(cookie, "cookie"));
/*  87 */     return CookieUtil.stripTrailingSeparator(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>()
/*     */     {
/*     */       public int compare(Cookie c1, Cookie c2) {
/*  98 */         String path1 = c1.path();
/*  99 */         String path2 = c2.path();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 105 */         int len1 = (path1 == null) ? Integer.MAX_VALUE : path1.length();
/* 106 */         int len2 = (path2 == null) ? Integer.MAX_VALUE : path2.length();
/*     */ 
/*     */ 
/*     */         
/* 110 */         return len2 - len1;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(Cookie... cookies) {
/* 122 */     if (((Cookie[])ObjectUtil.checkNotNull(cookies, "cookies")).length == 0) {
/* 123 */       return null;
/*     */     }
/*     */     
/* 126 */     StringBuilder buf = CookieUtil.stringBuilder();
/* 127 */     if (this.strict) {
/* 128 */       if (cookies.length == 1) {
/* 129 */         encode(buf, cookies[0]);
/*     */       } else {
/* 131 */         Cookie[] cookiesSorted = Arrays.<Cookie>copyOf(cookies, cookies.length);
/* 132 */         Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
/* 133 */         for (Cookie c : cookiesSorted) {
/* 134 */           encode(buf, c);
/*     */         }
/*     */       } 
/*     */     } else {
/* 138 */       for (Cookie c : cookies) {
/* 139 */         encode(buf, c);
/*     */       }
/*     */     } 
/* 142 */     return CookieUtil.stripTrailingSeparatorOrNull(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(Collection<? extends Cookie> cookies) {
/* 153 */     if (((Collection)ObjectUtil.checkNotNull(cookies, "cookies")).isEmpty()) {
/* 154 */       return null;
/*     */     }
/*     */     
/* 157 */     StringBuilder buf = CookieUtil.stringBuilder();
/* 158 */     if (this.strict) {
/* 159 */       if (cookies.size() == 1) {
/* 160 */         encode(buf, cookies.iterator().next());
/*     */       } else {
/* 162 */         Cookie[] cookiesSorted = cookies.<Cookie>toArray(new Cookie[0]);
/* 163 */         Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
/* 164 */         for (Cookie c : cookiesSorted) {
/* 165 */           encode(buf, c);
/*     */         }
/*     */       } 
/*     */     } else {
/* 169 */       for (Cookie c : cookies) {
/* 170 */         encode(buf, c);
/*     */       }
/*     */     } 
/* 173 */     return CookieUtil.stripTrailingSeparatorOrNull(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(Iterable<? extends Cookie> cookies) {
/* 183 */     Iterator<? extends Cookie> cookiesIt = ((Iterable<? extends Cookie>)ObjectUtil.checkNotNull(cookies, "cookies")).iterator();
/* 184 */     if (!cookiesIt.hasNext()) {
/* 185 */       return null;
/*     */     }
/*     */     
/* 188 */     StringBuilder buf = CookieUtil.stringBuilder();
/* 189 */     if (this.strict) {
/* 190 */       Cookie firstCookie = cookiesIt.next();
/* 191 */       if (!cookiesIt.hasNext()) {
/* 192 */         encode(buf, firstCookie);
/*     */       } else {
/* 194 */         List<Cookie> cookiesList = InternalThreadLocalMap.get().arrayList();
/* 195 */         cookiesList.add(firstCookie);
/* 196 */         while (cookiesIt.hasNext()) {
/* 197 */           cookiesList.add(cookiesIt.next());
/*     */         }
/* 199 */         Cookie[] cookiesSorted = cookiesList.<Cookie>toArray(new Cookie[0]);
/* 200 */         Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
/* 201 */         for (Cookie c : cookiesSorted) {
/* 202 */           encode(buf, c);
/*     */         }
/*     */       } 
/*     */     } else {
/* 206 */       while (cookiesIt.hasNext()) {
/* 207 */         encode(buf, cookiesIt.next());
/*     */       }
/*     */     } 
/* 210 */     return CookieUtil.stripTrailingSeparatorOrNull(buf);
/*     */   }
/*     */   
/*     */   private void encode(StringBuilder buf, Cookie c) {
/* 214 */     String name = c.name();
/* 215 */     String value = (c.value() != null) ? c.value() : "";
/*     */     
/* 217 */     validateCookie(name, value);
/*     */     
/* 219 */     if (c.wrap()) {
/* 220 */       CookieUtil.addQuoted(buf, name, value);
/*     */     } else {
/* 222 */       CookieUtil.add(buf, name, value);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\ClientCookieEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */