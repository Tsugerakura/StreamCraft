package pro.gravit.repackage.io.netty.channel.group;

import java.util.Set;
import pro.gravit.repackage.io.netty.channel.Channel;
import pro.gravit.repackage.io.netty.channel.ChannelId;

public interface ChannelGroup extends Set<Channel>, Comparable<ChannelGroup> {
  String name();
  
  Channel find(ChannelId paramChannelId);
  
  ChannelGroupFuture write(Object paramObject);
  
  ChannelGroupFuture write(Object paramObject, ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture write(Object paramObject, ChannelMatcher paramChannelMatcher, boolean paramBoolean);
  
  ChannelGroup flush();
  
  ChannelGroup flush(ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture writeAndFlush(Object paramObject);
  
  @Deprecated
  ChannelGroupFuture flushAndWrite(Object paramObject);
  
  ChannelGroupFuture writeAndFlush(Object paramObject, ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture writeAndFlush(Object paramObject, ChannelMatcher paramChannelMatcher, boolean paramBoolean);
  
  @Deprecated
  ChannelGroupFuture flushAndWrite(Object paramObject, ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture disconnect();
  
  ChannelGroupFuture disconnect(ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture close();
  
  ChannelGroupFuture close(ChannelMatcher paramChannelMatcher);
  
  @Deprecated
  ChannelGroupFuture deregister();
  
  @Deprecated
  ChannelGroupFuture deregister(ChannelMatcher paramChannelMatcher);
  
  ChannelGroupFuture newCloseFuture();
  
  ChannelGroupFuture newCloseFuture(ChannelMatcher paramChannelMatcher);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\group\ChannelGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */