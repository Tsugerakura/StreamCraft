/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ 
/*    */ public class JoinServerRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 11 */   private static final UUID uuid = UUID.fromString("2a12e7b5-3f4a-4891-a2f9-ea141c8e1995");
/*    */   
/*    */   public JoinServerRequestEvent(boolean allow) {
/* 14 */     this.allow = allow;
/*    */   }
/*    */ 
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public boolean allow;
/*    */   
/*    */   public String getType() {
/* 22 */     return "joinServer";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\JoinServerRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */