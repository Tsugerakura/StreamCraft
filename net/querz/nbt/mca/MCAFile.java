/*     */ package net.querz.nbt.mca;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import net.querz.nbt.CompoundTag;
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
/*     */ public class MCAFile
/*     */ {
/*     */   public static final int DEFAULT_DATA_VERSION = 1628;
/*     */   private int regionX;
/*     */   private int regionZ;
/*     */   private Chunk[] chunks;
/*     */   
/*     */   public MCAFile(int regionX, int regionZ) {
/*  26 */     this.regionX = regionX;
/*  27 */     this.regionZ = regionZ;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deserialize(RandomAccessFile raf) throws IOException {
/*  37 */     this.chunks = new Chunk[1024];
/*  38 */     for (int i = 0; i < 1024; i++) {
/*  39 */       raf.seek((i * 4));
/*  40 */       int offset = raf.read() << 16;
/*  41 */       offset |= (raf.read() & 0xFF) << 8;
/*  42 */       offset |= raf.read() & 0xFF;
/*  43 */       if (raf.readByte() != 0) {
/*     */ 
/*     */         
/*  46 */         raf.seek((4096 + i * 4));
/*  47 */         int timestamp = raf.readInt();
/*  48 */         Chunk chunk = new Chunk(timestamp);
/*  49 */         raf.seek((4096 * offset + 4));
/*  50 */         chunk.deserialize(raf);
/*  51 */         this.chunks[i] = chunk;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int serialize(RandomAccessFile raf) throws IOException {
/*  63 */     return serialize(raf, false);
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
/*     */   public int serialize(RandomAccessFile raf, boolean changeLastUpdate) throws IOException {
/*  76 */     int globalOffset = 2;
/*  77 */     int lastWritten = 0;
/*  78 */     int timestamp = (int)(System.currentTimeMillis() / 1000L);
/*  79 */     int chunksWritten = 0;
/*  80 */     int chunkXOffset = MCAUtil.regionToChunk(this.regionX);
/*  81 */     int chunkZOffset = MCAUtil.regionToChunk(this.regionZ);
/*     */     
/*  83 */     if (this.chunks == null) {
/*  84 */       return 0;
/*     */     }
/*     */     
/*  87 */     for (int cx = 0; cx < 32; cx++) {
/*  88 */       for (int cz = 0; cz < 32; cz++) {
/*  89 */         int index = getChunkIndex(cx, cz);
/*  90 */         Chunk chunk = this.chunks[index];
/*  91 */         if (chunk != null) {
/*     */ 
/*     */           
/*  94 */           raf.seek((4096 * globalOffset));
/*  95 */           lastWritten = chunk.serialize(raf, chunkXOffset + cx, chunkZOffset + cz);
/*     */           
/*  97 */           if (lastWritten != 0) {
/*     */ 
/*     */ 
/*     */             
/* 101 */             chunksWritten++;
/*     */             
/* 103 */             int sectors = (lastWritten >> 12) + 1;
/*     */             
/* 105 */             raf.seek((index * 4));
/* 106 */             raf.writeByte(globalOffset >>> 16);
/* 107 */             raf.writeByte(globalOffset >> 8 & 0xFF);
/* 108 */             raf.writeByte(globalOffset & 0xFF);
/* 109 */             raf.writeByte(sectors);
/*     */ 
/*     */             
/* 112 */             raf.seek((index * 4 + 4096));
/* 113 */             raf.writeInt(changeLastUpdate ? timestamp : chunk.getLastMCAUpdate());
/*     */             
/* 115 */             globalOffset += sectors;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 120 */     if (lastWritten % 4096 != 0) {
/* 121 */       raf.seek((globalOffset * 4096 - 1));
/* 122 */       raf.write(0);
/*     */     } 
/* 124 */     return chunksWritten;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setChunk(int index, Chunk chunk) {
/* 134 */     checkIndex(index);
/* 135 */     if (this.chunks == null) {
/* 136 */       this.chunks = new Chunk[1024];
/*     */     }
/* 138 */     this.chunks[index] = chunk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setChunk(int chunkX, int chunkZ, Chunk chunk) {
/* 149 */     setChunk(getChunkIndex(chunkX, chunkZ), chunk);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chunk getChunk(int index) {
/* 158 */     checkIndex(index);
/* 159 */     if (this.chunks == null) {
/* 160 */       return null;
/*     */     }
/* 162 */     return this.chunks[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chunk getChunk(int chunkX, int chunkZ) {
/* 172 */     return getChunk(getChunkIndex(chunkX, chunkZ));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getChunkIndex(int chunkX, int chunkZ) {
/* 183 */     return (chunkX & 0x1F) + (chunkZ & 0x1F) * 32;
/*     */   }
/*     */   
/*     */   private int checkIndex(int index) {
/* 187 */     if (index < 0 || index > 1023) {
/* 188 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 190 */     return index;
/*     */   }
/*     */   
/*     */   private Chunk createChunkIfMissing(int blockX, int blockZ) {
/* 194 */     int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
/* 195 */     Chunk chunk = getChunk(chunkX, chunkZ);
/* 196 */     if (chunk == null) {
/* 197 */       chunk = Chunk.newChunk();
/* 198 */       setChunk(getChunkIndex(chunkX, chunkZ), chunk);
/*     */     } 
/* 200 */     return chunk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBiomeAt(int blockX, int blockZ, int biomeID) {
/* 211 */     createChunkIfMissing(blockX, blockZ).setBiomeAt(blockX, blockZ, biomeID);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBiomeAt(int blockX, int blockZ) {
/* 221 */     int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
/* 222 */     Chunk chunk = getChunk(getChunkIndex(chunkX, chunkZ));
/* 223 */     if (chunk == null) {
/* 224 */       return -1;
/*     */     }
/* 226 */     return chunk.getBiomeAt(blockX, blockZ);
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
/*     */   public void setBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag state, boolean cleanup) {
/* 239 */     createChunkIfMissing(blockX, blockZ).setBlockStateAt(blockX, blockY, blockZ, state, cleanup);
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
/*     */   public CompoundTag getBlockStateAt(int blockX, int blockY, int blockZ) {
/* 251 */     int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
/* 252 */     Chunk chunk = getChunk(chunkX, chunkZ);
/* 253 */     if (chunk == null) {
/* 254 */       return null;
/*     */     }
/* 256 */     return chunk.getBlockStateAt(blockX, blockY, blockZ);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cleanupPalettesAndBlockStates() {
/* 263 */     for (Chunk chunk : this.chunks) {
/* 264 */       if (chunk != null)
/* 265 */         chunk.cleanupPalettesAndBlockStates(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\MCAFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */