package pro.gravit.repackage.io.netty.handler.ssl;

import java.security.cert.X509Certificate;
import pro.gravit.repackage.io.netty.util.ReferenceCounted;

interface OpenSslKeyMaterial extends ReferenceCounted {
  X509Certificate[] certificateChain();
  
  long certificateChainAddress();
  
  long privateKeyAddress();
  
  OpenSslKeyMaterial retain();
  
  OpenSslKeyMaterial retain(int paramInt);
  
  OpenSslKeyMaterial touch();
  
  OpenSslKeyMaterial touch(Object paramObject);
  
  boolean release();
  
  boolean release(int paramInt);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslKeyMaterial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */