/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.PreferHeapByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DefaultSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannelConfig;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class DefaultOioSocketChannelConfig
/*     */   extends DefaultSocketChannelConfig
/*     */   implements OioSocketChannelConfig
/*     */ {
/*     */   @Deprecated
/*     */   public DefaultOioSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
/*  43 */     super(channel, javaSocket);
/*  44 */     setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(getAllocator()));
/*     */   }
/*     */   
/*     */   DefaultOioSocketChannelConfig(OioSocketChannel channel, Socket javaSocket) {
/*  48 */     super(channel, javaSocket);
/*  49 */     setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(getAllocator()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  54 */     return getOptions(super
/*  55 */         .getOptions(), new ChannelOption[] { ChannelOption.SO_TIMEOUT });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  61 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  62 */       return (T)Integer.valueOf(getSoTimeout());
/*     */     }
/*  64 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/*  69 */     validate(option, value);
/*     */     
/*  71 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  72 */       setSoTimeout(((Integer)value).intValue());
/*     */     } else {
/*  74 */       return super.setOption(option, value);
/*     */     } 
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setSoTimeout(int timeout) {
/*     */     try {
/*  82 */       this.javaSocket.setSoTimeout(timeout);
/*  83 */     } catch (IOException e) {
/*  84 */       throw new ChannelException(e);
/*     */     } 
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSoTimeout() {
/*     */     try {
/*  92 */       return this.javaSocket.getSoTimeout();
/*  93 */     } catch (IOException e) {
/*  94 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
/* 100 */     super.setTcpNoDelay(tcpNoDelay);
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setSoLinger(int soLinger) {
/* 106 */     super.setSoLinger(soLinger);
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setSendBufferSize(int sendBufferSize) {
/* 112 */     super.setSendBufferSize(sendBufferSize);
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/* 118 */     super.setReceiveBufferSize(receiveBufferSize);
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setKeepAlive(boolean keepAlive) {
/* 124 */     super.setKeepAlive(keepAlive);
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setTrafficClass(int trafficClass) {
/* 130 */     super.setTrafficClass(trafficClass);
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setReuseAddress(boolean reuseAddress) {
/* 136 */     super.setReuseAddress(reuseAddress);
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
/* 142 */     super.setPerformancePreferences(connectionTime, latency, bandwidth);
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
/* 148 */     super.setAllowHalfClosure(allowHalfClosure);
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 154 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public OioSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 161 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 167 */     super.setWriteSpinCount(writeSpinCount);
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 173 */     super.setAllocator(allocator);
/* 174 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 179 */     super.setRecvByteBufAllocator(allocator);
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setAutoRead(boolean autoRead) {
/* 185 */     super.setAutoRead(autoRead);
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void autoReadCleared() {
/* 191 */     if (this.channel instanceof OioSocketChannel) {
/* 192 */       ((OioSocketChannel)this.channel).clearReadPending0();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setAutoClose(boolean autoClose) {
/* 198 */     super.setAutoClose(autoClose);
/* 199 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 204 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 210 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 216 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 217 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 222 */     super.setMessageSizeEstimator(estimator);
/* 223 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\DefaultOioSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */