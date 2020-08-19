package io.sentry.buffer;

import io.sentry.event.Event;
import java.util.Iterator;

public interface Buffer {
  void add(Event paramEvent);
  
  void discard(Event paramEvent);
  
  Iterator<Event> getEvents();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\buffer\Buffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */