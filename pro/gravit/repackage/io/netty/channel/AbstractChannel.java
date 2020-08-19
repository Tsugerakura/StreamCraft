/*      */ package pro.gravit.repackage.io.netty.channel;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.net.ConnectException;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NoRouteToHostException;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.NotYetConnectedException;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.channel.socket.ChannelOutputShutdownEvent;
/*      */ import pro.gravit.repackage.io.netty.channel.socket.ChannelOutputShutdownException;
/*      */ import pro.gravit.repackage.io.netty.util.DefaultAttributeMap;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractChannel
/*      */   extends DefaultAttributeMap
/*      */   implements Channel
/*      */ {
/*   45 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
/*      */   
/*      */   private final Channel parent;
/*      */   private final ChannelId id;
/*      */   private final Channel.Unsafe unsafe;
/*      */   private final DefaultChannelPipeline pipeline;
/*   51 */   private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
/*   52 */   private final CloseFuture closeFuture = new CloseFuture(this);
/*      */   
/*      */   private volatile SocketAddress localAddress;
/*      */   
/*      */   private volatile SocketAddress remoteAddress;
/*      */   
/*      */   private volatile EventLoop eventLoop;
/*      */   
/*      */   private volatile boolean registered;
/*      */   
/*      */   private boolean closeInitiated;
/*      */   
/*      */   private Throwable initialCloseCause;
/*      */   
/*      */   private boolean strValActive;
/*      */   
/*      */   private String strVal;
/*      */ 
/*      */   
/*      */   protected AbstractChannel(Channel parent) {
/*   72 */     this.parent = parent;
/*   73 */     this.id = newId();
/*   74 */     this.unsafe = newUnsafe();
/*   75 */     this.pipeline = newChannelPipeline();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractChannel(Channel parent, ChannelId id) {
/*   85 */     this.parent = parent;
/*   86 */     this.id = id;
/*   87 */     this.unsafe = newUnsafe();
/*   88 */     this.pipeline = newChannelPipeline();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelId id() {
/*   93 */     return this.id;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ChannelId newId() {
/*  101 */     return DefaultChannelId.newInstance();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultChannelPipeline newChannelPipeline() {
/*  108 */     return new DefaultChannelPipeline(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable() {
/*  113 */     ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
/*  114 */     return (buf != null && buf.isWritable());
/*      */   }
/*      */ 
/*      */   
/*      */   public long bytesBeforeUnwritable() {
/*  119 */     ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
/*      */ 
/*      */     
/*  122 */     return (buf != null) ? buf.bytesBeforeUnwritable() : 0L;
/*      */   }
/*      */ 
/*      */   
/*      */   public long bytesBeforeWritable() {
/*  127 */     ChannelOutboundBuffer buf = this.unsafe.outboundBuffer();
/*      */ 
/*      */     
/*  130 */     return (buf != null) ? buf.bytesBeforeWritable() : Long.MAX_VALUE;
/*      */   }
/*      */ 
/*      */   
/*      */   public Channel parent() {
/*  135 */     return this.parent;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelPipeline pipeline() {
/*  140 */     return this.pipeline;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBufAllocator alloc() {
/*  145 */     return config().getAllocator();
/*      */   }
/*      */ 
/*      */   
/*      */   public EventLoop eventLoop() {
/*  150 */     EventLoop eventLoop = this.eventLoop;
/*  151 */     if (eventLoop == null) {
/*  152 */       throw new IllegalStateException("channel not registered to an event loop");
/*      */     }
/*  154 */     return eventLoop;
/*      */   }
/*      */ 
/*      */   
/*      */   public SocketAddress localAddress() {
/*  159 */     SocketAddress localAddress = this.localAddress;
/*  160 */     if (localAddress == null) {
/*      */       try {
/*  162 */         this.localAddress = localAddress = unsafe().localAddress();
/*  163 */       } catch (Error e) {
/*  164 */         throw e;
/*  165 */       } catch (Throwable t) {
/*      */         
/*  167 */         return null;
/*      */       } 
/*      */     }
/*  170 */     return localAddress;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void invalidateLocalAddress() {
/*  178 */     this.localAddress = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public SocketAddress remoteAddress() {
/*  183 */     SocketAddress remoteAddress = this.remoteAddress;
/*  184 */     if (remoteAddress == null) {
/*      */       try {
/*  186 */         this.remoteAddress = remoteAddress = unsafe().remoteAddress();
/*  187 */       } catch (Error e) {
/*  188 */         throw e;
/*  189 */       } catch (Throwable t) {
/*      */         
/*  191 */         return null;
/*      */       } 
/*      */     }
/*  194 */     return remoteAddress;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void invalidateRemoteAddress() {
/*  202 */     this.remoteAddress = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRegistered() {
/*  207 */     return this.registered;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture bind(SocketAddress localAddress) {
/*  212 */     return this.pipeline.bind(localAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress) {
/*  217 */     return this.pipeline.connect(remoteAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
/*  222 */     return this.pipeline.connect(remoteAddress, localAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture disconnect() {
/*  227 */     return this.pipeline.disconnect();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture close() {
/*  232 */     return this.pipeline.close();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture deregister() {
/*  237 */     return this.pipeline.deregister();
/*      */   }
/*      */ 
/*      */   
/*      */   public Channel flush() {
/*  242 */     this.pipeline.flush();
/*  243 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
/*  248 */     return this.pipeline.bind(localAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
/*  253 */     return this.pipeline.connect(remoteAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/*  258 */     return this.pipeline.connect(remoteAddress, localAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture disconnect(ChannelPromise promise) {
/*  263 */     return this.pipeline.disconnect(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture close(ChannelPromise promise) {
/*  268 */     return this.pipeline.close(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture deregister(ChannelPromise promise) {
/*  273 */     return this.pipeline.deregister(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public Channel read() {
/*  278 */     this.pipeline.read();
/*  279 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture write(Object msg) {
/*  284 */     return this.pipeline.write(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture write(Object msg, ChannelPromise promise) {
/*  289 */     return this.pipeline.write(msg, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture writeAndFlush(Object msg) {
/*  294 */     return this.pipeline.writeAndFlush(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
/*  299 */     return this.pipeline.writeAndFlush(msg, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelPromise newPromise() {
/*  304 */     return this.pipeline.newPromise();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelProgressivePromise newProgressivePromise() {
/*  309 */     return this.pipeline.newProgressivePromise();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture newSucceededFuture() {
/*  314 */     return this.pipeline.newSucceededFuture();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture newFailedFuture(Throwable cause) {
/*  319 */     return this.pipeline.newFailedFuture(cause);
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture closeFuture() {
/*  324 */     return this.closeFuture;
/*      */   }
/*      */ 
/*      */   
/*      */   public Channel.Unsafe unsafe() {
/*  329 */     return this.unsafe;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int hashCode() {
/*  342 */     return this.id.hashCode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean equals(Object o) {
/*  351 */     return (this == o);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int compareTo(Channel o) {
/*  356 */     if (this == o) {
/*  357 */       return 0;
/*      */     }
/*      */     
/*  360 */     return id().compareTo(o.id());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  371 */     boolean active = isActive();
/*  372 */     if (this.strValActive == active && this.strVal != null) {
/*  373 */       return this.strVal;
/*      */     }
/*      */     
/*  376 */     SocketAddress remoteAddr = remoteAddress();
/*  377 */     SocketAddress localAddr = localAddress();
/*  378 */     if (remoteAddr != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  387 */       StringBuilder buf = (new StringBuilder(96)).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(localAddr).append(active ? " - " : " ! ").append("R:").append(remoteAddr).append(']');
/*  388 */       this.strVal = buf.toString();
/*  389 */     } else if (localAddr != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  395 */       StringBuilder buf = (new StringBuilder(64)).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(localAddr).append(']');
/*  396 */       this.strVal = buf.toString();
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  401 */       StringBuilder buf = (new StringBuilder(16)).append("[id: 0x").append(this.id.asShortText()).append(']');
/*  402 */       this.strVal = buf.toString();
/*      */     } 
/*      */     
/*  405 */     this.strValActive = active;
/*  406 */     return this.strVal;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPromise voidPromise() {
/*  411 */     return this.pipeline.voidPromise();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract class AbstractUnsafe
/*      */     implements Channel.Unsafe
/*      */   {
/*  419 */     private volatile ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
/*      */     
/*      */     private RecvByteBufAllocator.Handle recvHandle;
/*      */     private boolean inFlush0;
/*      */     private boolean neverRegistered = true;
/*      */     
/*      */     private void assertEventLoop() {
/*  426 */       assert !AbstractChannel.this.registered || AbstractChannel.this.eventLoop.inEventLoop();
/*      */     }
/*      */ 
/*      */     
/*      */     public RecvByteBufAllocator.Handle recvBufAllocHandle() {
/*  431 */       if (this.recvHandle == null) {
/*  432 */         this.recvHandle = AbstractChannel.this.config().<RecvByteBufAllocator>getRecvByteBufAllocator().newHandle();
/*      */       }
/*  434 */       return this.recvHandle;
/*      */     }
/*      */ 
/*      */     
/*      */     public final ChannelOutboundBuffer outboundBuffer() {
/*  439 */       return this.outboundBuffer;
/*      */     }
/*      */ 
/*      */     
/*      */     public final SocketAddress localAddress() {
/*  444 */       return AbstractChannel.this.localAddress0();
/*      */     }
/*      */ 
/*      */     
/*      */     public final SocketAddress remoteAddress() {
/*  449 */       return AbstractChannel.this.remoteAddress0();
/*      */     }
/*      */ 
/*      */     
/*      */     public final void register(EventLoop eventLoop, final ChannelPromise promise) {
/*  454 */       ObjectUtil.checkNotNull(eventLoop, "eventLoop");
/*  455 */       if (AbstractChannel.this.isRegistered()) {
/*  456 */         promise.setFailure(new IllegalStateException("registered to an event loop already"));
/*      */         return;
/*      */       } 
/*  459 */       if (!AbstractChannel.this.isCompatible(eventLoop)) {
/*  460 */         promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop
/*  461 */               .getClass().getName()));
/*      */         
/*      */         return;
/*      */       } 
/*  465 */       AbstractChannel.this.eventLoop = eventLoop;
/*      */       
/*  467 */       if (eventLoop.inEventLoop()) {
/*  468 */         register0(promise);
/*      */       } else {
/*      */         try {
/*  471 */           eventLoop.execute(new Runnable()
/*      */               {
/*      */                 public void run() {
/*  474 */                   AbstractChannel.AbstractUnsafe.this.register0(promise);
/*      */                 }
/*      */               });
/*  477 */         } catch (Throwable t) {
/*  478 */           AbstractChannel.logger.warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", AbstractChannel.this, t);
/*      */ 
/*      */           
/*  481 */           closeForcibly();
/*  482 */           AbstractChannel.this.closeFuture.setClosed();
/*  483 */           safeSetFailure(promise, t);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void register0(ChannelPromise promise) {
/*      */       try {
/*  492 */         if (!promise.setUncancellable() || !ensureOpen(promise)) {
/*      */           return;
/*      */         }
/*  495 */         boolean firstRegistration = this.neverRegistered;
/*  496 */         AbstractChannel.this.doRegister();
/*  497 */         this.neverRegistered = false;
/*  498 */         AbstractChannel.this.registered = true;
/*      */ 
/*      */ 
/*      */         
/*  502 */         AbstractChannel.this.pipeline.invokeHandlerAddedIfNeeded();
/*      */         
/*  504 */         safeSetSuccess(promise);
/*  505 */         AbstractChannel.this.pipeline.fireChannelRegistered();
/*      */ 
/*      */         
/*  508 */         if (AbstractChannel.this.isActive()) {
/*  509 */           if (firstRegistration) {
/*  510 */             AbstractChannel.this.pipeline.fireChannelActive();
/*  511 */           } else if (AbstractChannel.this.config().isAutoRead()) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  516 */             beginRead();
/*      */           } 
/*      */         }
/*  519 */       } catch (Throwable t) {
/*      */         
/*  521 */         closeForcibly();
/*  522 */         AbstractChannel.this.closeFuture.setClosed();
/*  523 */         safeSetFailure(promise, t);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public final void bind(SocketAddress localAddress, ChannelPromise promise) {
/*  529 */       assertEventLoop();
/*      */       
/*  531 */       if (!promise.setUncancellable() || !ensureOpen(promise)) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/*  536 */       if (Boolean.TRUE.equals(AbstractChannel.this.config().getOption(ChannelOption.SO_BROADCAST)) && localAddress instanceof InetSocketAddress && 
/*      */         
/*  538 */         !((InetSocketAddress)localAddress).getAddress().isAnyLocalAddress() && 
/*  539 */         !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser())
/*      */       {
/*      */         
/*  542 */         AbstractChannel.logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address (" + localAddress + ") anyway as requested.");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  548 */       boolean wasActive = AbstractChannel.this.isActive();
/*      */       try {
/*  550 */         AbstractChannel.this.doBind(localAddress);
/*  551 */       } catch (Throwable t) {
/*  552 */         safeSetFailure(promise, t);
/*  553 */         closeIfClosed();
/*      */         
/*      */         return;
/*      */       } 
/*  557 */       if (!wasActive && AbstractChannel.this.isActive()) {
/*  558 */         invokeLater(new Runnable()
/*      */             {
/*      */               public void run() {
/*  561 */                 AbstractChannel.this.pipeline.fireChannelActive();
/*      */               }
/*      */             });
/*      */       }
/*      */       
/*  566 */       safeSetSuccess(promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void disconnect(ChannelPromise promise) {
/*  571 */       assertEventLoop();
/*      */       
/*  573 */       if (!promise.setUncancellable()) {
/*      */         return;
/*      */       }
/*      */       
/*  577 */       boolean wasActive = AbstractChannel.this.isActive();
/*      */       try {
/*  579 */         AbstractChannel.this.doDisconnect();
/*      */         
/*  581 */         AbstractChannel.this.remoteAddress = null;
/*  582 */         AbstractChannel.this.localAddress = null;
/*  583 */       } catch (Throwable t) {
/*  584 */         safeSetFailure(promise, t);
/*  585 */         closeIfClosed();
/*      */         
/*      */         return;
/*      */       } 
/*  589 */       if (wasActive && !AbstractChannel.this.isActive()) {
/*  590 */         invokeLater(new Runnable()
/*      */             {
/*      */               public void run() {
/*  593 */                 AbstractChannel.this.pipeline.fireChannelInactive();
/*      */               }
/*      */             });
/*      */       }
/*      */       
/*  598 */       safeSetSuccess(promise);
/*  599 */       closeIfClosed();
/*      */     }
/*      */ 
/*      */     
/*      */     public final void close(ChannelPromise promise) {
/*  604 */       assertEventLoop();
/*      */       
/*  606 */       ClosedChannelException closedChannelException = new ClosedChannelException();
/*  607 */       close(promise, closedChannelException, closedChannelException, false);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void shutdownOutput(ChannelPromise promise) {
/*  616 */       assertEventLoop();
/*  617 */       shutdownOutput(promise, null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void shutdownOutput(final ChannelPromise promise, Throwable cause) {
/*  626 */       if (!promise.setUncancellable()) {
/*      */         return;
/*      */       }
/*      */       
/*  630 */       final ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
/*  631 */       if (outboundBuffer == null) {
/*  632 */         promise.setFailure(new ClosedChannelException());
/*      */         return;
/*      */       } 
/*  635 */       this.outboundBuffer = null;
/*      */       
/*  637 */       final ChannelOutputShutdownException shutdownCause = (cause == null) ? new ChannelOutputShutdownException("Channel output shutdown") : new ChannelOutputShutdownException("Channel output shutdown", cause);
/*      */ 
/*      */       
/*  640 */       Executor closeExecutor = prepareToClose();
/*  641 */       if (closeExecutor != null) {
/*  642 */         closeExecutor.execute(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/*      */                 try {
/*  647 */                   AbstractChannel.this.doShutdownOutput();
/*  648 */                   promise.setSuccess();
/*  649 */                 } catch (Throwable err) {
/*  650 */                   promise.setFailure(err);
/*      */                 } finally {
/*      */                   
/*  653 */                   AbstractChannel.this.eventLoop().execute(new Runnable()
/*      */                       {
/*      */                         public void run() {
/*  656 */                           AbstractChannel.AbstractUnsafe.this.closeOutboundBufferForShutdown(AbstractChannel.this.pipeline, outboundBuffer, shutdownCause);
/*      */                         }
/*      */                       });
/*      */                 } 
/*      */               }
/*      */             });
/*      */       } else {
/*      */         
/*      */         try {
/*  665 */           AbstractChannel.this.doShutdownOutput();
/*  666 */           promise.setSuccess();
/*  667 */         } catch (Throwable err) {
/*  668 */           promise.setFailure(err);
/*      */         } finally {
/*  670 */           closeOutboundBufferForShutdown(AbstractChannel.this.pipeline, outboundBuffer, (Throwable)channelOutputShutdownException);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void closeOutboundBufferForShutdown(ChannelPipeline pipeline, ChannelOutboundBuffer buffer, Throwable cause) {
/*  677 */       buffer.failFlushed(cause, false);
/*  678 */       buffer.close(cause, true);
/*  679 */       pipeline.fireUserEventTriggered(ChannelOutputShutdownEvent.INSTANCE);
/*      */     }
/*      */ 
/*      */     
/*      */     private void close(final ChannelPromise promise, final Throwable cause, final ClosedChannelException closeCause, final boolean notify) {
/*  684 */       if (!promise.setUncancellable()) {
/*      */         return;
/*      */       }
/*      */       
/*  688 */       if (AbstractChannel.this.closeInitiated) {
/*  689 */         if (AbstractChannel.this.closeFuture.isDone()) {
/*      */           
/*  691 */           safeSetSuccess(promise);
/*  692 */         } else if (!(promise instanceof VoidChannelPromise)) {
/*      */           
/*  694 */           AbstractChannel.this.closeFuture.addListener(new ChannelFutureListener()
/*      */               {
/*      */                 public void operationComplete(ChannelFuture future) throws Exception {
/*  697 */                   promise.setSuccess();
/*      */                 }
/*      */               });
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  704 */       AbstractChannel.this.closeInitiated = true;
/*      */       
/*  706 */       final boolean wasActive = AbstractChannel.this.isActive();
/*  707 */       final ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
/*  708 */       this.outboundBuffer = null;
/*  709 */       Executor closeExecutor = prepareToClose();
/*  710 */       if (closeExecutor != null) {
/*  711 */         closeExecutor.execute(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/*      */                 try {
/*  716 */                   AbstractChannel.AbstractUnsafe.this.doClose0(promise);
/*      */                 } finally {
/*      */                   
/*  719 */                   AbstractChannel.AbstractUnsafe.this.invokeLater(new Runnable()
/*      */                       {
/*      */                         public void run() {
/*  722 */                           if (outboundBuffer != null) {
/*      */                             
/*  724 */                             outboundBuffer.failFlushed(cause, notify);
/*  725 */                             outboundBuffer.close(closeCause);
/*      */                           } 
/*  727 */                           AbstractChannel.AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
/*      */                         }
/*      */                       });
/*      */                 } 
/*      */               }
/*      */             });
/*      */       } else {
/*      */         
/*      */         try {
/*  736 */           doClose0(promise);
/*      */         } finally {
/*  738 */           if (outboundBuffer != null) {
/*      */             
/*  740 */             outboundBuffer.failFlushed(cause, notify);
/*  741 */             outboundBuffer.close(closeCause);
/*      */           } 
/*      */         } 
/*  744 */         if (this.inFlush0) {
/*  745 */           invokeLater(new Runnable()
/*      */               {
/*      */                 public void run() {
/*  748 */                   AbstractChannel.AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
/*      */                 }
/*      */               });
/*      */         } else {
/*  752 */           fireChannelInactiveAndDeregister(wasActive);
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     private void doClose0(ChannelPromise promise) {
/*      */       try {
/*  759 */         AbstractChannel.this.doClose();
/*  760 */         AbstractChannel.this.closeFuture.setClosed();
/*  761 */         safeSetSuccess(promise);
/*  762 */       } catch (Throwable t) {
/*  763 */         AbstractChannel.this.closeFuture.setClosed();
/*  764 */         safeSetFailure(promise, t);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void fireChannelInactiveAndDeregister(boolean wasActive) {
/*  769 */       deregister(voidPromise(), (wasActive && !AbstractChannel.this.isActive()));
/*      */     }
/*      */ 
/*      */     
/*      */     public final void closeForcibly() {
/*  774 */       assertEventLoop();
/*      */       
/*      */       try {
/*  777 */         AbstractChannel.this.doClose();
/*  778 */       } catch (Exception e) {
/*  779 */         AbstractChannel.logger.warn("Failed to close a channel.", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public final void deregister(ChannelPromise promise) {
/*  785 */       assertEventLoop();
/*      */       
/*  787 */       deregister(promise, false);
/*      */     }
/*      */     
/*      */     private void deregister(final ChannelPromise promise, final boolean fireChannelInactive) {
/*  791 */       if (!promise.setUncancellable()) {
/*      */         return;
/*      */       }
/*      */       
/*  795 */       if (!AbstractChannel.this.registered) {
/*  796 */         safeSetSuccess(promise);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  809 */       invokeLater(new Runnable()
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  813 */                 AbstractChannel.this.doDeregister();
/*  814 */               } catch (Throwable t) {
/*  815 */                 AbstractChannel.logger.warn("Unexpected exception occurred while deregistering a channel.", t);
/*      */               } finally {
/*  817 */                 if (fireChannelInactive) {
/*  818 */                   AbstractChannel.this.pipeline.fireChannelInactive();
/*      */                 }
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  824 */                 if (AbstractChannel.this.registered) {
/*  825 */                   AbstractChannel.this.registered = false;
/*  826 */                   AbstractChannel.this.pipeline.fireChannelUnregistered();
/*      */                 } 
/*  828 */                 AbstractChannel.AbstractUnsafe.this.safeSetSuccess(promise);
/*      */               } 
/*      */             }
/*      */           });
/*      */     }
/*      */ 
/*      */     
/*      */     public final void beginRead() {
/*  836 */       assertEventLoop();
/*      */       
/*  838 */       if (!AbstractChannel.this.isActive()) {
/*      */         return;
/*      */       }
/*      */       
/*      */       try {
/*  843 */         AbstractChannel.this.doBeginRead();
/*  844 */       } catch (Exception e) {
/*  845 */         invokeLater(new Runnable()
/*      */             {
/*      */               public void run() {
/*  848 */                 AbstractChannel.this.pipeline.fireExceptionCaught(e);
/*      */               }
/*      */             });
/*  851 */         close(voidPromise());
/*      */       } 
/*      */     }
/*      */     
/*      */     public final void write(Object msg, ChannelPromise promise) {
/*      */       int size;
/*  857 */       assertEventLoop();
/*      */       
/*  859 */       ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
/*  860 */       if (outboundBuffer == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  865 */         safeSetFailure(promise, newClosedChannelException(AbstractChannel.this.initialCloseCause));
/*      */         
/*  867 */         ReferenceCountUtil.release(msg);
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*      */       try {
/*  873 */         msg = AbstractChannel.this.filterOutboundMessage(msg);
/*  874 */         size = AbstractChannel.this.pipeline.estimatorHandle().size(msg);
/*  875 */         if (size < 0) {
/*  876 */           size = 0;
/*      */         }
/*  878 */       } catch (Throwable t) {
/*  879 */         safeSetFailure(promise, t);
/*  880 */         ReferenceCountUtil.release(msg);
/*      */         
/*      */         return;
/*      */       } 
/*  884 */       outboundBuffer.addMessage(msg, size, promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void flush() {
/*  889 */       assertEventLoop();
/*      */       
/*  891 */       ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
/*  892 */       if (outboundBuffer == null) {
/*      */         return;
/*      */       }
/*      */       
/*  896 */       outboundBuffer.addFlush();
/*  897 */       flush0();
/*      */     }
/*      */ 
/*      */     
/*      */     protected void flush0() {
/*  902 */       if (this.inFlush0) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/*  907 */       ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
/*  908 */       if (outboundBuffer == null || outboundBuffer.isEmpty()) {
/*      */         return;
/*      */       }
/*      */       
/*  912 */       this.inFlush0 = true;
/*      */ 
/*      */       
/*  915 */       if (!AbstractChannel.this.isActive()) {
/*      */         try {
/*  917 */           if (AbstractChannel.this.isOpen()) {
/*  918 */             outboundBuffer.failFlushed(new NotYetConnectedException(), true);
/*      */           } else {
/*      */             
/*  921 */             outboundBuffer.failFlushed(newClosedChannelException(AbstractChannel.this.initialCloseCause), false);
/*      */           } 
/*      */         } finally {
/*  924 */           this.inFlush0 = false;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */       try {
/*  930 */         AbstractChannel.this.doWrite(outboundBuffer);
/*  931 */       } catch (Throwable t) {
/*  932 */         if (t instanceof IOException && AbstractChannel.this.config().isAutoClose()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  941 */           AbstractChannel.this.initialCloseCause = t;
/*  942 */           close(voidPromise(), t, newClosedChannelException(t), false);
/*      */         } else {
/*      */           try {
/*  945 */             shutdownOutput(voidPromise(), t);
/*  946 */           } catch (Throwable t2) {
/*  947 */             AbstractChannel.this.initialCloseCause = t;
/*  948 */             close(voidPromise(), t2, newClosedChannelException(t), false);
/*      */           } 
/*      */         } 
/*      */       } finally {
/*  952 */         this.inFlush0 = false;
/*      */       } 
/*      */     }
/*      */     
/*      */     private ClosedChannelException newClosedChannelException(Throwable cause) {
/*  957 */       ClosedChannelException exception = new ClosedChannelException();
/*  958 */       if (cause != null) {
/*  959 */         exception.initCause(cause);
/*      */       }
/*  961 */       return exception;
/*      */     }
/*      */ 
/*      */     
/*      */     public final ChannelPromise voidPromise() {
/*  966 */       assertEventLoop();
/*      */       
/*  968 */       return AbstractChannel.this.unsafeVoidPromise;
/*      */     }
/*      */     
/*      */     protected final boolean ensureOpen(ChannelPromise promise) {
/*  972 */       if (AbstractChannel.this.isOpen()) {
/*  973 */         return true;
/*      */       }
/*      */       
/*  976 */       safeSetFailure(promise, newClosedChannelException(AbstractChannel.this.initialCloseCause));
/*  977 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final void safeSetSuccess(ChannelPromise promise) {
/*  984 */       if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
/*  985 */         AbstractChannel.logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final void safeSetFailure(ChannelPromise promise, Throwable cause) {
/*  993 */       if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
/*  994 */         AbstractChannel.logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
/*      */       }
/*      */     }
/*      */     
/*      */     protected final void closeIfClosed() {
/*  999 */       if (AbstractChannel.this.isOpen()) {
/*      */         return;
/*      */       }
/* 1002 */       close(voidPromise());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void invokeLater(Runnable task) {
/*      */       try {
/* 1018 */         AbstractChannel.this.eventLoop().execute(task);
/* 1019 */       } catch (RejectedExecutionException e) {
/* 1020 */         AbstractChannel.logger.warn("Can't invoke task later as EventLoop rejected it", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Throwable annotateConnectException(Throwable cause, SocketAddress remoteAddress) {
/* 1028 */       if (cause instanceof ConnectException) {
/* 1029 */         return new AbstractChannel.AnnotatedConnectException((ConnectException)cause, remoteAddress);
/*      */       }
/* 1031 */       if (cause instanceof NoRouteToHostException) {
/* 1032 */         return new AbstractChannel.AnnotatedNoRouteToHostException((NoRouteToHostException)cause, remoteAddress);
/*      */       }
/* 1034 */       if (cause instanceof SocketException) {
/* 1035 */         return new AbstractChannel.AnnotatedSocketException((SocketException)cause, remoteAddress);
/*      */       }
/*      */       
/* 1038 */       return cause;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Executor prepareToClose() {
/* 1048 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doRegister() throws Exception {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doShutdownOutput() throws Exception {
/* 1097 */     doClose();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doDeregister() throws Exception {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object filterOutboundMessage(Object msg) throws Exception {
/* 1124 */     return msg;
/*      */   } protected abstract AbstractUnsafe newUnsafe(); protected abstract boolean isCompatible(EventLoop paramEventLoop); protected abstract SocketAddress localAddress0(); protected abstract SocketAddress remoteAddress0();
/*      */   protected abstract void doBind(SocketAddress paramSocketAddress) throws Exception;
/*      */   protected void validateFileRegion(DefaultFileRegion region, long position) throws IOException {
/* 1128 */     DefaultFileRegion.validate(region, position);
/*      */   } protected abstract void doDisconnect() throws Exception;
/*      */   protected abstract void doClose() throws Exception;
/*      */   protected abstract void doBeginRead() throws Exception;
/*      */   protected abstract void doWrite(ChannelOutboundBuffer paramChannelOutboundBuffer) throws Exception;
/*      */   static final class CloseFuture extends DefaultChannelPromise { CloseFuture(AbstractChannel ch) {
/* 1134 */       super(ch);
/*      */     }
/*      */ 
/*      */     
/*      */     public ChannelPromise setSuccess() {
/* 1139 */       throw new IllegalStateException();
/*      */     }
/*      */ 
/*      */     
/*      */     public ChannelPromise setFailure(Throwable cause) {
/* 1144 */       throw new IllegalStateException();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean trySuccess() {
/* 1149 */       throw new IllegalStateException();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean tryFailure(Throwable cause) {
/* 1154 */       throw new IllegalStateException();
/*      */     }
/*      */     
/*      */     boolean setClosed() {
/* 1158 */       return super.trySuccess();
/*      */     } }
/*      */ 
/*      */   
/*      */   private static final class AnnotatedConnectException
/*      */     extends ConnectException {
/*      */     private static final long serialVersionUID = 3901958112696433556L;
/*      */     
/*      */     AnnotatedConnectException(ConnectException exception, SocketAddress remoteAddress) {
/* 1167 */       super(exception.getMessage() + ": " + remoteAddress);
/* 1168 */       initCause(exception);
/*      */     }
/*      */ 
/*      */     
/*      */     public Throwable fillInStackTrace() {
/* 1173 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class AnnotatedNoRouteToHostException
/*      */     extends NoRouteToHostException {
/*      */     private static final long serialVersionUID = -6801433937592080623L;
/*      */     
/*      */     AnnotatedNoRouteToHostException(NoRouteToHostException exception, SocketAddress remoteAddress) {
/* 1182 */       super(exception.getMessage() + ": " + remoteAddress);
/* 1183 */       initCause(exception);
/*      */     }
/*      */ 
/*      */     
/*      */     public Throwable fillInStackTrace() {
/* 1188 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class AnnotatedSocketException
/*      */     extends SocketException {
/*      */     private static final long serialVersionUID = 3896743275010454039L;
/*      */     
/*      */     AnnotatedSocketException(SocketException exception, SocketAddress remoteAddress) {
/* 1197 */       super(exception.getMessage() + ": " + remoteAddress);
/* 1198 */       initCause(exception);
/*      */     }
/*      */ 
/*      */     
/*      */     public Throwable fillInStackTrace() {
/* 1203 */       return this;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AbstractChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */