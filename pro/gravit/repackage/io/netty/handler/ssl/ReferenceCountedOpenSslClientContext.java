/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.KeyStore;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509ExtendedTrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.CertificateCallback;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
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
/*     */ 
/*     */ public final class ReferenceCountedOpenSslClientContext
/*     */   extends ReferenceCountedOpenSslContext
/*     */ {
/*  52 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslClientContext.class);
/*  53 */   private static final Set<String> SUPPORTED_KEY_TYPES = Collections.unmodifiableSet(new LinkedHashSet<String>(
/*  54 */         Arrays.asList(new String[] { "RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC" })));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final OpenSslSessionContext sessionContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ReferenceCountedOpenSslClientContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStore) throws SSLException {
/*  67 */     super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 0, (Certificate[])keyCertChain, ClientAuth.NONE, protocols, false, enableOcsp, true);
/*     */     
/*  69 */     boolean success = false;
/*     */     try {
/*  71 */       this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore);
/*     */       
/*  73 */       success = true;
/*     */     } finally {
/*  75 */       if (!success) {
/*  76 */         release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslSessionContext sessionContext() {
/*  83 */     return this.sessionContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static OpenSslSessionContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, String keyStore) throws SSLException {
/*  93 */     if ((key == null && keyCertChain != null) || (key != null && keyCertChain == null)) {
/*  94 */       throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
/*     */     }
/*     */     
/*  97 */     OpenSslKeyMaterialProvider keyMaterialProvider = null;
/*     */     try {
/*     */       try {
/* 100 */         if (!OpenSsl.useKeyManagerFactory()) {
/* 101 */           if (keyManagerFactory != null) {
/* 102 */             throw new IllegalArgumentException("KeyManagerFactory not supported");
/*     */           }
/*     */           
/* 105 */           if (keyCertChain != null) {
/* 106 */             setKeyMaterial(ctx, keyCertChain, key, keyPassword);
/*     */           }
/*     */         } else {
/*     */           
/* 110 */           if (keyManagerFactory == null && keyCertChain != null) {
/* 111 */             char[] keyPasswordChars = keyStorePassword(keyPassword);
/* 112 */             KeyStore ks = buildKeyStore(keyCertChain, key, keyPasswordChars, keyStore);
/* 113 */             if (ks.aliases().hasMoreElements()) {
/* 114 */               keyManagerFactory = new OpenSslX509KeyManagerFactory();
/*     */             } else {
/*     */               
/* 117 */               keyManagerFactory = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
/*     */             } 
/* 119 */             keyManagerFactory.init(ks, keyPasswordChars);
/* 120 */             keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
/* 121 */           } else if (keyManagerFactory != null) {
/* 122 */             keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
/*     */           } 
/*     */           
/* 125 */           if (keyMaterialProvider != null) {
/* 126 */             OpenSslKeyMaterialManager materialManager = new OpenSslKeyMaterialManager(keyMaterialProvider);
/* 127 */             SSLContext.setCertificateCallback(ctx, new OpenSslClientCertificateCallback(engineMap, materialManager));
/*     */           }
/*     */         
/*     */         } 
/* 131 */       } catch (Exception e) {
/* 132 */         throw new SSLException("failed to set certificate and key", e);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 141 */       SSLContext.setVerify(ctx, 1, 10);
/*     */       
/*     */       try {
/* 144 */         if (trustCertCollection != null) {
/* 145 */           trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
/* 146 */         } else if (trustManagerFactory == null) {
/* 147 */           trustManagerFactory = TrustManagerFactory.getInstance(
/* 148 */               TrustManagerFactory.getDefaultAlgorithm());
/* 149 */           trustManagerFactory.init((KeyStore)null);
/*     */         } 
/* 151 */         X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 159 */         setVerifyCallback(ctx, engineMap, manager);
/* 160 */       } catch (Exception e) {
/* 161 */         if (keyMaterialProvider != null) {
/* 162 */           keyMaterialProvider.destroy();
/*     */         }
/* 164 */         throw new SSLException("unable to setup trustmanager", e);
/*     */       } 
/* 166 */       OpenSslClientSessionContext context = new OpenSslClientSessionContext(thiz, keyMaterialProvider);
/* 167 */       keyMaterialProvider = null;
/* 168 */       return context;
/*     */     } finally {
/* 170 */       if (keyMaterialProvider != null) {
/* 171 */         keyMaterialProvider.destroy();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Guarded by java version check")
/*     */   private static void setVerifyCallback(long ctx, OpenSslEngineMap engineMap, X509TrustManager manager) {
/* 179 */     if (useExtendedTrustManager(manager)) {
/* 180 */       SSLContext.setCertVerifyCallback(ctx, new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
/*     */     } else {
/*     */       
/* 183 */       SSLContext.setCertVerifyCallback(ctx, new TrustManagerVerifyCallback(engineMap, manager));
/*     */     } 
/*     */   }
/*     */   
/*     */   static final class OpenSslClientSessionContext
/*     */     extends OpenSslSessionContext {
/*     */     OpenSslClientSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider) {
/* 190 */       super(context, provider);
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSessionTimeout(int seconds) {
/* 195 */       if (seconds < 0) {
/* 196 */         throw new IllegalArgumentException();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public int getSessionTimeout() {
/* 202 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSessionCacheSize(int size) {
/* 207 */       if (size < 0) {
/* 208 */         throw new IllegalArgumentException();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public int getSessionCacheSize() {
/* 214 */       return 0;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void setSessionCacheEnabled(boolean enabled) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isSessionCacheEnabled() {
/* 224 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
/*     */     private final X509TrustManager manager;
/*     */     
/*     */     TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) {
/* 232 */       super(engineMap);
/* 233 */       this.manager = manager;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
/* 239 */       this.manager.checkServerTrusted(peerCerts, auth);
/*     */     }
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
/*     */     private final X509ExtendedTrustManager manager;
/*     */     
/*     */     ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
/* 248 */       super(engineMap);
/* 249 */       this.manager = OpenSslTlsv13X509ExtendedTrustManager.wrap(manager);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
/* 255 */       this.manager.checkServerTrusted(peerCerts, auth, engine);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class OpenSslClientCertificateCallback implements CertificateCallback {
/*     */     private final OpenSslEngineMap engineMap;
/*     */     private final OpenSslKeyMaterialManager keyManagerHolder;
/*     */     
/*     */     OpenSslClientCertificateCallback(OpenSslEngineMap engineMap, OpenSslKeyMaterialManager keyManagerHolder) {
/* 264 */       this.engineMap = engineMap;
/* 265 */       this.keyManagerHolder = keyManagerHolder;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handle(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws Exception {
/* 270 */       ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
/*     */       
/* 272 */       if (engine == null)
/*     */         return; 
/*     */       try {
/*     */         X500Principal[] issuers;
/* 276 */         Set<String> keyTypesSet = supportedClientKeyTypes(keyTypeBytes);
/* 277 */         String[] keyTypes = keyTypesSet.<String>toArray(new String[0]);
/*     */         
/* 279 */         if (asn1DerEncodedPrincipals == null) {
/* 280 */           issuers = null;
/*     */         } else {
/* 282 */           issuers = new X500Principal[asn1DerEncodedPrincipals.length];
/* 283 */           for (int i = 0; i < asn1DerEncodedPrincipals.length; i++) {
/* 284 */             issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
/*     */           }
/*     */         } 
/* 287 */         this.keyManagerHolder.setKeyMaterialClientSide(engine, keyTypes, issuers);
/* 288 */       } catch (Throwable cause) {
/* 289 */         ReferenceCountedOpenSslClientContext.logger.debug("request of key failed", cause);
/* 290 */         engine.initHandshakeException(cause);
/*     */       } 
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
/*     */     private static Set<String> supportedClientKeyTypes(byte[] clientCertificateTypes) {
/* 303 */       if (clientCertificateTypes == null)
/*     */       {
/* 305 */         return ReferenceCountedOpenSslClientContext.SUPPORTED_KEY_TYPES;
/*     */       }
/* 307 */       Set<String> result = new HashSet<String>(clientCertificateTypes.length);
/* 308 */       for (byte keyTypeCode : clientCertificateTypes) {
/* 309 */         String keyType = clientKeyType(keyTypeCode);
/* 310 */         if (keyType != null)
/*     */         {
/*     */ 
/*     */           
/* 314 */           result.add(keyType); } 
/*     */       } 
/* 316 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     private static String clientKeyType(byte clientCertificateType) {
/* 321 */       switch (clientCertificateType) {
/*     */         case 1:
/* 323 */           return "RSA";
/*     */         case 3:
/* 325 */           return "DH_RSA";
/*     */         case 64:
/* 327 */           return "EC";
/*     */         case 65:
/* 329 */           return "EC_RSA";
/*     */         case 66:
/* 331 */           return "EC_EC";
/*     */       } 
/* 333 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ReferenceCountedOpenSslClientContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */