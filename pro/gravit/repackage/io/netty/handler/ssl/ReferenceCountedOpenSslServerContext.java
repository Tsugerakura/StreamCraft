/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.KeyStore;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509ExtendedTrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.CertificateCallback;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SniHostNameMatcher;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ public final class ReferenceCountedOpenSslServerContext
/*     */   extends ReferenceCountedOpenSslContext
/*     */ {
/*  50 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
/*  51 */   private static final byte[] ID = new byte[] { 110, 101, 116, 116, 121 };
/*     */ 
/*     */ 
/*     */   
/*     */   private final OpenSslServerSessionContext sessionContext;
/*     */ 
/*     */ 
/*     */   
/*     */   ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
/*  60 */     this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, 
/*  61 */         toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStore);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
/*  71 */     super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 1, (Certificate[])keyCertChain, clientAuth, protocols, startTls, enableOcsp, true);
/*     */ 
/*     */     
/*  74 */     boolean success = false;
/*     */     try {
/*  76 */       this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore);
/*     */       
/*  78 */       success = true;
/*     */     } finally {
/*  80 */       if (!success) {
/*  81 */         release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslServerSessionContext sessionContext() {
/*  88 */     return this.sessionContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static OpenSslServerSessionContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, String keyStore) throws SSLException {
/*  99 */     OpenSslKeyMaterialProvider keyMaterialProvider = null;
/*     */     try {
/*     */       try {
/* 102 */         SSLContext.setVerify(ctx, 0, 10);
/* 103 */         if (!OpenSsl.useKeyManagerFactory()) {
/* 104 */           if (keyManagerFactory != null) {
/* 105 */             throw new IllegalArgumentException("KeyManagerFactory not supported");
/*     */           }
/*     */           
/* 108 */           ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
/*     */           
/* 110 */           setKeyMaterial(ctx, keyCertChain, key, keyPassword);
/*     */         }
/*     */         else {
/*     */           
/* 114 */           if (keyManagerFactory == null) {
/* 115 */             char[] keyPasswordChars = keyStorePassword(keyPassword);
/* 116 */             KeyStore ks = buildKeyStore(keyCertChain, key, keyPasswordChars, keyStore);
/* 117 */             if (ks.aliases().hasMoreElements()) {
/* 118 */               keyManagerFactory = new OpenSslX509KeyManagerFactory();
/*     */             } else {
/*     */               
/* 121 */               keyManagerFactory = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
/*     */             } 
/* 123 */             keyManagerFactory.init(ks, keyPasswordChars);
/*     */           } 
/* 125 */           keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
/*     */           
/* 127 */           SSLContext.setCertificateCallback(ctx, new OpenSslServerCertificateCallback(engineMap, new OpenSslKeyMaterialManager(keyMaterialProvider)));
/*     */         }
/*     */       
/* 130 */       } catch (Exception e) {
/* 131 */         throw new SSLException("failed to set certificate and key", e);
/*     */       } 
/*     */       try {
/* 134 */         if (trustCertCollection != null) {
/* 135 */           trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
/* 136 */         } else if (trustManagerFactory == null) {
/*     */           
/* 138 */           trustManagerFactory = TrustManagerFactory.getInstance(
/* 139 */               TrustManagerFactory.getDefaultAlgorithm());
/* 140 */           trustManagerFactory.init((KeyStore)null);
/*     */         } 
/*     */         
/* 143 */         X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 151 */         setVerifyCallback(ctx, engineMap, manager);
/*     */         
/* 153 */         X509Certificate[] issuers = manager.getAcceptedIssuers();
/* 154 */         if (issuers != null && issuers.length > 0) {
/* 155 */           long bio = 0L;
/*     */           try {
/* 157 */             bio = toBIO(ByteBufAllocator.DEFAULT, issuers);
/* 158 */             if (!SSLContext.setCACertificateBio(ctx, bio)) {
/* 159 */               throw new SSLException("unable to setup accepted issuers for trustmanager " + manager);
/*     */             }
/*     */           } finally {
/* 162 */             freeBio(bio);
/*     */           } 
/*     */         } 
/*     */         
/* 166 */         if (PlatformDependent.javaVersion() >= 8)
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 171 */           SSLContext.setSniHostnameMatcher(ctx, new OpenSslSniHostnameMatcher(engineMap));
/*     */         }
/* 173 */       } catch (SSLException e) {
/* 174 */         throw e;
/* 175 */       } catch (Exception e) {
/* 176 */         throw new SSLException("unable to setup trustmanager", e);
/*     */       } 
/*     */       
/* 179 */       OpenSslServerSessionContext sessionContext = new OpenSslServerSessionContext(thiz, keyMaterialProvider);
/* 180 */       sessionContext.setSessionIdContext(ID);
/*     */       
/* 182 */       keyMaterialProvider = null;
/*     */       
/* 184 */       return sessionContext;
/*     */     } finally {
/* 186 */       if (keyMaterialProvider != null) {
/* 187 */         keyMaterialProvider.destroy();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Guarded by java version check")
/*     */   private static void setVerifyCallback(long ctx, OpenSslEngineMap engineMap, X509TrustManager manager) {
/* 195 */     if (useExtendedTrustManager(manager)) {
/* 196 */       SSLContext.setCertVerifyCallback(ctx, new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
/*     */     } else {
/*     */       
/* 199 */       SSLContext.setCertVerifyCallback(ctx, new TrustManagerVerifyCallback(engineMap, manager));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class OpenSslServerCertificateCallback implements CertificateCallback {
/*     */     private final OpenSslEngineMap engineMap;
/*     */     private final OpenSslKeyMaterialManager keyManagerHolder;
/*     */     
/*     */     OpenSslServerCertificateCallback(OpenSslEngineMap engineMap, OpenSslKeyMaterialManager keyManagerHolder) {
/* 208 */       this.engineMap = engineMap;
/* 209 */       this.keyManagerHolder = keyManagerHolder;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handle(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws Exception {
/* 214 */       ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
/* 215 */       if (engine == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 222 */         this.keyManagerHolder.setKeyMaterialServerSide(engine);
/* 223 */       } catch (Throwable cause) {
/* 224 */         ReferenceCountedOpenSslServerContext.logger.debug("Failed to set the server-side key material", cause);
/* 225 */         engine.initHandshakeException(cause);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
/*     */     private final X509TrustManager manager;
/*     */     
/*     */     TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) {
/* 234 */       super(engineMap);
/* 235 */       this.manager = manager;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
/* 241 */       this.manager.checkClientTrusted(peerCerts, auth);
/*     */     }
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
/*     */     private final X509ExtendedTrustManager manager;
/*     */     
/*     */     ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
/* 250 */       super(engineMap);
/* 251 */       this.manager = OpenSslTlsv13X509ExtendedTrustManager.wrap(manager);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
/* 257 */       this.manager.checkClientTrusted(peerCerts, auth, engine);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class OpenSslSniHostnameMatcher implements SniHostNameMatcher {
/*     */     private final OpenSslEngineMap engineMap;
/*     */     
/*     */     OpenSslSniHostnameMatcher(OpenSslEngineMap engineMap) {
/* 265 */       this.engineMap = engineMap;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean match(long ssl, String hostname) {
/* 270 */       ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
/* 271 */       if (engine != null)
/*     */       {
/* 273 */         return engine.checkSniHostnameMatch(hostname.getBytes(CharsetUtil.UTF_8));
/*     */       }
/* 275 */       ReferenceCountedOpenSslServerContext.logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", Long.valueOf(ssl));
/* 276 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ReferenceCountedOpenSslServerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */