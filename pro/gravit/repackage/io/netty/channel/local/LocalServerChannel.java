/*     */ package pro.gravit.repackage.io.netty.channel.local;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractServerChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.DefaultChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.PreferHeapByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.SingleThreadEventExecutor;
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
/*     */ public class LocalServerChannel
/*     */   extends AbstractServerChannel
/*     */ {
/*  38 */   private final ChannelConfig config = (ChannelConfig)new DefaultChannelConfig((Channel)this);
/*  39 */   private final Queue<Object> inboundBuffer = new ArrayDeque();
/*  40 */   private final Runnable shutdownHook = new Runnable()
/*     */     {
/*     */       public void run() {
/*  43 */         LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
/*     */       }
/*     */     };
/*     */   
/*     */   private volatile int state;
/*     */   private volatile LocalAddress localAddress;
/*     */   private volatile boolean acceptInProgress;
/*     */   
/*     */   public LocalServerChannel() {
/*  52 */     config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator(this.config.getAllocator()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig config() {
/*  57 */     return this.config;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalAddress localAddress() {
/*  62 */     return (LocalAddress)super.localAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalAddress remoteAddress() {
/*  67 */     return (LocalAddress)super.remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/*  72 */     return (this.state < 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/*  77 */     return (this.state == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCompatible(EventLoop loop) {
/*  82 */     return loop instanceof pro.gravit.repackage.io.netty.channel.SingleThreadEventLoop;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketAddress localAddress0() {
/*  87 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doRegister() throws Exception {
/*  92 */     ((SingleThreadEventExecutor)eventLoop()).addShutdownHook(this.shutdownHook);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBind(SocketAddress localAddress) throws Exception {
/*  97 */     this.localAddress = LocalChannelRegistry.register((Channel)this, this.localAddress, localAddress);
/*  98 */     this.state = 1;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 103 */     if (this.state <= 1) {
/*     */       
/* 105 */       if (this.localAddress != null) {
/* 106 */         LocalChannelRegistry.unregister(this.localAddress);
/* 107 */         this.localAddress = null;
/*     */       } 
/* 109 */       this.state = 2;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doDeregister() throws Exception {
/* 115 */     ((SingleThreadEventExecutor)eventLoop()).removeShutdownHook(this.shutdownHook);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {
/* 120 */     if (this.acceptInProgress) {
/*     */       return;
/*     */     }
/*     */     
/* 124 */     Queue<Object> inboundBuffer = this.inboundBuffer;
/* 125 */     if (inboundBuffer.isEmpty()) {
/* 126 */       this.acceptInProgress = true;
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     readInbound();
/*     */   }
/*     */   
/*     */   LocalChannel serve(LocalChannel peer) {
/* 134 */     final LocalChannel child = newLocalChannel(peer);
/* 135 */     if (eventLoop().inEventLoop()) {
/* 136 */       serve0(child);
/*     */     } else {
/* 138 */       eventLoop().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 141 */               LocalServerChannel.this.serve0(child);
/*     */             }
/*     */           });
/*     */     } 
/* 145 */     return child;
/*     */   }
/*     */   
/*     */   private void readInbound() {
/* 149 */     RecvByteBufAllocator.Handle handle = unsafe().recvBufAllocHandle();
/* 150 */     handle.reset(config());
/* 151 */     ChannelPipeline pipeline = pipeline();
/*     */     do {
/* 153 */       Object m = this.inboundBuffer.poll();
/* 154 */       if (m == null) {
/*     */         break;
/*     */       }
/* 157 */       pipeline.fireChannelRead(m);
/* 158 */     } while (handle.continueReading());
/*     */     
/* 160 */     pipeline.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected LocalChannel newLocalChannel(LocalChannel peer) {
/* 168 */     return new LocalChannel(this, peer);
/*     */   }
/*     */   
/*     */   private void serve0(LocalChannel child) {
/* 172 */     this.inboundBuffer.add(child);
/* 173 */     if (this.acceptInProgress) {
/* 174 */       this.acceptInProgress = false;
/*     */       
/* 176 */       readInbound();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\local\LocalServerChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */