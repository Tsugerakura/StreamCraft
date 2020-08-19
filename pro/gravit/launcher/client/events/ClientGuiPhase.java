/*    */ package pro.gravit.launcher.client.events;
/*    */ 
/*    */ import pro.gravit.launcher.gui.RuntimeProvider;
/*    */ import pro.gravit.launcher.modules.LauncherModule;
/*    */ 
/*    */ public class ClientGuiPhase extends LauncherModule.Event {
/*    */   public final RuntimeProvider runtimeProvider;
/*    */   
/*    */   public ClientGuiPhase(RuntimeProvider runtimeProvider) {
/* 10 */     this.runtimeProvider = runtimeProvider;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\events\ClientGuiPhase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */