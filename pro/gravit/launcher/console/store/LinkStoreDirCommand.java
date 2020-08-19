/*    */ package pro.gravit.launcher.console.store;
/*    */ 
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import pro.gravit.launcher.NewLauncherSettings;
/*    */ import pro.gravit.launcher.managers.SettingsManager;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class LinkStoreDirCommand
/*    */   extends Command
/*    */ {
/*    */   public String getArgsDescription() {
/* 16 */     return "[index]";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 21 */     return "Create symlink to GravitLauncherStore directory";
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 26 */     verifyArgs(args, 1);
/* 27 */     int ind = 1;
/* 28 */     int index = Integer.valueOf(args[0]).intValue();
/* 29 */     for (NewLauncherSettings.HashedStoreEntry e : SettingsManager.settings.lastHDirs) {
/* 30 */       if (ind == index) {
/* 31 */         LogHelper.info("Copy [%d] FullPath: %s name: %s", new Object[] { Integer.valueOf(ind), e.fullPath, e.name });
/* 32 */         Path path = Paths.get(e.fullPath, new String[0]);
/* 33 */         if (!Files.isDirectory(path, new java.nio.file.LinkOption[0])) {
/* 34 */           LogHelper.error("Directory %s not found", new Object[] { path.toAbsolutePath().toString() });
/*    */           return;
/*    */         } 
/* 37 */         Path target = Paths.get(SettingsManager.settings.updatesDirPath, new String[0]).resolve(e.name);
/* 38 */         if (Files.exists(target, new java.nio.file.LinkOption[0])) {
/* 39 */           LogHelper.error("Directory %s already exists", new Object[] { target.toAbsolutePath().toString() });
/*    */           return;
/*    */         } 
/* 42 */         Files.createSymbolicLink(path, target, (FileAttribute<?>[])new FileAttribute[0]);
/*    */       } 
/* 44 */       ind++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\store\LinkStoreDirCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */