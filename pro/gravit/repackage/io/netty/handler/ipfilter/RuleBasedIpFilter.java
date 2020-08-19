/*    */ package pro.gravit.repackage.io.netty.handler.ipfilter;
/*    */ 
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.SocketAddress;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ @Sharable
/*    */ public class RuleBasedIpFilter
/*    */   extends AbstractRemoteAddressFilter<InetSocketAddress>
/*    */ {
/*    */   private final IpFilterRule[] rules;
/*    */   
/*    */   public RuleBasedIpFilter(IpFilterRule... rules) {
/* 40 */     this.rules = (IpFilterRule[])ObjectUtil.checkNotNull(rules, "rules");
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
/* 45 */     for (IpFilterRule rule : this.rules) {
/* 46 */       if (rule == null) {
/*    */         break;
/*    */       }
/*    */       
/* 50 */       if (rule.matches(remoteAddress)) {
/* 51 */         return (rule.ruleType() == IpFilterRuleType.ACCEPT);
/*    */       }
/*    */     } 
/*    */     
/* 55 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ipfilter\RuleBasedIpFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */