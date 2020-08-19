package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketExtension {
  public static final int RSV1 = 4;
  
  public static final int RSV2 = 2;
  
  public static final int RSV3 = 1;
  
  int rsv();
  
  WebSocketExtensionEncoder newExtensionEncoder();
  
  WebSocketExtensionDecoder newExtensionDecoder();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\WebSocketExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */