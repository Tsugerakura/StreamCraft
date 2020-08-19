/*    */ package pro.gravit.repackage.io.netty.resolver;
/*    */ 
/*    */ import java.net.InetSocketAddress;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
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
/*    */ public final class DefaultAddressResolverGroup
/*    */   extends AddressResolverGroup<InetSocketAddress>
/*    */ {
/* 30 */   public static final DefaultAddressResolverGroup INSTANCE = new DefaultAddressResolverGroup();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) throws Exception {
/* 36 */     return (new DefaultNameResolver(executor)).asAddressResolver();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\DefaultAddressResolverGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */