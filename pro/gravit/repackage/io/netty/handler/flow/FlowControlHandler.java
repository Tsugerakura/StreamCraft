/*     */ package pro.gravit.repackage.io.netty.handler.flow;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
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
/*     */ public class FlowControlHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  69 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(FlowControlHandler.class);
/*     */   
/*     */   private final boolean releaseMessages;
/*     */   
/*     */   private RecyclableArrayDeque queue;
/*     */   
/*     */   private ChannelConfig config;
/*     */   
/*     */   private boolean shouldConsume;
/*     */   
/*     */   public FlowControlHandler() {
/*  80 */     this(true);
/*     */   }
/*     */   
/*     */   public FlowControlHandler(boolean releaseMessages) {
/*  84 */     this.releaseMessages = releaseMessages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isQueueEmpty() {
/*  92 */     return (this.queue == null || this.queue.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void destroy() {
/*  99 */     if (this.queue != null) {
/*     */       
/* 101 */       if (!this.queue.isEmpty()) {
/* 102 */         logger.trace("Non-empty queue: {}", this.queue);
/*     */         
/* 104 */         if (this.releaseMessages) {
/*     */           Object msg;
/* 106 */           while ((msg = this.queue.poll()) != null) {
/* 107 */             ReferenceCountUtil.safeRelease(msg);
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 112 */       this.queue.recycle();
/* 113 */       this.queue = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 119 */     this.config = ctx.channel().config();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 124 */     destroy();
/* 125 */     ctx.fireChannelInactive();
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 130 */     if (dequeue(ctx, 1) == 0) {
/*     */ 
/*     */ 
/*     */       
/* 134 */       this.shouldConsume = true;
/* 135 */       ctx.read();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 141 */     if (this.queue == null) {
/* 142 */       this.queue = RecyclableArrayDeque.newInstance();
/*     */     }
/*     */     
/* 145 */     this.queue.offer(msg);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     int minConsume = this.shouldConsume ? 1 : 0;
/* 151 */     this.shouldConsume = false;
/*     */     
/* 153 */     dequeue(ctx, minConsume);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 158 */     if (isQueueEmpty()) {
/* 159 */       ctx.fireChannelReadComplete();
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int dequeue(ChannelHandlerContext ctx, int minConsume) {
/* 180 */     int consumed = 0;
/*     */ 
/*     */ 
/*     */     
/* 184 */     while (this.queue != null && (consumed < minConsume || this.config.isAutoRead())) {
/* 185 */       Object msg = this.queue.poll();
/* 186 */       if (msg == null) {
/*     */         break;
/*     */       }
/*     */       
/* 190 */       consumed++;
/* 191 */       ctx.fireChannelRead(msg);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 197 */     if (this.queue != null && this.queue.isEmpty()) {
/* 198 */       this.queue.recycle();
/* 199 */       this.queue = null;
/*     */       
/* 201 */       if (consumed > 0) {
/* 202 */         ctx.fireChannelReadComplete();
/*     */       }
/*     */     } 
/*     */     
/* 206 */     return consumed;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class RecyclableArrayDeque
/*     */     extends ArrayDeque<Object>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     
/*     */     private static final int DEFAULT_NUM_ELEMENTS = 2;
/*     */ 
/*     */ 
/*     */     
/* 221 */     private static final ObjectPool<RecyclableArrayDeque> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<RecyclableArrayDeque>()
/*     */         {
/*     */           public FlowControlHandler.RecyclableArrayDeque newObject(ObjectPool.Handle<FlowControlHandler.RecyclableArrayDeque> handle)
/*     */           {
/* 225 */             return new FlowControlHandler.RecyclableArrayDeque(2, handle);
/*     */           }
/*     */         });
/*     */     
/*     */     public static RecyclableArrayDeque newInstance() {
/* 230 */       return (RecyclableArrayDeque)RECYCLER.get();
/*     */     }
/*     */     
/*     */     private final ObjectPool.Handle<RecyclableArrayDeque> handle;
/*     */     
/*     */     private RecyclableArrayDeque(int numElements, ObjectPool.Handle<RecyclableArrayDeque> handle) {
/* 236 */       super(numElements);
/* 237 */       this.handle = handle;
/*     */     }
/*     */     
/*     */     public void recycle() {
/* 241 */       clear();
/* 242 */       this.handle.recycle(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\flow\FlowControlHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */