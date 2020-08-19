/*    */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.KeyPair;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.Provider;
/*    */ import java.security.SecureRandom;
/*    */ import java.security.cert.X509Certificate;
/*    */ import java.util.Date;
/*    */ import org.bouncycastle.asn1.x500.X500Name;
/*    */ import org.bouncycastle.cert.X509CertificateHolder;
/*    */ import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
/*    */ import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
/*    */ import org.bouncycastle.jce.provider.BouncyCastleProvider;
/*    */ import org.bouncycastle.operator.ContentSigner;
/*    */ import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
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
/*    */ final class BouncyCastleSelfSignedCertGenerator
/*    */ {
/* 43 */   private static final Provider PROVIDER = (Provider)new BouncyCastleProvider();
/*    */ 
/*    */   
/*    */   static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter) throws Exception {
/* 47 */     PrivateKey key = keypair.getPrivate();
/*    */ 
/*    */     
/* 50 */     X500Name owner = new X500Name("CN=" + fqdn);
/*    */     
/* 52 */     JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(owner, new BigInteger(64, random), notBefore, notAfter, owner, keypair.getPublic());
/*    */     
/* 54 */     ContentSigner signer = (new JcaContentSignerBuilder("SHA256WithRSAEncryption")).build(key);
/* 55 */     X509CertificateHolder certHolder = jcaX509v3CertificateBuilder.build(signer);
/* 56 */     X509Certificate cert = (new JcaX509CertificateConverter()).setProvider(PROVIDER).getCertificate(certHolder);
/* 57 */     cert.verify(keypair.getPublic());
/*    */     
/* 59 */     return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\BouncyCastleSelfSignedCertGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */