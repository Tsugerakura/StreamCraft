/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.base.ParserBase;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ 
/*      */ public class ReaderBasedJsonParser
/*      */   extends ParserBase {
/*   23 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */ 
/*      */   
/*   26 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */ 
/*      */   
/*   29 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
/*      */ 
/*      */   
/*   32 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   33 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   34 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*      */   
/*   36 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   37 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */ 
/*      */   
/*   41 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Reader _reader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _bufferRecyclable;
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
/*      */   protected final CharsToNameCanonicalizer _symbols;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _hashSeed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _tokenIncomplete;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _nameStartOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartRow;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartCol;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable) {
/*  133 */     super(ctxt, features);
/*  134 */     this._reader = r;
/*  135 */     this._inputBuffer = inputBuffer;
/*  136 */     this._inputPtr = start;
/*  137 */     this._inputEnd = end;
/*  138 */     this._objectCodec = codec;
/*  139 */     this._symbols = st;
/*  140 */     this._hashSeed = st.hashSeed();
/*  141 */     this._bufferRecyclable = bufferRecyclable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
/*  151 */     super(ctxt, features);
/*  152 */     this._reader = r;
/*  153 */     this._inputBuffer = ctxt.allocTokenBuffer();
/*  154 */     this._inputPtr = 0;
/*  155 */     this._inputEnd = 0;
/*  156 */     this._objectCodec = codec;
/*  157 */     this._symbols = st;
/*  158 */     this._hashSeed = st.hashSeed();
/*  159 */     this._bufferRecyclable = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  168 */     return this._objectCodec; } public void setCodec(ObjectCodec c) {
/*  169 */     this._objectCodec = c;
/*      */   }
/*      */   
/*      */   public int releaseBuffered(Writer w) throws IOException {
/*  173 */     int count = this._inputEnd - this._inputPtr;
/*  174 */     if (count < 1) return 0;
/*      */     
/*  176 */     int origPtr = this._inputPtr;
/*  177 */     w.write(this._inputBuffer, origPtr, count);
/*  178 */     return count;
/*      */   }
/*      */   public Object getInputSource() {
/*  181 */     return this._reader;
/*      */   }
/*      */   @Deprecated
/*      */   protected char getNextChar(String eofMsg) throws IOException {
/*  185 */     return getNextChar(eofMsg, (JsonToken)null);
/*      */   }
/*      */   
/*      */   protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
/*  189 */     if (this._inputPtr >= this._inputEnd && 
/*  190 */       !_loadMore()) {
/*  191 */       _reportInvalidEOF(eofMsg, forToken);
/*      */     }
/*      */     
/*  194 */     return this._inputBuffer[this._inputPtr++];
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
/*      */   protected void _closeInput() throws IOException {
/*  206 */     if (this._reader != null) {
/*  207 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
/*  208 */         this._reader.close();
/*      */       }
/*  210 */       this._reader = null;
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
/*      */   protected void _releaseBuffers() throws IOException {
/*  222 */     super._releaseBuffers();
/*      */     
/*  224 */     this._symbols.release();
/*      */     
/*  226 */     if (this._bufferRecyclable) {
/*  227 */       char[] buf = this._inputBuffer;
/*  228 */       if (buf != null) {
/*  229 */         this._inputBuffer = null;
/*  230 */         this._ioContext.releaseTokenBuffer(buf);
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
/*      */   protected void _loadMoreGuaranteed() throws IOException {
/*  242 */     if (!_loadMore()) _reportInvalidEOF();
/*      */   
/*      */   }
/*      */   
/*      */   protected boolean _loadMore() throws IOException {
/*  247 */     int bufSize = this._inputEnd;
/*      */     
/*  249 */     this._currInputProcessed += bufSize;
/*  250 */     this._currInputRowStart -= bufSize;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  255 */     this._nameStartOffset -= bufSize;
/*      */     
/*  257 */     if (this._reader != null) {
/*  258 */       int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/*  259 */       if (count > 0) {
/*  260 */         this._inputPtr = 0;
/*  261 */         this._inputEnd = count;
/*  262 */         return true;
/*      */       } 
/*      */       
/*  265 */       _closeInput();
/*      */       
/*  267 */       if (count == 0) {
/*  268 */         throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
/*      */       }
/*      */     } 
/*  271 */     return false;
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
/*      */   public final String getText() throws IOException {
/*  289 */     JsonToken t = this._currToken;
/*  290 */     if (t == JsonToken.VALUE_STRING) {
/*  291 */       if (this._tokenIncomplete) {
/*  292 */         this._tokenIncomplete = false;
/*  293 */         _finishString();
/*      */       } 
/*  295 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  297 */     return _getText2(t);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getText(Writer writer) throws IOException {
/*  303 */     JsonToken t = this._currToken;
/*  304 */     if (t == JsonToken.VALUE_STRING) {
/*  305 */       if (this._tokenIncomplete) {
/*  306 */         this._tokenIncomplete = false;
/*  307 */         _finishString();
/*      */       } 
/*  309 */       return this._textBuffer.contentsToWriter(writer);
/*      */     } 
/*  311 */     if (t == JsonToken.FIELD_NAME) {
/*  312 */       String n = this._parsingContext.getCurrentName();
/*  313 */       writer.write(n);
/*  314 */       return n.length();
/*      */     } 
/*  316 */     if (t != null) {
/*  317 */       if (t.isNumeric()) {
/*  318 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  320 */       char[] ch = t.asCharArray();
/*  321 */       writer.write(ch);
/*  322 */       return ch.length;
/*      */     } 
/*  324 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getValueAsString() throws IOException {
/*  333 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  334 */       if (this._tokenIncomplete) {
/*  335 */         this._tokenIncomplete = false;
/*  336 */         _finishString();
/*      */       } 
/*  338 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  340 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  341 */       return getCurrentName();
/*      */     }
/*  343 */     return super.getValueAsString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getValueAsString(String defValue) throws IOException {
/*  349 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  350 */       if (this._tokenIncomplete) {
/*  351 */         this._tokenIncomplete = false;
/*  352 */         _finishString();
/*      */       } 
/*  354 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  356 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  357 */       return getCurrentName();
/*      */     }
/*  359 */     return super.getValueAsString(defValue);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  363 */     if (t == null) {
/*  364 */       return null;
/*      */     }
/*  366 */     switch (t.id()) {
/*      */       case 5:
/*  368 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  374 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  376 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final char[] getTextCharacters() throws IOException {
/*  383 */     if (this._currToken != null) {
/*  384 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  386 */           if (!this._nameCopied) {
/*  387 */             String name = this._parsingContext.getCurrentName();
/*  388 */             int nameLen = name.length();
/*  389 */             if (this._nameCopyBuffer == null) {
/*  390 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  391 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  392 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  394 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  395 */             this._nameCopied = true;
/*      */           } 
/*  397 */           return this._nameCopyBuffer;
/*      */         case 6:
/*  399 */           if (this._tokenIncomplete) {
/*  400 */             this._tokenIncomplete = false;
/*  401 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  406 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*  408 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  411 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getTextLength() throws IOException {
/*  417 */     if (this._currToken != null) {
/*  418 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  420 */           return this._parsingContext.getCurrentName().length();
/*      */         case 6:
/*  422 */           if (this._tokenIncomplete) {
/*  423 */             this._tokenIncomplete = false;
/*  424 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  429 */           return this._textBuffer.size();
/*      */       } 
/*  431 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*      */     
/*  434 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getTextOffset() throws IOException {
/*  441 */     if (this._currToken != null) {
/*  442 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  444 */           return 0;
/*      */         case 6:
/*  446 */           if (this._tokenIncomplete) {
/*  447 */             this._tokenIncomplete = false;
/*  448 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  453 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  457 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  463 */     if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && this._binaryValue != null) {
/*  464 */       return this._binaryValue;
/*      */     }
/*  466 */     if (this._currToken != JsonToken.VALUE_STRING) {
/*  467 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*  470 */     if (this._tokenIncomplete) {
/*      */       try {
/*  472 */         this._binaryValue = _decodeBase64(b64variant);
/*  473 */       } catch (IllegalArgumentException iae) {
/*  474 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  479 */       this._tokenIncomplete = false;
/*      */     }
/*  481 */     else if (this._binaryValue == null) {
/*      */       
/*  483 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  484 */       _decodeBase64(getText(), builder, b64variant);
/*  485 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  488 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  495 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  496 */       byte[] b = getBinaryValue(b64variant);
/*  497 */       out.write(b);
/*  498 */       return b.length;
/*      */     } 
/*      */     
/*  501 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  503 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  505 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  511 */     int outputPtr = 0;
/*  512 */     int outputEnd = buffer.length - 3;
/*  513 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  519 */       if (this._inputPtr >= this._inputEnd) {
/*  520 */         _loadMoreGuaranteed();
/*      */       }
/*  522 */       char ch = this._inputBuffer[this._inputPtr++];
/*  523 */       if (ch > ' ') {
/*  524 */         int bits = b64variant.decodeBase64Char(ch);
/*  525 */         if (bits < 0) {
/*  526 */           if (ch == '"') {
/*      */             break;
/*      */           }
/*  529 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  530 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  536 */         if (outputPtr > outputEnd) {
/*  537 */           outputCount += outputPtr;
/*  538 */           out.write(buffer, 0, outputPtr);
/*  539 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  542 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/*  546 */         if (this._inputPtr >= this._inputEnd) {
/*  547 */           _loadMoreGuaranteed();
/*      */         }
/*  549 */         ch = this._inputBuffer[this._inputPtr++];
/*  550 */         bits = b64variant.decodeBase64Char(ch);
/*  551 */         if (bits < 0) {
/*  552 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  554 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  557 */         if (this._inputPtr >= this._inputEnd) {
/*  558 */           _loadMoreGuaranteed();
/*      */         }
/*  560 */         ch = this._inputBuffer[this._inputPtr++];
/*  561 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  564 */         if (bits < 0) {
/*  565 */           if (bits != -2) {
/*      */             
/*  567 */             if (ch == '"') {
/*  568 */               decodedData >>= 4;
/*  569 */               buffer[outputPtr++] = (byte)decodedData;
/*  570 */               if (b64variant.usesPadding()) {
/*  571 */                 this._inputPtr--;
/*  572 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  576 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  578 */           if (bits == -2) {
/*      */             
/*  580 */             if (this._inputPtr >= this._inputEnd) {
/*  581 */               _loadMoreGuaranteed();
/*      */             }
/*  583 */             ch = this._inputBuffer[this._inputPtr++];
/*  584 */             if (!b64variant.usesPaddingChar(ch) && 
/*  585 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/*  586 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  590 */             decodedData >>= 4;
/*  591 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  596 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  598 */         if (this._inputPtr >= this._inputEnd) {
/*  599 */           _loadMoreGuaranteed();
/*      */         }
/*  601 */         ch = this._inputBuffer[this._inputPtr++];
/*  602 */         bits = b64variant.decodeBase64Char(ch);
/*  603 */         if (bits < 0) {
/*  604 */           if (bits != -2) {
/*      */             
/*  606 */             if (ch == '"') {
/*  607 */               decodedData >>= 2;
/*  608 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  609 */               buffer[outputPtr++] = (byte)decodedData;
/*  610 */               if (b64variant.usesPadding()) {
/*  611 */                 this._inputPtr--;
/*  612 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  616 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  618 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  625 */             decodedData >>= 2;
/*  626 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  627 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  632 */         decodedData = decodedData << 6 | bits;
/*  633 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  634 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  635 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  637 */     }  this._tokenIncomplete = false;
/*  638 */     if (outputPtr > 0) {
/*  639 */       outputCount += outputPtr;
/*  640 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  642 */     return outputCount;
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
/*      */   public final JsonToken nextToken() throws IOException {
/*      */     JsonToken t;
/*  662 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  663 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  667 */     this._numTypesValid = 0;
/*  668 */     if (this._tokenIncomplete) {
/*  669 */       _skipString();
/*      */     }
/*  671 */     int i = _skipWSOrEnd();
/*  672 */     if (i < 0) {
/*      */ 
/*      */       
/*  675 */       close();
/*  676 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  679 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  682 */     if (i == 93 || i == 125) {
/*  683 */       _closeScope(i);
/*  684 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  688 */     if (this._parsingContext.expectComma()) {
/*  689 */       i = _skipComma(i);
/*      */ 
/*      */       
/*  692 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  693 */         i == 93 || i == 125)) {
/*  694 */         _closeScope(i);
/*  695 */         return this._currToken;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  703 */     boolean inObject = this._parsingContext.inObject();
/*  704 */     if (inObject) {
/*      */       
/*  706 */       _updateNameLocation();
/*  707 */       String name = (i == 34) ? _parseName() : _handleOddName(i);
/*  708 */       this._parsingContext.setCurrentName(name);
/*  709 */       this._currToken = JsonToken.FIELD_NAME;
/*  710 */       i = _skipColon();
/*      */     } 
/*  712 */     _updateLocation();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  718 */     switch (i) {
/*      */       case 34:
/*  720 */         this._tokenIncomplete = true;
/*  721 */         t = JsonToken.VALUE_STRING;
/*      */         break;
/*      */       case 91:
/*  724 */         if (!inObject) {
/*  725 */           this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*      */         }
/*  727 */         t = JsonToken.START_ARRAY;
/*      */         break;
/*      */       case 123:
/*  730 */         if (!inObject) {
/*  731 */           this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */         }
/*  733 */         t = JsonToken.START_OBJECT;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 125:
/*  738 */         _reportUnexpectedChar(i, "expected a value");
/*      */       case 116:
/*  740 */         _matchTrue();
/*  741 */         t = JsonToken.VALUE_TRUE;
/*      */         break;
/*      */       case 102:
/*  744 */         _matchFalse();
/*  745 */         t = JsonToken.VALUE_FALSE;
/*      */         break;
/*      */       case 110:
/*  748 */         _matchNull();
/*  749 */         t = JsonToken.VALUE_NULL;
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 45:
/*  757 */         t = _parseNegNumber();
/*      */         break;
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  769 */         t = _parsePosNumber(i);
/*      */         break;
/*      */       default:
/*  772 */         t = _handleOddValue(i);
/*      */         break;
/*      */     } 
/*      */     
/*  776 */     if (inObject) {
/*  777 */       this._nextToken = t;
/*  778 */       return this._currToken;
/*      */     } 
/*  780 */     this._currToken = t;
/*  781 */     return t;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  786 */     this._nameCopied = false;
/*  787 */     JsonToken t = this._nextToken;
/*  788 */     this._nextToken = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  793 */     if (t == JsonToken.START_ARRAY) {
/*  794 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  795 */     } else if (t == JsonToken.START_OBJECT) {
/*  796 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  798 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  803 */     if (this._tokenIncomplete) {
/*  804 */       this._tokenIncomplete = false;
/*  805 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString sstr) throws IOException {
/*  821 */     this._numTypesValid = 0;
/*  822 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  823 */       _nextAfterName();
/*  824 */       return false;
/*      */     } 
/*  826 */     if (this._tokenIncomplete) {
/*  827 */       _skipString();
/*      */     }
/*  829 */     int i = _skipWSOrEnd();
/*  830 */     if (i < 0) {
/*  831 */       close();
/*  832 */       this._currToken = null;
/*  833 */       return false;
/*      */     } 
/*  835 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  838 */     if (i == 93 || i == 125) {
/*  839 */       _closeScope(i);
/*  840 */       return false;
/*      */     } 
/*      */     
/*  843 */     if (this._parsingContext.expectComma()) {
/*  844 */       i = _skipComma(i);
/*      */ 
/*      */       
/*  847 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  848 */         i == 93 || i == 125)) {
/*  849 */         _closeScope(i);
/*  850 */         return false;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  855 */     if (!this._parsingContext.inObject()) {
/*  856 */       _updateLocation();
/*  857 */       _nextTokenNotInObject(i);
/*  858 */       return false;
/*      */     } 
/*      */     
/*  861 */     _updateNameLocation();
/*  862 */     if (i == 34) {
/*      */       
/*  864 */       char[] nameChars = sstr.asQuotedChars();
/*  865 */       int len = nameChars.length;
/*      */ 
/*      */       
/*  868 */       if (this._inputPtr + len + 4 < this._inputEnd) {
/*      */         
/*  870 */         int end = this._inputPtr + len;
/*  871 */         if (this._inputBuffer[end] == '"') {
/*  872 */           int offset = 0;
/*  873 */           int ptr = this._inputPtr;
/*      */           while (true) {
/*  875 */             if (ptr == end) {
/*  876 */               this._parsingContext.setCurrentName(sstr.getValue());
/*  877 */               _isNextTokenNameYes(_skipColonFast(ptr + 1));
/*  878 */               return true;
/*      */             } 
/*  880 */             if (nameChars[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/*  883 */             offset++;
/*  884 */             ptr++;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  889 */     return _isNextTokenNameMaybe(i, sstr.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextFieldName() throws IOException {
/*  897 */     this._numTypesValid = 0;
/*  898 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  899 */       _nextAfterName();
/*  900 */       return null;
/*      */     } 
/*  902 */     if (this._tokenIncomplete) {
/*  903 */       _skipString();
/*      */     }
/*  905 */     int i = _skipWSOrEnd();
/*  906 */     if (i < 0) {
/*  907 */       close();
/*  908 */       this._currToken = null;
/*  909 */       return null;
/*      */     } 
/*  911 */     this._binaryValue = null;
/*  912 */     if (i == 93 || i == 125) {
/*  913 */       _closeScope(i);
/*  914 */       return null;
/*      */     } 
/*  916 */     if (this._parsingContext.expectComma()) {
/*  917 */       i = _skipComma(i);
/*  918 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  919 */         i == 93 || i == 125)) {
/*  920 */         _closeScope(i);
/*  921 */         return null;
/*      */       } 
/*      */     } 
/*      */     
/*  925 */     if (!this._parsingContext.inObject()) {
/*  926 */       _updateLocation();
/*  927 */       _nextTokenNotInObject(i);
/*  928 */       return null;
/*      */     } 
/*      */     
/*  931 */     _updateNameLocation();
/*  932 */     String name = (i == 34) ? _parseName() : _handleOddName(i);
/*  933 */     this._parsingContext.setCurrentName(name);
/*  934 */     this._currToken = JsonToken.FIELD_NAME;
/*  935 */     i = _skipColon();
/*      */     
/*  937 */     _updateLocation();
/*  938 */     if (i == 34) {
/*  939 */       this._tokenIncomplete = true;
/*  940 */       this._nextToken = JsonToken.VALUE_STRING;
/*  941 */       return name;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  948 */     switch (i)
/*      */     { case 45:
/*  950 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  986 */         this._nextToken = t;
/*  987 */         return name;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return name;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return name;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return name;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return name;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return name;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return name; }  JsonToken t = _handleOddValue(i); this._nextToken = t; return name;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException {
/*  992 */     this._currToken = JsonToken.FIELD_NAME;
/*  993 */     _updateLocation();
/*      */     
/*  995 */     switch (i) {
/*      */       case 34:
/*  997 */         this._tokenIncomplete = true;
/*  998 */         this._nextToken = JsonToken.VALUE_STRING;
/*      */         return;
/*      */       case 91:
/* 1001 */         this._nextToken = JsonToken.START_ARRAY;
/*      */         return;
/*      */       case 123:
/* 1004 */         this._nextToken = JsonToken.START_OBJECT;
/*      */         return;
/*      */       case 116:
/* 1007 */         _matchToken("true", 1);
/* 1008 */         this._nextToken = JsonToken.VALUE_TRUE;
/*      */         return;
/*      */       case 102:
/* 1011 */         _matchToken("false", 1);
/* 1012 */         this._nextToken = JsonToken.VALUE_FALSE;
/*      */         return;
/*      */       case 110:
/* 1015 */         _matchToken("null", 1);
/* 1016 */         this._nextToken = JsonToken.VALUE_NULL;
/*      */         return;
/*      */       case 45:
/* 1019 */         this._nextToken = _parseNegNumber();
/*      */         return;
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/* 1031 */         this._nextToken = _parsePosNumber(i);
/*      */         return;
/*      */     } 
/* 1034 */     this._nextToken = _handleOddValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _isNextTokenNameMaybe(int i, String nameToMatch) throws IOException {
/* 1040 */     String name = (i == 34) ? _parseName() : _handleOddName(i);
/* 1041 */     this._parsingContext.setCurrentName(name);
/* 1042 */     this._currToken = JsonToken.FIELD_NAME;
/* 1043 */     i = _skipColon();
/* 1044 */     _updateLocation();
/* 1045 */     if (i == 34) {
/* 1046 */       this._tokenIncomplete = true;
/* 1047 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1048 */       return nameToMatch.equals(name);
/*      */     } 
/*      */ 
/*      */     
/* 1052 */     switch (i)
/*      */     { case 45:
/* 1054 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1090 */         this._nextToken = t;
/* 1091 */         return nameToMatch.equals(name);case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameToMatch.equals(name);case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameToMatch.equals(name);case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameToMatch.equals(name);case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameToMatch.equals(name);case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameToMatch.equals(name);case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameToMatch.equals(name); }  JsonToken t = _handleOddValue(i); this._nextToken = t; return nameToMatch.equals(name);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/* 1096 */     if (i == 34) {
/* 1097 */       this._tokenIncomplete = true;
/* 1098 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/* 1100 */     switch (i) {
/*      */       case 91:
/* 1102 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1103 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/* 1105 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/* 1106 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/* 1108 */         _matchToken("true", 1);
/* 1109 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/* 1111 */         _matchToken("false", 1);
/* 1112 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/* 1114 */         _matchToken("null", 1);
/* 1115 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/* 1117 */         return this._currToken = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/* 1132 */         return this._currToken = _parsePosNumber(i);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/*      */       case 93:
/* 1143 */         if ((this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 1144 */           this._inputPtr--;
/* 1145 */           return this._currToken = JsonToken.VALUE_NULL;
/*      */         }  break;
/*      */     } 
/* 1148 */     return this._currToken = _handleOddValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final String nextTextValue() throws IOException {
/* 1154 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1155 */       this._nameCopied = false;
/* 1156 */       JsonToken t = this._nextToken;
/* 1157 */       this._nextToken = null;
/* 1158 */       this._currToken = t;
/* 1159 */       if (t == JsonToken.VALUE_STRING) {
/* 1160 */         if (this._tokenIncomplete) {
/* 1161 */           this._tokenIncomplete = false;
/* 1162 */           _finishString();
/*      */         } 
/* 1164 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1166 */       if (t == JsonToken.START_ARRAY) {
/* 1167 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1168 */       } else if (t == JsonToken.START_OBJECT) {
/* 1169 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1171 */       return null;
/*      */     } 
/*      */     
/* 1174 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int nextIntValue(int defaultValue) throws IOException {
/* 1181 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1182 */       this._nameCopied = false;
/* 1183 */       JsonToken t = this._nextToken;
/* 1184 */       this._nextToken = null;
/* 1185 */       this._currToken = t;
/* 1186 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1187 */         return getIntValue();
/*      */       }
/* 1189 */       if (t == JsonToken.START_ARRAY) {
/* 1190 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1191 */       } else if (t == JsonToken.START_OBJECT) {
/* 1192 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1194 */       return defaultValue;
/*      */     } 
/*      */     
/* 1197 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long nextLongValue(long defaultValue) throws IOException {
/* 1204 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1205 */       this._nameCopied = false;
/* 1206 */       JsonToken t = this._nextToken;
/* 1207 */       this._nextToken = null;
/* 1208 */       this._currToken = t;
/* 1209 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1210 */         return getLongValue();
/*      */       }
/* 1212 */       if (t == JsonToken.START_ARRAY) {
/* 1213 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1214 */       } else if (t == JsonToken.START_OBJECT) {
/* 1215 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1217 */       return defaultValue;
/*      */     } 
/*      */     
/* 1220 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Boolean nextBooleanValue() throws IOException {
/* 1227 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1228 */       this._nameCopied = false;
/* 1229 */       JsonToken jsonToken = this._nextToken;
/* 1230 */       this._nextToken = null;
/* 1231 */       this._currToken = jsonToken;
/* 1232 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/* 1233 */         return Boolean.TRUE;
/*      */       }
/* 1235 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/* 1236 */         return Boolean.FALSE;
/*      */       }
/* 1238 */       if (jsonToken == JsonToken.START_ARRAY) {
/* 1239 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1240 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/* 1241 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1243 */       return null;
/*      */     } 
/* 1245 */     JsonToken t = nextToken();
/* 1246 */     if (t != null) {
/* 1247 */       int id = t.id();
/* 1248 */       if (id == 9) return Boolean.TRUE; 
/* 1249 */       if (id == 10) return Boolean.FALSE; 
/*      */     } 
/* 1251 */     return null;
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
/*      */   protected final JsonToken _parsePosNumber(int ch) throws IOException {
/* 1282 */     int ptr = this._inputPtr;
/* 1283 */     int startPtr = ptr - 1;
/* 1284 */     int inputLen = this._inputEnd;
/*      */ 
/*      */     
/* 1287 */     if (ch == 48) {
/* 1288 */       return _parseNumber2(false, startPtr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1297 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1302 */       if (ptr >= inputLen) {
/* 1303 */         this._inputPtr = startPtr;
/* 1304 */         return _parseNumber2(false, startPtr);
/*      */       } 
/* 1306 */       ch = this._inputBuffer[ptr++];
/* 1307 */       if (ch < 48 || ch > 57) {
/*      */         break;
/*      */       }
/* 1310 */       intLen++;
/*      */     } 
/* 1312 */     if (ch == 46 || ch == 101 || ch == 69) {
/* 1313 */       this._inputPtr = ptr;
/* 1314 */       return _parseFloat(ch, startPtr, ptr, false, intLen);
/*      */     } 
/*      */ 
/*      */     
/* 1318 */     this._inputPtr = --ptr;
/*      */     
/* 1320 */     if (this._parsingContext.inRoot()) {
/* 1321 */       _verifyRootSpace(ch);
/*      */     }
/* 1323 */     int len = ptr - startPtr;
/* 1324 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1325 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen) throws IOException {
/* 1331 */     int inputLen = this._inputEnd;
/* 1332 */     int fractLen = 0;
/*      */ 
/*      */     
/* 1335 */     if (ch == 46) {
/*      */       
/*      */       while (true) {
/* 1338 */         if (ptr >= inputLen) {
/* 1339 */           return _parseNumber2(neg, startPtr);
/*      */         }
/* 1341 */         ch = this._inputBuffer[ptr++];
/* 1342 */         if (ch < 48 || ch > 57) {
/*      */           break;
/*      */         }
/* 1345 */         fractLen++;
/*      */       } 
/*      */       
/* 1348 */       if (fractLen == 0) {
/* 1349 */         reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/* 1352 */     int expLen = 0;
/* 1353 */     if (ch == 101 || ch == 69) {
/* 1354 */       if (ptr >= inputLen) {
/* 1355 */         this._inputPtr = startPtr;
/* 1356 */         return _parseNumber2(neg, startPtr);
/*      */       } 
/*      */       
/* 1359 */       ch = this._inputBuffer[ptr++];
/* 1360 */       if (ch == 45 || ch == 43) {
/* 1361 */         if (ptr >= inputLen) {
/* 1362 */           this._inputPtr = startPtr;
/* 1363 */           return _parseNumber2(neg, startPtr);
/*      */         } 
/* 1365 */         ch = this._inputBuffer[ptr++];
/*      */       } 
/* 1367 */       while (ch <= 57 && ch >= 48) {
/* 1368 */         expLen++;
/* 1369 */         if (ptr >= inputLen) {
/* 1370 */           this._inputPtr = startPtr;
/* 1371 */           return _parseNumber2(neg, startPtr);
/*      */         } 
/* 1373 */         ch = this._inputBuffer[ptr++];
/*      */       } 
/*      */       
/* 1376 */       if (expLen == 0) {
/* 1377 */         reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1381 */     this._inputPtr = --ptr;
/*      */     
/* 1383 */     if (this._parsingContext.inRoot()) {
/* 1384 */       _verifyRootSpace(ch);
/*      */     }
/* 1386 */     int len = ptr - startPtr;
/* 1387 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*      */     
/* 1389 */     return resetFloat(neg, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken _parseNegNumber() throws IOException {
/* 1394 */     int ptr = this._inputPtr;
/* 1395 */     int startPtr = ptr - 1;
/* 1396 */     int inputLen = this._inputEnd;
/*      */     
/* 1398 */     if (ptr >= inputLen) {
/* 1399 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1401 */     int ch = this._inputBuffer[ptr++];
/*      */     
/* 1403 */     if (ch > 57 || ch < 48) {
/* 1404 */       this._inputPtr = ptr;
/* 1405 */       return _handleInvalidNumberStart(ch, true);
/*      */     } 
/*      */     
/* 1408 */     if (ch == 48) {
/* 1409 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1411 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1416 */       if (ptr >= inputLen) {
/* 1417 */         return _parseNumber2(true, startPtr);
/*      */       }
/* 1419 */       ch = this._inputBuffer[ptr++];
/* 1420 */       if (ch < 48 || ch > 57) {
/*      */         break;
/*      */       }
/* 1423 */       intLen++;
/*      */     } 
/*      */     
/* 1426 */     if (ch == 46 || ch == 101 || ch == 69) {
/* 1427 */       this._inputPtr = ptr;
/* 1428 */       return _parseFloat(ch, startPtr, ptr, true, intLen);
/*      */     } 
/*      */     
/* 1431 */     this._inputPtr = --ptr;
/* 1432 */     if (this._parsingContext.inRoot()) {
/* 1433 */       _verifyRootSpace(ch);
/*      */     }
/* 1435 */     int len = ptr - startPtr;
/* 1436 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1437 */     return resetInt(true, intLen);
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
/*      */   private final JsonToken _parseNumber2(boolean neg, int startPtr) throws IOException {
/* 1449 */     this._inputPtr = neg ? (startPtr + 1) : startPtr;
/* 1450 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1451 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1454 */     if (neg) {
/* 1455 */       outBuf[outPtr++] = '-';
/*      */     }
/*      */ 
/*      */     
/* 1459 */     int intLen = 0;
/*      */     
/* 1461 */     char c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("No digit following minus sign", JsonToken.VALUE_NUMBER_INT);
/* 1462 */     if (c == '0') {
/* 1463 */       c = _verifyNoLeadingZeroes();
/*      */     }
/* 1465 */     boolean eof = false;
/*      */ 
/*      */ 
/*      */     
/* 1469 */     while (c >= '0' && c <= '9') {
/* 1470 */       intLen++;
/* 1471 */       if (outPtr >= outBuf.length) {
/* 1472 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1473 */         outPtr = 0;
/*      */       } 
/* 1475 */       outBuf[outPtr++] = c;
/* 1476 */       if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */         
/* 1478 */         c = Character.MIN_VALUE;
/* 1479 */         eof = true;
/*      */         break;
/*      */       } 
/* 1482 */       c = this._inputBuffer[this._inputPtr++];
/*      */     } 
/*      */     
/* 1485 */     if (intLen == 0) {
/* 1486 */       return _handleInvalidNumberStart(c, neg);
/*      */     }
/*      */     
/* 1489 */     int fractLen = 0;
/*      */     
/* 1491 */     if (c == '.') {
/* 1492 */       if (outPtr >= outBuf.length) {
/* 1493 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1494 */         outPtr = 0;
/*      */       } 
/* 1496 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1500 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1501 */           eof = true;
/*      */           break;
/*      */         } 
/* 1504 */         c = this._inputBuffer[this._inputPtr++];
/* 1505 */         if (c < '0' || c > '9') {
/*      */           break;
/*      */         }
/* 1508 */         fractLen++;
/* 1509 */         if (outPtr >= outBuf.length) {
/* 1510 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1511 */           outPtr = 0;
/*      */         } 
/* 1513 */         outBuf[outPtr++] = c;
/*      */       } 
/*      */       
/* 1516 */       if (fractLen == 0) {
/* 1517 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1521 */     int expLen = 0;
/* 1522 */     if (c == 'e' || c == 'E') {
/* 1523 */       if (outPtr >= outBuf.length) {
/* 1524 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1525 */         outPtr = 0;
/*      */       } 
/* 1527 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 1530 */       c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*      */       
/* 1532 */       if (c == '-' || c == '+') {
/* 1533 */         if (outPtr >= outBuf.length) {
/* 1534 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1535 */           outPtr = 0;
/*      */         } 
/* 1537 */         outBuf[outPtr++] = c;
/*      */ 
/*      */         
/* 1540 */         c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*      */       } 
/*      */ 
/*      */       
/* 1544 */       while (c <= '9' && c >= '0') {
/* 1545 */         expLen++;
/* 1546 */         if (outPtr >= outBuf.length) {
/* 1547 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1548 */           outPtr = 0;
/*      */         } 
/* 1550 */         outBuf[outPtr++] = c;
/* 1551 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1552 */           eof = true;
/*      */           break;
/*      */         } 
/* 1555 */         c = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */       
/* 1558 */       if (expLen == 0) {
/* 1559 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1564 */     if (!eof) {
/* 1565 */       this._inputPtr--;
/* 1566 */       if (this._parsingContext.inRoot()) {
/* 1567 */         _verifyRootSpace(c);
/*      */       }
/*      */     } 
/* 1570 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1572 */     return reset(neg, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final char _verifyNoLeadingZeroes() throws IOException {
/* 1582 */     if (this._inputPtr < this._inputEnd) {
/* 1583 */       char ch = this._inputBuffer[this._inputPtr];
/*      */       
/* 1585 */       if (ch < '0' || ch > '9') {
/* 1586 */         return '0';
/*      */       }
/*      */     } 
/*      */     
/* 1590 */     return _verifyNLZ2();
/*      */   }
/*      */ 
/*      */   
/*      */   private char _verifyNLZ2() throws IOException {
/* 1595 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1596 */       return '0';
/*      */     }
/* 1598 */     char ch = this._inputBuffer[this._inputPtr];
/* 1599 */     if (ch < '0' || ch > '9') {
/* 1600 */       return '0';
/*      */     }
/* 1602 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1603 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1606 */     this._inputPtr++;
/* 1607 */     if (ch == '0') {
/* 1608 */       while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 1609 */         ch = this._inputBuffer[this._inputPtr];
/* 1610 */         if (ch < '0' || ch > '9') {
/* 1611 */           return '0';
/*      */         }
/* 1613 */         this._inputPtr++;
/* 1614 */         if (ch != '0') {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 1619 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException {
/* 1628 */     if (ch == 73) {
/* 1629 */       if (this._inputPtr >= this._inputEnd && 
/* 1630 */         !_loadMore()) {
/* 1631 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */       }
/*      */       
/* 1634 */       ch = this._inputBuffer[this._inputPtr++];
/* 1635 */       if (ch == 78) {
/* 1636 */         String match = negative ? "-INF" : "+INF";
/* 1637 */         _matchToken(match, 3);
/* 1638 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1639 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1641 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1642 */       } else if (ch == 110) {
/* 1643 */         String match = negative ? "-Infinity" : "+Infinity";
/* 1644 */         _matchToken(match, 3);
/* 1645 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1646 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1648 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */       } 
/*      */     } 
/* 1651 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1652 */     return null;
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
/*      */   private final void _verifyRootSpace(int ch) throws IOException {
/* 1665 */     this._inputPtr++;
/* 1666 */     switch (ch) {
/*      */       case 9:
/*      */       case 32:
/*      */         return;
/*      */       case 13:
/* 1671 */         _skipCR();
/*      */         return;
/*      */       case 10:
/* 1674 */         this._currInputRow++;
/* 1675 */         this._currInputRowStart = this._inputPtr;
/*      */         return;
/*      */     } 
/* 1678 */     _reportMissingRootWS(ch);
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
/*      */   protected final String _parseName() throws IOException {
/* 1691 */     int ptr = this._inputPtr;
/* 1692 */     int hash = this._hashSeed;
/* 1693 */     int[] codes = _icLatin1;
/*      */     
/* 1695 */     while (ptr < this._inputEnd) {
/* 1696 */       int ch = this._inputBuffer[ptr];
/* 1697 */       if (ch < codes.length && codes[ch] != 0) {
/* 1698 */         if (ch == 34) {
/* 1699 */           int i = this._inputPtr;
/* 1700 */           this._inputPtr = ptr + 1;
/* 1701 */           return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1705 */       hash = hash * 33 + ch;
/* 1706 */       ptr++;
/*      */     } 
/* 1708 */     int start = this._inputPtr;
/* 1709 */     this._inputPtr = ptr;
/* 1710 */     return _parseName2(start, hash, 34);
/*      */   }
/*      */ 
/*      */   
/*      */   private String _parseName2(int startPtr, int hash, int endChar) throws IOException {
/* 1715 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1720 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1721 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     while (true) {
/* 1724 */       if (this._inputPtr >= this._inputEnd && 
/* 1725 */         !_loadMore()) {
/* 1726 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 1729 */       char c = this._inputBuffer[this._inputPtr++];
/* 1730 */       int i = c;
/* 1731 */       if (i <= 92) {
/* 1732 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1737 */           c = _decodeEscaped();
/* 1738 */         } else if (i <= endChar) {
/* 1739 */           if (i == endChar) {
/*      */             break;
/*      */           }
/* 1742 */           if (i < 32) {
/* 1743 */             _throwUnquotedSpace(i, "name");
/*      */           }
/*      */         } 
/*      */       }
/* 1747 */       hash = hash * 33 + c;
/*      */       
/* 1749 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 1752 */       if (outPtr >= outBuf.length) {
/* 1753 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1754 */         outPtr = 0;
/*      */       } 
/*      */     } 
/* 1757 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1759 */     TextBuffer tb = this._textBuffer;
/* 1760 */     char[] buf = tb.getTextBuffer();
/* 1761 */     int start = tb.getTextOffset();
/* 1762 */     int len = tb.size();
/* 1763 */     return this._symbols.findSymbol(buf, start, len, hash);
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
/*      */   protected String _handleOddName(int i) throws IOException {
/*      */     boolean firstOk;
/* 1776 */     if (i == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1777 */       return _parseAposName();
/*      */     }
/*      */     
/* 1780 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 1781 */       _reportUnexpectedChar(i, "was expecting double-quote to start field name");
/*      */     }
/* 1783 */     int[] codes = CharTypes.getInputCodeLatin1JsNames();
/* 1784 */     int maxCode = codes.length;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1789 */     if (i < maxCode) {
/* 1790 */       firstOk = (codes[i] == 0);
/*      */     } else {
/* 1792 */       firstOk = Character.isJavaIdentifierPart((char)i);
/*      */     } 
/* 1794 */     if (!firstOk) {
/* 1795 */       _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/* 1797 */     int ptr = this._inputPtr;
/* 1798 */     int hash = this._hashSeed;
/* 1799 */     int inputLen = this._inputEnd;
/*      */     
/* 1801 */     if (ptr < inputLen) {
/*      */       do {
/* 1803 */         int ch = this._inputBuffer[ptr];
/* 1804 */         if (ch < maxCode) {
/* 1805 */           if (codes[ch] != 0) {
/* 1806 */             int j = this._inputPtr - 1;
/* 1807 */             this._inputPtr = ptr;
/* 1808 */             return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*      */           } 
/* 1810 */         } else if (!Character.isJavaIdentifierPart((char)ch)) {
/* 1811 */           int j = this._inputPtr - 1;
/* 1812 */           this._inputPtr = ptr;
/* 1813 */           return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*      */         } 
/* 1815 */         hash = hash * 33 + ch;
/* 1816 */         ++ptr;
/* 1817 */       } while (ptr < inputLen);
/*      */     }
/* 1819 */     int start = this._inputPtr - 1;
/* 1820 */     this._inputPtr = ptr;
/* 1821 */     return _handleOddName2(start, hash, codes);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 1827 */     int ptr = this._inputPtr;
/* 1828 */     int hash = this._hashSeed;
/* 1829 */     int inputLen = this._inputEnd;
/*      */     
/* 1831 */     if (ptr < inputLen) {
/* 1832 */       int[] codes = _icLatin1;
/* 1833 */       int maxCode = codes.length;
/*      */       
/*      */       do {
/* 1836 */         int ch = this._inputBuffer[ptr];
/* 1837 */         if (ch == 39) {
/* 1838 */           int i = this._inputPtr;
/* 1839 */           this._inputPtr = ptr + 1;
/* 1840 */           return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*      */         } 
/* 1842 */         if (ch < maxCode && codes[ch] != 0) {
/*      */           break;
/*      */         }
/* 1845 */         hash = hash * 33 + ch;
/* 1846 */         ++ptr;
/* 1847 */       } while (ptr < inputLen);
/*      */     } 
/*      */     
/* 1850 */     int start = this._inputPtr;
/* 1851 */     this._inputPtr = ptr;
/*      */     
/* 1853 */     return _parseName2(start, hash, 39);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleOddValue(int i) throws IOException {
/* 1863 */     switch (i) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 39:
/* 1870 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1871 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/* 1879 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */       
/*      */       case 44:
/* 1884 */         if ((this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 1885 */           this._inputPtr--;
/* 1886 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */         break;
/*      */       case 78:
/* 1890 */         _matchToken("NaN", 1);
/* 1891 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1892 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 1894 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 1897 */         _matchToken("Infinity", 1);
/* 1898 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1899 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 1901 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 1904 */         if (this._inputPtr >= this._inputEnd && 
/* 1905 */           !_loadMore()) {
/* 1906 */           _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */         }
/*      */         
/* 1909 */         return _handleInvalidNumberStart(this._inputBuffer[this._inputPtr++], false);
/*      */     } 
/*      */     
/* 1912 */     if (Character.isJavaIdentifierStart(i)) {
/* 1913 */       _reportInvalidToken("" + (char)i, _validJsonTokenList());
/*      */     }
/*      */     
/* 1916 */     _reportUnexpectedChar(i, "expected a valid value " + _validJsonValueList());
/* 1917 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 1922 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1923 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     while (true) {
/* 1926 */       if (this._inputPtr >= this._inputEnd && 
/* 1927 */         !_loadMore()) {
/* 1928 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */ 
/*      */       
/* 1932 */       char c = this._inputBuffer[this._inputPtr++];
/* 1933 */       int i = c;
/* 1934 */       if (i <= 92) {
/* 1935 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1940 */           c = _decodeEscaped();
/* 1941 */         } else if (i <= 39) {
/* 1942 */           if (i == 39) {
/*      */             break;
/*      */           }
/* 1945 */           if (i < 32) {
/* 1946 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/* 1951 */       if (outPtr >= outBuf.length) {
/* 1952 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1953 */         outPtr = 0;
/*      */       } 
/*      */       
/* 1956 */       outBuf[outPtr++] = c;
/*      */     } 
/* 1958 */     this._textBuffer.setCurrentLength(outPtr);
/* 1959 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException {
/* 1964 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/* 1965 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1966 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 1967 */     int maxCode = codes.length;
/*      */ 
/*      */     
/* 1970 */     while (this._inputPtr < this._inputEnd || 
/* 1971 */       _loadMore()) {
/*      */ 
/*      */ 
/*      */       
/* 1975 */       char c = this._inputBuffer[this._inputPtr];
/* 1976 */       int i = c;
/* 1977 */       if ((i < maxCode) ? (
/* 1978 */         codes[i] != 0) : 
/*      */ 
/*      */         
/* 1981 */         !Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 1984 */       this._inputPtr++;
/* 1985 */       hash = hash * 33 + i;
/*      */       
/* 1987 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 1990 */       if (outPtr >= outBuf.length) {
/* 1991 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1992 */         outPtr = 0;
/*      */       } 
/*      */     } 
/* 1995 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1997 */     TextBuffer tb = this._textBuffer;
/* 1998 */     char[] buf = tb.getTextBuffer();
/* 1999 */     int start = tb.getTextOffset();
/* 2000 */     int len = tb.size();
/*      */     
/* 2002 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _finishString() throws IOException {
/* 2013 */     int ptr = this._inputPtr;
/* 2014 */     int inputLen = this._inputEnd;
/*      */     
/* 2016 */     if (ptr < inputLen) {
/* 2017 */       int[] codes = _icLatin1;
/* 2018 */       int maxCode = codes.length;
/*      */       
/*      */       do {
/* 2021 */         int ch = this._inputBuffer[ptr];
/* 2022 */         if (ch < maxCode && codes[ch] != 0) {
/* 2023 */           if (ch == 34) {
/* 2024 */             this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2025 */             this._inputPtr = ptr + 1;
/*      */             
/*      */             return;
/*      */           } 
/*      */           break;
/*      */         } 
/* 2031 */         ++ptr;
/* 2032 */       } while (ptr < inputLen);
/*      */     } 
/*      */ 
/*      */     
/* 2036 */     this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2037 */     this._inputPtr = ptr;
/* 2038 */     _finishString2();
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _finishString2() throws IOException {
/* 2043 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 2044 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2045 */     int[] codes = _icLatin1;
/* 2046 */     int maxCode = codes.length;
/*      */     
/*      */     while (true) {
/* 2049 */       if (this._inputPtr >= this._inputEnd && 
/* 2050 */         !_loadMore()) {
/* 2051 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */ 
/*      */       
/* 2055 */       char c = this._inputBuffer[this._inputPtr++];
/* 2056 */       int i = c;
/* 2057 */       if (i < maxCode && codes[i] != 0) {
/* 2058 */         if (i == 34)
/*      */           break; 
/* 2060 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2065 */           c = _decodeEscaped();
/* 2066 */         } else if (i < 32) {
/* 2067 */           _throwUnquotedSpace(i, "string value");
/*      */         } 
/*      */       } 
/*      */       
/* 2071 */       if (outPtr >= outBuf.length) {
/* 2072 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2073 */         outPtr = 0;
/*      */       } 
/*      */       
/* 2076 */       outBuf[outPtr++] = c;
/*      */     } 
/* 2078 */     this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _skipString() throws IOException {
/* 2088 */     this._tokenIncomplete = false;
/*      */     
/* 2090 */     int inPtr = this._inputPtr;
/* 2091 */     int inLen = this._inputEnd;
/* 2092 */     char[] inBuf = this._inputBuffer;
/*      */     
/*      */     while (true) {
/* 2095 */       if (inPtr >= inLen) {
/* 2096 */         this._inputPtr = inPtr;
/* 2097 */         if (!_loadMore()) {
/* 2098 */           _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */         }
/*      */         
/* 2101 */         inPtr = this._inputPtr;
/* 2102 */         inLen = this._inputEnd;
/*      */       } 
/* 2104 */       char c = inBuf[inPtr++];
/* 2105 */       int i = c;
/* 2106 */       if (i <= 92) {
/* 2107 */         if (i == 92) {
/*      */ 
/*      */           
/* 2110 */           this._inputPtr = inPtr;
/* 2111 */           _decodeEscaped();
/* 2112 */           inPtr = this._inputPtr;
/* 2113 */           inLen = this._inputEnd; continue;
/* 2114 */         }  if (i <= 34) {
/* 2115 */           if (i == 34) {
/* 2116 */             this._inputPtr = inPtr;
/*      */             break;
/*      */           } 
/* 2119 */           if (i < 32) {
/* 2120 */             this._inputPtr = inPtr;
/* 2121 */             _throwUnquotedSpace(i, "string value");
/*      */           } 
/*      */         } 
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
/*      */   protected final void _skipCR() throws IOException {
/* 2139 */     if ((this._inputPtr < this._inputEnd || _loadMore()) && 
/* 2140 */       this._inputBuffer[this._inputPtr] == '\n') {
/* 2141 */       this._inputPtr++;
/*      */     }
/*      */     
/* 2144 */     this._currInputRow++;
/* 2145 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 2150 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 2151 */       return _skipColon2(false);
/*      */     }
/* 2153 */     char c = this._inputBuffer[this._inputPtr];
/* 2154 */     if (c == ':') {
/* 2155 */       int i = this._inputBuffer[++this._inputPtr];
/* 2156 */       if (i > 32) {
/* 2157 */         if (i == 47 || i == 35) {
/* 2158 */           return _skipColon2(true);
/*      */         }
/* 2160 */         this._inputPtr++;
/* 2161 */         return i;
/*      */       } 
/* 2163 */       if (i == 32 || i == 9) {
/* 2164 */         i = this._inputBuffer[++this._inputPtr];
/* 2165 */         if (i > 32) {
/* 2166 */           if (i == 47 || i == 35) {
/* 2167 */             return _skipColon2(true);
/*      */           }
/* 2169 */           this._inputPtr++;
/* 2170 */           return i;
/*      */         } 
/*      */       } 
/* 2173 */       return _skipColon2(true);
/*      */     } 
/* 2175 */     if (c == ' ' || c == '\t') {
/* 2176 */       c = this._inputBuffer[++this._inputPtr];
/*      */     }
/* 2178 */     if (c == ':') {
/* 2179 */       int i = this._inputBuffer[++this._inputPtr];
/* 2180 */       if (i > 32) {
/* 2181 */         if (i == 47 || i == 35) {
/* 2182 */           return _skipColon2(true);
/*      */         }
/* 2184 */         this._inputPtr++;
/* 2185 */         return i;
/*      */       } 
/* 2187 */       if (i == 32 || i == 9) {
/* 2188 */         i = this._inputBuffer[++this._inputPtr];
/* 2189 */         if (i > 32) {
/* 2190 */           if (i == 47 || i == 35) {
/* 2191 */             return _skipColon2(true);
/*      */           }
/* 2193 */           this._inputPtr++;
/* 2194 */           return i;
/*      */         } 
/*      */       } 
/* 2197 */       return _skipColon2(true);
/*      */     } 
/* 2199 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException {
/* 2204 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2205 */       int i = this._inputBuffer[this._inputPtr++];
/* 2206 */       if (i > 32) {
/* 2207 */         if (i == 47) {
/* 2208 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2211 */         if (i == 35 && 
/* 2212 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2216 */         if (gotColon) {
/* 2217 */           return i;
/*      */         }
/* 2219 */         if (i != 58) {
/* 2220 */           _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */         }
/* 2222 */         gotColon = true;
/*      */         continue;
/*      */       } 
/* 2225 */       if (i < 32) {
/* 2226 */         if (i == 10) {
/* 2227 */           this._currInputRow++;
/* 2228 */           this._currInputRowStart = this._inputPtr; continue;
/* 2229 */         }  if (i == 13) {
/* 2230 */           _skipCR(); continue;
/* 2231 */         }  if (i != 9) {
/* 2232 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2236 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 2238 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipColonFast(int ptr) throws IOException {
/* 2244 */     int i = this._inputBuffer[ptr++];
/* 2245 */     if (i == 58) {
/* 2246 */       i = this._inputBuffer[ptr++];
/* 2247 */       if (i > 32) {
/* 2248 */         if (i != 47 && i != 35) {
/* 2249 */           this._inputPtr = ptr;
/* 2250 */           return i;
/*      */         } 
/* 2252 */       } else if (i == 32 || i == 9) {
/* 2253 */         i = this._inputBuffer[ptr++];
/* 2254 */         if (i > 32 && 
/* 2255 */           i != 47 && i != 35) {
/* 2256 */           this._inputPtr = ptr;
/* 2257 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 2261 */       this._inputPtr = ptr - 1;
/* 2262 */       return _skipColon2(true);
/*      */     } 
/* 2264 */     if (i == 32 || i == 9) {
/* 2265 */       i = this._inputBuffer[ptr++];
/*      */     }
/* 2267 */     boolean gotColon = (i == 58);
/* 2268 */     if (gotColon) {
/* 2269 */       i = this._inputBuffer[ptr++];
/* 2270 */       if (i > 32) {
/* 2271 */         if (i != 47 && i != 35) {
/* 2272 */           this._inputPtr = ptr;
/* 2273 */           return i;
/*      */         } 
/* 2275 */       } else if (i == 32 || i == 9) {
/* 2276 */         i = this._inputBuffer[ptr++];
/* 2277 */         if (i > 32 && 
/* 2278 */           i != 47 && i != 35) {
/* 2279 */           this._inputPtr = ptr;
/* 2280 */           return i;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 2285 */     this._inputPtr = ptr - 1;
/* 2286 */     return _skipColon2(gotColon);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipComma(int i) throws IOException {
/* 2292 */     if (i != 44) {
/* 2293 */       _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     }
/* 2295 */     while (this._inputPtr < this._inputEnd) {
/* 2296 */       i = this._inputBuffer[this._inputPtr++];
/* 2297 */       if (i > 32) {
/* 2298 */         if (i == 47 || i == 35) {
/* 2299 */           this._inputPtr--;
/* 2300 */           return _skipAfterComma2();
/*      */         } 
/* 2302 */         return i;
/*      */       } 
/* 2304 */       if (i < 32) {
/* 2305 */         if (i == 10) {
/* 2306 */           this._currInputRow++;
/* 2307 */           this._currInputRowStart = this._inputPtr; continue;
/* 2308 */         }  if (i == 13) {
/* 2309 */           _skipCR(); continue;
/* 2310 */         }  if (i != 9) {
/* 2311 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2315 */     return _skipAfterComma2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipAfterComma2() throws IOException {
/* 2320 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2321 */       int i = this._inputBuffer[this._inputPtr++];
/* 2322 */       if (i > 32) {
/* 2323 */         if (i == 47) {
/* 2324 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2327 */         if (i == 35 && 
/* 2328 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2332 */         return i;
/*      */       } 
/* 2334 */       if (i < 32) {
/* 2335 */         if (i == 10) {
/* 2336 */           this._currInputRow++;
/* 2337 */           this._currInputRowStart = this._inputPtr; continue;
/* 2338 */         }  if (i == 13) {
/* 2339 */           _skipCR(); continue;
/* 2340 */         }  if (i != 9) {
/* 2341 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2345 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 2352 */     if (this._inputPtr >= this._inputEnd && 
/* 2353 */       !_loadMore()) {
/* 2354 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 2357 */     int i = this._inputBuffer[this._inputPtr++];
/* 2358 */     if (i > 32) {
/* 2359 */       if (i == 47 || i == 35) {
/* 2360 */         this._inputPtr--;
/* 2361 */         return _skipWSOrEnd2();
/*      */       } 
/* 2363 */       return i;
/*      */     } 
/* 2365 */     if (i != 32) {
/* 2366 */       if (i == 10) {
/* 2367 */         this._currInputRow++;
/* 2368 */         this._currInputRowStart = this._inputPtr;
/* 2369 */       } else if (i == 13) {
/* 2370 */         _skipCR();
/* 2371 */       } else if (i != 9) {
/* 2372 */         _throwInvalidSpace(i);
/*      */       } 
/*      */     }
/*      */     
/* 2376 */     while (this._inputPtr < this._inputEnd) {
/* 2377 */       i = this._inputBuffer[this._inputPtr++];
/* 2378 */       if (i > 32) {
/* 2379 */         if (i == 47 || i == 35) {
/* 2380 */           this._inputPtr--;
/* 2381 */           return _skipWSOrEnd2();
/*      */         } 
/* 2383 */         return i;
/*      */       } 
/* 2385 */       if (i != 32) {
/* 2386 */         if (i == 10) {
/* 2387 */           this._currInputRow++;
/* 2388 */           this._currInputRowStart = this._inputPtr; continue;
/* 2389 */         }  if (i == 13) {
/* 2390 */           _skipCR(); continue;
/* 2391 */         }  if (i != 9) {
/* 2392 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2396 */     return _skipWSOrEnd2();
/*      */   }
/*      */ 
/*      */   
/*      */   private int _skipWSOrEnd2() throws IOException {
/*      */     while (true) {
/* 2402 */       if (this._inputPtr >= this._inputEnd && 
/* 2403 */         !_loadMore()) {
/* 2404 */         return _eofAsNextChar();
/*      */       }
/*      */       
/* 2407 */       int i = this._inputBuffer[this._inputPtr++];
/* 2408 */       if (i > 32) {
/* 2409 */         if (i == 47) {
/* 2410 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2413 */         if (i == 35 && 
/* 2414 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2418 */         return i;
/* 2419 */       }  if (i != 32) {
/* 2420 */         if (i == 10) {
/* 2421 */           this._currInputRow++;
/* 2422 */           this._currInputRowStart = this._inputPtr; continue;
/* 2423 */         }  if (i == 13) {
/* 2424 */           _skipCR(); continue;
/* 2425 */         }  if (i != 9) {
/* 2426 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _skipComment() throws IOException {
/* 2434 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 2435 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 2438 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 2439 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 2441 */     char c = this._inputBuffer[this._inputPtr++];
/* 2442 */     if (c == '/') {
/* 2443 */       _skipLine();
/* 2444 */     } else if (c == '*') {
/* 2445 */       _skipCComment();
/*      */     } else {
/* 2447 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _skipCComment() throws IOException {
/* 2454 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2455 */       int i = this._inputBuffer[this._inputPtr++];
/* 2456 */       if (i <= 42) {
/* 2457 */         if (i == 42) {
/* 2458 */           if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */             break;
/*      */           }
/* 2461 */           if (this._inputBuffer[this._inputPtr] == '/') {
/* 2462 */             this._inputPtr++;
/*      */             return;
/*      */           } 
/*      */           continue;
/*      */         } 
/* 2467 */         if (i < 32) {
/* 2468 */           if (i == 10) {
/* 2469 */             this._currInputRow++;
/* 2470 */             this._currInputRowStart = this._inputPtr; continue;
/* 2471 */           }  if (i == 13) {
/* 2472 */             _skipCR(); continue;
/* 2473 */           }  if (i != 9) {
/* 2474 */             _throwInvalidSpace(i);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2479 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _skipYAMLComment() throws IOException {
/* 2484 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 2485 */       return false;
/*      */     }
/* 2487 */     _skipLine();
/* 2488 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _skipLine() throws IOException {
/* 2494 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2495 */       int i = this._inputBuffer[this._inputPtr++];
/* 2496 */       if (i < 32) {
/* 2497 */         if (i == 10) {
/* 2498 */           this._currInputRow++;
/* 2499 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 2501 */         if (i == 13) {
/* 2502 */           _skipCR(); break;
/*      */         } 
/* 2504 */         if (i != 9) {
/* 2505 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 2514 */     if (this._inputPtr >= this._inputEnd && 
/* 2515 */       !_loadMore()) {
/* 2516 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 2519 */     char c = this._inputBuffer[this._inputPtr++];
/*      */     
/* 2521 */     switch (c) {
/*      */       
/*      */       case 'b':
/* 2524 */         return '\b';
/*      */       case 't':
/* 2526 */         return '\t';
/*      */       case 'n':
/* 2528 */         return '\n';
/*      */       case 'f':
/* 2530 */         return '\f';
/*      */       case 'r':
/* 2532 */         return '\r';
/*      */ 
/*      */       
/*      */       case '"':
/*      */       case '/':
/*      */       case '\\':
/* 2538 */         return c;
/*      */       
/*      */       case 'u':
/*      */         break;
/*      */       
/*      */       default:
/* 2544 */         return _handleUnrecognizedCharacterEscape(c);
/*      */     } 
/*      */ 
/*      */     
/* 2548 */     int value = 0;
/* 2549 */     for (int i = 0; i < 4; i++) {
/* 2550 */       if (this._inputPtr >= this._inputEnd && 
/* 2551 */         !_loadMore()) {
/* 2552 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 2555 */       int ch = this._inputBuffer[this._inputPtr++];
/* 2556 */       int digit = CharTypes.charToHex(ch);
/* 2557 */       if (digit < 0) {
/* 2558 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2560 */       value = value << 4 | digit;
/*      */     } 
/* 2562 */     return (char)value;
/*      */   }
/*      */   
/*      */   private final void _matchTrue() throws IOException {
/* 2566 */     int ptr = this._inputPtr;
/* 2567 */     if (ptr + 3 < this._inputEnd) {
/* 2568 */       char[] b = this._inputBuffer;
/* 2569 */       if (b[ptr] == 'r' && b[++ptr] == 'u' && b[++ptr] == 'e') {
/* 2570 */         char c = b[++ptr];
/* 2571 */         if (c < '0' || c == ']' || c == '}') {
/* 2572 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2578 */     _matchToken("true", 1);
/*      */   }
/*      */   
/*      */   private final void _matchFalse() throws IOException {
/* 2582 */     int ptr = this._inputPtr;
/* 2583 */     if (ptr + 4 < this._inputEnd) {
/* 2584 */       char[] b = this._inputBuffer;
/* 2585 */       if (b[ptr] == 'a' && b[++ptr] == 'l' && b[++ptr] == 's' && b[++ptr] == 'e') {
/* 2586 */         char c = b[++ptr];
/* 2587 */         if (c < '0' || c == ']' || c == '}') {
/* 2588 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2594 */     _matchToken("false", 1);
/*      */   }
/*      */   
/*      */   private final void _matchNull() throws IOException {
/* 2598 */     int ptr = this._inputPtr;
/* 2599 */     if (ptr + 3 < this._inputEnd) {
/* 2600 */       char[] b = this._inputBuffer;
/* 2601 */       if (b[ptr] == 'u' && b[++ptr] == 'l' && b[++ptr] == 'l') {
/* 2602 */         char c = b[++ptr];
/* 2603 */         if (c < '0' || c == ']' || c == '}') {
/* 2604 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2610 */     _matchToken("null", 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2618 */     int len = matchStr.length();
/* 2619 */     if (this._inputPtr + len >= this._inputEnd) {
/* 2620 */       _matchToken2(matchStr, i);
/*      */       
/*      */       return;
/*      */     } 
/*      */     while (true) {
/* 2625 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2626 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2628 */       this._inputPtr++;
/* 2629 */       if (++i >= len) {
/* 2630 */         int ch = this._inputBuffer[this._inputPtr];
/* 2631 */         if (ch >= 48 && ch != 93 && ch != 125)
/* 2632 */           _checkMatchEnd(matchStr, i, ch); 
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   private final void _matchToken2(String matchStr, int i) throws IOException {
/* 2638 */     int len = matchStr.length();
/*      */     do {
/* 2640 */       if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr
/* 2641 */         .charAt(i)) {
/* 2642 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2644 */       this._inputPtr++;
/* 2645 */     } while (++i < len);
/*      */ 
/*      */     
/* 2648 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */       return;
/*      */     }
/* 2651 */     int ch = this._inputBuffer[this._inputPtr];
/* 2652 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2653 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int c) throws IOException {
/* 2659 */     char ch = (char)c;
/* 2660 */     if (Character.isJavaIdentifierPart(ch)) {
/* 2661 */       _reportInvalidToken(matchStr.substring(0, i));
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
/*      */   protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
/* 2678 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2685 */       if (this._inputPtr >= this._inputEnd) {
/* 2686 */         _loadMoreGuaranteed();
/*      */       }
/* 2688 */       char ch = this._inputBuffer[this._inputPtr++];
/* 2689 */       if (ch > ' ') {
/* 2690 */         int bits = b64variant.decodeBase64Char(ch);
/* 2691 */         if (bits < 0) {
/* 2692 */           if (ch == '"') {
/* 2693 */             return builder.toByteArray();
/*      */           }
/* 2695 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2696 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 2700 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/* 2704 */         if (this._inputPtr >= this._inputEnd) {
/* 2705 */           _loadMoreGuaranteed();
/*      */         }
/* 2707 */         ch = this._inputBuffer[this._inputPtr++];
/* 2708 */         bits = b64variant.decodeBase64Char(ch);
/* 2709 */         if (bits < 0) {
/* 2710 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 2712 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/* 2715 */         if (this._inputPtr >= this._inputEnd) {
/* 2716 */           _loadMoreGuaranteed();
/*      */         }
/* 2718 */         ch = this._inputBuffer[this._inputPtr++];
/* 2719 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 2722 */         if (bits < 0) {
/* 2723 */           if (bits != -2) {
/*      */             
/* 2725 */             if (ch == '"') {
/* 2726 */               decodedData >>= 4;
/* 2727 */               builder.append(decodedData);
/* 2728 */               if (b64variant.usesPadding()) {
/* 2729 */                 this._inputPtr--;
/* 2730 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 2732 */               return builder.toByteArray();
/*      */             } 
/* 2734 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 2736 */           if (bits == -2) {
/*      */             
/* 2738 */             if (this._inputPtr >= this._inputEnd) {
/* 2739 */               _loadMoreGuaranteed();
/*      */             }
/* 2741 */             ch = this._inputBuffer[this._inputPtr++];
/* 2742 */             if (!b64variant.usesPaddingChar(ch) && 
/* 2743 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/* 2744 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 2748 */             decodedData >>= 4;
/* 2749 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         
/* 2755 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2757 */         if (this._inputPtr >= this._inputEnd) {
/* 2758 */           _loadMoreGuaranteed();
/*      */         }
/* 2760 */         ch = this._inputBuffer[this._inputPtr++];
/* 2761 */         bits = b64variant.decodeBase64Char(ch);
/* 2762 */         if (bits < 0) {
/* 2763 */           if (bits != -2) {
/*      */             
/* 2765 */             if (ch == '"') {
/* 2766 */               decodedData >>= 2;
/* 2767 */               builder.appendTwoBytes(decodedData);
/* 2768 */               if (b64variant.usesPadding()) {
/* 2769 */                 this._inputPtr--;
/* 2770 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 2772 */               return builder.toByteArray();
/*      */             } 
/* 2774 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 2776 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2782 */             decodedData >>= 2;
/* 2783 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         
/* 2789 */         decodedData = decodedData << 6 | bits;
/* 2790 */         builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation() {
/* 2803 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 2804 */       long total = this._currInputProcessed + this._nameStartOffset - 1L;
/* 2805 */       return new JsonLocation(_getSourceReference(), -1L, total, this._nameStartRow, this._nameStartCol);
/*      */     } 
/*      */     
/* 2808 */     return new JsonLocation(_getSourceReference(), -1L, this._tokenInputTotal - 1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 2814 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 2815 */     return new JsonLocation(_getSourceReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateLocation() {
/* 2823 */     int ptr = this._inputPtr;
/* 2824 */     this._tokenInputTotal = this._currInputProcessed + ptr;
/* 2825 */     this._tokenInputRow = this._currInputRow;
/* 2826 */     this._tokenInputCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateNameLocation() {
/* 2832 */     int ptr = this._inputPtr;
/* 2833 */     this._nameStartOffset = ptr;
/* 2834 */     this._nameStartRow = this._currInputRow;
/* 2835 */     this._nameStartCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart) throws IOException {
/* 2845 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
/* 2854 */     StringBuilder sb = new StringBuilder(matchedPart);
/* 2855 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2856 */       char c = this._inputBuffer[this._inputPtr];
/* 2857 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2860 */       this._inputPtr++;
/* 2861 */       sb.append(c);
/* 2862 */       if (sb.length() >= 256) {
/* 2863 */         sb.append("...");
/*      */         break;
/*      */       } 
/*      */     } 
/* 2867 */     _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _closeScope(int i) throws JsonParseException {
/* 2877 */     if (i == 93) {
/* 2878 */       _updateLocation();
/* 2879 */       if (!this._parsingContext.inArray()) {
/* 2880 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/* 2882 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2883 */       this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/* 2885 */     if (i == 125) {
/* 2886 */       _updateLocation();
/* 2887 */       if (!this._parsingContext.inObject()) {
/* 2888 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/* 2890 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2891 */       this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\ReaderBasedJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */