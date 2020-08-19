/*     */ package pro.gravit.repackage.io.netty.channel.socket;
/*     */ 
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.util.NetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ 
/*     */ public class DefaultServerSocketChannelConfig
/*     */   extends DefaultChannelConfig
/*     */   implements ServerSocketChannelConfig
/*     */ {
/*     */   protected final ServerSocket javaSocket;
/*  44 */   private volatile int backlog = NetUtil.SOMAXCONN;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultServerSocketChannelConfig(ServerSocketChannel channel, ServerSocket javaSocket) {
/*  50 */     super((Channel)channel);
/*  51 */     this.javaSocket = (ServerSocket)ObjectUtil.checkNotNull(javaSocket, "javaSocket");
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  56 */     return getOptions(super.getOptions(), new ChannelOption[] { ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  62 */     if (option == ChannelOption.SO_RCVBUF) {
/*  63 */       return (T)Integer.valueOf(getReceiveBufferSize());
/*     */     }
/*  65 */     if (option == ChannelOption.SO_REUSEADDR) {
/*  66 */       return (T)Boolean.valueOf(isReuseAddress());
/*     */     }
/*  68 */     if (option == ChannelOption.SO_BACKLOG) {
/*  69 */       return (T)Integer.valueOf(getBacklog());
/*     */     }
/*     */     
/*  72 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/*  77 */     validate(option, value);
/*     */     
/*  79 */     if (option == ChannelOption.SO_RCVBUF) {
/*  80 */       setReceiveBufferSize(((Integer)value).intValue());
/*  81 */     } else if (option == ChannelOption.SO_REUSEADDR) {
/*  82 */       setReuseAddress(((Boolean)value).booleanValue());
/*  83 */     } else if (option == ChannelOption.SO_BACKLOG) {
/*  84 */       setBacklog(((Integer)value).intValue());
/*     */     } else {
/*  86 */       return super.setOption(option, value);
/*     */     } 
/*     */     
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReuseAddress() {
/*     */     try {
/*  95 */       return this.javaSocket.getReuseAddress();
/*  96 */     } catch (SocketException e) {
/*  97 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
/*     */     try {
/* 104 */       this.javaSocket.setReuseAddress(reuseAddress);
/* 105 */     } catch (SocketException e) {
/* 106 */       throw new ChannelException(e);
/*     */     } 
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReceiveBufferSize() {
/*     */     try {
/* 114 */       return this.javaSocket.getReceiveBufferSize();
/* 115 */     } catch (SocketException e) {
/* 116 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/*     */     try {
/* 123 */       this.javaSocket.setReceiveBufferSize(receiveBufferSize);
/* 124 */     } catch (SocketException e) {
/* 125 */       throw new ChannelException(e);
/*     */     } 
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
/* 132 */     this.javaSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
/* 133 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBacklog() {
/* 138 */     return this.backlog;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setBacklog(int backlog) {
/* 143 */     ObjectUtil.checkPositiveOrZero(backlog, "backlog");
/* 144 */     this.backlog = backlog;
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 150 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 157 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 163 */     super.setWriteSpinCount(writeSpinCount);
/* 164 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 169 */     super.setAllocator(allocator);
/* 170 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 175 */     super.setRecvByteBufAllocator(allocator);
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setAutoRead(boolean autoRead) {
/* 181 */     super.setAutoRead(autoRead);
/* 182 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 187 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 188 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 193 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 199 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 205 */     super.setMessageSizeEstimator(estimator);
/* 206 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\DefaultServerSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */