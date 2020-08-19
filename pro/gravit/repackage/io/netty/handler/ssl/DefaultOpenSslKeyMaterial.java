/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.cert.X509Certificate;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetectorFactory;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DefaultOpenSslKeyMaterial
/*     */   extends AbstractReferenceCounted
/*     */   implements OpenSslKeyMaterial
/*     */ {
/*  30 */   private static final ResourceLeakDetector<DefaultOpenSslKeyMaterial> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(DefaultOpenSslKeyMaterial.class);
/*     */   private final ResourceLeakTracker<DefaultOpenSslKeyMaterial> leak;
/*     */   private final X509Certificate[] x509CertificateChain;
/*     */   private long chain;
/*     */   private long privateKey;
/*     */   
/*     */   DefaultOpenSslKeyMaterial(long chain, long privateKey, X509Certificate[] x509CertificateChain) {
/*  37 */     this.chain = chain;
/*  38 */     this.privateKey = privateKey;
/*  39 */     this.x509CertificateChain = x509CertificateChain;
/*  40 */     this.leak = leakDetector.track(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public X509Certificate[] certificateChain() {
/*  45 */     return (X509Certificate[])this.x509CertificateChain.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public long certificateChainAddress() {
/*  50 */     if (refCnt() <= 0) {
/*  51 */       throw new IllegalReferenceCountException();
/*     */     }
/*  53 */     return this.chain;
/*     */   }
/*     */ 
/*     */   
/*     */   public long privateKeyAddress() {
/*  58 */     if (refCnt() <= 0) {
/*  59 */       throw new IllegalReferenceCountException();
/*     */     }
/*  61 */     return this.privateKey;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/*  66 */     SSL.freeX509Chain(this.chain);
/*  67 */     this.chain = 0L;
/*  68 */     SSL.freePrivateKey(this.privateKey);
/*  69 */     this.privateKey = 0L;
/*  70 */     if (this.leak != null) {
/*  71 */       boolean closed = this.leak.close(this);
/*  72 */       assert closed;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultOpenSslKeyMaterial retain() {
/*  78 */     if (this.leak != null) {
/*  79 */       this.leak.record();
/*     */     }
/*  81 */     super.retain();
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultOpenSslKeyMaterial retain(int increment) {
/*  87 */     if (this.leak != null) {
/*  88 */       this.leak.record();
/*     */     }
/*  90 */     super.retain(increment);
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultOpenSslKeyMaterial touch() {
/*  96 */     if (this.leak != null) {
/*  97 */       this.leak.record();
/*     */     }
/*  99 */     super.touch();
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultOpenSslKeyMaterial touch(Object hint) {
/* 105 */     if (this.leak != null) {
/* 106 */       this.leak.record(hint);
/*     */     }
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 113 */     if (this.leak != null) {
/* 114 */       this.leak.record();
/*     */     }
/* 116 */     return super.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 121 */     if (this.leak != null) {
/* 122 */       this.leak.record();
/*     */     }
/* 124 */     return super.release(decrement);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\DefaultOpenSslKeyMaterial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */