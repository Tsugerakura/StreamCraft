/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cors;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultHttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.EmptyHttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
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
/*     */ public final class CorsConfig
/*     */ {
/*     */   private final Set<String> origins;
/*     */   private final boolean anyOrigin;
/*     */   private final boolean enabled;
/*     */   private final Set<String> exposeHeaders;
/*     */   private final boolean allowCredentials;
/*     */   private final long maxAge;
/*     */   private final Set<HttpMethod> allowedRequestMethods;
/*     */   private final Set<String> allowedRequestHeaders;
/*     */   private final boolean allowNullOrigin;
/*     */   private final Map<CharSequence, Callable<?>> preflightHeaders;
/*     */   private final boolean shortCircuit;
/*     */   
/*     */   CorsConfig(CorsConfigBuilder builder) {
/*  50 */     this.origins = new LinkedHashSet<String>(builder.origins);
/*  51 */     this.anyOrigin = builder.anyOrigin;
/*  52 */     this.enabled = builder.enabled;
/*  53 */     this.exposeHeaders = builder.exposeHeaders;
/*  54 */     this.allowCredentials = builder.allowCredentials;
/*  55 */     this.maxAge = builder.maxAge;
/*  56 */     this.allowedRequestMethods = builder.requestMethods;
/*  57 */     this.allowedRequestHeaders = builder.requestHeaders;
/*  58 */     this.allowNullOrigin = builder.allowNullOrigin;
/*  59 */     this.preflightHeaders = builder.preflightHeaders;
/*  60 */     this.shortCircuit = builder.shortCircuit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCorsSupportEnabled() {
/*  69 */     return this.enabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAnyOriginSupported() {
/*  78 */     return this.anyOrigin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String origin() {
/*  87 */     return this.origins.isEmpty() ? "*" : this.origins.iterator().next();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> origins() {
/*  96 */     return this.origins;
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
/*     */   public boolean isNullOriginAllowed() {
/* 109 */     return this.allowNullOrigin;
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
/*     */   public Set<String> exposedHeaders() {
/* 135 */     return Collections.unmodifiableSet(this.exposeHeaders);
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
/*     */   public boolean isCredentialsAllowed() {
/* 156 */     return this.allowCredentials;
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
/*     */   public long maxAge() {
/* 170 */     return this.maxAge;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<HttpMethod> allowedRequestMethods() {
/* 180 */     return Collections.unmodifiableSet(this.allowedRequestMethods);
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
/*     */   public Set<String> allowedRequestHeaders() {
/* 192 */     return Collections.unmodifiableSet(this.allowedRequestHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders preflightResponseHeaders() {
/* 201 */     if (this.preflightHeaders.isEmpty()) {
/* 202 */       return (HttpHeaders)EmptyHttpHeaders.INSTANCE;
/*     */     }
/* 204 */     DefaultHttpHeaders defaultHttpHeaders = new DefaultHttpHeaders();
/* 205 */     for (Map.Entry<CharSequence, Callable<?>> entry : this.preflightHeaders.entrySet()) {
/* 206 */       Object value = getValue(entry.getValue());
/* 207 */       if (value instanceof Iterable) {
/* 208 */         defaultHttpHeaders.add(entry.getKey(), (Iterable)value); continue;
/*     */       } 
/* 210 */       defaultHttpHeaders.add(entry.getKey(), value);
/*     */     } 
/*     */     
/* 213 */     return (HttpHeaders)defaultHttpHeaders;
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
/*     */   public boolean isShortCircuit() {
/* 227 */     return this.shortCircuit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isShortCurcuit() {
/* 235 */     return isShortCircuit();
/*     */   }
/*     */   
/*     */   private static <T> T getValue(Callable<T> callable) {
/*     */     try {
/* 240 */       return callable.call();
/* 241 */     } catch (Exception e) {
/* 242 */       throw new IllegalStateException("Could not generate value for callable [" + callable + ']', e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 248 */     return StringUtil.simpleClassName(this) + "[enabled=" + this.enabled + ", origins=" + this.origins + ", anyOrigin=" + this.anyOrigin + ", exposedHeaders=" + this.exposeHeaders + ", isCredentialsAllowed=" + this.allowCredentials + ", maxAge=" + this.maxAge + ", allowedRequestMethods=" + this.allowedRequestMethods + ", allowedRequestHeaders=" + this.allowedRequestHeaders + ", preflightHeaders=" + this.preflightHeaders + ']';
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
/*     */   @Deprecated
/*     */   public static Builder withAnyOrigin() {
/* 264 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Builder withOrigin(String origin) {
/* 272 */     if ("*".equals(origin)) {
/* 273 */       return new Builder();
/*     */     }
/* 275 */     return new Builder(new String[] { origin });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Builder withOrigins(String... origins) {
/* 283 */     return new Builder(origins);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static class Builder
/*     */   {
/*     */     private final CorsConfigBuilder builder;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder(String... origins) {
/* 299 */       this.builder = new CorsConfigBuilder(origins);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder() {
/* 307 */       this.builder = new CorsConfigBuilder();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder allowNullOrigin() {
/* 315 */       this.builder.allowNullOrigin();
/* 316 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder disable() {
/* 324 */       this.builder.disable();
/* 325 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder exposeHeaders(String... headers) {
/* 333 */       this.builder.exposeHeaders(headers);
/* 334 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder allowCredentials() {
/* 342 */       this.builder.allowCredentials();
/* 343 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder maxAge(long max) {
/* 351 */       this.builder.maxAge(max);
/* 352 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder allowedRequestMethods(HttpMethod... methods) {
/* 360 */       this.builder.allowedRequestMethods(methods);
/* 361 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder allowedRequestHeaders(String... headers) {
/* 369 */       this.builder.allowedRequestHeaders(headers);
/* 370 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder preflightResponseHeader(CharSequence name, Object... values) {
/* 378 */       this.builder.preflightResponseHeader(name, values);
/* 379 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public <T> Builder preflightResponseHeader(CharSequence name, Iterable<T> value) {
/* 387 */       this.builder.preflightResponseHeader(name, value);
/* 388 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public <T> Builder preflightResponseHeader(String name, Callable<T> valueGenerator) {
/* 396 */       this.builder.preflightResponseHeader(name, valueGenerator);
/* 397 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder noPreflightResponseHeaders() {
/* 405 */       this.builder.noPreflightResponseHeaders();
/* 406 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public CorsConfig build() {
/* 414 */       return this.builder.build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Builder shortCurcuit() {
/* 422 */       this.builder.shortCircuit();
/* 423 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static final class DateValueGenerator
/*     */     implements Callable<Date>
/*     */   {
/*     */     public Date call() throws Exception {
/* 435 */       return new Date();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cors\CorsConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */