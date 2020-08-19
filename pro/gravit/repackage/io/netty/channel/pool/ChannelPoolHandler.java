package pro.gravit.repackage.io.netty.channel.pool;

import pro.gravit.repackage.io.netty.channel.Channel;

public interface ChannelPoolHandler {
  void channelReleased(Channel paramChannel) throws Exception;
  
  void channelAcquired(Channel paramChannel) throws Exception;
  
  void channelCreated(Channel paramChannel) throws Exception;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\ChannelPoolHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */