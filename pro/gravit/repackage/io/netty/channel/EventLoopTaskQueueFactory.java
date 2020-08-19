package pro.gravit.repackage.io.netty.channel;

import java.util.Queue;

public interface EventLoopTaskQueueFactory {
  Queue<Runnable> newTaskQueue(int paramInt);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\EventLoopTaskQueueFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */