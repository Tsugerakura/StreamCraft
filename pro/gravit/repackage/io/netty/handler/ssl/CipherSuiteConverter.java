/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ final class CipherSuiteConverter
/*     */ {
/*  39 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   private static final Pattern JAVA_CIPHERSUITE_PATTERN = Pattern.compile("^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   private static final Pattern OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile("^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private static final Pattern JAVA_AES_CBC_PATTERN = Pattern.compile("^(AES)_([0-9]+)_CBC$");
/*  84 */   private static final Pattern JAVA_AES_PATTERN = Pattern.compile("^(AES)_([0-9]+)_(.*)$");
/*  85 */   private static final Pattern OPENSSL_AES_CBC_PATTERN = Pattern.compile("^(AES)([0-9]+)$");
/*  86 */   private static final Pattern OPENSSL_AES_PATTERN = Pattern.compile("^(AES)([0-9]+)-(.*)$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   private static final ConcurrentMap<String, String> j2o = PlatformDependent.newConcurrentHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   private static final ConcurrentMap<String, Map<String, String>> o2j = PlatformDependent.newConcurrentHashMap();
/*     */   
/*     */   private static final Map<String, String> j2oTls13;
/*     */   private static final Map<String, Map<String, String>> o2jTls13;
/*     */   
/*     */   static {
/* 105 */     Map<String, String> j2oTls13Map = new HashMap<String, String>();
/* 106 */     j2oTls13Map.put("TLS_AES_128_GCM_SHA256", "AEAD-AES128-GCM-SHA256");
/* 107 */     j2oTls13Map.put("TLS_AES_256_GCM_SHA384", "AEAD-AES256-GCM-SHA384");
/* 108 */     j2oTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", "AEAD-CHACHA20-POLY1305-SHA256");
/* 109 */     j2oTls13 = Collections.unmodifiableMap(j2oTls13Map);
/*     */     
/* 111 */     Map<String, Map<String, String>> o2jTls13Map = new HashMap<String, Map<String, String>>();
/* 112 */     o2jTls13Map.put("TLS_AES_128_GCM_SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
/* 113 */     o2jTls13Map.put("TLS_AES_256_GCM_SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
/* 114 */     o2jTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
/* 115 */     o2jTls13Map.put("AEAD-AES128-GCM-SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
/* 116 */     o2jTls13Map.put("AEAD-AES256-GCM-SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
/* 117 */     o2jTls13Map.put("AEAD-CHACHA20-POLY1305-SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
/* 118 */     o2jTls13 = Collections.unmodifiableMap(o2jTls13Map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void clearCache() {
/* 125 */     j2o.clear();
/* 126 */     o2j.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isJ2OCached(String key, String value) {
/* 133 */     return value.equals(j2o.get(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isO2JCached(String key, String protocol, String value) {
/* 140 */     Map<String, String> p2j = o2j.get(key);
/* 141 */     if (p2j == null) {
/* 142 */       return false;
/*     */     }
/* 144 */     return value.equals(p2j.get(protocol));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String toOpenSsl(String javaCipherSuite, boolean boringSSL) {
/* 154 */     String converted = j2o.get(javaCipherSuite);
/* 155 */     if (converted != null) {
/* 156 */       return converted;
/*     */     }
/* 158 */     return cacheFromJava(javaCipherSuite, boringSSL);
/*     */   }
/*     */   
/*     */   private static String cacheFromJava(String javaCipherSuite, boolean boringSSL) {
/* 162 */     String converted = j2oTls13.get(javaCipherSuite);
/* 163 */     if (converted != null) {
/* 164 */       return boringSSL ? converted : javaCipherSuite;
/*     */     }
/*     */     
/* 167 */     String openSslCipherSuite = toOpenSslUncached(javaCipherSuite, boringSSL);
/* 168 */     if (openSslCipherSuite == null) {
/* 169 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 173 */     j2o.putIfAbsent(javaCipherSuite, openSslCipherSuite);
/*     */ 
/*     */     
/* 176 */     String javaCipherSuiteSuffix = javaCipherSuite.substring(4);
/* 177 */     Map<String, String> p2j = new HashMap<String, String>(4);
/* 178 */     p2j.put("", javaCipherSuiteSuffix);
/* 179 */     p2j.put("SSL", "SSL_" + javaCipherSuiteSuffix);
/* 180 */     p2j.put("TLS", "TLS_" + javaCipherSuiteSuffix);
/* 181 */     o2j.put(openSslCipherSuite, p2j);
/*     */     
/* 183 */     logger.debug("Cipher suite mapping: {} => {}", javaCipherSuite, openSslCipherSuite);
/*     */     
/* 185 */     return openSslCipherSuite;
/*     */   }
/*     */   
/*     */   static String toOpenSslUncached(String javaCipherSuite, boolean boringSSL) {
/* 189 */     String converted = j2oTls13.get(javaCipherSuite);
/* 190 */     if (converted != null) {
/* 191 */       return boringSSL ? converted : javaCipherSuite;
/*     */     }
/*     */     
/* 194 */     Matcher m = JAVA_CIPHERSUITE_PATTERN.matcher(javaCipherSuite);
/* 195 */     if (!m.matches()) {
/* 196 */       return null;
/*     */     }
/*     */     
/* 199 */     String handshakeAlgo = toOpenSslHandshakeAlgo(m.group(1));
/* 200 */     String bulkCipher = toOpenSslBulkCipher(m.group(2));
/* 201 */     String hmacAlgo = toOpenSslHmacAlgo(m.group(3));
/* 202 */     if (handshakeAlgo.isEmpty())
/* 203 */       return bulkCipher + '-' + hmacAlgo; 
/* 204 */     if (bulkCipher.contains("CHACHA20")) {
/* 205 */       return handshakeAlgo + '-' + bulkCipher;
/*     */     }
/* 207 */     return handshakeAlgo + '-' + bulkCipher + '-' + hmacAlgo;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String toOpenSslHandshakeAlgo(String handshakeAlgo) {
/* 212 */     boolean export = handshakeAlgo.endsWith("_EXPORT");
/* 213 */     if (export) {
/* 214 */       handshakeAlgo = handshakeAlgo.substring(0, handshakeAlgo.length() - 7);
/*     */     }
/*     */     
/* 217 */     if ("RSA".equals(handshakeAlgo)) {
/* 218 */       handshakeAlgo = "";
/* 219 */     } else if (handshakeAlgo.endsWith("_anon")) {
/* 220 */       handshakeAlgo = 'A' + handshakeAlgo.substring(0, handshakeAlgo.length() - 5);
/*     */     } 
/*     */     
/* 223 */     if (export) {
/* 224 */       if (handshakeAlgo.isEmpty()) {
/* 225 */         handshakeAlgo = "EXP";
/*     */       } else {
/* 227 */         handshakeAlgo = "EXP-" + handshakeAlgo;
/*     */       } 
/*     */     }
/*     */     
/* 231 */     return handshakeAlgo.replace('_', '-');
/*     */   }
/*     */   
/*     */   private static String toOpenSslBulkCipher(String bulkCipher) {
/* 235 */     if (bulkCipher.startsWith("AES_")) {
/* 236 */       Matcher m = JAVA_AES_CBC_PATTERN.matcher(bulkCipher);
/* 237 */       if (m.matches()) {
/* 238 */         return m.replaceFirst("$1$2");
/*     */       }
/*     */       
/* 241 */       m = JAVA_AES_PATTERN.matcher(bulkCipher);
/* 242 */       if (m.matches()) {
/* 243 */         return m.replaceFirst("$1$2-$3");
/*     */       }
/*     */     } 
/*     */     
/* 247 */     if ("3DES_EDE_CBC".equals(bulkCipher)) {
/* 248 */       return "DES-CBC3";
/*     */     }
/*     */     
/* 251 */     if ("RC4_128".equals(bulkCipher) || "RC4_40".equals(bulkCipher)) {
/* 252 */       return "RC4";
/*     */     }
/*     */     
/* 255 */     if ("DES40_CBC".equals(bulkCipher) || "DES_CBC_40".equals(bulkCipher)) {
/* 256 */       return "DES-CBC";
/*     */     }
/*     */     
/* 259 */     if ("RC2_CBC_40".equals(bulkCipher)) {
/* 260 */       return "RC2-CBC";
/*     */     }
/*     */     
/* 263 */     return bulkCipher.replace('_', '-');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toOpenSslHmacAlgo(String hmacAlgo) {
/* 273 */     return hmacAlgo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String toJava(String openSslCipherSuite, String protocol) {
/* 283 */     Map<String, String> p2j = o2j.get(openSslCipherSuite);
/* 284 */     if (p2j == null) {
/* 285 */       p2j = cacheFromOpenSsl(openSslCipherSuite);
/*     */ 
/*     */       
/* 288 */       if (p2j == null) {
/* 289 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 293 */     String javaCipherSuite = p2j.get(protocol);
/* 294 */     if (javaCipherSuite == null) {
/* 295 */       String cipher = p2j.get("");
/* 296 */       if (cipher == null) {
/* 297 */         return null;
/*     */       }
/* 299 */       javaCipherSuite = protocol + '_' + cipher;
/*     */     } 
/*     */     
/* 302 */     return javaCipherSuite;
/*     */   }
/*     */   
/*     */   private static Map<String, String> cacheFromOpenSsl(String openSslCipherSuite) {
/* 306 */     Map<String, String> converted = o2jTls13.get(openSslCipherSuite);
/* 307 */     if (converted != null) {
/* 308 */       return converted;
/*     */     }
/*     */     
/* 311 */     String javaCipherSuiteSuffix = toJavaUncached0(openSslCipherSuite, false);
/* 312 */     if (javaCipherSuiteSuffix == null) {
/* 313 */       return null;
/*     */     }
/*     */     
/* 316 */     String javaCipherSuiteSsl = "SSL_" + javaCipherSuiteSuffix;
/* 317 */     String javaCipherSuiteTls = "TLS_" + javaCipherSuiteSuffix;
/*     */ 
/*     */     
/* 320 */     Map<String, String> p2j = new HashMap<String, String>(4);
/* 321 */     p2j.put("", javaCipherSuiteSuffix);
/* 322 */     p2j.put("SSL", javaCipherSuiteSsl);
/* 323 */     p2j.put("TLS", javaCipherSuiteTls);
/* 324 */     o2j.putIfAbsent(openSslCipherSuite, p2j);
/*     */ 
/*     */     
/* 327 */     j2o.putIfAbsent(javaCipherSuiteTls, openSslCipherSuite);
/* 328 */     j2o.putIfAbsent(javaCipherSuiteSsl, openSslCipherSuite);
/*     */     
/* 330 */     logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteTls, openSslCipherSuite);
/* 331 */     logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteSsl, openSslCipherSuite);
/*     */     
/* 333 */     return p2j;
/*     */   }
/*     */   
/*     */   static String toJavaUncached(String openSslCipherSuite) {
/* 337 */     return toJavaUncached0(openSslCipherSuite, true);
/*     */   }
/*     */   private static String toJavaUncached0(String openSslCipherSuite, boolean checkTls13) {
/*     */     boolean export;
/* 341 */     if (checkTls13) {
/* 342 */       Map<String, String> converted = o2jTls13.get(openSslCipherSuite);
/* 343 */       if (converted != null) {
/* 344 */         return converted.get("TLS");
/*     */       }
/*     */     } 
/*     */     
/* 348 */     Matcher m = OPENSSL_CIPHERSUITE_PATTERN.matcher(openSslCipherSuite);
/* 349 */     if (!m.matches()) {
/* 350 */       return null;
/*     */     }
/*     */     
/* 353 */     String handshakeAlgo = m.group(1);
/*     */     
/* 355 */     if (handshakeAlgo == null) {
/* 356 */       handshakeAlgo = "";
/* 357 */       export = false;
/* 358 */     } else if (handshakeAlgo.startsWith("EXP-")) {
/* 359 */       handshakeAlgo = handshakeAlgo.substring(4);
/* 360 */       export = true;
/* 361 */     } else if ("EXP".equals(handshakeAlgo)) {
/* 362 */       handshakeAlgo = "";
/* 363 */       export = true;
/*     */     } else {
/* 365 */       export = false;
/*     */     } 
/*     */     
/* 368 */     handshakeAlgo = toJavaHandshakeAlgo(handshakeAlgo, export);
/* 369 */     String bulkCipher = toJavaBulkCipher(m.group(2), export);
/* 370 */     String hmacAlgo = toJavaHmacAlgo(m.group(3));
/*     */     
/* 372 */     String javaCipherSuite = handshakeAlgo + "_WITH_" + bulkCipher + '_' + hmacAlgo;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 377 */     return bulkCipher.contains("CHACHA20") ? (javaCipherSuite + "_SHA256") : javaCipherSuite;
/*     */   }
/*     */   
/*     */   private static String toJavaHandshakeAlgo(String handshakeAlgo, boolean export) {
/* 381 */     if (handshakeAlgo.isEmpty()) {
/* 382 */       handshakeAlgo = "RSA";
/* 383 */     } else if ("ADH".equals(handshakeAlgo)) {
/* 384 */       handshakeAlgo = "DH_anon";
/* 385 */     } else if ("AECDH".equals(handshakeAlgo)) {
/* 386 */       handshakeAlgo = "ECDH_anon";
/*     */     } 
/*     */     
/* 389 */     handshakeAlgo = handshakeAlgo.replace('-', '_');
/* 390 */     if (export) {
/* 391 */       return handshakeAlgo + "_EXPORT";
/*     */     }
/* 393 */     return handshakeAlgo;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String toJavaBulkCipher(String bulkCipher, boolean export) {
/* 398 */     if (bulkCipher.startsWith("AES")) {
/* 399 */       Matcher m = OPENSSL_AES_CBC_PATTERN.matcher(bulkCipher);
/* 400 */       if (m.matches()) {
/* 401 */         return m.replaceFirst("$1_$2_CBC");
/*     */       }
/*     */       
/* 404 */       m = OPENSSL_AES_PATTERN.matcher(bulkCipher);
/* 405 */       if (m.matches()) {
/* 406 */         return m.replaceFirst("$1_$2_$3");
/*     */       }
/*     */     } 
/*     */     
/* 410 */     if ("DES-CBC3".equals(bulkCipher)) {
/* 411 */       return "3DES_EDE_CBC";
/*     */     }
/*     */     
/* 414 */     if ("RC4".equals(bulkCipher)) {
/* 415 */       if (export) {
/* 416 */         return "RC4_40";
/*     */       }
/* 418 */       return "RC4_128";
/*     */     } 
/*     */ 
/*     */     
/* 422 */     if ("DES-CBC".equals(bulkCipher)) {
/* 423 */       if (export) {
/* 424 */         return "DES_CBC_40";
/*     */       }
/* 426 */       return "DES_CBC";
/*     */     } 
/*     */ 
/*     */     
/* 430 */     if ("RC2-CBC".equals(bulkCipher)) {
/* 431 */       if (export) {
/* 432 */         return "RC2_CBC_40";
/*     */       }
/* 434 */       return "RC2_CBC";
/*     */     } 
/*     */ 
/*     */     
/* 438 */     return bulkCipher.replace('-', '_');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String toJavaHmacAlgo(String hmacAlgo) {
/* 448 */     return hmacAlgo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void convertToCipherStrings(Iterable<String> cipherSuites, StringBuilder cipherBuilder, StringBuilder cipherTLSv13Builder, boolean boringSSL) {
/* 459 */     for (String c : cipherSuites) {
/* 460 */       if (c == null) {
/*     */         break;
/*     */       }
/*     */       
/* 464 */       String converted = toOpenSsl(c, boringSSL);
/* 465 */       if (converted == null) {
/* 466 */         converted = c;
/*     */       }
/*     */       
/* 469 */       if (!OpenSsl.isCipherSuiteAvailable(converted)) {
/* 470 */         throw new IllegalArgumentException("unsupported cipher suite: " + c + '(' + converted + ')');
/*     */       }
/*     */       
/* 473 */       if (SslUtils.isTLSv13Cipher(converted) || SslUtils.isTLSv13Cipher(c)) {
/* 474 */         cipherTLSv13Builder.append(converted);
/* 475 */         cipherTLSv13Builder.append(':'); continue;
/*     */       } 
/* 477 */       cipherBuilder.append(converted);
/* 478 */       cipherBuilder.append(':');
/*     */     } 
/*     */ 
/*     */     
/* 482 */     if (cipherBuilder.length() == 0 && cipherTLSv13Builder.length() == 0) {
/* 483 */       throw new IllegalArgumentException("empty cipher suites");
/*     */     }
/* 485 */     if (cipherBuilder.length() > 0) {
/* 486 */       cipherBuilder.setLength(cipherBuilder.length() - 1);
/*     */     }
/* 488 */     if (cipherTLSv13Builder.length() > 0)
/* 489 */       cipherTLSv13Builder.setLength(cipherTLSv13Builder.length() - 1); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\CipherSuiteConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */