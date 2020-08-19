/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.RegisterRequestEvent;
/*    */ import pro.gravit.launcher.hwid.HWID;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RegisterRequest
/*    */   extends Request<RegisterRequestEvent>
/*    */   implements WebSocketRequest
/*    */ {
/*    */   @LauncherNetworkAPI
/*    */   private final boolean first;
/*    */   @LauncherNetworkAPI
/*    */   private final String login;
/*    */   @LauncherNetworkAPI
/*    */   private final String email;
/*    */   @LauncherNetworkAPI
/*    */   private final String password;
/*    */   @LauncherNetworkAPI
/*    */   private final String captcha;
/*    */   @LauncherNetworkAPI
/*    */   private final HWID hwid;
/*    */   
/*    */   @LauncherAPI
/*    */   public RegisterRequest(boolean first, String login, String email, String password, String captcha, HWID hwid) {
/* 33 */     this.first = first;
/* 34 */     this.login = first ? "" : (String)VerifyHelper.verify(login, VerifyHelper.NOT_EMPTY, "Login can't be empty");
/* 35 */     this.email = first ? "" : (String)VerifyHelper.verify(email, VerifyHelper.NOT_EMPTY, "Email can't be empty");
/* 36 */     this.password = first ? "" : (String)VerifyHelper.verify(password, VerifyHelper.NOT_EMPTY, "Password can't be empty");
/* 37 */     this.captcha = first ? "" : (String)VerifyHelper.verify(captcha, VerifyHelper.NOT_EMPTY, "Captcha can't be empty");
/* 38 */     this.hwid = hwid;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getType() {
/* 44 */     return "register";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\RegisterRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */