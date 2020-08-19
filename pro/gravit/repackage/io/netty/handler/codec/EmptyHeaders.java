/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class EmptyHeaders<K, V, T extends Headers<K, V, T>>
/*     */   implements Headers<K, V, T>
/*     */ {
/*     */   public V get(K name) {
/*  28 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(K name, V defaultValue) {
/*  33 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public V getAndRemove(K name) {
/*  38 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V getAndRemove(K name, V defaultValue) {
/*  43 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<V> getAll(K name) {
/*  48 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<V> getAllAndRemove(K name) {
/*  53 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean getBoolean(K name) {
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean(K name, boolean defaultValue) {
/*  63 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte getByte(K name) {
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(K name, byte defaultValue) {
/*  73 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Character getChar(K name) {
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public char getChar(K name, char defaultValue) {
/*  83 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(K name) {
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(K name, short defaultValue) {
/*  93 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(K name) {
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(K name, int defaultValue) {
/* 103 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getLong(K name) {
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(K name, long defaultValue) {
/* 113 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Float getFloat(K name) {
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat(K name, float defaultValue) {
/* 123 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Double getDouble(K name) {
/* 128 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(K name, double defaultValue) {
/* 133 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getTimeMillis(K name) {
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeMillis(K name, long defaultValue) {
/* 143 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean getBooleanAndRemove(K name) {
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBooleanAndRemove(K name, boolean defaultValue) {
/* 153 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte getByteAndRemove(K name) {
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByteAndRemove(K name, byte defaultValue) {
/* 163 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Character getCharAndRemove(K name) {
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public char getCharAndRemove(K name, char defaultValue) {
/* 173 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShortAndRemove(K name) {
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShortAndRemove(K name, short defaultValue) {
/* 183 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getIntAndRemove(K name) {
/* 188 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntAndRemove(K name, int defaultValue) {
/* 193 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getLongAndRemove(K name) {
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongAndRemove(K name, long defaultValue) {
/* 203 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Float getFloatAndRemove(K name) {
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloatAndRemove(K name, float defaultValue) {
/* 213 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Double getDoubleAndRemove(K name) {
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDoubleAndRemove(K name, double defaultValue) {
/* 223 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getTimeMillisAndRemove(K name) {
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeMillisAndRemove(K name, long defaultValue) {
/* 233 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(K name) {
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(K name, V value) {
/* 243 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsObject(K name, Object value) {
/* 248 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsBoolean(K name, boolean value) {
/* 253 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsByte(K name, byte value) {
/* 258 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsChar(K name, char value) {
/* 263 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsShort(K name, short value) {
/* 268 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsInt(K name, int value) {
/* 273 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsLong(K name, long value) {
/* 278 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsFloat(K name, float value) {
/* 283 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsDouble(K name, double value) {
/* 288 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsTimeMillis(K name, long value) {
/* 293 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 298 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 303 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<K> names() {
/* 308 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public T add(K name, V value) {
/* 313 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T add(K name, Iterable<? extends V> values) {
/* 318 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T add(K name, V... values) {
/* 323 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addObject(K name, Object value) {
/* 328 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addObject(K name, Iterable<?> values) {
/* 333 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addObject(K name, Object... values) {
/* 338 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addBoolean(K name, boolean value) {
/* 343 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addByte(K name, byte value) {
/* 348 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addChar(K name, char value) {
/* 353 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addShort(K name, short value) {
/* 358 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addInt(K name, int value) {
/* 363 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addLong(K name, long value) {
/* 368 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addFloat(K name, float value) {
/* 373 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addDouble(K name, double value) {
/* 378 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T addTimeMillis(K name, long value) {
/* 383 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T add(Headers<? extends K, ? extends V, ?> headers) {
/* 388 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T set(K name, V value) {
/* 393 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T set(K name, Iterable<? extends V> values) {
/* 398 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T set(K name, V... values) {
/* 403 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setObject(K name, Object value) {
/* 408 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setObject(K name, Iterable<?> values) {
/* 413 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setObject(K name, Object... values) {
/* 418 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setBoolean(K name, boolean value) {
/* 423 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setByte(K name, byte value) {
/* 428 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setChar(K name, char value) {
/* 433 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setShort(K name, short value) {
/* 438 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setInt(K name, int value) {
/* 443 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setLong(K name, long value) {
/* 448 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setFloat(K name, float value) {
/* 453 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setDouble(K name, double value) {
/* 458 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setTimeMillis(K name, long value) {
/* 463 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T set(Headers<? extends K, ? extends V, ?> headers) {
/* 468 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public T setAll(Headers<? extends K, ? extends V, ?> headers) {
/* 473 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(K name) {
/* 478 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public T clear() {
/* 483 */     return thisT();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<V> valueIterator(K name) {
/* 492 */     List<V> empty = Collections.emptyList();
/* 493 */     return empty.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/* 498 */     List<Map.Entry<K, V>> empty = Collections.emptyList();
/* 499 */     return empty.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 504 */     if (!(o instanceof Headers)) {
/* 505 */       return false;
/*     */     }
/*     */     
/* 508 */     Headers<?, ?, ?> rhs = (Headers<?, ?, ?>)o;
/* 509 */     return (isEmpty() && rhs.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 514 */     return -1028477387;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 519 */     return getClass().getSimpleName() + '[' + ']';
/*     */   }
/*     */ 
/*     */   
/*     */   private T thisT() {
/* 524 */     return (T)this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\EmptyHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */