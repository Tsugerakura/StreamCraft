package pro.gravit.repackage.io.netty.handler.ssl;

import java.util.List;
import java.util.Set;

public interface CipherSuiteFilter {
  String[] filterCipherSuites(Iterable<String> paramIterable, List<String> paramList, Set<String> paramSet);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\CipherSuiteFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */