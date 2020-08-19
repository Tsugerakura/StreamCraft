/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ 
/*    */ public class UpdateListRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 12 */   private static final UUID uuid = UUID.fromString("5fa836ae-6b61-401c-96ac-d8396f07ec6b");
/*    */   @LauncherNetworkAPI
/*    */   public final HashSet<String> dirs;
/*    */   
/*    */   public UpdateListRequestEvent(HashSet<String> dirs) {
/* 17 */     this.dirs = dirs;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 22 */     return "updateList";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\UpdateListRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */