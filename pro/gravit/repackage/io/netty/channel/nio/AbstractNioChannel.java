/*     */ package pro.gravit.repackage.io.netty.channel.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ConnectTimeoutException;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ public abstract class AbstractNioChannel
/*     */   extends AbstractChannel
/*     */ {
/*  51 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
/*     */   private final SelectableChannel ch;
/*     */   protected final int readInterestOp;
/*     */   volatile SelectionKey selectionKey;
/*     */   boolean readPending;
/*     */   
/*  57 */   private final Runnable clearReadPendingRunnable = new Runnable()
/*     */     {
/*     */       public void run() {
/*  60 */         AbstractNioChannel.this.clearReadPending0();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelPromise connectPromise;
/*     */ 
/*     */ 
/*     */   
/*     */   private ScheduledFuture<?> connectTimeoutFuture;
/*     */ 
/*     */ 
/*     */   
/*     */   private SocketAddress requestedRemoteAddress;
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
/*  80 */     super(parent);
/*  81 */     this.ch = ch;
/*  82 */     this.readInterestOp = readInterestOp;
/*     */     try {
/*  84 */       ch.configureBlocking(false);
/*  85 */     } catch (IOException e) {
/*     */       try {
/*  87 */         ch.close();
/*  88 */       } catch (IOException e2) {
/*  89 */         logger.warn("Failed to close a partially initialized socket.", e2);
/*     */       } 
/*     */ 
/*     */       
/*  93 */       throw new ChannelException("Failed to enter non-blocking mode.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/*  99 */     return this.ch.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public NioUnsafe unsafe() {
/* 104 */     return (NioUnsafe)super.unsafe();
/*     */   }
/*     */   
/*     */   protected SelectableChannel javaChannel() {
/* 108 */     return this.ch;
/*     */   }
/*     */ 
/*     */   
/*     */   public NioEventLoop eventLoop() {
/* 113 */     return (NioEventLoop)super.eventLoop();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SelectionKey selectionKey() {
/* 120 */     assert this.selectionKey != null;
/* 121 */     return this.selectionKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected boolean isReadPending() {
/* 130 */     return this.readPending;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setReadPending(final boolean readPending) {
/* 139 */     if (isRegistered()) {
/* 140 */       NioEventLoop nioEventLoop = eventLoop();
/* 141 */       if (nioEventLoop.inEventLoop()) {
/* 142 */         setReadPending0(readPending);
/*     */       } else {
/* 144 */         nioEventLoop.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 147 */                 AbstractNioChannel.this.setReadPending0(readPending);
/*     */               }
/*     */             });
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 155 */       this.readPending = readPending;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void clearReadPending() {
/* 163 */     if (isRegistered()) {
/* 164 */       NioEventLoop nioEventLoop = eventLoop();
/* 165 */       if (nioEventLoop.inEventLoop()) {
/* 166 */         clearReadPending0();
/*     */       } else {
/* 168 */         nioEventLoop.execute(this.clearReadPendingRunnable);
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 174 */       this.readPending = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setReadPending0(boolean readPending) {
/* 179 */     this.readPending = readPending;
/* 180 */     if (!readPending) {
/* 181 */       ((AbstractNioUnsafe)unsafe()).removeReadOp();
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearReadPending0() {
/* 186 */     this.readPending = false;
/* 187 */     ((AbstractNioUnsafe)unsafe()).removeReadOp();
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface NioUnsafe
/*     */     extends Channel.Unsafe
/*     */   {
/*     */     SelectableChannel ch();
/*     */ 
/*     */     
/*     */     void finishConnect();
/*     */ 
/*     */     
/*     */     void read();
/*     */ 
/*     */     
/*     */     void forceFlush();
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract class AbstractNioUnsafe
/*     */     extends AbstractChannel.AbstractUnsafe
/*     */     implements NioUnsafe
/*     */   {
/*     */     protected AbstractNioUnsafe() {
/* 212 */       super(AbstractNioChannel.this);
/*     */     }
/*     */     protected final void removeReadOp() {
/* 215 */       SelectionKey key = AbstractNioChannel.this.selectionKey();
/*     */ 
/*     */ 
/*     */       
/* 219 */       if (!key.isValid()) {
/*     */         return;
/*     */       }
/* 222 */       int interestOps = key.interestOps();
/* 223 */       if ((interestOps & AbstractNioChannel.this.readInterestOp) != 0)
/*     */       {
/* 225 */         key.interestOps(interestOps & (AbstractNioChannel.this.readInterestOp ^ 0xFFFFFFFF));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public final SelectableChannel ch() {
/* 231 */       return AbstractNioChannel.this.javaChannel();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 237 */       if (!promise.setUncancellable() || !ensureOpen(promise)) {
/*     */         return;
/*     */       }
/*     */       
/*     */       try {
/* 242 */         if (AbstractNioChannel.this.connectPromise != null)
/*     */         {
/* 244 */           throw new ConnectionPendingException();
/*     */         }
/*     */         
/* 247 */         boolean wasActive = AbstractNioChannel.this.isActive();
/* 248 */         if (AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
/* 249 */           fulfillConnectPromise(promise, wasActive);
/*     */         } else {
/* 251 */           AbstractNioChannel.this.connectPromise = promise;
/* 252 */           AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
/*     */ 
/*     */           
/* 255 */           int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
/* 256 */           if (connectTimeoutMillis > 0) {
/* 257 */             AbstractNioChannel.this.connectTimeoutFuture = (ScheduledFuture<?>)AbstractNioChannel.this.eventLoop().schedule(new Runnable()
/*     */                 {
/*     */                   public void run() {
/* 260 */                     ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
/* 261 */                     ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
/*     */                     
/* 263 */                     if (connectPromise != null && connectPromise.tryFailure((Throwable)cause)) {
/* 264 */                       AbstractNioChannel.AbstractNioUnsafe.this.close(AbstractNioChannel.AbstractNioUnsafe.this.voidPromise());
/*     */                     }
/*     */                   }
/*     */                 },  connectTimeoutMillis, TimeUnit.MILLISECONDS);
/*     */           }
/*     */           
/* 270 */           promise.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */               {
/*     */                 public void operationComplete(ChannelFuture future) throws Exception {
/* 273 */                   if (future.isCancelled()) {
/* 274 */                     if (AbstractNioChannel.this.connectTimeoutFuture != null) {
/* 275 */                       AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
/*     */                     }
/* 277 */                     AbstractNioChannel.this.connectPromise = null;
/* 278 */                     AbstractNioChannel.AbstractNioUnsafe.this.close(AbstractNioChannel.AbstractNioUnsafe.this.voidPromise());
/*     */                   } 
/*     */                 }
/*     */               });
/*     */         } 
/* 283 */       } catch (Throwable t) {
/* 284 */         promise.tryFailure(annotateConnectException(t, remoteAddress));
/* 285 */         closeIfClosed();
/*     */       } 
/*     */     }
/*     */     
/*     */     private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
/* 290 */       if (promise == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 297 */       boolean active = AbstractNioChannel.this.isActive();
/*     */ 
/*     */       
/* 300 */       boolean promiseSet = promise.trySuccess();
/*     */ 
/*     */ 
/*     */       
/* 304 */       if (!wasActive && active) {
/* 305 */         AbstractNioChannel.this.pipeline().fireChannelActive();
/*     */       }
/*     */ 
/*     */       
/* 309 */       if (!promiseSet) {
/* 310 */         close(voidPromise());
/*     */       }
/*     */     }
/*     */     
/*     */     private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
/* 315 */       if (promise == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 321 */       promise.tryFailure(cause);
/* 322 */       closeIfClosed();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final void finishConnect() {
/* 330 */       assert AbstractNioChannel.this.eventLoop().inEventLoop();
/*     */       
/*     */       try {
/* 333 */         boolean wasActive = AbstractNioChannel.this.isActive();
/* 334 */         AbstractNioChannel.this.doFinishConnect();
/* 335 */         fulfillConnectPromise(AbstractNioChannel.this.connectPromise, wasActive);
/* 336 */       } catch (Throwable t) {
/* 337 */         fulfillConnectPromise(AbstractNioChannel.this.connectPromise, annotateConnectException(t, AbstractNioChannel.this.requestedRemoteAddress));
/*     */       }
/*     */       finally {
/*     */         
/* 341 */         if (AbstractNioChannel.this.connectTimeoutFuture != null) {
/* 342 */           AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
/*     */         }
/* 344 */         AbstractNioChannel.this.connectPromise = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final void flush0() {
/* 353 */       if (!isFlushPending()) {
/* 354 */         super.flush0();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void forceFlush() {
/* 361 */       super.flush0();
/*     */     }
/*     */     
/*     */     private boolean isFlushPending() {
/* 365 */       SelectionKey selectionKey = AbstractNioChannel.this.selectionKey();
/* 366 */       return (selectionKey.isValid() && (selectionKey.interestOps() & 0x4) != 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCompatible(EventLoop loop) {
/* 372 */     return loop instanceof NioEventLoop;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doRegister() throws Exception {
/* 377 */     boolean selected = false;
/*     */     while (true) {
/*     */       try {
/* 380 */         this.selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
/*     */         return;
/* 382 */       } catch (CancelledKeyException e) {
/* 383 */         if (!selected) {
/*     */ 
/*     */           
/* 386 */           eventLoop().selectNow();
/* 387 */           selected = true; continue;
/*     */         }  break;
/*     */       } 
/*     */     } 
/* 391 */     throw e;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doDeregister() throws Exception {
/* 399 */     eventLoop().cancel(selectionKey());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {
/* 405 */     SelectionKey selectionKey = this.selectionKey;
/* 406 */     if (!selectionKey.isValid()) {
/*     */       return;
/*     */     }
/*     */     
/* 410 */     this.readPending = true;
/*     */     
/* 412 */     int interestOps = selectionKey.interestOps();
/* 413 */     if ((interestOps & this.readInterestOp) == 0) {
/* 414 */       selectionKey.interestOps(interestOps | this.readInterestOp);
/*     */     }
/*     */   }
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
/*     */   protected final ByteBuf newDirectBuffer(ByteBuf buf) {
/* 434 */     int readableBytes = buf.readableBytes();
/* 435 */     if (readableBytes == 0) {
/* 436 */       ReferenceCountUtil.safeRelease(buf);
/* 437 */       return Unpooled.EMPTY_BUFFER;
/*     */     } 
/*     */     
/* 440 */     ByteBufAllocator alloc = alloc();
/* 441 */     if (alloc.isDirectBufferPooled()) {
/* 442 */       ByteBuf byteBuf = alloc.directBuffer(readableBytes);
/* 443 */       byteBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
/* 444 */       ReferenceCountUtil.safeRelease(buf);
/* 445 */       return byteBuf;
/*     */     } 
/*     */     
/* 448 */     ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
/* 449 */     if (directBuf != null) {
/* 450 */       directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
/* 451 */       ReferenceCountUtil.safeRelease(buf);
/* 452 */       return directBuf;
/*     */     } 
/*     */ 
/*     */     
/* 456 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf) {
/* 466 */     int readableBytes = buf.readableBytes();
/* 467 */     if (readableBytes == 0) {
/* 468 */       ReferenceCountUtil.safeRelease(holder);
/* 469 */       return Unpooled.EMPTY_BUFFER;
/*     */     } 
/*     */     
/* 472 */     ByteBufAllocator alloc = alloc();
/* 473 */     if (alloc.isDirectBufferPooled()) {
/* 474 */       ByteBuf byteBuf = alloc.directBuffer(readableBytes);
/* 475 */       byteBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
/* 476 */       ReferenceCountUtil.safeRelease(holder);
/* 477 */       return byteBuf;
/*     */     } 
/*     */     
/* 480 */     ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
/* 481 */     if (directBuf != null) {
/* 482 */       directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
/* 483 */       ReferenceCountUtil.safeRelease(holder);
/* 484 */       return directBuf;
/*     */     } 
/*     */ 
/*     */     
/* 488 */     if (holder != buf) {
/*     */       
/* 490 */       buf.retain();
/* 491 */       ReferenceCountUtil.safeRelease(holder);
/*     */     } 
/*     */     
/* 494 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 499 */     ChannelPromise promise = this.connectPromise;
/* 500 */     if (promise != null) {
/*     */       
/* 502 */       promise.tryFailure(new ClosedChannelException());
/* 503 */       this.connectPromise = null;
/*     */     } 
/*     */     
/* 506 */     ScheduledFuture<?> future = this.connectTimeoutFuture;
/* 507 */     if (future != null) {
/* 508 */       future.cancel(false);
/* 509 */       this.connectTimeoutFuture = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract boolean doConnect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2) throws Exception;
/*     */   
/*     */   protected abstract void doFinishConnect() throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\AbstractNioChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */