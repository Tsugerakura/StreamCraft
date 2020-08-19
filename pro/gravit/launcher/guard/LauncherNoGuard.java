/*    */ package pro.gravit.launcher.guard;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.Collections;
/*    */ import pro.gravit.launcher.client.ClientLauncherContext;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class LauncherNoGuard
/*    */   implements LauncherGuardInterface
/*    */ {
/*    */   public String getName() {
/* 15 */     return "noGuard";
/*    */   }
/*    */ 
/*    */   
/*    */   public Path getJavaBinPath() {
/* 20 */     return IOHelper.resolveJavaBin(Paths.get(System.getProperty("java.home"), new String[0]));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getClientJVMBits() {
/* 25 */     return JVMHelper.JVM_BITS;
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(boolean clientInstance) {
/* 30 */     LogHelper.warning("Using noGuard interface");
/*    */   }
/*    */ 
/*    */   
/*    */   public void addCustomParams(ClientLauncherContext context) {
/* 35 */     Collections.addAll(context.args, new String[] { "-cp" });
/* 36 */     Collections.addAll(context.args, new String[] { context.pathLauncher });
/*    */   }
/*    */   
/*    */   public void addCustomEnv(ClientLauncherContext context) {}
/*    */   
/*    */   public void setProtectToken(String token) {}
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherNoGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */