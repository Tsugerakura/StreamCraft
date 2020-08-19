/*      */ package pro.gravit.repackage.io.netty.handler.codec;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import pro.gravit.repackage.io.netty.util.HashingStrategy;
/*      */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DefaultHeaders<K, V, T extends Headers<K, V, T>>
/*      */   implements Headers<K, V, T>
/*      */ {
/*      */   static final int HASH_CODE_SEED = -1028477387;
/*      */   private final HeaderEntry<K, V>[] entries;
/*      */   protected final HeaderEntry<K, V> head;
/*      */   private final byte hashMask;
/*      */   private final ValueConverter<V> valueConverter;
/*      */   private final NameValidator<K> nameValidator;
/*      */   private final HashingStrategy<K> hashingStrategy;
/*      */   int size;
/*      */   
/*      */   public static interface NameValidator<K>
/*      */   {
/*   67 */     public static final NameValidator NOT_NULL = new NameValidator()
/*      */       {
/*      */         public void validateName(Object name) {
/*   70 */           ObjectUtil.checkNotNull(name, "name");
/*      */         }
/*      */       };
/*      */     
/*      */     void validateName(K param1K); }
/*      */   
/*      */   public DefaultHeaders(ValueConverter<V> valueConverter) {
/*   77 */     this(HashingStrategy.JAVA_HASHER, valueConverter);
/*      */   }
/*      */ 
/*      */   
/*      */   public DefaultHeaders(ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
/*   82 */     this(HashingStrategy.JAVA_HASHER, valueConverter, nameValidator);
/*      */   }
/*      */ 
/*      */   
/*      */   public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter) {
/*   87 */     this(nameHashingStrategy, valueConverter, NameValidator.NOT_NULL);
/*      */   }
/*      */ 
/*      */   
/*      */   public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
/*   92 */     this(nameHashingStrategy, valueConverter, nameValidator, 16);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DefaultHeaders(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator, int arraySizeHint) {
/*  106 */     this.valueConverter = (ValueConverter<V>)ObjectUtil.checkNotNull(valueConverter, "valueConverter");
/*  107 */     this.nameValidator = (NameValidator<K>)ObjectUtil.checkNotNull(nameValidator, "nameValidator");
/*  108 */     this.hashingStrategy = (HashingStrategy<K>)ObjectUtil.checkNotNull(nameHashingStrategy, "nameHashingStrategy");
/*      */ 
/*      */     
/*  111 */     this.entries = (HeaderEntry<K, V>[])new HeaderEntry[MathUtil.findNextPositivePowerOfTwo(Math.max(2, Math.min(arraySizeHint, 128)))];
/*  112 */     this.hashMask = (byte)(this.entries.length - 1);
/*  113 */     this.head = new HeaderEntry<K, V>();
/*      */   }
/*      */ 
/*      */   
/*      */   public V get(K name) {
/*  118 */     ObjectUtil.checkNotNull(name, "name");
/*      */     
/*  120 */     int h = this.hashingStrategy.hashCode(name);
/*  121 */     int i = index(h);
/*  122 */     HeaderEntry<K, V> e = this.entries[i];
/*  123 */     V value = null;
/*      */     
/*  125 */     while (e != null) {
/*  126 */       if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
/*  127 */         value = e.value;
/*      */       }
/*      */       
/*  130 */       e = e.next;
/*      */     } 
/*  132 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public V get(K name, V defaultValue) {
/*  137 */     V value = get(name);
/*  138 */     if (value == null) {
/*  139 */       return defaultValue;
/*      */     }
/*  141 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public V getAndRemove(K name) {
/*  146 */     int h = this.hashingStrategy.hashCode(name);
/*  147 */     return remove0(h, index(h), (K)ObjectUtil.checkNotNull(name, "name"));
/*      */   }
/*      */ 
/*      */   
/*      */   public V getAndRemove(K name, V defaultValue) {
/*  152 */     V value = getAndRemove(name);
/*  153 */     if (value == null) {
/*  154 */       return defaultValue;
/*      */     }
/*  156 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public List<V> getAll(K name) {
/*  161 */     ObjectUtil.checkNotNull(name, "name");
/*      */     
/*  163 */     LinkedList<V> values = new LinkedList<V>();
/*      */     
/*  165 */     int h = this.hashingStrategy.hashCode(name);
/*  166 */     int i = index(h);
/*  167 */     HeaderEntry<K, V> e = this.entries[i];
/*  168 */     while (e != null) {
/*  169 */       if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
/*  170 */         values.addFirst(e.getValue());
/*      */       }
/*  172 */       e = e.next;
/*      */     } 
/*  174 */     return values;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator<V> valueIterator(K name) {
/*  183 */     return new ValueIterator(name);
/*      */   }
/*      */ 
/*      */   
/*      */   public List<V> getAllAndRemove(K name) {
/*  188 */     List<V> all = getAll(name);
/*  189 */     remove(name);
/*  190 */     return all;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean contains(K name) {
/*  195 */     return (get(name) != null);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsObject(K name, Object value) {
/*  200 */     return contains(name, this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value")));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsBoolean(K name, boolean value) {
/*  205 */     return contains(name, this.valueConverter.convertBoolean(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsByte(K name, byte value) {
/*  210 */     return contains(name, this.valueConverter.convertByte(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsChar(K name, char value) {
/*  215 */     return contains(name, this.valueConverter.convertChar(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsShort(K name, short value) {
/*  220 */     return contains(name, this.valueConverter.convertShort(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsInt(K name, int value) {
/*  225 */     return contains(name, this.valueConverter.convertInt(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsLong(K name, long value) {
/*  230 */     return contains(name, this.valueConverter.convertLong(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsFloat(K name, float value) {
/*  235 */     return contains(name, this.valueConverter.convertFloat(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsDouble(K name, double value) {
/*  240 */     return contains(name, this.valueConverter.convertDouble(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsTimeMillis(K name, long value) {
/*  245 */     return contains(name, this.valueConverter.convertTimeMillis(value));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(K name, V value) {
/*  251 */     return contains(name, value, HashingStrategy.JAVA_HASHER);
/*      */   }
/*      */   
/*      */   public final boolean contains(K name, V value, HashingStrategy<? super V> valueHashingStrategy) {
/*  255 */     ObjectUtil.checkNotNull(name, "name");
/*      */     
/*  257 */     int h = this.hashingStrategy.hashCode(name);
/*  258 */     int i = index(h);
/*  259 */     HeaderEntry<K, V> e = this.entries[i];
/*  260 */     while (e != null) {
/*  261 */       if (e.hash == h && this.hashingStrategy.equals(name, e.key) && valueHashingStrategy.equals(value, e.value)) {
/*  262 */         return true;
/*      */       }
/*  264 */       e = e.next;
/*      */     } 
/*  266 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public int size() {
/*  271 */     return this.size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  276 */     return (this.head == this.head.after);
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<K> names() {
/*  281 */     if (isEmpty()) {
/*  282 */       return Collections.emptySet();
/*      */     }
/*  284 */     Set<K> names = new LinkedHashSet<K>(size());
/*  285 */     HeaderEntry<K, V> e = this.head.after;
/*  286 */     while (e != this.head) {
/*  287 */       names.add(e.getKey());
/*  288 */       e = e.after;
/*      */     } 
/*  290 */     return names;
/*      */   }
/*      */ 
/*      */   
/*      */   public T add(K name, V value) {
/*  295 */     this.nameValidator.validateName(name);
/*  296 */     ObjectUtil.checkNotNull(value, "value");
/*  297 */     int h = this.hashingStrategy.hashCode(name);
/*  298 */     int i = index(h);
/*  299 */     add0(h, i, name, value);
/*  300 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T add(K name, Iterable<? extends V> values) {
/*  305 */     this.nameValidator.validateName(name);
/*  306 */     int h = this.hashingStrategy.hashCode(name);
/*  307 */     int i = index(h);
/*  308 */     for (V v : values) {
/*  309 */       add0(h, i, name, v);
/*      */     }
/*  311 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T add(K name, V... values) {
/*  316 */     this.nameValidator.validateName(name);
/*  317 */     int h = this.hashingStrategy.hashCode(name);
/*  318 */     int i = index(h);
/*  319 */     for (V v : values) {
/*  320 */       add0(h, i, name, v);
/*      */     }
/*  322 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T addObject(K name, Object value) {
/*  327 */     return add(name, this.valueConverter.convertObject(ObjectUtil.checkNotNull(value, "value")));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addObject(K name, Iterable<?> values) {
/*  332 */     for (Object value : values) {
/*  333 */       addObject(name, value);
/*      */     }
/*  335 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T addObject(K name, Object... values) {
/*  340 */     for (Object value : values) {
/*  341 */       addObject(name, value);
/*      */     }
/*  343 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T addInt(K name, int value) {
/*  348 */     return add(name, this.valueConverter.convertInt(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addLong(K name, long value) {
/*  353 */     return add(name, this.valueConverter.convertLong(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addDouble(K name, double value) {
/*  358 */     return add(name, this.valueConverter.convertDouble(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addTimeMillis(K name, long value) {
/*  363 */     return add(name, this.valueConverter.convertTimeMillis(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addChar(K name, char value) {
/*  368 */     return add(name, this.valueConverter.convertChar(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addBoolean(K name, boolean value) {
/*  373 */     return add(name, this.valueConverter.convertBoolean(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addFloat(K name, float value) {
/*  378 */     return add(name, this.valueConverter.convertFloat(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addByte(K name, byte value) {
/*  383 */     return add(name, this.valueConverter.convertByte(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T addShort(K name, short value) {
/*  388 */     return add(name, this.valueConverter.convertShort(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T add(Headers<? extends K, ? extends V, ?> headers) {
/*  393 */     if (headers == this) {
/*  394 */       throw new IllegalArgumentException("can't add to itself.");
/*      */     }
/*  396 */     addImpl(headers);
/*  397 */     return thisT();
/*      */   }
/*      */   
/*      */   protected void addImpl(Headers<? extends K, ? extends V, ?> headers) {
/*  401 */     if (headers instanceof DefaultHeaders) {
/*      */       
/*  403 */       DefaultHeaders<? extends K, ? extends V, T> defaultHeaders = (DefaultHeaders)headers;
/*      */       
/*  405 */       HeaderEntry<? extends K, ? extends V> e = defaultHeaders.head.after;
/*  406 */       if (defaultHeaders.hashingStrategy == this.hashingStrategy && defaultHeaders.nameValidator == this.nameValidator) {
/*      */ 
/*      */         
/*  409 */         while (e != defaultHeaders.head) {
/*  410 */           add0(e.hash, index(e.hash), e.key, e.value);
/*  411 */           e = e.after;
/*      */         } 
/*      */       } else {
/*      */         
/*  415 */         while (e != defaultHeaders.head) {
/*  416 */           add(e.key, e.value);
/*  417 */           e = e.after;
/*      */         } 
/*      */       } 
/*      */     } else {
/*      */       
/*  422 */       for (Map.Entry<? extends K, ? extends V> header : headers) {
/*  423 */         add(header.getKey(), header.getValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public T set(K name, V value) {
/*  430 */     this.nameValidator.validateName(name);
/*  431 */     ObjectUtil.checkNotNull(value, "value");
/*  432 */     int h = this.hashingStrategy.hashCode(name);
/*  433 */     int i = index(h);
/*  434 */     remove0(h, i, name);
/*  435 */     add0(h, i, name, value);
/*  436 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T set(K name, Iterable<? extends V> values) {
/*  441 */     this.nameValidator.validateName(name);
/*  442 */     ObjectUtil.checkNotNull(values, "values");
/*      */     
/*  444 */     int h = this.hashingStrategy.hashCode(name);
/*  445 */     int i = index(h);
/*      */     
/*  447 */     remove0(h, i, name);
/*  448 */     for (V v : values) {
/*  449 */       if (v == null) {
/*      */         break;
/*      */       }
/*  452 */       add0(h, i, name, v);
/*      */     } 
/*      */     
/*  455 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T set(K name, V... values) {
/*  460 */     this.nameValidator.validateName(name);
/*  461 */     ObjectUtil.checkNotNull(values, "values");
/*      */     
/*  463 */     int h = this.hashingStrategy.hashCode(name);
/*  464 */     int i = index(h);
/*      */     
/*  466 */     remove0(h, i, name);
/*  467 */     for (V v : values) {
/*  468 */       if (v == null) {
/*      */         break;
/*      */       }
/*  471 */       add0(h, i, name, v);
/*      */     } 
/*      */     
/*  474 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T setObject(K name, Object value) {
/*  479 */     ObjectUtil.checkNotNull(value, "value");
/*  480 */     V convertedValue = (V)ObjectUtil.checkNotNull(this.valueConverter.convertObject(value), "convertedValue");
/*  481 */     return set(name, convertedValue);
/*      */   }
/*      */ 
/*      */   
/*      */   public T setObject(K name, Iterable<?> values) {
/*  486 */     this.nameValidator.validateName(name);
/*      */     
/*  488 */     int h = this.hashingStrategy.hashCode(name);
/*  489 */     int i = index(h);
/*      */     
/*  491 */     remove0(h, i, name);
/*  492 */     for (Object v : values) {
/*  493 */       if (v == null) {
/*      */         break;
/*      */       }
/*  496 */       add0(h, i, name, this.valueConverter.convertObject(v));
/*      */     } 
/*      */     
/*  499 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T setObject(K name, Object... values) {
/*  504 */     this.nameValidator.validateName(name);
/*      */     
/*  506 */     int h = this.hashingStrategy.hashCode(name);
/*  507 */     int i = index(h);
/*      */     
/*  509 */     remove0(h, i, name);
/*  510 */     for (Object v : values) {
/*  511 */       if (v == null) {
/*      */         break;
/*      */       }
/*  514 */       add0(h, i, name, this.valueConverter.convertObject(v));
/*      */     } 
/*      */     
/*  517 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T setInt(K name, int value) {
/*  522 */     return set(name, this.valueConverter.convertInt(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setLong(K name, long value) {
/*  527 */     return set(name, this.valueConverter.convertLong(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setDouble(K name, double value) {
/*  532 */     return set(name, this.valueConverter.convertDouble(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setTimeMillis(K name, long value) {
/*  537 */     return set(name, this.valueConverter.convertTimeMillis(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setFloat(K name, float value) {
/*  542 */     return set(name, this.valueConverter.convertFloat(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setChar(K name, char value) {
/*  547 */     return set(name, this.valueConverter.convertChar(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setBoolean(K name, boolean value) {
/*  552 */     return set(name, this.valueConverter.convertBoolean(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setByte(K name, byte value) {
/*  557 */     return set(name, this.valueConverter.convertByte(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T setShort(K name, short value) {
/*  562 */     return set(name, this.valueConverter.convertShort(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public T set(Headers<? extends K, ? extends V, ?> headers) {
/*  567 */     if (headers != this) {
/*  568 */       clear();
/*  569 */       addImpl(headers);
/*      */     } 
/*  571 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public T setAll(Headers<? extends K, ? extends V, ?> headers) {
/*  576 */     if (headers != this) {
/*  577 */       for (K key : headers.names()) {
/*  578 */         remove(key);
/*      */       }
/*  580 */       addImpl(headers);
/*      */     } 
/*  582 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean remove(K name) {
/*  587 */     return (getAndRemove(name) != null);
/*      */   }
/*      */ 
/*      */   
/*      */   public T clear() {
/*  592 */     Arrays.fill((Object[])this.entries, (Object)null);
/*  593 */     this.head.before = this.head.after = this.head;
/*  594 */     this.size = 0;
/*  595 */     return thisT();
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator<Map.Entry<K, V>> iterator() {
/*  600 */     return new HeaderIterator();
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean getBoolean(K name) {
/*  605 */     V v = get(name);
/*      */     try {
/*  607 */       return (v != null) ? Boolean.valueOf(this.valueConverter.convertToBoolean(v)) : null;
/*  608 */     } catch (RuntimeException ignore) {
/*  609 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBoolean(K name, boolean defaultValue) {
/*  615 */     Boolean v = getBoolean(name);
/*  616 */     return (v != null) ? v.booleanValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Byte getByte(K name) {
/*  621 */     V v = get(name);
/*      */     try {
/*  623 */       return (v != null) ? Byte.valueOf(this.valueConverter.convertToByte(v)) : null;
/*  624 */     } catch (RuntimeException ignore) {
/*  625 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(K name, byte defaultValue) {
/*  631 */     Byte v = getByte(name);
/*  632 */     return (v != null) ? v.byteValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Character getChar(K name) {
/*  637 */     V v = get(name);
/*      */     try {
/*  639 */       return (v != null) ? Character.valueOf(this.valueConverter.convertToChar(v)) : null;
/*  640 */     } catch (RuntimeException ignore) {
/*  641 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public char getChar(K name, char defaultValue) {
/*  647 */     Character v = getChar(name);
/*  648 */     return (v != null) ? v.charValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Short getShort(K name) {
/*  653 */     V v = get(name);
/*      */     try {
/*  655 */       return (v != null) ? Short.valueOf(this.valueConverter.convertToShort(v)) : null;
/*  656 */     } catch (RuntimeException ignore) {
/*  657 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(K name, short defaultValue) {
/*  663 */     Short v = getShort(name);
/*  664 */     return (v != null) ? v.shortValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Integer getInt(K name) {
/*  669 */     V v = get(name);
/*      */     try {
/*  671 */       return (v != null) ? Integer.valueOf(this.valueConverter.convertToInt(v)) : null;
/*  672 */     } catch (RuntimeException ignore) {
/*  673 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(K name, int defaultValue) {
/*  679 */     Integer v = getInt(name);
/*  680 */     return (v != null) ? v.intValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Long getLong(K name) {
/*  685 */     V v = get(name);
/*      */     try {
/*  687 */       return (v != null) ? Long.valueOf(this.valueConverter.convertToLong(v)) : null;
/*  688 */     } catch (RuntimeException ignore) {
/*  689 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(K name, long defaultValue) {
/*  695 */     Long v = getLong(name);
/*  696 */     return (v != null) ? v.longValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Float getFloat(K name) {
/*  701 */     V v = get(name);
/*      */     try {
/*  703 */       return (v != null) ? Float.valueOf(this.valueConverter.convertToFloat(v)) : null;
/*  704 */     } catch (RuntimeException ignore) {
/*  705 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(K name, float defaultValue) {
/*  711 */     Float v = getFloat(name);
/*  712 */     return (v != null) ? v.floatValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Double getDouble(K name) {
/*  717 */     V v = get(name);
/*      */     try {
/*  719 */       return (v != null) ? Double.valueOf(this.valueConverter.convertToDouble(v)) : null;
/*  720 */     } catch (RuntimeException ignore) {
/*  721 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(K name, double defaultValue) {
/*  727 */     Double v = getDouble(name);
/*  728 */     return (v != null) ? v.doubleValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Long getTimeMillis(K name) {
/*  733 */     V v = get(name);
/*      */     try {
/*  735 */       return (v != null) ? Long.valueOf(this.valueConverter.convertToTimeMillis(v)) : null;
/*  736 */     } catch (RuntimeException ignore) {
/*  737 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public long getTimeMillis(K name, long defaultValue) {
/*  743 */     Long v = getTimeMillis(name);
/*  744 */     return (v != null) ? v.longValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean getBooleanAndRemove(K name) {
/*  749 */     V v = getAndRemove(name);
/*      */     try {
/*  751 */       return (v != null) ? Boolean.valueOf(this.valueConverter.convertToBoolean(v)) : null;
/*  752 */     } catch (RuntimeException ignore) {
/*  753 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getBooleanAndRemove(K name, boolean defaultValue) {
/*  759 */     Boolean v = getBooleanAndRemove(name);
/*  760 */     return (v != null) ? v.booleanValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Byte getByteAndRemove(K name) {
/*  765 */     V v = getAndRemove(name);
/*      */     try {
/*  767 */       return (v != null) ? Byte.valueOf(this.valueConverter.convertToByte(v)) : null;
/*  768 */     } catch (RuntimeException ignore) {
/*  769 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByteAndRemove(K name, byte defaultValue) {
/*  775 */     Byte v = getByteAndRemove(name);
/*  776 */     return (v != null) ? v.byteValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Character getCharAndRemove(K name) {
/*  781 */     V v = getAndRemove(name);
/*      */     try {
/*  783 */       return (v != null) ? Character.valueOf(this.valueConverter.convertToChar(v)) : null;
/*  784 */     } catch (RuntimeException ignore) {
/*  785 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public char getCharAndRemove(K name, char defaultValue) {
/*  791 */     Character v = getCharAndRemove(name);
/*  792 */     return (v != null) ? v.charValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Short getShortAndRemove(K name) {
/*  797 */     V v = getAndRemove(name);
/*      */     try {
/*  799 */       return (v != null) ? Short.valueOf(this.valueConverter.convertToShort(v)) : null;
/*  800 */     } catch (RuntimeException ignore) {
/*  801 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShortAndRemove(K name, short defaultValue) {
/*  807 */     Short v = getShortAndRemove(name);
/*  808 */     return (v != null) ? v.shortValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Integer getIntAndRemove(K name) {
/*  813 */     V v = getAndRemove(name);
/*      */     try {
/*  815 */       return (v != null) ? Integer.valueOf(this.valueConverter.convertToInt(v)) : null;
/*  816 */     } catch (RuntimeException ignore) {
/*  817 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntAndRemove(K name, int defaultValue) {
/*  823 */     Integer v = getIntAndRemove(name);
/*  824 */     return (v != null) ? v.intValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Long getLongAndRemove(K name) {
/*  829 */     V v = getAndRemove(name);
/*      */     try {
/*  831 */       return (v != null) ? Long.valueOf(this.valueConverter.convertToLong(v)) : null;
/*  832 */     } catch (RuntimeException ignore) {
/*  833 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLongAndRemove(K name, long defaultValue) {
/*  839 */     Long v = getLongAndRemove(name);
/*  840 */     return (v != null) ? v.longValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Float getFloatAndRemove(K name) {
/*  845 */     V v = getAndRemove(name);
/*      */     try {
/*  847 */       return (v != null) ? Float.valueOf(this.valueConverter.convertToFloat(v)) : null;
/*  848 */     } catch (RuntimeException ignore) {
/*  849 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloatAndRemove(K name, float defaultValue) {
/*  855 */     Float v = getFloatAndRemove(name);
/*  856 */     return (v != null) ? v.floatValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Double getDoubleAndRemove(K name) {
/*  861 */     V v = getAndRemove(name);
/*      */     try {
/*  863 */       return (v != null) ? Double.valueOf(this.valueConverter.convertToDouble(v)) : null;
/*  864 */     } catch (RuntimeException ignore) {
/*  865 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDoubleAndRemove(K name, double defaultValue) {
/*  871 */     Double v = getDoubleAndRemove(name);
/*  872 */     return (v != null) ? v.doubleValue() : defaultValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public Long getTimeMillisAndRemove(K name) {
/*  877 */     V v = getAndRemove(name);
/*      */     try {
/*  879 */       return (v != null) ? Long.valueOf(this.valueConverter.convertToTimeMillis(v)) : null;
/*  880 */     } catch (RuntimeException ignore) {
/*  881 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public long getTimeMillisAndRemove(K name, long defaultValue) {
/*  887 */     Long v = getTimeMillisAndRemove(name);
/*  888 */     return (v != null) ? v.longValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/*  894 */     if (!(o instanceof Headers)) {
/*  895 */       return false;
/*      */     }
/*      */     
/*  898 */     return equals((Headers<K, V, ?>)o, HashingStrategy.JAVA_HASHER);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  904 */     return hashCode(HashingStrategy.JAVA_HASHER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean equals(Headers<K, V, ?> h2, HashingStrategy<V> valueHashingStrategy) {
/*  915 */     if (h2.size() != size()) {
/*  916 */       return false;
/*      */     }
/*      */     
/*  919 */     if (this == h2) {
/*  920 */       return true;
/*      */     }
/*      */     
/*  923 */     for (K name : names()) {
/*  924 */       List<V> otherValues = h2.getAll(name);
/*  925 */       List<V> values = getAll(name);
/*  926 */       if (otherValues.size() != values.size()) {
/*  927 */         return false;
/*      */       }
/*  929 */       for (int i = 0; i < otherValues.size(); i++) {
/*  930 */         if (!valueHashingStrategy.equals(otherValues.get(i), values.get(i))) {
/*  931 */           return false;
/*      */         }
/*      */       } 
/*      */     } 
/*  935 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int hashCode(HashingStrategy<V> valueHashingStrategy) {
/*  944 */     int result = -1028477387;
/*  945 */     for (K name : names()) {
/*  946 */       result = 31 * result + this.hashingStrategy.hashCode(name);
/*  947 */       List<V> values = getAll(name);
/*  948 */       for (int i = 0; i < values.size(); i++) {
/*  949 */         result = 31 * result + valueHashingStrategy.hashCode(values.get(i));
/*      */       }
/*      */     } 
/*  952 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/*  957 */     return HeadersUtils.toString(getClass(), iterator(), size());
/*      */   }
/*      */   
/*      */   protected HeaderEntry<K, V> newHeaderEntry(int h, K name, V value, HeaderEntry<K, V> next) {
/*  961 */     return new HeaderEntry<K, V>(h, name, value, next, this.head);
/*      */   }
/*      */   
/*      */   protected ValueConverter<V> valueConverter() {
/*  965 */     return this.valueConverter;
/*      */   }
/*      */   
/*      */   private int index(int hash) {
/*  969 */     return hash & this.hashMask;
/*      */   }
/*      */ 
/*      */   
/*      */   private void add0(int h, int i, K name, V value) {
/*  974 */     this.entries[i] = newHeaderEntry(h, name, value, this.entries[i]);
/*  975 */     this.size++;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private V remove0(int h, int i, K name) {
/*  982 */     HeaderEntry<K, V> e = this.entries[i];
/*  983 */     if (e == null) {
/*  984 */       return null;
/*      */     }
/*      */     
/*  987 */     V value = null;
/*  988 */     HeaderEntry<K, V> next = e.next;
/*  989 */     while (next != null) {
/*  990 */       if (next.hash == h && this.hashingStrategy.equals(name, next.key)) {
/*  991 */         value = next.value;
/*  992 */         e.next = next.next;
/*  993 */         next.remove();
/*  994 */         this.size--;
/*      */       } else {
/*  996 */         e = next;
/*      */       } 
/*      */       
/*  999 */       next = e.next;
/*      */     } 
/*      */     
/* 1002 */     e = this.entries[i];
/* 1003 */     if (e.hash == h && this.hashingStrategy.equals(name, e.key)) {
/* 1004 */       if (value == null) {
/* 1005 */         value = e.value;
/*      */       }
/* 1007 */       this.entries[i] = e.next;
/* 1008 */       e.remove();
/* 1009 */       this.size--;
/*      */     } 
/*      */     
/* 1012 */     return value;
/*      */   }
/*      */   
/*      */   private HeaderEntry<K, V> remove0(HeaderEntry<K, V> entry, HeaderEntry<K, V> previous) {
/* 1016 */     int i = index(entry.hash);
/* 1017 */     HeaderEntry<K, V> e = this.entries[i];
/* 1018 */     if (e == entry) {
/* 1019 */       this.entries[i] = entry.next;
/* 1020 */       previous = this.entries[i];
/*      */     } else {
/* 1022 */       previous.next = entry.next;
/*      */     } 
/* 1024 */     entry.remove();
/* 1025 */     this.size--;
/* 1026 */     return previous;
/*      */   }
/*      */ 
/*      */   
/*      */   private T thisT() {
/* 1031 */     return (T)this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DefaultHeaders<K, V, T> copy() {
/* 1038 */     DefaultHeaders<K, V, T> copy = new DefaultHeaders(this.hashingStrategy, this.valueConverter, this.nameValidator, this.entries.length);
/*      */     
/* 1040 */     copy.addImpl(this);
/* 1041 */     return copy;
/*      */   }
/*      */   
/*      */   private final class HeaderIterator implements Iterator<Map.Entry<K, V>> {
/* 1045 */     private DefaultHeaders.HeaderEntry<K, V> current = DefaultHeaders.this.head;
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 1049 */       return (this.current.after != DefaultHeaders.this.head);
/*      */     }
/*      */ 
/*      */     
/*      */     public Map.Entry<K, V> next() {
/* 1054 */       this.current = this.current.after;
/*      */       
/* 1056 */       if (this.current == DefaultHeaders.this.head) {
/* 1057 */         throw new NoSuchElementException();
/*      */       }
/*      */       
/* 1060 */       return this.current;
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/* 1065 */       throw new UnsupportedOperationException("read only");
/*      */     }
/*      */     
/*      */     private HeaderIterator() {}
/*      */   }
/*      */   
/*      */   private final class ValueIterator
/*      */     implements Iterator<V> {
/*      */     private final K name;
/*      */     private final int hash;
/*      */     
/*      */     ValueIterator(K name) {
/* 1077 */       this.name = (K)ObjectUtil.checkNotNull(name, "name");
/* 1078 */       this.hash = DefaultHeaders.this.hashingStrategy.hashCode(name);
/* 1079 */       calculateNext(DefaultHeaders.this.entries[DefaultHeaders.this.index(this.hash)]);
/*      */     }
/*      */     private DefaultHeaders.HeaderEntry<K, V> removalPrevious; private DefaultHeaders.HeaderEntry<K, V> previous; private DefaultHeaders.HeaderEntry<K, V> next;
/*      */     
/*      */     public boolean hasNext() {
/* 1084 */       return (this.next != null);
/*      */     }
/*      */ 
/*      */     
/*      */     public V next() {
/* 1089 */       if (!hasNext()) {
/* 1090 */         throw new NoSuchElementException();
/*      */       }
/* 1092 */       if (this.previous != null) {
/* 1093 */         this.removalPrevious = this.previous;
/*      */       }
/* 1095 */       this.previous = this.next;
/* 1096 */       calculateNext(this.next.next);
/* 1097 */       return this.previous.value;
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/* 1102 */       if (this.previous == null) {
/* 1103 */         throw new IllegalStateException();
/*      */       }
/* 1105 */       this.removalPrevious = DefaultHeaders.this.remove0(this.previous, this.removalPrevious);
/* 1106 */       this.previous = null;
/*      */     }
/*      */     
/*      */     private void calculateNext(DefaultHeaders.HeaderEntry<K, V> entry) {
/* 1110 */       while (entry != null) {
/* 1111 */         if (entry.hash == this.hash && DefaultHeaders.this.hashingStrategy.equals(this.name, entry.key)) {
/* 1112 */           this.next = entry;
/*      */           return;
/*      */         } 
/* 1115 */         entry = entry.next;
/*      */       } 
/* 1117 */       this.next = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected static class HeaderEntry<K, V>
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     protected final int hash;
/*      */     
/*      */     protected final K key;
/*      */     
/*      */     protected V value;
/*      */     protected HeaderEntry<K, V> next;
/*      */     protected HeaderEntry<K, V> before;
/*      */     protected HeaderEntry<K, V> after;
/*      */     
/*      */     protected HeaderEntry(int hash, K key) {
/* 1135 */       this.hash = hash;
/* 1136 */       this.key = key;
/*      */     }
/*      */     
/*      */     HeaderEntry(int hash, K key, V value, HeaderEntry<K, V> next, HeaderEntry<K, V> head) {
/* 1140 */       this.hash = hash;
/* 1141 */       this.key = key;
/* 1142 */       this.value = value;
/* 1143 */       this.next = next;
/*      */       
/* 1145 */       this.after = head;
/* 1146 */       this.before = head.before;
/* 1147 */       pointNeighborsToThis();
/*      */     }
/*      */     
/*      */     HeaderEntry() {
/* 1151 */       this.hash = -1;
/* 1152 */       this.key = null;
/* 1153 */       this.before = this.after = this;
/*      */     }
/*      */     
/*      */     protected final void pointNeighborsToThis() {
/* 1157 */       this.before.after = this;
/* 1158 */       this.after.before = this;
/*      */     }
/*      */     
/*      */     public final HeaderEntry<K, V> before() {
/* 1162 */       return this.before;
/*      */     }
/*      */     
/*      */     public final HeaderEntry<K, V> after() {
/* 1166 */       return this.after;
/*      */     }
/*      */     
/*      */     protected void remove() {
/* 1170 */       this.before.after = this.after;
/* 1171 */       this.after.before = this.before;
/*      */     }
/*      */ 
/*      */     
/*      */     public final K getKey() {
/* 1176 */       return this.key;
/*      */     }
/*      */ 
/*      */     
/*      */     public final V getValue() {
/* 1181 */       return this.value;
/*      */     }
/*      */ 
/*      */     
/*      */     public final V setValue(V value) {
/* 1186 */       ObjectUtil.checkNotNull(value, "value");
/* 1187 */       V oldValue = this.value;
/* 1188 */       this.value = value;
/* 1189 */       return oldValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public final String toString() {
/* 1194 */       return this.key.toString() + '=' + this.value.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object o) {
/* 1199 */       if (!(o instanceof Map.Entry)) {
/* 1200 */         return false;
/*      */       }
/* 1202 */       Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
/* 1203 */       return (((getKey() == null) ? (other.getKey() == null) : getKey().equals(other.getKey())) && (
/* 1204 */         (getValue() == null) ? (other.getValue() == null) : getValue().equals(other.getValue())));
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1209 */       return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DefaultHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */