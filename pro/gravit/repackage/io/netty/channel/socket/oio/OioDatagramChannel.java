/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MulticastSocket;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.AddressedEnvelope;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.oio.AbstractOioMessageChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramPacket;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class OioDatagramChannel
/*     */   extends AbstractOioMessageChannel
/*     */   implements DatagramChannel
/*     */ {
/*  63 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
/*     */   
/*  65 */   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
/*  66 */   private static final String EXPECTED_TYPES = " (expected: " + 
/*  67 */     StringUtil.simpleClassName(DatagramPacket.class) + ", " + 
/*  68 */     StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + 
/*  69 */     StringUtil.simpleClassName(ByteBuf.class) + ", " + 
/*  70 */     StringUtil.simpleClassName(SocketAddress.class) + ">, " + 
/*  71 */     StringUtil.simpleClassName(ByteBuf.class) + ')';
/*     */   
/*     */   private final MulticastSocket socket;
/*     */   private final OioDatagramChannelConfig config;
/*  75 */   private final DatagramPacket tmpPacket = new DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);
/*     */   
/*     */   private static MulticastSocket newSocket() {
/*     */     try {
/*  79 */       return new MulticastSocket(null);
/*  80 */     } catch (Exception e) {
/*  81 */       throw new ChannelException("failed to create a new socket", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OioDatagramChannel() {
/*  89 */     this(newSocket());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OioDatagramChannel(MulticastSocket socket) {
/*  98 */     super(null);
/*     */     
/* 100 */     boolean success = false;
/*     */     try {
/* 102 */       socket.setSoTimeout(1000);
/* 103 */       socket.setBroadcast(false);
/* 104 */       success = true;
/* 105 */     } catch (SocketException e) {
/* 106 */       throw new ChannelException("Failed to configure the datagram socket timeout.", e);
/*     */     } finally {
/*     */       
/* 109 */       if (!success) {
/* 110 */         socket.close();
/*     */       }
/*     */     } 
/*     */     
/* 114 */     this.socket = socket;
/* 115 */     this.config = new DefaultOioDatagramChannelConfig(this, socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 120 */     return METADATA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig config() {
/* 131 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 136 */     return !this.socket.isClosed();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 142 */     return (isOpen() && ((((Boolean)this.config
/* 143 */       .getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION)).booleanValue() && isRegistered()) || this.socket
/* 144 */       .isBound()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnected() {
/* 149 */     return this.socket.isConnected();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 154 */     return this.socket.getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 159 */     return this.socket.getRemoteSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 164 */     this.socket.bind(localAddress);
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/* 169 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 174 */     return (InetSocketAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 180 */     if (localAddress != null) {
/* 181 */       this.socket.bind(localAddress);
/*     */     }
/*     */     
/* 184 */     boolean success = false;
/*     */     try {
/* 186 */       this.socket.connect(remoteAddress);
/* 187 */       success = true;
/*     */     } finally {
/* 189 */       if (!success) {
/*     */         try {
/* 191 */           this.socket.close();
/* 192 */         } catch (Throwable t) {
/* 193 */           logger.warn("Failed to close a socket.", t);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 201 */     this.socket.disconnect();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 206 */     this.socket.close();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadMessages(List<Object> buf) throws Exception {
/* 211 */     DatagramChannelConfig config = config();
/* 212 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/*     */     
/* 214 */     ByteBuf data = config.getAllocator().heapBuffer(allocHandle.guess());
/* 215 */     boolean free = true;
/*     */     
/*     */     try {
/* 218 */       this.tmpPacket.setAddress(null);
/* 219 */       this.tmpPacket.setData(data.array(), data.arrayOffset(), data.capacity());
/* 220 */       this.socket.receive(this.tmpPacket);
/*     */       
/* 222 */       InetSocketAddress remoteAddr = (InetSocketAddress)this.tmpPacket.getSocketAddress();
/*     */       
/* 224 */       allocHandle.lastBytesRead(this.tmpPacket.getLength());
/* 225 */       buf.add(new DatagramPacket(data.writerIndex(allocHandle.lastBytesRead()), localAddress(), remoteAddr));
/* 226 */       free = false;
/* 227 */       return 1;
/* 228 */     } catch (SocketTimeoutException e) {
/*     */       
/* 230 */       return 0;
/* 231 */     } catch (SocketException e) {
/* 232 */       if (!e.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
/* 233 */         throw e;
/*     */       }
/* 235 */       return -1;
/* 236 */     } catch (Throwable cause) {
/* 237 */       PlatformDependent.throwException(cause);
/* 238 */       return -1;
/*     */     } finally {
/* 240 */       if (free)
/* 241 */         data.release(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/*     */     while (true) {
/*     */       ByteBuf data;
/*     */       SocketAddress remoteAddress;
/* 249 */       Object o = in.current();
/* 250 */       if (o == null) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 256 */       if (o instanceof AddressedEnvelope) {
/*     */         
/* 258 */         AddressedEnvelope<ByteBuf, SocketAddress> envelope = (AddressedEnvelope<ByteBuf, SocketAddress>)o;
/* 259 */         remoteAddress = envelope.recipient();
/* 260 */         data = (ByteBuf)envelope.content();
/*     */       } else {
/* 262 */         data = (ByteBuf)o;
/* 263 */         remoteAddress = null;
/*     */       } 
/*     */       
/* 266 */       int length = data.readableBytes();
/*     */       try {
/* 268 */         if (remoteAddress != null) {
/* 269 */           this.tmpPacket.setSocketAddress(remoteAddress);
/*     */         } else {
/* 271 */           if (!isConnected())
/*     */           {
/*     */             
/* 274 */             throw new NotYetConnectedException();
/*     */           }
/*     */           
/* 277 */           this.tmpPacket.setAddress(null);
/*     */         } 
/* 279 */         if (data.hasArray()) {
/* 280 */           this.tmpPacket.setData(data.array(), data.arrayOffset() + data.readerIndex(), length);
/*     */         } else {
/* 282 */           this.tmpPacket.setData(ByteBufUtil.getBytes(data, data.readerIndex(), length));
/*     */         } 
/* 284 */         this.socket.send(this.tmpPacket);
/* 285 */         in.remove();
/* 286 */       } catch (Exception e) {
/*     */ 
/*     */ 
/*     */         
/* 290 */         in.remove(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object filterOutboundMessage(Object msg) {
/* 297 */     if (msg instanceof DatagramPacket || msg instanceof ByteBuf) {
/* 298 */       return msg;
/*     */     }
/*     */     
/* 301 */     if (msg instanceof AddressedEnvelope) {
/*     */       
/* 303 */       AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
/* 304 */       if (e.content() instanceof ByteBuf) {
/* 305 */         return msg;
/*     */       }
/*     */     } 
/*     */     
/* 309 */     throw new UnsupportedOperationException("unsupported message type: " + 
/* 310 */         StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress) {
/* 315 */     return joinGroup(multicastAddress, newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
/* 320 */     ensureBound();
/*     */     try {
/* 322 */       this.socket.joinGroup(multicastAddress);
/* 323 */       promise.setSuccess();
/* 324 */     } catch (IOException e) {
/* 325 */       promise.setFailure(e);
/*     */     } 
/* 327 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
/* 332 */     return joinGroup(multicastAddress, networkInterface, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
/* 339 */     ensureBound();
/*     */     try {
/* 341 */       this.socket.joinGroup(multicastAddress, networkInterface);
/* 342 */       promise.setSuccess();
/* 343 */     } catch (IOException e) {
/* 344 */       promise.setFailure(e);
/*     */     } 
/* 346 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
/* 352 */     return newFailedFuture(new UnsupportedOperationException());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
/* 359 */     promise.setFailure(new UnsupportedOperationException());
/* 360 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void ensureBound() {
/* 364 */     if (!isActive()) {
/* 365 */       throw new IllegalStateException(DatagramChannel.class
/* 366 */           .getName() + " must be bound to join a group.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress) {
/* 373 */     return leaveGroup(multicastAddress, newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
/*     */     try {
/* 379 */       this.socket.leaveGroup(multicastAddress);
/* 380 */       promise.setSuccess();
/* 381 */     } catch (IOException e) {
/* 382 */       promise.setFailure(e);
/*     */     } 
/* 384 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
/* 390 */     return leaveGroup(multicastAddress, networkInterface, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
/*     */     try {
/* 398 */       this.socket.leaveGroup(multicastAddress, networkInterface);
/* 399 */       promise.setSuccess();
/* 400 */     } catch (IOException e) {
/* 401 */       promise.setFailure(e);
/*     */     } 
/* 403 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
/* 409 */     return newFailedFuture(new UnsupportedOperationException());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
/* 416 */     promise.setFailure(new UnsupportedOperationException());
/* 417 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
/* 423 */     return newFailedFuture(new UnsupportedOperationException());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
/* 430 */     promise.setFailure(new UnsupportedOperationException());
/* 431 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
/* 437 */     return newFailedFuture(new UnsupportedOperationException());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
/* 443 */     promise.setFailure(new UnsupportedOperationException());
/* 444 */     return (ChannelFuture)promise;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\OioDatagramChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */