/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base32
/*     */   extends BaseNCodec
/*     */ {
/*     */   private static final int BITS_PER_ENCODED_BYTE = 5;
/*     */   private static final int BYTES_PER_ENCODED_BLOCK = 8;
/*     */   private static final int BYTES_PER_UNENCODED_BLOCK = 5;
/*  60 */   private static final byte[] CHUNK_SEPARATOR = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private static final byte[] DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private static final byte[] ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   private static final byte[] HEX_DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   private static final byte[] HEX_ENCODE_TABLE = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_5BITS = 31;
/*     */ 
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
/*     */   private final byte[] decodeTable;
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
/*     */   private final byte[] encodeTable;
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
/*     */   
/*     */   public Base32() {
/* 165 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base32(byte pad) {
/* 176 */     this(false, pad);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base32(boolean useHex) {
/* 187 */     this(0, (byte[])null, useHex, (byte)61);
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
/*     */   public Base32(boolean useHex, byte pad) {
/* 199 */     this(0, (byte[])null, useHex, pad);
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
/*     */   public Base32(int lineLength) {
/* 214 */     this(lineLength, CHUNK_SEPARATOR);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator) {
/* 236 */     this(lineLength, lineSeparator, false, (byte)61);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator, boolean useHex) {
/* 261 */     this(lineLength, lineSeparator, useHex, (byte)61);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator, boolean useHex, byte pad) {
/* 287 */     super(5, 8, lineLength, (lineSeparator == null) ? 0 : lineSeparator.length, pad);
/*     */     
/* 289 */     if (useHex) {
/* 290 */       this.encodeTable = HEX_ENCODE_TABLE;
/* 291 */       this.decodeTable = HEX_DECODE_TABLE;
/*     */     } else {
/* 293 */       this.encodeTable = ENCODE_TABLE;
/* 294 */       this.decodeTable = DECODE_TABLE;
/*     */     } 
/* 296 */     if (lineLength > 0) {
/* 297 */       if (lineSeparator == null) {
/* 298 */         throw new IllegalArgumentException("lineLength " + lineLength + " > 0, but lineSeparator is null");
/*     */       }
/*     */       
/* 301 */       if (containsAlphabetOrPad(lineSeparator)) {
/* 302 */         String sep = StringUtils.newStringUtf8(lineSeparator);
/* 303 */         throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + sep + "]");
/*     */       } 
/* 305 */       this.encodeSize = 8 + lineSeparator.length;
/* 306 */       this.lineSeparator = new byte[lineSeparator.length];
/* 307 */       System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/*     */     } else {
/* 309 */       this.encodeSize = 8;
/* 310 */       this.lineSeparator = null;
/*     */     } 
/* 312 */     this.decodeSize = this.encodeSize - 1;
/*     */     
/* 314 */     if (isInAlphabet(pad) || isWhiteSpace(pad)) {
/* 315 */       throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
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
/*     */   void decode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 345 */     if (context.eof) {
/*     */       return;
/*     */     }
/* 348 */     if (inAvail < 0) {
/* 349 */       context.eof = true;
/*     */     }
/* 351 */     for (int i = 0; i < inAvail; i++) {
/* 352 */       byte b = in[inPos++];
/* 353 */       if (b == this.pad) {
/*     */         
/* 355 */         context.eof = true;
/*     */         break;
/*     */       } 
/* 358 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/* 359 */       if (b >= 0 && b < this.decodeTable.length) {
/* 360 */         int result = this.decodeTable[b];
/* 361 */         if (result >= 0) {
/* 362 */           context.modulus = (context.modulus + 1) % 8;
/*     */           
/* 364 */           context.lbitWorkArea = (context.lbitWorkArea << 5L) + result;
/* 365 */           if (context.modulus == 0) {
/* 366 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 32L & 0xFFL);
/* 367 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 24L & 0xFFL);
/* 368 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 369 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 370 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 379 */     if (context.eof && context.modulus >= 2) {
/* 380 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/*     */ 
/*     */       
/* 383 */       switch (context.modulus) {
/*     */         case 2:
/* 385 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 2L & 0xFFL);
/*     */           return;
/*     */         case 3:
/* 388 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 7L & 0xFFL);
/*     */           return;
/*     */         case 4:
/* 391 */           context.lbitWorkArea >>= 4L;
/* 392 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 393 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 5:
/* 396 */           context.lbitWorkArea >>= 1L;
/* 397 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 398 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 399 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 6:
/* 402 */           context.lbitWorkArea >>= 6L;
/* 403 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 404 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 405 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 7:
/* 408 */           context.lbitWorkArea >>= 3L;
/* 409 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 24L & 0xFFL);
/* 410 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 411 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 412 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */       } 
/*     */       
/* 416 */       throw new IllegalStateException("Impossible modulus " + context.modulus);
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
/*     */   void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 440 */     if (context.eof) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 445 */     if (inAvail < 0) {
/* 446 */       context.eof = true;
/* 447 */       if (0 == context.modulus && this.lineLength == 0) {
/*     */         return;
/*     */       }
/* 450 */       byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 451 */       int savedPos = context.pos;
/* 452 */       switch (context.modulus) {
/*     */         case 0:
/*     */           break;
/*     */         case 1:
/* 456 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 3L) & 0x1F];
/* 457 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 2L) & 0x1F];
/* 458 */           buffer[context.pos++] = this.pad;
/* 459 */           buffer[context.pos++] = this.pad;
/* 460 */           buffer[context.pos++] = this.pad;
/* 461 */           buffer[context.pos++] = this.pad;
/* 462 */           buffer[context.pos++] = this.pad;
/* 463 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 2:
/* 466 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 11L) & 0x1F];
/* 467 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 6L) & 0x1F];
/* 468 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 1L) & 0x1F];
/* 469 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 4L) & 0x1F];
/* 470 */           buffer[context.pos++] = this.pad;
/* 471 */           buffer[context.pos++] = this.pad;
/* 472 */           buffer[context.pos++] = this.pad;
/* 473 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 3:
/* 476 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 19L) & 0x1F];
/* 477 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 14L) & 0x1F];
/* 478 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 9L) & 0x1F];
/* 479 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 4L) & 0x1F];
/* 480 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 1L) & 0x1F];
/* 481 */           buffer[context.pos++] = this.pad;
/* 482 */           buffer[context.pos++] = this.pad;
/* 483 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 4:
/* 486 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 27L) & 0x1F];
/* 487 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 22L) & 0x1F];
/* 488 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 17L) & 0x1F];
/* 489 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 12L) & 0x1F];
/* 490 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 7L) & 0x1F];
/* 491 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 2L) & 0x1F];
/* 492 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 3L) & 0x1F];
/* 493 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         default:
/* 496 */           throw new IllegalStateException("Impossible modulus " + context.modulus);
/*     */       } 
/* 498 */       context.currentLinePos += context.pos - savedPos;
/*     */       
/* 500 */       if (this.lineLength > 0 && context.currentLinePos > 0) {
/* 501 */         System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 502 */         context.pos += this.lineSeparator.length;
/*     */       } 
/*     */     } else {
/* 505 */       for (int i = 0; i < inAvail; i++) {
/* 506 */         byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 507 */         context.modulus = (context.modulus + 1) % 5;
/* 508 */         int b = in[inPos++];
/* 509 */         if (b < 0) {
/* 510 */           b += 256;
/*     */         }
/* 512 */         context.lbitWorkArea = (context.lbitWorkArea << 8L) + b;
/* 513 */         if (0 == context.modulus) {
/* 514 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 35L) & 0x1F];
/* 515 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 30L) & 0x1F];
/* 516 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 25L) & 0x1F];
/* 517 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 20L) & 0x1F];
/* 518 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 15L) & 0x1F];
/* 519 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 10L) & 0x1F];
/* 520 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 5L) & 0x1F];
/* 521 */           buffer[context.pos++] = this.encodeTable[(int)context.lbitWorkArea & 0x1F];
/* 522 */           context.currentLinePos += 8;
/* 523 */           if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
/* 524 */             System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 525 */             context.pos += this.lineSeparator.length;
/* 526 */             context.currentLinePos = 0;
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
/*     */   public boolean isInAlphabet(byte octet) {
/* 542 */     return (octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\binary\Base32.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */