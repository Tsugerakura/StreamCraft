/*     */ package net.querz.nbt;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ 
/*     */ public class CompoundTag
/*     */   extends Tag<Map<String, Tag<?>>> implements Iterable<Map.Entry<String, Tag<?>>>, Comparable<CompoundTag> {
/*     */   public CompoundTag() {
/*  17 */     super(createEmptyValue());
/*     */   }
/*     */   
/*     */   private static Map<String, Tag<?>> createEmptyValue() {
/*  21 */     return new HashMap<>(8);
/*     */   }
/*     */   
/*     */   public int size() {
/*  25 */     return getValue().size();
/*     */   }
/*     */   
/*     */   public Tag<?> remove(String key) {
/*  29 */     return getValue().remove(key);
/*     */   }
/*     */   
/*     */   public void clear() {
/*  33 */     getValue().clear();
/*     */   }
/*     */   
/*     */   public boolean containsKey(String key) {
/*  37 */     return getValue().containsKey(key);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Tag<?> value) {
/*  41 */     return getValue().containsValue(value);
/*     */   }
/*     */   
/*     */   public Collection<Tag<?>> values() {
/*  45 */     return getValue().values();
/*     */   }
/*     */   
/*     */   public Set<String> keySet() {
/*  49 */     return getValue().keySet();
/*     */   }
/*     */   
/*     */   public Set<Map.Entry<String, Tag<?>>> entrySet() {
/*  53 */     return new NonNullEntrySet<>(getValue().entrySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<String, Tag<?>>> iterator() {
/*  58 */     return entrySet().iterator();
/*     */   }
/*     */   
/*     */   public void forEach(BiConsumer<String, Tag<?>> action) {
/*  62 */     getValue().forEach(action);
/*     */   }
/*     */   
/*     */   public <C extends Tag<?>> C get(String key, Class<C> type) {
/*  66 */     Tag<?> t = getValue().get(key);
/*  67 */     if (t != null) {
/*  68 */       return type.cast(t);
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */   
/*     */   public Tag<?> get(String key) {
/*  74 */     return getValue().get(key);
/*     */   }
/*     */   
/*     */   public ByteTag getByteTag(String key) {
/*  78 */     return get(key, ByteTag.class);
/*     */   }
/*     */   
/*     */   public ShortTag getShortTag(String key) {
/*  82 */     return get(key, ShortTag.class);
/*     */   }
/*     */   
/*     */   public IntTag getIntTag(String key) {
/*  86 */     return get(key, IntTag.class);
/*     */   }
/*     */   
/*     */   public LongTag getLongTag(String key) {
/*  90 */     return get(key, LongTag.class);
/*     */   }
/*     */   
/*     */   public FloatTag getFloatTag(String key) {
/*  94 */     return get(key, FloatTag.class);
/*     */   }
/*     */   
/*     */   public DoubleTag getDoubleTag(String key) {
/*  98 */     return get(key, DoubleTag.class);
/*     */   }
/*     */   
/*     */   public StringTag getStringTag(String key) {
/* 102 */     return get(key, StringTag.class);
/*     */   }
/*     */   
/*     */   public ByteArrayTag getByteArrayTag(String key) {
/* 106 */     return get(key, ByteArrayTag.class);
/*     */   }
/*     */   
/*     */   public IntArrayTag getIntArrayTag(String key) {
/* 110 */     return get(key, IntArrayTag.class);
/*     */   }
/*     */   
/*     */   public LongArrayTag getLongArrayTag(String key) {
/* 114 */     return get(key, LongArrayTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<?> getListTag(String key) {
/* 118 */     return get(key, ListTag.class);
/*     */   }
/*     */   
/*     */   public CompoundTag getCompoundTag(String key) {
/* 122 */     return get(key, CompoundTag.class);
/*     */   }
/*     */   
/*     */   public boolean getBoolean(String key) {
/* 126 */     Tag<?> t = get(key);
/* 127 */     return (t instanceof ByteTag && ((ByteTag)t).asByte() > 0);
/*     */   }
/*     */   
/*     */   public byte getByte(String key) {
/* 131 */     ByteTag t = getByteTag(key);
/* 132 */     return (t == null) ? 0 : t.asByte();
/*     */   }
/*     */   
/*     */   public short getShort(String key) {
/* 136 */     ShortTag t = getShortTag(key);
/* 137 */     return (t == null) ? 0 : t.asShort();
/*     */   }
/*     */   
/*     */   public int getInt(String key) {
/* 141 */     IntTag t = getIntTag(key);
/* 142 */     return (t == null) ? 0 : t.asInt();
/*     */   }
/*     */   
/*     */   public long getLong(String key) {
/* 146 */     LongTag t = getLongTag(key);
/* 147 */     return (t == null) ? 0L : t.asLong();
/*     */   }
/*     */   
/*     */   public float getFloat(String key) {
/* 151 */     FloatTag t = getFloatTag(key);
/* 152 */     return (t == null) ? 0.0F : t.asFloat();
/*     */   }
/*     */   
/*     */   public double getDouble(String key) {
/* 156 */     DoubleTag t = getDoubleTag(key);
/* 157 */     return (t == null) ? 0.0D : t.asDouble();
/*     */   }
/*     */   
/*     */   public String getString(String key) {
/* 161 */     StringTag t = getStringTag(key);
/* 162 */     return (t == null) ? "" : t.getValue();
/*     */   }
/*     */   
/*     */   public byte[] getByteArray(String key) {
/* 166 */     ByteArrayTag t = getByteArrayTag(key);
/* 167 */     return (t == null) ? ByteArrayTag.ZERO_VALUE : t.getValue();
/*     */   }
/*     */   
/*     */   public int[] getIntArray(String key) {
/* 171 */     IntArrayTag t = getIntArrayTag(key);
/* 172 */     return (t == null) ? IntArrayTag.ZERO_VALUE : t.getValue();
/*     */   }
/*     */   
/*     */   public long[] getLongArray(String key) {
/* 176 */     LongArrayTag t = getLongArrayTag(key);
/* 177 */     return (t == null) ? LongArrayTag.ZERO_VALUE : t.getValue();
/*     */   }
/*     */   
/*     */   public Tag<?> put(String key, Tag<?> tag) {
/* 181 */     return getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
/*     */   }
/*     */   
/*     */   public Tag<?> putBoolean(String key, boolean value) {
/* 185 */     return put(key, new ByteTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putByte(String key, byte value) {
/* 189 */     return put(key, new ByteTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putShort(String key, short value) {
/* 193 */     return put(key, new ShortTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putInt(String key, int value) {
/* 197 */     return put(key, new IntTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putLong(String key, long value) {
/* 201 */     return put(key, new LongTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putFloat(String key, float value) {
/* 205 */     return put(key, new FloatTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putDouble(String key, double value) {
/* 209 */     return put(key, new DoubleTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putString(String key, String value) {
/* 213 */     return put(key, new StringTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putByteArray(String key, byte[] value) {
/* 217 */     return put(key, new ByteArrayTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putIntArray(String key, int[] value) {
/* 221 */     return put(key, new IntArrayTag(value));
/*     */   }
/*     */   
/*     */   public Tag<?> putLongArray(String key, long[] value) {
/* 225 */     return put(key, new LongArrayTag(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 230 */     for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
/* 231 */       ((Tag)e.getValue()).serialize(dos, e.getKey(), decrementMaxDepth(maxDepth));
/*     */     }
/* 233 */     EndTag.INSTANCE.serialize(dos, maxDepth);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 238 */     clear();
/* 239 */     for (int id = dis.readByte() & 0xFF; id != 0; id = dis.readByte() & 0xFF) {
/* 240 */       Tag<?> tag = TagFactory.fromID(id);
/* 241 */       String name = dis.readUTF();
/* 242 */       tag.deserializeValue(dis, decrementMaxDepth(maxDepth));
/* 243 */       put(name, tag);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToString(int maxDepth) {
/* 249 */     StringBuilder sb = new StringBuilder("{");
/* 250 */     boolean first = true;
/* 251 */     for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
/* 252 */       sb.append(first ? "" : ",")
/* 253 */         .append(escapeString(e.getKey(), false)).append(":")
/* 254 */         .append(((Tag)e.getValue()).toString(decrementMaxDepth(maxDepth)));
/* 255 */       first = false;
/*     */     } 
/* 257 */     sb.append("}");
/* 258 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToTagString(int maxDepth) {
/* 263 */     StringBuilder sb = new StringBuilder("{");
/* 264 */     boolean first = true;
/* 265 */     for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
/* 266 */       sb.append(first ? "" : ",")
/* 267 */         .append(escapeString(e.getKey(), true)).append(":")
/* 268 */         .append(((Tag)e.getValue()).valueToTagString(decrementMaxDepth(maxDepth)));
/* 269 */       first = false;
/*     */     } 
/* 271 */     sb.append("}");
/* 272 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 277 */     if (this == other) {
/* 278 */       return true;
/*     */     }
/* 280 */     if (!super.equals(other) || size() != ((CompoundTag)other).size()) {
/* 281 */       return false;
/*     */     }
/* 283 */     for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
/*     */       Tag<?> v;
/* 285 */       if ((v = ((CompoundTag)other).get(e.getKey())) == null || !((Tag)e.getValue()).equals(v)) {
/* 286 */         return false;
/*     */       }
/*     */     } 
/* 289 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(CompoundTag o) {
/* 294 */     return Integer.compare(size(), o.getValue().size());
/*     */   }
/*     */ 
/*     */   
/*     */   public CompoundTag clone() {
/* 299 */     CompoundTag copy = new CompoundTag();
/* 300 */     for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
/* 301 */       copy.put(e.getKey(), ((Tag)e.getValue()).clone());
/*     */     }
/* 303 */     return copy;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\CompoundTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */