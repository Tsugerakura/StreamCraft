/*     */ package io.sentry.marshaller.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SentryJsonGenerator
/*     */   extends JsonGenerator
/*     */ {
/*  24 */   private static final Logger logger = LoggerFactory.getLogger(Util.class);
/*     */   
/*     */   private static final String RECURSION_LIMIT_HIT = "<recursion limit hit>";
/*     */   
/*     */   private static final int MAX_LENGTH_LIST = 10;
/*     */   
/*     */   private static final int MAX_SIZE_MAP = 50;
/*     */   
/*     */   private static final int MAX_LENGTH_STRING = 400;
/*     */   
/*     */   private static final int MAX_NESTING = 3;
/*     */   
/*     */   private static final String ELIDED = "...";
/*     */   
/*     */   private int maxLengthList;
/*     */   private int maxLengthString;
/*     */   private int maxSizeMap;
/*     */   private int maxNesting;
/*     */   private JsonGenerator generator;
/*     */   
/*     */   public SentryJsonGenerator(JsonGenerator generator) {
/*  45 */     this.generator = generator;
/*     */     
/*  47 */     this.maxLengthList = 10;
/*  48 */     this.maxLengthString = 400;
/*  49 */     this.maxSizeMap = 50;
/*  50 */     this.maxNesting = 3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObject(Object value) throws IOException {
/*  60 */     writeObject(value, 0);
/*     */   }
/*     */   
/*     */   private void writeObject(Object value, int recursionLevel) throws IOException {
/*  64 */     if (recursionLevel >= this.maxNesting) {
/*  65 */       this.generator.writeString("<recursion limit hit>");
/*     */       
/*     */       return;
/*     */     } 
/*  69 */     if (value == null) {
/*  70 */       this.generator.writeNull();
/*  71 */     } else if (value.getClass().isArray()) {
/*  72 */       this.generator.writeStartArray();
/*  73 */       writeArray(value, recursionLevel);
/*  74 */       this.generator.writeEndArray();
/*  75 */     } else if (value instanceof Map) {
/*  76 */       this.generator.writeStartObject();
/*  77 */       int i = 0;
/*  78 */       for (Map.Entry<?, ?> entry : (Iterable<Map.Entry<?, ?>>)((Map)value).entrySet()) {
/*  79 */         if (i >= this.maxSizeMap) {
/*     */           break;
/*     */         }
/*     */         
/*  83 */         if (entry.getKey() == null) {
/*  84 */           this.generator.writeFieldName("null");
/*     */         } else {
/*  86 */           this.generator.writeFieldName(Util.trimString(entry.getKey().toString(), this.maxLengthString));
/*     */         } 
/*  88 */         writeObject(entry.getValue(), recursionLevel + 1);
/*     */         
/*  90 */         i++;
/*     */       } 
/*  92 */       this.generator.writeEndObject();
/*  93 */     } else if (value instanceof java.util.Collection) {
/*  94 */       this.generator.writeStartArray();
/*  95 */       int i = 0;
/*  96 */       for (Object subValue : value) {
/*  97 */         if (i >= this.maxLengthList) {
/*  98 */           writeElided();
/*     */           
/*     */           break;
/*     */         } 
/* 102 */         writeObject(subValue, recursionLevel + 1);
/*     */         
/* 104 */         i++;
/*     */       } 
/* 106 */       this.generator.writeEndArray();
/* 107 */     } else if (value instanceof String) {
/* 108 */       this.generator.writeString(Util.trimString((String)value, this.maxLengthString));
/*     */     } else {
/*     */       
/*     */       try {
/* 112 */         this.generator.writeObject(value);
/* 113 */       } catch (IllegalStateException e) {
/* 114 */         logger.debug("Couldn't marshal '{}' of type '{}', had to be converted into a String", value, value
/* 115 */             .getClass());
/*     */         try {
/* 117 */           this.generator.writeString(Util.trimString(value.toString(), this.maxLengthString));
/* 118 */         } catch (IOException|RuntimeException innerE) {
/* 119 */           this.generator.writeString("<exception calling toString on object>");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeArray(Object value, int recursionLevel) throws IOException {
/* 127 */     if (value instanceof byte[]) {
/* 128 */       byte[] byteArray = (byte[])value;
/* 129 */       for (int i = 0; i < byteArray.length && i < this.maxLengthList; i++) {
/* 130 */         this.generator.writeNumber(byteArray[i]);
/*     */       }
/* 132 */       if (byteArray.length > this.maxLengthList) {
/* 133 */         writeElided();
/*     */       }
/* 135 */     } else if (value instanceof short[]) {
/* 136 */       short[] shortArray = (short[])value;
/* 137 */       for (int i = 0; i < shortArray.length && i < this.maxLengthList; i++) {
/* 138 */         this.generator.writeNumber(shortArray[i]);
/*     */       }
/* 140 */       if (shortArray.length > this.maxLengthList) {
/* 141 */         writeElided();
/*     */       }
/* 143 */     } else if (value instanceof int[]) {
/* 144 */       int[] intArray = (int[])value;
/* 145 */       for (int i = 0; i < intArray.length && i < this.maxLengthList; i++) {
/* 146 */         this.generator.writeNumber(intArray[i]);
/*     */       }
/* 148 */       if (intArray.length > this.maxLengthList) {
/* 149 */         writeElided();
/*     */       }
/* 151 */     } else if (value instanceof long[]) {
/* 152 */       long[] longArray = (long[])value;
/* 153 */       for (int i = 0; i < longArray.length && i < this.maxLengthList; i++) {
/* 154 */         this.generator.writeNumber(longArray[i]);
/*     */       }
/* 156 */       if (longArray.length > this.maxLengthList) {
/* 157 */         writeElided();
/*     */       }
/* 159 */     } else if (value instanceof float[]) {
/* 160 */       float[] floatArray = (float[])value;
/* 161 */       for (int i = 0; i < floatArray.length && i < this.maxLengthList; i++) {
/* 162 */         this.generator.writeNumber(floatArray[i]);
/*     */       }
/* 164 */       if (floatArray.length > this.maxLengthList) {
/* 165 */         writeElided();
/*     */       }
/* 167 */     } else if (value instanceof double[]) {
/* 168 */       double[] doubleArray = (double[])value;
/* 169 */       for (int i = 0; i < doubleArray.length && i < this.maxLengthList; i++) {
/* 170 */         this.generator.writeNumber(doubleArray[i]);
/*     */       }
/* 172 */       if (doubleArray.length > this.maxLengthList) {
/* 173 */         writeElided();
/*     */       }
/* 175 */     } else if (value instanceof char[]) {
/* 176 */       char[] charArray = (char[])value;
/* 177 */       for (int i = 0; i < charArray.length && i < this.maxLengthList; i++) {
/* 178 */         this.generator.writeString(String.valueOf(charArray[i]));
/*     */       }
/* 180 */       if (charArray.length > this.maxLengthList) {
/* 181 */         writeElided();
/*     */       }
/* 183 */     } else if (value instanceof boolean[]) {
/* 184 */       boolean[] boolArray = (boolean[])value;
/* 185 */       for (int i = 0; i < boolArray.length && i < this.maxLengthList; i++) {
/* 186 */         this.generator.writeBoolean(boolArray[i]);
/*     */       }
/* 188 */       if (boolArray.length > this.maxLengthList) {
/* 189 */         writeElided();
/*     */       }
/*     */     } else {
/* 192 */       Object[] objArray = (Object[])value;
/* 193 */       for (int i = 0; i < objArray.length && i < this.maxLengthList; i++) {
/* 194 */         writeObject(objArray[i], recursionLevel + 1);
/*     */       }
/* 196 */       if (objArray.length > this.maxLengthList) {
/* 197 */         writeElided();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeElided() throws IOException {
/* 203 */     this.generator.writeString("...");
/*     */   }
/*     */   
/*     */   public void setMaxLengthList(int maxLengthList) {
/* 207 */     this.maxLengthList = maxLengthList;
/*     */   }
/*     */   
/*     */   public void setMaxLengthString(int maxLengthString) {
/* 211 */     this.maxLengthString = maxLengthString;
/*     */   }
/*     */   
/*     */   public void setMaxSizeMap(int maxSizeMap) {
/* 215 */     this.maxSizeMap = maxSizeMap;
/*     */   }
/*     */   
/*     */   public void setMaxNesting(int maxNesting) {
/* 219 */     this.maxNesting = maxNesting;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/* 224 */     return this.generator.setCodec(oc);
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectCodec getCodec() {
/* 229 */     return this.generator.getCodec();
/*     */   }
/*     */ 
/*     */   
/*     */   public Version version() {
/* 234 */     return this.generator.version();
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 239 */     return this.generator.enable(f);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 244 */     return this.generator.disable(f);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(JsonGenerator.Feature f) {
/* 249 */     return this.generator.isEnabled(f);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFeatureMask() {
/* 254 */     return this.generator.getFeatureMask();
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setFeatureMask(int values) {
/* 259 */     return this.generator.setFeatureMask(values);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter() {
/* 264 */     return this.generator.useDefaultPrettyPrinter();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeStartArray() throws IOException {
/* 269 */     this.generator.writeStartArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeEndArray() throws IOException {
/* 274 */     this.generator.writeEndArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeStartObject() throws IOException {
/* 279 */     this.generator.writeStartObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeEndObject() throws IOException {
/* 284 */     this.generator.writeEndObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldName(String name) throws IOException {
/* 289 */     this.generator.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldName(SerializableString name) throws IOException {
/* 294 */     this.generator.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(String text) throws IOException {
/* 299 */     this.generator.writeString(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(char[] text, int offset, int len) throws IOException {
/* 304 */     this.generator.writeString(text, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(SerializableString text) throws IOException {
/* 309 */     this.generator.writeString(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/* 314 */     this.generator.writeRawUTF8String(text, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/* 319 */     this.generator.writeUTF8String(text, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRaw(String text) throws IOException {
/* 324 */     this.generator.writeRaw(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRaw(String text, int offset, int len) throws IOException {
/* 329 */     this.generator.writeRaw(text, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/* 334 */     this.generator.writeRaw(text, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRaw(char c) throws IOException {
/* 339 */     this.generator.writeRaw(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRawValue(String text) throws IOException {
/* 344 */     this.generator.writeRawValue(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 349 */     this.generator.writeRawValue(text, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 354 */     this.generator.writeRawValue(text, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
/* 359 */     this.generator.writeBinary(bv, data, offset, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
/* 364 */     return this.generator.writeBinary(bv, data, dataLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(int v) throws IOException {
/* 369 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(long v) throws IOException {
/* 374 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(BigInteger v) throws IOException {
/* 379 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(double v) throws IOException {
/* 384 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(float v) throws IOException {
/* 389 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(BigDecimal v) throws IOException {
/* 394 */     this.generator.writeNumber(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNumber(String encodedValue) throws IOException {
/* 399 */     this.generator.writeNumber(encodedValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBoolean(boolean state) throws IOException {
/* 404 */     this.generator.writeBoolean(state);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeNull() throws IOException {
/* 409 */     this.generator.writeNull();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTree(TreeNode rootNode) throws IOException {
/* 414 */     this.generator.writeTree(rootNode);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonStreamContext getOutputContext() {
/* 419 */     return this.generator.getOutputContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 424 */     this.generator.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 429 */     return this.generator.isClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 434 */     this.generator.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\SentryJsonGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */