package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorGroup;

public interface EventLoopGroup extends EventExecutorGroup {
  EventLoop next();
  
  ChannelFuture register(Channel paramChannel);
  
  ChannelFuture register(ChannelPromise paramChannelPromise);
  
  @Deprecated
  ChannelFuture register(Channel paramChannel, ChannelPromise paramChannelPromise);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\EventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */