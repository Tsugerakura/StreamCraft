/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.crypto.NoSuchPaddingException;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class JdkSslContext
/*     */   extends SslContext
/*     */ {
/*     */   static {
/*     */     SSLContext context;
/*     */   }
/*     */   
/*  60 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
/*     */   
/*     */   static final String PROTOCOL = "TLS";
/*     */   
/*     */   private static final String[] DEFAULT_PROTOCOLS;
/*     */   private static final List<String> DEFAULT_CIPHERS;
/*     */   private static final List<String> DEFAULT_CIPHERS_NON_TLSV13;
/*     */   private static final Set<String> SUPPORTED_CIPHERS;
/*     */   private static final Set<String> SUPPORTED_CIPHERS_NON_TLSV13;
/*     */   private static final Provider DEFAULT_PROVIDER;
/*     */   
/*     */   static {
/*     */     try {
/*  73 */       context = SSLContext.getInstance("TLS");
/*  74 */       context.init(null, null, null);
/*  75 */     } catch (Exception e) {
/*  76 */       throw new Error("failed to initialize the default SSL context", e);
/*     */     } 
/*     */     
/*  79 */     DEFAULT_PROVIDER = context.getProvider();
/*     */     
/*  81 */     SSLEngine engine = context.createSSLEngine();
/*  82 */     DEFAULT_PROTOCOLS = defaultProtocols(context, engine);
/*     */     
/*  84 */     SUPPORTED_CIPHERS = Collections.unmodifiableSet(supportedCiphers(engine));
/*  85 */     DEFAULT_CIPHERS = Collections.unmodifiableList(defaultCiphers(engine, SUPPORTED_CIPHERS));
/*     */     
/*  87 */     List<String> ciphersNonTLSv13 = new ArrayList<String>(DEFAULT_CIPHERS);
/*  88 */     ciphersNonTLSv13.removeAll(Arrays.asList((Object[])SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
/*  89 */     DEFAULT_CIPHERS_NON_TLSV13 = Collections.unmodifiableList(ciphersNonTLSv13);
/*     */     
/*  91 */     Set<String> suppertedCiphersNonTLSv13 = new LinkedHashSet<String>(SUPPORTED_CIPHERS);
/*  92 */     suppertedCiphersNonTLSv13.removeAll(Arrays.asList((Object[])SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
/*  93 */     SUPPORTED_CIPHERS_NON_TLSV13 = Collections.unmodifiableSet(suppertedCiphersNonTLSv13);
/*     */     
/*  95 */     if (logger.isDebugEnabled()) {
/*  96 */       logger.debug("Default protocols (JDK): {} ", Arrays.asList(DEFAULT_PROTOCOLS));
/*  97 */       logger.debug("Default cipher suites (JDK): {}", DEFAULT_CIPHERS);
/*     */     } 
/*     */   }
/*     */   private final String[] protocols; private final String[] cipherSuites; private final List<String> unmodifiableCipherSuites; private final JdkApplicationProtocolNegotiator apn; private final ClientAuth clientAuth; private final SSLContext sslContext; private final boolean isClient;
/*     */   
/*     */   private static String[] defaultProtocols(SSLContext context, SSLEngine engine) {
/* 103 */     String[] supportedProtocols = context.getDefaultSSLParameters().getProtocols();
/* 104 */     Set<String> supportedProtocolsSet = new HashSet<String>(supportedProtocols.length);
/* 105 */     Collections.addAll(supportedProtocolsSet, supportedProtocols);
/* 106 */     List<String> protocols = new ArrayList<String>();
/* 107 */     SslUtils.addIfSupported(supportedProtocolsSet, protocols, new String[] { "TLSv1.2", "TLSv1.1", "TLSv1" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     if (!protocols.isEmpty()) {
/* 113 */       return protocols.<String>toArray(new String[0]);
/*     */     }
/* 115 */     return engine.getEnabledProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   private static Set<String> supportedCiphers(SSLEngine engine) {
/* 120 */     String[] supportedCiphers = engine.getSupportedCipherSuites();
/* 121 */     Set<String> supportedCiphersSet = new LinkedHashSet<String>(supportedCiphers.length);
/* 122 */     for (int i = 0; i < supportedCiphers.length; i++) {
/* 123 */       String supportedCipher = supportedCiphers[i];
/* 124 */       supportedCiphersSet.add(supportedCipher);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 134 */       if (supportedCipher.startsWith("SSL_")) {
/* 135 */         String tlsPrefixedCipherName = "TLS_" + supportedCipher.substring("SSL_".length());
/*     */         try {
/* 137 */           engine.setEnabledCipherSuites(new String[] { tlsPrefixedCipherName });
/* 138 */           supportedCiphersSet.add(tlsPrefixedCipherName);
/* 139 */         } catch (IllegalArgumentException illegalArgumentException) {}
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 144 */     return supportedCiphersSet;
/*     */   }
/*     */   
/*     */   private static List<String> defaultCiphers(SSLEngine engine, Set<String> supportedCiphers) {
/* 148 */     List<String> ciphers = new ArrayList<String>();
/* 149 */     SslUtils.addIfSupported(supportedCiphers, ciphers, SslUtils.DEFAULT_CIPHER_SUITES);
/* 150 */     SslUtils.useFallbackCiphersIfDefaultIsEmpty(ciphers, engine.getEnabledCipherSuites());
/* 151 */     return ciphers;
/*     */   }
/*     */   
/*     */   private static boolean isTlsV13Supported(String[] protocols) {
/* 155 */     for (String protocol : protocols) {
/* 156 */       if ("TLSv1.3".equals(protocol)) {
/* 157 */         return true;
/*     */       }
/*     */     } 
/* 160 */     return false;
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
/*     */   @Deprecated
/*     */   public JdkSslContext(SSLContext sslContext, boolean isClient, ClientAuth clientAuth) {
/* 184 */     this(sslContext, isClient, (Iterable<String>)null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, clientAuth, (String[])null, false);
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
/*     */   @Deprecated
/*     */   public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth) {
/* 204 */     this(sslContext, isClient, ciphers, cipherFilter, apn, clientAuth, (String[])null, false);
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
/*     */   public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
/* 227 */     this(sslContext, isClient, ciphers, cipherFilter, 
/*     */ 
/*     */ 
/*     */         
/* 231 */         toNegotiator(apn, !isClient), clientAuth, (protocols == null) ? null : (String[])protocols
/*     */         
/* 233 */         .clone(), startTls);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
/* 240 */     super(startTls); List<String> defaultCiphers; Set<String> supportedCiphers;
/* 241 */     this.apn = (JdkApplicationProtocolNegotiator)ObjectUtil.checkNotNull(apn, "apn");
/* 242 */     this.clientAuth = (ClientAuth)ObjectUtil.checkNotNull(clientAuth, "clientAuth");
/* 243 */     this.sslContext = (SSLContext)ObjectUtil.checkNotNull(sslContext, "sslContext");
/*     */ 
/*     */ 
/*     */     
/* 247 */     if (DEFAULT_PROVIDER.equals(sslContext.getProvider())) {
/* 248 */       this.protocols = (protocols == null) ? DEFAULT_PROTOCOLS : protocols;
/* 249 */       if (isTlsV13Supported(this.protocols)) {
/* 250 */         supportedCiphers = SUPPORTED_CIPHERS;
/* 251 */         defaultCiphers = DEFAULT_CIPHERS;
/*     */       } else {
/*     */         
/* 254 */         supportedCiphers = SUPPORTED_CIPHERS_NON_TLSV13;
/* 255 */         defaultCiphers = DEFAULT_CIPHERS_NON_TLSV13;
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 261 */       SSLEngine engine = sslContext.createSSLEngine();
/*     */       try {
/* 263 */         if (protocols == null) {
/* 264 */           this.protocols = defaultProtocols(sslContext, engine);
/*     */         } else {
/* 266 */           this.protocols = protocols;
/*     */         } 
/* 268 */         supportedCiphers = supportedCiphers(engine);
/* 269 */         defaultCiphers = defaultCiphers(engine, supportedCiphers);
/* 270 */         if (!isTlsV13Supported(this.protocols))
/*     */         {
/* 272 */           for (String cipher : SslUtils.DEFAULT_TLSV13_CIPHER_SUITES) {
/* 273 */             supportedCiphers.remove(cipher);
/* 274 */             defaultCiphers.remove(cipher);
/*     */           } 
/*     */         }
/*     */       } finally {
/* 278 */         ReferenceCountUtil.release(engine);
/*     */       } 
/*     */     } 
/*     */     
/* 282 */     this.cipherSuites = ((CipherSuiteFilter)ObjectUtil.checkNotNull(cipherFilter, "cipherFilter")).filterCipherSuites(ciphers, defaultCiphers, supportedCiphers);
/*     */ 
/*     */     
/* 285 */     this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
/* 286 */     this.isClient = isClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SSLContext context() {
/* 293 */     return this.sslContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isClient() {
/* 298 */     return this.isClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SSLSessionContext sessionContext() {
/* 306 */     if (isServer()) {
/* 307 */       return context().getServerSessionContext();
/*     */     }
/* 309 */     return context().getClientSessionContext();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final List<String> cipherSuites() {
/* 315 */     return this.unmodifiableCipherSuites;
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionCacheSize() {
/* 320 */     return sessionContext().getSessionCacheSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionTimeout() {
/* 325 */     return sessionContext().getSessionTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc) {
/* 330 */     return configureAndWrapEngine(context().createSSLEngine(), alloc);
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
/* 335 */     return configureAndWrapEngine(context().createSSLEngine(peerHost, peerPort), alloc);
/*     */   }
/*     */ 
/*     */   
/*     */   private SSLEngine configureAndWrapEngine(SSLEngine engine, ByteBufAllocator alloc) {
/* 340 */     engine.setEnabledCipherSuites(this.cipherSuites);
/* 341 */     engine.setEnabledProtocols(this.protocols);
/* 342 */     engine.setUseClientMode(isClient());
/* 343 */     if (isServer()) {
/* 344 */       switch (this.clientAuth) {
/*     */         case NONE:
/* 346 */           engine.setWantClientAuth(true);
/*     */           break;
/*     */         case ALPN:
/* 349 */           engine.setNeedClientAuth(true);
/*     */           break;
/*     */         case NPN:
/*     */           break;
/*     */         default:
/* 354 */           throw new Error("Unknown auth " + this.clientAuth);
/*     */       } 
/*     */     }
/* 357 */     JdkApplicationProtocolNegotiator.SslEngineWrapperFactory factory = this.apn.wrapperFactory();
/* 358 */     if (factory instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory) {
/* 359 */       return ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)factory)
/* 360 */         .wrapSslEngine(engine, alloc, this.apn, isServer());
/*     */     }
/* 362 */     return factory.wrapSslEngine(engine, this.apn, isServer());
/*     */   }
/*     */ 
/*     */   
/*     */   public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
/* 367 */     return this.apn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config, boolean isServer) {
/* 378 */     if (config == null) {
/* 379 */       return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
/*     */     }
/*     */     
/* 382 */     switch (config.protocol()) {
/*     */       case NONE:
/* 384 */         return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
/*     */       case ALPN:
/* 386 */         if (isServer) {
/* 387 */           switch (config.selectorFailureBehavior()) {
/*     */             case NONE:
/* 389 */               return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
/*     */             case ALPN:
/* 391 */               return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
/*     */           } 
/* 393 */           throw new UnsupportedOperationException("JDK provider does not support " + config
/* 394 */               .selectorFailureBehavior() + " failure behavior");
/*     */         } 
/*     */         
/* 397 */         switch (config.selectedListenerFailureBehavior()) {
/*     */           case NONE:
/* 399 */             return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
/*     */           case ALPN:
/* 401 */             return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
/*     */         } 
/* 403 */         throw new UnsupportedOperationException("JDK provider does not support " + config
/* 404 */             .selectedListenerFailureBehavior() + " failure behavior");
/*     */ 
/*     */       
/*     */       case NPN:
/* 408 */         if (isServer) {
/* 409 */           switch (config.selectedListenerFailureBehavior()) {
/*     */             case NONE:
/* 411 */               return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
/*     */             case ALPN:
/* 413 */               return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
/*     */           } 
/* 415 */           throw new UnsupportedOperationException("JDK provider does not support " + config
/* 416 */               .selectedListenerFailureBehavior() + " failure behavior");
/*     */         } 
/*     */         
/* 419 */         switch (config.selectorFailureBehavior()) {
/*     */           case NONE:
/* 421 */             return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
/*     */           case ALPN:
/* 423 */             return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
/*     */         } 
/* 425 */         throw new UnsupportedOperationException("JDK provider does not support " + config
/* 426 */             .selectorFailureBehavior() + " failure behavior");
/*     */     } 
/*     */ 
/*     */     
/* 430 */     throw new UnsupportedOperationException("JDK provider does not support " + config
/* 431 */         .protocol() + " protocol");
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
/*     */   static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
/* 450 */     String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
/* 451 */     if (algorithm == null) {
/* 452 */       algorithm = "SunX509";
/*     */     }
/* 454 */     return buildKeyManagerFactory(certChainFile, algorithm, keyFile, keyPassword, kmf, keyStore);
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
/*     */   @Deprecated
/*     */   protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
/* 473 */     return buildKeyManagerFactory(certChainFile, keyFile, keyPassword, kmf, KeyStore.getDefaultType());
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
/*     */   static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
/* 496 */     return buildKeyManagerFactory(toX509Certificates(certChainFile), keyAlgorithm, 
/* 497 */         toPrivateKey(keyFile, keyPassword), keyPassword, kmf, keyStore);
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
/*     */   @Deprecated
/*     */   protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
/* 521 */     return buildKeyManagerFactory(toX509Certificates(certChainFile), keyAlgorithm, 
/* 522 */         toPrivateKey(keyFile, keyPassword), keyPassword, kmf, KeyStore.getDefaultType());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkSslContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */