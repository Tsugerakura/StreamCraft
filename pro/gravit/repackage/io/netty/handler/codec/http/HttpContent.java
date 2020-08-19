package pro.gravit.repackage.io.netty.handler.codec.http;

import pro.gravit.repackage.io.netty.buffer.ByteBuf;
import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;

public interface HttpContent extends HttpObject, ByteBufHolder {
  HttpContent copy();
  
  HttpContent duplicate();
  
  HttpContent retainedDuplicate();
  
  HttpContent replace(ByteBuf paramByteBuf);
  
  HttpContent retain();
  
  HttpContent retain(int paramInt);
  
  HttpContent touch();
  
  HttpContent touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */