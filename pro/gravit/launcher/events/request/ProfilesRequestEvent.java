/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ 
/*    */ 
/*    */ public class ProfilesRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 13 */   private static final UUID uuid = UUID.fromString("2f26fbdf-598a-46dd-92fc-1699c0e173b1");
/*    */   @LauncherNetworkAPI
/*    */   public List<ClientProfile> profiles;
/*    */   
/*    */   public ProfilesRequestEvent(List<ClientProfile> profiles) {
/* 18 */     this.profiles = profiles;
/*    */   }
/*    */ 
/*    */   
/*    */   public ProfilesRequestEvent() {}
/*    */ 
/*    */   
/*    */   public String getType() {
/* 26 */     return "profiles";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ProfilesRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */