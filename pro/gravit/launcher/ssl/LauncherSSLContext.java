/*    */ package pro.gravit.launcher.ssl;
/*    */ 
/*    */ import java.security.KeyManagementException;
/*    */ import java.security.KeyStore;
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.SecureRandom;
/*    */ import java.security.UnrecoverableKeyException;
/*    */ import javax.net.ssl.KeyManagerFactory;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import javax.net.ssl.SSLServerSocketFactory;
/*    */ import javax.net.ssl.SSLSocketFactory;
/*    */ import javax.net.ssl.TrustManager;
/*    */ 
/*    */ public class LauncherSSLContext
/*    */ {
/*    */   public SSLServerSocketFactory ssf;
/*    */   public SSLSocketFactory sf;
/*    */   
/*    */   public LauncherSSLContext(KeyStore ks, String keypassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
/* 21 */     TrustManager[] trustAllCerts = { new LauncherTrustManager() };
/*    */ 
/*    */     
/* 24 */     KeyManagerFactory kmf = KeyManagerFactory.getInstance(
/* 25 */         KeyManagerFactory.getDefaultAlgorithm());
/* 26 */     kmf.init(ks, keypassword.toCharArray());
/* 27 */     SSLContext sc = SSLContext.getInstance("TLSv1.2");
/* 28 */     sc.init(kmf.getKeyManagers(), trustAllCerts, new SecureRandom());
/* 29 */     this.ssf = sc.getServerSocketFactory();
/* 30 */     this.sf = sc.getSocketFactory();
/*    */   }
/*    */   
/*    */   public LauncherSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
/* 34 */     TrustManager[] trustAllCerts = { new LauncherTrustManager() };
/*    */ 
/*    */     
/* 37 */     SSLContext sc = SSLContext.getInstance("TLSv1.2");
/* 38 */     sc.init(null, trustAllCerts, new SecureRandom());
/* 39 */     this.ssf = null;
/* 40 */     this.sf = sc.getSocketFactory();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ssl\LauncherSSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */