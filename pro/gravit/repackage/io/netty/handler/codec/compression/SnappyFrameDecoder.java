/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
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
/*     */ public class SnappyFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private static final int SNAPPY_IDENTIFIER_LEN = 6;
/*     */   private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
/*     */   
/*     */   private enum ChunkType
/*     */   {
/*  40 */     STREAM_IDENTIFIER,
/*  41 */     COMPRESSED_DATA,
/*  42 */     UNCOMPRESSED_DATA,
/*  43 */     RESERVED_UNSKIPPABLE,
/*  44 */     RESERVED_SKIPPABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   private final Snappy snappy = new Snappy();
/*     */ 
/*     */   
/*     */   private final boolean validateChecksums;
/*     */ 
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   private boolean corrupted;
/*     */ 
/*     */   
/*     */   public SnappyFrameDecoder() {
/*  62 */     this(false);
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
/*     */   public SnappyFrameDecoder(boolean validateChecksums) {
/*  75 */     this.validateChecksums = validateChecksums;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  80 */     if (this.corrupted) {
/*  81 */       in.skipBytes(in.readableBytes()); return;
/*     */     } 
/*     */     try {
/*     */       int offset, checksum;
/*     */       ByteBuf uncompressed;
/*  86 */       int idx = in.readerIndex();
/*  87 */       int inSize = in.readableBytes();
/*  88 */       if (inSize < 4) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  94 */       int chunkTypeVal = in.getUnsignedByte(idx);
/*  95 */       ChunkType chunkType = mapChunkType((byte)chunkTypeVal);
/*  96 */       int chunkLength = in.getUnsignedMediumLE(idx + 1);
/*     */       
/*  98 */       switch (chunkType) {
/*     */         case STREAM_IDENTIFIER:
/* 100 */           if (chunkLength != 6) {
/* 101 */             throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
/*     */           }
/*     */           
/* 104 */           if (inSize < 10) {
/*     */             break;
/*     */           }
/*     */           
/* 108 */           in.skipBytes(4);
/* 109 */           offset = in.readerIndex();
/* 110 */           in.skipBytes(6);
/*     */           
/* 112 */           checkByte(in.getByte(offset++), (byte)115);
/* 113 */           checkByte(in.getByte(offset++), (byte)78);
/* 114 */           checkByte(in.getByte(offset++), (byte)97);
/* 115 */           checkByte(in.getByte(offset++), (byte)80);
/* 116 */           checkByte(in.getByte(offset++), (byte)112);
/* 117 */           checkByte(in.getByte(offset), (byte)89);
/*     */           
/* 119 */           this.started = true;
/*     */           break;
/*     */         case RESERVED_SKIPPABLE:
/* 122 */           if (!this.started) {
/* 123 */             throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
/*     */           }
/*     */           
/* 126 */           if (inSize < 4 + chunkLength) {
/*     */             return;
/*     */           }
/*     */ 
/*     */           
/* 131 */           in.skipBytes(4 + chunkLength);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case RESERVED_UNSKIPPABLE:
/* 137 */           throw new DecompressionException("Found reserved unskippable chunk type: 0x" + 
/* 138 */               Integer.toHexString(chunkTypeVal));
/*     */         case UNCOMPRESSED_DATA:
/* 140 */           if (!this.started) {
/* 141 */             throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
/*     */           }
/* 143 */           if (chunkLength > 65540) {
/* 144 */             throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
/*     */           }
/*     */           
/* 147 */           if (inSize < 4 + chunkLength) {
/*     */             return;
/*     */           }
/*     */           
/* 151 */           in.skipBytes(4);
/* 152 */           if (this.validateChecksums) {
/* 153 */             int i = in.readIntLE();
/* 154 */             Snappy.validateChecksum(i, in, in.readerIndex(), chunkLength - 4);
/*     */           } else {
/* 156 */             in.skipBytes(4);
/*     */           } 
/* 158 */           out.add(in.readRetainedSlice(chunkLength - 4));
/*     */           break;
/*     */         case COMPRESSED_DATA:
/* 161 */           if (!this.started) {
/* 162 */             throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
/*     */           }
/*     */           
/* 165 */           if (inSize < 4 + chunkLength) {
/*     */             return;
/*     */           }
/*     */           
/* 169 */           in.skipBytes(4);
/* 170 */           checksum = in.readIntLE();
/* 171 */           uncompressed = ctx.alloc().buffer();
/*     */           try {
/* 173 */             if (this.validateChecksums) {
/* 174 */               int oldWriterIndex = in.writerIndex();
/*     */               try {
/* 176 */                 in.writerIndex(in.readerIndex() + chunkLength - 4);
/* 177 */                 this.snappy.decode(in, uncompressed);
/*     */               } finally {
/* 179 */                 in.writerIndex(oldWriterIndex);
/*     */               } 
/* 181 */               Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
/*     */             } else {
/* 183 */               this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
/*     */             } 
/* 185 */             out.add(uncompressed);
/* 186 */             uncompressed = null;
/*     */           } finally {
/* 188 */             if (uncompressed != null) {
/* 189 */               uncompressed.release();
/*     */             }
/*     */           } 
/* 192 */           this.snappy.reset();
/*     */           break;
/*     */       } 
/* 195 */     } catch (Exception e) {
/* 196 */       this.corrupted = true;
/* 197 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void checkByte(byte actual, byte expect) {
/* 202 */     if (actual != expect) {
/* 203 */       throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ChunkType mapChunkType(byte type) {
/* 215 */     if (type == 0)
/* 216 */       return ChunkType.COMPRESSED_DATA; 
/* 217 */     if (type == 1)
/* 218 */       return ChunkType.UNCOMPRESSED_DATA; 
/* 219 */     if (type == -1)
/* 220 */       return ChunkType.STREAM_IDENTIFIER; 
/* 221 */     if ((type & 0x80) == 128) {
/* 222 */       return ChunkType.RESERVED_SKIPPABLE;
/*     */     }
/* 224 */     return ChunkType.RESERVED_UNSKIPPABLE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\SnappyFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */