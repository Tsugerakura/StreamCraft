package pro.gravit.repackage.io.netty.channel.socket.oio;

import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannelConfig;

@Deprecated
public interface OioServerSocketChannelConfig extends ServerSocketChannelConfig {
  OioServerSocketChannelConfig setSoTimeout(int paramInt);
  
  int getSoTimeout();
  
  OioServerSocketChannelConfig setBacklog(int paramInt);
  
  OioServerSocketChannelConfig setReuseAddress(boolean paramBoolean);
  
  OioServerSocketChannelConfig setReceiveBufferSize(int paramInt);
  
  OioServerSocketChannelConfig setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3);
  
  OioServerSocketChannelConfig setConnectTimeoutMillis(int paramInt);
  
  @Deprecated
  OioServerSocketChannelConfig setMaxMessagesPerRead(int paramInt);
  
  OioServerSocketChannelConfig setWriteSpinCount(int paramInt);
  
  OioServerSocketChannelConfig setAllocator(ByteBufAllocator paramByteBufAllocator);
  
  OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator paramRecvByteBufAllocator);
  
  OioServerSocketChannelConfig setAutoRead(boolean paramBoolean);
  
  OioServerSocketChannelConfig setAutoClose(boolean paramBoolean);
  
  OioServerSocketChannelConfig setWriteBufferHighWaterMark(int paramInt);
  
  OioServerSocketChannelConfig setWriteBufferLowWaterMark(int paramInt);
  
  OioServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark paramWriteBufferWaterMark);
  
  OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator paramMessageSizeEstimator);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\OioServerSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */