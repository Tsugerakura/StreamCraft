/*    */ package pro.gravit.launcher.guard;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.Collections;
/*    */ import pro.gravit.launcher.client.ClientLauncher;
/*    */ import pro.gravit.launcher.client.ClientLauncherContext;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ 
/*    */ public class LauncherJavaGuard
/*    */   implements LauncherGuardInterface
/*    */ {
/*    */   public String getName() {
/* 15 */     return "java";
/*    */   }
/*    */ 
/*    */   
/*    */   public Path getJavaBinPath() {
/* 20 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
/* 21 */       return IOHelper.resolveJavaBin(ClientLauncher.getJavaBinPath());
/*    */     }
/* 23 */     return IOHelper.resolveJavaBin(Paths.get(System.getProperty("java.home"), new String[0]));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getClientJVMBits() {
/* 28 */     return JVMHelper.OS_BITS;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void init(boolean clientInstance) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void addCustomParams(ClientLauncherContext context) {
/* 38 */     Collections.addAll(context.args, new String[] { "-cp" });
/* 39 */     Collections.addAll(context.args, new String[] { context.pathLauncher });
/*    */   }
/*    */   
/*    */   public void addCustomEnv(ClientLauncherContext context) {}
/*    */   
/*    */   public void setProtectToken(String token) {}
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherJavaGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */