/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.FormatSchema;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonParserDelegate
/*     */   extends JsonParser
/*     */ {
/*     */   protected JsonParser delegate;
/*     */   
/*     */   public JsonParserDelegate(JsonParser d) {
/*  26 */     this.delegate = d;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/*  31 */     return this.delegate.getCurrentValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/*  36 */     this.delegate.setCurrentValue(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCodec(ObjectCodec c) {
/*  45 */     this.delegate.setCodec(c); } public ObjectCodec getCodec() {
/*  46 */     return this.delegate.getCodec();
/*     */   }
/*     */   
/*     */   public JsonParser enable(JsonParser.Feature f) {
/*  50 */     this.delegate.enable(f);
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser disable(JsonParser.Feature f) {
/*  56 */     this.delegate.disable(f);
/*  57 */     return this;
/*     */   }
/*     */   
/*  60 */   public boolean isEnabled(JsonParser.Feature f) { return this.delegate.isEnabled(f); } public int getFeatureMask() {
/*  61 */     return this.delegate.getFeatureMask();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonParser setFeatureMask(int mask) {
/*  66 */     this.delegate.setFeatureMask(mask);
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser overrideStdFeatures(int values, int mask) {
/*  72 */     this.delegate.overrideStdFeatures(values, mask);
/*  73 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser overrideFormatFeatures(int values, int mask) {
/*  78 */     this.delegate.overrideFormatFeatures(values, mask);
/*  79 */     return this;
/*     */   }
/*     */   
/*  82 */   public FormatSchema getSchema() { return this.delegate.getSchema(); }
/*  83 */   public void setSchema(FormatSchema schema) { this.delegate.setSchema(schema); }
/*  84 */   public boolean canUseSchema(FormatSchema schema) { return this.delegate.canUseSchema(schema); }
/*  85 */   public Version version() { return this.delegate.version(); } public Object getInputSource() {
/*  86 */     return this.delegate.getInputSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean requiresCustomCodec() {
/*  94 */     return this.delegate.requiresCustomCodec();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 102 */     this.delegate.close(); } public boolean isClosed() {
/* 103 */     return this.delegate.isClosed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken currentToken() {
/* 111 */     return this.delegate.currentToken(); } public int currentTokenId() {
/* 112 */     return this.delegate.currentTokenId();
/*     */   }
/* 114 */   public JsonToken getCurrentToken() { return this.delegate.getCurrentToken(); }
/* 115 */   public int getCurrentTokenId() { return this.delegate.getCurrentTokenId(); }
/* 116 */   public boolean hasCurrentToken() { return this.delegate.hasCurrentToken(); }
/* 117 */   public boolean hasTokenId(int id) { return this.delegate.hasTokenId(id); } public boolean hasToken(JsonToken t) {
/* 118 */     return this.delegate.hasToken(t);
/*     */   }
/* 120 */   public String getCurrentName() throws IOException { return this.delegate.getCurrentName(); }
/* 121 */   public JsonLocation getCurrentLocation() { return this.delegate.getCurrentLocation(); }
/* 122 */   public JsonStreamContext getParsingContext() { return this.delegate.getParsingContext(); }
/* 123 */   public boolean isExpectedStartArrayToken() { return this.delegate.isExpectedStartArrayToken(); }
/* 124 */   public boolean isExpectedStartObjectToken() { return this.delegate.isExpectedStartObjectToken(); } public boolean isNaN() throws IOException {
/* 125 */     return this.delegate.isNaN();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearCurrentToken() {
/* 133 */     this.delegate.clearCurrentToken();
/* 134 */   } public JsonToken getLastClearedToken() { return this.delegate.getLastClearedToken(); } public void overrideCurrentName(String name) {
/* 135 */     this.delegate.overrideCurrentName(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() throws IOException {
/* 143 */     return this.delegate.getText();
/* 144 */   } public boolean hasTextCharacters() { return this.delegate.hasTextCharacters(); }
/* 145 */   public char[] getTextCharacters() throws IOException { return this.delegate.getTextCharacters(); }
/* 146 */   public int getTextLength() throws IOException { return this.delegate.getTextLength(); }
/* 147 */   public int getTextOffset() throws IOException { return this.delegate.getTextOffset(); } public int getText(Writer writer) throws IOException, UnsupportedOperationException {
/* 148 */     return this.delegate.getText(writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getBigIntegerValue() throws IOException {
/* 157 */     return this.delegate.getBigIntegerValue();
/*     */   }
/*     */   public boolean getBooleanValue() throws IOException {
/* 160 */     return this.delegate.getBooleanValue();
/*     */   }
/*     */   public byte getByteValue() throws IOException {
/* 163 */     return this.delegate.getByteValue();
/*     */   }
/*     */   public short getShortValue() throws IOException {
/* 166 */     return this.delegate.getShortValue();
/*     */   }
/*     */   public BigDecimal getDecimalValue() throws IOException {
/* 169 */     return this.delegate.getDecimalValue();
/*     */   }
/*     */   public double getDoubleValue() throws IOException {
/* 172 */     return this.delegate.getDoubleValue();
/*     */   }
/*     */   public float getFloatValue() throws IOException {
/* 175 */     return this.delegate.getFloatValue();
/*     */   }
/*     */   public int getIntValue() throws IOException {
/* 178 */     return this.delegate.getIntValue();
/*     */   }
/*     */   public long getLongValue() throws IOException {
/* 181 */     return this.delegate.getLongValue();
/*     */   }
/*     */   public JsonParser.NumberType getNumberType() throws IOException {
/* 184 */     return this.delegate.getNumberType();
/*     */   }
/*     */   public Number getNumberValue() throws IOException {
/* 187 */     return this.delegate.getNumberValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt() throws IOException {
/* 195 */     return this.delegate.getValueAsInt();
/* 196 */   } public int getValueAsInt(int defaultValue) throws IOException { return this.delegate.getValueAsInt(defaultValue); }
/* 197 */   public long getValueAsLong() throws IOException { return this.delegate.getValueAsLong(); }
/* 198 */   public long getValueAsLong(long defaultValue) throws IOException { return this.delegate.getValueAsLong(defaultValue); }
/* 199 */   public double getValueAsDouble() throws IOException { return this.delegate.getValueAsDouble(); }
/* 200 */   public double getValueAsDouble(double defaultValue) throws IOException { return this.delegate.getValueAsDouble(defaultValue); }
/* 201 */   public boolean getValueAsBoolean() throws IOException { return this.delegate.getValueAsBoolean(); }
/* 202 */   public boolean getValueAsBoolean(boolean defaultValue) throws IOException { return this.delegate.getValueAsBoolean(defaultValue); }
/* 203 */   public String getValueAsString() throws IOException { return this.delegate.getValueAsString(); } public String getValueAsString(String defaultValue) throws IOException {
/* 204 */     return this.delegate.getValueAsString(defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmbeddedObject() throws IOException {
/* 212 */     return this.delegate.getEmbeddedObject();
/* 213 */   } public byte[] getBinaryValue(Base64Variant b64variant) throws IOException { return this.delegate.getBinaryValue(b64variant); }
/* 214 */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException { return this.delegate.readBinaryValue(b64variant, out); } public JsonLocation getTokenLocation() {
/* 215 */     return this.delegate.getTokenLocation();
/*     */   } public JsonToken nextToken() throws IOException {
/* 217 */     return this.delegate.nextToken();
/*     */   } public JsonToken nextValue() throws IOException {
/* 219 */     return this.delegate.nextValue();
/*     */   } public void finishToken() throws IOException {
/* 221 */     this.delegate.finishToken();
/*     */   }
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 225 */     this.delegate.skipChildren();
/*     */     
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canReadObjectId() {
/* 236 */     return this.delegate.canReadObjectId();
/* 237 */   } public boolean canReadTypeId() { return this.delegate.canReadTypeId(); }
/* 238 */   public Object getObjectId() throws IOException { return this.delegate.getObjectId(); } public Object getTypeId() throws IOException {
/* 239 */     return this.delegate.getTypeId();
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
/*     */   public JsonParser delegate() {
/* 252 */     return this.delegate;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\JsonParserDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */