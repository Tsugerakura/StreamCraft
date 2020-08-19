package org.apache.http;

import org.apache.http.util.CharArrayBuffer;

public interface FormattedHeader extends Header {
  CharArrayBuffer getBuffer();
  
  int getValuePos();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\FormattedHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */