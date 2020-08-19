/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.KeyStoreSpi;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.KeyManagerFactorySpi;
/*     */ import javax.net.ssl.ManagerFactoryParameters;
/*     */ import javax.net.ssl.X509KeyManager;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.UnpooledByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class OpenSslX509KeyManagerFactory
/*     */   extends KeyManagerFactory
/*     */ {
/*     */   private final OpenSslKeyManagerFactorySpi spi;
/*     */   
/*     */   public OpenSslX509KeyManagerFactory() {
/*  68 */     this(newOpenSslKeyManagerFactorySpi(null));
/*     */   }
/*     */   
/*     */   public OpenSslX509KeyManagerFactory(Provider provider) {
/*  72 */     this(newOpenSslKeyManagerFactorySpi(provider));
/*     */   }
/*     */   
/*     */   public OpenSslX509KeyManagerFactory(String algorithm, Provider provider) throws NoSuchAlgorithmException {
/*  76 */     this(newOpenSslKeyManagerFactorySpi(algorithm, provider));
/*     */   }
/*     */   
/*     */   private OpenSslX509KeyManagerFactory(OpenSslKeyManagerFactorySpi spi) {
/*  80 */     super(spi, spi.kmf.getProvider(), spi.kmf.getAlgorithm());
/*  81 */     this.spi = spi;
/*     */   }
/*     */   
/*     */   private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(Provider provider) {
/*     */     try {
/*  86 */       return newOpenSslKeyManagerFactorySpi(null, provider);
/*  87 */     } catch (NoSuchAlgorithmException e) {
/*     */       
/*  89 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String algorithm, Provider provider) throws NoSuchAlgorithmException {
/*  95 */     if (algorithm == null) {
/*  96 */       algorithm = KeyManagerFactory.getDefaultAlgorithm();
/*     */     }
/*  98 */     return new OpenSslKeyManagerFactorySpi((provider == null) ? 
/*  99 */         KeyManagerFactory.getInstance(algorithm) : 
/* 100 */         KeyManagerFactory.getInstance(algorithm, provider));
/*     */   }
/*     */   
/*     */   OpenSslKeyMaterialProvider newProvider() {
/* 104 */     return this.spi.newProvider();
/*     */   }
/*     */   
/*     */   private static final class OpenSslKeyManagerFactorySpi extends KeyManagerFactorySpi {
/*     */     final KeyManagerFactory kmf;
/*     */     private volatile ProviderFactory providerFactory;
/*     */     
/*     */     OpenSslKeyManagerFactorySpi(KeyManagerFactory kmf) {
/* 112 */       this.kmf = (KeyManagerFactory)ObjectUtil.checkNotNull(kmf, "kmf");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected synchronized void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 118 */       if (this.providerFactory != null) {
/* 119 */         throw new KeyStoreException("Already initialized");
/*     */       }
/* 121 */       if (!keyStore.aliases().hasMoreElements()) {
/* 122 */         throw new KeyStoreException("No aliases found");
/*     */       }
/*     */       
/* 125 */       this.kmf.init(keyStore, chars);
/* 126 */       this
/* 127 */         .providerFactory = new ProviderFactory(ReferenceCountedOpenSslContext.chooseX509KeyManager(this.kmf.getKeyManagers()), password(chars), Collections.list(keyStore.aliases()));
/*     */     }
/*     */     
/*     */     private static String password(char[] password) {
/* 131 */       if (password == null || password.length == 0) {
/* 132 */         return null;
/*     */       }
/* 134 */       return new String(password);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
/* 140 */       throw new InvalidAlgorithmParameterException("Not supported");
/*     */     }
/*     */ 
/*     */     
/*     */     protected KeyManager[] engineGetKeyManagers() {
/* 145 */       ProviderFactory providerFactory = this.providerFactory;
/* 146 */       if (providerFactory == null) {
/* 147 */         throw new IllegalStateException("engineInit(...) not called yet");
/*     */       }
/* 149 */       return new KeyManager[] { ProviderFactory.access$000(providerFactory) };
/*     */     }
/*     */     
/*     */     OpenSslKeyMaterialProvider newProvider() {
/* 153 */       ProviderFactory providerFactory = this.providerFactory;
/* 154 */       if (providerFactory == null) {
/* 155 */         throw new IllegalStateException("engineInit(...) not called yet");
/*     */       }
/* 157 */       return providerFactory.newProvider();
/*     */     }
/*     */     
/*     */     private static final class ProviderFactory {
/*     */       private final X509KeyManager keyManager;
/*     */       private final String password;
/*     */       private final Iterable<String> aliases;
/*     */       
/*     */       ProviderFactory(X509KeyManager keyManager, String password, Iterable<String> aliases) {
/* 166 */         this.keyManager = keyManager;
/* 167 */         this.password = password;
/* 168 */         this.aliases = aliases;
/*     */       }
/*     */       
/*     */       OpenSslKeyMaterialProvider newProvider() {
/* 172 */         return new OpenSslPopulatedKeyMaterialProvider(this.keyManager, this.password, this.aliases);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       private static final class OpenSslPopulatedKeyMaterialProvider
/*     */         extends OpenSslKeyMaterialProvider
/*     */       {
/*     */         private final Map<String, Object> materialMap;
/*     */ 
/*     */ 
/*     */         
/*     */         OpenSslPopulatedKeyMaterialProvider(X509KeyManager keyManager, String password, Iterable<String> aliases) {
/* 185 */           super(keyManager, password);
/* 186 */           this.materialMap = new HashMap<String, Object>();
/* 187 */           boolean initComplete = false;
/*     */           try {
/* 189 */             for (String alias : aliases) {
/* 190 */               if (alias != null && !this.materialMap.containsKey(alias)) {
/*     */                 try {
/* 192 */                   this.materialMap.put(alias, super.chooseKeyMaterial((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, alias));
/*     */                 }
/* 194 */                 catch (Exception e) {
/*     */ 
/*     */                   
/* 197 */                   this.materialMap.put(alias, e);
/*     */                 } 
/*     */               }
/*     */             } 
/* 201 */             initComplete = true;
/*     */           } finally {
/* 203 */             if (!initComplete) {
/* 204 */               destroy();
/*     */             }
/*     */           } 
/* 207 */           if (this.materialMap.isEmpty()) {
/* 208 */             throw new IllegalArgumentException("aliases must be non-empty");
/*     */           }
/*     */         }
/*     */ 
/*     */         
/*     */         OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
/* 214 */           Object value = this.materialMap.get(alias);
/* 215 */           if (value == null)
/*     */           {
/* 217 */             return null;
/*     */           }
/* 219 */           if (value instanceof OpenSslKeyMaterial) {
/* 220 */             return ((OpenSslKeyMaterial)value).retain();
/*     */           }
/* 222 */           throw (Exception)value;
/*     */         }
/*     */ 
/*     */         
/*     */         void destroy() {
/* 227 */           for (Object material : this.materialMap.values()) {
/* 228 */             ReferenceCountUtil.release(material);
/*     */           }
/* 230 */           this.materialMap.clear();
/*     */         }
/*     */       }
/*     */     }
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
/*     */   public static OpenSslX509KeyManagerFactory newEngineBased(File certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 245 */     return newEngineBased(SslContext.toX509Certificates(certificateChain), password);
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
/*     */   public static OpenSslX509KeyManagerFactory newEngineBased(X509Certificate[] certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 257 */     KeyStore store = new OpenSslKeyStore((X509Certificate[])certificateChain.clone(), false);
/* 258 */     store.load(null, null);
/* 259 */     OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
/* 260 */     factory.init(store, (password == null) ? null : password.toCharArray());
/* 261 */     return factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OpenSslX509KeyManagerFactory newKeyless(File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 270 */     return newKeyless(SslContext.toX509Certificates(chain));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OpenSslX509KeyManagerFactory newKeyless(InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 279 */     return newKeyless(SslContext.toX509Certificates(chain));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OpenSslX509KeyManagerFactory newKeyless(X509Certificate... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
/* 289 */     KeyStore store = new OpenSslKeyStore((X509Certificate[])certificateChain.clone(), true);
/* 290 */     store.load(null, null);
/* 291 */     OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
/* 292 */     factory.init(store, null);
/* 293 */     return factory;
/*     */   }
/*     */   
/*     */   private static final class OpenSslKeyStore extends KeyStore {
/*     */     private OpenSslKeyStore(X509Certificate[] certificateChain, boolean keyless) {
/* 298 */       super(new KeyStoreSpi(keyless, certificateChain)
/*     */           {
/* 300 */             private final Date creationDate = new Date();
/*     */ 
/*     */             
/*     */             public Key engineGetKey(String alias, char[] password) throws UnrecoverableKeyException {
/* 304 */               if (engineContainsAlias(alias)) {
/*     */                 long privateKeyAddress;
/* 306 */                 if (keyless) {
/* 307 */                   privateKeyAddress = 0L;
/*     */                 } else {
/*     */                   try {
/* 310 */                     privateKeyAddress = SSL.loadPrivateKeyFromEngine(alias, (password == null) ? null : new String(password));
/*     */                   }
/* 312 */                   catch (Exception e) {
/* 313 */                     UnrecoverableKeyException keyException = new UnrecoverableKeyException("Unable to load key from engine");
/*     */                     
/* 315 */                     keyException.initCause(e);
/* 316 */                     throw keyException;
/*     */                   } 
/*     */                 } 
/* 319 */                 return new OpenSslPrivateKey(privateKeyAddress);
/*     */               } 
/* 321 */               return null;
/*     */             }
/*     */ 
/*     */             
/*     */             public Certificate[] engineGetCertificateChain(String alias) {
/* 326 */               return engineContainsAlias(alias) ? (Certificate[])certificateChain.clone() : null;
/*     */             }
/*     */ 
/*     */             
/*     */             public Certificate engineGetCertificate(String alias) {
/* 331 */               return engineContainsAlias(alias) ? certificateChain[0] : null;
/*     */             }
/*     */ 
/*     */             
/*     */             public Date engineGetCreationDate(String alias) {
/* 336 */               return engineContainsAlias(alias) ? this.creationDate : null;
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
/* 342 */               throw new KeyStoreException("Not supported");
/*     */             }
/*     */ 
/*     */             
/*     */             public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
/* 347 */               throw new KeyStoreException("Not supported");
/*     */             }
/*     */ 
/*     */             
/*     */             public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
/* 352 */               throw new KeyStoreException("Not supported");
/*     */             }
/*     */ 
/*     */             
/*     */             public void engineDeleteEntry(String alias) throws KeyStoreException {
/* 357 */               throw new KeyStoreException("Not supported");
/*     */             }
/*     */ 
/*     */             
/*     */             public Enumeration<String> engineAliases() {
/* 362 */               return Collections.enumeration(Collections.singleton("key"));
/*     */             }
/*     */ 
/*     */             
/*     */             public boolean engineContainsAlias(String alias) {
/* 367 */               return "key".equals(alias);
/*     */             }
/*     */ 
/*     */             
/*     */             public int engineSize() {
/* 372 */               return 1;
/*     */             }
/*     */ 
/*     */             
/*     */             public boolean engineIsKeyEntry(String alias) {
/* 377 */               return engineContainsAlias(alias);
/*     */             }
/*     */ 
/*     */             
/*     */             public boolean engineIsCertificateEntry(String alias) {
/* 382 */               return engineContainsAlias(alias);
/*     */             }
/*     */ 
/*     */             
/*     */             public String engineGetCertificateAlias(Certificate cert) {
/* 387 */               if (cert instanceof X509Certificate) {
/* 388 */                 for (X509Certificate x509Certificate : certificateChain) {
/* 389 */                   if (x509Certificate.equals(cert)) {
/* 390 */                     return "key";
/*     */                   }
/*     */                 } 
/*     */               }
/* 394 */               return null;
/*     */             }
/*     */ 
/*     */             
/*     */             public void engineStore(OutputStream stream, char[] password) {
/* 399 */               throw new UnsupportedOperationException();
/*     */             }
/*     */ 
/*     */             
/*     */             public void engineLoad(InputStream stream, char[] password) {
/* 404 */               if (stream != null && password != null) {
/* 405 */                 throw new UnsupportedOperationException();
/*     */               }
/*     */             }
/*     */           }null, "native");
/*     */       
/* 410 */       OpenSsl.ensureAvailability();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslX509KeyManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */