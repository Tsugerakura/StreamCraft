package pro.gravit.repackage.io.netty.handler.codec.spdy;

public interface SpdyGoAwayFrame extends SpdyFrame {
  int lastGoodStreamId();
  
  SpdyGoAwayFrame setLastGoodStreamId(int paramInt);
  
  SpdySessionStatus status();
  
  SpdyGoAwayFrame setStatus(SpdySessionStatus paramSpdySessionStatus);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyGoAwayFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */