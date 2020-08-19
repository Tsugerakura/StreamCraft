package io.sentry.event.helper;

import io.sentry.event.Event;

public interface ShouldSendEventCallback {
  boolean shouldSend(Event paramEvent);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\helper\ShouldSendEventCallback.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */