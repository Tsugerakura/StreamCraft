/*    */ package pro.gravit.repackage.io.netty.channel.socket;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public final class ChannelOutputShutdownException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 6712549938359321378L;
/*    */   
/*    */   public ChannelOutputShutdownException(String msg) {
/* 32 */     super(msg);
/*    */   }
/*    */   
/*    */   public ChannelOutputShutdownException(String msg, Throwable cause) {
/* 36 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\ChannelOutputShutdownException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */