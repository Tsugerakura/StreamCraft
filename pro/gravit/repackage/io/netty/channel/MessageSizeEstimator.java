package pro.gravit.repackage.io.netty.channel;

public interface MessageSizeEstimator {
  Handle newHandle();
  
  public static interface Handle {
    int size(Object param1Object);
  }
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\MessageSizeEstimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */