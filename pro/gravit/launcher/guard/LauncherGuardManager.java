/*    */ package pro.gravit.launcher.guard;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherConfig;
/*    */ 
/*    */ public class LauncherGuardManager
/*    */ {
/*    */   public static LauncherGuardInterface guard;
/*    */   
/*    */   public static void initGuard(boolean clientInstance) {
/* 12 */     LauncherConfig config = Launcher.getConfig();
/* 13 */     switch (config.guardType) {
/*    */       case "gravitguard":
/* 15 */         guard = new LauncherGravitGuard();
/*    */         break;
/*    */       
/*    */       case "wrapper":
/* 19 */         guard = new LauncherWrapperGuard();
/*    */         break;
/*    */       
/*    */       case "java":
/* 23 */         guard = new LauncherJavaGuard();
/*    */         break;
/*    */       
/*    */       default:
/* 27 */         guard = new LauncherNoGuard();
/*    */         break;
/*    */     } 
/* 30 */     guard.init(clientInstance);
/*    */   }
/*    */   
/*    */   public static Path getGuardJavaBinPath() {
/* 34 */     return guard.getJavaBinPath();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherGuardManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */