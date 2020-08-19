/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import pro.gravit.launcher.modules.ModulesConfigManager;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class SimpleModulesConfigManager implements ModulesConfigManager {
/*    */   public Path configDir;
/*    */   
/*    */   public SimpleModulesConfigManager(Path configDir) {
/* 15 */     this.configDir = configDir;
/*    */   }
/*    */   
/*    */   public Path getModuleConfig(String moduleName) {
/* 19 */     return getModuleConfig(moduleName, "Config");
/*    */   }
/*    */ 
/*    */   
/*    */   public Path getModuleConfig(String moduleName, String configName) {
/* 24 */     return getModuleConfigDir(moduleName).resolve(moduleName.concat(configName.concat(".json")));
/*    */   }
/*    */   
/*    */   public Path getModuleConfigDir(String moduleName) {
/* 28 */     if (!IOHelper.isDir(this.configDir)) {
/*    */       try {
/* 30 */         Files.createDirectories(this.configDir, (FileAttribute<?>[])new FileAttribute[0]);
/* 31 */       } catch (IOException e) {
/* 32 */         LogHelper.error(e);
/*    */       } 
/*    */     }
/* 35 */     return this.configDir.resolve(moduleName);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\SimpleModulesConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */