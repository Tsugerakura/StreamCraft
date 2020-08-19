/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.util.HashMap;
/*    */ import java.util.Objects;
/*    */ import pro.gravit.launcher.config.JsonConfigurable;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ 
/*    */ public class ConfigManager
/*    */ {
/* 14 */   private final HashMap<String, JsonConfigurable> CONFIGURABLE = new HashMap<>();
/*    */   
/*    */   public void registerConfigurable(String name, JsonConfigurable reconfigurable) {
/* 17 */     VerifyHelper.putIfAbsent(this.CONFIGURABLE, name.toLowerCase(), Objects.requireNonNull(reconfigurable, "adapter"), 
/* 18 */         String.format("Reloadable has been already registered: '%s'", new Object[] { name }));
/*    */   }
/*    */   
/*    */   public void printConfigurables() {
/* 22 */     LogHelper.info("Print configurables");
/* 23 */     this.CONFIGURABLE.forEach((k, v) -> LogHelper.subInfo(k));
/* 24 */     LogHelper.info("Found %d configurables", new Object[] { Integer.valueOf(this.CONFIGURABLE.size()) });
/*    */   }
/*    */   
/*    */   public void save(String name) throws IOException {
/* 28 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).saveConfig();
/*    */   }
/*    */   
/*    */   public void load(String name) throws IOException {
/* 32 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).loadConfig();
/*    */   }
/*    */   
/*    */   public void save(String name, Path path) throws IOException {
/* 36 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).saveConfig(path);
/*    */   }
/*    */   
/*    */   public void reset(String name) throws IOException {
/* 40 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).resetConfig();
/*    */   }
/*    */   
/*    */   public void load(String name, Path path) throws IOException {
/* 44 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).loadConfig(path);
/*    */   }
/*    */   
/*    */   public void reset(String name, Path path) throws IOException {
/* 48 */     ((JsonConfigurable)this.CONFIGURABLE.get(name)).resetConfig(path);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\ConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */