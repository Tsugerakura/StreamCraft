/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.core.util.TextBuffer;
/*     */ import java.util.Arrays;
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
/*     */ public final class JsonStringEncoder
/*     */ {
/*  24 */   private static final char[] HC = CharTypes.copyHexChars();
/*     */   
/*  26 */   private static final byte[] HB = CharTypes.copyHexBytes();
/*     */ 
/*     */   
/*     */   private static final int SURR1_FIRST = 55296;
/*     */ 
/*     */   
/*     */   private static final int SURR1_LAST = 56319;
/*     */ 
/*     */   
/*     */   private static final int SURR2_FIRST = 56320;
/*     */   
/*     */   private static final int SURR2_LAST = 57343;
/*     */   
/*     */   private static final int INITIAL_CHAR_BUFFER_SIZE = 120;
/*     */   
/*     */   private static final int INITIAL_BYTE_BUFFER_SIZE = 200;
/*     */   
/*  43 */   private static final JsonStringEncoder instance = new JsonStringEncoder();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonStringEncoder getInstance() {
/*  52 */     return instance;
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
/*     */   public char[] quoteAsString(String input) {
/*  67 */     char[] outputBuffer = new char[120];
/*  68 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/*  69 */     int escCodeCount = escCodes.length;
/*  70 */     int inPtr = 0;
/*  71 */     int inputLen = input.length();
/*  72 */     TextBuffer textBuffer = null;
/*  73 */     int outPtr = 0;
/*  74 */     char[] qbuf = null;
/*     */ 
/*     */     
/*  77 */     while (inPtr < inputLen) {
/*     */       
/*     */       label35: while (true) {
/*  80 */         char d, c = input.charAt(inPtr);
/*  81 */         if (c < escCodeCount && escCodes[c] != 0)
/*     */         
/*     */         { 
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
/*  97 */           if (qbuf == null) {
/*  98 */             qbuf = _qbuf();
/*     */           }
/* 100 */           d = input.charAt(inPtr++);
/* 101 */           int escCode = escCodes[d];
/*     */ 
/*     */           
/* 104 */           int length = (escCode < 0) ? _appendNumeric(d, qbuf) : _appendNamed(escCode, qbuf);
/*     */           
/* 106 */           if (outPtr + length > outputBuffer.length) {
/* 107 */             int first = outputBuffer.length - outPtr;
/* 108 */             if (first > 0) {
/* 109 */               System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
/*     */             }
/* 111 */             if (textBuffer == null) {
/* 112 */               textBuffer = TextBuffer.fromInitial(outputBuffer); break label35;
/*     */             } 
/* 114 */             outputBuffer = textBuffer.finishCurrentSegment();
/* 115 */             int second = length - first;
/* 116 */             System.arraycopy(qbuf, first, outputBuffer, 0, second);
/* 117 */             outPtr = second; continue;
/*     */           } 
/* 119 */           System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
/* 120 */           outPtr += length; continue; }  if (outPtr >= outputBuffer.length) { if (textBuffer == null)
/*     */             textBuffer = TextBuffer.fromInitial(outputBuffer);  outputBuffer = textBuffer.finishCurrentSegment(); outPtr = 0; }  outputBuffer[outPtr++] = d; if (++inPtr >= inputLen)
/*     */           break; 
/*     */       } 
/* 124 */     }  if (textBuffer == null) {
/* 125 */       return Arrays.copyOfRange(outputBuffer, 0, outPtr);
/*     */     }
/* 127 */     textBuffer.setCurrentLength(outPtr);
/* 128 */     return textBuffer.contentsAsArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] quoteAsString(CharSequence input) {
/* 139 */     if (input instanceof String) {
/* 140 */       return quoteAsString((String)input);
/*     */     }
/*     */     
/* 143 */     TextBuffer textBuffer = null;
/*     */     
/* 145 */     char[] outputBuffer = new char[120];
/* 146 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/* 147 */     int escCodeCount = escCodes.length;
/* 148 */     int inPtr = 0;
/* 149 */     int inputLen = input.length();
/* 150 */     int outPtr = 0;
/* 151 */     char[] qbuf = null;
/*     */ 
/*     */     
/* 154 */     while (inPtr < inputLen) {
/*     */       
/*     */       label37: while (true) {
/* 157 */         char d, c = input.charAt(inPtr);
/* 158 */         if (c < escCodeCount && escCodes[c] != 0)
/*     */         
/*     */         { 
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
/* 174 */           if (qbuf == null) {
/* 175 */             qbuf = _qbuf();
/*     */           }
/* 177 */           d = input.charAt(inPtr++);
/* 178 */           int escCode = escCodes[d];
/*     */ 
/*     */           
/* 181 */           int length = (escCode < 0) ? _appendNumeric(d, qbuf) : _appendNamed(escCode, qbuf);
/*     */           
/* 183 */           if (outPtr + length > outputBuffer.length) {
/* 184 */             int first = outputBuffer.length - outPtr;
/* 185 */             if (first > 0) {
/* 186 */               System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
/*     */             }
/* 188 */             if (textBuffer == null) {
/* 189 */               textBuffer = TextBuffer.fromInitial(outputBuffer); break label37;
/*     */             } 
/* 191 */             outputBuffer = textBuffer.finishCurrentSegment();
/* 192 */             int second = length - first;
/* 193 */             System.arraycopy(qbuf, first, outputBuffer, 0, second);
/* 194 */             outPtr = second; continue;
/*     */           } 
/* 196 */           System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
/* 197 */           outPtr += length; continue; }  if (outPtr >= outputBuffer.length) { if (textBuffer == null)
/*     */             textBuffer = TextBuffer.fromInitial(outputBuffer);  outputBuffer = textBuffer.finishCurrentSegment(); outPtr = 0; }  outputBuffer[outPtr++] = d; if (++inPtr >= inputLen)
/*     */           break; 
/*     */       } 
/* 201 */     }  if (textBuffer == null) {
/* 202 */       return Arrays.copyOfRange(outputBuffer, 0, outPtr);
/*     */     }
/* 204 */     textBuffer.setCurrentLength(outPtr);
/* 205 */     return textBuffer.contentsAsArray();
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
/*     */   public void quoteAsString(CharSequence input, StringBuilder output) {
/* 217 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/* 218 */     int escCodeCount = escCodes.length;
/* 219 */     int inPtr = 0;
/* 220 */     int inputLen = input.length();
/* 221 */     char[] qbuf = null;
/*     */ 
/*     */     
/* 224 */     while (inPtr < inputLen) {
/*     */       
/*     */       while (true) {
/* 227 */         char d, c = input.charAt(inPtr);
/* 228 */         if (c < escCodeCount && escCodes[c] != 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 237 */           if (qbuf == null) {
/* 238 */             qbuf = _qbuf();
/*     */           }
/* 240 */           d = input.charAt(inPtr++);
/* 241 */           int escCode = escCodes[d];
/* 242 */           if (escCode < 0);
/*     */           
/* 244 */           int length = _appendNamed(escCode, qbuf);
/* 245 */           output.append(qbuf, 0, length);
/*     */           continue;
/*     */         } 
/*     */         output.append(d);
/*     */         if (++inPtr >= inputLen)
/*     */           break; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public byte[] quoteAsUTF8(String text) {
/* 256 */     int inputPtr = 0;
/* 257 */     int inputEnd = text.length();
/* 258 */     int outputPtr = 0;
/* 259 */     byte[] outputBuffer = new byte[200];
/* 260 */     ByteArrayBuilder bb = null;
/*     */ 
/*     */     
/* 263 */     while (inputPtr < inputEnd)
/* 264 */     { int[] escCodes = CharTypes.get7BitOutputEscapes();
/*     */ 
/*     */       
/*     */       label55: while (true)
/* 268 */       { int ch = text.charAt(inputPtr);
/* 269 */         if (ch > 127 || escCodes[ch] != 0)
/*     */         
/*     */         { 
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
/* 284 */           if (bb == null) {
/* 285 */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */           }
/* 287 */           if (outputPtr >= outputBuffer.length) {
/* 288 */             outputBuffer = bb.finishCurrentSegment();
/* 289 */             outputPtr = 0;
/*     */           } 
/*     */           
/* 292 */           ch = text.charAt(inputPtr++);
/* 293 */           if (ch <= 127) {
/* 294 */             int escape = escCodes[ch];
/*     */             
/* 296 */             outputPtr = _appendByte(ch, escape, bb, outputPtr);
/* 297 */             outputBuffer = bb.getCurrentSegment();
/*     */             continue;
/*     */           } 
/* 300 */           if (ch <= 2047) {
/* 301 */             outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 302 */             ch = 0x80 | ch & 0x3F;
/*     */           
/*     */           }
/* 305 */           else if (ch < 55296 || ch > 57343) {
/* 306 */             outputBuffer[outputPtr++] = (byte)(0xE0 | ch >> 12);
/* 307 */             if (outputPtr >= outputBuffer.length) {
/* 308 */               outputBuffer = bb.finishCurrentSegment();
/* 309 */               outputPtr = 0;
/*     */             } 
/* 311 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 312 */             ch = 0x80 | ch & 0x3F;
/*     */           } else {
/* 314 */             if (ch > 56319) {
/* 315 */               _illegal(ch);
/*     */             }
/*     */             
/* 318 */             if (inputPtr >= inputEnd) {
/* 319 */               _illegal(ch);
/*     */             }
/* 321 */             ch = _convert(ch, text.charAt(inputPtr++));
/* 322 */             if (ch > 1114111) {
/* 323 */               _illegal(ch);
/*     */             }
/* 325 */             outputBuffer[outputPtr++] = (byte)(0xF0 | ch >> 18);
/* 326 */             if (outputPtr >= outputBuffer.length) {
/* 327 */               outputBuffer = bb.finishCurrentSegment();
/* 328 */               outputPtr = 0;
/*     */             } 
/* 330 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 12 & 0x3F);
/* 331 */             if (outputPtr >= outputBuffer.length) {
/* 332 */               outputBuffer = bb.finishCurrentSegment();
/* 333 */               outputPtr = 0;
/*     */             } 
/* 335 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 336 */             ch = 0x80 | ch & 0x3F;
/*     */           } 
/*     */           
/* 339 */           if (outputPtr >= outputBuffer.length) {
/* 340 */             outputBuffer = bb.finishCurrentSegment();
/* 341 */             outputPtr = 0; break label55;
/*     */           } 
/* 343 */           outputBuffer[outputPtr++] = (byte)ch; continue; }  if (outputPtr >= outputBuffer.length) { if (bb == null)
/*     */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);  outputBuffer = bb.finishCurrentSegment(); outputPtr = 0; }  outputBuffer[outputPtr++] = (byte)ch; if (++inputPtr >= inputEnd)
/* 345 */           break;  }  }  if (bb == null) {
/* 346 */       return Arrays.copyOfRange(outputBuffer, 0, outputPtr);
/*     */     }
/* 348 */     return bb.completeAndCoalesce(outputPtr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encodeAsUTF8(String text) {
/* 358 */     int inputPtr = 0;
/* 359 */     int inputEnd = text.length();
/* 360 */     int outputPtr = 0;
/* 361 */     byte[] outputBuffer = new byte[200];
/* 362 */     int outputEnd = outputBuffer.length;
/* 363 */     ByteArrayBuilder bb = null;
/*     */ 
/*     */     
/* 366 */     label51: while (inputPtr < inputEnd) {
/* 367 */       int c = text.charAt(inputPtr++);
/*     */ 
/*     */       
/* 370 */       while (c <= 127) {
/* 371 */         if (outputPtr >= outputEnd) {
/* 372 */           if (bb == null) {
/* 373 */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */           }
/* 375 */           outputBuffer = bb.finishCurrentSegment();
/* 376 */           outputEnd = outputBuffer.length;
/* 377 */           outputPtr = 0;
/*     */         } 
/* 379 */         outputBuffer[outputPtr++] = (byte)c;
/* 380 */         if (inputPtr >= inputEnd) {
/*     */           break label51;
/*     */         }
/* 383 */         c = text.charAt(inputPtr++);
/*     */       } 
/*     */ 
/*     */       
/* 387 */       if (bb == null) {
/* 388 */         bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */       }
/* 390 */       if (outputPtr >= outputEnd) {
/* 391 */         outputBuffer = bb.finishCurrentSegment();
/* 392 */         outputEnd = outputBuffer.length;
/* 393 */         outputPtr = 0;
/*     */       } 
/* 395 */       if (c < 2048) {
/* 396 */         outputBuffer[outputPtr++] = (byte)(0xC0 | c >> 6);
/*     */       
/*     */       }
/* 399 */       else if (c < 55296 || c > 57343) {
/* 400 */         outputBuffer[outputPtr++] = (byte)(0xE0 | c >> 12);
/* 401 */         if (outputPtr >= outputEnd) {
/* 402 */           outputBuffer = bb.finishCurrentSegment();
/* 403 */           outputEnd = outputBuffer.length;
/* 404 */           outputPtr = 0;
/*     */         } 
/* 406 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } else {
/* 408 */         if (c > 56319) {
/* 409 */           _illegal(c);
/*     */         }
/*     */         
/* 412 */         if (inputPtr >= inputEnd) {
/* 413 */           _illegal(c);
/*     */         }
/* 415 */         c = _convert(c, text.charAt(inputPtr++));
/* 416 */         if (c > 1114111) {
/* 417 */           _illegal(c);
/*     */         }
/* 419 */         outputBuffer[outputPtr++] = (byte)(0xF0 | c >> 18);
/* 420 */         if (outputPtr >= outputEnd) {
/* 421 */           outputBuffer = bb.finishCurrentSegment();
/* 422 */           outputEnd = outputBuffer.length;
/* 423 */           outputPtr = 0;
/*     */         } 
/* 425 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 426 */         if (outputPtr >= outputEnd) {
/* 427 */           outputBuffer = bb.finishCurrentSegment();
/* 428 */           outputEnd = outputBuffer.length;
/* 429 */           outputPtr = 0;
/*     */         } 
/* 431 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } 
/*     */       
/* 434 */       if (outputPtr >= outputEnd) {
/* 435 */         outputBuffer = bb.finishCurrentSegment();
/* 436 */         outputEnd = outputBuffer.length;
/* 437 */         outputPtr = 0;
/*     */       } 
/* 439 */       outputBuffer[outputPtr++] = (byte)(0x80 | c & 0x3F);
/*     */     } 
/* 441 */     if (bb == null) {
/* 442 */       return Arrays.copyOfRange(outputBuffer, 0, outputPtr);
/*     */     }
/* 444 */     return bb.completeAndCoalesce(outputPtr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _qbuf() {
/* 454 */     char[] qbuf = new char[6];
/* 455 */     qbuf[0] = '\\';
/* 456 */     qbuf[2] = '0';
/* 457 */     qbuf[3] = '0';
/* 458 */     return qbuf;
/*     */   }
/*     */   
/*     */   private int _appendNumeric(int value, char[] qbuf) {
/* 462 */     qbuf[1] = 'u';
/*     */     
/* 464 */     qbuf[4] = HC[value >> 4];
/* 465 */     qbuf[5] = HC[value & 0xF];
/* 466 */     return 6;
/*     */   }
/*     */   
/*     */   private int _appendNamed(int esc, char[] qbuf) {
/* 470 */     qbuf[1] = (char)esc;
/* 471 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   private int _appendByte(int ch, int esc, ByteArrayBuilder bb, int ptr) {
/* 476 */     bb.setCurrentSegmentLength(ptr);
/* 477 */     bb.append(92);
/* 478 */     if (esc < 0) {
/* 479 */       bb.append(117);
/* 480 */       if (ch > 255) {
/* 481 */         int hi = ch >> 8;
/* 482 */         bb.append(HB[hi >> 4]);
/* 483 */         bb.append(HB[hi & 0xF]);
/* 484 */         ch &= 0xFF;
/*     */       } else {
/* 486 */         bb.append(48);
/* 487 */         bb.append(48);
/*     */       } 
/* 489 */       bb.append(HB[ch >> 4]);
/* 490 */       bb.append(HB[ch & 0xF]);
/*     */     } else {
/* 492 */       bb.append((byte)esc);
/*     */     } 
/* 494 */     return bb.getCurrentSegmentLength();
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _convert(int p1, int p2) {
/* 499 */     if (p2 < 56320 || p2 > 57343) {
/* 500 */       throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
/*     */     }
/* 502 */     return 65536 + (p1 - 55296 << 10) + p2 - 56320;
/*     */   }
/*     */   
/*     */   private static void _illegal(int c) {
/* 506 */     throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\io\JsonStringEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */