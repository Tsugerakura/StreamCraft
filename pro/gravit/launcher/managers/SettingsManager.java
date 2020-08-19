/*     */ package pro.gravit.launcher.managers;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.NewLauncherSettings;
/*     */ import pro.gravit.launcher.client.DirBridge;
/*     */ import pro.gravit.launcher.config.JsonConfigurable;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public class SettingsManager extends JsonConfigurable<NewLauncherSettings> {
/*     */   @LauncherAPI
/*     */   public static NewLauncherSettings settings;
/*     */   
/*     */   public class StoreFileVisitor extends SimpleFileVisitor<Path> {
/*     */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*  27 */       try (HInput input = new HInput(IOHelper.newInput(file))) {
/*  28 */         String dirName = input.readString(128);
/*  29 */         String fullPath = input.readString(1024);
/*  30 */         HashedDir dir = new HashedDir(input);
/*  31 */         SettingsManager.settings.lastHDirs.add(new NewLauncherSettings.HashedStoreEntry(dir, dirName, fullPath));
/*  32 */       } catch (IOException e) {
/*  33 */         LogHelper.error("Skip file %s exception: %s", new Object[] { file.toAbsolutePath().toString(), e.getMessage() });
/*     */       } 
/*  35 */       return super.visitFile(file, attrs);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SettingsManager() {
/*  44 */     super(NewLauncherSettings.class, DirBridge.dir.resolve("settings.json"));
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public NewLauncherSettings getConfig() {
/*  50 */     if (settings.updatesDir != null)
/*  51 */       settings.updatesDirPath = settings.updatesDir.toString(); 
/*  52 */     return settings;
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public NewLauncherSettings getDefaultConfig() {
/*  58 */     return new NewLauncherSettings();
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void setConfig(NewLauncherSettings config) {
/*  64 */     settings = config;
/*  65 */     if (settings.updatesDirPath != null)
/*  66 */       settings.updatesDir = Paths.get(settings.updatesDirPath, new String[0]); 
/*  67 */     if (settings.consoleUnlockKey != null && !ConsoleManager.isConsoleUnlock && 
/*  68 */       ConsoleManager.checkUnlockKey(settings.consoleUnlockKey)) {
/*  69 */       ConsoleManager.unlock();
/*  70 */       LogHelper.info("Console auto unlocked");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void loadHDirStore(Path storePath) throws IOException {
/*  77 */     Files.createDirectories(storePath, (FileAttribute<?>[])new FileAttribute[0]);
/*  78 */     IOHelper.walk(storePath, new StoreFileVisitor(), false);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void saveHDirStore(Path storeProjectPath) throws IOException {
/*  83 */     Files.createDirectories(storeProjectPath, (FileAttribute<?>[])new FileAttribute[0]);
/*  84 */     for (NewLauncherSettings.HashedStoreEntry e : settings.lastHDirs) {
/*  85 */       if (!e.needSave)
/*  86 */         continue;  Path file = storeProjectPath.resolve(e.name.concat(".bin"));
/*  87 */       if (!Files.exists(file, new java.nio.file.LinkOption[0])) Files.createFile(file, (FileAttribute<?>[])new FileAttribute[0]); 
/*  88 */       try (HOutput output = new HOutput(IOHelper.newOutput(file))) {
/*  89 */         output.writeString(e.name, 128);
/*  90 */         output.writeString(e.fullPath, 1024);
/*  91 */         e.hdir.write(output);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void loadHDirStore() throws IOException {
/*  98 */     loadHDirStore(DirBridge.dirStore);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void saveHDirStore() throws IOException {
/* 103 */     saveHDirStore(DirBridge.dirProjectStore);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setType(Type type) {
/* 108 */     super.setType(type);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\SettingsManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */