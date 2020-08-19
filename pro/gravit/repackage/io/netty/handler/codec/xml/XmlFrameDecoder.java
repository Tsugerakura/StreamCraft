/*     */ package pro.gravit.repackage.io.netty.handler.codec.xml;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CorruptedFrameException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XmlFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private final int maxFrameLength;
/*     */   
/*     */   public XmlFrameDecoder(int maxFrameLength) {
/*  81 */     if (maxFrameLength < 1) {
/*  82 */       throw new IllegalArgumentException("maxFrameLength must be a positive int");
/*     */     }
/*  84 */     this.maxFrameLength = maxFrameLength;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  89 */     boolean openingBracketFound = false;
/*  90 */     boolean atLeastOneXmlElementFound = false;
/*  91 */     boolean inCDATASection = false;
/*  92 */     long openBracketsCount = 0L;
/*  93 */     int length = 0;
/*  94 */     int leadingWhiteSpaceCount = 0;
/*  95 */     int bufferLength = in.writerIndex();
/*     */     
/*  97 */     if (bufferLength > this.maxFrameLength) {
/*     */       
/*  99 */       in.skipBytes(in.readableBytes());
/* 100 */       fail(bufferLength);
/*     */       
/*     */       return;
/*     */     } 
/* 104 */     for (int i = in.readerIndex(); i < bufferLength; i++) {
/* 105 */       byte readByte = in.getByte(i);
/* 106 */       if (!openingBracketFound && Character.isWhitespace(readByte))
/*     */       
/* 108 */       { leadingWhiteSpaceCount++; }
/* 109 */       else { if (!openingBracketFound && readByte != 60) {
/*     */           
/* 111 */           fail(ctx);
/* 112 */           in.skipBytes(in.readableBytes()); return;
/*     */         } 
/* 114 */         if (!inCDATASection && readByte == 60) {
/* 115 */           openingBracketFound = true;
/*     */           
/* 117 */           if (i < bufferLength - 1) {
/* 118 */             byte peekAheadByte = in.getByte(i + 1);
/* 119 */             if (peekAheadByte == 47) {
/*     */               
/* 121 */               int peekFurtherAheadIndex = i + 2;
/* 122 */               while (peekFurtherAheadIndex <= bufferLength - 1) {
/*     */                 
/* 124 */                 if (in.getByte(peekFurtherAheadIndex) == 62) {
/* 125 */                   openBracketsCount--;
/*     */                   break;
/*     */                 } 
/* 128 */                 peekFurtherAheadIndex++;
/*     */               } 
/* 130 */             } else if (isValidStartCharForXmlElement(peekAheadByte)) {
/* 131 */               atLeastOneXmlElementFound = true;
/*     */ 
/*     */               
/* 134 */               openBracketsCount++;
/* 135 */             } else if (peekAheadByte == 33) {
/* 136 */               if (isCommentBlockStart(in, i)) {
/*     */                 
/* 138 */                 openBracketsCount++;
/* 139 */               } else if (isCDATABlockStart(in, i)) {
/*     */                 
/* 141 */                 openBracketsCount++;
/* 142 */                 inCDATASection = true;
/*     */               } 
/* 144 */             } else if (peekAheadByte == 63) {
/*     */               
/* 146 */               openBracketsCount++;
/*     */             } 
/*     */           } 
/* 149 */         } else if (!inCDATASection && readByte == 47) {
/* 150 */           if (i < bufferLength - 1 && in.getByte(i + 1) == 62)
/*     */           {
/* 152 */             openBracketsCount--;
/*     */           }
/* 154 */         } else if (readByte == 62) {
/* 155 */           length = i + 1;
/*     */           
/* 157 */           if (i - 1 > -1) {
/* 158 */             byte peekBehindByte = in.getByte(i - 1);
/*     */             
/* 160 */             if (!inCDATASection) {
/* 161 */               if (peekBehindByte == 63) {
/*     */                 
/* 163 */                 openBracketsCount--;
/* 164 */               } else if (peekBehindByte == 45 && i - 2 > -1 && in.getByte(i - 2) == 45) {
/*     */                 
/* 166 */                 openBracketsCount--;
/*     */               } 
/* 168 */             } else if (peekBehindByte == 93 && i - 2 > -1 && in.getByte(i - 2) == 93) {
/*     */               
/* 170 */               openBracketsCount--;
/* 171 */               inCDATASection = false;
/*     */             } 
/*     */           } 
/*     */           
/* 175 */           if (atLeastOneXmlElementFound && openBracketsCount == 0L) {
/*     */             break;
/*     */           }
/*     */         }  }
/*     */     
/*     */     } 
/*     */     
/* 182 */     int readerIndex = in.readerIndex();
/* 183 */     int xmlElementLength = length - readerIndex;
/*     */     
/* 185 */     if (openBracketsCount == 0L && xmlElementLength > 0) {
/* 186 */       if (readerIndex + xmlElementLength >= bufferLength) {
/* 187 */         xmlElementLength = in.readableBytes();
/*     */       }
/*     */       
/* 190 */       ByteBuf frame = extractFrame(in, readerIndex + leadingWhiteSpaceCount, xmlElementLength - leadingWhiteSpaceCount);
/* 191 */       in.skipBytes(xmlElementLength);
/* 192 */       out.add(frame);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fail(long frameLength) {
/* 197 */     if (frameLength > 0L) {
/* 198 */       throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
/*     */     }
/*     */     
/* 201 */     throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void fail(ChannelHandlerContext ctx) {
/* 207 */     ctx.fireExceptionCaught((Throwable)new CorruptedFrameException("frame contains content before the xml starts"));
/*     */   }
/*     */   
/*     */   private static ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
/* 211 */     return buffer.copy(index, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isValidStartCharForXmlElement(byte b) {
/* 226 */     return ((b >= 97 && b <= 122) || (b >= 65 && b <= 90) || b == 58 || b == 95);
/*     */   }
/*     */   
/*     */   private static boolean isCommentBlockStart(ByteBuf in, int i) {
/* 230 */     return (i < in.writerIndex() - 3 && in
/* 231 */       .getByte(i + 2) == 45 && in
/* 232 */       .getByte(i + 3) == 45);
/*     */   }
/*     */   
/*     */   private static boolean isCDATABlockStart(ByteBuf in, int i) {
/* 236 */     return (i < in.writerIndex() - 8 && in
/* 237 */       .getByte(i + 2) == 91 && in
/* 238 */       .getByte(i + 3) == 67 && in
/* 239 */       .getByte(i + 4) == 68 && in
/* 240 */       .getByte(i + 5) == 65 && in
/* 241 */       .getByte(i + 6) == 84 && in
/* 242 */       .getByte(i + 7) == 65 && in
/* 243 */       .getByte(i + 8) == 91);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\xml\XmlFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */