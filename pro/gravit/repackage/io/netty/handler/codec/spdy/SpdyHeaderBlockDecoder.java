/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*    */ abstract class SpdyHeaderBlockDecoder
/*    */ {
/*    */   static SpdyHeaderBlockDecoder newInstance(SpdyVersion spdyVersion, int maxHeaderSize) {
/* 24 */     return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
/*    */   }
/*    */   
/*    */   abstract void decode(ByteBufAllocator paramByteBufAllocator, ByteBuf paramByteBuf, SpdyHeadersFrame paramSpdyHeadersFrame) throws Exception;
/*    */   
/*    */   abstract void endHeaderBlock(SpdyHeadersFrame paramSpdyHeadersFrame) throws Exception;
/*    */   
/*    */   abstract void end();
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHeaderBlockDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */