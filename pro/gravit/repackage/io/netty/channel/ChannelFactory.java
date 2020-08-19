package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.bootstrap.ChannelFactory;

public interface ChannelFactory<T extends Channel> extends ChannelFactory<T> {
  T newChannel();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */