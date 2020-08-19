/*      */ package pro.gravit.repackage.io.netty.channel;
/*      */ 
/*      */ import java.net.SocketAddress;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.util.Attribute;
/*      */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakHint;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.AbstractEventExecutor;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PromiseNotificationUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ThrowableUtil;
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
/*      */ abstract class AbstractChannelHandlerContext
/*      */   implements ChannelHandlerContext, ResourceLeakHint
/*      */ {
/*   61 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
/*      */   
/*      */   volatile AbstractChannelHandlerContext next;
/*      */   
/*      */   volatile AbstractChannelHandlerContext prev;
/*   66 */   private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannelHandlerContext.class, "handlerState");
/*      */ 
/*      */   
/*      */   private static final int ADD_PENDING = 1;
/*      */ 
/*      */   
/*      */   private static final int ADD_COMPLETE = 2;
/*      */ 
/*      */   
/*      */   private static final int REMOVE_COMPLETE = 3;
/*      */ 
/*      */   
/*      */   private static final int INIT = 0;
/*      */ 
/*      */   
/*      */   private final DefaultChannelPipeline pipeline;
/*      */ 
/*      */   
/*      */   private final String name;
/*      */ 
/*      */   
/*      */   private final boolean ordered;
/*      */ 
/*      */   
/*      */   private final int executionMask;
/*      */ 
/*      */   
/*      */   final EventExecutor executor;
/*      */ 
/*      */   
/*      */   private ChannelFuture succeededFuture;
/*      */   
/*      */   private Tasks invokeTasks;
/*      */   
/*  100 */   private volatile int handlerState = 0;
/*      */ 
/*      */   
/*      */   AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, Class<? extends ChannelHandler> handlerClass) {
/*  104 */     this.name = (String)ObjectUtil.checkNotNull(name, "name");
/*  105 */     this.pipeline = pipeline;
/*  106 */     this.executor = executor;
/*  107 */     this.executionMask = ChannelHandlerMask.mask(handlerClass);
/*      */     
/*  109 */     this.ordered = (executor == null || executor instanceof pro.gravit.repackage.io.netty.util.concurrent.OrderedEventExecutor);
/*      */   }
/*      */ 
/*      */   
/*      */   public Channel channel() {
/*  114 */     return this.pipeline.channel();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelPipeline pipeline() {
/*  119 */     return this.pipeline;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBufAllocator alloc() {
/*  124 */     return channel().config().getAllocator();
/*      */   }
/*      */ 
/*      */   
/*      */   public EventExecutor executor() {
/*  129 */     if (this.executor == null) {
/*  130 */       return (EventExecutor)channel().eventLoop();
/*      */     }
/*  132 */     return this.executor;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String name() {
/*  138 */     return this.name;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelRegistered() {
/*  143 */     invokeChannelRegistered(findContextInbound(2));
/*  144 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
/*  148 */     EventExecutor executor = next.executor();
/*  149 */     if (executor.inEventLoop()) {
/*  150 */       next.invokeChannelRegistered();
/*      */     } else {
/*  152 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  155 */               next.invokeChannelRegistered();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelRegistered() {
/*  162 */     if (invokeHandler()) {
/*      */       try {
/*  164 */         ((ChannelInboundHandler)handler()).channelRegistered(this);
/*  165 */       } catch (Throwable t) {
/*  166 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  169 */       fireChannelRegistered();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelUnregistered() {
/*  175 */     invokeChannelUnregistered(findContextInbound(4));
/*  176 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelUnregistered(final AbstractChannelHandlerContext next) {
/*  180 */     EventExecutor executor = next.executor();
/*  181 */     if (executor.inEventLoop()) {
/*  182 */       next.invokeChannelUnregistered();
/*      */     } else {
/*  184 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  187 */               next.invokeChannelUnregistered();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelUnregistered() {
/*  194 */     if (invokeHandler()) {
/*      */       try {
/*  196 */         ((ChannelInboundHandler)handler()).channelUnregistered(this);
/*  197 */       } catch (Throwable t) {
/*  198 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  201 */       fireChannelUnregistered();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelActive() {
/*  207 */     invokeChannelActive(findContextInbound(8));
/*  208 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelActive(final AbstractChannelHandlerContext next) {
/*  212 */     EventExecutor executor = next.executor();
/*  213 */     if (executor.inEventLoop()) {
/*  214 */       next.invokeChannelActive();
/*      */     } else {
/*  216 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  219 */               next.invokeChannelActive();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelActive() {
/*  226 */     if (invokeHandler()) {
/*      */       try {
/*  228 */         ((ChannelInboundHandler)handler()).channelActive(this);
/*  229 */       } catch (Throwable t) {
/*  230 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  233 */       fireChannelActive();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelInactive() {
/*  239 */     invokeChannelInactive(findContextInbound(16));
/*  240 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelInactive(final AbstractChannelHandlerContext next) {
/*  244 */     EventExecutor executor = next.executor();
/*  245 */     if (executor.inEventLoop()) {
/*  246 */       next.invokeChannelInactive();
/*      */     } else {
/*  248 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  251 */               next.invokeChannelInactive();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelInactive() {
/*  258 */     if (invokeHandler()) {
/*      */       try {
/*  260 */         ((ChannelInboundHandler)handler()).channelInactive(this);
/*  261 */       } catch (Throwable t) {
/*  262 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  265 */       fireChannelInactive();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
/*  271 */     invokeExceptionCaught(findContextInbound(1), cause);
/*  272 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeExceptionCaught(final AbstractChannelHandlerContext next, final Throwable cause) {
/*  276 */     ObjectUtil.checkNotNull(cause, "cause");
/*  277 */     EventExecutor executor = next.executor();
/*  278 */     if (executor.inEventLoop()) {
/*  279 */       next.invokeExceptionCaught(cause);
/*      */     } else {
/*      */       try {
/*  282 */         executor.execute(new Runnable()
/*      */             {
/*      */               public void run() {
/*  285 */                 next.invokeExceptionCaught(cause);
/*      */               }
/*      */             });
/*  288 */       } catch (Throwable t) {
/*  289 */         if (logger.isWarnEnabled()) {
/*  290 */           logger.warn("Failed to submit an exceptionCaught() event.", t);
/*  291 */           logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeExceptionCaught(Throwable cause) {
/*  298 */     if (invokeHandler()) {
/*      */       try {
/*  300 */         handler().exceptionCaught(this, cause);
/*  301 */       } catch (Throwable error) {
/*  302 */         if (logger.isDebugEnabled()) {
/*  303 */           logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", 
/*      */ 
/*      */ 
/*      */               
/*  307 */               ThrowableUtil.stackTraceToString(error), cause);
/*  308 */         } else if (logger.isWarnEnabled()) {
/*  309 */           logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", error, cause);
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  316 */       fireExceptionCaught(cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireUserEventTriggered(Object event) {
/*  322 */     invokeUserEventTriggered(findContextInbound(128), event);
/*  323 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeUserEventTriggered(final AbstractChannelHandlerContext next, final Object event) {
/*  327 */     ObjectUtil.checkNotNull(event, "event");
/*  328 */     EventExecutor executor = next.executor();
/*  329 */     if (executor.inEventLoop()) {
/*  330 */       next.invokeUserEventTriggered(event);
/*      */     } else {
/*  332 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  335 */               next.invokeUserEventTriggered(event);
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeUserEventTriggered(Object event) {
/*  342 */     if (invokeHandler()) {
/*      */       try {
/*  344 */         ((ChannelInboundHandler)handler()).userEventTriggered(this, event);
/*  345 */       } catch (Throwable t) {
/*  346 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  349 */       fireUserEventTriggered(event);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelRead(Object msg) {
/*  355 */     invokeChannelRead(findContextInbound(32), msg);
/*  356 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
/*  360 */     final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
/*  361 */     EventExecutor executor = next.executor();
/*  362 */     if (executor.inEventLoop()) {
/*  363 */       next.invokeChannelRead(m);
/*      */     } else {
/*  365 */       executor.execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  368 */               next.invokeChannelRead(m);
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelRead(Object msg) {
/*  375 */     if (invokeHandler()) {
/*      */       try {
/*  377 */         ((ChannelInboundHandler)handler()).channelRead(this, msg);
/*  378 */       } catch (Throwable t) {
/*  379 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  382 */       fireChannelRead(msg);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelReadComplete() {
/*  388 */     invokeChannelReadComplete(findContextInbound(64));
/*  389 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelReadComplete(AbstractChannelHandlerContext next) {
/*  393 */     EventExecutor executor = next.executor();
/*  394 */     if (executor.inEventLoop()) {
/*  395 */       next.invokeChannelReadComplete();
/*      */     } else {
/*  397 */       Tasks tasks = next.invokeTasks;
/*  398 */       if (tasks == null) {
/*  399 */         next.invokeTasks = tasks = new Tasks(next);
/*      */       }
/*  401 */       executor.execute(tasks.invokeChannelReadCompleteTask);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelReadComplete() {
/*  406 */     if (invokeHandler()) {
/*      */       try {
/*  408 */         ((ChannelInboundHandler)handler()).channelReadComplete(this);
/*  409 */       } catch (Throwable t) {
/*  410 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  413 */       fireChannelReadComplete();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext fireChannelWritabilityChanged() {
/*  419 */     invokeChannelWritabilityChanged(findContextInbound(256));
/*  420 */     return this;
/*      */   }
/*      */   
/*      */   static void invokeChannelWritabilityChanged(AbstractChannelHandlerContext next) {
/*  424 */     EventExecutor executor = next.executor();
/*  425 */     if (executor.inEventLoop()) {
/*  426 */       next.invokeChannelWritabilityChanged();
/*      */     } else {
/*  428 */       Tasks tasks = next.invokeTasks;
/*  429 */       if (tasks == null) {
/*  430 */         next.invokeTasks = tasks = new Tasks(next);
/*      */       }
/*  432 */       executor.execute(tasks.invokeChannelWritableStateChangedTask);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeChannelWritabilityChanged() {
/*  437 */     if (invokeHandler()) {
/*      */       try {
/*  439 */         ((ChannelInboundHandler)handler()).channelWritabilityChanged(this);
/*  440 */       } catch (Throwable t) {
/*  441 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  444 */       fireChannelWritabilityChanged();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture bind(SocketAddress localAddress) {
/*  450 */     return bind(localAddress, newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress) {
/*  455 */     return connect(remoteAddress, newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
/*  460 */     return connect(remoteAddress, localAddress, newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture disconnect() {
/*  465 */     return disconnect(newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture close() {
/*  470 */     return close(newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture deregister() {
/*  475 */     return deregister(newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
/*  480 */     ObjectUtil.checkNotNull(localAddress, "localAddress");
/*  481 */     if (isNotValidPromise(promise, false))
/*      */     {
/*  483 */       return promise;
/*      */     }
/*      */     
/*  486 */     final AbstractChannelHandlerContext next = findContextOutbound(512);
/*  487 */     EventExecutor executor = next.executor();
/*  488 */     if (executor.inEventLoop()) {
/*  489 */       next.invokeBind(localAddress, promise);
/*      */     } else {
/*  491 */       safeExecute(executor, new Runnable()
/*      */           {
/*      */             public void run() {
/*  494 */               next.invokeBind(localAddress, promise);
/*      */             }
/*      */           }promise, null, false);
/*      */     } 
/*  498 */     return promise;
/*      */   }
/*      */   
/*      */   private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
/*  502 */     if (invokeHandler()) {
/*      */       try {
/*  504 */         ((ChannelOutboundHandler)handler()).bind(this, localAddress, promise);
/*  505 */       } catch (Throwable t) {
/*  506 */         notifyOutboundHandlerException(t, promise);
/*      */       } 
/*      */     } else {
/*  509 */       bind(localAddress, promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
/*  515 */     return connect(remoteAddress, null, promise);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
/*  521 */     ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
/*      */     
/*  523 */     if (isNotValidPromise(promise, false))
/*      */     {
/*  525 */       return promise;
/*      */     }
/*      */     
/*  528 */     final AbstractChannelHandlerContext next = findContextOutbound(1024);
/*  529 */     EventExecutor executor = next.executor();
/*  530 */     if (executor.inEventLoop()) {
/*  531 */       next.invokeConnect(remoteAddress, localAddress, promise);
/*      */     } else {
/*  533 */       safeExecute(executor, new Runnable()
/*      */           {
/*      */             public void run() {
/*  536 */               next.invokeConnect(remoteAddress, localAddress, promise);
/*      */             }
/*      */           }promise, null, false);
/*      */     } 
/*  540 */     return promise;
/*      */   }
/*      */   
/*      */   private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/*  544 */     if (invokeHandler()) {
/*      */       try {
/*  546 */         ((ChannelOutboundHandler)handler()).connect(this, remoteAddress, localAddress, promise);
/*  547 */       } catch (Throwable t) {
/*  548 */         notifyOutboundHandlerException(t, promise);
/*      */       } 
/*      */     } else {
/*  551 */       connect(remoteAddress, localAddress, promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture disconnect(final ChannelPromise promise) {
/*  557 */     if (!channel().metadata().hasDisconnect())
/*      */     {
/*      */       
/*  560 */       return close(promise);
/*      */     }
/*  562 */     if (isNotValidPromise(promise, false))
/*      */     {
/*  564 */       return promise;
/*      */     }
/*      */     
/*  567 */     final AbstractChannelHandlerContext next = findContextOutbound(2048);
/*  568 */     EventExecutor executor = next.executor();
/*  569 */     if (executor.inEventLoop()) {
/*  570 */       next.invokeDisconnect(promise);
/*      */     } else {
/*  572 */       safeExecute(executor, new Runnable()
/*      */           {
/*      */             public void run() {
/*  575 */               next.invokeDisconnect(promise);
/*      */             }
/*      */           },  promise, null, false);
/*      */     } 
/*  579 */     return promise;
/*      */   }
/*      */   
/*      */   private void invokeDisconnect(ChannelPromise promise) {
/*  583 */     if (invokeHandler()) {
/*      */       try {
/*  585 */         ((ChannelOutboundHandler)handler()).disconnect(this, promise);
/*  586 */       } catch (Throwable t) {
/*  587 */         notifyOutboundHandlerException(t, promise);
/*      */       } 
/*      */     } else {
/*  590 */       disconnect(promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture close(final ChannelPromise promise) {
/*  596 */     if (isNotValidPromise(promise, false))
/*      */     {
/*  598 */       return promise;
/*      */     }
/*      */     
/*  601 */     final AbstractChannelHandlerContext next = findContextOutbound(4096);
/*  602 */     EventExecutor executor = next.executor();
/*  603 */     if (executor.inEventLoop()) {
/*  604 */       next.invokeClose(promise);
/*      */     } else {
/*  606 */       safeExecute(executor, new Runnable()
/*      */           {
/*      */             public void run() {
/*  609 */               next.invokeClose(promise);
/*      */             }
/*      */           },  promise, null, false);
/*      */     } 
/*      */     
/*  614 */     return promise;
/*      */   }
/*      */   
/*      */   private void invokeClose(ChannelPromise promise) {
/*  618 */     if (invokeHandler()) {
/*      */       try {
/*  620 */         ((ChannelOutboundHandler)handler()).close(this, promise);
/*  621 */       } catch (Throwable t) {
/*  622 */         notifyOutboundHandlerException(t, promise);
/*      */       } 
/*      */     } else {
/*  625 */       close(promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture deregister(final ChannelPromise promise) {
/*  631 */     if (isNotValidPromise(promise, false))
/*      */     {
/*  633 */       return promise;
/*      */     }
/*      */     
/*  636 */     final AbstractChannelHandlerContext next = findContextOutbound(8192);
/*  637 */     EventExecutor executor = next.executor();
/*  638 */     if (executor.inEventLoop()) {
/*  639 */       next.invokeDeregister(promise);
/*      */     } else {
/*  641 */       safeExecute(executor, new Runnable()
/*      */           {
/*      */             public void run() {
/*  644 */               next.invokeDeregister(promise);
/*      */             }
/*      */           },  promise, null, false);
/*      */     } 
/*      */     
/*  649 */     return promise;
/*      */   }
/*      */   
/*      */   private void invokeDeregister(ChannelPromise promise) {
/*  653 */     if (invokeHandler()) {
/*      */       try {
/*  655 */         ((ChannelOutboundHandler)handler()).deregister(this, promise);
/*  656 */       } catch (Throwable t) {
/*  657 */         notifyOutboundHandlerException(t, promise);
/*      */       } 
/*      */     } else {
/*  660 */       deregister(promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext read() {
/*  666 */     AbstractChannelHandlerContext next = findContextOutbound(16384);
/*  667 */     EventExecutor executor = next.executor();
/*  668 */     if (executor.inEventLoop()) {
/*  669 */       next.invokeRead();
/*      */     } else {
/*  671 */       Tasks tasks = next.invokeTasks;
/*  672 */       if (tasks == null) {
/*  673 */         next.invokeTasks = tasks = new Tasks(next);
/*      */       }
/*  675 */       executor.execute(tasks.invokeReadTask);
/*      */     } 
/*      */     
/*  678 */     return this;
/*      */   }
/*      */   
/*      */   private void invokeRead() {
/*  682 */     if (invokeHandler()) {
/*      */       try {
/*  684 */         ((ChannelOutboundHandler)handler()).read(this);
/*  685 */       } catch (Throwable t) {
/*  686 */         notifyHandlerException(t);
/*      */       } 
/*      */     } else {
/*  689 */       read();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture write(Object msg) {
/*  695 */     return write(msg, newPromise());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture write(Object msg, ChannelPromise promise) {
/*  700 */     write(msg, false, promise);
/*      */     
/*  702 */     return promise;
/*      */   }
/*      */   
/*      */   void invokeWrite(Object msg, ChannelPromise promise) {
/*  706 */     if (invokeHandler()) {
/*  707 */       invokeWrite0(msg, promise);
/*      */     } else {
/*  709 */       write(msg, promise);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeWrite0(Object msg, ChannelPromise promise) {
/*      */     try {
/*  715 */       ((ChannelOutboundHandler)handler()).write(this, msg, promise);
/*  716 */     } catch (Throwable t) {
/*  717 */       notifyOutboundHandlerException(t, promise);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelHandlerContext flush() {
/*  723 */     AbstractChannelHandlerContext next = findContextOutbound(65536);
/*  724 */     EventExecutor executor = next.executor();
/*  725 */     if (executor.inEventLoop()) {
/*  726 */       next.invokeFlush();
/*      */     } else {
/*  728 */       Tasks tasks = next.invokeTasks;
/*  729 */       if (tasks == null) {
/*  730 */         next.invokeTasks = tasks = new Tasks(next);
/*      */       }
/*  732 */       safeExecute(executor, tasks.invokeFlushTask, channel().voidPromise(), null, false);
/*      */     } 
/*      */     
/*  735 */     return this;
/*      */   }
/*      */   
/*      */   private void invokeFlush() {
/*  739 */     if (invokeHandler()) {
/*  740 */       invokeFlush0();
/*      */     } else {
/*  742 */       flush();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void invokeFlush0() {
/*      */     try {
/*  748 */       ((ChannelOutboundHandler)handler()).flush(this);
/*  749 */     } catch (Throwable t) {
/*  750 */       notifyHandlerException(t);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
/*  756 */     write(msg, true, promise);
/*  757 */     return promise;
/*      */   }
/*      */   
/*      */   void invokeWriteAndFlush(Object msg, ChannelPromise promise) {
/*  761 */     if (invokeHandler()) {
/*  762 */       invokeWrite0(msg, promise);
/*  763 */       invokeFlush0();
/*      */     } else {
/*  765 */       writeAndFlush(msg, promise);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void write(Object msg, boolean flush, ChannelPromise promise) {
/*  770 */     ObjectUtil.checkNotNull(msg, "msg");
/*      */     try {
/*  772 */       if (isNotValidPromise(promise, true)) {
/*  773 */         ReferenceCountUtil.release(msg);
/*      */         
/*      */         return;
/*      */       } 
/*  777 */     } catch (RuntimeException e) {
/*  778 */       ReferenceCountUtil.release(msg);
/*  779 */       throw e;
/*      */     } 
/*      */     
/*  782 */     AbstractChannelHandlerContext next = findContextOutbound(flush ? 98304 : 32768);
/*      */     
/*  784 */     Object m = this.pipeline.touch(msg, next);
/*  785 */     EventExecutor executor = next.executor();
/*  786 */     if (executor.inEventLoop()) {
/*  787 */       if (flush) {
/*  788 */         next.invokeWriteAndFlush(m, promise);
/*      */       } else {
/*  790 */         next.invokeWrite(m, promise);
/*      */       } 
/*      */     } else {
/*  793 */       WriteTask task = WriteTask.newInstance(next, m, promise, flush);
/*  794 */       if (!safeExecute(executor, task, promise, m, !flush))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  799 */         task.cancel();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture writeAndFlush(Object msg) {
/*  806 */     return writeAndFlush(msg, newPromise());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
/*  812 */     PromiseNotificationUtil.tryFailure(promise, cause, (promise instanceof VoidChannelPromise) ? null : logger);
/*      */   }
/*      */   
/*      */   private void notifyHandlerException(Throwable cause) {
/*  816 */     if (inExceptionCaught(cause)) {
/*  817 */       if (logger.isWarnEnabled()) {
/*  818 */         logger.warn("An exception was thrown by a user handler while handling an exceptionCaught event", cause);
/*      */       }
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  825 */     invokeExceptionCaught(cause);
/*      */   }
/*      */   
/*      */   private static boolean inExceptionCaught(Throwable cause) {
/*      */     do {
/*  830 */       StackTraceElement[] trace = cause.getStackTrace();
/*  831 */       if (trace != null) {
/*  832 */         for (StackTraceElement t : trace) {
/*  833 */           if (t == null) {
/*      */             break;
/*      */           }
/*  836 */           if ("exceptionCaught".equals(t.getMethodName())) {
/*  837 */             return true;
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/*  842 */       cause = cause.getCause();
/*  843 */     } while (cause != null);
/*      */     
/*  845 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelPromise newPromise() {
/*  850 */     return new DefaultChannelPromise(channel(), executor());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelProgressivePromise newProgressivePromise() {
/*  855 */     return new DefaultChannelProgressivePromise(channel(), executor());
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture newSucceededFuture() {
/*  860 */     ChannelFuture succeededFuture = this.succeededFuture;
/*  861 */     if (succeededFuture == null) {
/*  862 */       this.succeededFuture = succeededFuture = new SucceededChannelFuture(channel(), executor());
/*      */     }
/*  864 */     return succeededFuture;
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelFuture newFailedFuture(Throwable cause) {
/*  869 */     return new FailedChannelFuture(channel(), executor(), cause);
/*      */   }
/*      */   
/*      */   private boolean isNotValidPromise(ChannelPromise promise, boolean allowVoidPromise) {
/*  873 */     ObjectUtil.checkNotNull(promise, "promise");
/*      */     
/*  875 */     if (promise.isDone()) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  880 */       if (promise.isCancelled()) {
/*  881 */         return true;
/*      */       }
/*  883 */       throw new IllegalArgumentException("promise already done: " + promise);
/*      */     } 
/*      */     
/*  886 */     if (promise.channel() != channel()) {
/*  887 */       throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", new Object[] { promise
/*  888 */               .channel(), channel() }));
/*      */     }
/*      */     
/*  891 */     if (promise.getClass() == DefaultChannelPromise.class) {
/*  892 */       return false;
/*      */     }
/*      */     
/*  895 */     if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
/*  896 */       throw new IllegalArgumentException(
/*  897 */           StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
/*      */     }
/*      */     
/*  900 */     if (promise instanceof AbstractChannel.CloseFuture) {
/*  901 */       throw new IllegalArgumentException(
/*  902 */           StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
/*      */     }
/*  904 */     return false;
/*      */   }
/*      */   
/*      */   private AbstractChannelHandlerContext findContextInbound(int mask) {
/*  908 */     AbstractChannelHandlerContext ctx = this;
/*      */     while (true) {
/*  910 */       ctx = ctx.next;
/*  911 */       if ((ctx.executionMask & mask) != 0)
/*  912 */         return ctx; 
/*      */     } 
/*      */   }
/*      */   private AbstractChannelHandlerContext findContextOutbound(int mask) {
/*  916 */     AbstractChannelHandlerContext ctx = this;
/*      */     while (true) {
/*  918 */       ctx = ctx.prev;
/*  919 */       if ((ctx.executionMask & mask) != 0)
/*  920 */         return ctx; 
/*      */     } 
/*      */   }
/*      */   
/*      */   public ChannelPromise voidPromise() {
/*  925 */     return channel().voidPromise();
/*      */   }
/*      */   
/*      */   final void setRemoved() {
/*  929 */     this.handlerState = 3;
/*      */   }
/*      */   
/*      */   final boolean setAddComplete() {
/*      */     while (true) {
/*  934 */       int oldState = this.handlerState;
/*  935 */       if (oldState == 3) {
/*  936 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  941 */       if (HANDLER_STATE_UPDATER.compareAndSet(this, oldState, 2)) {
/*  942 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   final void setAddPending() {
/*  948 */     boolean updated = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);
/*  949 */     assert updated;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   final void callHandlerAdded() throws Exception {
/*  955 */     if (setAddComplete()) {
/*  956 */       handler().handlerAdded(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   final void callHandlerRemoved() throws Exception {
/*      */     try {
/*  963 */       if (this.handlerState == 2) {
/*  964 */         handler().handlerRemoved(this);
/*      */       }
/*      */     } finally {
/*      */       
/*  968 */       setRemoved();
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
/*      */   private boolean invokeHandler() {
/*  982 */     int handlerState = this.handlerState;
/*  983 */     return (handlerState == 2 || (!this.ordered && handlerState == 1));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRemoved() {
/*  988 */     return (this.handlerState == 3);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> Attribute<T> attr(AttributeKey<T> key) {
/*  993 */     return channel().attr(key);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> boolean hasAttr(AttributeKey<T> key) {
/*  998 */     return channel().hasAttr(key);
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg, boolean lazy) {
/*      */     try {
/* 1004 */       if (lazy && executor instanceof AbstractEventExecutor) {
/* 1005 */         ((AbstractEventExecutor)executor).lazyExecute(runnable);
/*      */       } else {
/* 1007 */         executor.execute(runnable);
/*      */       } 
/* 1009 */       return true;
/* 1010 */     } catch (Throwable cause) {
/*      */       try {
/* 1012 */         promise.setFailure(cause);
/*      */       } finally {
/* 1014 */         if (msg != null) {
/* 1015 */           ReferenceCountUtil.release(msg);
/*      */         }
/*      */       } 
/* 1018 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String toHintString() {
/* 1024 */     return '\'' + this.name + "' will handle the message from this point.";
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1029 */     return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + channel() + ')';
/*      */   }
/*      */   
/*      */   static final class WriteTask implements Runnable {
/* 1033 */     private static final ObjectPool<WriteTask> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<WriteTask>()
/*      */         {
/*      */           public AbstractChannelHandlerContext.WriteTask newObject(ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> handle) {
/* 1036 */             return new AbstractChannelHandlerContext.WriteTask(handle);
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*      */     static WriteTask newInstance(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
/* 1042 */       WriteTask task = (WriteTask)RECYCLER.get();
/* 1043 */       init(task, ctx, msg, promise, flush);
/* 1044 */       return task;
/*      */     }
/*      */ 
/*      */     
/* 1048 */     private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.transport.estimateSizeOnSubmit", true);
/*      */ 
/*      */ 
/*      */     
/* 1052 */     private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.transport.writeTaskSizeOverhead", 32);
/*      */     
/*      */     private final ObjectPool.Handle<WriteTask> handle;
/*      */     
/*      */     private AbstractChannelHandlerContext ctx;
/*      */     private Object msg;
/*      */     private ChannelPromise promise;
/*      */     private int size;
/*      */     
/*      */     private WriteTask(ObjectPool.Handle<? extends WriteTask> handle) {
/* 1062 */       this.handle = (ObjectPool.Handle)handle;
/*      */     }
/*      */ 
/*      */     
/*      */     protected static void init(WriteTask task, AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
/* 1067 */       task.ctx = ctx;
/* 1068 */       task.msg = msg;
/* 1069 */       task.promise = promise;
/*      */       
/* 1071 */       if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
/* 1072 */         task.size = ctx.pipeline.estimatorHandle().size(msg) + WRITE_TASK_OVERHEAD;
/* 1073 */         ctx.pipeline.incrementPendingOutboundBytes(task.size);
/*      */       } else {
/* 1075 */         task.size = 0;
/*      */       } 
/* 1077 */       if (flush) {
/* 1078 */         task.size |= Integer.MIN_VALUE;
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/*      */       try {
/* 1085 */         decrementPendingOutboundBytes();
/* 1086 */         if (this.size >= 0) {
/* 1087 */           this.ctx.invokeWrite(this.msg, this.promise);
/*      */         } else {
/* 1089 */           this.ctx.invokeWriteAndFlush(this.msg, this.promise);
/*      */         } 
/*      */       } finally {
/* 1092 */         recycle();
/*      */       } 
/*      */     }
/*      */     
/*      */     void cancel() {
/*      */       try {
/* 1098 */         decrementPendingOutboundBytes();
/*      */       } finally {
/* 1100 */         recycle();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void decrementPendingOutboundBytes() {
/* 1105 */       if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
/* 1106 */         this.ctx.pipeline.decrementPendingOutboundBytes((this.size & Integer.MAX_VALUE));
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void recycle() {
/* 1112 */       this.ctx = null;
/* 1113 */       this.msg = null;
/* 1114 */       this.promise = null;
/* 1115 */       this.handle.recycle(this);
/*      */     } }
/*      */   
/*      */   private static final class Tasks {
/*      */     private final AbstractChannelHandlerContext next;
/*      */     
/* 1121 */     private final Runnable invokeChannelReadCompleteTask = new Runnable()
/*      */       {
/*      */         public void run() {
/* 1124 */           AbstractChannelHandlerContext.Tasks.this.next.invokeChannelReadComplete();
/*      */         }
/*      */       };
/* 1127 */     private final Runnable invokeReadTask = new Runnable()
/*      */       {
/*      */         public void run() {
/* 1130 */           AbstractChannelHandlerContext.Tasks.this.next.invokeRead();
/*      */         }
/*      */       };
/* 1133 */     private final Runnable invokeChannelWritableStateChangedTask = new Runnable()
/*      */       {
/*      */         public void run() {
/* 1136 */           AbstractChannelHandlerContext.Tasks.this.next.invokeChannelWritabilityChanged();
/*      */         }
/*      */       };
/* 1139 */     private final Runnable invokeFlushTask = new Runnable()
/*      */       {
/*      */         public void run() {
/* 1142 */           AbstractChannelHandlerContext.Tasks.this.next.invokeFlush();
/*      */         }
/*      */       };
/*      */     
/*      */     Tasks(AbstractChannelHandlerContext next) {
/* 1147 */       this.next = next;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AbstractChannelHandlerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */