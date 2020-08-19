package pro.gravit.launcher.modules;

import java.nio.file.Path;

public interface ModulesConfigManager {
  Path getModuleConfig(String paramString);
  
  Path getModuleConfig(String paramString1, String paramString2);
  
  Path getModuleConfigDir(String paramString);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\ModulesConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */