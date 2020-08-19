/*     */ package pro.gravit.repackage.io.netty.channel.socket;
/*     */ 
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ 
/*     */ public class DefaultSocketChannelConfig
/*     */   extends DefaultChannelConfig
/*     */   implements SocketChannelConfig
/*     */ {
/*     */   protected final Socket javaSocket;
/*     */   private volatile boolean allowHalfClosure;
/*     */   
/*     */   public DefaultSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
/*  47 */     super(channel);
/*  48 */     this.javaSocket = (Socket)ObjectUtil.checkNotNull(javaSocket, "javaSocket");
/*     */ 
/*     */     
/*  51 */     if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
/*     */       try {
/*  53 */         setTcpNoDelay(true);
/*  54 */       } catch (Exception exception) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  62 */     return getOptions(super
/*  63 */         .getOptions(), new ChannelOption[] { ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  71 */     if (option == ChannelOption.SO_RCVBUF) {
/*  72 */       return (T)Integer.valueOf(getReceiveBufferSize());
/*     */     }
/*  74 */     if (option == ChannelOption.SO_SNDBUF) {
/*  75 */       return (T)Integer.valueOf(getSendBufferSize());
/*     */     }
/*  77 */     if (option == ChannelOption.TCP_NODELAY) {
/*  78 */       return (T)Boolean.valueOf(isTcpNoDelay());
/*     */     }
/*  80 */     if (option == ChannelOption.SO_KEEPALIVE) {
/*  81 */       return (T)Boolean.valueOf(isKeepAlive());
/*     */     }
/*  83 */     if (option == ChannelOption.SO_REUSEADDR) {
/*  84 */       return (T)Boolean.valueOf(isReuseAddress());
/*     */     }
/*  86 */     if (option == ChannelOption.SO_LINGER) {
/*  87 */       return (T)Integer.valueOf(getSoLinger());
/*     */     }
/*  89 */     if (option == ChannelOption.IP_TOS) {
/*  90 */       return (T)Integer.valueOf(getTrafficClass());
/*     */     }
/*  92 */     if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
/*  93 */       return (T)Boolean.valueOf(isAllowHalfClosure());
/*     */     }
/*     */     
/*  96 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/* 101 */     validate(option, value);
/*     */     
/* 103 */     if (option == ChannelOption.SO_RCVBUF) {
/* 104 */       setReceiveBufferSize(((Integer)value).intValue());
/* 105 */     } else if (option == ChannelOption.SO_SNDBUF) {
/* 106 */       setSendBufferSize(((Integer)value).intValue());
/* 107 */     } else if (option == ChannelOption.TCP_NODELAY) {
/* 108 */       setTcpNoDelay(((Boolean)value).booleanValue());
/* 109 */     } else if (option == ChannelOption.SO_KEEPALIVE) {
/* 110 */       setKeepAlive(((Boolean)value).booleanValue());
/* 111 */     } else if (option == ChannelOption.SO_REUSEADDR) {
/* 112 */       setReuseAddress(((Boolean)value).booleanValue());
/* 113 */     } else if (option == ChannelOption.SO_LINGER) {
/* 114 */       setSoLinger(((Integer)value).intValue());
/* 115 */     } else if (option == ChannelOption.IP_TOS) {
/* 116 */       setTrafficClass(((Integer)value).intValue());
/* 117 */     } else if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
/* 118 */       setAllowHalfClosure(((Boolean)value).booleanValue());
/*     */     } else {
/* 120 */       return super.setOption(option, value);
/*     */     } 
/*     */     
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReceiveBufferSize() {
/*     */     try {
/* 129 */       return this.javaSocket.getReceiveBufferSize();
/* 130 */     } catch (SocketException e) {
/* 131 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSendBufferSize() {
/*     */     try {
/* 138 */       return this.javaSocket.getSendBufferSize();
/* 139 */     } catch (SocketException e) {
/* 140 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSoLinger() {
/*     */     try {
/* 147 */       return this.javaSocket.getSoLinger();
/* 148 */     } catch (SocketException e) {
/* 149 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTrafficClass() {
/*     */     try {
/* 156 */       return this.javaSocket.getTrafficClass();
/* 157 */     } catch (SocketException e) {
/* 158 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isKeepAlive() {
/*     */     try {
/* 165 */       return this.javaSocket.getKeepAlive();
/* 166 */     } catch (SocketException e) {
/* 167 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReuseAddress() {
/*     */     try {
/* 174 */       return this.javaSocket.getReuseAddress();
/* 175 */     } catch (SocketException e) {
/* 176 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTcpNoDelay() {
/*     */     try {
/* 183 */       return this.javaSocket.getTcpNoDelay();
/* 184 */     } catch (SocketException e) {
/* 185 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setKeepAlive(boolean keepAlive) {
/*     */     try {
/* 192 */       this.javaSocket.setKeepAlive(keepAlive);
/* 193 */     } catch (SocketException e) {
/* 194 */       throw new ChannelException(e);
/*     */     } 
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
/* 202 */     this.javaSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/*     */     try {
/* 209 */       this.javaSocket.setReceiveBufferSize(receiveBufferSize);
/* 210 */     } catch (SocketException e) {
/* 211 */       throw new ChannelException(e);
/*     */     } 
/* 213 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setReuseAddress(boolean reuseAddress) {
/*     */     try {
/* 219 */       this.javaSocket.setReuseAddress(reuseAddress);
/* 220 */     } catch (SocketException e) {
/* 221 */       throw new ChannelException(e);
/*     */     } 
/* 223 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setSendBufferSize(int sendBufferSize) {
/*     */     try {
/* 229 */       this.javaSocket.setSendBufferSize(sendBufferSize);
/* 230 */     } catch (SocketException e) {
/* 231 */       throw new ChannelException(e);
/*     */     } 
/* 233 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setSoLinger(int soLinger) {
/*     */     try {
/* 239 */       if (soLinger < 0) {
/* 240 */         this.javaSocket.setSoLinger(false, 0);
/*     */       } else {
/* 242 */         this.javaSocket.setSoLinger(true, soLinger);
/*     */       } 
/* 244 */     } catch (SocketException e) {
/* 245 */       throw new ChannelException(e);
/*     */     } 
/* 247 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
/*     */     try {
/* 253 */       this.javaSocket.setTcpNoDelay(tcpNoDelay);
/* 254 */     } catch (SocketException e) {
/* 255 */       throw new ChannelException(e);
/*     */     } 
/* 257 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setTrafficClass(int trafficClass) {
/*     */     try {
/* 263 */       this.javaSocket.setTrafficClass(trafficClass);
/* 264 */     } catch (SocketException e) {
/* 265 */       throw new ChannelException(e);
/*     */     } 
/* 267 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAllowHalfClosure() {
/* 272 */     return this.allowHalfClosure;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
/* 277 */     this.allowHalfClosure = allowHalfClosure;
/* 278 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 283 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 284 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 290 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 296 */     super.setWriteSpinCount(writeSpinCount);
/* 297 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 302 */     super.setAllocator(allocator);
/* 303 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 308 */     super.setRecvByteBufAllocator(allocator);
/* 309 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setAutoRead(boolean autoRead) {
/* 314 */     super.setAutoRead(autoRead);
/* 315 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setAutoClose(boolean autoClose) {
/* 320 */     super.setAutoClose(autoClose);
/* 321 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 326 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 327 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 332 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 333 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 338 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 339 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 344 */     super.setMessageSizeEstimator(estimator);
/* 345 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\DefaultSocketChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */