package pro.gravit.repackage.io.netty.channel.socket;

import java.net.InetSocketAddress;
import pro.gravit.repackage.io.netty.channel.ServerChannel;

public interface ServerSocketChannel extends ServerChannel {
  ServerSocketChannelConfig config();
  
  InetSocketAddress localAddress();
  
  InetSocketAddress remoteAddress();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\ServerSocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */