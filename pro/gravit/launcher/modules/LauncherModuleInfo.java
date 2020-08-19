/*    */ package pro.gravit.launcher.modules;
/*    */ 
/*    */ import pro.gravit.utils.Version;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LauncherModuleInfo
/*    */ {
/*    */   public final String name;
/*    */   public final Version version;
/*    */   public final int priority;
/*    */   public final String[] dependencies;
/*    */   public final String[] providers;
/*    */   
/*    */   public LauncherModuleInfo(String name, Version version) {
/* 16 */     this.name = name;
/* 17 */     this.version = version;
/* 18 */     this.priority = 0;
/* 19 */     this.dependencies = new String[0];
/* 20 */     this.providers = new String[0];
/*    */   }
/*    */   
/*    */   public LauncherModuleInfo(String name) {
/* 24 */     this.name = name;
/* 25 */     this.version = new Version(1, 0, 0);
/* 26 */     this.priority = 0;
/* 27 */     this.dependencies = new String[0];
/* 28 */     this.providers = new String[0];
/*    */   }
/*    */   
/*    */   public LauncherModuleInfo(String name, Version version, String[] dependencies) {
/* 32 */     this.name = name;
/* 33 */     this.version = version;
/* 34 */     this.priority = 0;
/* 35 */     this.dependencies = dependencies;
/* 36 */     this.providers = new String[0];
/*    */   }
/*    */   
/*    */   public LauncherModuleInfo(String name, Version version, int priority, String[] dependencies) {
/* 40 */     this.name = name;
/* 41 */     this.version = version;
/* 42 */     this.priority = priority;
/* 43 */     this.dependencies = dependencies;
/* 44 */     this.providers = new String[0];
/*    */   }
/*    */   
/*    */   public LauncherModuleInfo(String name, Version version, int priority, String[] dependencies, String[] providers) {
/* 48 */     this.name = name;
/* 49 */     this.version = version;
/* 50 */     this.priority = priority;
/* 51 */     this.dependencies = dependencies;
/* 52 */     this.providers = providers;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\LauncherModuleInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */