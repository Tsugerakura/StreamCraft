/*    */ package pro.gravit.launcher.request.uuid;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.ProfileByUsernameRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class ProfileByUsernameRequest extends Request<ProfileByUsernameRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final String username;
/*    */   
/*    */   @LauncherAPI
/*    */   public ProfileByUsernameRequest(String username) {
/* 16 */     this.username = VerifyHelper.verifyUsername(username);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 21 */     return "profileByUsername";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\uuid\ProfileByUsernameRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */