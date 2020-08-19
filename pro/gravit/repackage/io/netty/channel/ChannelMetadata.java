/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ChannelMetadata
/*    */ {
/*    */   private final boolean hasDisconnect;
/*    */   private final int defaultMaxMessagesPerRead;
/*    */   
/*    */   public ChannelMetadata(boolean hasDisconnect) {
/* 38 */     this(hasDisconnect, 1);
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
/*    */   public ChannelMetadata(boolean hasDisconnect, int defaultMaxMessagesPerRead) {
/* 51 */     ObjectUtil.checkPositive(defaultMaxMessagesPerRead, "defaultMaxMessagesPerRead");
/* 52 */     this.hasDisconnect = hasDisconnect;
/* 53 */     this.defaultMaxMessagesPerRead = defaultMaxMessagesPerRead;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasDisconnect() {
/* 62 */     return this.hasDisconnect;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int defaultMaxMessagesPerRead() {
/* 70 */     return this.defaultMaxMessagesPerRead;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */