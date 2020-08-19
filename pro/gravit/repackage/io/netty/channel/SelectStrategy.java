package pro.gravit.repackage.io.netty.channel;

import pro.gravit.repackage.io.netty.util.IntSupplier;

public interface SelectStrategy {
  public static final int SELECT = -1;
  
  public static final int CONTINUE = -2;
  
  public static final int BUSY_WAIT = -3;
  
  int calculateStrategy(IntSupplier paramIntSupplier, boolean paramBoolean) throws Exception;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\SelectStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */