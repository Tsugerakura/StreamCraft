package org.apache.http;

import org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy {
  boolean keepAlive(HttpResponse paramHttpResponse, HttpContext paramHttpContext);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\ConnectionReuseStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */