/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.base.GeneratorBase;
/*     */ import com.fasterxml.jackson.core.io.CharTypes;
/*     */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*     */ import com.fasterxml.jackson.core.io.IOContext;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import com.fasterxml.jackson.core.util.VersionUtil;
/*     */ import java.io.IOException;
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
/*     */ public abstract class JsonGeneratorImpl
/*     */   extends GeneratorBase
/*     */ {
/*  31 */   protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
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
/*     */   protected final IOContext _ioContext;
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
/*  53 */   protected int[] _outputEscapes = sOutputEscapes;
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
/*     */   protected int _maximumNonEscapedChar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CharacterEscapes _characterEscapes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   protected SerializableString _rootValueSeparator = (SerializableString)DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _cfgUnqNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec) {
/* 104 */     super(features, codec);
/* 105 */     this._ioContext = ctxt;
/* 106 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features))
/*     */     {
/* 108 */       this._maximumNonEscapedChar = 127;
/*     */     }
/* 110 */     this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version version() {
/* 121 */     return VersionUtil.versionFor(getClass());
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
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 133 */     super.enable(f);
/* 134 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 135 */       this._cfgUnqNames = false;
/*     */     }
/* 137 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 143 */     super.disable(f);
/* 144 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 145 */       this._cfgUnqNames = true;
/*     */     }
/* 147 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/* 153 */     super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
/* 154 */     this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/* 159 */     this._maximumNonEscapedChar = (charCode < 0) ? 0 : charCode;
/* 160 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHighestEscapedChar() {
/* 165 */     return this._maximumNonEscapedChar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/* 171 */     this._characterEscapes = esc;
/* 172 */     if (esc == null) {
/* 173 */       this._outputEscapes = sOutputEscapes;
/*     */     } else {
/* 175 */       this._outputEscapes = esc.getEscapeCodesForAscii();
/*     */     } 
/* 177 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharacterEscapes getCharacterEscapes() {
/* 186 */     return this._characterEscapes;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/* 191 */     this._rootValueSeparator = sep;
/* 192 */     return (JsonGenerator)this;
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
/*     */   public final void writeStringField(String fieldName, String value) throws IOException {
/* 206 */     writeFieldName(fieldName);
/* 207 */     writeString(value);
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
/*     */   protected void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
/* 219 */     switch (status) {
/*     */       case 1:
/* 221 */         this._cfgPrettyPrinter.writeArrayValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       case 2:
/* 224 */         this._cfgPrettyPrinter.writeObjectFieldValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       case 3:
/* 227 */         this._cfgPrettyPrinter.writeRootValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       
/*     */       case 0:
/* 231 */         if (this._writeContext.inArray()) {
/* 232 */           this._cfgPrettyPrinter.beforeArrayValues((JsonGenerator)this);
/* 233 */         } else if (this._writeContext.inObject()) {
/* 234 */           this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*     */         } 
/*     */         return;
/*     */       case 5:
/* 238 */         _reportCantWriteValueExpectName(typeMsg);
/*     */         return;
/*     */     } 
/* 241 */     _throwInternal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportCantWriteValueExpectName(String typeMsg) throws IOException {
/* 248 */     _reportError(String.format("Can not %s, expecting field name (context: %s)", new Object[] { typeMsg, this._writeContext
/* 249 */             .typeDesc() }));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\JsonGeneratorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */