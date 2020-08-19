/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.KeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.base64.Base64;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
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
/*     */ final class PemReader
/*     */ {
/*  46 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
/*     */   
/*  48 */   private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static ByteBuf[] readCertificates(File file) throws CertificateException {
/*     */     try {
/*  61 */       InputStream in = new FileInputStream(file);
/*     */       
/*     */       try {
/*  64 */         return readCertificates(in);
/*     */       } finally {
/*  66 */         safeClose(in);
/*     */       } 
/*  68 */     } catch (FileNotFoundException e) {
/*  69 */       throw new CertificateException("could not find certificate file: " + file);
/*     */     } 
/*     */   }
/*     */   
/*     */   static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
/*     */     String content;
/*     */     try {
/*  76 */       content = readContent(in);
/*  77 */     } catch (IOException e) {
/*  78 */       throw new CertificateException("failed to read certificate input stream", e);
/*     */     } 
/*     */     
/*  81 */     List<ByteBuf> certs = new ArrayList<ByteBuf>();
/*  82 */     Matcher m = CERT_PATTERN.matcher(content);
/*  83 */     int start = 0;
/*     */     
/*  85 */     while (m.find(start)) {
/*     */ 
/*     */ 
/*     */       
/*  89 */       ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
/*  90 */       ByteBuf der = Base64.decode(base64);
/*  91 */       base64.release();
/*  92 */       certs.add(der);
/*     */       
/*  94 */       start = m.end();
/*     */     } 
/*     */     
/*  97 */     if (certs.isEmpty()) {
/*  98 */       throw new CertificateException("found no certificates in input stream");
/*     */     }
/*     */     
/* 101 */     return certs.<ByteBuf>toArray(new ByteBuf[0]);
/*     */   }
/*     */   
/*     */   static ByteBuf readPrivateKey(File file) throws KeyException {
/*     */     try {
/* 106 */       InputStream in = new FileInputStream(file);
/*     */       
/*     */       try {
/* 109 */         return readPrivateKey(in);
/*     */       } finally {
/* 111 */         safeClose(in);
/*     */       } 
/* 113 */     } catch (FileNotFoundException e) {
/* 114 */       throw new KeyException("could not find key file: " + file);
/*     */     } 
/*     */   }
/*     */   
/*     */   static ByteBuf readPrivateKey(InputStream in) throws KeyException {
/*     */     String content;
/*     */     try {
/* 121 */       content = readContent(in);
/* 122 */     } catch (IOException e) {
/* 123 */       throw new KeyException("failed to read key input stream", e);
/*     */     } 
/*     */     
/* 126 */     Matcher m = KEY_PATTERN.matcher(content);
/* 127 */     if (!m.find()) {
/* 128 */       throw new KeyException("could not find a PKCS #8 private key in input stream (see https://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
/*     */     }
/*     */ 
/*     */     
/* 132 */     ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
/* 133 */     ByteBuf der = Base64.decode(base64);
/* 134 */     base64.release();
/* 135 */     return der;
/*     */   }
/*     */   
/*     */   private static String readContent(InputStream in) throws IOException {
/* 139 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */     try {
/* 141 */       byte[] buf = new byte[8192];
/*     */       while (true) {
/* 143 */         int ret = in.read(buf);
/* 144 */         if (ret < 0) {
/*     */           break;
/*     */         }
/* 147 */         out.write(buf, 0, ret);
/*     */       } 
/* 149 */       return out.toString(CharsetUtil.US_ASCII.name());
/*     */     } finally {
/* 151 */       safeClose(out);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void safeClose(InputStream in) {
/*     */     try {
/* 157 */       in.close();
/* 158 */     } catch (IOException e) {
/* 159 */       logger.warn("Failed to close a stream.", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void safeClose(OutputStream out) {
/*     */     try {
/* 165 */       out.close();
/* 166 */     } catch (IOException e) {
/* 167 */       logger.warn("Failed to close a stream.", e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\PemReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */