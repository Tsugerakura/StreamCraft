/*    */ package pro.gravit.launcher.modules.impl;
/*    */ 
/*    */ import pro.gravit.launcher.modules.LauncherModulesContext;
/*    */ import pro.gravit.launcher.modules.LauncherModulesManager;
/*    */ import pro.gravit.launcher.modules.ModulesConfigManager;
/*    */ 
/*    */ public class SimpleModuleContext implements LauncherModulesContext {
/*    */   public final LauncherModulesManager modulesManager;
/*    */   public final ModulesConfigManager configManager;
/*    */   
/*    */   public LauncherModulesManager getModulesManager() {
/* 12 */     return this.modulesManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public ModulesConfigManager getModulesConfigManager() {
/* 17 */     return this.configManager;
/*    */   }
/*    */   
/*    */   public SimpleModuleContext(LauncherModulesManager modulesManager, ModulesConfigManager configManager) {
/* 21 */     this.modulesManager = modulesManager;
/* 22 */     this.configManager = configManager;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\impl\SimpleModuleContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */