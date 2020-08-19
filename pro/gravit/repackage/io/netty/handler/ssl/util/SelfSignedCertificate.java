/*     */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.base64.Base64;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ThrowableUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ public final class SelfSignedCertificate
/*     */ {
/*  62 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
/*     */ 
/*     */   
/*  65 */   private static final Date DEFAULT_NOT_BEFORE = new Date(SystemPropertyUtil.getLong("pro.gravit.repackage.io.netty.selfSignedCertificate.defaultNotBefore", 
/*  66 */         System.currentTimeMillis() - 31536000000L));
/*     */   
/*  68 */   private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("pro.gravit.repackage.io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   private static final int DEFAULT_KEY_LENGTH_BITS = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.handler.ssl.util.selfSignedKeyStrength", 2048);
/*     */   
/*     */   private final File certificate;
/*     */   
/*     */   private final File privateKey;
/*     */   
/*     */   private final X509Certificate cert;
/*     */   
/*     */   private final PrivateKey key;
/*     */   
/*     */   public SelfSignedCertificate() throws CertificateException {
/*  88 */     this(DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
/*  97 */     this("example.com", notBefore, notAfter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelfSignedCertificate(String fqdn) throws CertificateException {
/* 106 */     this(fqdn, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
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
/*     */   public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
/* 119 */     this(fqdn, ThreadLocalInsecureRandom.current(), DEFAULT_KEY_LENGTH_BITS, notBefore, notAfter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
/* 130 */     this(fqdn, random, bits, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
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
/*     */   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
/*     */     KeyPair keypair;
/*     */     String[] paths;
/*     */     try {
/* 147 */       KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
/* 148 */       keyGen.initialize(bits, random);
/* 149 */       keypair = keyGen.generateKeyPair();
/* 150 */     } catch (NoSuchAlgorithmException e) {
/*     */       
/* 152 */       throw new Error(e);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 158 */       paths = OpenJdkSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
/* 159 */     } catch (Throwable t) {
/* 160 */       logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", t);
/*     */       
/*     */       try {
/* 163 */         paths = BouncyCastleSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
/* 164 */       } catch (Throwable t2) {
/* 165 */         logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", t2);
/* 166 */         CertificateException certificateException = new CertificateException("No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", t2);
/*     */ 
/*     */         
/* 169 */         ThrowableUtil.addSuppressed(certificateException, t);
/* 170 */         throw certificateException;
/*     */       } 
/*     */     } 
/*     */     
/* 174 */     this.certificate = new File(paths[0]);
/* 175 */     this.privateKey = new File(paths[1]);
/* 176 */     this.key = keypair.getPrivate();
/* 177 */     FileInputStream certificateInput = null;
/*     */     try {
/* 179 */       certificateInput = new FileInputStream(this.certificate);
/* 180 */       this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(certificateInput);
/* 181 */     } catch (Exception e) {
/* 182 */       throw new CertificateEncodingException(e);
/*     */     } finally {
/* 184 */       if (certificateInput != null) {
/*     */         try {
/* 186 */           certificateInput.close();
/* 187 */         } catch (IOException e) {
/* 188 */           if (logger.isWarnEnabled()) {
/* 189 */             logger.warn("Failed to close a file: " + this.certificate, e);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File certificate() {
/* 200 */     return this.certificate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File privateKey() {
/* 207 */     return this.privateKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public X509Certificate cert() {
/* 214 */     return this.cert;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrivateKey key() {
/* 221 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void delete() {
/* 228 */     safeDelete(this.certificate);
/* 229 */     safeDelete(this.privateKey);
/*     */   }
/*     */ 
/*     */   
/*     */   static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
/*     */     String keyText, certText;
/* 235 */     ByteBuf wrappedBuf = Unpooled.wrappedBuffer(key.getEncoded());
/*     */ 
/*     */     
/*     */     try {
/* 239 */       ByteBuf encodedBuf = Base64.encode(wrappedBuf, true);
/*     */       
/*     */       try {
/* 242 */         keyText = "-----BEGIN PRIVATE KEY-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
/*     */       } finally {
/*     */         
/* 245 */         encodedBuf.release();
/*     */       } 
/*     */     } finally {
/* 248 */       wrappedBuf.release();
/*     */     } 
/*     */     
/* 251 */     File keyFile = File.createTempFile("keyutil_" + fqdn + '_', ".key");
/* 252 */     keyFile.deleteOnExit();
/*     */     
/* 254 */     OutputStream keyOut = new FileOutputStream(keyFile);
/*     */     try {
/* 256 */       keyOut.write(keyText.getBytes(CharsetUtil.US_ASCII));
/* 257 */       keyOut.close();
/* 258 */       keyOut = null;
/*     */     } finally {
/* 260 */       if (keyOut != null) {
/* 261 */         safeClose(keyFile, keyOut);
/* 262 */         safeDelete(keyFile);
/*     */       } 
/*     */     } 
/*     */     
/* 266 */     wrappedBuf = Unpooled.wrappedBuffer(cert.getEncoded());
/*     */     
/*     */     try {
/* 269 */       ByteBuf encodedBuf = Base64.encode(wrappedBuf, true);
/*     */ 
/*     */       
/*     */       try {
/* 273 */         certText = "-----BEGIN CERTIFICATE-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
/*     */       } finally {
/*     */         
/* 276 */         encodedBuf.release();
/*     */       } 
/*     */     } finally {
/* 279 */       wrappedBuf.release();
/*     */     } 
/*     */     
/* 282 */     File certFile = File.createTempFile("keyutil_" + fqdn + '_', ".crt");
/* 283 */     certFile.deleteOnExit();
/*     */     
/* 285 */     OutputStream certOut = new FileOutputStream(certFile);
/*     */     try {
/* 287 */       certOut.write(certText.getBytes(CharsetUtil.US_ASCII));
/* 288 */       certOut.close();
/* 289 */       certOut = null;
/*     */     } finally {
/* 291 */       if (certOut != null) {
/* 292 */         safeClose(certFile, certOut);
/* 293 */         safeDelete(certFile);
/* 294 */         safeDelete(keyFile);
/*     */       } 
/*     */     } 
/*     */     
/* 298 */     return new String[] { certFile.getPath(), keyFile.getPath() };
/*     */   }
/*     */   
/*     */   private static void safeDelete(File certFile) {
/* 302 */     if (!certFile.delete() && 
/* 303 */       logger.isWarnEnabled()) {
/* 304 */       logger.warn("Failed to delete a file: " + certFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void safeClose(File keyFile, OutputStream keyOut) {
/*     */     try {
/* 311 */       keyOut.close();
/* 312 */     } catch (IOException e) {
/* 313 */       if (logger.isWarnEnabled())
/* 314 */         logger.warn("Failed to close a file: " + keyFile, e); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\SelfSignedCertificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */