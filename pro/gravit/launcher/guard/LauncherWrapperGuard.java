/*    */ package pro.gravit.launcher.guard;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherConfig;
/*    */ import pro.gravit.launcher.client.ClientLauncherContext;
/*    */ import pro.gravit.launcher.client.DirBridge;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ import pro.gravit.utils.helper.UnpackHelper;
/*    */ 
/*    */ 
/*    */ public class LauncherWrapperGuard
/*    */   implements LauncherGuardInterface
/*    */ {
/*    */   public String protectToken;
/*    */   
/*    */   public String getName() {
/* 23 */     return "wrapper";
/*    */   }
/*    */ 
/*    */   
/*    */   public Path getJavaBinPath() {
/* 28 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
/* 29 */       String projectName = (Launcher.getConfig()).projectname;
/* 30 */       String wrapperUnpackName = (JVMHelper.JVM_BITS == 64) ? projectName.concat("64.exe") : projectName.concat("32.exe");
/* 31 */       return DirBridge.getGuardDir().resolve(wrapperUnpackName);
/*    */     } 
/* 33 */     return IOHelper.resolveJavaBin(Paths.get(System.getProperty("java.home"), new String[0]));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getClientJVMBits() {
/* 38 */     return JVMHelper.JVM_BITS;
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(boolean clientInstance) {
/*    */     try {
/* 44 */       String wrapperName = (JVMHelper.JVM_BITS == 64) ? "wrapper64.exe" : "wrapper32.exe";
/* 45 */       String projectName = (Launcher.getConfig()).projectname;
/* 46 */       String wrapperUnpackName = (JVMHelper.JVM_BITS == 64) ? projectName.concat("64.exe") : projectName.concat("32.exe");
/* 47 */       String antiInjectName = (JVMHelper.JVM_BITS == 64) ? "AntiInject64.dll" : "AntiInject32.dll";
/* 48 */       UnpackHelper.unpack(Launcher.getResourceURL(wrapperName, "guard"), DirBridge.getGuardDir().resolve(wrapperUnpackName));
/* 49 */       UnpackHelper.unpack(Launcher.getResourceURL(antiInjectName, "guard"), DirBridge.getGuardDir().resolve(antiInjectName));
/* 50 */     } catch (IOException e) {
/* 51 */       throw new SecurityException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void addCustomParams(ClientLauncherContext context) {
/* 57 */     Collections.addAll(context.args, new String[] { "-Djava.class.path=".concat(context.pathLauncher) });
/*    */   }
/*    */ 
/*    */   
/*    */   public void addCustomEnv(ClientLauncherContext context) {
/* 62 */     Map<String, String> env = context.builder.environment();
/* 63 */     env.put("JAVA_HOME", System.getProperty("java.home"));
/* 64 */     LauncherConfig config = Launcher.getConfig();
/* 65 */     env.put("GUARD_USERNAME", context.playerProfile.username);
/* 66 */     env.put("GUARD_PUBLICKEY", config.publicKey.getModulus().toString(16));
/* 67 */     env.put("GUARD_PROJECTNAME", config.projectname);
/* 68 */     if (this.protectToken != null)
/* 69 */       env.put("GUARD_TOKEN", this.protectToken); 
/* 70 */     if (config.guardLicenseName != null)
/* 71 */       env.put("GUARD_LICENSE_NAME", config.guardLicenseName); 
/* 72 */     if (config.guardLicenseKey != null) {
/* 73 */       env.put("GUARD_LICENSE_KEY", config.guardLicenseKey);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void setProtectToken(String token) {
/* 79 */     this.protectToken = token;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherWrapperGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */