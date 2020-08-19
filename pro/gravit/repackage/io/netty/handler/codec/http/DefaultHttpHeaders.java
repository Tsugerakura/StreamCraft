/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CharSequenceValueConverter;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DateFormatter;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DefaultHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DefaultHeadersImpl;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.Headers;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.HeadersUtils;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ValueConverter;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public class DefaultHttpHeaders
/*     */   extends HttpHeaders
/*     */ {
/*     */   private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = -16;
/*     */   
/*  47 */   private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  50 */         DefaultHttpHeaders.validateHeaderNameElement(value);
/*  51 */         return true;
/*     */       }
/*     */     };
/*  54 */   static final DefaultHeaders.NameValidator<CharSequence> HttpNameValidator = new DefaultHeaders.NameValidator<CharSequence>()
/*     */     {
/*     */       public void validateName(CharSequence name) {
/*  57 */         if (name == null || name.length() == 0) {
/*  58 */           throw new IllegalArgumentException("empty headers are not allowed [" + name + "]");
/*     */         }
/*  60 */         if (name instanceof AsciiString) {
/*     */           try {
/*  62 */             ((AsciiString)name).forEachByte(DefaultHttpHeaders.HEADER_NAME_VALIDATOR);
/*  63 */           } catch (Exception e) {
/*  64 */             PlatformDependent.throwException(e);
/*     */           } 
/*     */         } else {
/*     */           
/*  68 */           for (int index = 0; index < name.length(); index++) {
/*  69 */             DefaultHttpHeaders.validateHeaderNameElement(name.charAt(index));
/*     */           }
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   private final DefaultHeaders<CharSequence, CharSequence, ?> headers;
/*     */   
/*     */   public DefaultHttpHeaders() {
/*  78 */     this(true);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpHeaders(boolean validate) {
/*  94 */     this(validate, nameValidator(validate));
/*     */   }
/*     */   
/*     */   protected DefaultHttpHeaders(boolean validate, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
/*  98 */     this((DefaultHeaders<CharSequence, CharSequence, ?>)new DefaultHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, 
/*  99 */           valueConverter(validate), nameValidator));
/*     */   }
/*     */ 
/*     */   
/*     */   protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
/* 104 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(HttpHeaders headers) {
/* 109 */     if (headers instanceof DefaultHttpHeaders) {
/* 110 */       this.headers.add((Headers)((DefaultHttpHeaders)headers).headers);
/* 111 */       return this;
/*     */     } 
/* 113 */     return super.add(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders set(HttpHeaders headers) {
/* 119 */     if (headers instanceof DefaultHttpHeaders) {
/* 120 */       this.headers.set((Headers)((DefaultHttpHeaders)headers).headers);
/* 121 */       return this;
/*     */     } 
/* 123 */     return super.set(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Object value) {
/* 129 */     this.headers.addObject(name, value);
/* 130 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(CharSequence name, Object value) {
/* 135 */     this.headers.addObject(name, value);
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(String name, Iterable<?> values) {
/* 141 */     this.headers.addObject(name, values);
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders add(CharSequence name, Iterable<?> values) {
/* 147 */     this.headers.addObject(name, values);
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addInt(CharSequence name, int value) {
/* 153 */     this.headers.addInt(name, value);
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders addShort(CharSequence name, short value) {
/* 159 */     this.headers.addShort(name, value);
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders remove(String name) {
/* 165 */     this.headers.remove(name);
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders remove(CharSequence name) {
/* 171 */     this.headers.remove(name);
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Object value) {
/* 177 */     this.headers.setObject(name, value);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(CharSequence name, Object value) {
/* 183 */     this.headers.setObject(name, value);
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(String name, Iterable<?> values) {
/* 189 */     this.headers.setObject(name, values);
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders set(CharSequence name, Iterable<?> values) {
/* 195 */     this.headers.setObject(name, values);
/* 196 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setInt(CharSequence name, int value) {
/* 201 */     this.headers.setInt(name, value);
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders setShort(CharSequence name, short value) {
/* 207 */     this.headers.setShort(name, value);
/* 208 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders clear() {
/* 213 */     this.headers.clear();
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(String name) {
/* 219 */     return get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(CharSequence name) {
/* 224 */     return HeadersUtils.getAsString((Headers)this.headers, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInt(CharSequence name) {
/* 229 */     return this.headers.getInt(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(CharSequence name, int defaultValue) {
/* 234 */     return this.headers.getInt(name, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(CharSequence name) {
/* 239 */     return this.headers.getShort(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(CharSequence name, short defaultValue) {
/* 244 */     return this.headers.getShort(name, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getTimeMillis(CharSequence name) {
/* 249 */     return this.headers.getTimeMillis(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeMillis(CharSequence name, long defaultValue) {
/* 254 */     return this.headers.getTimeMillis(name, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getAll(String name) {
/* 259 */     return getAll(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getAll(CharSequence name) {
/* 264 */     return HeadersUtils.getAllAsString((Headers)this.headers, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Map.Entry<String, String>> entries() {
/* 269 */     if (isEmpty()) {
/* 270 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 273 */     List<Map.Entry<String, String>> entriesConverted = new ArrayList<Map.Entry<String, String>>(this.headers.size());
/* 274 */     for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)this) {
/* 275 */       entriesConverted.add(entry);
/*     */     }
/* 277 */     return entriesConverted;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Iterator<Map.Entry<String, String>> iterator() {
/* 283 */     return HeadersUtils.iteratorAsString((Iterable)this.headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
/* 288 */     return this.headers.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> valueStringIterator(CharSequence name) {
/* 293 */     final Iterator<CharSequence> itr = valueCharSequenceIterator(name);
/* 294 */     return new Iterator<String>()
/*     */       {
/*     */         public boolean hasNext() {
/* 297 */           return itr.hasNext();
/*     */         }
/*     */ 
/*     */         
/*     */         public String next() {
/* 302 */           return ((CharSequence)itr.next()).toString();
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 307 */           itr.remove();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
/* 314 */     return this.headers.valueIterator(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String name) {
/* 319 */     return contains(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(CharSequence name) {
/* 324 */     return this.headers.contains(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 329 */     return this.headers.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 334 */     return this.headers.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(String name, String value, boolean ignoreCase) {
/* 339 */     return contains(name, value, ignoreCase);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
/* 344 */     return this.headers.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> names() {
/* 349 */     return HeadersUtils.namesAsString((Headers)this.headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 354 */     return (o instanceof DefaultHttpHeaders && this.headers
/* 355 */       .equals((Headers)((DefaultHttpHeaders)o).headers, AsciiString.CASE_SENSITIVE_HASHER));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 360 */     return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders copy() {
/* 365 */     return new DefaultHttpHeaders(this.headers.copy());
/*     */   }
/*     */   
/*     */   private static void validateHeaderNameElement(byte value) {
/* 369 */     switch (value) {
/*     */       case 0:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*     */       case 13:
/*     */       case 32:
/*     */       case 44:
/*     */       case 58:
/*     */       case 59:
/*     */       case 61:
/* 381 */         throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 386 */     if (value < 0) {
/* 387 */       throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void validateHeaderNameElement(char value) {
/* 393 */     switch (value) {
/*     */       case '\000':
/*     */       case '\t':
/*     */       case '\n':
/*     */       case '\013':
/*     */       case '\f':
/*     */       case '\r':
/*     */       case ' ':
/*     */       case ',':
/*     */       case ':':
/*     */       case ';':
/*     */       case '=':
/* 405 */         throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 410 */     if (value > '') {
/* 411 */       throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static ValueConverter<CharSequence> valueConverter(boolean validate) {
/* 418 */     return validate ? (ValueConverter<CharSequence>)HeaderValueConverterAndValidator.INSTANCE : (ValueConverter<CharSequence>)HeaderValueConverter.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean validate) {
/* 423 */     return validate ? HttpNameValidator : DefaultHeaders.NameValidator.NOT_NULL;
/*     */   }
/*     */   
/*     */   private static class HeaderValueConverter extends CharSequenceValueConverter {
/* 427 */     static final HeaderValueConverter INSTANCE = new HeaderValueConverter();
/*     */     private HeaderValueConverter() {}
/*     */     
/*     */     public CharSequence convertObject(Object value) {
/* 431 */       if (value instanceof CharSequence) {
/* 432 */         return (CharSequence)value;
/*     */       }
/* 434 */       if (value instanceof Date) {
/* 435 */         return DateFormatter.format((Date)value);
/*     */       }
/* 437 */       if (value instanceof Calendar) {
/* 438 */         return DateFormatter.format(((Calendar)value).getTime());
/*     */       }
/* 440 */       return value.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class HeaderValueConverterAndValidator extends HeaderValueConverter {
/* 445 */     static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();
/*     */ 
/*     */     
/*     */     public CharSequence convertObject(Object value) {
/* 449 */       CharSequence seq = super.convertObject(value);
/* 450 */       int state = 0;
/*     */       
/* 452 */       for (int index = 0; index < seq.length(); index++) {
/* 453 */         state = validateValueChar(seq, state, seq.charAt(index));
/*     */       }
/*     */       
/* 456 */       if (state != 0) {
/* 457 */         throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
/*     */       }
/* 459 */       return seq;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static int validateValueChar(CharSequence seq, int state, char character) {
/* 469 */       if ((character & 0xFFFFFFF0) == 0)
/*     */       {
/* 471 */         switch (character) {
/*     */           case '\000':
/* 473 */             throw new IllegalArgumentException("a header value contains a prohibited character '\000': " + seq);
/*     */           case '\013':
/* 475 */             throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
/*     */           case '\f':
/* 477 */             throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
/*     */         } 
/*     */ 
/*     */       
/*     */       }
/* 482 */       switch (state) {
/*     */         case 0:
/* 484 */           switch (character) {
/*     */             case '\r':
/* 486 */               return 1;
/*     */             case '\n':
/* 488 */               return 2;
/*     */           } 
/*     */           break;
/*     */         case 1:
/* 492 */           switch (character) {
/*     */             case '\n':
/* 494 */               return 2;
/*     */           } 
/* 496 */           throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
/*     */         
/*     */         case 2:
/* 499 */           switch (character) {
/*     */             case '\t':
/*     */             case ' ':
/* 502 */               return 0;
/*     */           } 
/* 504 */           throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
/*     */       } 
/*     */       
/* 507 */       return state;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */