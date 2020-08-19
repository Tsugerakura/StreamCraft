package org.apache.http;

import java.util.Iterator;

public interface TokenIterator extends Iterator<Object> {
  boolean hasNext();
  
  String nextToken();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\TokenIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */