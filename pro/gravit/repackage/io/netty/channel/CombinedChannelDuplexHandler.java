/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.Attribute;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ThrowableUtil;
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
/*     */ public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  35 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CombinedChannelDuplexHandler.class);
/*     */ 
/*     */   
/*     */   private DelegatingChannelHandlerContext inboundCtx;
/*     */   
/*     */   private DelegatingChannelHandlerContext outboundCtx;
/*     */   
/*     */   private volatile boolean handlerAdded;
/*     */   
/*     */   private I inboundHandler;
/*     */   
/*     */   private O outboundHandler;
/*     */ 
/*     */   
/*     */   protected CombinedChannelDuplexHandler() {
/*  50 */     ensureNotSharable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CombinedChannelDuplexHandler(I inboundHandler, O outboundHandler) {
/*  57 */     ensureNotSharable();
/*  58 */     init(inboundHandler, outboundHandler);
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
/*     */   protected final void init(I inboundHandler, O outboundHandler) {
/*  70 */     validate(inboundHandler, outboundHandler);
/*  71 */     this.inboundHandler = inboundHandler;
/*  72 */     this.outboundHandler = outboundHandler;
/*     */   }
/*     */   
/*     */   private void validate(I inboundHandler, O outboundHandler) {
/*  76 */     if (this.inboundHandler != null) {
/*  77 */       throw new IllegalStateException("init() can not be invoked if " + CombinedChannelDuplexHandler.class
/*  78 */           .getSimpleName() + " was constructed with non-default constructor.");
/*     */     }
/*     */ 
/*     */     
/*  82 */     ObjectUtil.checkNotNull(inboundHandler, "inboundHandler");
/*  83 */     ObjectUtil.checkNotNull(outboundHandler, "outboundHandler");
/*     */     
/*  85 */     if (inboundHandler instanceof ChannelOutboundHandler) {
/*  86 */       throw new IllegalArgumentException("inboundHandler must not implement " + ChannelOutboundHandler.class
/*     */           
/*  88 */           .getSimpleName() + " to get combined.");
/*     */     }
/*  90 */     if (outboundHandler instanceof ChannelInboundHandler) {
/*  91 */       throw new IllegalArgumentException("outboundHandler must not implement " + ChannelInboundHandler.class
/*     */           
/*  93 */           .getSimpleName() + " to get combined.");
/*     */     }
/*     */   }
/*     */   
/*     */   protected final I inboundHandler() {
/*  98 */     return this.inboundHandler;
/*     */   }
/*     */   
/*     */   protected final O outboundHandler() {
/* 102 */     return this.outboundHandler;
/*     */   }
/*     */   
/*     */   private void checkAdded() {
/* 106 */     if (!this.handlerAdded) {
/* 107 */       throw new IllegalStateException("handler not added to pipeline yet");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void removeInboundHandler() {
/* 115 */     checkAdded();
/* 116 */     this.inboundCtx.remove();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void removeOutboundHandler() {
/* 123 */     checkAdded();
/* 124 */     this.outboundCtx.remove();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 129 */     if (this.inboundHandler == null) {
/* 130 */       throw new IllegalStateException("init() must be invoked before being added to a " + ChannelPipeline.class
/* 131 */           .getSimpleName() + " if " + CombinedChannelDuplexHandler.class
/* 132 */           .getSimpleName() + " was constructed with the default constructor.");
/*     */     }
/*     */ 
/*     */     
/* 136 */     this.outboundCtx = new DelegatingChannelHandlerContext(ctx, (ChannelHandler)this.outboundHandler);
/* 137 */     this.inboundCtx = new DelegatingChannelHandlerContext(ctx, (ChannelHandler)this.inboundHandler)
/*     */       {
/*     */         public ChannelHandlerContext fireExceptionCaught(Throwable cause)
/*     */         {
/* 141 */           if (!CombinedChannelDuplexHandler.this.outboundCtx.removed) {
/*     */ 
/*     */             
/*     */             try {
/* 145 */               CombinedChannelDuplexHandler.this.outboundHandler.exceptionCaught(CombinedChannelDuplexHandler.this.outboundCtx, cause);
/* 146 */             } catch (Throwable error) {
/* 147 */               if (CombinedChannelDuplexHandler.logger.isDebugEnabled()) {
/* 148 */                 CombinedChannelDuplexHandler.logger.debug("An exception {}was thrown by a user handler's exceptionCaught() method while handling the following exception:", 
/*     */ 
/*     */ 
/*     */                     
/* 152 */                     ThrowableUtil.stackTraceToString(error), cause);
/* 153 */               } else if (CombinedChannelDuplexHandler.logger.isWarnEnabled()) {
/* 154 */                 CombinedChannelDuplexHandler.logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:", error, cause);
/*     */               }
/*     */             
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 161 */             super.fireExceptionCaught(cause);
/*     */           } 
/* 163 */           return this;
/*     */         }
/*     */       };
/*     */ 
/*     */ 
/*     */     
/* 169 */     this.handlerAdded = true;
/*     */     
/*     */     try {
/* 172 */       this.inboundHandler.handlerAdded(this.inboundCtx);
/*     */     } finally {
/* 174 */       this.outboundHandler.handlerAdded(this.outboundCtx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/*     */     try {
/* 181 */       this.inboundCtx.remove();
/*     */     } finally {
/* 183 */       this.outboundCtx.remove();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/* 189 */     assert ctx == this.inboundCtx.ctx;
/* 190 */     if (!this.inboundCtx.removed) {
/* 191 */       this.inboundHandler.channelRegistered(this.inboundCtx);
/*     */     } else {
/* 193 */       this.inboundCtx.fireChannelRegistered();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
/* 199 */     assert ctx == this.inboundCtx.ctx;
/* 200 */     if (!this.inboundCtx.removed) {
/* 201 */       this.inboundHandler.channelUnregistered(this.inboundCtx);
/*     */     } else {
/* 203 */       this.inboundCtx.fireChannelUnregistered();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/* 209 */     assert ctx == this.inboundCtx.ctx;
/* 210 */     if (!this.inboundCtx.removed) {
/* 211 */       this.inboundHandler.channelActive(this.inboundCtx);
/*     */     } else {
/* 213 */       this.inboundCtx.fireChannelActive();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 219 */     assert ctx == this.inboundCtx.ctx;
/* 220 */     if (!this.inboundCtx.removed) {
/* 221 */       this.inboundHandler.channelInactive(this.inboundCtx);
/*     */     } else {
/* 223 */       this.inboundCtx.fireChannelInactive();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 229 */     assert ctx == this.inboundCtx.ctx;
/* 230 */     if (!this.inboundCtx.removed) {
/* 231 */       this.inboundHandler.exceptionCaught(this.inboundCtx, cause);
/*     */     } else {
/* 233 */       this.inboundCtx.fireExceptionCaught(cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 239 */     assert ctx == this.inboundCtx.ctx;
/* 240 */     if (!this.inboundCtx.removed) {
/* 241 */       this.inboundHandler.userEventTriggered(this.inboundCtx, evt);
/*     */     } else {
/* 243 */       this.inboundCtx.fireUserEventTriggered(evt);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 249 */     assert ctx == this.inboundCtx.ctx;
/* 250 */     if (!this.inboundCtx.removed) {
/* 251 */       this.inboundHandler.channelRead(this.inboundCtx, msg);
/*     */     } else {
/* 253 */       this.inboundCtx.fireChannelRead(msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 259 */     assert ctx == this.inboundCtx.ctx;
/* 260 */     if (!this.inboundCtx.removed) {
/* 261 */       this.inboundHandler.channelReadComplete(this.inboundCtx);
/*     */     } else {
/* 263 */       this.inboundCtx.fireChannelReadComplete();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/* 269 */     assert ctx == this.inboundCtx.ctx;
/* 270 */     if (!this.inboundCtx.removed) {
/* 271 */       this.inboundHandler.channelWritabilityChanged(this.inboundCtx);
/*     */     } else {
/* 273 */       this.inboundCtx.fireChannelWritabilityChanged();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 281 */     assert ctx == this.outboundCtx.ctx;
/* 282 */     if (!this.outboundCtx.removed) {
/* 283 */       this.outboundHandler.bind(this.outboundCtx, localAddress, promise);
/*     */     } else {
/* 285 */       this.outboundCtx.bind(localAddress, promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 294 */     assert ctx == this.outboundCtx.ctx;
/* 295 */     if (!this.outboundCtx.removed) {
/* 296 */       this.outboundHandler.connect(this.outboundCtx, remoteAddress, localAddress, promise);
/*     */     } else {
/* 298 */       this.outboundCtx.connect(localAddress, promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 304 */     assert ctx == this.outboundCtx.ctx;
/* 305 */     if (!this.outboundCtx.removed) {
/* 306 */       this.outboundHandler.disconnect(this.outboundCtx, promise);
/*     */     } else {
/* 308 */       this.outboundCtx.disconnect(promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 314 */     assert ctx == this.outboundCtx.ctx;
/* 315 */     if (!this.outboundCtx.removed) {
/* 316 */       this.outboundHandler.close(this.outboundCtx, promise);
/*     */     } else {
/* 318 */       this.outboundCtx.close(promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 324 */     assert ctx == this.outboundCtx.ctx;
/* 325 */     if (!this.outboundCtx.removed) {
/* 326 */       this.outboundHandler.deregister(this.outboundCtx, promise);
/*     */     } else {
/* 328 */       this.outboundCtx.deregister(promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 334 */     assert ctx == this.outboundCtx.ctx;
/* 335 */     if (!this.outboundCtx.removed) {
/* 336 */       this.outboundHandler.read(this.outboundCtx);
/*     */     } else {
/* 338 */       this.outboundCtx.read();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 344 */     assert ctx == this.outboundCtx.ctx;
/* 345 */     if (!this.outboundCtx.removed) {
/* 346 */       this.outboundHandler.write(this.outboundCtx, msg, promise);
/*     */     } else {
/* 348 */       this.outboundCtx.write(msg, promise);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 354 */     assert ctx == this.outboundCtx.ctx;
/* 355 */     if (!this.outboundCtx.removed) {
/* 356 */       this.outboundHandler.flush(this.outboundCtx);
/*     */     } else {
/* 358 */       this.outboundCtx.flush();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class DelegatingChannelHandlerContext
/*     */     implements ChannelHandlerContext {
/*     */     private final ChannelHandlerContext ctx;
/*     */     private final ChannelHandler handler;
/*     */     boolean removed;
/*     */     
/*     */     DelegatingChannelHandlerContext(ChannelHandlerContext ctx, ChannelHandler handler) {
/* 369 */       this.ctx = ctx;
/* 370 */       this.handler = handler;
/*     */     }
/*     */ 
/*     */     
/*     */     public Channel channel() {
/* 375 */       return this.ctx.channel();
/*     */     }
/*     */ 
/*     */     
/*     */     public EventExecutor executor() {
/* 380 */       return this.ctx.executor();
/*     */     }
/*     */ 
/*     */     
/*     */     public String name() {
/* 385 */       return this.ctx.name();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandler handler() {
/* 390 */       return this.ctx.handler();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isRemoved() {
/* 395 */       return (this.removed || this.ctx.isRemoved());
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelRegistered() {
/* 400 */       this.ctx.fireChannelRegistered();
/* 401 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelUnregistered() {
/* 406 */       this.ctx.fireChannelUnregistered();
/* 407 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelActive() {
/* 412 */       this.ctx.fireChannelActive();
/* 413 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelInactive() {
/* 418 */       this.ctx.fireChannelInactive();
/* 419 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
/* 424 */       this.ctx.fireExceptionCaught(cause);
/* 425 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireUserEventTriggered(Object event) {
/* 430 */       this.ctx.fireUserEventTriggered(event);
/* 431 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelRead(Object msg) {
/* 436 */       this.ctx.fireChannelRead(msg);
/* 437 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelReadComplete() {
/* 442 */       this.ctx.fireChannelReadComplete();
/* 443 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext fireChannelWritabilityChanged() {
/* 448 */       this.ctx.fireChannelWritabilityChanged();
/* 449 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture bind(SocketAddress localAddress) {
/* 454 */       return this.ctx.bind(localAddress);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture connect(SocketAddress remoteAddress) {
/* 459 */       return this.ctx.connect(remoteAddress);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
/* 464 */       return this.ctx.connect(remoteAddress, localAddress);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture disconnect() {
/* 469 */       return this.ctx.disconnect();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture close() {
/* 474 */       return this.ctx.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture deregister() {
/* 479 */       return this.ctx.deregister();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
/* 484 */       return this.ctx.bind(localAddress, promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
/* 489 */       return this.ctx.connect(remoteAddress, promise);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/* 495 */       return this.ctx.connect(remoteAddress, localAddress, promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture disconnect(ChannelPromise promise) {
/* 500 */       return this.ctx.disconnect(promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture close(ChannelPromise promise) {
/* 505 */       return this.ctx.close(promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture deregister(ChannelPromise promise) {
/* 510 */       return this.ctx.deregister(promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext read() {
/* 515 */       this.ctx.read();
/* 516 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture write(Object msg) {
/* 521 */       return this.ctx.write(msg);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture write(Object msg, ChannelPromise promise) {
/* 526 */       return this.ctx.write(msg, promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelHandlerContext flush() {
/* 531 */       this.ctx.flush();
/* 532 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
/* 537 */       return this.ctx.writeAndFlush(msg, promise);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture writeAndFlush(Object msg) {
/* 542 */       return this.ctx.writeAndFlush(msg);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelPipeline pipeline() {
/* 547 */       return this.ctx.pipeline();
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBufAllocator alloc() {
/* 552 */       return this.ctx.alloc();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelPromise newPromise() {
/* 557 */       return this.ctx.newPromise();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelProgressivePromise newProgressivePromise() {
/* 562 */       return this.ctx.newProgressivePromise();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture newSucceededFuture() {
/* 567 */       return this.ctx.newSucceededFuture();
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelFuture newFailedFuture(Throwable cause) {
/* 572 */       return this.ctx.newFailedFuture(cause);
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelPromise voidPromise() {
/* 577 */       return this.ctx.voidPromise();
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> Attribute<T> attr(AttributeKey<T> key) {
/* 582 */       return this.ctx.channel().attr(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> boolean hasAttr(AttributeKey<T> key) {
/* 587 */       return this.ctx.channel().hasAttr(key);
/*     */     }
/*     */     
/*     */     final void remove() {
/* 591 */       EventExecutor executor = executor();
/* 592 */       if (executor.inEventLoop()) {
/* 593 */         remove0();
/*     */       } else {
/* 595 */         executor.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 598 */                 CombinedChannelDuplexHandler.DelegatingChannelHandlerContext.this.remove0();
/*     */               }
/*     */             });
/*     */       } 
/*     */     }
/*     */     
/*     */     private void remove0() {
/* 605 */       if (!this.removed) {
/* 606 */         this.removed = true;
/*     */         try {
/* 608 */           this.handler.handlerRemoved(this);
/* 609 */         } catch (Throwable cause) {
/* 610 */           fireExceptionCaught(new ChannelPipelineException(this.handler
/* 611 */                 .getClass().getName() + ".handlerRemoved() has thrown an exception.", cause));
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\CombinedChannelDuplexHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */