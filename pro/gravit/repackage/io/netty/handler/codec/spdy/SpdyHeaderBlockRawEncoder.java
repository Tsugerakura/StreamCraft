/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import java.util.Set;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ public class SpdyHeaderBlockRawEncoder
/*    */   extends SpdyHeaderBlockEncoder
/*    */ {
/*    */   private final int version;
/*    */   
/*    */   public SpdyHeaderBlockRawEncoder(SpdyVersion version) {
/* 33 */     this.version = ((SpdyVersion)ObjectUtil.checkNotNull(version, "version")).getVersion();
/*    */   }
/*    */   
/*    */   private static void setLengthField(ByteBuf buffer, int writerIndex, int length) {
/* 37 */     buffer.setInt(writerIndex, length);
/*    */   }
/*    */   
/*    */   private static void writeLengthField(ByteBuf buffer, int length) {
/* 41 */     buffer.writeInt(length);
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
/* 46 */     Set<CharSequence> names = frame.headers().names();
/* 47 */     int numHeaders = names.size();
/* 48 */     if (numHeaders == 0) {
/* 49 */       return Unpooled.EMPTY_BUFFER;
/*    */     }
/* 51 */     if (numHeaders > 65535) {
/* 52 */       throw new IllegalArgumentException("header block contains too many headers");
/*    */     }
/*    */     
/* 55 */     ByteBuf headerBlock = alloc.heapBuffer();
/* 56 */     writeLengthField(headerBlock, numHeaders);
/* 57 */     for (CharSequence name : names) {
/* 58 */       writeLengthField(headerBlock, name.length());
/* 59 */       ByteBufUtil.writeAscii(headerBlock, name);
/* 60 */       int savedIndex = headerBlock.writerIndex();
/* 61 */       int valueLength = 0;
/* 62 */       writeLengthField(headerBlock, valueLength);
/* 63 */       for (CharSequence value : frame.headers().getAll(name)) {
/* 64 */         int length = value.length();
/* 65 */         if (length > 0) {
/* 66 */           ByteBufUtil.writeAscii(headerBlock, value);
/* 67 */           headerBlock.writeByte(0);
/* 68 */           valueLength += length + 1;
/*    */         } 
/*    */       } 
/* 71 */       if (valueLength != 0) {
/* 72 */         valueLength--;
/*    */       }
/* 74 */       if (valueLength > 65535) {
/* 75 */         throw new IllegalArgumentException("header exceeds allowable length: " + name);
/*    */       }
/*    */       
/* 78 */       if (valueLength > 0) {
/* 79 */         setLengthField(headerBlock, savedIndex, valueLength);
/* 80 */         headerBlock.writerIndex(headerBlock.writerIndex() - 1);
/*    */       } 
/*    */     } 
/* 83 */     return headerBlock;
/*    */   }
/*    */   
/*    */   void end() {}
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHeaderBlockRawEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */