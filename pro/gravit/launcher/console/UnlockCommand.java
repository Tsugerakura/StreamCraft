/*    */ package pro.gravit.launcher.console;
/*    */ 
/*    */ import pro.gravit.launcher.managers.ConsoleManager;
/*    */ import pro.gravit.launcher.managers.SettingsManager;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class UnlockCommand
/*    */   extends Command {
/*    */   public String getArgsDescription() {
/* 11 */     return "[key]";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 16 */     return "Unlock console commands";
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 21 */     verifyArgs(args, 1);
/* 22 */     if (ConsoleManager.checkUnlockKey(args[0])) {
/* 23 */       LogHelper.info("Unlock successful");
/* 24 */       ConsoleManager.unlock();
/* 25 */       ConsoleManager.handler.unregisterCommand("unlock");
/* 26 */       LogHelper.info("Write unlock key");
/* 27 */       SettingsManager.settings.consoleUnlockKey = args[0];
/*    */     } else {
/* 29 */       LogHelper.error("Unlock key incorrect");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\UnlockCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */