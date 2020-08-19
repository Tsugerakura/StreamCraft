/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.oio.AbstractOioMessageChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
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
/*     */ 
/*     */ @Deprecated
/*     */ public class OioServerSocketChannel
/*     */   extends AbstractOioMessageChannel
/*     */   implements ServerSocketChannel
/*     */ {
/*  50 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
/*     */   
/*  52 */   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 1);
/*     */   
/*     */   private static ServerSocket newServerSocket() {
/*     */     try {
/*  56 */       return new ServerSocket();
/*  57 */     } catch (IOException e) {
/*  58 */       throw new ChannelException("failed to create a server socket", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   final ServerSocket socket;
/*  63 */   final Lock shutdownLock = new ReentrantLock();
/*     */ 
/*     */   
/*     */   private final OioServerSocketChannelConfig config;
/*     */ 
/*     */   
/*     */   public OioServerSocketChannel() {
/*  70 */     this(newServerSocket());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OioServerSocketChannel(ServerSocket socket) {
/*  79 */     super(null);
/*  80 */     ObjectUtil.checkNotNull(socket, "socket");
/*     */     
/*  82 */     boolean success = false;
/*     */     try {
/*  84 */       socket.setSoTimeout(1000);
/*  85 */       success = true;
/*  86 */     } catch (IOException e) {
/*  87 */       throw new ChannelException("Failed to set the server socket timeout.", e);
/*     */     } finally {
/*     */       
/*  90 */       if (!success) {
/*     */         try {
/*  92 */           socket.close();
/*  93 */         } catch (IOException e) {
/*  94 */           if (logger.isWarnEnabled()) {
/*  95 */             logger.warn("Failed to close a partially initialized socket.", e);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 101 */     this.socket = socket;
/* 102 */     this.config = new DefaultOioServerSocketChannelConfig(this, socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/* 107 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/* 112 */     return METADATA;
/*     */   }
/*     */ 
/*     */   
/*     */   public OioServerSocketChannelConfig config() {
/* 117 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 127 */     return !this.socket.isClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 132 */     return (isOpen() && this.socket.isBound());
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 137 */     return SocketUtils.localSocketAddress(this.socket);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 142 */     this.socket.bind(localAddress, this.config.getBacklog());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 147 */     this.socket.close();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadMessages(List<Object> buf) throws Exception {
/* 152 */     if (this.socket.isClosed()) {
/* 153 */       return -1;
/*     */     }
/*     */     
/*     */     try {
/* 157 */       Socket s = this.socket.accept();
/*     */       try {
/* 159 */         buf.add(new OioSocketChannel((Channel)this, s));
/* 160 */         return 1;
/* 161 */       } catch (Throwable t) {
/* 162 */         logger.warn("Failed to create a new channel from an accepted socket.", t);
/*     */         try {
/* 164 */           s.close();
/* 165 */         } catch (Throwable t2) {
/* 166 */           logger.warn("Failed to close a socket.", t2);
/*     */         } 
/*     */       } 
/* 169 */     } catch (SocketTimeoutException socketTimeoutException) {}
/*     */ 
/*     */     
/* 172 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/* 177 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object filterOutboundMessage(Object msg) throws Exception {
/* 182 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 188 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 193 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 198 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setReadPending(boolean readPending) {
/* 204 */     super.setReadPending(readPending);
/*     */   }
/*     */   
/*     */   final void clearReadPending0() {
/* 208 */     clearReadPending();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\OioServerSocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */