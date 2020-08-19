/*     */ package pro.gravit.repackage.io.netty.handler.timeout;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReadTimeoutHandler
/*     */   extends IdleStateHandler
/*     */ {
/*     */   private boolean closed;
/*     */   
/*     */   public ReadTimeoutHandler(int timeoutSeconds) {
/*  72 */     this(timeoutSeconds, TimeUnit.SECONDS);
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
/*     */   public ReadTimeoutHandler(long timeout, TimeUnit unit) {
/*  84 */     super(timeout, 0L, 0L, unit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
/*  89 */     assert evt.state() == IdleState.READER_IDLE;
/*  90 */     readTimedOut(ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
/*  97 */     if (!this.closed) {
/*  98 */       ctx.fireExceptionCaught((Throwable)ReadTimeoutException.INSTANCE);
/*  99 */       ctx.close();
/* 100 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\ReadTimeoutHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */