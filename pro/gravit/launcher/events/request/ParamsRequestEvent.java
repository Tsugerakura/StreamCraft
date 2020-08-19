/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ 
/*    */ 
/*    */ public class ParamsRequestEvent
/*    */   extends RequestEvent
/*    */ {
/*    */   @LauncherNetworkAPI
/*    */   public ClientProfile profile;
/*    */   @LauncherNetworkAPI
/*    */   public byte[] data;
/*    */   @LauncherNetworkAPI
/*    */   public byte[] packet;
/*    */   
/*    */   public ParamsRequestEvent(ClientProfile profile, byte[] data) {
/* 19 */     this.profile = profile;
/* 20 */     this.data = data;
/*    */   }
/*    */   
/*    */   public ParamsRequestEvent(ClientProfile profile, byte[] data, byte[] packet) {
/* 24 */     this.profile = profile;
/* 25 */     this.data = data;
/* 26 */     this.packet = packet;
/*    */   }
/*    */ 
/*    */   
/*    */   public ParamsRequestEvent() {}
/*    */ 
/*    */   
/*    */   public String getType() {
/* 34 */     return "params";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ParamsRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */