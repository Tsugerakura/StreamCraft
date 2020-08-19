/*     */ package pro.gravit.repackage.io.netty.channel.socket.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.AbstractNioMessageChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.DefaultServerSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ public class NioServerSocketChannel
/*     */   extends AbstractNioMessageChannel
/*     */   implements ServerSocketChannel
/*     */ {
/*  49 */   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
/*  50 */   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
/*     */   
/*  52 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private final ServerSocketChannelConfig config;
/*     */ 
/*     */ 
/*     */   
/*     */   private static ServerSocketChannel newSocket(SelectorProvider provider) {
/*     */     try {
/*  62 */       return provider.openServerSocketChannel();
/*  63 */     } catch (IOException e) {
/*  64 */       throw new ChannelException("Failed to open a server socket.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioServerSocketChannel() {
/*  75 */     this(newSocket(DEFAULT_SELECTOR_PROVIDER));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioServerSocketChannel(SelectorProvider provider) {
/*  82 */     this(newSocket(provider));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioServerSocketChannel(ServerSocketChannel channel) {
/*  89 */     super(null, channel, 16);
/*  90 */     this.config = (ServerSocketChannelConfig)new NioServerSocketChannelConfig(this, javaChannel().socket());
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/*  95 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 100 */     return METADATA;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannelConfig config() {
/* 105 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 112 */     return (isOpen() && javaChannel().socket().isBound());
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ServerSocketChannel javaChannel() {
/* 122 */     return (ServerSocketChannel)super.javaChannel();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 127 */     return SocketUtils.localSocketAddress(javaChannel().socket());
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 133 */     if (PlatformDependent.javaVersion() >= 7) {
/* 134 */       javaChannel().bind(localAddress, this.config.getBacklog());
/*     */     } else {
/* 136 */       javaChannel().socket().bind(localAddress, this.config.getBacklog());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 142 */     javaChannel().close();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadMessages(List<Object> buf) throws Exception {
/* 147 */     SocketChannel ch = SocketUtils.accept(javaChannel());
/*     */     
/*     */     try {
/* 150 */       if (ch != null) {
/* 151 */         buf.add(new NioSocketChannel((Channel)this, ch));
/* 152 */         return 1;
/*     */       } 
/* 154 */     } catch (Throwable t) {
/* 155 */       logger.warn("Failed to create a new channel from an accepted socket.", t);
/*     */       
/*     */       try {
/* 158 */         ch.close();
/* 159 */       } catch (Throwable t2) {
/* 160 */         logger.warn("Failed to close a socket.", t2);
/*     */       } 
/*     */     } 
/*     */     
/* 164 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 171 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doFinishConnect() throws Exception {
/* 176 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 181 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 186 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
/* 191 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected final Object filterOutboundMessage(Object msg) throws Exception {
/* 196 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig {
/*     */     private NioServerSocketChannelConfig(NioServerSocketChannel channel, ServerSocket javaSocket) {
/* 201 */       super(channel, javaSocket);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void autoReadCleared() {
/* 206 */       NioServerSocketChannel.this.clearReadPending();
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> boolean setOption(ChannelOption<T> option, T value) {
/* 211 */       if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
/* 212 */         return NioChannelOption.setOption(jdkChannel(), (NioChannelOption<T>)option, value);
/*     */       }
/* 214 */       return super.setOption(option, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> T getOption(ChannelOption<T> option) {
/* 219 */       if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
/* 220 */         return NioChannelOption.getOption(jdkChannel(), (NioChannelOption<T>)option);
/*     */       }
/* 222 */       return (T)super.getOption(option);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<ChannelOption<?>, Object> getOptions() {
/* 227 */       if (PlatformDependent.javaVersion() >= 7) {
/* 228 */         return getOptions(super.getOptions(), NioChannelOption.getOptions(jdkChannel()));
/*     */       }
/* 230 */       return super.getOptions();
/*     */     }
/*     */     
/*     */     private ServerSocketChannel jdkChannel() {
/* 234 */       return ((NioServerSocketChannel)this.channel).javaChannel();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean closeOnReadError(Throwable cause) {
/* 241 */     return super.closeOnReadError(cause);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\nio\NioServerSocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */