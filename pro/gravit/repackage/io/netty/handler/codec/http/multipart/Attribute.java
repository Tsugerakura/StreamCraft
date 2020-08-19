package pro.gravit.repackage.io.netty.handler.codec.http.multipart;

import java.io.IOException;
import pro.gravit.repackage.io.netty.buffer.ByteBuf;

public interface Attribute extends HttpData {
  String getValue() throws IOException;
  
  void setValue(String paramString) throws IOException;
  
  Attribute copy();
  
  Attribute duplicate();
  
  Attribute retainedDuplicate();
  
  Attribute replace(ByteBuf paramByteBuf);
  
  Attribute retain();
  
  Attribute retain(int paramInt);
  
  Attribute touch();
  
  Attribute touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\Attribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */