/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonEncoding;
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.format.InputAccessor;
/*     */ import com.fasterxml.jackson.core.format.MatchStrength;
/*     */ import com.fasterxml.jackson.core.io.IOContext;
/*     */ import com.fasterxml.jackson.core.io.MergedStream;
/*     */ import com.fasterxml.jackson.core.io.UTF32Reader;
/*     */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*     */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.DataInput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
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
/*     */ public final class ByteSourceJsonBootstrapper
/*     */ {
/*     */   public static final byte UTF8_BOM_1 = -17;
/*     */   public static final byte UTF8_BOM_2 = -69;
/*     */   public static final byte UTF8_BOM_3 = -65;
/*     */   private final IOContext _context;
/*     */   private final InputStream _in;
/*     */   private final byte[] _inputBuffer;
/*     */   private int _inputPtr;
/*     */   private int _inputEnd;
/*     */   private final boolean _bufferRecyclable;
/*     */   private boolean _bigEndian = true;
/*     */   private int _bytesPerChar;
/*     */   
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, InputStream in) {
/*  88 */     this._context = ctxt;
/*  89 */     this._in = in;
/*  90 */     this._inputBuffer = ctxt.allocReadIOBuffer();
/*  91 */     this._inputEnd = this._inputPtr = 0;
/*     */     
/*  93 */     this._bufferRecyclable = true;
/*     */   }
/*     */   
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
/*  97 */     this._context = ctxt;
/*  98 */     this._in = null;
/*  99 */     this._inputBuffer = inputBuffer;
/* 100 */     this._inputPtr = inputStart;
/* 101 */     this._inputEnd = inputStart + inputLen;
/*     */ 
/*     */     
/* 104 */     this._bufferRecyclable = false;
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
/*     */   public JsonEncoding detectEncoding() throws IOException {
/*     */     JsonEncoding enc;
/* 120 */     boolean foundEncoding = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     if (ensureLoaded(4)) {
/* 130 */       int quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[this._inputPtr + 1] & 0xFF) << 16 | (this._inputBuffer[this._inputPtr + 2] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 3] & 0xFF;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 135 */       if (handleBOM(quad)) {
/* 136 */         foundEncoding = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 144 */       else if (checkUTF32(quad)) {
/* 145 */         foundEncoding = true;
/* 146 */       } else if (checkUTF16(quad >>> 16)) {
/* 147 */         foundEncoding = true;
/*     */       }
/*     */     
/* 150 */     } else if (ensureLoaded(2)) {
/* 151 */       int i16 = (this._inputBuffer[this._inputPtr] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 1] & 0xFF;
/*     */       
/* 153 */       if (checkUTF16(i16)) {
/* 154 */         foundEncoding = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     if (!foundEncoding)
/* 162 */     { enc = JsonEncoding.UTF8; }
/*     */     else
/* 164 */     { switch (this._bytesPerChar) { case 1:
/* 165 */           enc = JsonEncoding.UTF8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 174 */           this._context.setEncoding(enc);
/* 175 */           return enc;case 2: enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE; this._context.setEncoding(enc); return enc;case 4: enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE; this._context.setEncoding(enc); return enc; }  throw new RuntimeException("Internal error"); }  this._context.setEncoding(enc); return enc;
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
/*     */   public static int skipUTF8BOM(DataInput input) throws IOException {
/* 187 */     int b = input.readUnsignedByte();
/* 188 */     if (b != 239) {
/* 189 */       return b;
/*     */     }
/*     */ 
/*     */     
/* 193 */     b = input.readUnsignedByte();
/* 194 */     if (b != 187) {
/* 195 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF; should get 0xBB as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 198 */     b = input.readUnsignedByte();
/* 199 */     if (b != 191) {
/* 200 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF 0xBB; should get 0xBF as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 203 */     return input.readUnsignedByte();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader constructReader() throws IOException {
/*     */     InputStream in;
/*     */     MergedStream mergedStream;
/* 215 */     JsonEncoding enc = this._context.getEncoding();
/* 216 */     switch (enc.bits()) {
/*     */ 
/*     */       
/*     */       case 8:
/*     */       case 16:
/* 221 */         in = this._in;
/*     */         
/* 223 */         if (in == null) {
/* 224 */           in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 229 */         else if (this._inputPtr < this._inputEnd) {
/* 230 */           mergedStream = new MergedStream(this._context, in, this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */         } 
/*     */         
/* 233 */         return new InputStreamReader((InputStream)mergedStream, enc.getJavaName());
/*     */       
/*     */       case 32:
/* 236 */         return (Reader)new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context
/* 237 */             .getEncoding().isBigEndian());
/*     */     } 
/* 239 */     throw new RuntimeException("Internal error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser constructParser(int parserFeatures, ObjectCodec codec, ByteQuadsCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols, int factoryFeatures) throws IOException {
/* 246 */     int prevInputPtr = this._inputPtr;
/* 247 */     JsonEncoding enc = detectEncoding();
/* 248 */     int bytesProcessed = this._inputPtr - prevInputPtr;
/*     */     
/* 250 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/*     */ 
/*     */       
/* 254 */       if (JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(factoryFeatures)) {
/* 255 */         ByteQuadsCanonicalizer can = rootByteSymbols.makeChild(factoryFeatures);
/* 256 */         return (JsonParser)new UTF8StreamJsonParser(this._context, parserFeatures, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, bytesProcessed, this._bufferRecyclable);
/*     */       } 
/*     */     }
/*     */     
/* 260 */     return (JsonParser)new ReaderBasedJsonParser(this._context, parserFeatures, constructReader(), codec, rootCharSymbols
/* 261 */         .makeChild(factoryFeatures));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 281 */     if (!acc.hasMoreBytes()) {
/* 282 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/* 284 */     byte b = acc.nextByte();
/*     */     
/* 286 */     if (b == -17) {
/* 287 */       if (!acc.hasMoreBytes()) {
/* 288 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 290 */       if (acc.nextByte() != -69) {
/* 291 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 293 */       if (!acc.hasMoreBytes()) {
/* 294 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 296 */       if (acc.nextByte() != -65) {
/* 297 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 299 */       if (!acc.hasMoreBytes()) {
/* 300 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 302 */       b = acc.nextByte();
/*     */     } 
/*     */     
/* 305 */     int ch = skipSpace(acc, b);
/* 306 */     if (ch < 0) {
/* 307 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/*     */     
/* 310 */     if (ch == 123) {
/*     */       
/* 312 */       ch = skipSpace(acc);
/* 313 */       if (ch < 0) {
/* 314 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 316 */       if (ch == 34 || ch == 125) {
/* 317 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/*     */       
/* 320 */       return MatchStrength.NO_MATCH;
/*     */     } 
/*     */ 
/*     */     
/* 324 */     if (ch == 91) {
/* 325 */       ch = skipSpace(acc);
/* 326 */       if (ch < 0) {
/* 327 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/*     */       
/* 330 */       if (ch == 93 || ch == 91) {
/* 331 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/* 333 */       return MatchStrength.SOLID_MATCH;
/*     */     } 
/*     */     
/* 336 */     MatchStrength strength = MatchStrength.WEAK_MATCH;
/*     */ 
/*     */     
/* 339 */     if (ch == 34) {
/* 340 */       return strength;
/*     */     }
/* 342 */     if (ch <= 57 && ch >= 48) {
/* 343 */       return strength;
/*     */     }
/* 345 */     if (ch == 45) {
/* 346 */       ch = skipSpace(acc);
/* 347 */       if (ch < 0) {
/* 348 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 350 */       return (ch <= 57 && ch >= 48) ? strength : MatchStrength.NO_MATCH;
/*     */     } 
/*     */     
/* 353 */     if (ch == 110) {
/* 354 */       return tryMatch(acc, "ull", strength);
/*     */     }
/* 356 */     if (ch == 116) {
/* 357 */       return tryMatch(acc, "rue", strength);
/*     */     }
/* 359 */     if (ch == 102) {
/* 360 */       return tryMatch(acc, "alse", strength);
/*     */     }
/* 362 */     return MatchStrength.NO_MATCH;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength) throws IOException {
/* 368 */     for (int i = 0, len = matchStr.length(); i < len; i++) {
/* 369 */       if (!acc.hasMoreBytes()) {
/* 370 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 372 */       if (acc.nextByte() != matchStr.charAt(i)) {
/* 373 */         return MatchStrength.NO_MATCH;
/*     */       }
/*     */     } 
/* 376 */     return fullMatchStrength;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int skipSpace(InputAccessor acc) throws IOException {
/* 381 */     if (!acc.hasMoreBytes()) {
/* 382 */       return -1;
/*     */     }
/* 384 */     return skipSpace(acc, acc.nextByte());
/*     */   }
/*     */ 
/*     */   
/*     */   private static int skipSpace(InputAccessor acc, byte b) throws IOException {
/*     */     while (true) {
/* 390 */       int ch = b & 0xFF;
/* 391 */       if (ch != 32 && ch != 13 && ch != 10 && ch != 9) {
/* 392 */         return ch;
/*     */       }
/* 394 */       if (!acc.hasMoreBytes()) {
/* 395 */         return -1;
/*     */       }
/* 397 */       b = acc.nextByte();
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
/*     */ 
/*     */   
/*     */   private boolean handleBOM(int quad) throws IOException {
/* 416 */     switch (quad) {
/*     */       case 65279:
/* 418 */         this._bigEndian = true;
/* 419 */         this._inputPtr += 4;
/* 420 */         this._bytesPerChar = 4;
/* 421 */         return true;
/*     */       case -131072:
/* 423 */         this._inputPtr += 4;
/* 424 */         this._bytesPerChar = 4;
/* 425 */         this._bigEndian = false;
/* 426 */         return true;
/*     */       case 65534:
/* 428 */         reportWeirdUCS4("2143");
/*     */         break;
/*     */       case -16842752:
/* 431 */         reportWeirdUCS4("3412");
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 436 */     int msw = quad >>> 16;
/* 437 */     if (msw == 65279) {
/* 438 */       this._inputPtr += 2;
/* 439 */       this._bytesPerChar = 2;
/* 440 */       this._bigEndian = true;
/* 441 */       return true;
/*     */     } 
/* 443 */     if (msw == 65534) {
/* 444 */       this._inputPtr += 2;
/* 445 */       this._bytesPerChar = 2;
/* 446 */       this._bigEndian = false;
/* 447 */       return true;
/*     */     } 
/*     */     
/* 450 */     if (quad >>> 8 == 15711167) {
/* 451 */       this._inputPtr += 3;
/* 452 */       this._bytesPerChar = 1;
/* 453 */       this._bigEndian = true;
/* 454 */       return true;
/*     */     } 
/* 456 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkUTF32(int quad) throws IOException {
/* 464 */     if (quad >> 8 == 0) {
/* 465 */       this._bigEndian = true;
/* 466 */     } else if ((quad & 0xFFFFFF) == 0) {
/* 467 */       this._bigEndian = false;
/* 468 */     } else if ((quad & 0xFF00FFFF) == 0) {
/* 469 */       reportWeirdUCS4("3412");
/* 470 */     } else if ((quad & 0xFFFF00FF) == 0) {
/* 471 */       reportWeirdUCS4("2143");
/*     */     } else {
/*     */       
/* 474 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 478 */     this._bytesPerChar = 4;
/* 479 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkUTF16(int i16) {
/* 484 */     if ((i16 & 0xFF00) == 0) {
/* 485 */       this._bigEndian = true;
/* 486 */     } else if ((i16 & 0xFF) == 0) {
/* 487 */       this._bigEndian = false;
/*     */     } else {
/* 489 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 493 */     this._bytesPerChar = 2;
/* 494 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reportWeirdUCS4(String type) throws IOException {
/* 504 */     throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
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
/*     */   protected boolean ensureLoaded(int minimum) throws IOException {
/* 517 */     int gotten = this._inputEnd - this._inputPtr;
/* 518 */     while (gotten < minimum) {
/*     */       int count;
/*     */       
/* 521 */       if (this._in == null) {
/* 522 */         count = -1;
/*     */       } else {
/* 524 */         count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*     */       } 
/* 526 */       if (count < 1) {
/* 527 */         return false;
/*     */       }
/* 529 */       this._inputEnd += count;
/* 530 */       gotten += count;
/*     */     } 
/* 532 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\ByteSourceJsonBootstrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */