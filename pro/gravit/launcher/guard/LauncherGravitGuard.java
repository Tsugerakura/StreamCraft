/*    */ package pro.gravit.launcher.guard;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherConfig;
/*    */ import pro.gravit.launcher.bridge.GravitGuardBridge;
/*    */ import pro.gravit.launcher.client.ClientLauncher;
/*    */ import pro.gravit.launcher.client.ClientLauncherContext;
/*    */ import pro.gravit.launcher.client.DirBridge;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ import pro.gravit.utils.helper.UnpackHelper;
/*    */ 
/*    */ 
/*    */ public class LauncherGravitGuard
/*    */   implements LauncherGuardInterface
/*    */ {
/*    */   public String protectToken;
/*    */   public Path javaBinPath;
/*    */   
/*    */   public String getName() {
/* 26 */     return "wrapper";
/*    */   }
/*    */ 
/*    */   
/*    */   public Path getJavaBinPath() {
/* 31 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
/* 32 */       String projectName = (Launcher.getConfig()).projectname;
/* 33 */       String wrapperUnpackName = (JVMHelper.JVM_BITS == 64) ? projectName.concat("64.exe") : projectName.concat("32.exe");
/* 34 */       return DirBridge.getGuardDir().resolve(wrapperUnpackName);
/* 35 */     }  if (ClientLauncher.getJavaBinPath() != null) {
/* 36 */       this.javaBinPath = ClientLauncher.getJavaBinPath();
/* 37 */       String projectName = (Launcher.getConfig()).projectname;
/* 38 */       String wrapperUnpackName = (JVMHelper.JVM_BITS == 64) ? projectName.concat("64.exe") : projectName.concat("32.exe");
/* 39 */       return DirBridge.getGuardDir().resolve(wrapperUnpackName);
/*    */     } 
/* 41 */     return IOHelper.resolveJavaBin(Paths.get(System.getProperty("java.home"), new String[0]));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getClientJVMBits() {
/* 46 */     return JVMHelper.JVM_BITS;
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(boolean clientInstance) {
/*    */     try {
/* 52 */       String wrapperName = (JVMHelper.JVM_BITS == 64) ? "wrapper64.exe" : "wrapper32.exe";
/* 53 */       String projectName = (Launcher.getConfig()).projectname;
/* 54 */       String wrapperUnpackName = (JVMHelper.JVM_BITS == 64) ? projectName.concat("64.exe") : projectName.concat("32.exe");
/* 55 */       String antiInjectName = (JVMHelper.JVM_BITS == 64) ? "AntiInject64.dll" : "AntiInject32.dll";
/* 56 */       UnpackHelper.unpack(Launcher.getResourceURL(wrapperName, "guard"), DirBridge.getGuardDir().resolve(wrapperUnpackName));
/* 57 */       UnpackHelper.unpack(Launcher.getResourceURL(antiInjectName, "guard"), DirBridge.getGuardDir().resolve(antiInjectName));
/* 58 */     } catch (IOException e) {
/* 59 */       throw new SecurityException(e);
/*    */     } 
/* 61 */     if (clientInstance) GravitGuardBridge.callGuard();
/*    */   
/*    */   }
/*    */   
/*    */   public void addCustomParams(ClientLauncherContext context) {
/* 66 */     Collections.addAll(context.args, new String[] { "-Djava.class.path=".concat(context.pathLauncher) });
/*    */   }
/*    */ 
/*    */   
/*    */   public void addCustomEnv(ClientLauncherContext context) {
/* 71 */     Map<String, String> env = context.builder.environment();
/* 72 */     if (this.javaBinPath == null) {
/* 73 */       env.put("JAVA_HOME", System.getProperty("java.home"));
/*    */     } else {
/* 75 */       env.put("JAVA_HOME", this.javaBinPath.toAbsolutePath().toString());
/* 76 */     }  LauncherConfig config = Launcher.getConfig();
/* 77 */     env.put("GUARD_BRIDGE", GravitGuardBridge.class.getName());
/* 78 */     env.put("GUARD_USERNAME", context.playerProfile.username);
/* 79 */     env.put("GUARD_PUBLICKEY", config.publicKey.getModulus().toString(16));
/* 80 */     env.put("GUARD_PROJECTNAME", config.projectname);
/* 81 */     if (this.protectToken != null)
/* 82 */       env.put("GUARD_TOKEN", this.protectToken); 
/* 83 */     if (config.guardLicenseName != null)
/* 84 */       env.put("GUARD_LICENSE_NAME", config.guardLicenseName); 
/* 85 */     if (config.guardLicenseKey != null) {
/* 86 */       env.put("GUARD_LICENSE_KEY", config.guardLicenseKey);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void setProtectToken(String token) {
/* 92 */     this.protectToken = token;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherGravitGuard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */