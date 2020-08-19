package pro.gravit.repackage.io.netty.handler.codec.http;

import pro.gravit.repackage.io.netty.buffer.ByteBuf;

public interface FullHttpMessage extends HttpMessage, LastHttpContent {
  FullHttpMessage copy();
  
  FullHttpMessage duplicate();
  
  FullHttpMessage retainedDuplicate();
  
  FullHttpMessage replace(ByteBuf paramByteBuf);
  
  FullHttpMessage retain(int paramInt);
  
  FullHttpMessage retain();
  
  FullHttpMessage touch();
  
  FullHttpMessage touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\FullHttpMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */