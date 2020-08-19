/*     */ package pro.gravit.repackage.io.netty.channel.socket.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.MembershipKey;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.AddressedEnvelope;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultAddressedEnvelope;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.AbstractNioMessageChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DatagramPacket;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.InternetProtocolFamily;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ public final class NioDatagramChannel
/*     */   extends AbstractNioMessageChannel
/*     */   implements DatagramChannel
/*     */ {
/*  66 */   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
/*  67 */   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
/*  68 */   private static final String EXPECTED_TYPES = " (expected: " + 
/*  69 */     StringUtil.simpleClassName(DatagramPacket.class) + ", " + 
/*  70 */     StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + 
/*  71 */     StringUtil.simpleClassName(ByteBuf.class) + ", " + 
/*  72 */     StringUtil.simpleClassName(SocketAddress.class) + ">, " + 
/*  73 */     StringUtil.simpleClassName(ByteBuf.class) + ')';
/*     */ 
/*     */ 
/*     */   
/*     */   private final DatagramChannelConfig config;
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<InetAddress, List<MembershipKey>> memberships;
/*     */ 
/*     */ 
/*     */   
/*     */   private static DatagramChannel newSocket(SelectorProvider provider) {
/*     */     try {
/*  87 */       return provider.openDatagramChannel();
/*  88 */     } catch (IOException e) {
/*  89 */       throw new ChannelException("Failed to open a socket.", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   private static DatagramChannel newSocket(SelectorProvider provider, InternetProtocolFamily ipFamily) {
/*  95 */     if (ipFamily == null) {
/*  96 */       return newSocket(provider);
/*     */     }
/*     */     
/*  99 */     checkJavaVersion();
/*     */     
/*     */     try {
/* 102 */       return provider.openDatagramChannel(ProtocolFamilyConverter.convert(ipFamily));
/* 103 */     } catch (IOException e) {
/* 104 */       throw new ChannelException("Failed to open a socket.", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void checkJavaVersion() {
/* 109 */     if (PlatformDependent.javaVersion() < 7) {
/* 110 */       throw new UnsupportedOperationException("Only supported on java 7+.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioDatagramChannel() {
/* 118 */     this(newSocket(DEFAULT_SELECTOR_PROVIDER));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioDatagramChannel(SelectorProvider provider) {
/* 126 */     this(newSocket(provider));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioDatagramChannel(InternetProtocolFamily ipFamily) {
/* 134 */     this(newSocket(DEFAULT_SELECTOR_PROVIDER, ipFamily));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioDatagramChannel(SelectorProvider provider, InternetProtocolFamily ipFamily) {
/* 143 */     this(newSocket(provider, ipFamily));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioDatagramChannel(DatagramChannel socket) {
/* 150 */     super(null, socket, 1);
/* 151 */     this.config = (DatagramChannelConfig)new NioDatagramChannelConfig(this, socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 156 */     return METADATA;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatagramChannelConfig config() {
/* 161 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 167 */     DatagramChannel ch = javaChannel();
/* 168 */     return (ch.isOpen() && ((((Boolean)this.config
/* 169 */       .getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION)).booleanValue() && isRegistered()) || ch
/* 170 */       .socket().isBound()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnected() {
/* 175 */     return javaChannel().isConnected();
/*     */   }
/*     */ 
/*     */   
/*     */   protected DatagramChannel javaChannel() {
/* 180 */     return (DatagramChannel)super.javaChannel();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 185 */     return javaChannel().socket().getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 190 */     return javaChannel().socket().getRemoteSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 195 */     doBind0(localAddress);
/*     */   }
/*     */   
/*     */   private void doBind0(SocketAddress localAddress) throws Exception {
/* 199 */     if (PlatformDependent.javaVersion() >= 7) {
/* 200 */       SocketUtils.bind(javaChannel(), localAddress);
/*     */     } else {
/* 202 */       javaChannel().socket().bind(localAddress);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 209 */     if (localAddress != null) {
/* 210 */       doBind0(localAddress);
/*     */     }
/*     */     
/* 213 */     boolean success = false;
/*     */     try {
/* 215 */       javaChannel().connect(remoteAddress);
/* 216 */       success = true;
/* 217 */       return true;
/*     */     } finally {
/* 219 */       if (!success) {
/* 220 */         doClose();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doFinishConnect() throws Exception {
/* 227 */     throw new Error();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 232 */     javaChannel().disconnect();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 237 */     javaChannel().close();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadMessages(List<Object> buf) throws Exception {
/* 242 */     DatagramChannel ch = javaChannel();
/* 243 */     DatagramChannelConfig config = config();
/* 244 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/*     */     
/* 246 */     ByteBuf data = allocHandle.allocate(config.getAllocator());
/* 247 */     allocHandle.attemptedBytesRead(data.writableBytes());
/* 248 */     boolean free = true;
/*     */     try {
/* 250 */       ByteBuffer nioData = data.internalNioBuffer(data.writerIndex(), data.writableBytes());
/* 251 */       int pos = nioData.position();
/* 252 */       InetSocketAddress remoteAddress = (InetSocketAddress)ch.receive(nioData);
/* 253 */       if (remoteAddress == null) {
/* 254 */         return 0;
/*     */       }
/*     */       
/* 257 */       allocHandle.lastBytesRead(nioData.position() - pos);
/* 258 */       buf.add(new DatagramPacket(data.writerIndex(data.writerIndex() + allocHandle.lastBytesRead()), 
/* 259 */             localAddress(), remoteAddress));
/* 260 */       free = false;
/* 261 */       return 1;
/* 262 */     } catch (Throwable cause) {
/* 263 */       PlatformDependent.throwException(cause);
/* 264 */       return -1;
/*     */     } finally {
/* 266 */       if (free) {
/* 267 */         data.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
/*     */     SocketAddress remoteAddress;
/*     */     ByteBuf data;
/*     */     int writtenBytes;
/* 276 */     if (msg instanceof AddressedEnvelope) {
/*     */       
/* 278 */       AddressedEnvelope<ByteBuf, SocketAddress> envelope = (AddressedEnvelope<ByteBuf, SocketAddress>)msg;
/* 279 */       remoteAddress = envelope.recipient();
/* 280 */       data = (ByteBuf)envelope.content();
/*     */     } else {
/* 282 */       data = (ByteBuf)msg;
/* 283 */       remoteAddress = null;
/*     */     } 
/*     */     
/* 286 */     int dataLen = data.readableBytes();
/* 287 */     if (dataLen == 0) {
/* 288 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 292 */     ByteBuffer nioData = (data.nioBufferCount() == 1) ? data.internalNioBuffer(data.readerIndex(), dataLen) : data.nioBuffer(data.readerIndex(), dataLen);
/*     */     
/* 294 */     if (remoteAddress != null) {
/* 295 */       writtenBytes = javaChannel().send(nioData, remoteAddress);
/*     */     } else {
/* 297 */       writtenBytes = javaChannel().write(nioData);
/*     */     } 
/* 299 */     return (writtenBytes > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object filterOutboundMessage(Object msg) {
/* 304 */     if (msg instanceof DatagramPacket) {
/* 305 */       DatagramPacket p = (DatagramPacket)msg;
/* 306 */       ByteBuf content = (ByteBuf)p.content();
/* 307 */       if (isSingleDirectBuffer(content)) {
/* 308 */         return p;
/*     */       }
/* 310 */       return new DatagramPacket(newDirectBuffer((ReferenceCounted)p, content), (InetSocketAddress)p.recipient());
/*     */     } 
/*     */     
/* 313 */     if (msg instanceof ByteBuf) {
/* 314 */       ByteBuf buf = (ByteBuf)msg;
/* 315 */       if (isSingleDirectBuffer(buf)) {
/* 316 */         return buf;
/*     */       }
/* 318 */       return newDirectBuffer(buf);
/*     */     } 
/*     */     
/* 321 */     if (msg instanceof AddressedEnvelope) {
/*     */       
/* 323 */       AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
/* 324 */       if (e.content() instanceof ByteBuf) {
/* 325 */         ByteBuf content = (ByteBuf)e.content();
/* 326 */         if (isSingleDirectBuffer(content)) {
/* 327 */           return e;
/*     */         }
/* 329 */         return new DefaultAddressedEnvelope(newDirectBuffer((ReferenceCounted)e, content), e.recipient());
/*     */       } 
/*     */     } 
/*     */     
/* 333 */     throw new UnsupportedOperationException("unsupported message type: " + 
/* 334 */         StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isSingleDirectBuffer(ByteBuf buf) {
/* 342 */     return (buf.isDirect() && buf.nioBufferCount() == 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean continueOnWriteError() {
/* 350 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/* 355 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 360 */     return (InetSocketAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress) {
/* 365 */     return joinGroup(multicastAddress, newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
/*     */     try {
/* 371 */       return joinGroup(multicastAddress, 
/*     */           
/* 373 */           NetworkInterface.getByInetAddress(localAddress().getAddress()), (InetAddress)null, promise);
/*     */     }
/* 375 */     catch (SocketException e) {
/* 376 */       promise.setFailure(e);
/*     */       
/* 378 */       return (ChannelFuture)promise;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
/* 384 */     return joinGroup(multicastAddress, networkInterface, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
/* 391 */     return joinGroup(multicastAddress.getAddress(), networkInterface, (InetAddress)null, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
/* 397 */     return joinGroup(multicastAddress, networkInterface, source, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
/* 406 */     checkJavaVersion();
/*     */     
/* 408 */     ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
/* 409 */     ObjectUtil.checkNotNull(networkInterface, "networkInterface");
/*     */     
/*     */     try {
/*     */       MembershipKey key;
/* 413 */       if (source == null) {
/* 414 */         key = javaChannel().join(multicastAddress, networkInterface);
/*     */       } else {
/* 416 */         key = javaChannel().join(multicastAddress, networkInterface, source);
/*     */       } 
/*     */       
/* 419 */       synchronized (this) {
/* 420 */         List<MembershipKey> keys = null;
/* 421 */         if (this.memberships == null) {
/* 422 */           this.memberships = new HashMap<InetAddress, List<MembershipKey>>();
/*     */         } else {
/* 424 */           keys = this.memberships.get(multicastAddress);
/*     */         } 
/* 426 */         if (keys == null) {
/* 427 */           keys = new ArrayList<MembershipKey>();
/* 428 */           this.memberships.put(multicastAddress, keys);
/*     */         } 
/* 430 */         keys.add(key);
/*     */       } 
/*     */       
/* 433 */       promise.setSuccess();
/* 434 */     } catch (Throwable e) {
/* 435 */       promise.setFailure(e);
/*     */     } 
/*     */     
/* 438 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress) {
/* 443 */     return leaveGroup(multicastAddress, newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
/*     */     try {
/* 449 */       return leaveGroup(multicastAddress, 
/* 450 */           NetworkInterface.getByInetAddress(localAddress().getAddress()), (InetAddress)null, promise);
/* 451 */     } catch (SocketException e) {
/* 452 */       promise.setFailure(e);
/*     */       
/* 454 */       return (ChannelFuture)promise;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
/* 460 */     return leaveGroup(multicastAddress, networkInterface, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
/* 467 */     return leaveGroup(multicastAddress.getAddress(), networkInterface, (InetAddress)null, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
/* 473 */     return leaveGroup(multicastAddress, networkInterface, source, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
/* 481 */     checkJavaVersion();
/*     */     
/* 483 */     ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
/* 484 */     ObjectUtil.checkNotNull(networkInterface, "networkInterface");
/*     */     
/* 486 */     synchronized (this) {
/* 487 */       if (this.memberships != null) {
/* 488 */         List<MembershipKey> keys = this.memberships.get(multicastAddress);
/* 489 */         if (keys != null) {
/* 490 */           Iterator<MembershipKey> keyIt = keys.iterator();
/*     */           
/* 492 */           while (keyIt.hasNext()) {
/* 493 */             MembershipKey key = keyIt.next();
/* 494 */             if (networkInterface.equals(key.networkInterface()) && ((
/* 495 */               source == null && key.sourceAddress() == null) || (source != null && source
/* 496 */               .equals(key.sourceAddress())))) {
/* 497 */               key.drop();
/* 498 */               keyIt.remove();
/*     */             } 
/*     */           } 
/*     */           
/* 502 */           if (keys.isEmpty()) {
/* 503 */             this.memberships.remove(multicastAddress);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 509 */     promise.setSuccess();
/* 510 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
/* 520 */     return block(multicastAddress, networkInterface, sourceToBlock, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
/* 531 */     checkJavaVersion();
/*     */     
/* 533 */     ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
/* 534 */     ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
/* 535 */     ObjectUtil.checkNotNull(networkInterface, "networkInterface");
/*     */     
/* 537 */     synchronized (this) {
/* 538 */       if (this.memberships != null) {
/* 539 */         List<MembershipKey> keys = this.memberships.get(multicastAddress);
/* 540 */         for (MembershipKey key : keys) {
/* 541 */           if (networkInterface.equals(key.networkInterface())) {
/*     */             try {
/* 543 */               key.block(sourceToBlock);
/* 544 */             } catch (IOException e) {
/* 545 */               promise.setFailure(e);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 551 */     promise.setSuccess();
/* 552 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
/* 561 */     return block(multicastAddress, sourceToBlock, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
/*     */     try {
/* 572 */       return block(multicastAddress, 
/*     */           
/* 574 */           NetworkInterface.getByInetAddress(localAddress().getAddress()), sourceToBlock, promise);
/*     */     }
/* 576 */     catch (SocketException e) {
/* 577 */       promise.setFailure(e);
/*     */       
/* 579 */       return (ChannelFuture)promise;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected void setReadPending(boolean readPending) {
/* 585 */     super.setReadPending(readPending);
/*     */   }
/*     */   
/*     */   void clearReadPending0() {
/* 589 */     clearReadPending();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean closeOnReadError(Throwable cause) {
/* 596 */     if (cause instanceof SocketException) {
/* 597 */       return false;
/*     */     }
/* 599 */     return super.closeOnReadError(cause);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\nio\NioDatagramChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */