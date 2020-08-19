package pro.gravit.repackage.io.netty.channel;

public interface ChannelInboundInvoker {
  ChannelInboundInvoker fireChannelRegistered();
  
  ChannelInboundInvoker fireChannelUnregistered();
  
  ChannelInboundInvoker fireChannelActive();
  
  ChannelInboundInvoker fireChannelInactive();
  
  ChannelInboundInvoker fireExceptionCaught(Throwable paramThrowable);
  
  ChannelInboundInvoker fireUserEventTriggered(Object paramObject);
  
  ChannelInboundInvoker fireChannelRead(Object paramObject);
  
  ChannelInboundInvoker fireChannelReadComplete();
  
  ChannelInboundInvoker fireChannelWritabilityChanged();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelInboundInvoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */