/*     */ package pro.gravit.repackage.io.netty.channel.socket.oio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketTimeoutException;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ConnectTimeoutException;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.oio.OioByteStreamChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ServerSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ @Deprecated
/*     */ public class OioSocketChannel
/*     */   extends OioByteStreamChannel
/*     */   implements SocketChannel
/*     */ {
/*  48 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSocketChannel.class);
/*     */ 
/*     */   
/*     */   private final Socket socket;
/*     */   
/*     */   private final OioSocketChannelConfig config;
/*     */ 
/*     */   
/*     */   public OioSocketChannel() {
/*  57 */     this(new Socket());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OioSocketChannel(Socket socket) {
/*  66 */     this((Channel)null, socket);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OioSocketChannel(Channel parent, Socket socket) {
/*  77 */     super(parent);
/*  78 */     this.socket = socket;
/*  79 */     this.config = new DefaultOioSocketChannelConfig(this, socket);
/*     */     
/*  81 */     boolean success = false;
/*     */     try {
/*  83 */       if (socket.isConnected()) {
/*  84 */         activate(socket.getInputStream(), socket.getOutputStream());
/*     */       }
/*  86 */       socket.setSoTimeout(1000);
/*  87 */       success = true;
/*  88 */     } catch (Exception e) {
/*  89 */       throw new ChannelException("failed to initialize a socket", e);
/*     */     } finally {
/*  91 */       if (!success) {
/*     */         try {
/*  93 */           socket.close();
/*  94 */         } catch (IOException e) {
/*  95 */           logger.warn("Failed to close a socket.", e);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocketChannel parent() {
/* 103 */     return (ServerSocketChannel)super.parent();
/*     */   }
/*     */ 
/*     */   
/*     */   public OioSocketChannelConfig config() {
/* 108 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 113 */     return !this.socket.isClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 118 */     return (!this.socket.isClosed() && this.socket.isConnected());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutputShutdown() {
/* 123 */     return (this.socket.isOutputShutdown() || !isActive());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInputShutdown() {
/* 128 */     return (this.socket.isInputShutdown() || !isActive());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isShutdown() {
/* 133 */     return ((this.socket.isInputShutdown() && this.socket.isOutputShutdown()) || !isActive());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void doShutdownOutput() throws Exception {
/* 139 */     shutdownOutput0();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownOutput() {
/* 144 */     return shutdownOutput(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownInput() {
/* 149 */     return shutdownInput(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdown() {
/* 154 */     return shutdown(newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadBytes(ByteBuf buf) throws Exception {
/* 159 */     if (this.socket.isClosed()) {
/* 160 */       return -1;
/*     */     }
/*     */     try {
/* 163 */       return super.doReadBytes(buf);
/* 164 */     } catch (SocketTimeoutException ignored) {
/* 165 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownOutput(final ChannelPromise promise) {
/* 171 */     EventLoop loop = eventLoop();
/* 172 */     if (loop.inEventLoop()) {
/* 173 */       shutdownOutput0(promise);
/*     */     } else {
/* 175 */       loop.execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 178 */               OioSocketChannel.this.shutdownOutput0(promise);
/*     */             }
/*     */           });
/*     */     } 
/* 182 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void shutdownOutput0(ChannelPromise promise) {
/*     */     try {
/* 187 */       shutdownOutput0();
/* 188 */       promise.setSuccess();
/* 189 */     } catch (Throwable t) {
/* 190 */       promise.setFailure(t);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void shutdownOutput0() throws IOException {
/* 195 */     this.socket.shutdownOutput();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdownInput(final ChannelPromise promise) {
/* 200 */     EventLoop loop = eventLoop();
/* 201 */     if (loop.inEventLoop()) {
/* 202 */       shutdownInput0(promise);
/*     */     } else {
/* 204 */       loop.execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 207 */               OioSocketChannel.this.shutdownInput0(promise);
/*     */             }
/*     */           });
/*     */     } 
/* 211 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void shutdownInput0(ChannelPromise promise) {
/*     */     try {
/* 216 */       this.socket.shutdownInput();
/* 217 */       promise.setSuccess();
/* 218 */     } catch (Throwable t) {
/* 219 */       promise.setFailure(t);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture shutdown(final ChannelPromise promise) {
/* 225 */     ChannelFuture shutdownOutputFuture = shutdownOutput();
/* 226 */     if (shutdownOutputFuture.isDone()) {
/* 227 */       shutdownOutputDone(shutdownOutputFuture, promise);
/*     */     } else {
/* 229 */       shutdownOutputFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
/* 232 */               OioSocketChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
/*     */             }
/*     */           });
/*     */     } 
/* 236 */     return (ChannelFuture)promise;
/*     */   }
/*     */   
/*     */   private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
/* 240 */     ChannelFuture shutdownInputFuture = shutdownInput();
/* 241 */     if (shutdownInputFuture.isDone()) {
/* 242 */       shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
/*     */     } else {
/* 244 */       shutdownInputFuture.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
/* 247 */               OioSocketChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
/* 256 */     Throwable shutdownOutputCause = shutdownOutputFuture.cause();
/* 257 */     Throwable shutdownInputCause = shutdownInputFuture.cause();
/* 258 */     if (shutdownOutputCause != null) {
/* 259 */       if (shutdownInputCause != null) {
/* 260 */         logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
/*     */       }
/*     */       
/* 263 */       promise.setFailure(shutdownOutputCause);
/* 264 */     } else if (shutdownInputCause != null) {
/* 265 */       promise.setFailure(shutdownInputCause);
/*     */     } else {
/* 267 */       promise.setSuccess();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress localAddress() {
/* 273 */     return (InetSocketAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress remoteAddress() {
/* 278 */     return (InetSocketAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/* 283 */     return this.socket.getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress remoteAddress0() {
/* 288 */     return this.socket.getRemoteSocketAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/* 293 */     SocketUtils.bind(this.socket, localAddress);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
/* 299 */     if (localAddress != null) {
/* 300 */       SocketUtils.bind(this.socket, localAddress);
/*     */     }
/*     */     
/* 303 */     boolean success = false;
/*     */     try {
/* 305 */       SocketUtils.connect(this.socket, remoteAddress, config().getConnectTimeoutMillis());
/* 306 */       activate(this.socket.getInputStream(), this.socket.getOutputStream());
/* 307 */       success = true;
/* 308 */     } catch (SocketTimeoutException e) {
/* 309 */       ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
/* 310 */       cause.setStackTrace(e.getStackTrace());
/* 311 */       throw cause;
/*     */     } finally {
/* 313 */       if (!success) {
/* 314 */         doClose();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDisconnect() throws Exception {
/* 321 */     doClose();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 326 */     this.socket.close();
/*     */   }
/*     */   
/*     */   protected boolean checkInputShutdown() {
/* 330 */     if (isInputShutdown()) {
/*     */       try {
/* 332 */         Thread.sleep(config().getSoTimeout());
/* 333 */       } catch (Throwable throwable) {}
/*     */ 
/*     */       
/* 336 */       return true;
/*     */     } 
/* 338 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setReadPending(boolean readPending) {
/* 344 */     super.setReadPending(readPending);
/*     */   }
/*     */   
/*     */   final void clearReadPending0() {
/* 348 */     clearReadPending();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\oio\OioSocketChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */