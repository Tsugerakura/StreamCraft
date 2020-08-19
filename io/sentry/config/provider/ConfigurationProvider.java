package io.sentry.config.provider;

import io.sentry.util.Nullable;

public interface ConfigurationProvider {
  @Nullable
  String getProperty(String paramString);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\config\provider\ConfigurationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */