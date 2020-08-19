package org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C> {
  C create(T paramT) throws IOException;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\pool\ConnFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */