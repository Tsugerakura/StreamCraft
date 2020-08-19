package com.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ByteBufferFeeder extends NonBlockingInputFeeder {
  void feedInput(ByteBuffer paramByteBuffer) throws IOException;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\async\ByteBufferFeeder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */