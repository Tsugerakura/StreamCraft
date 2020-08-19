/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
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
/*     */ final class Bzip2BlockCompressor
/*     */ {
/*  36 */   private final ByteProcessor writeProcessor = new ByteProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  39 */         return Bzip2BlockCompressor.this.write(value);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Bzip2BitWriter writer;
/*     */ 
/*     */ 
/*     */   
/*  51 */   private final Crc32 crc = new Crc32();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] block;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int blockLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int blockLengthLimit;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   private final boolean[] blockValuesPresent = new boolean[256];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int[] bwtBlock;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private int rleCurrentValue = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int rleLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Bzip2BlockCompressor(Bzip2BitWriter writer, int blockSize) {
/*  95 */     this.writer = writer;
/*     */ 
/*     */     
/*  98 */     this.block = new byte[blockSize + 1];
/*  99 */     this.bwtBlock = new int[blockSize + 1];
/* 100 */     this.blockLengthLimit = blockSize - 6;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeSymbolMap(ByteBuf out) {
/* 107 */     Bzip2BitWriter writer = this.writer;
/*     */     
/* 109 */     boolean[] blockValuesPresent = this.blockValuesPresent;
/* 110 */     boolean[] condensedInUse = new boolean[16];
/*     */     int i;
/* 112 */     for (i = 0; i < condensedInUse.length; i++) {
/* 113 */       for (int j = 0, k = i << 4; j < 16; j++, k++) {
/* 114 */         if (blockValuesPresent[k]) {
/* 115 */           condensedInUse[i] = true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 120 */     for (boolean isCondensedInUse : condensedInUse) {
/* 121 */       writer.writeBoolean(out, isCondensedInUse);
/*     */     }
/*     */     
/* 124 */     for (i = 0; i < condensedInUse.length; i++) {
/* 125 */       if (condensedInUse[i]) {
/* 126 */         for (int j = 0, k = i << 4; j < 16; j++, k++) {
/* 127 */           writer.writeBoolean(out, blockValuesPresent[k]);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeRun(int value, int runLength) {
/* 139 */     int blockLength = this.blockLength;
/* 140 */     byte[] block = this.block;
/*     */     
/* 142 */     this.blockValuesPresent[value] = true;
/* 143 */     this.crc.updateCRC(value, runLength);
/*     */     
/* 145 */     byte byteValue = (byte)value;
/* 146 */     switch (runLength) {
/*     */       case 1:
/* 148 */         block[blockLength] = byteValue;
/* 149 */         this.blockLength = blockLength + 1;
/*     */         return;
/*     */       case 2:
/* 152 */         block[blockLength] = byteValue;
/* 153 */         block[blockLength + 1] = byteValue;
/* 154 */         this.blockLength = blockLength + 2;
/*     */         return;
/*     */       case 3:
/* 157 */         block[blockLength] = byteValue;
/* 158 */         block[blockLength + 1] = byteValue;
/* 159 */         block[blockLength + 2] = byteValue;
/* 160 */         this.blockLength = blockLength + 3;
/*     */         return;
/*     */     } 
/* 163 */     runLength -= 4;
/* 164 */     this.blockValuesPresent[runLength] = true;
/* 165 */     block[blockLength] = byteValue;
/* 166 */     block[blockLength + 1] = byteValue;
/* 167 */     block[blockLength + 2] = byteValue;
/* 168 */     block[blockLength + 3] = byteValue;
/* 169 */     block[blockLength + 4] = (byte)runLength;
/* 170 */     this.blockLength = blockLength + 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean write(int value) {
/* 181 */     if (this.blockLength > this.blockLengthLimit) {
/* 182 */       return false;
/*     */     }
/* 184 */     int rleCurrentValue = this.rleCurrentValue;
/* 185 */     int rleLength = this.rleLength;
/*     */     
/* 187 */     if (rleLength == 0) {
/* 188 */       this.rleCurrentValue = value;
/* 189 */       this.rleLength = 1;
/* 190 */     } else if (rleCurrentValue != value) {
/*     */       
/* 192 */       writeRun(rleCurrentValue & 0xFF, rleLength);
/* 193 */       this.rleCurrentValue = value;
/* 194 */       this.rleLength = 1;
/*     */     }
/* 196 */     else if (rleLength == 254) {
/* 197 */       writeRun(rleCurrentValue & 0xFF, 255);
/* 198 */       this.rleLength = 0;
/*     */     } else {
/* 200 */       this.rleLength = rleLength + 1;
/*     */     } 
/*     */     
/* 203 */     return true;
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
/*     */   int write(ByteBuf buffer, int offset, int length) {
/* 215 */     int index = buffer.forEachByte(offset, length, this.writeProcessor);
/* 216 */     return (index == -1) ? length : (index - offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void close(ByteBuf out) {
/* 224 */     if (this.rleLength > 0) {
/* 225 */       writeRun(this.rleCurrentValue & 0xFF, this.rleLength);
/*     */     }
/*     */ 
/*     */     
/* 229 */     this.block[this.blockLength] = this.block[0];
/*     */ 
/*     */     
/* 232 */     Bzip2DivSufSort divSufSort = new Bzip2DivSufSort(this.block, this.bwtBlock, this.blockLength);
/* 233 */     int bwtStartPointer = divSufSort.bwt();
/*     */     
/* 235 */     Bzip2BitWriter writer = this.writer;
/*     */ 
/*     */     
/* 238 */     writer.writeBits(out, 24, 3227993L);
/* 239 */     writer.writeBits(out, 24, 2511705L);
/* 240 */     writer.writeInt(out, this.crc.getCRC());
/* 241 */     writer.writeBoolean(out, false);
/* 242 */     writer.writeBits(out, 24, bwtStartPointer);
/*     */ 
/*     */     
/* 245 */     writeSymbolMap(out);
/*     */ 
/*     */     
/* 248 */     Bzip2MTFAndRLE2StageEncoder mtfEncoder = new Bzip2MTFAndRLE2StageEncoder(this.bwtBlock, this.blockLength, this.blockValuesPresent);
/*     */     
/* 250 */     mtfEncoder.encode();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 257 */     Bzip2HuffmanStageEncoder huffmanEncoder = new Bzip2HuffmanStageEncoder(writer, mtfEncoder.mtfBlock(), mtfEncoder.mtfLength(), mtfEncoder.mtfAlphabetSize(), mtfEncoder.mtfSymbolFrequencies());
/* 258 */     huffmanEncoder.encode(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int availableSize() {
/* 266 */     if (this.blockLength == 0) {
/* 267 */       return this.blockLengthLimit + 2;
/*     */     }
/* 269 */     return this.blockLengthLimit - this.blockLength + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isFull() {
/* 277 */     return (this.blockLength > this.blockLengthLimit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isEmpty() {
/* 285 */     return (this.blockLength == 0 && this.rleLength == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int crc() {
/* 293 */     return this.crc.getCRC();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Bzip2BlockCompressor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */