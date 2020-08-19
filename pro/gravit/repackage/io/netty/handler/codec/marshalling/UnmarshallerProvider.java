package pro.gravit.repackage.io.netty.handler.codec.marshalling;

import org.jboss.marshalling.Unmarshaller;
import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;

public interface UnmarshallerProvider {
  Unmarshaller getUnmarshaller(ChannelHandlerContext paramChannelHandlerContext) throws Exception;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\marshalling\UnmarshallerProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */