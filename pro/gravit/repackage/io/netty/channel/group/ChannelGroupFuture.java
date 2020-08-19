package pro.gravit.repackage.io.netty.channel.group;

import java.util.Iterator;
import pro.gravit.repackage.io.netty.channel.Channel;
import pro.gravit.repackage.io.netty.channel.ChannelFuture;
import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;

public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture> {
  ChannelGroup group();
  
  ChannelFuture find(Channel paramChannel);
  
  boolean isSuccess();
  
  ChannelGroupException cause();
  
  boolean isPartialSuccess();
  
  boolean isPartialFailure();
  
  ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelGroupFuture await() throws InterruptedException;
  
  ChannelGroupFuture awaitUninterruptibly();
  
  ChannelGroupFuture syncUninterruptibly();
  
  ChannelGroupFuture sync() throws InterruptedException;
  
  Iterator<ChannelFuture> iterator();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\group\ChannelGroupFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */