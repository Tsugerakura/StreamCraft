/*    */ package pro.gravit.repackage.io.netty.channel;
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
/*    */ public final class DefaultSelectStrategyFactory
/*    */   implements SelectStrategyFactory
/*    */ {
/* 22 */   public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SelectStrategy newSelectStrategy() {
/* 28 */     return DefaultSelectStrategy.INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultSelectStrategyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */