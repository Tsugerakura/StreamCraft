/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.Principal;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SignatureException;
/*     */ import java.util.Date;
/*     */ import javax.security.cert.CertificateException;
/*     */ import javax.security.cert.CertificateExpiredException;
/*     */ import javax.security.cert.CertificateNotYetValidException;
/*     */ import javax.security.cert.X509Certificate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class OpenSslJavaxX509Certificate
/*     */   extends X509Certificate
/*     */ {
/*     */   private final byte[] bytes;
/*     */   private X509Certificate wrapped;
/*     */   
/*     */   OpenSslJavaxX509Certificate(byte[] bytes) {
/*  36 */     this.bytes = bytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
/*  41 */     unwrap().checkValidity();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
/*  46 */     unwrap().checkValidity(date);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/*  51 */     return unwrap().getVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getSerialNumber() {
/*  56 */     return unwrap().getSerialNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getIssuerDN() {
/*  61 */     return unwrap().getIssuerDN();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getSubjectDN() {
/*  66 */     return unwrap().getSubjectDN();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotBefore() {
/*  71 */     return unwrap().getNotBefore();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotAfter() {
/*  76 */     return unwrap().getNotAfter();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgName() {
/*  81 */     return unwrap().getSigAlgName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgOID() {
/*  86 */     return unwrap().getSigAlgOID();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSigAlgParams() {
/*  91 */     return unwrap().getSigAlgParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getEncoded() {
/*  96 */     return (byte[])this.bytes.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
/* 103 */     unwrap().verify(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
/* 110 */     unwrap().verify(key, sigProvider);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     return unwrap().toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public PublicKey getPublicKey() {
/* 120 */     return unwrap().getPublicKey();
/*     */   }
/*     */   
/*     */   private X509Certificate unwrap() {
/* 124 */     X509Certificate wrapped = this.wrapped;
/* 125 */     if (wrapped == null) {
/*     */       try {
/* 127 */         wrapped = this.wrapped = X509Certificate.getInstance(this.bytes);
/* 128 */       } catch (CertificateException e) {
/* 129 */         throw new IllegalStateException(e);
/*     */       } 
/*     */     }
/* 132 */     return wrapped;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslJavaxX509Certificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */