/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.request.WebSocketEvent;
/*    */ 
/*    */ public class LogEvent
/*    */   implements WebSocketEvent {
/*    */   public String getType() {
/*  9 */     return "log";
/*    */   }
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public String string;
/*    */   
/*    */   public LogEvent(String string) {
/* 16 */     this.string = string;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\LogEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */