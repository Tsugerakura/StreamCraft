/*    */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*    */ 
/*    */ import java.net.Socket;
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import javax.net.ssl.X509ExtendedTrustManager;
/*    */ import javax.net.ssl.X509TrustManager;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*    */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*    */ final class X509TrustManagerWrapper
/*    */   extends X509ExtendedTrustManager
/*    */ {
/*    */   private final X509TrustManager delegate;
/*    */   
/*    */   X509TrustManagerWrapper(X509TrustManager delegate) {
/* 35 */     this.delegate = (X509TrustManager)ObjectUtil.checkNotNull(delegate, "delegate");
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException {
/* 40 */     this.delegate.checkClientTrusted(chain, s);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] chain, String s, Socket socket) throws CertificateException {
/* 46 */     this.delegate.checkClientTrusted(chain, s);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] chain, String s, SSLEngine sslEngine) throws CertificateException {
/* 52 */     this.delegate.checkClientTrusted(chain, s);
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
/* 57 */     this.delegate.checkServerTrusted(chain, s);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] chain, String s, Socket socket) throws CertificateException {
/* 63 */     this.delegate.checkServerTrusted(chain, s);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] chain, String s, SSLEngine sslEngine) throws CertificateException {
/* 69 */     this.delegate.checkServerTrusted(chain, s);
/*    */   }
/*    */ 
/*    */   
/*    */   public X509Certificate[] getAcceptedIssuers() {
/* 74 */     return this.delegate.getAcceptedIssuers();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\X509TrustManagerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */