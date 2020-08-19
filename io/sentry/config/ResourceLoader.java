package io.sentry.config;

import io.sentry.util.Nullable;
import java.io.InputStream;

public interface ResourceLoader {
  @Nullable
  InputStream getInputStream(String paramString);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\ResourceLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */