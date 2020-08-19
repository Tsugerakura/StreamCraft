/*     */ package com.fasterxml.jackson.core.base;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonParseException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.exc.InputCoercionException;
/*     */ import com.fasterxml.jackson.core.io.JsonEOFException;
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.core.util.VersionUtil;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public abstract class ParserMinimalBase
/*     */   extends JsonParser
/*     */ {
/*     */   protected static final int INT_TAB = 9;
/*     */   protected static final int INT_LF = 10;
/*     */   protected static final int INT_CR = 13;
/*     */   protected static final int INT_SPACE = 32;
/*     */   protected static final int INT_LBRACKET = 91;
/*     */   protected static final int INT_RBRACKET = 93;
/*     */   protected static final int INT_LCURLY = 123;
/*     */   protected static final int INT_RCURLY = 125;
/*     */   protected static final int INT_QUOTE = 34;
/*     */   protected static final int INT_APOS = 39;
/*     */   protected static final int INT_BACKSLASH = 92;
/*     */   protected static final int INT_SLASH = 47;
/*     */   protected static final int INT_ASTERISK = 42;
/*     */   protected static final int INT_COLON = 58;
/*     */   protected static final int INT_COMMA = 44;
/*     */   protected static final int INT_HASH = 35;
/*     */   protected static final int INT_0 = 48;
/*     */   protected static final int INT_9 = 57;
/*     */   protected static final int INT_MINUS = 45;
/*     */   protected static final int INT_PLUS = 43;
/*     */   protected static final int INT_PERIOD = 46;
/*     */   protected static final int INT_e = 101;
/*     */   protected static final int INT_E = 69;
/*     */   protected static final char CHAR_NULL = '\000';
/*  62 */   protected static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   protected static final int[] NO_INTS = new int[0];
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_UNKNOWN = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_INT = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_LONG = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_BIGINT = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_DOUBLE = 8;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_BIGDECIMAL = 16;
/*     */ 
/*     */   
/*     */   protected static final int NR_FLOAT = 32;
/*     */ 
/*     */   
/*  97 */   protected static final BigInteger BI_MIN_INT = BigInteger.valueOf(-2147483648L);
/*  98 */   protected static final BigInteger BI_MAX_INT = BigInteger.valueOf(2147483647L);
/*     */   
/* 100 */   protected static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
/* 101 */   protected static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
/*     */   
/* 103 */   protected static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
/* 104 */   protected static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
/*     */   
/* 106 */   protected static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
/* 107 */   protected static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final long MIN_INT_L = -2147483648L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final long MAX_INT_L = 2147483647L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MIN_LONG_D = -9.223372036854776E18D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MAX_LONG_D = 9.223372036854776E18D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MIN_INT_D = -2.147483648E9D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MAX_INT_D = 2.147483647E9D;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int MAX_ERROR_TOKEN_LENGTH = 256;
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonToken _currToken;
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonToken _lastClearedToken;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ParserMinimalBase() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ParserMinimalBase(int features) {
/* 160 */     super(features);
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
/*     */   public abstract JsonToken nextToken() throws IOException;
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
/*     */   public JsonToken currentToken() {
/* 186 */     return this._currToken;
/*     */   } public int currentTokenId() {
/* 188 */     JsonToken t = this._currToken;
/* 189 */     return (t == null) ? 0 : t.id();
/*     */   }
/*     */   public JsonToken getCurrentToken() {
/* 192 */     return this._currToken;
/*     */   } public int getCurrentTokenId() {
/* 194 */     JsonToken t = this._currToken;
/* 195 */     return (t == null) ? 0 : t.id();
/*     */   }
/*     */   public boolean hasCurrentToken() {
/* 198 */     return (this._currToken != null);
/*     */   } public boolean hasTokenId(int id) {
/* 200 */     JsonToken t = this._currToken;
/* 201 */     if (t == null) {
/* 202 */       return (0 == id);
/*     */     }
/* 204 */     return (t.id() == id);
/*     */   }
/*     */   
/*     */   public boolean hasToken(JsonToken t) {
/* 208 */     return (this._currToken == t);
/*     */   }
/*     */   
/* 211 */   public boolean isExpectedStartArrayToken() { return (this._currToken == JsonToken.START_ARRAY); } public boolean isExpectedStartObjectToken() {
/* 212 */     return (this._currToken == JsonToken.START_OBJECT);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken nextValue() throws IOException {
/* 218 */     JsonToken t = nextToken();
/* 219 */     if (t == JsonToken.FIELD_NAME) {
/* 220 */       t = nextToken();
/*     */     }
/* 222 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 228 */     if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY)
/*     */     {
/* 230 */       return this;
/*     */     }
/* 232 */     int open = 1;
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 237 */       JsonToken t = nextToken();
/* 238 */       if (t == null) {
/* 239 */         _handleEOF();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 244 */         return this;
/*     */       } 
/* 246 */       if (t.isStructStart()) {
/* 247 */         open++; continue;
/* 248 */       }  if (t.isStructEnd()) {
/* 249 */         if (--open == 0)
/* 250 */           return this; 
/*     */         continue;
/*     */       } 
/* 253 */       if (t == JsonToken.NOT_AVAILABLE)
/*     */       {
/*     */         
/* 256 */         _reportError("Not enough content available for `skipChildren()`: non-blocking parser? (%s)", 
/* 257 */             getClass().getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void _handleEOF() throws JsonParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getCurrentName() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void close() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isClosed();
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonStreamContext getParsingContext();
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearCurrentToken() {
/* 287 */     if (this._currToken != null) {
/* 288 */       this._lastClearedToken = this._currToken;
/* 289 */       this._currToken = null;
/*     */     } 
/*     */   }
/*     */   public JsonToken getLastClearedToken() {
/* 293 */     return this._lastClearedToken;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void overrideCurrentName(String paramString);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getText() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract char[] getTextCharacters() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean hasTextCharacters();
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getTextLength() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getTextOffset() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract byte[] getBinaryValue(Base64Variant paramBase64Variant) throws IOException;
/*     */ 
/*     */   
/*     */   public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
/* 326 */     JsonToken t = this._currToken;
/* 327 */     if (t != null) {
/* 328 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 330 */           str = getText().trim();
/* 331 */           if ("true".equals(str)) {
/* 332 */             return true;
/*     */           }
/* 334 */           if ("false".equals(str)) {
/* 335 */             return false;
/*     */           }
/* 337 */           if (_hasTextualNull(str)) {
/* 338 */             return false;
/*     */           }
/*     */           break;
/*     */         case 7:
/* 342 */           return (getIntValue() != 0);
/*     */         case 9:
/* 344 */           return true;
/*     */         case 10:
/*     */         case 11:
/* 347 */           return false;
/*     */         case 12:
/* 349 */           value = getEmbeddedObject();
/* 350 */           if (value instanceof Boolean) {
/* 351 */             return ((Boolean)value).booleanValue();
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 357 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt() throws IOException {
/* 363 */     JsonToken t = this._currToken;
/* 364 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 365 */       return getIntValue();
/*     */     }
/* 367 */     return getValueAsInt(0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt(int defaultValue) throws IOException {
/* 373 */     JsonToken t = this._currToken;
/* 374 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 375 */       return getIntValue();
/*     */     }
/* 377 */     if (t != null) {
/* 378 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 380 */           str = getText();
/* 381 */           if (_hasTextualNull(str)) {
/* 382 */             return 0;
/*     */           }
/* 384 */           return NumberInput.parseAsInt(str, defaultValue);
/*     */         case 9:
/* 386 */           return 1;
/*     */         case 10:
/* 388 */           return 0;
/*     */         case 11:
/* 390 */           return 0;
/*     */         case 12:
/* 392 */           value = getEmbeddedObject();
/* 393 */           if (value instanceof Number)
/* 394 */             return ((Number)value).intValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 398 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValueAsLong() throws IOException {
/* 404 */     JsonToken t = this._currToken;
/* 405 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 406 */       return getLongValue();
/*     */     }
/* 408 */     return getValueAsLong(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValueAsLong(long defaultValue) throws IOException {
/* 414 */     JsonToken t = this._currToken;
/* 415 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 416 */       return getLongValue();
/*     */     }
/* 418 */     if (t != null) {
/* 419 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 421 */           str = getText();
/* 422 */           if (_hasTextualNull(str)) {
/* 423 */             return 0L;
/*     */           }
/* 425 */           return NumberInput.parseAsLong(str, defaultValue);
/*     */         case 9:
/* 427 */           return 1L;
/*     */         case 10:
/*     */         case 11:
/* 430 */           return 0L;
/*     */         case 12:
/* 432 */           value = getEmbeddedObject();
/* 433 */           if (value instanceof Number)
/* 434 */             return ((Number)value).longValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 438 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getValueAsDouble(double defaultValue) throws IOException {
/* 444 */     JsonToken t = this._currToken;
/* 445 */     if (t != null) {
/* 446 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 448 */           str = getText();
/* 449 */           if (_hasTextualNull(str)) {
/* 450 */             return 0.0D;
/*     */           }
/* 452 */           return NumberInput.parseAsDouble(str, defaultValue);
/*     */         case 7:
/*     */         case 8:
/* 455 */           return getDoubleValue();
/*     */         case 9:
/* 457 */           return 1.0D;
/*     */         case 10:
/*     */         case 11:
/* 460 */           return 0.0D;
/*     */         case 12:
/* 462 */           value = getEmbeddedObject();
/* 463 */           if (value instanceof Number)
/* 464 */             return ((Number)value).doubleValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 468 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValueAsString() throws IOException {
/* 473 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 474 */       return getText();
/*     */     }
/* 476 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 477 */       return getCurrentName();
/*     */     }
/* 479 */     return getValueAsString((String)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValueAsString(String defaultValue) throws IOException {
/* 484 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 485 */       return getText();
/*     */     }
/* 487 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 488 */       return getCurrentName();
/*     */     }
/* 490 */     if (this._currToken == null || this._currToken == JsonToken.VALUE_NULL || !this._currToken.isScalarValue()) {
/* 491 */       return defaultValue;
/*     */     }
/* 493 */     return getText();
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
/*     */   protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant) throws IOException {
/*     */     try {
/* 509 */       b64variant.decode(str, builder);
/* 510 */     } catch (IllegalArgumentException e) {
/* 511 */       _reportError(e.getMessage());
/*     */     } 
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
/*     */   protected boolean _hasTextualNull(String value) {
/* 528 */     return "null".equals(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportUnexpectedNumberChar(int ch, String comment) throws JsonParseException {
/* 537 */     String msg = String.format("Unexpected character (%s) in numeric value", new Object[] { _getCharDesc(ch) });
/* 538 */     if (comment != null) {
/* 539 */       msg = msg + ": " + comment;
/*     */     }
/* 541 */     _reportError(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportInvalidNumber(String msg) throws JsonParseException {
/* 551 */     _reportError("Invalid numeric value: " + msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt() throws IOException {
/* 560 */     reportOverflowInt(getText());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt(String numDesc) throws IOException {
/* 565 */     reportOverflowInt(numDesc, JsonToken.VALUE_NUMBER_INT);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt(String numDesc, JsonToken inputType) throws IOException {
/* 570 */     _reportInputCoercion(String.format("Numeric value (%s) out of range of int (%d - %s)", new Object[] {
/* 571 */             _longIntegerDesc(numDesc), Integer.valueOf(-2147483648), Integer.valueOf(2147483647)
/*     */           }), inputType, int.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong() throws IOException {
/* 581 */     reportOverflowLong(getText());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong(String numDesc) throws IOException {
/* 586 */     reportOverflowLong(numDesc, JsonToken.VALUE_NUMBER_INT);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong(String numDesc, JsonToken inputType) throws IOException {
/* 591 */     _reportInputCoercion(String.format("Numeric value (%s) out of range of long (%d - %s)", new Object[] {
/* 592 */             _longIntegerDesc(numDesc), Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE)
/*     */           }), inputType, long.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportInputCoercion(String msg, JsonToken inputType, Class<?> targetType) throws InputCoercionException {
/* 601 */     throw new InputCoercionException(this, msg, inputType, targetType);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String _longIntegerDesc(String rawNum) {
/* 606 */     int rawLen = rawNum.length();
/* 607 */     if (rawLen < 1000) {
/* 608 */       return rawNum;
/*     */     }
/* 610 */     if (rawNum.startsWith("-")) {
/* 611 */       rawLen--;
/*     */     }
/* 613 */     return String.format("[Integer with %d digits]", new Object[] { Integer.valueOf(rawLen) });
/*     */   }
/*     */ 
/*     */   
/*     */   protected String _longNumberDesc(String rawNum) {
/* 618 */     int rawLen = rawNum.length();
/* 619 */     if (rawLen < 1000) {
/* 620 */       return rawNum;
/*     */     }
/* 622 */     if (rawNum.startsWith("-")) {
/* 623 */       rawLen--;
/*     */     }
/* 625 */     return String.format("[number with %d characters]", new Object[] { Integer.valueOf(rawLen) });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _reportUnexpectedChar(int ch, String comment) throws JsonParseException {
/* 630 */     if (ch < 0) {
/* 631 */       _reportInvalidEOF();
/*     */     }
/* 633 */     String msg = String.format("Unexpected character (%s)", new Object[] { _getCharDesc(ch) });
/* 634 */     if (comment != null) {
/* 635 */       msg = msg + ": " + comment;
/*     */     }
/* 637 */     _reportError(msg);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidEOF() throws JsonParseException {
/* 641 */     _reportInvalidEOF(" in " + this._currToken, this._currToken);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportInvalidEOFInValue(JsonToken type) throws JsonParseException {
/*     */     String msg;
/* 649 */     if (type == JsonToken.VALUE_STRING) {
/* 650 */       msg = " in a String value";
/* 651 */     } else if (type == JsonToken.VALUE_NUMBER_INT || type == JsonToken.VALUE_NUMBER_FLOAT) {
/*     */       
/* 653 */       msg = " in a Number value";
/*     */     } else {
/* 655 */       msg = " in a value";
/*     */     } 
/* 657 */     _reportInvalidEOF(msg, type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportInvalidEOF(String msg, JsonToken currToken) throws JsonParseException {
/* 664 */     throw new JsonEOFException(this, currToken, "Unexpected end-of-input" + msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void _reportInvalidEOFInValue() throws JsonParseException {
/* 672 */     _reportInvalidEOF(" in a value");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void _reportInvalidEOF(String msg) throws JsonParseException {
/* 680 */     throw new JsonEOFException(this, null, "Unexpected end-of-input" + msg);
/*     */   }
/*     */   
/*     */   protected void _reportMissingRootWS(int ch) throws JsonParseException {
/* 684 */     _reportUnexpectedChar(ch, "Expected space separating root-level values");
/*     */   }
/*     */   
/*     */   protected void _throwInvalidSpace(int i) throws JsonParseException {
/* 688 */     char c = (char)i;
/* 689 */     String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
/* 690 */     _reportError(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String _getCharDesc(int ch) {
/* 701 */     char c = (char)ch;
/* 702 */     if (Character.isISOControl(c)) {
/* 703 */       return "(CTRL-CHAR, code " + ch + ")";
/*     */     }
/* 705 */     if (ch > 255) {
/* 706 */       return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
/*     */     }
/* 708 */     return "'" + c + "' (code " + ch + ")";
/*     */   }
/*     */   
/*     */   protected final void _reportError(String msg) throws JsonParseException {
/* 712 */     throw _constructError(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void _reportError(String msg, Object arg) throws JsonParseException {
/* 717 */     throw _constructError(String.format(msg, new Object[] { arg }));
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void _reportError(String msg, Object arg1, Object arg2) throws JsonParseException {
/* 722 */     throw _constructError(String.format(msg, new Object[] { arg1, arg2 }));
/*     */   }
/*     */   
/*     */   protected final void _wrapError(String msg, Throwable t) throws JsonParseException {
/* 726 */     throw _constructError(msg, t);
/*     */   }
/*     */   
/*     */   protected final void _throwInternal() {
/* 730 */     VersionUtil.throwInternal();
/*     */   }
/*     */   
/*     */   protected final JsonParseException _constructError(String msg, Throwable t) {
/* 734 */     return new JsonParseException(this, msg, t);
/*     */   }
/*     */   
/*     */   protected static byte[] _asciiBytes(String str) {
/* 738 */     byte[] b = new byte[str.length()];
/* 739 */     for (int i = 0, len = str.length(); i < len; i++) {
/* 740 */       b[i] = (byte)str.charAt(i);
/*     */     }
/* 742 */     return b;
/*     */   }
/*     */   
/*     */   protected static String _ascii(byte[] b) {
/*     */     try {
/* 747 */       return new String(b, "US-ASCII");
/* 748 */     } catch (IOException e) {
/* 749 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\base\ParserMinimalBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */