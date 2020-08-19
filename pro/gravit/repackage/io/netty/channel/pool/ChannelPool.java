package pro.gravit.repackage.io.netty.channel.pool;

import java.io.Closeable;
import pro.gravit.repackage.io.netty.channel.Channel;
import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.Promise;

public interface ChannelPool extends Closeable {
  Future<Channel> acquire();
  
  Future<Channel> acquire(Promise<Channel> paramPromise);
  
  Future<Void> release(Channel paramChannel);
  
  Future<Void> release(Channel paramChannel, Promise<Void> paramPromise);
  
  void close();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\ChannelPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */