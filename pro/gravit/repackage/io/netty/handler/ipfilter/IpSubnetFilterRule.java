/*     */ package pro.gravit.repackage.io.netty.handler.ipfilter;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SocketUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class IpSubnetFilterRule
/*     */   implements IpFilterRule
/*     */ {
/*     */   private final IpFilterRule filterRule;
/*     */   
/*     */   public IpSubnetFilterRule(String ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
/*     */     try {
/*  38 */       this.filterRule = selectFilterRule(SocketUtils.addressByName(ipAddress), cidrPrefix, ruleType);
/*  39 */     } catch (UnknownHostException e) {
/*  40 */       throw new IllegalArgumentException("ipAddress", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public IpSubnetFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
/*  45 */     this.filterRule = selectFilterRule(ipAddress, cidrPrefix, ruleType);
/*     */   }
/*     */   
/*     */   private static IpFilterRule selectFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
/*  49 */     ObjectUtil.checkNotNull(ipAddress, "ipAddress");
/*  50 */     ObjectUtil.checkNotNull(ruleType, "ruleType");
/*     */     
/*  52 */     if (ipAddress instanceof Inet4Address)
/*  53 */       return new Ip4SubnetFilterRule((Inet4Address)ipAddress, cidrPrefix, ruleType); 
/*  54 */     if (ipAddress instanceof Inet6Address) {
/*  55 */       return new Ip6SubnetFilterRule((Inet6Address)ipAddress, cidrPrefix, ruleType);
/*     */     }
/*  57 */     throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(InetSocketAddress remoteAddress) {
/*  63 */     return this.filterRule.matches(remoteAddress);
/*     */   }
/*     */ 
/*     */   
/*     */   public IpFilterRuleType ruleType() {
/*  68 */     return this.filterRule.ruleType();
/*     */   }
/*     */   
/*     */   private static final class Ip4SubnetFilterRule
/*     */     implements IpFilterRule {
/*     */     private final int networkAddress;
/*     */     private final int subnetMask;
/*     */     private final IpFilterRuleType ruleType;
/*     */     
/*     */     private Ip4SubnetFilterRule(Inet4Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
/*  78 */       if (cidrPrefix < 0 || cidrPrefix > 32) {
/*  79 */         throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", new Object[] {
/*  80 */                 Integer.valueOf(cidrPrefix)
/*     */               }));
/*     */       }
/*  83 */       this.subnetMask = prefixToSubnetMask(cidrPrefix);
/*  84 */       this.networkAddress = ipToInt(ipAddress) & this.subnetMask;
/*  85 */       this.ruleType = ruleType;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean matches(InetSocketAddress remoteAddress) {
/*  90 */       InetAddress inetAddress = remoteAddress.getAddress();
/*  91 */       if (inetAddress instanceof Inet4Address) {
/*  92 */         int ipAddress = ipToInt((Inet4Address)inetAddress);
/*  93 */         return ((ipAddress & this.subnetMask) == this.networkAddress);
/*     */       } 
/*  95 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public IpFilterRuleType ruleType() {
/* 100 */       return this.ruleType;
/*     */     }
/*     */     
/*     */     private static int ipToInt(Inet4Address ipAddress) {
/* 104 */       byte[] octets = ipAddress.getAddress();
/* 105 */       assert octets.length == 4;
/*     */       
/* 107 */       return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | octets[3] & 0xFF;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static int prefixToSubnetMask(int cidrPrefix) {
/* 124 */       return (int)(-1L << 32 - cidrPrefix & 0xFFFFFFFFFFFFFFFFL);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Ip6SubnetFilterRule
/*     */     implements IpFilterRule {
/* 130 */     private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1L);
/*     */     
/*     */     private final BigInteger networkAddress;
/*     */     private final BigInteger subnetMask;
/*     */     private final IpFilterRuleType ruleType;
/*     */     
/*     */     private Ip6SubnetFilterRule(Inet6Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
/* 137 */       if (cidrPrefix < 0 || cidrPrefix > 128) {
/* 138 */         throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", new Object[] {
/* 139 */                 Integer.valueOf(cidrPrefix)
/*     */               }));
/*     */       }
/* 142 */       this.subnetMask = prefixToSubnetMask(cidrPrefix);
/* 143 */       this.networkAddress = ipToInt(ipAddress).and(this.subnetMask);
/* 144 */       this.ruleType = ruleType;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean matches(InetSocketAddress remoteAddress) {
/* 149 */       InetAddress inetAddress = remoteAddress.getAddress();
/* 150 */       if (inetAddress instanceof Inet6Address) {
/* 151 */         BigInteger ipAddress = ipToInt((Inet6Address)inetAddress);
/* 152 */         return ipAddress.and(this.subnetMask).equals(this.networkAddress);
/*     */       } 
/* 154 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public IpFilterRuleType ruleType() {
/* 159 */       return this.ruleType;
/*     */     }
/*     */     
/*     */     private static BigInteger ipToInt(Inet6Address ipAddress) {
/* 163 */       byte[] octets = ipAddress.getAddress();
/* 164 */       assert octets.length == 16;
/*     */       
/* 166 */       return new BigInteger(octets);
/*     */     }
/*     */     
/*     */     private static BigInteger prefixToSubnetMask(int cidrPrefix) {
/* 170 */       return MINUS_ONE.shiftLeft(128 - cidrPrefix);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ipfilter\IpSubnetFilterRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */