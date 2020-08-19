package pro.gravit.repackage.io.netty.handler.codec.http;

public interface HttpMessage extends HttpObject {
  @Deprecated
  HttpVersion getProtocolVersion();
  
  HttpVersion protocolVersion();
  
  HttpMessage setProtocolVersion(HttpVersion paramHttpVersion);
  
  HttpHeaders headers();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */