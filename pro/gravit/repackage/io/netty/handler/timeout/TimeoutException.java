/*    */ package pro.gravit.repackage.io.netty.handler.timeout;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelException;
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
/*    */ public class TimeoutException
/*    */   extends ChannelException
/*    */ {
/*    */   private static final long serialVersionUID = 4673641882869672533L;
/*    */   
/*    */   TimeoutException() {}
/*    */   
/*    */   TimeoutException(boolean shared) {
/* 32 */     super(null, null, shared);
/*    */   }
/*    */ 
/*    */   
/*    */   public Throwable fillInStackTrace() {
/* 37 */     return (Throwable)this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\TimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */