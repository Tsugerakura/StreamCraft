package pro.gravit.launcher.gui;

public interface RuntimeProvider {
  void run(String[] paramArrayOfString) throws Exception;
  
  void preLoad() throws Exception;
  
  void init(boolean paramBoolean) throws Exception;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\gui\RuntimeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */