/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ 
/*    */ 
/*    */ public class SetProfileRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 12 */   private static final UUID uuid = UUID.fromString("08c0de9e-4364-4152-9066-8354a3a48541");
/*    */   @LauncherNetworkAPI
/*    */   public ClientProfile newProfile;
/*    */   
/*    */   public SetProfileRequestEvent(ClientProfile newProfile) {
/* 17 */     this.newProfile = newProfile;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 22 */     return "setProfile";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\SetProfileRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */