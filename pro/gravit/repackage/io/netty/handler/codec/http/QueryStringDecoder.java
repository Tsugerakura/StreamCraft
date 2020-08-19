/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QueryStringDecoder
/*     */ {
/*     */   private static final int DEFAULT_MAX_PARAMS = 1024;
/*     */   private final Charset charset;
/*     */   private final String uri;
/*     */   private final int maxParams;
/*     */   private final boolean semicolonIsNormalChar;
/*     */   private int pathEndIdx;
/*     */   private String path;
/*     */   private Map<String, List<String>> params;
/*     */   
/*     */   public QueryStringDecoder(String uri) {
/*  80 */     this(uri, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(String uri, boolean hasPath) {
/*  88 */     this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(String uri, Charset charset) {
/*  96 */     this(uri, charset, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
/* 104 */     this(uri, charset, hasPath, 1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
/* 112 */     this(uri, charset, hasPath, maxParams, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams, boolean semicolonIsNormalChar) {
/* 121 */     this.uri = (String)ObjectUtil.checkNotNull(uri, "uri");
/* 122 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/* 123 */     this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
/* 124 */     this.semicolonIsNormalChar = semicolonIsNormalChar;
/*     */ 
/*     */     
/* 127 */     this.pathEndIdx = hasPath ? -1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(URI uri) {
/* 135 */     this(uri, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(URI uri, Charset charset) {
/* 143 */     this(uri, charset, 1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
/* 151 */     this(uri, charset, maxParams, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringDecoder(URI uri, Charset charset, int maxParams, boolean semicolonIsNormalChar) {
/* 159 */     String rawPath = uri.getRawPath();
/* 160 */     if (rawPath == null) {
/* 161 */       rawPath = "";
/*     */     }
/* 163 */     String rawQuery = uri.getRawQuery();
/*     */     
/* 165 */     this.uri = (rawQuery == null) ? rawPath : (rawPath + '?' + rawQuery);
/* 166 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/* 167 */     this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
/* 168 */     this.semicolonIsNormalChar = semicolonIsNormalChar;
/* 169 */     this.pathEndIdx = rawPath.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 174 */     return uri();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String uri() {
/* 181 */     return this.uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String path() {
/* 188 */     if (this.path == null) {
/* 189 */       this.path = decodeComponent(this.uri, 0, pathEndIdx(), this.charset, true);
/*     */     }
/* 191 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> parameters() {
/* 198 */     if (this.params == null) {
/* 199 */       this.params = decodeParams(this.uri, pathEndIdx(), this.charset, this.maxParams, this.semicolonIsNormalChar);
/*     */     }
/* 201 */     return this.params;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String rawPath() {
/* 208 */     return this.uri.substring(0, pathEndIdx());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String rawQuery() {
/* 215 */     int start = pathEndIdx() + 1;
/* 216 */     return (start < this.uri.length()) ? this.uri.substring(start) : "";
/*     */   }
/*     */   
/*     */   private int pathEndIdx() {
/* 220 */     if (this.pathEndIdx == -1) {
/* 221 */       this.pathEndIdx = findPathEndIndex(this.uri);
/*     */     }
/* 223 */     return this.pathEndIdx;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit, boolean semicolonIsNormalChar) {
/* 228 */     int len = s.length();
/* 229 */     if (from >= len) {
/* 230 */       return Collections.emptyMap();
/*     */     }
/* 232 */     if (s.charAt(from) == '?') {
/* 233 */       from++;
/*     */     }
/* 235 */     Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
/* 236 */     int nameStart = from;
/* 237 */     int valueStart = -1;
/*     */     
/*     */     int i;
/* 240 */     for (i = from; i < len; i++) {
/* 241 */       switch (s.charAt(i)) {
/*     */         case '=':
/* 243 */           if (nameStart == i) {
/* 244 */             nameStart = i + 1; break;
/* 245 */           }  if (valueStart < nameStart) {
/* 246 */             valueStart = i + 1;
/*     */           }
/*     */           break;
/*     */         case ';':
/* 250 */           if (semicolonIsNormalChar) {
/*     */             break;
/*     */           }
/*     */ 
/*     */         
/*     */         case '&':
/* 256 */           paramsLimit--;
/* 257 */           if (addParam(s, nameStart, valueStart, i, params, charset) && paramsLimit == 0) {
/* 258 */             return params;
/*     */           }
/*     */           
/* 261 */           nameStart = i + 1;
/*     */           break;
/*     */         
/*     */         case '#':
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 269 */     addParam(s, nameStart, valueStart, i, params, charset);
/* 270 */     return params;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean addParam(String s, int nameStart, int valueStart, int valueEnd, Map<String, List<String>> params, Charset charset) {
/* 275 */     if (nameStart >= valueEnd) {
/* 276 */       return false;
/*     */     }
/* 278 */     if (valueStart <= nameStart) {
/* 279 */       valueStart = valueEnd + 1;
/*     */     }
/* 281 */     String name = decodeComponent(s, nameStart, valueStart - 1, charset, false);
/* 282 */     String value = decodeComponent(s, valueStart, valueEnd, charset, false);
/* 283 */     List<String> values = params.get(name);
/* 284 */     if (values == null) {
/* 285 */       values = new ArrayList<String>(1);
/* 286 */       params.put(name, values);
/*     */     } 
/* 288 */     values.add(value);
/* 289 */     return true;
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
/*     */   public static String decodeComponent(String s) {
/* 304 */     return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
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
/*     */   public static String decodeComponent(String s, Charset charset) {
/* 330 */     if (s == null) {
/* 331 */       return "";
/*     */     }
/* 333 */     return decodeComponent(s, 0, s.length(), charset, false);
/*     */   }
/*     */   
/*     */   private static String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean isPath) {
/* 337 */     int len = toExcluded - from;
/* 338 */     if (len <= 0) {
/* 339 */       return "";
/*     */     }
/* 341 */     int firstEscaped = -1;
/* 342 */     for (int i = from; i < toExcluded; i++) {
/* 343 */       char c = s.charAt(i);
/* 344 */       if (c == '%' || (c == '+' && !isPath)) {
/* 345 */         firstEscaped = i;
/*     */         break;
/*     */       } 
/*     */     } 
/* 349 */     if (firstEscaped == -1) {
/* 350 */       return s.substring(from, toExcluded);
/*     */     }
/*     */ 
/*     */     
/* 354 */     int decodedCapacity = (toExcluded - firstEscaped) / 3;
/* 355 */     byte[] buf = PlatformDependent.allocateUninitializedArray(decodedCapacity);
/*     */ 
/*     */     
/* 358 */     StringBuilder strBuf = new StringBuilder(len);
/* 359 */     strBuf.append(s, from, firstEscaped);
/*     */     
/* 361 */     for (int j = firstEscaped; j < toExcluded; j++) {
/* 362 */       char c = s.charAt(j);
/* 363 */       if (c != '%') {
/* 364 */         strBuf.append((c != '+' || isPath) ? c : 32);
/*     */       }
/*     */       else {
/*     */         
/* 368 */         int bufIdx = 0;
/*     */         do {
/* 370 */           if (j + 3 > toExcluded) {
/* 371 */             throw new IllegalArgumentException("unterminated escape sequence at index " + j + " of: " + s);
/*     */           }
/* 373 */           buf[bufIdx++] = StringUtil.decodeHexByte(s, j + 1);
/* 374 */           j += 3;
/* 375 */         } while (j < toExcluded && s.charAt(j) == '%');
/* 376 */         j--;
/*     */         
/* 378 */         strBuf.append(new String(buf, 0, bufIdx, charset));
/*     */       } 
/* 380 */     }  return strBuf.toString();
/*     */   }
/*     */   
/*     */   private static int findPathEndIndex(String uri) {
/* 384 */     int len = uri.length();
/* 385 */     for (int i = 0; i < len; i++) {
/* 386 */       char c = uri.charAt(i);
/* 387 */       if (c == '?' || c == '#') {
/* 388 */         return i;
/*     */       }
/*     */     } 
/* 391 */     return len;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\QueryStringDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */