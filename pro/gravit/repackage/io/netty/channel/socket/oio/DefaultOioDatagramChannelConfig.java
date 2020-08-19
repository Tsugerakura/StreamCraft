/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.PreferHeapByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DefaultDatagramChannelConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DefaultOioDatagramChannelConfig
/*     */   extends DefaultDatagramChannelConfig
/*     */   implements OioDatagramChannelConfig
/*     */ {
/*     */   DefaultOioDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
/*  39 */     super(channel, javaSocket);
/*  40 */     setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(getAllocator()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  45 */     return getOptions(super.getOptions(), new ChannelOption[] { ChannelOption.SO_TIMEOUT });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  51 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  52 */       return (T)Integer.valueOf(getSoTimeout());
/*     */     }
/*  54 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/*  59 */     validate(option, value);
/*     */     
/*  61 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  62 */       setSoTimeout(((Integer)value).intValue());
/*     */     } else {
/*  64 */       return super.setOption(option, value);
/*     */     } 
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setSoTimeout(int timeout) {
/*     */     try {
/*  72 */       javaSocket().setSoTimeout(timeout);
/*  73 */     } catch (IOException e) {
/*  74 */       throw new ChannelException(e);
/*     */     } 
/*  76 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSoTimeout() {
/*     */     try {
/*  82 */       return javaSocket().getSoTimeout();
/*  83 */     } catch (IOException e) {
/*  84 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setBroadcast(boolean broadcast) {
/*  90 */     super.setBroadcast(broadcast);
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
/*  96 */     super.setInterface(interfaceAddress);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
/* 102 */     super.setLoopbackModeDisabled(loopbackModeDisabled);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
/* 108 */     super.setNetworkInterface(networkInterface);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
/* 114 */     super.setReuseAddress(reuseAddress);
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/* 120 */     super.setReceiveBufferSize(receiveBufferSize);
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
/* 126 */     super.setSendBufferSize(sendBufferSize);
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setTimeToLive(int ttl) {
/* 132 */     super.setTimeToLive(ttl);
/* 133 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setTrafficClass(int trafficClass) {
/* 138 */     super.setTrafficClass(trafficClass);
/* 139 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 144 */     super.setWriteSpinCount(writeSpinCount);
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 150 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 156 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 157 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 162 */     super.setAllocator(allocator);
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 168 */     super.setRecvByteBufAllocator(allocator);
/* 169 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setAutoRead(boolean autoRead) {
/* 174 */     super.setAutoRead(autoRead);
/* 175 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setAutoClose(boolean autoClose) {
/* 180 */     super.setAutoClose(autoClose);
/* 181 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 186 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 192 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 193 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 198 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 199 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 204 */     super.setMessageSizeEstimator(estimator);
/* 205 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\DefaultOioDatagramChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */