/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CharSequenceValueConverter;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ReadOnlyHttpHeaders
/*     */   extends HttpHeaders
/*     */ {
/*     */   private final CharSequence[] nameValuePairs;
/*     */   
/*     */   public ReadOnlyHttpHeaders(boolean validateHeaders, CharSequence... nameValuePairs) {
/*  57 */     if ((nameValuePairs.length & 0x1) != 0) {
/*  58 */       throw newInvalidArraySizeException();
/*     */     }
/*  60 */     if (validateHeaders) {
/*  61 */       validateHeaders(nameValuePairs);
/*     */     }
/*  63 */     this.nameValuePairs = nameValuePairs;
/*     */   }
/*     */   
/*     */   private static IllegalArgumentException newInvalidArraySizeException() {
/*  67 */     return new IllegalArgumentException("nameValuePairs must be arrays of [name, value] pairs");
/*     */   }
/*     */   
/*     */   private static void validateHeaders(CharSequence... keyValuePairs) {
/*  71 */     for (int i = 0; i < keyValuePairs.length; i += 2) {
/*  72 */       DefaultHttpHeaders.HttpNameValidator.validateName(keyValuePairs[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   private CharSequence get0(CharSequence name) {
/*  77 */     int nameHash = AsciiString.hashCode(name);
/*  78 */     for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/*  79 */       CharSequence roName = this.nameValuePairs[i];
/*  80 */       if (AsciiString.hashCode(roName) == nameHash && AsciiString.contentEqualsIgnoreCase(roName, name)) {
/*  81 */         return this.nameValuePairs[i + 1];
/*     */       }
/*     */     } 
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(String name) {
/*  89 */     CharSequence value = get0(name);
/*  90 */     return (value == null) ? null : value.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(CharSequence name) {
/*  95 */     CharSequence value = get0(name);
/*  96 */     return (value == null) ? null : Integer.valueOf(CharSequenceValueConverter.INSTANCE.convertToInt(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(CharSequence name, int defaultValue) {
/* 101 */     CharSequence value = get0(name);
/* 102 */     return (value == null) ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToInt(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(CharSequence name) {
/* 107 */     CharSequence value = get0(name);
/* 108 */     return (value == null) ? null : Short.valueOf(CharSequenceValueConverter.INSTANCE.convertToShort(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(CharSequence name, short defaultValue) {
/* 113 */     CharSequence value = get0(name);
/* 114 */     return (value == null) ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToShort(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getTimeMillis(CharSequence name) {
/* 119 */     CharSequence value = get0(name);
/* 120 */     return (value == null) ? null : Long.valueOf(CharSequenceValueConverter.INSTANCE.convertToTimeMillis(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeMillis(CharSequence name, long defaultValue) {
/* 125 */     CharSequence value = get0(name);
/* 126 */     return (value == null) ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToTimeMillis(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getAll(String name) {
/* 131 */     if (isEmpty()) {
/* 132 */       return Collections.emptyList();
/*     */     }
/* 134 */     int nameHash = AsciiString.hashCode(name);
/* 135 */     List<String> values = new ArrayList<String>(4);
/* 136 */     for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/* 137 */       CharSequence roName = this.nameValuePairs[i];
/* 138 */       if (AsciiString.hashCode(roName) == nameHash && AsciiString.contentEqualsIgnoreCase(roName, name)) {
/* 139 */         values.add(this.nameValuePairs[i + 1].toString());
/*     */       }
/*     */     } 
/* 142 */     return values;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Map.Entry<String, String>> entries() {
/* 147 */     if (isEmpty()) {
/* 148 */       return Collections.emptyList();
/*     */     }
/* 150 */     List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(size());
/* 151 */     for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/* 152 */       entries.add(new AbstractMap.SimpleImmutableEntry<String, String>(this.nameValuePairs[i].toString(), this.nameValuePairs[i + 1]
/* 153 */             .toString()));
/*     */     }
/* 155 */     return entries;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String name) {
/* 160 */     return (get0(name) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String name, String value, boolean ignoreCase) {
/* 165 */     return containsValue(name, value, ignoreCase);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
/* 170 */     if (ignoreCase) {
/* 171 */       for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/* 172 */         if (AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i], name) && 
/* 173 */           AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i + 1], value)) {
/* 174 */           return true;
/*     */         }
/*     */       } 
/*     */     } else {
/* 178 */       for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/* 179 */         if (AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i], name) && 
/* 180 */           AsciiString.contentEquals(this.nameValuePairs[i + 1], value)) {
/* 181 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> valueStringIterator(CharSequence name) {
/* 190 */     return new ReadOnlyStringValueIterator(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
/* 195 */     return new ReadOnlyValueIterator(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<String, String>> iterator() {
/* 200 */     return new ReadOnlyStringIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
/* 205 */     return new ReadOnlyIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 210 */     return (this.nameValuePairs.length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 215 */     return this.nameValuePairs.length >>> 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> names() {
/* 220 */     if (isEmpty()) {
/* 221 */       return Collections.emptySet();
/*     */     }
/* 223 */     Set<String> names = new LinkedHashSet<String>(size());
/* 224 */     for (int i = 0; i < this.nameValuePairs.length; i += 2) {
/* 225 */       names.add(this.nameValuePairs[i].toString());
/*     */     }
/* 227 */     return names;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Object value) {
/* 232 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Iterable<?> values) {
/* 237 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addInt(CharSequence name, int value) {
/* 242 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addShort(CharSequence name, short value) {
/* 247 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Object value) {
/* 252 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Iterable<?> values) {
/* 257 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setInt(CharSequence name, int value) {
/* 262 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setShort(CharSequence name, short value) {
/* 267 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders remove(String name) {
/* 272 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders clear() {
/* 277 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */   
/*     */   private final class ReadOnlyIterator implements Map.Entry<CharSequence, CharSequence>, Iterator<Map.Entry<CharSequence, CharSequence>> {
/*     */     private CharSequence key;
/*     */     private CharSequence value;
/*     */     private int nextNameIndex;
/*     */     
/*     */     private ReadOnlyIterator() {}
/*     */     
/*     */     public boolean hasNext() {
/* 288 */       return (this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<CharSequence, CharSequence> next() {
/* 293 */       if (!hasNext()) {
/* 294 */         throw new NoSuchElementException();
/*     */       }
/* 296 */       this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex];
/* 297 */       this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
/* 298 */       this.nextNameIndex += 2;
/* 299 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 304 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence getKey() {
/* 309 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence getValue() {
/* 314 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence setValue(CharSequence value) {
/* 319 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 324 */       return this.key.toString() + '=' + this.value.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private final class ReadOnlyStringIterator implements Map.Entry<String, String>, Iterator<Map.Entry<String, String>> {
/*     */     private String key;
/*     */     private String value;
/*     */     private int nextNameIndex;
/*     */     
/*     */     private ReadOnlyStringIterator() {}
/*     */     
/*     */     public boolean hasNext() {
/* 336 */       return (this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, String> next() {
/* 341 */       if (!hasNext()) {
/* 342 */         throw new NoSuchElementException();
/*     */       }
/* 344 */       this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex].toString();
/* 345 */       this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
/* 346 */       this.nextNameIndex += 2;
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 352 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 357 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 362 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String setValue(String value) {
/* 367 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 372 */       return this.key + '=' + this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class ReadOnlyStringValueIterator implements Iterator<String> {
/*     */     private final CharSequence name;
/*     */     private final int nameHash;
/*     */     private int nextNameIndex;
/*     */     
/*     */     ReadOnlyStringValueIterator(CharSequence name) {
/* 382 */       this.name = name;
/* 383 */       this.nameHash = AsciiString.hashCode(name);
/* 384 */       this.nextNameIndex = findNextValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 389 */       return (this.nextNameIndex != -1);
/*     */     }
/*     */ 
/*     */     
/*     */     public String next() {
/* 394 */       if (!hasNext()) {
/* 395 */         throw new NoSuchElementException();
/*     */       }
/* 397 */       String value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
/* 398 */       this.nextNameIndex = findNextValue();
/* 399 */       return value;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 404 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */     
/*     */     private int findNextValue() {
/* 408 */       for (int i = this.nextNameIndex; i < ReadOnlyHttpHeaders.this.nameValuePairs.length; i += 2) {
/* 409 */         CharSequence roName = ReadOnlyHttpHeaders.this.nameValuePairs[i];
/* 410 */         if (this.nameHash == AsciiString.hashCode(roName) && AsciiString.contentEqualsIgnoreCase(this.name, roName)) {
/* 411 */           return i;
/*     */         }
/*     */       } 
/* 414 */       return -1;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class ReadOnlyValueIterator implements Iterator<CharSequence> {
/*     */     private final CharSequence name;
/*     */     private final int nameHash;
/*     */     private int nextNameIndex;
/*     */     
/*     */     ReadOnlyValueIterator(CharSequence name) {
/* 424 */       this.name = name;
/* 425 */       this.nameHash = AsciiString.hashCode(name);
/* 426 */       this.nextNameIndex = findNextValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 431 */       return (this.nextNameIndex != -1);
/*     */     }
/*     */ 
/*     */     
/*     */     public CharSequence next() {
/* 436 */       if (!hasNext()) {
/* 437 */         throw new NoSuchElementException();
/*     */       }
/* 439 */       CharSequence value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
/* 440 */       this.nextNameIndex = findNextValue();
/* 441 */       return value;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 446 */       throw new UnsupportedOperationException("read only");
/*     */     }
/*     */     
/*     */     private int findNextValue() {
/* 450 */       for (int i = this.nextNameIndex; i < ReadOnlyHttpHeaders.this.nameValuePairs.length; i += 2) {
/* 451 */         CharSequence roName = ReadOnlyHttpHeaders.this.nameValuePairs[i];
/* 452 */         if (this.nameHash == AsciiString.hashCode(roName) && AsciiString.contentEqualsIgnoreCase(this.name, roName)) {
/* 453 */           return i;
/*     */         }
/*     */       } 
/* 456 */       return -1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\ReadOnlyHttpHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */