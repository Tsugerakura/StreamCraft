package pro.gravit.repackage.io.netty.handler.codec.http.cookie;

public interface Cookie extends Comparable<Cookie> {
  public static final long UNDEFINED_MAX_AGE = -9223372036854775808L;
  
  String name();
  
  String value();
  
  void setValue(String paramString);
  
  boolean wrap();
  
  void setWrap(boolean paramBoolean);
  
  String domain();
  
  void setDomain(String paramString);
  
  String path();
  
  void setPath(String paramString);
  
  long maxAge();
  
  void setMaxAge(long paramLong);
  
  boolean isSecure();
  
  void setSecure(boolean paramBoolean);
  
  boolean isHttpOnly();
  
  void setHttpOnly(boolean paramBoolean);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\Cookie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */