/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*    */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*    */ public class ChannelException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 2908618315971075004L;
/*    */   
/*    */   public ChannelException() {}
/*    */   
/*    */   public ChannelException(String message, Throwable cause) {
/* 39 */     super(message, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChannelException(String message) {
/* 46 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChannelException(Throwable cause) {
/* 53 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @SuppressJava6Requirement(reason = "uses Java 7+ RuntimeException.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
/*    */   protected ChannelException(String message, Throwable cause, boolean shared) {
/* 60 */     super(message, cause, false, true);
/* 61 */     assert shared;
/*    */   }
/*    */   
/*    */   static ChannelException newStatic(String message, Throwable cause) {
/* 65 */     if (PlatformDependent.javaVersion() >= 7) {
/* 66 */       return new ChannelException(message, cause, true);
/*    */     }
/* 68 */     return new ChannelException(message, cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */