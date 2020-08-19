/*    */ package pro.gravit.repackage.io.netty.channel.local;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ import pro.gravit.repackage.io.netty.channel.Channel;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*    */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*    */ final class LocalChannelRegistry
/*    */ {
/* 28 */   private static final ConcurrentMap<LocalAddress, Channel> boundChannels = PlatformDependent.newConcurrentHashMap();
/*    */ 
/*    */   
/*    */   static LocalAddress register(Channel channel, LocalAddress oldLocalAddress, SocketAddress localAddress) {
/* 32 */     if (oldLocalAddress != null) {
/* 33 */       throw new ChannelException("already bound");
/*    */     }
/* 35 */     if (!(localAddress instanceof LocalAddress)) {
/* 36 */       throw new ChannelException("unsupported address type: " + StringUtil.simpleClassName(localAddress));
/*    */     }
/*    */     
/* 39 */     LocalAddress addr = (LocalAddress)localAddress;
/* 40 */     if (LocalAddress.ANY.equals(addr)) {
/* 41 */       addr = new LocalAddress(channel);
/*    */     }
/*    */     
/* 44 */     Channel boundChannel = boundChannels.putIfAbsent(addr, channel);
/* 45 */     if (boundChannel != null) {
/* 46 */       throw new ChannelException("address already in use by: " + boundChannel);
/*    */     }
/* 48 */     return addr;
/*    */   }
/*    */   
/*    */   static Channel get(SocketAddress localAddress) {
/* 52 */     return boundChannels.get(localAddress);
/*    */   }
/*    */   
/*    */   static void unregister(LocalAddress localAddress) {
/* 56 */     boundChannels.remove(localAddress);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\local\LocalChannelRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */