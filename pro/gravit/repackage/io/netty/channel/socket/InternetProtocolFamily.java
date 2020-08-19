/*    */ package pro.gravit.repackage.io.netty.channel.socket;
/*    */ 
/*    */ import java.net.Inet4Address;
/*    */ import java.net.Inet6Address;
/*    */ import java.net.InetAddress;
/*    */ import pro.gravit.repackage.io.netty.util.NetUtil;
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
/*    */ public enum InternetProtocolFamily
/*    */ {
/* 28 */   IPv4((Class)Inet4Address.class, 1, NetUtil.LOCALHOST4),
/* 29 */   IPv6((Class)Inet6Address.class, 2, NetUtil.LOCALHOST6);
/*    */   
/*    */   private final Class<? extends InetAddress> addressType;
/*    */   private final int addressNumber;
/*    */   private final InetAddress localHost;
/*    */   
/*    */   InternetProtocolFamily(Class<? extends InetAddress> addressType, int addressNumber, InetAddress localHost) {
/* 36 */     this.addressType = addressType;
/* 37 */     this.addressNumber = addressNumber;
/* 38 */     this.localHost = localHost;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<? extends InetAddress> addressType() {
/* 45 */     return this.addressType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int addressNumber() {
/* 54 */     return this.addressNumber;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InetAddress localhost() {
/* 61 */     return this.localHost;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static InternetProtocolFamily of(InetAddress address) {
/* 68 */     if (address instanceof Inet4Address) {
/* 69 */       return IPv4;
/*    */     }
/* 71 */     if (address instanceof Inet6Address) {
/* 72 */       return IPv6;
/*    */     }
/* 74 */     throw new IllegalArgumentException("address " + address + " not supported");
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\InternetProtocolFamily.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */