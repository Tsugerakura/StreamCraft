package org.apache.http.conn;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

@Deprecated
public interface ClientConnectionManagerFactory {
  ClientConnectionManager newInstance(HttpParams paramHttpParams, SchemeRegistry paramSchemeRegistry);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\conn\ClientConnectionManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */