package pro.gravit.repackage.io.netty.channel;

import java.io.Serializable;

public interface ChannelId extends Serializable, Comparable<ChannelId> {
  String asShortText();
  
  String asLongText();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */