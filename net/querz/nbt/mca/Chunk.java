/*     */ package net.querz.nbt.mca;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import net.querz.nbt.CompoundTag;
/*     */ import net.querz.nbt.ListTag;
/*     */ import net.querz.nbt.Tag;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Chunk
/*     */ {
/*     */   public static final int DEFAULT_DATA_VERSION = 1628;
/*     */   private int lastMCAUpdate;
/*     */   private CompoundTag data;
/*     */   private int dataVersion;
/*     */   private long lastUpdate;
/*     */   private long inhabitedTime;
/*     */   private int[] biomes;
/*     */   private CompoundTag heightMaps;
/*     */   private CompoundTag carvingMasks;
/*  29 */   private Section[] sections = new Section[16];
/*     */   private ListTag<CompoundTag> entities;
/*     */   private ListTag<CompoundTag> tileEntities;
/*     */   private ListTag<CompoundTag> tileTicks;
/*     */   private ListTag<CompoundTag> liquidTicks;
/*     */   private ListTag<ListTag<?>> lights;
/*     */   private ListTag<ListTag<?>> liquidsToBeTicked;
/*     */   private ListTag<ListTag<?>> toBeTicked;
/*     */   private ListTag<ListTag<?>> postProcessing;
/*     */   private String status;
/*     */   private CompoundTag structures;
/*     */   
/*     */   Chunk(int lastMCAUpdate) {
/*  42 */     this.lastMCAUpdate = lastMCAUpdate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chunk(CompoundTag data) {
/*  50 */     this.data = data;
/*  51 */     initReferences();
/*     */   }
/*     */   
/*     */   private void initReferences() {
/*  55 */     if (this.data == null) {
/*  56 */       throw new NullPointerException("data cannot be null");
/*     */     }
/*     */     CompoundTag level;
/*  59 */     if ((level = this.data.getCompoundTag("Level")) == null) {
/*  60 */       throw new IllegalArgumentException("data does not contain \"Level\" tag");
/*     */     }
/*  62 */     this.dataVersion = this.data.getInt("DataVersion");
/*  63 */     this.inhabitedTime = level.getLong("InhabitedTime");
/*  64 */     this.lastUpdate = level.getLong("LastUpdate");
/*  65 */     this.biomes = level.getIntArray("Biomes");
/*  66 */     this.heightMaps = level.getCompoundTag("HeightMaps");
/*  67 */     this.carvingMasks = level.getCompoundTag("CarvingMasks");
/*  68 */     this.entities = level.containsKey("Entities") ? level.getListTag("Entities").asCompoundTagList() : null;
/*  69 */     this.tileEntities = level.containsKey("TileEntities") ? level.getListTag("TileEntities").asCompoundTagList() : null;
/*  70 */     this.tileTicks = level.containsKey("TileTicks") ? level.getListTag("TileTicks").asCompoundTagList() : null;
/*  71 */     this.liquidTicks = level.containsKey("LiquidTicks") ? level.getListTag("LiquidTicks").asCompoundTagList() : null;
/*  72 */     this.lights = level.containsKey("Lights") ? level.getListTag("Lights").asListTagList() : null;
/*  73 */     this.liquidsToBeTicked = level.containsKey("LiquidsToBeTicked") ? level.getListTag("LiquidsToBeTicked").asListTagList() : null;
/*  74 */     this.toBeTicked = level.containsKey("ToBeTicked") ? level.getListTag("ToBeTicked").asListTagList() : null;
/*  75 */     this.postProcessing = level.containsKey("PostProcessing") ? level.getListTag("PostProcessing").asListTagList() : null;
/*  76 */     this.status = level.getString("Status");
/*  77 */     this.structures = level.getCompoundTag("Structures");
/*  78 */     if (level.containsKey("Sections")) {
/*  79 */       for (CompoundTag section : level.getListTag("Sections").asCompoundTagList()) {
/*  80 */         int sectionIndex = section.getByte("Y");
/*  81 */         if (sectionIndex > 15 || sectionIndex < 0) {
/*     */           continue;
/*     */         }
/*  84 */         Section newSection = new Section(section);
/*  85 */         if (newSection.isEmpty()) {
/*     */           continue;
/*     */         }
/*  88 */         this.sections[sectionIndex] = newSection;
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
/*     */ 
/*     */   
/*     */   public int serialize(RandomAccessFile raf, int xPos, int zPos) throws IOException {
/* 102 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
/* 103 */     try (DataOutputStream nbtOut = new DataOutputStream(new BufferedOutputStream(CompressionType.ZLIB.compress(baos)))) {
/* 104 */       updateHandle(xPos, zPos).serialize(nbtOut, 512);
/*     */     } 
/* 106 */     byte[] rawData = baos.toByteArray();
/* 107 */     raf.writeInt(rawData.length);
/* 108 */     raf.writeByte(CompressionType.ZLIB.getID());
/* 109 */     raf.write(rawData);
/* 110 */     return rawData.length + 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deserialize(RandomAccessFile raf) throws IOException {
/* 119 */     byte compressionTypeByte = raf.readByte();
/* 120 */     CompressionType compressionType = CompressionType.getFromID(compressionTypeByte);
/* 121 */     if (compressionType == null) {
/* 122 */       throw new IOException("invalid compression type " + compressionTypeByte);
/*     */     }
/* 124 */     DataInputStream dis = new DataInputStream(new BufferedInputStream(compressionType.decompress(new FileInputStream(raf.getFD()))));
/* 125 */     Tag<?> tag = Tag.deserialize(dis, 512);
/* 126 */     if (tag instanceof CompoundTag) {
/* 127 */       this.data = (CompoundTag)tag;
/* 128 */       initReferences();
/*     */     } else {
/* 130 */       throw new IOException("invalid data tag: " + ((tag == null) ? "null" : tag.getClass().getName()));
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
/*     */   public int getBiomeAt(int blockX, int blockZ) {
/* 142 */     if (this.biomes == null || this.biomes.length != 256) {
/* 143 */       return -1;
/*     */     }
/* 145 */     return this.biomes[getBlockIndex(blockX, blockZ)];
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
/*     */   public void setBiomeAt(int blockX, int blockZ, int biomeID) {
/* 157 */     if (this.biomes == null || this.biomes.length != 256) {
/* 158 */       this.biomes = new int[256];
/* 159 */       for (int i = 0; i < this.biomes.length; i++) {
/* 160 */         this.biomes[i] = -1;
/*     */       }
/*     */     } 
/* 163 */     this.biomes[getBlockIndex(blockX, blockZ)] = biomeID;
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
/* 175 */     Section section = this.sections[MCAUtil.blockToChunk(blockY)];
/* 176 */     if (section == null) {
/* 177 */       return null;
/*     */     }
/* 179 */     return section.getBlockStateAt(blockX, blockY, blockZ);
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
/*     */   public void setBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag state, boolean cleanup) {
/* 194 */     int sectionIndex = MCAUtil.blockToChunk(blockY);
/* 195 */     Section section = this.sections[sectionIndex];
/* 196 */     if (section == null) {
/* 197 */       section = this.sections[sectionIndex] = Section.newSection();
/*     */     }
/* 199 */     section.setBlockStateAt(blockX, blockY, blockZ, state, cleanup);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDataVersion() {
/* 206 */     return this.dataVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDataVersion(int dataVersion) {
/* 215 */     this.dataVersion = dataVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLastMCAUpdate() {
/* 222 */     return this.lastMCAUpdate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastMCAUpdate(int lastMCAUpdate) {
/* 230 */     this.lastMCAUpdate = lastMCAUpdate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStatus() {
/* 237 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(String status) {
/* 245 */     this.status = status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Section getSection(int sectionY) {
/* 254 */     return this.sections[sectionY];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSection(int sectionY, Section section) {
/* 263 */     this.sections[sectionY] = section;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastUpdate() {
/* 270 */     return this.lastUpdate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastUpdate(long lastUpdate) {
/* 278 */     this.lastUpdate = lastUpdate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getInhabitedTime() {
/* 285 */     return this.inhabitedTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInhabitedTime(long inhabitedTime) {
/* 293 */     this.inhabitedTime = inhabitedTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getBiomes() {
/* 300 */     return this.biomes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBiomes(int[] biomes) {
/* 310 */     if (biomes != null && biomes.length != 256) {
/* 311 */       throw new IllegalArgumentException("biomes array must have a length of 256");
/*     */     }
/* 313 */     this.biomes = biomes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompoundTag getHeightMaps() {
/* 320 */     return this.heightMaps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeightMaps(CompoundTag heightMaps) {
/* 328 */     this.heightMaps = heightMaps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompoundTag getCarvingMasks() {
/* 335 */     return this.carvingMasks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCarvingMasks(CompoundTag carvingMasks) {
/* 343 */     this.carvingMasks = carvingMasks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<CompoundTag> getEntities() {
/* 350 */     return this.entities;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEntities(ListTag<CompoundTag> entities) {
/* 358 */     this.entities = entities;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<CompoundTag> getTileEntities() {
/* 365 */     return this.tileEntities;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTileEntities(ListTag<CompoundTag> tileEntities) {
/* 373 */     this.tileEntities = tileEntities;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<CompoundTag> getTileTicks() {
/* 380 */     return this.tileTicks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTileTicks(ListTag<CompoundTag> tileTicks) {
/* 388 */     this.tileTicks = tileTicks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<CompoundTag> getLiquidTicks() {
/* 395 */     return this.liquidTicks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLiquidTicks(ListTag<CompoundTag> liquidTicks) {
/* 403 */     this.liquidTicks = liquidTicks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<ListTag<?>> getLights() {
/* 410 */     return this.lights;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLights(ListTag<ListTag<?>> lights) {
/* 418 */     this.lights = lights;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<ListTag<?>> getLiquidsToBeTicked() {
/* 425 */     return this.liquidsToBeTicked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLiquidsToBeTicked(ListTag<ListTag<?>> liquidsToBeTicked) {
/* 433 */     this.liquidsToBeTicked = liquidsToBeTicked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<ListTag<?>> getToBeTicked() {
/* 440 */     return this.toBeTicked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setToBeTicked(ListTag<ListTag<?>> toBeTicked) {
/* 448 */     this.toBeTicked = toBeTicked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<ListTag<?>> getPostProcessing() {
/* 455 */     return this.postProcessing;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPostProcessing(ListTag<ListTag<?>> postProcessing) {
/* 463 */     this.postProcessing = postProcessing;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompoundTag getStructures() {
/* 470 */     return this.structures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStructures(CompoundTag structures) {
/* 478 */     this.structures = structures;
/*     */   }
/*     */   
/*     */   int getBlockIndex(int blockX, int blockZ) {
/* 482 */     return (blockZ & 0xF) * 16 + (blockX & 0xF);
/*     */   }
/*     */   
/*     */   public void cleanupPalettesAndBlockStates() {
/* 486 */     for (Section section : this.sections) {
/* 487 */       if (section != null) {
/* 488 */         section.cleanupPaletteAndBlockStates();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Chunk newChunk() {
/* 494 */     Chunk c = new Chunk(0);
/* 495 */     c.dataVersion = 1628;
/* 496 */     c.data = new CompoundTag();
/* 497 */     c.data.put("Level", (Tag)new CompoundTag());
/* 498 */     c.status = "mobs_spawned";
/* 499 */     return c;
/*     */   }
/*     */   
/*     */   public CompoundTag updateHandle(int xPos, int zPos) {
/* 503 */     this.data.putInt("DataVersion", this.dataVersion);
/* 504 */     CompoundTag level = this.data.getCompoundTag("Level");
/* 505 */     level.putInt("xPos", xPos);
/* 506 */     level.putInt("zPos", zPos);
/* 507 */     level.putLong("LastUpdate", this.lastUpdate);
/* 508 */     level.putLong("InhabitedTime", this.inhabitedTime);
/* 509 */     if (this.biomes != null && this.biomes.length == 256) level.putIntArray("Biomes", this.biomes); 
/* 510 */     if (this.heightMaps != null) level.put("HeightMaps", (Tag)this.heightMaps); 
/* 511 */     if (this.carvingMasks != null) level.put("CarvingMasks", (Tag)this.carvingMasks); 
/* 512 */     if (this.entities != null) level.put("Entities", (Tag)this.entities); 
/* 513 */     if (this.tileEntities != null) level.put("TileEntities", (Tag)this.tileEntities); 
/* 514 */     if (this.tileTicks != null) level.put("TileTicks", (Tag)this.tileTicks); 
/* 515 */     if (this.liquidTicks != null) level.put("LiquidTicks", (Tag)this.liquidTicks); 
/* 516 */     if (this.lights != null) level.put("Lights", (Tag)this.lights); 
/* 517 */     if (this.liquidsToBeTicked != null) level.put("LiquidsToBeTicked", (Tag)this.liquidsToBeTicked); 
/* 518 */     if (this.toBeTicked != null) level.put("ToBeTicked", (Tag)this.toBeTicked); 
/* 519 */     if (this.postProcessing != null) level.put("PostProcessing", (Tag)this.postProcessing); 
/* 520 */     level.putString("Status", this.status);
/* 521 */     if (this.structures != null) level.put("Structures", (Tag)this.structures); 
/* 522 */     ListTag<CompoundTag> sections = new ListTag(CompoundTag.class);
/* 523 */     for (int i = 0; i < this.sections.length; i++) {
/* 524 */       if (this.sections[i] != null) {
/* 525 */         sections.add((Tag)this.sections[i].updateHandle(i));
/*     */       }
/*     */     } 
/* 528 */     level.put("Sections", (Tag)sections);
/* 529 */     return this.data;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\Chunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */