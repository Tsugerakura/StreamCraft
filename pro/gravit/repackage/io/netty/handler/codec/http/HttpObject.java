package pro.gravit.repackage.io.netty.handler.codec.http;

import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
import pro.gravit.repackage.io.netty.handler.codec.DecoderResultProvider;

public interface HttpObject extends DecoderResultProvider {
  @Deprecated
  DecoderResult getDecoderResult();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */