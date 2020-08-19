/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.UnpooledByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.Buffer;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.Library;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.NativeLibraryLoader;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ public final class OpenSsl
/*     */ {
/*  52 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Throwable UNAVAILABILITY_CAUSE;
/*     */ 
/*     */ 
/*     */   
/*     */   static final List<String> DEFAULT_CIPHERS;
/*     */ 
/*     */ 
/*     */   
/*     */   static final Set<String> AVAILABLE_CIPHER_SUITES;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean USE_KEYMANAGER_FACTORY;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean SUPPORTS_OCSP;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean TLSV13_SUPPORTED;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean IS_BORINGSSL;
/*     */ 
/*     */ 
/*     */   
/*     */   static final Set<String> SUPPORTED_PROTOCOLS_SET;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String CERT = "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----";
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 109 */     Throwable cause = null;
/*     */     
/* 111 */     if (SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.handler.ssl.noOpenSsl", false)) {
/* 112 */       cause = new UnsupportedOperationException("OpenSSL was explicit disabled with -Dio.netty.handler.ssl.noOpenSsl=true");
/*     */ 
/*     */       
/* 115 */       logger.debug("netty-tcnative explicit disabled; " + OpenSslEngine.class
/*     */           
/* 117 */           .getSimpleName() + " will be unavailable.", cause);
/*     */     } else {
/*     */       
/*     */       try {
/* 121 */         Class.forName("pro.gravit.repackage.io.netty.internal.tcnative.SSL", false, OpenSsl.class.getClassLoader());
/* 122 */       } catch (ClassNotFoundException t) {
/* 123 */         cause = t;
/* 124 */         logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class
/*     */             
/* 126 */             .getSimpleName() + " will be unavailable.");
/*     */       } 
/*     */ 
/*     */       
/* 130 */       if (cause == null) {
/*     */         
/*     */         try {
/* 133 */           loadTcNative();
/* 134 */         } catch (Throwable t) {
/* 135 */           cause = t;
/* 136 */           logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class
/*     */               
/* 138 */               .getSimpleName() + " will be unavailable, unless the application has already loaded the symbols by some other means. See https://netty.io/wiki/forked-tomcat-native.html for more information.", t);
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 144 */           String engine = SystemPropertyUtil.get("pro.gravit.repackage.io.netty.handler.ssl.openssl.engine", null);
/* 145 */           if (engine == null) {
/* 146 */             logger.debug("Initialize netty-tcnative using engine: 'default'");
/*     */           } else {
/* 148 */             logger.debug("Initialize netty-tcnative using engine: '{}'", engine);
/*     */           } 
/* 150 */           initializeTcNative(engine);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 155 */           cause = null;
/* 156 */         } catch (Throwable t) {
/* 157 */           if (cause == null) {
/* 158 */             cause = t;
/*     */           }
/* 160 */           logger.debug("Failed to initialize netty-tcnative; " + OpenSslEngine.class
/*     */               
/* 162 */               .getSimpleName() + " will be unavailable. See https://netty.io/wiki/forked-tomcat-native.html for more information.", t);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 168 */     UNAVAILABILITY_CAUSE = cause;
/*     */     
/* 170 */     if (cause == null) {
/* 171 */       logger.debug("netty-tcnative using native library: {}", SSL.versionString());
/*     */       
/* 173 */       List<String> defaultCiphers = new ArrayList<String>();
/* 174 */       Set<String> availableOpenSslCipherSuites = new LinkedHashSet<String>(128);
/* 175 */       boolean supportsKeyManagerFactory = false;
/* 176 */       boolean useKeyManagerFactory = false;
/* 177 */       boolean tlsv13Supported = false;
/*     */       
/* 179 */       IS_BORINGSSL = "BoringSSL".equals(versionString());
/*     */       
/*     */       try {
/* 182 */         long sslCtx = SSLContext.make(63, 1);
/* 183 */         long certBio = 0L;
/* 184 */         long keyBio = 0L;
/* 185 */         long cert = 0L;
/* 186 */         long key = 0L;
/*     */         try {
/*     */           try {
/* 189 */             StringBuilder tlsv13Ciphers = new StringBuilder();
/*     */             
/* 191 */             for (String cipher : SslUtils.TLSV13_CIPHERS) {
/* 192 */               String converted = CipherSuiteConverter.toOpenSsl(cipher, IS_BORINGSSL);
/* 193 */               if (converted != null) {
/* 194 */                 tlsv13Ciphers.append(converted).append(':');
/*     */               }
/*     */             } 
/* 197 */             if (tlsv13Ciphers.length() == 0) {
/* 198 */               tlsv13Supported = false;
/*     */             } else {
/* 200 */               tlsv13Ciphers.setLength(tlsv13Ciphers.length() - 1);
/* 201 */               SSLContext.setCipherSuite(sslCtx, tlsv13Ciphers.toString(), true);
/* 202 */               tlsv13Supported = true;
/*     */             }
/*     */           
/* 205 */           } catch (Exception ignore) {
/* 206 */             tlsv13Supported = false;
/*     */           } 
/*     */           
/* 209 */           SSLContext.setCipherSuite(sslCtx, "ALL", false);
/*     */           
/* 211 */           long ssl = SSL.newSSL(sslCtx, true);
/*     */           try {
/* 213 */             for (String c : SSL.getCiphers(ssl)) {
/*     */               
/* 215 */               if (c != null && !c.isEmpty() && !availableOpenSslCipherSuites.contains(c) && (tlsv13Supported || 
/*     */                 
/* 217 */                 !SslUtils.isTLSv13Cipher(c)))
/*     */               {
/*     */                 
/* 220 */                 availableOpenSslCipherSuites.add(c); } 
/*     */             } 
/* 222 */             if (IS_BORINGSSL)
/*     */             {
/*     */               
/* 225 */               Collections.addAll(availableOpenSslCipherSuites, new String[] { "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "AEAD-AES128-GCM-SHA256", "AEAD-AES256-GCM-SHA384", "AEAD-CHACHA20-POLY1305-SHA256" });
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 234 */             PemEncoded privateKey = PemPrivateKey.valueOf("-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----".getBytes(CharsetUtil.US_ASCII));
/*     */ 
/*     */             
/*     */             try {
/* 238 */               SSLContext.setCertificateCallback(sslCtx, null);
/*     */               
/* 240 */               X509Certificate certificate = selfSignedCertificate();
/* 241 */               certBio = ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, new X509Certificate[] { certificate });
/* 242 */               cert = SSL.parseX509Chain(certBio);
/*     */               
/* 244 */               keyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, privateKey
/* 245 */                   .retain());
/* 246 */               key = SSL.parsePrivateKey(keyBio, null);
/*     */               
/* 248 */               SSL.setKeyMaterial(ssl, cert, key);
/* 249 */               supportsKeyManagerFactory = true;
/*     */               try {
/* 251 */                 boolean propertySet = SystemPropertyUtil.contains("pro.gravit.repackage.io.netty.handler.ssl.openssl.useKeyManagerFactory");
/*     */                 
/* 253 */                 if (!IS_BORINGSSL) {
/* 254 */                   useKeyManagerFactory = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.handler.ssl.openssl.useKeyManagerFactory", true);
/*     */ 
/*     */                   
/* 257 */                   if (propertySet) {
/* 258 */                     logger.info("System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and so will be ignored in the future");
/*     */                   }
/*     */                 }
/*     */                 else {
/*     */                   
/* 263 */                   useKeyManagerFactory = true;
/* 264 */                   if (propertySet) {
/* 265 */                     logger.info("System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and will be ignored when using BoringSSL");
/*     */                   }
/*     */                 }
/*     */               
/*     */               }
/* 270 */               catch (Throwable ignore) {
/* 271 */                 logger.debug("Failed to get useKeyManagerFactory system property.");
/*     */               } 
/* 273 */             } catch (Error ignore) {
/* 274 */               logger.debug("KeyManagerFactory not supported.");
/*     */             } finally {
/* 276 */               privateKey.release();
/*     */             } 
/*     */           } finally {
/* 279 */             SSL.freeSSL(ssl);
/* 280 */             if (certBio != 0L) {
/* 281 */               SSL.freeBIO(certBio);
/*     */             }
/* 283 */             if (keyBio != 0L) {
/* 284 */               SSL.freeBIO(keyBio);
/*     */             }
/* 286 */             if (cert != 0L) {
/* 287 */               SSL.freeX509Chain(cert);
/*     */             }
/* 289 */             if (key != 0L) {
/* 290 */               SSL.freePrivateKey(key);
/*     */             }
/*     */           } 
/*     */         } finally {
/* 294 */           SSLContext.free(sslCtx);
/*     */         } 
/* 296 */       } catch (Exception e) {
/* 297 */         logger.warn("Failed to get the list of available OpenSSL cipher suites.", e);
/*     */       } 
/* 299 */       AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(availableOpenSslCipherSuites);
/*     */       
/* 301 */       Set<String> availableJavaCipherSuites = new LinkedHashSet<String>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2);
/* 302 */       for (String cipher : AVAILABLE_OPENSSL_CIPHER_SUITES) {
/*     */         
/* 304 */         if (!SslUtils.isTLSv13Cipher(cipher)) {
/* 305 */           availableJavaCipherSuites.add(CipherSuiteConverter.toJava(cipher, "TLS"));
/* 306 */           availableJavaCipherSuites.add(CipherSuiteConverter.toJava(cipher, "SSL"));
/*     */           continue;
/*     */         } 
/* 309 */         availableJavaCipherSuites.add(cipher);
/*     */       } 
/*     */ 
/*     */       
/* 313 */       SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, SslUtils.DEFAULT_CIPHER_SUITES);
/* 314 */       SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, SslUtils.TLSV13_CIPHER_SUITES);
/*     */       
/* 316 */       SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, availableJavaCipherSuites);
/* 317 */       DEFAULT_CIPHERS = Collections.unmodifiableList(defaultCiphers);
/*     */       
/* 319 */       AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(availableJavaCipherSuites);
/*     */ 
/*     */       
/* 322 */       Set<String> availableCipherSuites = new LinkedHashSet<String>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size());
/* 323 */       availableCipherSuites.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
/* 324 */       availableCipherSuites.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
/*     */       
/* 326 */       AVAILABLE_CIPHER_SUITES = availableCipherSuites;
/* 327 */       SUPPORTS_KEYMANAGER_FACTORY = supportsKeyManagerFactory;
/* 328 */       USE_KEYMANAGER_FACTORY = useKeyManagerFactory;
/*     */       
/* 330 */       Set<String> protocols = new LinkedHashSet<String>(6);
/*     */       
/* 332 */       protocols.add("SSLv2Hello");
/* 333 */       if (doesSupportProtocol(1, SSL.SSL_OP_NO_SSLv2)) {
/* 334 */         protocols.add("SSLv2");
/*     */       }
/* 336 */       if (doesSupportProtocol(2, SSL.SSL_OP_NO_SSLv3)) {
/* 337 */         protocols.add("SSLv3");
/*     */       }
/* 339 */       if (doesSupportProtocol(4, SSL.SSL_OP_NO_TLSv1)) {
/* 340 */         protocols.add("TLSv1");
/*     */       }
/* 342 */       if (doesSupportProtocol(8, SSL.SSL_OP_NO_TLSv1_1)) {
/* 343 */         protocols.add("TLSv1.1");
/*     */       }
/* 345 */       if (doesSupportProtocol(16, SSL.SSL_OP_NO_TLSv1_2)) {
/* 346 */         protocols.add("TLSv1.2");
/*     */       }
/*     */ 
/*     */       
/* 350 */       if (tlsv13Supported && doesSupportProtocol(32, SSL.SSL_OP_NO_TLSv1_3)) {
/* 351 */         protocols.add("TLSv1.3");
/* 352 */         TLSV13_SUPPORTED = true;
/*     */       } else {
/* 354 */         TLSV13_SUPPORTED = false;
/*     */       } 
/*     */       
/* 357 */       SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(protocols);
/* 358 */       SUPPORTS_OCSP = doesSupportOcsp();
/*     */       
/* 360 */       if (logger.isDebugEnabled()) {
/* 361 */         logger.debug("Supported protocols (OpenSSL): {} ", SUPPORTED_PROTOCOLS_SET);
/* 362 */         logger.debug("Default cipher suites (OpenSSL): {}", DEFAULT_CIPHERS);
/*     */       } 
/*     */     } else {
/* 365 */       DEFAULT_CIPHERS = Collections.emptyList();
/* 366 */       AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
/* 367 */       AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
/* 368 */       AVAILABLE_CIPHER_SUITES = Collections.emptySet();
/* 369 */       SUPPORTS_KEYMANAGER_FACTORY = false;
/* 370 */       USE_KEYMANAGER_FACTORY = false;
/* 371 */       SUPPORTED_PROTOCOLS_SET = Collections.emptySet();
/* 372 */       SUPPORTS_OCSP = false;
/* 373 */       TLSV13_SUPPORTED = false;
/* 374 */       IS_BORINGSSL = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static X509Certificate selfSignedCertificate() throws CertificateException {
/* 382 */     return (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate(new ByteArrayInputStream("-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----"
/* 383 */           .getBytes(CharsetUtil.US_ASCII)));
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean doesSupportOcsp() {
/* 388 */     boolean supportsOcsp = false;
/* 389 */     if (version() >= 268443648L) {
/* 390 */       long sslCtx = -1L;
/*     */       try {
/* 392 */         sslCtx = SSLContext.make(16, 1);
/* 393 */         SSLContext.enableOcsp(sslCtx, false);
/* 394 */         supportsOcsp = true;
/* 395 */       } catch (Exception exception) {
/*     */       
/*     */       } finally {
/* 398 */         if (sslCtx != -1L) {
/* 399 */           SSLContext.free(sslCtx);
/*     */         }
/*     */       } 
/*     */     } 
/* 403 */     return supportsOcsp;
/*     */   }
/*     */   private static boolean doesSupportProtocol(int protocol, int opt) {
/* 406 */     if (opt == 0)
/*     */     {
/* 408 */       return false;
/*     */     }
/* 410 */     long sslCtx = -1L;
/*     */     try {
/* 412 */       sslCtx = SSLContext.make(protocol, 2);
/* 413 */       return true;
/* 414 */     } catch (Exception ignore) {
/* 415 */       return false;
/*     */     } finally {
/* 417 */       if (sslCtx != -1L) {
/* 418 */         SSLContext.free(sslCtx);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAvailable() {
/* 429 */     return (UNAVAILABILITY_CAUSE == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static boolean isAlpnSupported() {
/* 440 */     return (version() >= 268443648L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOcspSupported() {
/* 447 */     return SUPPORTS_OCSP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int version() {
/* 455 */     return isAvailable() ? SSL.version() : -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String versionString() {
/* 463 */     return isAvailable() ? SSL.versionString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void ensureAvailability() {
/* 473 */     if (UNAVAILABILITY_CAUSE != null) {
/* 474 */       throw (Error)(new UnsatisfiedLinkError("failed to load the required native library"))
/* 475 */         .initCause(UNAVAILABILITY_CAUSE);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable unavailabilityCause() {
/* 486 */     return UNAVAILABILITY_CAUSE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Set<String> availableCipherSuites() {
/* 494 */     return availableOpenSslCipherSuites();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<String> availableOpenSslCipherSuites() {
/* 502 */     return AVAILABLE_OPENSSL_CIPHER_SUITES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<String> availableJavaCipherSuites() {
/* 510 */     return AVAILABLE_JAVA_CIPHER_SUITES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCipherSuiteAvailable(String cipherSuite) {
/* 518 */     String converted = CipherSuiteConverter.toOpenSsl(cipherSuite, IS_BORINGSSL);
/* 519 */     if (converted != null) {
/* 520 */       cipherSuite = converted;
/*     */     }
/* 522 */     return AVAILABLE_OPENSSL_CIPHER_SUITES.contains(cipherSuite);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean supportsKeyManagerFactory() {
/* 529 */     return SUPPORTS_KEYMANAGER_FACTORY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static boolean supportsHostnameValidation() {
/* 540 */     return isAvailable();
/*     */   }
/*     */   
/*     */   static boolean useKeyManagerFactory() {
/* 544 */     return USE_KEYMANAGER_FACTORY;
/*     */   }
/*     */   
/*     */   static long memoryAddress(ByteBuf buf) {
/* 548 */     assert buf.isDirect();
/* 549 */     return buf.hasMemoryAddress() ? buf.memoryAddress() : Buffer.address(buf.nioBuffer());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void loadTcNative() throws Exception {
/* 555 */     String os = PlatformDependent.normalizedOs();
/* 556 */     String arch = PlatformDependent.normalizedArch();
/*     */     
/* 558 */     Set<String> libNames = new LinkedHashSet<String>(5);
/* 559 */     String staticLibName = "netty_tcnative";
/*     */ 
/*     */ 
/*     */     
/* 563 */     if ("linux".equalsIgnoreCase(os)) {
/* 564 */       Set<String> classifiers = PlatformDependent.normalizedLinuxClassifiers();
/* 565 */       for (String classifier : classifiers) {
/* 566 */         libNames.add(staticLibName + "_" + os + '_' + arch + "_" + classifier);
/*     */       }
/*     */       
/* 569 */       libNames.add(staticLibName + "_" + os + '_' + arch);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 574 */       libNames.add(staticLibName + "_" + os + '_' + arch + "_fedora");
/*     */     } else {
/* 576 */       libNames.add(staticLibName + "_" + os + '_' + arch);
/*     */     } 
/* 578 */     libNames.add(staticLibName + "_" + arch);
/* 579 */     libNames.add(staticLibName);
/*     */     
/* 581 */     NativeLibraryLoader.loadFirstAvailable(SSL.class.getClassLoader(), libNames
/* 582 */         .<String>toArray(new String[0]));
/*     */   }
/*     */   
/*     */   private static boolean initializeTcNative(String engine) throws Exception {
/* 586 */     return Library.initialize("provided", engine);
/*     */   }
/*     */   
/*     */   static void releaseIfNeeded(ReferenceCounted counted) {
/* 590 */     if (counted.refCnt() > 0) {
/* 591 */       ReferenceCountUtil.safeRelease(counted);
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean isTlsv13Supported() {
/* 596 */     return TLSV13_SUPPORTED;
/*     */   }
/*     */   
/*     */   static boolean isBoringSSL() {
/* 600 */     return IS_BORINGSSL;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSsl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */