/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.FormatSchema;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonGeneratorDelegate
/*     */   extends JsonGenerator
/*     */ {
/*     */   protected JsonGenerator delegate;
/*     */   protected boolean delegateCopyMethods;
/*     */   
/*     */   public JsonGeneratorDelegate(JsonGenerator d) {
/*  33 */     this(d, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGeneratorDelegate(JsonGenerator d, boolean delegateCopyMethods) {
/*  42 */     this.delegate = d;
/*  43 */     this.delegateCopyMethods = delegateCopyMethods;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/*  48 */     return this.delegate.getCurrentValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/*  53 */     this.delegate.setCurrentValue(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator getDelegate() {
/*  62 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectCodec getCodec() {
/*  70 */     return this.delegate.getCodec();
/*     */   }
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/*  73 */     this.delegate.setCodec(oc);
/*  74 */     return this;
/*     */   }
/*     */   
/*  77 */   public void setSchema(FormatSchema schema) { this.delegate.setSchema(schema); }
/*  78 */   public FormatSchema getSchema() { return this.delegate.getSchema(); }
/*  79 */   public Version version() { return this.delegate.version(); }
/*  80 */   public Object getOutputTarget() { return this.delegate.getOutputTarget(); } public int getOutputBuffered() {
/*  81 */     return this.delegate.getOutputBuffered();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canUseSchema(FormatSchema schema) {
/*  90 */     return this.delegate.canUseSchema(schema);
/*     */   }
/*     */   public boolean canWriteTypeId() {
/*  93 */     return this.delegate.canWriteTypeId();
/*     */   }
/*     */   public boolean canWriteObjectId() {
/*  96 */     return this.delegate.canWriteObjectId();
/*     */   }
/*     */   public boolean canWriteBinaryNatively() {
/*  99 */     return this.delegate.canWriteBinaryNatively();
/*     */   }
/*     */   public boolean canOmitFields() {
/* 102 */     return this.delegate.canOmitFields();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 112 */     this.delegate.enable(f);
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 118 */     this.delegate.disable(f);
/* 119 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isEnabled(JsonGenerator.Feature f) {
/* 123 */     return this.delegate.isEnabled(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFeatureMask() {
/* 129 */     return this.delegate.getFeatureMask();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator setFeatureMask(int mask) {
/* 134 */     this.delegate.setFeatureMask(mask);
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/* 140 */     this.delegate.overrideStdFeatures(values, mask);
/* 141 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideFormatFeatures(int values, int mask) {
/* 146 */     this.delegate.overrideFormatFeatures(values, mask);
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
/* 158 */     this.delegate.setPrettyPrinter(pp);
/* 159 */     return this;
/*     */   }
/*     */   
/*     */   public PrettyPrinter getPrettyPrinter() {
/* 163 */     return this.delegate.getPrettyPrinter();
/*     */   }
/*     */   public JsonGenerator useDefaultPrettyPrinter() {
/* 166 */     this.delegate.useDefaultPrettyPrinter();
/* 167 */     return this;
/*     */   }
/*     */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/* 170 */     this.delegate.setHighestNonEscapedChar(charCode);
/* 171 */     return this;
/*     */   }
/*     */   public int getHighestEscapedChar() {
/* 174 */     return this.delegate.getHighestEscapedChar();
/*     */   }
/*     */   public CharacterEscapes getCharacterEscapes() {
/* 177 */     return this.delegate.getCharacterEscapes();
/*     */   }
/*     */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/* 180 */     this.delegate.setCharacterEscapes(esc);
/* 181 */     return this;
/*     */   }
/*     */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/* 184 */     this.delegate.setRootValueSeparator(sep);
/* 185 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartArray() throws IOException {
/* 194 */     this.delegate.writeStartArray();
/*     */   }
/*     */   public void writeStartArray(int size) throws IOException {
/* 197 */     this.delegate.writeStartArray(size);
/*     */   }
/*     */   public void writeStartArray(Object forValue) throws IOException {
/* 200 */     this.delegate.writeStartArray(forValue);
/*     */   }
/*     */   public void writeStartArray(Object forValue, int size) throws IOException {
/* 203 */     this.delegate.writeStartArray(forValue, size);
/*     */   }
/*     */   public void writeEndArray() throws IOException {
/* 206 */     this.delegate.writeEndArray();
/*     */   }
/*     */   public void writeStartObject() throws IOException {
/* 209 */     this.delegate.writeStartObject();
/*     */   }
/*     */   public void writeStartObject(Object forValue) throws IOException {
/* 212 */     this.delegate.writeStartObject(forValue);
/*     */   }
/*     */   
/*     */   public void writeStartObject(Object forValue, int size) throws IOException {
/* 216 */     this.delegate.writeStartObject(forValue, size);
/*     */   }
/*     */   
/*     */   public void writeEndObject() throws IOException {
/* 220 */     this.delegate.writeEndObject();
/*     */   }
/*     */   
/*     */   public void writeFieldName(String name) throws IOException {
/* 224 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldName(SerializableString name) throws IOException {
/* 229 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldId(long id) throws IOException {
/* 234 */     this.delegate.writeFieldId(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(int[] array, int offset, int length) throws IOException {
/* 239 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(long[] array, int offset, int length) throws IOException {
/* 244 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(double[] array, int offset, int length) throws IOException {
/* 249 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeString(String text) throws IOException {
/* 259 */     this.delegate.writeString(text);
/*     */   }
/*     */   
/*     */   public void writeString(Reader reader, int len) throws IOException {
/* 263 */     this.delegate.writeString(reader, len);
/*     */   }
/*     */   
/*     */   public void writeString(char[] text, int offset, int len) throws IOException {
/* 267 */     this.delegate.writeString(text, offset, len);
/*     */   }
/*     */   public void writeString(SerializableString text) throws IOException {
/* 270 */     this.delegate.writeString(text);
/*     */   }
/*     */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/* 273 */     this.delegate.writeRawUTF8String(text, offset, length);
/*     */   }
/*     */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/* 276 */     this.delegate.writeUTF8String(text, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeRaw(String text) throws IOException {
/* 285 */     this.delegate.writeRaw(text);
/*     */   }
/*     */   public void writeRaw(String text, int offset, int len) throws IOException {
/* 288 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   public void writeRaw(SerializableString raw) throws IOException {
/* 291 */     this.delegate.writeRaw(raw);
/*     */   }
/*     */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/* 294 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   public void writeRaw(char c) throws IOException {
/* 297 */     this.delegate.writeRaw(c);
/*     */   }
/*     */   public void writeRawValue(String text) throws IOException {
/* 300 */     this.delegate.writeRawValue(text);
/*     */   }
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 303 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 306 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
/* 309 */     this.delegate.writeBinary(b64variant, data, offset, len);
/*     */   }
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/* 312 */     return this.delegate.writeBinary(b64variant, data, dataLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeNumber(short v) throws IOException {
/* 321 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(int v) throws IOException {
/* 324 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(long v) throws IOException {
/* 327 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(BigInteger v) throws IOException {
/* 330 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(double v) throws IOException {
/* 333 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(float v) throws IOException {
/* 336 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(BigDecimal v) throws IOException {
/* 339 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
/* 342 */     this.delegate.writeNumber(encodedValue);
/*     */   }
/*     */   public void writeBoolean(boolean state) throws IOException {
/* 345 */     this.delegate.writeBoolean(state);
/*     */   }
/*     */   public void writeNull() throws IOException {
/* 348 */     this.delegate.writeNull();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeOmittedField(String fieldName) throws IOException {
/* 357 */     this.delegate.writeOmittedField(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObjectId(Object id) throws IOException {
/* 366 */     this.delegate.writeObjectId(id);
/*     */   }
/*     */   public void writeObjectRef(Object id) throws IOException {
/* 369 */     this.delegate.writeObjectRef(id);
/*     */   }
/*     */   public void writeTypeId(Object id) throws IOException {
/* 372 */     this.delegate.writeTypeId(id);
/*     */   }
/*     */   public void writeEmbeddedObject(Object object) throws IOException {
/* 375 */     this.delegate.writeEmbeddedObject(object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObject(Object pojo) throws IOException {
/* 385 */     if (this.delegateCopyMethods) {
/* 386 */       this.delegate.writeObject(pojo);
/*     */       return;
/*     */     } 
/* 389 */     if (pojo == null) {
/* 390 */       writeNull();
/*     */     } else {
/* 392 */       ObjectCodec c = getCodec();
/* 393 */       if (c != null) {
/* 394 */         c.writeValue(this, pojo);
/*     */         return;
/*     */       } 
/* 397 */       _writeSimpleObject(pojo);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTree(TreeNode tree) throws IOException {
/* 403 */     if (this.delegateCopyMethods) {
/* 404 */       this.delegate.writeTree(tree);
/*     */       
/*     */       return;
/*     */     } 
/* 408 */     if (tree == null) {
/* 409 */       writeNull();
/*     */     } else {
/* 411 */       ObjectCodec c = getCodec();
/* 412 */       if (c == null) {
/* 413 */         throw new IllegalStateException("No ObjectCodec defined");
/*     */       }
/* 415 */       c.writeTree(this, tree);
/*     */     } 
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyCurrentEvent(JsonParser p) throws IOException {
/* 435 */     if (this.delegateCopyMethods) { this.delegate.copyCurrentEvent(p); }
/* 436 */     else { super.copyCurrentEvent(p); }
/*     */   
/*     */   }
/*     */   
/*     */   public void copyCurrentStructure(JsonParser p) throws IOException {
/* 441 */     if (this.delegateCopyMethods) { this.delegate.copyCurrentStructure(p); }
/* 442 */     else { super.copyCurrentStructure(p); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonStreamContext getOutputContext() {
/* 451 */     return this.delegate.getOutputContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 459 */     this.delegate.flush(); } public void close() throws IOException {
/* 460 */     this.delegate.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 468 */     return this.delegate.isClosed();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\JsonGeneratorDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */