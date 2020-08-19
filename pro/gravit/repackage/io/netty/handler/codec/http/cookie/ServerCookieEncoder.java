/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ServerCookieEncoder
/*     */   extends CookieEncoder
/*     */ {
/*  61 */   public static final ServerCookieEncoder STRICT = new ServerCookieEncoder(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final ServerCookieEncoder LAX = new ServerCookieEncoder(false);
/*     */   
/*     */   private ServerCookieEncoder(boolean strict) {
/*  70 */     super(strict);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(String name, String value) {
/*  81 */     return encode(new DefaultCookie(name, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(Cookie cookie) {
/*  91 */     String name = ((Cookie)ObjectUtil.checkNotNull(cookie, "cookie")).name();
/*  92 */     String value = (cookie.value() != null) ? cookie.value() : "";
/*     */     
/*  94 */     validateCookie(name, value);
/*     */     
/*  96 */     StringBuilder buf = CookieUtil.stringBuilder();
/*     */     
/*  98 */     if (cookie.wrap()) {
/*  99 */       CookieUtil.addQuoted(buf, name, value);
/*     */     } else {
/* 101 */       CookieUtil.add(buf, name, value);
/*     */     } 
/*     */     
/* 104 */     if (cookie.maxAge() != Long.MIN_VALUE) {
/* 105 */       CookieUtil.add(buf, "Max-Age", cookie.maxAge());
/* 106 */       Date expires = new Date(cookie.maxAge() * 1000L + System.currentTimeMillis());
/* 107 */       buf.append("Expires");
/* 108 */       buf.append('=');
/* 109 */       DateFormatter.append(expires, buf);
/* 110 */       buf.append(';');
/* 111 */       buf.append(' ');
/*     */     } 
/*     */     
/* 114 */     if (cookie.path() != null) {
/* 115 */       CookieUtil.add(buf, "Path", cookie.path());
/*     */     }
/*     */     
/* 118 */     if (cookie.domain() != null) {
/* 119 */       CookieUtil.add(buf, "Domain", cookie.domain());
/*     */     }
/* 121 */     if (cookie.isSecure()) {
/* 122 */       CookieUtil.add(buf, "Secure");
/*     */     }
/* 124 */     if (cookie.isHttpOnly()) {
/* 125 */       CookieUtil.add(buf, "HTTPOnly");
/*     */     }
/*     */     
/* 128 */     return CookieUtil.stripTrailingSeparator(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> dedup(List<String> encoded, Map<String, Integer> nameToLastIndex) {
/* 138 */     boolean[] isLastInstance = new boolean[encoded.size()];
/* 139 */     for (Iterator<Integer> iterator = nameToLastIndex.values().iterator(); iterator.hasNext(); ) { int idx = ((Integer)iterator.next()).intValue();
/* 140 */       isLastInstance[idx] = true; }
/*     */     
/* 142 */     List<String> dedupd = new ArrayList<String>(nameToLastIndex.size());
/* 143 */     for (int i = 0, n = encoded.size(); i < n; i++) {
/* 144 */       if (isLastInstance[i]) {
/* 145 */         dedupd.add(encoded.get(i));
/*     */       }
/*     */     } 
/* 148 */     return dedupd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> encode(Cookie... cookies) {
/*     */     int j;
/* 158 */     if (((Cookie[])ObjectUtil.checkNotNull(cookies, "cookies")).length == 0) {
/* 159 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 162 */     List<String> encoded = new ArrayList<String>(cookies.length);
/* 163 */     Map<String, Integer> nameToIndex = (this.strict && cookies.length > 1) ? new HashMap<String, Integer>() : null;
/* 164 */     boolean hasDupdName = false;
/* 165 */     for (int i = 0; i < cookies.length; i++) {
/* 166 */       Cookie c = cookies[i];
/* 167 */       encoded.add(encode(c));
/* 168 */       if (nameToIndex != null) {
/* 169 */         j = hasDupdName | ((nameToIndex.put(c.name(), Integer.valueOf(i)) != null) ? 1 : 0);
/*     */       }
/*     */     } 
/* 172 */     return (j != 0) ? dedup(encoded, nameToIndex) : encoded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> encode(Collection<? extends Cookie> cookies) {
/*     */     int j;
/* 182 */     if (((Collection)ObjectUtil.checkNotNull(cookies, "cookies")).isEmpty()) {
/* 183 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 186 */     List<String> encoded = new ArrayList<String>(cookies.size());
/* 187 */     Map<String, Integer> nameToIndex = (this.strict && cookies.size() > 1) ? new HashMap<String, Integer>() : null;
/* 188 */     int i = 0;
/* 189 */     boolean hasDupdName = false;
/* 190 */     for (Cookie c : cookies) {
/* 191 */       encoded.add(encode(c));
/* 192 */       if (nameToIndex != null) {
/* 193 */         j = hasDupdName | ((nameToIndex.put(c.name(), Integer.valueOf(i++)) != null) ? 1 : 0);
/*     */       }
/*     */     } 
/* 196 */     return (j != 0) ? dedup(encoded, nameToIndex) : encoded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> encode(Iterable<? extends Cookie> cookies) {
/*     */     int j;
/* 206 */     Iterator<? extends Cookie> cookiesIt = ((Iterable<? extends Cookie>)ObjectUtil.checkNotNull(cookies, "cookies")).iterator();
/* 207 */     if (!cookiesIt.hasNext()) {
/* 208 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 211 */     List<String> encoded = new ArrayList<String>();
/* 212 */     Cookie firstCookie = cookiesIt.next();
/* 213 */     Map<String, Integer> nameToIndex = (this.strict && cookiesIt.hasNext()) ? new HashMap<String, Integer>() : null;
/* 214 */     int i = 0;
/* 215 */     encoded.add(encode(firstCookie));
/* 216 */     boolean hasDupdName = (nameToIndex != null && nameToIndex.put(firstCookie.name(), Integer.valueOf(i++)) != null);
/* 217 */     while (cookiesIt.hasNext()) {
/* 218 */       Cookie c = cookiesIt.next();
/* 219 */       encoded.add(encode(c));
/* 220 */       if (nameToIndex != null) {
/* 221 */         j = hasDupdName | ((nameToIndex.put(c.name(), Integer.valueOf(i++)) != null) ? 1 : 0);
/*     */       }
/*     */     } 
/* 224 */     return (j != 0) ? dedup(encoded, nameToIndex) : encoded;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\ServerCookieEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */