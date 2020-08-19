/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.console.FeatureCommand;
/*    */ import pro.gravit.launcher.console.UnlockCommand;
/*    */ import pro.gravit.launcher.console.admin.ExecCommand;
/*    */ import pro.gravit.launcher.console.admin.LogListenerCommand;
/*    */ import pro.gravit.launcher.console.store.CopyStoreDirCommand;
/*    */ import pro.gravit.launcher.console.store.LinkStoreDirCommand;
/*    */ import pro.gravit.launcher.console.store.StoreListCommand;
/*    */ import pro.gravit.utils.command.BaseCommandCategory;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.command.CommandCategory;
/*    */ import pro.gravit.utils.command.CommandHandler;
/*    */ import pro.gravit.utils.command.JLineCommandHandler;
/*    */ import pro.gravit.utils.command.StdCommandHandler;
/*    */ import pro.gravit.utils.command.basic.ClearCommand;
/*    */ import pro.gravit.utils.command.basic.DebugCommand;
/*    */ import pro.gravit.utils.command.basic.GCCommand;
/*    */ import pro.gravit.utils.command.basic.HelpCommand;
/*    */ import pro.gravit.utils.helper.CommonHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class ConsoleManager
/*    */ {
/*    */   public static CommandHandler handler;
/*    */   
/*    */   public static void initConsole() throws IOException {
/*    */     StdCommandHandler stdCommandHandler;
/*    */     try {
/* 32 */       Class.forName("org.jline.terminal.Terminal");
/*    */ 
/*    */       
/* 35 */       JLineCommandHandler jLineCommandHandler = new JLineCommandHandler();
/* 36 */       LogHelper.info("JLine2 terminal enabled");
/* 37 */     } catch (ClassNotFoundException ignored) {
/* 38 */       stdCommandHandler = new StdCommandHandler(true);
/* 39 */       LogHelper.warning("JLine2 isn't in classpath, using std");
/*    */     } 
/* 41 */     handler = (CommandHandler)stdCommandHandler;
/* 42 */     registerCommands();
/* 43 */     thread = CommonHelper.newThread("Launcher Console", true, (Runnable)handler);
/* 44 */     thread.start();
/*    */   }
/*    */   public static Thread thread; public static boolean isConsoleUnlock = false;
/*    */   public static void registerCommands() {
/* 48 */     handler.registerCommand("help", (Command)new HelpCommand(handler));
/* 49 */     handler.registerCommand("gc", (Command)new GCCommand());
/* 50 */     handler.registerCommand("clear", (Command)new ClearCommand(handler));
/* 51 */     handler.registerCommand("unlock", (Command)new UnlockCommand());
/*    */   }
/*    */   
/*    */   public static boolean checkUnlockKey(String key) {
/* 55 */     return key.equals((Launcher.getConfig()).oemUnlockKey);
/*    */   }
/*    */   
/*    */   public static void unlock() {
/* 59 */     handler.registerCommand("debug", (Command)new DebugCommand());
/* 60 */     handler.registerCommand("feature", (Command)new FeatureCommand());
/* 61 */     BaseCommandCategory admin = new BaseCommandCategory();
/* 62 */     admin.registerCommand("exec", (Command)new ExecCommand());
/* 63 */     admin.registerCommand("logListen", (Command)new LogListenerCommand());
/* 64 */     handler.registerCategory(new CommandHandler.Category((CommandCategory)admin, "admin", "Server admin commands"));
/* 65 */     BaseCommandCategory store = new BaseCommandCategory();
/* 66 */     store.registerCommand("storeList", (Command)new StoreListCommand());
/* 67 */     store.registerCommand("copyStoreDir", (Command)new CopyStoreDirCommand());
/* 68 */     store.registerCommand("linkStoreDir", (Command)new LinkStoreDirCommand());
/* 69 */     handler.registerCategory(new CommandHandler.Category((CommandCategory)admin, "store", "Store admin commands"));
/* 70 */     isConsoleUnlock = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\ConsoleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */