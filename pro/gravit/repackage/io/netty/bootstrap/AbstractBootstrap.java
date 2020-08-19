/*     */ package pro.gravit.repackage.io.netty.bootstrap;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFactory;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.ReflectiveChannelFactory;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GlobalEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
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
/*     */ 
/*     */ 
/*     */ public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel>
/*     */   implements Cloneable
/*     */ {
/*  54 */   static final Map.Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY = (Map.Entry<ChannelOption<?>, Object>[])new Map.Entry[0];
/*     */   
/*  56 */   static final Map.Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY = (Map.Entry<AttributeKey<?>, Object>[])new Map.Entry[0];
/*     */   
/*     */   volatile EventLoopGroup group;
/*     */   
/*     */   private volatile ChannelFactory<? extends C> channelFactory;
/*     */   private volatile SocketAddress localAddress;
/*  62 */   private final Map<ChannelOption<?>, Object> options = new ConcurrentHashMap<ChannelOption<?>, Object>();
/*  63 */   private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
/*     */ 
/*     */   
/*     */   private volatile ChannelHandler handler;
/*     */ 
/*     */ 
/*     */   
/*     */   AbstractBootstrap(AbstractBootstrap<B, C> bootstrap) {
/*  71 */     this.group = bootstrap.group;
/*  72 */     this.channelFactory = bootstrap.channelFactory;
/*  73 */     this.handler = bootstrap.handler;
/*  74 */     this.localAddress = bootstrap.localAddress;
/*  75 */     this.options.putAll(bootstrap.options);
/*  76 */     this.attrs.putAll(bootstrap.attrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B group(EventLoopGroup group) {
/*  84 */     ObjectUtil.checkNotNull(group, "group");
/*  85 */     if (this.group != null) {
/*  86 */       throw new IllegalStateException("group set already");
/*     */     }
/*  88 */     this.group = group;
/*  89 */     return self();
/*     */   }
/*     */ 
/*     */   
/*     */   private B self() {
/*  94 */     return (B)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B channel(Class<? extends C> channelClass) {
/* 103 */     return channelFactory((ChannelFactory<? extends C>)new ReflectiveChannelFactory(
/* 104 */           (Class)ObjectUtil.checkNotNull(channelClass, "channelClass")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public B channelFactory(ChannelFactory<? extends C> channelFactory) {
/* 113 */     ObjectUtil.checkNotNull(channelFactory, "channelFactory");
/* 114 */     if (this.channelFactory != null) {
/* 115 */       throw new IllegalStateException("channelFactory set already");
/*     */     }
/*     */     
/* 118 */     this.channelFactory = channelFactory;
/* 119 */     return self();
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
/*     */   public B channelFactory(ChannelFactory<? extends C> channelFactory) {
/* 131 */     return channelFactory((ChannelFactory<? extends C>)channelFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B localAddress(SocketAddress localAddress) {
/* 138 */     this.localAddress = localAddress;
/* 139 */     return self();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B localAddress(int inetPort) {
/* 146 */     return localAddress(new InetSocketAddress(inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B localAddress(String inetHost, int inetPort) {
/* 153 */     return localAddress(SocketUtils.socketAddress(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B localAddress(InetAddress inetHost, int inetPort) {
/* 160 */     return localAddress(new InetSocketAddress(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> B option(ChannelOption<T> option, T value) {
/* 168 */     ObjectUtil.checkNotNull(option, "option");
/* 169 */     if (value == null) {
/* 170 */       this.options.remove(option);
/*     */     } else {
/* 172 */       this.options.put(option, value);
/*     */     } 
/* 174 */     return self();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> B attr(AttributeKey<T> key, T value) {
/* 182 */     ObjectUtil.checkNotNull(key, "key");
/* 183 */     if (value == null) {
/* 184 */       this.attrs.remove(key);
/*     */     } else {
/* 186 */       this.attrs.put(key, value);
/*     */     } 
/* 188 */     return self();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B validate() {
/* 196 */     if (this.group == null) {
/* 197 */       throw new IllegalStateException("group not set");
/*     */     }
/* 199 */     if (this.channelFactory == null) {
/* 200 */       throw new IllegalStateException("channel or channelFactory not set");
/*     */     }
/* 202 */     return self();
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
/*     */   public ChannelFuture register() {
/* 218 */     validate();
/* 219 */     return initAndRegister();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture bind() {
/* 226 */     validate();
/* 227 */     SocketAddress localAddress = this.localAddress;
/* 228 */     if (localAddress == null) {
/* 229 */       throw new IllegalStateException("localAddress not set");
/*     */     }
/* 231 */     return doBind(localAddress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture bind(int inetPort) {
/* 238 */     return bind(new InetSocketAddress(inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture bind(String inetHost, int inetPort) {
/* 245 */     return bind(SocketUtils.socketAddress(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture bind(InetAddress inetHost, int inetPort) {
/* 252 */     return bind(new InetSocketAddress(inetHost, inetPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture bind(SocketAddress localAddress) {
/* 259 */     validate();
/* 260 */     return doBind((SocketAddress)ObjectUtil.checkNotNull(localAddress, "localAddress"));
/*     */   }
/*     */   
/*     */   private ChannelFuture doBind(final SocketAddress localAddress) {
/* 264 */     final ChannelFuture regFuture = initAndRegister();
/* 265 */     final Channel channel = regFuture.channel();
/* 266 */     if (regFuture.cause() != null) {
/* 267 */       return regFuture;
/*     */     }
/*     */     
/* 270 */     if (regFuture.isDone()) {
/*     */       
/* 272 */       ChannelPromise channelPromise = channel.newPromise();
/* 273 */       doBind0(regFuture, channel, localAddress, channelPromise);
/* 274 */       return (ChannelFuture)channelPromise;
/*     */     } 
/*     */     
/* 277 */     final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
/* 278 */     regFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/* 281 */             Throwable cause = future.cause();
/* 282 */             if (cause != null) {
/*     */ 
/*     */               
/* 285 */               promise.setFailure(cause);
/*     */             }
/*     */             else {
/*     */               
/* 289 */               promise.registered();
/*     */               
/* 291 */               AbstractBootstrap.doBind0(regFuture, channel, localAddress, (ChannelPromise)promise);
/*     */             } 
/*     */           }
/*     */         });
/* 295 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */   
/*     */   final ChannelFuture initAndRegister() {
/* 300 */     Channel channel = null;
/*     */     try {
/* 302 */       channel = (Channel)this.channelFactory.newChannel();
/* 303 */       init(channel);
/* 304 */     } catch (Throwable t) {
/* 305 */       if (channel != null) {
/*     */         
/* 307 */         channel.unsafe().closeForcibly();
/*     */         
/* 309 */         return (ChannelFuture)(new DefaultChannelPromise(channel, (EventExecutor)GlobalEventExecutor.INSTANCE)).setFailure(t);
/*     */       } 
/*     */       
/* 312 */       return (ChannelFuture)(new DefaultChannelPromise((Channel)new FailedChannel(), (EventExecutor)GlobalEventExecutor.INSTANCE)).setFailure(t);
/*     */     } 
/*     */     
/* 315 */     ChannelFuture regFuture = config().group().register(channel);
/* 316 */     if (regFuture.cause() != null) {
/* 317 */       if (channel.isRegistered()) {
/* 318 */         channel.close();
/*     */       } else {
/* 320 */         channel.unsafe().closeForcibly();
/*     */       } 
/*     */     }
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
/* 333 */     return regFuture;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void doBind0(final ChannelFuture regFuture, final Channel channel, final SocketAddress localAddress, final ChannelPromise promise) {
/* 344 */     channel.eventLoop().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 347 */             if (regFuture.isSuccess()) {
/* 348 */               channel.bind(localAddress, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
/*     */             } else {
/* 350 */               promise.setFailure(regFuture.cause());
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public B handler(ChannelHandler handler) {
/* 360 */     this.handler = (ChannelHandler)ObjectUtil.checkNotNull(handler, "handler");
/* 361 */     return self();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final EventLoopGroup group() {
/* 371 */     return this.group;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final Map<ChannelOption<?>, Object> options0() {
/* 381 */     return this.options;
/*     */   }
/*     */   
/*     */   final Map<AttributeKey<?>, Object> attrs0() {
/* 385 */     return this.attrs;
/*     */   }
/*     */   
/*     */   final SocketAddress localAddress() {
/* 389 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   final ChannelFactory<? extends C> channelFactory() {
/* 394 */     return this.channelFactory;
/*     */   }
/*     */   
/*     */   final ChannelHandler handler() {
/* 398 */     return this.handler;
/*     */   }
/*     */   
/*     */   final Map<ChannelOption<?>, Object> options() {
/* 402 */     return copiedMap(this.options);
/*     */   }
/*     */   
/*     */   final Map<AttributeKey<?>, Object> attrs() {
/* 406 */     return copiedMap(this.attrs);
/*     */   }
/*     */   
/*     */   static <K, V> Map<K, V> copiedMap(Map<K, V> map) {
/* 410 */     if (map.isEmpty()) {
/* 411 */       return Collections.emptyMap();
/*     */     }
/* 413 */     return Collections.unmodifiableMap(new HashMap<K, V>(map));
/*     */   }
/*     */   
/*     */   static void setAttributes(Channel channel, Map.Entry<AttributeKey<?>, Object>[] attrs) {
/* 417 */     for (Map.Entry<AttributeKey<?>, Object> e : attrs) {
/*     */       
/* 419 */       AttributeKey<Object> key = (AttributeKey<Object>)e.getKey();
/* 420 */       channel.attr(key).set(e.getValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void setChannelOptions(Channel channel, Map.Entry<ChannelOption<?>, Object>[] options, InternalLogger logger) {
/* 426 */     for (Map.Entry<ChannelOption<?>, Object> e : options) {
/* 427 */       setChannelOption(channel, e.getKey(), e.getValue(), logger);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value, InternalLogger logger) {
/*     */     try {
/* 435 */       if (!channel.config().setOption(option, value)) {
/* 436 */         logger.warn("Unknown channel option '{}' for channel '{}'", option, channel);
/*     */       }
/* 438 */     } catch (Throwable t) {
/* 439 */       logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", new Object[] { option, value, channel, t });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 448 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append('(').append(config()).append(')');
/* 449 */     return buf.toString();
/*     */   }
/*     */   AbstractBootstrap() {}
/*     */   public abstract B clone();
/*     */   
/*     */   abstract void init(Channel paramChannel) throws Exception;
/*     */   
/*     */   public abstract AbstractBootstrapConfig<B, C> config();
/*     */   
/*     */   static final class PendingRegistrationPromise extends DefaultChannelPromise { PendingRegistrationPromise(Channel channel) {
/* 459 */       super(channel);
/*     */     }
/*     */     private volatile boolean registered;
/*     */     void registered() {
/* 463 */       this.registered = true;
/*     */     }
/*     */ 
/*     */     
/*     */     protected EventExecutor executor() {
/* 468 */       if (this.registered)
/*     */       {
/*     */ 
/*     */         
/* 472 */         return super.executor();
/*     */       }
/*     */       
/* 475 */       return (EventExecutor)GlobalEventExecutor.INSTANCE;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\bootstrap\AbstractBootstrap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */