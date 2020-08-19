package pro.gravit.repackage.io.netty.handler.codec;

public interface ValueConverter<T> {
  T convertObject(Object paramObject);
  
  T convertBoolean(boolean paramBoolean);
  
  boolean convertToBoolean(T paramT);
  
  T convertByte(byte paramByte);
  
  byte convertToByte(T paramT);
  
  T convertChar(char paramChar);
  
  char convertToChar(T paramT);
  
  T convertShort(short paramShort);
  
  short convertToShort(T paramT);
  
  T convertInt(int paramInt);
  
  int convertToInt(T paramT);
  
  T convertLong(long paramLong);
  
  long convertToLong(T paramT);
  
  T convertTimeMillis(long paramLong);
  
  long convertToTimeMillis(T paramT);
  
  T convertFloat(float paramFloat);
  
  float convertToFloat(T paramT);
  
  T convertDouble(double paramDouble);
  
  double convertToDouble(T paramT);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\ValueConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */