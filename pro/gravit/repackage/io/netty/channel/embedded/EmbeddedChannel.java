/*     */ package pro.gravit.repackage.io.netty.channel.embedded;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelId;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInitializer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.RecyclableArrayList;
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
/*     */ public class EmbeddedChannel
/*     */   extends AbstractChannel
/*     */ {
/*  52 */   private static final SocketAddress LOCAL_ADDRESS = new EmbeddedSocketAddress();
/*  53 */   private static final SocketAddress REMOTE_ADDRESS = new EmbeddedSocketAddress();
/*     */   
/*  55 */   private static final ChannelHandler[] EMPTY_HANDLERS = new ChannelHandler[0];
/*  56 */   private enum State { OPEN, ACTIVE, CLOSED; }
/*     */   
/*  58 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
/*     */   
/*  60 */   private static final ChannelMetadata METADATA_NO_DISCONNECT = new ChannelMetadata(false);
/*  61 */   private static final ChannelMetadata METADATA_DISCONNECT = new ChannelMetadata(true);
/*     */   
/*  63 */   private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
/*  64 */   private final ChannelFutureListener recordExceptionListener = new ChannelFutureListener()
/*     */     {
/*     */       public void operationComplete(ChannelFuture future) throws Exception {
/*  67 */         EmbeddedChannel.this.recordException(future);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private final ChannelMetadata metadata;
/*     */   
/*     */   private final ChannelConfig config;
/*     */   
/*     */   private Queue<Object> inboundMessages;
/*     */   
/*     */   private Queue<Object> outboundMessages;
/*     */   private Throwable lastException;
/*     */   private State state;
/*     */   
/*     */   public EmbeddedChannel() {
/*  83 */     this(EMPTY_HANDLERS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel(ChannelId channelId) {
/*  92 */     this(channelId, EMPTY_HANDLERS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel(ChannelHandler... handlers) {
/* 101 */     this(EmbeddedChannelId.INSTANCE, handlers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel(boolean hasDisconnect, ChannelHandler... handlers) {
/* 112 */     this(EmbeddedChannelId.INSTANCE, hasDisconnect, handlers);
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
/*     */   public EmbeddedChannel(boolean register, boolean hasDisconnect, ChannelHandler... handlers) {
/* 125 */     this(EmbeddedChannelId.INSTANCE, register, hasDisconnect, handlers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel(ChannelId channelId, ChannelHandler... handlers) {
/* 136 */     this(channelId, false, handlers);
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
/*     */   public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelHandler... handlers) {
/* 149 */     this(channelId, true, hasDisconnect, handlers);
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
/*     */   public EmbeddedChannel(ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler... handlers) {
/* 165 */     this((Channel)null, channelId, register, hasDisconnect, handlers);
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
/*     */   public EmbeddedChannel(Channel parent, ChannelId channelId, boolean register, boolean hasDisconnect, ChannelHandler... handlers) {
/* 182 */     super(parent, channelId);
/* 183 */     this.metadata = metadata(hasDisconnect);
/* 184 */     this.config = (ChannelConfig)new DefaultChannelConfig((Channel)this);
/* 185 */     setup(register, handlers);
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
/*     */   public EmbeddedChannel(ChannelId channelId, boolean hasDisconnect, ChannelConfig config, ChannelHandler... handlers) {
/* 200 */     super(null, channelId);
/* 201 */     this.metadata = metadata(hasDisconnect);
/* 202 */     this.config = (ChannelConfig)ObjectUtil.checkNotNull(config, "config");
/* 203 */     setup(true, handlers);
/*     */   }
/*     */   
/*     */   private static ChannelMetadata metadata(boolean hasDisconnect) {
/* 207 */     return hasDisconnect ? METADATA_DISCONNECT : METADATA_NO_DISCONNECT;
/*     */   }
/*     */   
/*     */   private void setup(boolean register, ChannelHandler... handlers) {
/* 211 */     ObjectUtil.checkNotNull(handlers, "handlers");
/* 212 */     ChannelPipeline p = pipeline();
/* 213 */     p.addLast(new ChannelHandler[] { (ChannelHandler)new ChannelInitializer<Channel>()
/*     */           {
/*     */             protected void initChannel(Channel ch) throws Exception {
/* 216 */               ChannelPipeline pipeline = ch.pipeline();
/* 217 */               for (ChannelHandler h : handlers) {
/* 218 */                 if (h == null) {
/*     */                   break;
/*     */                 }
/* 221 */                 pipeline.addLast(new ChannelHandler[] { h });
/*     */               } 
/*     */             }
/*     */           } });
/* 225 */     if (register) {
/* 226 */       ChannelFuture future = this.loop.register((Channel)this);
/* 227 */       assert future.isDone();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void register() throws Exception {
/* 235 */     ChannelFuture future = this.loop.register((Channel)this);
/* 236 */     assert future.isDone();
/* 237 */     Throwable cause = future.cause();
/* 238 */     if (cause != null) {
/* 239 */       PlatformDependent.throwException(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected final DefaultChannelPipeline newChannelPipeline() {
/* 245 */     return new EmbeddedChannelPipeline(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 250 */     return this.metadata;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig config() {
/* 255 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 260 */     return (this.state != State.CLOSED);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 265 */     return (this.state == State.ACTIVE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Queue<Object> inboundMessages() {
/* 272 */     if (this.inboundMessages == null) {
/* 273 */       this.inboundMessages = new ArrayDeque();
/*     */     }
/* 275 */     return this.inboundMessages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Queue<Object> lastInboundBuffer() {
/* 283 */     return inboundMessages();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Queue<Object> outboundMessages() {
/* 290 */     if (this.outboundMessages == null) {
/* 291 */       this.outboundMessages = new ArrayDeque();
/*     */     }
/* 293 */     return this.outboundMessages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Queue<Object> lastOutboundBuffer() {
/* 301 */     return outboundMessages();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T readInbound() {
/* 309 */     T message = (T)poll(this.inboundMessages);
/* 310 */     if (message != null) {
/* 311 */       ReferenceCountUtil.touch(message, "Caller of readInbound() will handle the message from this point");
/*     */     }
/* 313 */     return message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T readOutbound() {
/* 321 */     T message = (T)poll(this.outboundMessages);
/* 322 */     if (message != null) {
/* 323 */       ReferenceCountUtil.touch(message, "Caller of readOutbound() will handle the message from this point.");
/*     */     }
/* 325 */     return message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean writeInbound(Object... msgs) {
/* 336 */     ensureOpen();
/* 337 */     if (msgs.length == 0) {
/* 338 */       return isNotEmpty(this.inboundMessages);
/*     */     }
/*     */     
/* 341 */     ChannelPipeline p = pipeline();
/* 342 */     for (Object m : msgs) {
/* 343 */       p.fireChannelRead(m);
/*     */     }
/*     */     
/* 346 */     flushInbound(false, voidPromise());
/* 347 */     return isNotEmpty(this.inboundMessages);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture writeOneInbound(Object msg) {
/* 357 */     return writeOneInbound(msg, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture writeOneInbound(Object msg, ChannelPromise promise) {
/* 367 */     if (checkOpen(true)) {
/* 368 */       pipeline().fireChannelRead(msg);
/*     */     }
/* 370 */     return checkException(promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel flushInbound() {
/* 379 */     flushInbound(true, voidPromise());
/* 380 */     return this;
/*     */   }
/*     */   
/*     */   private ChannelFuture flushInbound(boolean recordException, ChannelPromise promise) {
/* 384 */     if (checkOpen(recordException)) {
/* 385 */       pipeline().fireChannelReadComplete();
/* 386 */       runPendingTasks();
/*     */     } 
/*     */     
/* 389 */     return checkException(promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean writeOutbound(Object... msgs) {
/* 399 */     ensureOpen();
/* 400 */     if (msgs.length == 0) {
/* 401 */       return isNotEmpty(this.outboundMessages);
/*     */     }
/*     */     
/* 404 */     RecyclableArrayList futures = RecyclableArrayList.newInstance(msgs.length);
/*     */     try {
/* 406 */       for (Object m : msgs) {
/* 407 */         if (m == null) {
/*     */           break;
/*     */         }
/* 410 */         futures.add(write(m));
/*     */       } 
/*     */       
/* 413 */       flushOutbound0();
/*     */       
/* 415 */       int size = futures.size();
/* 416 */       for (int i = 0; i < size; i++) {
/* 417 */         ChannelFuture future = (ChannelFuture)futures.get(i);
/* 418 */         if (future.isDone()) {
/* 419 */           recordException(future);
/*     */         } else {
/*     */           
/* 422 */           future.addListener((GenericFutureListener)this.recordExceptionListener);
/*     */         } 
/*     */       } 
/*     */       
/* 426 */       checkException();
/* 427 */       return isNotEmpty(this.outboundMessages);
/*     */     } finally {
/* 429 */       futures.recycle();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture writeOneOutbound(Object msg) {
/* 440 */     return writeOneOutbound(msg, newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture writeOneOutbound(Object msg, ChannelPromise promise) {
/* 450 */     if (checkOpen(true)) {
/* 451 */       return write(msg, promise);
/*     */     }
/* 453 */     return checkException(promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmbeddedChannel flushOutbound() {
/* 462 */     if (checkOpen(true)) {
/* 463 */       flushOutbound0();
/*     */     }
/* 465 */     checkException(voidPromise());
/* 466 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void flushOutbound0() {
/* 472 */     runPendingTasks();
/*     */     
/* 474 */     flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean finish() {
/* 483 */     return finish(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean finishAndReleaseAll() {
/* 493 */     return finish(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean finish(boolean releaseAll) {
/* 503 */     close();
/*     */     try {
/* 505 */       checkException();
/* 506 */       return (isNotEmpty(this.inboundMessages) || isNotEmpty(this.outboundMessages));
/*     */     } finally {
/* 508 */       if (releaseAll) {
/* 509 */         releaseAll(this.inboundMessages);
/* 510 */         releaseAll(this.outboundMessages);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean releaseInbound() {
/* 520 */     return releaseAll(this.inboundMessages);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean releaseOutbound() {
/* 528 */     return releaseAll(this.outboundMessages);
/*     */   }
/*     */   
/*     */   private static boolean releaseAll(Queue<Object> queue) {
/* 532 */     if (isNotEmpty(queue)) {
/*     */       while (true) {
/* 534 */         Object msg = queue.poll();
/* 535 */         if (msg == null) {
/*     */           break;
/*     */         }
/* 538 */         ReferenceCountUtil.release(msg);
/*     */       } 
/* 540 */       return true;
/*     */     } 
/* 542 */     return false;
/*     */   }
/*     */   
/*     */   private void finishPendingTasks(boolean cancel) {
/* 546 */     runPendingTasks();
/* 547 */     if (cancel)
/*     */     {
/* 549 */       this.loop.cancelScheduledTasks();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final ChannelFuture close() {
/* 555 */     return close(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public final ChannelFuture disconnect() {
/* 560 */     return disconnect(newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ChannelFuture close(ChannelPromise promise) {
/* 567 */     runPendingTasks();
/* 568 */     ChannelFuture future = super.close(promise);
/*     */ 
/*     */     
/* 571 */     finishPendingTasks(true);
/* 572 */     return future;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ChannelFuture disconnect(ChannelPromise promise) {
/* 577 */     ChannelFuture future = super.disconnect(promise);
/* 578 */     finishPendingTasks(!this.metadata.hasDisconnect());
/* 579 */     return future;
/*     */   }
/*     */   
/*     */   private static boolean isNotEmpty(Queue<Object> queue) {
/* 583 */     return (queue != null && !queue.isEmpty());
/*     */   }
/*     */   
/*     */   private static Object poll(Queue<Object> queue) {
/* 587 */     return (queue != null) ? queue.poll() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void runPendingTasks() {
/*     */     try {
/* 596 */       this.loop.runTasks();
/* 597 */     } catch (Exception e) {
/* 598 */       recordException(e);
/*     */     } 
/*     */     
/*     */     try {
/* 602 */       this.loop.runScheduledTasks();
/* 603 */     } catch (Exception e) {
/* 604 */       recordException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long runScheduledPendingTasks() {
/*     */     try {
/* 615 */       return this.loop.runScheduledTasks();
/* 616 */     } catch (Exception e) {
/* 617 */       recordException(e);
/* 618 */       return this.loop.nextScheduledTask();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void recordException(ChannelFuture future) {
/* 623 */     if (!future.isSuccess()) {
/* 624 */       recordException(future.cause());
/*     */     }
/*     */   }
/*     */   
/*     */   private void recordException(Throwable cause) {
/* 629 */     if (this.lastException == null) {
/* 630 */       this.lastException = cause;
/*     */     } else {
/* 632 */       logger.warn("More than one exception was raised. Will report only the first one and log others.", cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelFuture checkException(ChannelPromise promise) {
/* 642 */     Throwable t = this.lastException;
/* 643 */     if (t != null) {
/* 644 */       this.lastException = null;
/*     */       
/* 646 */       if (promise.isVoid()) {
/* 647 */         PlatformDependent.throwException(t);
/*     */       }
/*     */       
/* 650 */       return (ChannelFuture)promise.setFailure(t);
/*     */     } 
/*     */     
/* 653 */     return (ChannelFuture)promise.setSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkException() {
/* 660 */     checkException(voidPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkOpen(boolean recordException) {
/* 668 */     if (!isOpen()) {
/* 669 */       if (recordException) {
/* 670 */         recordException(new ClosedChannelException());
/*     */       }
/* 672 */       return false;
/*     */     } 
/*     */     
/* 675 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void ensureOpen() {
/* 682 */     if (!checkOpen(true)) {
/* 683 */       checkException();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCompatible(EventLoop loop) {
/* 689 */     return loop instanceof EmbeddedEventLoop;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 694 */     return isActive() ? LOCAL_ADDRESS : null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 699 */     return isActive() ? REMOTE_ADDRESS : null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doRegister() throws Exception {
/* 704 */     this.state = State.ACTIVE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 714 */     if (!this.metadata.hasDisconnect()) {
/* 715 */       doClose();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 721 */     this.state = State.CLOSED;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractChannel.AbstractUnsafe newUnsafe() {
/* 731 */     return new EmbeddedUnsafe();
/*     */   }
/*     */ 
/*     */   
/*     */   public Channel.Unsafe unsafe() {
/* 736 */     return ((EmbeddedUnsafe)super.unsafe()).wrapped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/*     */     while (true) {
/* 742 */       Object msg = in.current();
/* 743 */       if (msg == null) {
/*     */         break;
/*     */       }
/*     */       
/* 747 */       ReferenceCountUtil.retain(msg);
/* 748 */       handleOutboundMessage(msg);
/* 749 */       in.remove();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleOutboundMessage(Object msg) {
/* 759 */     outboundMessages().add(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleInboundMessage(Object msg) {
/* 766 */     inboundMessages().add(msg);
/*     */   }
/*     */   private final class EmbeddedUnsafe extends AbstractChannel.AbstractUnsafe { private EmbeddedUnsafe() {
/* 769 */       super(EmbeddedChannel.this);
/*     */ 
/*     */ 
/*     */       
/* 773 */       this.wrapped = new Channel.Unsafe()
/*     */         {
/*     */           public RecvByteBufAllocator.Handle recvBufAllocHandle() {
/* 776 */             return EmbeddedChannel.EmbeddedUnsafe.this.recvBufAllocHandle();
/*     */           }
/*     */ 
/*     */           
/*     */           public SocketAddress localAddress() {
/* 781 */             return EmbeddedChannel.EmbeddedUnsafe.this.localAddress();
/*     */           }
/*     */ 
/*     */           
/*     */           public SocketAddress remoteAddress() {
/* 786 */             return EmbeddedChannel.EmbeddedUnsafe.this.remoteAddress();
/*     */           }
/*     */ 
/*     */           
/*     */           public void register(EventLoop eventLoop, ChannelPromise promise) {
/* 791 */             EmbeddedChannel.EmbeddedUnsafe.this.register(eventLoop, promise);
/* 792 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void bind(SocketAddress localAddress, ChannelPromise promise) {
/* 797 */             EmbeddedChannel.EmbeddedUnsafe.this.bind(localAddress, promise);
/* 798 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 803 */             EmbeddedChannel.EmbeddedUnsafe.this.connect(remoteAddress, localAddress, promise);
/* 804 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void disconnect(ChannelPromise promise) {
/* 809 */             EmbeddedChannel.EmbeddedUnsafe.this.disconnect(promise);
/* 810 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void close(ChannelPromise promise) {
/* 815 */             EmbeddedChannel.EmbeddedUnsafe.this.close(promise);
/* 816 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void closeForcibly() {
/* 821 */             EmbeddedChannel.EmbeddedUnsafe.this.closeForcibly();
/* 822 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void deregister(ChannelPromise promise) {
/* 827 */             EmbeddedChannel.EmbeddedUnsafe.this.deregister(promise);
/* 828 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void beginRead() {
/* 833 */             EmbeddedChannel.EmbeddedUnsafe.this.beginRead();
/* 834 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void write(Object msg, ChannelPromise promise) {
/* 839 */             EmbeddedChannel.EmbeddedUnsafe.this.write(msg, promise);
/* 840 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public void flush() {
/* 845 */             EmbeddedChannel.EmbeddedUnsafe.this.flush();
/* 846 */             EmbeddedChannel.this.runPendingTasks();
/*     */           }
/*     */ 
/*     */           
/*     */           public ChannelPromise voidPromise() {
/* 851 */             return EmbeddedChannel.EmbeddedUnsafe.this.voidPromise();
/*     */           }
/*     */ 
/*     */           
/*     */           public ChannelOutboundBuffer outboundBuffer() {
/* 856 */             return EmbeddedChannel.EmbeddedUnsafe.this.outboundBuffer();
/*     */           }
/*     */         };
/*     */     }
/*     */     final Channel.Unsafe wrapped;
/*     */     public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 862 */       safeSetSuccess(promise);
/*     */     } }
/*     */ 
/*     */   
/*     */   private final class EmbeddedChannelPipeline extends DefaultChannelPipeline {
/*     */     EmbeddedChannelPipeline(EmbeddedChannel channel) {
/* 868 */       super((Channel)channel);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void onUnhandledInboundException(Throwable cause) {
/* 873 */       EmbeddedChannel.this.recordException(cause);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
/* 878 */       EmbeddedChannel.this.handleInboundMessage(msg);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\embedded\EmbeddedChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */