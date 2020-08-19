/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ 
/*    */ 
/*    */ public class CheckServerRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 12 */   private static final UUID _uuid = UUID.fromString("8801d07c-51ba-4059-b61d-fe1f1510b28a");
/*    */   @LauncherNetworkAPI
/*    */   public UUID uuid;
/*    */   @LauncherNetworkAPI
/*    */   public PlayerProfile playerProfile;
/*    */   
/*    */   public CheckServerRequestEvent(PlayerProfile playerProfile) {
/* 19 */     this.playerProfile = playerProfile;
/*    */   }
/*    */ 
/*    */   
/*    */   public CheckServerRequestEvent() {}
/*    */ 
/*    */   
/*    */   public String getType() {
/* 27 */     return "checkServer";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\CheckServerRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */