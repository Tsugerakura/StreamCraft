/*    */ package pro.gravit.launcher;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import pro.gravit.launcher.client.UserSettings;
/*    */ import pro.gravit.launcher.hasher.HashedDir;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ 
/*    */ public class NewLauncherSettings
/*    */ {
/*    */   @LauncherAPI
/*    */   public String login;
/*    */   @LauncherAPI
/*    */   public String auth;
/*    */   @LauncherAPI
/*    */   public byte[] rsaPassword;
/*    */   @LauncherAPI
/*    */   public int profile;
/*    */   @LauncherAPI
/*    */   public transient Path updatesDir;
/*    */   @LauncherAPI
/*    */   public String updatesDirPath;
/*    */   @LauncherAPI
/*    */   public boolean autoEnter;
/*    */   @LauncherAPI
/*    */   public boolean debug;
/*    */   @LauncherAPI
/*    */   public boolean fullScreen;
/*    */   @LauncherAPI
/*    */   public boolean offline;
/*    */   @LauncherAPI
/*    */   public int ram;
/*    */   @LauncherAPI
/*    */   public byte[] lastDigest;
/*    */   @LauncherAPI
/* 40 */   public List<ClientProfile> lastProfiles = new LinkedList<>();
/*    */   @LauncherAPI
/* 42 */   public Map<String, UserSettings> userSettings = new HashMap<>();
/*    */   @LauncherAPI
/*    */   public boolean featureStore;
/*    */   @LauncherAPI
/*    */   public String consoleUnlockKey;
/*    */   
/*    */   public static class HashedStoreEntry
/*    */   {
/*    */     @LauncherAPI
/*    */     public HashedDir hdir;
/*    */     @LauncherAPI
/*    */     public String name;
/*    */     @LauncherAPI
/*    */     public String fullPath;
/*    */     @LauncherAPI
/*    */     public transient boolean needSave = false;
/*    */     
/*    */     public HashedStoreEntry(HashedDir hdir, String name, String fullPath) {
/* 60 */       this.hdir = hdir;
/* 61 */       this.name = name;
/* 62 */       this.fullPath = fullPath;
/*    */     }
/*    */   }
/*    */   @LauncherAPI
/* 66 */   public transient List<HashedStoreEntry> lastHDirs = new ArrayList<>(16);
/*    */ 
/*    */   
/*    */   @LauncherAPI
/*    */   public void putHDir(String name, Path path, HashedDir dir) {
/* 71 */     String fullPath = path.toAbsolutePath().toString();
/* 72 */     this.lastHDirs.removeIf(e -> (e.fullPath.equals(fullPath) && e.name.equals(name)));
/* 73 */     HashedStoreEntry e = new HashedStoreEntry(dir, name, fullPath);
/* 74 */     e.needSave = true;
/* 75 */     this.lastHDirs.add(e);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\NewLauncherSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */