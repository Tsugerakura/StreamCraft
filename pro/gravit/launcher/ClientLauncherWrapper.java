/*    */ package pro.gravit.launcher;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import pro.gravit.launcher.client.ClientLauncher;
/*    */ import pro.gravit.utils.helper.EnvHelper;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ 
/*    */ public class ClientLauncherWrapper
/*    */ {
/*    */   public static final String MAGIC_ARG = "-Djdk.attach.allowAttachSelf";
/*    */   public static final String WAIT_PROCESS_PROPERTY = "launcher.waitProcess";
/* 20 */   public static boolean waitProcess = Boolean.getBoolean("launcher.waitProcess");
/*    */   
/*    */   public static void main(String[] arguments) throws IOException, InterruptedException {
/* 23 */     LogHelper.printVersion("Launcher");
/* 24 */     LogHelper.printLicense("Launcher");
/* 25 */     JVMHelper.checkStackTrace(ClientLauncherWrapper.class);
/* 26 */     JVMHelper.verifySystemProperties(Launcher.class, true);
/* 27 */     EnvHelper.checkDangerousParams();
/* 28 */     LauncherConfig config = Launcher.getConfig();
/* 29 */     LogHelper.info("Launcher for project %s", new Object[] { config.projectname });
/* 30 */     if (config.environment.equals(LauncherConfig.LauncherEnvironment.PROD)) {
/* 31 */       if (System.getProperty("launcher.debug") != null) {
/* 32 */         LogHelper.warning("Found -Dlauncher.debug=true");
/*    */       }
/* 34 */       if (System.getProperty("launcher.stacktrace") != null) {
/* 35 */         LogHelper.warning("Found -Dlauncher.stacktrace=true");
/*    */       }
/* 37 */       LogHelper.info("Debug mode disabled (found env PRODUCTION)");
/*    */     } else {
/* 39 */       LogHelper.info("If need debug output use -Dlauncher.debug=true");
/* 40 */       LogHelper.info("If need stacktrace output use -Dlauncher.stacktrace=true");
/* 41 */       if (LogHelper.isDebugEnabled()) waitProcess = true; 
/*    */     } 
/* 43 */     LogHelper.info("Restart Launcher with JavaAgent...");
/* 44 */     ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
/* 45 */     if (waitProcess) processBuilder.inheritIO(); 
/* 46 */     Path javaBin = IOHelper.resolveJavaBin(Paths.get(System.getProperty("java.home"), new String[0]));
/* 47 */     List<String> args = new LinkedList<>();
/* 48 */     args.add(javaBin.toString());
/* 49 */     String pathLauncher = IOHelper.getCodeSource(ClientLauncher.class).toString();
/* 50 */     args.add(JVMHelper.jvmProperty("launcher.debug", Boolean.toString(LogHelper.isDebugEnabled())));
/* 51 */     args.add(JVMHelper.jvmProperty("launcher.stacktrace", Boolean.toString(LogHelper.isStacktraceEnabled())));
/* 52 */     args.add(JVMHelper.jvmProperty("launcher.dev", Boolean.toString(LogHelper.isDevEnabled())));
/* 53 */     JVMHelper.addSystemPropertyToArgs(args, "launcher.customdir");
/* 54 */     JVMHelper.addSystemPropertyToArgs(args, "launcher.usecustomdir");
/* 55 */     JVMHelper.addSystemPropertyToArgs(args, "launcher.useoptdir");
/* 56 */     Collections.addAll(args, new String[] { "-Djdk.attach.allowAttachSelf" });
/* 57 */     Collections.addAll(args, new String[] { "-XX:+DisableAttachMechanism" });
/* 58 */     Collections.addAll(args, new String[] { "-javaagent:".concat(pathLauncher).concat("=pr") });
/* 59 */     Collections.addAll(args, new String[] { "-cp" });
/* 60 */     Collections.addAll(args, new String[] { pathLauncher });
/* 61 */     Collections.addAll(args, new String[] { LauncherEngine.class.getName() });
/* 62 */     EnvHelper.addEnv(processBuilder);
/* 63 */     LogHelper.debug("Commandline: " + args);
/* 64 */     processBuilder.command(args);
/* 65 */     Process process = processBuilder.start();
/* 66 */     if (!waitProcess) {
/* 67 */       Thread.sleep(3000L);
/* 68 */       if (!process.isAlive())
/* 69 */       { int errorcode = process.exitValue();
/* 70 */         if (errorcode != 0) {
/* 71 */           LogHelper.error("Process exit with error code: %d", new Object[] { Integer.valueOf(errorcode) });
/*    */         } else {
/* 73 */           LogHelper.info("Process exit with code 0");
/*    */         }  }
/* 75 */       else { LogHelper.debug("Process started success"); }
/*    */     
/*    */     } else {
/* 78 */       process.waitFor();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ClientLauncherWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */