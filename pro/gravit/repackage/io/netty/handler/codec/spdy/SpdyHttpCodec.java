/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandler;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*    */ import pro.gravit.repackage.io.netty.channel.CombinedChannelDuplexHandler;
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
/*    */ public final class SpdyHttpCodec
/*    */   extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder>
/*    */ {
/*    */   public SpdyHttpCodec(SpdyVersion version, int maxContentLength) {
/* 28 */     super((ChannelInboundHandler)new SpdyHttpDecoder(version, maxContentLength), (ChannelOutboundHandler)new SpdyHttpEncoder(version));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SpdyHttpCodec(SpdyVersion version, int maxContentLength, boolean validateHttpHeaders) {
/* 35 */     super((ChannelInboundHandler)new SpdyHttpDecoder(version, maxContentLength, validateHttpHeaders), (ChannelOutboundHandler)new SpdyHttpEncoder(version));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHttpCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */