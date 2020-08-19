/*     */ package com.fasterxml.jackson.core.base;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.json.DupDetector;
/*     */ import com.fasterxml.jackson.core.json.JsonWriteContext;
/*     */ import com.fasterxml.jackson.core.json.PackageVersion;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GeneratorBase
/*     */   extends JsonGenerator
/*     */ {
/*     */   public static final int SURR1_FIRST = 55296;
/*     */   public static final int SURR1_LAST = 56319;
/*     */   public static final int SURR2_FIRST = 56320;
/*     */   public static final int SURR2_LAST = 57343;
/*  32 */   protected static final int DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
/*  33 */     .getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII
/*  34 */     .getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION
/*  35 */     .getMask();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_BINARY = "write a binary value";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_BOOLEAN = "write a boolean value";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_NULL = "write a null";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_NUMBER = "write a number";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_RAW = "write a raw (unencoded) value";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_STRING = "write a string";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectCodec _objectCodec;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _features;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _cfgNumbersAsStrings;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonWriteContext _writeContext;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _closed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GeneratorBase(int features, ObjectCodec codec) {
/* 107 */     this._features = features;
/* 108 */     this._objectCodec = codec;
/*     */     
/* 110 */     DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/* 111 */     this._writeContext = JsonWriteContext.createRootContext(dups);
/* 112 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
/* 121 */     this._features = features;
/* 122 */     this._objectCodec = codec;
/* 123 */     this._writeContext = ctxt;
/* 124 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version version() {
/* 132 */     return PackageVersion.VERSION;
/*     */   }
/*     */   
/*     */   public Object getCurrentValue() {
/* 136 */     return this._writeContext.getCurrentValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 141 */     if (this._writeContext != null) {
/* 142 */       this._writeContext.setCurrentValue(v);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnabled(JsonGenerator.Feature f) {
/* 153 */     return ((this._features & f.getMask()) != 0); } public int getFeatureMask() {
/* 154 */     return this._features;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 161 */     int mask = f.getMask();
/* 162 */     this._features |= mask;
/* 163 */     if ((mask & DERIVED_FEATURES_MASK) != 0)
/*     */     {
/* 165 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 166 */         this._cfgNumbersAsStrings = true;
/* 167 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 168 */         setHighestNonEscapedChar(127);
/* 169 */       } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION && 
/* 170 */         this._writeContext.getDupDetector() == null) {
/* 171 */         this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */       } 
/*     */     }
/*     */     
/* 175 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 181 */     int mask = f.getMask();
/* 182 */     this._features &= mask ^ 0xFFFFFFFF;
/* 183 */     if ((mask & DERIVED_FEATURES_MASK) != 0) {
/* 184 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 185 */         this._cfgNumbersAsStrings = false;
/* 186 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 187 */         setHighestNonEscapedChar(0);
/* 188 */       } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
/* 189 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       } 
/*     */     }
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator setFeatureMask(int newMask) {
/* 198 */     int changed = newMask ^ this._features;
/* 199 */     this._features = newMask;
/* 200 */     if (changed != 0) {
/* 201 */       _checkStdFeatureChanges(newMask, changed);
/*     */     }
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/* 208 */     int oldState = this._features;
/* 209 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/* 210 */     int changed = oldState ^ newState;
/* 211 */     if (changed != 0) {
/* 212 */       this._features = newState;
/* 213 */       _checkStdFeatureChanges(newState, changed);
/*     */     } 
/* 215 */     return this;
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
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/* 230 */     if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
/*     */       return;
/*     */     }
/* 233 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
/* 234 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
/* 235 */       if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
/* 236 */         setHighestNonEscapedChar(127);
/*     */       } else {
/* 238 */         setHighestNonEscapedChar(0);
/*     */       } 
/*     */     }
/* 241 */     if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
/* 242 */       if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
/* 243 */         if (this._writeContext.getDupDetector() == null) {
/* 244 */           this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */         }
/*     */       } else {
/* 247 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter() {
/* 254 */     if (getPrettyPrinter() != null) {
/* 255 */       return this;
/*     */     }
/* 257 */     return setPrettyPrinter(_constructDefaultPrettyPrinter());
/*     */   }
/*     */   
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/* 261 */     this._objectCodec = oc;
/* 262 */     return this;
/*     */   }
/*     */   public ObjectCodec getCodec() {
/* 265 */     return this._objectCodec;
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
/*     */   public JsonStreamContext getOutputContext() {
/* 278 */     return (JsonStreamContext)this._writeContext;
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
/*     */   public void writeStartObject(Object forValue) throws IOException {
/* 294 */     writeStartObject();
/* 295 */     if (forValue != null) {
/* 296 */       setCurrentValue(forValue);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFieldName(SerializableString name) throws IOException {
/* 307 */     writeFieldName(name.getValue());
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
/*     */   public void writeString(SerializableString text) throws IOException {
/* 322 */     writeString(text.getValue());
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text) throws IOException {
/* 326 */     _verifyValueWrite("write raw value");
/* 327 */     writeRaw(text);
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 331 */     _verifyValueWrite("write raw value");
/* 332 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 336 */     _verifyValueWrite("write raw value");
/* 337 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(SerializableString text) throws IOException {
/* 341 */     _verifyValueWrite("write raw value");
/* 342 */     writeRaw(text);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/* 348 */     _reportUnsupportedOperation();
/* 349 */     return 0;
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
/*     */   public void writeObject(Object value) throws IOException {
/* 378 */     if (value == null) {
/*     */       
/* 380 */       writeNull();
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 387 */       if (this._objectCodec != null) {
/* 388 */         this._objectCodec.writeValue(this, value);
/*     */         return;
/*     */       } 
/* 391 */       _writeSimpleObject(value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTree(TreeNode rootNode) throws IOException {
/* 398 */     if (rootNode == null) {
/* 399 */       writeNull();
/*     */     } else {
/* 401 */       if (this._objectCodec == null) {
/* 402 */         throw new IllegalStateException("No ObjectCodec defined");
/*     */       }
/* 404 */       this._objectCodec.writeValue(this, rootNode);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void flush() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 415 */     this._closed = true; } public boolean isClosed() {
/* 416 */     return this._closed;
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
/*     */   protected abstract void _releaseBuffers();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void _verifyValueWrite(String paramString) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PrettyPrinter _constructDefaultPrettyPrinter() {
/* 447 */     return (PrettyPrinter)new DefaultPrettyPrinter();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _asString(BigDecimal value) throws IOException {
/* 457 */     if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
/*     */       
/* 459 */       int scale = value.scale();
/* 460 */       if (scale < -9999 || scale > 9999)
/* 461 */         _reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", new Object[] {
/*     */                 
/* 463 */                 Integer.valueOf(scale), Integer.valueOf(9999), Integer.valueOf(9999)
/*     */               })); 
/* 465 */       return value.toPlainString();
/*     */     } 
/* 467 */     return value.toString();
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
/*     */   protected final int _decodeSurrogate(int surr1, int surr2) throws IOException {
/* 482 */     if (surr2 < 56320 || surr2 > 57343) {
/* 483 */       String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
/* 484 */       _reportError(msg);
/*     */     } 
/* 486 */     int c = 65536 + (surr1 - 55296 << 10) + surr2 - 56320;
/* 487 */     return c;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\base\GeneratorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */