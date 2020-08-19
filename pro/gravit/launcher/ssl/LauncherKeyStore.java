/*    */ package pro.gravit.launcher.ssl;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.security.KeyStore;
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.cert.CertificateException;
/*    */ 
/*    */ public class LauncherKeyStore {
/*    */   public static KeyStore getKeyStore(String keystore, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
/* 13 */     KeyStore ks = KeyStore.getInstance("JKS");
/* 14 */     try (InputStream ksIs = new FileInputStream(keystore)) {
/* 15 */       ks.load(ksIs, password.toCharArray());
/*    */     } 
/* 17 */     return ks;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ssl\LauncherKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */