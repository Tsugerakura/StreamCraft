package pro.gravit.repackage.io.netty.handler.ipfilter;

import java.net.InetSocketAddress;

public interface IpFilterRule {
  boolean matches(InetSocketAddress paramInetSocketAddress);
  
  IpFilterRuleType ruleType();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ipfilter\IpFilterRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */