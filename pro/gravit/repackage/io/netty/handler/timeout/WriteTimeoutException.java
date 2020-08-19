/*    */ package pro.gravit.repackage.io.netty.handler.timeout;
/*    */ 
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
/*    */ public final class WriteTimeoutException
/*    */   extends TimeoutException
/*    */ {
/*    */   private static final long serialVersionUID = -144786655770296065L;
/* 28 */   public static final WriteTimeoutException INSTANCE = (PlatformDependent.javaVersion() >= 7) ? new WriteTimeoutException(true) : new WriteTimeoutException();
/*    */ 
/*    */   
/*    */   private WriteTimeoutException() {}
/*    */   
/*    */   private WriteTimeoutException(boolean shared) {
/* 34 */     super(shared);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\WriteTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */