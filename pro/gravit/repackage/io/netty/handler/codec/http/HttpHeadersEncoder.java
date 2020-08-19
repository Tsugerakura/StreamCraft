/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*    */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*    */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
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
/*    */ final class HttpHeadersEncoder
/*    */ {
/*    */   private static final int COLON_AND_SPACE_SHORT = 14880;
/*    */   
/*    */   static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf) {
/* 34 */     int nameLen = name.length();
/* 35 */     int valueLen = value.length();
/* 36 */     int entryLen = nameLen + valueLen + 4;
/* 37 */     buf.ensureWritable(entryLen);
/* 38 */     int offset = buf.writerIndex();
/* 39 */     writeAscii(buf, offset, name);
/* 40 */     offset += nameLen;
/* 41 */     ByteBufUtil.setShortBE(buf, offset, 14880);
/* 42 */     offset += 2;
/* 43 */     writeAscii(buf, offset, value);
/* 44 */     offset += valueLen;
/* 45 */     ByteBufUtil.setShortBE(buf, offset, 3338);
/* 46 */     offset += 2;
/* 47 */     buf.writerIndex(offset);
/*    */   }
/*    */   
/*    */   private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
/* 51 */     if (value instanceof AsciiString) {
/* 52 */       ByteBufUtil.copy((AsciiString)value, 0, buf, offset, value.length());
/*    */     } else {
/* 54 */       buf.setCharSequence(offset, value, CharsetUtil.US_ASCII);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpHeadersEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */