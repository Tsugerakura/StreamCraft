package pro.gravit.repackage.io.netty.handler.ssl;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

interface OpenSslSession extends SSLSession {
  void handshakeFinished() throws SSLException;
  
  void tryExpandApplicationBufferSize(int paramInt);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */