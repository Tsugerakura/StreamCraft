/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class ErrorRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 10 */   public static UUID uuid = UUID.fromString("0af22bc7-aa01-4881-bdbb-dc62b3cdac96");
/*    */   
/*    */   public ErrorRequestEvent(String error) {
/* 13 */     this.error = error;
/*    */   }
/*    */ 
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public final String error;
/*    */   
/*    */   public String getType() {
/* 21 */     return "error";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ErrorRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */