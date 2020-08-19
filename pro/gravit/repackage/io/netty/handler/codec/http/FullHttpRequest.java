package pro.gravit.repackage.io.netty.handler.codec.http;

import pro.gravit.repackage.io.netty.buffer.ByteBuf;

public interface FullHttpRequest extends HttpRequest, FullHttpMessage {
  FullHttpRequest copy();
  
  FullHttpRequest duplicate();
  
  FullHttpRequest retainedDuplicate();
  
  FullHttpRequest replace(ByteBuf paramByteBuf);
  
  FullHttpRequest retain(int paramInt);
  
  FullHttpRequest retain();
  
  FullHttpRequest touch();
  
  FullHttpRequest touch(Object paramObject);
  
  FullHttpRequest setProtocolVersion(HttpVersion paramHttpVersion);
  
  FullHttpRequest setMethod(HttpMethod paramHttpMethod);
  
  FullHttpRequest setUri(String paramString);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\FullHttpRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */