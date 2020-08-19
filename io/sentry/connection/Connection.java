package io.sentry.connection;

import io.sentry.event.Event;
import java.io.Closeable;

public interface Connection extends Closeable {
  void send(Event paramEvent) throws ConnectionException;
  
  void addEventSendCallback(EventSendCallback paramEventSendCallback);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\Connection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */