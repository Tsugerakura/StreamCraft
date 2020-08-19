/*     */ package pro.gravit.repackage.io.netty.channel.oio;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractOioChannel
/*     */   extends AbstractChannel
/*     */ {
/*     */   protected static final int SO_TIMEOUT = 1000;
/*     */   boolean readPending;
/*     */   
/*  37 */   private final Runnable readTask = new Runnable()
/*     */     {
/*     */       public void run() {
/*  40 */         AbstractOioChannel.this.doRead();
/*     */       }
/*     */     };
/*  43 */   private final Runnable clearReadPendingRunnable = new Runnable()
/*     */     {
/*     */       public void run() {
/*  46 */         AbstractOioChannel.this.readPending = false;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractOioChannel(Channel parent) {
/*  54 */     super(parent);
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractChannel.AbstractUnsafe newUnsafe() {
/*  59 */     return new DefaultOioUnsafe();
/*     */   }
/*     */   private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe { private DefaultOioUnsafe() {
/*  62 */       super(AbstractOioChannel.this);
/*     */     }
/*     */ 
/*     */     
/*     */     public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
/*  67 */       if (!promise.setUncancellable() || !ensureOpen(promise)) {
/*     */         return;
/*     */       }
/*     */       
/*     */       try {
/*  72 */         boolean wasActive = AbstractOioChannel.this.isActive();
/*  73 */         AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
/*     */ 
/*     */ 
/*     */         
/*  77 */         boolean active = AbstractOioChannel.this.isActive();
/*     */         
/*  79 */         safeSetSuccess(promise);
/*  80 */         if (!wasActive && active) {
/*  81 */           AbstractOioChannel.this.pipeline().fireChannelActive();
/*     */         }
/*  83 */       } catch (Throwable t) {
/*  84 */         safeSetFailure(promise, annotateConnectException(t, remoteAddress));
/*  85 */         closeIfClosed();
/*     */       } 
/*     */     } }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isCompatible(EventLoop loop) {
/*  92 */     return loop instanceof pro.gravit.repackage.io.netty.channel.ThreadPerChannelEventLoop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void doConnect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2) throws Exception;
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {
/* 103 */     if (this.readPending) {
/*     */       return;
/*     */     }
/*     */     
/* 107 */     this.readPending = true;
/* 108 */     eventLoop().execute(this.readTask);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void doRead();
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected boolean isReadPending() {
/* 119 */     return this.readPending;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void setReadPending(final boolean readPending) {
/* 128 */     if (isRegistered()) {
/* 129 */       EventLoop eventLoop = eventLoop();
/* 130 */       if (eventLoop.inEventLoop()) {
/* 131 */         this.readPending = readPending;
/*     */       } else {
/* 133 */         eventLoop.execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 136 */                 AbstractOioChannel.this.readPending = readPending;
/*     */               }
/*     */             });
/*     */       } 
/*     */     } else {
/* 141 */       this.readPending = readPending;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void clearReadPending() {
/* 149 */     if (isRegistered()) {
/* 150 */       EventLoop eventLoop = eventLoop();
/* 151 */       if (eventLoop.inEventLoop()) {
/* 152 */         this.readPending = false;
/*     */       } else {
/* 154 */         eventLoop.execute(this.clearReadPendingRunnable);
/*     */       } 
/*     */     } else {
/*     */       
/* 158 */       this.readPending = false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\oio\AbstractOioChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */