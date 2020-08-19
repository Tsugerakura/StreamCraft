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
/*    */ public final class ReadTimeoutException
/*    */   extends TimeoutException
/*    */ {
/*    */   private static final long serialVersionUID = 169287984113283421L;
/* 28 */   public static final ReadTimeoutException INSTANCE = (PlatformDependent.javaVersion() >= 7) ? new ReadTimeoutException(true) : new ReadTimeoutException();
/*    */ 
/*    */   
/*    */   ReadTimeoutException() {}
/*    */   
/*    */   private ReadTimeoutException(boolean shared) {
/* 34 */     super(shared);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\timeout\ReadTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */