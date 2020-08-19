package pro.gravit.launcher.hwid;

public interface HWID {
  String getSerializeString();
  
  int getLevel();
  
  int compare(HWID paramHWID);
  
  boolean isNull();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hwid\HWID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */