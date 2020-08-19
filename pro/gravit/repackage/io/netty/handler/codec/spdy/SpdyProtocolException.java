/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
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
/*    */ public class SpdyProtocolException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 7870000537743847264L;
/*    */   
/*    */   public SpdyProtocolException() {}
/*    */   
/*    */   public SpdyProtocolException(String message, Throwable cause) {
/* 34 */     super(message, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SpdyProtocolException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SpdyProtocolException(Throwable cause) {
/* 48 */     super(cause);
/*    */   }
/*    */   
/*    */   static SpdyProtocolException newStatic(String message) {
/* 52 */     if (PlatformDependent.javaVersion() >= 7) {
/* 53 */       return new SpdyProtocolException(message, true);
/*    */     }
/* 55 */     return new SpdyProtocolException(message);
/*    */   }
/*    */ 
/*    */   
/*    */   @SuppressJava6Requirement(reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
/*    */   private SpdyProtocolException(String message, boolean shared) {
/* 61 */     super(message, null, false, true);
/* 62 */     assert shared;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyProtocolException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */