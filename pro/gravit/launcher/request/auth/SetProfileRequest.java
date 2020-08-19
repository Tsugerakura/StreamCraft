/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.SetProfileRequestEvent;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class SetProfileRequest extends Request<SetProfileRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   public String client;
/*    */   
/*    */   public SetProfileRequest(ClientProfile profile) {
/* 14 */     this.client = profile.getTitle();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 19 */     return "setProfile";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\SetProfileRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */