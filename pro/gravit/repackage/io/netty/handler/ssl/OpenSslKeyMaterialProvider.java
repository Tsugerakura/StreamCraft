/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.PrivateKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.X509KeyManager;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.UnpooledByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class OpenSslKeyMaterialProvider
/*     */ {
/*     */   private final X509KeyManager keyManager;
/*     */   private final String password;
/*     */   
/*     */   OpenSslKeyMaterialProvider(X509KeyManager keyManager, String password) {
/*  38 */     this.keyManager = keyManager;
/*  39 */     this.password = password;
/*     */   }
/*     */ 
/*     */   
/*     */   static void validateKeyMaterialSupported(X509Certificate[] keyCertChain, PrivateKey key, String keyPassword) throws SSLException {
/*  44 */     validateSupported(keyCertChain);
/*  45 */     validateSupported(key, keyPassword);
/*     */   }
/*     */   
/*     */   private static void validateSupported(PrivateKey key, String password) throws SSLException {
/*  49 */     if (key == null) {
/*     */       return;
/*     */     }
/*     */     
/*  53 */     long pkeyBio = 0L;
/*  54 */     long pkey = 0L;
/*     */     
/*     */     try {
/*  57 */       pkeyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, key);
/*  58 */       pkey = SSL.parsePrivateKey(pkeyBio, password);
/*  59 */     } catch (Exception e) {
/*  60 */       throw new SSLException("PrivateKey type not supported " + key.getFormat(), e);
/*     */     } finally {
/*  62 */       SSL.freeBIO(pkeyBio);
/*  63 */       if (pkey != 0L) {
/*  64 */         SSL.freePrivateKey(pkey);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void validateSupported(X509Certificate[] certificates) throws SSLException {
/*  70 */     if (certificates == null || certificates.length == 0) {
/*     */       return;
/*     */     }
/*     */     
/*  74 */     long chainBio = 0L;
/*  75 */     long chain = 0L;
/*  76 */     PemEncoded encoded = null;
/*     */     try {
/*  78 */       encoded = PemX509Certificate.toPEM((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, true, certificates);
/*  79 */       chainBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, encoded.retain());
/*  80 */       chain = SSL.parseX509Chain(chainBio);
/*  81 */     } catch (Exception e) {
/*  82 */       throw new SSLException("Certificate type not supported", e);
/*     */     } finally {
/*  84 */       SSL.freeBIO(chainBio);
/*  85 */       if (chain != 0L) {
/*  86 */         SSL.freeX509Chain(chain);
/*     */       }
/*  88 */       if (encoded != null) {
/*  89 */         encoded.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   X509KeyManager keyManager() {
/*  98 */     return this.keyManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
/* 106 */     X509Certificate[] certificates = this.keyManager.getCertificateChain(alias);
/* 107 */     if (certificates == null || certificates.length == 0) {
/* 108 */       return null;
/*     */     }
/*     */     
/* 111 */     PrivateKey key = this.keyManager.getPrivateKey(alias);
/* 112 */     PemEncoded encoded = PemX509Certificate.toPEM(allocator, true, certificates);
/* 113 */     long chainBio = 0L;
/* 114 */     long pkeyBio = 0L;
/* 115 */     long chain = 0L;
/* 116 */     long pkey = 0L; try {
/*     */       OpenSslKeyMaterial keyMaterial;
/* 118 */       chainBio = ReferenceCountedOpenSslContext.toBIO(allocator, encoded.retain());
/* 119 */       chain = SSL.parseX509Chain(chainBio);
/*     */ 
/*     */       
/* 122 */       if (key instanceof OpenSslPrivateKey) {
/* 123 */         keyMaterial = ((OpenSslPrivateKey)key).newKeyMaterial(chain, certificates);
/*     */       } else {
/* 125 */         pkeyBio = ReferenceCountedOpenSslContext.toBIO(allocator, key);
/* 126 */         pkey = (key == null) ? 0L : SSL.parsePrivateKey(pkeyBio, this.password);
/* 127 */         keyMaterial = new DefaultOpenSslKeyMaterial(chain, pkey, certificates);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 132 */       chain = 0L;
/* 133 */       pkey = 0L;
/* 134 */       return keyMaterial;
/*     */     } finally {
/* 136 */       SSL.freeBIO(chainBio);
/* 137 */       SSL.freeBIO(pkeyBio);
/* 138 */       if (chain != 0L) {
/* 139 */         SSL.freeX509Chain(chain);
/*     */       }
/* 141 */       if (pkey != 0L) {
/* 142 */         SSL.freePrivateKey(pkey);
/*     */       }
/* 144 */       encoded.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   void destroy() {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslKeyMaterialProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */