/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class GetAvailabilityAuthRequestEvent
/*    */   extends RequestEvent {
/*    */   @LauncherNetworkAPI
/*    */   public List<AuthAvailability> list;
/*    */   @LauncherNetworkAPI
/*    */   public byte[] packet;
/*    */   
/*    */   public static class AuthAvailability {
/*    */     public AuthAvailability(String name, String displayName) {
/* 16 */       this.name = name;
/* 17 */       this.displayName = displayName;
/*    */     }
/*    */ 
/*    */     
/*    */     @LauncherNetworkAPI
/*    */     public String name;
/*    */     @LauncherNetworkAPI
/*    */     public String displayName;
/*    */   }
/*    */   
/*    */   public GetAvailabilityAuthRequestEvent(List<AuthAvailability> list, byte[] packet) {
/* 28 */     this.list = list;
/* 29 */     this.packet = packet;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getType() {
/* 35 */     return "getAvailabilityAuth";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\GetAvailabilityAuthRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */