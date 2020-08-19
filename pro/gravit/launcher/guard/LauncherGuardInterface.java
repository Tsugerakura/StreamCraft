package pro.gravit.launcher.guard;

import java.nio.file.Path;
import pro.gravit.launcher.client.ClientLauncherContext;

public interface LauncherGuardInterface {
  String getName();
  
  Path getJavaBinPath();
  
  int getClientJVMBits();
  
  void init(boolean paramBoolean);
  
  void addCustomParams(ClientLauncherContext paramClientLauncherContext);
  
  void addCustomEnv(ClientLauncherContext paramClientLauncherContext);
  
  void setProtectToken(String paramString);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\guard\LauncherGuardInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */