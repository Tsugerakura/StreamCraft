/*    */ package pro.gravit.launcher.config;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ import java.nio.file.Path;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public abstract class JsonConfigurable<T>
/*    */ {
/*    */   private Type type;
/*    */   protected Path configPath;
/*    */   
/*    */   @LauncherAPI
/*    */   public void saveConfig() throws IOException {
/* 20 */     saveConfig(this.configPath);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void loadConfig() throws IOException {
/* 25 */     loadConfig(this.configPath);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public JsonConfigurable(Type type, Path configPath) {
/* 30 */     this.type = type;
/* 31 */     this.configPath = configPath;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void saveConfig(Path configPath) throws IOException {
/* 36 */     try (BufferedWriter writer = IOHelper.newWriter(configPath)) {
/* 37 */       Launcher.gsonManager.configGson.toJson(getConfig(), this.type, writer);
/*    */     } 
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void loadConfig(Path configPath) throws IOException {
/* 43 */     if (generateConfigIfNotExists(configPath))
/* 44 */       return;  try (BufferedReader reader = IOHelper.newReader(configPath)) {
/* 45 */       setConfig((T)Launcher.gsonManager.configGson.fromJson(reader, this.type));
/* 46 */     } catch (Exception e) {
/*    */       
/* 48 */       LogHelper.error(e);
/* 49 */       resetConfig(configPath);
/*    */     } 
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void resetConfig() throws IOException {
/* 55 */     setConfig(getDefaultConfig());
/* 56 */     saveConfig();
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void resetConfig(Path newPath) throws IOException {
/* 61 */     setConfig(getDefaultConfig());
/* 62 */     saveConfig(newPath);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public boolean generateConfigIfNotExists(Path path) throws IOException {
/* 67 */     if (IOHelper.isFile(path))
/* 68 */       return false; 
/* 69 */     resetConfig(path);
/* 70 */     return true;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public boolean generateConfigIfNotExists() throws IOException {
/* 75 */     if (IOHelper.isFile(this.configPath))
/* 76 */       return false; 
/* 77 */     resetConfig();
/* 78 */     return true;
/*    */   }
/*    */   
/*    */   protected void setType(Type type) {
/* 82 */     this.type = type;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract T getConfig();
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract T getDefaultConfig();
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract void setConfig(T paramT);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\config\JsonConfigurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */