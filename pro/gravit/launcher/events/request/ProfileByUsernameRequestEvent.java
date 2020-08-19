/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ 
/*    */ public class ProfileByUsernameRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 12 */   private static final UUID uuid = UUID.fromString("06204302-ff6b-4779-b97d-541e3bc39aa1");
/*    */   @LauncherNetworkAPI
/*    */   public String error;
/*    */   @LauncherNetworkAPI
/*    */   public PlayerProfile playerProfile;
/*    */   
/*    */   public ProfileByUsernameRequestEvent(PlayerProfile playerProfile) {
/* 19 */     this.playerProfile = playerProfile;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 24 */     return "profileByUsername";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ProfileByUsernameRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */