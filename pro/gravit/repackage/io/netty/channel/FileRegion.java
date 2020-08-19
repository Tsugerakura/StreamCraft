package pro.gravit.repackage.io.netty.channel;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import pro.gravit.repackage.io.netty.util.ReferenceCounted;

public interface FileRegion extends ReferenceCounted {
  long position();
  
  @Deprecated
  long transfered();
  
  long transferred();
  
  long count();
  
  long transferTo(WritableByteChannel paramWritableByteChannel, long paramLong) throws IOException;
  
  FileRegion retain();
  
  FileRegion retain(int paramInt);
  
  FileRegion touch();
  
  FileRegion touch(Object paramObject);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\FileRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */