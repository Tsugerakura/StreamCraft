package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.ReferenceCounted;

public interface AddressedEnvelope<M, A extends java.net.SocketAddress> extends ReferenceCounted {
  M content();
  
  A sender();
  
  A recipient();
  
  AddressedEnvelope<M, A> retain();
  
  AddressedEnvelope<M, A> retain(int paramInt);
  
  AddressedEnvelope<M, A> touch();
  
  AddressedEnvelope<M, A> touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AddressedEnvelope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */