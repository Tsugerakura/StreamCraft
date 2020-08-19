package pro.gravit.repackage.io.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;

public interface MarshallerProvider {
  Marshaller getMarshaller(ChannelHandlerContext paramChannelHandlerContext) throws Exception;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\marshalling\MarshallerProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */