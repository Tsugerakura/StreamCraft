/*    */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
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
/*    */ final class CompressionUtil
/*    */ {
/*    */   static void checkChecksum(ByteBufChecksum checksum, ByteBuf uncompressed, int currentChecksum) {
/* 27 */     checksum.reset();
/* 28 */     checksum.update(uncompressed, uncompressed
/* 29 */         .readerIndex(), uncompressed.readableBytes());
/*    */     
/* 31 */     int checksumResult = (int)checksum.getValue();
/* 32 */     if (checksumResult != currentChecksum)
/* 33 */       throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", new Object[] {
/*    */               
/* 35 */               Integer.valueOf(checksumResult), Integer.valueOf(currentChecksum)
/*    */             })); 
/*    */   }
/*    */   
/*    */   static ByteBuffer safeNioBuffer(ByteBuf buffer) {
/* 40 */     return (buffer.nioBufferCount() == 1) ? buffer.internalNioBuffer(buffer.readerIndex(), buffer.readableBytes()) : buffer
/* 41 */       .nioBuffer();
/*    */   }
/*    */   
/*    */   static ByteBuffer safeNioBuffer(ByteBuf buffer, int index, int length) {
/* 45 */     return (buffer.nioBufferCount() == 1) ? buffer.internalNioBuffer(index, length) : buffer
/* 46 */       .nioBuffer(index, length);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\CompressionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */