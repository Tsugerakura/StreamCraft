package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.AbstractEventExecutorGroup;
import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;

public abstract class AbstractEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {
  public abstract EventLoop next();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AbstractEventLoopGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */