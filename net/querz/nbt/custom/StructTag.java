/*     */ package net.querz.nbt.custom;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ import net.querz.nbt.ByteArrayTag;
/*     */ import net.querz.nbt.ByteTag;
/*     */ import net.querz.nbt.CompoundTag;
/*     */ import net.querz.nbt.DoubleTag;
/*     */ import net.querz.nbt.FloatTag;
/*     */ import net.querz.nbt.IntArrayTag;
/*     */ import net.querz.nbt.IntTag;
/*     */ import net.querz.nbt.ListTag;
/*     */ import net.querz.nbt.LongArrayTag;
/*     */ import net.querz.nbt.LongTag;
/*     */ import net.querz.nbt.ShortTag;
/*     */ import net.querz.nbt.StringTag;
/*     */ import net.querz.nbt.Tag;
/*     */ import net.querz.nbt.TagFactory;
/*     */ 
/*     */ public class StructTag extends Tag<List<Tag<?>>> implements Iterable<Tag<?>>, Comparable<StructTag> {
/*     */   public static void register() {
/*  30 */     TagFactory.registerCustomTag(120, StructTag::new, StructTag.class);
/*     */   }
/*     */   
/*     */   public StructTag() {
/*  34 */     super(createEmptyValue());
/*     */   }
/*     */   
/*     */   private static List<Tag<?>> createEmptyValue() {
/*  38 */     return new ArrayList<>(3);
/*     */   }
/*     */   
/*     */   public int size() {
/*  42 */     return ((List)getValue()).size();
/*     */   }
/*     */   
/*     */   public Tag<?> remove(int index) {
/*  46 */     return ((List<Tag>)getValue()).remove(index);
/*     */   }
/*     */   
/*     */   public boolean remove(Tag<?> tag) {
/*  50 */     return ((List)getValue()).remove(tag);
/*     */   }
/*     */   
/*     */   public void clear() {
/*  54 */     ((List)getValue()).clear();
/*     */   }
/*     */   
/*     */   public boolean contains(Tag<?> tag) {
/*  58 */     return ((List)getValue()).contains(tag);
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<Tag<?>> tags) {
/*  62 */     return ((List)getValue()).containsAll(tags);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Tag<?>> iterator() {
/*  67 */     return ((List<Tag<?>>)getValue()).iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(Consumer<? super Tag<?>> action) {
/*  72 */     ((List<Tag<?>>)getValue()).forEach(action);
/*     */   }
/*     */   
/*     */   public <S extends Tag<?>> S get(int index, Class<S> type) {
/*  76 */     Tag<?> t = ((List<Tag>)getValue()).get(index);
/*  77 */     return type.cast(t);
/*     */   }
/*     */   
/*     */   public Tag<?> get(int index) {
/*  81 */     return ((List<Tag>)getValue()).get(index);
/*     */   }
/*     */   
/*     */   public ByteTag getByteTag(int index) {
/*  85 */     return get(index, ByteTag.class);
/*     */   }
/*     */   
/*     */   public ShortTag getShortTag(int index) {
/*  89 */     return get(index, ShortTag.class);
/*     */   }
/*     */   
/*     */   public IntTag getIntTag(int index) {
/*  93 */     return get(index, IntTag.class);
/*     */   }
/*     */   
/*     */   public LongTag getLongTag(int index) {
/*  97 */     return get(index, LongTag.class);
/*     */   }
/*     */   
/*     */   public FloatTag getFloatTag(int index) {
/* 101 */     return get(index, FloatTag.class);
/*     */   }
/*     */   
/*     */   public DoubleTag getDoubleTag(int index) {
/* 105 */     return get(index, DoubleTag.class);
/*     */   }
/*     */   
/*     */   public StringTag getStringTag(int index) {
/* 109 */     return get(index, StringTag.class);
/*     */   }
/*     */   
/*     */   public ByteArrayTag getByteArrayTag(int index) {
/* 113 */     return get(index, ByteArrayTag.class);
/*     */   }
/*     */   
/*     */   public IntArrayTag getIntArrayTag(int index) {
/* 117 */     return get(index, IntArrayTag.class);
/*     */   }
/*     */   
/*     */   public LongArrayTag getLongArrayTag(int index) {
/* 121 */     return get(index, LongArrayTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<?> getListTag(int index) {
/* 125 */     return get(index, ListTag.class);
/*     */   }
/*     */   
/*     */   public CompoundTag getCompoundTag(int index) {
/* 129 */     return get(index, CompoundTag.class);
/*     */   }
/*     */   
/*     */   public boolean getBoolean(int index) {
/* 133 */     Tag<?> t = get(index);
/* 134 */     return (t instanceof ByteTag && ((ByteTag)t).asByte() > 0);
/*     */   }
/*     */   
/*     */   public byte getByte(int index) {
/* 138 */     return getByteTag(index).asByte();
/*     */   }
/*     */   
/*     */   public short getShort(int index) {
/* 142 */     return getShortTag(index).asShort();
/*     */   }
/*     */   
/*     */   public int getInt(int index) {
/* 146 */     return getIntTag(index).asInt();
/*     */   }
/*     */   
/*     */   public long getLong(int index) {
/* 150 */     return getLongTag(index).asLong();
/*     */   }
/*     */   
/*     */   public float getFloat(int index) {
/* 154 */     return getFloatTag(index).asFloat();
/*     */   }
/*     */   
/*     */   public double getDouble(int index) {
/* 158 */     return getDoubleTag(index).asDouble();
/*     */   }
/*     */   
/*     */   public String getString(int index) {
/* 162 */     return getStringTag(index).getValue();
/*     */   }
/*     */   
/*     */   public byte[] getByteArray(int index) {
/* 166 */     return (byte[])getByteArrayTag(index).getValue();
/*     */   }
/*     */   
/*     */   public int[] getIntArray(int index) {
/* 170 */     return (int[])getIntArrayTag(index).getValue();
/*     */   }
/*     */   
/*     */   public long[] getLongArray(int index) {
/* 174 */     return (long[])getLongArrayTag(index).getValue();
/*     */   }
/*     */   
/*     */   public Tag<?> set(int index, Tag<?> tag) {
/* 178 */     return ((List<Tag>)getValue()).set(index, Objects.requireNonNull(tag));
/*     */   }
/*     */   
/*     */   public void add(Tag<?> tag) {
/* 182 */     ((List)getValue()).add(Objects.requireNonNull(tag));
/*     */   }
/*     */   
/*     */   public void add(int index, Tag<?> tag) {
/* 186 */     ((List)getValue()).add(index, Objects.requireNonNull(tag));
/*     */   }
/*     */   
/*     */   public void addBoolean(boolean value) {
/* 190 */     add((Tag<?>)new ByteTag(value));
/*     */   }
/*     */   
/*     */   public void addByte(byte value) {
/* 194 */     add((Tag<?>)new ByteTag(value));
/*     */   }
/*     */   
/*     */   public void addShort(short value) {
/* 198 */     add((Tag<?>)new ShortTag(value));
/*     */   }
/*     */   
/*     */   public void addInt(int value) {
/* 202 */     add((Tag<?>)new IntTag(value));
/*     */   }
/*     */   
/*     */   public void addLong(long value) {
/* 206 */     add((Tag<?>)new LongTag(value));
/*     */   }
/*     */   
/*     */   public void addFloat(float value) {
/* 210 */     add((Tag<?>)new FloatTag(value));
/*     */   }
/*     */   
/*     */   public void addDouble(double value) {
/* 214 */     add((Tag<?>)new DoubleTag(value));
/*     */   }
/*     */   
/*     */   public void addString(String value) {
/* 218 */     add((Tag<?>)new StringTag(value));
/*     */   }
/*     */   
/*     */   public void addByteArray(byte[] value) {
/* 222 */     add((Tag<?>)new ByteArrayTag(value));
/*     */   }
/*     */   
/*     */   public void addIntArray(int[] value) {
/* 226 */     add((Tag<?>)new IntArrayTag(value));
/*     */   }
/*     */   
/*     */   public void addLongArray(long[] value) {
/* 230 */     add((Tag<?>)new LongArrayTag(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 235 */     dos.writeInt(size());
/* 236 */     for (Tag<?> tag : (Iterable<Tag<?>>)getValue()) {
/* 237 */       dos.writeByte(tag.getID());
/* 238 */       tag.serializeValue(dos, decrementMaxDepth(maxDepth));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 244 */     int size = dis.readInt();
/* 245 */     size = (size < 0) ? 0 : size;
/* 246 */     setValue(new ArrayList(size));
/* 247 */     for (int i = 0; i < size; i++) {
/* 248 */       Tag<?> tag = TagFactory.fromID(dis.readByte());
/* 249 */       tag.deserializeValue(dis, decrementMaxDepth(maxDepth));
/* 250 */       add(tag);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToString(int maxDepth) {
/* 256 */     StringBuilder sb = new StringBuilder("[");
/* 257 */     for (int i = 0; i < size(); i++) {
/* 258 */       sb.append((i > 0) ? "," : "").append(get(i).toString(decrementMaxDepth(maxDepth)));
/*     */     }
/* 260 */     sb.append("]");
/* 261 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToTagString(int maxDepth) {
/* 266 */     StringBuilder sb = new StringBuilder("[");
/* 267 */     for (int i = 0; i < size(); i++) {
/* 268 */       sb.append((i > 0) ? "," : "").append(get(i).valueToTagString(decrementMaxDepth(maxDepth)));
/*     */     }
/* 270 */     sb.append("]");
/* 271 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 276 */     if (!super.equals(other) || size() != ((StructTag)other).size()) {
/* 277 */       return false;
/*     */     }
/* 279 */     for (int i = 0; i < size(); i++) {
/* 280 */       if (!get(i).equals(((StructTag)other).get(i))) {
/* 281 */         return false;
/*     */       }
/*     */     } 
/* 284 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 289 */     return ((List)getValue()).hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(StructTag o) {
/* 294 */     return Integer.compare(size(), o.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public StructTag clone() {
/* 299 */     StructTag copy = new StructTag();
/* 300 */     for (Tag<?> tag : (Iterable<Tag<?>>)getValue()) {
/* 301 */       copy.add(tag.clone());
/*     */     }
/* 303 */     return copy;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\custom\StructTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */