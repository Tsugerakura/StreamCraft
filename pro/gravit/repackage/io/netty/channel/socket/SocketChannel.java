package pro.gravit.repackage.io.netty.channel.socket;

import java.net.InetSocketAddress;

public interface SocketChannel extends DuplexChannel {
  ServerSocketChannel parent();
  
  SocketChannelConfig config();
  
  InetSocketAddress localAddress();
  
  InetSocketAddress remoteAddress();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\SocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */