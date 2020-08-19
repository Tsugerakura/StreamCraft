/*      */ package pro.gravit.repackage.io.netty.channel;
/*      */ 
/*      */ import java.net.SocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutorGroup;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*      */ public class DefaultChannelPipeline
/*      */   implements ChannelPipeline
/*      */ {
/*   48 */   static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
/*      */   
/*   50 */   private static final String HEAD_NAME = generateName0(HeadContext.class);
/*   51 */   private static final String TAIL_NAME = generateName0(TailContext.class);
/*      */   
/*   53 */   private static final FastThreadLocal<Map<Class<?>, String>> nameCaches = new FastThreadLocal<Map<Class<?>, String>>()
/*      */     {
/*      */       protected Map<Class<?>, String> initialValue()
/*      */       {
/*   57 */         return new WeakHashMap<Class<?>, String>();
/*      */       }
/*      */     };
/*      */ 
/*      */   
/*   62 */   private static final AtomicReferenceFieldUpdater<DefaultChannelPipeline, MessageSizeEstimator.Handle> ESTIMATOR = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelPipeline.class, MessageSizeEstimator.Handle.class, "estimatorHandle");
/*      */   
/*      */   final AbstractChannelHandlerContext head;
/*      */   
/*      */   final AbstractChannelHandlerContext tail;
/*      */   private final Channel channel;
/*      */   private final ChannelFuture succeededFuture;
/*      */   private final VoidChannelPromise voidPromise;
/*   70 */   private final boolean touch = ResourceLeakDetector.isEnabled();
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<EventExecutorGroup, EventExecutor> childExecutors;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile MessageSizeEstimator.Handle estimatorHandle;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean firstRegistration = true;
/*      */ 
/*      */   
/*      */   private PendingHandlerCallback pendingHandlerCallbackHead;
/*      */ 
/*      */   
/*      */   private boolean registered;
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultChannelPipeline(Channel channel) {
/*   93 */     this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
/*   94 */     this.succeededFuture = new SucceededChannelFuture(channel, null);
/*   95 */     this.voidPromise = new VoidChannelPromise(channel, true);
/*      */     
/*   97 */     this.tail = new TailContext(this);
/*   98 */     this.head = new HeadContext(this);
/*      */     
/*  100 */     this.head.next = this.tail;
/*  101 */     this.tail.prev = this.head;
/*      */   }
/*      */   
/*      */   final MessageSizeEstimator.Handle estimatorHandle() {
/*  105 */     MessageSizeEstimator.Handle handle = this.estimatorHandle;
/*  106 */     if (handle == null) {
/*  107 */       handle = this.channel.config().getMessageSizeEstimator().newHandle();
/*  108 */       if (!ESTIMATOR.compareAndSet(this, null, handle)) {
/*  109 */         handle = this.estimatorHandle;
/*      */       }
/*      */     } 
/*  112 */     return handle;
/*      */   }
/*      */   
/*      */   final Object touch(Object msg, AbstractChannelHandlerContext next) {
/*  116 */     return this.touch ? ReferenceCountUtil.touch(msg, next) : msg;
/*      */   }
/*      */   
/*      */   private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
/*  120 */     return new DefaultChannelHandlerContext(this, childExecutor(group), name, handler);
/*      */   }
/*      */   
/*      */   private EventExecutor childExecutor(EventExecutorGroup group) {
/*  124 */     if (group == null) {
/*  125 */       return null;
/*      */     }
/*  127 */     Boolean pinEventExecutor = this.channel.config().<Boolean>getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
/*  128 */     if (pinEventExecutor != null && !pinEventExecutor.booleanValue()) {
/*  129 */       return group.next();
/*      */     }
/*  131 */     Map<EventExecutorGroup, EventExecutor> childExecutors = this.childExecutors;
/*  132 */     if (childExecutors == null)
/*      */     {
/*  134 */       childExecutors = this.childExecutors = new IdentityHashMap<EventExecutorGroup, EventExecutor>(4);
/*      */     }
/*      */ 
/*      */     
/*  138 */     EventExecutor childExecutor = childExecutors.get(group);
/*  139 */     if (childExecutor == null) {
/*  140 */       childExecutor = group.next();
/*  141 */       childExecutors.put(group, childExecutor);
/*      */     } 
/*  143 */     return childExecutor;
/*      */   }
/*      */   
/*      */   public final Channel channel() {
/*  147 */     return this.channel;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addFirst(String name, ChannelHandler handler) {
/*  152 */     return addFirst(null, name, handler);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
/*      */     AbstractChannelHandlerContext newCtx;
/*  158 */     synchronized (this) {
/*  159 */       checkMultiplicity(handler);
/*  160 */       name = filterName(name, handler);
/*      */       
/*  162 */       newCtx = newContext(group, name, handler);
/*      */       
/*  164 */       addFirst0(newCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  169 */       if (!this.registered) {
/*  170 */         newCtx.setAddPending();
/*  171 */         callHandlerCallbackLater(newCtx, true);
/*  172 */         return this;
/*      */       } 
/*      */       
/*  175 */       EventExecutor executor = newCtx.executor();
/*  176 */       if (!executor.inEventLoop()) {
/*  177 */         callHandlerAddedInEventLoop(newCtx, executor);
/*  178 */         return this;
/*      */       } 
/*      */     } 
/*  181 */     callHandlerAdded0(newCtx);
/*  182 */     return this;
/*      */   }
/*      */   
/*      */   private void addFirst0(AbstractChannelHandlerContext newCtx) {
/*  186 */     AbstractChannelHandlerContext nextCtx = this.head.next;
/*  187 */     newCtx.prev = this.head;
/*  188 */     newCtx.next = nextCtx;
/*  189 */     this.head.next = newCtx;
/*  190 */     nextCtx.prev = newCtx;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addLast(String name, ChannelHandler handler) {
/*  195 */     return addLast(null, name, handler);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
/*      */     AbstractChannelHandlerContext newCtx;
/*  201 */     synchronized (this) {
/*  202 */       checkMultiplicity(handler);
/*      */       
/*  204 */       newCtx = newContext(group, filterName(name, handler), handler);
/*      */       
/*  206 */       addLast0(newCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  211 */       if (!this.registered) {
/*  212 */         newCtx.setAddPending();
/*  213 */         callHandlerCallbackLater(newCtx, true);
/*  214 */         return this;
/*      */       } 
/*      */       
/*  217 */       EventExecutor executor = newCtx.executor();
/*  218 */       if (!executor.inEventLoop()) {
/*  219 */         callHandlerAddedInEventLoop(newCtx, executor);
/*  220 */         return this;
/*      */       } 
/*      */     } 
/*  223 */     callHandlerAdded0(newCtx);
/*  224 */     return this;
/*      */   }
/*      */   
/*      */   private void addLast0(AbstractChannelHandlerContext newCtx) {
/*  228 */     AbstractChannelHandlerContext prev = this.tail.prev;
/*  229 */     newCtx.prev = prev;
/*  230 */     newCtx.next = this.tail;
/*  231 */     prev.next = newCtx;
/*  232 */     this.tail.prev = newCtx;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
/*  237 */     return addBefore(null, baseName, name, handler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
/*      */     AbstractChannelHandlerContext newCtx;
/*  245 */     synchronized (this) {
/*  246 */       checkMultiplicity(handler);
/*  247 */       name = filterName(name, handler);
/*  248 */       AbstractChannelHandlerContext ctx = getContextOrDie(baseName);
/*      */       
/*  250 */       newCtx = newContext(group, name, handler);
/*      */       
/*  252 */       addBefore0(ctx, newCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  257 */       if (!this.registered) {
/*  258 */         newCtx.setAddPending();
/*  259 */         callHandlerCallbackLater(newCtx, true);
/*  260 */         return this;
/*      */       } 
/*      */       
/*  263 */       EventExecutor executor = newCtx.executor();
/*  264 */       if (!executor.inEventLoop()) {
/*  265 */         callHandlerAddedInEventLoop(newCtx, executor);
/*  266 */         return this;
/*      */       } 
/*      */     } 
/*  269 */     callHandlerAdded0(newCtx);
/*  270 */     return this;
/*      */   }
/*      */   
/*      */   private static void addBefore0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
/*  274 */     newCtx.prev = ctx.prev;
/*  275 */     newCtx.next = ctx;
/*  276 */     ctx.prev.next = newCtx;
/*  277 */     ctx.prev = newCtx;
/*      */   }
/*      */   
/*      */   private String filterName(String name, ChannelHandler handler) {
/*  281 */     if (name == null) {
/*  282 */       return generateName(handler);
/*      */     }
/*  284 */     checkDuplicateName(name);
/*  285 */     return name;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
/*  290 */     return addAfter(null, baseName, name, handler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
/*      */     AbstractChannelHandlerContext newCtx;
/*  299 */     synchronized (this) {
/*  300 */       checkMultiplicity(handler);
/*  301 */       name = filterName(name, handler);
/*  302 */       AbstractChannelHandlerContext ctx = getContextOrDie(baseName);
/*      */       
/*  304 */       newCtx = newContext(group, name, handler);
/*      */       
/*  306 */       addAfter0(ctx, newCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  311 */       if (!this.registered) {
/*  312 */         newCtx.setAddPending();
/*  313 */         callHandlerCallbackLater(newCtx, true);
/*  314 */         return this;
/*      */       } 
/*  316 */       EventExecutor executor = newCtx.executor();
/*  317 */       if (!executor.inEventLoop()) {
/*  318 */         callHandlerAddedInEventLoop(newCtx, executor);
/*  319 */         return this;
/*      */       } 
/*      */     } 
/*  322 */     callHandlerAdded0(newCtx);
/*  323 */     return this;
/*      */   }
/*      */   
/*      */   private static void addAfter0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
/*  327 */     newCtx.prev = ctx;
/*  328 */     newCtx.next = ctx.next;
/*  329 */     ctx.next.prev = newCtx;
/*  330 */     ctx.next = newCtx;
/*      */   }
/*      */   
/*      */   public final ChannelPipeline addFirst(ChannelHandler handler) {
/*  334 */     return addFirst((String)null, handler);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addFirst(ChannelHandler... handlers) {
/*  339 */     return addFirst((EventExecutorGroup)null, handlers);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler... handlers) {
/*  344 */     ObjectUtil.checkNotNull(handlers, "handlers");
/*  345 */     if (handlers.length == 0 || handlers[0] == null) {
/*  346 */       return this;
/*      */     }
/*      */     
/*      */     int size;
/*  350 */     for (size = 1; size < handlers.length && 
/*  351 */       handlers[size] != null; size++);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  356 */     for (int i = size - 1; i >= 0; i--) {
/*  357 */       ChannelHandler h = handlers[i];
/*  358 */       addFirst(executor, null, h);
/*      */     } 
/*      */     
/*  361 */     return this;
/*      */   }
/*      */   
/*      */   public final ChannelPipeline addLast(ChannelHandler handler) {
/*  365 */     return addLast((String)null, handler);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addLast(ChannelHandler... handlers) {
/*  370 */     return addLast((EventExecutorGroup)null, handlers);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler... handlers) {
/*  375 */     ObjectUtil.checkNotNull(handlers, "handlers");
/*      */     
/*  377 */     for (ChannelHandler h : handlers) {
/*  378 */       if (h == null) {
/*      */         break;
/*      */       }
/*  381 */       addLast(executor, null, h);
/*      */     } 
/*      */     
/*  384 */     return this;
/*      */   }
/*      */   
/*      */   private String generateName(ChannelHandler handler) {
/*  388 */     Map<Class<?>, String> cache = (Map<Class<?>, String>)nameCaches.get();
/*  389 */     Class<?> handlerType = handler.getClass();
/*  390 */     String name = cache.get(handlerType);
/*  391 */     if (name == null) {
/*  392 */       name = generateName0(handlerType);
/*  393 */       cache.put(handlerType, name);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  398 */     if (context0(name) != null) {
/*  399 */       String baseName = name.substring(0, name.length() - 1);
/*  400 */       for (int i = 1;; i++) {
/*  401 */         String newName = baseName + i;
/*  402 */         if (context0(newName) == null) {
/*  403 */           name = newName;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*  408 */     return name;
/*      */   }
/*      */   
/*      */   private static String generateName0(Class<?> handlerType) {
/*  412 */     return StringUtil.simpleClassName(handlerType) + "#0";
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline remove(ChannelHandler handler) {
/*  417 */     remove(getContextOrDie(handler));
/*  418 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler remove(String name) {
/*  423 */     return remove(getContextOrDie(name)).handler();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T extends ChannelHandler> T remove(Class<T> handlerType) {
/*  429 */     return (T)remove(getContextOrDie(handlerType)).handler();
/*      */   }
/*      */   
/*      */   public final <T extends ChannelHandler> T removeIfExists(String name) {
/*  433 */     return removeIfExists(context(name));
/*      */   }
/*      */   
/*      */   public final <T extends ChannelHandler> T removeIfExists(Class<T> handlerType) {
/*  437 */     return removeIfExists(context(handlerType));
/*      */   }
/*      */   
/*      */   public final <T extends ChannelHandler> T removeIfExists(ChannelHandler handler) {
/*  441 */     return removeIfExists(context(handler));
/*      */   }
/*      */ 
/*      */   
/*      */   private <T extends ChannelHandler> T removeIfExists(ChannelHandlerContext ctx) {
/*  446 */     if (ctx == null) {
/*  447 */       return null;
/*      */     }
/*  449 */     return (T)remove((AbstractChannelHandlerContext)ctx).handler();
/*      */   }
/*      */   
/*      */   private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext ctx) {
/*  453 */     assert ctx != this.head && ctx != this.tail;
/*      */     
/*  455 */     synchronized (this) {
/*  456 */       atomicRemoveFromHandlerList(ctx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  461 */       if (!this.registered) {
/*  462 */         callHandlerCallbackLater(ctx, false);
/*  463 */         return ctx;
/*      */       } 
/*      */       
/*  466 */       EventExecutor executor = ctx.executor();
/*  467 */       if (!executor.inEventLoop()) {
/*  468 */         executor.execute(new Runnable()
/*      */             {
/*      */               public void run() {
/*  471 */                 DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
/*      */               }
/*      */             });
/*  474 */         return ctx;
/*      */       } 
/*      */     } 
/*  477 */     callHandlerRemoved0(ctx);
/*  478 */     return ctx;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void atomicRemoveFromHandlerList(AbstractChannelHandlerContext ctx) {
/*  485 */     AbstractChannelHandlerContext prev = ctx.prev;
/*  486 */     AbstractChannelHandlerContext next = ctx.next;
/*  487 */     prev.next = next;
/*  488 */     next.prev = prev;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler removeFirst() {
/*  493 */     if (this.head.next == this.tail) {
/*  494 */       throw new NoSuchElementException();
/*      */     }
/*  496 */     return remove(this.head.next).handler();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler removeLast() {
/*  501 */     if (this.head.next == this.tail) {
/*  502 */       throw new NoSuchElementException();
/*      */     }
/*  504 */     return remove(this.tail.prev).handler();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
/*  509 */     replace(getContextOrDie(oldHandler), newName, newHandler);
/*  510 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
/*  515 */     return replace(getContextOrDie(oldName), newName, newHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
/*  522 */     return (T)replace(getContextOrDie(oldHandlerType), newName, newHandler);
/*      */   }
/*      */   
/*      */   private ChannelHandler replace(final AbstractChannelHandlerContext ctx, String newName, ChannelHandler newHandler) {
/*      */     final AbstractChannelHandlerContext newCtx;
/*  527 */     assert ctx != this.head && ctx != this.tail;
/*      */ 
/*      */     
/*  530 */     synchronized (this) {
/*  531 */       checkMultiplicity(newHandler);
/*  532 */       if (newName == null) {
/*  533 */         newName = generateName(newHandler);
/*      */       } else {
/*  535 */         boolean sameName = ctx.name().equals(newName);
/*  536 */         if (!sameName) {
/*  537 */           checkDuplicateName(newName);
/*      */         }
/*      */       } 
/*      */       
/*  541 */       newCtx = newContext((EventExecutorGroup)ctx.executor, newName, newHandler);
/*      */       
/*  543 */       replace0(ctx, newCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  549 */       if (!this.registered) {
/*  550 */         callHandlerCallbackLater(newCtx, true);
/*  551 */         callHandlerCallbackLater(ctx, false);
/*  552 */         return ctx.handler();
/*      */       } 
/*  554 */       EventExecutor executor = ctx.executor();
/*  555 */       if (!executor.inEventLoop()) {
/*  556 */         executor.execute(new Runnable()
/*      */             {
/*      */ 
/*      */               
/*      */               public void run()
/*      */               {
/*  562 */                 DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
/*  563 */                 DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
/*      */               }
/*      */             });
/*  566 */         return ctx.handler();
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  572 */     callHandlerAdded0(newCtx);
/*  573 */     callHandlerRemoved0(ctx);
/*  574 */     return ctx.handler();
/*      */   }
/*      */   
/*      */   private static void replace0(AbstractChannelHandlerContext oldCtx, AbstractChannelHandlerContext newCtx) {
/*  578 */     AbstractChannelHandlerContext prev = oldCtx.prev;
/*  579 */     AbstractChannelHandlerContext next = oldCtx.next;
/*  580 */     newCtx.prev = prev;
/*  581 */     newCtx.next = next;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  587 */     prev.next = newCtx;
/*  588 */     next.prev = newCtx;
/*      */ 
/*      */     
/*  591 */     oldCtx.prev = newCtx;
/*  592 */     oldCtx.next = newCtx;
/*      */   }
/*      */   
/*      */   private static void checkMultiplicity(ChannelHandler handler) {
/*  596 */     if (handler instanceof ChannelHandlerAdapter) {
/*  597 */       ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
/*  598 */       if (!h.isSharable() && h.added) {
/*  599 */         throw new ChannelPipelineException(h
/*  600 */             .getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
/*      */       }
/*      */       
/*  603 */       h.added = true;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void callHandlerAdded0(AbstractChannelHandlerContext ctx) {
/*      */     try {
/*  609 */       ctx.callHandlerAdded();
/*  610 */     } catch (Throwable t) {
/*  611 */       boolean removed = false;
/*      */       try {
/*  613 */         atomicRemoveFromHandlerList(ctx);
/*  614 */         ctx.callHandlerRemoved();
/*  615 */         removed = true;
/*  616 */       } catch (Throwable t2) {
/*  617 */         if (logger.isWarnEnabled()) {
/*  618 */           logger.warn("Failed to remove a handler: " + ctx.name(), t2);
/*      */         }
/*      */       } 
/*      */       
/*  622 */       if (removed) {
/*  623 */         fireExceptionCaught(new ChannelPipelineException(ctx
/*  624 */               .handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", t));
/*      */       } else {
/*      */         
/*  627 */         fireExceptionCaught(new ChannelPipelineException(ctx
/*  628 */               .handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", t));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
/*      */     try {
/*  637 */       ctx.callHandlerRemoved();
/*  638 */     } catch (Throwable t) {
/*  639 */       fireExceptionCaught(new ChannelPipelineException(ctx
/*  640 */             .handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", t));
/*      */     } 
/*      */   }
/*      */   
/*      */   final void invokeHandlerAddedIfNeeded() {
/*  645 */     assert this.channel.eventLoop().inEventLoop();
/*  646 */     if (this.firstRegistration) {
/*  647 */       this.firstRegistration = false;
/*      */ 
/*      */       
/*  650 */       callHandlerAddedForAllHandlers();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler first() {
/*  656 */     ChannelHandlerContext first = firstContext();
/*  657 */     if (first == null) {
/*  658 */       return null;
/*      */     }
/*  660 */     return first.handler();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandlerContext firstContext() {
/*  665 */     AbstractChannelHandlerContext first = this.head.next;
/*  666 */     if (first == this.tail) {
/*  667 */       return null;
/*      */     }
/*  669 */     return this.head.next;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler last() {
/*  674 */     AbstractChannelHandlerContext last = this.tail.prev;
/*  675 */     if (last == this.head) {
/*  676 */       return null;
/*      */     }
/*  678 */     return last.handler();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandlerContext lastContext() {
/*  683 */     AbstractChannelHandlerContext last = this.tail.prev;
/*  684 */     if (last == this.head) {
/*  685 */       return null;
/*      */     }
/*  687 */     return last;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandler get(String name) {
/*  692 */     ChannelHandlerContext ctx = context(name);
/*  693 */     if (ctx == null) {
/*  694 */       return null;
/*      */     }
/*  696 */     return ctx.handler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T extends ChannelHandler> T get(Class<T> handlerType) {
/*  703 */     ChannelHandlerContext ctx = context(handlerType);
/*  704 */     if (ctx == null) {
/*  705 */       return null;
/*      */     }
/*  707 */     return (T)ctx.handler();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final ChannelHandlerContext context(String name) {
/*  713 */     return context0((String)ObjectUtil.checkNotNull(name, "name"));
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandlerContext context(ChannelHandler handler) {
/*  718 */     ObjectUtil.checkNotNull(handler, "handler");
/*      */     
/*  720 */     AbstractChannelHandlerContext ctx = this.head.next;
/*      */     
/*      */     while (true) {
/*  723 */       if (ctx == null) {
/*  724 */         return null;
/*      */       }
/*      */       
/*  727 */       if (ctx.handler() == handler) {
/*  728 */         return ctx;
/*      */       }
/*      */       
/*  731 */       ctx = ctx.next;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
/*  737 */     ObjectUtil.checkNotNull(handlerType, "handlerType");
/*      */     
/*  739 */     AbstractChannelHandlerContext ctx = this.head.next;
/*      */     while (true) {
/*  741 */       if (ctx == null) {
/*  742 */         return null;
/*      */       }
/*  744 */       if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
/*  745 */         return ctx;
/*      */       }
/*  747 */       ctx = ctx.next;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final List<String> names() {
/*  753 */     List<String> list = new ArrayList<String>();
/*  754 */     AbstractChannelHandlerContext ctx = this.head.next;
/*      */     while (true) {
/*  756 */       if (ctx == null) {
/*  757 */         return list;
/*      */       }
/*  759 */       list.add(ctx.name());
/*  760 */       ctx = ctx.next;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final Map<String, ChannelHandler> toMap() {
/*  766 */     Map<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
/*  767 */     AbstractChannelHandlerContext ctx = this.head.next;
/*      */     while (true) {
/*  769 */       if (ctx == this.tail) {
/*  770 */         return map;
/*      */       }
/*  772 */       map.put(ctx.name(), ctx.handler());
/*  773 */       ctx = ctx.next;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final Iterator<Map.Entry<String, ChannelHandler>> iterator() {
/*  779 */     return toMap().entrySet().iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String toString() {
/*  789 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append('{');
/*  790 */     AbstractChannelHandlerContext ctx = this.head.next;
/*      */     
/*  792 */     while (ctx != this.tail) {
/*      */ 
/*      */ 
/*      */       
/*  796 */       buf.append('(')
/*  797 */         .append(ctx.name())
/*  798 */         .append(" = ")
/*  799 */         .append(ctx.handler().getClass().getName())
/*  800 */         .append(')');
/*      */       
/*  802 */       ctx = ctx.next;
/*  803 */       if (ctx == this.tail) {
/*      */         break;
/*      */       }
/*      */       
/*  807 */       buf.append(", ");
/*      */     } 
/*  809 */     buf.append('}');
/*  810 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelRegistered() {
/*  815 */     AbstractChannelHandlerContext.invokeChannelRegistered(this.head);
/*  816 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelUnregistered() {
/*  821 */     AbstractChannelHandlerContext.invokeChannelUnregistered(this.head);
/*  822 */     return this;
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
/*      */   private synchronized void destroy() {
/*  836 */     destroyUp(this.head.next, false);
/*      */   }
/*      */   
/*      */   private void destroyUp(AbstractChannelHandlerContext ctx, boolean inEventLoop) {
/*  840 */     Thread currentThread = Thread.currentThread();
/*  841 */     AbstractChannelHandlerContext tail = this.tail;
/*      */     while (true) {
/*  843 */       if (ctx == tail) {
/*  844 */         destroyDown(currentThread, tail.prev, inEventLoop);
/*      */         
/*      */         break;
/*      */       } 
/*  848 */       EventExecutor executor = ctx.executor();
/*  849 */       if (!inEventLoop && !executor.inEventLoop(currentThread)) {
/*  850 */         final AbstractChannelHandlerContext finalCtx = ctx;
/*  851 */         executor.execute(new Runnable()
/*      */             {
/*      */               public void run() {
/*  854 */                 DefaultChannelPipeline.this.destroyUp(finalCtx, true);
/*      */               }
/*      */             });
/*      */         
/*      */         break;
/*      */       } 
/*  860 */       ctx = ctx.next;
/*  861 */       inEventLoop = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void destroyDown(Thread currentThread, AbstractChannelHandlerContext ctx, boolean inEventLoop) {
/*  867 */     AbstractChannelHandlerContext head = this.head;
/*      */     
/*  869 */     while (ctx != head) {
/*      */ 
/*      */ 
/*      */       
/*  873 */       EventExecutor executor = ctx.executor();
/*  874 */       if (inEventLoop || executor.inEventLoop(currentThread)) {
/*  875 */         atomicRemoveFromHandlerList(ctx);
/*  876 */         callHandlerRemoved0(ctx);
/*      */       } else {
/*  878 */         final AbstractChannelHandlerContext finalCtx = ctx;
/*  879 */         executor.execute(new Runnable()
/*      */             {
/*      */               public void run() {
/*  882 */                 DefaultChannelPipeline.this.destroyDown(Thread.currentThread(), finalCtx, true);
/*      */               }
/*      */             });
/*      */         
/*      */         break;
/*      */       } 
/*  888 */       ctx = ctx.prev;
/*  889 */       inEventLoop = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelActive() {
/*  895 */     AbstractChannelHandlerContext.invokeChannelActive(this.head);
/*  896 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelInactive() {
/*  901 */     AbstractChannelHandlerContext.invokeChannelInactive(this.head);
/*  902 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireExceptionCaught(Throwable cause) {
/*  907 */     AbstractChannelHandlerContext.invokeExceptionCaught(this.head, cause);
/*  908 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireUserEventTriggered(Object event) {
/*  913 */     AbstractChannelHandlerContext.invokeUserEventTriggered(this.head, event);
/*  914 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelRead(Object msg) {
/*  919 */     AbstractChannelHandlerContext.invokeChannelRead(this.head, msg);
/*  920 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelReadComplete() {
/*  925 */     AbstractChannelHandlerContext.invokeChannelReadComplete(this.head);
/*  926 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline fireChannelWritabilityChanged() {
/*  931 */     AbstractChannelHandlerContext.invokeChannelWritabilityChanged(this.head);
/*  932 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture bind(SocketAddress localAddress) {
/*  937 */     return this.tail.bind(localAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture connect(SocketAddress remoteAddress) {
/*  942 */     return this.tail.connect(remoteAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
/*  947 */     return this.tail.connect(remoteAddress, localAddress);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture disconnect() {
/*  952 */     return this.tail.disconnect();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture close() {
/*  957 */     return this.tail.close();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture deregister() {
/*  962 */     return this.tail.deregister();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline flush() {
/*  967 */     this.tail.flush();
/*  968 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
/*  973 */     return this.tail.bind(localAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
/*  978 */     return this.tail.connect(remoteAddress, promise);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/*  984 */     return this.tail.connect(remoteAddress, localAddress, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture disconnect(ChannelPromise promise) {
/*  989 */     return this.tail.disconnect(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture close(ChannelPromise promise) {
/*  994 */     return this.tail.close(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture deregister(ChannelPromise promise) {
/*  999 */     return this.tail.deregister(promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPipeline read() {
/* 1004 */     this.tail.read();
/* 1005 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture write(Object msg) {
/* 1010 */     return this.tail.write(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture write(Object msg, ChannelPromise promise) {
/* 1015 */     return this.tail.write(msg, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
/* 1020 */     return this.tail.writeAndFlush(msg, promise);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture writeAndFlush(Object msg) {
/* 1025 */     return this.tail.writeAndFlush(msg);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPromise newPromise() {
/* 1030 */     return new DefaultChannelPromise(this.channel);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelProgressivePromise newProgressivePromise() {
/* 1035 */     return new DefaultChannelProgressivePromise(this.channel);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture newSucceededFuture() {
/* 1040 */     return this.succeededFuture;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelFuture newFailedFuture(Throwable cause) {
/* 1045 */     return new FailedChannelFuture(this.channel, null, cause);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ChannelPromise voidPromise() {
/* 1050 */     return this.voidPromise;
/*      */   }
/*      */   
/*      */   private void checkDuplicateName(String name) {
/* 1054 */     if (context0(name) != null) {
/* 1055 */       throw new IllegalArgumentException("Duplicate handler name: " + name);
/*      */     }
/*      */   }
/*      */   
/*      */   private AbstractChannelHandlerContext context0(String name) {
/* 1060 */     AbstractChannelHandlerContext context = this.head.next;
/* 1061 */     while (context != this.tail) {
/* 1062 */       if (context.name().equals(name)) {
/* 1063 */         return context;
/*      */       }
/* 1065 */       context = context.next;
/*      */     } 
/* 1067 */     return null;
/*      */   }
/*      */   
/*      */   private AbstractChannelHandlerContext getContextOrDie(String name) {
/* 1071 */     AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)context(name);
/* 1072 */     if (ctx == null) {
/* 1073 */       throw new NoSuchElementException(name);
/*      */     }
/* 1075 */     return ctx;
/*      */   }
/*      */ 
/*      */   
/*      */   private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
/* 1080 */     AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)context(handler);
/* 1081 */     if (ctx == null) {
/* 1082 */       throw new NoSuchElementException(handler.getClass().getName());
/*      */     }
/* 1084 */     return ctx;
/*      */   }
/*      */ 
/*      */   
/*      */   private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
/* 1089 */     AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)context(handlerType);
/* 1090 */     if (ctx == null) {
/* 1091 */       throw new NoSuchElementException(handlerType.getName());
/*      */     }
/* 1093 */     return ctx;
/*      */   }
/*      */ 
/*      */   
/*      */   private void callHandlerAddedForAllHandlers() {
/*      */     PendingHandlerCallback pendingHandlerCallbackHead;
/* 1099 */     synchronized (this) {
/* 1100 */       assert !this.registered;
/*      */ 
/*      */       
/* 1103 */       this.registered = true;
/*      */       
/* 1105 */       pendingHandlerCallbackHead = this.pendingHandlerCallbackHead;
/*      */       
/* 1107 */       this.pendingHandlerCallbackHead = null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1113 */     PendingHandlerCallback task = pendingHandlerCallbackHead;
/* 1114 */     while (task != null) {
/* 1115 */       task.execute();
/* 1116 */       task = task.next;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void callHandlerCallbackLater(AbstractChannelHandlerContext ctx, boolean added) {
/* 1121 */     assert !this.registered;
/*      */     
/* 1123 */     PendingHandlerCallback task = added ? new PendingHandlerAddedTask(ctx) : new PendingHandlerRemovedTask(ctx);
/* 1124 */     PendingHandlerCallback pending = this.pendingHandlerCallbackHead;
/* 1125 */     if (pending == null) {
/* 1126 */       this.pendingHandlerCallbackHead = task;
/*      */     } else {
/*      */       
/* 1129 */       while (pending.next != null) {
/* 1130 */         pending = pending.next;
/*      */       }
/* 1132 */       pending.next = task;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void callHandlerAddedInEventLoop(final AbstractChannelHandlerContext newCtx, EventExecutor executor) {
/* 1137 */     newCtx.setAddPending();
/* 1138 */     executor.execute(new Runnable()
/*      */         {
/*      */           public void run() {
/* 1141 */             DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundException(Throwable cause) {
/*      */     try {
/* 1152 */       logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", cause);
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/* 1157 */       ReferenceCountUtil.release(cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundChannelActive() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundChannelInactive() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundMessage(Object msg) {
/*      */     try {
/* 1182 */       logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", msg);
/*      */     }
/*      */     finally {
/*      */       
/* 1186 */       ReferenceCountUtil.release(msg);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
/* 1196 */     onUnhandledInboundMessage(msg);
/* 1197 */     if (logger.isDebugEnabled()) {
/* 1198 */       logger.debug("Discarded message pipeline : {}. Channel : {}.", ctx
/* 1199 */           .pipeline().names(), ctx.channel());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundChannelReadComplete() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledInboundUserEventTriggered(Object evt) {
/* 1218 */     ReferenceCountUtil.release(evt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void onUnhandledChannelWritabilityChanged() {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void incrementPendingOutboundBytes(long size) {
/* 1230 */     ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
/* 1231 */     if (buffer != null) {
/* 1232 */       buffer.incrementPendingOutboundBytes(size);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void decrementPendingOutboundBytes(long size) {
/* 1238 */     ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
/* 1239 */     if (buffer != null)
/* 1240 */       buffer.decrementPendingOutboundBytes(size); 
/*      */   }
/*      */   
/*      */   final class TailContext
/*      */     extends AbstractChannelHandlerContext
/*      */     implements ChannelInboundHandler
/*      */   {
/*      */     TailContext(DefaultChannelPipeline pipeline) {
/* 1248 */       super(pipeline, (EventExecutor)null, DefaultChannelPipeline.TAIL_NAME, (Class)TailContext.class);
/* 1249 */       setAddComplete();
/*      */     }
/*      */ 
/*      */     
/*      */     public ChannelHandler handler() {
/* 1254 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelRegistered(ChannelHandlerContext ctx) {}
/*      */ 
/*      */     
/*      */     public void channelUnregistered(ChannelHandlerContext ctx) {}
/*      */ 
/*      */     
/*      */     public void channelActive(ChannelHandlerContext ctx) {
/* 1265 */       DefaultChannelPipeline.this.onUnhandledInboundChannelActive();
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelInactive(ChannelHandlerContext ctx) {
/* 1270 */       DefaultChannelPipeline.this.onUnhandledInboundChannelInactive();
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelWritabilityChanged(ChannelHandlerContext ctx) {
/* 1275 */       DefaultChannelPipeline.this.onUnhandledChannelWritabilityChanged();
/*      */     }
/*      */ 
/*      */     
/*      */     public void handlerAdded(ChannelHandlerContext ctx) {}
/*      */ 
/*      */     
/*      */     public void handlerRemoved(ChannelHandlerContext ctx) {}
/*      */ 
/*      */     
/*      */     public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
/* 1286 */       DefaultChannelPipeline.this.onUnhandledInboundUserEventTriggered(evt);
/*      */     }
/*      */ 
/*      */     
/*      */     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
/* 1291 */       DefaultChannelPipeline.this.onUnhandledInboundException(cause);
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelRead(ChannelHandlerContext ctx, Object msg) {
/* 1296 */       DefaultChannelPipeline.this.onUnhandledInboundMessage(ctx, msg);
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelReadComplete(ChannelHandlerContext ctx) {
/* 1301 */       DefaultChannelPipeline.this.onUnhandledInboundChannelReadComplete();
/*      */     }
/*      */   }
/*      */   
/*      */   final class HeadContext
/*      */     extends AbstractChannelHandlerContext
/*      */     implements ChannelOutboundHandler, ChannelInboundHandler {
/*      */     private final Channel.Unsafe unsafe;
/*      */     
/*      */     HeadContext(DefaultChannelPipeline pipeline) {
/* 1311 */       super(pipeline, (EventExecutor)null, DefaultChannelPipeline.HEAD_NAME, (Class)HeadContext.class);
/* 1312 */       this.unsafe = pipeline.channel().unsafe();
/* 1313 */       setAddComplete();
/*      */     }
/*      */ 
/*      */     
/*      */     public ChannelHandler handler() {
/* 1318 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handlerAdded(ChannelHandlerContext ctx) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handlerRemoved(ChannelHandlerContext ctx) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
/* 1334 */       this.unsafe.bind(localAddress, promise);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 1342 */       this.unsafe.connect(remoteAddress, localAddress, promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 1347 */       this.unsafe.disconnect(promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 1352 */       this.unsafe.close(promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 1357 */       this.unsafe.deregister(promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public void read(ChannelHandlerContext ctx) {
/* 1362 */       this.unsafe.beginRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
/* 1367 */       this.unsafe.write(msg, promise);
/*      */     }
/*      */ 
/*      */     
/*      */     public void flush(ChannelHandlerContext ctx) {
/* 1372 */       this.unsafe.flush();
/*      */     }
/*      */ 
/*      */     
/*      */     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
/* 1377 */       ctx.fireExceptionCaught(cause);
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelRegistered(ChannelHandlerContext ctx) {
/* 1382 */       DefaultChannelPipeline.this.invokeHandlerAddedIfNeeded();
/* 1383 */       ctx.fireChannelRegistered();
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelUnregistered(ChannelHandlerContext ctx) {
/* 1388 */       ctx.fireChannelUnregistered();
/*      */ 
/*      */       
/* 1391 */       if (!DefaultChannelPipeline.this.channel.isOpen()) {
/* 1392 */         DefaultChannelPipeline.this.destroy();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelActive(ChannelHandlerContext ctx) {
/* 1398 */       ctx.fireChannelActive();
/*      */       
/* 1400 */       readIfIsAutoRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelInactive(ChannelHandlerContext ctx) {
/* 1405 */       ctx.fireChannelInactive();
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelRead(ChannelHandlerContext ctx, Object msg) {
/* 1410 */       ctx.fireChannelRead(msg);
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelReadComplete(ChannelHandlerContext ctx) {
/* 1415 */       ctx.fireChannelReadComplete();
/*      */       
/* 1417 */       readIfIsAutoRead();
/*      */     }
/*      */     
/*      */     private void readIfIsAutoRead() {
/* 1421 */       if (DefaultChannelPipeline.this.channel.config().isAutoRead()) {
/* 1422 */         DefaultChannelPipeline.this.channel.read();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
/* 1428 */       ctx.fireUserEventTriggered(evt);
/*      */     }
/*      */ 
/*      */     
/*      */     public void channelWritabilityChanged(ChannelHandlerContext ctx) {
/* 1433 */       ctx.fireChannelWritabilityChanged();
/*      */     }
/*      */   }
/*      */   
/*      */   private static abstract class PendingHandlerCallback implements Runnable {
/*      */     final AbstractChannelHandlerContext ctx;
/*      */     PendingHandlerCallback next;
/*      */     
/*      */     PendingHandlerCallback(AbstractChannelHandlerContext ctx) {
/* 1442 */       this.ctx = ctx;
/*      */     }
/*      */     
/*      */     abstract void execute();
/*      */   }
/*      */   
/*      */   private final class PendingHandlerAddedTask
/*      */     extends PendingHandlerCallback {
/*      */     PendingHandlerAddedTask(AbstractChannelHandlerContext ctx) {
/* 1451 */       super(ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/* 1456 */       DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     void execute() {
/* 1461 */       EventExecutor executor = this.ctx.executor();
/* 1462 */       if (executor.inEventLoop()) {
/* 1463 */         DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
/*      */       } else {
/*      */         try {
/* 1466 */           executor.execute(this);
/* 1467 */         } catch (RejectedExecutionException e) {
/* 1468 */           if (DefaultChannelPipeline.logger.isWarnEnabled()) {
/* 1469 */             DefaultChannelPipeline.logger.warn("Can't invoke handlerAdded() as the EventExecutor {} rejected it, removing handler {}.", new Object[] { executor, this.ctx
/*      */                   
/* 1471 */                   .name(), e });
/*      */           }
/* 1473 */           DefaultChannelPipeline.this.atomicRemoveFromHandlerList(this.ctx);
/* 1474 */           this.ctx.setRemoved();
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private final class PendingHandlerRemovedTask
/*      */     extends PendingHandlerCallback {
/*      */     PendingHandlerRemovedTask(AbstractChannelHandlerContext ctx) {
/* 1483 */       super(ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/* 1488 */       DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
/*      */     }
/*      */ 
/*      */     
/*      */     void execute() {
/* 1493 */       EventExecutor executor = this.ctx.executor();
/* 1494 */       if (executor.inEventLoop()) {
/* 1495 */         DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
/*      */       } else {
/*      */         try {
/* 1498 */           executor.execute(this);
/* 1499 */         } catch (RejectedExecutionException e) {
/* 1500 */           if (DefaultChannelPipeline.logger.isWarnEnabled()) {
/* 1501 */             DefaultChannelPipeline.logger.warn("Can't invoke handlerRemoved() as the EventExecutor {} rejected it, removing handler {}.", new Object[] { executor, this.ctx
/*      */                   
/* 1503 */                   .name(), e });
/*      */           }
/*      */           
/* 1506 */           this.ctx.setRemoved();
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultChannelPipeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */