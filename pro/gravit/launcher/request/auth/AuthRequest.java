/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.AuthRequestEvent;
/*    */ import pro.gravit.launcher.hwid.HWID;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class AuthRequest extends Request<AuthRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final String login;
/*    */   @LauncherNetworkAPI
/*    */   private final byte[] encryptedPassword;
/*    */   @LauncherNetworkAPI
/*    */   private final String auth_id;
/*    */   @LauncherNetworkAPI
/*    */   private final HWID hwid;
/*    */   @LauncherNetworkAPI
/*    */   private final String customText;
/*    */   @LauncherNetworkAPI
/*    */   private final boolean getSession;
/*    */   @LauncherNetworkAPI
/*    */   private final ConnectTypes authType;
/*    */   @LauncherNetworkAPI
/*    */   public boolean initProxy;
/*    */   @LauncherNetworkAPI
/*    */   public String password;
/*    */   
/*    */   public enum ConnectTypes {
/* 32 */     SERVER,
/*    */     
/* 34 */     CLIENT,
/*    */     
/* 36 */     BOT,
/*    */     
/* 38 */     PROXY;
/*    */   }
/*    */ 
/*    */   
/*    */   @LauncherAPI
/*    */   public AuthRequest(String login, byte[] password, HWID hwid) {
/* 44 */     this.login = (String)VerifyHelper.verify(login, VerifyHelper.NOT_EMPTY, "Login can't be empty");
/* 45 */     this.encryptedPassword = (byte[])password.clone();
/* 46 */     this.hwid = hwid;
/* 47 */     this.customText = "";
/* 48 */     this.auth_id = "";
/* 49 */     this.getSession = true;
/* 50 */     this.authType = ConnectTypes.CLIENT;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public AuthRequest(String login, byte[] password, HWID hwid, String auth_id) {
/* 55 */     this.login = (String)VerifyHelper.verify(login, VerifyHelper.NOT_EMPTY, "Login can't be empty");
/* 56 */     this.encryptedPassword = (byte[])password.clone();
/* 57 */     this.hwid = hwid;
/* 58 */     this.auth_id = auth_id;
/* 59 */     this.customText = "";
/* 60 */     this.getSession = true;
/* 61 */     this.authType = ConnectTypes.CLIENT;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public AuthRequest(String login, byte[] password, HWID hwid, String customText, String auth_id) {
/* 66 */     this.login = (String)VerifyHelper.verify(login, VerifyHelper.NOT_EMPTY, "Login can't be empty");
/* 67 */     this.encryptedPassword = (byte[])password.clone();
/* 68 */     this.hwid = hwid;
/* 69 */     this.auth_id = auth_id;
/* 70 */     this.customText = customText;
/* 71 */     this.getSession = true;
/* 72 */     this.authType = ConnectTypes.CLIENT;
/*    */   }
/*    */   
/*    */   public AuthRequest(String login, byte[] encryptedPassword, String auth_id, ConnectTypes authType) {
/* 76 */     this.login = login;
/* 77 */     this.encryptedPassword = encryptedPassword;
/* 78 */     this.auth_id = auth_id;
/* 79 */     this.authType = authType;
/* 80 */     this.hwid = null;
/* 81 */     this.customText = "";
/* 82 */     this.getSession = false;
/*    */   }
/*    */   
/*    */   public AuthRequest(String login, String password, String auth_id, ConnectTypes authType) {
/* 86 */     this.login = login;
/* 87 */     this.password = password;
/* 88 */     this.encryptedPassword = null;
/* 89 */     this.auth_id = auth_id;
/* 90 */     this.authType = authType;
/* 91 */     this.hwid = null;
/* 92 */     this.customText = "";
/* 93 */     this.getSession = false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 98 */     return "auth";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\AuthRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */