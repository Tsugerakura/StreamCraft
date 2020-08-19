/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.Principal;
/*     */ import java.security.Provider;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateExpiredException;
/*     */ import java.security.cert.CertificateNotYetValidException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class OpenSslX509Certificate
/*     */   extends X509Certificate
/*     */ {
/*     */   private final byte[] bytes;
/*     */   private X509Certificate wrapped;
/*     */   
/*     */   OpenSslX509Certificate(byte[] bytes) {
/*  47 */     this.bytes = bytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
/*  52 */     unwrap().checkValidity();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
/*  57 */     unwrap().checkValidity(date);
/*     */   }
/*     */ 
/*     */   
/*     */   public X500Principal getIssuerX500Principal() {
/*  62 */     return unwrap().getIssuerX500Principal();
/*     */   }
/*     */ 
/*     */   
/*     */   public X500Principal getSubjectX500Principal() {
/*  67 */     return unwrap().getSubjectX500Principal();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getExtendedKeyUsage() throws CertificateParsingException {
/*  72 */     return unwrap().getExtendedKeyUsage();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
/*  77 */     return unwrap().getSubjectAlternativeNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
/*  82 */     return unwrap().getSubjectAlternativeNames();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Can only be called from Java8 as class is package-private")
/*     */   public void verify(PublicKey key, Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
/*  89 */     unwrap().verify(key, sigProvider);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/*  94 */     return unwrap().getVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getSerialNumber() {
/*  99 */     return unwrap().getSerialNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getIssuerDN() {
/* 104 */     return unwrap().getIssuerDN();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getSubjectDN() {
/* 109 */     return unwrap().getSubjectDN();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotBefore() {
/* 114 */     return unwrap().getNotBefore();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotAfter() {
/* 119 */     return unwrap().getNotAfter();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getTBSCertificate() throws CertificateEncodingException {
/* 124 */     return unwrap().getTBSCertificate();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSignature() {
/* 129 */     return unwrap().getSignature();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgName() {
/* 134 */     return unwrap().getSigAlgName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgOID() {
/* 139 */     return unwrap().getSigAlgOID();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSigAlgParams() {
/* 144 */     return unwrap().getSigAlgParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getIssuerUniqueID() {
/* 149 */     return unwrap().getIssuerUniqueID();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getSubjectUniqueID() {
/* 154 */     return unwrap().getSubjectUniqueID();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getKeyUsage() {
/* 159 */     return unwrap().getKeyUsage();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBasicConstraints() {
/* 164 */     return unwrap().getBasicConstraints();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getEncoded() {
/* 169 */     return (byte[])this.bytes.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
/* 176 */     unwrap().verify(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
/* 183 */     unwrap().verify(key, sigProvider);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 188 */     return unwrap().toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public PublicKey getPublicKey() {
/* 193 */     return unwrap().getPublicKey();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasUnsupportedCriticalExtension() {
/* 198 */     return unwrap().hasUnsupportedCriticalExtension();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getCriticalExtensionOIDs() {
/* 203 */     return unwrap().getCriticalExtensionOIDs();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getNonCriticalExtensionOIDs() {
/* 208 */     return unwrap().getNonCriticalExtensionOIDs();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getExtensionValue(String oid) {
/* 213 */     return unwrap().getExtensionValue(oid);
/*     */   }
/*     */   
/*     */   private X509Certificate unwrap() {
/* 217 */     X509Certificate wrapped = this.wrapped;
/* 218 */     if (wrapped == null) {
/*     */       try {
/* 220 */         wrapped = this.wrapped = (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate(new ByteArrayInputStream(this.bytes));
/*     */       }
/* 222 */       catch (CertificateException e) {
/* 223 */         throw new IllegalStateException(e);
/*     */       } 
/*     */     }
/* 226 */     return wrapped;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslX509Certificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */