/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.base.ParserBase;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import java.io.DataInput;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
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
/*      */ public class UTF8DataInputJsonParser
/*      */   extends ParserBase
/*      */ {
/*      */   static final byte BYTE_LF = 10;
/*   41 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   43 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   45 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
/*      */   
/*   47 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   48 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   49 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*   50 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   51 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */   
/*   54 */   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
/*      */ 
/*      */ 
/*      */   
/*   58 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
/*      */ 
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
/*      */   
/*      */   protected final ByteQuadsCanonicalizer _symbols;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   87 */   protected int[] _quadBuffer = new int[16];
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
/*      */   private int _quad1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DataInput _inputData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   protected int _nextByte = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte) {
/*  125 */     super(ctxt, features);
/*  126 */     this._objectCodec = codec;
/*  127 */     this._symbols = sym;
/*  128 */     this._inputData = inputData;
/*  129 */     this._nextByte = firstByte;
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  134 */     return this._objectCodec;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCodec(ObjectCodec c) {
/*  139 */     this._objectCodec = c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  150 */     return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getInputSource() {
/*  155 */     return this._inputData;
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
/*      */   protected void _closeInput() throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() throws IOException {
/*  176 */     super._releaseBuffers();
/*      */     
/*  178 */     this._symbols.release();
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
/*      */   public String getText() throws IOException {
/*  190 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  191 */       if (this._tokenIncomplete) {
/*  192 */         this._tokenIncomplete = false;
/*  193 */         return _finishAndReturnString();
/*      */       } 
/*  195 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  197 */     return _getText2(this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getText(Writer writer) throws IOException {
/*  203 */     JsonToken t = this._currToken;
/*  204 */     if (t == JsonToken.VALUE_STRING) {
/*  205 */       if (this._tokenIncomplete) {
/*  206 */         this._tokenIncomplete = false;
/*  207 */         _finishString();
/*      */       } 
/*  209 */       return this._textBuffer.contentsToWriter(writer);
/*      */     } 
/*  211 */     if (t == JsonToken.FIELD_NAME) {
/*  212 */       String n = this._parsingContext.getCurrentName();
/*  213 */       writer.write(n);
/*  214 */       return n.length();
/*      */     } 
/*  216 */     if (t != null) {
/*  217 */       if (t.isNumeric()) {
/*  218 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  220 */       char[] ch = t.asCharArray();
/*  221 */       writer.write(ch);
/*  222 */       return ch.length;
/*      */     } 
/*  224 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString() throws IOException {
/*  231 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  232 */       if (this._tokenIncomplete) {
/*  233 */         this._tokenIncomplete = false;
/*  234 */         return _finishAndReturnString();
/*      */       } 
/*  236 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  238 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  239 */       return getCurrentName();
/*      */     }
/*  241 */     return super.getValueAsString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString(String defValue) throws IOException {
/*  247 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  248 */       if (this._tokenIncomplete) {
/*  249 */         this._tokenIncomplete = false;
/*  250 */         return _finishAndReturnString();
/*      */       } 
/*  252 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  254 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  255 */       return getCurrentName();
/*      */     }
/*  257 */     return super.getValueAsString(defValue);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt() throws IOException {
/*  263 */     JsonToken t = this._currToken;
/*  264 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  266 */       if ((this._numTypesValid & 0x1) == 0) {
/*  267 */         if (this._numTypesValid == 0) {
/*  268 */           return _parseIntValue();
/*      */         }
/*  270 */         if ((this._numTypesValid & 0x1) == 0) {
/*  271 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  274 */       return this._numberInt;
/*      */     } 
/*  276 */     return super.getValueAsInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt(int defValue) throws IOException {
/*  282 */     JsonToken t = this._currToken;
/*  283 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  285 */       if ((this._numTypesValid & 0x1) == 0) {
/*  286 */         if (this._numTypesValid == 0) {
/*  287 */           return _parseIntValue();
/*      */         }
/*  289 */         if ((this._numTypesValid & 0x1) == 0) {
/*  290 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  293 */       return this._numberInt;
/*      */     } 
/*  295 */     return super.getValueAsInt(defValue);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  300 */     if (t == null) {
/*  301 */       return null;
/*      */     }
/*  303 */     switch (t.id()) {
/*      */       case 5:
/*  305 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  311 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  313 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getTextCharacters() throws IOException {
/*  320 */     if (this._currToken != null) {
/*  321 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  324 */           if (!this._nameCopied) {
/*  325 */             String name = this._parsingContext.getCurrentName();
/*  326 */             int nameLen = name.length();
/*  327 */             if (this._nameCopyBuffer == null) {
/*  328 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  329 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  330 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  332 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  333 */             this._nameCopied = true;
/*      */           } 
/*  335 */           return this._nameCopyBuffer;
/*      */         
/*      */         case 6:
/*  338 */           if (this._tokenIncomplete) {
/*  339 */             this._tokenIncomplete = false;
/*  340 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  345 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*      */       
/*  348 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  351 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextLength() throws IOException {
/*  357 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  358 */       if (this._tokenIncomplete) {
/*  359 */         this._tokenIncomplete = false;
/*  360 */         _finishString();
/*      */       } 
/*  362 */       return this._textBuffer.size();
/*      */     } 
/*  364 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  365 */       return this._parsingContext.getCurrentName().length();
/*      */     }
/*  367 */     if (this._currToken != null) {
/*  368 */       if (this._currToken.isNumeric()) {
/*  369 */         return this._textBuffer.size();
/*      */       }
/*  371 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*  373 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextOffset() throws IOException {
/*  380 */     if (this._currToken != null) {
/*  381 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  383 */           return 0;
/*      */         case 6:
/*  385 */           if (this._tokenIncomplete) {
/*  386 */             this._tokenIncomplete = false;
/*  387 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  392 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  396 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  402 */     if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null))
/*      */     {
/*  404 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  409 */     if (this._tokenIncomplete) {
/*      */       try {
/*  411 */         this._binaryValue = _decodeBase64(b64variant);
/*  412 */       } catch (IllegalArgumentException iae) {
/*  413 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  418 */       this._tokenIncomplete = false;
/*      */     }
/*  420 */     else if (this._binaryValue == null) {
/*      */       
/*  422 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  423 */       _decodeBase64(getText(), builder, b64variant);
/*  424 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  427 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  434 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  435 */       byte[] b = getBinaryValue(b64variant);
/*  436 */       out.write(b);
/*  437 */       return b.length;
/*      */     } 
/*      */     
/*  440 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  442 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  444 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  451 */     int outputPtr = 0;
/*  452 */     int outputEnd = buffer.length - 3;
/*  453 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  459 */       int ch = this._inputData.readUnsignedByte();
/*  460 */       if (ch > 32) {
/*  461 */         int bits = b64variant.decodeBase64Char(ch);
/*  462 */         if (bits < 0) {
/*  463 */           if (ch == 34) {
/*      */             break;
/*      */           }
/*  466 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  467 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  473 */         if (outputPtr > outputEnd) {
/*  474 */           outputCount += outputPtr;
/*  475 */           out.write(buffer, 0, outputPtr);
/*  476 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  479 */         int decodedData = bits;
/*      */ 
/*      */         
/*  482 */         ch = this._inputData.readUnsignedByte();
/*  483 */         bits = b64variant.decodeBase64Char(ch);
/*  484 */         if (bits < 0) {
/*  485 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  487 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  490 */         ch = this._inputData.readUnsignedByte();
/*  491 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  494 */         if (bits < 0) {
/*  495 */           if (bits != -2) {
/*      */             
/*  497 */             if (ch == 34) {
/*  498 */               decodedData >>= 4;
/*  499 */               buffer[outputPtr++] = (byte)decodedData;
/*  500 */               if (b64variant.usesPadding()) {
/*  501 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/*      */               break;
/*      */             } 
/*  505 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  507 */           if (bits == -2) {
/*      */             
/*  509 */             ch = this._inputData.readUnsignedByte();
/*  510 */             if (!b64variant.usesPaddingChar(ch) && (
/*  511 */               ch != 92 || 
/*  512 */               _decodeBase64Escape(b64variant, ch, 3) != -2)) {
/*  513 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  517 */             decodedData >>= 4;
/*  518 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  523 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  525 */         ch = this._inputData.readUnsignedByte();
/*  526 */         bits = b64variant.decodeBase64Char(ch);
/*  527 */         if (bits < 0) {
/*  528 */           if (bits != -2) {
/*      */             
/*  530 */             if (ch == 34) {
/*  531 */               decodedData >>= 2;
/*  532 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  533 */               buffer[outputPtr++] = (byte)decodedData;
/*  534 */               if (b64variant.usesPadding()) {
/*  535 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/*      */               break;
/*      */             } 
/*  539 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  541 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  548 */             decodedData >>= 2;
/*  549 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  550 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  555 */         decodedData = decodedData << 6 | bits;
/*  556 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  557 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  558 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  560 */     }  this._tokenIncomplete = false;
/*  561 */     if (outputPtr > 0) {
/*  562 */       outputCount += outputPtr;
/*  563 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  565 */     return outputCount;
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
/*      */   public JsonToken nextToken() throws IOException {
/*  581 */     if (this._closed) {
/*  582 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  588 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  589 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  593 */     this._numTypesValid = 0;
/*  594 */     if (this._tokenIncomplete) {
/*  595 */       _skipString();
/*      */     }
/*  597 */     int i = _skipWSOrEnd();
/*  598 */     if (i < 0) {
/*      */       
/*  600 */       close();
/*  601 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  604 */     this._binaryValue = null;
/*  605 */     this._tokenInputRow = this._currInputRow;
/*      */ 
/*      */     
/*  608 */     if (i == 93 || i == 125) {
/*  609 */       _closeScope(i);
/*  610 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  614 */     if (this._parsingContext.expectComma()) {
/*  615 */       if (i != 44) {
/*  616 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  618 */       i = _skipWS();
/*      */ 
/*      */       
/*  621 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  622 */         i == 93 || i == 125)) {
/*  623 */         _closeScope(i);
/*  624 */         return this._currToken;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  633 */     if (!this._parsingContext.inObject()) {
/*  634 */       return _nextTokenNotInObject(i);
/*      */     }
/*      */     
/*  637 */     String n = _parseName(i);
/*  638 */     this._parsingContext.setCurrentName(n);
/*  639 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  641 */     i = _skipColon();
/*      */ 
/*      */     
/*  644 */     if (i == 34) {
/*  645 */       this._tokenIncomplete = true;
/*  646 */       this._nextToken = JsonToken.VALUE_STRING;
/*  647 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  651 */     switch (i)
/*      */     { case 45:
/*  653 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  694 */         this._nextToken = t;
/*  695 */         return this._currToken;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return this._currToken;case 102: _matchToken("false", 1); t = JsonToken.VALUE_FALSE; this._nextToken = t; return this._currToken;case 110: _matchToken("null", 1); t = JsonToken.VALUE_NULL; this._nextToken = t; return this._currToken;case 116: _matchToken("true", 1); t = JsonToken.VALUE_TRUE; this._nextToken = t; return this._currToken;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return this._currToken;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return this._currToken; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return this._currToken;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/*  700 */     if (i == 34) {
/*  701 */       this._tokenIncomplete = true;
/*  702 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/*  704 */     switch (i) {
/*      */       case 91:
/*  706 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  707 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/*  709 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  710 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/*  712 */         _matchToken("true", 1);
/*  713 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/*  715 */         _matchToken("false", 1);
/*  716 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/*  718 */         _matchToken("null", 1);
/*  719 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/*  721 */         return this._currToken = _parseNegNumber();
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
/*  736 */         return this._currToken = _parsePosNumber(i);
/*      */     } 
/*  738 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  743 */     this._nameCopied = false;
/*  744 */     JsonToken t = this._nextToken;
/*  745 */     this._nextToken = null;
/*      */ 
/*      */     
/*  748 */     if (t == JsonToken.START_ARRAY) {
/*  749 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  750 */     } else if (t == JsonToken.START_OBJECT) {
/*  751 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  753 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  758 */     if (this._tokenIncomplete) {
/*  759 */       this._tokenIncomplete = false;
/*  760 */       _finishString();
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
/*      */   public String nextFieldName() throws IOException {
/*  778 */     this._numTypesValid = 0;
/*  779 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  780 */       _nextAfterName();
/*  781 */       return null;
/*      */     } 
/*  783 */     if (this._tokenIncomplete) {
/*  784 */       _skipString();
/*      */     }
/*  786 */     int i = _skipWS();
/*  787 */     this._binaryValue = null;
/*  788 */     this._tokenInputRow = this._currInputRow;
/*      */     
/*  790 */     if (i == 93 || i == 125) {
/*  791 */       _closeScope(i);
/*  792 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  796 */     if (this._parsingContext.expectComma()) {
/*  797 */       if (i != 44) {
/*  798 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  800 */       i = _skipWS();
/*      */ 
/*      */       
/*  803 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  804 */         i == 93 || i == 125)) {
/*  805 */         _closeScope(i);
/*  806 */         return null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  811 */     if (!this._parsingContext.inObject()) {
/*  812 */       _nextTokenNotInObject(i);
/*  813 */       return null;
/*      */     } 
/*      */     
/*  816 */     String nameStr = _parseName(i);
/*  817 */     this._parsingContext.setCurrentName(nameStr);
/*  818 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  820 */     i = _skipColon();
/*  821 */     if (i == 34) {
/*  822 */       this._tokenIncomplete = true;
/*  823 */       this._nextToken = JsonToken.VALUE_STRING;
/*  824 */       return nameStr;
/*      */     } 
/*      */     
/*  827 */     switch (i)
/*      */     { case 45:
/*  829 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  865 */         this._nextToken = t;
/*  866 */         return nameStr;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameStr;case 102: _matchToken("false", 1); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameStr;case 110: _matchToken("null", 1); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameStr;case 116: _matchToken("true", 1); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameStr;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameStr;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameStr; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return nameStr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextTextValue() throws IOException {
/*  873 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  874 */       this._nameCopied = false;
/*  875 */       JsonToken t = this._nextToken;
/*  876 */       this._nextToken = null;
/*  877 */       this._currToken = t;
/*  878 */       if (t == JsonToken.VALUE_STRING) {
/*  879 */         if (this._tokenIncomplete) {
/*  880 */           this._tokenIncomplete = false;
/*  881 */           return _finishAndReturnString();
/*      */         } 
/*  883 */         return this._textBuffer.contentsAsString();
/*      */       } 
/*  885 */       if (t == JsonToken.START_ARRAY) {
/*  886 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  887 */       } else if (t == JsonToken.START_OBJECT) {
/*  888 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  890 */       return null;
/*      */     } 
/*  892 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextIntValue(int defaultValue) throws IOException {
/*  899 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  900 */       this._nameCopied = false;
/*  901 */       JsonToken t = this._nextToken;
/*  902 */       this._nextToken = null;
/*  903 */       this._currToken = t;
/*  904 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  905 */         return getIntValue();
/*      */       }
/*  907 */       if (t == JsonToken.START_ARRAY) {
/*  908 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  909 */       } else if (t == JsonToken.START_OBJECT) {
/*  910 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  912 */       return defaultValue;
/*      */     } 
/*  914 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long nextLongValue(long defaultValue) throws IOException {
/*  921 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  922 */       this._nameCopied = false;
/*  923 */       JsonToken t = this._nextToken;
/*  924 */       this._nextToken = null;
/*  925 */       this._currToken = t;
/*  926 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  927 */         return getLongValue();
/*      */       }
/*  929 */       if (t == JsonToken.START_ARRAY) {
/*  930 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  931 */       } else if (t == JsonToken.START_OBJECT) {
/*  932 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  934 */       return defaultValue;
/*      */     } 
/*  936 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean nextBooleanValue() throws IOException {
/*  943 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  944 */       this._nameCopied = false;
/*  945 */       JsonToken jsonToken = this._nextToken;
/*  946 */       this._nextToken = null;
/*  947 */       this._currToken = jsonToken;
/*  948 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/*  949 */         return Boolean.TRUE;
/*      */       }
/*  951 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/*  952 */         return Boolean.FALSE;
/*      */       }
/*  954 */       if (jsonToken == JsonToken.START_ARRAY) {
/*  955 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  956 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/*  957 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  959 */       return null;
/*      */     } 
/*      */     
/*  962 */     JsonToken t = nextToken();
/*  963 */     if (t == JsonToken.VALUE_TRUE) {
/*  964 */       return Boolean.TRUE;
/*      */     }
/*  966 */     if (t == JsonToken.VALUE_FALSE) {
/*  967 */       return Boolean.FALSE;
/*      */     }
/*  969 */     return null;
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
/*      */   protected JsonToken _parsePosNumber(int c) throws IOException {
/*      */     int outPtr;
/*  995 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1000 */     if (c == 48) {
/* 1001 */       c = _handleLeadingZeroes();
/* 1002 */       if (c <= 57 && c >= 48) {
/* 1003 */         outPtr = 0;
/*      */       } else {
/* 1005 */         outBuf[0] = '0';
/* 1006 */         outPtr = 1;
/*      */       } 
/*      */     } else {
/* 1009 */       outBuf[0] = (char)c;
/* 1010 */       c = this._inputData.readUnsignedByte();
/* 1011 */       outPtr = 1;
/*      */     } 
/* 1013 */     int intLen = outPtr;
/*      */ 
/*      */     
/* 1016 */     while (c <= 57 && c >= 48) {
/* 1017 */       intLen++;
/* 1018 */       outBuf[outPtr++] = (char)c;
/* 1019 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/* 1021 */     if (c == 46 || c == 101 || c == 69) {
/* 1022 */       return _parseFloat(outBuf, outPtr, c, false, intLen);
/*      */     }
/* 1024 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1026 */     if (this._parsingContext.inRoot()) {
/* 1027 */       _verifyRootSpace();
/*      */     } else {
/* 1029 */       this._nextByte = c;
/*      */     } 
/*      */     
/* 1032 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _parseNegNumber() throws IOException {
/* 1037 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1038 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1041 */     outBuf[outPtr++] = '-';
/* 1042 */     int c = this._inputData.readUnsignedByte();
/* 1043 */     outBuf[outPtr++] = (char)c;
/*      */     
/* 1045 */     if (c <= 48) {
/*      */       
/* 1047 */       if (c == 48) {
/* 1048 */         c = _handleLeadingZeroes();
/*      */       } else {
/* 1050 */         return _handleInvalidNumberStart(c, true);
/*      */       } 
/*      */     } else {
/* 1053 */       if (c > 57) {
/* 1054 */         return _handleInvalidNumberStart(c, true);
/*      */       }
/* 1056 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1059 */     int intLen = 1;
/*      */ 
/*      */     
/* 1062 */     while (c <= 57 && c >= 48) {
/* 1063 */       intLen++;
/* 1064 */       outBuf[outPtr++] = (char)c;
/* 1065 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/* 1067 */     if (c == 46 || c == 101 || c == 69) {
/* 1068 */       return _parseFloat(outBuf, outPtr, c, true, intLen);
/*      */     }
/* 1070 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1072 */     this._nextByte = c;
/* 1073 */     if (this._parsingContext.inRoot()) {
/* 1074 */       _verifyRootSpace();
/*      */     }
/*      */     
/* 1077 */     return resetInt(true, intLen);
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
/*      */   private final int _handleLeadingZeroes() throws IOException {
/* 1089 */     int ch = this._inputData.readUnsignedByte();
/*      */     
/* 1091 */     if (ch < 48 || ch > 57) {
/* 1092 */       return ch;
/*      */     }
/*      */     
/* 1095 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1096 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1099 */     while (ch == 48) {
/* 1100 */       ch = this._inputData.readUnsignedByte();
/*      */     }
/* 1102 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
/* 1108 */     int fractLen = 0;
/*      */ 
/*      */     
/* 1111 */     if (c == 46) {
/* 1112 */       outBuf[outPtr++] = (char)c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1116 */         c = this._inputData.readUnsignedByte();
/* 1117 */         if (c < 48 || c > 57) {
/*      */           break;
/*      */         }
/* 1120 */         fractLen++;
/* 1121 */         if (outPtr >= outBuf.length) {
/* 1122 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1123 */           outPtr = 0;
/*      */         } 
/* 1125 */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */       
/* 1128 */       if (fractLen == 0) {
/* 1129 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1133 */     int expLen = 0;
/* 1134 */     if (c == 101 || c == 69) {
/* 1135 */       if (outPtr >= outBuf.length) {
/* 1136 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1137 */         outPtr = 0;
/*      */       } 
/* 1139 */       outBuf[outPtr++] = (char)c;
/* 1140 */       c = this._inputData.readUnsignedByte();
/*      */       
/* 1142 */       if (c == 45 || c == 43) {
/* 1143 */         if (outPtr >= outBuf.length) {
/* 1144 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1145 */           outPtr = 0;
/*      */         } 
/* 1147 */         outBuf[outPtr++] = (char)c;
/* 1148 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/* 1150 */       while (c <= 57 && c >= 48) {
/* 1151 */         expLen++;
/* 1152 */         if (outPtr >= outBuf.length) {
/* 1153 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1154 */           outPtr = 0;
/*      */         } 
/* 1156 */         outBuf[outPtr++] = (char)c;
/* 1157 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/*      */       
/* 1160 */       if (expLen == 0) {
/* 1161 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1167 */     this._nextByte = c;
/* 1168 */     if (this._parsingContext.inRoot()) {
/* 1169 */       _verifyRootSpace();
/*      */     }
/* 1171 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1174 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
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
/*      */   private final void _verifyRootSpace() throws IOException {
/* 1187 */     int ch = this._nextByte;
/* 1188 */     if (ch <= 32) {
/* 1189 */       this._nextByte = -1;
/* 1190 */       if (ch == 13 || ch == 10) {
/* 1191 */         this._currInputRow++;
/*      */       }
/*      */       return;
/*      */     } 
/* 1195 */     _reportMissingRootWS(ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final String _parseName(int i) throws IOException {
/* 1206 */     if (i != 34) {
/* 1207 */       return _handleOddName(i);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1215 */     int[] codes = _icLatin1;
/*      */     
/* 1217 */     int q = this._inputData.readUnsignedByte();
/*      */     
/* 1219 */     if (codes[q] == 0) {
/* 1220 */       i = this._inputData.readUnsignedByte();
/* 1221 */       if (codes[i] == 0) {
/* 1222 */         q = q << 8 | i;
/* 1223 */         i = this._inputData.readUnsignedByte();
/* 1224 */         if (codes[i] == 0) {
/* 1225 */           q = q << 8 | i;
/* 1226 */           i = this._inputData.readUnsignedByte();
/* 1227 */           if (codes[i] == 0) {
/* 1228 */             q = q << 8 | i;
/* 1229 */             i = this._inputData.readUnsignedByte();
/* 1230 */             if (codes[i] == 0) {
/* 1231 */               this._quad1 = q;
/* 1232 */               return _parseMediumName(i);
/*      */             } 
/* 1234 */             if (i == 34) {
/* 1235 */               return findName(q, 4);
/*      */             }
/* 1237 */             return parseName(q, i, 4);
/*      */           } 
/* 1239 */           if (i == 34) {
/* 1240 */             return findName(q, 3);
/*      */           }
/* 1242 */           return parseName(q, i, 3);
/*      */         } 
/* 1244 */         if (i == 34) {
/* 1245 */           return findName(q, 2);
/*      */         }
/* 1247 */         return parseName(q, i, 2);
/*      */       } 
/* 1249 */       if (i == 34) {
/* 1250 */         return findName(q, 1);
/*      */       }
/* 1252 */       return parseName(q, i, 1);
/*      */     } 
/* 1254 */     if (q == 34) {
/* 1255 */       return "";
/*      */     }
/* 1257 */     return parseName(0, q, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName(int q2) throws IOException {
/* 1262 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1265 */     int i = this._inputData.readUnsignedByte();
/* 1266 */     if (codes[i] != 0) {
/* 1267 */       if (i == 34) {
/* 1268 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1270 */       return parseName(this._quad1, q2, i, 1);
/*      */     } 
/* 1272 */     q2 = q2 << 8 | i;
/* 1273 */     i = this._inputData.readUnsignedByte();
/* 1274 */     if (codes[i] != 0) {
/* 1275 */       if (i == 34) {
/* 1276 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1278 */       return parseName(this._quad1, q2, i, 2);
/*      */     } 
/* 1280 */     q2 = q2 << 8 | i;
/* 1281 */     i = this._inputData.readUnsignedByte();
/* 1282 */     if (codes[i] != 0) {
/* 1283 */       if (i == 34) {
/* 1284 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1286 */       return parseName(this._quad1, q2, i, 3);
/*      */     } 
/* 1288 */     q2 = q2 << 8 | i;
/* 1289 */     i = this._inputData.readUnsignedByte();
/* 1290 */     if (codes[i] != 0) {
/* 1291 */       if (i == 34) {
/* 1292 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1294 */       return parseName(this._quad1, q2, i, 4);
/*      */     } 
/* 1296 */     return _parseMediumName2(i, q2);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName2(int q3, int q2) throws IOException {
/* 1301 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1304 */     int i = this._inputData.readUnsignedByte();
/* 1305 */     if (codes[i] != 0) {
/* 1306 */       if (i == 34) {
/* 1307 */         return findName(this._quad1, q2, q3, 1);
/*      */       }
/* 1309 */       return parseName(this._quad1, q2, q3, i, 1);
/*      */     } 
/* 1311 */     q3 = q3 << 8 | i;
/* 1312 */     i = this._inputData.readUnsignedByte();
/* 1313 */     if (codes[i] != 0) {
/* 1314 */       if (i == 34) {
/* 1315 */         return findName(this._quad1, q2, q3, 2);
/*      */       }
/* 1317 */       return parseName(this._quad1, q2, q3, i, 2);
/*      */     } 
/* 1319 */     q3 = q3 << 8 | i;
/* 1320 */     i = this._inputData.readUnsignedByte();
/* 1321 */     if (codes[i] != 0) {
/* 1322 */       if (i == 34) {
/* 1323 */         return findName(this._quad1, q2, q3, 3);
/*      */       }
/* 1325 */       return parseName(this._quad1, q2, q3, i, 3);
/*      */     } 
/* 1327 */     q3 = q3 << 8 | i;
/* 1328 */     i = this._inputData.readUnsignedByte();
/* 1329 */     if (codes[i] != 0) {
/* 1330 */       if (i == 34) {
/* 1331 */         return findName(this._quad1, q2, q3, 4);
/*      */       }
/* 1333 */       return parseName(this._quad1, q2, q3, i, 4);
/*      */     } 
/* 1335 */     return _parseLongName(i, q2, q3);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseLongName(int q, int q2, int q3) throws IOException {
/* 1340 */     this._quadBuffer[0] = this._quad1;
/* 1341 */     this._quadBuffer[1] = q2;
/* 1342 */     this._quadBuffer[2] = q3;
/*      */ 
/*      */     
/* 1345 */     int[] codes = _icLatin1;
/* 1346 */     int qlen = 3;
/*      */     
/*      */     while (true) {
/* 1349 */       int i = this._inputData.readUnsignedByte();
/* 1350 */       if (codes[i] != 0) {
/* 1351 */         if (i == 34) {
/* 1352 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1354 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
/*      */       } 
/*      */       
/* 1357 */       q = q << 8 | i;
/* 1358 */       i = this._inputData.readUnsignedByte();
/* 1359 */       if (codes[i] != 0) {
/* 1360 */         if (i == 34) {
/* 1361 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1363 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 2);
/*      */       } 
/*      */       
/* 1366 */       q = q << 8 | i;
/* 1367 */       i = this._inputData.readUnsignedByte();
/* 1368 */       if (codes[i] != 0) {
/* 1369 */         if (i == 34) {
/* 1370 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1372 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 3);
/*      */       } 
/*      */       
/* 1375 */       q = q << 8 | i;
/* 1376 */       i = this._inputData.readUnsignedByte();
/* 1377 */       if (codes[i] != 0) {
/* 1378 */         if (i == 34) {
/* 1379 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1381 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 4);
/*      */       } 
/*      */ 
/*      */       
/* 1385 */       if (qlen >= this._quadBuffer.length) {
/* 1386 */         this._quadBuffer = _growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1388 */       this._quadBuffer[qlen++] = q;
/* 1389 */       q = i;
/*      */     } 
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
/* 1394 */     return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
/* 1398 */     this._quadBuffer[0] = q1;
/* 1399 */     return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
/* 1403 */     this._quadBuffer[0] = q1;
/* 1404 */     this._quadBuffer[1] = q2;
/* 1405 */     return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
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
/*      */   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
/* 1422 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1425 */       if (codes[ch] != 0) {
/* 1426 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1430 */         if (ch != 92) {
/*      */           
/* 1432 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 1435 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1442 */         if (ch > 127) {
/*      */           
/* 1444 */           if (currQuadBytes >= 4) {
/* 1445 */             if (qlen >= quads.length) {
/* 1446 */               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */             }
/* 1448 */             quads[qlen++] = currQuad;
/* 1449 */             currQuad = 0;
/* 1450 */             currQuadBytes = 0;
/*      */           } 
/* 1452 */           if (ch < 2048) {
/* 1453 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1454 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 1457 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1458 */             currQuadBytes++;
/*      */             
/* 1460 */             if (currQuadBytes >= 4) {
/* 1461 */               if (qlen >= quads.length) {
/* 1462 */                 this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */               }
/* 1464 */               quads[qlen++] = currQuad;
/* 1465 */               currQuad = 0;
/* 1466 */               currQuadBytes = 0;
/*      */             } 
/* 1468 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1469 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 1472 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 1476 */       if (currQuadBytes < 4) {
/* 1477 */         currQuadBytes++;
/* 1478 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1480 */         if (qlen >= quads.length) {
/* 1481 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1483 */         quads[qlen++] = currQuad;
/* 1484 */         currQuad = ch;
/* 1485 */         currQuadBytes = 1;
/*      */       } 
/* 1487 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1490 */     if (currQuadBytes > 0) {
/* 1491 */       if (qlen >= quads.length) {
/* 1492 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1494 */       quads[qlen++] = pad(currQuad, currQuadBytes);
/*      */     } 
/* 1496 */     String name = this._symbols.findName(quads, qlen);
/* 1497 */     if (name == null) {
/* 1498 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1500 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _handleOddName(int ch) throws IOException {
/* 1511 */     if (ch == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1512 */       return _parseAposName();
/*      */     }
/* 1514 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 1515 */       char c = (char)_decodeCharForError(ch);
/* 1516 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1522 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 1524 */     if (codes[ch] != 0) {
/* 1525 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1532 */     int[] quads = this._quadBuffer;
/* 1533 */     int qlen = 0;
/* 1534 */     int currQuad = 0;
/* 1535 */     int currQuadBytes = 0;
/*      */ 
/*      */     
/*      */     do {
/* 1539 */       if (currQuadBytes < 4) {
/* 1540 */         currQuadBytes++;
/* 1541 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1543 */         if (qlen >= quads.length) {
/* 1544 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1546 */         quads[qlen++] = currQuad;
/* 1547 */         currQuad = ch;
/* 1548 */         currQuadBytes = 1;
/*      */       } 
/* 1550 */       ch = this._inputData.readUnsignedByte();
/* 1551 */     } while (codes[ch] == 0);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1556 */     this._nextByte = ch;
/* 1557 */     if (currQuadBytes > 0) {
/* 1558 */       if (qlen >= quads.length) {
/* 1559 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1561 */       quads[qlen++] = currQuad;
/*      */     } 
/* 1563 */     String name = this._symbols.findName(quads, qlen);
/* 1564 */     if (name == null) {
/* 1565 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1567 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 1578 */     int ch = this._inputData.readUnsignedByte();
/* 1579 */     if (ch == 39) {
/* 1580 */       return "";
/*      */     }
/* 1582 */     int[] quads = this._quadBuffer;
/* 1583 */     int qlen = 0;
/* 1584 */     int currQuad = 0;
/* 1585 */     int currQuadBytes = 0;
/*      */ 
/*      */ 
/*      */     
/* 1589 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1592 */     while (ch != 39) {
/*      */ 
/*      */ 
/*      */       
/* 1596 */       if (ch != 34 && codes[ch] != 0) {
/* 1597 */         if (ch != 92) {
/*      */ 
/*      */           
/* 1600 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 1603 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1609 */         if (ch > 127) {
/*      */           
/* 1611 */           if (currQuadBytes >= 4) {
/* 1612 */             if (qlen >= quads.length) {
/* 1613 */               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */             }
/* 1615 */             quads[qlen++] = currQuad;
/* 1616 */             currQuad = 0;
/* 1617 */             currQuadBytes = 0;
/*      */           } 
/* 1619 */           if (ch < 2048) {
/* 1620 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1621 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 1624 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1625 */             currQuadBytes++;
/*      */             
/* 1627 */             if (currQuadBytes >= 4) {
/* 1628 */               if (qlen >= quads.length) {
/* 1629 */                 this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */               }
/* 1631 */               quads[qlen++] = currQuad;
/* 1632 */               currQuad = 0;
/* 1633 */               currQuadBytes = 0;
/*      */             } 
/* 1635 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1636 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 1639 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 1643 */       if (currQuadBytes < 4) {
/* 1644 */         currQuadBytes++;
/* 1645 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1647 */         if (qlen >= quads.length) {
/* 1648 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1650 */         quads[qlen++] = currQuad;
/* 1651 */         currQuad = ch;
/* 1652 */         currQuadBytes = 1;
/*      */       } 
/* 1654 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1657 */     if (currQuadBytes > 0) {
/* 1658 */       if (qlen >= quads.length) {
/* 1659 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1661 */       quads[qlen++] = pad(currQuad, currQuadBytes);
/*      */     } 
/* 1663 */     String name = this._symbols.findName(quads, qlen);
/* 1664 */     if (name == null) {
/* 1665 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1667 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int lastQuadBytes) throws JsonParseException {
/* 1678 */     q1 = pad(q1, lastQuadBytes);
/*      */     
/* 1680 */     String name = this._symbols.findName(q1);
/* 1681 */     if (name != null) {
/* 1682 */       return name;
/*      */     }
/*      */     
/* 1685 */     this._quadBuffer[0] = q1;
/* 1686 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
/* 1691 */     q2 = pad(q2, lastQuadBytes);
/*      */     
/* 1693 */     String name = this._symbols.findName(q1, q2);
/* 1694 */     if (name != null) {
/* 1695 */       return name;
/*      */     }
/*      */     
/* 1698 */     this._quadBuffer[0] = q1;
/* 1699 */     this._quadBuffer[1] = q2;
/* 1700 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
/* 1705 */     q3 = pad(q3, lastQuadBytes);
/* 1706 */     String name = this._symbols.findName(q1, q2, q3);
/* 1707 */     if (name != null) {
/* 1708 */       return name;
/*      */     }
/* 1710 */     int[] quads = this._quadBuffer;
/* 1711 */     quads[0] = q1;
/* 1712 */     quads[1] = q2;
/* 1713 */     quads[2] = pad(q3, lastQuadBytes);
/* 1714 */     return addName(quads, 3, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
/* 1719 */     if (qlen >= quads.length) {
/* 1720 */       this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */     }
/* 1722 */     quads[qlen++] = pad(lastQuad, lastQuadBytes);
/* 1723 */     String name = this._symbols.findName(quads, qlen);
/* 1724 */     if (name == null) {
/* 1725 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 1727 */     return name;
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
/*      */   private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
/* 1743 */     int lastQuad, byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1752 */     if (lastQuadBytes < 4) {
/* 1753 */       lastQuad = quads[qlen - 1];
/*      */       
/* 1755 */       quads[qlen - 1] = lastQuad << 4 - lastQuadBytes << 3;
/*      */     } else {
/* 1757 */       lastQuad = 0;
/*      */     } 
/*      */ 
/*      */     
/* 1761 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1762 */     int cix = 0;
/*      */     
/* 1764 */     for (int ix = 0; ix < byteLen; ) {
/* 1765 */       int ch = quads[ix >> 2];
/* 1766 */       int byteIx = ix & 0x3;
/* 1767 */       ch = ch >> 3 - byteIx << 3 & 0xFF;
/* 1768 */       ix++;
/*      */       
/* 1770 */       if (ch > 127) {
/*      */         int needed;
/* 1772 */         if ((ch & 0xE0) == 192) {
/* 1773 */           ch &= 0x1F;
/* 1774 */           needed = 1;
/* 1775 */         } else if ((ch & 0xF0) == 224) {
/* 1776 */           ch &= 0xF;
/* 1777 */           needed = 2;
/* 1778 */         } else if ((ch & 0xF8) == 240) {
/* 1779 */           ch &= 0x7;
/* 1780 */           needed = 3;
/*      */         } else {
/* 1782 */           _reportInvalidInitial(ch);
/* 1783 */           needed = ch = 1;
/*      */         } 
/* 1785 */         if (ix + needed > byteLen) {
/* 1786 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */         }
/*      */ 
/*      */         
/* 1790 */         int ch2 = quads[ix >> 2];
/* 1791 */         byteIx = ix & 0x3;
/* 1792 */         ch2 >>= 3 - byteIx << 3;
/* 1793 */         ix++;
/*      */         
/* 1795 */         if ((ch2 & 0xC0) != 128) {
/* 1796 */           _reportInvalidOther(ch2);
/*      */         }
/* 1798 */         ch = ch << 6 | ch2 & 0x3F;
/* 1799 */         if (needed > 1) {
/* 1800 */           ch2 = quads[ix >> 2];
/* 1801 */           byteIx = ix & 0x3;
/* 1802 */           ch2 >>= 3 - byteIx << 3;
/* 1803 */           ix++;
/*      */           
/* 1805 */           if ((ch2 & 0xC0) != 128) {
/* 1806 */             _reportInvalidOther(ch2);
/*      */           }
/* 1808 */           ch = ch << 6 | ch2 & 0x3F;
/* 1809 */           if (needed > 2) {
/* 1810 */             ch2 = quads[ix >> 2];
/* 1811 */             byteIx = ix & 0x3;
/* 1812 */             ch2 >>= 3 - byteIx << 3;
/* 1813 */             ix++;
/* 1814 */             if ((ch2 & 0xC0) != 128) {
/* 1815 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 1817 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           } 
/*      */         } 
/* 1820 */         if (needed > 2) {
/* 1821 */           ch -= 65536;
/* 1822 */           if (cix >= cbuf.length) {
/* 1823 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 1825 */           cbuf[cix++] = (char)(55296 + (ch >> 10));
/* 1826 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         } 
/*      */       } 
/* 1829 */       if (cix >= cbuf.length) {
/* 1830 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1832 */       cbuf[cix++] = (char)ch;
/*      */     } 
/*      */ 
/*      */     
/* 1836 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 1838 */     if (lastQuadBytes < 4) {
/* 1839 */       quads[qlen - 1] = lastQuad;
/*      */     }
/* 1841 */     return this._symbols.addName(baseName, quads, qlen);
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
/*      */   protected void _finishString() throws IOException {
/* 1853 */     int outPtr = 0;
/* 1854 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1855 */     int[] codes = _icUTF8;
/* 1856 */     int outEnd = outBuf.length;
/*      */     
/*      */     while (true) {
/* 1859 */       int c = this._inputData.readUnsignedByte();
/* 1860 */       if (codes[c] != 0) {
/* 1861 */         if (c == 34) {
/* 1862 */           this._textBuffer.setCurrentLength(outPtr);
/*      */           return;
/*      */         } 
/* 1865 */         _finishString2(outBuf, outPtr, c);
/*      */         return;
/*      */       } 
/* 1868 */       outBuf[outPtr++] = (char)c;
/* 1869 */       if (outPtr >= outEnd) {
/* 1870 */         _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   } private String _finishAndReturnString() throws IOException {
/* 1875 */     int outPtr = 0;
/* 1876 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1877 */     int[] codes = _icUTF8;
/* 1878 */     int outEnd = outBuf.length;
/*      */     
/*      */     while (true) {
/* 1881 */       int c = this._inputData.readUnsignedByte();
/* 1882 */       if (codes[c] != 0) {
/* 1883 */         if (c == 34) {
/* 1884 */           return this._textBuffer.setCurrentAndReturn(outPtr);
/*      */         }
/* 1886 */         _finishString2(outBuf, outPtr, c);
/* 1887 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1889 */       outBuf[outPtr++] = (char)c;
/* 1890 */       if (outPtr >= outEnd) {
/* 1891 */         _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
/* 1892 */         return this._textBuffer.contentsAsString();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _finishString2(char[] outBuf, int outPtr, int c) throws IOException {
/* 1899 */     int[] codes = _icUTF8;
/* 1900 */     int outEnd = outBuf.length;
/*      */ 
/*      */     
/* 1903 */     for (;; c = this._inputData.readUnsignedByte()) {
/*      */       
/* 1905 */       while (codes[c] == 0) {
/* 1906 */         if (outPtr >= outEnd) {
/* 1907 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1908 */           outPtr = 0;
/* 1909 */           outEnd = outBuf.length;
/*      */         } 
/* 1911 */         outBuf[outPtr++] = (char)c;
/* 1912 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/*      */       
/* 1915 */       if (c == 34) {
/*      */         break;
/*      */       }
/* 1918 */       switch (codes[c]) {
/*      */         case 1:
/* 1920 */           c = _decodeEscaped();
/*      */           break;
/*      */         case 2:
/* 1923 */           c = _decodeUtf8_2(c);
/*      */           break;
/*      */         case 3:
/* 1926 */           c = _decodeUtf8_3(c);
/*      */           break;
/*      */         case 4:
/* 1929 */           c = _decodeUtf8_4(c);
/*      */           
/* 1931 */           outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 1932 */           if (outPtr >= outBuf.length) {
/* 1933 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 1934 */             outPtr = 0;
/* 1935 */             outEnd = outBuf.length;
/*      */           } 
/* 1937 */           c = 0xDC00 | c & 0x3FF;
/*      */           break;
/*      */         
/*      */         default:
/* 1941 */           if (c < 32) {
/* 1942 */             _throwUnquotedSpace(c, "string value");
/*      */             break;
/*      */           } 
/* 1945 */           _reportInvalidChar(c);
/*      */           break;
/*      */       } 
/*      */       
/* 1949 */       if (outPtr >= outBuf.length) {
/* 1950 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1951 */         outPtr = 0;
/* 1952 */         outEnd = outBuf.length;
/*      */       } 
/*      */       
/* 1955 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 1957 */     this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _skipString() throws IOException {
/* 1967 */     this._tokenIncomplete = false;
/*      */ 
/*      */     
/* 1970 */     int[] codes = _icUTF8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1978 */       int c = this._inputData.readUnsignedByte();
/* 1979 */       if (codes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1984 */         if (c == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1988 */         switch (codes[c]) {
/*      */           case 1:
/* 1990 */             _decodeEscaped();
/*      */             continue;
/*      */           case 2:
/* 1993 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 1996 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 1999 */             _skipUtf8_4();
/*      */             continue;
/*      */         } 
/* 2002 */         if (c < 32) {
/* 2003 */           _throwUnquotedSpace(c, "string value");
/*      */           continue;
/*      */         } 
/* 2006 */         _reportInvalidChar(c);
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
/*      */   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
/* 2020 */     switch (c) {
/*      */       case 93:
/* 2022 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/* 2030 */         if ((this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/*      */           
/* 2032 */           this._nextByte = c;
/* 2033 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */ 
/*      */ 
/*      */       
/*      */       case 125:
/* 2039 */         _reportUnexpectedChar(c, "expected a value");
/*      */       case 39:
/* 2041 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2042 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */       case 78:
/* 2046 */         _matchToken("NaN", 1);
/* 2047 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2048 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 2050 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 2053 */         _matchToken("Infinity", 1);
/* 2054 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2055 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 2057 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 2060 */         return _handleInvalidNumberStart(this._inputData.readUnsignedByte(), false);
/*      */     } 
/*      */     
/* 2063 */     if (Character.isJavaIdentifierStart(c)) {
/* 2064 */       _reportInvalidToken(c, "" + (char)c, _validJsonTokenList());
/*      */     }
/*      */     
/* 2067 */     _reportUnexpectedChar(c, "expected a valid value " + _validJsonValueList());
/* 2068 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 2073 */     int c = 0;
/*      */     
/* 2075 */     int outPtr = 0;
/* 2076 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */     
/* 2079 */     int[] codes = _icUTF8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     label35: while (true) {
/* 2086 */       int outEnd = outBuf.length;
/* 2087 */       if (outPtr >= outBuf.length) {
/* 2088 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2089 */         outPtr = 0;
/* 2090 */         outEnd = outBuf.length;
/*      */       } 
/*      */       while (true)
/* 2093 */       { c = this._inputData.readUnsignedByte();
/* 2094 */         if (c == 39) {
/*      */           break;
/*      */         }
/* 2097 */         if (codes[c] != 0)
/*      */         
/*      */         { 
/*      */ 
/*      */ 
/*      */           
/* 2103 */           switch (codes[c]) {
/*      */             case 1:
/* 2105 */               c = _decodeEscaped();
/*      */               break;
/*      */             case 2:
/* 2108 */               c = _decodeUtf8_2(c);
/*      */               break;
/*      */             case 3:
/* 2111 */               c = _decodeUtf8_3(c);
/*      */               break;
/*      */             case 4:
/* 2114 */               c = _decodeUtf8_4(c);
/*      */               
/* 2116 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2117 */               if (outPtr >= outBuf.length) {
/* 2118 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2119 */                 outPtr = 0;
/*      */               } 
/* 2121 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2125 */               if (c < 32) {
/* 2126 */                 _throwUnquotedSpace(c, "string value");
/*      */               }
/*      */               
/* 2129 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/* 2132 */           if (outPtr >= outBuf.length) {
/* 2133 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2134 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2137 */           outBuf[outPtr++] = (char)c; continue; }  outBuf[outPtr++] = (char)c; if (outPtr >= outEnd)
/*      */           continue label35;  }  break;
/* 2139 */     }  this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 2141 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
/* 2151 */     while (ch == 73) {
/* 2152 */       String match; ch = this._inputData.readUnsignedByte();
/*      */       
/* 2154 */       if (ch == 78) {
/* 2155 */         match = neg ? "-INF" : "+INF";
/* 2156 */       } else if (ch == 110) {
/* 2157 */         match = neg ? "-Infinity" : "+Infinity";
/*      */       } else {
/*      */         break;
/*      */       } 
/* 2161 */       _matchToken(match, 3);
/* 2162 */       if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2163 */         return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */       }
/* 2165 */       _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */     } 
/* 2167 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2168 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2173 */     int len = matchStr.length();
/*      */     do {
/* 2175 */       int j = this._inputData.readUnsignedByte();
/* 2176 */       if (j == matchStr.charAt(i))
/* 2177 */         continue;  _reportInvalidToken(j, matchStr.substring(0, i));
/*      */     }
/* 2179 */     while (++i < len);
/*      */     
/* 2181 */     int ch = this._inputData.readUnsignedByte();
/* 2182 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2183 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/* 2185 */     this._nextByte = ch;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
/* 2190 */     char c = (char)_decodeCharForError(ch);
/* 2191 */     if (Character.isJavaIdentifierPart(c)) {
/* 2192 */       _reportInvalidToken(c, matchStr.substring(0, i));
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
/*      */   private final int _skipWS() throws IOException {
/* 2204 */     int i = this._nextByte;
/* 2205 */     if (i < 0) {
/* 2206 */       i = this._inputData.readUnsignedByte();
/*      */     } else {
/* 2208 */       this._nextByte = -1;
/*      */     } 
/*      */     while (true) {
/* 2211 */       if (i > 32) {
/* 2212 */         if (i == 47 || i == 35) {
/* 2213 */           return _skipWSComment(i);
/*      */         }
/* 2215 */         return i;
/*      */       } 
/*      */ 
/*      */       
/* 2219 */       if (i == 13 || i == 10) {
/* 2220 */         this._currInputRow++;
/*      */       }
/*      */       
/* 2223 */       i = this._inputData.readUnsignedByte();
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
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 2235 */     int i = this._nextByte;
/* 2236 */     if (i < 0) {
/*      */       try {
/* 2238 */         i = this._inputData.readUnsignedByte();
/* 2239 */       } catch (EOFException e) {
/* 2240 */         return _eofAsNextChar();
/*      */       } 
/*      */     } else {
/* 2243 */       this._nextByte = -1;
/*      */     } 
/*      */     while (true) {
/* 2246 */       if (i > 32) {
/* 2247 */         if (i == 47 || i == 35) {
/* 2248 */           return _skipWSComment(i);
/*      */         }
/* 2250 */         return i;
/*      */       } 
/*      */ 
/*      */       
/* 2254 */       if (i == 13 || i == 10) {
/* 2255 */         this._currInputRow++;
/*      */       }
/*      */       
/*      */       try {
/* 2259 */         i = this._inputData.readUnsignedByte();
/* 2260 */       } catch (EOFException e) {
/* 2261 */         return _eofAsNextChar();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWSComment(int i) throws IOException {
/*      */     while (true) {
/* 2269 */       if (i > 32) {
/* 2270 */         if (i == 47) {
/* 2271 */           _skipComment();
/* 2272 */         } else if (i == 35) {
/* 2273 */           if (!_skipYAMLComment()) {
/* 2274 */             return i;
/*      */           }
/*      */         } else {
/* 2277 */           return i;
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 2282 */       else if (i == 13 || i == 10) {
/* 2283 */         this._currInputRow++;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2291 */       i = this._inputData.readUnsignedByte();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 2297 */     int i = this._nextByte;
/* 2298 */     if (i < 0) {
/* 2299 */       i = this._inputData.readUnsignedByte();
/*      */     } else {
/* 2301 */       this._nextByte = -1;
/*      */     } 
/*      */     
/* 2304 */     if (i == 58) {
/* 2305 */       i = this._inputData.readUnsignedByte();
/* 2306 */       if (i > 32) {
/* 2307 */         if (i == 47 || i == 35) {
/* 2308 */           return _skipColon2(i, true);
/*      */         }
/* 2310 */         return i;
/*      */       } 
/* 2312 */       if (i == 32 || i == 9) {
/* 2313 */         i = this._inputData.readUnsignedByte();
/* 2314 */         if (i > 32) {
/* 2315 */           if (i == 47 || i == 35) {
/* 2316 */             return _skipColon2(i, true);
/*      */           }
/* 2318 */           return i;
/*      */         } 
/*      */       } 
/* 2321 */       return _skipColon2(i, true);
/*      */     } 
/* 2323 */     if (i == 32 || i == 9) {
/* 2324 */       i = this._inputData.readUnsignedByte();
/*      */     }
/* 2326 */     if (i == 58) {
/* 2327 */       i = this._inputData.readUnsignedByte();
/* 2328 */       if (i > 32) {
/* 2329 */         if (i == 47 || i == 35) {
/* 2330 */           return _skipColon2(i, true);
/*      */         }
/* 2332 */         return i;
/*      */       } 
/* 2334 */       if (i == 32 || i == 9) {
/* 2335 */         i = this._inputData.readUnsignedByte();
/* 2336 */         if (i > 32) {
/* 2337 */           if (i == 47 || i == 35) {
/* 2338 */             return _skipColon2(i, true);
/*      */           }
/* 2340 */           return i;
/*      */         } 
/*      */       } 
/* 2343 */       return _skipColon2(i, true);
/*      */     } 
/* 2345 */     return _skipColon2(i, false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(int i, boolean gotColon) throws IOException {
/* 2350 */     for (;; i = this._inputData.readUnsignedByte()) {
/* 2351 */       if (i > 32) {
/* 2352 */         if (i == 47) {
/* 2353 */           _skipComment();
/*      */         
/*      */         }
/* 2356 */         else if (i != 35 || 
/* 2357 */           !_skipYAMLComment()) {
/*      */ 
/*      */ 
/*      */           
/* 2361 */           if (gotColon) {
/* 2362 */             return i;
/*      */           }
/* 2364 */           if (i != 58) {
/* 2365 */             _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */           }
/* 2367 */           gotColon = true;
/*      */         }
/*      */       
/*      */       }
/* 2371 */       else if (i == 13 || i == 10) {
/* 2372 */         this._currInputRow++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipComment() throws IOException {
/* 2380 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 2381 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/* 2383 */     int c = this._inputData.readUnsignedByte();
/* 2384 */     if (c == 47) {
/* 2385 */       _skipLine();
/* 2386 */     } else if (c == 42) {
/* 2387 */       _skipCComment();
/*      */     } else {
/* 2389 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipCComment() throws IOException {
/* 2396 */     int[] codes = CharTypes.getInputCodeComment();
/* 2397 */     int i = this._inputData.readUnsignedByte();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2402 */       int code = codes[i];
/* 2403 */       if (code != 0)
/* 2404 */         switch (code) {
/*      */           case 42:
/* 2406 */             i = this._inputData.readUnsignedByte();
/* 2407 */             if (i == 47) {
/*      */               return;
/*      */             }
/*      */             continue;
/*      */           case 10:
/*      */           case 13:
/* 2413 */             this._currInputRow++;
/*      */             break;
/*      */           case 2:
/* 2416 */             _skipUtf8_2();
/*      */             break;
/*      */           case 3:
/* 2419 */             _skipUtf8_3();
/*      */             break;
/*      */           case 4:
/* 2422 */             _skipUtf8_4();
/*      */             break;
/*      */           
/*      */           default:
/* 2426 */             _reportInvalidChar(i);
/*      */             break;
/*      */         }  
/* 2429 */       i = this._inputData.readUnsignedByte();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final boolean _skipYAMLComment() throws IOException {
/* 2435 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 2436 */       return false;
/*      */     }
/* 2438 */     _skipLine();
/* 2439 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipLine() throws IOException {
/* 2449 */     int[] codes = CharTypes.getInputCodeComment();
/*      */     while (true) {
/* 2451 */       int i = this._inputData.readUnsignedByte();
/* 2452 */       int code = codes[i];
/* 2453 */       if (code != 0) {
/* 2454 */         switch (code) {
/*      */           case 10:
/*      */           case 13:
/* 2457 */             this._currInputRow++;
/*      */             return;
/*      */           case 42:
/*      */             continue;
/*      */           case 2:
/* 2462 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 2465 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 2468 */             _skipUtf8_4();
/*      */             continue;
/*      */         } 
/* 2471 */         if (code < 0)
/*      */         {
/* 2473 */           _reportInvalidChar(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 2483 */     int c = this._inputData.readUnsignedByte();
/*      */     
/* 2485 */     switch (c) {
/*      */       
/*      */       case 98:
/* 2488 */         return '\b';
/*      */       case 116:
/* 2490 */         return '\t';
/*      */       case 110:
/* 2492 */         return '\n';
/*      */       case 102:
/* 2494 */         return '\f';
/*      */       case 114:
/* 2496 */         return '\r';
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 2502 */         return (char)c;
/*      */       
/*      */       case 117:
/*      */         break;
/*      */       
/*      */       default:
/* 2508 */         return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     } 
/*      */ 
/*      */     
/* 2512 */     int value = 0;
/* 2513 */     for (int i = 0; i < 4; i++) {
/* 2514 */       int ch = this._inputData.readUnsignedByte();
/* 2515 */       int digit = CharTypes.charToHex(ch);
/* 2516 */       if (digit < 0) {
/* 2517 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2519 */       value = value << 4 | digit;
/*      */     } 
/* 2521 */     return (char)value;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _decodeCharForError(int firstByte) throws IOException {
/* 2526 */     int c = firstByte & 0xFF;
/* 2527 */     if (c > 127) {
/*      */       int needed;
/*      */ 
/*      */       
/* 2531 */       if ((c & 0xE0) == 192) {
/* 2532 */         c &= 0x1F;
/* 2533 */         needed = 1;
/* 2534 */       } else if ((c & 0xF0) == 224) {
/* 2535 */         c &= 0xF;
/* 2536 */         needed = 2;
/* 2537 */       } else if ((c & 0xF8) == 240) {
/*      */         
/* 2539 */         c &= 0x7;
/* 2540 */         needed = 3;
/*      */       } else {
/* 2542 */         _reportInvalidInitial(c & 0xFF);
/* 2543 */         needed = 1;
/*      */       } 
/*      */       
/* 2546 */       int d = this._inputData.readUnsignedByte();
/* 2547 */       if ((d & 0xC0) != 128) {
/* 2548 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 2550 */       c = c << 6 | d & 0x3F;
/*      */       
/* 2552 */       if (needed > 1) {
/* 2553 */         d = this._inputData.readUnsignedByte();
/* 2554 */         if ((d & 0xC0) != 128) {
/* 2555 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 2557 */         c = c << 6 | d & 0x3F;
/* 2558 */         if (needed > 2) {
/* 2559 */           d = this._inputData.readUnsignedByte();
/* 2560 */           if ((d & 0xC0) != 128) {
/* 2561 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 2563 */           c = c << 6 | d & 0x3F;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2567 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_2(int c) throws IOException {
/* 2578 */     int d = this._inputData.readUnsignedByte();
/* 2579 */     if ((d & 0xC0) != 128) {
/* 2580 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2582 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3(int c1) throws IOException {
/* 2587 */     c1 &= 0xF;
/* 2588 */     int d = this._inputData.readUnsignedByte();
/* 2589 */     if ((d & 0xC0) != 128) {
/* 2590 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2592 */     int c = c1 << 6 | d & 0x3F;
/* 2593 */     d = this._inputData.readUnsignedByte();
/* 2594 */     if ((d & 0xC0) != 128) {
/* 2595 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2597 */     c = c << 6 | d & 0x3F;
/* 2598 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_4(int c) throws IOException {
/* 2607 */     int d = this._inputData.readUnsignedByte();
/* 2608 */     if ((d & 0xC0) != 128) {
/* 2609 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2611 */     c = (c & 0x7) << 6 | d & 0x3F;
/* 2612 */     d = this._inputData.readUnsignedByte();
/* 2613 */     if ((d & 0xC0) != 128) {
/* 2614 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2616 */     c = c << 6 | d & 0x3F;
/* 2617 */     d = this._inputData.readUnsignedByte();
/* 2618 */     if ((d & 0xC0) != 128) {
/* 2619 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2625 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_2() throws IOException {
/* 2630 */     int c = this._inputData.readUnsignedByte();
/* 2631 */     if ((c & 0xC0) != 128) {
/* 2632 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_3() throws IOException {
/* 2642 */     int c = this._inputData.readUnsignedByte();
/* 2643 */     if ((c & 0xC0) != 128) {
/* 2644 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/* 2646 */     c = this._inputData.readUnsignedByte();
/* 2647 */     if ((c & 0xC0) != 128) {
/* 2648 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_4() throws IOException {
/* 2654 */     int d = this._inputData.readUnsignedByte();
/* 2655 */     if ((d & 0xC0) != 128) {
/* 2656 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2658 */     d = this._inputData.readUnsignedByte();
/* 2659 */     if ((d & 0xC0) != 128) {
/* 2660 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2662 */     d = this._inputData.readUnsignedByte();
/* 2663 */     if ((d & 0xC0) != 128) {
/* 2664 */       _reportInvalidOther(d & 0xFF);
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
/*      */   protected void _reportInvalidToken(int ch, String matchedPart) throws IOException {
/* 2676 */     _reportInvalidToken(ch, matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(int ch, String matchedPart, String msg) throws IOException {
/* 2682 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2689 */       char c = (char)_decodeCharForError(ch);
/* 2690 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2693 */       sb.append(c);
/* 2694 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/* 2696 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidChar(int c) throws JsonParseException {
/* 2703 */     if (c < 32) {
/* 2704 */       _throwInvalidSpace(c);
/*      */     }
/* 2706 */     _reportInvalidInitial(c);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidInitial(int mask) throws JsonParseException {
/* 2712 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _reportInvalidOther(int mask) throws JsonParseException {
/* 2718 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */   
/*      */   private static int[] _growArrayBy(int[] arr, int more) {
/* 2723 */     if (arr == null) {
/* 2724 */       return new int[more];
/*      */     }
/* 2726 */     return Arrays.copyOf(arr, arr.length + more);
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
/*      */   protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
/* 2742 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2749 */       int ch = this._inputData.readUnsignedByte();
/* 2750 */       if (ch > 32) {
/* 2751 */         int bits = b64variant.decodeBase64Char(ch);
/* 2752 */         if (bits < 0) {
/* 2753 */           if (ch == 34) {
/* 2754 */             return builder.toByteArray();
/*      */           }
/* 2756 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2757 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 2761 */         int decodedData = bits;
/*      */ 
/*      */         
/* 2764 */         ch = this._inputData.readUnsignedByte();
/* 2765 */         bits = b64variant.decodeBase64Char(ch);
/* 2766 */         if (bits < 0) {
/* 2767 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 2769 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2771 */         ch = this._inputData.readUnsignedByte();
/* 2772 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 2775 */         if (bits < 0) {
/* 2776 */           if (bits != -2) {
/*      */             
/* 2778 */             if (ch == 34) {
/* 2779 */               decodedData >>= 4;
/* 2780 */               builder.append(decodedData);
/* 2781 */               if (b64variant.usesPadding()) {
/* 2782 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/* 2784 */               return builder.toByteArray();
/*      */             } 
/* 2786 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 2788 */           if (bits == -2) {
/* 2789 */             ch = this._inputData.readUnsignedByte();
/* 2790 */             if (!b64variant.usesPaddingChar(ch) && (
/* 2791 */               ch != 92 || 
/* 2792 */               _decodeBase64Escape(b64variant, ch, 3) != -2)) {
/* 2793 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 2797 */             decodedData >>= 4;
/* 2798 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 2803 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2805 */         ch = this._inputData.readUnsignedByte();
/* 2806 */         bits = b64variant.decodeBase64Char(ch);
/* 2807 */         if (bits < 0) {
/* 2808 */           if (bits != -2) {
/*      */             
/* 2810 */             if (ch == 34) {
/* 2811 */               decodedData >>= 2;
/* 2812 */               builder.appendTwoBytes(decodedData);
/* 2813 */               if (b64variant.usesPadding()) {
/* 2814 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/* 2816 */               return builder.toByteArray();
/*      */             } 
/* 2818 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 2820 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2827 */             decodedData >>= 2;
/* 2828 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 2833 */         decodedData = decodedData << 6 | bits;
/* 2834 */         builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation() {
/* 2846 */     return new JsonLocation(_getSourceReference(), -1L, -1L, this._tokenInputRow, -1);
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 2851 */     return new JsonLocation(_getSourceReference(), -1L, -1L, this._currInputRow, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _closeScope(int i) throws JsonParseException {
/* 2861 */     if (i == 93) {
/* 2862 */       if (!this._parsingContext.inArray()) {
/* 2863 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/* 2865 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2866 */       this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/* 2868 */     if (i == 125) {
/* 2869 */       if (!this._parsingContext.inObject()) {
/* 2870 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/* 2872 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2873 */       this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int pad(int q, int bytes) {
/* 2881 */     return (bytes == 4) ? q : (q | -1 << bytes << 3);
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\UTF8DataInputJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */