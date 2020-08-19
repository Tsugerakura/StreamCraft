/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*    */ import pro.gravit.repackage.io.netty.channel.embedded.EmbeddedChannel;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibCodecFactory;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibWrapper;
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
/*    */ public class HttpContentDecompressor
/*    */   extends HttpContentDecoder
/*    */ {
/*    */   private final boolean strict;
/*    */   
/*    */   public HttpContentDecompressor() {
/* 40 */     this(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpContentDecompressor(boolean strict) {
/* 50 */     this.strict = strict;
/*    */   }
/*    */ 
/*    */   
/*    */   protected EmbeddedChannel newContentDecoder(String contentEncoding) throws Exception {
/* 55 */     if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_GZIP
/* 56 */       .contentEqualsIgnoreCase(contentEncoding)) {
/* 57 */       return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx
/* 58 */           .channel().config(), new ChannelHandler[] { (ChannelHandler)ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP) });
/*    */     }
/* 60 */     if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_DEFLATE
/* 61 */       .contentEqualsIgnoreCase(contentEncoding)) {
/* 62 */       ZlibWrapper wrapper = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
/*    */       
/* 64 */       return new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx
/* 65 */           .channel().config(), new ChannelHandler[] { (ChannelHandler)ZlibCodecFactory.newZlibDecoder(wrapper) });
/*    */     } 
/*    */ 
/*    */     
/* 69 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpContentDecompressor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */