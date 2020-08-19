/*     */ package net.querz.nbt;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NonNullEntrySet<K, V>
/*     */   implements Set<Map.Entry<K, V>>
/*     */ {
/*     */   private Set<Map.Entry<K, V>> set;
/*     */   
/*     */   NonNullEntrySet(Set<Map.Entry<K, V>> set) {
/*  17 */     this.set = set;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  22 */     return this.set.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  27 */     return this.set.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/*  32 */     return this.set.contains(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<K, V>> iterator() {
/*  37 */     return new NonNullEntrySetIterator(this.set.iterator());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/*  42 */     return this.set.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/*  47 */     return this.set.toArray(a);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Map.Entry<K, V> kvEntry) {
/*  52 */     return this.set.add(kvEntry);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/*  57 */     return this.set.remove(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/*  62 */     return this.set.containsAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
/*  67 */     return this.set.addAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/*  72 */     return this.set.retainAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/*  77 */     return this.set.removeAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  82 */     this.set.clear();
/*     */   }
/*     */   
/*     */   class NonNullEntrySetIterator
/*     */     implements Iterator<Map.Entry<K, V>> {
/*     */     private Iterator<Map.Entry<K, V>> iterator;
/*     */     
/*     */     NonNullEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
/*  90 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/*  95 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<K, V> next() {
/* 100 */       return new NonNullEntrySet.NonNullEntry(this.iterator.next());
/*     */     }
/*     */   }
/*     */   
/*     */   class NonNullEntry
/*     */     implements Map.Entry<K, V> {
/*     */     private Map.Entry<K, V> entry;
/*     */     
/*     */     NonNullEntry(Map.Entry<K, V> entry) {
/* 109 */       this.entry = entry;
/*     */     }
/*     */ 
/*     */     
/*     */     public K getKey() {
/* 114 */       return this.entry.getKey();
/*     */     }
/*     */ 
/*     */     
/*     */     public V getValue() {
/* 119 */       return this.entry.getValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public V setValue(V value) {
/* 124 */       if (value == null) {
/* 125 */         throw new NullPointerException(getClass().getSimpleName() + " does not allow setting null");
/*     */       }
/* 127 */       return this.entry.setValue(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 132 */       return this.entry.equals(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 137 */       return this.entry.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\NonNullEntrySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */