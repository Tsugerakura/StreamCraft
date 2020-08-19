/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import pro.gravit.launcher.client.ClientLauncherContext;
/*    */ import pro.gravit.launcher.gui.RuntimeProvider;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.utils.BiHookSet;
/*    */ import pro.gravit.utils.HookSet;
/*    */ 
/*    */ public class ClientHookManager {
/* 11 */   public static HookSet<RuntimeProvider> initGuiHook = new HookSet();
/* 12 */   public static HookSet<HInput> paramsInputHook = new HookSet();
/* 13 */   public static HookSet<HOutput> paramsOutputHook = new HookSet();
/*    */   
/* 15 */   public static HookSet<ClientLauncherContext> clientLaunchHook = new HookSet();
/* 16 */   public static HookSet<ClientLauncherContext> clientLaunchFinallyHook = new HookSet();
/*    */   
/* 18 */   public static BiHookSet<ClientLauncherContext, ProcessBuilder> preStartHook = new BiHookSet();
/* 19 */   public static BiHookSet<ClientLauncherContext, ProcessBuilder> postStartHook = new BiHookSet();
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\ClientHookManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */