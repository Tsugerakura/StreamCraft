/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.ClientPermissions;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ public class AuthRequestEvent
/*    */   extends RequestEvent
/*    */ {
/*    */   @LauncherNetworkAPI
/*    */   public ClientPermissions permissions;
/*    */   @LauncherNetworkAPI
/*    */   public PlayerProfile playerProfile;
/*    */   @LauncherNetworkAPI
/*    */   public String accessToken;
/*    */   @LauncherNetworkAPI
/*    */   public String protectToken;
/*    */   @LauncherNetworkAPI
/*    */   public long session;
/*    */   
/*    */   public AuthRequestEvent() {}
/*    */   
/*    */   public AuthRequestEvent(PlayerProfile pp, String accessToken, ClientPermissions permissions) {
/* 25 */     this.playerProfile = pp;
/* 26 */     this.accessToken = accessToken;
/* 27 */     this.permissions = permissions;
/*    */   }
/*    */   
/*    */   public AuthRequestEvent(ClientPermissions permissions, PlayerProfile playerProfile, String accessToken, String protectToken) {
/* 31 */     this.permissions = permissions;
/* 32 */     this.playerProfile = playerProfile;
/* 33 */     this.accessToken = accessToken;
/* 34 */     this.protectToken = protectToken;
/*    */   }
/*    */   
/*    */   public AuthRequestEvent(ClientPermissions permissions, PlayerProfile playerProfile, String accessToken, String protectToken, long session) {
/* 38 */     this.permissions = permissions;
/* 39 */     this.playerProfile = playerProfile;
/* 40 */     this.accessToken = accessToken;
/* 41 */     this.protectToken = protectToken;
/* 42 */     this.session = session;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 47 */     return "auth";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\AuthRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */