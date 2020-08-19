/*    */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectEncoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Sharable
/*    */ @Deprecated
/*    */ public abstract class RtspObjectEncoder<H extends HttpMessage>
/*    */   extends HttpObjectEncoder<H>
/*    */ {
/*    */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/* 42 */     return msg instanceof pro.gravit.repackage.io.netty.handler.codec.http.FullHttpMessage;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspObjectEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */