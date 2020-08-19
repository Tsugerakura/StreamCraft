/*     */ package net.querz.nbt.mca;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import net.querz.nbt.CompoundTag;
/*     */ import net.querz.nbt.ListTag;
/*     */ import net.querz.nbt.Tag;
/*     */ 
/*     */ public class Section {
/*     */   private CompoundTag data;
/*  13 */   private Map<String, List<PaletteIndex>> valueIndexedPalette = new HashMap<>();
/*     */   
/*     */   private ListTag<CompoundTag> palette;
/*     */   
/*     */   private byte[] blockLight;
/*     */   
/*     */   private long[] blockStates;
/*     */   
/*     */   private byte[] skyLight;
/*     */   
/*     */   public Section(CompoundTag sectionRoot) {
/*  24 */     ListTag<?> rawPalette = sectionRoot.getListTag("Palette");
/*  25 */     if (rawPalette == null) {
/*     */       return;
/*     */     }
/*  28 */     this.palette = rawPalette.asCompoundTagList();
/*  29 */     for (int i = 0; i < this.palette.size(); i++) {
/*  30 */       CompoundTag data = (CompoundTag)this.palette.get(i);
/*  31 */       putValueIndexedPalette(data, i);
/*     */     } 
/*  33 */     this.blockLight = sectionRoot.getByteArray("BlockLight");
/*  34 */     this.blockStates = sectionRoot.getLongArray("BlockStates");
/*  35 */     this.skyLight = sectionRoot.getByteArray("SkyLight");
/*  36 */     this.data = sectionRoot;
/*     */   }
/*     */   
/*     */   Section() {}
/*     */   
/*     */   private void putValueIndexedPalette(CompoundTag data, int index) {
/*  42 */     PaletteIndex leaf = new PaletteIndex(data, index);
/*  43 */     String name = data.getString("Name");
/*  44 */     List<PaletteIndex> leaves = this.valueIndexedPalette.get(name);
/*  45 */     if (leaves == null) {
/*  46 */       leaves = new ArrayList<>(1);
/*  47 */       leaves.add(leaf);
/*  48 */       this.valueIndexedPalette.put(name, leaves);
/*     */     } else {
/*  50 */       for (PaletteIndex pal : leaves) {
/*  51 */         if (pal.data.equals(data)) {
/*     */           return;
/*     */         }
/*     */       } 
/*  55 */       leaves.add(leaf);
/*     */     } 
/*     */   }
/*     */   
/*     */   private PaletteIndex getValueIndexedPalette(CompoundTag data) {
/*  60 */     List<PaletteIndex> leaves = this.valueIndexedPalette.get(data.getString("Name"));
/*  61 */     if (leaves == null) {
/*  62 */       return null;
/*     */     }
/*  64 */     for (PaletteIndex leaf : leaves) {
/*  65 */       if (leaf.data.equals(data)) {
/*  66 */         return leaf;
/*     */       }
/*     */     } 
/*  69 */     return null;
/*     */   }
/*     */   
/*     */   private class PaletteIndex
/*     */   {
/*     */     CompoundTag data;
/*     */     int index;
/*     */     
/*     */     PaletteIndex(CompoundTag data, int index) {
/*  78 */       this.data = data;
/*  79 */       this.index = index;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  88 */     return (this.data == null);
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
/* 100 */     int index = getBlockIndex(blockX, blockY, blockZ);
/* 101 */     int paletteIndex = getPaletteIndex(index);
/* 102 */     return (CompoundTag)this.palette.get(paletteIndex);
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
/*     */   public void setBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag state, boolean cleanup) {
/* 116 */     int paletteSizeBefore = this.palette.size();
/* 117 */     int paletteIndex = addToPalette(state);
/*     */ 
/*     */ 
/*     */     
/* 121 */     if (paletteSizeBefore != this.palette.size() && (paletteIndex & paletteIndex - 1) == 0) {
/* 122 */       adjustBlockStateBits(null, this.blockStates);
/* 123 */       cleanup = true;
/*     */     } 
/*     */     
/* 126 */     setPaletteIndex(getBlockIndex(blockX, blockY, blockZ), paletteIndex, this.blockStates);
/*     */     
/* 128 */     if (cleanup) {
/* 129 */       cleanupPaletteAndBlockStates();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPaletteIndex(int blockStateIndex) {
/* 139 */     int bits = this.blockStates.length >> 6;
/* 140 */     double blockStatesIndex = blockStateIndex / 4096.0D / this.blockStates.length;
/* 141 */     int longIndex = (int)blockStatesIndex;
/* 142 */     int startBit = (int)((blockStatesIndex - Math.floor(blockStatesIndex)) * 64.0D);
/* 143 */     if (startBit + bits > 64) {
/* 144 */       long prev = bitRange(this.blockStates[longIndex], startBit, 64);
/* 145 */       long next = bitRange(this.blockStates[longIndex + 1], 0, startBit + bits - 64);
/* 146 */       return (int)((next << 64 - startBit) + prev);
/*     */     } 
/* 148 */     return (int)bitRange(this.blockStates[longIndex], startBit, startBit + bits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPaletteIndex(int blockIndex, int paletteIndex, long[] blockStates) {
/* 159 */     int bits = blockStates.length / 64;
/* 160 */     double blockStatesIndex = blockIndex / 4096.0D / blockStates.length;
/* 161 */     int longIndex = (int)blockStatesIndex;
/* 162 */     int startBit = (int)((blockStatesIndex - Math.floor(longIndex)) * 64.0D);
/* 163 */     if (startBit + bits > 64) {
/* 164 */       blockStates[longIndex] = updateBits(blockStates[longIndex], paletteIndex, startBit, 64);
/* 165 */       blockStates[longIndex + 1] = updateBits(blockStates[longIndex + 1], paletteIndex, startBit - 64, startBit + bits - 64);
/*     */     } else {
/* 167 */       blockStates[longIndex] = updateBits(blockStates[longIndex], paletteIndex, startBit, startBit + bits);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<CompoundTag> getPalette() {
/* 176 */     return this.palette;
/*     */   }
/*     */   
/*     */   int addToPalette(CompoundTag data) {
/*     */     PaletteIndex index;
/* 181 */     if ((index = getValueIndexedPalette(data)) != null) {
/* 182 */       return index.index;
/*     */     }
/* 184 */     this.palette.add((Tag)data);
/* 185 */     putValueIndexedPalette(data, this.palette.size() - 1);
/* 186 */     return this.palette.size() - 1;
/*     */   }
/*     */   
/*     */   private int getBlockIndex(int blockX, int blockY, int blockZ) {
/* 190 */     return (blockY & 0xF) * 256 + (blockZ & 0xF) * 16 + (blockX & 0xF);
/*     */   }
/*     */ 
/*     */   
/*     */   private static long updateBits(long n, long m, int i, int j) {
/* 195 */     long mShifted = (i > 0) ? ((m & (1L << j - i) - 1L) << i) : ((m & (1L << j - i) - 1L) >>> -i);
/* 196 */     return n & (((j > 63) ? 0L : (-1L << j)) | ((i < 0) ? 0L : ((1L << i) - 1L))) | mShifted;
/*     */   }
/*     */   
/*     */   private static long bitRange(long value, int from, int to) {
/* 200 */     int waste = 64 - to;
/* 201 */     return value << waste >>> waste + from;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cleanupPaletteAndBlockStates() {
/* 210 */     Map<Integer, Integer> oldToNewMapping = cleanupPalette();
/* 211 */     adjustBlockStateBits(oldToNewMapping, this.blockStates);
/*     */   }
/*     */ 
/*     */   
/*     */   private Map<Integer, Integer> cleanupPalette() {
/* 216 */     Map<Integer, Integer> allIndices = new HashMap<>();
/* 217 */     for (int i = 0; i < 4096; i++) {
/* 218 */       int paletteIndex = getPaletteIndex(i);
/* 219 */       allIndices.put(Integer.valueOf(paletteIndex), Integer.valueOf(paletteIndex));
/*     */     } 
/*     */ 
/*     */     
/* 223 */     int index = 1;
/* 224 */     this.valueIndexedPalette = new HashMap<>(this.valueIndexedPalette.size());
/* 225 */     putValueIndexedPalette((CompoundTag)this.palette.get(0), 0);
/* 226 */     for (int j = 1; j < this.palette.size(); j++) {
/* 227 */       if (!allIndices.containsKey(Integer.valueOf(index))) {
/* 228 */         this.palette.remove(j);
/* 229 */         j--;
/*     */       } else {
/* 231 */         putValueIndexedPalette((CompoundTag)this.palette.get(j), j);
/* 232 */         allIndices.put(Integer.valueOf(index), Integer.valueOf(j));
/*     */       } 
/* 234 */       index++;
/*     */     } 
/*     */     
/* 237 */     return allIndices;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void adjustBlockStateBits(Map<Integer, Integer> oldToNewMapping, long[] blockStates) {
/* 245 */     int newBits = 32 - Integer.numberOfLeadingZeros(this.palette.size() - 1);
/* 246 */     newBits = (newBits < 4) ? 4 : newBits;
/*     */     
/* 248 */     long[] newBlockStates = (newBits == blockStates.length / 64) ? blockStates : new long[newBits * 64];
/* 249 */     if (oldToNewMapping != null) {
/* 250 */       for (int i = 0; i < 4096; i++) {
/* 251 */         setPaletteIndex(i, ((Integer)oldToNewMapping.get(Integer.valueOf(getPaletteIndex(i)))).intValue(), newBlockStates);
/*     */       }
/*     */     } else {
/* 254 */       for (int i = 0; i < 4096; i++) {
/* 255 */         setPaletteIndex(i, getPaletteIndex(i), newBlockStates);
/*     */       }
/*     */     } 
/* 258 */     this.blockStates = newBlockStates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBlockLight() {
/* 265 */     return this.blockLight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlockLight(byte[] blockLight) {
/* 274 */     if (blockLight != null && blockLight.length != 2048) {
/* 275 */       throw new IllegalArgumentException("BlockLight array must have a length of 2048");
/*     */     }
/* 277 */     this.blockLight = blockLight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long[] getBlockStates() {
/* 284 */     return this.blockStates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlockStates(long[] blockStates) {
/* 294 */     if (blockStates == null)
/* 295 */       throw new NullPointerException("BlockStates cannot be null"); 
/* 296 */     if (blockStates.length % 64 != 0 || blockStates.length < 256 || blockStates.length > 4096) {
/* 297 */       throw new IllegalArgumentException("BlockStates must have a length > 255 and < 4097 and must be divisible by 64");
/*     */     }
/* 299 */     this.blockStates = blockStates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getSkyLight() {
/* 306 */     return this.skyLight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSkyLight(byte[] skyLight) {
/* 315 */     if (skyLight != null && skyLight.length != 2048) {
/* 316 */       throw new IllegalArgumentException("SkyLight array must have a length of 2048");
/*     */     }
/* 318 */     this.skyLight = skyLight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Section newSection() {
/* 326 */     Section s = new Section();
/* 327 */     s.blockStates = new long[256];
/* 328 */     s.palette = new ListTag(CompoundTag.class);
/* 329 */     CompoundTag air = new CompoundTag();
/* 330 */     air.putString("Name", "minecraft:air");
/* 331 */     s.palette.add((Tag)air);
/* 332 */     s.data = new CompoundTag();
/* 333 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompoundTag updateHandle(int y) {
/* 344 */     this.data.putByte("Y", (byte)y);
/* 345 */     this.data.put("Palette", (Tag)this.palette);
/* 346 */     if (this.blockLight != null) this.data.putByteArray("BlockLight", this.blockLight); 
/* 347 */     this.data.putLongArray("BlockStates", this.blockStates);
/* 348 */     if (this.skyLight != null) this.data.putByteArray("SkyLight", this.skyLight); 
/* 349 */     return this.data;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\Section.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */