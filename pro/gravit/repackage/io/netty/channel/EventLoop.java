package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.OrderedEventExecutor;

public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
  EventLoopGroup parent();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\EventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */