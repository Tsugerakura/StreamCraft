/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import java.nio.channels.ClosedChannelException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandlerAdapter;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*    */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.ScheduledFuture;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WebSocketCloseFrameHandler
/*    */   extends ChannelOutboundHandlerAdapter
/*    */ {
/*    */   private final WebSocketCloseStatus closeStatus;
/*    */   private final long forceCloseTimeoutMillis;
/*    */   private ChannelPromise closeSent;
/*    */   
/*    */   WebSocketCloseFrameHandler(WebSocketCloseStatus closeStatus, long forceCloseTimeoutMillis) {
/* 39 */     this.closeStatus = (WebSocketCloseStatus)ObjectUtil.checkNotNull(closeStatus, "closeStatus");
/* 40 */     this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
/* 45 */     if (!ctx.channel().isActive()) {
/* 46 */       ctx.close(promise);
/*    */       return;
/*    */     } 
/* 49 */     if (this.closeSent == null) {
/* 50 */       write(ctx, new CloseWebSocketFrame(this.closeStatus), ctx.newPromise());
/*    */     }
/* 52 */     flush(ctx);
/* 53 */     applyCloseSentTimeout(ctx);
/* 54 */     this.closeSent.addListener((GenericFutureListener)new ChannelFutureListener()
/*    */         {
/*    */           public void operationComplete(ChannelFuture future) {
/* 57 */             ctx.close(promise);
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 64 */     if (this.closeSent != null) {
/* 65 */       ReferenceCountUtil.release(msg);
/* 66 */       promise.setFailure(new ClosedChannelException());
/*    */       return;
/*    */     } 
/* 69 */     if (msg instanceof CloseWebSocketFrame) {
/* 70 */       promise = promise.unvoid();
/* 71 */       this.closeSent = promise;
/*    */     } 
/* 73 */     super.write(ctx, msg, promise);
/*    */   }
/*    */   
/*    */   private void applyCloseSentTimeout(ChannelHandlerContext ctx) {
/* 77 */     if (this.closeSent.isDone() || this.forceCloseTimeoutMillis < 0L) {
/*    */       return;
/*    */     }
/*    */     
/* 81 */     final ScheduledFuture<?> timeoutTask = ctx.executor().schedule(new Runnable()
/*    */         {
/*    */           public void run() {
/* 84 */             if (!WebSocketCloseFrameHandler.this.closeSent.isDone()) {
/* 85 */               WebSocketCloseFrameHandler.this.closeSent.tryFailure(new WebSocketHandshakeException("send close frame timed out"));
/*    */             }
/*    */           }
/*    */         },  this.forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
/*    */     
/* 90 */     this.closeSent.addListener((GenericFutureListener)new ChannelFutureListener()
/*    */         {
/*    */           public void operationComplete(ChannelFuture future) {
/* 93 */             timeoutTask.cancel(false);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketCloseFrameHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */