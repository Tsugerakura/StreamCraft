/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.ExtendedSSLSession;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.security.cert.X509Certificate;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ abstract class ExtendedOpenSslSession
/*     */   extends ExtendedSSLSession
/*     */   implements OpenSslSession
/*     */ {
/*  40 */   private static final String[] LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS = new String[] { "SHA512withRSA", "SHA512withECDSA", "SHA384withRSA", "SHA384withECDSA", "SHA256withRSA", "SHA256withECDSA", "SHA224withRSA", "SHA224withECDSA", "SHA1withRSA", "SHA1withECDSA" };
/*     */ 
/*     */   
/*     */   private final OpenSslSession wrapped;
/*     */ 
/*     */ 
/*     */   
/*     */   ExtendedOpenSslSession(OpenSslSession wrapped) {
/*  48 */     this.wrapped = wrapped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract List getRequestedServerNames();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<byte[]> getStatusResponses() {
/*  60 */     return (List)Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void handshakeFinished() throws SSLException {
/*  65 */     this.wrapped.handshakeFinished();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
/*  70 */     this.wrapped.tryExpandApplicationBufferSize(packetLengthDataOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String[] getLocalSupportedSignatureAlgorithms() {
/*  75 */     return (String[])LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public final byte[] getId() {
/*  80 */     return this.wrapped.getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLSessionContext getSessionContext() {
/*  85 */     return this.wrapped.getSessionContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long getCreationTime() {
/*  90 */     return this.wrapped.getCreationTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long getLastAccessedTime() {
/*  95 */     return this.wrapped.getLastAccessedTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void invalidate() {
/* 100 */     this.wrapped.invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isValid() {
/* 105 */     return this.wrapped.isValid();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void putValue(String s, Object o) {
/* 110 */     this.wrapped.putValue(s, o);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object getValue(String s) {
/* 115 */     return this.wrapped.getValue(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void removeValue(String s) {
/* 120 */     this.wrapped.removeValue(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String[] getValueNames() {
/* 125 */     return this.wrapped.getValueNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
/* 130 */     return this.wrapped.getPeerCertificates();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Certificate[] getLocalCertificates() {
/* 135 */     return this.wrapped.getLocalCertificates();
/*     */   }
/*     */ 
/*     */   
/*     */   public final X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
/* 140 */     return this.wrapped.getPeerCertificateChain();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
/* 145 */     return this.wrapped.getPeerPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Principal getLocalPrincipal() {
/* 150 */     return this.wrapped.getLocalPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getCipherSuite() {
/* 155 */     return this.wrapped.getCipherSuite();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocol() {
/* 160 */     return this.wrapped.getProtocol();
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getPeerHost() {
/* 165 */     return this.wrapped.getPeerHost();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getPeerPort() {
/* 170 */     return this.wrapped.getPeerPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getPacketBufferSize() {
/* 175 */     return this.wrapped.getPacketBufferSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getApplicationBufferSize() {
/* 180 */     return this.wrapped.getApplicationBufferSize();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ExtendedOpenSslSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */