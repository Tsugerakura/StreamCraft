package pro.gravit.repackage.io.netty.handler.ssl;

import pro.gravit.repackage.io.netty.buffer.ByteBuf;
import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;

interface PemEncoded extends ByteBufHolder {
  boolean isSensitive();
  
  PemEncoded copy();
  
  PemEncoded duplicate();
  
  PemEncoded retainedDuplicate();
  
  PemEncoded replace(ByteBuf paramByteBuf);
  
  PemEncoded retain();
  
  PemEncoded retain(int paramInt);
  
  PemEncoded touch();
  
  PemEncoded touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\PemEncoded.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */