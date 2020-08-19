/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonGenerationException;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.NumberOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ public class UTF8JsonGenerator
/*      */   extends JsonGeneratorImpl {
/*      */   private static final byte BYTE_u = 117;
/*      */   private static final byte BYTE_0 = 48;
/*      */   private static final byte BYTE_LBRACKET = 91;
/*      */   private static final byte BYTE_RBRACKET = 93;
/*      */   private static final byte BYTE_LCURLY = 123;
/*      */   private static final byte BYTE_RCURLY = 125;
/*      */   private static final byte BYTE_BACKSLASH = 92;
/*      */   private static final byte BYTE_COMMA = 44;
/*      */   private static final byte BYTE_COLON = 58;
/*      */   private static final int MAX_BYTES_TO_BUFFER = 512;
/*   32 */   private static final byte[] HEX_CHARS = CharTypes.copyHexBytes();
/*      */   
/*   34 */   private static final byte[] NULL_BYTES = new byte[] { 110, 117, 108, 108 };
/*   35 */   private static final byte[] TRUE_BYTES = new byte[] { 116, 114, 117, 101 };
/*   36 */   private static final byte[] FALSE_BYTES = new byte[] { 102, 97, 108, 115, 101 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final OutputStream _outputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _outputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _outputTail;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _outputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _outputMaxContiguous;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _charBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _charBufferLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _entityBuffer;
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
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar) {
/*  123 */     super(ctxt, features, codec);
/*  124 */     this._outputStream = out;
/*  125 */     this._quoteChar = (byte)quoteChar;
/*  126 */     if (quoteChar != '"') {
/*  127 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
/*      */     }
/*      */     
/*  130 */     this._bufferRecyclable = true;
/*  131 */     this._outputBuffer = ctxt.allocWriteEncodingBuffer();
/*  132 */     this._outputEnd = this._outputBuffer.length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  138 */     this._outputMaxContiguous = this._outputEnd >> 3;
/*  139 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  140 */     this._charBufferLength = this._charBuffer.length;
/*      */ 
/*      */     
/*  143 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  144 */       setHighestNonEscapedChar(127);
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
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
/*  156 */     super(ctxt, features, codec);
/*  157 */     this._outputStream = out;
/*  158 */     this._quoteChar = (byte)quoteChar;
/*  159 */     if (quoteChar != '"') {
/*  160 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
/*      */     }
/*      */     
/*  163 */     this._bufferRecyclable = bufferRecyclable;
/*  164 */     this._outputTail = outputOffset;
/*  165 */     this._outputBuffer = outputBuffer;
/*  166 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*  168 */     this._outputMaxContiguous = this._outputEnd >> 3;
/*  169 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  170 */     this._charBufferLength = this._charBuffer.length;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
/*  176 */     this(ctxt, features, codec, out, '"');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
/*  184 */     this(ctxt, features, codec, out, '"', outputBuffer, outputOffset, bufferRecyclable);
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
/*      */   public Object getOutputTarget() {
/*  196 */     return this._outputStream;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOutputBuffered() {
/*  202 */     return this._outputTail;
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
/*      */   public void writeFieldName(String name) throws IOException {
/*  214 */     if (this._cfgPrettyPrinter != null) {
/*  215 */       _writePPFieldName(name);
/*      */       return;
/*      */     } 
/*  218 */     int status = this._writeContext.writeFieldName(name);
/*  219 */     if (status == 4) {
/*  220 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  222 */     if (status == 1) {
/*  223 */       if (this._outputTail >= this._outputEnd) {
/*  224 */         _flushBuffer();
/*      */       }
/*  226 */       this._outputBuffer[this._outputTail++] = 44;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  231 */     if (this._cfgUnqNames) {
/*  232 */       _writeStringSegments(name, false);
/*      */       return;
/*      */     } 
/*  235 */     int len = name.length();
/*      */     
/*  237 */     if (len > this._charBufferLength) {
/*  238 */       _writeStringSegments(name, true);
/*      */       return;
/*      */     } 
/*  241 */     if (this._outputTail >= this._outputEnd) {
/*  242 */       _flushBuffer();
/*      */     }
/*  244 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  246 */     if (len <= this._outputMaxContiguous) {
/*  247 */       if (this._outputTail + len > this._outputEnd) {
/*  248 */         _flushBuffer();
/*      */       }
/*  250 */       _writeStringSegment(name, 0, len);
/*      */     } else {
/*  252 */       _writeStringSegments(name, 0, len);
/*      */     } 
/*      */     
/*  255 */     if (this._outputTail >= this._outputEnd) {
/*  256 */       _flushBuffer();
/*      */     }
/*  258 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  264 */     if (this._cfgPrettyPrinter != null) {
/*  265 */       _writePPFieldName(name);
/*      */       return;
/*      */     } 
/*  268 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  269 */     if (status == 4) {
/*  270 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  272 */     if (status == 1) {
/*  273 */       if (this._outputTail >= this._outputEnd) {
/*  274 */         _flushBuffer();
/*      */       }
/*  276 */       this._outputBuffer[this._outputTail++] = 44;
/*      */     } 
/*  278 */     if (this._cfgUnqNames) {
/*  279 */       _writeUnq(name);
/*      */       return;
/*      */     } 
/*  282 */     if (this._outputTail >= this._outputEnd) {
/*  283 */       _flushBuffer();
/*      */     }
/*  285 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  286 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  287 */     if (len < 0) {
/*  288 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  290 */       this._outputTail += len;
/*      */     } 
/*  292 */     if (this._outputTail >= this._outputEnd) {
/*  293 */       _flushBuffer();
/*      */     }
/*  295 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */   
/*      */   private final void _writeUnq(SerializableString name) throws IOException {
/*  299 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  300 */     if (len < 0) {
/*  301 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  303 */       this._outputTail += len;
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
/*      */   public final void writeStartArray() throws IOException {
/*  316 */     _verifyValueWrite("start an array");
/*  317 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  318 */     if (this._cfgPrettyPrinter != null) {
/*  319 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  321 */       if (this._outputTail >= this._outputEnd) {
/*  322 */         _flushBuffer();
/*      */       }
/*  324 */       this._outputBuffer[this._outputTail++] = 91;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(int size) throws IOException {
/*  331 */     _verifyValueWrite("start an array");
/*  332 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  333 */     if (this._cfgPrettyPrinter != null) {
/*  334 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  336 */       if (this._outputTail >= this._outputEnd) {
/*  337 */         _flushBuffer();
/*      */       }
/*  339 */       this._outputBuffer[this._outputTail++] = 91;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndArray() throws IOException {
/*  346 */     if (!this._writeContext.inArray()) {
/*  347 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  349 */     if (this._cfgPrettyPrinter != null) {
/*  350 */       this._cfgPrettyPrinter.writeEndArray((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  352 */       if (this._outputTail >= this._outputEnd) {
/*  353 */         _flushBuffer();
/*      */       }
/*  355 */       this._outputBuffer[this._outputTail++] = 93;
/*      */     } 
/*  357 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeStartObject() throws IOException {
/*  363 */     _verifyValueWrite("start an object");
/*  364 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  365 */     if (this._cfgPrettyPrinter != null) {
/*  366 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  368 */       if (this._outputTail >= this._outputEnd) {
/*  369 */         _flushBuffer();
/*      */       }
/*  371 */       this._outputBuffer[this._outputTail++] = 123;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  378 */     _verifyValueWrite("start an object");
/*  379 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  380 */     this._writeContext = ctxt;
/*  381 */     if (this._cfgPrettyPrinter != null) {
/*  382 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  384 */       if (this._outputTail >= this._outputEnd) {
/*  385 */         _flushBuffer();
/*      */       }
/*  387 */       this._outputBuffer[this._outputTail++] = 123;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndObject() throws IOException {
/*  394 */     if (!this._writeContext.inObject()) {
/*  395 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  397 */     if (this._cfgPrettyPrinter != null) {
/*  398 */       this._cfgPrettyPrinter.writeEndObject((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  400 */       if (this._outputTail >= this._outputEnd) {
/*  401 */         _flushBuffer();
/*      */       }
/*  403 */       this._outputBuffer[this._outputTail++] = 125;
/*      */     } 
/*  405 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(String name) throws IOException {
/*  414 */     int status = this._writeContext.writeFieldName(name);
/*  415 */     if (status == 4) {
/*  416 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  418 */     if (status == 1) {
/*  419 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  421 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*  423 */     if (this._cfgUnqNames) {
/*  424 */       _writeStringSegments(name, false);
/*      */       return;
/*      */     } 
/*  427 */     int len = name.length();
/*  428 */     if (len > this._charBufferLength) {
/*  429 */       _writeStringSegments(name, true);
/*      */       return;
/*      */     } 
/*  432 */     if (this._outputTail >= this._outputEnd) {
/*  433 */       _flushBuffer();
/*      */     }
/*  435 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  436 */     name.getChars(0, len, this._charBuffer, 0);
/*      */     
/*  438 */     if (len <= this._outputMaxContiguous) {
/*  439 */       if (this._outputTail + len > this._outputEnd) {
/*  440 */         _flushBuffer();
/*      */       }
/*  442 */       _writeStringSegment(this._charBuffer, 0, len);
/*      */     } else {
/*  444 */       _writeStringSegments(this._charBuffer, 0, len);
/*      */     } 
/*  446 */     if (this._outputTail >= this._outputEnd) {
/*  447 */       _flushBuffer();
/*      */     }
/*  449 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name) throws IOException {
/*  454 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  455 */     if (status == 4) {
/*  456 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  458 */     if (status == 1) {
/*  459 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  461 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*      */     
/*  464 */     boolean addQuotes = !this._cfgUnqNames;
/*  465 */     if (addQuotes) {
/*  466 */       if (this._outputTail >= this._outputEnd) {
/*  467 */         _flushBuffer();
/*      */       }
/*  469 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*  471 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  472 */     if (len < 0) {
/*  473 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  475 */       this._outputTail += len;
/*      */     } 
/*  477 */     if (addQuotes) {
/*  478 */       if (this._outputTail >= this._outputEnd) {
/*  479 */         _flushBuffer();
/*      */       }
/*  481 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   public void writeString(String text) throws IOException {
/*  494 */     _verifyValueWrite("write a string");
/*  495 */     if (text == null) {
/*  496 */       _writeNull();
/*      */       
/*      */       return;
/*      */     } 
/*  500 */     int len = text.length();
/*  501 */     if (len > this._outputMaxContiguous) {
/*  502 */       _writeStringSegments(text, true);
/*      */       return;
/*      */     } 
/*  505 */     if (this._outputTail + len >= this._outputEnd) {
/*  506 */       _flushBuffer();
/*      */     }
/*  508 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  509 */     _writeStringSegment(text, 0, len);
/*  510 */     if (this._outputTail >= this._outputEnd) {
/*  511 */       _flushBuffer();
/*      */     }
/*  513 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  518 */     _verifyValueWrite("write a string");
/*  519 */     if (reader == null) {
/*  520 */       _reportError("null reader");
/*      */     }
/*      */     
/*  523 */     int toRead = (len >= 0) ? len : Integer.MAX_VALUE;
/*      */     
/*  525 */     char[] buf = this._charBuffer;
/*      */ 
/*      */     
/*  528 */     if (this._outputTail >= this._outputEnd) {
/*  529 */       _flushBuffer();
/*      */     }
/*  531 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */ 
/*      */     
/*  534 */     while (toRead > 0) {
/*  535 */       int toReadNow = Math.min(toRead, buf.length);
/*  536 */       int numRead = reader.read(buf, 0, toReadNow);
/*  537 */       if (numRead <= 0) {
/*      */         break;
/*      */       }
/*  540 */       if (this._outputTail + len >= this._outputEnd) {
/*  541 */         _flushBuffer();
/*      */       }
/*  543 */       _writeStringSegments(buf, 0, numRead);
/*      */       
/*  545 */       toRead -= numRead;
/*      */     } 
/*      */ 
/*      */     
/*  549 */     if (this._outputTail >= this._outputEnd) {
/*  550 */       _flushBuffer();
/*      */     }
/*  552 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  554 */     if (toRead > 0 && len >= 0) {
/*  555 */       _reportError("Didn't read enough from reader");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  562 */     _verifyValueWrite("write a string");
/*  563 */     if (this._outputTail >= this._outputEnd) {
/*  564 */       _flushBuffer();
/*      */     }
/*  566 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  568 */     if (len <= this._outputMaxContiguous) {
/*  569 */       if (this._outputTail + len > this._outputEnd) {
/*  570 */         _flushBuffer();
/*      */       }
/*  572 */       _writeStringSegment(text, offset, len);
/*      */     } else {
/*  574 */       _writeStringSegments(text, offset, len);
/*      */     } 
/*      */     
/*  577 */     if (this._outputTail >= this._outputEnd) {
/*  578 */       _flushBuffer();
/*      */     }
/*  580 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeString(SerializableString text) throws IOException {
/*  586 */     _verifyValueWrite("write a string");
/*  587 */     if (this._outputTail >= this._outputEnd) {
/*  588 */       _flushBuffer();
/*      */     }
/*  590 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  591 */     int len = text.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  592 */     if (len < 0) {
/*  593 */       _writeBytes(text.asQuotedUTF8());
/*      */     } else {
/*  595 */       this._outputTail += len;
/*      */     } 
/*  597 */     if (this._outputTail >= this._outputEnd) {
/*  598 */       _flushBuffer();
/*      */     }
/*  600 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  606 */     _verifyValueWrite("write a string");
/*  607 */     if (this._outputTail >= this._outputEnd) {
/*  608 */       _flushBuffer();
/*      */     }
/*  610 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  611 */     _writeBytes(text, offset, length);
/*  612 */     if (this._outputTail >= this._outputEnd) {
/*  613 */       _flushBuffer();
/*      */     }
/*  615 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
/*  621 */     _verifyValueWrite("write a string");
/*  622 */     if (this._outputTail >= this._outputEnd) {
/*  623 */       _flushBuffer();
/*      */     }
/*  625 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  627 */     if (len <= this._outputMaxContiguous) {
/*  628 */       _writeUTF8Segment(text, offset, len);
/*      */     } else {
/*  630 */       _writeUTF8Segments(text, offset, len);
/*      */     } 
/*  632 */     if (this._outputTail >= this._outputEnd) {
/*  633 */       _flushBuffer();
/*      */     }
/*  635 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text) throws IOException {
/*  646 */     int len = text.length();
/*  647 */     char[] buf = this._charBuffer;
/*  648 */     if (len <= buf.length) {
/*  649 */       text.getChars(0, len, buf, 0);
/*  650 */       writeRaw(buf, 0, len);
/*      */     } else {
/*  652 */       writeRaw(text, 0, len);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException {
/*  659 */     char[] buf = this._charBuffer;
/*  660 */     int cbufLen = buf.length;
/*      */ 
/*      */     
/*  663 */     if (len <= cbufLen) {
/*  664 */       text.getChars(offset, offset + len, buf, 0);
/*  665 */       writeRaw(buf, 0, len);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*  672 */     int maxChunk = Math.min(cbufLen, (this._outputEnd >> 2) + (this._outputEnd >> 4));
/*      */     
/*  674 */     int maxBytes = maxChunk * 3;
/*      */     
/*  676 */     while (len > 0) {
/*  677 */       int len2 = Math.min(maxChunk, len);
/*  678 */       text.getChars(offset, offset + len2, buf, 0);
/*  679 */       if (this._outputTail + maxBytes > this._outputEnd) {
/*  680 */         _flushBuffer();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  688 */       if (len2 > 1) {
/*  689 */         char ch = buf[len2 - 1];
/*  690 */         if (ch >= '?' && ch <= '?') {
/*  691 */           len2--;
/*      */         }
/*      */       } 
/*  694 */       _writeRawSegment(buf, 0, len2);
/*  695 */       offset += len2;
/*  696 */       len -= len2;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  703 */     int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
/*  704 */     if (len < 0) {
/*  705 */       _writeBytes(text.asUnquotedUTF8());
/*      */     } else {
/*  707 */       this._outputTail += len;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(SerializableString text) throws IOException {
/*  714 */     _verifyValueWrite("write a raw (unencoded) value");
/*  715 */     int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
/*  716 */     if (len < 0) {
/*  717 */       _writeBytes(text.asUnquotedUTF8());
/*      */     } else {
/*  719 */       this._outputTail += len;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeRaw(char[] cbuf, int offset, int len) throws IOException {
/*  729 */     int len3 = len + len + len;
/*  730 */     if (this._outputTail + len3 > this._outputEnd) {
/*      */       
/*  732 */       if (this._outputEnd < len3) {
/*  733 */         _writeSegmentedRaw(cbuf, offset, len);
/*      */         
/*      */         return;
/*      */       } 
/*  737 */       _flushBuffer();
/*      */     } 
/*      */     
/*  740 */     len += offset;
/*      */ 
/*      */ 
/*      */     
/*  744 */     while (offset < len) {
/*      */       
/*      */       while (true) {
/*  747 */         int ch = cbuf[offset];
/*  748 */         if (ch > 127) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  756 */           ch = cbuf[offset++];
/*  757 */           if (ch < 2048) {
/*  758 */             this._outputBuffer[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  759 */             this._outputBuffer[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  761 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
/*      */           continue;
/*      */         } 
/*      */         this._outputBuffer[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= len)
/*      */           break; 
/*      */       } 
/*      */     }  } public void writeRaw(char ch) throws IOException {
/*  769 */     if (this._outputTail + 3 >= this._outputEnd) {
/*  770 */       _flushBuffer();
/*      */     }
/*  772 */     byte[] bbuf = this._outputBuffer;
/*  773 */     if (ch <= '') {
/*  774 */       bbuf[this._outputTail++] = (byte)ch;
/*  775 */     } else if (ch < 'ࠀ') {
/*  776 */       bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  777 */       bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*      */     } else {
/*  779 */       _outputRawMultiByteChar(ch, (char[])null, 0, 0);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeSegmentedRaw(char[] cbuf, int offset, int len) throws IOException {
/*  789 */     int end = this._outputEnd;
/*  790 */     byte[] bbuf = this._outputBuffer;
/*  791 */     int inputEnd = offset + len;
/*      */ 
/*      */     
/*  794 */     while (offset < inputEnd) {
/*      */       
/*      */       while (true) {
/*  797 */         int ch = cbuf[offset];
/*  798 */         if (ch >= 128) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  810 */           if (this._outputTail + 3 >= this._outputEnd) {
/*  811 */             _flushBuffer();
/*      */           }
/*  813 */           ch = cbuf[offset++];
/*  814 */           if (ch < 2048) {
/*  815 */             bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  816 */             bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  818 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, inputEnd);
/*      */           continue;
/*      */         } 
/*      */         if (this._outputTail >= end) {
/*      */           _flushBuffer();
/*      */         }
/*      */         bbuf[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= inputEnd) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeRawSegment(char[] cbuf, int offset, int end) throws IOException {
/*  835 */     while (offset < end) {
/*      */       
/*      */       while (true) {
/*  838 */         int ch = cbuf[offset];
/*  839 */         if (ch > 127) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  847 */           ch = cbuf[offset++];
/*  848 */           if (ch < 2048) {
/*  849 */             this._outputBuffer[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  850 */             this._outputBuffer[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  852 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, end);
/*      */           continue;
/*      */         } 
/*      */         this._outputBuffer[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
/*  868 */     _verifyValueWrite("write a binary value");
/*      */     
/*  870 */     if (this._outputTail >= this._outputEnd) {
/*  871 */       _flushBuffer();
/*      */     }
/*  873 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  874 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  876 */     if (this._outputTail >= this._outputEnd) {
/*  877 */       _flushBuffer();
/*      */     }
/*  879 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
/*      */     int bytes;
/*  887 */     _verifyValueWrite("write a binary value");
/*      */     
/*  889 */     if (this._outputTail >= this._outputEnd) {
/*  890 */       _flushBuffer();
/*      */     }
/*  892 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  893 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     
/*      */     try {
/*  896 */       if (dataLength < 0) {
/*  897 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  899 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  900 */         if (missing > 0) {
/*  901 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  903 */         bytes = dataLength;
/*      */       } 
/*      */     } finally {
/*  906 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     } 
/*      */     
/*  909 */     if (this._outputTail >= this._outputEnd) {
/*  910 */       _flushBuffer();
/*      */     }
/*  912 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  913 */     return bytes;
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
/*      */   public void writeNumber(short s) throws IOException {
/*  925 */     _verifyValueWrite("write a number");
/*      */     
/*  927 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  928 */       _flushBuffer();
/*      */     }
/*  930 */     if (this._cfgNumbersAsStrings) {
/*  931 */       _writeQuotedShort(s);
/*      */       return;
/*      */     } 
/*  934 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedShort(short s) throws IOException {
/*  938 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  939 */       _flushBuffer();
/*      */     }
/*  941 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  942 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  943 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(int i) throws IOException {
/*  949 */     _verifyValueWrite("write a number");
/*      */     
/*  951 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  952 */       _flushBuffer();
/*      */     }
/*  954 */     if (this._cfgNumbersAsStrings) {
/*  955 */       _writeQuotedInt(i);
/*      */       return;
/*      */     } 
/*  958 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedInt(int i) throws IOException {
/*  963 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  964 */       _flushBuffer();
/*      */     }
/*  966 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  967 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  968 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(long l) throws IOException {
/*  974 */     _verifyValueWrite("write a number");
/*  975 */     if (this._cfgNumbersAsStrings) {
/*  976 */       _writeQuotedLong(l);
/*      */       return;
/*      */     } 
/*  979 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  981 */       _flushBuffer();
/*      */     }
/*  983 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedLong(long l) throws IOException {
/*  988 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  989 */       _flushBuffer();
/*      */     }
/*  991 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  992 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  993 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger value) throws IOException {
/*  999 */     _verifyValueWrite("write a number");
/* 1000 */     if (value == null) {
/* 1001 */       _writeNull();
/* 1002 */     } else if (this._cfgNumbersAsStrings) {
/* 1003 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/* 1005 */       writeRaw(value.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(double d) throws IOException {
/* 1013 */     if (this._cfgNumbersAsStrings || (
/* 1014 */       NumberOutput.notFinite(d) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
/* 1015 */       .enabledIn(this._features))) {
/* 1016 */       writeString(String.valueOf(d));
/*      */       
/*      */       return;
/*      */     } 
/* 1020 */     _verifyValueWrite("write a number");
/* 1021 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(float f) throws IOException {
/* 1028 */     if (this._cfgNumbersAsStrings || (
/* 1029 */       NumberOutput.notFinite(f) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
/* 1030 */       .enabledIn(this._features))) {
/* 1031 */       writeString(String.valueOf(f));
/*      */       
/*      */       return;
/*      */     } 
/* 1035 */     _verifyValueWrite("write a number");
/* 1036 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal value) throws IOException {
/* 1043 */     _verifyValueWrite("write a number");
/* 1044 */     if (value == null) {
/* 1045 */       _writeNull();
/* 1046 */     } else if (this._cfgNumbersAsStrings) {
/* 1047 */       _writeQuotedRaw(_asString(value));
/*      */     } else {
/* 1049 */       writeRaw(_asString(value));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException {
/* 1056 */     _verifyValueWrite("write a number");
/* 1057 */     if (this._cfgNumbersAsStrings) {
/* 1058 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/* 1060 */       writeRaw(encodedValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedRaw(String value) throws IOException {
/* 1066 */     if (this._outputTail >= this._outputEnd) {
/* 1067 */       _flushBuffer();
/*      */     }
/* 1069 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/* 1070 */     writeRaw(value);
/* 1071 */     if (this._outputTail >= this._outputEnd) {
/* 1072 */       _flushBuffer();
/*      */     }
/* 1074 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException {
/* 1080 */     _verifyValueWrite("write a boolean value");
/* 1081 */     if (this._outputTail + 5 >= this._outputEnd) {
/* 1082 */       _flushBuffer();
/*      */     }
/* 1084 */     byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
/* 1085 */     int len = keyword.length;
/* 1086 */     System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
/* 1087 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/* 1093 */     _verifyValueWrite("write a null");
/* 1094 */     _writeNull();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyValueWrite(String typeMsg) throws IOException {
/*      */     byte b;
/* 1106 */     int status = this._writeContext.writeValue();
/* 1107 */     if (this._cfgPrettyPrinter != null) {
/*      */       
/* 1109 */       _verifyPrettyValueWrite(typeMsg, status);
/*      */       
/*      */       return;
/*      */     } 
/* 1113 */     switch (status) {
/*      */       default:
/*      */         return;
/*      */       
/*      */       case 1:
/* 1118 */         b = 44;
/*      */         break;
/*      */       case 2:
/* 1121 */         b = 58;
/*      */         break;
/*      */       case 3:
/* 1124 */         if (this._rootValueSeparator != null) {
/* 1125 */           byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
/* 1126 */           if (raw.length > 0) {
/* 1127 */             _writeBytes(raw);
/*      */           }
/*      */         } 
/*      */         return;
/*      */       case 5:
/* 1132 */         _reportCantWriteValueExpectName(typeMsg);
/*      */         return;
/*      */     } 
/* 1135 */     if (this._outputTail >= this._outputEnd) {
/* 1136 */       _flushBuffer();
/*      */     }
/* 1138 */     this._outputBuffer[this._outputTail++] = b;
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
/*      */   public void flush() throws IOException {
/* 1150 */     _flushBuffer();
/* 1151 */     if (this._outputStream != null && 
/* 1152 */       isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/* 1153 */       this._outputStream.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/* 1161 */     super.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1167 */     if (this._outputBuffer != null && 
/* 1168 */       isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
/*      */       while (true) {
/* 1170 */         JsonStreamContext ctxt = getOutputContext();
/* 1171 */         if (ctxt.inArray()) {
/* 1172 */           writeEndArray(); continue;
/* 1173 */         }  if (ctxt.inObject()) {
/* 1174 */           writeEndObject();
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/*      */     }
/* 1180 */     _flushBuffer();
/* 1181 */     this._outputTail = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1189 */     if (this._outputStream != null) {
/* 1190 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
/* 1191 */         this._outputStream.close();
/* 1192 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*      */         
/* 1194 */         this._outputStream.flush();
/*      */       } 
/*      */     }
/*      */     
/* 1198 */     _releaseBuffers();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() {
/* 1204 */     byte[] buf = this._outputBuffer;
/* 1205 */     if (buf != null && this._bufferRecyclable) {
/* 1206 */       this._outputBuffer = null;
/* 1207 */       this._ioContext.releaseWriteEncodingBuffer(buf);
/*      */     } 
/* 1209 */     char[] cbuf = this._charBuffer;
/* 1210 */     if (cbuf != null) {
/* 1211 */       this._charBuffer = null;
/* 1212 */       this._ioContext.releaseConcatBuffer(cbuf);
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
/*      */   private final void _writeBytes(byte[] bytes) throws IOException {
/* 1224 */     int len = bytes.length;
/* 1225 */     if (this._outputTail + len > this._outputEnd) {
/* 1226 */       _flushBuffer();
/*      */       
/* 1228 */       if (len > 512) {
/* 1229 */         this._outputStream.write(bytes, 0, len);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1233 */     System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
/* 1234 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException {
/* 1239 */     if (this._outputTail + len > this._outputEnd) {
/* 1240 */       _flushBuffer();
/*      */       
/* 1242 */       if (len > 512) {
/* 1243 */         this._outputStream.write(bytes, offset, len);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1247 */     System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
/* 1248 */     this._outputTail += len;
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
/*      */   private final void _writeStringSegments(String text, boolean addQuotes) throws IOException {
/* 1266 */     if (addQuotes) {
/* 1267 */       if (this._outputTail >= this._outputEnd) {
/* 1268 */         _flushBuffer();
/*      */       }
/* 1270 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*      */     
/* 1273 */     int left = text.length();
/* 1274 */     int offset = 0;
/*      */     
/* 1276 */     while (left > 0) {
/* 1277 */       int len = Math.min(this._outputMaxContiguous, left);
/* 1278 */       if (this._outputTail + len > this._outputEnd) {
/* 1279 */         _flushBuffer();
/*      */       }
/* 1281 */       _writeStringSegment(text, offset, len);
/* 1282 */       offset += len;
/* 1283 */       left -= len;
/*      */     } 
/*      */     
/* 1286 */     if (addQuotes) {
/* 1287 */       if (this._outputTail >= this._outputEnd) {
/* 1288 */         _flushBuffer();
/*      */       }
/* 1290 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException {
/*      */     do {
/* 1303 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1304 */       if (this._outputTail + len > this._outputEnd) {
/* 1305 */         _flushBuffer();
/*      */       }
/* 1307 */       _writeStringSegment(cbuf, offset, len);
/* 1308 */       offset += len;
/* 1309 */       totalLen -= len;
/* 1310 */     } while (totalLen > 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeStringSegments(String text, int offset, int totalLen) throws IOException {
/*      */     do {
/* 1316 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1317 */       if (this._outputTail + len > this._outputEnd) {
/* 1318 */         _flushBuffer();
/*      */       }
/* 1320 */       _writeStringSegment(text, offset, len);
/* 1321 */       offset += len;
/* 1322 */       totalLen -= len;
/* 1323 */     } while (totalLen > 0);
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
/*      */   private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException {
/* 1346 */     len += offset;
/*      */     
/* 1348 */     int outputPtr = this._outputTail;
/* 1349 */     byte[] outputBuffer = this._outputBuffer;
/* 1350 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1352 */     while (offset < len) {
/* 1353 */       int ch = cbuf[offset];
/*      */       
/* 1355 */       if (ch > 127 || escCodes[ch] != 0) {
/*      */         break;
/*      */       }
/* 1358 */       outputBuffer[outputPtr++] = (byte)ch;
/* 1359 */       offset++;
/*      */     } 
/* 1361 */     this._outputTail = outputPtr;
/* 1362 */     if (offset < len) {
/* 1363 */       if (this._characterEscapes != null) {
/* 1364 */         _writeCustomStringSegment2(cbuf, offset, len);
/* 1365 */       } else if (this._maximumNonEscapedChar == 0) {
/* 1366 */         _writeStringSegment2(cbuf, offset, len);
/*      */       } else {
/* 1368 */         _writeStringSegmentASCII2(cbuf, offset, len);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeStringSegment(String text, int offset, int len) throws IOException {
/* 1378 */     len += offset;
/*      */     
/* 1380 */     int outputPtr = this._outputTail;
/* 1381 */     byte[] outputBuffer = this._outputBuffer;
/* 1382 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1384 */     while (offset < len) {
/* 1385 */       int ch = text.charAt(offset);
/*      */       
/* 1387 */       if (ch > 127 || escCodes[ch] != 0) {
/*      */         break;
/*      */       }
/* 1390 */       outputBuffer[outputPtr++] = (byte)ch;
/* 1391 */       offset++;
/*      */     } 
/* 1393 */     this._outputTail = outputPtr;
/* 1394 */     if (offset < len) {
/* 1395 */       if (this._characterEscapes != null) {
/* 1396 */         _writeCustomStringSegment2(text, offset, len);
/* 1397 */       } else if (this._maximumNonEscapedChar == 0) {
/* 1398 */         _writeStringSegment2(text, offset, len);
/*      */       } else {
/* 1400 */         _writeStringSegmentASCII2(text, offset, len);
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
/*      */   private final void _writeStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/* 1412 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1413 */       _flushBuffer();
/*      */     }
/*      */     
/* 1416 */     int outputPtr = this._outputTail;
/*      */     
/* 1418 */     byte[] outputBuffer = this._outputBuffer;
/* 1419 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1421 */     while (offset < end) {
/* 1422 */       int ch = cbuf[offset++];
/* 1423 */       if (ch <= 127) {
/* 1424 */         if (escCodes[ch] == 0) {
/* 1425 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1428 */         int escape = escCodes[ch];
/* 1429 */         if (escape > 0) {
/* 1430 */           outputBuffer[outputPtr++] = 92;
/* 1431 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1434 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1438 */       if (ch <= 2047) {
/* 1439 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1440 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1442 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1445 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeStringSegment2(String text, int offset, int end) throws IOException {
/* 1450 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1451 */       _flushBuffer();
/*      */     }
/*      */     
/* 1454 */     int outputPtr = this._outputTail;
/*      */     
/* 1456 */     byte[] outputBuffer = this._outputBuffer;
/* 1457 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1459 */     while (offset < end) {
/* 1460 */       int ch = text.charAt(offset++);
/* 1461 */       if (ch <= 127) {
/* 1462 */         if (escCodes[ch] == 0) {
/* 1463 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1466 */         int escape = escCodes[ch];
/* 1467 */         if (escape > 0) {
/* 1468 */           outputBuffer[outputPtr++] = 92;
/* 1469 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1472 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1476 */       if (ch <= 2047) {
/* 1477 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1478 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1480 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1483 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end) throws IOException {
/* 1500 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1501 */       _flushBuffer();
/*      */     }
/*      */     
/* 1504 */     int outputPtr = this._outputTail;
/*      */     
/* 1506 */     byte[] outputBuffer = this._outputBuffer;
/* 1507 */     int[] escCodes = this._outputEscapes;
/* 1508 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1510 */     while (offset < end) {
/* 1511 */       int ch = cbuf[offset++];
/* 1512 */       if (ch <= 127) {
/* 1513 */         if (escCodes[ch] == 0) {
/* 1514 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1517 */         int escape = escCodes[ch];
/* 1518 */         if (escape > 0) {
/* 1519 */           outputBuffer[outputPtr++] = 92;
/* 1520 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1523 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1527 */       if (ch > maxUnescaped) {
/* 1528 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1531 */       if (ch <= 2047) {
/* 1532 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1533 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1535 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1538 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeStringSegmentASCII2(String text, int offset, int end) throws IOException {
/* 1544 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1545 */       _flushBuffer();
/*      */     }
/*      */     
/* 1548 */     int outputPtr = this._outputTail;
/*      */     
/* 1550 */     byte[] outputBuffer = this._outputBuffer;
/* 1551 */     int[] escCodes = this._outputEscapes;
/* 1552 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1554 */     while (offset < end) {
/* 1555 */       int ch = text.charAt(offset++);
/* 1556 */       if (ch <= 127) {
/* 1557 */         if (escCodes[ch] == 0) {
/* 1558 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1561 */         int escape = escCodes[ch];
/* 1562 */         if (escape > 0) {
/* 1563 */           outputBuffer[outputPtr++] = 92;
/* 1564 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1567 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1571 */       if (ch > maxUnescaped) {
/* 1572 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1575 */       if (ch <= 2047) {
/* 1576 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1577 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1579 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1582 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/* 1599 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1600 */       _flushBuffer();
/*      */     }
/* 1602 */     int outputPtr = this._outputTail;
/*      */     
/* 1604 */     byte[] outputBuffer = this._outputBuffer;
/* 1605 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1607 */     int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/* 1608 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1610 */     while (offset < end) {
/* 1611 */       int ch = cbuf[offset++];
/* 1612 */       if (ch <= 127) {
/* 1613 */         if (escCodes[ch] == 0) {
/* 1614 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1617 */         int escape = escCodes[ch];
/* 1618 */         if (escape > 0) {
/* 1619 */           outputBuffer[outputPtr++] = 92;
/* 1620 */           outputBuffer[outputPtr++] = (byte)escape; continue;
/* 1621 */         }  if (escape == -2) {
/* 1622 */           SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/* 1623 */           if (serializableString == null) {
/* 1624 */             _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + 
/* 1625 */                 Integer.toHexString(ch) + ", although was supposed to have one");
/*      */           }
/* 1627 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*      */           continue;
/*      */         } 
/* 1630 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1634 */       if (ch > maxUnescaped) {
/* 1635 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1638 */       SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1639 */       if (esc != null) {
/* 1640 */         outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */         continue;
/*      */       } 
/* 1643 */       if (ch <= 2047) {
/* 1644 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1645 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1647 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1650 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeCustomStringSegment2(String text, int offset, int end) throws IOException {
/* 1656 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1657 */       _flushBuffer();
/*      */     }
/* 1659 */     int outputPtr = this._outputTail;
/*      */     
/* 1661 */     byte[] outputBuffer = this._outputBuffer;
/* 1662 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1664 */     int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/* 1665 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1667 */     while (offset < end) {
/* 1668 */       int ch = text.charAt(offset++);
/* 1669 */       if (ch <= 127) {
/* 1670 */         if (escCodes[ch] == 0) {
/* 1671 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1674 */         int escape = escCodes[ch];
/* 1675 */         if (escape > 0) {
/* 1676 */           outputBuffer[outputPtr++] = 92;
/* 1677 */           outputBuffer[outputPtr++] = (byte)escape; continue;
/* 1678 */         }  if (escape == -2) {
/* 1679 */           SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/* 1680 */           if (serializableString == null) {
/* 1681 */             _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + 
/* 1682 */                 Integer.toHexString(ch) + ", although was supposed to have one");
/*      */           }
/* 1684 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*      */           continue;
/*      */         } 
/* 1687 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1691 */       if (ch > maxUnescaped) {
/* 1692 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1695 */       SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1696 */       if (esc != null) {
/* 1697 */         outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */         continue;
/*      */       } 
/* 1700 */       if (ch <= 2047) {
/* 1701 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1702 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1704 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1707 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
/* 1713 */     byte[] raw = esc.asUnquotedUTF8();
/* 1714 */     int len = raw.length;
/* 1715 */     if (len > 6) {
/* 1716 */       return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars);
/*      */     }
/*      */     
/* 1719 */     System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1720 */     return outputPtr + len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars) throws IOException, JsonGenerationException {
/* 1727 */     int len = raw.length;
/* 1728 */     if (outputPtr + len > outputEnd) {
/* 1729 */       this._outputTail = outputPtr;
/* 1730 */       _flushBuffer();
/* 1731 */       outputPtr = this._outputTail;
/* 1732 */       if (len > outputBuffer.length) {
/* 1733 */         this._outputStream.write(raw, 0, len);
/* 1734 */         return outputPtr;
/*      */       } 
/* 1736 */       System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1737 */       outputPtr += len;
/*      */     } 
/*      */     
/* 1740 */     if (outputPtr + 6 * remainingChars > outputEnd) {
/* 1741 */       _flushBuffer();
/* 1742 */       return this._outputTail;
/*      */     } 
/* 1744 */     return outputPtr;
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
/*      */   private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen) throws IOException, JsonGenerationException {
/*      */     do {
/* 1762 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1763 */       _writeUTF8Segment(utf8, offset, len);
/* 1764 */       offset += len;
/* 1765 */       totalLen -= len;
/* 1766 */     } while (totalLen > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeUTF8Segment(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/* 1773 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1775 */     for (int ptr = offset, end = offset + len; ptr < end; ) {
/*      */       
/* 1777 */       int ch = utf8[ptr++];
/* 1778 */       if (ch >= 0 && escCodes[ch] != 0) {
/* 1779 */         _writeUTF8Segment2(utf8, offset, len);
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/* 1785 */     if (this._outputTail + len > this._outputEnd) {
/* 1786 */       _flushBuffer();
/*      */     }
/* 1788 */     System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
/* 1789 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeUTF8Segment2(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/* 1795 */     int outputPtr = this._outputTail;
/*      */ 
/*      */     
/* 1798 */     if (outputPtr + len * 6 > this._outputEnd) {
/* 1799 */       _flushBuffer();
/* 1800 */       outputPtr = this._outputTail;
/*      */     } 
/*      */     
/* 1803 */     byte[] outputBuffer = this._outputBuffer;
/* 1804 */     int[] escCodes = this._outputEscapes;
/* 1805 */     len += offset;
/*      */     
/* 1807 */     while (offset < len) {
/* 1808 */       byte b = utf8[offset++];
/* 1809 */       int ch = b;
/* 1810 */       if (ch < 0 || escCodes[ch] == 0) {
/* 1811 */         outputBuffer[outputPtr++] = b;
/*      */         continue;
/*      */       } 
/* 1814 */       int escape = escCodes[ch];
/* 1815 */       if (escape > 0) {
/* 1816 */         outputBuffer[outputPtr++] = 92;
/* 1817 */         outputBuffer[outputPtr++] = (byte)escape;
/*      */         continue;
/*      */       } 
/* 1820 */       outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */     } 
/*      */     
/* 1823 */     this._outputTail = outputPtr;
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
/*      */   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
/* 1837 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1839 */     int safeOutputEnd = this._outputEnd - 6;
/* 1840 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/* 1843 */     while (inputPtr <= safeInputEnd) {
/* 1844 */       if (this._outputTail > safeOutputEnd) {
/* 1845 */         _flushBuffer();
/*      */       }
/*      */       
/* 1848 */       int b24 = input[inputPtr++] << 8;
/* 1849 */       b24 |= input[inputPtr++] & 0xFF;
/* 1850 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 1851 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1852 */       if (--chunksBeforeLF <= 0) {
/*      */         
/* 1854 */         this._outputBuffer[this._outputTail++] = 92;
/* 1855 */         this._outputBuffer[this._outputTail++] = 110;
/* 1856 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1861 */     int inputLeft = inputEnd - inputPtr;
/* 1862 */     if (inputLeft > 0) {
/* 1863 */       if (this._outputTail > safeOutputEnd) {
/* 1864 */         _flushBuffer();
/*      */       }
/* 1866 */       int b24 = input[inputPtr++] << 16;
/* 1867 */       if (inputLeft == 2) {
/* 1868 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*      */       }
/* 1870 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
/* 1879 */     int inputPtr = 0;
/* 1880 */     int inputEnd = 0;
/* 1881 */     int lastFullOffset = -3;
/*      */ 
/*      */     
/* 1884 */     int safeOutputEnd = this._outputEnd - 6;
/* 1885 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1887 */     while (bytesLeft > 2) {
/* 1888 */       if (inputPtr > lastFullOffset) {
/* 1889 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1890 */         inputPtr = 0;
/* 1891 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1894 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1896 */       if (this._outputTail > safeOutputEnd) {
/* 1897 */         _flushBuffer();
/*      */       }
/* 1899 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1900 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1901 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1902 */       bytesLeft -= 3;
/* 1903 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1904 */       if (--chunksBeforeLF <= 0) {
/* 1905 */         this._outputBuffer[this._outputTail++] = 92;
/* 1906 */         this._outputBuffer[this._outputTail++] = 110;
/* 1907 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1912 */     if (bytesLeft > 0) {
/* 1913 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1914 */       inputPtr = 0;
/* 1915 */       if (inputEnd > 0) {
/* 1916 */         int amount; if (this._outputTail > safeOutputEnd) {
/* 1917 */           _flushBuffer();
/*      */         }
/* 1919 */         int b24 = readBuffer[inputPtr++] << 16;
/*      */         
/* 1921 */         if (inputPtr < inputEnd) {
/* 1922 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1923 */           amount = 2;
/*      */         } else {
/* 1925 */           amount = 1;
/*      */         } 
/* 1927 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1928 */         bytesLeft -= amount;
/*      */       } 
/*      */     } 
/* 1931 */     return bytesLeft;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
/* 1939 */     int inputPtr = 0;
/* 1940 */     int inputEnd = 0;
/* 1941 */     int lastFullOffset = -3;
/* 1942 */     int bytesDone = 0;
/*      */ 
/*      */     
/* 1945 */     int safeOutputEnd = this._outputEnd - 6;
/* 1946 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1950 */       if (inputPtr > lastFullOffset) {
/* 1951 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1952 */         inputPtr = 0;
/* 1953 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1956 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1958 */       if (this._outputTail > safeOutputEnd) {
/* 1959 */         _flushBuffer();
/*      */       }
/*      */       
/* 1962 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1963 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1964 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1965 */       bytesDone += 3;
/* 1966 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1967 */       if (--chunksBeforeLF <= 0) {
/* 1968 */         this._outputBuffer[this._outputTail++] = 92;
/* 1969 */         this._outputBuffer[this._outputTail++] = 110;
/* 1970 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1975 */     if (inputPtr < inputEnd) {
/* 1976 */       if (this._outputTail > safeOutputEnd) {
/* 1977 */         _flushBuffer();
/*      */       }
/* 1979 */       int b24 = readBuffer[inputPtr++] << 16;
/* 1980 */       int amount = 1;
/* 1981 */       if (inputPtr < inputEnd) {
/* 1982 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1983 */         amount = 2;
/*      */       } 
/* 1985 */       bytesDone += amount;
/* 1986 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     } 
/* 1988 */     return bytesDone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
/* 1996 */     int i = 0;
/* 1997 */     while (inputPtr < inputEnd) {
/* 1998 */       readBuffer[i++] = readBuffer[inputPtr++];
/*      */     }
/* 2000 */     inputPtr = 0;
/* 2001 */     inputEnd = i;
/* 2002 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     
/*      */     do {
/* 2005 */       int length = maxRead - inputEnd;
/* 2006 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 2009 */       int count = in.read(readBuffer, inputEnd, length);
/* 2010 */       if (count < 0) {
/* 2011 */         return inputEnd;
/*      */       }
/* 2013 */       inputEnd += count;
/* 2014 */     } while (inputEnd < 3);
/* 2015 */     return inputEnd;
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
/*      */   private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputEnd) throws IOException {
/* 2033 */     if (ch >= 55296 && 
/* 2034 */       ch <= 57343) {
/*      */       
/* 2036 */       if (inputOffset >= inputEnd || cbuf == null)
/* 2037 */         _reportError(String.format("Split surrogate on writeRaw() input (last character): first character 0x%4x", new Object[] {
/* 2038 */                 Integer.valueOf(ch)
/*      */               })); 
/* 2040 */       _outputSurrogates(ch, cbuf[inputOffset]);
/* 2041 */       return inputOffset + 1;
/*      */     } 
/*      */     
/* 2044 */     byte[] bbuf = this._outputBuffer;
/* 2045 */     bbuf[this._outputTail++] = (byte)(0xE0 | ch >> 12);
/* 2046 */     bbuf[this._outputTail++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 2047 */     bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/* 2048 */     return inputOffset;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _outputSurrogates(int surr1, int surr2) throws IOException {
/* 2053 */     int c = _decodeSurrogate(surr1, surr2);
/* 2054 */     if (this._outputTail + 4 > this._outputEnd) {
/* 2055 */       _flushBuffer();
/*      */     }
/* 2057 */     byte[] bbuf = this._outputBuffer;
/* 2058 */     bbuf[this._outputTail++] = (byte)(0xF0 | c >> 18);
/* 2059 */     bbuf[this._outputTail++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 2060 */     bbuf[this._outputTail++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 2061 */     bbuf[this._outputTail++] = (byte)(0x80 | c & 0x3F);
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
/*      */   private final int _outputMultiByteChar(int ch, int outputPtr) throws IOException {
/* 2075 */     byte[] bbuf = this._outputBuffer;
/* 2076 */     if (ch >= 55296 && ch <= 57343) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2081 */       bbuf[outputPtr++] = 92;
/* 2082 */       bbuf[outputPtr++] = 117;
/*      */       
/* 2084 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 12 & 0xF];
/* 2085 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 8 & 0xF];
/* 2086 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 4 & 0xF];
/* 2087 */       bbuf[outputPtr++] = HEX_CHARS[ch & 0xF];
/*      */     } else {
/*      */       
/* 2090 */       bbuf[outputPtr++] = (byte)(0xE0 | ch >> 12);
/* 2091 */       bbuf[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 2092 */       bbuf[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*      */     } 
/* 2094 */     return outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeNull() throws IOException {
/* 2099 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 2100 */       _flushBuffer();
/*      */     }
/* 2102 */     System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
/* 2103 */     this._outputTail += 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
/* 2113 */     byte[] bbuf = this._outputBuffer;
/* 2114 */     bbuf[outputPtr++] = 92;
/* 2115 */     bbuf[outputPtr++] = 117;
/* 2116 */     if (charToEscape > 255) {
/* 2117 */       int hi = charToEscape >> 8 & 0xFF;
/* 2118 */       bbuf[outputPtr++] = HEX_CHARS[hi >> 4];
/* 2119 */       bbuf[outputPtr++] = HEX_CHARS[hi & 0xF];
/* 2120 */       charToEscape &= 0xFF;
/*      */     } else {
/* 2122 */       bbuf[outputPtr++] = 48;
/* 2123 */       bbuf[outputPtr++] = 48;
/*      */     } 
/*      */     
/* 2126 */     bbuf[outputPtr++] = HEX_CHARS[charToEscape >> 4];
/* 2127 */     bbuf[outputPtr++] = HEX_CHARS[charToEscape & 0xF];
/* 2128 */     return outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _flushBuffer() throws IOException {
/* 2133 */     int len = this._outputTail;
/* 2134 */     if (len > 0) {
/* 2135 */       this._outputTail = 0;
/* 2136 */       this._outputStream.write(this._outputBuffer, 0, len);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\UTF8JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */