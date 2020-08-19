package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
import pro.gravit.repackage.io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture extends ChannelFuture, ProgressiveFuture<Void> {
  ChannelProgressiveFuture addListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelProgressiveFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelProgressiveFuture removeListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelProgressiveFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelProgressiveFuture sync() throws InterruptedException;
  
  ChannelProgressiveFuture syncUninterruptibly();
  
  ChannelProgressiveFuture await() throws InterruptedException;
  
  ChannelProgressiveFuture awaitUninterruptibly();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelProgressiveFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */