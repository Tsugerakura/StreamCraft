/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public final class HeadersUtils
/*     */ {
/*     */   public static <K, V> List<String> getAllAsString(Headers<K, V, ?> headers, K name) {
/*  42 */     final List<V> allNames = headers.getAll(name);
/*  43 */     return new AbstractList<String>()
/*     */       {
/*     */         public String get(int index) {
/*  46 */           V value = allNames.get(index);
/*  47 */           return (value != null) ? value.toString() : null;
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/*  52 */           return allNames.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> String getAsString(Headers<K, V, ?> headers, K name) {
/*  64 */     V orig = headers.get(name);
/*  65 */     return (orig != null) ? orig.toString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Iterator<Map.Entry<String, String>> iteratorAsString(Iterable<Map.Entry<CharSequence, CharSequence>> headers) {
/*  73 */     return new StringEntryIterator(headers.iterator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> String toString(Class<?> headersClass, Iterator<Map.Entry<K, V>> headersIt, int size) {
/*  84 */     String simpleName = headersClass.getSimpleName();
/*  85 */     if (size == 0) {
/*  86 */       return simpleName + "[]";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  91 */     StringBuilder sb = (new StringBuilder(simpleName.length() + 2 + size * 20)).append(simpleName).append('[');
/*  92 */     while (headersIt.hasNext()) {
/*  93 */       Map.Entry<?, ?> header = headersIt.next();
/*  94 */       sb.append(header.getKey()).append(": ").append(header.getValue()).append(", ");
/*     */     } 
/*  96 */     sb.setLength(sb.length() - 2);
/*  97 */     return sb.append(']').toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<String> namesAsString(Headers<CharSequence, CharSequence, ?> headers) {
/* 107 */     return new CharSequenceDelegatingStringSet(headers.names());
/*     */   }
/*     */   
/*     */   private static final class StringEntryIterator implements Iterator<Map.Entry<String, String>> {
/*     */     private final Iterator<Map.Entry<CharSequence, CharSequence>> iter;
/*     */     
/*     */     StringEntryIterator(Iterator<Map.Entry<CharSequence, CharSequence>> iter) {
/* 114 */       this.iter = iter;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 119 */       return this.iter.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map.Entry<String, String> next() {
/* 124 */       return new HeadersUtils.StringEntry(this.iter.next());
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 129 */       this.iter.remove();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class StringEntry implements Map.Entry<String, String> {
/*     */     private final Map.Entry<CharSequence, CharSequence> entry;
/*     */     private String name;
/*     */     private String value;
/*     */     
/*     */     StringEntry(Map.Entry<CharSequence, CharSequence> entry) {
/* 139 */       this.entry = entry;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 144 */       if (this.name == null) {
/* 145 */         this.name = ((CharSequence)this.entry.getKey()).toString();
/*     */       }
/* 147 */       return this.name;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 152 */       if (this.value == null && this.entry.getValue() != null) {
/* 153 */         this.value = ((CharSequence)this.entry.getValue()).toString();
/*     */       }
/* 155 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String setValue(String value) {
/* 160 */       String old = getValue();
/* 161 */       this.entry.setValue(value);
/* 162 */       return old;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 167 */       return this.entry.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class StringIterator<T> implements Iterator<String> {
/*     */     private final Iterator<T> iter;
/*     */     
/*     */     StringIterator(Iterator<T> iter) {
/* 175 */       this.iter = iter;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 180 */       return this.iter.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public String next() {
/* 185 */       T next = this.iter.next();
/* 186 */       return (next != null) ? next.toString() : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 191 */       this.iter.remove();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class CharSequenceDelegatingStringSet extends DelegatingStringSet<CharSequence> {
/*     */     CharSequenceDelegatingStringSet(Set<CharSequence> allNames) {
/* 197 */       super(allNames);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean add(String e) {
/* 202 */       return this.allNames.add(e);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean addAll(Collection<? extends String> c) {
/* 207 */       return this.allNames.addAll((Collection)c);
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class DelegatingStringSet<T> extends AbstractCollection<String> implements Set<String> {
/*     */     protected final Set<T> allNames;
/*     */     
/*     */     DelegatingStringSet(Set<T> allNames) {
/* 215 */       this.allNames = (Set<T>)ObjectUtil.checkNotNull(allNames, "allNames");
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 220 */       return this.allNames.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 225 */       return this.allNames.isEmpty();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean contains(Object o) {
/* 230 */       return this.allNames.contains(o.toString());
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<String> iterator() {
/* 235 */       return new HeadersUtils.StringIterator(this.allNames.iterator());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean remove(Object o) {
/* 240 */       return this.allNames.remove(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() {
/* 245 */       this.allNames.clear();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\HeadersUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */