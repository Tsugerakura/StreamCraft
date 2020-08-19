/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class JVMRequestEvent extends RequestEvent {
/*    */   @LauncherNetworkAPI
/*    */   private byte[] packet;
/*    */   
/*    */   public JVMRequestEvent(byte[] packet) {
/* 11 */     this.packet = packet;
/*    */   }
/*    */ 
/*    */   
/*    */   public JVMRequestEvent() {}
/*    */   
/*    */   public String getType() {
/* 18 */     return "jvm";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\JVMRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */