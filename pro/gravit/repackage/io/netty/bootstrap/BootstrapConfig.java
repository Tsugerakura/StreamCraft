/*    */ package pro.gravit.repackage.io.netty.bootstrap;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import pro.gravit.repackage.io.netty.channel.Channel;
/*    */ import pro.gravit.repackage.io.netty.resolver.AddressResolverGroup;
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
/*    */ public final class BootstrapConfig
/*    */   extends AbstractBootstrapConfig<Bootstrap, Channel>
/*    */ {
/*    */   BootstrapConfig(Bootstrap bootstrap) {
/* 29 */     super(bootstrap);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SocketAddress remoteAddress() {
/* 36 */     return this.bootstrap.remoteAddress();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AddressResolverGroup<?> resolver() {
/* 43 */     return this.bootstrap.resolver();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     StringBuilder buf = new StringBuilder(super.toString());
/* 49 */     buf.setLength(buf.length() - 1);
/* 50 */     buf.append(", resolver: ").append(resolver());
/* 51 */     SocketAddress remoteAddress = remoteAddress();
/* 52 */     if (remoteAddress != null) {
/* 53 */       buf.append(", remoteAddress: ")
/* 54 */         .append(remoteAddress);
/*    */     }
/* 56 */     return buf.append(')').toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\bootstrap\BootstrapConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */