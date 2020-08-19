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
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ public class WriterBasedJsonGenerator extends JsonGeneratorImpl {
/*      */   protected static final int SHORT_WRITE = 32;
/*   22 */   protected static final char[] HEX_CHARS = CharTypes.copyHexChars();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Writer _writer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _outputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _outputHead;
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
/*      */   protected int _outputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _entityBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SerializableString _currentEscape;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _copyBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
/*   96 */     this(ctxt, features, codec, w, '"');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w, char quoteChar) {
/*  107 */     super(ctxt, features, codec);
/*  108 */     this._writer = w;
/*  109 */     this._outputBuffer = ctxt.allocConcatBuffer();
/*  110 */     this._outputEnd = this._outputBuffer.length;
/*  111 */     this._quoteChar = quoteChar;
/*  112 */     if (quoteChar != '"') {
/*  113 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
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
/*      */   public Object getOutputTarget() {
/*  125 */     return this._writer;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOutputBuffered() {
/*  131 */     int len = this._outputTail - this._outputHead;
/*  132 */     return Math.max(0, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canWriteFormattedNumbers() {
/*  137 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(String name) throws IOException {
/*  148 */     int status = this._writeContext.writeFieldName(name);
/*  149 */     if (status == 4) {
/*  150 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  152 */     _writeFieldName(name, (status == 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  159 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  160 */     if (status == 4) {
/*  161 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  163 */     _writeFieldName(name, (status == 1));
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writeFieldName(String name, boolean commaBefore) throws IOException {
/*  168 */     if (this._cfgPrettyPrinter != null) {
/*  169 */       _writePPFieldName(name, commaBefore);
/*      */       
/*      */       return;
/*      */     } 
/*  173 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  174 */       _flushBuffer();
/*      */     }
/*  176 */     if (commaBefore) {
/*  177 */       this._outputBuffer[this._outputTail++] = ',';
/*      */     }
/*      */     
/*  180 */     if (this._cfgUnqNames) {
/*  181 */       _writeString(name);
/*      */       
/*      */       return;
/*      */     } 
/*  185 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  187 */     _writeString(name);
/*      */     
/*  189 */     if (this._outputTail >= this._outputEnd) {
/*  190 */       _flushBuffer();
/*      */     }
/*  192 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException {
/*  197 */     if (this._cfgPrettyPrinter != null) {
/*  198 */       _writePPFieldName(name, commaBefore);
/*      */       
/*      */       return;
/*      */     } 
/*  202 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  203 */       _flushBuffer();
/*      */     }
/*  205 */     if (commaBefore) {
/*  206 */       this._outputBuffer[this._outputTail++] = ',';
/*      */     }
/*      */     
/*  209 */     if (this._cfgUnqNames) {
/*  210 */       char[] ch = name.asQuotedChars();
/*  211 */       writeRaw(ch, 0, ch.length);
/*      */       
/*      */       return;
/*      */     } 
/*  215 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */ 
/*      */     
/*  218 */     int len = name.appendQuoted(this._outputBuffer, this._outputTail);
/*  219 */     if (len < 0) {
/*  220 */       _writeFieldNameTail(name);
/*      */       return;
/*      */     } 
/*  223 */     this._outputTail += len;
/*  224 */     if (this._outputTail >= this._outputEnd) {
/*  225 */       _flushBuffer();
/*      */     }
/*  227 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeFieldNameTail(SerializableString name) throws IOException {
/*  232 */     char[] quoted = name.asQuotedChars();
/*  233 */     writeRaw(quoted, 0, quoted.length);
/*  234 */     if (this._outputTail >= this._outputEnd) {
/*  235 */       _flushBuffer();
/*      */     }
/*  237 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   public void writeStartArray() throws IOException {
/*  249 */     _verifyValueWrite("start an array");
/*  250 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  251 */     if (this._cfgPrettyPrinter != null) {
/*  252 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  254 */       if (this._outputTail >= this._outputEnd) {
/*  255 */         _flushBuffer();
/*      */       }
/*  257 */       this._outputBuffer[this._outputTail++] = '[';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(int size) throws IOException {
/*  264 */     _verifyValueWrite("start an array");
/*  265 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  266 */     if (this._cfgPrettyPrinter != null) {
/*  267 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  269 */       if (this._outputTail >= this._outputEnd) {
/*  270 */         _flushBuffer();
/*      */       }
/*  272 */       this._outputBuffer[this._outputTail++] = '[';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndArray() throws IOException {
/*  279 */     if (!this._writeContext.inArray()) {
/*  280 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  282 */     if (this._cfgPrettyPrinter != null) {
/*  283 */       this._cfgPrettyPrinter.writeEndArray((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  285 */       if (this._outputTail >= this._outputEnd) {
/*  286 */         _flushBuffer();
/*      */       }
/*  288 */       this._outputBuffer[this._outputTail++] = ']';
/*      */     } 
/*  290 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject() throws IOException {
/*  296 */     _verifyValueWrite("start an object");
/*  297 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  298 */     if (this._cfgPrettyPrinter != null) {
/*  299 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  301 */       if (this._outputTail >= this._outputEnd) {
/*  302 */         _flushBuffer();
/*      */       }
/*  304 */       this._outputBuffer[this._outputTail++] = '{';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  311 */     _verifyValueWrite("start an object");
/*  312 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  313 */     this._writeContext = ctxt;
/*  314 */     if (this._cfgPrettyPrinter != null) {
/*  315 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  317 */       if (this._outputTail >= this._outputEnd) {
/*  318 */         _flushBuffer();
/*      */       }
/*  320 */       this._outputBuffer[this._outputTail++] = '{';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndObject() throws IOException {
/*  327 */     if (!this._writeContext.inObject()) {
/*  328 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  330 */     if (this._cfgPrettyPrinter != null) {
/*  331 */       this._cfgPrettyPrinter.writeEndObject((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  333 */       if (this._outputTail >= this._outputEnd) {
/*  334 */         _flushBuffer();
/*      */       }
/*  336 */       this._outputBuffer[this._outputTail++] = '}';
/*      */     } 
/*  338 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException {
/*  347 */     if (commaBefore) {
/*  348 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  350 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*      */     
/*  353 */     if (this._cfgUnqNames) {
/*  354 */       _writeString(name);
/*      */     } else {
/*  356 */       if (this._outputTail >= this._outputEnd) {
/*  357 */         _flushBuffer();
/*      */       }
/*  359 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  360 */       _writeString(name);
/*  361 */       if (this._outputTail >= this._outputEnd) {
/*  362 */         _flushBuffer();
/*      */       }
/*  364 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException {
/*  370 */     if (commaBefore) {
/*  371 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  373 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*  375 */     char[] quoted = name.asQuotedChars();
/*  376 */     if (this._cfgUnqNames) {
/*  377 */       writeRaw(quoted, 0, quoted.length);
/*      */     } else {
/*  379 */       if (this._outputTail >= this._outputEnd) {
/*  380 */         _flushBuffer();
/*      */       }
/*  382 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  383 */       writeRaw(quoted, 0, quoted.length);
/*  384 */       if (this._outputTail >= this._outputEnd) {
/*  385 */         _flushBuffer();
/*      */       }
/*  387 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*  400 */     _verifyValueWrite("write a string");
/*  401 */     if (text == null) {
/*  402 */       _writeNull();
/*      */       return;
/*      */     } 
/*  405 */     if (this._outputTail >= this._outputEnd) {
/*  406 */       _flushBuffer();
/*      */     }
/*  408 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  409 */     _writeString(text);
/*      */     
/*  411 */     if (this._outputTail >= this._outputEnd) {
/*  412 */       _flushBuffer();
/*      */     }
/*  414 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  419 */     _verifyValueWrite("write a string");
/*  420 */     if (reader == null) {
/*  421 */       _reportError("null reader");
/*      */     }
/*  423 */     int toRead = (len >= 0) ? len : Integer.MAX_VALUE;
/*      */     
/*  425 */     if (this._outputTail + len >= this._outputEnd) {
/*  426 */       _flushBuffer();
/*      */     }
/*  428 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  430 */     char[] buf = _allocateCopyBuffer();
/*      */     
/*  432 */     while (toRead > 0) {
/*  433 */       int toReadNow = Math.min(toRead, buf.length);
/*  434 */       int numRead = reader.read(buf, 0, toReadNow);
/*  435 */       if (numRead <= 0) {
/*      */         break;
/*      */       }
/*  438 */       if (this._outputTail + len >= this._outputEnd) {
/*  439 */         _flushBuffer();
/*      */       }
/*  441 */       _writeString(buf, 0, numRead);
/*      */ 
/*      */       
/*  444 */       toRead -= numRead;
/*      */     } 
/*      */     
/*  447 */     if (this._outputTail + len >= this._outputEnd) {
/*  448 */       _flushBuffer();
/*      */     }
/*  450 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  452 */     if (toRead > 0 && len >= 0) {
/*  453 */       _reportError("Didn't read enough from reader");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  460 */     _verifyValueWrite("write a string");
/*  461 */     if (this._outputTail >= this._outputEnd) {
/*  462 */       _flushBuffer();
/*      */     }
/*  464 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  465 */     _writeString(text, offset, len);
/*      */     
/*  467 */     if (this._outputTail >= this._outputEnd) {
/*  468 */       _flushBuffer();
/*      */     }
/*  470 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(SerializableString sstr) throws IOException {
/*  476 */     _verifyValueWrite("write a string");
/*  477 */     if (this._outputTail >= this._outputEnd) {
/*  478 */       _flushBuffer();
/*      */     }
/*  480 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  481 */     int len = sstr.appendQuoted(this._outputBuffer, this._outputTail);
/*  482 */     if (len < 0) {
/*  483 */       _writeString2(sstr);
/*      */       return;
/*      */     } 
/*  486 */     this._outputTail += len;
/*  487 */     if (this._outputTail >= this._outputEnd) {
/*  488 */       _flushBuffer();
/*      */     }
/*  490 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString2(SerializableString sstr) throws IOException {
/*  496 */     char[] text = sstr.asQuotedChars();
/*  497 */     int len = text.length;
/*  498 */     if (len < 32) {
/*  499 */       int room = this._outputEnd - this._outputTail;
/*  500 */       if (len > room) {
/*  501 */         _flushBuffer();
/*      */       }
/*  503 */       System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
/*  504 */       this._outputTail += len;
/*      */     } else {
/*  506 */       _flushBuffer();
/*  507 */       this._writer.write(text, 0, len);
/*      */     } 
/*  509 */     if (this._outputTail >= this._outputEnd) {
/*  510 */       _flushBuffer();
/*      */     }
/*  512 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  518 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/*  524 */     _reportUnsupportedOperation();
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
/*      */   public void writeRaw(String text) throws IOException {
/*  537 */     int len = text.length();
/*  538 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  540 */     if (room == 0) {
/*  541 */       _flushBuffer();
/*  542 */       room = this._outputEnd - this._outputTail;
/*      */     } 
/*      */     
/*  545 */     if (room >= len) {
/*  546 */       text.getChars(0, len, this._outputBuffer, this._outputTail);
/*  547 */       this._outputTail += len;
/*      */     } else {
/*  549 */       writeRawLong(text);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int start, int len) throws IOException {
/*  557 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  559 */     if (room < len) {
/*  560 */       _flushBuffer();
/*  561 */       room = this._outputEnd - this._outputTail;
/*      */     } 
/*      */     
/*  564 */     if (room >= len) {
/*  565 */       text.getChars(start, start + len, this._outputBuffer, this._outputTail);
/*  566 */       this._outputTail += len;
/*      */     } else {
/*  568 */       writeRawLong(text.substring(start, start + len));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  575 */     int len = text.appendUnquoted(this._outputBuffer, this._outputTail);
/*  576 */     if (len < 0) {
/*  577 */       writeRaw(text.getValue());
/*      */       return;
/*      */     } 
/*  580 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/*  587 */     if (len < 32) {
/*  588 */       int room = this._outputEnd - this._outputTail;
/*  589 */       if (len > room) {
/*  590 */         _flushBuffer();
/*      */       }
/*  592 */       System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
/*  593 */       this._outputTail += len;
/*      */       
/*      */       return;
/*      */     } 
/*  597 */     _flushBuffer();
/*  598 */     this._writer.write(text, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char c) throws IOException {
/*  604 */     if (this._outputTail >= this._outputEnd) {
/*  605 */       _flushBuffer();
/*      */     }
/*  607 */     this._outputBuffer[this._outputTail++] = c;
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeRawLong(String text) throws IOException {
/*  612 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  614 */     text.getChars(0, room, this._outputBuffer, this._outputTail);
/*  615 */     this._outputTail += room;
/*  616 */     _flushBuffer();
/*  617 */     int offset = room;
/*  618 */     int len = text.length() - room;
/*      */     
/*  620 */     while (len > this._outputEnd) {
/*  621 */       int amount = this._outputEnd;
/*  622 */       text.getChars(offset, offset + amount, this._outputBuffer, 0);
/*  623 */       this._outputHead = 0;
/*  624 */       this._outputTail = amount;
/*  625 */       _flushBuffer();
/*  626 */       offset += amount;
/*  627 */       len -= amount;
/*      */     } 
/*      */     
/*  630 */     text.getChars(offset, offset + len, this._outputBuffer, 0);
/*  631 */     this._outputHead = 0;
/*  632 */     this._outputTail = len;
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
/*  645 */     _verifyValueWrite("write a binary value");
/*      */     
/*  647 */     if (this._outputTail >= this._outputEnd) {
/*  648 */       _flushBuffer();
/*      */     }
/*  650 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  651 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  653 */     if (this._outputTail >= this._outputEnd) {
/*  654 */       _flushBuffer();
/*      */     }
/*  656 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
/*      */     int bytes;
/*  664 */     _verifyValueWrite("write a binary value");
/*      */     
/*  666 */     if (this._outputTail >= this._outputEnd) {
/*  667 */       _flushBuffer();
/*      */     }
/*  669 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  670 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     
/*      */     try {
/*  673 */       if (dataLength < 0) {
/*  674 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  676 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  677 */         if (missing > 0) {
/*  678 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  680 */         bytes = dataLength;
/*      */       } 
/*      */     } finally {
/*  683 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     } 
/*      */     
/*  686 */     if (this._outputTail >= this._outputEnd) {
/*  687 */       _flushBuffer();
/*      */     }
/*  689 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  690 */     return bytes;
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
/*  702 */     _verifyValueWrite("write a number");
/*  703 */     if (this._cfgNumbersAsStrings) {
/*  704 */       _writeQuotedShort(s);
/*      */       
/*      */       return;
/*      */     } 
/*  708 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  709 */       _flushBuffer();
/*      */     }
/*  711 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedShort(short s) throws IOException {
/*  715 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  716 */       _flushBuffer();
/*      */     }
/*  718 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  719 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  720 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(int i) throws IOException {
/*  726 */     _verifyValueWrite("write a number");
/*  727 */     if (this._cfgNumbersAsStrings) {
/*  728 */       _writeQuotedInt(i);
/*      */       
/*      */       return;
/*      */     } 
/*  732 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  733 */       _flushBuffer();
/*      */     }
/*  735 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedInt(int i) throws IOException {
/*  739 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  740 */       _flushBuffer();
/*      */     }
/*  742 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  743 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  744 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(long l) throws IOException {
/*  750 */     _verifyValueWrite("write a number");
/*  751 */     if (this._cfgNumbersAsStrings) {
/*  752 */       _writeQuotedLong(l);
/*      */       return;
/*      */     } 
/*  755 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  757 */       _flushBuffer();
/*      */     }
/*  759 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedLong(long l) throws IOException {
/*  763 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  764 */       _flushBuffer();
/*      */     }
/*  766 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  767 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  768 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger value) throws IOException {
/*  776 */     _verifyValueWrite("write a number");
/*  777 */     if (value == null) {
/*  778 */       _writeNull();
/*  779 */     } else if (this._cfgNumbersAsStrings) {
/*  780 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/*  782 */       writeRaw(value.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(double d) throws IOException {
/*  790 */     if (this._cfgNumbersAsStrings || (
/*  791 */       NumberOutput.notFinite(d) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
/*  792 */       writeString(String.valueOf(d));
/*      */       
/*      */       return;
/*      */     } 
/*  796 */     _verifyValueWrite("write a number");
/*  797 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(float f) throws IOException {
/*  804 */     if (this._cfgNumbersAsStrings || (
/*  805 */       NumberOutput.notFinite(f) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
/*  806 */       writeString(String.valueOf(f));
/*      */       
/*      */       return;
/*      */     } 
/*  810 */     _verifyValueWrite("write a number");
/*  811 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal value) throws IOException {
/*  818 */     _verifyValueWrite("write a number");
/*  819 */     if (value == null) {
/*  820 */       _writeNull();
/*  821 */     } else if (this._cfgNumbersAsStrings) {
/*  822 */       _writeQuotedRaw(_asString(value));
/*      */     } else {
/*  824 */       writeRaw(_asString(value));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException {
/*  831 */     _verifyValueWrite("write a number");
/*  832 */     if (this._cfgNumbersAsStrings) {
/*  833 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  835 */       writeRaw(encodedValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _writeQuotedRaw(String value) throws IOException {
/*  841 */     if (this._outputTail >= this._outputEnd) {
/*  842 */       _flushBuffer();
/*      */     }
/*  844 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  845 */     writeRaw(value);
/*  846 */     if (this._outputTail >= this._outputEnd) {
/*  847 */       _flushBuffer();
/*      */     }
/*  849 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException {
/*  855 */     _verifyValueWrite("write a boolean value");
/*  856 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  857 */       _flushBuffer();
/*      */     }
/*  859 */     int ptr = this._outputTail;
/*  860 */     char[] buf = this._outputBuffer;
/*  861 */     if (state) {
/*  862 */       buf[ptr] = 't';
/*  863 */       buf[++ptr] = 'r';
/*  864 */       buf[++ptr] = 'u';
/*  865 */       buf[++ptr] = 'e';
/*      */     } else {
/*  867 */       buf[ptr] = 'f';
/*  868 */       buf[++ptr] = 'a';
/*  869 */       buf[++ptr] = 'l';
/*  870 */       buf[++ptr] = 's';
/*  871 */       buf[++ptr] = 'e';
/*      */     } 
/*  873 */     this._outputTail = ptr + 1;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/*  878 */     _verifyValueWrite("write a null");
/*  879 */     _writeNull();
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
/*      */     char c;
/*  891 */     int status = this._writeContext.writeValue();
/*  892 */     if (this._cfgPrettyPrinter != null) {
/*      */       
/*  894 */       _verifyPrettyValueWrite(typeMsg, status);
/*      */       
/*      */       return;
/*      */     } 
/*  898 */     switch (status) {
/*      */       default:
/*      */         return;
/*      */       
/*      */       case 1:
/*  903 */         c = ',';
/*      */         break;
/*      */       case 2:
/*  906 */         c = ':';
/*      */         break;
/*      */       case 3:
/*  909 */         if (this._rootValueSeparator != null) {
/*  910 */           writeRaw(this._rootValueSeparator.getValue());
/*      */         }
/*      */         return;
/*      */       case 5:
/*  914 */         _reportCantWriteValueExpectName(typeMsg);
/*      */         return;
/*      */     } 
/*  917 */     if (this._outputTail >= this._outputEnd) {
/*  918 */       _flushBuffer();
/*      */     }
/*  920 */     this._outputBuffer[this._outputTail++] = c;
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
/*  932 */     _flushBuffer();
/*  933 */     if (this._writer != null && 
/*  934 */       isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*  935 */       this._writer.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  943 */     super.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  949 */     if (this._outputBuffer != null && 
/*  950 */       isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
/*      */       while (true) {
/*  952 */         JsonStreamContext ctxt = getOutputContext();
/*  953 */         if (ctxt.inArray()) {
/*  954 */           writeEndArray(); continue;
/*  955 */         }  if (ctxt.inObject()) {
/*  956 */           writeEndObject();
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/*      */     }
/*  962 */     _flushBuffer();
/*  963 */     this._outputHead = 0;
/*  964 */     this._outputTail = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  972 */     if (this._writer != null) {
/*  973 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
/*  974 */         this._writer.close();
/*  975 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*      */         
/*  977 */         this._writer.flush();
/*      */       } 
/*      */     }
/*      */     
/*  981 */     _releaseBuffers();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() {
/*  987 */     char[] buf = this._outputBuffer;
/*  988 */     if (buf != null) {
/*  989 */       this._outputBuffer = null;
/*  990 */       this._ioContext.releaseConcatBuffer(buf);
/*      */     } 
/*  992 */     buf = this._copyBuffer;
/*  993 */     if (buf != null) {
/*  994 */       this._copyBuffer = null;
/*  995 */       this._ioContext.releaseNameCopyBuffer(buf);
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
/*      */   private void _writeString(String text) throws IOException {
/* 1012 */     int len = text.length();
/* 1013 */     if (len > this._outputEnd) {
/* 1014 */       _writeLongString(text);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1020 */     if (this._outputTail + len > this._outputEnd) {
/* 1021 */       _flushBuffer();
/*      */     }
/* 1023 */     text.getChars(0, len, this._outputBuffer, this._outputTail);
/*      */     
/* 1025 */     if (this._characterEscapes != null) {
/* 1026 */       _writeStringCustom(len);
/* 1027 */     } else if (this._maximumNonEscapedChar != 0) {
/* 1028 */       _writeStringASCII(len, this._maximumNonEscapedChar);
/*      */     } else {
/* 1030 */       _writeString2(len);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString2(int len) throws IOException {
/* 1037 */     int end = this._outputTail + len;
/* 1038 */     int[] escCodes = this._outputEscapes;
/* 1039 */     int escLen = escCodes.length;
/*      */ 
/*      */     
/* 1042 */     while (this._outputTail < end) {
/*      */       
/*      */       label17: while (true) {
/*      */         
/* 1046 */         char c = this._outputBuffer[this._outputTail];
/* 1047 */         if (c < escLen && escCodes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1059 */           int flushLen = this._outputTail - this._outputHead;
/* 1060 */           if (flushLen > 0) {
/* 1061 */             this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */             
/*      */             break label17;
/*      */           } 
/*      */           
/* 1066 */           char c1 = this._outputBuffer[this._outputTail++];
/* 1067 */           _prependOrWriteCharacterEscape(c1, escCodes[c1]);
/*      */           continue;
/*      */         } 
/*      */         if (++this._outputTail >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void _writeLongString(String text) throws IOException {
/* 1078 */     _flushBuffer();
/*      */ 
/*      */     
/* 1081 */     int textLen = text.length();
/* 1082 */     int offset = 0;
/*      */     do {
/* 1084 */       int max = this._outputEnd;
/* 1085 */       int segmentLen = (offset + max > textLen) ? (textLen - offset) : max;
/*      */       
/* 1087 */       text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
/* 1088 */       if (this._characterEscapes != null) {
/* 1089 */         _writeSegmentCustom(segmentLen);
/* 1090 */       } else if (this._maximumNonEscapedChar != 0) {
/* 1091 */         _writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
/*      */       } else {
/* 1093 */         _writeSegment(segmentLen);
/*      */       } 
/* 1095 */       offset += segmentLen;
/* 1096 */     } while (offset < textLen);
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
/*      */   private void _writeSegment(int end) throws IOException {
/* 1110 */     int[] escCodes = this._outputEscapes;
/* 1111 */     int escLen = escCodes.length;
/*      */     
/* 1113 */     int ptr = 0;
/* 1114 */     int start = ptr;
/*      */ 
/*      */     
/* 1117 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1121 */         c = this._outputBuffer[ptr];
/* 1122 */         if (c < escLen && escCodes[c] != 0) {
/*      */           break;
/*      */         }
/* 1125 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1134 */       int flushLen = ptr - start;
/* 1135 */       if (flushLen > 0) {
/* 1136 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1137 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1141 */       ptr++;
/*      */       
/* 1143 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString(char[] text, int offset, int len) throws IOException {
/* 1153 */     if (this._characterEscapes != null) {
/* 1154 */       _writeStringCustom(text, offset, len);
/*      */       return;
/*      */     } 
/* 1157 */     if (this._maximumNonEscapedChar != 0) {
/* 1158 */       _writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/* 1166 */     len += offset;
/* 1167 */     int[] escCodes = this._outputEscapes;
/* 1168 */     int escLen = escCodes.length;
/* 1169 */     while (offset < len) {
/* 1170 */       int start = offset;
/*      */       
/*      */       do {
/* 1173 */         char c1 = text[offset];
/* 1174 */         if (c1 < escLen && escCodes[c1] != 0) {
/*      */           break;
/*      */         }
/* 1177 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1183 */       int newAmount = offset - start;
/* 1184 */       if (newAmount < 32) {
/*      */         
/* 1186 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1187 */           _flushBuffer();
/*      */         }
/* 1189 */         if (newAmount > 0) {
/* 1190 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1191 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1194 */         _flushBuffer();
/* 1195 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1198 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1202 */       char c = text[offset++];
/* 1203 */       _appendCharacterEscape(c, escCodes[c]);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringASCII(int len, int maxNonEscaped) throws IOException, JsonGenerationException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield _outputTail : I
/*      */     //   4: iload_1
/*      */     //   5: iadd
/*      */     //   6: istore_3
/*      */     //   7: aload_0
/*      */     //   8: getfield _outputEscapes : [I
/*      */     //   11: astore #4
/*      */     //   13: aload #4
/*      */     //   15: arraylength
/*      */     //   16: iload_2
/*      */     //   17: iconst_1
/*      */     //   18: iadd
/*      */     //   19: invokestatic min : (II)I
/*      */     //   22: istore #5
/*      */     //   24: iconst_0
/*      */     //   25: istore #6
/*      */     //   27: aload_0
/*      */     //   28: getfield _outputTail : I
/*      */     //   31: iload_3
/*      */     //   32: if_icmpge -> 152
/*      */     //   35: aload_0
/*      */     //   36: getfield _outputBuffer : [C
/*      */     //   39: aload_0
/*      */     //   40: getfield _outputTail : I
/*      */     //   43: caload
/*      */     //   44: istore #7
/*      */     //   46: iload #7
/*      */     //   48: iload #5
/*      */     //   50: if_icmpge -> 68
/*      */     //   53: aload #4
/*      */     //   55: iload #7
/*      */     //   57: iaload
/*      */     //   58: istore #6
/*      */     //   60: iload #6
/*      */     //   62: ifeq -> 80
/*      */     //   65: goto -> 98
/*      */     //   68: iload #7
/*      */     //   70: iload_2
/*      */     //   71: if_icmple -> 80
/*      */     //   74: iconst_m1
/*      */     //   75: istore #6
/*      */     //   77: goto -> 98
/*      */     //   80: aload_0
/*      */     //   81: dup
/*      */     //   82: getfield _outputTail : I
/*      */     //   85: iconst_1
/*      */     //   86: iadd
/*      */     //   87: dup_x1
/*      */     //   88: putfield _outputTail : I
/*      */     //   91: iload_3
/*      */     //   92: if_icmplt -> 35
/*      */     //   95: goto -> 152
/*      */     //   98: aload_0
/*      */     //   99: getfield _outputTail : I
/*      */     //   102: aload_0
/*      */     //   103: getfield _outputHead : I
/*      */     //   106: isub
/*      */     //   107: istore #8
/*      */     //   109: iload #8
/*      */     //   111: ifle -> 131
/*      */     //   114: aload_0
/*      */     //   115: getfield _writer : Ljava/io/Writer;
/*      */     //   118: aload_0
/*      */     //   119: getfield _outputBuffer : [C
/*      */     //   122: aload_0
/*      */     //   123: getfield _outputHead : I
/*      */     //   126: iload #8
/*      */     //   128: invokevirtual write : ([CII)V
/*      */     //   131: aload_0
/*      */     //   132: dup
/*      */     //   133: getfield _outputTail : I
/*      */     //   136: iconst_1
/*      */     //   137: iadd
/*      */     //   138: putfield _outputTail : I
/*      */     //   141: aload_0
/*      */     //   142: iload #7
/*      */     //   144: iload #6
/*      */     //   146: invokespecial _prependOrWriteCharacterEscape : (CI)V
/*      */     //   149: goto -> 27
/*      */     //   152: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1221	-> 0
/*      */     //   #1222	-> 7
/*      */     //   #1223	-> 13
/*      */     //   #1224	-> 24
/*      */     //   #1227	-> 27
/*      */     //   #1232	-> 35
/*      */     //   #1233	-> 46
/*      */     //   #1234	-> 53
/*      */     //   #1235	-> 60
/*      */     //   #1236	-> 65
/*      */     //   #1238	-> 68
/*      */     //   #1239	-> 74
/*      */     //   #1240	-> 77
/*      */     //   #1242	-> 80
/*      */     //   #1243	-> 95
/*      */     //   #1246	-> 98
/*      */     //   #1247	-> 109
/*      */     //   #1248	-> 114
/*      */     //   #1250	-> 131
/*      */     //   #1251	-> 141
/*      */     //   #1252	-> 149
/*      */     //   #1253	-> 152
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   46	103	7	c	C
/*      */     //   109	40	8	flushLen	I
/*      */     //   0	153	0	this	Lcom/fasterxml/jackson/core/json/WriterBasedJsonGenerator;
/*      */     //   0	153	1	len	I
/*      */     //   0	153	2	maxNonEscaped	I
/*      */     //   7	146	3	end	I
/*      */     //   13	140	4	escCodes	[I
/*      */     //   24	129	5	escLimit	I
/*      */     //   27	126	6	escCode	I
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
/*      */   private void _writeSegmentASCII(int end, int maxNonEscaped) throws IOException, JsonGenerationException {
/* 1258 */     int[] escCodes = this._outputEscapes;
/* 1259 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1261 */     int ptr = 0;
/* 1262 */     int escCode = 0;
/* 1263 */     int start = ptr;
/*      */ 
/*      */     
/* 1266 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1270 */         c = this._outputBuffer[ptr];
/* 1271 */         if (c < escLimit) {
/* 1272 */           escCode = escCodes[c];
/* 1273 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1276 */         } else if (c > maxNonEscaped) {
/* 1277 */           escCode = -1;
/*      */           break;
/*      */         } 
/* 1280 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */       
/* 1284 */       int flushLen = ptr - start;
/* 1285 */       if (flushLen > 0) {
/* 1286 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1287 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1291 */       ptr++;
/* 1292 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped) throws IOException, JsonGenerationException {
/* 1300 */     len += offset;
/* 1301 */     int[] escCodes = this._outputEscapes;
/* 1302 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1304 */     int escCode = 0;
/*      */     
/* 1306 */     while (offset < len) {
/* 1307 */       char c; int start = offset;
/*      */ 
/*      */       
/*      */       do {
/* 1311 */         c = text[offset];
/* 1312 */         if (c < escLimit) {
/* 1313 */           escCode = escCodes[c];
/* 1314 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1317 */         } else if (c > maxNonEscaped) {
/* 1318 */           escCode = -1;
/*      */           break;
/*      */         } 
/* 1321 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1327 */       int newAmount = offset - start;
/* 1328 */       if (newAmount < 32) {
/*      */         
/* 1330 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1331 */           _flushBuffer();
/*      */         }
/* 1333 */         if (newAmount > 0) {
/* 1334 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1335 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1338 */         _flushBuffer();
/* 1339 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1342 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1346 */       offset++;
/* 1347 */       _appendCharacterEscape(c, escCode);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringCustom(int len) throws IOException, JsonGenerationException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield _outputTail : I
/*      */     //   4: iload_1
/*      */     //   5: iadd
/*      */     //   6: istore_2
/*      */     //   7: aload_0
/*      */     //   8: getfield _outputEscapes : [I
/*      */     //   11: astore_3
/*      */     //   12: aload_0
/*      */     //   13: getfield _maximumNonEscapedChar : I
/*      */     //   16: iconst_1
/*      */     //   17: if_icmpge -> 26
/*      */     //   20: ldc_w 65535
/*      */     //   23: goto -> 30
/*      */     //   26: aload_0
/*      */     //   27: getfield _maximumNonEscapedChar : I
/*      */     //   30: istore #4
/*      */     //   32: aload_3
/*      */     //   33: arraylength
/*      */     //   34: iload #4
/*      */     //   36: iconst_1
/*      */     //   37: iadd
/*      */     //   38: invokestatic min : (II)I
/*      */     //   41: istore #5
/*      */     //   43: iconst_0
/*      */     //   44: istore #6
/*      */     //   46: aload_0
/*      */     //   47: getfield _characterEscapes : Lcom/fasterxml/jackson/core/io/CharacterEscapes;
/*      */     //   50: astore #7
/*      */     //   52: aload_0
/*      */     //   53: getfield _outputTail : I
/*      */     //   56: iload_2
/*      */     //   57: if_icmpge -> 199
/*      */     //   60: aload_0
/*      */     //   61: getfield _outputBuffer : [C
/*      */     //   64: aload_0
/*      */     //   65: getfield _outputTail : I
/*      */     //   68: caload
/*      */     //   69: istore #8
/*      */     //   71: iload #8
/*      */     //   73: iload #5
/*      */     //   75: if_icmpge -> 92
/*      */     //   78: aload_3
/*      */     //   79: iload #8
/*      */     //   81: iaload
/*      */     //   82: istore #6
/*      */     //   84: iload #6
/*      */     //   86: ifeq -> 127
/*      */     //   89: goto -> 145
/*      */     //   92: iload #8
/*      */     //   94: iload #4
/*      */     //   96: if_icmple -> 105
/*      */     //   99: iconst_m1
/*      */     //   100: istore #6
/*      */     //   102: goto -> 145
/*      */     //   105: aload_0
/*      */     //   106: aload #7
/*      */     //   108: iload #8
/*      */     //   110: invokevirtual getEscapeSequence : (I)Lcom/fasterxml/jackson/core/SerializableString;
/*      */     //   113: dup_x1
/*      */     //   114: putfield _currentEscape : Lcom/fasterxml/jackson/core/SerializableString;
/*      */     //   117: ifnull -> 127
/*      */     //   120: bipush #-2
/*      */     //   122: istore #6
/*      */     //   124: goto -> 145
/*      */     //   127: aload_0
/*      */     //   128: dup
/*      */     //   129: getfield _outputTail : I
/*      */     //   132: iconst_1
/*      */     //   133: iadd
/*      */     //   134: dup_x1
/*      */     //   135: putfield _outputTail : I
/*      */     //   138: iload_2
/*      */     //   139: if_icmplt -> 60
/*      */     //   142: goto -> 199
/*      */     //   145: aload_0
/*      */     //   146: getfield _outputTail : I
/*      */     //   149: aload_0
/*      */     //   150: getfield _outputHead : I
/*      */     //   153: isub
/*      */     //   154: istore #9
/*      */     //   156: iload #9
/*      */     //   158: ifle -> 178
/*      */     //   161: aload_0
/*      */     //   162: getfield _writer : Ljava/io/Writer;
/*      */     //   165: aload_0
/*      */     //   166: getfield _outputBuffer : [C
/*      */     //   169: aload_0
/*      */     //   170: getfield _outputHead : I
/*      */     //   173: iload #9
/*      */     //   175: invokevirtual write : ([CII)V
/*      */     //   178: aload_0
/*      */     //   179: dup
/*      */     //   180: getfield _outputTail : I
/*      */     //   183: iconst_1
/*      */     //   184: iadd
/*      */     //   185: putfield _outputTail : I
/*      */     //   188: aload_0
/*      */     //   189: iload #8
/*      */     //   191: iload #6
/*      */     //   193: invokespecial _prependOrWriteCharacterEscape : (CI)V
/*      */     //   196: goto -> 52
/*      */     //   199: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1365	-> 0
/*      */     //   #1366	-> 7
/*      */     //   #1367	-> 12
/*      */     //   #1368	-> 32
/*      */     //   #1369	-> 43
/*      */     //   #1370	-> 46
/*      */     //   #1373	-> 52
/*      */     //   #1378	-> 60
/*      */     //   #1379	-> 71
/*      */     //   #1380	-> 78
/*      */     //   #1381	-> 84
/*      */     //   #1382	-> 89
/*      */     //   #1384	-> 92
/*      */     //   #1385	-> 99
/*      */     //   #1386	-> 102
/*      */     //   #1388	-> 105
/*      */     //   #1389	-> 120
/*      */     //   #1390	-> 124
/*      */     //   #1393	-> 127
/*      */     //   #1394	-> 142
/*      */     //   #1397	-> 145
/*      */     //   #1398	-> 156
/*      */     //   #1399	-> 161
/*      */     //   #1401	-> 178
/*      */     //   #1402	-> 188
/*      */     //   #1403	-> 196
/*      */     //   #1404	-> 199
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   71	125	8	c	C
/*      */     //   156	40	9	flushLen	I
/*      */     //   0	200	0	this	Lcom/fasterxml/jackson/core/json/WriterBasedJsonGenerator;
/*      */     //   0	200	1	len	I
/*      */     //   7	193	2	end	I
/*      */     //   12	188	3	escCodes	[I
/*      */     //   32	168	4	maxNonEscaped	I
/*      */     //   43	157	5	escLimit	I
/*      */     //   46	154	6	escCode	I
/*      */     //   52	148	7	customEscapes	Lcom/fasterxml/jackson/core/io/CharacterEscapes;
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
/*      */   private void _writeSegmentCustom(int end) throws IOException, JsonGenerationException {
/* 1409 */     int[] escCodes = this._outputEscapes;
/* 1410 */     int maxNonEscaped = (this._maximumNonEscapedChar < 1) ? 65535 : this._maximumNonEscapedChar;
/* 1411 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1412 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1414 */     int ptr = 0;
/* 1415 */     int escCode = 0;
/* 1416 */     int start = ptr;
/*      */ 
/*      */     
/* 1419 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1423 */         c = this._outputBuffer[ptr];
/* 1424 */         if (c < escLimit) {
/* 1425 */           escCode = escCodes[c];
/* 1426 */           if (escCode != 0)
/*      */             break; 
/*      */         } else {
/* 1429 */           if (c > maxNonEscaped) {
/* 1430 */             escCode = -1;
/*      */             break;
/*      */           } 
/* 1433 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1434 */             escCode = -2;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1438 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */       
/* 1442 */       int flushLen = ptr - start;
/* 1443 */       if (flushLen > 0) {
/* 1444 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1445 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1449 */       ptr++;
/* 1450 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringCustom(char[] text, int offset, int len) throws IOException, JsonGenerationException {
/* 1457 */     len += offset;
/* 1458 */     int[] escCodes = this._outputEscapes;
/* 1459 */     int maxNonEscaped = (this._maximumNonEscapedChar < 1) ? 65535 : this._maximumNonEscapedChar;
/* 1460 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1461 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1463 */     int escCode = 0;
/*      */     
/* 1465 */     while (offset < len) {
/* 1466 */       char c; int start = offset;
/*      */ 
/*      */       
/*      */       do {
/* 1470 */         c = text[offset];
/* 1471 */         if (c < escLimit) {
/* 1472 */           escCode = escCodes[c];
/* 1473 */           if (escCode != 0)
/*      */             break; 
/*      */         } else {
/* 1476 */           if (c > maxNonEscaped) {
/* 1477 */             escCode = -1;
/*      */             break;
/*      */           } 
/* 1480 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1481 */             escCode = -2;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1485 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1491 */       int newAmount = offset - start;
/* 1492 */       if (newAmount < 32) {
/*      */         
/* 1494 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1495 */           _flushBuffer();
/*      */         }
/* 1497 */         if (newAmount > 0) {
/* 1498 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1499 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1502 */         _flushBuffer();
/* 1503 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1506 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1510 */       offset++;
/* 1511 */       _appendCharacterEscape(c, escCode);
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
/*      */   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
/* 1525 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1527 */     int safeOutputEnd = this._outputEnd - 6;
/* 1528 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/* 1531 */     while (inputPtr <= safeInputEnd) {
/* 1532 */       if (this._outputTail > safeOutputEnd) {
/* 1533 */         _flushBuffer();
/*      */       }
/*      */       
/* 1536 */       int b24 = input[inputPtr++] << 8;
/* 1537 */       b24 |= input[inputPtr++] & 0xFF;
/* 1538 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 1539 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1540 */       if (--chunksBeforeLF <= 0) {
/*      */         
/* 1542 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1543 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1544 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1549 */     int inputLeft = inputEnd - inputPtr;
/* 1550 */     if (inputLeft > 0) {
/* 1551 */       if (this._outputTail > safeOutputEnd) {
/* 1552 */         _flushBuffer();
/*      */       }
/* 1554 */       int b24 = input[inputPtr++] << 16;
/* 1555 */       if (inputLeft == 2) {
/* 1556 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*      */       }
/* 1558 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
/* 1567 */     int inputPtr = 0;
/* 1568 */     int inputEnd = 0;
/* 1569 */     int lastFullOffset = -3;
/*      */ 
/*      */     
/* 1572 */     int safeOutputEnd = this._outputEnd - 6;
/* 1573 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1575 */     while (bytesLeft > 2) {
/* 1576 */       if (inputPtr > lastFullOffset) {
/* 1577 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1578 */         inputPtr = 0;
/* 1579 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1582 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1584 */       if (this._outputTail > safeOutputEnd) {
/* 1585 */         _flushBuffer();
/*      */       }
/* 1587 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1588 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1589 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1590 */       bytesLeft -= 3;
/* 1591 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1592 */       if (--chunksBeforeLF <= 0) {
/* 1593 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1594 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1595 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1600 */     if (bytesLeft > 0) {
/* 1601 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1602 */       inputPtr = 0;
/* 1603 */       if (inputEnd > 0) {
/* 1604 */         int amount; if (this._outputTail > safeOutputEnd) {
/* 1605 */           _flushBuffer();
/*      */         }
/* 1607 */         int b24 = readBuffer[inputPtr++] << 16;
/*      */         
/* 1609 */         if (inputPtr < inputEnd) {
/* 1610 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1611 */           amount = 2;
/*      */         } else {
/* 1613 */           amount = 1;
/*      */         } 
/* 1615 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1616 */         bytesLeft -= amount;
/*      */       } 
/*      */     } 
/* 1619 */     return bytesLeft;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
/* 1627 */     int inputPtr = 0;
/* 1628 */     int inputEnd = 0;
/* 1629 */     int lastFullOffset = -3;
/* 1630 */     int bytesDone = 0;
/*      */ 
/*      */     
/* 1633 */     int safeOutputEnd = this._outputEnd - 6;
/* 1634 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1638 */       if (inputPtr > lastFullOffset) {
/* 1639 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1640 */         inputPtr = 0;
/* 1641 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1644 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1646 */       if (this._outputTail > safeOutputEnd) {
/* 1647 */         _flushBuffer();
/*      */       }
/*      */       
/* 1650 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1651 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1652 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1653 */       bytesDone += 3;
/* 1654 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1655 */       if (--chunksBeforeLF <= 0) {
/* 1656 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1657 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1658 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1663 */     if (inputPtr < inputEnd) {
/* 1664 */       if (this._outputTail > safeOutputEnd) {
/* 1665 */         _flushBuffer();
/*      */       }
/* 1667 */       int b24 = readBuffer[inputPtr++] << 16;
/* 1668 */       int amount = 1;
/* 1669 */       if (inputPtr < inputEnd) {
/* 1670 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1671 */         amount = 2;
/*      */       } 
/* 1673 */       bytesDone += amount;
/* 1674 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     } 
/* 1676 */     return bytesDone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
/* 1684 */     int i = 0;
/* 1685 */     while (inputPtr < inputEnd) {
/* 1686 */       readBuffer[i++] = readBuffer[inputPtr++];
/*      */     }
/* 1688 */     inputPtr = 0;
/* 1689 */     inputEnd = i;
/* 1690 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     
/*      */     do {
/* 1693 */       int length = maxRead - inputEnd;
/* 1694 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 1697 */       int count = in.read(readBuffer, inputEnd, length);
/* 1698 */       if (count < 0) {
/* 1699 */         return inputEnd;
/*      */       }
/* 1701 */       inputEnd += count;
/* 1702 */     } while (inputEnd < 3);
/* 1703 */     return inputEnd;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeNull() throws IOException {
/* 1714 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1715 */       _flushBuffer();
/*      */     }
/* 1717 */     int ptr = this._outputTail;
/* 1718 */     char[] buf = this._outputBuffer;
/* 1719 */     buf[ptr] = 'n';
/* 1720 */     buf[++ptr] = 'u';
/* 1721 */     buf[++ptr] = 'l';
/* 1722 */     buf[++ptr] = 'l';
/* 1723 */     this._outputTail = ptr + 1;
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
/*      */   private void _prependOrWriteCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1740 */     if (escCode >= 0) {
/* 1741 */       if (this._outputTail >= 2) {
/* 1742 */         int ptr = this._outputTail - 2;
/* 1743 */         this._outputHead = ptr;
/* 1744 */         this._outputBuffer[ptr++] = '\\';
/* 1745 */         this._outputBuffer[ptr] = (char)escCode;
/*      */         
/*      */         return;
/*      */       } 
/* 1749 */       char[] buf = this._entityBuffer;
/* 1750 */       if (buf == null) {
/* 1751 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1753 */       this._outputHead = this._outputTail;
/* 1754 */       buf[1] = (char)escCode;
/* 1755 */       this._writer.write(buf, 0, 2);
/*      */       return;
/*      */     } 
/* 1758 */     if (escCode != -2) {
/* 1759 */       if (this._outputTail >= 6) {
/* 1760 */         char[] arrayOfChar = this._outputBuffer;
/* 1761 */         int ptr = this._outputTail - 6;
/* 1762 */         this._outputHead = ptr;
/* 1763 */         arrayOfChar[ptr] = '\\';
/* 1764 */         arrayOfChar[++ptr] = 'u';
/*      */         
/* 1766 */         if (ch > 'ÿ') {
/* 1767 */           int hi = ch >> 8 & 0xFF;
/* 1768 */           arrayOfChar[++ptr] = HEX_CHARS[hi >> 4];
/* 1769 */           arrayOfChar[++ptr] = HEX_CHARS[hi & 0xF];
/* 1770 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1772 */           arrayOfChar[++ptr] = '0';
/* 1773 */           arrayOfChar[++ptr] = '0';
/*      */         } 
/* 1775 */         arrayOfChar[++ptr] = HEX_CHARS[ch >> 4];
/* 1776 */         arrayOfChar[++ptr] = HEX_CHARS[ch & 0xF];
/*      */         
/*      */         return;
/*      */       } 
/* 1780 */       char[] buf = this._entityBuffer;
/* 1781 */       if (buf == null) {
/* 1782 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1784 */       this._outputHead = this._outputTail;
/* 1785 */       if (ch > 'ÿ') {
/* 1786 */         int hi = ch >> 8 & 0xFF;
/* 1787 */         int lo = ch & 0xFF;
/* 1788 */         buf[10] = HEX_CHARS[hi >> 4];
/* 1789 */         buf[11] = HEX_CHARS[hi & 0xF];
/* 1790 */         buf[12] = HEX_CHARS[lo >> 4];
/* 1791 */         buf[13] = HEX_CHARS[lo & 0xF];
/* 1792 */         this._writer.write(buf, 8, 6);
/*      */       } else {
/* 1794 */         buf[6] = HEX_CHARS[ch >> 4];
/* 1795 */         buf[7] = HEX_CHARS[ch & 0xF];
/* 1796 */         this._writer.write(buf, 2, 6);
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1802 */     if (this._currentEscape == null) {
/* 1803 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1805 */       escape = this._currentEscape.getValue();
/* 1806 */       this._currentEscape = null;
/*      */     } 
/* 1808 */     int len = escape.length();
/* 1809 */     if (this._outputTail >= len) {
/* 1810 */       int ptr = this._outputTail - len;
/* 1811 */       this._outputHead = ptr;
/* 1812 */       escape.getChars(0, len, this._outputBuffer, ptr);
/*      */       
/*      */       return;
/*      */     } 
/* 1816 */     this._outputHead = this._outputTail;
/* 1817 */     this._writer.write(escape);
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
/*      */   private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1831 */     if (escCode >= 0) {
/* 1832 */       if (ptr > 1 && ptr < end) {
/* 1833 */         ptr -= 2;
/* 1834 */         buffer[ptr] = '\\';
/* 1835 */         buffer[ptr + 1] = (char)escCode;
/*      */       } else {
/* 1837 */         char[] ent = this._entityBuffer;
/* 1838 */         if (ent == null) {
/* 1839 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1841 */         ent[1] = (char)escCode;
/* 1842 */         this._writer.write(ent, 0, 2);
/*      */       } 
/* 1844 */       return ptr;
/*      */     } 
/* 1846 */     if (escCode != -2) {
/* 1847 */       if (ptr > 5 && ptr < end) {
/* 1848 */         ptr -= 6;
/* 1849 */         buffer[ptr++] = '\\';
/* 1850 */         buffer[ptr++] = 'u';
/*      */         
/* 1852 */         if (ch > 'ÿ') {
/* 1853 */           int hi = ch >> 8 & 0xFF;
/* 1854 */           buffer[ptr++] = HEX_CHARS[hi >> 4];
/* 1855 */           buffer[ptr++] = HEX_CHARS[hi & 0xF];
/* 1856 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1858 */           buffer[ptr++] = '0';
/* 1859 */           buffer[ptr++] = '0';
/*      */         } 
/* 1861 */         buffer[ptr++] = HEX_CHARS[ch >> 4];
/* 1862 */         buffer[ptr] = HEX_CHARS[ch & 0xF];
/* 1863 */         ptr -= 5;
/*      */       } else {
/*      */         
/* 1866 */         char[] ent = this._entityBuffer;
/* 1867 */         if (ent == null) {
/* 1868 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1870 */         this._outputHead = this._outputTail;
/* 1871 */         if (ch > 'ÿ') {
/* 1872 */           int hi = ch >> 8 & 0xFF;
/* 1873 */           int lo = ch & 0xFF;
/* 1874 */           ent[10] = HEX_CHARS[hi >> 4];
/* 1875 */           ent[11] = HEX_CHARS[hi & 0xF];
/* 1876 */           ent[12] = HEX_CHARS[lo >> 4];
/* 1877 */           ent[13] = HEX_CHARS[lo & 0xF];
/* 1878 */           this._writer.write(ent, 8, 6);
/*      */         } else {
/* 1880 */           ent[6] = HEX_CHARS[ch >> 4];
/* 1881 */           ent[7] = HEX_CHARS[ch & 0xF];
/* 1882 */           this._writer.write(ent, 2, 6);
/*      */         } 
/*      */       } 
/* 1885 */       return ptr;
/*      */     } 
/*      */     
/* 1888 */     if (this._currentEscape == null) {
/* 1889 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1891 */       escape = this._currentEscape.getValue();
/* 1892 */       this._currentEscape = null;
/*      */     } 
/* 1894 */     int len = escape.length();
/* 1895 */     if (ptr >= len && ptr < end) {
/* 1896 */       ptr -= len;
/* 1897 */       escape.getChars(0, len, buffer, ptr);
/*      */     } else {
/* 1899 */       this._writer.write(escape);
/*      */     } 
/* 1901 */     return ptr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _appendCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1911 */     if (escCode >= 0) {
/* 1912 */       if (this._outputTail + 2 > this._outputEnd) {
/* 1913 */         _flushBuffer();
/*      */       }
/* 1915 */       this._outputBuffer[this._outputTail++] = '\\';
/* 1916 */       this._outputBuffer[this._outputTail++] = (char)escCode;
/*      */       return;
/*      */     } 
/* 1919 */     if (escCode != -2) {
/* 1920 */       if (this._outputTail + 5 >= this._outputEnd) {
/* 1921 */         _flushBuffer();
/*      */       }
/* 1923 */       int ptr = this._outputTail;
/* 1924 */       char[] buf = this._outputBuffer;
/* 1925 */       buf[ptr++] = '\\';
/* 1926 */       buf[ptr++] = 'u';
/*      */       
/* 1928 */       if (ch > 'ÿ') {
/* 1929 */         int hi = ch >> 8 & 0xFF;
/* 1930 */         buf[ptr++] = HEX_CHARS[hi >> 4];
/* 1931 */         buf[ptr++] = HEX_CHARS[hi & 0xF];
/* 1932 */         ch = (char)(ch & 0xFF);
/*      */       } else {
/* 1934 */         buf[ptr++] = '0';
/* 1935 */         buf[ptr++] = '0';
/*      */       } 
/* 1937 */       buf[ptr++] = HEX_CHARS[ch >> 4];
/* 1938 */       buf[ptr++] = HEX_CHARS[ch & 0xF];
/* 1939 */       this._outputTail = ptr;
/*      */       
/*      */       return;
/*      */     } 
/* 1943 */     if (this._currentEscape == null) {
/* 1944 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1946 */       escape = this._currentEscape.getValue();
/* 1947 */       this._currentEscape = null;
/*      */     } 
/* 1949 */     int len = escape.length();
/* 1950 */     if (this._outputTail + len > this._outputEnd) {
/* 1951 */       _flushBuffer();
/* 1952 */       if (len > this._outputEnd) {
/* 1953 */         this._writer.write(escape);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1957 */     escape.getChars(0, len, this._outputBuffer, this._outputTail);
/* 1958 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */   
/*      */   private char[] _allocateEntityBuffer() {
/* 1963 */     char[] buf = new char[14];
/*      */     
/* 1965 */     buf[0] = '\\';
/*      */     
/* 1967 */     buf[2] = '\\';
/* 1968 */     buf[3] = 'u';
/* 1969 */     buf[4] = '0';
/* 1970 */     buf[5] = '0';
/*      */     
/* 1972 */     buf[8] = '\\';
/* 1973 */     buf[9] = 'u';
/* 1974 */     this._entityBuffer = buf;
/* 1975 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private char[] _allocateCopyBuffer() {
/* 1982 */     if (this._copyBuffer == null) {
/* 1983 */       this._copyBuffer = this._ioContext.allocNameCopyBuffer(2000);
/*      */     }
/* 1985 */     return this._copyBuffer;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _flushBuffer() throws IOException {
/* 1990 */     int len = this._outputTail - this._outputHead;
/* 1991 */     if (len > 0) {
/* 1992 */       int offset = this._outputHead;
/* 1993 */       this._outputTail = this._outputHead = 0;
/* 1994 */       this._writer.write(this._outputBuffer, offset, len);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\WriterBasedJsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */