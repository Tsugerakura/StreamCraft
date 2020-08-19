/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class ExecCommandRequestEvent
/*    */   extends RequestEvent {
/*    */   public String getType() {
/*  9 */     return "cmdExec";
/*    */   }
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public boolean success;
/*    */   
/*    */   public ExecCommandRequestEvent(boolean success) {
/* 16 */     this.success = success;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\ExecCommandRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */