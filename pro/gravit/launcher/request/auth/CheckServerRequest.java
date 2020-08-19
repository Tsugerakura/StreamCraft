/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.CheckServerRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class CheckServerRequest extends Request<CheckServerRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final String username;
/*    */   @LauncherNetworkAPI
/*    */   private final String serverID;
/*    */   
/*    */   @LauncherAPI
/*    */   public CheckServerRequest(String username, String serverID) {
/* 18 */     this.username = VerifyHelper.verifyUsername(username);
/* 19 */     this.serverID = VerifyHelper.verifyServerID(serverID);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 24 */     return "checkServer";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\CheckServerRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */