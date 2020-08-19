/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.net.Socket;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.X509ExtendedTrustManager;
/*     */ import javax.security.cert.X509Certificate;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ final class OpenSslTlsv13X509ExtendedTrustManager
/*     */   extends X509ExtendedTrustManager
/*     */ {
/*     */   private final X509ExtendedTrustManager tm;
/*     */   
/*     */   private OpenSslTlsv13X509ExtendedTrustManager(X509ExtendedTrustManager tm) {
/*  45 */     this.tm = tm;
/*     */   }
/*     */   
/*     */   static X509ExtendedTrustManager wrap(X509ExtendedTrustManager tm) {
/*  49 */     if (PlatformDependent.javaVersion() < 11 && OpenSsl.isTlsv13Supported()) {
/*  50 */       return new OpenSslTlsv13X509ExtendedTrustManager(tm);
/*     */     }
/*  52 */     return tm;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
/*  58 */     this.tm.checkClientTrusted(x509Certificates, s, socket);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
/*  64 */     this.tm.checkServerTrusted(x509Certificates, s, socket);
/*     */   }
/*     */   
/*     */   private static SSLEngine wrapEngine(final SSLEngine engine) {
/*  68 */     final SSLSession session = engine.getHandshakeSession();
/*  69 */     if (session != null && "TLSv1.3".equals(session.getProtocol())) {
/*  70 */       return new JdkSslEngine(engine)
/*     */         {
/*     */           public String getNegotiatedApplicationProtocol() {
/*  73 */             if (engine instanceof ApplicationProtocolAccessor) {
/*  74 */               return ((ApplicationProtocolAccessor)engine).getNegotiatedApplicationProtocol();
/*     */             }
/*  76 */             return super.getNegotiatedApplicationProtocol();
/*     */           }
/*     */ 
/*     */           
/*     */           public SSLSession getHandshakeSession() {
/*  81 */             if (PlatformDependent.javaVersion() >= 7 && session instanceof ExtendedOpenSslSession) {
/*  82 */               final ExtendedOpenSslSession extendedOpenSslSession = (ExtendedOpenSslSession)session;
/*  83 */               return new ExtendedOpenSslSession(extendedOpenSslSession)
/*     */                 {
/*     */                   public List getRequestedServerNames() {
/*  86 */                     return extendedOpenSslSession.getRequestedServerNames();
/*     */                   }
/*     */ 
/*     */                   
/*     */                   public String[] getPeerSupportedSignatureAlgorithms() {
/*  91 */                     return extendedOpenSslSession.getPeerSupportedSignatureAlgorithms();
/*     */                   }
/*     */ 
/*     */                   
/*     */                   public String getProtocol() {
/*  96 */                     return "TLSv1.2";
/*     */                   }
/*     */                 };
/*     */             } 
/* 100 */             return new SSLSession()
/*     */               {
/*     */                 public byte[] getId() {
/* 103 */                   return session.getId();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public SSLSessionContext getSessionContext() {
/* 108 */                   return session.getSessionContext();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public long getCreationTime() {
/* 113 */                   return session.getCreationTime();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public long getLastAccessedTime() {
/* 118 */                   return session.getLastAccessedTime();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public void invalidate() {
/* 123 */                   session.invalidate();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public boolean isValid() {
/* 128 */                   return session.isValid();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public void putValue(String s, Object o) {
/* 133 */                   session.putValue(s, o);
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Object getValue(String s) {
/* 138 */                   return session.getValue(s);
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public void removeValue(String s) {
/* 143 */                   session.removeValue(s);
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String[] getValueNames() {
/* 148 */                   return session.getValueNames();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
/* 153 */                   return session.getPeerCertificates();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Certificate[] getLocalCertificates() {
/* 158 */                   return session.getLocalCertificates();
/*     */                 }
/*     */ 
/*     */ 
/*     */                 
/*     */                 public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
/* 164 */                   return session.getPeerCertificateChain();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
/* 169 */                   return session.getPeerPrincipal();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public Principal getLocalPrincipal() {
/* 174 */                   return session.getLocalPrincipal();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getCipherSuite() {
/* 179 */                   return session.getCipherSuite();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getProtocol() {
/* 184 */                   return "TLSv1.2";
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public String getPeerHost() {
/* 189 */                   return session.getPeerHost();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public int getPeerPort() {
/* 194 */                   return session.getPeerPort();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public int getPacketBufferSize() {
/* 199 */                   return session.getPacketBufferSize();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public int getApplicationBufferSize() {
/* 204 */                   return session.getApplicationBufferSize();
/*     */                 }
/*     */               };
/*     */           }
/*     */         };
/*     */     }
/*     */     
/* 211 */     return engine;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
/* 217 */     this.tm.checkClientTrusted(x509Certificates, s, wrapEngine(sslEngine));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
/* 223 */     this.tm.checkServerTrusted(x509Certificates, s, wrapEngine(sslEngine));
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
/* 228 */     this.tm.checkClientTrusted(x509Certificates, s);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
/* 233 */     this.tm.checkServerTrusted(x509Certificates, s);
/*     */   }
/*     */ 
/*     */   
/*     */   public X509Certificate[] getAcceptedIssuers() {
/* 238 */     return this.tm.getAcceptedIssuers();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslTlsv13X509ExtendedTrustManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */