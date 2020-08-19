/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public final class ChannelFlushPromiseNotifier
/*     */ {
/*     */   private long writeCounter;
/*  32 */   private final Queue<FlushCheckpoint> flushCheckpoints = new ArrayDeque<FlushCheckpoint>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean tryNotify;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFlushPromiseNotifier(boolean tryNotify) {
/*  44 */     this.tryNotify = tryNotify;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFlushPromiseNotifier() {
/*  52 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFlushPromiseNotifier add(ChannelPromise promise, int pendingDataSize) {
/*  60 */     return add(promise, pendingDataSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFlushPromiseNotifier add(ChannelPromise promise, long pendingDataSize) {
/*  68 */     ObjectUtil.checkNotNull(promise, "promise");
/*  69 */     ObjectUtil.checkPositiveOrZero(pendingDataSize, "pendingDataSize");
/*  70 */     long checkpoint = this.writeCounter + pendingDataSize;
/*  71 */     if (promise instanceof FlushCheckpoint) {
/*  72 */       FlushCheckpoint cp = (FlushCheckpoint)promise;
/*  73 */       cp.flushCheckpoint(checkpoint);
/*  74 */       this.flushCheckpoints.add(cp);
/*     */     } else {
/*  76 */       this.flushCheckpoints.add(new DefaultFlushCheckpoint(checkpoint, promise));
/*     */     } 
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFlushPromiseNotifier increaseWriteCounter(long delta) {
/*  84 */     ObjectUtil.checkPositiveOrZero(delta, "delta");
/*  85 */     this.writeCounter += delta;
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long writeCounter() {
/*  93 */     return this.writeCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFlushPromiseNotifier notifyPromises() {
/* 104 */     notifyPromises0(null);
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFlushPromiseNotifier notifyFlushFutures() {
/* 113 */     return notifyPromises();
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
/*     */   public ChannelFlushPromiseNotifier notifyPromises(Throwable cause) {
/* 128 */     notifyPromises();
/*     */     while (true) {
/* 130 */       FlushCheckpoint cp = this.flushCheckpoints.poll();
/* 131 */       if (cp == null) {
/*     */         break;
/*     */       }
/* 134 */       if (this.tryNotify) {
/* 135 */         cp.promise().tryFailure(cause); continue;
/*     */       } 
/* 137 */       cp.promise().setFailure(cause);
/*     */     } 
/*     */     
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause) {
/* 148 */     return notifyPromises(cause);
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
/*     */   public ChannelFlushPromiseNotifier notifyPromises(Throwable cause1, Throwable cause2) {
/* 168 */     notifyPromises0(cause1);
/*     */     while (true) {
/* 170 */       FlushCheckpoint cp = this.flushCheckpoints.poll();
/* 171 */       if (cp == null) {
/*     */         break;
/*     */       }
/* 174 */       if (this.tryNotify) {
/* 175 */         cp.promise().tryFailure(cause2); continue;
/*     */       } 
/* 177 */       cp.promise().setFailure(cause2);
/*     */     } 
/*     */     
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause1, Throwable cause2) {
/* 188 */     return notifyPromises(cause1, cause2);
/*     */   }
/*     */   
/*     */   private void notifyPromises0(Throwable cause) {
/* 192 */     if (this.flushCheckpoints.isEmpty()) {
/* 193 */       this.writeCounter = 0L;
/*     */       
/*     */       return;
/*     */     } 
/* 197 */     long writeCounter = this.writeCounter;
/*     */     while (true) {
/* 199 */       FlushCheckpoint cp = this.flushCheckpoints.peek();
/* 200 */       if (cp == null) {
/*     */         
/* 202 */         this.writeCounter = 0L;
/*     */         
/*     */         break;
/*     */       } 
/* 206 */       if (cp.flushCheckpoint() > writeCounter) {
/* 207 */         if (writeCounter > 0L && this.flushCheckpoints.size() == 1) {
/* 208 */           this.writeCounter = 0L;
/* 209 */           cp.flushCheckpoint(cp.flushCheckpoint() - writeCounter);
/*     */         } 
/*     */         
/*     */         break;
/*     */       } 
/* 214 */       this.flushCheckpoints.remove();
/* 215 */       ChannelPromise promise = cp.promise();
/* 216 */       if (cause == null) {
/* 217 */         if (this.tryNotify) {
/* 218 */           promise.trySuccess(); continue;
/*     */         } 
/* 220 */         promise.setSuccess();
/*     */         continue;
/*     */       } 
/* 223 */       if (this.tryNotify) {
/* 224 */         promise.tryFailure(cause); continue;
/*     */       } 
/* 226 */       promise.setFailure(cause);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 232 */     long newWriteCounter = this.writeCounter;
/* 233 */     if (newWriteCounter >= 549755813888L) {
/*     */ 
/*     */       
/* 236 */       this.writeCounter = 0L;
/* 237 */       for (FlushCheckpoint cp : this.flushCheckpoints) {
/* 238 */         cp.flushCheckpoint(cp.flushCheckpoint() - newWriteCounter);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class DefaultFlushCheckpoint
/*     */     implements FlushCheckpoint
/*     */   {
/*     */     private long checkpoint;
/*     */     
/*     */     private final ChannelPromise future;
/*     */ 
/*     */     
/*     */     DefaultFlushCheckpoint(long checkpoint, ChannelPromise future) {
/* 254 */       this.checkpoint = checkpoint;
/* 255 */       this.future = future;
/*     */     }
/*     */ 
/*     */     
/*     */     public long flushCheckpoint() {
/* 260 */       return this.checkpoint;
/*     */     }
/*     */ 
/*     */     
/*     */     public void flushCheckpoint(long checkpoint) {
/* 265 */       this.checkpoint = checkpoint;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelPromise promise() {
/* 270 */       return this.future;
/*     */     }
/*     */   }
/*     */   
/*     */   static interface FlushCheckpoint {
/*     */     long flushCheckpoint();
/*     */     
/*     */     void flushCheckpoint(long param1Long);
/*     */     
/*     */     ChannelPromise promise();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelFlushPromiseNotifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */