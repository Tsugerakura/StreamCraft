package org.apache.http.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy {
  String chooseAlias(Map<String, PrivateKeyDetails> paramMap, Socket paramSocket);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\ssl\PrivateKeyStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */