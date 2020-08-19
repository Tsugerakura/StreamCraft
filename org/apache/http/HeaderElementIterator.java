package org.apache.http;

import java.util.Iterator;

public interface HeaderElementIterator extends Iterator<Object> {
  boolean hasNext();
  
  HeaderElement nextElement();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\HeaderElementIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */