/*    */ package pro.gravit.launcher.events;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ 
/*    */ public class SignalEvent
/*    */ {
/*    */   @LauncherNetworkAPI
/*    */   public int signal;
/*    */   
/*    */   public SignalEvent(int signal) {
/* 11 */     this.signal = signal;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\SignalEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */