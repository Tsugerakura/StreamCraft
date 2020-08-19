/*     */ package pro.gravit.repackage.io.netty.bootstrap;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.resolver.AddressResolver;
/*     */ import pro.gravit.repackage.io.netty.resolver.AddressResolverGroup;
/*     */ import pro.gravit.repackage.io.netty.resolver.DefaultAddressResolverGroup;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
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
/*     */ public class Bootstrap
/*     */   extends AbstractBootstrap<Bootstrap, Channel>
/*     */ {
/*  48 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
/*     */   
/*  50 */   private static final AddressResolverGroup<?> DEFAULT_RESOLVER = (AddressResolverGroup<?>)DefaultAddressResolverGroup.INSTANCE;
/*     */   
/*  52 */   private final BootstrapConfig config = new BootstrapConfig(this);
/*     */   
/*  54 */   private volatile AddressResolverGroup<SocketAddress> resolver = (AddressResolverGroup)DEFAULT_RESOLVER;
/*     */ 
/*     */   
/*     */   private volatile SocketAddress remoteAddress;
/*     */ 
/*     */ 
/*     */   
/*     */   private Bootstrap(Bootstrap bootstrap) {
/*  62 */     super(bootstrap);
/*  63 */     this.resolver = bootstrap.resolver;
/*  64 */     this.remoteAddress = bootstrap.remoteAddress;
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
/*     */   public Bootstrap resolver(AddressResolverGroup<?> resolver) {
/*  77 */     this.resolver = (resolver == null) ? (AddressResolverGroup)DEFAULT_RESOLVER : (AddressResolverGroup)resolver;
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bootstrap remoteAddress(SocketAddress remoteAddress) {
/*  86 */     this.remoteAddress = remoteAddress;
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bootstrap remoteAddress(String inetHost, int inetPort) {
/*  94 */     this.remoteAddress = InetSocketAddress.createUnresolved(inetHost, inetPort);
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bootstrap remoteAddress(InetAddress inetHost, int inetPort) {
/* 102 */     this.remoteAddress = new InetSocketAddress(inetHost, inetPort);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture connect() {
/* 110 */     validate();
/* 111 */     SocketAddress remoteAddress = this.remoteAddress;
/* 112 */     if (remoteAddress == null) {
/* 113 */       throw new IllegalStateException("remoteAddress not set");
/*     */     }
/*     */     
/* 116 */     return doResolveAndConnect(remoteAddress, this.config.localAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture connect(String inetHost, int inetPort) {
/* 123 */     return connect(InetSocketAddress.createUnresolved(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture connect(InetAddress inetHost, int inetPort) {
/* 130 */     return connect(new InetSocketAddress(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture connect(SocketAddress remoteAddress) {
/* 137 */     ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
/* 138 */     validate();
/* 139 */     return doResolveAndConnect(remoteAddress, this.config.localAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
/* 146 */     ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
/* 147 */     validate();
/* 148 */     return doResolveAndConnect(remoteAddress, localAddress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelFuture doResolveAndConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
/* 155 */     ChannelFuture regFuture = initAndRegister();
/* 156 */     final Channel channel = regFuture.channel();
/*     */     
/* 158 */     if (regFuture.isDone()) {
/* 159 */       if (!regFuture.isSuccess()) {
/* 160 */         return regFuture;
/*     */       }
/* 162 */       return doResolveAndConnect0(channel, remoteAddress, localAddress, channel.newPromise());
/*     */     } 
/*     */     
/* 165 */     final AbstractBootstrap.PendingRegistrationPromise promise = new AbstractBootstrap.PendingRegistrationPromise(channel);
/* 166 */     regFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           
/*     */           public void operationComplete(ChannelFuture future) throws Exception
/*     */           {
/* 171 */             Throwable cause = future.cause();
/* 172 */             if (cause != null) {
/*     */ 
/*     */               
/* 175 */               promise.setFailure(cause);
/*     */             }
/*     */             else {
/*     */               
/* 179 */               promise.registered();
/* 180 */               Bootstrap.this.doResolveAndConnect0(channel, remoteAddress, localAddress, (ChannelPromise)promise);
/*     */             } 
/*     */           }
/*     */         });
/* 184 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelFuture doResolveAndConnect0(final Channel channel, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
/*     */     try {
/* 191 */       EventLoop eventLoop = channel.eventLoop();
/* 192 */       AddressResolver<SocketAddress> resolver = this.resolver.getResolver((EventExecutor)eventLoop);
/*     */       
/* 194 */       if (!resolver.isSupported(remoteAddress) || resolver.isResolved(remoteAddress)) {
/*     */         
/* 196 */         doConnect(remoteAddress, localAddress, promise);
/* 197 */         return (ChannelFuture)promise;
/*     */       } 
/*     */       
/* 200 */       Future<SocketAddress> resolveFuture = resolver.resolve(remoteAddress);
/*     */       
/* 202 */       if (resolveFuture.isDone()) {
/* 203 */         Throwable resolveFailureCause = resolveFuture.cause();
/*     */         
/* 205 */         if (resolveFailureCause != null) {
/*     */           
/* 207 */           channel.close();
/* 208 */           promise.setFailure(resolveFailureCause);
/*     */         } else {
/*     */           
/* 211 */           doConnect((SocketAddress)resolveFuture.getNow(), localAddress, promise);
/*     */         } 
/* 213 */         return (ChannelFuture)promise;
/*     */       } 
/*     */ 
/*     */       
/* 217 */       resolveFuture.addListener((GenericFutureListener)new FutureListener<SocketAddress>()
/*     */           {
/*     */             public void operationComplete(Future<SocketAddress> future) throws Exception {
/* 220 */               if (future.cause() != null) {
/* 221 */                 channel.close();
/* 222 */                 promise.setFailure(future.cause());
/*     */               } else {
/* 224 */                 Bootstrap.doConnect((SocketAddress)future.getNow(), localAddress, promise);
/*     */               } 
/*     */             }
/*     */           });
/* 228 */     } catch (Throwable cause) {
/* 229 */       promise.tryFailure(cause);
/*     */     } 
/* 231 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise connectPromise) {
/* 239 */     final Channel channel = connectPromise.channel();
/* 240 */     channel.eventLoop().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 243 */             if (localAddress == null) {
/* 244 */               channel.connect(remoteAddress, connectPromise);
/*     */             } else {
/* 246 */               channel.connect(remoteAddress, localAddress, connectPromise);
/*     */             } 
/* 248 */             connectPromise.addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void init(Channel channel) {
/* 256 */     ChannelPipeline p = channel.pipeline();
/* 257 */     p.addLast(new ChannelHandler[] { this.config.handler() });
/*     */     
/* 259 */     setChannelOptions(channel, (Map.Entry<ChannelOption<?>, Object>[])options0().entrySet().toArray((Object[])EMPTY_OPTION_ARRAY), logger);
/* 260 */     setAttributes(channel, (Map.Entry<AttributeKey<?>, Object>[])attrs0().entrySet().toArray((Object[])EMPTY_ATTRIBUTE_ARRAY));
/*     */   }
/*     */ 
/*     */   
/*     */   public Bootstrap validate() {
/* 265 */     super.validate();
/* 266 */     if (this.config.handler() == null) {
/* 267 */       throw new IllegalStateException("handler not set");
/*     */     }
/* 269 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Bootstrap clone() {
/* 275 */     return new Bootstrap(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bootstrap clone(EventLoopGroup group) {
/* 284 */     Bootstrap bs = new Bootstrap(this);
/* 285 */     bs.group = group;
/* 286 */     return bs;
/*     */   }
/*     */ 
/*     */   
/*     */   public final BootstrapConfig config() {
/* 291 */     return this.config;
/*     */   }
/*     */   
/*     */   final SocketAddress remoteAddress() {
/* 295 */     return this.remoteAddress;
/*     */   }
/*     */   
/*     */   final AddressResolverGroup<?> resolver() {
/* 299 */     return this.resolver;
/*     */   }
/*     */   
/*     */   public Bootstrap() {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\bootstrap\Bootstrap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */