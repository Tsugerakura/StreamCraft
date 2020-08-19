/*     */ package pro.gravit.repackage.io.netty.channel.socket;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MulticastSocket;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.FixedRecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.MessageSizeEstimator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.WriteBufferWaterMark;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ public class DefaultDatagramChannelConfig
/*     */   extends DefaultChannelConfig
/*     */   implements DatagramChannelConfig
/*     */ {
/*  46 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDatagramChannelConfig.class);
/*     */ 
/*     */   
/*     */   private final DatagramSocket javaSocket;
/*     */   
/*     */   private volatile boolean activeOnOpen;
/*     */ 
/*     */   
/*     */   public DefaultDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
/*  55 */     super(channel, (RecvByteBufAllocator)new FixedRecvByteBufAllocator(2048));
/*  56 */     this.javaSocket = (DatagramSocket)ObjectUtil.checkNotNull(javaSocket, "javaSocket");
/*     */   }
/*     */   
/*     */   protected final DatagramSocket javaSocket() {
/*  60 */     return this.javaSocket;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  66 */     return getOptions(super
/*  67 */         .getOptions(), new ChannelOption[] { ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/*  75 */     if (option == ChannelOption.SO_BROADCAST) {
/*  76 */       return (T)Boolean.valueOf(isBroadcast());
/*     */     }
/*  78 */     if (option == ChannelOption.SO_RCVBUF) {
/*  79 */       return (T)Integer.valueOf(getReceiveBufferSize());
/*     */     }
/*  81 */     if (option == ChannelOption.SO_SNDBUF) {
/*  82 */       return (T)Integer.valueOf(getSendBufferSize());
/*     */     }
/*  84 */     if (option == ChannelOption.SO_REUSEADDR) {
/*  85 */       return (T)Boolean.valueOf(isReuseAddress());
/*     */     }
/*  87 */     if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
/*  88 */       return (T)Boolean.valueOf(isLoopbackModeDisabled());
/*     */     }
/*  90 */     if (option == ChannelOption.IP_MULTICAST_ADDR) {
/*  91 */       return (T)getInterface();
/*     */     }
/*  93 */     if (option == ChannelOption.IP_MULTICAST_IF) {
/*  94 */       return (T)getNetworkInterface();
/*     */     }
/*  96 */     if (option == ChannelOption.IP_MULTICAST_TTL) {
/*  97 */       return (T)Integer.valueOf(getTimeToLive());
/*     */     }
/*  99 */     if (option == ChannelOption.IP_TOS) {
/* 100 */       return (T)Integer.valueOf(getTrafficClass());
/*     */     }
/* 102 */     if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
/* 103 */       return (T)Boolean.valueOf(this.activeOnOpen);
/*     */     }
/* 105 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/* 111 */     validate(option, value);
/*     */     
/* 113 */     if (option == ChannelOption.SO_BROADCAST) {
/* 114 */       setBroadcast(((Boolean)value).booleanValue());
/* 115 */     } else if (option == ChannelOption.SO_RCVBUF) {
/* 116 */       setReceiveBufferSize(((Integer)value).intValue());
/* 117 */     } else if (option == ChannelOption.SO_SNDBUF) {
/* 118 */       setSendBufferSize(((Integer)value).intValue());
/* 119 */     } else if (option == ChannelOption.SO_REUSEADDR) {
/* 120 */       setReuseAddress(((Boolean)value).booleanValue());
/* 121 */     } else if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
/* 122 */       setLoopbackModeDisabled(((Boolean)value).booleanValue());
/* 123 */     } else if (option == ChannelOption.IP_MULTICAST_ADDR) {
/* 124 */       setInterface((InetAddress)value);
/* 125 */     } else if (option == ChannelOption.IP_MULTICAST_IF) {
/* 126 */       setNetworkInterface((NetworkInterface)value);
/* 127 */     } else if (option == ChannelOption.IP_MULTICAST_TTL) {
/* 128 */       setTimeToLive(((Integer)value).intValue());
/* 129 */     } else if (option == ChannelOption.IP_TOS) {
/* 130 */       setTrafficClass(((Integer)value).intValue());
/* 131 */     } else if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
/* 132 */       setActiveOnOpen(((Boolean)value).booleanValue());
/*     */     } else {
/* 134 */       return super.setOption(option, value);
/*     */     } 
/*     */     
/* 137 */     return true;
/*     */   }
/*     */   
/*     */   private void setActiveOnOpen(boolean activeOnOpen) {
/* 141 */     if (this.channel.isRegistered()) {
/* 142 */       throw new IllegalStateException("Can only changed before channel was registered");
/*     */     }
/* 144 */     this.activeOnOpen = activeOnOpen;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBroadcast() {
/*     */     try {
/* 150 */       return this.javaSocket.getBroadcast();
/* 151 */     } catch (SocketException e) {
/* 152 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setBroadcast(boolean broadcast) {
/*     */     try {
/* 160 */       if (broadcast && 
/* 161 */         !this.javaSocket.getLocalAddress().isAnyLocalAddress() && 
/* 162 */         !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser())
/*     */       {
/*     */         
/* 165 */         logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; setting the SO_BROADCAST flag anyway as requested on the socket which is bound to " + this.javaSocket
/*     */ 
/*     */ 
/*     */             
/* 169 */             .getLocalSocketAddress() + '.');
/*     */       }
/*     */       
/* 172 */       this.javaSocket.setBroadcast(broadcast);
/* 173 */     } catch (SocketException e) {
/* 174 */       throw new ChannelException(e);
/*     */     } 
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getInterface() {
/* 181 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 183 */         return ((MulticastSocket)this.javaSocket).getInterface();
/* 184 */       } catch (SocketException e) {
/* 185 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 188 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
/* 194 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 196 */         ((MulticastSocket)this.javaSocket).setInterface(interfaceAddress);
/* 197 */       } catch (SocketException e) {
/* 198 */         throw new ChannelException(e);
/*     */       } 
/*     */     } else {
/* 201 */       throw new UnsupportedOperationException();
/*     */     } 
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLoopbackModeDisabled() {
/* 208 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 210 */         return ((MulticastSocket)this.javaSocket).getLoopbackMode();
/* 211 */       } catch (SocketException e) {
/* 212 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 215 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
/* 221 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 223 */         ((MulticastSocket)this.javaSocket).setLoopbackMode(loopbackModeDisabled);
/* 224 */       } catch (SocketException e) {
/* 225 */         throw new ChannelException(e);
/*     */       } 
/*     */     } else {
/* 228 */       throw new UnsupportedOperationException();
/*     */     } 
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkInterface getNetworkInterface() {
/* 235 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 237 */         return ((MulticastSocket)this.javaSocket).getNetworkInterface();
/* 238 */       } catch (SocketException e) {
/* 239 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 242 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
/* 248 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 250 */         ((MulticastSocket)this.javaSocket).setNetworkInterface(networkInterface);
/* 251 */       } catch (SocketException e) {
/* 252 */         throw new ChannelException(e);
/*     */       } 
/*     */     } else {
/* 255 */       throw new UnsupportedOperationException();
/*     */     } 
/* 257 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReuseAddress() {
/*     */     try {
/* 263 */       return this.javaSocket.getReuseAddress();
/* 264 */     } catch (SocketException e) {
/* 265 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setReuseAddress(boolean reuseAddress) {
/*     */     try {
/* 272 */       this.javaSocket.setReuseAddress(reuseAddress);
/* 273 */     } catch (SocketException e) {
/* 274 */       throw new ChannelException(e);
/*     */     } 
/* 276 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReceiveBufferSize() {
/*     */     try {
/* 282 */       return this.javaSocket.getReceiveBufferSize();
/* 283 */     } catch (SocketException e) {
/* 284 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
/*     */     try {
/* 291 */       this.javaSocket.setReceiveBufferSize(receiveBufferSize);
/* 292 */     } catch (SocketException e) {
/* 293 */       throw new ChannelException(e);
/*     */     } 
/* 295 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSendBufferSize() {
/*     */     try {
/* 301 */       return this.javaSocket.getSendBufferSize();
/* 302 */     } catch (SocketException e) {
/* 303 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setSendBufferSize(int sendBufferSize) {
/*     */     try {
/* 310 */       this.javaSocket.setSendBufferSize(sendBufferSize);
/* 311 */     } catch (SocketException e) {
/* 312 */       throw new ChannelException(e);
/*     */     } 
/* 314 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTimeToLive() {
/* 319 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 321 */         return ((MulticastSocket)this.javaSocket).getTimeToLive();
/* 322 */       } catch (IOException e) {
/* 323 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 326 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setTimeToLive(int ttl) {
/* 332 */     if (this.javaSocket instanceof MulticastSocket) {
/*     */       try {
/* 334 */         ((MulticastSocket)this.javaSocket).setTimeToLive(ttl);
/* 335 */       } catch (IOException e) {
/* 336 */         throw new ChannelException(e);
/*     */       } 
/*     */     } else {
/* 339 */       throw new UnsupportedOperationException();
/*     */     } 
/* 341 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTrafficClass() {
/*     */     try {
/* 347 */       return this.javaSocket.getTrafficClass();
/* 348 */     } catch (SocketException e) {
/* 349 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setTrafficClass(int trafficClass) {
/*     */     try {
/* 356 */       this.javaSocket.setTrafficClass(trafficClass);
/* 357 */     } catch (SocketException e) {
/* 358 */       throw new ChannelException(e);
/*     */     } 
/* 360 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 365 */     super.setWriteSpinCount(writeSpinCount);
/* 366 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 371 */     super.setConnectTimeoutMillis(connectTimeoutMillis);
/* 372 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public DatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/* 378 */     super.setMaxMessagesPerRead(maxMessagesPerRead);
/* 379 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 384 */     super.setAllocator(allocator);
/* 385 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 390 */     super.setRecvByteBufAllocator(allocator);
/* 391 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setAutoRead(boolean autoRead) {
/* 396 */     super.setAutoRead(autoRead);
/* 397 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setAutoClose(boolean autoClose) {
/* 402 */     super.setAutoClose(autoClose);
/* 403 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/* 408 */     super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
/* 409 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/* 414 */     super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
/* 415 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 420 */     super.setWriteBufferWaterMark(writeBufferWaterMark);
/* 421 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 426 */     super.setMessageSizeEstimator(estimator);
/* 427 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\DefaultDatagramChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */