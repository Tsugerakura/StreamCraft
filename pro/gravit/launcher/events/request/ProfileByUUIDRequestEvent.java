/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ 
/*    */ public class ProfileByUUIDRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 12 */   private static final UUID uuid = UUID.fromString("b9014cf3-4b95-4d38-8c5f-867f190a18a0");
/*    */   @LauncherNetworkAPI
/*    */   public String error;
/*    */   @LauncherNetworkAPI
/*    */   public PlayerProfile playerProfile;
/*    */   
/*    */   public ProfileByUUIDRequestEvent(PlayerProfile playerProfile) {
/* 19 */     this.playerProfile = playerProfile;
/*    */   }
/*    */ 
/*    */   
/*    */   public ProfileByUUIDRequestEvent() {}
/*    */ 
/*    */   
/*    */   public String getType() {
/* 27 */     return "profileByUUID";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ProfileByUUIDRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */