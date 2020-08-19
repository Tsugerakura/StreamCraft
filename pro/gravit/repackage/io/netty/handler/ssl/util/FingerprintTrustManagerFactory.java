/*     */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*     */ 
/*     */ import java.security.KeyStore;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.net.ssl.ManagerFactoryParameters;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public final class FingerprintTrustManagerFactory
/*     */   extends SimpleTrustManagerFactory
/*     */ {
/*  76 */   private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
/*  77 */   private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
/*     */   private static final int SHA1_BYTE_LEN = 20;
/*     */   private static final int SHA1_HEX_LEN = 40;
/*     */   
/*  81 */   private static final FastThreadLocal<MessageDigest> tlmd = new FastThreadLocal<MessageDigest>()
/*     */     {
/*     */       protected MessageDigest initialValue() {
/*     */         try {
/*  85 */           return MessageDigest.getInstance("SHA1");
/*  86 */         } catch (NoSuchAlgorithmException e) {
/*     */           
/*  88 */           throw new Error(e);
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*  93 */   private final TrustManager tm = new X509TrustManager()
/*     */     {
/*     */       public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException
/*     */       {
/*  97 */         checkTrusted("client", chain);
/*     */       }
/*     */ 
/*     */       
/*     */       public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
/* 102 */         checkTrusted("server", chain);
/*     */       }
/*     */       
/*     */       private void checkTrusted(String type, X509Certificate[] chain) throws CertificateException {
/* 106 */         X509Certificate cert = chain[0];
/* 107 */         byte[] fingerprint = fingerprint(cert);
/* 108 */         boolean found = false;
/* 109 */         for (byte[] allowedFingerprint : FingerprintTrustManagerFactory.this.fingerprints) {
/* 110 */           if (Arrays.equals(fingerprint, allowedFingerprint)) {
/* 111 */             found = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 116 */         if (!found) {
/* 117 */           throw new CertificateException(type + " certificate with unknown fingerprint: " + cert
/* 118 */               .getSubjectDN());
/*     */         }
/*     */       }
/*     */       
/*     */       private byte[] fingerprint(X509Certificate cert) throws CertificateEncodingException {
/* 123 */         MessageDigest md = (MessageDigest)FingerprintTrustManagerFactory.tlmd.get();
/* 124 */         md.reset();
/* 125 */         return md.digest(cert.getEncoded());
/*     */       }
/*     */ 
/*     */       
/*     */       public X509Certificate[] getAcceptedIssuers() {
/* 130 */         return EmptyArrays.EMPTY_X509_CERTIFICATES;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[][] fingerprints;
/*     */ 
/*     */ 
/*     */   
/*     */   public FingerprintTrustManagerFactory(Iterable<String> fingerprints) {
/* 142 */     this(toFingerprintArray(fingerprints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FingerprintTrustManagerFactory(String... fingerprints) {
/* 151 */     this(toFingerprintArray(Arrays.asList(fingerprints)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FingerprintTrustManagerFactory(byte[]... fingerprints) {
/* 160 */     ObjectUtil.checkNotNull(fingerprints, "fingerprints");
/*     */     
/* 162 */     List<byte[]> list = (List)new ArrayList<byte>(fingerprints.length);
/* 163 */     for (byte[] f : fingerprints) {
/* 164 */       if (f == null) {
/*     */         break;
/*     */       }
/* 167 */       if (f.length != 20) {
/* 168 */         throw new IllegalArgumentException("malformed fingerprint: " + 
/* 169 */             ByteBufUtil.hexDump(Unpooled.wrappedBuffer(f)) + " (expected: SHA1)");
/*     */       }
/* 171 */       list.add(f.clone());
/*     */     } 
/*     */     
/* 174 */     this.fingerprints = list.<byte[]>toArray(new byte[0][]);
/*     */   }
/*     */   
/*     */   private static byte[][] toFingerprintArray(Iterable<String> fingerprints) {
/* 178 */     ObjectUtil.checkNotNull(fingerprints, "fingerprints");
/*     */     
/* 180 */     List<byte[]> list = (List)new ArrayList<byte>();
/* 181 */     for (String f : fingerprints) {
/* 182 */       if (f == null) {
/*     */         break;
/*     */       }
/*     */       
/* 186 */       if (!FINGERPRINT_PATTERN.matcher(f).matches()) {
/* 187 */         throw new IllegalArgumentException("malformed fingerprint: " + f);
/*     */       }
/* 189 */       f = FINGERPRINT_STRIP_PATTERN.matcher(f).replaceAll("");
/* 190 */       if (f.length() != 40) {
/* 191 */         throw new IllegalArgumentException("malformed fingerprint: " + f + " (expected: SHA1)");
/*     */       }
/*     */       
/* 194 */       list.add(StringUtil.decodeHexDump(f));
/*     */     } 
/*     */     
/* 197 */     return list.<byte[]>toArray(new byte[0][]);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void engineInit(KeyStore keyStore) throws Exception {}
/*     */ 
/*     */   
/*     */   protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {}
/*     */ 
/*     */   
/*     */   protected TrustManager[] engineGetTrustManagers() {
/* 208 */     return new TrustManager[] { this.tm };
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\FingerprintTrustManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */