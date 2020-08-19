/*      */ package com.fasterxml.jackson.core;
/*      */ 
/*      */ import com.fasterxml.jackson.core.format.InputAccessor;
/*      */ import com.fasterxml.jackson.core.format.MatchStrength;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.InputDecorator;
/*      */ import com.fasterxml.jackson.core.io.OutputDecorator;
/*      */ import com.fasterxml.jackson.core.io.SerializedString;
/*      */ import com.fasterxml.jackson.core.io.UTF8Writer;
/*      */ import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
/*      */ import com.fasterxml.jackson.core.json.PackageVersion;
/*      */ import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
/*      */ import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
/*      */ import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
/*      */ import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
/*      */ import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.BufferRecycler;
/*      */ import com.fasterxml.jackson.core.util.BufferRecyclers;
/*      */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*      */ import java.io.CharArrayReader;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataOutput;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringReader;
/*      */ import java.io.Writer;
/*      */ import java.net.URL;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JsonFactory
/*      */   extends TokenStreamFactory
/*      */   implements Versioned, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2L;
/*      */   public static final String FORMAT_NAME_JSON = "JSON";
/*      */   
/*      */   public enum Feature
/*      */   {
/*   79 */     INTERN_FIELD_NAMES(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   89 */     CANONICALIZE_FIELD_NAMES(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  105 */     FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  122 */     USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean _defaultState;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int collectDefaults() {
/*  136 */       int flags = 0;
/*  137 */       for (Feature f : values()) {
/*  138 */         if (f.enabledByDefault()) flags |= f.getMask(); 
/*      */       } 
/*  140 */       return flags;
/*      */     }
/*      */     Feature(boolean defaultState) {
/*  143 */       this._defaultState = defaultState;
/*      */     }
/*  145 */     public boolean enabledByDefault() { return this._defaultState; }
/*  146 */     public boolean enabledIn(int flags) { return ((flags & getMask()) != 0); } public int getMask() {
/*  147 */       return 1 << ordinal();
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
/*  165 */   protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  171 */   protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  177 */   protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*      */   
/*  179 */   public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = (SerializableString)DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final char DEFAULT_QUOTE_CHAR = '"';
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  197 */   protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  208 */   protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  219 */   protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  224 */   protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  229 */   protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectCodec _objectCodec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected CharacterEscapes _characterEscapes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InputDecorator _inputDecorator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected OutputDecorator _outputDecorator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  272 */   protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _maximumNonEscapedChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final char _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory() {
/*  307 */     this((ObjectCodec)null);
/*      */   }
/*      */   public JsonFactory(ObjectCodec oc) {
/*  310 */     this._objectCodec = oc;
/*  311 */     this._quoteChar = '"';
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/*  321 */     this._objectCodec = codec;
/*      */ 
/*      */     
/*  324 */     this._factoryFeatures = src._factoryFeatures;
/*  325 */     this._parserFeatures = src._parserFeatures;
/*  326 */     this._generatorFeatures = src._generatorFeatures;
/*  327 */     this._inputDecorator = src._inputDecorator;
/*  328 */     this._outputDecorator = src._outputDecorator;
/*      */ 
/*      */     
/*  331 */     this._characterEscapes = src._characterEscapes;
/*  332 */     this._rootValueSeparator = src._rootValueSeparator;
/*  333 */     this._maximumNonEscapedChar = src._maximumNonEscapedChar;
/*  334 */     this._quoteChar = src._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory(JsonFactoryBuilder b) {
/*  343 */     this._objectCodec = null;
/*      */ 
/*      */     
/*  346 */     this._factoryFeatures = b._factoryFeatures;
/*  347 */     this._parserFeatures = b._streamReadFeatures;
/*  348 */     this._generatorFeatures = b._streamWriteFeatures;
/*  349 */     this._inputDecorator = b._inputDecorator;
/*  350 */     this._outputDecorator = b._outputDecorator;
/*      */ 
/*      */     
/*  353 */     this._characterEscapes = b._characterEscapes;
/*  354 */     this._rootValueSeparator = b._rootValueSeparator;
/*  355 */     this._maximumNonEscapedChar = b._maximumNonEscapedChar;
/*  356 */     this._quoteChar = b._quoteChar;
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
/*      */   protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
/*  368 */     this._objectCodec = null;
/*      */     
/*  370 */     this._factoryFeatures = b._factoryFeatures;
/*  371 */     this._parserFeatures = b._streamReadFeatures;
/*  372 */     this._generatorFeatures = b._streamWriteFeatures;
/*  373 */     this._inputDecorator = b._inputDecorator;
/*  374 */     this._outputDecorator = b._outputDecorator;
/*      */ 
/*      */     
/*  377 */     this._characterEscapes = null;
/*  378 */     this._rootValueSeparator = null;
/*  379 */     this._maximumNonEscapedChar = 0;
/*  380 */     this._quoteChar = '"';
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TSFBuilder<?, ?> rebuild() {
/*  391 */     _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
/*  392 */     return new JsonFactoryBuilder(this);
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
/*      */   public static TSFBuilder<?, ?> builder() {
/*  405 */     return new JsonFactoryBuilder();
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
/*      */   public JsonFactory copy() {
/*  424 */     _checkInvalidCopy(JsonFactory.class);
/*      */     
/*  426 */     return new JsonFactory(this, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _checkInvalidCopy(Class<?> exp) {
/*  434 */     if (getClass() != exp) {
/*  435 */       throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + 
/*  436 */           version() + ") does not override copy(); it has to");
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
/*      */   protected Object readResolve() {
/*  452 */     return new JsonFactory(this, this._objectCodec);
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
/*      */   public boolean requiresPropertyOrdering() {
/*  477 */     return false;
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
/*      */   public boolean canHandleBinaryNatively() {
/*  492 */     return false;
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
/*      */   public boolean canUseCharArrays() {
/*  506 */     return true;
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
/*      */   public boolean canParseAsync() {
/*  520 */     return _isJSONFactory();
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<? extends FormatFeature> getFormatReadFeatureType() {
/*  525 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/*  530 */     return null;
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
/*      */   public boolean canUseSchema(FormatSchema schema) {
/*  551 */     if (schema == null) {
/*  552 */       return false;
/*      */     }
/*  554 */     String ourFormat = getFormatName();
/*  555 */     return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
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
/*      */   public String getFormatName() {
/*  572 */     if (getClass() == JsonFactory.class) {
/*  573 */       return "JSON";
/*      */     }
/*  575 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/*  585 */     if (getClass() == JsonFactory.class) {
/*  586 */       return hasJSONFormat(acc);
/*      */     }
/*  588 */     return null;
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
/*      */   public boolean requiresCustomCodec() {
/*  605 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/*  614 */     return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version version() {
/*  625 */     return PackageVersion.VERSION;
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
/*      */   @Deprecated
/*      */   public final JsonFactory configure(Feature f, boolean state) {
/*  642 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonFactory enable(Feature f) {
/*  653 */     this._factoryFeatures |= f.getMask();
/*  654 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonFactory disable(Feature f) {
/*  665 */     this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  666 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(Feature f) {
/*  673 */     return ((this._factoryFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int getParserFeatures() {
/*  678 */     return this._parserFeatures;
/*      */   }
/*      */ 
/*      */   
/*      */   public final int getGeneratorFeatures() {
/*  683 */     return this._generatorFeatures;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFormatParserFeatures() {
/*  689 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFormatGeneratorFeatures() {
/*  695 */     return 0;
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
/*      */   public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/*  709 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory enable(JsonParser.Feature f) {
/*  717 */     this._parserFeatures |= f.getMask();
/*  718 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory disable(JsonParser.Feature f) {
/*  726 */     this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  727 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(JsonParser.Feature f) {
/*  735 */     return ((this._parserFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(StreamReadFeature f) {
/*  742 */     return ((this._parserFeatures & f.mappedFeature().getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputDecorator getInputDecorator() {
/*  750 */     return this._inputDecorator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonFactory setInputDecorator(InputDecorator d) {
/*  760 */     this._inputDecorator = d;
/*  761 */     return this;
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
/*      */   public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/*  775 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory enable(JsonGenerator.Feature f) {
/*  783 */     this._generatorFeatures |= f.getMask();
/*  784 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory disable(JsonGenerator.Feature f) {
/*  792 */     this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  793 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(JsonGenerator.Feature f) {
/*  801 */     return ((this._generatorFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(StreamWriteFeature f) {
/*  808 */     return ((this._generatorFeatures & f.mappedFeature().getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CharacterEscapes getCharacterEscapes() {
/*  815 */     return this._characterEscapes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/*  822 */     this._characterEscapes = esc;
/*  823 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputDecorator getOutputDecorator() {
/*  831 */     return this._outputDecorator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonFactory setOutputDecorator(OutputDecorator d) {
/*  841 */     this._outputDecorator = d;
/*  842 */     return this;
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
/*      */   public JsonFactory setRootValueSeparator(String sep) {
/*  855 */     this._rootValueSeparator = (sep == null) ? null : (SerializableString)new SerializedString(sep);
/*  856 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRootValueSeparator() {
/*  863 */     return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
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
/*      */   public JsonFactory setCodec(ObjectCodec oc) {
/*  880 */     this._objectCodec = oc;
/*  881 */     return this;
/*      */   }
/*      */   public ObjectCodec getCodec() {
/*  884 */     return this._objectCodec;
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
/*      */   public JsonParser createParser(File f) throws IOException, JsonParseException {
/*  915 */     IOContext ctxt = _createContext(f, true);
/*  916 */     InputStream in = new FileInputStream(f);
/*  917 */     return _createParser(_decorate(in, ctxt), ctxt);
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
/*      */   public JsonParser createParser(URL url) throws IOException, JsonParseException {
/*  943 */     IOContext ctxt = _createContext(url, true);
/*  944 */     InputStream in = _optimizedStreamFromURL(url);
/*  945 */     return _createParser(_decorate(in, ctxt), ctxt);
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
/*      */   public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/*  971 */     IOContext ctxt = _createContext(in, false);
/*  972 */     return _createParser(_decorate(in, ctxt), ctxt);
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
/*      */   public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/*  992 */     IOContext ctxt = _createContext(r, false);
/*  993 */     return _createParser(_decorate(r, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 1004 */     IOContext ctxt = _createContext(data, true);
/* 1005 */     if (this._inputDecorator != null) {
/* 1006 */       InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 1007 */       if (in != null) {
/* 1008 */         return _createParser(in, ctxt);
/*      */       }
/*      */     } 
/* 1011 */     return _createParser(data, 0, data.length, ctxt);
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
/*      */   public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 1026 */     IOContext ctxt = _createContext(data, true);
/*      */     
/* 1028 */     if (this._inputDecorator != null) {
/* 1029 */       InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 1030 */       if (in != null) {
/* 1031 */         return _createParser(in, ctxt);
/*      */       }
/*      */     } 
/* 1034 */     return _createParser(data, offset, len, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(String content) throws IOException, JsonParseException {
/* 1045 */     int strLen = content.length();
/*      */     
/* 1047 */     if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays())
/*      */     {
/*      */       
/* 1050 */       return createParser(new StringReader(content));
/*      */     }
/* 1052 */     IOContext ctxt = _createContext(content, true);
/* 1053 */     char[] buf = ctxt.allocTokenBuffer(strLen);
/* 1054 */     content.getChars(0, strLen, buf, 0);
/* 1055 */     return _createParser(buf, 0, strLen, ctxt, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(char[] content) throws IOException {
/* 1066 */     return createParser(content, 0, content.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 1076 */     if (this._inputDecorator != null) {
/* 1077 */       return createParser(new CharArrayReader(content, offset, len));
/*      */     }
/* 1079 */     return _createParser(content, offset, len, _createContext(content, true), false);
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
/*      */   public JsonParser createParser(DataInput in) throws IOException {
/* 1095 */     IOContext ctxt = _createContext(in, false);
/* 1096 */     return _createParser(_decorate(in, ctxt), ctxt);
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
/*      */   public JsonParser createNonBlockingByteArrayParser() throws IOException {
/* 1122 */     _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
/* 1123 */     IOContext ctxt = _createNonBlockingContext((Object)null);
/* 1124 */     ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 1125 */     return (JsonParser)new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
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
/*      */   public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/* 1161 */     IOContext ctxt = _createContext(out, false);
/* 1162 */     ctxt.setEncoding(enc);
/* 1163 */     if (enc == JsonEncoding.UTF8) {
/* 1164 */       return _createUTF8Generator(_decorate(out, ctxt), ctxt);
/*      */     }
/* 1166 */     Writer w = _createWriter(out, enc, ctxt);
/* 1167 */     return _createGenerator(_decorate(w, ctxt), ctxt);
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
/*      */   public JsonGenerator createGenerator(OutputStream out) throws IOException {
/* 1180 */     return createGenerator(out, JsonEncoding.UTF8);
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
/*      */   public JsonGenerator createGenerator(Writer w) throws IOException {
/* 1200 */     IOContext ctxt = _createContext(w, false);
/* 1201 */     return _createGenerator(_decorate(w, ctxt), ctxt);
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
/*      */   public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/* 1223 */     OutputStream out = new FileOutputStream(f);
/*      */     
/* 1225 */     IOContext ctxt = _createContext(out, true);
/* 1226 */     ctxt.setEncoding(enc);
/* 1227 */     if (enc == JsonEncoding.UTF8) {
/* 1228 */       return _createUTF8Generator(_decorate(out, ctxt), ctxt);
/*      */     }
/* 1230 */     Writer w = _createWriter(out, enc, ctxt);
/* 1231 */     return _createGenerator(_decorate(w, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/* 1242 */     return createGenerator(_createDataOutputWrapper(out), enc);
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
/*      */   public JsonGenerator createGenerator(DataOutput out) throws IOException {
/* 1255 */     return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
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
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/* 1285 */     return createParser(f);
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
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/* 1310 */     return createParser(url);
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
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/* 1336 */     return createParser(in);
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
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/* 1355 */     return createParser(r);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/* 1365 */     return createParser(data);
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
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 1380 */     return createParser(data, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/* 1391 */     return createParser(content);
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
/*      */   @Deprecated
/*      */   public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/* 1424 */     return createGenerator(out, enc);
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
/*      */   @Deprecated
/*      */   public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/* 1444 */     return createGenerator(out);
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
/*      */   @Deprecated
/*      */   public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/* 1457 */     return createGenerator(out, JsonEncoding.UTF8);
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
/*      */   protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/* 1481 */     return (new ByteSourceJsonBootstrapper(ctxt, in)).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
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
/*      */   protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/* 1498 */     return (JsonParser)new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols
/* 1499 */         .makeChild(this._factoryFeatures));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/* 1510 */     return (JsonParser)new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols
/* 1511 */         .makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
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
/*      */   protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/* 1528 */     return (new ByteSourceJsonBootstrapper(ctxt, data, offset, len)).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
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
/*      */   protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/* 1541 */     _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
/*      */ 
/*      */     
/* 1544 */     int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/* 1545 */     ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 1546 */     return (JsonParser)new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
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
/*      */   protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/* 1569 */     WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
/*      */     
/* 1571 */     if (this._maximumNonEscapedChar > 0) {
/* 1572 */       gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
/*      */     }
/* 1574 */     if (this._characterEscapes != null) {
/* 1575 */       gen.setCharacterEscapes(this._characterEscapes);
/*      */     }
/* 1577 */     SerializableString rootSep = this._rootValueSeparator;
/* 1578 */     if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/* 1579 */       gen.setRootValueSeparator(rootSep);
/*      */     }
/* 1581 */     return (JsonGenerator)gen;
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
/*      */   protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/* 1595 */     UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
/*      */     
/* 1597 */     if (this._maximumNonEscapedChar > 0) {
/* 1598 */       gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
/*      */     }
/* 1600 */     if (this._characterEscapes != null) {
/* 1601 */       gen.setCharacterEscapes(this._characterEscapes);
/*      */     }
/* 1603 */     SerializableString rootSep = this._rootValueSeparator;
/* 1604 */     if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/* 1605 */       gen.setRootValueSeparator(rootSep);
/*      */     }
/* 1607 */     return (JsonGenerator)gen;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/* 1613 */     if (enc == JsonEncoding.UTF8) {
/* 1614 */       return (Writer)new UTF8Writer(ctxt, out);
/*      */     }
/*      */     
/* 1617 */     return new OutputStreamWriter(out, enc.getJavaName());
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
/*      */   protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/* 1630 */     if (this._inputDecorator != null) {
/* 1631 */       InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/* 1632 */       if (in2 != null) {
/* 1633 */         return in2;
/*      */       }
/*      */     } 
/* 1636 */     return in;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/* 1643 */     if (this._inputDecorator != null) {
/* 1644 */       Reader in2 = this._inputDecorator.decorate(ctxt, in);
/* 1645 */       if (in2 != null) {
/* 1646 */         return in2;
/*      */       }
/*      */     } 
/* 1649 */     return in;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/* 1656 */     if (this._inputDecorator != null) {
/* 1657 */       DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/* 1658 */       if (in2 != null) {
/* 1659 */         return in2;
/*      */       }
/*      */     } 
/* 1662 */     return in;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/* 1669 */     if (this._outputDecorator != null) {
/* 1670 */       OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/* 1671 */       if (out2 != null) {
/* 1672 */         return out2;
/*      */       }
/*      */     } 
/* 1675 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/* 1682 */     if (this._outputDecorator != null) {
/* 1683 */       Writer out2 = this._outputDecorator.decorate(ctxt, out);
/* 1684 */       if (out2 != null) {
/* 1685 */         return out2;
/*      */       }
/*      */     } 
/* 1688 */     return out;
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
/*      */   public BufferRecycler _getBufferRecycler() {
/* 1709 */     if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/* 1710 */       return BufferRecyclers.getBufferRecycler();
/*      */     }
/* 1712 */     return new BufferRecycler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/* 1720 */     return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
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
/*      */   protected IOContext _createNonBlockingContext(Object srcRef) {
/* 1732 */     return new IOContext(_getBufferRecycler(), srcRef, false);
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
/*      */   private final void _requireJSONFactory(String msg) {
/* 1754 */     if (!_isJSONFactory()) {
/* 1755 */       throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() }));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _isJSONFactory() {
/* 1762 */     return (getFormatName() == "JSON");
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\JsonFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */