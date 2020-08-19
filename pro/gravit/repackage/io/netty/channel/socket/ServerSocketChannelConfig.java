package pro.gravit.repackage.io.netty.channel.socket;

import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.ChannelConfig;
import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;

public interface ServerSocketChannelConfig extends ChannelConfig {
  int getBacklog();
  
  ServerSocketChannelConfig setBacklog(int paramInt);
  
  boolean isReuseAddress();
  
  ServerSocketChannelConfig setReuseAddress(boolean paramBoolean);
  
  int getReceiveBufferSize();
  
  ServerSocketChannelConfig setReceiveBufferSize(int paramInt);
  
  ServerSocketChannelConfig setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3);
  
  ServerSocketChannelConfig setConnectTimeoutMillis(int paramInt);
  
  @Deprecated
  ServerSocketChannelConfig setMaxMessagesPerRead(int paramInt);
  
  ServerSocketChannelConfig setWriteSpinCount(int paramInt);
  
  ServerSocketChannelConfig setAllocator(ByteBufAllocator paramByteBufAllocator);
  
  ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator paramRecvByteBufAllocator);
  
  ServerSocketChannelConfig setAutoRead(boolean paramBoolean);
  
  ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator paramMessageSizeEstimator);
  
  ServerSocketChannelConfig setWriteBufferHighWaterMark(int paramInt);
  
  ServerSocketChannelConfig setWriteBufferLowWaterMark(int paramInt);
  
  ServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark paramWriteBufferWaterMark);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\ServerSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */