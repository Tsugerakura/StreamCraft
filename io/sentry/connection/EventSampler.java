package io.sentry.connection;

import io.sentry.event.Event;

public interface EventSampler {
  boolean shouldSendEvent(Event paramEvent);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\EventSampler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */