/*    */ package pro.gravit.launcher.ssl;
/*    */ 
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.X509TrustManager;
/*    */ 
/*    */ public class LauncherTrustManager
/*    */   implements X509TrustManager {
/*    */   public void checkClientTrusted(X509Certificate[] certs, String authType) {}
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] certs, String authType) {}
/*    */   
/*    */   public X509Certificate[] getAcceptedIssuers() {
/* 13 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ssl\LauncherTrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */