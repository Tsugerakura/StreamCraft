/*    */ package pro.gravit.launcher.console.store;
/*    */ 
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import pro.gravit.launcher.NewLauncherSettings;
/*    */ import pro.gravit.launcher.managers.SettingsManager;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class CopyStoreDirCommand
/*    */   extends Command
/*    */ {
/*    */   public String getArgsDescription() {
/* 15 */     return "[index] [overwrite(true/false)]";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 20 */     return "Copy dir in GravitLauncherStore";
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 25 */     verifyArgs(args, 2);
/* 26 */     int ind = 1;
/* 27 */     int index = Integer.valueOf(args[0]).intValue();
/* 28 */     boolean overwrite = Boolean.valueOf(args[1]).booleanValue();
/* 29 */     for (NewLauncherSettings.HashedStoreEntry e : SettingsManager.settings.lastHDirs) {
/* 30 */       if (ind == index) {
/* 31 */         LogHelper.info("Copy [%d] FullPath: %s name: %s", new Object[] { Integer.valueOf(ind), e.fullPath, e.name });
/* 32 */         Path path = Paths.get(e.fullPath, new String[0]);
/* 33 */         if (!Files.isDirectory(path, new java.nio.file.LinkOption[0])) {
/* 34 */           LogHelper.error("Directory %s not found", new Object[] { path.toAbsolutePath().toString() });
/*    */           return;
/*    */         } 
/* 37 */         Path target = Paths.get(SettingsManager.settings.updatesDirPath, new String[0]).resolve(e.name);
/* 38 */         if (Files.exists(target, new java.nio.file.LinkOption[0]) && !overwrite) {
/* 39 */           LogHelper.error("Directory %s found, flag overwrite not found", new Object[] { target.toAbsolutePath().toString() });
/*    */           return;
/*    */         } 
/* 42 */         Files.copy(path, target, new java.nio.file.CopyOption[0]);
/*    */       } 
/* 44 */       ind++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\store\CopyStoreDirCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */