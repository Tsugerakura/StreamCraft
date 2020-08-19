/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.security.KeyStore;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class JdkSslClientContext
/*     */   extends JdkSslContext
/*     */ {
/*     */   @Deprecated
/*     */   public JdkSslClientContext() throws SSLException {
/*  48 */     this((File)null, (TrustManagerFactory)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JdkSslClientContext(File certChainFile) throws SSLException {
/*  60 */     this(certChainFile, (TrustManagerFactory)null);
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
/*     */   @Deprecated
/*     */   public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
/*  73 */     this((File)null, trustManagerFactory);
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
/*     */   @Deprecated
/*     */   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
/*  88 */     this(certChainFile, trustManagerFactory, (Iterable<String>)null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
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
/*     */   @Deprecated
/*     */   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 115 */     this(certChainFile, trustManagerFactory, ciphers, IdentityCipherSuiteFilter.INSTANCE, 
/* 116 */         toNegotiator(toApplicationProtocolConfig(nextProtocols), false), sessionCacheSize, sessionTimeout);
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
/*     */   @Deprecated
/*     */   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 142 */     this(certChainFile, trustManagerFactory, ciphers, cipherFilter, 
/* 143 */         toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
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
/*     */   @Deprecated
/*     */   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 169 */     this((Provider)null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JdkSslClientContext(Provider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 176 */     super(newSSLContext(provider, toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, (X509Certificate[])null, (PrivateKey)null, (String)null, (KeyManagerFactory)null, sessionCacheSize, sessionTimeout, 
/*     */           
/* 178 */           KeyStore.getDefaultType()), true, ciphers, cipherFilter, apn, ClientAuth.NONE, (String[])null, false);
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
/*     */ 
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
/*     */   public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 218 */     this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, 
/* 219 */         toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
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
/*     */   public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/* 258 */     super(newSSLContext((Provider)null, toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, 
/*     */           
/* 260 */           toX509CertificatesInternal(keyCertChainFile), toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, 
/* 261 */           KeyStore.getDefaultType()), true, ciphers, cipherFilter, apn, ClientAuth.NONE, (String[])null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JdkSslClientContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, String keyStoreType) throws SSLException {
/* 272 */     super(newSSLContext(sslContextProvider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, keyStoreType), true, ciphers, cipherFilter, 
/*     */ 
/*     */         
/* 275 */         toNegotiator(apn, false), ClientAuth.NONE, protocols, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static SSLContext newSSLContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
/*     */     try {
/* 285 */       if (trustCertCollection != null) {
/* 286 */         trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
/*     */       }
/* 288 */       if (keyCertChain != null) {
/* 289 */         keyManagerFactory = buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory, (String)null);
/*     */       }
/*     */       
/* 292 */       SSLContext ctx = (sslContextProvider == null) ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
/* 293 */       ctx.init((keyManagerFactory == null) ? null : keyManagerFactory.getKeyManagers(), (trustManagerFactory == null) ? null : trustManagerFactory
/* 294 */           .getTrustManagers(), null);
/*     */ 
/*     */       
/* 297 */       SSLSessionContext sessCtx = ctx.getClientSessionContext();
/* 298 */       if (sessionCacheSize > 0L) {
/* 299 */         sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
/*     */       }
/* 301 */       if (sessionTimeout > 0L) {
/* 302 */         sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
/*     */       }
/* 304 */       return ctx;
/* 305 */     } catch (Exception e) {
/* 306 */       if (e instanceof SSLException) {
/* 307 */         throw (SSLException)e;
/*     */       }
/* 309 */       throw new SSLException("failed to initialize the client-side SSL context", e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkSslClientContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */