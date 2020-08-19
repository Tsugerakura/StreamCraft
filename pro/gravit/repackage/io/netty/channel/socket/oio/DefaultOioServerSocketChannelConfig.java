/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.PreferHeapByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DefaultServerSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannelConfig;
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
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class DefaultOioServerSocketChannelConfig
/*     */   extends DefaultServerSocketChannelConfig
/*     */   implements OioServerSocketChannelConfig
/*     */ {
/*     */   @Deprecated
/*     */   public DefaultOioServerSocketChannelConfig(ServerSocketChannel channel, ServerSocket javaSocket) {
/*  45 */     super(channel, javaSocket);
/*  46 */     setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(getAllocator()));
/*     */   }
/*     */   
/*     */   DefaultOioServerSocketChannelConfig(OioServerSocketChannel channel, ServerSocket javaSocket) {
/*  50 */     super(channel, javaSocket);
/*  51 */     setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(getAllocator()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  56 */     return getOptions(super
/*  57 */         .getOptions(), new ChannelOption[] { ChannelOption.SO_TIMEOUT });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  63 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  64 */       return (T)Integer.valueOf(getSoTimeout());
/*     */     }
/*  66 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/*  71 */     validate(option, value);
/*     */     
/*  73 */     if (option == ChannelOption.SO_TIMEOUT) {
/*  74 */       setSoTimeout(((Integer)value).intValue());
/*     */     } else {
/*  76 */       return super.setOption(option, value);
/*     */     } 
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setSoTimeout(int timeout) {
/*     */     try {
/*  84 */       this.javaSocket.setSoTimeout(timeout);
/*  85 */     } catch (IOException e) {
/*  86 */       throw new ChannelException(e);
/*     */     } 
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSoTimeout() {
/*     */     try {
/*  94 */       return this.javaSocket.getSoTimeout();
/*  95 */     } catch (IOException e) {
/*  96 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setBacklog(int backlog) {
/* 102 */     super.setBacklog(backlog);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
/* 108 */     super.setReuseAddress(reuseAddress);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/* 114 */     super.setReceiveBufferSize(receiveBufferSize);
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
/* 120 */     super.setPerformancePreferences(connectionTime, latency, bandwidth);
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 126 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public OioServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 133 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 139 */     super.setWriteSpinCount(writeSpinCount);
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 145 */     super.setAllocator(allocator);
/* 146 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 151 */     super.setRecvByteBufAllocator(allocator);
/* 152 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setAutoRead(boolean autoRead) {
/* 157 */     super.setAutoRead(autoRead);
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void autoReadCleared() {
/* 163 */     if (this.channel instanceof OioServerSocketChannel) {
/* 164 */       ((OioServerSocketChannel)this.channel).clearReadPending0();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setAutoClose(boolean autoClose) {
/* 170 */     super.setAutoClose(autoClose);
/* 171 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 176 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 182 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 188 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 189 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 194 */     super.setMessageSizeEstimator(estimator);
/* 195 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\DefaultOioServerSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */