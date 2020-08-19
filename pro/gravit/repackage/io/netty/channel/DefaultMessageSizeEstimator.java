/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
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
/*    */ public final class DefaultMessageSizeEstimator
/*    */   implements MessageSizeEstimator
/*    */ {
/*    */   private static final class HandleImpl
/*    */     implements MessageSizeEstimator.Handle
/*    */   {
/*    */     private final int unknownSize;
/*    */     
/*    */     private HandleImpl(int unknownSize) {
/* 33 */       this.unknownSize = unknownSize;
/*    */     }
/*    */ 
/*    */     
/*    */     public int size(Object msg) {
/* 38 */       if (msg instanceof ByteBuf) {
/* 39 */         return ((ByteBuf)msg).readableBytes();
/*    */       }
/* 41 */       if (msg instanceof ByteBufHolder) {
/* 42 */         return ((ByteBufHolder)msg).content().readableBytes();
/*    */       }
/* 44 */       if (msg instanceof FileRegion) {
/* 45 */         return 0;
/*    */       }
/* 47 */       return this.unknownSize;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 54 */   public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator(8);
/*    */ 
/*    */ 
/*    */   
/*    */   private final MessageSizeEstimator.Handle handle;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultMessageSizeEstimator(int unknownSize) {
/* 64 */     ObjectUtil.checkPositiveOrZero(unknownSize, "unknownSize");
/* 65 */     this.handle = new HandleImpl(unknownSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSizeEstimator.Handle newHandle() {
/* 70 */     return this.handle;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultMessageSizeEstimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */