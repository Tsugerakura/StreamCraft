/*     */ package pro.gravit.repackage.io.netty.channel.socket.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.FileRegion;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.AbstractNioByteChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.AbstractNioChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.NioEventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DefaultSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GlobalEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ public class NioSocketChannel
/*     */   extends AbstractNioByteChannel
/*     */   implements SocketChannel
/*     */ {
/*  58 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);
/*  59 */   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
/*     */ 
/*     */ 
/*     */   
/*     */   private final SocketChannelConfig config;
/*     */ 
/*     */ 
/*     */   
/*     */   private static SocketChannel newSocket(SelectorProvider provider) {
/*     */     try {
/*  69 */       return provider.openSocketChannel();
/*  70 */     } catch (IOException e) {
/*  71 */       throw new ChannelException("Failed to open a socket.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioSocketChannel() {
/*  81 */     this(DEFAULT_SELECTOR_PROVIDER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioSocketChannel(SelectorProvider provider) {
/*  88 */     this(newSocket(provider));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioSocketChannel(SocketChannel socket) {
/*  95 */     this((Channel)null, socket);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioSocketChannel(Channel parent, SocketChannel socket) {
/* 105 */     super(parent, socket);
/* 106 */     this.config = (SocketChannelConfig)new NioSocketChannelConfig(this, socket.socket());
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannel parent() {
/* 111 */     return (ServerSocketChannel)super.parent();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketChannelConfig config() {
/* 116 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketChannel javaChannel() {
/* 121 */     return (SocketChannel)super.javaChannel();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 126 */     SocketChannel ch = javaChannel();
/* 127 */     return (ch.isOpen() && ch.isConnected());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutputShutdown() {
/* 132 */     return (javaChannel().socket().isOutputShutdown() || !isActive());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInputShutdown() {
/* 137 */     return (javaChannel().socket().isInputShutdown() || !isActive());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isShutdown() {
/* 142 */     Socket socket = javaChannel().socket();
/* 143 */     return ((socket.isInputShutdown() && socket.isOutputShutdown()) || !isActive());
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/* 148 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 153 */     return (InetSocketAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   protected final void doShutdownOutput() throws Exception {
/* 160 */     if (PlatformDependent.javaVersion() >= 7) {
/* 161 */       javaChannel().shutdownOutput();
/*     */     } else {
/* 163 */       javaChannel().socket().shutdownOutput();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownOutput() {
/* 169 */     return shutdownOutput(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownOutput(final ChannelPromise promise) {
/* 174 */     NioEventLoop nioEventLoop = eventLoop();
/* 175 */     if (nioEventLoop.inEventLoop()) {
/* 176 */       ((AbstractChannel.AbstractUnsafe)unsafe()).shutdownOutput(promise);
/*     */     } else {
/* 178 */       nioEventLoop.execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 181 */               ((AbstractChannel.AbstractUnsafe)NioSocketChannel.this.unsafe()).shutdownOutput(promise);
/*     */             }
/*     */           });
/*     */     } 
/* 185 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownInput() {
/* 190 */     return shutdownInput(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isInputShutdown0() {
/* 195 */     return isInputShutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownInput(final ChannelPromise promise) {
/* 200 */     NioEventLoop nioEventLoop = eventLoop();
/* 201 */     if (nioEventLoop.inEventLoop()) {
/* 202 */       shutdownInput0(promise);
/*     */     } else {
/* 204 */       nioEventLoop.execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 207 */               NioSocketChannel.this.shutdownInput0(promise);
/*     */             }
/*     */           });
/*     */     } 
/* 211 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdown() {
/* 216 */     return shutdown(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdown(final ChannelPromise promise) {
/* 221 */     ChannelFuture shutdownOutputFuture = shutdownOutput();
/* 222 */     if (shutdownOutputFuture.isDone()) {
/* 223 */       shutdownOutputDone(shutdownOutputFuture, promise);
/*     */     } else {
/* 225 */       shutdownOutputFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
/* 228 */               NioSocketChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
/*     */             }
/*     */           });
/*     */     } 
/* 232 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
/* 236 */     ChannelFuture shutdownInputFuture = shutdownInput();
/* 237 */     if (shutdownInputFuture.isDone()) {
/* 238 */       shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
/*     */     } else {
/* 240 */       shutdownInputFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
/* 243 */               NioSocketChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
/* 252 */     Throwable shutdownOutputCause = shutdownOutputFuture.cause();
/* 253 */     Throwable shutdownInputCause = shutdownInputFuture.cause();
/* 254 */     if (shutdownOutputCause != null) {
/* 255 */       if (shutdownInputCause != null) {
/* 256 */         logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
/*     */       }
/*     */       
/* 259 */       promise.setFailure(shutdownOutputCause);
/* 260 */     } else if (shutdownInputCause != null) {
/* 261 */       promise.setFailure(shutdownInputCause);
/*     */     } else {
/* 263 */       promise.setSuccess();
/*     */     } 
/*     */   }
/*     */   private void shutdownInput0(ChannelPromise promise) {
/*     */     try {
/* 268 */       shutdownInput0();
/* 269 */       promise.setSuccess();
/* 270 */     } catch (Throwable t) {
/* 271 */       promise.setFailure(t);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   private void shutdownInput0() throws Exception {
/* 277 */     if (PlatformDependent.javaVersion() >= 7) {
/* 278 */       javaChannel().shutdownInput();
/*     */     } else {
/* 280 */       javaChannel().socket().shutdownInput();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 286 */     return javaChannel().socket().getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 291 */     return javaChannel().socket().getRemoteSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 296 */     doBind0(localAddress);
/*     */   }
/*     */   
/*     */   private void doBind0(SocketAddress localAddress) throws Exception {
/* 300 */     if (PlatformDependent.javaVersion() >= 7) {
/* 301 */       SocketUtils.bind(javaChannel(), localAddress);
/*     */     } else {
/* 303 */       SocketUtils.bind(javaChannel().socket(), localAddress);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 309 */     if (localAddress != null) {
/* 310 */       doBind0(localAddress);
/*     */     }
/*     */     
/* 313 */     boolean success = false;
/*     */     try {
/* 315 */       boolean connected = SocketUtils.connect(javaChannel(), remoteAddress);
/* 316 */       if (!connected) {
/* 317 */         selectionKey().interestOps(8);
/*     */       }
/* 319 */       success = true;
/* 320 */       return connected;
/*     */     } finally {
/* 322 */       if (!success) {
/* 323 */         doClose();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doFinishConnect() throws Exception {
/* 330 */     if (!javaChannel().finishConnect()) {
/* 331 */       throw new Error();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 337 */     doClose();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 342 */     super.doClose();
/* 343 */     javaChannel().close();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadBytes(ByteBuf byteBuf) throws Exception {
/* 348 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/* 349 */     allocHandle.attemptedBytesRead(byteBuf.writableBytes());
/* 350 */     return byteBuf.writeBytes(javaChannel(), allocHandle.attemptedBytesRead());
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doWriteBytes(ByteBuf buf) throws Exception {
/* 355 */     int expectedWrittenBytes = buf.readableBytes();
/* 356 */     return buf.readBytes(javaChannel(), expectedWrittenBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long doWriteFileRegion(FileRegion region) throws Exception {
/* 361 */     long position = region.transferred();
/* 362 */     return region.transferTo(javaChannel(), position);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void adjustMaxBytesPerGatheringWrite(int attempted, int written, int oldMaxBytesPerGatheringWrite) {
/* 369 */     if (attempted == written) {
/* 370 */       if (attempted << 1 > oldMaxBytesPerGatheringWrite) {
/* 371 */         ((NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted << 1);
/*     */       }
/* 373 */     } else if (attempted > 4096 && written < attempted >>> 1) {
/* 374 */       ((NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite(attempted >>> 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/* 380 */     SocketChannel ch = javaChannel();
/* 381 */     int writeSpinCount = config().getWriteSpinCount(); do {
/*     */       ByteBuffer buffer; long attemptedBytes; int i, j; long localWrittenBytes;
/* 383 */       if (in.isEmpty()) {
/*     */         
/* 385 */         clearOpWrite();
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 391 */       int maxBytesPerGatheringWrite = ((NioSocketChannelConfig)this.config).getMaxBytesPerGatheringWrite();
/* 392 */       ByteBuffer[] nioBuffers = in.nioBuffers(1024, maxBytesPerGatheringWrite);
/* 393 */       int nioBufferCnt = in.nioBufferCount();
/*     */ 
/*     */ 
/*     */       
/* 397 */       switch (nioBufferCnt) {
/*     */         
/*     */         case 0:
/* 400 */           writeSpinCount -= doWrite0(in);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 406 */           buffer = nioBuffers[0];
/* 407 */           i = buffer.remaining();
/* 408 */           j = ch.write(buffer);
/* 409 */           if (j <= 0) {
/* 410 */             incompleteWrite(true);
/*     */             return;
/*     */           } 
/* 413 */           adjustMaxBytesPerGatheringWrite(i, j, maxBytesPerGatheringWrite);
/* 414 */           in.removeBytes(j);
/* 415 */           writeSpinCount--;
/*     */           break;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 422 */           attemptedBytes = in.nioBufferSize();
/* 423 */           localWrittenBytes = ch.write(nioBuffers, 0, nioBufferCnt);
/* 424 */           if (localWrittenBytes <= 0L) {
/* 425 */             incompleteWrite(true);
/*     */             
/*     */             return;
/*     */           } 
/* 429 */           adjustMaxBytesPerGatheringWrite((int)attemptedBytes, (int)localWrittenBytes, maxBytesPerGatheringWrite);
/*     */           
/* 431 */           in.removeBytes(localWrittenBytes);
/* 432 */           writeSpinCount--;
/*     */           break;
/*     */       } 
/*     */     
/* 436 */     } while (writeSpinCount > 0);
/*     */     
/* 438 */     incompleteWrite((writeSpinCount < 0));
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
/* 443 */     return (AbstractNioChannel.AbstractNioUnsafe)new NioSocketChannelUnsafe();
/*     */   }
/*     */   private final class NioSocketChannelUnsafe extends AbstractNioByteChannel.NioByteUnsafe { private NioSocketChannelUnsafe() {
/* 446 */       super(NioSocketChannel.this);
/*     */     }
/*     */     protected Executor prepareToClose() {
/*     */       try {
/* 450 */         if (NioSocketChannel.this.javaChannel().isOpen() && NioSocketChannel.this.config().getSoLinger() > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 455 */           NioSocketChannel.this.doDeregister();
/* 456 */           return (Executor)GlobalEventExecutor.INSTANCE;
/*     */         } 
/* 458 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 463 */       return null;
/*     */     } }
/*     */ 
/*     */   
/*     */   private final class NioSocketChannelConfig extends DefaultSocketChannelConfig {
/* 468 */     private volatile int maxBytesPerGatheringWrite = Integer.MAX_VALUE;
/*     */     private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {
/* 470 */       super(channel, javaSocket);
/* 471 */       calculateMaxBytesPerGatheringWrite();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void autoReadCleared() {
/* 476 */       NioSocketChannel.this.clearReadPending();
/*     */     }
/*     */ 
/*     */     
/*     */     public NioSocketChannelConfig setSendBufferSize(int sendBufferSize) {
/* 481 */       super.setSendBufferSize(sendBufferSize);
/* 482 */       calculateMaxBytesPerGatheringWrite();
/* 483 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> boolean setOption(ChannelOption<T> option, T value) {
/* 488 */       if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
/* 489 */         return NioChannelOption.setOption(jdkChannel(), (NioChannelOption<T>)option, value);
/*     */       }
/* 491 */       return super.setOption(option, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> T getOption(ChannelOption<T> option) {
/* 496 */       if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
/* 497 */         return NioChannelOption.getOption(jdkChannel(), (NioChannelOption<T>)option);
/*     */       }
/* 499 */       return (T)super.getOption(option);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<ChannelOption<?>, Object> getOptions() {
/* 504 */       if (PlatformDependent.javaVersion() >= 7) {
/* 505 */         return getOptions(super.getOptions(), NioChannelOption.getOptions(jdkChannel()));
/*     */       }
/* 507 */       return super.getOptions();
/*     */     }
/*     */     
/*     */     void setMaxBytesPerGatheringWrite(int maxBytesPerGatheringWrite) {
/* 511 */       this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
/*     */     }
/*     */     
/*     */     int getMaxBytesPerGatheringWrite() {
/* 515 */       return this.maxBytesPerGatheringWrite;
/*     */     }
/*     */ 
/*     */     
/*     */     private void calculateMaxBytesPerGatheringWrite() {
/* 520 */       int newSendBufferSize = getSendBufferSize() << 1;
/* 521 */       if (newSendBufferSize > 0) {
/* 522 */         setMaxBytesPerGatheringWrite(newSendBufferSize);
/*     */       }
/*     */     }
/*     */     
/*     */     private SocketChannel jdkChannel() {
/* 527 */       return ((NioSocketChannel)this.channel).javaChannel();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\nio\NioSocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */