/*     */ package org.apache.commons.codec.binary;
/*     */ 
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
/*     */ public class Base64
/*     */   extends BaseNCodec
/*     */ {
/*     */   private static final int BITS_PER_ENCODED_BYTE = 6;
/*     */   private static final int BYTES_PER_UNENCODED_BLOCK = 3;
/*     */   private static final int BYTES_PER_ENCODED_BLOCK = 4;
/*  74 */   static final byte[] CHUNK_SEPARATOR = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private static final byte[] STANDARD_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
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
/*  96 */   private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
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
/* 115 */   private static final byte[] DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_6BITS = 63;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] encodeTable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 145 */   private final byte[] decodeTable = DECODE_TABLE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] lineSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int decodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int encodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64() {
/* 175 */     this(0);
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
/*     */   public Base64(boolean urlSafe) {
/* 194 */     this(76, CHUNK_SEPARATOR, urlSafe);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64(int lineLength) {
/* 217 */     this(lineLength, CHUNK_SEPARATOR);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64(int lineLength, byte[] lineSeparator) {
/* 244 */     this(lineLength, lineSeparator, false);
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
/*     */   public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
/* 275 */     super(3, 4, lineLength, (lineSeparator == null) ? 0 : lineSeparator.length);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 280 */     if (lineSeparator != null) {
/* 281 */       if (containsAlphabetOrPad(lineSeparator)) {
/* 282 */         String sep = StringUtils.newStringUtf8(lineSeparator);
/* 283 */         throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
/*     */       } 
/* 285 */       if (lineLength > 0) {
/* 286 */         this.encodeSize = 4 + lineSeparator.length;
/* 287 */         this.lineSeparator = new byte[lineSeparator.length];
/* 288 */         System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/*     */       } else {
/* 290 */         this.encodeSize = 4;
/* 291 */         this.lineSeparator = null;
/*     */       } 
/*     */     } else {
/* 294 */       this.encodeSize = 4;
/* 295 */       this.lineSeparator = null;
/*     */     } 
/* 297 */     this.decodeSize = this.encodeSize - 1;
/* 298 */     this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUrlSafe() {
/* 308 */     return (this.encodeTable == URL_SAFE_ENCODE_TABLE);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 334 */     if (context.eof) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 339 */     if (inAvail < 0) {
/* 340 */       context.eof = true;
/* 341 */       if (0 == context.modulus && this.lineLength == 0) {
/*     */         return;
/*     */       }
/* 344 */       byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 345 */       int savedPos = context.pos;
/* 346 */       switch (context.modulus) {
/*     */         case 0:
/*     */           break;
/*     */         
/*     */         case 1:
/* 351 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 2 & 0x3F];
/*     */           
/* 353 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea << 4 & 0x3F];
/*     */           
/* 355 */           if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 356 */             buffer[context.pos++] = this.pad;
/* 357 */             buffer[context.pos++] = this.pad;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 2:
/* 362 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 10 & 0x3F];
/* 363 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 4 & 0x3F];
/* 364 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea << 2 & 0x3F];
/*     */           
/* 366 */           if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 367 */             buffer[context.pos++] = this.pad;
/*     */           }
/*     */           break;
/*     */         default:
/* 371 */           throw new IllegalStateException("Impossible modulus " + context.modulus);
/*     */       } 
/* 373 */       context.currentLinePos += context.pos - savedPos;
/*     */       
/* 375 */       if (this.lineLength > 0 && context.currentLinePos > 0) {
/* 376 */         System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 377 */         context.pos += this.lineSeparator.length;
/*     */       } 
/*     */     } else {
/* 380 */       for (int i = 0; i < inAvail; i++) {
/* 381 */         byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 382 */         context.modulus = (context.modulus + 1) % 3;
/* 383 */         int b = in[inPos++];
/* 384 */         if (b < 0) {
/* 385 */           b += 256;
/*     */         }
/* 387 */         context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
/* 388 */         if (0 == context.modulus) {
/* 389 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 18 & 0x3F];
/* 390 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 12 & 0x3F];
/* 391 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 6 & 0x3F];
/* 392 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea & 0x3F];
/* 393 */           context.currentLinePos += 4;
/* 394 */           if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
/* 395 */             System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 396 */             context.pos += this.lineSeparator.length;
/* 397 */             context.currentLinePos = 0;
/*     */           } 
/*     */         } 
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
/*     */   void decode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 431 */     if (context.eof) {
/*     */       return;
/*     */     }
/* 434 */     if (inAvail < 0) {
/* 435 */       context.eof = true;
/*     */     }
/* 437 */     for (int i = 0; i < inAvail; i++) {
/* 438 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/* 439 */       byte b = in[inPos++];
/* 440 */       if (b == this.pad) {
/*     */         
/* 442 */         context.eof = true;
/*     */         break;
/*     */       } 
/* 445 */       if (b >= 0 && b < DECODE_TABLE.length) {
/* 446 */         int result = DECODE_TABLE[b];
/* 447 */         if (result >= 0) {
/* 448 */           context.modulus = (context.modulus + 1) % 4;
/* 449 */           context.ibitWorkArea = (context.ibitWorkArea << 6) + result;
/* 450 */           if (context.modulus == 0) {
/* 451 */             buffer[context.pos++] = (byte)(context.ibitWorkArea >> 16 & 0xFF);
/* 452 */             buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 0xFF);
/* 453 */             buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 462 */     if (context.eof && context.modulus != 0) {
/* 463 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/*     */ 
/*     */ 
/*     */       
/* 467 */       switch (context.modulus) {
/*     */         case 1:
/*     */           return;
/*     */ 
/*     */         
/*     */         case 2:
/* 473 */           context.ibitWorkArea >>= 4;
/* 474 */           buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */         
/*     */         case 3:
/* 477 */           context.ibitWorkArea >>= 2;
/* 478 */           buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 0xFF);
/* 479 */           buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */       } 
/*     */       
/* 482 */       throw new IllegalStateException("Impossible modulus " + context.modulus);
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
/*     */   @Deprecated
/*     */   public static boolean isArrayByteBase64(byte[] arrayOctet) {
/* 499 */     return isBase64(arrayOctet);
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
/*     */   public static boolean isBase64(byte octet) {
/* 511 */     return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
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
/*     */   public static boolean isBase64(String base64) {
/* 525 */     return isBase64(StringUtils.getBytesUtf8(base64));
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
/*     */   public static boolean isBase64(byte[] arrayOctet) {
/* 539 */     for (int i = 0; i < arrayOctet.length; i++) {
/* 540 */       if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
/* 541 */         return false;
/*     */       }
/*     */     } 
/* 544 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64(byte[] binaryData) {
/* 555 */     return encodeBase64(binaryData, false);
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
/*     */   public static String encodeBase64String(byte[] binaryData) {
/* 570 */     return StringUtils.newStringUsAscii(encodeBase64(binaryData, false));
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
/*     */   public static byte[] encodeBase64URLSafe(byte[] binaryData) {
/* 583 */     return encodeBase64(binaryData, false, true);
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
/*     */   public static String encodeBase64URLSafeString(byte[] binaryData) {
/* 596 */     return StringUtils.newStringUsAscii(encodeBase64(binaryData, false, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 607 */     return encodeBase64(binaryData, true);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 622 */     return encodeBase64(binaryData, isChunked, false);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
/* 641 */     return encodeBase64(binaryData, isChunked, urlSafe, 2147483647);
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
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
/* 663 */     if (binaryData == null || binaryData.length == 0) {
/* 664 */       return binaryData;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 669 */     Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 670 */     long len = b64.getEncodedLength(binaryData);
/* 671 */     if (len > maxResultSize) {
/* 672 */       throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 678 */     return b64.encode(binaryData);
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
/*     */   public static byte[] decodeBase64(String base64String) {
/* 693 */     return (new Base64()).decode(base64String);
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
/*     */   public static byte[] decodeBase64(byte[] base64Data) {
/* 707 */     return (new Base64()).decode(base64Data);
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
/*     */   public static BigInteger decodeInteger(byte[] pArray) {
/* 722 */     return new BigInteger(1, decodeBase64(pArray));
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
/*     */   public static byte[] encodeInteger(BigInteger bigInt) {
/* 736 */     if (bigInt == null) {
/* 737 */       throw new NullPointerException("encodeInteger called with null parameter");
/*     */     }
/* 739 */     return encodeBase64(toIntegerBytes(bigInt), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] toIntegerBytes(BigInteger bigInt) {
/* 750 */     int bitlen = bigInt.bitLength();
/*     */     
/* 752 */     bitlen = bitlen + 7 >> 3 << 3;
/* 753 */     byte[] bigBytes = bigInt.toByteArray();
/*     */     
/* 755 */     if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
/* 756 */       return bigBytes;
/*     */     }
/*     */     
/* 759 */     int startSrc = 0;
/* 760 */     int len = bigBytes.length;
/*     */ 
/*     */     
/* 763 */     if (bigInt.bitLength() % 8 == 0) {
/* 764 */       startSrc = 1;
/* 765 */       len--;
/*     */     } 
/* 767 */     int startDst = bitlen / 8 - len;
/* 768 */     byte[] resizedBytes = new byte[bitlen / 8];
/* 769 */     System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/* 770 */     return resizedBytes;
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
/*     */   protected boolean isInAlphabet(byte octet) {
/* 782 */     return (octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\binary\Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */