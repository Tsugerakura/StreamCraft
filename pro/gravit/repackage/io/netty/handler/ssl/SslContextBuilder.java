/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.security.KeyStore;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SslContextBuilder
/*     */ {
/*     */   private final boolean forServer;
/*     */   private SslProvider provider;
/*     */   private Provider sslContextProvider;
/*     */   private X509Certificate[] trustCertCollection;
/*     */   private TrustManagerFactory trustManagerFactory;
/*     */   private X509Certificate[] keyCertChain;
/*     */   private PrivateKey key;
/*     */   private String keyPassword;
/*     */   private KeyManagerFactory keyManagerFactory;
/*     */   private Iterable<String> ciphers;
/*     */   
/*     */   public static SslContextBuilder forClient() {
/*  49 */     return new SslContextBuilder(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslContextBuilder forServer(File keyCertChainFile, File keyFile) {
/*  60 */     return (new SslContextBuilder(true)).keyManager(keyCertChainFile, keyFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream) {
/*  71 */     return (new SslContextBuilder(true)).keyManager(keyCertChainInputStream, keyInputStream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslContextBuilder forServer(PrivateKey key, X509Certificate... keyCertChain) {
/*  82 */     return (new SslContextBuilder(true)).keyManager(key, keyCertChain);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslContextBuilder forServer(PrivateKey key, Iterable<? extends X509Certificate> keyCertChain) {
/*  93 */     return forServer(key, toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
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
/*     */   public static SslContextBuilder forServer(File keyCertChainFile, File keyFile, String keyPassword) {
/* 107 */     return (new SslContextBuilder(true)).keyManager(keyCertChainFile, keyFile, keyPassword);
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
/*     */   public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
/* 121 */     return (new SslContextBuilder(true)).keyManager(keyCertChainInputStream, keyInputStream, keyPassword);
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
/*     */   public static SslContextBuilder forServer(PrivateKey key, String keyPassword, X509Certificate... keyCertChain) {
/* 135 */     return (new SslContextBuilder(true)).keyManager(key, keyPassword, keyCertChain);
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
/*     */   public static SslContextBuilder forServer(PrivateKey key, String keyPassword, Iterable<? extends X509Certificate> keyCertChain) {
/* 149 */     return forServer(key, keyPassword, toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
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
/*     */   public static SslContextBuilder forServer(KeyManagerFactory keyManagerFactory) {
/* 162 */     return (new SslContextBuilder(true)).keyManager(keyManagerFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslContextBuilder forServer(KeyManager keyManager) {
/* 171 */     return (new SslContextBuilder(true)).keyManager(keyManager);
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
/* 184 */   private CipherSuiteFilter cipherFilter = IdentityCipherSuiteFilter.INSTANCE;
/*     */   private ApplicationProtocolConfig apn;
/*     */   private long sessionCacheSize;
/*     */   private long sessionTimeout;
/* 188 */   private ClientAuth clientAuth = ClientAuth.NONE;
/*     */   private String[] protocols;
/*     */   private boolean startTls;
/*     */   private boolean enableOcsp;
/* 192 */   private String keyStoreType = KeyStore.getDefaultType();
/*     */   
/*     */   private SslContextBuilder(boolean forServer) {
/* 195 */     this.forServer = forServer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder sslProvider(SslProvider provider) {
/* 202 */     this.provider = provider;
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyStoreType(String keyStoreType) {
/* 210 */     this.keyStoreType = keyStoreType;
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder sslContextProvider(Provider sslContextProvider) {
/* 219 */     this.sslContextProvider = sslContextProvider;
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder trustManager(File trustCertCollectionFile) {
/*     */     try {
/* 229 */       return trustManager(SslContext.toX509Certificates(trustCertCollectionFile));
/* 230 */     } catch (Exception e) {
/* 231 */       throw new IllegalArgumentException("File does not contain valid certificates: " + trustCertCollectionFile, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder trustManager(InputStream trustCertCollectionInputStream) {
/*     */     try {
/* 242 */       return trustManager(SslContext.toX509Certificates(trustCertCollectionInputStream));
/* 243 */     } catch (Exception e) {
/* 244 */       throw new IllegalArgumentException("Input stream does not contain valid certificates.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder trustManager(X509Certificate... trustCertCollection) {
/* 252 */     this.trustCertCollection = (trustCertCollection != null) ? (X509Certificate[])trustCertCollection.clone() : null;
/* 253 */     this.trustManagerFactory = null;
/* 254 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder trustManager(Iterable<? extends X509Certificate> trustCertCollection) {
/* 261 */     return trustManager(toArray(trustCertCollection, EmptyArrays.EMPTY_X509_CERTIFICATES));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder trustManager(TrustManagerFactory trustManagerFactory) {
/* 268 */     this.trustCertCollection = null;
/* 269 */     this.trustManagerFactory = trustManagerFactory;
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
/*     */   public SslContextBuilder trustManager(TrustManager trustManager) {
/* 281 */     this.trustManagerFactory = (TrustManagerFactory)new TrustManagerFactoryWrapper(trustManager);
/* 282 */     this.trustCertCollection = null;
/* 283 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyManager(File keyCertChainFile, File keyFile) {
/* 294 */     return keyManager(keyCertChainFile, keyFile, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream) {
/* 305 */     return keyManager(keyCertChainInputStream, keyInputStream, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyManager(PrivateKey key, X509Certificate... keyCertChain) {
/* 316 */     return keyManager(key, (String)null, keyCertChain);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyManager(PrivateKey key, Iterable<? extends X509Certificate> keyCertChain) {
/* 327 */     return keyManager(key, toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
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
/*     */   public SslContextBuilder keyManager(File keyCertChainFile, File keyFile, String keyPassword) {
/*     */     X509Certificate[] keyCertChain;
/*     */     PrivateKey key;
/*     */     try {
/* 343 */       keyCertChain = SslContext.toX509Certificates(keyCertChainFile);
/* 344 */     } catch (Exception e) {
/* 345 */       throw new IllegalArgumentException("File does not contain valid certificates: " + keyCertChainFile, e);
/*     */     } 
/*     */     try {
/* 348 */       key = SslContext.toPrivateKey(keyFile, keyPassword);
/* 349 */     } catch (Exception e) {
/* 350 */       throw new IllegalArgumentException("File does not contain valid private key: " + keyFile, e);
/*     */     } 
/* 352 */     return keyManager(key, keyPassword, keyCertChain);
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
/*     */   public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
/*     */     X509Certificate[] keyCertChain;
/*     */     PrivateKey key;
/*     */     try {
/* 369 */       keyCertChain = SslContext.toX509Certificates(keyCertChainInputStream);
/* 370 */     } catch (Exception e) {
/* 371 */       throw new IllegalArgumentException("Input stream not contain valid certificates.", e);
/*     */     } 
/*     */     try {
/* 374 */       key = SslContext.toPrivateKey(keyInputStream, keyPassword);
/* 375 */     } catch (Exception e) {
/* 376 */       throw new IllegalArgumentException("Input stream does not contain valid private key.", e);
/*     */     } 
/* 378 */     return keyManager(key, keyPassword, keyCertChain);
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
/*     */   public SslContextBuilder keyManager(PrivateKey key, String keyPassword, X509Certificate... keyCertChain) {
/* 391 */     if (this.forServer) {
/* 392 */       ObjectUtil.checkNotNull(keyCertChain, "keyCertChain required for servers");
/* 393 */       if (keyCertChain.length == 0) {
/* 394 */         throw new IllegalArgumentException("keyCertChain must be non-empty");
/*     */       }
/* 396 */       ObjectUtil.checkNotNull(key, "key required for servers");
/*     */     } 
/* 398 */     if (keyCertChain == null || keyCertChain.length == 0) {
/* 399 */       this.keyCertChain = null;
/*     */     } else {
/* 401 */       for (X509Certificate cert : keyCertChain) {
/* 402 */         if (cert == null) {
/* 403 */           throw new IllegalArgumentException("keyCertChain contains null entry");
/*     */         }
/*     */       } 
/* 406 */       this.keyCertChain = (X509Certificate[])keyCertChain.clone();
/*     */     } 
/* 408 */     this.key = key;
/* 409 */     this.keyPassword = keyPassword;
/* 410 */     this.keyManagerFactory = null;
/* 411 */     return this;
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
/*     */   public SslContextBuilder keyManager(PrivateKey key, String keyPassword, Iterable<? extends X509Certificate> keyCertChain) {
/* 425 */     return keyManager(key, keyPassword, toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
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
/*     */   public SslContextBuilder keyManager(KeyManagerFactory keyManagerFactory) {
/* 440 */     if (this.forServer) {
/* 441 */       ObjectUtil.checkNotNull(keyManagerFactory, "keyManagerFactory required for servers");
/*     */     }
/* 443 */     this.keyCertChain = null;
/* 444 */     this.key = null;
/* 445 */     this.keyPassword = null;
/* 446 */     this.keyManagerFactory = keyManagerFactory;
/* 447 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder keyManager(KeyManager keyManager) {
/* 458 */     if (this.forServer) {
/* 459 */       ObjectUtil.checkNotNull(keyManager, "keyManager required for servers");
/*     */     }
/* 461 */     if (keyManager != null) {
/* 462 */       this.keyManagerFactory = (KeyManagerFactory)new KeyManagerFactoryWrapper(keyManager);
/*     */     } else {
/* 464 */       this.keyManagerFactory = null;
/*     */     } 
/* 466 */     this.keyCertChain = null;
/* 467 */     this.key = null;
/* 468 */     this.keyPassword = null;
/* 469 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder ciphers(Iterable<String> ciphers) {
/* 477 */     return ciphers(ciphers, IdentityCipherSuiteFilter.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder ciphers(Iterable<String> ciphers, CipherSuiteFilter cipherFilter) {
/* 486 */     this.cipherFilter = (CipherSuiteFilter)ObjectUtil.checkNotNull(cipherFilter, "cipherFilter");
/* 487 */     this.ciphers = ciphers;
/* 488 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder applicationProtocolConfig(ApplicationProtocolConfig apn) {
/* 495 */     this.apn = apn;
/* 496 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder sessionCacheSize(long sessionCacheSize) {
/* 504 */     this.sessionCacheSize = sessionCacheSize;
/* 505 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder sessionTimeout(long sessionTimeout) {
/* 513 */     this.sessionTimeout = sessionTimeout;
/* 514 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder clientAuth(ClientAuth clientAuth) {
/* 521 */     this.clientAuth = (ClientAuth)ObjectUtil.checkNotNull(clientAuth, "clientAuth");
/* 522 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder protocols(String... protocols) {
/* 531 */     this.protocols = (protocols == null) ? null : (String[])protocols.clone();
/* 532 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder protocols(Iterable<String> protocols) {
/* 541 */     return protocols(toArray(protocols, EmptyArrays.EMPTY_STRINGS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder startTls(boolean startTls) {
/* 548 */     this.startTls = startTls;
/* 549 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContextBuilder enableOcsp(boolean enableOcsp) {
/* 560 */     this.enableOcsp = enableOcsp;
/* 561 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContext build() throws SSLException {
/* 570 */     if (this.forServer) {
/* 571 */       return SslContext.newServerContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.sessionCacheSize, this.sessionTimeout, this.clientAuth, this.protocols, this.startTls, this.enableOcsp, this.keyStoreType);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 576 */     return SslContext.newClientContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.protocols, this.sessionCacheSize, this.sessionTimeout, this.enableOcsp, this.keyStoreType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> T[] toArray(Iterable<? extends T> iterable, T[] prototype) {
/* 583 */     if (iterable == null) {
/* 584 */       return null;
/*     */     }
/* 586 */     List<T> list = new ArrayList<T>();
/* 587 */     for (T element : iterable) {
/* 588 */       list.add(element);
/*     */     }
/* 590 */     return list.toArray(prototype);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslContextBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */