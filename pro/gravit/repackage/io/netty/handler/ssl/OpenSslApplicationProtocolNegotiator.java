package pro.gravit.repackage.io.netty.handler.ssl;

@Deprecated
public interface OpenSslApplicationProtocolNegotiator extends ApplicationProtocolNegotiator {
  ApplicationProtocolConfig.Protocol protocol();
  
  ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior();
  
  ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslApplicationProtocolNegotiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */