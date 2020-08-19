/*    */ package pro.gravit.launcher.bridge;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ 
/*    */ @LauncherAPI
/*    */ public class GravitGuardBridge
/*    */ {
/*    */   @LauncherAPI
/*    */   public static native void callGuard();
/*    */   
/*    */   @LauncherAPI
/*    */   public static int sendHTTPRequest(String strurl) throws IOException {
/* 16 */     URL url = new URL(strurl);
/* 17 */     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/* 18 */     connection.setRequestMethod("GET");
/* 19 */     connection.setRequestProperty("Content-Language", "en-US");
/* 20 */     return connection.getResponseCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\bridge\GravitGuardBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */