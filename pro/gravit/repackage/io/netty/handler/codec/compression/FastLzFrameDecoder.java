/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.zip.Adler32;
/*     */ import java.util.zip.Checksum;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
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
/*     */ public class FastLzFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private enum State
/*     */   {
/*  39 */     INIT_BLOCK,
/*  40 */     INIT_BLOCK_PARAMS,
/*  41 */     DECOMPRESS_DATA,
/*  42 */     CORRUPTED;
/*     */   }
/*     */   
/*  45 */   private State currentState = State.INIT_BLOCK;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Checksum checksum;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int chunkLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int originalLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isCompressed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasChecksum;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int currentChecksum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastLzFrameDecoder() {
/*  82 */     this(false);
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
/*     */   public FastLzFrameDecoder(boolean validateChecksums) {
/*  96 */     this(validateChecksums ? new Adler32() : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastLzFrameDecoder(Checksum checksum) {
/* 107 */     this.checksum = checksum;
/*     */   } protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*     */     try {
/*     */       int magic;
/*     */       byte options;
/*     */       int chunkLength;
/* 113 */       switch (this.currentState) {
/*     */         case INIT_BLOCK:
/* 115 */           if (in.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 119 */           magic = in.readUnsignedMedium();
/* 120 */           if (magic != 4607066) {
/* 121 */             throw new DecompressionException("unexpected block identifier");
/*     */           }
/*     */           
/* 124 */           options = in.readByte();
/* 125 */           this.isCompressed = ((options & 0x1) == 1);
/* 126 */           this.hasChecksum = ((options & 0x10) == 16);
/*     */           
/* 128 */           this.currentState = State.INIT_BLOCK_PARAMS;
/*     */         
/*     */         case INIT_BLOCK_PARAMS:
/* 131 */           if (in.readableBytes() < 2 + (this.isCompressed ? 2 : 0) + (this.hasChecksum ? 4 : 0)) {
/*     */             return;
/*     */           }
/* 134 */           this.currentChecksum = this.hasChecksum ? in.readInt() : 0;
/* 135 */           this.chunkLength = in.readUnsignedShort();
/* 136 */           this.originalLength = this.isCompressed ? in.readUnsignedShort() : this.chunkLength;
/*     */           
/* 138 */           this.currentState = State.DECOMPRESS_DATA;
/*     */         
/*     */         case DECOMPRESS_DATA:
/* 141 */           chunkLength = this.chunkLength;
/* 142 */           if (in.readableBytes() >= chunkLength) {
/*     */             ByteBuf uncompressed;
/*     */             
/*     */             byte[] output;
/* 146 */             int outputPtr, idx = in.readerIndex();
/* 147 */             int originalLength = this.originalLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 153 */             if (originalLength != 0) {
/* 154 */               uncompressed = ctx.alloc().heapBuffer(originalLength, originalLength);
/* 155 */               output = uncompressed.array();
/* 156 */               outputPtr = uncompressed.arrayOffset() + uncompressed.writerIndex();
/*     */             } else {
/* 158 */               uncompressed = null;
/* 159 */               output = EmptyArrays.EMPTY_BYTES;
/* 160 */               outputPtr = 0;
/*     */             } 
/*     */             
/* 163 */             boolean success = false;
/*     */             try {
/* 165 */               if (this.isCompressed) {
/*     */                 byte[] input;
/*     */                 int inputPtr;
/* 168 */                 if (in.hasArray()) {
/* 169 */                   input = in.array();
/* 170 */                   inputPtr = in.arrayOffset() + idx;
/*     */                 } else {
/* 172 */                   input = new byte[chunkLength];
/* 173 */                   in.getBytes(idx, input);
/* 174 */                   inputPtr = 0;
/*     */                 } 
/*     */                 
/* 177 */                 int decompressedBytes = FastLz.decompress(input, inputPtr, chunkLength, output, outputPtr, originalLength);
/*     */                 
/* 179 */                 if (originalLength != decompressedBytes)
/* 180 */                   throw new DecompressionException(String.format("stream corrupted: originalLength(%d) and actual length(%d) mismatch", new Object[] {
/*     */                           
/* 182 */                           Integer.valueOf(originalLength), Integer.valueOf(decompressedBytes)
/*     */                         })); 
/*     */               } else {
/* 185 */                 in.getBytes(idx, output, outputPtr, chunkLength);
/*     */               } 
/*     */               
/* 188 */               Checksum checksum = this.checksum;
/* 189 */               if (this.hasChecksum && checksum != null) {
/* 190 */                 checksum.reset();
/* 191 */                 checksum.update(output, outputPtr, originalLength);
/* 192 */                 int checksumResult = (int)checksum.getValue();
/* 193 */                 if (checksumResult != this.currentChecksum) {
/* 194 */                   throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", new Object[] {
/*     */                           
/* 196 */                           Integer.valueOf(checksumResult), Integer.valueOf(this.currentChecksum)
/*     */                         }));
/*     */                 }
/*     */               } 
/* 200 */               if (uncompressed != null) {
/* 201 */                 uncompressed.writerIndex(uncompressed.writerIndex() + originalLength);
/* 202 */                 out.add(uncompressed);
/*     */               } 
/* 204 */               in.skipBytes(chunkLength);
/*     */               
/* 206 */               this.currentState = State.INIT_BLOCK;
/* 207 */               success = true;
/*     */             } finally {
/* 209 */               if (!success && uncompressed != null)
/* 210 */                 uncompressed.release(); 
/*     */             } 
/*     */           } 
/*     */           return;
/*     */         case CORRUPTED:
/* 215 */           in.skipBytes(in.readableBytes());
/*     */           return;
/*     */       } 
/* 218 */       throw new IllegalStateException();
/*     */     }
/* 220 */     catch (Exception e) {
/* 221 */       this.currentState = State.CORRUPTED;
/* 222 */       throw e;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\FastLzFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */