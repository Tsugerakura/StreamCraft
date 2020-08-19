/*     */ package net.querz.nbt;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ListTag<T extends Tag<?>>
/*     */   extends Tag<List<T>>
/*     */   implements Iterable<T>, Comparable<ListTag<T>>
/*     */ {
/*  23 */   private Class<?> typeClass = null;
/*     */   
/*     */   private ListTag() {
/*  26 */     super(createEmptyValue(3));
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
/*     */   protected static ListTag<?> createUnchecked() {
/*  39 */     return new ListTag();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> List<T> createEmptyValue(int initialCapacity) {
/*  50 */     return new ArrayList<>(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
/*  59 */     super(createEmptyValue(3));
/*  60 */     if (typeClass == EndTag.class) {
/*  61 */       throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
/*     */     }
/*  63 */     this.typeClass = Objects.<Class<?>>requireNonNull(typeClass);
/*     */   }
/*     */   
/*     */   public Class<?> getTypeClass() {
/*  67 */     return (this.typeClass == null) ? EndTag.class : this.typeClass;
/*     */   }
/*     */   
/*     */   public int size() {
/*  71 */     return getValue().size();
/*     */   }
/*     */   
/*     */   public T remove(int index) {
/*  75 */     return getValue().remove(index);
/*     */   }
/*     */   
/*     */   public void clear() {
/*  79 */     getValue().clear();
/*     */   }
/*     */   
/*     */   public boolean contains(T t) {
/*  83 */     return getValue().contains(t);
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<Tag<?>> tags) {
/*  87 */     return getValue().containsAll(tags);
/*     */   }
/*     */   
/*     */   public void sort(Comparator<T> comparator) {
/*  91 */     getValue().sort(comparator);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/*  96 */     return getValue().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(Consumer<? super T> action) {
/* 101 */     getValue().forEach(action);
/*     */   }
/*     */   
/*     */   public T set(int index, T t) {
/* 105 */     return getValue().set(index, Objects.requireNonNull(t));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(T t) {
/* 113 */     add(size(), t);
/*     */   }
/*     */   
/*     */   public void add(int index, T t) {
/* 117 */     Objects.requireNonNull(t);
/* 118 */     if (this.typeClass == null || this.typeClass == EndTag.class) {
/* 119 */       this.typeClass = t.getClass();
/* 120 */     } else if (this.typeClass != t.getClass()) {
/* 121 */       throw new ClassCastException(
/* 122 */           String.format("cannot add %s to ListTag<%s>", new Object[] {
/* 123 */               t.getClass().getSimpleName(), this.typeClass
/* 124 */               .getSimpleName() }));
/*     */     } 
/* 126 */     getValue().add(index, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(Collection<T> t) {
/* 131 */     for (Tag tag : t) {
/* 132 */       add((T)tag);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addAll(int index, Collection<T> t) {
/* 137 */     int i = 0;
/* 138 */     for (Tag tag : t) {
/* 139 */       add(index + i, (T)tag);
/* 140 */       i++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addBoolean(boolean value) {
/* 145 */     addUnchecked(new ByteTag(value));
/*     */   }
/*     */   
/*     */   public void addByte(byte value) {
/* 149 */     addUnchecked(new ByteTag(value));
/*     */   }
/*     */   
/*     */   public void addShort(short value) {
/* 153 */     addUnchecked(new ShortTag(value));
/*     */   }
/*     */   
/*     */   public void addInt(int value) {
/* 157 */     addUnchecked(new IntTag(value));
/*     */   }
/*     */   
/*     */   public void addLong(long value) {
/* 161 */     addUnchecked(new LongTag(value));
/*     */   }
/*     */   
/*     */   public void addFloat(float value) {
/* 165 */     addUnchecked(new FloatTag(value));
/*     */   }
/*     */   
/*     */   public void addDouble(double value) {
/* 169 */     addUnchecked(new DoubleTag(value));
/*     */   }
/*     */   
/*     */   public void addString(String value) {
/* 173 */     addUnchecked(new StringTag(value));
/*     */   }
/*     */   
/*     */   public void addByteArray(byte[] value) {
/* 177 */     addUnchecked(new ByteArrayTag(value));
/*     */   }
/*     */   
/*     */   public void addIntArray(int[] value) {
/* 181 */     addUnchecked(new IntArrayTag(value));
/*     */   }
/*     */   
/*     */   public void addLongArray(long[] value) {
/* 185 */     addUnchecked(new LongArrayTag(value));
/*     */   }
/*     */   
/*     */   public T get(int index) {
/* 189 */     return getValue().get(index);
/*     */   }
/*     */   
/*     */   public int indexOf(T t) {
/* 193 */     return getValue().indexOf(t);
/*     */   }
/*     */ 
/*     */   
/*     */   public <L extends Tag<?>> ListTag<L> asTypedList(Class<L> type) {
/* 198 */     checkTypeClass(type);
/* 199 */     this.typeClass = type;
/* 200 */     return this;
/*     */   }
/*     */   
/*     */   public ListTag<ByteTag> asByteTagList() {
/* 204 */     return asTypedList(ByteTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<ShortTag> asShortTagList() {
/* 208 */     return asTypedList(ShortTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<IntTag> asIntTagList() {
/* 212 */     return asTypedList(IntTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<LongTag> asLongTagList() {
/* 216 */     return asTypedList(LongTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<FloatTag> asFloatTagList() {
/* 220 */     return asTypedList(FloatTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<DoubleTag> asDoubleTagList() {
/* 224 */     return asTypedList(DoubleTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<StringTag> asStringTagList() {
/* 228 */     return asTypedList(StringTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<ByteArrayTag> asByteArrayTagList() {
/* 232 */     return asTypedList(ByteArrayTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<IntArrayTag> asIntArrayTagList() {
/* 236 */     return asTypedList(IntArrayTag.class);
/*     */   }
/*     */   
/*     */   public ListTag<LongArrayTag> asLongArrayTagList() {
/* 240 */     return asTypedList(LongArrayTag.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public ListTag<ListTag<?>> asListTagList() {
/* 245 */     checkTypeClass(ListTag.class);
/* 246 */     this.typeClass = ListTag.class;
/* 247 */     return (ListTag)this;
/*     */   }
/*     */   
/*     */   public ListTag<CompoundTag> asCompoundTagList() {
/* 251 */     return asTypedList(CompoundTag.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 256 */     dos.writeByte(TagFactory.idFromClass(getTypeClass()));
/* 257 */     dos.writeInt(size());
/* 258 */     if (size() != 0) {
/* 259 */       for (Tag tag : getValue()) {
/* 260 */         tag.serializeValue(dos, decrementMaxDepth(maxDepth));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 268 */     int typeID = dis.readByte();
/* 269 */     if (typeID != 0) {
/* 270 */       this.typeClass = TagFactory.classFromID(typeID);
/*     */     }
/* 272 */     int size = dis.readInt();
/* 273 */     size = (size < 0) ? 0 : size;
/* 274 */     setValue(createEmptyValue(size));
/* 275 */     if (size != 0) {
/* 276 */       for (int i = 0; i < size; i++) {
/* 277 */         Tag<?> tag = TagFactory.fromID(typeID);
/* 278 */         tag.deserializeValue(dis, decrementMaxDepth(maxDepth));
/* 279 */         add((T)tag);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToString(int maxDepth) {
/* 286 */     StringBuilder sb = (new StringBuilder("{\"type\":\"")).append(getTypeClass().getSimpleName()).append("\",\"list\":[");
/* 287 */     for (int i = 0; i < size(); i++) {
/* 288 */       sb.append((i > 0) ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
/*     */     }
/* 290 */     sb.append("]}");
/* 291 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToTagString(int maxDepth) {
/* 296 */     StringBuilder sb = new StringBuilder("[");
/* 297 */     for (int i = 0; i < size(); i++) {
/* 298 */       sb.append((i > 0) ? "," : "").append(get(i).valueToTagString(decrementMaxDepth(maxDepth)));
/*     */     }
/* 300 */     sb.append("]");
/* 301 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 306 */     if (this == other) {
/* 307 */       return true;
/*     */     }
/* 309 */     if (!super.equals(other) || size() != ((ListTag)other).size() || getTypeClass() != ((ListTag)other).getTypeClass()) {
/* 310 */       return false;
/*     */     }
/* 312 */     for (int i = 0; i < size(); i++) {
/* 313 */       if (!get(i).equals(((ListTag)other).get(i))) {
/* 314 */         return false;
/*     */       }
/*     */     } 
/* 317 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 322 */     return Objects.hash(new Object[] { Integer.valueOf(getTypeClass().hashCode()), Integer.valueOf(getValue().hashCode()) });
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(ListTag<T> o) {
/* 327 */     return Integer.compare(size(), o.getValue().size());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ListTag<T> clone() {
/* 333 */     ListTag<T> copy = new ListTag();
/*     */     
/* 335 */     copy.typeClass = this.typeClass;
/* 336 */     for (Tag tag : getValue()) {
/* 337 */       copy.add((T)tag.clone());
/*     */     }
/* 339 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addUnchecked(Tag<?> tag) {
/* 344 */     if (this.typeClass != null && this.typeClass != tag.getClass()) {
/* 345 */       throw new IllegalArgumentException(String.format("cannot add %s to ListTag<%s>", new Object[] { tag
/*     */               
/* 347 */               .getClass().getSimpleName(), this.typeClass.getSimpleName() }));
/*     */     }
/* 349 */     add(size(), (T)tag);
/*     */   }
/*     */   
/*     */   private void checkTypeClass(Class<?> clazz) {
/* 353 */     if (this.typeClass != null && this.typeClass != clazz)
/* 354 */       throw new ClassCastException(String.format("cannot cast ListTag<%s> to ListTag<%s>", new Object[] { this.typeClass
/*     */               
/* 356 */               .getSimpleName(), clazz.getSimpleName() })); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\ListTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */