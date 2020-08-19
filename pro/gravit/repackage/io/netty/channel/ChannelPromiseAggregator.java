/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.PromiseAggregator;
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
/*    */ @Deprecated
/*    */ public final class ChannelPromiseAggregator
/*    */   extends PromiseAggregator<Void, ChannelFuture>
/*    */   implements ChannelFutureListener
/*    */ {
/*    */   public ChannelPromiseAggregator(ChannelPromise aggregatePromise) {
/* 35 */     super(aggregatePromise);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelPromiseAggregator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */