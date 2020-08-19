package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
import pro.gravit.repackage.io.netty.util.concurrent.Promise;

public interface ChannelPromise extends ChannelFuture, Promise<Void> {
  Channel channel();
  
  ChannelPromise setSuccess(Void paramVoid);
  
  ChannelPromise setSuccess();
  
  boolean trySuccess();
  
  ChannelPromise setFailure(Throwable paramThrowable);
  
  ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelPromise sync() throws InterruptedException;
  
  ChannelPromise syncUninterruptibly();
  
  ChannelPromise await() throws InterruptedException;
  
  ChannelPromise awaitUninterruptibly();
  
  ChannelPromise unvoid();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelPromise.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */