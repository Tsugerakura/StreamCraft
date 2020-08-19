package pro.gravit.repackage.io.netty.resolver;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.List;
import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.Promise;

public interface AddressResolver<T extends SocketAddress> extends Closeable {
  boolean isSupported(SocketAddress paramSocketAddress);
  
  boolean isResolved(SocketAddress paramSocketAddress);
  
  Future<T> resolve(SocketAddress paramSocketAddress);
  
  Future<T> resolve(SocketAddress paramSocketAddress, Promise<T> paramPromise);
  
  Future<List<T>> resolveAll(SocketAddress paramSocketAddress);
  
  Future<List<T>> resolveAll(SocketAddress paramSocketAddress, Promise<List<T>> paramPromise);
  
  void close();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\AddressResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */