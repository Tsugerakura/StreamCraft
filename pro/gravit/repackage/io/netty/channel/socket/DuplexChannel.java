package pro.gravit.repackage.io.netty.channel.socket;

import pro.gravit.repackage.io.netty.channel.Channel;
import pro.gravit.repackage.io.netty.channel.ChannelFuture;
import pro.gravit.repackage.io.netty.channel.ChannelPromise;

public interface DuplexChannel extends Channel {
  boolean isInputShutdown();
  
  ChannelFuture shutdownInput();
  
  ChannelFuture shutdownInput(ChannelPromise paramChannelPromise);
  
  boolean isOutputShutdown();
  
  ChannelFuture shutdownOutput();
  
  ChannelFuture shutdownOutput(ChannelPromise paramChannelPromise);
  
  boolean isShutdown();
  
  ChannelFuture shutdown();
  
  ChannelFuture shutdown(ChannelPromise paramChannelPromise);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\DuplexChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */