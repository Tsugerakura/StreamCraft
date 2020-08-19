package pro.gravit.repackage.io.netty.channel.socket.oio;

import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
import pro.gravit.repackage.io.netty.channel.socket.SocketChannelConfig;

@Deprecated
public interface OioSocketChannelConfig extends SocketChannelConfig {
  OioSocketChannelConfig setSoTimeout(int paramInt);
  
  int getSoTimeout();
  
  OioSocketChannelConfig setTcpNoDelay(boolean paramBoolean);
  
  OioSocketChannelConfig setSoLinger(int paramInt);
  
  OioSocketChannelConfig setSendBufferSize(int paramInt);
  
  OioSocketChannelConfig setReceiveBufferSize(int paramInt);
  
  OioSocketChannelConfig setKeepAlive(boolean paramBoolean);
  
  OioSocketChannelConfig setTrafficClass(int paramInt);
  
  OioSocketChannelConfig setReuseAddress(boolean paramBoolean);
  
  OioSocketChannelConfig setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3);
  
  OioSocketChannelConfig setAllowHalfClosure(boolean paramBoolean);
  
  OioSocketChannelConfig setConnectTimeoutMillis(int paramInt);
  
  @Deprecated
  OioSocketChannelConfig setMaxMessagesPerRead(int paramInt);
  
  OioSocketChannelConfig setWriteSpinCount(int paramInt);
  
  OioSocketChannelConfig setAllocator(ByteBufAllocator paramByteBufAllocator);
  
  OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator paramRecvByteBufAllocator);
  
  OioSocketChannelConfig setAutoRead(boolean paramBoolean);
  
  OioSocketChannelConfig setAutoClose(boolean paramBoolean);
  
  OioSocketChannelConfig setWriteBufferHighWaterMark(int paramInt);
  
  OioSocketChannelConfig setWriteBufferLowWaterMark(int paramInt);
  
  OioSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark paramWriteBufferWaterMark);
  
  OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator paramMessageSizeEstimator);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\OioSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */