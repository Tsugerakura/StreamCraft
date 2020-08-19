/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.PromiseCombiner;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ public final class PendingWriteQueue
/*     */ {
/*  33 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   private static final int PENDING_WRITE_OVERHEAD = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.transport.pendingWriteSizeOverhead", 64);
/*     */   
/*     */   private final ChannelHandlerContext ctx;
/*     */   
/*     */   private final PendingBytesTracker tracker;
/*     */   
/*     */   private PendingWrite head;
/*     */   private PendingWrite tail;
/*     */   private int size;
/*     */   private long bytes;
/*     */   
/*     */   public PendingWriteQueue(ChannelHandlerContext ctx) {
/*  51 */     this.tracker = PendingBytesTracker.newTracker(ctx.channel());
/*  52 */     this.ctx = ctx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  59 */     assert this.ctx.executor().inEventLoop();
/*  60 */     return (this.head == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  67 */     assert this.ctx.executor().inEventLoop();
/*  68 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long bytes() {
/*  76 */     assert this.ctx.executor().inEventLoop();
/*  77 */     return this.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int size(Object msg) {
/*  83 */     int messageSize = this.tracker.size(msg);
/*  84 */     if (messageSize < 0)
/*     */     {
/*  86 */       messageSize = 0;
/*     */     }
/*  88 */     return messageSize + PENDING_WRITE_OVERHEAD;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Object msg, ChannelPromise promise) {
/*  95 */     assert this.ctx.executor().inEventLoop();
/*  96 */     ObjectUtil.checkNotNull(msg, "msg");
/*  97 */     ObjectUtil.checkNotNull(promise, "promise");
/*     */ 
/*     */     
/* 100 */     int messageSize = size(msg);
/*     */     
/* 102 */     PendingWrite write = PendingWrite.newInstance(msg, messageSize, promise);
/* 103 */     PendingWrite currentTail = this.tail;
/* 104 */     if (currentTail == null) {
/* 105 */       this.tail = this.head = write;
/*     */     } else {
/* 107 */       currentTail.next = write;
/* 108 */       this.tail = write;
/*     */     } 
/* 110 */     this.size++;
/* 111 */     this.bytes += messageSize;
/* 112 */     this.tracker.incrementPendingOutboundBytes(write.size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture removeAndWriteAll() {
/* 123 */     assert this.ctx.executor().inEventLoop();
/*     */     
/* 125 */     if (isEmpty()) {
/* 126 */       return null;
/*     */     }
/*     */     
/* 129 */     ChannelPromise p = this.ctx.newPromise();
/* 130 */     PromiseCombiner combiner = new PromiseCombiner(this.ctx.executor());
/*     */ 
/*     */     
/*     */     try {
/* 134 */       for (PendingWrite write = this.head; write != null; write = this.head) {
/* 135 */         this.head = this.tail = null;
/* 136 */         this.size = 0;
/* 137 */         this.bytes = 0L;
/*     */         
/* 139 */         while (write != null) {
/* 140 */           PendingWrite next = write.next;
/* 141 */           Object msg = write.msg;
/* 142 */           ChannelPromise promise = write.promise;
/* 143 */           recycle(write, false);
/* 144 */           if (!(promise instanceof VoidChannelPromise)) {
/* 145 */             combiner.add(promise);
/*     */           }
/* 147 */           this.ctx.write(msg, promise);
/* 148 */           write = next;
/*     */         } 
/*     */       } 
/* 151 */       combiner.finish(p);
/* 152 */     } catch (Throwable cause) {
/* 153 */       p.setFailure(cause);
/*     */     } 
/* 155 */     assertEmpty();
/* 156 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAndFailAll(Throwable cause) {
/* 164 */     assert this.ctx.executor().inEventLoop();
/* 165 */     ObjectUtil.checkNotNull(cause, "cause");
/*     */ 
/*     */     
/* 168 */     for (PendingWrite write = this.head; write != null; write = this.head) {
/* 169 */       this.head = this.tail = null;
/* 170 */       this.size = 0;
/* 171 */       this.bytes = 0L;
/* 172 */       while (write != null) {
/* 173 */         PendingWrite next = write.next;
/* 174 */         ReferenceCountUtil.safeRelease(write.msg);
/* 175 */         ChannelPromise promise = write.promise;
/* 176 */         recycle(write, false);
/* 177 */         safeFail(promise, cause);
/* 178 */         write = next;
/*     */       } 
/*     */     } 
/* 181 */     assertEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAndFail(Throwable cause) {
/* 189 */     assert this.ctx.executor().inEventLoop();
/* 190 */     ObjectUtil.checkNotNull(cause, "cause");
/*     */     
/* 192 */     PendingWrite write = this.head;
/* 193 */     if (write == null) {
/*     */       return;
/*     */     }
/* 196 */     ReferenceCountUtil.safeRelease(write.msg);
/* 197 */     ChannelPromise promise = write.promise;
/* 198 */     safeFail(promise, cause);
/* 199 */     recycle(write, true);
/*     */   }
/*     */   
/*     */   private void assertEmpty() {
/* 203 */     assert this.tail == null && this.head == null && this.size == 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture removeAndWrite() {
/* 214 */     assert this.ctx.executor().inEventLoop();
/* 215 */     PendingWrite write = this.head;
/* 216 */     if (write == null) {
/* 217 */       return null;
/*     */     }
/* 219 */     Object msg = write.msg;
/* 220 */     ChannelPromise promise = write.promise;
/* 221 */     recycle(write, true);
/* 222 */     return this.ctx.write(msg, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelPromise remove() {
/* 232 */     assert this.ctx.executor().inEventLoop();
/* 233 */     PendingWrite write = this.head;
/* 234 */     if (write == null) {
/* 235 */       return null;
/*     */     }
/* 237 */     ChannelPromise promise = write.promise;
/* 238 */     ReferenceCountUtil.safeRelease(write.msg);
/* 239 */     recycle(write, true);
/* 240 */     return promise;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object current() {
/* 247 */     assert this.ctx.executor().inEventLoop();
/* 248 */     PendingWrite write = this.head;
/* 249 */     if (write == null) {
/* 250 */       return null;
/*     */     }
/* 252 */     return write.msg;
/*     */   }
/*     */   
/*     */   private void recycle(PendingWrite write, boolean update) {
/* 256 */     PendingWrite next = write.next;
/* 257 */     long writeSize = write.size;
/*     */     
/* 259 */     if (update) {
/* 260 */       if (next == null) {
/*     */ 
/*     */         
/* 263 */         this.head = this.tail = null;
/* 264 */         this.size = 0;
/* 265 */         this.bytes = 0L;
/*     */       } else {
/* 267 */         this.head = next;
/* 268 */         this.size--;
/* 269 */         this.bytes -= writeSize;
/* 270 */         assert this.size > 0 && this.bytes >= 0L;
/*     */       } 
/*     */     }
/*     */     
/* 274 */     write.recycle();
/* 275 */     this.tracker.decrementPendingOutboundBytes(writeSize);
/*     */   }
/*     */   
/*     */   private static void safeFail(ChannelPromise promise, Throwable cause) {
/* 279 */     if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
/* 280 */       logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class PendingWrite
/*     */   {
/* 288 */     private static final ObjectPool<PendingWrite> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PendingWrite>()
/*     */         {
/*     */           public PendingWriteQueue.PendingWrite newObject(ObjectPool.Handle<PendingWriteQueue.PendingWrite> handle) {
/* 291 */             return new PendingWriteQueue.PendingWrite(handle);
/*     */           }
/*     */         });
/*     */     
/*     */     private final ObjectPool.Handle<PendingWrite> handle;
/*     */     private PendingWrite next;
/*     */     private long size;
/*     */     private ChannelPromise promise;
/*     */     private Object msg;
/*     */     
/*     */     private PendingWrite(ObjectPool.Handle<PendingWrite> handle) {
/* 302 */       this.handle = handle;
/*     */     }
/*     */     
/*     */     static PendingWrite newInstance(Object msg, int size, ChannelPromise promise) {
/* 306 */       PendingWrite write = (PendingWrite)RECYCLER.get();
/* 307 */       write.size = size;
/* 308 */       write.msg = msg;
/* 309 */       write.promise = promise;
/* 310 */       return write;
/*     */     }
/*     */     
/*     */     private void recycle() {
/* 314 */       this.size = 0L;
/* 315 */       this.next = null;
/* 316 */       this.msg = null;
/* 317 */       this.promise = null;
/* 318 */       this.handle.recycle(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\PendingWriteQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */