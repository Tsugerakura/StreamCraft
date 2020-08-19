package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.concurrent.Future;
import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
import pro.gravit.repackage.io.netty.util.concurrent.ProgressivePromise;

public interface ChannelProgressivePromise extends ProgressivePromise<Void>, ChannelProgressiveFuture, ChannelPromise {
  ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelProgressivePromise sync() throws InterruptedException;
  
  ChannelProgressivePromise syncUninterruptibly();
  
  ChannelProgressivePromise await() throws InterruptedException;
  
  ChannelProgressivePromise awaitUninterruptibly();
  
  ChannelProgressivePromise setSuccess(Void paramVoid);
  
  ChannelProgressivePromise setSuccess();
  
  ChannelProgressivePromise setFailure(Throwable paramThrowable);
  
  ChannelProgressivePromise setProgress(long paramLong1, long paramLong2);
  
  ChannelProgressivePromise unvoid();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelProgressivePromise.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */