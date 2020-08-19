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
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
/*      */ 
/*      */ public class UTF8StreamJsonParser
/*      */   extends ParserBase
/*      */ {
/*      */   static final byte BYTE_LF = 10;
/*   24 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   26 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   28 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
/*      */   
/*   30 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   31 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   32 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*   33 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   34 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */   
/*   37 */   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
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
/*   70 */   protected int[] _quadBuffer = new int[16];
/*      */ 
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
/*      */   
/*      */   private int _quad1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartOffset;
/*      */ 
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
/*      */   protected InputStream _inputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _inputBuffer;
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
/*      */   
/*      */   @Deprecated
/*      */   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
/*  149 */     this(ctxt, features, in, codec, sym, inputBuffer, start, end, 0, bufferRecyclable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, int bytesPreProcessed, boolean bufferRecyclable) {
/*  158 */     super(ctxt, features);
/*  159 */     this._inputStream = in;
/*  160 */     this._objectCodec = codec;
/*  161 */     this._symbols = sym;
/*  162 */     this._inputBuffer = inputBuffer;
/*  163 */     this._inputPtr = start;
/*  164 */     this._inputEnd = end;
/*  165 */     this._currInputRowStart = start - bytesPreProcessed;
/*      */     
/*  167 */     this._currInputProcessed = (-start + bytesPreProcessed);
/*  168 */     this._bufferRecyclable = bufferRecyclable;
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  173 */     return this._objectCodec;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCodec(ObjectCodec c) {
/*  178 */     this._objectCodec = c;
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
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  190 */     int count = this._inputEnd - this._inputPtr;
/*  191 */     if (count < 1) {
/*  192 */       return 0;
/*      */     }
/*      */     
/*  195 */     int origPtr = this._inputPtr;
/*  196 */     out.write(this._inputBuffer, origPtr, count);
/*  197 */     return count;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getInputSource() {
/*  202 */     return this._inputStream;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _loadMore() throws IOException {
/*  213 */     int bufSize = this._inputEnd;
/*      */     
/*  215 */     this._currInputProcessed += this._inputEnd;
/*  216 */     this._currInputRowStart -= this._inputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  221 */     this._nameStartOffset -= bufSize;
/*      */     
/*  223 */     if (this._inputStream != null) {
/*  224 */       int space = this._inputBuffer.length;
/*  225 */       if (space == 0) {
/*  226 */         return false;
/*      */       }
/*      */       
/*  229 */       int count = this._inputStream.read(this._inputBuffer, 0, space);
/*  230 */       if (count > 0) {
/*  231 */         this._inputPtr = 0;
/*  232 */         this._inputEnd = count;
/*  233 */         return true;
/*      */       } 
/*      */       
/*  236 */       _closeInput();
/*      */       
/*  238 */       if (count == 0) {
/*  239 */         throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
/*      */       }
/*      */     } 
/*  242 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _closeInput() throws IOException {
/*  250 */     if (this._inputStream != null) {
/*  251 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
/*  252 */         this._inputStream.close();
/*      */       }
/*  254 */       this._inputStream = null;
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
/*      */   protected void _releaseBuffers() throws IOException {
/*  267 */     super._releaseBuffers();
/*      */     
/*  269 */     this._symbols.release();
/*  270 */     if (this._bufferRecyclable) {
/*  271 */       byte[] buf = this._inputBuffer;
/*  272 */       if (buf != null) {
/*      */ 
/*      */         
/*  275 */         this._inputBuffer = NO_BYTES;
/*  276 */         this._ioContext.releaseReadIOBuffer(buf);
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
/*      */   public String getText() throws IOException {
/*  290 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  291 */       if (this._tokenIncomplete) {
/*  292 */         this._tokenIncomplete = false;
/*  293 */         return _finishAndReturnString();
/*      */       } 
/*  295 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  297 */     return _getText2(this._currToken);
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
/*      */   public String getValueAsString() throws IOException {
/*  333 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  334 */       if (this._tokenIncomplete) {
/*  335 */         this._tokenIncomplete = false;
/*  336 */         return _finishAndReturnString();
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
/*      */   
/*      */   public String getValueAsString(String defValue) throws IOException {
/*  350 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  351 */       if (this._tokenIncomplete) {
/*  352 */         this._tokenIncomplete = false;
/*  353 */         return _finishAndReturnString();
/*      */       } 
/*  355 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  357 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  358 */       return getCurrentName();
/*      */     }
/*  360 */     return super.getValueAsString(defValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt() throws IOException {
/*  367 */     JsonToken t = this._currToken;
/*  368 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  370 */       if ((this._numTypesValid & 0x1) == 0) {
/*  371 */         if (this._numTypesValid == 0) {
/*  372 */           return _parseIntValue();
/*      */         }
/*  374 */         if ((this._numTypesValid & 0x1) == 0) {
/*  375 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  378 */       return this._numberInt;
/*      */     } 
/*  380 */     return super.getValueAsInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt(int defValue) throws IOException {
/*  387 */     JsonToken t = this._currToken;
/*  388 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  390 */       if ((this._numTypesValid & 0x1) == 0) {
/*  391 */         if (this._numTypesValid == 0) {
/*  392 */           return _parseIntValue();
/*      */         }
/*  394 */         if ((this._numTypesValid & 0x1) == 0) {
/*  395 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  398 */       return this._numberInt;
/*      */     } 
/*  400 */     return super.getValueAsInt(defValue);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  405 */     if (t == null) {
/*  406 */       return null;
/*      */     }
/*  408 */     switch (t.id()) {
/*      */       case 5:
/*  410 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  416 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  418 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getTextCharacters() throws IOException {
/*  425 */     if (this._currToken != null) {
/*  426 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  429 */           if (!this._nameCopied) {
/*  430 */             String name = this._parsingContext.getCurrentName();
/*  431 */             int nameLen = name.length();
/*  432 */             if (this._nameCopyBuffer == null) {
/*  433 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  434 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  435 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  437 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  438 */             this._nameCopied = true;
/*      */           } 
/*  440 */           return this._nameCopyBuffer;
/*      */         
/*      */         case 6:
/*  443 */           if (this._tokenIncomplete) {
/*  444 */             this._tokenIncomplete = false;
/*  445 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  450 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*      */       
/*  453 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  456 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextLength() throws IOException {
/*  462 */     if (this._currToken != null) {
/*  463 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  466 */           return this._parsingContext.getCurrentName().length();
/*      */         case 6:
/*  468 */           if (this._tokenIncomplete) {
/*  469 */             this._tokenIncomplete = false;
/*  470 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  475 */           return this._textBuffer.size();
/*      */       } 
/*      */       
/*  478 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*      */     
/*  481 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextOffset() throws IOException {
/*  488 */     if (this._currToken != null) {
/*  489 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  491 */           return 0;
/*      */         case 6:
/*  493 */           if (this._tokenIncomplete) {
/*  494 */             this._tokenIncomplete = false;
/*  495 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  500 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  504 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  510 */     if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null))
/*      */     {
/*  512 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*  515 */     if (this._tokenIncomplete) {
/*      */       try {
/*  517 */         this._binaryValue = _decodeBase64(b64variant);
/*  518 */       } catch (IllegalArgumentException iae) {
/*  519 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */       
/*  522 */       this._tokenIncomplete = false;
/*      */     }
/*  524 */     else if (this._binaryValue == null) {
/*      */       
/*  526 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  527 */       _decodeBase64(getText(), builder, b64variant);
/*  528 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  531 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  538 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  539 */       byte[] b = getBinaryValue(b64variant);
/*  540 */       out.write(b);
/*  541 */       return b.length;
/*      */     } 
/*      */     
/*  544 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  546 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  548 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  555 */     int outputPtr = 0;
/*  556 */     int outputEnd = buffer.length - 3;
/*  557 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  563 */       if (this._inputPtr >= this._inputEnd) {
/*  564 */         _loadMoreGuaranteed();
/*      */       }
/*  566 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  567 */       if (ch > 32) {
/*  568 */         int bits = b64variant.decodeBase64Char(ch);
/*  569 */         if (bits < 0) {
/*  570 */           if (ch == 34) {
/*      */             break;
/*      */           }
/*  573 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  574 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  580 */         if (outputPtr > outputEnd) {
/*  581 */           outputCount += outputPtr;
/*  582 */           out.write(buffer, 0, outputPtr);
/*  583 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  586 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/*  590 */         if (this._inputPtr >= this._inputEnd) {
/*  591 */           _loadMoreGuaranteed();
/*      */         }
/*  593 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  594 */         bits = b64variant.decodeBase64Char(ch);
/*  595 */         if (bits < 0) {
/*  596 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  598 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  601 */         if (this._inputPtr >= this._inputEnd) {
/*  602 */           _loadMoreGuaranteed();
/*      */         }
/*  604 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  605 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  608 */         if (bits < 0) {
/*  609 */           if (bits != -2) {
/*      */             
/*  611 */             if (ch == 34) {
/*  612 */               decodedData >>= 4;
/*  613 */               buffer[outputPtr++] = (byte)decodedData;
/*  614 */               if (b64variant.usesPadding()) {
/*  615 */                 this._inputPtr--;
/*  616 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  620 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  622 */           if (bits == -2) {
/*      */             
/*  624 */             if (this._inputPtr >= this._inputEnd) {
/*  625 */               _loadMoreGuaranteed();
/*      */             }
/*  627 */             ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  628 */             if (!b64variant.usesPaddingChar(ch) && 
/*  629 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/*  630 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  634 */             decodedData >>= 4;
/*  635 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  640 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  642 */         if (this._inputPtr >= this._inputEnd) {
/*  643 */           _loadMoreGuaranteed();
/*      */         }
/*  645 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  646 */         bits = b64variant.decodeBase64Char(ch);
/*  647 */         if (bits < 0) {
/*  648 */           if (bits != -2) {
/*      */             
/*  650 */             if (ch == 34) {
/*  651 */               decodedData >>= 2;
/*  652 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  653 */               buffer[outputPtr++] = (byte)decodedData;
/*  654 */               if (b64variant.usesPadding()) {
/*  655 */                 this._inputPtr--;
/*  656 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  660 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  662 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  669 */             decodedData >>= 2;
/*  670 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  671 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  676 */         decodedData = decodedData << 6 | bits;
/*  677 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  678 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  679 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  681 */     }  this._tokenIncomplete = false;
/*  682 */     if (outputPtr > 0) {
/*  683 */       outputCount += outputPtr;
/*  684 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  686 */     return outputCount;
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
/*      */   public JsonToken nextToken() throws IOException {
/*  706 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  707 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  711 */     this._numTypesValid = 0;
/*  712 */     if (this._tokenIncomplete) {
/*  713 */       _skipString();
/*      */     }
/*  715 */     int i = _skipWSOrEnd();
/*  716 */     if (i < 0) {
/*      */       
/*  718 */       close();
/*  719 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  722 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  725 */     if (i == 93) {
/*  726 */       _closeArrayScope();
/*  727 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/*  729 */     if (i == 125) {
/*  730 */       _closeObjectScope();
/*  731 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */ 
/*      */     
/*  735 */     if (this._parsingContext.expectComma()) {
/*  736 */       if (i != 44) {
/*  737 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  739 */       i = _skipWS();
/*      */       
/*  741 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  742 */         i == 93 || i == 125)) {
/*  743 */         return _closeScope(i);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  751 */     if (!this._parsingContext.inObject()) {
/*  752 */       _updateLocation();
/*  753 */       return _nextTokenNotInObject(i);
/*      */     } 
/*      */     
/*  756 */     _updateNameLocation();
/*  757 */     String n = _parseName(i);
/*  758 */     this._parsingContext.setCurrentName(n);
/*  759 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  761 */     i = _skipColon();
/*  762 */     _updateLocation();
/*      */ 
/*      */     
/*  765 */     if (i == 34) {
/*  766 */       this._tokenIncomplete = true;
/*  767 */       this._nextToken = JsonToken.VALUE_STRING;
/*  768 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  772 */     switch (i)
/*      */     { case 45:
/*  774 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  813 */         this._nextToken = t;
/*  814 */         return this._currToken;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return this._currToken;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return this._currToken;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return this._currToken;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return this._currToken;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return this._currToken;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return this._currToken; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return this._currToken;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/*  819 */     if (i == 34) {
/*  820 */       this._tokenIncomplete = true;
/*  821 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/*  823 */     switch (i) {
/*      */       case 91:
/*  825 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  826 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/*  828 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  829 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/*  831 */         _matchTrue();
/*  832 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/*  834 */         _matchFalse();
/*  835 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/*  837 */         _matchNull();
/*  838 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/*  840 */         return this._currToken = _parseNegNumber();
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
/*  854 */         return this._currToken = _parsePosNumber(i);
/*      */     } 
/*  856 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  861 */     this._nameCopied = false;
/*  862 */     JsonToken t = this._nextToken;
/*  863 */     this._nextToken = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  868 */     if (t == JsonToken.START_ARRAY) {
/*  869 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  870 */     } else if (t == JsonToken.START_OBJECT) {
/*  871 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  873 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  878 */     if (this._tokenIncomplete) {
/*  879 */       this._tokenIncomplete = false;
/*  880 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString str) throws IOException {
/*  894 */     this._numTypesValid = 0;
/*  895 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  896 */       _nextAfterName();
/*  897 */       return false;
/*      */     } 
/*  899 */     if (this._tokenIncomplete) {
/*  900 */       _skipString();
/*      */     }
/*  902 */     int i = _skipWSOrEnd();
/*  903 */     if (i < 0) {
/*  904 */       close();
/*  905 */       this._currToken = null;
/*  906 */       return false;
/*      */     } 
/*  908 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  911 */     if (i == 93) {
/*  912 */       _closeArrayScope();
/*  913 */       this._currToken = JsonToken.END_ARRAY;
/*  914 */       return false;
/*      */     } 
/*  916 */     if (i == 125) {
/*  917 */       _closeObjectScope();
/*  918 */       this._currToken = JsonToken.END_OBJECT;
/*  919 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  923 */     if (this._parsingContext.expectComma()) {
/*  924 */       if (i != 44) {
/*  925 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  927 */       i = _skipWS();
/*      */ 
/*      */       
/*  930 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  931 */         i == 93 || i == 125)) {
/*  932 */         _closeScope(i);
/*  933 */         return false;
/*      */       } 
/*      */     } 
/*      */     
/*  937 */     if (!this._parsingContext.inObject()) {
/*  938 */       _updateLocation();
/*  939 */       _nextTokenNotInObject(i);
/*  940 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  944 */     _updateNameLocation();
/*  945 */     if (i == 34) {
/*      */       
/*  947 */       byte[] nameBytes = str.asQuotedUTF8();
/*  948 */       int len = nameBytes.length;
/*      */ 
/*      */       
/*  951 */       if (this._inputPtr + len + 4 < this._inputEnd) {
/*      */         
/*  953 */         int end = this._inputPtr + len;
/*  954 */         if (this._inputBuffer[end] == 34) {
/*  955 */           int offset = 0;
/*  956 */           int ptr = this._inputPtr;
/*      */           while (true) {
/*  958 */             if (ptr == end) {
/*  959 */               this._parsingContext.setCurrentName(str.getValue());
/*  960 */               i = _skipColonFast(ptr + 1);
/*  961 */               _isNextTokenNameYes(i);
/*  962 */               return true;
/*      */             } 
/*  964 */             if (nameBytes[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/*  967 */             offset++;
/*  968 */             ptr++;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  973 */     return _isNextTokenNameMaybe(i, str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextFieldName() throws IOException {
/*  980 */     this._numTypesValid = 0;
/*  981 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  982 */       _nextAfterName();
/*  983 */       return null;
/*      */     } 
/*  985 */     if (this._tokenIncomplete) {
/*  986 */       _skipString();
/*      */     }
/*  988 */     int i = _skipWSOrEnd();
/*  989 */     if (i < 0) {
/*  990 */       close();
/*  991 */       this._currToken = null;
/*  992 */       return null;
/*      */     } 
/*  994 */     this._binaryValue = null;
/*      */     
/*  996 */     if (i == 93) {
/*  997 */       _closeArrayScope();
/*  998 */       this._currToken = JsonToken.END_ARRAY;
/*  999 */       return null;
/*      */     } 
/* 1001 */     if (i == 125) {
/* 1002 */       _closeObjectScope();
/* 1003 */       this._currToken = JsonToken.END_OBJECT;
/* 1004 */       return null;
/*      */     } 
/*      */ 
/*      */     
/* 1008 */     if (this._parsingContext.expectComma()) {
/* 1009 */       if (i != 44) {
/* 1010 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/* 1012 */       i = _skipWS();
/*      */       
/* 1014 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/* 1015 */         i == 93 || i == 125)) {
/* 1016 */         _closeScope(i);
/* 1017 */         return null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1022 */     if (!this._parsingContext.inObject()) {
/* 1023 */       _updateLocation();
/* 1024 */       _nextTokenNotInObject(i);
/* 1025 */       return null;
/*      */     } 
/*      */     
/* 1028 */     _updateNameLocation();
/* 1029 */     String nameStr = _parseName(i);
/* 1030 */     this._parsingContext.setCurrentName(nameStr);
/* 1031 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/* 1033 */     i = _skipColon();
/* 1034 */     _updateLocation();
/* 1035 */     if (i == 34) {
/* 1036 */       this._tokenIncomplete = true;
/* 1037 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1038 */       return nameStr;
/*      */     } 
/*      */     
/* 1041 */     switch (i)
/*      */     { case 45:
/* 1043 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1079 */         this._nextToken = t;
/* 1080 */         return nameStr;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameStr;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameStr;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameStr;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameStr;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameStr;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameStr; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return nameStr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipColonFast(int ptr) throws IOException {
/* 1086 */     int i = this._inputBuffer[ptr++];
/* 1087 */     if (i == 58) {
/* 1088 */       i = this._inputBuffer[ptr++];
/* 1089 */       if (i > 32) {
/* 1090 */         if (i != 47 && i != 35) {
/* 1091 */           this._inputPtr = ptr;
/* 1092 */           return i;
/*      */         } 
/* 1094 */       } else if (i == 32 || i == 9) {
/* 1095 */         i = this._inputBuffer[ptr++];
/* 1096 */         if (i > 32 && 
/* 1097 */           i != 47 && i != 35) {
/* 1098 */           this._inputPtr = ptr;
/* 1099 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 1103 */       this._inputPtr = ptr - 1;
/* 1104 */       return _skipColon2(true);
/*      */     } 
/* 1106 */     if (i == 32 || i == 9) {
/* 1107 */       i = this._inputBuffer[ptr++];
/*      */     }
/* 1109 */     if (i == 58) {
/* 1110 */       i = this._inputBuffer[ptr++];
/* 1111 */       if (i > 32) {
/* 1112 */         if (i != 47 && i != 35) {
/* 1113 */           this._inputPtr = ptr;
/* 1114 */           return i;
/*      */         } 
/* 1116 */       } else if (i == 32 || i == 9) {
/* 1117 */         i = this._inputBuffer[ptr++];
/* 1118 */         if (i > 32 && 
/* 1119 */           i != 47 && i != 35) {
/* 1120 */           this._inputPtr = ptr;
/* 1121 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 1125 */       this._inputPtr = ptr - 1;
/* 1126 */       return _skipColon2(true);
/*      */     } 
/* 1128 */     this._inputPtr = ptr - 1;
/* 1129 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException {
/* 1134 */     this._currToken = JsonToken.FIELD_NAME;
/* 1135 */     _updateLocation();
/*      */     
/* 1137 */     switch (i) {
/*      */       case 34:
/* 1139 */         this._tokenIncomplete = true;
/* 1140 */         this._nextToken = JsonToken.VALUE_STRING;
/*      */         return;
/*      */       case 91:
/* 1143 */         this._nextToken = JsonToken.START_ARRAY;
/*      */         return;
/*      */       case 123:
/* 1146 */         this._nextToken = JsonToken.START_OBJECT;
/*      */         return;
/*      */       case 116:
/* 1149 */         _matchTrue();
/* 1150 */         this._nextToken = JsonToken.VALUE_TRUE;
/*      */         return;
/*      */       case 102:
/* 1153 */         _matchFalse();
/* 1154 */         this._nextToken = JsonToken.VALUE_FALSE;
/*      */         return;
/*      */       case 110:
/* 1157 */         _matchNull();
/* 1158 */         this._nextToken = JsonToken.VALUE_NULL;
/*      */         return;
/*      */       case 45:
/* 1161 */         this._nextToken = _parseNegNumber();
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
/* 1173 */         this._nextToken = _parsePosNumber(i);
/*      */         return;
/*      */     } 
/* 1176 */     this._nextToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _isNextTokenNameMaybe(int i, SerializableString str) throws IOException {
/* 1183 */     String n = _parseName(i);
/* 1184 */     this._parsingContext.setCurrentName(n);
/* 1185 */     boolean match = n.equals(str.getValue());
/* 1186 */     this._currToken = JsonToken.FIELD_NAME;
/* 1187 */     i = _skipColon();
/* 1188 */     _updateLocation();
/*      */ 
/*      */     
/* 1191 */     if (i == 34) {
/* 1192 */       this._tokenIncomplete = true;
/* 1193 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1194 */       return match;
/*      */     } 
/*      */ 
/*      */     
/* 1198 */     switch (i)
/*      */     { case 91:
/* 1200 */         t = JsonToken.START_ARRAY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1235 */         this._nextToken = t;
/* 1236 */         return match;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return match;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return match;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return match;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return match;case 45: t = _parseNegNumber(); this._nextToken = t; return match;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return match; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return match;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextTextValue() throws IOException {
/* 1243 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1244 */       this._nameCopied = false;
/* 1245 */       JsonToken t = this._nextToken;
/* 1246 */       this._nextToken = null;
/* 1247 */       this._currToken = t;
/* 1248 */       if (t == JsonToken.VALUE_STRING) {
/* 1249 */         if (this._tokenIncomplete) {
/* 1250 */           this._tokenIncomplete = false;
/* 1251 */           return _finishAndReturnString();
/*      */         } 
/* 1253 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1255 */       if (t == JsonToken.START_ARRAY) {
/* 1256 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1257 */       } else if (t == JsonToken.START_OBJECT) {
/* 1258 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1260 */       return null;
/*      */     } 
/*      */     
/* 1263 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextIntValue(int defaultValue) throws IOException {
/* 1270 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1271 */       this._nameCopied = false;
/* 1272 */       JsonToken t = this._nextToken;
/* 1273 */       this._nextToken = null;
/* 1274 */       this._currToken = t;
/* 1275 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1276 */         return getIntValue();
/*      */       }
/* 1278 */       if (t == JsonToken.START_ARRAY) {
/* 1279 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1280 */       } else if (t == JsonToken.START_OBJECT) {
/* 1281 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1283 */       return defaultValue;
/*      */     } 
/*      */     
/* 1286 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long nextLongValue(long defaultValue) throws IOException {
/* 1293 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1294 */       this._nameCopied = false;
/* 1295 */       JsonToken t = this._nextToken;
/* 1296 */       this._nextToken = null;
/* 1297 */       this._currToken = t;
/* 1298 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1299 */         return getLongValue();
/*      */       }
/* 1301 */       if (t == JsonToken.START_ARRAY) {
/* 1302 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1303 */       } else if (t == JsonToken.START_OBJECT) {
/* 1304 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1306 */       return defaultValue;
/*      */     } 
/*      */     
/* 1309 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean nextBooleanValue() throws IOException {
/* 1316 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1317 */       this._nameCopied = false;
/* 1318 */       JsonToken jsonToken = this._nextToken;
/* 1319 */       this._nextToken = null;
/* 1320 */       this._currToken = jsonToken;
/* 1321 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/* 1322 */         return Boolean.TRUE;
/*      */       }
/* 1324 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/* 1325 */         return Boolean.FALSE;
/*      */       }
/* 1327 */       if (jsonToken == JsonToken.START_ARRAY) {
/* 1328 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1329 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/* 1330 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1332 */       return null;
/*      */     } 
/*      */     
/* 1335 */     JsonToken t = nextToken();
/* 1336 */     if (t == JsonToken.VALUE_TRUE) {
/* 1337 */       return Boolean.TRUE;
/*      */     }
/* 1339 */     if (t == JsonToken.VALUE_FALSE) {
/* 1340 */       return Boolean.FALSE;
/*      */     }
/* 1342 */     return null;
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
/*      */   protected JsonToken _parsePosNumber(int c) throws IOException {
/* 1368 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */     
/* 1370 */     if (c == 48) {
/* 1371 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*      */     
/* 1374 */     outBuf[0] = (char)c;
/* 1375 */     int intLen = 1;
/* 1376 */     int outPtr = 1;
/*      */ 
/*      */     
/* 1379 */     int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - 1);
/*      */     
/*      */     while (true) {
/* 1382 */       if (this._inputPtr >= end) {
/* 1383 */         return _parseNumber2(outBuf, outPtr, false, intLen);
/*      */       }
/* 1385 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1386 */       if (c < 48 || c > 57) {
/*      */         break;
/*      */       }
/* 1389 */       intLen++;
/* 1390 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 1392 */     if (c == 46 || c == 101 || c == 69) {
/* 1393 */       return _parseFloat(outBuf, outPtr, c, false, intLen);
/*      */     }
/* 1395 */     this._inputPtr--;
/* 1396 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1398 */     if (this._parsingContext.inRoot()) {
/* 1399 */       _verifyRootSpace(c);
/*      */     }
/*      */     
/* 1402 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _parseNegNumber() throws IOException {
/* 1407 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1408 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1411 */     outBuf[outPtr++] = '-';
/*      */     
/* 1413 */     if (this._inputPtr >= this._inputEnd) {
/* 1414 */       _loadMoreGuaranteed();
/*      */     }
/* 1416 */     int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     
/* 1418 */     if (c <= 48) {
/*      */       
/* 1420 */       if (c != 48) {
/* 1421 */         return _handleInvalidNumberStart(c, true);
/*      */       }
/* 1423 */       c = _verifyNoLeadingZeroes();
/* 1424 */     } else if (c > 57) {
/* 1425 */       return _handleInvalidNumberStart(c, true);
/*      */     } 
/*      */ 
/*      */     
/* 1429 */     outBuf[outPtr++] = (char)c;
/* 1430 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/* 1434 */     int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - outPtr);
/*      */     
/*      */     while (true) {
/* 1437 */       if (this._inputPtr >= end)
/*      */       {
/* 1439 */         return _parseNumber2(outBuf, outPtr, true, intLen);
/*      */       }
/* 1441 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1442 */       if (c < 48 || c > 57) {
/*      */         break;
/*      */       }
/* 1445 */       intLen++;
/* 1446 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 1448 */     if (c == 46 || c == 101 || c == 69) {
/* 1449 */       return _parseFloat(outBuf, outPtr, c, true, intLen);
/*      */     }
/*      */     
/* 1452 */     this._inputPtr--;
/* 1453 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1455 */     if (this._parsingContext.inRoot()) {
/* 1456 */       _verifyRootSpace(c);
/*      */     }
/*      */ 
/*      */     
/* 1460 */     return resetInt(true, intLen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength) throws IOException {
/*      */     while (true) {
/* 1472 */       if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1473 */         this._textBuffer.setCurrentLength(outPtr);
/* 1474 */         return resetInt(negative, intPartLength);
/*      */       } 
/* 1476 */       int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1477 */       if (c > 57 || c < 48) {
/* 1478 */         if (c == 46 || c == 101 || c == 69) {
/* 1479 */           return _parseFloat(outBuf, outPtr, c, negative, intPartLength);
/*      */         }
/*      */         break;
/*      */       } 
/* 1483 */       if (outPtr >= outBuf.length) {
/* 1484 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1485 */         outPtr = 0;
/*      */       } 
/* 1487 */       outBuf[outPtr++] = (char)c;
/* 1488 */       intPartLength++;
/*      */     } 
/* 1490 */     this._inputPtr--;
/* 1491 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1493 */     if (this._parsingContext.inRoot()) {
/* 1494 */       _verifyRootSpace(this._inputBuffer[this._inputPtr] & 0xFF);
/*      */     }
/*      */ 
/*      */     
/* 1498 */     return resetInt(negative, intPartLength);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _verifyNoLeadingZeroes() throws IOException {
/* 1509 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1510 */       return 48;
/*      */     }
/* 1512 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     
/* 1514 */     if (ch < 48 || ch > 57) {
/* 1515 */       return 48;
/*      */     }
/*      */     
/* 1518 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1519 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1522 */     this._inputPtr++;
/* 1523 */     if (ch == 48) {
/* 1524 */       while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 1525 */         ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1526 */         if (ch < 48 || ch > 57) {
/* 1527 */           return 48;
/*      */         }
/* 1529 */         this._inputPtr++;
/* 1530 */         if (ch != 48) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 1535 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
/* 1541 */     int fractLen = 0;
/* 1542 */     boolean eof = false;
/*      */ 
/*      */     
/* 1545 */     if (c == 46) {
/* 1546 */       if (outPtr >= outBuf.length) {
/* 1547 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1548 */         outPtr = 0;
/*      */       } 
/* 1550 */       outBuf[outPtr++] = (char)c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1554 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1555 */           eof = true;
/*      */           break;
/*      */         } 
/* 1558 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1559 */         if (c < 48 || c > 57) {
/*      */           break;
/*      */         }
/* 1562 */         fractLen++;
/* 1563 */         if (outPtr >= outBuf.length) {
/* 1564 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1565 */           outPtr = 0;
/*      */         } 
/* 1567 */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */       
/* 1570 */       if (fractLen == 0) {
/* 1571 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1575 */     int expLen = 0;
/* 1576 */     if (c == 101 || c == 69) {
/* 1577 */       if (outPtr >= outBuf.length) {
/* 1578 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1579 */         outPtr = 0;
/*      */       } 
/* 1581 */       outBuf[outPtr++] = (char)c;
/*      */       
/* 1583 */       if (this._inputPtr >= this._inputEnd) {
/* 1584 */         _loadMoreGuaranteed();
/*      */       }
/* 1586 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       
/* 1588 */       if (c == 45 || c == 43) {
/* 1589 */         if (outPtr >= outBuf.length) {
/* 1590 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1591 */           outPtr = 0;
/*      */         } 
/* 1593 */         outBuf[outPtr++] = (char)c;
/*      */         
/* 1595 */         if (this._inputPtr >= this._inputEnd) {
/* 1596 */           _loadMoreGuaranteed();
/*      */         }
/* 1598 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       } 
/*      */ 
/*      */       
/* 1602 */       while (c >= 48 && c <= 57) {
/* 1603 */         expLen++;
/* 1604 */         if (outPtr >= outBuf.length) {
/* 1605 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1606 */           outPtr = 0;
/*      */         } 
/* 1608 */         outBuf[outPtr++] = (char)c;
/* 1609 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1610 */           eof = true;
/*      */           break;
/*      */         } 
/* 1613 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       } 
/*      */       
/* 1616 */       if (expLen == 0) {
/* 1617 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1622 */     if (!eof) {
/* 1623 */       this._inputPtr--;
/*      */       
/* 1625 */       if (this._parsingContext.inRoot()) {
/* 1626 */         _verifyRootSpace(c);
/*      */       }
/*      */     } 
/* 1629 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1632 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
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
/* 1645 */     this._inputPtr++;
/*      */     
/* 1647 */     switch (ch) {
/*      */       case 9:
/*      */       case 32:
/*      */         return;
/*      */       case 13:
/* 1652 */         _skipCR();
/*      */         return;
/*      */       case 10:
/* 1655 */         this._currInputRow++;
/* 1656 */         this._currInputRowStart = this._inputPtr;
/*      */         return;
/*      */     } 
/* 1659 */     _reportMissingRootWS(ch);
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
/* 1670 */     if (i != 34) {
/* 1671 */       return _handleOddName(i);
/*      */     }
/*      */     
/* 1674 */     if (this._inputPtr + 13 > this._inputEnd) {
/* 1675 */       return slowParseName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1684 */     byte[] input = this._inputBuffer;
/* 1685 */     int[] codes = _icLatin1;
/*      */     
/* 1687 */     int q = input[this._inputPtr++] & 0xFF;
/*      */     
/* 1689 */     if (codes[q] == 0) {
/* 1690 */       i = input[this._inputPtr++] & 0xFF;
/* 1691 */       if (codes[i] == 0) {
/* 1692 */         q = q << 8 | i;
/* 1693 */         i = input[this._inputPtr++] & 0xFF;
/* 1694 */         if (codes[i] == 0) {
/* 1695 */           q = q << 8 | i;
/* 1696 */           i = input[this._inputPtr++] & 0xFF;
/* 1697 */           if (codes[i] == 0) {
/* 1698 */             q = q << 8 | i;
/* 1699 */             i = input[this._inputPtr++] & 0xFF;
/* 1700 */             if (codes[i] == 0) {
/* 1701 */               this._quad1 = q;
/* 1702 */               return parseMediumName(i);
/*      */             } 
/* 1704 */             if (i == 34) {
/* 1705 */               return findName(q, 4);
/*      */             }
/* 1707 */             return parseName(q, i, 4);
/*      */           } 
/* 1709 */           if (i == 34) {
/* 1710 */             return findName(q, 3);
/*      */           }
/* 1712 */           return parseName(q, i, 3);
/*      */         } 
/* 1714 */         if (i == 34) {
/* 1715 */           return findName(q, 2);
/*      */         }
/* 1717 */         return parseName(q, i, 2);
/*      */       } 
/* 1719 */       if (i == 34) {
/* 1720 */         return findName(q, 1);
/*      */       }
/* 1722 */       return parseName(q, i, 1);
/*      */     } 
/* 1724 */     if (q == 34) {
/* 1725 */       return "";
/*      */     }
/* 1727 */     return parseName(0, q, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String parseMediumName(int q2) throws IOException {
/* 1732 */     byte[] input = this._inputBuffer;
/* 1733 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1736 */     int i = input[this._inputPtr++] & 0xFF;
/* 1737 */     if (codes[i] != 0) {
/* 1738 */       if (i == 34) {
/* 1739 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1741 */       return parseName(this._quad1, q2, i, 1);
/*      */     } 
/* 1743 */     q2 = q2 << 8 | i;
/* 1744 */     i = input[this._inputPtr++] & 0xFF;
/* 1745 */     if (codes[i] != 0) {
/* 1746 */       if (i == 34) {
/* 1747 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1749 */       return parseName(this._quad1, q2, i, 2);
/*      */     } 
/* 1751 */     q2 = q2 << 8 | i;
/* 1752 */     i = input[this._inputPtr++] & 0xFF;
/* 1753 */     if (codes[i] != 0) {
/* 1754 */       if (i == 34) {
/* 1755 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1757 */       return parseName(this._quad1, q2, i, 3);
/*      */     } 
/* 1759 */     q2 = q2 << 8 | i;
/* 1760 */     i = input[this._inputPtr++] & 0xFF;
/* 1761 */     if (codes[i] != 0) {
/* 1762 */       if (i == 34) {
/* 1763 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1765 */       return parseName(this._quad1, q2, i, 4);
/*      */     } 
/* 1767 */     return parseMediumName2(i, q2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final String parseMediumName2(int q3, int q2) throws IOException {
/* 1775 */     byte[] input = this._inputBuffer;
/* 1776 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1779 */     int i = input[this._inputPtr++] & 0xFF;
/* 1780 */     if (codes[i] != 0) {
/* 1781 */       if (i == 34) {
/* 1782 */         return findName(this._quad1, q2, q3, 1);
/*      */       }
/* 1784 */       return parseName(this._quad1, q2, q3, i, 1);
/*      */     } 
/* 1786 */     q3 = q3 << 8 | i;
/* 1787 */     i = input[this._inputPtr++] & 0xFF;
/* 1788 */     if (codes[i] != 0) {
/* 1789 */       if (i == 34) {
/* 1790 */         return findName(this._quad1, q2, q3, 2);
/*      */       }
/* 1792 */       return parseName(this._quad1, q2, q3, i, 2);
/*      */     } 
/* 1794 */     q3 = q3 << 8 | i;
/* 1795 */     i = input[this._inputPtr++] & 0xFF;
/* 1796 */     if (codes[i] != 0) {
/* 1797 */       if (i == 34) {
/* 1798 */         return findName(this._quad1, q2, q3, 3);
/*      */       }
/* 1800 */       return parseName(this._quad1, q2, q3, i, 3);
/*      */     } 
/* 1802 */     q3 = q3 << 8 | i;
/* 1803 */     i = input[this._inputPtr++] & 0xFF;
/* 1804 */     if (codes[i] != 0) {
/* 1805 */       if (i == 34) {
/* 1806 */         return findName(this._quad1, q2, q3, 4);
/*      */       }
/* 1808 */       return parseName(this._quad1, q2, q3, i, 4);
/*      */     } 
/* 1810 */     return parseLongName(i, q2, q3);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String parseLongName(int q, int q2, int q3) throws IOException {
/* 1815 */     this._quadBuffer[0] = this._quad1;
/* 1816 */     this._quadBuffer[1] = q2;
/* 1817 */     this._quadBuffer[2] = q3;
/*      */ 
/*      */     
/* 1820 */     byte[] input = this._inputBuffer;
/* 1821 */     int[] codes = _icLatin1;
/* 1822 */     int qlen = 3;
/*      */     
/* 1824 */     while (this._inputPtr + 4 <= this._inputEnd) {
/* 1825 */       int i = input[this._inputPtr++] & 0xFF;
/* 1826 */       if (codes[i] != 0) {
/* 1827 */         if (i == 34) {
/* 1828 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1830 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
/*      */       } 
/*      */       
/* 1833 */       q = q << 8 | i;
/* 1834 */       i = input[this._inputPtr++] & 0xFF;
/* 1835 */       if (codes[i] != 0) {
/* 1836 */         if (i == 34) {
/* 1837 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1839 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 2);
/*      */       } 
/*      */       
/* 1842 */       q = q << 8 | i;
/* 1843 */       i = input[this._inputPtr++] & 0xFF;
/* 1844 */       if (codes[i] != 0) {
/* 1845 */         if (i == 34) {
/* 1846 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1848 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 3);
/*      */       } 
/*      */       
/* 1851 */       q = q << 8 | i;
/* 1852 */       i = input[this._inputPtr++] & 0xFF;
/* 1853 */       if (codes[i] != 0) {
/* 1854 */         if (i == 34) {
/* 1855 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1857 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 4);
/*      */       } 
/*      */ 
/*      */       
/* 1861 */       if (qlen >= this._quadBuffer.length) {
/* 1862 */         this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1864 */       this._quadBuffer[qlen++] = q;
/* 1865 */       q = i;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1872 */     return parseEscapedName(this._quadBuffer, qlen, 0, q, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String slowParseName() throws IOException {
/* 1882 */     if (this._inputPtr >= this._inputEnd && 
/* 1883 */       !_loadMore()) {
/* 1884 */       _reportInvalidEOF(": was expecting closing '\"' for name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 1887 */     int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1888 */     if (i == 34) {
/* 1889 */       return "";
/*      */     }
/* 1891 */     return parseEscapedName(this._quadBuffer, 0, 0, i, 0);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
/* 1895 */     return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
/* 1899 */     this._quadBuffer[0] = q1;
/* 1900 */     return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
/* 1904 */     this._quadBuffer[0] = q1;
/* 1905 */     this._quadBuffer[1] = q2;
/* 1906 */     return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
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
/*      */   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
/* 1921 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1924 */       if (codes[ch] != 0) {
/* 1925 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1929 */         if (ch != 92) {
/*      */           
/* 1931 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 1934 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1939 */         if (ch > 127) {
/*      */           
/* 1941 */           if (currQuadBytes >= 4) {
/* 1942 */             if (qlen >= quads.length) {
/* 1943 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 1945 */             quads[qlen++] = currQuad;
/* 1946 */             currQuad = 0;
/* 1947 */             currQuadBytes = 0;
/*      */           } 
/* 1949 */           if (ch < 2048) {
/* 1950 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1951 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 1954 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1955 */             currQuadBytes++;
/*      */             
/* 1957 */             if (currQuadBytes >= 4) {
/* 1958 */               if (qlen >= quads.length) {
/* 1959 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 1961 */               quads[qlen++] = currQuad;
/* 1962 */               currQuad = 0;
/* 1963 */               currQuadBytes = 0;
/*      */             } 
/* 1965 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1966 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 1969 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 1973 */       if (currQuadBytes < 4) {
/* 1974 */         currQuadBytes++;
/* 1975 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1977 */         if (qlen >= quads.length) {
/* 1978 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 1980 */         quads[qlen++] = currQuad;
/* 1981 */         currQuad = ch;
/* 1982 */         currQuadBytes = 1;
/*      */       } 
/* 1984 */       if (this._inputPtr >= this._inputEnd && 
/* 1985 */         !_loadMore()) {
/* 1986 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 1989 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*      */     
/* 1992 */     if (currQuadBytes > 0) {
/* 1993 */       if (qlen >= quads.length) {
/* 1994 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 1996 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/*      */     } 
/* 1998 */     String name = this._symbols.findName(quads, qlen);
/* 1999 */     if (name == null) {
/* 2000 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2002 */     return name;
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
/*      */   protected String _handleOddName(int ch) throws IOException {
/* 2014 */     if (ch == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2015 */       return _parseAposName();
/*      */     }
/*      */     
/* 2018 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 2019 */       char c = (char)_decodeCharForError(ch);
/* 2020 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2026 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 2028 */     if (codes[ch] != 0) {
/* 2029 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2035 */     int[] quads = this._quadBuffer;
/* 2036 */     int qlen = 0;
/* 2037 */     int currQuad = 0;
/* 2038 */     int currQuadBytes = 0;
/*      */ 
/*      */     
/*      */     while (true) {
/* 2042 */       if (currQuadBytes < 4) {
/* 2043 */         currQuadBytes++;
/* 2044 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2046 */         if (qlen >= quads.length) {
/* 2047 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2049 */         quads[qlen++] = currQuad;
/* 2050 */         currQuad = ch;
/* 2051 */         currQuadBytes = 1;
/*      */       } 
/* 2053 */       if (this._inputPtr >= this._inputEnd && 
/* 2054 */         !_loadMore()) {
/* 2055 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2058 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2059 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 2062 */       this._inputPtr++;
/*      */     } 
/*      */     
/* 2065 */     if (currQuadBytes > 0) {
/* 2066 */       if (qlen >= quads.length) {
/* 2067 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2069 */       quads[qlen++] = currQuad;
/*      */     } 
/* 2071 */     String name = this._symbols.findName(quads, qlen);
/* 2072 */     if (name == null) {
/* 2073 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2075 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 2085 */     if (this._inputPtr >= this._inputEnd && 
/* 2086 */       !_loadMore()) {
/* 2087 */       _reportInvalidEOF(": was expecting closing ''' for field name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 2090 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2091 */     if (ch == 39) {
/* 2092 */       return "";
/*      */     }
/* 2094 */     int[] quads = this._quadBuffer;
/* 2095 */     int qlen = 0;
/* 2096 */     int currQuad = 0;
/* 2097 */     int currQuadBytes = 0;
/*      */ 
/*      */ 
/*      */     
/* 2101 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 2104 */     while (ch != 39) {
/*      */ 
/*      */ 
/*      */       
/* 2108 */       if (codes[ch] != 0 && ch != 34) {
/* 2109 */         if (ch != 92) {
/*      */ 
/*      */           
/* 2112 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 2115 */           ch = _decodeEscaped();
/*      */         } 
/*      */         
/* 2118 */         if (ch > 127) {
/*      */           
/* 2120 */           if (currQuadBytes >= 4) {
/* 2121 */             if (qlen >= quads.length) {
/* 2122 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 2124 */             quads[qlen++] = currQuad;
/* 2125 */             currQuad = 0;
/* 2126 */             currQuadBytes = 0;
/*      */           } 
/* 2128 */           if (ch < 2048) {
/* 2129 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2130 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 2133 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2134 */             currQuadBytes++;
/*      */             
/* 2136 */             if (currQuadBytes >= 4) {
/* 2137 */               if (qlen >= quads.length) {
/* 2138 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 2140 */               quads[qlen++] = currQuad;
/* 2141 */               currQuad = 0;
/* 2142 */               currQuadBytes = 0;
/*      */             } 
/* 2144 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2145 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 2148 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 2152 */       if (currQuadBytes < 4) {
/* 2153 */         currQuadBytes++;
/* 2154 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2156 */         if (qlen >= quads.length) {
/* 2157 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2159 */         quads[qlen++] = currQuad;
/* 2160 */         currQuad = ch;
/* 2161 */         currQuadBytes = 1;
/*      */       } 
/* 2163 */       if (this._inputPtr >= this._inputEnd && 
/* 2164 */         !_loadMore()) {
/* 2165 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2168 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*      */     
/* 2171 */     if (currQuadBytes > 0) {
/* 2172 */       if (qlen >= quads.length) {
/* 2173 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2175 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/*      */     } 
/* 2177 */     String name = this._symbols.findName(quads, qlen);
/* 2178 */     if (name == null) {
/* 2179 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2181 */     return name;
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
/* 2192 */     q1 = _padLastQuad(q1, lastQuadBytes);
/*      */     
/* 2194 */     String name = this._symbols.findName(q1);
/* 2195 */     if (name != null) {
/* 2196 */       return name;
/*      */     }
/*      */     
/* 2199 */     this._quadBuffer[0] = q1;
/* 2200 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
/* 2205 */     q2 = _padLastQuad(q2, lastQuadBytes);
/*      */     
/* 2207 */     String name = this._symbols.findName(q1, q2);
/* 2208 */     if (name != null) {
/* 2209 */       return name;
/*      */     }
/*      */     
/* 2212 */     this._quadBuffer[0] = q1;
/* 2213 */     this._quadBuffer[1] = q2;
/* 2214 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
/* 2219 */     q3 = _padLastQuad(q3, lastQuadBytes);
/* 2220 */     String name = this._symbols.findName(q1, q2, q3);
/* 2221 */     if (name != null) {
/* 2222 */       return name;
/*      */     }
/* 2224 */     int[] quads = this._quadBuffer;
/* 2225 */     quads[0] = q1;
/* 2226 */     quads[1] = q2;
/* 2227 */     quads[2] = _padLastQuad(q3, lastQuadBytes);
/* 2228 */     return addName(quads, 3, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
/* 2233 */     if (qlen >= quads.length) {
/* 2234 */       this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */     }
/* 2236 */     quads[qlen++] = _padLastQuad(lastQuad, lastQuadBytes);
/* 2237 */     String name = this._symbols.findName(quads, qlen);
/* 2238 */     if (name == null) {
/* 2239 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 2241 */     return name;
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
/* 2257 */     int lastQuad, byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2266 */     if (lastQuadBytes < 4) {
/* 2267 */       lastQuad = quads[qlen - 1];
/*      */       
/* 2269 */       quads[qlen - 1] = lastQuad << 4 - lastQuadBytes << 3;
/*      */     } else {
/* 2271 */       lastQuad = 0;
/*      */     } 
/*      */ 
/*      */     
/* 2275 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2276 */     int cix = 0;
/*      */     
/* 2278 */     for (int ix = 0; ix < byteLen; ) {
/* 2279 */       int ch = quads[ix >> 2];
/* 2280 */       int byteIx = ix & 0x3;
/* 2281 */       ch = ch >> 3 - byteIx << 3 & 0xFF;
/* 2282 */       ix++;
/*      */       
/* 2284 */       if (ch > 127) {
/*      */         int needed;
/* 2286 */         if ((ch & 0xE0) == 192) {
/* 2287 */           ch &= 0x1F;
/* 2288 */           needed = 1;
/* 2289 */         } else if ((ch & 0xF0) == 224) {
/* 2290 */           ch &= 0xF;
/* 2291 */           needed = 2;
/* 2292 */         } else if ((ch & 0xF8) == 240) {
/* 2293 */           ch &= 0x7;
/* 2294 */           needed = 3;
/*      */         } else {
/* 2296 */           _reportInvalidInitial(ch);
/* 2297 */           needed = ch = 1;
/*      */         } 
/* 2299 */         if (ix + needed > byteLen) {
/* 2300 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */         }
/*      */ 
/*      */         
/* 2304 */         int ch2 = quads[ix >> 2];
/* 2305 */         byteIx = ix & 0x3;
/* 2306 */         ch2 >>= 3 - byteIx << 3;
/* 2307 */         ix++;
/*      */         
/* 2309 */         if ((ch2 & 0xC0) != 128) {
/* 2310 */           _reportInvalidOther(ch2);
/*      */         }
/* 2312 */         ch = ch << 6 | ch2 & 0x3F;
/* 2313 */         if (needed > 1) {
/* 2314 */           ch2 = quads[ix >> 2];
/* 2315 */           byteIx = ix & 0x3;
/* 2316 */           ch2 >>= 3 - byteIx << 3;
/* 2317 */           ix++;
/*      */           
/* 2319 */           if ((ch2 & 0xC0) != 128) {
/* 2320 */             _reportInvalidOther(ch2);
/*      */           }
/* 2322 */           ch = ch << 6 | ch2 & 0x3F;
/* 2323 */           if (needed > 2) {
/* 2324 */             ch2 = quads[ix >> 2];
/* 2325 */             byteIx = ix & 0x3;
/* 2326 */             ch2 >>= 3 - byteIx << 3;
/* 2327 */             ix++;
/* 2328 */             if ((ch2 & 0xC0) != 128) {
/* 2329 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 2331 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           } 
/*      */         } 
/* 2334 */         if (needed > 2) {
/* 2335 */           ch -= 65536;
/* 2336 */           if (cix >= cbuf.length) {
/* 2337 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 2339 */           cbuf[cix++] = (char)(55296 + (ch >> 10));
/* 2340 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         } 
/*      */       } 
/* 2343 */       if (cix >= cbuf.length) {
/* 2344 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 2346 */       cbuf[cix++] = (char)ch;
/*      */     } 
/*      */ 
/*      */     
/* 2350 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 2352 */     if (lastQuadBytes < 4) {
/* 2353 */       quads[qlen - 1] = lastQuad;
/*      */     }
/* 2355 */     return this._symbols.addName(baseName, quads, qlen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int _padLastQuad(int q, int bytes) {
/* 2362 */     return (bytes == 4) ? q : (q | -1 << bytes << 3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _loadMoreGuaranteed() throws IOException {
/* 2372 */     if (!_loadMore()) _reportInvalidEOF();
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _finishString() throws IOException {
/* 2379 */     int ptr = this._inputPtr;
/* 2380 */     if (ptr >= this._inputEnd) {
/* 2381 */       _loadMoreGuaranteed();
/* 2382 */       ptr = this._inputPtr;
/*      */     } 
/* 2384 */     int outPtr = 0;
/* 2385 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2386 */     int[] codes = _icUTF8;
/*      */     
/* 2388 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2389 */     byte[] inputBuffer = this._inputBuffer;
/* 2390 */     while (ptr < max) {
/* 2391 */       int c = inputBuffer[ptr] & 0xFF;
/* 2392 */       if (codes[c] != 0) {
/* 2393 */         if (c == 34) {
/* 2394 */           this._inputPtr = ptr + 1;
/* 2395 */           this._textBuffer.setCurrentLength(outPtr);
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       } 
/* 2400 */       ptr++;
/* 2401 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2403 */     this._inputPtr = ptr;
/* 2404 */     _finishString2(outBuf, outPtr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _finishAndReturnString() throws IOException {
/* 2413 */     int ptr = this._inputPtr;
/* 2414 */     if (ptr >= this._inputEnd) {
/* 2415 */       _loadMoreGuaranteed();
/* 2416 */       ptr = this._inputPtr;
/*      */     } 
/* 2418 */     int outPtr = 0;
/* 2419 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2420 */     int[] codes = _icUTF8;
/*      */     
/* 2422 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2423 */     byte[] inputBuffer = this._inputBuffer;
/* 2424 */     while (ptr < max) {
/* 2425 */       int c = inputBuffer[ptr] & 0xFF;
/* 2426 */       if (codes[c] != 0) {
/* 2427 */         if (c == 34) {
/* 2428 */           this._inputPtr = ptr + 1;
/* 2429 */           return this._textBuffer.setCurrentAndReturn(outPtr);
/*      */         } 
/*      */         break;
/*      */       } 
/* 2433 */       ptr++;
/* 2434 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2436 */     this._inputPtr = ptr;
/* 2437 */     _finishString2(outBuf, outPtr);
/* 2438 */     return this._textBuffer.contentsAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _finishString2(char[] outBuf, int outPtr) throws IOException {
/* 2447 */     int[] codes = _icUTF8;
/* 2448 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2455 */       int ptr = this._inputPtr;
/* 2456 */       if (ptr >= this._inputEnd) {
/* 2457 */         _loadMoreGuaranteed();
/* 2458 */         ptr = this._inputPtr;
/*      */       } 
/* 2460 */       if (outPtr >= outBuf.length) {
/* 2461 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2462 */         outPtr = 0;
/*      */       } 
/* 2464 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2465 */       while (ptr < max) {
/* 2466 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2467 */         if (codes[c] != 0) {
/* 2468 */           this._inputPtr = ptr;
/*      */         } else {
/*      */           
/* 2471 */           outBuf[outPtr++] = (char)c;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 2476 */         if (c == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 2480 */         switch (codes[c]) {
/*      */           case 1:
/* 2482 */             c = _decodeEscaped();
/*      */             break;
/*      */           case 2:
/* 2485 */             c = _decodeUtf8_2(c);
/*      */             break;
/*      */           case 3:
/* 2488 */             if (this._inputEnd - this._inputPtr >= 2) {
/* 2489 */               c = _decodeUtf8_3fast(c); break;
/*      */             } 
/* 2491 */             c = _decodeUtf8_3(c);
/*      */             break;
/*      */           
/*      */           case 4:
/* 2495 */             c = _decodeUtf8_4(c);
/*      */             
/* 2497 */             outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2498 */             if (outPtr >= outBuf.length) {
/* 2499 */               outBuf = this._textBuffer.finishCurrentSegment();
/* 2500 */               outPtr = 0;
/*      */             } 
/* 2502 */             c = 0xDC00 | c & 0x3FF;
/*      */             break;
/*      */           
/*      */           default:
/* 2506 */             if (c < 32) {
/*      */               
/* 2508 */               _throwUnquotedSpace(c, "string value");
/*      */               break;
/*      */             } 
/* 2511 */             _reportInvalidChar(c);
/*      */             break;
/*      */         } 
/*      */         
/* 2515 */         if (outPtr >= outBuf.length) {
/* 2516 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 2517 */           outPtr = 0;
/*      */         } 
/*      */         
/* 2520 */         outBuf[outPtr++] = (char)c;
/*      */       }  this._inputPtr = ptr;
/* 2522 */     }  this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _skipString() throws IOException {
/* 2532 */     this._tokenIncomplete = false;
/*      */ 
/*      */     
/* 2535 */     int[] codes = _icUTF8;
/* 2536 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2544 */       int ptr = this._inputPtr;
/* 2545 */       int max = this._inputEnd;
/* 2546 */       if (ptr >= max) {
/* 2547 */         _loadMoreGuaranteed();
/* 2548 */         ptr = this._inputPtr;
/* 2549 */         max = this._inputEnd;
/*      */       } 
/* 2551 */       while (ptr < max) {
/* 2552 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2553 */         if (codes[c] != 0) {
/* 2554 */           this._inputPtr = ptr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2561 */           if (c != 34) {
/*      */ 
/*      */ 
/*      */             
/* 2565 */             switch (codes[c]) {
/*      */               case 1:
/* 2567 */                 _decodeEscaped();
/*      */                 continue;
/*      */               case 2:
/* 2570 */                 _skipUtf8_2();
/*      */                 continue;
/*      */               case 3:
/* 2573 */                 _skipUtf8_3();
/*      */                 continue;
/*      */               case 4:
/* 2576 */                 _skipUtf8_4(c);
/*      */                 continue;
/*      */             } 
/* 2579 */             if (c < 32) {
/* 2580 */               _throwUnquotedSpace(c, "string value");
/*      */               continue;
/*      */             } 
/* 2583 */             _reportInvalidChar(c);
/*      */             continue;
/*      */           } 
/*      */           return;
/*      */         } 
/*      */       } 
/*      */       this._inputPtr = ptr;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
/* 2596 */     switch (c) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/* 2605 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/* 2613 */         if ((this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 2614 */           this._inputPtr--;
/* 2615 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */ 
/*      */ 
/*      */       
/*      */       case 125:
/* 2621 */         _reportUnexpectedChar(c, "expected a value");
/*      */       case 39:
/* 2623 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2624 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */       case 78:
/* 2628 */         _matchToken("NaN", 1);
/* 2629 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2630 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 2632 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 2635 */         _matchToken("Infinity", 1);
/* 2636 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2637 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 2639 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 2642 */         if (this._inputPtr >= this._inputEnd && 
/* 2643 */           !_loadMore()) {
/* 2644 */           _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */         }
/*      */         
/* 2647 */         return _handleInvalidNumberStart(this._inputBuffer[this._inputPtr++] & 0xFF, false);
/*      */     } 
/*      */     
/* 2650 */     if (Character.isJavaIdentifierStart(c)) {
/* 2651 */       _reportInvalidToken("" + (char)c, _validJsonTokenList());
/*      */     }
/*      */     
/* 2654 */     _reportUnexpectedChar(c, "expected a valid value " + _validJsonValueList());
/* 2655 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 2660 */     int c = 0;
/*      */     
/* 2662 */     int outPtr = 0;
/* 2663 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */     
/* 2666 */     int[] codes = _icUTF8;
/* 2667 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true)
/* 2674 */     { if (this._inputPtr >= this._inputEnd) {
/* 2675 */         _loadMoreGuaranteed();
/*      */       }
/* 2677 */       if (outPtr >= outBuf.length) {
/* 2678 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2679 */         outPtr = 0;
/*      */       } 
/* 2681 */       int max = this._inputEnd;
/*      */       
/* 2683 */       int max2 = this._inputPtr + outBuf.length - outPtr;
/* 2684 */       if (max2 < max) {
/* 2685 */         max = max2;
/*      */       }
/*      */       
/* 2688 */       while (this._inputPtr < max)
/* 2689 */       { c = inputBuffer[this._inputPtr++] & 0xFF;
/* 2690 */         if (c == 39 || codes[c] != 0)
/*      */         
/*      */         { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2698 */           if (c != 39) {
/*      */ 
/*      */ 
/*      */             
/* 2702 */             switch (codes[c]) {
/*      */               case 1:
/* 2704 */                 c = _decodeEscaped();
/*      */                 break;
/*      */               case 2:
/* 2707 */                 c = _decodeUtf8_2(c);
/*      */                 break;
/*      */               case 3:
/* 2710 */                 if (this._inputEnd - this._inputPtr >= 2) {
/* 2711 */                   c = _decodeUtf8_3fast(c); break;
/*      */                 } 
/* 2713 */                 c = _decodeUtf8_3(c);
/*      */                 break;
/*      */               
/*      */               case 4:
/* 2717 */                 c = _decodeUtf8_4(c);
/*      */                 
/* 2719 */                 outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2720 */                 if (outPtr >= outBuf.length) {
/* 2721 */                   outBuf = this._textBuffer.finishCurrentSegment();
/* 2722 */                   outPtr = 0;
/*      */                 } 
/* 2724 */                 c = 0xDC00 | c & 0x3FF;
/*      */                 break;
/*      */               
/*      */               default:
/* 2728 */                 if (c < 32) {
/* 2729 */                   _throwUnquotedSpace(c, "string value");
/*      */                 }
/*      */                 
/* 2732 */                 _reportInvalidChar(c);
/*      */                 break;
/*      */             } 
/* 2735 */             if (outPtr >= outBuf.length) {
/* 2736 */               outBuf = this._textBuffer.finishCurrentSegment();
/* 2737 */               outPtr = 0;
/*      */             } 
/*      */             
/* 2740 */             outBuf[outPtr++] = (char)c; continue;
/*      */           } 
/* 2742 */           this._textBuffer.setCurrentLength(outPtr);
/*      */           
/* 2744 */           return JsonToken.VALUE_STRING; }  outBuf[outPtr++] = (char)c; }  }  this._textBuffer.setCurrentLength(outPtr); return JsonToken.VALUE_STRING;
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
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
/* 2759 */     while (ch == 73) {
/* 2760 */       String match; if (this._inputPtr >= this._inputEnd && 
/* 2761 */         !_loadMore()) {
/* 2762 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_FLOAT);
/*      */       }
/*      */       
/* 2765 */       ch = this._inputBuffer[this._inputPtr++];
/*      */       
/* 2767 */       if (ch == 78) {
/* 2768 */         match = neg ? "-INF" : "+INF";
/* 2769 */       } else if (ch == 110) {
/* 2770 */         match = neg ? "-Infinity" : "+Infinity";
/*      */       } else {
/*      */         break;
/*      */       } 
/* 2774 */       _matchToken(match, 3);
/* 2775 */       if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2776 */         return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */       }
/* 2778 */       _reportError("Non-standard token '%s': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow", match);
/*      */     } 
/*      */     
/* 2781 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2782 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _matchTrue() throws IOException {
/* 2788 */     int ptr = this._inputPtr;
/* 2789 */     if (ptr + 3 < this._inputEnd) {
/* 2790 */       byte[] buf = this._inputBuffer;
/* 2791 */       if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
/*      */ 
/*      */         
/* 2794 */         int ch = buf[ptr] & 0xFF;
/* 2795 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2796 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2801 */     _matchToken2("true", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchFalse() throws IOException {
/* 2806 */     int ptr = this._inputPtr;
/* 2807 */     if (ptr + 4 < this._inputEnd) {
/* 2808 */       byte[] buf = this._inputBuffer;
/* 2809 */       if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
/*      */ 
/*      */ 
/*      */         
/* 2813 */         int ch = buf[ptr] & 0xFF;
/* 2814 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2815 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2820 */     _matchToken2("false", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchNull() throws IOException {
/* 2825 */     int ptr = this._inputPtr;
/* 2826 */     if (ptr + 3 < this._inputEnd) {
/* 2827 */       byte[] buf = this._inputBuffer;
/* 2828 */       if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
/*      */ 
/*      */         
/* 2831 */         int ch = buf[ptr] & 0xFF;
/* 2832 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2833 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2838 */     _matchToken2("null", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2843 */     int len = matchStr.length();
/* 2844 */     if (this._inputPtr + len >= this._inputEnd) {
/* 2845 */       _matchToken2(matchStr, i);
/*      */       return;
/*      */     } 
/*      */     do {
/* 2849 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2850 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2852 */       this._inputPtr++;
/* 2853 */     } while (++i < len);
/*      */     
/* 2855 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2856 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2857 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _matchToken2(String matchStr, int i) throws IOException {
/* 2863 */     int len = matchStr.length();
/*      */     do {
/* 2865 */       if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr
/* 2866 */         .charAt(i)) {
/* 2867 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2869 */       this._inputPtr++;
/* 2870 */     } while (++i < len);
/*      */ 
/*      */     
/* 2873 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */       return;
/*      */     }
/* 2876 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2877 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2878 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
/* 2884 */     char c = (char)_decodeCharForError(ch);
/* 2885 */     if (Character.isJavaIdentifierPart(c)) {
/* 2886 */       _reportInvalidToken(matchStr.substring(0, i));
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
/* 2898 */     while (this._inputPtr < this._inputEnd) {
/* 2899 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2900 */       if (i > 32) {
/* 2901 */         if (i == 47 || i == 35) {
/* 2902 */           this._inputPtr--;
/* 2903 */           return _skipWS2();
/*      */         } 
/* 2905 */         return i;
/*      */       } 
/* 2907 */       if (i != 32) {
/* 2908 */         if (i == 10) {
/* 2909 */           this._currInputRow++;
/* 2910 */           this._currInputRowStart = this._inputPtr; continue;
/* 2911 */         }  if (i == 13) {
/* 2912 */           _skipCR(); continue;
/* 2913 */         }  if (i != 9) {
/* 2914 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2918 */     return _skipWS2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWS2() throws IOException {
/* 2923 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2924 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2925 */       if (i > 32) {
/* 2926 */         if (i == 47) {
/* 2927 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2930 */         if (i == 35 && 
/* 2931 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2935 */         return i;
/*      */       } 
/* 2937 */       if (i != 32) {
/* 2938 */         if (i == 10) {
/* 2939 */           this._currInputRow++;
/* 2940 */           this._currInputRowStart = this._inputPtr; continue;
/* 2941 */         }  if (i == 13) {
/* 2942 */           _skipCR(); continue;
/* 2943 */         }  if (i != 9) {
/* 2944 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2948 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 2955 */     if (this._inputPtr >= this._inputEnd && 
/* 2956 */       !_loadMore()) {
/* 2957 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 2960 */     int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2961 */     if (i > 32) {
/* 2962 */       if (i == 47 || i == 35) {
/* 2963 */         this._inputPtr--;
/* 2964 */         return _skipWSOrEnd2();
/*      */       } 
/* 2966 */       return i;
/*      */     } 
/* 2968 */     if (i != 32) {
/* 2969 */       if (i == 10) {
/* 2970 */         this._currInputRow++;
/* 2971 */         this._currInputRowStart = this._inputPtr;
/* 2972 */       } else if (i == 13) {
/* 2973 */         _skipCR();
/* 2974 */       } else if (i != 9) {
/* 2975 */         _throwInvalidSpace(i);
/*      */       } 
/*      */     }
/*      */     
/* 2979 */     while (this._inputPtr < this._inputEnd) {
/* 2980 */       i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2981 */       if (i > 32) {
/* 2982 */         if (i == 47 || i == 35) {
/* 2983 */           this._inputPtr--;
/* 2984 */           return _skipWSOrEnd2();
/*      */         } 
/* 2986 */         return i;
/*      */       } 
/* 2988 */       if (i != 32) {
/* 2989 */         if (i == 10) {
/* 2990 */           this._currInputRow++;
/* 2991 */           this._currInputRowStart = this._inputPtr; continue;
/* 2992 */         }  if (i == 13) {
/* 2993 */           _skipCR(); continue;
/* 2994 */         }  if (i != 9) {
/* 2995 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2999 */     return _skipWSOrEnd2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd2() throws IOException {
/* 3004 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3005 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3006 */       if (i > 32) {
/* 3007 */         if (i == 47) {
/* 3008 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 3011 */         if (i == 35 && 
/* 3012 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 3016 */         return i;
/* 3017 */       }  if (i != 32) {
/* 3018 */         if (i == 10) {
/* 3019 */           this._currInputRow++;
/* 3020 */           this._currInputRowStart = this._inputPtr; continue;
/* 3021 */         }  if (i == 13) {
/* 3022 */           _skipCR(); continue;
/* 3023 */         }  if (i != 9) {
/* 3024 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 3029 */     return _eofAsNextChar();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 3034 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 3035 */       return _skipColon2(false);
/*      */     }
/*      */     
/* 3038 */     int i = this._inputBuffer[this._inputPtr];
/* 3039 */     if (i == 58) {
/* 3040 */       i = this._inputBuffer[++this._inputPtr];
/* 3041 */       if (i > 32) {
/* 3042 */         if (i == 47 || i == 35) {
/* 3043 */           return _skipColon2(true);
/*      */         }
/* 3045 */         this._inputPtr++;
/* 3046 */         return i;
/*      */       } 
/* 3048 */       if (i == 32 || i == 9) {
/* 3049 */         i = this._inputBuffer[++this._inputPtr];
/* 3050 */         if (i > 32) {
/* 3051 */           if (i == 47 || i == 35) {
/* 3052 */             return _skipColon2(true);
/*      */           }
/* 3054 */           this._inputPtr++;
/* 3055 */           return i;
/*      */         } 
/*      */       } 
/* 3058 */       return _skipColon2(true);
/*      */     } 
/* 3060 */     if (i == 32 || i == 9) {
/* 3061 */       i = this._inputBuffer[++this._inputPtr];
/*      */     }
/* 3063 */     if (i == 58) {
/* 3064 */       i = this._inputBuffer[++this._inputPtr];
/* 3065 */       if (i > 32) {
/* 3066 */         if (i == 47 || i == 35) {
/* 3067 */           return _skipColon2(true);
/*      */         }
/* 3069 */         this._inputPtr++;
/* 3070 */         return i;
/*      */       } 
/* 3072 */       if (i == 32 || i == 9) {
/* 3073 */         i = this._inputBuffer[++this._inputPtr];
/* 3074 */         if (i > 32) {
/* 3075 */           if (i == 47 || i == 35) {
/* 3076 */             return _skipColon2(true);
/*      */           }
/* 3078 */           this._inputPtr++;
/* 3079 */           return i;
/*      */         } 
/*      */       } 
/* 3082 */       return _skipColon2(true);
/*      */     } 
/* 3084 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException {
/* 3089 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3090 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       
/* 3092 */       if (i > 32) {
/* 3093 */         if (i == 47) {
/* 3094 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 3097 */         if (i == 35 && 
/* 3098 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 3102 */         if (gotColon) {
/* 3103 */           return i;
/*      */         }
/* 3105 */         if (i != 58) {
/* 3106 */           _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */         }
/* 3108 */         gotColon = true; continue;
/* 3109 */       }  if (i != 32) {
/* 3110 */         if (i == 10) {
/* 3111 */           this._currInputRow++;
/* 3112 */           this._currInputRowStart = this._inputPtr; continue;
/* 3113 */         }  if (i == 13) {
/* 3114 */           _skipCR(); continue;
/* 3115 */         }  if (i != 9) {
/* 3116 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 3120 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 3122 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipComment() throws IOException {
/* 3127 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 3128 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 3131 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 3132 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 3134 */     int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3135 */     if (c == 47) {
/* 3136 */       _skipLine();
/* 3137 */     } else if (c == 42) {
/* 3138 */       _skipCComment();
/*      */     } else {
/* 3140 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipCComment() throws IOException {
/* 3147 */     int[] codes = CharTypes.getInputCodeComment();
/*      */ 
/*      */ 
/*      */     
/* 3151 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3152 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3153 */       int code = codes[i];
/* 3154 */       if (code != 0) {
/* 3155 */         switch (code) {
/*      */           case 42:
/* 3157 */             if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */               break;
/*      */             }
/* 3160 */             if (this._inputBuffer[this._inputPtr] == 47) {
/* 3161 */               this._inputPtr++;
/*      */               return;
/*      */             } 
/*      */             continue;
/*      */           case 10:
/* 3166 */             this._currInputRow++;
/* 3167 */             this._currInputRowStart = this._inputPtr;
/*      */             continue;
/*      */           case 13:
/* 3170 */             _skipCR();
/*      */             continue;
/*      */           case 2:
/* 3173 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 3176 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 3179 */             _skipUtf8_4(i);
/*      */             continue;
/*      */         } 
/*      */         
/* 3183 */         _reportInvalidChar(i);
/*      */       } 
/*      */     } 
/*      */     
/* 3187 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */ 
/*      */   
/*      */   private final boolean _skipYAMLComment() throws IOException {
/* 3192 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 3193 */       return false;
/*      */     }
/* 3195 */     _skipLine();
/* 3196 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipLine() throws IOException {
/* 3206 */     int[] codes = CharTypes.getInputCodeComment();
/* 3207 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3208 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3209 */       int code = codes[i];
/* 3210 */       if (code != 0) {
/* 3211 */         switch (code) {
/*      */           case 10:
/* 3213 */             this._currInputRow++;
/* 3214 */             this._currInputRowStart = this._inputPtr;
/*      */             return;
/*      */           case 13:
/* 3217 */             _skipCR();
/*      */             return;
/*      */           case 42:
/*      */             continue;
/*      */           case 2:
/* 3222 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 3225 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 3228 */             _skipUtf8_4(i);
/*      */             continue;
/*      */         } 
/* 3231 */         if (code < 0)
/*      */         {
/* 3233 */           _reportInvalidChar(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 3243 */     if (this._inputPtr >= this._inputEnd && 
/* 3244 */       !_loadMore()) {
/* 3245 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 3248 */     int c = this._inputBuffer[this._inputPtr++];
/*      */     
/* 3250 */     switch (c) {
/*      */       
/*      */       case 98:
/* 3253 */         return '\b';
/*      */       case 116:
/* 3255 */         return '\t';
/*      */       case 110:
/* 3257 */         return '\n';
/*      */       case 102:
/* 3259 */         return '\f';
/*      */       case 114:
/* 3261 */         return '\r';
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 3267 */         return (char)c;
/*      */       
/*      */       case 117:
/*      */         break;
/*      */       
/*      */       default:
/* 3273 */         return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     } 
/*      */ 
/*      */     
/* 3277 */     int value = 0;
/* 3278 */     for (int i = 0; i < 4; i++) {
/* 3279 */       if (this._inputPtr >= this._inputEnd && 
/* 3280 */         !_loadMore()) {
/* 3281 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 3284 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3285 */       int digit = CharTypes.charToHex(ch);
/* 3286 */       if (digit < 0) {
/* 3287 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 3289 */       value = value << 4 | digit;
/*      */     } 
/* 3291 */     return (char)value;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _decodeCharForError(int firstByte) throws IOException {
/* 3296 */     int c = firstByte & 0xFF;
/* 3297 */     if (c > 127) {
/*      */       int needed;
/*      */ 
/*      */       
/* 3301 */       if ((c & 0xE0) == 192) {
/* 3302 */         c &= 0x1F;
/* 3303 */         needed = 1;
/* 3304 */       } else if ((c & 0xF0) == 224) {
/* 3305 */         c &= 0xF;
/* 3306 */         needed = 2;
/* 3307 */       } else if ((c & 0xF8) == 240) {
/*      */         
/* 3309 */         c &= 0x7;
/* 3310 */         needed = 3;
/*      */       } else {
/* 3312 */         _reportInvalidInitial(c & 0xFF);
/* 3313 */         needed = 1;
/*      */       } 
/*      */       
/* 3316 */       int d = nextByte();
/* 3317 */       if ((d & 0xC0) != 128) {
/* 3318 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 3320 */       c = c << 6 | d & 0x3F;
/*      */       
/* 3322 */       if (needed > 1) {
/* 3323 */         d = nextByte();
/* 3324 */         if ((d & 0xC0) != 128) {
/* 3325 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 3327 */         c = c << 6 | d & 0x3F;
/* 3328 */         if (needed > 2) {
/* 3329 */           d = nextByte();
/* 3330 */           if ((d & 0xC0) != 128) {
/* 3331 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 3333 */           c = c << 6 | d & 0x3F;
/*      */         } 
/*      */       } 
/*      */     } 
/* 3337 */     return c;
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
/* 3348 */     if (this._inputPtr >= this._inputEnd) {
/* 3349 */       _loadMoreGuaranteed();
/*      */     }
/* 3351 */     int d = this._inputBuffer[this._inputPtr++];
/* 3352 */     if ((d & 0xC0) != 128) {
/* 3353 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3355 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3(int c1) throws IOException {
/* 3360 */     if (this._inputPtr >= this._inputEnd) {
/* 3361 */       _loadMoreGuaranteed();
/*      */     }
/* 3363 */     c1 &= 0xF;
/* 3364 */     int d = this._inputBuffer[this._inputPtr++];
/* 3365 */     if ((d & 0xC0) != 128) {
/* 3366 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3368 */     int c = c1 << 6 | d & 0x3F;
/* 3369 */     if (this._inputPtr >= this._inputEnd) {
/* 3370 */       _loadMoreGuaranteed();
/*      */     }
/* 3372 */     d = this._inputBuffer[this._inputPtr++];
/* 3373 */     if ((d & 0xC0) != 128) {
/* 3374 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3376 */     c = c << 6 | d & 0x3F;
/* 3377 */     return c;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3fast(int c1) throws IOException {
/* 3382 */     c1 &= 0xF;
/* 3383 */     int d = this._inputBuffer[this._inputPtr++];
/* 3384 */     if ((d & 0xC0) != 128) {
/* 3385 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3387 */     int c = c1 << 6 | d & 0x3F;
/* 3388 */     d = this._inputBuffer[this._inputPtr++];
/* 3389 */     if ((d & 0xC0) != 128) {
/* 3390 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3392 */     c = c << 6 | d & 0x3F;
/* 3393 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_4(int c) throws IOException {
/* 3402 */     if (this._inputPtr >= this._inputEnd) {
/* 3403 */       _loadMoreGuaranteed();
/*      */     }
/* 3405 */     int d = this._inputBuffer[this._inputPtr++];
/* 3406 */     if ((d & 0xC0) != 128) {
/* 3407 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3409 */     c = (c & 0x7) << 6 | d & 0x3F;
/*      */     
/* 3411 */     if (this._inputPtr >= this._inputEnd) {
/* 3412 */       _loadMoreGuaranteed();
/*      */     }
/* 3414 */     d = this._inputBuffer[this._inputPtr++];
/* 3415 */     if ((d & 0xC0) != 128) {
/* 3416 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3418 */     c = c << 6 | d & 0x3F;
/* 3419 */     if (this._inputPtr >= this._inputEnd) {
/* 3420 */       _loadMoreGuaranteed();
/*      */     }
/* 3422 */     d = this._inputBuffer[this._inputPtr++];
/* 3423 */     if ((d & 0xC0) != 128) {
/* 3424 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3430 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_2() throws IOException {
/* 3435 */     if (this._inputPtr >= this._inputEnd) {
/* 3436 */       _loadMoreGuaranteed();
/*      */     }
/* 3438 */     int c = this._inputBuffer[this._inputPtr++];
/* 3439 */     if ((c & 0xC0) != 128) {
/* 3440 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_3() throws IOException {
/* 3449 */     if (this._inputPtr >= this._inputEnd) {
/* 3450 */       _loadMoreGuaranteed();
/*      */     }
/*      */     
/* 3453 */     int c = this._inputBuffer[this._inputPtr++];
/* 3454 */     if ((c & 0xC0) != 128) {
/* 3455 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/* 3457 */     if (this._inputPtr >= this._inputEnd) {
/* 3458 */       _loadMoreGuaranteed();
/*      */     }
/* 3460 */     c = this._inputBuffer[this._inputPtr++];
/* 3461 */     if ((c & 0xC0) != 128) {
/* 3462 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_4(int c) throws IOException {
/* 3468 */     if (this._inputPtr >= this._inputEnd) {
/* 3469 */       _loadMoreGuaranteed();
/*      */     }
/* 3471 */     int d = this._inputBuffer[this._inputPtr++];
/* 3472 */     if ((d & 0xC0) != 128) {
/* 3473 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3475 */     if (this._inputPtr >= this._inputEnd) {
/* 3476 */       _loadMoreGuaranteed();
/*      */     }
/* 3478 */     d = this._inputBuffer[this._inputPtr++];
/* 3479 */     if ((d & 0xC0) != 128) {
/* 3480 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3482 */     if (this._inputPtr >= this._inputEnd) {
/* 3483 */       _loadMoreGuaranteed();
/*      */     }
/* 3485 */     d = this._inputBuffer[this._inputPtr++];
/* 3486 */     if ((d & 0xC0) != 128) {
/* 3487 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
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
/*      */   protected final void _skipCR() throws IOException {
/* 3503 */     if ((this._inputPtr < this._inputEnd || _loadMore()) && 
/* 3504 */       this._inputBuffer[this._inputPtr] == 10) {
/* 3505 */       this._inputPtr++;
/*      */     }
/*      */     
/* 3508 */     this._currInputRow++;
/* 3509 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private int nextByte() throws IOException {
/* 3514 */     if (this._inputPtr >= this._inputEnd) {
/* 3515 */       _loadMoreGuaranteed();
/*      */     }
/* 3517 */     return this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, int ptr) throws IOException {
/* 3527 */     this._inputPtr = ptr;
/* 3528 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart) throws IOException {
/* 3532 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
/* 3541 */     StringBuilder sb = new StringBuilder(matchedPart);
/* 3542 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3543 */       int i = this._inputBuffer[this._inputPtr++];
/* 3544 */       char c = (char)_decodeCharForError(i);
/* 3545 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/*      */ 
/*      */       
/* 3550 */       sb.append(c);
/* 3551 */       if (sb.length() >= 256) {
/* 3552 */         sb.append("...");
/*      */         break;
/*      */       } 
/*      */     } 
/* 3556 */     _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidChar(int c) throws JsonParseException {
/* 3562 */     if (c < 32) {
/* 3563 */       _throwInvalidSpace(c);
/*      */     }
/* 3565 */     _reportInvalidInitial(c);
/*      */   }
/*      */   
/*      */   protected void _reportInvalidInitial(int mask) throws JsonParseException {
/* 3569 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask) throws JsonParseException {
/* 3573 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
/* 3579 */     this._inputPtr = ptr;
/* 3580 */     _reportInvalidOther(mask);
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
/* 3596 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 3602 */       if (this._inputPtr >= this._inputEnd) {
/* 3603 */         _loadMoreGuaranteed();
/*      */       }
/* 3605 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3606 */       if (ch > 32) {
/* 3607 */         int bits = b64variant.decodeBase64Char(ch);
/* 3608 */         if (bits < 0) {
/* 3609 */           if (ch == 34) {
/* 3610 */             return builder.toByteArray();
/*      */           }
/* 3612 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 3613 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 3617 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/* 3621 */         if (this._inputPtr >= this._inputEnd) {
/* 3622 */           _loadMoreGuaranteed();
/*      */         }
/* 3624 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3625 */         bits = b64variant.decodeBase64Char(ch);
/* 3626 */         if (bits < 0) {
/* 3627 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 3629 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/* 3632 */         if (this._inputPtr >= this._inputEnd) {
/* 3633 */           _loadMoreGuaranteed();
/*      */         }
/* 3635 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3636 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 3639 */         if (bits < 0) {
/* 3640 */           if (bits != -2) {
/*      */             
/* 3642 */             if (ch == 34) {
/* 3643 */               decodedData >>= 4;
/* 3644 */               builder.append(decodedData);
/* 3645 */               if (b64variant.usesPadding()) {
/* 3646 */                 this._inputPtr--;
/* 3647 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 3649 */               return builder.toByteArray();
/*      */             } 
/* 3651 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 3653 */           if (bits == -2) {
/*      */             
/* 3655 */             if (this._inputPtr >= this._inputEnd) {
/* 3656 */               _loadMoreGuaranteed();
/*      */             }
/* 3658 */             ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3659 */             if (!b64variant.usesPaddingChar(ch) && 
/* 3660 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/* 3661 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 3665 */             decodedData >>= 4;
/* 3666 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 3671 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 3673 */         if (this._inputPtr >= this._inputEnd) {
/* 3674 */           _loadMoreGuaranteed();
/*      */         }
/* 3676 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3677 */         bits = b64variant.decodeBase64Char(ch);
/* 3678 */         if (bits < 0) {
/* 3679 */           if (bits != -2) {
/*      */             
/* 3681 */             if (ch == 34) {
/* 3682 */               decodedData >>= 2;
/* 3683 */               builder.appendTwoBytes(decodedData);
/* 3684 */               if (b64variant.usesPadding()) {
/* 3685 */                 this._inputPtr--;
/* 3686 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 3688 */               return builder.toByteArray();
/*      */             } 
/* 3690 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 3692 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */             
/* 3696 */             decodedData >>= 2;
/* 3697 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 3702 */         decodedData = decodedData << 6 | bits;
/* 3703 */         builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation() {
/* 3717 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 3718 */       long total = this._currInputProcessed + (this._nameStartOffset - 1);
/* 3719 */       return new JsonLocation(_getSourceReference(), total, -1L, this._nameStartRow, this._nameStartCol);
/*      */     } 
/*      */     
/* 3722 */     return new JsonLocation(_getSourceReference(), this._tokenInputTotal - 1L, -1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 3730 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 3731 */     return new JsonLocation(_getSourceReference(), this._currInputProcessed + this._inputPtr, -1L, this._currInputRow, col);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateLocation() {
/* 3739 */     this._tokenInputRow = this._currInputRow;
/* 3740 */     int ptr = this._inputPtr;
/* 3741 */     this._tokenInputTotal = this._currInputProcessed + ptr;
/* 3742 */     this._tokenInputCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateNameLocation() {
/* 3748 */     this._nameStartRow = this._currInputRow;
/* 3749 */     int ptr = this._inputPtr;
/* 3750 */     this._nameStartOffset = ptr;
/* 3751 */     this._nameStartCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _closeScope(int i) throws JsonParseException {
/* 3761 */     if (i == 125) {
/* 3762 */       _closeObjectScope();
/* 3763 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/* 3765 */     _closeArrayScope();
/* 3766 */     return this._currToken = JsonToken.END_ARRAY;
/*      */   }
/*      */   
/*      */   private final void _closeArrayScope() throws JsonParseException {
/* 3770 */     _updateLocation();
/* 3771 */     if (!this._parsingContext.inArray()) {
/* 3772 */       _reportMismatchedEndMarker(93, '}');
/*      */     }
/* 3774 */     this._parsingContext = this._parsingContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */   private final void _closeObjectScope() throws JsonParseException {
/* 3778 */     _updateLocation();
/* 3779 */     if (!this._parsingContext.inObject()) {
/* 3780 */       _reportMismatchedEndMarker(125, ']');
/*      */     }
/* 3782 */     this._parsingContext = this._parsingContext.clearAndGetParent();
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\UTF8StreamJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */