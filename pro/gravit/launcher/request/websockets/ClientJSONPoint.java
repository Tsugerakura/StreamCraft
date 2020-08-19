/*     */ package pro.gravit.launcher.request.websockets;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import javax.net.ssl.SSLException;
/*     */ import pro.gravit.repackage.io.netty.bootstrap.Bootstrap;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInitializer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.nio.NioEventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.nio.NioSocketChannel;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.EmptyHttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpClientCodec;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectAggregator;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.WebSocketVersion;
/*     */ import pro.gravit.repackage.io.netty.handler.ssl.SslContext;
/*     */ import pro.gravit.repackage.io.netty.handler.ssl.SslContextBuilder;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public abstract class ClientJSONPoint
/*     */ {
/*     */   private final URI uri;
/*     */   protected Channel ch;
/*  32 */   private static final EventLoopGroup group = (EventLoopGroup)new NioEventLoopGroup();
/*     */   protected WebSocketClientHandler webSocketClientHandler;
/*     */   protected Bootstrap bootstrap;
/*     */   protected boolean ssl;
/*     */   protected int port;
/*     */   public boolean isClosed;
/*     */   
/*     */   public ClientJSONPoint(String uri) throws SSLException {
/*  40 */     this(URI.create(uri)); } public ClientJSONPoint(final URI uri) throws SSLException {
/*     */     final SslContext sslCtx;
/*     */     this.bootstrap = new Bootstrap();
/*     */     this.ssl = false;
/*  44 */     this.uri = uri;
/*  45 */     String protocol = uri.getScheme();
/*  46 */     if (!"ws".equals(protocol) && !"wss".equals(protocol)) {
/*  47 */       throw new IllegalArgumentException("Unsupported protocol: " + protocol);
/*     */     }
/*  49 */     if ("wss".equals(protocol)) {
/*  50 */       this.ssl = true;
/*     */     }
/*  52 */     if (uri.getPort() == -1)
/*  53 */     { if ("ws".equals(protocol)) { this.port = 80; }
/*  54 */       else { this.port = 443; }  }
/*  55 */     else { this.port = uri.getPort(); }
/*     */     
/*  57 */     if (this.ssl)
/*  58 */     { sslCtx = SslContextBuilder.forClient().build(); }
/*  59 */     else { sslCtx = null; }
/*  60 */      ((Bootstrap)((Bootstrap)this.bootstrap.group(group))
/*  61 */       .channel(NioSocketChannel.class))
/*  62 */       .handler((ChannelHandler)new ChannelInitializer<SocketChannel>()
/*     */         {
/*     */           public void initChannel(SocketChannel ch) throws Exception {
/*  65 */             ChannelPipeline pipeline = ch.pipeline();
/*  66 */             if (sslCtx != null) {
/*  67 */               pipeline.addLast(new ChannelHandler[] { (ChannelHandler)this.val$sslCtx.newHandler(ch.alloc(), this.val$uri.getHost(), this.this$0.port) });
/*     */             }
/*  69 */             pipeline.addLast("http-codec", (ChannelHandler)new HttpClientCodec());
/*  70 */             pipeline.addLast("aggregator", (ChannelHandler)new HttpObjectAggregator(65536));
/*  71 */             pipeline.addLast("ws-handler", (ChannelHandler)ClientJSONPoint.this.webSocketClientHandler);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void open() throws Exception {
/*  78 */     this
/*     */       
/*  80 */       .webSocketClientHandler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(this.uri, WebSocketVersion.V13, null, false, (HttpHeaders)EmptyHttpHeaders.INSTANCE, 12800000), this);
/*     */     
/*  82 */     this.ch = this.bootstrap.connect(this.uri.getHost(), this.port).sync().channel();
/*  83 */     this.webSocketClientHandler.handshakeFuture().sync();
/*     */   }
/*     */   
/*     */   public ChannelFuture send(String text) {
/*  87 */     LogHelper.dev("Send: %s", new Object[] { text });
/*  88 */     return this.ch.writeAndFlush(new TextWebSocketFrame(text), this.ch.voidPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   abstract void onMessage(String paramString) throws Exception;
/*     */   
/*     */   abstract void onDisconnect() throws Exception;
/*     */   
/*     */   abstract void onOpen() throws Exception;
/*     */   
/*     */   public void close() throws InterruptedException {
/*  99 */     this.isClosed = true;
/* 100 */     if (this.ch != null && this.ch.isActive()) {
/* 101 */       this.ch.writeAndFlush(new CloseWebSocketFrame(), this.ch.voidPromise());
/* 102 */       this.ch.closeFuture().sync();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void eval(String text) throws IOException {
/* 109 */     this.ch.writeAndFlush(new TextWebSocketFrame(text), this.ch.voidPromise());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\websockets\ClientJSONPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */