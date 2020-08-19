/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
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
/*     */ public class ThreadPerChannelEventLoop
/*     */   extends SingleThreadEventLoop
/*     */ {
/*     */   private final ThreadPerChannelEventLoopGroup parent;
/*     */   private Channel ch;
/*     */   
/*     */   public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent) {
/*  31 */     super(parent, parent.executor, true);
/*  32 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture register(ChannelPromise promise) {
/*  37 */     return super.register(promise).addListener(new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/*  40 */             if (future.isSuccess()) {
/*  41 */               ThreadPerChannelEventLoop.this.ch = future.channel();
/*     */             } else {
/*  43 */               ThreadPerChannelEventLoop.this.deregister();
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFuture register(Channel channel, ChannelPromise promise) {
/*  52 */     return super.register(channel, promise).addListener(new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture future) throws Exception {
/*  55 */             if (future.isSuccess()) {
/*  56 */               ThreadPerChannelEventLoop.this.ch = future.channel();
/*     */             } else {
/*  58 */               ThreadPerChannelEventLoop.this.deregister();
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void run() {
/*     */     while (true) {
/*  67 */       Runnable task = takeTask();
/*  68 */       if (task != null) {
/*  69 */         task.run();
/*  70 */         updateLastExecutionTime();
/*     */       } 
/*     */       
/*  73 */       Channel ch = this.ch;
/*  74 */       if (isShuttingDown()) {
/*  75 */         if (ch != null) {
/*  76 */           ch.unsafe().close(ch.unsafe().voidPromise());
/*     */         }
/*  78 */         if (confirmShutdown())
/*     */           break; 
/*     */         continue;
/*     */       } 
/*  82 */       if (ch != null)
/*     */       {
/*  84 */         if (!ch.isRegistered()) {
/*  85 */           runAllTasks();
/*  86 */           deregister();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deregister() {
/*  94 */     this.ch = null;
/*  95 */     this.parent.activeChildren.remove(this);
/*  96 */     this.parent.idleChildren.add(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int registeredChannels() {
/* 101 */     return 1;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ThreadPerChannelEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */