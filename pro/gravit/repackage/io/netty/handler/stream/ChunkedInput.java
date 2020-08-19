package pro.gravit.repackage.io.netty.handler.stream;

import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;

public interface ChunkedInput<B> {
  boolean isEndOfInput() throws Exception;
  
  void close() throws Exception;
  
  @Deprecated
  B readChunk(ChannelHandlerContext paramChannelHandlerContext) throws Exception;
  
  B readChunk(ByteBufAllocator paramByteBufAllocator) throws Exception;
  
  long length();
  
  long progress();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */