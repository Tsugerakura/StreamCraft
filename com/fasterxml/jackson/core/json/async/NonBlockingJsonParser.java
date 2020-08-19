/*      */ package com.fasterxml.jackson.core.json.async;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.async.ByteArrayFeeder;
/*      */ import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.VersionUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ 
/*      */ 
/*      */ public class NonBlockingJsonParser
/*      */   extends NonBlockingJsonParserBase
/*      */   implements ByteArrayFeeder
/*      */ {
/*   19 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   21 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   23 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   24 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   25 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*   26 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   27 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */   
/*   30 */   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
/*      */ 
/*      */ 
/*      */   
/*   34 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   45 */   protected byte[] _inputBuffer = NO_BYTES;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _origBufferLen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
/*   68 */     super(ctxt, parserFeatures, sym);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteArrayFeeder getNonBlockingInputFeeder() {
/*   79 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean needMoreInput() {
/*   84 */     return (this._inputPtr >= this._inputEnd && !this._endOfInput);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void feedInput(byte[] buf, int start, int end) throws IOException {
/*   91 */     if (this._inputPtr < this._inputEnd) {
/*   92 */       _reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(this._inputEnd - this._inputPtr));
/*      */     }
/*   94 */     if (end < start) {
/*   95 */       _reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
/*      */     }
/*      */     
/*   98 */     if (this._endOfInput) {
/*   99 */       _reportError("Already closed, can not feed more input");
/*      */     }
/*      */     
/*  102 */     this._currInputProcessed += this._origBufferLen;
/*      */ 
/*      */     
/*  105 */     this._currInputRowStart = start - this._inputEnd - this._currInputRowStart;
/*      */ 
/*      */     
/*  108 */     this._currBufferStart = start;
/*  109 */     this._inputBuffer = buf;
/*  110 */     this._inputPtr = start;
/*  111 */     this._inputEnd = end;
/*  112 */     this._origBufferLen = end - start;
/*      */   }
/*      */ 
/*      */   
/*      */   public void endOfInput() {
/*  117 */     this._endOfInput = true;
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
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  139 */     int avail = this._inputEnd - this._inputPtr;
/*  140 */     if (avail > 0) {
/*  141 */       out.write(this._inputBuffer, this._inputPtr, avail);
/*      */     }
/*  143 */     return avail;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/*  150 */     VersionUtil.throwInternal();
/*  151 */     return ' ';
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
/*      */   public JsonToken nextToken() throws IOException {
/*  165 */     if (this._inputPtr >= this._inputEnd) {
/*  166 */       if (this._closed) {
/*  167 */         return null;
/*      */       }
/*      */       
/*  170 */       if (this._endOfInput) {
/*      */ 
/*      */         
/*  173 */         if (this._currToken == JsonToken.NOT_AVAILABLE) {
/*  174 */           return _finishTokenWithEOF();
/*      */         }
/*  176 */         return _eofAsNextToken();
/*      */       } 
/*  178 */       return JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */     
/*  181 */     if (this._currToken == JsonToken.NOT_AVAILABLE) {
/*  182 */       return _finishToken();
/*      */     }
/*      */ 
/*      */     
/*  186 */     this._numTypesValid = 0;
/*  187 */     this._tokenInputTotal = this._currInputProcessed + this._inputPtr;
/*      */     
/*  189 */     this._binaryValue = null;
/*  190 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     
/*  192 */     switch (this._majorState) {
/*      */       case 0:
/*  194 */         return _startDocument(ch);
/*      */       
/*      */       case 1:
/*  197 */         return _startValue(ch);
/*      */       
/*      */       case 2:
/*  200 */         return _startFieldName(ch);
/*      */       case 3:
/*  202 */         return _startFieldNameAfterComma(ch);
/*      */       
/*      */       case 4:
/*  205 */         return _startValueExpectColon(ch);
/*      */       
/*      */       case 5:
/*  208 */         return _startValue(ch);
/*      */       
/*      */       case 6:
/*  211 */         return _startValueExpectComma(ch);
/*      */     } 
/*      */ 
/*      */     
/*  215 */     VersionUtil.throwInternal();
/*  216 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonToken _finishToken() throws IOException {
/*      */     int c;
/*  227 */     switch (this._minorState) {
/*      */       case 1:
/*  229 */         return _finishBOM(this._pending32);
/*      */       case 4:
/*  231 */         return _startFieldName(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 5:
/*  233 */         return _startFieldNameAfterComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */ 
/*      */       
/*      */       case 7:
/*  237 */         return _parseEscapedName(this._quadLength, this._pending32, this._pendingBytes);
/*      */       case 8:
/*  239 */         return _finishFieldWithEscape();
/*      */       case 9:
/*  241 */         return _finishAposName(this._quadLength, this._pending32, this._pendingBytes);
/*      */       case 10:
/*  243 */         return _finishUnquotedName(this._quadLength, this._pending32, this._pendingBytes);
/*      */ 
/*      */ 
/*      */       
/*      */       case 12:
/*  248 */         return _startValue(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 15:
/*  250 */         return _startValueAfterComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 13:
/*  252 */         return _startValueExpectComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 14:
/*  254 */         return _startValueExpectColon(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       
/*      */       case 16:
/*  257 */         return _finishKeywordToken("null", this._pending32, JsonToken.VALUE_NULL);
/*      */       case 17:
/*  259 */         return _finishKeywordToken("true", this._pending32, JsonToken.VALUE_TRUE);
/*      */       case 18:
/*  261 */         return _finishKeywordToken("false", this._pending32, JsonToken.VALUE_FALSE);
/*      */       case 19:
/*  263 */         return _finishNonStdToken(this._nonStdTokenType, this._pending32);
/*      */       
/*      */       case 23:
/*  266 */         return _finishNumberMinus(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 24:
/*  268 */         return _finishNumberLeadingZeroes();
/*      */       case 25:
/*  270 */         return _finishNumberLeadingNegZeroes();
/*      */       case 26:
/*  272 */         return _finishNumberIntegralPart(this._textBuffer.getBufferWithoutReset(), this._textBuffer
/*  273 */             .getCurrentSegmentSize());
/*      */       case 30:
/*  275 */         return _finishFloatFraction();
/*      */       case 31:
/*  277 */         return _finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 32:
/*  279 */         return _finishFloatExponent(false, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       
/*      */       case 40:
/*  282 */         return _finishRegularString();
/*      */       case 42:
/*  284 */         this._textBuffer.append((char)_decodeUTF8_2(this._pending32, this._inputBuffer[this._inputPtr++]));
/*  285 */         if (this._minorStateAfterSplit == 45) {
/*  286 */           return _finishAposString();
/*      */         }
/*  288 */         return _finishRegularString();
/*      */       case 43:
/*  290 */         if (!_decodeSplitUTF8_3(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
/*  291 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  293 */         if (this._minorStateAfterSplit == 45) {
/*  294 */           return _finishAposString();
/*      */         }
/*  296 */         return _finishRegularString();
/*      */       case 44:
/*  298 */         if (!_decodeSplitUTF8_4(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
/*  299 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  301 */         if (this._minorStateAfterSplit == 45) {
/*  302 */           return _finishAposString();
/*      */         }
/*  304 */         return _finishRegularString();
/*      */ 
/*      */       
/*      */       case 41:
/*  308 */         c = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
/*  309 */         if (c < 0) {
/*  310 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  312 */         this._textBuffer.append((char)c);
/*      */         
/*  314 */         if (this._minorStateAfterSplit == 45) {
/*  315 */           return _finishAposString();
/*      */         }
/*  317 */         return _finishRegularString();
/*      */       
/*      */       case 45:
/*  320 */         return _finishAposString();
/*      */       
/*      */       case 50:
/*  323 */         return _finishErrorToken();
/*      */ 
/*      */ 
/*      */       
/*      */       case 51:
/*  328 */         return _startSlashComment(this._pending32);
/*      */       case 52:
/*  330 */         return _finishCComment(this._pending32, true);
/*      */       case 53:
/*  332 */         return _finishCComment(this._pending32, false);
/*      */       case 54:
/*  334 */         return _finishCppComment(this._pending32);
/*      */       case 55:
/*  336 */         return _finishHashComment(this._pending32);
/*      */     } 
/*  338 */     VersionUtil.throwInternal();
/*  339 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonToken _finishTokenWithEOF() throws IOException {
/*      */     int len;
/*  351 */     JsonToken t = this._currToken;
/*  352 */     switch (this._minorState) {
/*      */       case 3:
/*  354 */         return _eofAsNextToken();
/*      */       case 12:
/*  356 */         return _eofAsNextToken();
/*      */ 
/*      */       
/*      */       case 16:
/*  360 */         return _finishKeywordTokenWithEOF("null", this._pending32, JsonToken.VALUE_NULL);
/*      */       case 17:
/*  362 */         return _finishKeywordTokenWithEOF("true", this._pending32, JsonToken.VALUE_TRUE);
/*      */       case 18:
/*  364 */         return _finishKeywordTokenWithEOF("false", this._pending32, JsonToken.VALUE_FALSE);
/*      */       case 19:
/*  366 */         return _finishNonStdTokenWithEOF(this._nonStdTokenType, this._pending32);
/*      */       case 50:
/*  368 */         return _finishErrorTokenWithEOF();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 24:
/*      */       case 25:
/*  375 */         return _valueCompleteInt(0, "0");
/*      */ 
/*      */       
/*      */       case 26:
/*  379 */         len = this._textBuffer.getCurrentSegmentSize();
/*  380 */         if (this._numberNegative) {
/*  381 */           len--;
/*      */         }
/*  383 */         this._intLength = len;
/*      */         
/*  385 */         return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */       
/*      */       case 30:
/*  388 */         this._expLength = 0;
/*      */       
/*      */       case 32:
/*  391 */         return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */       
/*      */       case 31:
/*  394 */         _reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 52:
/*      */       case 53:
/*  402 */         _reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
/*      */ 
/*      */       
/*      */       case 54:
/*      */       case 55:
/*  407 */         return _eofAsNextToken();
/*      */     } 
/*      */ 
/*      */     
/*  411 */     _reportInvalidEOF(": was expecting rest of token (internal state: " + this._minorState + ")", this._currToken);
/*  412 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startDocument(int ch) throws IOException {
/*  423 */     ch &= 0xFF;
/*      */ 
/*      */     
/*  426 */     if (ch == 239 && this._minorState != 1) {
/*  427 */       return _finishBOM(1);
/*      */     }
/*      */ 
/*      */     
/*  431 */     while (ch <= 32) {
/*  432 */       if (ch != 32) {
/*  433 */         if (ch == 10) {
/*  434 */           this._currInputRow++;
/*  435 */           this._currInputRowStart = this._inputPtr;
/*  436 */         } else if (ch == 13) {
/*  437 */           this._currInputRowAlt++;
/*  438 */           this._currInputRowStart = this._inputPtr;
/*  439 */         } else if (ch != 9) {
/*  440 */           _throwInvalidSpace(ch);
/*      */         } 
/*      */       }
/*  443 */       if (this._inputPtr >= this._inputEnd) {
/*  444 */         this._minorState = 3;
/*  445 */         if (this._closed) {
/*  446 */           return null;
/*      */         }
/*      */         
/*  449 */         if (this._endOfInput) {
/*  450 */           return _eofAsNextToken();
/*      */         }
/*  452 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/*  454 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*  456 */     return _startValue(ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishBOM(int bytesHandled) throws IOException {
/*  465 */     while (this._inputPtr < this._inputEnd) {
/*  466 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  467 */       switch (bytesHandled) {
/*      */ 
/*      */         
/*      */         case 3:
/*  471 */           this._currInputProcessed -= 3L;
/*  472 */           return _startDocument(ch);
/*      */         case 2:
/*  474 */           if (ch != 191) {
/*  475 */             _reportError("Unexpected byte 0x%02x following 0xEF 0xBB; should get 0xBF as third byte of UTF-8 BOM", Integer.valueOf(ch));
/*      */           }
/*      */           break;
/*      */         case 1:
/*  479 */           if (ch != 187) {
/*  480 */             _reportError("Unexpected byte 0x%02x following 0xEF; should get 0xBB as second byte UTF-8 BOM", Integer.valueOf(ch));
/*      */           }
/*      */           break;
/*      */       } 
/*  484 */       bytesHandled++;
/*      */     } 
/*  486 */     this._pending32 = bytesHandled;
/*  487 */     this._minorState = 1;
/*  488 */     return this._currToken = JsonToken.NOT_AVAILABLE;
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
/*      */   private final JsonToken _startFieldName(int ch) throws IOException {
/*  504 */     if (ch <= 32) {
/*  505 */       ch = _skipWS(ch);
/*  506 */       if (ch <= 0) {
/*  507 */         this._minorState = 4;
/*  508 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  511 */     _updateTokenLocation();
/*  512 */     if (ch != 34) {
/*  513 */       if (ch == 125) {
/*  514 */         return _closeObjectScope();
/*      */       }
/*  516 */       return _handleOddName(ch);
/*      */     } 
/*      */     
/*  519 */     if (this._inputPtr + 13 <= this._inputEnd) {
/*  520 */       String n = _fastParseName();
/*  521 */       if (n != null) {
/*  522 */         return _fieldComplete(n);
/*      */       }
/*      */     } 
/*  525 */     return _parseEscapedName(0, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startFieldNameAfterComma(int ch) throws IOException {
/*  531 */     if (ch <= 32) {
/*  532 */       ch = _skipWS(ch);
/*  533 */       if (ch <= 0) {
/*  534 */         this._minorState = 5;
/*  535 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  538 */     if (ch != 44) {
/*  539 */       if (ch == 125) {
/*  540 */         return _closeObjectScope();
/*      */       }
/*  542 */       if (ch == 35) {
/*  543 */         return _finishHashComment(5);
/*      */       }
/*  545 */       if (ch == 47) {
/*  546 */         return _startSlashComment(5);
/*      */       }
/*  548 */       _reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     } 
/*  550 */     int ptr = this._inputPtr;
/*  551 */     if (ptr >= this._inputEnd) {
/*  552 */       this._minorState = 4;
/*  553 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  555 */     ch = this._inputBuffer[ptr];
/*  556 */     this._inputPtr = ptr + 1;
/*  557 */     if (ch <= 32) {
/*  558 */       ch = _skipWS(ch);
/*  559 */       if (ch <= 0) {
/*  560 */         this._minorState = 4;
/*  561 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  564 */     _updateTokenLocation();
/*  565 */     if (ch != 34) {
/*  566 */       if (ch == 125 && (
/*  567 */         this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  568 */         return _closeObjectScope();
/*      */       }
/*      */       
/*  571 */       return _handleOddName(ch);
/*      */     } 
/*      */     
/*  574 */     if (this._inputPtr + 13 <= this._inputEnd) {
/*  575 */       String n = _fastParseName();
/*  576 */       if (n != null) {
/*  577 */         return _fieldComplete(n);
/*      */       }
/*      */     } 
/*  580 */     return _parseEscapedName(0, 0, 0);
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
/*      */   private final JsonToken _startValue(int ch) throws IOException {
/*  597 */     if (ch <= 32) {
/*  598 */       ch = _skipWS(ch);
/*  599 */       if (ch <= 0) {
/*  600 */         this._minorState = 12;
/*  601 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  604 */     _updateTokenLocation();
/*      */     
/*  606 */     this._parsingContext.expectComma();
/*      */     
/*  608 */     if (ch == 34) {
/*  609 */       return _startString();
/*      */     }
/*  611 */     switch (ch) {
/*      */       case 35:
/*  613 */         return _finishHashComment(12);
/*      */       case 45:
/*  615 */         return _startNegativeNumber();
/*      */       case 47:
/*  617 */         return _startSlashComment(12);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  623 */         return _startNumberLeadingZero();
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  633 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  635 */         return _startFalseToken();
/*      */       case 110:
/*  637 */         return _startNullToken();
/*      */       case 116:
/*  639 */         return _startTrueToken();
/*      */       case 91:
/*  641 */         return _startArrayScope();
/*      */       case 93:
/*  643 */         return _closeArrayScope();
/*      */       case 123:
/*  645 */         return _startObjectScope();
/*      */       case 125:
/*  647 */         return _closeObjectScope();
/*      */     } 
/*      */     
/*  650 */     return _startUnexpectedValue(false, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueExpectComma(int ch) throws IOException {
/*  660 */     if (ch <= 32) {
/*  661 */       ch = _skipWS(ch);
/*  662 */       if (ch <= 0) {
/*  663 */         this._minorState = 13;
/*  664 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  667 */     if (ch != 44) {
/*  668 */       if (ch == 93) {
/*  669 */         return _closeArrayScope();
/*      */       }
/*  671 */       if (ch == 125) {
/*  672 */         return _closeObjectScope();
/*      */       }
/*  674 */       if (ch == 47) {
/*  675 */         return _startSlashComment(13);
/*      */       }
/*  677 */       if (ch == 35) {
/*  678 */         return _finishHashComment(13);
/*      */       }
/*  680 */       _reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     } 
/*      */ 
/*      */     
/*  684 */     this._parsingContext.expectComma();
/*      */     
/*  686 */     int ptr = this._inputPtr;
/*  687 */     if (ptr >= this._inputEnd) {
/*  688 */       this._minorState = 15;
/*  689 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  691 */     ch = this._inputBuffer[ptr];
/*  692 */     this._inputPtr = ptr + 1;
/*  693 */     if (ch <= 32) {
/*  694 */       ch = _skipWS(ch);
/*  695 */       if (ch <= 0) {
/*  696 */         this._minorState = 15;
/*  697 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  700 */     _updateTokenLocation();
/*  701 */     if (ch == 34) {
/*  702 */       return _startString();
/*      */     }
/*  704 */     switch (ch) {
/*      */       case 35:
/*  706 */         return _finishHashComment(15);
/*      */       case 45:
/*  708 */         return _startNegativeNumber();
/*      */       case 47:
/*  710 */         return _startSlashComment(15);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  716 */         return _startNumberLeadingZero();
/*      */       case 49: case 50: case 51: case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  723 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  725 */         return _startFalseToken();
/*      */       case 110:
/*  727 */         return _startNullToken();
/*      */       case 116:
/*  729 */         return _startTrueToken();
/*      */       case 91:
/*  731 */         return _startArrayScope();
/*      */       
/*      */       case 93:
/*  734 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  735 */           return _closeArrayScope();
/*      */         }
/*      */         break;
/*      */       case 123:
/*  739 */         return _startObjectScope();
/*      */       
/*      */       case 125:
/*  742 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  743 */           return _closeObjectScope();
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  748 */     return _startUnexpectedValue(true, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueExpectColon(int ch) throws IOException {
/*  759 */     if (ch <= 32) {
/*  760 */       ch = _skipWS(ch);
/*  761 */       if (ch <= 0) {
/*  762 */         this._minorState = 14;
/*  763 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  766 */     if (ch != 58) {
/*  767 */       if (ch == 47) {
/*  768 */         return _startSlashComment(14);
/*      */       }
/*  770 */       if (ch == 35) {
/*  771 */         return _finishHashComment(14);
/*      */       }
/*      */       
/*  774 */       _reportUnexpectedChar(ch, "was expecting a colon to separate field name and value");
/*      */     } 
/*  776 */     int ptr = this._inputPtr;
/*  777 */     if (ptr >= this._inputEnd) {
/*  778 */       this._minorState = 12;
/*  779 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  781 */     ch = this._inputBuffer[ptr];
/*  782 */     this._inputPtr = ptr + 1;
/*  783 */     if (ch <= 32) {
/*  784 */       ch = _skipWS(ch);
/*  785 */       if (ch <= 0) {
/*  786 */         this._minorState = 12;
/*  787 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  790 */     _updateTokenLocation();
/*  791 */     if (ch == 34) {
/*  792 */       return _startString();
/*      */     }
/*  794 */     switch (ch) {
/*      */       case 35:
/*  796 */         return _finishHashComment(12);
/*      */       case 45:
/*  798 */         return _startNegativeNumber();
/*      */       case 47:
/*  800 */         return _startSlashComment(12);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  806 */         return _startNumberLeadingZero();
/*      */       case 49: case 50: case 51: case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  813 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  815 */         return _startFalseToken();
/*      */       case 110:
/*  817 */         return _startNullToken();
/*      */       case 116:
/*  819 */         return _startTrueToken();
/*      */       case 91:
/*  821 */         return _startArrayScope();
/*      */       case 123:
/*  823 */         return _startObjectScope();
/*      */     } 
/*      */     
/*  826 */     return _startUnexpectedValue(false, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueAfterComma(int ch) throws IOException {
/*  834 */     if (ch <= 32) {
/*  835 */       ch = _skipWS(ch);
/*  836 */       if (ch <= 0) {
/*  837 */         this._minorState = 15;
/*  838 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  841 */     _updateTokenLocation();
/*  842 */     if (ch == 34) {
/*  843 */       return _startString();
/*      */     }
/*  845 */     switch (ch) {
/*      */       case 35:
/*  847 */         return _finishHashComment(15);
/*      */       case 45:
/*  849 */         return _startNegativeNumber();
/*      */       case 47:
/*  851 */         return _startSlashComment(15);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  857 */         return _startNumberLeadingZero();
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  867 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  869 */         return _startFalseToken();
/*      */       case 110:
/*  871 */         return _startNullToken();
/*      */       case 116:
/*  873 */         return _startTrueToken();
/*      */       case 91:
/*  875 */         return _startArrayScope();
/*      */       
/*      */       case 93:
/*  878 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  879 */           return _closeArrayScope();
/*      */         }
/*      */         break;
/*      */       case 123:
/*  883 */         return _startObjectScope();
/*      */       
/*      */       case 125:
/*  886 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  887 */           return _closeObjectScope();
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  892 */     return _startUnexpectedValue(true, ch);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startUnexpectedValue(boolean leadingComma, int ch) throws IOException {
/*  897 */     switch (ch) {
/*      */       case 93:
/*  899 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/*  907 */         if ((this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/*  908 */           this._inputPtr--;
/*  909 */           return _valueComplete(JsonToken.VALUE_NULL);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 39:
/*  917 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/*  918 */           return _startAposString();
/*      */         }
/*      */         break;
/*      */       case 43:
/*  922 */         return _finishNonStdToken(2, 1);
/*      */       case 78:
/*  924 */         return _finishNonStdToken(0, 1);
/*      */       case 73:
/*  926 */         return _finishNonStdToken(1, 1);
/*      */     } 
/*      */     
/*  929 */     _reportUnexpectedChar(ch, "expected a valid value " + _validJsonValueList());
/*  930 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWS(int ch) throws IOException {
/*      */     while (true) {
/*  942 */       if (ch != 32) {
/*  943 */         if (ch == 10) {
/*  944 */           this._currInputRow++;
/*  945 */           this._currInputRowStart = this._inputPtr;
/*  946 */         } else if (ch == 13) {
/*  947 */           this._currInputRowAlt++;
/*  948 */           this._currInputRowStart = this._inputPtr;
/*  949 */         } else if (ch != 9) {
/*  950 */           _throwInvalidSpace(ch);
/*      */         } 
/*      */       }
/*  953 */       if (this._inputPtr >= this._inputEnd) {
/*  954 */         this._currToken = JsonToken.NOT_AVAILABLE;
/*  955 */         return 0;
/*      */       } 
/*  957 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  958 */       if (ch > 32)
/*  959 */         return ch; 
/*      */     } 
/*      */   }
/*      */   
/*      */   private final JsonToken _startSlashComment(int fromMinorState) throws IOException {
/*  964 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/*  965 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */ 
/*      */     
/*  969 */     if (this._inputPtr >= this._inputEnd) {
/*  970 */       this._pending32 = fromMinorState;
/*  971 */       this._minorState = 51;
/*  972 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  974 */     int ch = this._inputBuffer[this._inputPtr++];
/*  975 */     if (ch == 42) {
/*  976 */       return _finishCComment(fromMinorState, false);
/*      */     }
/*  978 */     if (ch == 47) {
/*  979 */       return _finishCppComment(fromMinorState);
/*      */     }
/*  981 */     _reportUnexpectedChar(ch & 0xFF, "was expecting either '*' or '/' for a comment");
/*  982 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishHashComment(int fromMinorState) throws IOException {
/*  988 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/*  989 */       _reportUnexpectedChar(35, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_YAML_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     while (true) {
/*  992 */       if (this._inputPtr >= this._inputEnd) {
/*  993 */         this._minorState = 55;
/*  994 */         this._pending32 = fromMinorState;
/*  995 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/*  997 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  998 */       if (ch < 32) {
/*  999 */         if (ch == 10) {
/* 1000 */           this._currInputRow++;
/* 1001 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1003 */         if (ch == 13) {
/* 1004 */           this._currInputRowAlt++;
/* 1005 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1007 */         if (ch != 9) {
/* 1008 */           _throwInvalidSpace(ch);
/*      */         }
/*      */       } 
/*      */     } 
/* 1012 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _finishCppComment(int fromMinorState) throws IOException {
/*      */     while (true) {
/* 1018 */       if (this._inputPtr >= this._inputEnd) {
/* 1019 */         this._minorState = 54;
/* 1020 */         this._pending32 = fromMinorState;
/* 1021 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1023 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1024 */       if (ch < 32) {
/* 1025 */         if (ch == 10) {
/* 1026 */           this._currInputRow++;
/* 1027 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1029 */         if (ch == 13) {
/* 1030 */           this._currInputRowAlt++;
/* 1031 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1033 */         if (ch != 9) {
/* 1034 */           _throwInvalidSpace(ch);
/*      */         }
/*      */       } 
/*      */     } 
/* 1038 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws IOException {
/*      */     while (true) {
/* 1044 */       if (this._inputPtr >= this._inputEnd) {
/* 1045 */         this._minorState = gotStar ? 52 : 53;
/* 1046 */         this._pending32 = fromMinorState;
/* 1047 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1049 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1050 */       if (ch < 32)
/* 1051 */       { if (ch == 10) {
/* 1052 */           this._currInputRow++;
/* 1053 */           this._currInputRowStart = this._inputPtr;
/* 1054 */         } else if (ch == 13) {
/* 1055 */           this._currInputRowAlt++;
/* 1056 */           this._currInputRowStart = this._inputPtr;
/* 1057 */         } else if (ch != 9) {
/* 1058 */           _throwInvalidSpace(ch);
/*      */         }  }
/* 1060 */       else { if (ch == 42) {
/* 1061 */           gotStar = true; continue;
/*      */         } 
/* 1063 */         if (ch == 47 && 
/* 1064 */           gotStar) {
/*      */           break;
/*      */         } }
/*      */       
/* 1068 */       gotStar = false;
/*      */     } 
/* 1070 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startAfterComment(int fromMinorState) throws IOException {
/* 1076 */     if (this._inputPtr >= this._inputEnd) {
/* 1077 */       this._minorState = fromMinorState;
/* 1078 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1080 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1081 */     switch (fromMinorState) {
/*      */       case 4:
/* 1083 */         return _startFieldName(ch);
/*      */       case 5:
/* 1085 */         return _startFieldNameAfterComma(ch);
/*      */       case 12:
/* 1087 */         return _startValue(ch);
/*      */       case 13:
/* 1089 */         return _startValueExpectComma(ch);
/*      */       case 14:
/* 1091 */         return _startValueExpectColon(ch);
/*      */       case 15:
/* 1093 */         return _startValueAfterComma(ch);
/*      */     } 
/*      */     
/* 1096 */     VersionUtil.throwInternal();
/* 1097 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _startFalseToken() throws IOException {
/* 1108 */     int ptr = this._inputPtr;
/* 1109 */     if (ptr + 4 < this._inputEnd) {
/* 1110 */       byte[] buf = this._inputBuffer;
/* 1111 */       if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
/*      */ 
/*      */ 
/*      */         
/* 1115 */         int ch = buf[ptr] & 0xFF;
/* 1116 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1117 */           this._inputPtr = ptr;
/* 1118 */           return _valueComplete(JsonToken.VALUE_FALSE);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1122 */     this._minorState = 18;
/* 1123 */     return _finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startTrueToken() throws IOException {
/* 1128 */     int ptr = this._inputPtr;
/* 1129 */     if (ptr + 3 < this._inputEnd) {
/* 1130 */       byte[] buf = this._inputBuffer;
/* 1131 */       if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
/*      */ 
/*      */         
/* 1134 */         int ch = buf[ptr] & 0xFF;
/* 1135 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1136 */           this._inputPtr = ptr;
/* 1137 */           return _valueComplete(JsonToken.VALUE_TRUE);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1141 */     this._minorState = 17;
/* 1142 */     return _finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNullToken() throws IOException {
/* 1147 */     int ptr = this._inputPtr;
/* 1148 */     if (ptr + 3 < this._inputEnd) {
/* 1149 */       byte[] buf = this._inputBuffer;
/* 1150 */       if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
/*      */ 
/*      */         
/* 1153 */         int ch = buf[ptr] & 0xFF;
/* 1154 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1155 */           this._inputPtr = ptr;
/* 1156 */           return _valueComplete(JsonToken.VALUE_NULL);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1160 */     this._minorState = 16;
/* 1161 */     return _finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishKeywordToken(String expToken, int matched, JsonToken result) throws IOException {
/* 1167 */     int end = expToken.length();
/*      */     
/*      */     while (true) {
/* 1170 */       if (this._inputPtr >= this._inputEnd) {
/* 1171 */         this._pending32 = matched;
/* 1172 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1174 */       int ch = this._inputBuffer[this._inputPtr];
/* 1175 */       if (matched == end) {
/* 1176 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1177 */           return _valueComplete(result);
/*      */         }
/*      */         break;
/*      */       } 
/* 1181 */       if (ch != expToken.charAt(matched)) {
/*      */         break;
/*      */       }
/* 1184 */       matched++;
/* 1185 */       this._inputPtr++;
/*      */     } 
/* 1187 */     this._minorState = 50;
/* 1188 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1189 */     return _finishErrorToken();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched, JsonToken result) throws IOException {
/* 1195 */     if (matched == expToken.length()) {
/* 1196 */       return this._currToken = result;
/*      */     }
/* 1198 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1199 */     return _finishErrorTokenWithEOF();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNonStdToken(int type, int matched) throws IOException {
/* 1204 */     String expToken = _nonStdToken(type);
/* 1205 */     int end = expToken.length();
/*      */     
/*      */     while (true) {
/* 1208 */       if (this._inputPtr >= this._inputEnd) {
/* 1209 */         this._nonStdTokenType = type;
/* 1210 */         this._pending32 = matched;
/* 1211 */         this._minorState = 19;
/* 1212 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1214 */       int ch = this._inputBuffer[this._inputPtr];
/* 1215 */       if (matched == end) {
/* 1216 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1217 */           return _valueNonStdNumberComplete(type);
/*      */         }
/*      */         break;
/*      */       } 
/* 1221 */       if (ch != expToken.charAt(matched)) {
/*      */         break;
/*      */       }
/* 1224 */       matched++;
/* 1225 */       this._inputPtr++;
/*      */     } 
/* 1227 */     this._minorState = 50;
/* 1228 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1229 */     return _finishErrorToken();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNonStdTokenWithEOF(int type, int matched) throws IOException {
/* 1234 */     String expToken = _nonStdToken(type);
/* 1235 */     if (matched == expToken.length()) {
/* 1236 */       return _valueNonStdNumberComplete(type);
/*      */     }
/* 1238 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1239 */     return _finishErrorTokenWithEOF();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishErrorToken() throws IOException {
/* 1244 */     while (this._inputPtr < this._inputEnd) {
/* 1245 */       int i = this._inputBuffer[this._inputPtr++];
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1250 */       char ch = (char)i;
/* 1251 */       if (Character.isJavaIdentifierPart(ch)) {
/*      */ 
/*      */         
/* 1254 */         this._textBuffer.append(ch);
/* 1255 */         if (this._textBuffer.size() < 256) {
/*      */           continue;
/*      */         }
/*      */       } 
/* 1259 */       return _reportErrorToken(this._textBuffer.contentsAsString());
/*      */     } 
/* 1261 */     return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishErrorTokenWithEOF() throws IOException {
/* 1266 */     return _reportErrorToken(this._textBuffer.contentsAsString());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _reportErrorToken(String actualToken) throws IOException {
/* 1272 */     _reportError("Unrecognized token '%s': was expecting %s", this._textBuffer.contentsAsString(), 
/* 1273 */         _validJsonTokenList());
/* 1274 */     return JsonToken.NOT_AVAILABLE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _startPositiveNumber(int ch) throws IOException {
/* 1285 */     this._numberNegative = false;
/* 1286 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1287 */     outBuf[0] = (char)ch;
/*      */     
/* 1289 */     if (this._inputPtr >= this._inputEnd) {
/* 1290 */       this._minorState = 26;
/* 1291 */       this._textBuffer.setCurrentLength(1);
/* 1292 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */     
/* 1295 */     int outPtr = 1;
/*      */     
/* 1297 */     ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     while (true) {
/* 1299 */       if (ch < 48) {
/* 1300 */         if (ch == 46) {
/* 1301 */           this._intLength = outPtr;
/* 1302 */           this._inputPtr++;
/* 1303 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1307 */       if (ch > 57) {
/* 1308 */         if (ch == 101 || ch == 69) {
/* 1309 */           this._intLength = outPtr;
/* 1310 */           this._inputPtr++;
/* 1311 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1315 */       if (outPtr >= outBuf.length)
/*      */       {
/*      */         
/* 1318 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1320 */       outBuf[outPtr++] = (char)ch;
/* 1321 */       if (++this._inputPtr >= this._inputEnd) {
/* 1322 */         this._minorState = 26;
/* 1323 */         this._textBuffer.setCurrentLength(outPtr);
/* 1324 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1326 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     } 
/* 1328 */     this._intLength = outPtr;
/* 1329 */     this._textBuffer.setCurrentLength(outPtr);
/* 1330 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNegativeNumber() throws IOException {
/* 1335 */     this._numberNegative = true;
/* 1336 */     if (this._inputPtr >= this._inputEnd) {
/* 1337 */       this._minorState = 23;
/* 1338 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1340 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1341 */     if (ch <= 48) {
/* 1342 */       if (ch == 48) {
/* 1343 */         return _finishNumberLeadingNegZeroes();
/*      */       }
/*      */       
/* 1346 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1347 */     } else if (ch > 57) {
/* 1348 */       if (ch == 73) {
/* 1349 */         return _finishNonStdToken(3, 2);
/*      */       }
/* 1351 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*      */     } 
/* 1353 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1354 */     outBuf[0] = '-';
/* 1355 */     outBuf[1] = (char)ch;
/* 1356 */     if (this._inputPtr >= this._inputEnd) {
/* 1357 */       this._minorState = 26;
/* 1358 */       this._textBuffer.setCurrentLength(2);
/* 1359 */       this._intLength = 1;
/* 1360 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1362 */     ch = this._inputBuffer[this._inputPtr];
/* 1363 */     int outPtr = 2;
/*      */     
/*      */     while (true) {
/* 1366 */       if (ch < 48) {
/* 1367 */         if (ch == 46) {
/* 1368 */           this._intLength = outPtr - 1;
/* 1369 */           this._inputPtr++;
/* 1370 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1374 */       if (ch > 57) {
/* 1375 */         if (ch == 101 || ch == 69) {
/* 1376 */           this._intLength = outPtr - 1;
/* 1377 */           this._inputPtr++;
/* 1378 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1382 */       if (outPtr >= outBuf.length)
/*      */       {
/* 1384 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1386 */       outBuf[outPtr++] = (char)ch;
/* 1387 */       if (++this._inputPtr >= this._inputEnd) {
/* 1388 */         this._minorState = 26;
/* 1389 */         this._textBuffer.setCurrentLength(outPtr);
/* 1390 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1392 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     } 
/* 1394 */     this._intLength = outPtr - 1;
/* 1395 */     this._textBuffer.setCurrentLength(outPtr);
/* 1396 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNumberLeadingZero() throws IOException {
/* 1401 */     int ptr = this._inputPtr;
/* 1402 */     if (ptr >= this._inputEnd) {
/* 1403 */       this._minorState = 24;
/* 1404 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1411 */     int ch = this._inputBuffer[ptr++] & 0xFF;
/*      */     
/* 1413 */     if (ch < 48) {
/* 1414 */       if (ch == 46) {
/* 1415 */         this._inputPtr = ptr;
/* 1416 */         this._intLength = 1;
/* 1417 */         char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1418 */         outBuf[0] = '0';
/* 1419 */         return _startFloat(outBuf, 1, ch);
/*      */       } 
/* 1421 */     } else if (ch > 57) {
/* 1422 */       if (ch == 101 || ch == 69) {
/* 1423 */         this._inputPtr = ptr;
/* 1424 */         this._intLength = 1;
/* 1425 */         char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1426 */         outBuf[0] = '0';
/* 1427 */         return _startFloat(outBuf, 1, ch);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1432 */       if (ch != 93 && ch != 125) {
/* 1433 */         reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1438 */       return _finishNumberLeadingZeroes();
/*      */     } 
/*      */     
/* 1441 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberMinus(int ch) throws IOException {
/* 1446 */     if (ch <= 48) {
/* 1447 */       if (ch == 48) {
/* 1448 */         return _finishNumberLeadingNegZeroes();
/*      */       }
/* 1450 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1451 */     } else if (ch > 57) {
/* 1452 */       if (ch == 73) {
/* 1453 */         return _finishNonStdToken(3, 2);
/*      */       }
/* 1455 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*      */     } 
/* 1457 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1458 */     outBuf[0] = '-';
/* 1459 */     outBuf[1] = (char)ch;
/* 1460 */     this._intLength = 1;
/* 1461 */     return _finishNumberIntegralPart(outBuf, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberLeadingZeroes() throws IOException {
/*      */     while (true) {
/* 1469 */       if (this._inputPtr >= this._inputEnd) {
/* 1470 */         this._minorState = 24;
/* 1471 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1473 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1474 */       if (ch < 48) {
/* 1475 */         if (ch == 46) {
/* 1476 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1477 */           arrayOfChar[0] = '0';
/* 1478 */           this._intLength = 1;
/* 1479 */           return _startFloat(arrayOfChar, 1, ch);
/*      */         }  break;
/* 1481 */       }  if (ch > 57) {
/* 1482 */         if (ch == 101 || ch == 69) {
/* 1483 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1484 */           arrayOfChar[0] = '0';
/* 1485 */           this._intLength = 1;
/* 1486 */           return _startFloat(arrayOfChar, 1, ch);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1491 */         if (ch != 93 && ch != 125) {
/* 1492 */           reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */         }
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 1498 */       if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1499 */         reportInvalidNumber("Leading zeroes not allowed");
/*      */       }
/* 1501 */       if (ch == 48) {
/*      */         continue;
/*      */       }
/* 1504 */       char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */       
/* 1506 */       outBuf[0] = (char)ch;
/* 1507 */       this._intLength = 1;
/* 1508 */       return _finishNumberIntegralPart(outBuf, 1);
/*      */     } 
/* 1510 */     this._inputPtr--;
/* 1511 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberLeadingNegZeroes() throws IOException {
/*      */     while (true) {
/* 1520 */       if (this._inputPtr >= this._inputEnd) {
/* 1521 */         this._minorState = 25;
/* 1522 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1524 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1525 */       if (ch < 48) {
/* 1526 */         if (ch == 46) {
/* 1527 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1528 */           arrayOfChar[0] = '-';
/* 1529 */           arrayOfChar[1] = '0';
/* 1530 */           this._intLength = 1;
/* 1531 */           return _startFloat(arrayOfChar, 2, ch);
/*      */         }  break;
/* 1533 */       }  if (ch > 57) {
/* 1534 */         if (ch == 101 || ch == 69) {
/* 1535 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1536 */           arrayOfChar[0] = '-';
/* 1537 */           arrayOfChar[1] = '0';
/* 1538 */           this._intLength = 1;
/* 1539 */           return _startFloat(arrayOfChar, 2, ch);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1544 */         if (ch != 93 && ch != 125) {
/* 1545 */           reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */         }
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 1551 */       if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1552 */         reportInvalidNumber("Leading zeroes not allowed");
/*      */       }
/* 1554 */       if (ch == 48) {
/*      */         continue;
/*      */       }
/* 1557 */       char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */       
/* 1559 */       outBuf[0] = '-';
/* 1560 */       outBuf[1] = (char)ch;
/* 1561 */       this._intLength = 1;
/* 1562 */       return _finishNumberIntegralPart(outBuf, 2);
/*      */     } 
/* 1564 */     this._inputPtr--;
/* 1565 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr) throws IOException {
/* 1571 */     int negMod = this._numberNegative ? -1 : 0;
/*      */     
/*      */     while (true) {
/* 1574 */       if (this._inputPtr >= this._inputEnd) {
/* 1575 */         this._minorState = 26;
/* 1576 */         this._textBuffer.setCurrentLength(outPtr);
/* 1577 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1579 */       int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1580 */       if (ch < 48) {
/* 1581 */         if (ch == 46) {
/* 1582 */           this._intLength = outPtr + negMod;
/* 1583 */           this._inputPtr++;
/* 1584 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1588 */       if (ch > 57) {
/* 1589 */         if (ch == 101 || ch == 69) {
/* 1590 */           this._intLength = outPtr + negMod;
/* 1591 */           this._inputPtr++;
/* 1592 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1596 */       this._inputPtr++;
/* 1597 */       if (outPtr >= outBuf.length)
/*      */       {
/*      */         
/* 1600 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1602 */       outBuf[outPtr++] = (char)ch;
/*      */     } 
/* 1604 */     this._intLength = outPtr + negMod;
/* 1605 */     this._textBuffer.setCurrentLength(outPtr);
/* 1606 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch) throws IOException {
/* 1611 */     int fractLen = 0;
/* 1612 */     if (ch == 46) {
/* 1613 */       if (outPtr >= outBuf.length) {
/* 1614 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1616 */       outBuf[outPtr++] = '.';
/*      */       while (true) {
/* 1618 */         if (this._inputPtr >= this._inputEnd) {
/* 1619 */           this._textBuffer.setCurrentLength(outPtr);
/* 1620 */           this._minorState = 30;
/* 1621 */           this._fractLength = fractLen;
/* 1622 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1624 */         ch = this._inputBuffer[this._inputPtr++];
/* 1625 */         if (ch < 48 || ch > 57) {
/* 1626 */           ch &= 0xFF;
/*      */           
/* 1628 */           if (fractLen == 0) {
/* 1629 */             reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */           }
/*      */           break;
/*      */         } 
/* 1633 */         if (outPtr >= outBuf.length) {
/* 1634 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1636 */         outBuf[outPtr++] = (char)ch;
/* 1637 */         fractLen++;
/*      */       } 
/*      */     } 
/* 1640 */     this._fractLength = fractLen;
/* 1641 */     int expLen = 0;
/* 1642 */     if (ch == 101 || ch == 69) {
/* 1643 */       if (outPtr >= outBuf.length) {
/* 1644 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1646 */       outBuf[outPtr++] = (char)ch;
/* 1647 */       if (this._inputPtr >= this._inputEnd) {
/* 1648 */         this._textBuffer.setCurrentLength(outPtr);
/* 1649 */         this._minorState = 31;
/* 1650 */         this._expLength = 0;
/* 1651 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1653 */       ch = this._inputBuffer[this._inputPtr++];
/* 1654 */       if (ch == 45 || ch == 43) {
/* 1655 */         if (outPtr >= outBuf.length) {
/* 1656 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1658 */         outBuf[outPtr++] = (char)ch;
/* 1659 */         if (this._inputPtr >= this._inputEnd) {
/* 1660 */           this._textBuffer.setCurrentLength(outPtr);
/* 1661 */           this._minorState = 32;
/* 1662 */           this._expLength = 0;
/* 1663 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1665 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/* 1667 */       while (ch >= 48 && ch <= 57) {
/* 1668 */         expLen++;
/* 1669 */         if (outPtr >= outBuf.length) {
/* 1670 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1672 */         outBuf[outPtr++] = (char)ch;
/* 1673 */         if (this._inputPtr >= this._inputEnd) {
/* 1674 */           this._textBuffer.setCurrentLength(outPtr);
/* 1675 */           this._minorState = 32;
/* 1676 */           this._expLength = expLen;
/* 1677 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1679 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */       
/* 1682 */       ch &= 0xFF;
/* 1683 */       if (expLen == 0) {
/* 1684 */         reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1688 */     this._inputPtr--;
/* 1689 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1691 */     this._expLength = expLen;
/* 1692 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishFloatFraction() throws IOException {
/* 1697 */     int fractLen = this._fractLength;
/* 1698 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 1699 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     int ch;
/*      */     
/* 1703 */     while ((ch = this._inputBuffer[this._inputPtr++]) >= 48 && ch <= 57) {
/* 1704 */       fractLen++;
/* 1705 */       if (outPtr >= outBuf.length) {
/* 1706 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1708 */       outBuf[outPtr++] = (char)ch;
/* 1709 */       if (this._inputPtr >= this._inputEnd) {
/* 1710 */         this._textBuffer.setCurrentLength(outPtr);
/* 1711 */         this._fractLength = fractLen;
/* 1712 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1718 */     if (fractLen == 0) {
/* 1719 */       reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */     }
/* 1721 */     this._fractLength = fractLen;
/* 1722 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1725 */     if (ch == 101 || ch == 69) {
/* 1726 */       this._textBuffer.append((char)ch);
/* 1727 */       this._expLength = 0;
/* 1728 */       if (this._inputPtr >= this._inputEnd) {
/* 1729 */         this._minorState = 31;
/* 1730 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1732 */       this._minorState = 32;
/* 1733 */       return _finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */     } 
/*      */ 
/*      */     
/* 1737 */     this._inputPtr--;
/* 1738 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1740 */     this._expLength = 0;
/* 1741 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishFloatExponent(boolean checkSign, int ch) throws IOException {
/* 1746 */     if (checkSign) {
/* 1747 */       this._minorState = 32;
/* 1748 */       if (ch == 45 || ch == 43) {
/* 1749 */         this._textBuffer.append((char)ch);
/* 1750 */         if (this._inputPtr >= this._inputEnd) {
/* 1751 */           this._minorState = 32;
/* 1752 */           this._expLength = 0;
/* 1753 */           return JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1755 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */     } 
/*      */     
/* 1759 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 1760 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 1761 */     int expLen = this._expLength;
/*      */     
/* 1763 */     while (ch >= 48 && ch <= 57) {
/* 1764 */       expLen++;
/* 1765 */       if (outPtr >= outBuf.length) {
/* 1766 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1768 */       outBuf[outPtr++] = (char)ch;
/* 1769 */       if (this._inputPtr >= this._inputEnd) {
/* 1770 */         this._textBuffer.setCurrentLength(outPtr);
/* 1771 */         this._expLength = expLen;
/* 1772 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1774 */       ch = this._inputBuffer[this._inputPtr++];
/*      */     } 
/*      */     
/* 1777 */     ch &= 0xFF;
/* 1778 */     if (expLen == 0) {
/* 1779 */       reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */     }
/*      */     
/* 1782 */     this._inputPtr--;
/* 1783 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1785 */     this._expLength = expLen;
/* 1786 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
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
/*      */   private final String _fastParseName() throws IOException {
/* 1802 */     byte[] input = this._inputBuffer;
/* 1803 */     int[] codes = _icLatin1;
/* 1804 */     int ptr = this._inputPtr;
/*      */     
/* 1806 */     int q0 = input[ptr++] & 0xFF;
/* 1807 */     if (codes[q0] == 0) {
/* 1808 */       int i = input[ptr++] & 0xFF;
/* 1809 */       if (codes[i] == 0) {
/* 1810 */         int q = q0 << 8 | i;
/* 1811 */         i = input[ptr++] & 0xFF;
/* 1812 */         if (codes[i] == 0) {
/* 1813 */           q = q << 8 | i;
/* 1814 */           i = input[ptr++] & 0xFF;
/* 1815 */           if (codes[i] == 0) {
/* 1816 */             q = q << 8 | i;
/* 1817 */             i = input[ptr++] & 0xFF;
/* 1818 */             if (codes[i] == 0) {
/* 1819 */               this._quad1 = q;
/* 1820 */               return _parseMediumName(ptr, i);
/*      */             } 
/* 1822 */             if (i == 34) {
/* 1823 */               this._inputPtr = ptr;
/* 1824 */               return _findName(q, 4);
/*      */             } 
/* 1826 */             return null;
/*      */           } 
/* 1828 */           if (i == 34) {
/* 1829 */             this._inputPtr = ptr;
/* 1830 */             return _findName(q, 3);
/*      */           } 
/* 1832 */           return null;
/*      */         } 
/* 1834 */         if (i == 34) {
/* 1835 */           this._inputPtr = ptr;
/* 1836 */           return _findName(q, 2);
/*      */         } 
/* 1838 */         return null;
/*      */       } 
/* 1840 */       if (i == 34) {
/* 1841 */         this._inputPtr = ptr;
/* 1842 */         return _findName(q0, 1);
/*      */       } 
/* 1844 */       return null;
/*      */     } 
/* 1846 */     if (q0 == 34) {
/* 1847 */       this._inputPtr = ptr;
/* 1848 */       return "";
/*      */     } 
/* 1850 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName(int ptr, int q2) throws IOException {
/* 1855 */     byte[] input = this._inputBuffer;
/* 1856 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1859 */     int i = input[ptr++] & 0xFF;
/* 1860 */     if (codes[i] == 0) {
/* 1861 */       q2 = q2 << 8 | i;
/* 1862 */       i = input[ptr++] & 0xFF;
/* 1863 */       if (codes[i] == 0) {
/* 1864 */         q2 = q2 << 8 | i;
/* 1865 */         i = input[ptr++] & 0xFF;
/* 1866 */         if (codes[i] == 0) {
/* 1867 */           q2 = q2 << 8 | i;
/* 1868 */           i = input[ptr++] & 0xFF;
/* 1869 */           if (codes[i] == 0) {
/* 1870 */             return _parseMediumName2(ptr, i, q2);
/*      */           }
/* 1872 */           if (i == 34) {
/* 1873 */             this._inputPtr = ptr;
/* 1874 */             return _findName(this._quad1, q2, 4);
/*      */           } 
/* 1876 */           return null;
/*      */         } 
/* 1878 */         if (i == 34) {
/* 1879 */           this._inputPtr = ptr;
/* 1880 */           return _findName(this._quad1, q2, 3);
/*      */         } 
/* 1882 */         return null;
/*      */       } 
/* 1884 */       if (i == 34) {
/* 1885 */         this._inputPtr = ptr;
/* 1886 */         return _findName(this._quad1, q2, 2);
/*      */       } 
/* 1888 */       return null;
/*      */     } 
/* 1890 */     if (i == 34) {
/* 1891 */       this._inputPtr = ptr;
/* 1892 */       return _findName(this._quad1, q2, 1);
/*      */     } 
/* 1894 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName2(int ptr, int q3, int q2) throws IOException {
/* 1899 */     byte[] input = this._inputBuffer;
/* 1900 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1903 */     int i = input[ptr++] & 0xFF;
/* 1904 */     if (codes[i] != 0) {
/* 1905 */       if (i == 34) {
/* 1906 */         this._inputPtr = ptr;
/* 1907 */         return _findName(this._quad1, q2, q3, 1);
/*      */       } 
/* 1909 */       return null;
/*      */     } 
/* 1911 */     q3 = q3 << 8 | i;
/* 1912 */     i = input[ptr++] & 0xFF;
/* 1913 */     if (codes[i] != 0) {
/* 1914 */       if (i == 34) {
/* 1915 */         this._inputPtr = ptr;
/* 1916 */         return _findName(this._quad1, q2, q3, 2);
/*      */       } 
/* 1918 */       return null;
/*      */     } 
/* 1920 */     q3 = q3 << 8 | i;
/* 1921 */     i = input[ptr++] & 0xFF;
/* 1922 */     if (codes[i] != 0) {
/* 1923 */       if (i == 34) {
/* 1924 */         this._inputPtr = ptr;
/* 1925 */         return _findName(this._quad1, q2, q3, 3);
/*      */       } 
/* 1927 */       return null;
/*      */     } 
/* 1929 */     q3 = q3 << 8 | i;
/* 1930 */     i = input[ptr++] & 0xFF;
/* 1931 */     if (i == 34) {
/* 1932 */       this._inputPtr = ptr;
/* 1933 */       return _findName(this._quad1, q2, q3, 4);
/*      */     } 
/*      */     
/* 1936 */     return null;
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
/*      */   private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 1952 */     int[] quads = this._quadBuffer;
/* 1953 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1956 */       if (this._inputPtr >= this._inputEnd) {
/* 1957 */         this._quadLength = qlen;
/* 1958 */         this._pending32 = currQuad;
/* 1959 */         this._pendingBytes = currQuadBytes;
/* 1960 */         this._minorState = 7;
/* 1961 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1963 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1964 */       if (codes[ch] == 0) {
/* 1965 */         if (currQuadBytes < 4) {
/* 1966 */           currQuadBytes++;
/* 1967 */           currQuad = currQuad << 8 | ch;
/*      */           continue;
/*      */         } 
/* 1970 */         if (qlen >= quads.length) {
/* 1971 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 1973 */         quads[qlen++] = currQuad;
/* 1974 */         currQuad = ch;
/* 1975 */         currQuadBytes = 1;
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1980 */       if (ch == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 1984 */       if (ch != 92) {
/*      */         
/* 1986 */         _throwUnquotedSpace(ch, "name");
/*      */       } else {
/*      */         
/* 1989 */         ch = _decodeCharEscape();
/* 1990 */         if (ch < 0) {
/* 1991 */           this._minorState = 8;
/* 1992 */           this._minorStateAfterSplit = 7;
/* 1993 */           this._quadLength = qlen;
/* 1994 */           this._pending32 = currQuad;
/* 1995 */           this._pendingBytes = currQuadBytes;
/* 1996 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2003 */       if (qlen >= quads.length) {
/* 2004 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2006 */       if (ch > 127) {
/*      */         
/* 2008 */         if (currQuadBytes >= 4) {
/* 2009 */           quads[qlen++] = currQuad;
/* 2010 */           currQuad = 0;
/* 2011 */           currQuadBytes = 0;
/*      */         } 
/* 2013 */         if (ch < 2048) {
/* 2014 */           currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2015 */           currQuadBytes++;
/*      */         } else {
/*      */           
/* 2018 */           currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2019 */           currQuadBytes++;
/*      */           
/* 2021 */           if (currQuadBytes >= 4) {
/* 2022 */             quads[qlen++] = currQuad;
/* 2023 */             currQuad = 0;
/* 2024 */             currQuadBytes = 0;
/*      */           } 
/* 2026 */           currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2027 */           currQuadBytes++;
/*      */         } 
/*      */         
/* 2030 */         ch = 0x80 | ch & 0x3F;
/*      */       } 
/* 2032 */       if (currQuadBytes < 4) {
/* 2033 */         currQuadBytes++;
/* 2034 */         currQuad = currQuad << 8 | ch;
/*      */         continue;
/*      */       } 
/* 2037 */       quads[qlen++] = currQuad;
/* 2038 */       currQuad = ch;
/* 2039 */       currQuadBytes = 1;
/*      */     } 
/*      */     
/* 2042 */     if (currQuadBytes > 0) {
/* 2043 */       if (qlen >= quads.length) {
/* 2044 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2046 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/* 2047 */     } else if (qlen == 0) {
/* 2048 */       return _fieldComplete("");
/*      */     } 
/* 2050 */     String name = this._symbols.findName(quads, qlen);
/* 2051 */     if (name == null) {
/* 2052 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2054 */     return _fieldComplete(name);
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
/*      */   private JsonToken _handleOddName(int ch) throws IOException {
/* 2066 */     switch (ch) {
/*      */ 
/*      */       
/*      */       case 35:
/* 2070 */         if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) != 0) {
/* 2071 */           return _finishHashComment(4);
/*      */         }
/*      */         break;
/*      */       case 47:
/* 2075 */         return _startSlashComment(4);
/*      */       case 39:
/* 2077 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2078 */           return _finishAposName(0, 0, 0);
/*      */         }
/*      */         break;
/*      */       case 93:
/* 2082 */         return _closeArrayScope();
/*      */     } 
/*      */     
/* 2085 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/*      */ 
/*      */       
/* 2088 */       char c = (char)ch;
/* 2089 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */     
/* 2093 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 2095 */     if (codes[ch] != 0) {
/* 2096 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */     
/* 2099 */     return _finishUnquotedName(0, ch, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JsonToken _finishUnquotedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 2110 */     int[] quads = this._quadBuffer;
/* 2111 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2116 */       if (this._inputPtr >= this._inputEnd) {
/* 2117 */         this._quadLength = qlen;
/* 2118 */         this._pending32 = currQuad;
/* 2119 */         this._pendingBytes = currQuadBytes;
/* 2120 */         this._minorState = 10;
/* 2121 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2123 */       int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2124 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 2127 */       this._inputPtr++;
/*      */       
/* 2129 */       if (currQuadBytes < 4) {
/* 2130 */         currQuadBytes++;
/* 2131 */         currQuad = currQuad << 8 | ch; continue;
/*      */       } 
/* 2133 */       if (qlen >= quads.length) {
/* 2134 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2136 */       quads[qlen++] = currQuad;
/* 2137 */       currQuad = ch;
/* 2138 */       currQuadBytes = 1;
/*      */     } 
/*      */ 
/*      */     
/* 2142 */     if (currQuadBytes > 0) {
/* 2143 */       if (qlen >= quads.length) {
/* 2144 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2146 */       quads[qlen++] = currQuad;
/*      */     } 
/* 2148 */     String name = this._symbols.findName(quads, qlen);
/* 2149 */     if (name == null) {
/* 2150 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2152 */     return _fieldComplete(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private JsonToken _finishAposName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 2158 */     int[] quads = this._quadBuffer;
/* 2159 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 2162 */       if (this._inputPtr >= this._inputEnd) {
/* 2163 */         this._quadLength = qlen;
/* 2164 */         this._pending32 = currQuad;
/* 2165 */         this._pendingBytes = currQuadBytes;
/* 2166 */         this._minorState = 9;
/* 2167 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2169 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2170 */       if (ch == 39) {
/*      */         break;
/*      */       }
/*      */       
/* 2174 */       if (ch != 34 && codes[ch] != 0) {
/* 2175 */         if (ch != 92) {
/*      */           
/* 2177 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 2180 */           ch = _decodeCharEscape();
/* 2181 */           if (ch < 0) {
/* 2182 */             this._minorState = 8;
/* 2183 */             this._minorStateAfterSplit = 9;
/* 2184 */             this._quadLength = qlen;
/* 2185 */             this._pending32 = currQuad;
/* 2186 */             this._pendingBytes = currQuadBytes;
/* 2187 */             return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */           } 
/*      */         } 
/* 2190 */         if (ch > 127) {
/*      */           
/* 2192 */           if (currQuadBytes >= 4) {
/* 2193 */             if (qlen >= quads.length) {
/* 2194 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 2196 */             quads[qlen++] = currQuad;
/* 2197 */             currQuad = 0;
/* 2198 */             currQuadBytes = 0;
/*      */           } 
/* 2200 */           if (ch < 2048) {
/* 2201 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2202 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 2205 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2206 */             currQuadBytes++;
/*      */             
/* 2208 */             if (currQuadBytes >= 4) {
/* 2209 */               if (qlen >= quads.length) {
/* 2210 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 2212 */               quads[qlen++] = currQuad;
/* 2213 */               currQuad = 0;
/* 2214 */               currQuadBytes = 0;
/*      */             } 
/* 2216 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2217 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 2220 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 2224 */       if (currQuadBytes < 4) {
/* 2225 */         currQuadBytes++;
/* 2226 */         currQuad = currQuad << 8 | ch; continue;
/*      */       } 
/* 2228 */       if (qlen >= quads.length) {
/* 2229 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2231 */       quads[qlen++] = currQuad;
/* 2232 */       currQuad = ch;
/* 2233 */       currQuadBytes = 1;
/*      */     } 
/*      */ 
/*      */     
/* 2237 */     if (currQuadBytes > 0) {
/* 2238 */       if (qlen >= quads.length) {
/* 2239 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2241 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/* 2242 */     } else if (qlen == 0) {
/* 2243 */       return _fieldComplete("");
/*      */     } 
/* 2245 */     String name = this._symbols.findName(quads, qlen);
/* 2246 */     if (name == null) {
/* 2247 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2249 */     return _fieldComplete(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonToken _finishFieldWithEscape() throws IOException {
/* 2255 */     int ch = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
/* 2256 */     if (ch < 0) {
/* 2257 */       this._minorState = 8;
/* 2258 */       return JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 2260 */     if (this._quadLength >= this._quadBuffer.length) {
/* 2261 */       this._quadBuffer = growArrayBy(this._quadBuffer, 32);
/*      */     }
/* 2263 */     int currQuad = this._pending32;
/* 2264 */     int currQuadBytes = this._pendingBytes;
/* 2265 */     if (ch > 127) {
/*      */       
/* 2267 */       if (currQuadBytes >= 4) {
/* 2268 */         this._quadBuffer[this._quadLength++] = currQuad;
/* 2269 */         currQuad = 0;
/* 2270 */         currQuadBytes = 0;
/*      */       } 
/* 2272 */       if (ch < 2048) {
/* 2273 */         currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2274 */         currQuadBytes++;
/*      */       } else {
/*      */         
/* 2277 */         currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/*      */         
/* 2279 */         if (++currQuadBytes >= 4) {
/* 2280 */           this._quadBuffer[this._quadLength++] = currQuad;
/* 2281 */           currQuad = 0;
/* 2282 */           currQuadBytes = 0;
/*      */         } 
/* 2284 */         currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2285 */         currQuadBytes++;
/*      */       } 
/*      */       
/* 2288 */       ch = 0x80 | ch & 0x3F;
/*      */     } 
/* 2290 */     if (currQuadBytes < 4) {
/* 2291 */       currQuadBytes++;
/* 2292 */       currQuad = currQuad << 8 | ch;
/*      */     } else {
/* 2294 */       this._quadBuffer[this._quadLength++] = currQuad;
/* 2295 */       currQuad = ch;
/* 2296 */       currQuadBytes = 1;
/*      */     } 
/* 2298 */     if (this._minorStateAfterSplit == 9) {
/* 2299 */       return _finishAposName(this._quadLength, currQuad, currQuadBytes);
/*      */     }
/* 2301 */     return _parseEscapedName(this._quadLength, currQuad, currQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private int _decodeSplitEscaped(int value, int bytesRead) throws IOException {
/* 2306 */     if (this._inputPtr >= this._inputEnd) {
/* 2307 */       this._quoted32 = value;
/* 2308 */       this._quotedDigits = bytesRead;
/* 2309 */       return -1;
/*      */     } 
/* 2311 */     int c = this._inputBuffer[this._inputPtr++];
/* 2312 */     if (bytesRead == -1) {
/* 2313 */       char ch; switch (c) {
/*      */         
/*      */         case 98:
/* 2316 */           return 8;
/*      */         case 116:
/* 2318 */           return 9;
/*      */         case 110:
/* 2320 */           return 10;
/*      */         case 102:
/* 2322 */           return 12;
/*      */         case 114:
/* 2324 */           return 13;
/*      */ 
/*      */         
/*      */         case 34:
/*      */         case 47:
/*      */         case 92:
/* 2330 */           return c;
/*      */ 
/*      */ 
/*      */         
/*      */         case 117:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 2339 */           ch = (char)c;
/* 2340 */           return _handleUnrecognizedCharacterEscape(ch);
/*      */       } 
/*      */       
/* 2343 */       if (this._inputPtr >= this._inputEnd) {
/* 2344 */         this._quotedDigits = 0;
/* 2345 */         this._quoted32 = 0;
/* 2346 */         return -1;
/*      */       } 
/* 2348 */       c = this._inputBuffer[this._inputPtr++];
/* 2349 */       bytesRead = 0;
/*      */     } 
/* 2351 */     c &= 0xFF;
/*      */     while (true) {
/* 2353 */       int digit = CharTypes.charToHex(c);
/* 2354 */       if (digit < 0) {
/* 2355 */         _reportUnexpectedChar(c, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2357 */       value = value << 4 | digit;
/* 2358 */       if (++bytesRead == 4) {
/* 2359 */         return value;
/*      */       }
/* 2361 */       if (this._inputPtr >= this._inputEnd) {
/* 2362 */         this._quotedDigits = bytesRead;
/* 2363 */         this._quoted32 = value;
/* 2364 */         return -1;
/*      */       } 
/* 2366 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
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
/*      */   protected JsonToken _startString() throws IOException {
/* 2378 */     int ptr = this._inputPtr;
/* 2379 */     int outPtr = 0;
/* 2380 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2381 */     int[] codes = _icUTF8;
/*      */     
/* 2383 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2384 */     byte[] inputBuffer = this._inputBuffer;
/* 2385 */     while (ptr < max) {
/* 2386 */       int c = inputBuffer[ptr] & 0xFF;
/* 2387 */       if (codes[c] != 0) {
/* 2388 */         if (c == 34) {
/* 2389 */           this._inputPtr = ptr + 1;
/* 2390 */           this._textBuffer.setCurrentLength(outPtr);
/* 2391 */           return _valueComplete(JsonToken.VALUE_STRING);
/*      */         } 
/*      */         break;
/*      */       } 
/* 2395 */       ptr++;
/* 2396 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2398 */     this._textBuffer.setCurrentLength(outPtr);
/* 2399 */     this._inputPtr = ptr;
/* 2400 */     return _finishRegularString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishRegularString() throws IOException {
/* 2408 */     int[] codes = _icUTF8;
/* 2409 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/* 2411 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 2412 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2413 */     int ptr = this._inputPtr;
/* 2414 */     int safeEnd = this._inputEnd - 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2421 */       if (ptr >= this._inputEnd) {
/* 2422 */         this._inputPtr = ptr;
/* 2423 */         this._minorState = 40;
/* 2424 */         this._textBuffer.setCurrentLength(outPtr);
/* 2425 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2427 */       if (outPtr >= outBuf.length) {
/* 2428 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2429 */         outPtr = 0;
/*      */       } 
/* 2431 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2432 */       while (ptr < max) {
/* 2433 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2434 */         if (codes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2441 */           if (c == 34) {
/* 2442 */             this._inputPtr = ptr;
/* 2443 */             this._textBuffer.setCurrentLength(outPtr);
/* 2444 */             return _valueComplete(JsonToken.VALUE_STRING);
/*      */           } 
/*      */           
/* 2447 */           if (ptr >= safeEnd) {
/* 2448 */             this._inputPtr = ptr;
/* 2449 */             this._textBuffer.setCurrentLength(outPtr);
/* 2450 */             if (!_decodeSplitMultiByte(c, codes[c], (ptr < this._inputEnd))) {
/* 2451 */               this._minorStateAfterSplit = 40;
/* 2452 */               return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */             } 
/* 2454 */             outBuf = this._textBuffer.getBufferWithoutReset();
/* 2455 */             outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2456 */             ptr = this._inputPtr;
/*      */             
/*      */             continue;
/*      */           } 
/* 2460 */           switch (codes[c]) {
/*      */             case 1:
/* 2462 */               this._inputPtr = ptr;
/* 2463 */               c = _decodeFastCharEscape();
/* 2464 */               ptr = this._inputPtr;
/*      */               break;
/*      */             case 2:
/* 2467 */               c = _decodeUTF8_2(c, this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 3:
/* 2470 */               c = _decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 4:
/* 2473 */               c = _decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */ 
/*      */               
/* 2476 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2477 */               if (outPtr >= outBuf.length) {
/* 2478 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2479 */                 outPtr = 0;
/*      */               } 
/* 2481 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2485 */               if (c < 32) {
/*      */                 
/* 2487 */                 _throwUnquotedSpace(c, "string value");
/*      */                 break;
/*      */               } 
/* 2490 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/*      */           
/* 2494 */           if (outPtr >= outBuf.length) {
/* 2495 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2496 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2499 */           outBuf[outPtr++] = (char)c;
/*      */           continue;
/*      */         } 
/*      */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */     }  } protected JsonToken _startAposString() throws IOException {
/* 2505 */     int ptr = this._inputPtr;
/* 2506 */     int outPtr = 0;
/* 2507 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2508 */     int[] codes = _icUTF8;
/*      */     
/* 2510 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2511 */     byte[] inputBuffer = this._inputBuffer;
/* 2512 */     while (ptr < max) {
/* 2513 */       int c = inputBuffer[ptr] & 0xFF;
/* 2514 */       if (c == 39) {
/* 2515 */         this._inputPtr = ptr + 1;
/* 2516 */         this._textBuffer.setCurrentLength(outPtr);
/* 2517 */         return _valueComplete(JsonToken.VALUE_STRING);
/*      */       } 
/*      */       
/* 2520 */       if (codes[c] != 0) {
/*      */         break;
/*      */       }
/* 2523 */       ptr++;
/* 2524 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2526 */     this._textBuffer.setCurrentLength(outPtr);
/* 2527 */     this._inputPtr = ptr;
/* 2528 */     return _finishAposString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishAposString() throws IOException {
/* 2534 */     int[] codes = _icUTF8;
/* 2535 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/* 2537 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 2538 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2539 */     int ptr = this._inputPtr;
/* 2540 */     int safeEnd = this._inputEnd - 5;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2546 */       if (ptr >= this._inputEnd) {
/* 2547 */         this._inputPtr = ptr;
/* 2548 */         this._minorState = 45;
/* 2549 */         this._textBuffer.setCurrentLength(outPtr);
/* 2550 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2552 */       if (outPtr >= outBuf.length) {
/* 2553 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2554 */         outPtr = 0;
/*      */       } 
/* 2556 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2557 */       while (ptr < max) {
/* 2558 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2559 */         if (codes[c] != 0 && c != 34) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2572 */           if (ptr >= safeEnd) {
/* 2573 */             this._inputPtr = ptr;
/* 2574 */             this._textBuffer.setCurrentLength(outPtr);
/* 2575 */             if (!_decodeSplitMultiByte(c, codes[c], (ptr < this._inputEnd))) {
/* 2576 */               this._minorStateAfterSplit = 45;
/* 2577 */               return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */             } 
/* 2579 */             outBuf = this._textBuffer.getBufferWithoutReset();
/* 2580 */             outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2581 */             ptr = this._inputPtr;
/*      */             
/*      */             continue;
/*      */           } 
/* 2585 */           switch (codes[c]) {
/*      */             case 1:
/* 2587 */               this._inputPtr = ptr;
/* 2588 */               c = _decodeFastCharEscape();
/* 2589 */               ptr = this._inputPtr;
/*      */               break;
/*      */             case 2:
/* 2592 */               c = _decodeUTF8_2(c, this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 3:
/* 2595 */               c = _decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 4:
/* 2598 */               c = _decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */ 
/*      */               
/* 2601 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2602 */               if (outPtr >= outBuf.length) {
/* 2603 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2604 */                 outPtr = 0;
/*      */               } 
/* 2606 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2610 */               if (c < 32) {
/*      */                 
/* 2612 */                 _throwUnquotedSpace(c, "string value");
/*      */                 break;
/*      */               } 
/* 2615 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/*      */           
/* 2619 */           if (outPtr >= outBuf.length) {
/* 2620 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2621 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2624 */           outBuf[outPtr++] = (char)c; continue;
/*      */         }  if (c == 39) {
/*      */           this._inputPtr = ptr; this._textBuffer.setCurrentLength(outPtr);
/*      */           return _valueComplete(JsonToken.VALUE_STRING);
/*      */         } 
/*      */         outBuf[outPtr++] = (char)c;
/*      */       } 
/* 2631 */     }  } private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext) throws IOException { switch (type) {
/*      */       case 1:
/* 2633 */         c = _decodeSplitEscaped(0, -1);
/* 2634 */         if (c < 0) {
/* 2635 */           this._minorState = 41;
/* 2636 */           return false;
/*      */         } 
/* 2638 */         this._textBuffer.append((char)c);
/* 2639 */         return true;
/*      */       case 2:
/* 2641 */         if (gotNext) {
/*      */           
/* 2643 */           c = _decodeUTF8_2(c, this._inputBuffer[this._inputPtr++]);
/* 2644 */           this._textBuffer.append((char)c);
/* 2645 */           return true;
/*      */         } 
/* 2647 */         this._minorState = 42;
/* 2648 */         this._pending32 = c;
/* 2649 */         return false;
/*      */       case 3:
/* 2651 */         c &= 0xF;
/* 2652 */         if (gotNext) {
/* 2653 */           return _decodeSplitUTF8_3(c, 1, this._inputBuffer[this._inputPtr++]);
/*      */         }
/* 2655 */         this._minorState = 43;
/* 2656 */         this._pending32 = c;
/* 2657 */         this._pendingBytes = 1;
/* 2658 */         return false;
/*      */       case 4:
/* 2660 */         c &= 0x7;
/* 2661 */         if (gotNext) {
/* 2662 */           return _decodeSplitUTF8_4(c, 1, this._inputBuffer[this._inputPtr++]);
/*      */         }
/* 2664 */         this._pending32 = c;
/* 2665 */         this._pendingBytes = 1;
/* 2666 */         this._minorState = 44;
/* 2667 */         return false;
/*      */     } 
/* 2669 */     if (c < 32) {
/*      */       
/* 2671 */       _throwUnquotedSpace(c, "string value");
/*      */     } else {
/*      */       
/* 2674 */       _reportInvalidChar(c);
/*      */     } 
/* 2676 */     this._textBuffer.append((char)c);
/* 2677 */     return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next) throws IOException {
/* 2684 */     if (prevCount == 1) {
/* 2685 */       if ((next & 0xC0) != 128) {
/* 2686 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2688 */       prev = prev << 6 | next & 0x3F;
/* 2689 */       if (this._inputPtr >= this._inputEnd) {
/* 2690 */         this._minorState = 43;
/* 2691 */         this._pending32 = prev;
/* 2692 */         this._pendingBytes = 2;
/* 2693 */         return false;
/*      */       } 
/* 2695 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2697 */     if ((next & 0xC0) != 128) {
/* 2698 */       _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */     }
/* 2700 */     this._textBuffer.append((char)(prev << 6 | next & 0x3F));
/* 2701 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next) throws IOException {
/* 2709 */     if (prevCount == 1) {
/* 2710 */       if ((next & 0xC0) != 128) {
/* 2711 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2713 */       prev = prev << 6 | next & 0x3F;
/* 2714 */       if (this._inputPtr >= this._inputEnd) {
/* 2715 */         this._minorState = 44;
/* 2716 */         this._pending32 = prev;
/* 2717 */         this._pendingBytes = 2;
/* 2718 */         return false;
/*      */       } 
/* 2720 */       prevCount = 2;
/* 2721 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2723 */     if (prevCount == 2) {
/* 2724 */       if ((next & 0xC0) != 128) {
/* 2725 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2727 */       prev = prev << 6 | next & 0x3F;
/* 2728 */       if (this._inputPtr >= this._inputEnd) {
/* 2729 */         this._minorState = 44;
/* 2730 */         this._pending32 = prev;
/* 2731 */         this._pendingBytes = 3;
/* 2732 */         return false;
/*      */       } 
/* 2734 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2736 */     if ((next & 0xC0) != 128) {
/* 2737 */       _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */     }
/* 2739 */     int c = (prev << 6 | next & 0x3F) - 65536;
/*      */     
/* 2741 */     this._textBuffer.append((char)(0xD800 | c >> 10));
/* 2742 */     c = 0xDC00 | c & 0x3FF;
/*      */     
/* 2744 */     this._textBuffer.append((char)c);
/* 2745 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeCharEscape() throws IOException {
/* 2756 */     int left = this._inputEnd - this._inputPtr;
/* 2757 */     if (left < 5) {
/* 2758 */       return _decodeSplitEscaped(0, -1);
/*      */     }
/* 2760 */     return _decodeFastCharEscape();
/*      */   }
/*      */   
/*      */   private final int _decodeFastCharEscape() throws IOException {
/*      */     char c1;
/* 2765 */     int c = this._inputBuffer[this._inputPtr++];
/* 2766 */     switch (c) {
/*      */       
/*      */       case 98:
/* 2769 */         return 8;
/*      */       case 116:
/* 2771 */         return 9;
/*      */       case 110:
/* 2773 */         return 10;
/*      */       case 102:
/* 2775 */         return 12;
/*      */       case 114:
/* 2777 */         return 13;
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 2783 */         return (char)c;
/*      */ 
/*      */ 
/*      */       
/*      */       case 117:
/*      */         break;
/*      */ 
/*      */       
/*      */       default:
/* 2792 */         c1 = (char)c;
/* 2793 */         return _handleUnrecognizedCharacterEscape(c1);
/*      */     } 
/*      */ 
/*      */     
/* 2797 */     int ch = this._inputBuffer[this._inputPtr++];
/* 2798 */     int digit = CharTypes.charToHex(ch);
/* 2799 */     int result = digit;
/*      */     
/* 2801 */     if (digit >= 0) {
/* 2802 */       ch = this._inputBuffer[this._inputPtr++];
/* 2803 */       digit = CharTypes.charToHex(ch);
/* 2804 */       if (digit >= 0) {
/* 2805 */         result = result << 4 | digit;
/* 2806 */         ch = this._inputBuffer[this._inputPtr++];
/* 2807 */         digit = CharTypes.charToHex(ch);
/* 2808 */         if (digit >= 0) {
/* 2809 */           result = result << 4 | digit;
/* 2810 */           ch = this._inputBuffer[this._inputPtr++];
/* 2811 */           digit = CharTypes.charToHex(ch);
/* 2812 */           if (digit >= 0) {
/* 2813 */             return result << 4 | digit;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2818 */     _reportUnexpectedChar(ch & 0xFF, "expected a hex-digit for character escape sequence");
/* 2819 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_2(int c, int d) throws IOException {
/* 2830 */     if ((d & 0xC0) != 128) {
/* 2831 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2833 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_3(int c, int d, int e) throws IOException {
/* 2838 */     c &= 0xF;
/* 2839 */     if ((d & 0xC0) != 128) {
/* 2840 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2842 */     c = c << 6 | d & 0x3F;
/* 2843 */     if ((e & 0xC0) != 128) {
/* 2844 */       _reportInvalidOther(e & 0xFF, this._inputPtr);
/*      */     }
/* 2846 */     return c << 6 | e & 0x3F;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_4(int c, int d, int e, int f) throws IOException {
/* 2853 */     if ((d & 0xC0) != 128) {
/* 2854 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2856 */     c = (c & 0x7) << 6 | d & 0x3F;
/* 2857 */     if ((e & 0xC0) != 128) {
/* 2858 */       _reportInvalidOther(e & 0xFF, this._inputPtr);
/*      */     }
/* 2860 */     c = c << 6 | e & 0x3F;
/* 2861 */     if ((f & 0xC0) != 128) {
/* 2862 */       _reportInvalidOther(f & 0xFF, this._inputPtr);
/*      */     }
/* 2864 */     return (c << 6 | f & 0x3F) - 65536;
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\async\NonBlockingJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */