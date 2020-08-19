/*      */ package pro.gravit.repackage.io.netty.handler.ssl;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.KeyException;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.UnrecoverableKeyException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.security.spec.InvalidKeySpecException;
/*      */ import java.security.spec.PKCS8EncodedKeySpec;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.Executor;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.EncryptedPrivateKeyInfo;
/*      */ import javax.crypto.NoSuchPaddingException;
/*      */ import javax.crypto.SecretKey;
/*      */ import javax.crypto.SecretKeyFactory;
/*      */ import javax.crypto.spec.PBEKeySpec;
/*      */ import javax.net.ssl.KeyManagerFactory;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSessionContext;
/*      */ import javax.net.ssl.TrustManagerFactory;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufInputStream;
/*      */ import pro.gravit.repackage.io.netty.util.AttributeMap;
/*      */ import pro.gravit.repackage.io.netty.util.DefaultAttributeMap;
/*      */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class SslContext
/*      */ {
/*      */   static final String ALIAS = "key";
/*      */   static final CertificateFactory X509_CERT_FACTORY;
/*      */   private final boolean startTls;
/*      */   
/*      */   static {
/*      */     try {
/*   95 */       X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
/*   96 */     } catch (CertificateException e) {
/*   97 */       throw new IllegalStateException("unable to instance X.509 CertificateFactory", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*  102 */   private final AttributeMap attributes = (AttributeMap)new DefaultAttributeMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SslProvider defaultServerProvider() {
/*  110 */     return defaultProvider();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SslProvider defaultClientProvider() {
/*  119 */     return defaultProvider();
/*      */   }
/*      */   
/*      */   private static SslProvider defaultProvider() {
/*  123 */     if (OpenSsl.isAvailable()) {
/*  124 */       return SslProvider.OPENSSL;
/*      */     }
/*  126 */     return SslProvider.JDK;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
/*  140 */     return newServerContext(certChainFile, keyFile, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
/*  156 */     return newServerContext(null, certChainFile, keyFile, keyPassword);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  183 */     return newServerContext((SslProvider)null, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  211 */     return newServerContext((SslProvider)null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
/*  229 */     return newServerContext(provider, certChainFile, keyFile, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
/*  247 */     return newServerContext(provider, certChainFile, keyFile, keyPassword, (Iterable<String>)null, IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)null, 0L, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  277 */     return newServerContext(provider, certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, 
/*      */         
/*  279 */         toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  312 */     return newServerContext(provider, null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, 
/*      */ 
/*      */         
/*  315 */         toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  344 */     return newServerContext(provider, null, null, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 
/*  345 */         KeyStore.getDefaultType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  388 */     return newServerContext(provider, trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 
/*      */         
/*  390 */         KeyStore.getDefaultType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
/*      */     try {
/*  433 */       return newServerContextInternal(provider, null, toX509Certificates(trustCertCollectionFile), trustManagerFactory, 
/*  434 */           toX509Certificates(keyCertChainFile), 
/*  435 */           toPrivateKey(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, ClientAuth.NONE, null, false, false, keyStore);
/*      */ 
/*      */     
/*      */     }
/*  439 */     catch (Exception e) {
/*  440 */       if (e instanceof SSLException) {
/*  441 */         throw (SSLException)e;
/*      */       }
/*  443 */       throw new SSLException("failed to initialize the server-side SSL context", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SslContext newServerContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStoreType) throws SSLException {
/*  456 */     if (provider == null) {
/*  457 */       provider = defaultServerProvider();
/*      */     }
/*      */     
/*  460 */     switch (provider) {
/*      */       case JDK:
/*  462 */         if (enableOcsp) {
/*  463 */           throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
/*      */         }
/*  465 */         return new JdkSslServerContext(sslContextProvider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, keyStoreType);
/*      */ 
/*      */ 
/*      */       
/*      */       case OPENSSL:
/*  470 */         verifyNullSslContextProvider(provider, sslContextProvider);
/*  471 */         return new OpenSslServerContext(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStoreType);
/*      */ 
/*      */ 
/*      */       
/*      */       case OPENSSL_REFCNT:
/*  476 */         verifyNullSslContextProvider(provider, sslContextProvider);
/*  477 */         return new ReferenceCountedOpenSslServerContext(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStoreType);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  482 */     throw new Error(provider.toString());
/*      */   }
/*      */ 
/*      */   
/*      */   private static void verifyNullSslContextProvider(SslProvider provider, Provider sslContextProvider) {
/*  487 */     if (sslContextProvider != null) {
/*  488 */       throw new IllegalArgumentException("Java Security Provider unsupported for SslProvider: " + provider);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext() throws SSLException {
/*  500 */     return newClientContext(null, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(File certChainFile) throws SSLException {
/*  513 */     return newClientContext((SslProvider)null, certChainFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
/*  528 */     return newClientContext(null, null, trustManagerFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
/*  546 */     return newClientContext(null, certChainFile, trustManagerFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  574 */     return newClientContext((SslProvider)null, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  604 */     return newClientContext(null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider) throws SSLException {
/*  620 */     return newClientContext(provider, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
/*  636 */     return newClientContext(provider, certChainFile, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
/*  654 */     return newClientContext(provider, null, trustManagerFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
/*  674 */     return newClientContext(provider, certChainFile, trustManagerFactory, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  706 */     return newClientContext(provider, certChainFile, trustManagerFactory, null, null, null, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, 
/*      */ 
/*      */         
/*  709 */         toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*  741 */     return newClientContext(provider, certChainFile, trustManagerFactory, null, null, null, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static SslContext newClientContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
/*      */     try {
/*  792 */       return newClientContextInternal(provider, null, 
/*  793 */           toX509Certificates(trustCertCollectionFile), trustManagerFactory, 
/*  794 */           toX509Certificates(keyCertChainFile), toPrivateKey(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, null, sessionCacheSize, sessionTimeout, false, 
/*      */ 
/*      */           
/*  797 */           KeyStore.getDefaultType());
/*  798 */     } catch (Exception e) {
/*  799 */       if (e instanceof SSLException) {
/*  800 */         throw (SSLException)e;
/*      */       }
/*  802 */       throw new SSLException("failed to initialize the client-side SSL context", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SslContext newClientContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCert, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStoreType) throws SSLException {
/*  813 */     if (provider == null) {
/*  814 */       provider = defaultClientProvider();
/*      */     }
/*  816 */     switch (provider) {
/*      */       case JDK:
/*  818 */         if (enableOcsp) {
/*  819 */           throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
/*      */         }
/*  821 */         return new JdkSslClientContext(sslContextProvider, trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, keyStoreType);
/*      */ 
/*      */ 
/*      */       
/*      */       case OPENSSL:
/*  826 */         verifyNullSslContextProvider(provider, sslContextProvider);
/*  827 */         return new OpenSslClientContext(trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, enableOcsp, keyStoreType);
/*      */ 
/*      */ 
/*      */       
/*      */       case OPENSSL_REFCNT:
/*  832 */         verifyNullSslContextProvider(provider, sslContextProvider);
/*  833 */         return new ReferenceCountedOpenSslClientContext(trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, enableOcsp, keyStoreType);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  838 */     throw new Error(provider.toString());
/*      */   }
/*      */ 
/*      */   
/*      */   static ApplicationProtocolConfig toApplicationProtocolConfig(Iterable<String> nextProtocols) {
/*      */     ApplicationProtocolConfig apn;
/*  844 */     if (nextProtocols == null) {
/*  845 */       apn = ApplicationProtocolConfig.DISABLED;
/*      */     } else {
/*  847 */       apn = new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.NPN_AND_ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL, ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, nextProtocols);
/*      */     } 
/*      */ 
/*      */     
/*  851 */     return apn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SslContext() {
/*  858 */     this(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SslContext(boolean startTls) {
/*  865 */     this.startTls = startTls;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final AttributeMap attributes() {
/*  872 */     return this.attributes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isServer() {
/*  879 */     return !isClient();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final List<String> nextProtocols() {
/*  907 */     return applicationProtocolNegotiator().protocols();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SslHandler newHandler(ByteBufAllocator alloc) {
/*  945 */     return newHandler(alloc, this.startTls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
/*  953 */     return new SslHandler(newEngine(alloc), startTls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler newHandler(ByteBufAllocator alloc, Executor delegatedTaskExecutor) {
/*  982 */     return newHandler(alloc, this.startTls, delegatedTaskExecutor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
/*  990 */     return new SslHandler(newEngine(alloc), startTls, executor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort) {
/*  999 */     return newHandler(alloc, peerHost, peerPort, this.startTls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
/* 1007 */     return new SslHandler(newEngine(alloc, peerHost, peerPort), startTls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, Executor delegatedTaskExecutor) {
/* 1040 */     return newHandler(alloc, peerHost, peerPort, this.startTls, delegatedTaskExecutor);
/*      */   }
/*      */ 
/*      */   
/*      */   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor delegatedTaskExecutor) {
/* 1045 */     return new SslHandler(newEngine(alloc, peerHost, peerPort), startTls, delegatedTaskExecutor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static PKCS8EncodedKeySpec generateKeySpec(char[] password, byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
/* 1068 */     if (password == null) {
/* 1069 */       return new PKCS8EncodedKeySpec(key);
/*      */     }
/*      */     
/* 1072 */     EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(key);
/* 1073 */     SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
/* 1074 */     PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
/* 1075 */     SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
/*      */     
/* 1077 */     Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
/* 1078 */     cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
/*      */     
/* 1080 */     return encryptedPrivateKeyInfo.getKeySpec(cipher);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyStore buildKeyStore(X509Certificate[] certChain, PrivateKey key, char[] keyPasswordChars, String keyStoreType) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
/* 1097 */     if (keyStoreType == null) {
/* 1098 */       keyStoreType = KeyStore.getDefaultType();
/*      */     }
/* 1100 */     KeyStore ks = KeyStore.getInstance(keyStoreType);
/* 1101 */     ks.load(null, null);
/* 1102 */     ks.setKeyEntry("key", key, keyPasswordChars, (Certificate[])certChain);
/* 1103 */     return ks;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static PrivateKey toPrivateKey(File keyFile, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
/* 1110 */     if (keyFile == null) {
/* 1111 */       return null;
/*      */     }
/* 1113 */     return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyFile), keyPassword);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static PrivateKey toPrivateKey(InputStream keyInputStream, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
/* 1120 */     if (keyInputStream == null) {
/* 1121 */       return null;
/*      */     }
/* 1123 */     return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyInputStream), keyPassword);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static PrivateKey getPrivateKeyFromByteBuffer(ByteBuf encodedKeyBuf, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
/* 1130 */     byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
/* 1131 */     encodedKeyBuf.readBytes(encodedKey).release();
/*      */     
/* 1133 */     PKCS8EncodedKeySpec encodedKeySpec = generateKeySpec((keyPassword == null) ? null : keyPassword
/* 1134 */         .toCharArray(), encodedKey);
/*      */     try {
/* 1136 */       return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
/* 1137 */     } catch (InvalidKeySpecException ignore) {
/*      */       try {
/* 1139 */         return KeyFactory.getInstance("DSA").generatePrivate(encodedKeySpec);
/* 1140 */       } catch (InvalidKeySpecException ignore2) {
/*      */         try {
/* 1142 */           return KeyFactory.getInstance("EC").generatePrivate(encodedKeySpec);
/* 1143 */         } catch (InvalidKeySpecException e) {
/* 1144 */           throw new InvalidKeySpecException("Neither RSA, DSA nor EC worked", e);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
/* 1160 */     return buildTrustManagerFactory(certChainFile, trustManagerFactory, KeyStore.getDefaultType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory, String keyType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
/* 1173 */     X509Certificate[] x509Certs = toX509Certificates(certChainFile);
/*      */     
/* 1175 */     return buildTrustManagerFactory(x509Certs, trustManagerFactory, keyType);
/*      */   }
/*      */   
/*      */   static X509Certificate[] toX509Certificates(File file) throws CertificateException {
/* 1179 */     if (file == null) {
/* 1180 */       return null;
/*      */     }
/* 1182 */     return getCertificatesFromBuffers(PemReader.readCertificates(file));
/*      */   }
/*      */   
/*      */   static X509Certificate[] toX509Certificates(InputStream in) throws CertificateException {
/* 1186 */     if (in == null) {
/* 1187 */       return null;
/*      */     }
/* 1189 */     return getCertificatesFromBuffers(PemReader.readCertificates(in));
/*      */   }
/*      */   
/*      */   private static X509Certificate[] getCertificatesFromBuffers(ByteBuf[] certs) throws CertificateException {
/* 1193 */     CertificateFactory cf = CertificateFactory.getInstance("X.509");
/* 1194 */     X509Certificate[] x509Certs = new X509Certificate[certs.length];
/*      */     
/*      */     try {
/* 1197 */       for (int i = 0; i < certs.length; i++) {
/* 1198 */         ByteBufInputStream byteBufInputStream = new ByteBufInputStream(certs[i], false);
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */ 
/*      */       
/* 1211 */       for (ByteBuf buf : certs) {
/* 1212 */         buf.release();
/*      */       }
/*      */     } 
/* 1215 */     return x509Certs;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static TrustManagerFactory buildTrustManagerFactory(X509Certificate[] certCollection, TrustManagerFactory trustManagerFactory, String keyStoreType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
/* 1221 */     if (keyStoreType == null) {
/* 1222 */       keyStoreType = KeyStore.getDefaultType();
/*      */     }
/* 1224 */     KeyStore ks = KeyStore.getInstance(keyStoreType);
/* 1225 */     ks.load(null, null);
/*      */     
/* 1227 */     int i = 1;
/* 1228 */     for (X509Certificate cert : certCollection) {
/* 1229 */       String alias = Integer.toString(i);
/* 1230 */       ks.setCertificateEntry(alias, cert);
/* 1231 */       i++;
/*      */     } 
/*      */ 
/*      */     
/* 1235 */     if (trustManagerFactory == null) {
/* 1236 */       trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*      */     }
/* 1238 */     trustManagerFactory.init(ks);
/*      */     
/* 1240 */     return trustManagerFactory;
/*      */   }
/*      */   
/*      */   static PrivateKey toPrivateKeyInternal(File keyFile, String keyPassword) throws SSLException {
/*      */     try {
/* 1245 */       return toPrivateKey(keyFile, keyPassword);
/* 1246 */     } catch (Exception e) {
/* 1247 */       throw new SSLException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   static X509Certificate[] toX509CertificatesInternal(File file) throws SSLException {
/*      */     try {
/* 1253 */       return toX509Certificates(file);
/* 1254 */     } catch (CertificateException e) {
/* 1255 */       throw new SSLException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChain, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStoreType) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
/* 1263 */     return buildKeyManagerFactory(certChain, KeyManagerFactory.getDefaultAlgorithm(), key, keyPassword, kmf, keyStoreType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
/* 1273 */     char[] keyPasswordChars = keyStorePassword(keyPassword);
/* 1274 */     KeyStore ks = buildKeyStore(certChainFile, key, keyPasswordChars, keyStore);
/* 1275 */     return buildKeyManagerFactory(ks, keyAlgorithm, keyPasswordChars, kmf);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
/* 1283 */     char[] keyPasswordChars = keyStorePassword(keyPassword);
/* 1284 */     KeyStore ks = buildKeyStore(certChainFile, key, keyPasswordChars, KeyStore.getDefaultType());
/* 1285 */     return buildKeyManagerFactory(ks, keyAlgorithm, keyPasswordChars, kmf);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyManagerFactory buildKeyManagerFactory(KeyStore ks, String keyAlgorithm, char[] keyPasswordChars, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 1293 */     if (kmf == null) {
/* 1294 */       kmf = KeyManagerFactory.getInstance(keyAlgorithm);
/*      */     }
/* 1296 */     kmf.init(ks, keyPasswordChars);
/*      */     
/* 1298 */     return kmf;
/*      */   }
/*      */   
/*      */   static char[] keyStorePassword(String keyPassword) {
/* 1302 */     return (keyPassword == null) ? EmptyArrays.EMPTY_CHARS : keyPassword.toCharArray();
/*      */   }
/*      */   
/*      */   public abstract boolean isClient();
/*      */   
/*      */   public abstract List<String> cipherSuites();
/*      */   
/*      */   public abstract long sessionCacheSize();
/*      */   
/*      */   public abstract long sessionTimeout();
/*      */   
/*      */   public abstract ApplicationProtocolNegotiator applicationProtocolNegotiator();
/*      */   
/*      */   public abstract SSLEngine newEngine(ByteBufAllocator paramByteBufAllocator);
/*      */   
/*      */   public abstract SSLEngine newEngine(ByteBufAllocator paramByteBufAllocator, String paramString, int paramInt);
/*      */   
/*      */   public abstract SSLSessionContext sessionContext();
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */