package org.apache.http;

import java.net.InetAddress;

public interface HttpInetConnection extends HttpConnection {
  InetAddress getLocalAddress();
  
  int getLocalPort();
  
  InetAddress getRemoteAddress();
  
  int getRemotePort();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\HttpInetConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */