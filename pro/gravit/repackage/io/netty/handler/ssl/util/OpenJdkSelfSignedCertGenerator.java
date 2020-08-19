/*    */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.KeyPair;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.SecureRandom;
/*    */ import java.security.cert.CertificateException;
/*    */ import java.util.Date;
/*    */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*    */ import sun.security.x509.AlgorithmId;
/*    */ import sun.security.x509.CertificateAlgorithmId;
/*    */ import sun.security.x509.CertificateIssuerName;
/*    */ import sun.security.x509.CertificateSerialNumber;
/*    */ import sun.security.x509.CertificateSubjectName;
/*    */ import sun.security.x509.CertificateValidity;
/*    */ import sun.security.x509.CertificateVersion;
/*    */ import sun.security.x509.CertificateX509Key;
/*    */ import sun.security.x509.X500Name;
/*    */ import sun.security.x509.X509CertImpl;
/*    */ import sun.security.x509.X509CertInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class OpenJdkSelfSignedCertGenerator
/*    */ {
/*    */   @SuppressJava6Requirement(reason = "Usage guarded by dependency check")
/*    */   static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter) throws Exception {
/* 49 */     PrivateKey key = keypair.getPrivate();
/*    */ 
/*    */     
/* 52 */     X509CertInfo info = new X509CertInfo();
/* 53 */     X500Name owner = new X500Name("CN=" + fqdn);
/* 54 */     info.set("version", new CertificateVersion(2));
/* 55 */     info.set("serialNumber", new CertificateSerialNumber(new BigInteger(64, random)));
/*    */     try {
/* 57 */       info.set("subject", new CertificateSubjectName(owner));
/* 58 */     } catch (CertificateException ignore) {
/* 59 */       info.set("subject", owner);
/*    */     } 
/*    */     try {
/* 62 */       info.set("issuer", new CertificateIssuerName(owner));
/* 63 */     } catch (CertificateException ignore) {
/* 64 */       info.set("issuer", owner);
/*    */     } 
/* 66 */     info.set("validity", new CertificateValidity(notBefore, notAfter));
/* 67 */     info.set("key", new CertificateX509Key(keypair.getPublic()));
/* 68 */     info.set("algorithmID", new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha256WithRSAEncryption_oid)));
/*    */ 
/*    */ 
/*    */     
/* 72 */     X509CertImpl cert = new X509CertImpl(info);
/* 73 */     cert.sign(key, "SHA256withRSA");
/*    */ 
/*    */     
/* 76 */     info.set("algorithmID.algorithm", cert.get("x509.algorithm"));
/* 77 */     cert = new X509CertImpl(info);
/* 78 */     cert.sign(key, "SHA256withRSA");
/* 79 */     cert.verify(keypair.getPublic());
/*    */     
/* 81 */     return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\OpenJdkSelfSignedCertGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */