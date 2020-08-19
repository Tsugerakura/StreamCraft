/*    */ package pro.gravit.launcher.client.events;
/*    */ 
/*    */ import pro.gravit.launcher.gui.RuntimeProvider;
/*    */ import pro.gravit.launcher.modules.LauncherModule;
/*    */ 
/*    */ public class ClientPreGuiPhase extends LauncherModule.Event {
/*    */   public RuntimeProvider runtimeProvider;
/*    */   
/*    */   public ClientPreGuiPhase(RuntimeProvider runtimeProvider) {
/* 10 */     this.runtimeProvider = runtimeProvider;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\events\ClientPreGuiPhase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */