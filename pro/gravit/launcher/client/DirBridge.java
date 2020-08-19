/*    */ package pro.gravit.launcher.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DirBridge
/*    */ {
/*    */   public static final String USE_CUSTOMDIR_PROPERTY = "launcher.usecustomdir";
/*    */   public static final String CUSTOMDIR_PROPERTY = "launcher.customdir";
/*    */   public static final String USE_OPTDIR_PROPERTY = "launcher.useoptdir";
/*    */   @LauncherAPI
/*    */   public static Path dir;
/*    */   @LauncherAPI
/*    */   public static Path dirStore;
/*    */   @LauncherAPI
/*    */   public static Path dirProjectStore;
/*    */   @LauncherAPI
/*    */   public static Path dirUpdates;
/*    */   @LauncherAPI
/*    */   public static Path defaultUpdatesDir;
/*    */   @LauncherAPI
/*    */   public static boolean useLegacyDir;
/*    */   
/*    */   @LauncherAPI
/*    */   public static void move(Path newDir) throws IOException {
/* 33 */     IOHelper.move(dirUpdates, newDir);
/* 34 */     dirUpdates = newDir;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getAppDataDir() throws IOException {
/* 39 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
/* 40 */       Path appdata = IOHelper.HOME_DIR.resolve("AppData").resolve("Roaming");
/* 41 */       if (!IOHelper.isDir(appdata)) Files.createDirectories(appdata, (FileAttribute<?>[])new FileAttribute[0]); 
/* 42 */       return appdata;
/*    */     } 
/* 44 */     return IOHelper.HOME_DIR;
/*    */   }
/*    */ 
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getLauncherDir(String projectname) throws IOException {
/* 50 */     return getAppDataDir().resolve(projectname);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getStoreDir(String projectname) throws IOException {
/* 55 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX)
/* 56 */       return getAppDataDir().resolve("store"); 
/* 57 */     if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
/* 58 */       return getAppDataDir().resolve("GravitLauncherStore");
/*    */     }
/* 60 */     return getAppDataDir().resolve("minecraftStore");
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getProjectStoreDir(String projectname) throws IOException {
/* 65 */     return getStoreDir(projectname).resolve(projectname);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getGuardDir() {
/* 70 */     return dir.resolve("guard");
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static Path getLegacyLauncherDir(String projectname) {
/* 75 */     return IOHelper.HOME_DIR.resolve(projectname);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static void setUseLegacyDir(boolean b) {
/* 80 */     useLegacyDir = b;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\DirBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */