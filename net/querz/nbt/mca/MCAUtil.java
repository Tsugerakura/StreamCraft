/*     */ package net.querz.nbt.mca;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public final class MCAUtil
/*     */ {
/*     */   public static MCAFile readMCAFile(String file) throws IOException {
/*  26 */     return readMCAFile(new File(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MCAFile readMCAFile(File file) throws IOException {
/*  36 */     MCAFile mcaFile = newMCAFile(file);
/*  37 */     try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
/*  38 */       mcaFile.deserialize(raf);
/*  39 */       return mcaFile;
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
/*     */   
/*     */   public static int writeMCAFile(MCAFile mcaFile, String file) throws IOException {
/*  52 */     return writeMCAFile(mcaFile, new File(file), false);
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
/*     */   public static int writeMCAFile(MCAFile mcaFile, File file) throws IOException {
/*  64 */     return writeMCAFile(mcaFile, file, false);
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
/*     */   public static int writeMCAFile(MCAFile mcaFile, String file, boolean changeLastUpdate) throws IOException {
/*  76 */     return writeMCAFile(mcaFile, new File(file), changeLastUpdate);
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
/*     */   public static int writeMCAFile(MCAFile mcaFile, File file, boolean changeLastUpdate) throws IOException {
/*     */     int chunks;
/*  91 */     File to = file;
/*  92 */     if (file.exists()) {
/*  93 */       to = File.createTempFile(to.getName(), null);
/*     */     }
/*     */     
/*  96 */     try (RandomAccessFile raf = new RandomAccessFile(to, "rw")) {
/*  97 */       chunks = mcaFile.serialize(raf, changeLastUpdate);
/*     */     } 
/*     */     
/* 100 */     if (chunks > 0 && to != file) {
/* 101 */       Files.move(to.toPath(), file.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */     }
/* 103 */     return chunks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String createNameFromChunkLocation(int chunkX, int chunkZ) {
/* 114 */     return createNameFromRegionLocation(chunkToRegion(chunkX), chunkToRegion(chunkZ));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String createNameFromBlockLocation(int blockX, int blockZ) {
/* 125 */     return createNameFromRegionLocation(blockToRegion(blockX), blockToRegion(blockZ));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String createNameFromRegionLocation(int regionX, int regionZ) {
/* 135 */     return "r." + regionX + "." + regionZ + ".mca";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int blockToChunk(int block) {
/* 144 */     return block >> 4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int blockToRegion(int block) {
/* 153 */     return block >> 9;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int chunkToRegion(int chunk) {
/* 162 */     return chunk >> 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int regionToChunk(int region) {
/* 171 */     return region << 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int regionToBlock(int region) {
/* 180 */     return region << 9;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int chunkToBlock(int chunk) {
/* 189 */     return chunk << 4;
/*     */   }
/*     */   
/* 192 */   private static final Pattern mcaFilePattern = Pattern.compile("^.*r\\.(?<regionX>-?\\d+)\\.(?<regionZ>-?\\d+)\\.mca$");
/*     */   
/*     */   private static MCAFile newMCAFile(File file) {
/* 195 */     Matcher m = mcaFilePattern.matcher(file.getName());
/* 196 */     if (m.find()) {
/* 197 */       return new MCAFile(Integer.parseInt(m.group("regionX")), Integer.parseInt(m.group("regionZ")));
/*     */     }
/* 199 */     throw new IllegalArgumentException("invalid mca file name: " + file.getName());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\MCAUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */