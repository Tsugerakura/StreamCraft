package pro.gravit.repackage.io.netty.channel.socket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.ChannelConfig;
import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;

public interface DatagramChannelConfig extends ChannelConfig {
  int getSendBufferSize();
  
  DatagramChannelConfig setSendBufferSize(int paramInt);
  
  int getReceiveBufferSize();
  
  DatagramChannelConfig setReceiveBufferSize(int paramInt);
  
  int getTrafficClass();
  
  DatagramChannelConfig setTrafficClass(int paramInt);
  
  boolean isReuseAddress();
  
  DatagramChannelConfig setReuseAddress(boolean paramBoolean);
  
  boolean isBroadcast();
  
  DatagramChannelConfig setBroadcast(boolean paramBoolean);
  
  boolean isLoopbackModeDisabled();
  
  DatagramChannelConfig setLoopbackModeDisabled(boolean paramBoolean);
  
  int getTimeToLive();
  
  DatagramChannelConfig setTimeToLive(int paramInt);
  
  InetAddress getInterface();
  
  DatagramChannelConfig setInterface(InetAddress paramInetAddress);
  
  NetworkInterface getNetworkInterface();
  
  DatagramChannelConfig setNetworkInterface(NetworkInterface paramNetworkInterface);
  
  @Deprecated
  DatagramChannelConfig setMaxMessagesPerRead(int paramInt);
  
  DatagramChannelConfig setWriteSpinCount(int paramInt);
  
  DatagramChannelConfig setConnectTimeoutMillis(int paramInt);
  
  DatagramChannelConfig setAllocator(ByteBufAllocator paramByteBufAllocator);
  
  DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator paramRecvByteBufAllocator);
  
  DatagramChannelConfig setAutoRead(boolean paramBoolean);
  
  DatagramChannelConfig setAutoClose(boolean paramBoolean);
  
  DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator paramMessageSizeEstimator);
  
  DatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark paramWriteBufferWaterMark);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\DatagramChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */