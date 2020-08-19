/*    */ package pro.gravit.launcher.console.store;
/*    */ 
/*    */ import pro.gravit.launcher.NewLauncherSettings;
/*    */ import pro.gravit.launcher.managers.SettingsManager;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class StoreListCommand
/*    */   extends Command {
/*    */   public String getArgsDescription() {
/* 11 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 16 */     return "List GravitLauncherStore";
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 21 */     int ind = 1;
/* 22 */     for (NewLauncherSettings.HashedStoreEntry e : SettingsManager.settings.lastHDirs) {
/* 23 */       LogHelper.info("[%d] FullPath: %s name: %s", new Object[] { Integer.valueOf(ind), e.fullPath, e.name });
/* 24 */       ind++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\store\StoreListCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */