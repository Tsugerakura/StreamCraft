package pro.gravit.repackage.io.netty.channel;

import java.net.SocketAddress;
import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.util.AttributeMap;

public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {
  ChannelId id();
  
  EventLoop eventLoop();
  
  Channel parent();
  
  ChannelConfig config();
  
  boolean isOpen();
  
  boolean isRegistered();
  
  boolean isActive();
  
  ChannelMetadata metadata();
  
  SocketAddress localAddress();
  
  SocketAddress remoteAddress();
  
  ChannelFuture closeFuture();
  
  boolean isWritable();
  
  long bytesBeforeUnwritable();
  
  long bytesBeforeWritable();
  
  Unsafe unsafe();
  
  ChannelPipeline pipeline();
  
  ByteBufAllocator alloc();
  
  Channel read();
  
  Channel flush();
  
  public static interface Unsafe {
    RecvByteBufAllocator.Handle recvBufAllocHandle();
    
    SocketAddress localAddress();
    
    SocketAddress remoteAddress();
    
    void register(EventLoop param1EventLoop, ChannelPromise param1ChannelPromise);
    
    void bind(SocketAddress param1SocketAddress, ChannelPromise param1ChannelPromise);
    
    void connect(SocketAddress param1SocketAddress1, SocketAddress param1SocketAddress2, ChannelPromise param1ChannelPromise);
    
    void disconnect(ChannelPromise param1ChannelPromise);
    
    void close(ChannelPromise param1ChannelPromise);
    
    void closeForcibly();
    
    void deregister(ChannelPromise param1ChannelPromise);
    
    void beginRead();
    
    void write(Object param1Object, ChannelPromise param1ChannelPromise);
    
    void flush();
    
    ChannelPromise voidPromise();
    
    ChannelOutboundBuffer outboundBuffer();
  }
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\Channel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */