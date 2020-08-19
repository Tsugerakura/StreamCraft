/*      */ package com.fasterxml.jackson.core;
/*      */ 
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*      */ import com.fasterxml.jackson.core.util.VersionUtil;
/*      */ import java.io.Closeable;
/*      */ import java.io.Flushable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicLong;
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
/*      */ public abstract class JsonGenerator
/*      */   implements Closeable, Flushable, Versioned
/*      */ {
/*      */   protected PrettyPrinter _cfgPrettyPrinter;
/*      */   
/*      */   public abstract JsonGenerator setCodec(ObjectCodec paramObjectCodec);
/*      */   
/*      */   public abstract ObjectCodec getCodec();
/*      */   
/*      */   public abstract Version version();
/*      */   
/*      */   public abstract JsonGenerator enable(Feature paramFeature);
/*      */   
/*      */   public abstract JsonGenerator disable(Feature paramFeature);
/*      */   
/*      */   public enum Feature
/*      */   {
/*   51 */     AUTO_CLOSE_TARGET(true),
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
/*   63 */     AUTO_CLOSE_JSON_CONTENT(true),
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
/*   76 */     FLUSH_PASSED_TO_STREAM(true),
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
/*   91 */     QUOTE_FIELD_NAMES(true),
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
/*  108 */     QUOTE_NON_NUMERIC_NUMBERS(true),
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
/*  130 */     ESCAPE_NON_ASCII(false),
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
/*  155 */     WRITE_NUMBERS_AS_STRINGS(false),
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
/*  172 */     WRITE_BIGDECIMAL_AS_PLAIN(false),
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
/*  191 */     STRICT_DUPLICATE_DETECTION(false),
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
/*  213 */     IGNORE_UNKNOWN(false);
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean _defaultState;
/*      */ 
/*      */     
/*      */     private final int _mask;
/*      */ 
/*      */ 
/*      */     
/*      */     public static int collectDefaults() {
/*  225 */       int flags = 0;
/*  226 */       for (Feature f : values()) {
/*  227 */         if (f.enabledByDefault()) {
/*  228 */           flags |= f.getMask();
/*      */         }
/*      */       } 
/*  231 */       return flags;
/*      */     }
/*      */     
/*      */     Feature(boolean defaultState) {
/*  235 */       this._defaultState = defaultState;
/*  236 */       this._mask = 1 << ordinal();
/*      */     }
/*      */     public boolean enabledByDefault() {
/*  239 */       return this._defaultState;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean enabledIn(int flags) {
/*  244 */       return ((flags & this._mask) != 0);
/*      */     } public int getMask() {
/*  246 */       return this._mask;
/*      */     }
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
/*      */   public final JsonGenerator configure(Feature f, boolean state) {
/*  321 */     if (state) { enable(f); } else { disable(f); }
/*  322 */      return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isEnabled(Feature paramFeature);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(StreamWriteFeature f) {
/*  335 */     return isEnabled(f.mappedFeature());
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
/*      */ 
/*      */   
/*      */   public abstract int getFeatureMask();
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
/*      */   @Deprecated
/*      */   public abstract JsonGenerator setFeatureMask(int paramInt);
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
/*      */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/*  379 */     int oldState = getFeatureMask();
/*  380 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  381 */     return setFeatureMask(newState);
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
/*      */   public int getFormatFeatures() {
/*  393 */     return 0;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator overrideFormatFeatures(int values, int mask) {
/*  413 */     return this;
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
/*      */   public void setSchema(FormatSchema schema) {
/*  438 */     throw new UnsupportedOperationException("Generator of type " + getClass().getName() + " does not support schema of type '" + schema
/*  439 */         .getSchemaType() + "'");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FormatSchema getSchema() {
/*  448 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
/*  468 */     this._cfgPrettyPrinter = pp;
/*  469 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PrettyPrinter getPrettyPrinter() {
/*  479 */     return this._cfgPrettyPrinter;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract JsonGenerator useDefaultPrettyPrinter();
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
/*      */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/*  512 */     return this;
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
/*      */   public int getHighestEscapedChar() {
/*  526 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public CharacterEscapes getCharacterEscapes() {
/*  532 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/*  540 */     return this;
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
/*      */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/*  554 */     throw new UnsupportedOperationException();
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
/*      */   public Object getOutputTarget() {
/*  579 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOutputBuffered() {
/*  601 */     return -1;
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
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getCurrentValue() {
/*  618 */     JsonStreamContext ctxt = getOutputContext();
/*  619 */     return (ctxt == null) ? null : ctxt.getCurrentValue();
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
/*      */   public void setCurrentValue(Object v) {
/*  631 */     JsonStreamContext ctxt = getOutputContext();
/*  632 */     if (ctxt != null) {
/*  633 */       ctxt.setCurrentValue(v);
/*      */     }
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
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canUseSchema(FormatSchema schema) {
/*  651 */     return false;
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
/*      */ 
/*      */   
/*      */   public boolean canWriteObjectId() {
/*  667 */     return false;
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
/*      */ 
/*      */   
/*      */   public boolean canWriteTypeId() {
/*  683 */     return false;
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
/*      */   public boolean canWriteBinaryNatively() {
/*  695 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canOmitFields() {
/*  705 */     return true;
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
/*      */   public boolean canWriteFormattedNumbers() {
/*  719 */     return false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeStartArray() throws IOException;
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
/*      */   public void writeStartArray(int size) throws IOException {
/*  755 */     writeStartArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue) throws IOException {
/*  762 */     writeStartArray();
/*  763 */     setCurrentValue(forValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue, int size) throws IOException {
/*  770 */     writeStartArray(size);
/*  771 */     setCurrentValue(forValue);
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
/*      */   
/*      */   public abstract void writeEndArray() throws IOException;
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
/*      */   public abstract void writeStartObject() throws IOException;
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
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  810 */     writeStartObject();
/*  811 */     setCurrentValue(forValue);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue, int size) throws IOException {
/*  832 */     writeStartObject();
/*  833 */     setCurrentValue(forValue);
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
/*      */   public abstract void writeEndObject() throws IOException;
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
/*      */   public abstract void writeFieldName(String paramString) throws IOException;
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
/*      */   public abstract void writeFieldName(SerializableString paramSerializableString) throws IOException;
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
/*      */   public void writeFieldId(long id) throws IOException {
/*  883 */     writeFieldName(Long.toString(id));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeArray(int[] array, int offset, int length) throws IOException {
/*  905 */     if (array == null) {
/*  906 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  908 */     _verifyOffsets(array.length, offset, length);
/*  909 */     writeStartArray();
/*  910 */     for (int i = offset, end = offset + length; i < end; i++) {
/*  911 */       writeNumber(array[i]);
/*      */     }
/*  913 */     writeEndArray();
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
/*      */ 
/*      */   
/*      */   public void writeArray(long[] array, int offset, int length) throws IOException {
/*  929 */     if (array == null) {
/*  930 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  932 */     _verifyOffsets(array.length, offset, length);
/*  933 */     writeStartArray();
/*  934 */     for (int i = offset, end = offset + length; i < end; i++) {
/*  935 */       writeNumber(array[i]);
/*      */     }
/*  937 */     writeEndArray();
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
/*      */ 
/*      */   
/*      */   public void writeArray(double[] array, int offset, int length) throws IOException {
/*  953 */     if (array == null) {
/*  954 */       throw new IllegalArgumentException("null array");
/*      */     }
/*  956 */     _verifyOffsets(array.length, offset, length);
/*  957 */     writeStartArray();
/*  958 */     for (int i = offset, end = offset + length; i < end; i++) {
/*  959 */       writeNumber(array[i]);
/*      */     }
/*  961 */     writeEndArray();
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
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeString(String paramString) throws IOException;
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
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  993 */     _reportUnsupportedOperation();
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeString(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public abstract void writeString(SerializableString paramSerializableString) throws IOException;
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
/*      */   public abstract void writeRawUTF8String(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public abstract void writeUTF8String(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public abstract void writeRaw(String paramString) throws IOException;
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
/*      */   public abstract void writeRaw(String paramString, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public abstract void writeRaw(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public abstract void writeRaw(char paramChar) throws IOException;
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
/*      */   public void writeRaw(SerializableString raw) throws IOException {
/* 1138 */     writeRaw(raw.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(String paramString, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(SerializableString raw) throws IOException {
/* 1163 */     writeRawValue(raw.getValue());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeBinary(Base64Variant paramBase64Variant, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
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
/*      */   public void writeBinary(byte[] data, int offset, int len) throws IOException {
/* 1196 */     writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinary(byte[] data) throws IOException {
/* 1206 */     writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(InputStream data, int dataLength) throws IOException {
/* 1224 */     return writeBinary(Base64Variants.getDefaultVariant(), data, dataLength);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int writeBinary(Base64Variant paramBase64Variant, InputStream paramInputStream, int paramInt) throws IOException;
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
/*      */   public void writeNumber(short v) throws IOException {
/* 1268 */     writeNumber(v);
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
/*      */   
/*      */   public abstract void writeNumber(int paramInt) throws IOException;
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
/*      */   public abstract void writeNumber(long paramLong) throws IOException;
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
/*      */   public abstract void writeNumber(BigInteger paramBigInteger) throws IOException;
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
/*      */   public abstract void writeNumber(double paramDouble) throws IOException;
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
/*      */   public abstract void writeNumber(float paramFloat) throws IOException;
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
/*      */   public abstract void writeNumber(BigDecimal paramBigDecimal) throws IOException;
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
/*      */   public abstract void writeNumber(String paramString) throws IOException;
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
/*      */   public abstract void writeBoolean(boolean paramBoolean) throws IOException;
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
/*      */   public abstract void writeNull() throws IOException;
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
/*      */   public void writeEmbeddedObject(Object object) throws IOException {
/* 1392 */     if (object == null) {
/* 1393 */       writeNull();
/*      */       return;
/*      */     } 
/* 1396 */     if (object instanceof byte[]) {
/* 1397 */       writeBinary((byte[])object);
/*      */       return;
/*      */     } 
/* 1400 */     throw new JsonGenerationException("No native support for writing embedded objects of type " + object
/* 1401 */         .getClass().getName(), this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeObjectId(Object id) throws IOException {
/* 1423 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
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
/*      */   public void writeObjectRef(Object id) throws IOException {
/* 1436 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
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
/*      */   
/*      */   public void writeTypeId(Object id) throws IOException {
/* 1451 */     throw new JsonGenerationException("No native support for writing Type Ids", this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public WritableTypeId writeTypePrefix(WritableTypeId typeIdDef) throws IOException {
/* 1470 */     Object id = typeIdDef.id;
/*      */     
/* 1472 */     JsonToken valueShape = typeIdDef.valueShape;
/* 1473 */     if (canWriteTypeId()) {
/* 1474 */       typeIdDef.wrapperWritten = false;
/*      */       
/* 1476 */       writeTypeId(id);
/*      */     }
/*      */     else {
/*      */       
/* 1480 */       String idStr = (id instanceof String) ? (String)id : String.valueOf(id);
/* 1481 */       typeIdDef.wrapperWritten = true;
/*      */       
/* 1483 */       WritableTypeId.Inclusion incl = typeIdDef.include;
/*      */       
/* 1485 */       if (valueShape != JsonToken.START_OBJECT && incl
/* 1486 */         .requiresObjectContext()) {
/* 1487 */         typeIdDef.include = incl = WritableTypeId.Inclusion.WRAPPER_ARRAY;
/*      */       }
/*      */       
/* 1490 */       switch (incl) {
/*      */         case PARENT_PROPERTY:
/*      */         case PAYLOAD_PROPERTY:
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case METADATA_PROPERTY:
/* 1502 */           writeStartObject(typeIdDef.forValue);
/* 1503 */           writeStringField(typeIdDef.asProperty, idStr);
/* 1504 */           return typeIdDef;
/*      */ 
/*      */         
/*      */         case WRAPPER_OBJECT:
/* 1508 */           writeStartObject();
/* 1509 */           writeFieldName(idStr);
/*      */           break;
/*      */         
/*      */         default:
/* 1513 */           writeStartArray();
/* 1514 */           writeString(idStr);
/*      */           break;
/*      */       } 
/*      */     } 
/* 1518 */     if (valueShape == JsonToken.START_OBJECT) {
/* 1519 */       writeStartObject(typeIdDef.forValue);
/* 1520 */     } else if (valueShape == JsonToken.START_ARRAY) {
/*      */       
/* 1522 */       writeStartArray();
/*      */     } 
/* 1524 */     return typeIdDef;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public WritableTypeId writeTypeSuffix(WritableTypeId typeIdDef) throws IOException {
/* 1532 */     JsonToken valueShape = typeIdDef.valueShape;
/*      */     
/* 1534 */     if (valueShape == JsonToken.START_OBJECT) {
/* 1535 */       writeEndObject();
/* 1536 */     } else if (valueShape == JsonToken.START_ARRAY) {
/* 1537 */       writeEndArray();
/*      */     } 
/*      */     
/* 1540 */     if (typeIdDef.wrapperWritten) {
/* 1541 */       Object id; String idStr; switch (typeIdDef.include) {
/*      */         case WRAPPER_ARRAY:
/* 1543 */           writeEndArray();
/*      */ 
/*      */ 
/*      */         
/*      */         case PARENT_PROPERTY:
/* 1548 */           id = typeIdDef.id;
/* 1549 */           idStr = (id instanceof String) ? (String)id : String.valueOf(id);
/* 1550 */           writeStringField(typeIdDef.asProperty, idStr);
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
/*      */         case PAYLOAD_PROPERTY:
/*      */         case METADATA_PROPERTY:
/* 1563 */           return typeIdDef;
/*      */       } 
/*      */       writeEndObject();
/*      */     } 
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
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeObject(Object paramObject) throws IOException;
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
/*      */   public abstract void writeTree(TreeNode paramTreeNode) throws IOException;
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
/*      */   public void writeStringField(String fieldName, String value) throws IOException {
/* 1612 */     writeFieldName(fieldName);
/* 1613 */     writeString(value);
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
/*      */   public final void writeBooleanField(String fieldName, boolean value) throws IOException {
/* 1625 */     writeFieldName(fieldName);
/* 1626 */     writeBoolean(value);
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
/*      */   public final void writeNullField(String fieldName) throws IOException {
/* 1638 */     writeFieldName(fieldName);
/* 1639 */     writeNull();
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
/*      */   public final void writeNumberField(String fieldName, int value) throws IOException {
/* 1651 */     writeFieldName(fieldName);
/* 1652 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, long value) throws IOException {
/* 1664 */     writeFieldName(fieldName);
/* 1665 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, double value) throws IOException {
/* 1677 */     writeFieldName(fieldName);
/* 1678 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, float value) throws IOException {
/* 1690 */     writeFieldName(fieldName);
/* 1691 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, BigDecimal value) throws IOException {
/* 1704 */     writeFieldName(fieldName);
/* 1705 */     writeNumber(value);
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
/*      */   public final void writeBinaryField(String fieldName, byte[] data) throws IOException {
/* 1718 */     writeFieldName(fieldName);
/* 1719 */     writeBinary(data);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeArrayFieldStart(String fieldName) throws IOException {
/* 1736 */     writeFieldName(fieldName);
/* 1737 */     writeStartArray();
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
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeObjectFieldStart(String fieldName) throws IOException {
/* 1754 */     writeFieldName(fieldName);
/* 1755 */     writeStartObject();
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
/*      */   public final void writeObjectField(String fieldName, Object pojo) throws IOException {
/* 1768 */     writeFieldName(fieldName);
/* 1769 */     writeObject(pojo);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeOmittedField(String fieldName) throws IOException {}
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
/*      */   public void copyCurrentEvent(JsonParser p) throws IOException {
/*      */     JsonParser.NumberType n;
/* 1801 */     JsonToken t = p.currentToken();
/* 1802 */     int token = (t == null) ? -1 : t.id();
/* 1803 */     switch (token) {
/*      */       case -1:
/* 1805 */         _reportError("No current event to copy");
/*      */         return;
/*      */       case 1:
/* 1808 */         writeStartObject();
/*      */         return;
/*      */       case 2:
/* 1811 */         writeEndObject();
/*      */         return;
/*      */       case 3:
/* 1814 */         writeStartArray();
/*      */         return;
/*      */       case 4:
/* 1817 */         writeEndArray();
/*      */         return;
/*      */       case 5:
/* 1820 */         writeFieldName(p.getCurrentName());
/*      */         return;
/*      */       case 6:
/* 1823 */         if (p.hasTextCharacters()) {
/* 1824 */           writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */         } else {
/* 1826 */           writeString(p.getText());
/*      */         } 
/*      */         return;
/*      */       
/*      */       case 7:
/* 1831 */         n = p.getNumberType();
/* 1832 */         if (n == JsonParser.NumberType.INT) {
/* 1833 */           writeNumber(p.getIntValue());
/* 1834 */         } else if (n == JsonParser.NumberType.BIG_INTEGER) {
/* 1835 */           writeNumber(p.getBigIntegerValue());
/*      */         } else {
/* 1837 */           writeNumber(p.getLongValue());
/*      */         } 
/*      */         return;
/*      */ 
/*      */       
/*      */       case 8:
/* 1843 */         n = p.getNumberType();
/* 1844 */         if (n == JsonParser.NumberType.BIG_DECIMAL) {
/* 1845 */           writeNumber(p.getDecimalValue());
/* 1846 */         } else if (n == JsonParser.NumberType.FLOAT) {
/* 1847 */           writeNumber(p.getFloatValue());
/*      */         } else {
/* 1849 */           writeNumber(p.getDoubleValue());
/*      */         } 
/*      */         return;
/*      */       
/*      */       case 9:
/* 1854 */         writeBoolean(true);
/*      */         return;
/*      */       case 10:
/* 1857 */         writeBoolean(false);
/*      */         return;
/*      */       case 11:
/* 1860 */         writeNull();
/*      */         return;
/*      */       case 12:
/* 1863 */         writeObject(p.getEmbeddedObject());
/*      */         return;
/*      */     } 
/* 1866 */     throw new IllegalStateException("Internal error: unknown current token, " + t);
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
/*      */   public void copyCurrentStructure(JsonParser p) throws IOException {
/* 1902 */     JsonToken t = p.currentToken();
/*      */     
/* 1904 */     int id = (t == null) ? -1 : t.id();
/* 1905 */     if (id == 5) {
/* 1906 */       writeFieldName(p.getCurrentName());
/* 1907 */       t = p.nextToken();
/* 1908 */       id = (t == null) ? -1 : t.id();
/*      */     } 
/*      */     
/* 1911 */     switch (id) {
/*      */       case 1:
/* 1913 */         writeStartObject();
/* 1914 */         _copyCurrentContents(p);
/*      */         return;
/*      */       case 3:
/* 1917 */         writeStartArray();
/* 1918 */         _copyCurrentContents(p);
/*      */         return;
/*      */     } 
/*      */     
/* 1922 */     copyCurrentEvent(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _copyCurrentContents(JsonParser p) throws IOException {
/* 1931 */     int depth = 1;
/*      */     
/*      */     JsonToken t;
/*      */     
/* 1935 */     while ((t = p.nextToken()) != null) {
/* 1936 */       JsonParser.NumberType n; switch (t.id()) {
/*      */         case 5:
/* 1938 */           writeFieldName(p.getCurrentName());
/*      */           continue;
/*      */         
/*      */         case 3:
/* 1942 */           writeStartArray();
/* 1943 */           depth++;
/*      */           continue;
/*      */         
/*      */         case 1:
/* 1947 */           writeStartObject();
/* 1948 */           depth++;
/*      */           continue;
/*      */         
/*      */         case 4:
/* 1952 */           writeEndArray();
/* 1953 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */         case 2:
/* 1958 */           writeEndObject();
/* 1959 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */         
/*      */         case 6:
/* 1965 */           if (p.hasTextCharacters()) {
/* 1966 */             writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength()); continue;
/*      */           } 
/* 1968 */           writeString(p.getText());
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 7:
/* 1973 */           n = p.getNumberType();
/* 1974 */           if (n == JsonParser.NumberType.INT) {
/* 1975 */             writeNumber(p.getIntValue()); continue;
/* 1976 */           }  if (n == JsonParser.NumberType.BIG_INTEGER) {
/* 1977 */             writeNumber(p.getBigIntegerValue()); continue;
/*      */           } 
/* 1979 */           writeNumber(p.getLongValue());
/*      */           continue;
/*      */ 
/*      */ 
/*      */         
/*      */         case 8:
/* 1985 */           n = p.getNumberType();
/* 1986 */           if (n == JsonParser.NumberType.BIG_DECIMAL) {
/* 1987 */             writeNumber(p.getDecimalValue()); continue;
/* 1988 */           }  if (n == JsonParser.NumberType.FLOAT) {
/* 1989 */             writeNumber(p.getFloatValue()); continue;
/*      */           } 
/* 1991 */           writeNumber(p.getDoubleValue());
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 9:
/* 1996 */           writeBoolean(true);
/*      */           continue;
/*      */         case 10:
/* 1999 */           writeBoolean(false);
/*      */           continue;
/*      */         case 11:
/* 2002 */           writeNull();
/*      */           continue;
/*      */         case 12:
/* 2005 */           writeObject(p.getEmbeddedObject());
/*      */           continue;
/*      */       } 
/* 2008 */       throw new IllegalStateException("Internal error: unknown current token, " + t);
/*      */     } 
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
/*      */ 
/*      */   
/*      */   public abstract JsonStreamContext getOutputContext();
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
/*      */   public abstract void flush() throws IOException;
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
/*      */   public abstract boolean isClosed();
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
/*      */   public abstract void close() throws IOException;
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
/*      */   protected void _reportError(String msg) throws JsonGenerationException {
/* 2080 */     throw new JsonGenerationException(msg, this);
/*      */   }
/*      */   protected final void _throwInternal() {
/* 2083 */     VersionUtil.throwInternal();
/*      */   }
/*      */   protected void _reportUnsupportedOperation() {
/* 2086 */     throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyOffsets(int arrayLength, int offset, int length) {
/* 2094 */     if (offset < 0 || offset + length > arrayLength) {
/* 2095 */       throw new IllegalArgumentException(String.format("invalid argument(s) (offset=%d, length=%d) for input array of %d element", new Object[] {
/*      */               
/* 2097 */               Integer.valueOf(offset), Integer.valueOf(length), Integer.valueOf(arrayLength)
/*      */             }));
/*      */     }
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
/*      */   
/*      */   protected void _writeSimpleObject(Object value) throws IOException {
/* 2114 */     if (value == null) {
/* 2115 */       writeNull();
/*      */       return;
/*      */     } 
/* 2118 */     if (value instanceof String) {
/* 2119 */       writeString((String)value);
/*      */       return;
/*      */     } 
/* 2122 */     if (value instanceof Number) {
/* 2123 */       Number n = (Number)value;
/* 2124 */       if (n instanceof Integer) {
/* 2125 */         writeNumber(n.intValue()); return;
/*      */       } 
/* 2127 */       if (n instanceof Long) {
/* 2128 */         writeNumber(n.longValue()); return;
/*      */       } 
/* 2130 */       if (n instanceof Double) {
/* 2131 */         writeNumber(n.doubleValue()); return;
/*      */       } 
/* 2133 */       if (n instanceof Float) {
/* 2134 */         writeNumber(n.floatValue()); return;
/*      */       } 
/* 2136 */       if (n instanceof Short) {
/* 2137 */         writeNumber(n.shortValue()); return;
/*      */       } 
/* 2139 */       if (n instanceof Byte) {
/* 2140 */         writeNumber((short)n.byteValue()); return;
/*      */       } 
/* 2142 */       if (n instanceof BigInteger) {
/* 2143 */         writeNumber((BigInteger)n); return;
/*      */       } 
/* 2145 */       if (n instanceof BigDecimal) {
/* 2146 */         writeNumber((BigDecimal)n);
/*      */         
/*      */         return;
/*      */       } 
/* 2150 */       if (n instanceof AtomicInteger) {
/* 2151 */         writeNumber(((AtomicInteger)n).get()); return;
/*      */       } 
/* 2153 */       if (n instanceof AtomicLong) {
/* 2154 */         writeNumber(((AtomicLong)n).get()); return;
/*      */       } 
/*      */     } else {
/* 2157 */       if (value instanceof byte[]) {
/* 2158 */         writeBinary((byte[])value); return;
/*      */       } 
/* 2160 */       if (value instanceof Boolean) {
/* 2161 */         writeBoolean(((Boolean)value).booleanValue()); return;
/*      */       } 
/* 2163 */       if (value instanceof AtomicBoolean) {
/* 2164 */         writeBoolean(((AtomicBoolean)value).get()); return;
/*      */       } 
/*      */     } 
/* 2167 */     throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value
/* 2168 */         .getClass().getName() + ")");
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */