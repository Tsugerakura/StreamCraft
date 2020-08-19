/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class FailedChannelFuture
/*    */   extends CompleteChannelFuture
/*    */ {
/*    */   private final Throwable cause;
/*    */   
/*    */   FailedChannelFuture(Channel channel, EventExecutor executor, Throwable cause) {
/* 38 */     super(channel, executor);
/* 39 */     this.cause = (Throwable)ObjectUtil.checkNotNull(cause, "cause");
/*    */   }
/*    */ 
/*    */   
/*    */   public Throwable cause() {
/* 44 */     return this.cause;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSuccess() {
/* 49 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelFuture sync() {
/* 54 */     PlatformDependent.throwException(this.cause);
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ChannelFuture syncUninterruptibly() {
/* 60 */     PlatformDependent.throwException(this.cause);
/* 61 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\FailedChannelFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */