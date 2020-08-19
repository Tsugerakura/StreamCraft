/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.JoinServerRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class JoinServerRequest
/*    */   extends Request<JoinServerRequestEvent>
/*    */   implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final String username;
/*    */   @LauncherNetworkAPI
/*    */   private final String accessToken;
/*    */   @LauncherNetworkAPI
/*    */   private final String serverID;
/*    */   
/*    */   @LauncherAPI
/*    */   public JoinServerRequest(String username, String accessToken, String serverID) {
/* 23 */     this.username = VerifyHelper.verifyUsername(username);
/* 24 */     this.accessToken = SecurityHelper.verifyToken(accessToken);
/* 25 */     this.serverID = VerifyHelper.verifyServerID(serverID);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 30 */     return "joinServer";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\JoinServerRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */