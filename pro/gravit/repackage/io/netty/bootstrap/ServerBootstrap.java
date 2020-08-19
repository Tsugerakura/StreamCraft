/*     */ package pro.gravit.repackage.io.netty.bootstrap;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInitializer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.ServerChannel;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class ServerBootstrap
/*     */   extends AbstractBootstrap<ServerBootstrap, ServerChannel>
/*     */ {
/*  46 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
/*     */   
/*  48 */   private final Map<ChannelOption<?>, Object> childOptions = new ConcurrentHashMap<ChannelOption<?>, Object>();
/*  49 */   private final Map<AttributeKey<?>, Object> childAttrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
/*  50 */   private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
/*     */   
/*     */   private volatile EventLoopGroup childGroup;
/*     */   
/*     */   private volatile ChannelHandler childHandler;
/*     */   
/*     */   private ServerBootstrap(ServerBootstrap bootstrap) {
/*  57 */     super(bootstrap);
/*  58 */     this.childGroup = bootstrap.childGroup;
/*  59 */     this.childHandler = bootstrap.childHandler;
/*  60 */     this.childOptions.putAll(bootstrap.childOptions);
/*  61 */     this.childAttrs.putAll(bootstrap.childAttrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerBootstrap group(EventLoopGroup group) {
/*  69 */     return group(group, group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
/*  78 */     super.group(parentGroup);
/*  79 */     if (this.childGroup != null) {
/*  80 */       throw new IllegalStateException("childGroup set already");
/*     */     }
/*  82 */     this.childGroup = (EventLoopGroup)ObjectUtil.checkNotNull(childGroup, "childGroup");
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
/*  92 */     ObjectUtil.checkNotNull(childOption, "childOption");
/*  93 */     if (value == null) {
/*  94 */       this.childOptions.remove(childOption);
/*     */     } else {
/*  96 */       this.childOptions.put(childOption, value);
/*     */     } 
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
/* 106 */     ObjectUtil.checkNotNull(childKey, "childKey");
/* 107 */     if (value == null) {
/* 108 */       this.childAttrs.remove(childKey);
/*     */     } else {
/* 110 */       this.childAttrs.put(childKey, value);
/*     */     } 
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerBootstrap childHandler(ChannelHandler childHandler) {
/* 119 */     this.childHandler = (ChannelHandler)ObjectUtil.checkNotNull(childHandler, "childHandler");
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   void init(Channel channel) {
/* 125 */     setChannelOptions(channel, (Map.Entry<ChannelOption<?>, Object>[])options0().entrySet().toArray((Object[])EMPTY_OPTION_ARRAY), logger);
/* 126 */     setAttributes(channel, (Map.Entry<AttributeKey<?>, Object>[])attrs0().entrySet().toArray((Object[])EMPTY_ATTRIBUTE_ARRAY));
/*     */     
/* 128 */     ChannelPipeline p = channel.pipeline();
/*     */     
/* 130 */     final EventLoopGroup currentChildGroup = this.childGroup;
/* 131 */     final ChannelHandler currentChildHandler = this.childHandler;
/*     */     
/* 133 */     final Map.Entry[] currentChildOptions = (Map.Entry[])this.childOptions.entrySet().toArray((Object[])EMPTY_OPTION_ARRAY);
/* 134 */     final Map.Entry[] currentChildAttrs = (Map.Entry[])this.childAttrs.entrySet().toArray((Object[])EMPTY_ATTRIBUTE_ARRAY);
/*     */     
/* 136 */     p.addLast(new ChannelHandler[] { (ChannelHandler)new ChannelInitializer<Channel>()
/*     */           {
/*     */             public void initChannel(final Channel ch) {
/* 139 */               final ChannelPipeline pipeline = ch.pipeline();
/* 140 */               ChannelHandler handler = ServerBootstrap.this.config.handler();
/* 141 */               if (handler != null) {
/* 142 */                 pipeline.addLast(new ChannelHandler[] { handler });
/*     */               }
/*     */               
/* 145 */               ch.eventLoop().execute(new Runnable()
/*     */                   {
/*     */                     public void run() {
/* 148 */                       pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new ServerBootstrap.ServerBootstrapAcceptor(this.val$ch, this.this$1.val$currentChildGroup, this.this$1.val$currentChildHandler, (Map.Entry<ChannelOption<?>, Object>[])this.this$1.val$currentChildOptions, (Map.Entry<AttributeKey<?>, Object>[])this.this$1.val$currentChildAttrs) });
/*     */                     }
/*     */                   });
/*     */             }
/*     */           } });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerBootstrap validate() {
/* 158 */     super.validate();
/* 159 */     if (this.childHandler == null) {
/* 160 */       throw new IllegalStateException("childHandler not set");
/*     */     }
/* 162 */     if (this.childGroup == null) {
/* 163 */       logger.warn("childGroup is not set. Using parentGroup instead.");
/* 164 */       this.childGroup = this.config.group();
/*     */     } 
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ServerBootstrapAcceptor
/*     */     extends ChannelInboundHandlerAdapter
/*     */   {
/*     */     private final EventLoopGroup childGroup;
/*     */     private final ChannelHandler childHandler;
/*     */     private final Map.Entry<ChannelOption<?>, Object>[] childOptions;
/*     */     private final Map.Entry<AttributeKey<?>, Object>[] childAttrs;
/*     */     private final Runnable enableAutoReadTask;
/*     */     
/*     */     ServerBootstrapAcceptor(final Channel channel, EventLoopGroup childGroup, ChannelHandler childHandler, Map.Entry<ChannelOption<?>, Object>[] childOptions, Map.Entry<AttributeKey<?>, Object>[] childAttrs) {
/* 180 */       this.childGroup = childGroup;
/* 181 */       this.childHandler = childHandler;
/* 182 */       this.childOptions = childOptions;
/* 183 */       this.childAttrs = childAttrs;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 190 */       this.enableAutoReadTask = new Runnable()
/*     */         {
/*     */           public void run() {
/* 193 */             channel.config().setAutoRead(true);
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void channelRead(ChannelHandlerContext ctx, Object msg) {
/* 201 */       final Channel child = (Channel)msg;
/*     */       
/* 203 */       child.pipeline().addLast(new ChannelHandler[] { this.childHandler });
/*     */       
/* 205 */       AbstractBootstrap.setChannelOptions(child, this.childOptions, ServerBootstrap.logger);
/* 206 */       AbstractBootstrap.setAttributes(child, this.childAttrs);
/*     */       
/*     */       try {
/* 209 */         this.childGroup.register(child).addListener((GenericFutureListener)new ChannelFutureListener()
/*     */             {
/*     */               public void operationComplete(ChannelFuture future) throws Exception {
/* 212 */                 if (!future.isSuccess()) {
/* 213 */                   ServerBootstrap.ServerBootstrapAcceptor.forceClose(child, future.cause());
/*     */                 }
/*     */               }
/*     */             });
/* 217 */       } catch (Throwable t) {
/* 218 */         forceClose(child, t);
/*     */       } 
/*     */     }
/*     */     
/*     */     private static void forceClose(Channel child, Throwable t) {
/* 223 */       child.unsafe().closeForcibly();
/* 224 */       ServerBootstrap.logger.warn("Failed to register an accepted channel: {}", child, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 229 */       ChannelConfig config = ctx.channel().config();
/* 230 */       if (config.isAutoRead()) {
/*     */ 
/*     */         
/* 233 */         config.setAutoRead(false);
/* 234 */         ctx.channel().eventLoop().schedule(this.enableAutoReadTask, 1L, TimeUnit.SECONDS);
/*     */       } 
/*     */ 
/*     */       
/* 238 */       ctx.fireExceptionCaught(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerBootstrap clone() {
/* 245 */     return new ServerBootstrap(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public EventLoopGroup childGroup() {
/* 256 */     return this.childGroup;
/*     */   }
/*     */   
/*     */   final ChannelHandler childHandler() {
/* 260 */     return this.childHandler;
/*     */   }
/*     */   
/*     */   final Map<ChannelOption<?>, Object> childOptions() {
/* 264 */     return copiedMap(this.childOptions);
/*     */   }
/*     */   
/*     */   final Map<AttributeKey<?>, Object> childAttrs() {
/* 268 */     return copiedMap(this.childAttrs);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ServerBootstrapConfig config() {
/* 273 */     return this.config;
/*     */   }
/*     */   
/*     */   public ServerBootstrap() {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\bootstrap\ServerBootstrap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */