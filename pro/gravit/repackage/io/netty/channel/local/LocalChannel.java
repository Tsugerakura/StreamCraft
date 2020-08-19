/*     */ package pro.gravit.repackage.io.netty.channel.local;
/*     */ 
/*     */ import java.net.ConnectException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.AlreadyConnectedException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.PreferHeapByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.SingleThreadEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
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
/*     */ public class LocalChannel
/*     */   extends AbstractChannel
/*     */ {
/*  51 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannel.class);
/*     */ 
/*     */   
/*  54 */   private static final AtomicReferenceFieldUpdater<LocalChannel, Future> FINISH_READ_FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(LocalChannel.class, Future.class, "finishReadFuture");
/*  55 */   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
/*     */   private static final int MAX_READER_STACK_DEPTH = 8;
/*     */   
/*  58 */   private enum State { OPEN, BOUND, CONNECTED, CLOSED; }
/*     */   
/*  60 */   private final ChannelConfig config = (ChannelConfig)new DefaultChannelConfig((Channel)this);
/*     */   
/*  62 */   final Queue<Object> inboundBuffer = PlatformDependent.newSpscQueue();
/*  63 */   private final Runnable readTask = new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/*  67 */         if (!LocalChannel.this.inboundBuffer.isEmpty()) {
/*  68 */           LocalChannel.this.readInbound();
/*     */         }
/*     */       }
/*     */     };
/*     */   
/*  73 */   private final Runnable shutdownHook = new Runnable()
/*     */     {
/*     */       public void run() {
/*  76 */         LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
/*     */       }
/*     */     };
/*     */   
/*     */   private volatile State state;
/*     */   private volatile LocalChannel peer;
/*     */   private volatile LocalAddress localAddress;
/*     */   private volatile LocalAddress remoteAddress;
/*     */   private volatile ChannelPromise connectPromise;
/*     */   private volatile boolean readInProgress;
/*     */   private volatile boolean writeInProgress;
/*     */   private volatile Future<?> finishReadFuture;
/*     */   
/*     */   public LocalChannel() {
/*  90 */     super(null);
/*  91 */     config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(this.config.getAllocator()));
/*     */   }
/*     */   
/*     */   protected LocalChannel(LocalServerChannel parent, LocalChannel peer) {
/*  95 */     super((Channel)parent);
/*  96 */     config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(this.config.getAllocator()));
/*  97 */     this.peer = peer;
/*  98 */     this.localAddress = parent.localAddress();
/*  99 */     this.remoteAddress = peer.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 104 */     return METADATA;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig config() {
/* 109 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalServerChannel parent() {
/* 114 */     return (LocalServerChannel)super.parent();
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalAddress localAddress() {
/* 119 */     return (LocalAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalAddress remoteAddress() {
/* 124 */     return (LocalAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 129 */     return (this.state != State.CLOSED);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 134 */     return (this.state == State.CONNECTED);
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractChannel.AbstractUnsafe newUnsafe() {
/* 139 */     return new LocalUnsafe();
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCompatible(EventLoop loop) {
/* 144 */     return loop instanceof pro.gravit.repackage.io.netty.channel.SingleThreadEventLoop;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 149 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 154 */     return this.remoteAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doRegister() throws Exception {
/* 164 */     if (this.peer != null && parent() != null) {
/*     */ 
/*     */       
/* 167 */       final LocalChannel peer = this.peer;
/* 168 */       this.state = State.CONNECTED;
/*     */       
/* 170 */       peer.remoteAddress = (parent() == null) ? null : parent().localAddress();
/* 171 */       peer.state = State.CONNECTED;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 177 */       peer.eventLoop().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 180 */               ChannelPromise promise = peer.connectPromise;
/*     */ 
/*     */ 
/*     */               
/* 184 */               if (promise != null && promise.trySuccess()) {
/* 185 */                 peer.pipeline().fireChannelActive();
/*     */               }
/*     */             }
/*     */           });
/*     */     } 
/* 190 */     ((SingleThreadEventExecutor)eventLoop()).addShutdownHook(this.shutdownHook);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 195 */     this
/* 196 */       .localAddress = LocalChannelRegistry.register((Channel)this, this.localAddress, localAddress);
/*     */     
/* 198 */     this.state = State.BOUND;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 203 */     doClose();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 208 */     final LocalChannel peer = this.peer;
/* 209 */     State oldState = this.state;
/*     */     try {
/* 211 */       if (oldState != State.CLOSED) {
/*     */         
/* 213 */         if (this.localAddress != null) {
/* 214 */           if (parent() == null) {
/* 215 */             LocalChannelRegistry.unregister(this.localAddress);
/*     */           }
/* 217 */           this.localAddress = null;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 222 */         this.state = State.CLOSED;
/*     */ 
/*     */         
/* 225 */         if (this.writeInProgress && peer != null) {
/* 226 */           finishPeerRead(peer);
/*     */         }
/*     */         
/* 229 */         ChannelPromise promise = this.connectPromise;
/* 230 */         if (promise != null) {
/*     */           
/* 232 */           promise.tryFailure(new ClosedChannelException());
/* 233 */           this.connectPromise = null;
/*     */         } 
/*     */       } 
/*     */       
/* 237 */       if (peer != null) {
/* 238 */         this.peer = null;
/*     */ 
/*     */ 
/*     */         
/* 242 */         EventLoop peerEventLoop = peer.eventLoop();
/* 243 */         final boolean peerIsActive = peer.isActive();
/*     */         try {
/* 245 */           peerEventLoop.execute(new Runnable()
/*     */               {
/*     */                 public void run() {
/* 248 */                   peer.tryClose(peerIsActive);
/*     */                 }
/*     */               });
/* 251 */         } catch (Throwable cause) {
/* 252 */           logger.warn("Releasing Inbound Queues for channels {}-{} because exception occurred!", new Object[] { this, peer, cause });
/*     */           
/* 254 */           if (peerEventLoop.inEventLoop()) {
/* 255 */             peer.releaseInboundBuffers();
/*     */           }
/*     */           else {
/*     */             
/* 259 */             peer.close();
/*     */           } 
/* 261 */           PlatformDependent.throwException(cause);
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       
/* 266 */       if (oldState != null && oldState != State.CLOSED)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 271 */         releaseInboundBuffers();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void tryClose(boolean isActive) {
/* 277 */     if (isActive) {
/* 278 */       unsafe().close(unsafe().voidPromise());
/*     */     } else {
/* 280 */       releaseInboundBuffers();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doDeregister() throws Exception {
/* 287 */     ((SingleThreadEventExecutor)eventLoop()).removeShutdownHook(this.shutdownHook);
/*     */   }
/*     */   
/*     */   private void readInbound() {
/* 291 */     RecvByteBufAllocator.Handle handle = unsafe().recvBufAllocHandle();
/* 292 */     handle.reset(config());
/* 293 */     ChannelPipeline pipeline = pipeline();
/*     */     do {
/* 295 */       Object received = this.inboundBuffer.poll();
/* 296 */       if (received == null) {
/*     */         break;
/*     */       }
/* 299 */       pipeline.fireChannelRead(received);
/* 300 */     } while (handle.continueReading());
/*     */     
/* 302 */     pipeline.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {
/* 307 */     if (this.readInProgress) {
/*     */       return;
/*     */     }
/*     */     
/* 311 */     Queue<Object> inboundBuffer = this.inboundBuffer;
/* 312 */     if (inboundBuffer.isEmpty()) {
/* 313 */       this.readInProgress = true;
/*     */       
/*     */       return;
/*     */     } 
/* 317 */     InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
/* 318 */     Integer stackDepth = Integer.valueOf(threadLocals.localChannelReaderStackDepth());
/* 319 */     if (stackDepth.intValue() < 8) {
/* 320 */       threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue() + 1);
/*     */       try {
/* 322 */         readInbound();
/*     */       } finally {
/* 324 */         threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue());
/*     */       } 
/*     */     } else {
/*     */       try {
/* 328 */         eventLoop().execute(this.readTask);
/* 329 */       } catch (Throwable cause) {
/* 330 */         logger.warn("Closing Local channels {}-{} because exception occurred!", new Object[] { this, this.peer, cause });
/* 331 */         close();
/* 332 */         this.peer.close();
/* 333 */         PlatformDependent.throwException(cause);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/* 340 */     switch (this.state) {
/*     */       case OPEN:
/*     */       case BOUND:
/* 343 */         throw new NotYetConnectedException();
/*     */       case CLOSED:
/* 345 */         throw new ClosedChannelException();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 350 */     LocalChannel peer = this.peer;
/*     */     
/* 352 */     this.writeInProgress = true;
/*     */     try {
/* 354 */       ClosedChannelException exception = null;
/*     */       while (true) {
/* 356 */         Object msg = in.current();
/* 357 */         if (msg == null) {
/*     */           break;
/*     */         }
/*     */ 
/*     */         
/*     */         try {
/* 363 */           if (peer.state == State.CONNECTED) {
/* 364 */             peer.inboundBuffer.add(ReferenceCountUtil.retain(msg));
/* 365 */             in.remove(); continue;
/*     */           } 
/* 367 */           if (exception == null) {
/* 368 */             exception = new ClosedChannelException();
/*     */           }
/* 370 */           in.remove(exception);
/*     */         }
/* 372 */         catch (Throwable cause) {
/* 373 */           in.remove(cause);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 382 */       this.writeInProgress = false;
/*     */     } 
/*     */     
/* 385 */     finishPeerRead(peer);
/*     */   }
/*     */ 
/*     */   
/*     */   private void finishPeerRead(LocalChannel peer) {
/* 390 */     if (peer.eventLoop() == eventLoop() && !peer.writeInProgress) {
/* 391 */       finishPeerRead0(peer);
/*     */     } else {
/* 393 */       runFinishPeerReadTask(peer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void runFinishPeerReadTask(final LocalChannel peer) {
/* 400 */     Runnable finishPeerReadTask = new Runnable()
/*     */       {
/*     */         public void run() {
/* 403 */           LocalChannel.this.finishPeerRead0(peer);
/*     */         }
/*     */       };
/*     */     try {
/* 407 */       if (peer.writeInProgress) {
/* 408 */         peer.finishReadFuture = peer.eventLoop().submit(finishPeerReadTask);
/*     */       } else {
/* 410 */         peer.eventLoop().execute(finishPeerReadTask);
/*     */       } 
/* 412 */     } catch (Throwable cause) {
/* 413 */       logger.warn("Closing Local channels {}-{} because exception occurred!", new Object[] { this, peer, cause });
/* 414 */       close();
/* 415 */       peer.close();
/* 416 */       PlatformDependent.throwException(cause);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void releaseInboundBuffers() {
/* 421 */     assert eventLoop() == null || eventLoop().inEventLoop();
/* 422 */     this.readInProgress = false;
/* 423 */     Queue<Object> inboundBuffer = this.inboundBuffer;
/*     */     Object msg;
/* 425 */     while ((msg = inboundBuffer.poll()) != null) {
/* 426 */       ReferenceCountUtil.release(msg);
/*     */     }
/*     */   }
/*     */   
/*     */   private void finishPeerRead0(LocalChannel peer) {
/* 431 */     Future<?> peerFinishReadFuture = peer.finishReadFuture;
/* 432 */     if (peerFinishReadFuture != null) {
/* 433 */       if (!peerFinishReadFuture.isDone()) {
/* 434 */         runFinishPeerReadTask(peer);
/*     */         
/*     */         return;
/*     */       } 
/* 438 */       FINISH_READ_FUTURE_UPDATER.compareAndSet(peer, peerFinishReadFuture, null);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 443 */     if (peer.readInProgress && !peer.inboundBuffer.isEmpty()) {
/* 444 */       peer.readInProgress = false;
/* 445 */       peer.readInbound();
/*     */     } 
/*     */   }
/*     */   private class LocalUnsafe extends AbstractChannel.AbstractUnsafe { private LocalUnsafe() {
/* 449 */       super(LocalChannel.this);
/*     */     }
/*     */ 
/*     */     
/*     */     public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 454 */       if (!promise.setUncancellable() || !ensureOpen(promise)) {
/*     */         return;
/*     */       }
/*     */       
/* 458 */       if (LocalChannel.this.state == LocalChannel.State.CONNECTED) {
/* 459 */         Exception cause = new AlreadyConnectedException();
/* 460 */         safeSetFailure(promise, cause);
/* 461 */         LocalChannel.this.pipeline().fireExceptionCaught(cause);
/*     */         
/*     */         return;
/*     */       } 
/* 465 */       if (LocalChannel.this.connectPromise != null) {
/* 466 */         throw new ConnectionPendingException();
/*     */       }
/*     */       
/* 469 */       LocalChannel.this.connectPromise = promise;
/*     */       
/* 471 */       if (LocalChannel.this.state != LocalChannel.State.BOUND)
/*     */       {
/* 473 */         if (localAddress == null) {
/* 474 */           localAddress = new LocalAddress((Channel)LocalChannel.this);
/*     */         }
/*     */       }
/*     */       
/* 478 */       if (localAddress != null) {
/*     */         try {
/* 480 */           LocalChannel.this.doBind(localAddress);
/* 481 */         } catch (Throwable t) {
/* 482 */           safeSetFailure(promise, t);
/* 483 */           close(voidPromise());
/*     */           
/*     */           return;
/*     */         } 
/*     */       }
/* 488 */       Channel boundChannel = LocalChannelRegistry.get(remoteAddress);
/* 489 */       if (!(boundChannel instanceof LocalServerChannel)) {
/* 490 */         Exception cause = new ConnectException("connection refused: " + remoteAddress);
/* 491 */         safeSetFailure(promise, cause);
/* 492 */         close(voidPromise());
/*     */         
/*     */         return;
/*     */       } 
/* 496 */       LocalServerChannel serverChannel = (LocalServerChannel)boundChannel;
/* 497 */       LocalChannel.this.peer = serverChannel.serve(LocalChannel.this);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\local\LocalChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */