/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cors;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CorsConfigBuilder
/*     */ {
/*     */   final Set<String> origins;
/*     */   final boolean anyOrigin;
/*     */   boolean allowNullOrigin;
/*     */   
/*     */   public static CorsConfigBuilder forAnyOrigin() {
/*  42 */     return new CorsConfigBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CorsConfigBuilder forOrigin(String origin) {
/*  51 */     if ("*".equals(origin)) {
/*  52 */       return new CorsConfigBuilder();
/*     */     }
/*  54 */     return new CorsConfigBuilder(new String[] { origin });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CorsConfigBuilder forOrigins(String... origins) {
/*  63 */     return new CorsConfigBuilder(origins);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean enabled = true;
/*     */   
/*     */   boolean allowCredentials;
/*     */   
/*  71 */   final Set<String> exposeHeaders = new HashSet<String>();
/*     */   long maxAge;
/*  73 */   final Set<HttpMethod> requestMethods = new HashSet<HttpMethod>();
/*  74 */   final Set<String> requestHeaders = new HashSet<String>();
/*  75 */   final Map<CharSequence, Callable<?>> preflightHeaders = new HashMap<CharSequence, Callable<?>>();
/*     */ 
/*     */   
/*     */   private boolean noPreflightHeaders;
/*     */ 
/*     */   
/*     */   boolean shortCircuit;
/*     */ 
/*     */   
/*     */   CorsConfigBuilder(String... origins) {
/*  85 */     this.origins = new LinkedHashSet<String>(Arrays.asList(origins));
/*  86 */     this.anyOrigin = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   CorsConfigBuilder() {
/*  95 */     this.anyOrigin = true;
/*  96 */     this.origins = Collections.emptySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfigBuilder allowNullOrigin() {
/* 107 */     this.allowNullOrigin = true;
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfigBuilder disable() {
/* 117 */     this.enabled = false;
/* 118 */     return this;
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
/*     */   public CorsConfigBuilder exposeHeaders(String... headers) {
/* 147 */     this.exposeHeaders.addAll(Arrays.asList(headers));
/* 148 */     return this;
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
/*     */   public CorsConfigBuilder exposeHeaders(CharSequence... headers) {
/* 177 */     for (CharSequence header : headers) {
/* 178 */       this.exposeHeaders.add(header.toString());
/*     */     }
/* 180 */     return this;
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
/*     */   public CorsConfigBuilder allowCredentials() {
/* 199 */     this.allowCredentials = true;
/* 200 */     return this;
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
/*     */   public CorsConfigBuilder maxAge(long max) {
/* 213 */     this.maxAge = max;
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfigBuilder allowedRequestMethods(HttpMethod... methods) {
/* 225 */     this.requestMethods.addAll(Arrays.asList(methods));
/* 226 */     return this;
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
/*     */   public CorsConfigBuilder allowedRequestHeaders(String... headers) {
/* 246 */     this.requestHeaders.addAll(Arrays.asList(headers));
/* 247 */     return this;
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
/*     */   public CorsConfigBuilder allowedRequestHeaders(CharSequence... headers) {
/* 267 */     for (CharSequence header : headers) {
/* 268 */       this.requestHeaders.add(header.toString());
/*     */     }
/* 270 */     return this;
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
/*     */   public CorsConfigBuilder preflightResponseHeader(CharSequence name, Object... values) {
/* 284 */     if (values.length == 1) {
/* 285 */       this.preflightHeaders.put(name, new ConstantValueGenerator(values[0]));
/*     */     } else {
/* 287 */       preflightResponseHeader(name, Arrays.asList(values));
/*     */     } 
/* 289 */     return this;
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
/*     */   public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Iterable<T> value) {
/* 304 */     this.preflightHeaders.put(name, new ConstantValueGenerator(value));
/* 305 */     return this;
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
/*     */   public <T> CorsConfigBuilder preflightResponseHeader(CharSequence name, Callable<T> valueGenerator) {
/* 324 */     this.preflightHeaders.put(name, valueGenerator);
/* 325 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfigBuilder noPreflightResponseHeaders() {
/* 334 */     this.noPreflightHeaders = true;
/* 335 */     return this;
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
/*     */   public CorsConfigBuilder shortCircuit() {
/* 349 */     this.shortCircuit = true;
/* 350 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfig build() {
/* 359 */     if (this.preflightHeaders.isEmpty() && !this.noPreflightHeaders) {
/* 360 */       this.preflightHeaders.put(HttpHeaderNames.DATE, DateValueGenerator.INSTANCE);
/* 361 */       this.preflightHeaders.put(HttpHeaderNames.CONTENT_LENGTH, new ConstantValueGenerator("0"));
/*     */     } 
/* 363 */     return new CorsConfig(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ConstantValueGenerator
/*     */     implements Callable<Object>
/*     */   {
/*     */     private final Object value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private ConstantValueGenerator(Object value) {
/* 381 */       if (value == null) {
/* 382 */         throw new IllegalArgumentException("value must not be null");
/*     */       }
/* 384 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object call() {
/* 389 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class DateValueGenerator
/*     */     implements Callable<Date>
/*     */   {
/* 400 */     static final DateValueGenerator INSTANCE = new DateValueGenerator();
/*     */ 
/*     */     
/*     */     public Date call() throws Exception {
/* 404 */       return new Date();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cors\CorsConfigBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */