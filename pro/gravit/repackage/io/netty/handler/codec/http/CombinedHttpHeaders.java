/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DefaultHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.Headers;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ValueConverter;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.HashingStrategy;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public class CombinedHttpHeaders
/*     */   extends DefaultHttpHeaders
/*     */ {
/*     */   public CombinedHttpHeaders(boolean validate) {
/*  41 */     super(new CombinedHttpHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, valueConverter(validate), nameValidator(validate)));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
/*  46 */     return super.containsValue(name, StringUtil.trimOws(value), ignoreCase);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class CombinedHttpHeadersImpl
/*     */     extends DefaultHeaders<CharSequence, CharSequence, CombinedHttpHeadersImpl>
/*     */   {
/*     */     private static final int VALUE_LENGTH_ESTIMATE = 10;
/*     */     
/*     */     private CsvValueEscaper<Object> objectEscaper;
/*     */     private CsvValueEscaper<CharSequence> charSequenceEscaper;
/*     */     
/*     */     private CsvValueEscaper<Object> objectEscaper() {
/*  59 */       if (this.objectEscaper == null) {
/*  60 */         this.objectEscaper = new CsvValueEscaper()
/*     */           {
/*     */             public CharSequence escape(Object value) {
/*  63 */               return StringUtil.escapeCsv((CharSequence)CombinedHttpHeaders.CombinedHttpHeadersImpl.this.valueConverter().convertObject(value), true);
/*     */             }
/*     */           };
/*     */       }
/*  67 */       return this.objectEscaper;
/*     */     }
/*     */     
/*     */     private CsvValueEscaper<CharSequence> charSequenceEscaper() {
/*  71 */       if (this.charSequenceEscaper == null) {
/*  72 */         this.charSequenceEscaper = new CsvValueEscaper<CharSequence>()
/*     */           {
/*     */             public CharSequence escape(CharSequence value) {
/*  75 */               return StringUtil.escapeCsv(value, true);
/*     */             }
/*     */           };
/*     */       }
/*  79 */       return this.charSequenceEscaper;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     CombinedHttpHeadersImpl(HashingStrategy<CharSequence> nameHashingStrategy, ValueConverter<CharSequence> valueConverter, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
/*  85 */       super(nameHashingStrategy, valueConverter, nameValidator);
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<CharSequence> valueIterator(CharSequence name) {
/*  90 */       Iterator<CharSequence> itr = super.valueIterator(name);
/*  91 */       if (!itr.hasNext() || cannotBeCombined(name)) {
/*  92 */         return itr;
/*     */       }
/*  94 */       Iterator<CharSequence> unescapedItr = StringUtil.unescapeCsvFields(itr.next()).iterator();
/*  95 */       if (itr.hasNext()) {
/*  96 */         throw new IllegalStateException("CombinedHttpHeaders should only have one value");
/*     */       }
/*  98 */       return unescapedItr;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<CharSequence> getAll(CharSequence name) {
/* 103 */       List<CharSequence> values = super.getAll(name);
/* 104 */       if (values.isEmpty() || cannotBeCombined(name)) {
/* 105 */         return values;
/*     */       }
/* 107 */       if (values.size() != 1) {
/* 108 */         throw new IllegalStateException("CombinedHttpHeaders should only have one value");
/*     */       }
/* 110 */       return StringUtil.unescapeCsvFields(values.get(0));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl add(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
/* 116 */       if (headers == this) {
/* 117 */         throw new IllegalArgumentException("can't add to itself.");
/*     */       }
/* 119 */       if (headers instanceof CombinedHttpHeadersImpl) {
/* 120 */         if (isEmpty()) {
/*     */           
/* 122 */           addImpl(headers);
/*     */         } else {
/*     */           
/* 125 */           for (Map.Entry<? extends CharSequence, ? extends CharSequence> header : headers) {
/* 126 */             addEscapedValue(header.getKey(), header.getValue());
/*     */           }
/*     */         } 
/*     */       } else {
/* 130 */         for (Map.Entry<? extends CharSequence, ? extends CharSequence> header : headers) {
/* 131 */           add(header.getKey(), header.getValue());
/*     */         }
/*     */       } 
/* 134 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl set(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
/* 139 */       if (headers == this) {
/* 140 */         return this;
/*     */       }
/* 142 */       clear();
/* 143 */       return add(headers);
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
/* 148 */       if (headers == this) {
/* 149 */         return this;
/*     */       }
/* 151 */       for (CharSequence key : headers.names()) {
/* 152 */         remove(key);
/*     */       }
/* 154 */       return add(headers);
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl add(CharSequence name, CharSequence value) {
/* 159 */       return addEscapedValue(name, charSequenceEscaper().escape(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl add(CharSequence name, CharSequence... values) {
/* 164 */       return addEscapedValue(name, commaSeparate(charSequenceEscaper(), values));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl add(CharSequence name, Iterable<? extends CharSequence> values) {
/* 169 */       return addEscapedValue(name, commaSeparate(charSequenceEscaper(), values));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl addObject(CharSequence name, Object value) {
/* 174 */       return addEscapedValue(name, commaSeparate(objectEscaper(), new Object[] { value }));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl addObject(CharSequence name, Iterable<?> values) {
/* 179 */       return addEscapedValue(name, commaSeparate(objectEscaper(), values));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl addObject(CharSequence name, Object... values) {
/* 184 */       return addEscapedValue(name, commaSeparate(objectEscaper(), values));
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl set(CharSequence name, CharSequence... values) {
/* 189 */       set(name, commaSeparate(charSequenceEscaper(), values));
/* 190 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl set(CharSequence name, Iterable<? extends CharSequence> values) {
/* 195 */       set(name, commaSeparate(charSequenceEscaper(), values));
/* 196 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl setObject(CharSequence name, Object value) {
/* 201 */       set(name, commaSeparate(objectEscaper(), new Object[] { value }));
/* 202 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl setObject(CharSequence name, Object... values) {
/* 207 */       set(name, commaSeparate(objectEscaper(), values));
/* 208 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public CombinedHttpHeadersImpl setObject(CharSequence name, Iterable<?> values) {
/* 213 */       set(name, commaSeparate(objectEscaper(), values));
/* 214 */       return this;
/*     */     }
/*     */     
/*     */     private static boolean cannotBeCombined(CharSequence name) {
/* 218 */       return HttpHeaderNames.SET_COOKIE.contentEqualsIgnoreCase(name);
/*     */     }
/*     */     
/*     */     private CombinedHttpHeadersImpl addEscapedValue(CharSequence name, CharSequence escapedValue) {
/* 222 */       CharSequence currentValue = (CharSequence)get(name);
/* 223 */       if (currentValue == null || cannotBeCombined(name)) {
/* 224 */         super.add(name, escapedValue);
/*     */       } else {
/* 226 */         set(name, commaSeparateEscapedValues(currentValue, escapedValue));
/*     */       } 
/* 228 */       return this;
/*     */     }
/*     */     
/*     */     private static <T> CharSequence commaSeparate(CsvValueEscaper<T> escaper, T... values) {
/* 232 */       StringBuilder sb = new StringBuilder(values.length * 10);
/* 233 */       if (values.length > 0) {
/* 234 */         int end = values.length - 1;
/* 235 */         for (int i = 0; i < end; i++) {
/* 236 */           sb.append(escaper.escape(values[i])).append(',');
/*     */         }
/* 238 */         sb.append(escaper.escape(values[end]));
/*     */       } 
/* 240 */       return sb;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static <T> CharSequence commaSeparate(CsvValueEscaper<T> escaper, Iterable<? extends T> values) {
/* 246 */       StringBuilder sb = (values instanceof Collection) ? new StringBuilder(((Collection)values).size() * 10) : new StringBuilder();
/* 247 */       Iterator<? extends T> iterator = values.iterator();
/* 248 */       if (iterator.hasNext()) {
/* 249 */         T next = iterator.next();
/* 250 */         while (iterator.hasNext()) {
/* 251 */           sb.append(escaper.escape(next)).append(',');
/* 252 */           next = iterator.next();
/*     */         } 
/* 254 */         sb.append(escaper.escape(next));
/*     */       } 
/* 256 */       return sb;
/*     */     }
/*     */     
/*     */     private static CharSequence commaSeparateEscapedValues(CharSequence currentValue, CharSequence value) {
/* 260 */       return (new StringBuilder(currentValue.length() + 1 + value.length()))
/* 261 */         .append(currentValue)
/* 262 */         .append(',')
/* 263 */         .append(value);
/*     */     }
/*     */     
/*     */     private static interface CsvValueEscaper<T> {
/*     */       CharSequence escape(T param2T);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\CombinedHttpHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */