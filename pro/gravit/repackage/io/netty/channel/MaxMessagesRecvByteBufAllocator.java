package pro.gravit.repackage.io.netty.channel;

public interface MaxMessagesRecvByteBufAllocator extends RecvByteBufAllocator {
  int maxMessagesPerRead();
  
  MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int paramInt);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\MaxMessagesRecvByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */