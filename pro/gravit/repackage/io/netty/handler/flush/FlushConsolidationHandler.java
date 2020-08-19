/*     */ package pro.gravit.repackage.io.netty.handler.flush;
/*     */ 
/*     */ import java.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FlushConsolidationHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*     */   private final int explicitFlushAfterFlushes;
/*     */   private final boolean consolidateWhenNoReadInProgress;
/*     */   private final Runnable flushTask;
/*     */   private int flushPendingCount;
/*     */   private boolean readInProgress;
/*     */   private ChannelHandlerContext ctx;
/*     */   private Future<?> nextScheduledFlush;
/*     */   public static final int DEFAULT_EXPLICIT_FLUSH_AFTER_FLUSHES = 256;
/*     */   
/*     */   public FlushConsolidationHandler() {
/*  79 */     this(256, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlushConsolidationHandler(int explicitFlushAfterFlushes) {
/*  88 */     this(explicitFlushAfterFlushes, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlushConsolidationHandler(int explicitFlushAfterFlushes, boolean consolidateWhenNoReadInProgress) {
/*  99 */     this
/* 100 */       .explicitFlushAfterFlushes = ObjectUtil.checkPositive(explicitFlushAfterFlushes, "explicitFlushAfterFlushes");
/* 101 */     this.consolidateWhenNoReadInProgress = consolidateWhenNoReadInProgress;
/* 102 */     this.flushTask = consolidateWhenNoReadInProgress ? new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/* 106 */           if (FlushConsolidationHandler.this.flushPendingCount > 0 && !FlushConsolidationHandler.this.readInProgress) {
/* 107 */             FlushConsolidationHandler.this.flushPendingCount = 0;
/* 108 */             FlushConsolidationHandler.this.nextScheduledFlush = null;
/* 109 */             FlushConsolidationHandler.this.ctx.flush();
/*     */           } 
/*     */         }
/*     */       } : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 118 */     this.ctx = ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 123 */     if (this.readInProgress) {
/*     */ 
/*     */       
/* 126 */       if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
/* 127 */         flushNow(ctx);
/*     */       }
/* 129 */     } else if (this.consolidateWhenNoReadInProgress) {
/*     */       
/* 131 */       if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
/* 132 */         flushNow(ctx);
/*     */       } else {
/* 134 */         scheduleFlush(ctx);
/*     */       } 
/*     */     } else {
/*     */       
/* 138 */       flushNow(ctx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 145 */     resetReadAndFlushIfNeeded(ctx);
/* 146 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 151 */     this.readInProgress = true;
/* 152 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 158 */     resetReadAndFlushIfNeeded(ctx);
/* 159 */     ctx.fireExceptionCaught(cause);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 165 */     resetReadAndFlushIfNeeded(ctx);
/* 166 */     ctx.disconnect(promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 172 */     resetReadAndFlushIfNeeded(ctx);
/* 173 */     ctx.close(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/* 178 */     if (!ctx.channel().isWritable())
/*     */     {
/* 180 */       flushIfNeeded(ctx);
/*     */     }
/* 182 */     ctx.fireChannelWritabilityChanged();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 187 */     flushIfNeeded(ctx);
/*     */   }
/*     */   
/*     */   private void resetReadAndFlushIfNeeded(ChannelHandlerContext ctx) {
/* 191 */     this.readInProgress = false;
/* 192 */     flushIfNeeded(ctx);
/*     */   }
/*     */   
/*     */   private void flushIfNeeded(ChannelHandlerContext ctx) {
/* 196 */     if (this.flushPendingCount > 0) {
/* 197 */       flushNow(ctx);
/*     */     }
/*     */   }
/*     */   
/*     */   private void flushNow(ChannelHandlerContext ctx) {
/* 202 */     cancelScheduledFlush();
/* 203 */     this.flushPendingCount = 0;
/* 204 */     ctx.flush();
/*     */   }
/*     */   
/*     */   private void scheduleFlush(ChannelHandlerContext ctx) {
/* 208 */     if (this.nextScheduledFlush == null)
/*     */     {
/* 210 */       this.nextScheduledFlush = (Future<?>)ctx.channel().eventLoop().submit(this.flushTask);
/*     */     }
/*     */   }
/*     */   
/*     */   private void cancelScheduledFlush() {
/* 215 */     if (this.nextScheduledFlush != null) {
/* 216 */       this.nextScheduledFlush.cancel(false);
/* 217 */       this.nextScheduledFlush = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\flush\FlushConsolidationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */