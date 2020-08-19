package io.sentry.connection;

import io.sentry.event.Event;

public interface EventSendCallback {
  void onFailure(Event paramEvent, Exception paramException);
  
  void onSuccess(Event paramEvent);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\EventSendCallback.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */