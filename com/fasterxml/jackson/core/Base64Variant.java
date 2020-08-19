/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import java.io.Serializable;
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
/*     */ public final class Base64Variant
/*     */   implements Serializable
/*     */ {
/*     */   private static final int INT_SPACE = 32;
/*     */   private static final long serialVersionUID = 1L;
/*     */   static final char PADDING_CHAR_NONE = '\000';
/*     */   public static final int BASE64_VALUE_INVALID = -1;
/*     */   public static final int BASE64_VALUE_PADDING = -2;
/*  55 */   private final transient int[] _asciiToBase64 = new int[128];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private final transient char[] _base64ToAsciiC = new char[64];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private final transient byte[] _base64ToAsciiB = new byte[64];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String _name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final transient boolean _usesPadding;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final transient char _paddingChar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final transient int _maxLineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant(String name, String base64Alphabet, boolean usesPadding, char paddingChar, int maxLineLength) {
/* 113 */     this._name = name;
/* 114 */     this._usesPadding = usesPadding;
/* 115 */     this._paddingChar = paddingChar;
/* 116 */     this._maxLineLength = maxLineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     int alphaLen = base64Alphabet.length();
/* 122 */     if (alphaLen != 64) {
/* 123 */       throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
/*     */     }
/*     */ 
/*     */     
/* 127 */     base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
/* 128 */     Arrays.fill(this._asciiToBase64, -1);
/* 129 */     for (int i = 0; i < alphaLen; i++) {
/* 130 */       char alpha = this._base64ToAsciiC[i];
/* 131 */       this._base64ToAsciiB[i] = (byte)alpha;
/* 132 */       this._asciiToBase64[alpha] = i;
/*     */     } 
/*     */ 
/*     */     
/* 136 */     if (usesPadding) {
/* 137 */       this._asciiToBase64[paddingChar] = -2;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant(Base64Variant base, String name, int maxLineLength) {
/* 148 */     this(base, name, base._usesPadding, base._paddingChar, maxLineLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant(Base64Variant base, String name, boolean usesPadding, char paddingChar, int maxLineLength) {
/* 158 */     this._name = name;
/* 159 */     byte[] srcB = base._base64ToAsciiB;
/* 160 */     System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
/* 161 */     char[] srcC = base._base64ToAsciiC;
/* 162 */     System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
/* 163 */     int[] srcV = base._asciiToBase64;
/* 164 */     System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
/*     */     
/* 166 */     this._usesPadding = usesPadding;
/* 167 */     this._paddingChar = paddingChar;
/* 168 */     this._maxLineLength = maxLineLength;
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
/*     */   protected Object readResolve() {
/* 182 */     return Base64Variants.valueOf(this._name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 191 */     return this._name;
/*     */   }
/* 193 */   public boolean usesPadding() { return this._usesPadding; }
/* 194 */   public boolean usesPaddingChar(char c) { return (c == this._paddingChar); }
/* 195 */   public boolean usesPaddingChar(int ch) { return (ch == this._paddingChar); }
/* 196 */   public char getPaddingChar() { return this._paddingChar; } public byte getPaddingByte() {
/* 197 */     return (byte)this._paddingChar;
/*     */   } public int getMaxLineLength() {
/* 199 */     return this._maxLineLength;
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
/*     */   public int decodeBase64Char(char c) {
/* 212 */     int ch = c;
/* 213 */     return (ch <= 127) ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decodeBase64Char(int ch) {
/* 218 */     return (ch <= 127) ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decodeBase64Byte(byte b) {
/* 223 */     int ch = b;
/*     */     
/* 225 */     if (ch < 0) {
/* 226 */       return -1;
/*     */     }
/* 228 */     return this._asciiToBase64[ch];
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
/*     */   public char encodeBase64BitsAsChar(int value) {
/* 242 */     return this._base64ToAsciiC[value];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encodeBase64Chunk(int b24, char[] buffer, int ptr) {
/* 251 */     buffer[ptr++] = this._base64ToAsciiC[b24 >> 18 & 0x3F];
/* 252 */     buffer[ptr++] = this._base64ToAsciiC[b24 >> 12 & 0x3F];
/* 253 */     buffer[ptr++] = this._base64ToAsciiC[b24 >> 6 & 0x3F];
/* 254 */     buffer[ptr++] = this._base64ToAsciiC[b24 & 0x3F];
/* 255 */     return ptr;
/*     */   }
/*     */ 
/*     */   
/*     */   public void encodeBase64Chunk(StringBuilder sb, int b24) {
/* 260 */     sb.append(this._base64ToAsciiC[b24 >> 18 & 0x3F]);
/* 261 */     sb.append(this._base64ToAsciiC[b24 >> 12 & 0x3F]);
/* 262 */     sb.append(this._base64ToAsciiC[b24 >> 6 & 0x3F]);
/* 263 */     sb.append(this._base64ToAsciiC[b24 & 0x3F]);
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
/* 276 */     buffer[outPtr++] = this._base64ToAsciiC[bits >> 18 & 0x3F];
/* 277 */     buffer[outPtr++] = this._base64ToAsciiC[bits >> 12 & 0x3F];
/* 278 */     if (this._usesPadding) {
/* 279 */       buffer[outPtr++] = (outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar;
/*     */       
/* 281 */       buffer[outPtr++] = this._paddingChar;
/*     */     }
/* 283 */     else if (outputBytes == 2) {
/* 284 */       buffer[outPtr++] = this._base64ToAsciiC[bits >> 6 & 0x3F];
/*     */     } 
/*     */     
/* 287 */     return outPtr;
/*     */   }
/*     */ 
/*     */   
/*     */   public void encodeBase64Partial(StringBuilder sb, int bits, int outputBytes) {
/* 292 */     sb.append(this._base64ToAsciiC[bits >> 18 & 0x3F]);
/* 293 */     sb.append(this._base64ToAsciiC[bits >> 12 & 0x3F]);
/* 294 */     if (this._usesPadding) {
/* 295 */       sb.append((outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar);
/*     */       
/* 297 */       sb.append(this._paddingChar);
/*     */     }
/* 299 */     else if (outputBytes == 2) {
/* 300 */       sb.append(this._base64ToAsciiC[bits >> 6 & 0x3F]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte encodeBase64BitsAsByte(int value) {
/* 308 */     return this._base64ToAsciiB[value];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encodeBase64Chunk(int b24, byte[] buffer, int ptr) {
/* 317 */     buffer[ptr++] = this._base64ToAsciiB[b24 >> 18 & 0x3F];
/* 318 */     buffer[ptr++] = this._base64ToAsciiB[b24 >> 12 & 0x3F];
/* 319 */     buffer[ptr++] = this._base64ToAsciiB[b24 >> 6 & 0x3F];
/* 320 */     buffer[ptr++] = this._base64ToAsciiB[b24 & 0x3F];
/* 321 */     return ptr;
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
/* 334 */     buffer[outPtr++] = this._base64ToAsciiB[bits >> 18 & 0x3F];
/* 335 */     buffer[outPtr++] = this._base64ToAsciiB[bits >> 12 & 0x3F];
/* 336 */     if (this._usesPadding) {
/* 337 */       byte pb = (byte)this._paddingChar;
/* 338 */       buffer[outPtr++] = (outputBytes == 2) ? this._base64ToAsciiB[bits >> 6 & 0x3F] : pb;
/*     */       
/* 340 */       buffer[outPtr++] = pb;
/*     */     }
/* 342 */     else if (outputBytes == 2) {
/* 343 */       buffer[outPtr++] = this._base64ToAsciiB[bits >> 6 & 0x3F];
/*     */     } 
/*     */     
/* 346 */     return outPtr;
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
/*     */   public String encode(byte[] input) {
/* 365 */     return encode(input, false);
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
/*     */   public String encode(byte[] input, boolean addQuotes) {
/* 379 */     int inputEnd = input.length;
/* 380 */     StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
/* 381 */     if (addQuotes) {
/* 382 */       sb.append('"');
/*     */     }
/*     */     
/* 385 */     int chunksBeforeLF = getMaxLineLength() >> 2;
/*     */ 
/*     */     
/* 388 */     int inputPtr = 0;
/* 389 */     int safeInputEnd = inputEnd - 3;
/*     */     
/* 391 */     while (inputPtr <= safeInputEnd) {
/*     */       
/* 393 */       int b24 = input[inputPtr++] << 8;
/* 394 */       b24 |= input[inputPtr++] & 0xFF;
/* 395 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 396 */       encodeBase64Chunk(sb, b24);
/* 397 */       if (--chunksBeforeLF <= 0) {
/*     */         
/* 399 */         sb.append('\\');
/* 400 */         sb.append('n');
/* 401 */         chunksBeforeLF = getMaxLineLength() >> 2;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 406 */     int inputLeft = inputEnd - inputPtr;
/* 407 */     if (inputLeft > 0) {
/* 408 */       int b24 = input[inputPtr++] << 16;
/* 409 */       if (inputLeft == 2) {
/* 410 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*     */       }
/* 412 */       encodeBase64Partial(sb, b24, inputLeft);
/*     */     } 
/*     */     
/* 415 */     if (addQuotes) {
/* 416 */       sb.append('"');
/*     */     }
/* 418 */     return sb.toString();
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
/*     */   public String encode(byte[] input, boolean addQuotes, String linefeed) {
/* 433 */     int inputEnd = input.length;
/* 434 */     StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
/* 435 */     if (addQuotes) {
/* 436 */       sb.append('"');
/*     */     }
/*     */     
/* 439 */     int chunksBeforeLF = getMaxLineLength() >> 2;
/*     */     
/* 441 */     int inputPtr = 0;
/* 442 */     int safeInputEnd = inputEnd - 3;
/*     */     
/* 444 */     while (inputPtr <= safeInputEnd) {
/* 445 */       int b24 = input[inputPtr++] << 8;
/* 446 */       b24 |= input[inputPtr++] & 0xFF;
/* 447 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 448 */       encodeBase64Chunk(sb, b24);
/* 449 */       if (--chunksBeforeLF <= 0) {
/* 450 */         sb.append(linefeed);
/* 451 */         chunksBeforeLF = getMaxLineLength() >> 2;
/*     */       } 
/*     */     } 
/* 454 */     int inputLeft = inputEnd - inputPtr;
/* 455 */     if (inputLeft > 0) {
/* 456 */       int b24 = input[inputPtr++] << 16;
/* 457 */       if (inputLeft == 2) {
/* 458 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*     */       }
/* 460 */       encodeBase64Partial(sb, b24, inputLeft);
/*     */     } 
/*     */     
/* 463 */     if (addQuotes) {
/* 464 */       sb.append('"');
/*     */     }
/* 466 */     return sb.toString();
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
/*     */   public byte[] decode(String input) throws IllegalArgumentException {
/* 482 */     ByteArrayBuilder b = new ByteArrayBuilder();
/* 483 */     decode(input, b);
/* 484 */     return b.toByteArray();
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
/*     */   public void decode(String str, ByteArrayBuilder builder) throws IllegalArgumentException {
/* 502 */     int ptr = 0;
/* 503 */     int len = str.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 510 */     while (ptr < len) {
/*     */ 
/*     */       
/* 513 */       char ch = str.charAt(ptr++);
/* 514 */       if (ch > ' ') {
/* 515 */         int bits = decodeBase64Char(ch);
/* 516 */         if (bits < 0) {
/* 517 */           _reportInvalidBase64(ch, 0, null);
/*     */         }
/* 519 */         int decodedData = bits;
/*     */         
/* 521 */         if (ptr >= len) {
/* 522 */           _reportBase64EOF();
/*     */         }
/* 524 */         ch = str.charAt(ptr++);
/* 525 */         bits = decodeBase64Char(ch);
/* 526 */         if (bits < 0) {
/* 527 */           _reportInvalidBase64(ch, 1, null);
/*     */         }
/* 529 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 531 */         if (ptr >= len) {
/*     */           
/* 533 */           if (!usesPadding()) {
/* 534 */             decodedData >>= 4;
/* 535 */             builder.append(decodedData);
/*     */             break;
/*     */           } 
/* 538 */           _reportBase64EOF();
/*     */         } 
/* 540 */         ch = str.charAt(ptr++);
/* 541 */         bits = decodeBase64Char(ch);
/*     */ 
/*     */         
/* 544 */         if (bits < 0) {
/* 545 */           if (bits != -2) {
/* 546 */             _reportInvalidBase64(ch, 2, null);
/*     */           }
/*     */           
/* 549 */           if (ptr >= len) {
/* 550 */             _reportBase64EOF();
/*     */           }
/* 552 */           ch = str.charAt(ptr++);
/* 553 */           if (!usesPaddingChar(ch)) {
/* 554 */             _reportInvalidBase64(ch, 3, "expected padding character '" + getPaddingChar() + "'");
/*     */           }
/*     */           
/* 557 */           decodedData >>= 4;
/* 558 */           builder.append(decodedData);
/*     */           
/*     */           continue;
/*     */         } 
/* 562 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 564 */         if (ptr >= len) {
/*     */           
/* 566 */           if (!usesPadding()) {
/* 567 */             decodedData >>= 2;
/* 568 */             builder.appendTwoBytes(decodedData);
/*     */             break;
/*     */           } 
/* 571 */           _reportBase64EOF();
/*     */         } 
/* 573 */         ch = str.charAt(ptr++);
/* 574 */         bits = decodeBase64Char(ch);
/* 575 */         if (bits < 0) {
/* 576 */           if (bits != -2) {
/* 577 */             _reportInvalidBase64(ch, 3, null);
/*     */           }
/* 579 */           decodedData >>= 2;
/* 580 */           builder.appendTwoBytes(decodedData);
/*     */           continue;
/*     */         } 
/* 583 */         decodedData = decodedData << 6 | bits;
/* 584 */         builder.appendThreeBytes(decodedData);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 596 */     return this._name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 601 */     return (o == this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 606 */     return this._name.hashCode();
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
/*     */   protected void _reportInvalidBase64(char ch, int bindex, String msg) throws IllegalArgumentException {
/*     */     String base;
/* 623 */     if (ch <= ' ') {
/* 624 */       base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
/* 625 */     } else if (usesPaddingChar(ch)) {
/* 626 */       base = "Unexpected padding character ('" + getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
/* 627 */     } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
/*     */       
/* 629 */       base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */     } else {
/* 631 */       base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */     } 
/* 633 */     if (msg != null) {
/* 634 */       base = base + ": " + msg;
/*     */     }
/* 636 */     throw new IllegalArgumentException(base);
/*     */   }
/*     */   
/*     */   protected void _reportBase64EOF() throws IllegalArgumentException {
/* 640 */     throw new IllegalArgumentException(missingPaddingMessage());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String missingPaddingMessage() {
/* 650 */     return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects padding (one or more '%c' characters) at the end", new Object[] {
/* 651 */           getName(), Character.valueOf(getPaddingChar())
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\Base64Variant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */