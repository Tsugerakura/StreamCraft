package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.util.Attribute;
import pro.gravit.repackage.io.netty.util.AttributeKey;
import pro.gravit.repackage.io.netty.util.AttributeMap;
import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;

public interface ChannelHandlerContext extends AttributeMap, ChannelInboundInvoker, ChannelOutboundInvoker {
  Channel channel();
  
  EventExecutor executor();
  
  String name();
  
  ChannelHandler handler();
  
  boolean isRemoved();
  
  ChannelHandlerContext fireChannelRegistered();
  
  ChannelHandlerContext fireChannelUnregistered();
  
  ChannelHandlerContext fireChannelActive();
  
  ChannelHandlerContext fireChannelInactive();
  
  ChannelHandlerContext fireExceptionCaught(Throwable paramThrowable);
  
  ChannelHandlerContext fireUserEventTriggered(Object paramObject);
  
  ChannelHandlerContext fireChannelRead(Object paramObject);
  
  ChannelHandlerContext fireChannelReadComplete();
  
  ChannelHandlerContext fireChannelWritabilityChanged();
  
  ChannelHandlerContext read();
  
  ChannelHandlerContext flush();
  
  ChannelPipeline pipeline();
  
  ByteBufAllocator alloc();
  
  @Deprecated
  <T> Attribute<T> attr(AttributeKey<T> paramAttributeKey);
  
  @Deprecated
  <T> boolean hasAttr(AttributeKey<T> paramAttributeKey);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelHandlerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */