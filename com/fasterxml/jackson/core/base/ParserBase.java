/*      */ package com.fasterxml.jackson.core.base;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonProcessingException;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.Version;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.NumberInput;
/*      */ import com.fasterxml.jackson.core.json.DupDetector;
/*      */ import com.fasterxml.jackson.core.json.JsonReadContext;
/*      */ import com.fasterxml.jackson.core.json.PackageVersion;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class ParserBase
/*      */   extends ParserMinimalBase
/*      */ {
/*      */   protected final IOContext _ioContext;
/*      */   protected boolean _closed;
/*      */   protected int _inputPtr;
/*      */   protected int _inputEnd;
/*      */   protected long _currInputProcessed;
/*   78 */   protected int _currInputRow = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _currInputRowStart;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _tokenInputTotal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  107 */   protected int _tokenInputRow = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _tokenInputCol;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonReadContext _parsingContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _nextToken;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final TextBuffer _textBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _nameCopyBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _nameCopied;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ByteArrayBuilder _byteArrayBuilder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _binaryValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  182 */   protected int _numTypesValid = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _numberInt;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _numberLong;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double _numberDouble;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigInteger _numberBigInt;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigDecimal _numberBigDecimal;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _numberNegative;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _intLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _fractLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _expLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ParserBase(IOContext ctxt, int features) {
/*  233 */     super(features);
/*  234 */     this._ioContext = ctxt;
/*  235 */     this._textBuffer = ctxt.constructTextBuffer();
/*      */     
/*  237 */     DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/*  238 */     this._parsingContext = JsonReadContext.createRootContext(dups);
/*      */   }
/*      */   public Version version() {
/*  241 */     return PackageVersion.VERSION;
/*      */   }
/*      */   
/*      */   public Object getCurrentValue() {
/*  245 */     return this._parsingContext.getCurrentValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCurrentValue(Object v) {
/*  250 */     this._parsingContext.setCurrentValue(v);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser enable(JsonParser.Feature f) {
/*  261 */     this._features |= f.getMask();
/*  262 */     if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && 
/*  263 */       this._parsingContext.getDupDetector() == null) {
/*  264 */       this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
/*      */     }
/*      */     
/*  267 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonParser disable(JsonParser.Feature f) {
/*  272 */     this._features &= f.getMask() ^ 0xFFFFFFFF;
/*  273 */     if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
/*  274 */       this._parsingContext = this._parsingContext.withDupDetector(null);
/*      */     }
/*  276 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonParser setFeatureMask(int newMask) {
/*  282 */     int changes = this._features ^ newMask;
/*  283 */     if (changes != 0) {
/*  284 */       this._features = newMask;
/*  285 */       _checkStdFeatureChanges(newMask, changes);
/*      */     } 
/*  287 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonParser overrideStdFeatures(int values, int mask) {
/*  292 */     int oldState = this._features;
/*  293 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  294 */     int changed = oldState ^ newState;
/*  295 */     if (changed != 0) {
/*  296 */       this._features = newState;
/*  297 */       _checkStdFeatureChanges(newState, changed);
/*      */     } 
/*  299 */     return this;
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
/*      */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/*  313 */     int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
/*      */     
/*  315 */     if ((changedFeatures & f) != 0 && (
/*  316 */       newFeatureFlags & f) != 0) {
/*  317 */       if (this._parsingContext.getDupDetector() == null) {
/*  318 */         this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
/*      */       } else {
/*  320 */         this._parsingContext = this._parsingContext.withDupDetector(null);
/*      */       } 
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
/*      */   public String getCurrentName() throws IOException {
/*  338 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*  339 */       JsonReadContext parent = this._parsingContext.getParent();
/*  340 */       if (parent != null) {
/*  341 */         return parent.getCurrentName();
/*      */       }
/*      */     } 
/*  344 */     return this._parsingContext.getCurrentName();
/*      */   }
/*      */ 
/*      */   
/*      */   public void overrideCurrentName(String name) {
/*  349 */     JsonReadContext ctxt = this._parsingContext;
/*  350 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*  351 */       ctxt = ctxt.getParent();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  357 */       ctxt.setCurrentName(name);
/*  358 */     } catch (IOException e) {
/*  359 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void close() throws IOException {
/*  364 */     if (!this._closed) {
/*      */       
/*  366 */       this._inputPtr = Math.max(this._inputPtr, this._inputEnd);
/*  367 */       this._closed = true;
/*      */       try {
/*  369 */         _closeInput();
/*      */       }
/*      */       finally {
/*      */         
/*  373 */         _releaseBuffers();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*  378 */   public boolean isClosed() { return this._closed; } public JsonReadContext getParsingContext() {
/*  379 */     return this._parsingContext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getTokenLocation() {
/*  388 */     return new JsonLocation(_getSourceReference(), -1L, 
/*  389 */         getTokenCharacterOffset(), 
/*  390 */         getTokenLineNr(), 
/*  391 */         getTokenColumnNr());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/*  400 */     int col = this._inputPtr - this._currInputRowStart + 1;
/*  401 */     return new JsonLocation(_getSourceReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
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
/*      */   public boolean hasTextCharacters() {
/*  414 */     if (this._currToken == JsonToken.VALUE_STRING) return true; 
/*  415 */     if (this._currToken == JsonToken.FIELD_NAME) return this._nameCopied; 
/*  416 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant variant) throws IOException {
/*  423 */     if (this._binaryValue == null) {
/*  424 */       if (this._currToken != JsonToken.VALUE_STRING) {
/*  425 */         _reportError("Current token (" + this._currToken + ") not VALUE_STRING, can not access as binary");
/*      */       }
/*  427 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  428 */       _decodeBase64(getText(), builder, variant);
/*  429 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*  431 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getTokenCharacterOffset() {
/*  440 */     return this._tokenInputTotal; } public int getTokenLineNr() {
/*  441 */     return this._tokenInputRow;
/*      */   }
/*      */   public int getTokenColumnNr() {
/*  444 */     int col = this._tokenInputCol;
/*  445 */     return (col < 0) ? col : (col + 1);
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
/*      */   protected void _releaseBuffers() throws IOException {
/*  469 */     this._textBuffer.releaseBuffers();
/*  470 */     char[] buf = this._nameCopyBuffer;
/*  471 */     if (buf != null) {
/*  472 */       this._nameCopyBuffer = null;
/*  473 */       this._ioContext.releaseNameCopyBuffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _handleEOF() throws JsonParseException {
/*  484 */     if (!this._parsingContext.inRoot()) {
/*  485 */       String marker = this._parsingContext.inArray() ? "Array" : "Object";
/*  486 */       _reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", new Object[] { marker, this._parsingContext
/*      */ 
/*      */               
/*  489 */               .getStartLocation(_getSourceReference()) }), (JsonToken)null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _eofAsNextChar() throws JsonParseException {
/*  498 */     _handleEOF();
/*  499 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteArrayBuilder _getByteArrayBuilder() {
/*  510 */     if (this._byteArrayBuilder == null) {
/*  511 */       this._byteArrayBuilder = new ByteArrayBuilder();
/*      */     } else {
/*  513 */       this._byteArrayBuilder.reset();
/*      */     } 
/*  515 */     return this._byteArrayBuilder;
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
/*      */   protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) {
/*  528 */     if (fractLen < 1 && expLen < 1) {
/*  529 */       return resetInt(negative, intLen);
/*      */     }
/*  531 */     return resetFloat(negative, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetInt(boolean negative, int intLen) {
/*  536 */     this._numberNegative = negative;
/*  537 */     this._intLength = intLen;
/*  538 */     this._fractLength = 0;
/*  539 */     this._expLength = 0;
/*  540 */     this._numTypesValid = 0;
/*  541 */     return JsonToken.VALUE_NUMBER_INT;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) {
/*  546 */     this._numberNegative = negative;
/*  547 */     this._intLength = intLen;
/*  548 */     this._fractLength = fractLen;
/*  549 */     this._expLength = expLen;
/*  550 */     this._numTypesValid = 0;
/*  551 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetAsNaN(String valueStr, double value) {
/*  556 */     this._textBuffer.resetWithString(valueStr);
/*  557 */     this._numberDouble = value;
/*  558 */     this._numTypesValid = 8;
/*  559 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isNaN() {
/*  564 */     if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT && (
/*  565 */       this._numTypesValid & 0x8) != 0) {
/*      */       
/*  567 */       double d = this._numberDouble;
/*  568 */       return (Double.isNaN(d) || Double.isInfinite(d));
/*      */     } 
/*      */     
/*  571 */     return false;
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
/*      */   public Number getNumberValue() throws IOException {
/*  583 */     if (this._numTypesValid == 0) {
/*  584 */       _parseNumericValue(0);
/*      */     }
/*      */     
/*  587 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  588 */       if ((this._numTypesValid & 0x1) != 0) {
/*  589 */         return Integer.valueOf(this._numberInt);
/*      */       }
/*  591 */       if ((this._numTypesValid & 0x2) != 0) {
/*  592 */         return Long.valueOf(this._numberLong);
/*      */       }
/*  594 */       if ((this._numTypesValid & 0x4) != 0) {
/*  595 */         return this._numberBigInt;
/*      */       }
/*      */       
/*  598 */       return this._numberBigDecimal;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  604 */     if ((this._numTypesValid & 0x10) != 0) {
/*  605 */       return this._numberBigDecimal;
/*      */     }
/*  607 */     if ((this._numTypesValid & 0x8) == 0) {
/*  608 */       _throwInternal();
/*      */     }
/*  610 */     return Double.valueOf(this._numberDouble);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser.NumberType getNumberType() throws IOException {
/*  616 */     if (this._numTypesValid == 0) {
/*  617 */       _parseNumericValue(0);
/*      */     }
/*  619 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  620 */       if ((this._numTypesValid & 0x1) != 0) {
/*  621 */         return JsonParser.NumberType.INT;
/*      */       }
/*  623 */       if ((this._numTypesValid & 0x2) != 0) {
/*  624 */         return JsonParser.NumberType.LONG;
/*      */       }
/*  626 */       return JsonParser.NumberType.BIG_INTEGER;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  635 */     if ((this._numTypesValid & 0x10) != 0) {
/*  636 */       return JsonParser.NumberType.BIG_DECIMAL;
/*      */     }
/*  638 */     return JsonParser.NumberType.DOUBLE;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getIntValue() throws IOException {
/*  644 */     if ((this._numTypesValid & 0x1) == 0) {
/*  645 */       if (this._numTypesValid == 0) {
/*  646 */         return _parseIntValue();
/*      */       }
/*  648 */       if ((this._numTypesValid & 0x1) == 0) {
/*  649 */         convertNumberToInt();
/*      */       }
/*      */     } 
/*  652 */     return this._numberInt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLongValue() throws IOException {
/*  658 */     if ((this._numTypesValid & 0x2) == 0) {
/*  659 */       if (this._numTypesValid == 0) {
/*  660 */         _parseNumericValue(2);
/*      */       }
/*  662 */       if ((this._numTypesValid & 0x2) == 0) {
/*  663 */         convertNumberToLong();
/*      */       }
/*      */     } 
/*  666 */     return this._numberLong;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BigInteger getBigIntegerValue() throws IOException {
/*  672 */     if ((this._numTypesValid & 0x4) == 0) {
/*  673 */       if (this._numTypesValid == 0) {
/*  674 */         _parseNumericValue(4);
/*      */       }
/*  676 */       if ((this._numTypesValid & 0x4) == 0) {
/*  677 */         convertNumberToBigInteger();
/*      */       }
/*      */     } 
/*  680 */     return this._numberBigInt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloatValue() throws IOException {
/*  686 */     double value = getDoubleValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  695 */     return (float)value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDoubleValue() throws IOException {
/*  701 */     if ((this._numTypesValid & 0x8) == 0) {
/*  702 */       if (this._numTypesValid == 0) {
/*  703 */         _parseNumericValue(8);
/*      */       }
/*  705 */       if ((this._numTypesValid & 0x8) == 0) {
/*  706 */         convertNumberToDouble();
/*      */       }
/*      */     } 
/*  709 */     return this._numberDouble;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getDecimalValue() throws IOException {
/*  715 */     if ((this._numTypesValid & 0x10) == 0) {
/*  716 */       if (this._numTypesValid == 0) {
/*  717 */         _parseNumericValue(16);
/*      */       }
/*  719 */       if ((this._numTypesValid & 0x10) == 0) {
/*  720 */         convertNumberToBigDecimal();
/*      */       }
/*      */     } 
/*  723 */     return this._numberBigDecimal;
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
/*      */   protected void _parseNumericValue(int expType) throws IOException {
/*  744 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  745 */       int len = this._intLength;
/*      */       
/*  747 */       if (len <= 9) {
/*  748 */         int i = this._textBuffer.contentsAsInt(this._numberNegative);
/*  749 */         this._numberInt = i;
/*  750 */         this._numTypesValid = 1;
/*      */         return;
/*      */       } 
/*  753 */       if (len <= 18) {
/*  754 */         long l = this._textBuffer.contentsAsLong(this._numberNegative);
/*      */         
/*  756 */         if (len == 10) {
/*  757 */           if (this._numberNegative) {
/*  758 */             if (l >= -2147483648L) {
/*  759 */               this._numberInt = (int)l;
/*  760 */               this._numTypesValid = 1;
/*      */               
/*      */               return;
/*      */             } 
/*  764 */           } else if (l <= 2147483647L) {
/*  765 */             this._numberInt = (int)l;
/*  766 */             this._numTypesValid = 1;
/*      */             
/*      */             return;
/*      */           } 
/*      */         }
/*  771 */         this._numberLong = l;
/*  772 */         this._numTypesValid = 2;
/*      */         return;
/*      */       } 
/*  775 */       _parseSlowInt(expType);
/*      */       return;
/*      */     } 
/*  778 */     if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
/*  779 */       _parseSlowFloat(expType);
/*      */       return;
/*      */     } 
/*  782 */     _reportError("Current token (%s) not numeric, can not use numeric value accessors", this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _parseIntValue() throws IOException {
/*  791 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT && 
/*  792 */       this._intLength <= 9) {
/*  793 */       int i = this._textBuffer.contentsAsInt(this._numberNegative);
/*  794 */       this._numberInt = i;
/*  795 */       this._numTypesValid = 1;
/*  796 */       return i;
/*      */     } 
/*      */ 
/*      */     
/*  800 */     _parseNumericValue(1);
/*  801 */     if ((this._numTypesValid & 0x1) == 0) {
/*  802 */       convertNumberToInt();
/*      */     }
/*  804 */     return this._numberInt;
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
/*      */   private void _parseSlowFloat(int expType) throws IOException {
/*      */     try {
/*  817 */       if (expType == 16) {
/*  818 */         this._numberBigDecimal = this._textBuffer.contentsAsDecimal();
/*  819 */         this._numTypesValid = 16;
/*      */       } else {
/*      */         
/*  822 */         this._numberDouble = this._textBuffer.contentsAsDouble();
/*  823 */         this._numTypesValid = 8;
/*      */       } 
/*  825 */     } catch (NumberFormatException nex) {
/*      */       
/*  827 */       _wrapError("Malformed numeric value (" + _longNumberDesc(this._textBuffer.contentsAsString()) + ")", nex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _parseSlowInt(int expType) throws IOException {
/*  833 */     String numStr = this._textBuffer.contentsAsString();
/*      */     try {
/*  835 */       int len = this._intLength;
/*  836 */       char[] buf = this._textBuffer.getTextBuffer();
/*  837 */       int offset = this._textBuffer.getTextOffset();
/*  838 */       if (this._numberNegative) {
/*  839 */         offset++;
/*      */       }
/*      */       
/*  842 */       if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
/*      */         
/*  844 */         this._numberLong = Long.parseLong(numStr);
/*  845 */         this._numTypesValid = 2;
/*      */       } else {
/*      */         
/*  848 */         if (expType == 1 || expType == 2) {
/*  849 */           _reportTooLongIntegral(expType, numStr);
/*      */         }
/*  851 */         if (expType == 8 || expType == 32) {
/*  852 */           this._numberDouble = NumberInput.parseDouble(numStr);
/*  853 */           this._numTypesValid = 8;
/*      */         } else {
/*      */           
/*  856 */           this._numberBigInt = new BigInteger(numStr);
/*  857 */           this._numTypesValid = 4;
/*      */         } 
/*      */       } 
/*  860 */     } catch (NumberFormatException nex) {
/*      */       
/*  862 */       _wrapError("Malformed numeric value (" + _longNumberDesc(numStr) + ")", nex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportTooLongIntegral(int expType, String rawNum) throws IOException {
/*  869 */     if (expType == 1) {
/*  870 */       reportOverflowInt(rawNum);
/*      */     } else {
/*  872 */       reportOverflowLong(rawNum);
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
/*      */   protected void convertNumberToInt() throws IOException {
/*  885 */     if ((this._numTypesValid & 0x2) != 0) {
/*      */       
/*  887 */       int result = (int)this._numberLong;
/*  888 */       if (result != this._numberLong) {
/*  889 */         reportOverflowInt(getText(), currentToken());
/*      */       }
/*  891 */       this._numberInt = result;
/*  892 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  893 */       if (BI_MIN_INT.compareTo(this._numberBigInt) > 0 || BI_MAX_INT
/*  894 */         .compareTo(this._numberBigInt) < 0) {
/*  895 */         reportOverflowInt();
/*      */       }
/*  897 */       this._numberInt = this._numberBigInt.intValue();
/*  898 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*      */       
/*  900 */       if (this._numberDouble < -2.147483648E9D || this._numberDouble > 2.147483647E9D) {
/*  901 */         reportOverflowInt();
/*      */       }
/*  903 */       this._numberInt = (int)this._numberDouble;
/*  904 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  905 */       if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT
/*  906 */         .compareTo(this._numberBigDecimal) < 0) {
/*  907 */         reportOverflowInt();
/*      */       }
/*  909 */       this._numberInt = this._numberBigDecimal.intValue();
/*      */     } else {
/*  911 */       _throwInternal();
/*      */     } 
/*  913 */     this._numTypesValid |= 0x1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void convertNumberToLong() throws IOException {
/*  918 */     if ((this._numTypesValid & 0x1) != 0) {
/*  919 */       this._numberLong = this._numberInt;
/*  920 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  921 */       if (BI_MIN_LONG.compareTo(this._numberBigInt) > 0 || BI_MAX_LONG
/*  922 */         .compareTo(this._numberBigInt) < 0) {
/*  923 */         reportOverflowLong();
/*      */       }
/*  925 */       this._numberLong = this._numberBigInt.longValue();
/*  926 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*      */       
/*  928 */       if (this._numberDouble < -9.223372036854776E18D || this._numberDouble > 9.223372036854776E18D) {
/*  929 */         reportOverflowLong();
/*      */       }
/*  931 */       this._numberLong = (long)this._numberDouble;
/*  932 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  933 */       if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG
/*  934 */         .compareTo(this._numberBigDecimal) < 0) {
/*  935 */         reportOverflowLong();
/*      */       }
/*  937 */       this._numberLong = this._numberBigDecimal.longValue();
/*      */     } else {
/*  939 */       _throwInternal();
/*      */     } 
/*  941 */     this._numTypesValid |= 0x2;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void convertNumberToBigInteger() throws IOException {
/*  946 */     if ((this._numTypesValid & 0x10) != 0) {
/*      */       
/*  948 */       this._numberBigInt = this._numberBigDecimal.toBigInteger();
/*  949 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  950 */       this._numberBigInt = BigInteger.valueOf(this._numberLong);
/*  951 */     } else if ((this._numTypesValid & 0x1) != 0) {
/*  952 */       this._numberBigInt = BigInteger.valueOf(this._numberInt);
/*  953 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*  954 */       this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
/*      */     } else {
/*  956 */       _throwInternal();
/*      */     } 
/*  958 */     this._numTypesValid |= 0x4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void convertNumberToDouble() throws IOException {
/*  969 */     if ((this._numTypesValid & 0x10) != 0) {
/*  970 */       this._numberDouble = this._numberBigDecimal.doubleValue();
/*  971 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  972 */       this._numberDouble = this._numberBigInt.doubleValue();
/*  973 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  974 */       this._numberDouble = this._numberLong;
/*  975 */     } else if ((this._numTypesValid & 0x1) != 0) {
/*  976 */       this._numberDouble = this._numberInt;
/*      */     } else {
/*  978 */       _throwInternal();
/*      */     } 
/*  980 */     this._numTypesValid |= 0x8;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void convertNumberToBigDecimal() throws IOException {
/*  991 */     if ((this._numTypesValid & 0x8) != 0) {
/*      */ 
/*      */ 
/*      */       
/*  995 */       this._numberBigDecimal = NumberInput.parseBigDecimal(getText());
/*  996 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  997 */       this._numberBigDecimal = new BigDecimal(this._numberBigInt);
/*  998 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  999 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
/* 1000 */     } else if ((this._numTypesValid & 0x1) != 0) {
/* 1001 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
/*      */     } else {
/* 1003 */       _throwInternal();
/*      */     } 
/* 1005 */     this._numTypesValid |= 0x10;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
/* 1015 */     JsonReadContext ctxt = getParsingContext();
/* 1016 */     _reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", new Object[] {
/*      */             
/* 1018 */             Character.valueOf((char)actCh), Character.valueOf(expCh), ctxt.typeDesc(), ctxt.getStartLocation(_getSourceReference())
/*      */           }));
/*      */   }
/*      */ 
/*      */   
/*      */   protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
/* 1024 */     if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
/* 1025 */       return ch;
/*      */     }
/*      */     
/* 1028 */     if (ch == '\'' && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 1029 */       return ch;
/*      */     }
/* 1031 */     _reportError("Unrecognized character escape " + _getCharDesc(ch));
/* 1032 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
/* 1043 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
/* 1044 */       char c = (char)i;
/* 1045 */       String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
/* 1046 */       _reportError(msg);
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
/*      */   protected String _validJsonTokenList() throws IOException {
/* 1058 */     return _validJsonValueList();
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
/*      */   protected String _validJsonValueList() throws IOException {
/* 1070 */     if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1071 */       return "(JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')";
/*      */     }
/* 1073 */     return "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
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
/*      */   protected char _decodeEscaped() throws IOException {
/* 1088 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws IOException {
/* 1094 */     if (ch != 92) {
/* 1095 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1097 */     int unescaped = _decodeEscaped();
/*      */     
/* 1099 */     if (unescaped <= 32 && 
/* 1100 */       index == 0) {
/* 1101 */       return -1;
/*      */     }
/*      */ 
/*      */     
/* 1105 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1106 */     if (bits < 0 && 
/* 1107 */       bits != -2) {
/* 1108 */       throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */     }
/*      */     
/* 1111 */     return bits;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException {
/* 1116 */     if (ch != '\\') {
/* 1117 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1119 */     char unescaped = _decodeEscaped();
/*      */     
/* 1121 */     if (unescaped <= ' ' && 
/* 1122 */       index == 0) {
/* 1123 */       return -1;
/*      */     }
/*      */ 
/*      */     
/* 1127 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1128 */     if (bits < 0)
/*      */     {
/* 1130 */       if (bits != -2 || index < 2) {
/* 1131 */         throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */       }
/*      */     }
/* 1134 */     return bits;
/*      */   }
/*      */   
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
/* 1138 */     return reportInvalidBase64Char(b64variant, ch, bindex, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
/*      */     String base;
/* 1147 */     if (ch <= 32) {
/* 1148 */       base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", new Object[] {
/* 1149 */             Integer.toHexString(ch), Integer.valueOf(bindex + 1) });
/* 1150 */     } else if (b64variant.usesPaddingChar(ch)) {
/* 1151 */       base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
/* 1152 */     } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
/*      */       
/* 1154 */       base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */     } else {
/* 1156 */       base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */     } 
/* 1158 */     if (msg != null) {
/* 1159 */       base = base + ": " + msg;
/*      */     }
/* 1161 */     return new IllegalArgumentException(base);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _handleBase64MissingPadding(Base64Variant b64variant) throws IOException {
/* 1167 */     _reportError(b64variant.missingPaddingMessage());
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
/*      */   protected Object _getSourceReference() {
/* 1183 */     if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
/* 1184 */       return this._ioContext.getSourceReference();
/*      */     }
/* 1186 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected static int[] growArrayBy(int[] arr, int more) {
/* 1191 */     if (arr == null) {
/* 1192 */       return new int[more];
/*      */     }
/* 1194 */     return Arrays.copyOf(arr, arr.length + more);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void loadMoreGuaranteed() throws IOException {
/* 1206 */     if (!loadMore()) _reportInvalidEOF(); 
/*      */   }
/*      */   @Deprecated
/*      */   protected boolean loadMore() throws IOException {
/* 1210 */     return false;
/*      */   }
/*      */   
/*      */   protected void _finishString() throws IOException {}
/*      */   
/*      */   protected abstract void _closeInput() throws IOException;
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\base\ParserBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */