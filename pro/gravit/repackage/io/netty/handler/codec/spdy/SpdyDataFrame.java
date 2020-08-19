package pro.gravit.repackage.io.netty.handler.codec.spdy;

import pro.gravit.repackage.io.netty.buffer.ByteBuf;
import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;

public interface SpdyDataFrame extends ByteBufHolder, SpdyStreamFrame {
  SpdyDataFrame setStreamId(int paramInt);
  
  SpdyDataFrame setLast(boolean paramBoolean);
  
  ByteBuf content();
  
  SpdyDataFrame copy();
  
  SpdyDataFrame duplicate();
  
  SpdyDataFrame retainedDuplicate();
  
  SpdyDataFrame replace(ByteBuf paramByteBuf);
  
  SpdyDataFrame retain();
  
  SpdyDataFrame retain(int paramInt);
  
  SpdyDataFrame touch();
  
  SpdyDataFrame touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyDataFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */