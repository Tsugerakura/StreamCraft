/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
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
/*     */ public class EmptyHttpHeaders
/*     */   extends HttpHeaders
/*     */ {
/*  27 */   static final Iterator<Map.Entry<CharSequence, CharSequence>> EMPTY_CHARS_ITERATOR = Collections.<Map.Entry<CharSequence, CharSequence>>emptyList().iterator();
/*     */   
/*  29 */   public static final EmptyHttpHeaders INSTANCE = instance();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   static EmptyHttpHeaders instance() {
/*  40 */     return InstanceInitializer.EMPTY_HEADERS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String name) {
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(CharSequence name) {
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(CharSequence name, int defaultValue) {
/*  58 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(CharSequence name) {
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(CharSequence name, short defaultValue) {
/*  68 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getTimeMillis(CharSequence name) {
/*  73 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeMillis(CharSequence name, long defaultValue) {
/*  78 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getAll(String name) {
/*  83 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Map.Entry<String, String>> entries() {
/*  88 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String name) {
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  98 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 103 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> names() {
/* 108 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Object value) {
/* 113 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Iterable<?> values) {
/* 118 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addInt(CharSequence name, int value) {
/* 123 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addShort(CharSequence name, short value) {
/* 128 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Object value) {
/* 133 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Iterable<?> values) {
/* 138 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setInt(CharSequence name, int value) {
/* 143 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setShort(CharSequence name, short value) {
/* 148 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders remove(String name) {
/* 153 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders clear() {
/* 158 */     throw new UnsupportedOperationException("read only");
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<String, String>> iterator() {
/* 163 */     return entries().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
/* 168 */     return EMPTY_CHARS_ITERATOR;
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
/*     */   @Deprecated
/*     */   private static final class InstanceInitializer
/*     */   {
/*     */     @Deprecated
/* 183 */     private static final EmptyHttpHeaders EMPTY_HEADERS = new EmptyHttpHeaders();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\EmptyHttpHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */