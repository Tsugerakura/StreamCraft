package org.apache.http.conn;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpClientConnection;
import org.apache.http.concurrent.Cancellable;

public interface ConnectionRequest extends Cancellable {
  HttpClientConnection get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\conn\ConnectionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */