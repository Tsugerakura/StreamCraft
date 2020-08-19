/*    */ package pro.gravit.repackage.io.netty.channel.socket.nio;
/*    */ 
/*    */ import java.net.ProtocolFamily;
/*    */ import java.net.StandardProtocolFamily;
/*    */ import pro.gravit.repackage.io.netty.channel.socket.InternetProtocolFamily;
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
/*    */ 
/*    */ final class ProtocolFamilyConverter
/*    */ {
/*    */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*    */   public static ProtocolFamily convert(InternetProtocolFamily family) {
/* 38 */     switch (family) {
/*    */       case IPv4:
/* 40 */         return StandardProtocolFamily.INET;
/*    */       case IPv6:
/* 42 */         return StandardProtocolFamily.INET6;
/*    */     } 
/* 44 */     throw new IllegalArgumentException();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\nio\ProtocolFamilyConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */