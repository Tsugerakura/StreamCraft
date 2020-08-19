package io.sentry.marshaller.json;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

public interface InterfaceBinding<T extends io.sentry.event.interfaces.SentryInterface> {
  void writeInterface(JsonGenerator paramJsonGenerator, T paramT) throws IOException;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\InterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */