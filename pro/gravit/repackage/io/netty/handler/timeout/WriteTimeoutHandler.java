/*     */ package pro.gravit.repackage.io.netty.handler.timeout;
/*     */ 
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ public class WriteTimeoutHandler
/*     */   extends ChannelOutboundHandlerAdapter
/*     */ {
/*  67 */   private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
/*     */ 
/*     */ 
/*     */   
/*     */   private final long timeoutNanos;
/*     */ 
/*     */ 
/*     */   
/*     */   private WriteTimeoutTask lastTask;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean closed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WriteTimeoutHandler(int timeoutSeconds) {
/*  85 */     this(timeoutSeconds, TimeUnit.SECONDS);
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
/*     */   public WriteTimeoutHandler(long timeout, TimeUnit unit) {
/*  97 */     ObjectUtil.checkNotNull(unit, "unit");
/*     */     
/*  99 */     if (timeout <= 0L) {
/* 100 */       this.timeoutNanos = 0L;
/*     */     } else {
/* 102 */       this.timeoutNanos = Math.max(unit.toNanos(timeout), MIN_TIMEOUT_NANOS);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 108 */     if (this.timeoutNanos > 0L) {
/* 109 */       promise = promise.unvoid();
/* 110 */       scheduleTimeout(ctx, promise);
/*     */     } 
/* 112 */     ctx.write(msg, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 117 */     WriteTimeoutTask task = this.lastTask;
/* 118 */     this.lastTask = null;
/* 119 */     while (task != null) {
/* 120 */       task.scheduledFuture.cancel(false);
/* 121 */       WriteTimeoutTask prev = task.prev;
/* 122 */       task.prev = null;
/* 123 */       task.next = null;
/* 124 */       task = prev;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void scheduleTimeout(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 130 */     WriteTimeoutTask task = new WriteTimeoutTask(ctx, promise);
/* 131 */     task.scheduledFuture = (ScheduledFuture<?>)ctx.executor().schedule(task, this.timeoutNanos, TimeUnit.NANOSECONDS);
/*     */     
/* 133 */     if (!task.scheduledFuture.isDone()) {
/* 134 */       addWriteTimeoutTask(task);
/*     */ 
/*     */       
/* 137 */       promise.addListener((GenericFutureListener)task);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addWriteTimeoutTask(WriteTimeoutTask task) {
/* 142 */     if (this.lastTask != null) {
/* 143 */       this.lastTask.next = task;
/* 144 */       task.prev = this.lastTask;
/*     */     } 
/* 146 */     this.lastTask = task;
/*     */   }
/*     */   
/*     */   private void removeWriteTimeoutTask(WriteTimeoutTask task) {
/* 150 */     if (task == this.lastTask) {
/*     */       
/* 152 */       assert task.next == null;
/* 153 */       this.lastTask = this.lastTask.prev;
/* 154 */       if (this.lastTask != null)
/* 155 */         this.lastTask.next = null; 
/*     */     } else {
/* 157 */       if (task.prev == null && task.next == null) {
/*     */         return;
/*     */       }
/* 160 */       if (task.prev == null) {
/*     */         
/* 162 */         task.next.prev = null;
/*     */       } else {
/* 164 */         task.prev.next = task.next;
/* 165 */         task.next.prev = task.prev;
/*     */       } 
/* 167 */     }  task.prev = null;
/* 168 */     task.next = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeTimedOut(ChannelHandlerContext ctx) throws Exception {
/* 175 */     if (!this.closed) {
/* 176 */       ctx.fireExceptionCaught((Throwable)WriteTimeoutException.INSTANCE);
/* 177 */       ctx.close();
/* 178 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final class WriteTimeoutTask
/*     */     implements Runnable, ChannelFutureListener
/*     */   {
/*     */     private final ChannelHandlerContext ctx;
/*     */     
/*     */     private final ChannelPromise promise;
/*     */     WriteTimeoutTask prev;
/*     */     WriteTimeoutTask next;
/*     */     ScheduledFuture<?> scheduledFuture;
/*     */     
/*     */     WriteTimeoutTask(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 194 */       this.ctx = ctx;
/* 195 */       this.promise = promise;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 203 */       if (!this.promise.isDone()) {
/*     */         try {
/* 205 */           WriteTimeoutHandler.this.writeTimedOut(this.ctx);
/* 206 */         } catch (Throwable t) {
/* 207 */           this.ctx.fireExceptionCaught(t);
/*     */         } 
/*     */       }
/* 210 */       WriteTimeoutHandler.this.removeWriteTimeoutTask(this);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void operationComplete(ChannelFuture future) throws Exception {
/* 216 */       this.scheduledFuture.cancel(false);
/* 217 */       WriteTimeoutHandler.this.removeWriteTimeoutTask(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\WriteTimeoutHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */