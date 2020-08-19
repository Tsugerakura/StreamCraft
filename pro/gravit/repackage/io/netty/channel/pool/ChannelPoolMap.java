package pro.gravit.repackage.io.netty.channel.pool;

public interface ChannelPoolMap<K, P extends ChannelPool> {
  P get(K paramK);
  
  boolean contains(K paramK);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\ChannelPoolMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */