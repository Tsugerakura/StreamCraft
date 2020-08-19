/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*    */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CoalescingBufferQueue
/*    */   extends AbstractCoalescingBufferQueue
/*    */ {
/*    */   private final Channel channel;
/*    */   
/*    */   public CoalescingBufferQueue(Channel channel) {
/* 39 */     this(channel, 4);
/*    */   }
/*    */   
/*    */   public CoalescingBufferQueue(Channel channel, int initSize) {
/* 43 */     this(channel, initSize, false);
/*    */   }
/*    */   
/*    */   public CoalescingBufferQueue(Channel channel, int initSize, boolean updateWritability) {
/* 47 */     super(updateWritability ? channel : null, initSize);
/* 48 */     this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
/*    */   }
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
/*    */   public ByteBuf remove(int bytes, ChannelPromise aggregatePromise) {
/* 62 */     return remove(this.channel.alloc(), bytes, aggregatePromise);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void releaseAndFailAll(Throwable cause) {
/* 69 */     releaseAndFailAll(this.channel, cause);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
/* 74 */     if (cumulation instanceof CompositeByteBuf) {
/* 75 */       CompositeByteBuf composite = (CompositeByteBuf)cumulation;
/* 76 */       composite.addComponent(true, next);
/* 77 */       return (ByteBuf)composite;
/*    */     } 
/* 79 */     return composeIntoComposite(alloc, cumulation, next);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ByteBuf removeEmptyValue() {
/* 84 */     return Unpooled.EMPTY_BUFFER;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\CoalescingBufferQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */