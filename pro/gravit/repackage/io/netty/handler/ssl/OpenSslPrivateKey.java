/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.PrivateKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class OpenSslPrivateKey
/*     */   extends AbstractReferenceCounted
/*     */   implements PrivateKey
/*     */ {
/*     */   private long privateKeyAddress;
/*     */   
/*     */   OpenSslPrivateKey(long privateKeyAddress) {
/*  32 */     this.privateKeyAddress = privateKeyAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlgorithm() {
/*  37 */     return "unknown";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormat() {
/*  43 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getEncoded() {
/*  48 */     return null;
/*     */   }
/*     */   
/*     */   private long privateKeyAddress() {
/*  52 */     if (refCnt() <= 0) {
/*  53 */       throw new IllegalReferenceCountException();
/*     */     }
/*  55 */     return this.privateKeyAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/*  60 */     SSL.freePrivateKey(this.privateKeyAddress);
/*  61 */     this.privateKeyAddress = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslPrivateKey retain() {
/*  66 */     super.retain();
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslPrivateKey retain(int increment) {
/*  72 */     super.retain(increment);
/*  73 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslPrivateKey touch() {
/*  78 */     super.touch();
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OpenSslPrivateKey touch(Object hint) {
/*  84 */     return this;
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
/*     */   public void destroy() {
/*  96 */     release(refCnt());
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
/*     */   public boolean isDestroyed() {
/* 108 */     return (refCnt() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   OpenSslKeyMaterial newKeyMaterial(long certificateChain, X509Certificate[] chain) {
/* 118 */     return new OpenSslPrivateKeyMaterial(certificateChain, chain);
/*     */   }
/*     */   
/*     */   final class OpenSslPrivateKeyMaterial
/*     */     extends AbstractReferenceCounted
/*     */     implements OpenSslKeyMaterial
/*     */   {
/*     */     long certificateChain;
/*     */     private final X509Certificate[] x509CertificateChain;
/*     */     
/*     */     OpenSslPrivateKeyMaterial(long certificateChain, X509Certificate[] x509CertificateChain) {
/* 129 */       this.certificateChain = certificateChain;
/* 130 */       this.x509CertificateChain = (x509CertificateChain == null) ? EmptyArrays.EMPTY_X509_CERTIFICATES : x509CertificateChain;
/*     */       
/* 132 */       OpenSslPrivateKey.this.retain();
/*     */     }
/*     */ 
/*     */     
/*     */     public X509Certificate[] certificateChain() {
/* 137 */       return (X509Certificate[])this.x509CertificateChain.clone();
/*     */     }
/*     */ 
/*     */     
/*     */     public long certificateChainAddress() {
/* 142 */       if (refCnt() <= 0) {
/* 143 */         throw new IllegalReferenceCountException();
/*     */       }
/* 145 */       return this.certificateChain;
/*     */     }
/*     */ 
/*     */     
/*     */     public long privateKeyAddress() {
/* 150 */       if (refCnt() <= 0) {
/* 151 */         throw new IllegalReferenceCountException();
/*     */       }
/* 153 */       return OpenSslPrivateKey.this.privateKeyAddress();
/*     */     }
/*     */ 
/*     */     
/*     */     public OpenSslKeyMaterial touch(Object hint) {
/* 158 */       OpenSslPrivateKey.this.touch(hint);
/* 159 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public OpenSslKeyMaterial retain() {
/* 164 */       super.retain();
/* 165 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public OpenSslKeyMaterial retain(int increment) {
/* 170 */       super.retain(increment);
/* 171 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public OpenSslKeyMaterial touch() {
/* 176 */       OpenSslPrivateKey.this.touch();
/* 177 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void deallocate() {
/* 182 */       releaseChain();
/* 183 */       OpenSslPrivateKey.this.release();
/*     */     }
/*     */     
/*     */     private void releaseChain() {
/* 187 */       SSL.freeX509Chain(this.certificateChain);
/* 188 */       this.certificateChain = 0L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */