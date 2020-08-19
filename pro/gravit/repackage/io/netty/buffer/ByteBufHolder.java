package pro.gravit.repackage.io.netty.buffer;

import pro.gravit.repackage.io.netty.util.ReferenceCounted;

public interface ByteBufHolder extends ReferenceCounted {
  ByteBuf content();
  
  ByteBufHolder copy();
  
  ByteBufHolder duplicate();
  
  ByteBufHolder retainedDuplicate();
  
  ByteBufHolder replace(ByteBuf paramByteBuf);
  
  ByteBufHolder retain();
  
  ByteBufHolder retain(int paramInt);
  
  ByteBufHolder touch();
  
  ByteBufHolder touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */