/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.JsonNull;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.SerializedName;
/*     */ import com.google.gson.internal.LazilyParsedNumber;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Calendar;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicIntegerArray;
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
/*     */ public final class TypeAdapters
/*     */ {
/*     */   private TypeAdapters() {
/*  65 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*  69 */   public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>()
/*     */     {
/*     */       public void write(JsonWriter out, Class value) throws IOException {
/*  72 */         throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value
/*  73 */             .getName() + ". Forgot to register a type adapter?");
/*     */       }
/*     */       
/*     */       public Class read(JsonReader in) throws IOException {
/*  77 */         throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
/*     */       }
/*  80 */     }).nullSafe();
/*     */   
/*  82 */   public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
/*     */   
/*  84 */   public static final TypeAdapter<BitSet> BIT_SET = (new TypeAdapter<BitSet>() {
/*     */       public BitSet read(JsonReader in) throws IOException {
/*  86 */         BitSet bitset = new BitSet();
/*  87 */         in.beginArray();
/*  88 */         int i = 0;
/*  89 */         JsonToken tokenType = in.peek();
/*  90 */         while (tokenType != JsonToken.END_ARRAY) {
/*     */           boolean set; String stringValue;
/*  92 */           switch (tokenType) {
/*     */             case NUMBER:
/*  94 */               set = (in.nextInt() != 0);
/*     */               break;
/*     */             case BOOLEAN:
/*  97 */               set = in.nextBoolean();
/*     */               break;
/*     */             case STRING:
/* 100 */               stringValue = in.nextString();
/*     */               try {
/* 102 */                 set = (Integer.parseInt(stringValue) != 0);
/* 103 */               } catch (NumberFormatException e) {
/* 104 */                 throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + stringValue);
/*     */               } 
/*     */               break;
/*     */             
/*     */             default:
/* 109 */               throw new JsonSyntaxException("Invalid bitset value type: " + tokenType);
/*     */           } 
/* 111 */           if (set) {
/* 112 */             bitset.set(i);
/*     */           }
/* 114 */           i++;
/* 115 */           tokenType = in.peek();
/*     */         } 
/* 117 */         in.endArray();
/* 118 */         return bitset;
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, BitSet src) throws IOException {
/* 122 */         out.beginArray();
/* 123 */         for (int i = 0, length = src.length(); i < length; i++) {
/* 124 */           int value = src.get(i) ? 1 : 0;
/* 125 */           out.value(value);
/*     */         } 
/* 127 */         out.endArray();
/*     */       }
/* 129 */     }).nullSafe();
/*     */   
/* 131 */   public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
/*     */   
/* 133 */   public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>()
/*     */     {
/*     */       public Boolean read(JsonReader in) throws IOException {
/* 136 */         JsonToken peek = in.peek();
/* 137 */         if (peek == JsonToken.NULL) {
/* 138 */           in.nextNull();
/* 139 */           return null;
/* 140 */         }  if (peek == JsonToken.STRING)
/*     */         {
/* 142 */           return Boolean.valueOf(Boolean.parseBoolean(in.nextString()));
/*     */         }
/* 144 */         return Boolean.valueOf(in.nextBoolean());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Boolean value) throws IOException {
/* 148 */         out.value(value);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 156 */   public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
/*     */       public Boolean read(JsonReader in) throws IOException {
/* 158 */         if (in.peek() == JsonToken.NULL) {
/* 159 */           in.nextNull();
/* 160 */           return null;
/*     */         } 
/* 162 */         return Boolean.valueOf(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Boolean value) throws IOException {
/* 166 */         out.value((value == null) ? "null" : value.toString());
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 171 */   public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(boolean.class, (Class)Boolean.class, (TypeAdapter)BOOLEAN);
/*     */   
/* 173 */   public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 176 */         if (in.peek() == JsonToken.NULL) {
/* 177 */           in.nextNull();
/* 178 */           return null;
/*     */         } 
/*     */         try {
/* 181 */           int intValue = in.nextInt();
/* 182 */           return Byte.valueOf((byte)intValue);
/* 183 */         } catch (NumberFormatException e) {
/* 184 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 189 */         out.value(value);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 194 */   public static final TypeAdapterFactory BYTE_FACTORY = newFactory(byte.class, (Class)Byte.class, (TypeAdapter)BYTE);
/*     */   
/* 196 */   public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 199 */         if (in.peek() == JsonToken.NULL) {
/* 200 */           in.nextNull();
/* 201 */           return null;
/*     */         } 
/*     */         try {
/* 204 */           return Short.valueOf((short)in.nextInt());
/* 205 */         } catch (NumberFormatException e) {
/* 206 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 211 */         out.value(value);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 216 */   public static final TypeAdapterFactory SHORT_FACTORY = newFactory(short.class, (Class)Short.class, (TypeAdapter)SHORT);
/*     */   
/* 218 */   public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 221 */         if (in.peek() == JsonToken.NULL) {
/* 222 */           in.nextNull();
/* 223 */           return null;
/*     */         } 
/*     */         try {
/* 226 */           return Integer.valueOf(in.nextInt());
/* 227 */         } catch (NumberFormatException e) {
/* 228 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 233 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 237 */   public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(int.class, (Class)Integer.class, (TypeAdapter)INTEGER);
/*     */   
/* 239 */   public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
/*     */       public AtomicInteger read(JsonReader in) throws IOException {
/*     */         try {
/* 242 */           return new AtomicInteger(in.nextInt());
/* 243 */         } catch (NumberFormatException e) {
/* 244 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       public void write(JsonWriter out, AtomicInteger value) throws IOException {
/* 248 */         out.value(value.get());
/*     */       }
/* 250 */     }).nullSafe();
/*     */   
/* 252 */   public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
/*     */   
/* 254 */   public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
/*     */       public AtomicBoolean read(JsonReader in) throws IOException {
/* 256 */         return new AtomicBoolean(in.nextBoolean());
/*     */       }
/*     */       public void write(JsonWriter out, AtomicBoolean value) throws IOException {
/* 259 */         out.value(value.get());
/*     */       }
/* 261 */     }).nullSafe();
/*     */   
/* 263 */   public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
/*     */   
/* 265 */   public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
/*     */       public AtomicIntegerArray read(JsonReader in) throws IOException {
/* 267 */         List<Integer> list = new ArrayList<Integer>();
/* 268 */         in.beginArray();
/* 269 */         while (in.hasNext()) {
/*     */           try {
/* 271 */             int integer = in.nextInt();
/* 272 */             list.add(Integer.valueOf(integer));
/* 273 */           } catch (NumberFormatException e) {
/* 274 */             throw new JsonSyntaxException(e);
/*     */           } 
/*     */         } 
/* 277 */         in.endArray();
/* 278 */         int length = list.size();
/* 279 */         AtomicIntegerArray array = new AtomicIntegerArray(length);
/* 280 */         for (int i = 0; i < length; i++) {
/* 281 */           array.set(i, ((Integer)list.get(i)).intValue());
/*     */         }
/* 283 */         return array;
/*     */       }
/*     */       public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
/* 286 */         out.beginArray();
/* 287 */         for (int i = 0, length = value.length(); i < length; i++) {
/* 288 */           out.value(value.get(i));
/*     */         }
/* 290 */         out.endArray();
/*     */       }
/* 292 */     }).nullSafe();
/*     */   
/* 294 */   public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
/*     */   
/* 296 */   public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 299 */         if (in.peek() == JsonToken.NULL) {
/* 300 */           in.nextNull();
/* 301 */           return null;
/*     */         } 
/*     */         try {
/* 304 */           return Long.valueOf(in.nextLong());
/* 305 */         } catch (NumberFormatException e) {
/* 306 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 311 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 315 */   public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 318 */         if (in.peek() == JsonToken.NULL) {
/* 319 */           in.nextNull();
/* 320 */           return null;
/*     */         } 
/* 322 */         return Float.valueOf((float)in.nextDouble());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 326 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 330 */   public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 333 */         if (in.peek() == JsonToken.NULL) {
/* 334 */           in.nextNull();
/* 335 */           return null;
/*     */         } 
/* 337 */         return Double.valueOf(in.nextDouble());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 341 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 345 */   public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>()
/*     */     {
/*     */       public Number read(JsonReader in) throws IOException {
/* 348 */         JsonToken jsonToken = in.peek();
/* 349 */         switch (jsonToken) {
/*     */           case NULL:
/* 351 */             in.nextNull();
/* 352 */             return null;
/*     */           case NUMBER:
/*     */           case STRING:
/* 355 */             return (Number)new LazilyParsedNumber(in.nextString());
/*     */         } 
/* 357 */         throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 362 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 366 */   public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);
/*     */   
/* 368 */   public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>()
/*     */     {
/*     */       public Character read(JsonReader in) throws IOException {
/* 371 */         if (in.peek() == JsonToken.NULL) {
/* 372 */           in.nextNull();
/* 373 */           return null;
/*     */         } 
/* 375 */         String str = in.nextString();
/* 376 */         if (str.length() != 1) {
/* 377 */           throw new JsonSyntaxException("Expecting character, got: " + str);
/*     */         }
/* 379 */         return Character.valueOf(str.charAt(0));
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Character value) throws IOException {
/* 383 */         out.value((value == null) ? null : String.valueOf(value));
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 388 */   public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(char.class, (Class)Character.class, (TypeAdapter)CHARACTER);
/*     */   
/* 390 */   public static final TypeAdapter<String> STRING = new TypeAdapter<String>()
/*     */     {
/*     */       public String read(JsonReader in) throws IOException {
/* 393 */         JsonToken peek = in.peek();
/* 394 */         if (peek == JsonToken.NULL) {
/* 395 */           in.nextNull();
/* 396 */           return null;
/*     */         } 
/*     */         
/* 399 */         if (peek == JsonToken.BOOLEAN) {
/* 400 */           return Boolean.toString(in.nextBoolean());
/*     */         }
/* 402 */         return in.nextString();
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, String value) throws IOException {
/* 406 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 410 */   public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
/*     */       public BigDecimal read(JsonReader in) throws IOException {
/* 412 */         if (in.peek() == JsonToken.NULL) {
/* 413 */           in.nextNull();
/* 414 */           return null;
/*     */         } 
/*     */         try {
/* 417 */           return new BigDecimal(in.nextString());
/* 418 */         } catch (NumberFormatException e) {
/* 419 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, BigDecimal value) throws IOException {
/* 424 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 428 */   public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
/*     */       public BigInteger read(JsonReader in) throws IOException {
/* 430 */         if (in.peek() == JsonToken.NULL) {
/* 431 */           in.nextNull();
/* 432 */           return null;
/*     */         } 
/*     */         try {
/* 435 */           return new BigInteger(in.nextString());
/* 436 */         } catch (NumberFormatException e) {
/* 437 */           throw new JsonSyntaxException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, BigInteger value) throws IOException {
/* 442 */         out.value(value);
/*     */       }
/*     */     };
/*     */   
/* 446 */   public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
/*     */   
/* 448 */   public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>()
/*     */     {
/*     */       public StringBuilder read(JsonReader in) throws IOException {
/* 451 */         if (in.peek() == JsonToken.NULL) {
/* 452 */           in.nextNull();
/* 453 */           return null;
/*     */         } 
/* 455 */         return new StringBuilder(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, StringBuilder value) throws IOException {
/* 459 */         out.value((value == null) ? null : value.toString());
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 464 */   public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
/*     */   
/* 466 */   public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>()
/*     */     {
/*     */       public StringBuffer read(JsonReader in) throws IOException {
/* 469 */         if (in.peek() == JsonToken.NULL) {
/* 470 */           in.nextNull();
/* 471 */           return null;
/*     */         } 
/* 473 */         return new StringBuffer(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, StringBuffer value) throws IOException {
/* 477 */         out.value((value == null) ? null : value.toString());
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 482 */   public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
/*     */   
/* 484 */   public static final TypeAdapter<URL> URL = new TypeAdapter<URL>()
/*     */     {
/*     */       public URL read(JsonReader in) throws IOException {
/* 487 */         if (in.peek() == JsonToken.NULL) {
/* 488 */           in.nextNull();
/* 489 */           return null;
/*     */         } 
/* 491 */         String nextString = in.nextString();
/* 492 */         return "null".equals(nextString) ? null : new URL(nextString);
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, URL value) throws IOException {
/* 496 */         out.value((value == null) ? null : value.toExternalForm());
/*     */       }
/*     */     };
/*     */   
/* 500 */   public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
/*     */   
/* 502 */   public static final TypeAdapter<URI> URI = new TypeAdapter<URI>()
/*     */     {
/*     */       public URI read(JsonReader in) throws IOException {
/* 505 */         if (in.peek() == JsonToken.NULL) {
/* 506 */           in.nextNull();
/* 507 */           return null;
/*     */         } 
/*     */         try {
/* 510 */           String nextString = in.nextString();
/* 511 */           return "null".equals(nextString) ? null : new URI(nextString);
/* 512 */         } catch (URISyntaxException e) {
/* 513 */           throw new JsonIOException(e);
/*     */         } 
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, URI value) throws IOException {
/* 518 */         out.value((value == null) ? null : value.toASCIIString());
/*     */       }
/*     */     };
/*     */   
/* 522 */   public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
/*     */   
/* 524 */   public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>()
/*     */     {
/*     */       public InetAddress read(JsonReader in) throws IOException {
/* 527 */         if (in.peek() == JsonToken.NULL) {
/* 528 */           in.nextNull();
/* 529 */           return null;
/*     */         } 
/*     */         
/* 532 */         return InetAddress.getByName(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, InetAddress value) throws IOException {
/* 536 */         out.value((value == null) ? null : value.getHostAddress());
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 541 */   public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
/*     */   
/* 543 */   public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>()
/*     */     {
/*     */       public UUID read(JsonReader in) throws IOException {
/* 546 */         if (in.peek() == JsonToken.NULL) {
/* 547 */           in.nextNull();
/* 548 */           return null;
/*     */         } 
/* 550 */         return UUID.fromString(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, UUID value) throws IOException {
/* 554 */         out.value((value == null) ? null : value.toString());
/*     */       }
/*     */     };
/*     */   
/* 558 */   public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
/*     */   
/* 560 */   public static final TypeAdapter<Currency> CURRENCY = (new TypeAdapter<Currency>()
/*     */     {
/*     */       public Currency read(JsonReader in) throws IOException {
/* 563 */         return Currency.getInstance(in.nextString());
/*     */       }
/*     */       
/*     */       public void write(JsonWriter out, Currency value) throws IOException {
/* 567 */         out.value(value.getCurrencyCode());
/*     */       }
/* 569 */     }).nullSafe();
/* 570 */   public static final TypeAdapterFactory CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
/*     */   
/* 572 */   public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 575 */         if (typeToken.getRawType() != Timestamp.class) {
/* 576 */           return null;
/*     */         }
/*     */         
/* 579 */         final TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
/* 580 */         return (TypeAdapter)new TypeAdapter<Timestamp>() {
/*     */             public Timestamp read(JsonReader in) throws IOException {
/* 582 */               Date date = (Date)dateTypeAdapter.read(in);
/* 583 */               return (date != null) ? new Timestamp(date.getTime()) : null;
/*     */             }
/*     */             
/*     */             public void write(JsonWriter out, Timestamp value) throws IOException {
/* 587 */               dateTypeAdapter.write(out, value);
/*     */             }
/*     */           };
/*     */       }
/*     */     };
/*     */   
/* 593 */   public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>()
/*     */     {
/*     */       private static final String YEAR = "year";
/*     */       private static final String MONTH = "month";
/*     */       private static final String DAY_OF_MONTH = "dayOfMonth";
/*     */       private static final String HOUR_OF_DAY = "hourOfDay";
/*     */       private static final String MINUTE = "minute";
/*     */       private static final String SECOND = "second";
/*     */       
/*     */       public Calendar read(JsonReader in) throws IOException {
/* 603 */         if (in.peek() == JsonToken.NULL) {
/* 604 */           in.nextNull();
/* 605 */           return null;
/*     */         } 
/* 607 */         in.beginObject();
/* 608 */         int year = 0;
/* 609 */         int month = 0;
/* 610 */         int dayOfMonth = 0;
/* 611 */         int hourOfDay = 0;
/* 612 */         int minute = 0;
/* 613 */         int second = 0;
/* 614 */         while (in.peek() != JsonToken.END_OBJECT) {
/* 615 */           String name = in.nextName();
/* 616 */           int value = in.nextInt();
/* 617 */           if ("year".equals(name)) {
/* 618 */             year = value; continue;
/* 619 */           }  if ("month".equals(name)) {
/* 620 */             month = value; continue;
/* 621 */           }  if ("dayOfMonth".equals(name)) {
/* 622 */             dayOfMonth = value; continue;
/* 623 */           }  if ("hourOfDay".equals(name)) {
/* 624 */             hourOfDay = value; continue;
/* 625 */           }  if ("minute".equals(name)) {
/* 626 */             minute = value; continue;
/* 627 */           }  if ("second".equals(name)) {
/* 628 */             second = value;
/*     */           }
/*     */         } 
/* 631 */         in.endObject();
/* 632 */         return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(JsonWriter out, Calendar value) throws IOException {
/* 637 */         if (value == null) {
/* 638 */           out.nullValue();
/*     */           return;
/*     */         } 
/* 641 */         out.beginObject();
/* 642 */         out.name("year");
/* 643 */         out.value(value.get(1));
/* 644 */         out.name("month");
/* 645 */         out.value(value.get(2));
/* 646 */         out.name("dayOfMonth");
/* 647 */         out.value(value.get(5));
/* 648 */         out.name("hourOfDay");
/* 649 */         out.value(value.get(11));
/* 650 */         out.name("minute");
/* 651 */         out.value(value.get(12));
/* 652 */         out.name("second");
/* 653 */         out.value(value.get(13));
/* 654 */         out.endObject();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 659 */   public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, (Class)GregorianCalendar.class, CALENDAR);
/*     */   
/* 661 */   public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>()
/*     */     {
/*     */       public Locale read(JsonReader in) throws IOException {
/* 664 */         if (in.peek() == JsonToken.NULL) {
/* 665 */           in.nextNull();
/* 666 */           return null;
/*     */         } 
/* 668 */         String locale = in.nextString();
/* 669 */         StringTokenizer tokenizer = new StringTokenizer(locale, "_");
/* 670 */         String language = null;
/* 671 */         String country = null;
/* 672 */         String variant = null;
/* 673 */         if (tokenizer.hasMoreElements()) {
/* 674 */           language = tokenizer.nextToken();
/*     */         }
/* 676 */         if (tokenizer.hasMoreElements()) {
/* 677 */           country = tokenizer.nextToken();
/*     */         }
/* 679 */         if (tokenizer.hasMoreElements()) {
/* 680 */           variant = tokenizer.nextToken();
/*     */         }
/* 682 */         if (country == null && variant == null)
/* 683 */           return new Locale(language); 
/* 684 */         if (variant == null) {
/* 685 */           return new Locale(language, country);
/*     */         }
/* 687 */         return new Locale(language, country, variant);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(JsonWriter out, Locale value) throws IOException {
/* 692 */         out.value((value == null) ? null : value.toString());
/*     */       }
/*     */     };
/*     */   
/* 696 */   public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
/*     */   
/* 698 */   public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>() { public JsonElement read(JsonReader in) throws IOException { String number; JsonArray array;
/*     */         JsonObject object;
/* 700 */         switch (in.peek()) {
/*     */           case STRING:
/* 702 */             return (JsonElement)new JsonPrimitive(in.nextString());
/*     */           case NUMBER:
/* 704 */             number = in.nextString();
/* 705 */             return (JsonElement)new JsonPrimitive((Number)new LazilyParsedNumber(number));
/*     */           case BOOLEAN:
/* 707 */             return (JsonElement)new JsonPrimitive(Boolean.valueOf(in.nextBoolean()));
/*     */           case NULL:
/* 709 */             in.nextNull();
/* 710 */             return (JsonElement)JsonNull.INSTANCE;
/*     */           case BEGIN_ARRAY:
/* 712 */             array = new JsonArray();
/* 713 */             in.beginArray();
/* 714 */             while (in.hasNext()) {
/* 715 */               array.add(read(in));
/*     */             }
/* 717 */             in.endArray();
/* 718 */             return (JsonElement)array;
/*     */           case BEGIN_OBJECT:
/* 720 */             object = new JsonObject();
/* 721 */             in.beginObject();
/* 722 */             while (in.hasNext()) {
/* 723 */               object.add(in.nextName(), read(in));
/*     */             }
/* 725 */             in.endObject();
/* 726 */             return (JsonElement)object;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 732 */         throw new IllegalArgumentException(); }
/*     */ 
/*     */ 
/*     */       
/*     */       public void write(JsonWriter out, JsonElement value) throws IOException {
/* 737 */         if (value == null || value.isJsonNull()) {
/* 738 */           out.nullValue();
/* 739 */         } else if (value.isJsonPrimitive()) {
/* 740 */           JsonPrimitive primitive = value.getAsJsonPrimitive();
/* 741 */           if (primitive.isNumber()) {
/* 742 */             out.value(primitive.getAsNumber());
/* 743 */           } else if (primitive.isBoolean()) {
/* 744 */             out.value(primitive.getAsBoolean());
/*     */           } else {
/* 746 */             out.value(primitive.getAsString());
/*     */           }
/*     */         
/* 749 */         } else if (value.isJsonArray()) {
/* 750 */           out.beginArray();
/* 751 */           for (JsonElement e : value.getAsJsonArray()) {
/* 752 */             write(out, e);
/*     */           }
/* 754 */           out.endArray();
/*     */         }
/* 756 */         else if (value.isJsonObject()) {
/* 757 */           out.beginObject();
/* 758 */           for (Map.Entry<String, JsonElement> e : (Iterable<Map.Entry<String, JsonElement>>)value.getAsJsonObject().entrySet()) {
/* 759 */             out.name(e.getKey());
/* 760 */             write(out, e.getValue());
/*     */           } 
/* 762 */           out.endObject();
/*     */         } else {
/*     */           
/* 765 */           throw new IllegalArgumentException("Couldn't write " + value.getClass());
/*     */         } 
/*     */       } }
/*     */   ;
/*     */ 
/*     */   
/* 771 */   public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
/*     */   
/*     */   private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
/* 774 */     private final Map<String, T> nameToConstant = new HashMap<String, T>();
/* 775 */     private final Map<T, String> constantToName = new HashMap<T, String>();
/*     */     
/*     */     public EnumTypeAdapter(Class<T> classOfT) {
/*     */       try {
/* 779 */         for (Enum enum_ : (Enum[])classOfT.getEnumConstants()) {
/* 780 */           String name = enum_.name();
/* 781 */           SerializedName annotation = classOfT.getField(name).<SerializedName>getAnnotation(SerializedName.class);
/* 782 */           if (annotation != null) {
/* 783 */             name = annotation.value();
/* 784 */             for (String alternate : annotation.alternate()) {
/* 785 */               this.nameToConstant.put(alternate, (T)enum_);
/*     */             }
/*     */           } 
/* 788 */           this.nameToConstant.put(name, (T)enum_);
/* 789 */           this.constantToName.put((T)enum_, name);
/*     */         } 
/* 791 */       } catch (NoSuchFieldException e) {
/* 792 */         throw new AssertionError(e);
/*     */       } 
/*     */     }
/*     */     public T read(JsonReader in) throws IOException {
/* 796 */       if (in.peek() == JsonToken.NULL) {
/* 797 */         in.nextNull();
/* 798 */         return null;
/*     */       } 
/* 800 */       return this.nameToConstant.get(in.nextString());
/*     */     }
/*     */     
/*     */     public void write(JsonWriter out, T value) throws IOException {
/* 804 */       out.value((value == null) ? null : this.constantToName.get(value));
/*     */     }
/*     */   }
/*     */   
/* 808 */   public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 811 */         Class<? super T> rawType = typeToken.getRawType();
/* 812 */         if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
/* 813 */           return null;
/*     */         }
/* 815 */         if (!rawType.isEnum()) {
/* 816 */           rawType = rawType.getSuperclass();
/*     */         }
/* 818 */         return new TypeAdapters.EnumTypeAdapter<T>(rawType);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
/* 824 */     return new TypeAdapterFactory()
/*     */       {
/*     */         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 827 */           return typeToken.equals(type) ? typeAdapter : null;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
/* 834 */     return new TypeAdapterFactory()
/*     */       {
/*     */         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 837 */           return (typeToken.getRawType() == type) ? typeAdapter : null;
/*     */         }
/*     */         public String toString() {
/* 840 */           return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
/* 847 */     return new TypeAdapterFactory()
/*     */       {
/*     */         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 850 */           Class<? super T> rawType = typeToken.getRawType();
/* 851 */           return (rawType == unboxed || rawType == boxed) ? typeAdapter : null;
/*     */         }
/*     */         public String toString() {
/* 854 */           return "Factory[type=" + boxed.getName() + "+" + unboxed
/* 855 */             .getName() + ",adapter=" + typeAdapter + "]";
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter) {
/* 862 */     return new TypeAdapterFactory()
/*     */       {
/*     */         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 865 */           Class<? super T> rawType = typeToken.getRawType();
/* 866 */           return (rawType == base || rawType == sub) ? typeAdapter : null;
/*     */         }
/*     */         public String toString() {
/* 869 */           return "Factory[type=" + base.getName() + "+" + sub
/* 870 */             .getName() + ",adapter=" + typeAdapter + "]";
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
/* 881 */     return new TypeAdapterFactory()
/*     */       {
/*     */         public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
/* 884 */           final Class<? super T2> requestedType = typeToken.getRawType();
/* 885 */           if (!clazz.isAssignableFrom(requestedType)) {
/* 886 */             return null;
/*     */           }
/* 888 */           return new TypeAdapter<T1>() {
/*     */               public void write(JsonWriter out, T1 value) throws IOException {
/* 890 */                 typeAdapter.write(out, value);
/*     */               }
/*     */               
/*     */               public T1 read(JsonReader in) throws IOException {
/* 894 */                 T1 result = (T1)typeAdapter.read(in);
/* 895 */                 if (result != null && !requestedType.isInstance(result)) {
/* 896 */                   throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + result
/* 897 */                       .getClass().getName());
/*     */                 }
/* 899 */                 return result;
/*     */               }
/*     */             };
/*     */         }
/*     */         public String toString() {
/* 904 */           return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\TypeAdapters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */