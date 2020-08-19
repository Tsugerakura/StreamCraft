/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.Principal;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public final class PemX509Certificate
/*     */   extends X509Certificate
/*     */   implements PemEncoded
/*     */ {
/*  49 */   private static final byte[] BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
/*  50 */   private static final byte[] END_CERT = "\n-----END CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
/*     */ 
/*     */   
/*     */   private final ByteBuf content;
/*     */ 
/*     */ 
/*     */   
/*     */   static PemEncoded toPEM(ByteBufAllocator allocator, boolean useDirect, X509Certificate... chain) throws CertificateEncodingException {
/*  58 */     if (chain == null || chain.length == 0) {
/*  59 */       throw new IllegalArgumentException("X.509 certificate chain can't be null or empty");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     if (chain.length == 1) {
/*  68 */       X509Certificate first = chain[0];
/*  69 */       if (first instanceof PemEncoded) {
/*  70 */         return ((PemEncoded)first).retain();
/*     */       }
/*     */     } 
/*     */     
/*  74 */     boolean success = false;
/*  75 */     ByteBuf pem = null;
/*     */     try {
/*  77 */       for (X509Certificate cert : chain) {
/*     */         
/*  79 */         if (cert == null) {
/*  80 */           throw new IllegalArgumentException("Null element in chain: " + Arrays.toString(chain));
/*     */         }
/*     */         
/*  83 */         if (cert instanceof PemEncoded) {
/*  84 */           pem = append(allocator, useDirect, (PemEncoded)cert, chain.length, pem);
/*     */         } else {
/*  86 */           pem = append(allocator, useDirect, cert, chain.length, pem);
/*     */         } 
/*     */       } 
/*     */       
/*  90 */       PemValue value = new PemValue(pem, false);
/*  91 */       success = true;
/*  92 */       return value;
/*     */     } finally {
/*     */       
/*  95 */       if (!success && pem != null) {
/*  96 */         pem.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ByteBuf append(ByteBufAllocator allocator, boolean useDirect, PemEncoded encoded, int count, ByteBuf pem) {
/* 108 */     ByteBuf content = encoded.content();
/*     */     
/* 110 */     if (pem == null)
/*     */     {
/* 112 */       pem = newBuffer(allocator, useDirect, content.readableBytes() * count);
/*     */     }
/*     */     
/* 115 */     pem.writeBytes(content.slice());
/* 116 */     return pem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ByteBuf append(ByteBufAllocator allocator, boolean useDirect, X509Certificate cert, int count, ByteBuf pem) throws CertificateEncodingException {
/* 126 */     ByteBuf encoded = Unpooled.wrappedBuffer(cert.getEncoded());
/*     */     try {
/* 128 */       ByteBuf base64 = SslUtils.toBase64(allocator, encoded);
/*     */       try {
/* 130 */         if (pem == null)
/*     */         {
/*     */ 
/*     */           
/* 134 */           pem = newBuffer(allocator, useDirect, (BEGIN_CERT.length + base64
/* 135 */               .readableBytes() + END_CERT.length) * count);
/*     */         }
/*     */         
/* 138 */         pem.writeBytes(BEGIN_CERT);
/* 139 */         pem.writeBytes(base64);
/* 140 */         pem.writeBytes(END_CERT);
/*     */       } finally {
/* 142 */         base64.release();
/*     */       } 
/*     */     } finally {
/* 145 */       encoded.release();
/*     */     } 
/*     */     
/* 148 */     return pem;
/*     */   }
/*     */   
/*     */   private static ByteBuf newBuffer(ByteBufAllocator allocator, boolean useDirect, int initialCapacity) {
/* 152 */     return useDirect ? allocator.directBuffer(initialCapacity) : allocator.buffer(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PemX509Certificate valueOf(byte[] key) {
/* 162 */     return valueOf(Unpooled.wrappedBuffer(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PemX509Certificate valueOf(ByteBuf key) {
/* 172 */     return new PemX509Certificate(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private PemX509Certificate(ByteBuf content) {
/* 178 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSensitive() {
/* 184 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/* 189 */     return this.content.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/* 194 */     int count = refCnt();
/* 195 */     if (count <= 0) {
/* 196 */       throw new IllegalReferenceCountException(count);
/*     */     }
/*     */     
/* 199 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate copy() {
/* 204 */     return replace(this.content.copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate duplicate() {
/* 209 */     return replace(this.content.duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate retainedDuplicate() {
/* 214 */     return replace(this.content.retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate replace(ByteBuf content) {
/* 219 */     return new PemX509Certificate(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate retain() {
/* 224 */     this.content.retain();
/* 225 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate retain(int increment) {
/* 230 */     this.content.retain(increment);
/* 231 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate touch() {
/* 236 */     this.content.touch();
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PemX509Certificate touch(Object hint) {
/* 242 */     this.content.touch(hint);
/* 243 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 248 */     return this.content.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 253 */     return this.content.release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getEncoded() {
/* 258 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasUnsupportedCriticalExtension() {
/* 263 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getCriticalExtensionOIDs() {
/* 268 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getNonCriticalExtensionOIDs() {
/* 273 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getExtensionValue(String oid) {
/* 278 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity() {
/* 283 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkValidity(Date date) {
/* 288 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 293 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getSerialNumber() {
/* 298 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getIssuerDN() {
/* 303 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getSubjectDN() {
/* 308 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotBefore() {
/* 313 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getNotAfter() {
/* 318 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getTBSCertificate() {
/* 323 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSignature() {
/* 328 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgName() {
/* 333 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSigAlgOID() {
/* 338 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSigAlgParams() {
/* 343 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getIssuerUniqueID() {
/* 348 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getSubjectUniqueID() {
/* 353 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] getKeyUsage() {
/* 358 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBasicConstraints() {
/* 363 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key) {
/* 368 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void verify(PublicKey key, String sigProvider) {
/* 373 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public PublicKey getPublicKey() {
/* 378 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 383 */     if (o == this)
/* 384 */       return true; 
/* 385 */     if (!(o instanceof PemX509Certificate)) {
/* 386 */       return false;
/*     */     }
/*     */     
/* 389 */     PemX509Certificate other = (PemX509Certificate)o;
/* 390 */     return this.content.equals(other.content);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 395 */     return this.content.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 400 */     return this.content.toString(CharsetUtil.UTF_8);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\PemX509Certificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */