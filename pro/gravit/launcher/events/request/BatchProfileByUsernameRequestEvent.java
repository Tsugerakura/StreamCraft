/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ public class BatchProfileByUsernameRequestEvent extends RequestEvent {
/*    */   @LauncherNetworkAPI
/*    */   public String error;
/*    */   @LauncherNetworkAPI
/*    */   public PlayerProfile[] playerProfiles;
/*    */   
/*    */   public BatchProfileByUsernameRequestEvent(PlayerProfile[] profiles) {
/* 14 */     this.playerProfiles = profiles;
/*    */   }
/*    */ 
/*    */   
/*    */   public BatchProfileByUsernameRequestEvent() {}
/*    */ 
/*    */   
/*    */   public String getType() {
/* 22 */     return "batchProfileByUsername";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\BatchProfileByUsernameRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */