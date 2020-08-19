/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Snappy
/*     */ {
/*     */   private static final int MAX_HT_SIZE = 16384;
/*     */   private static final int MIN_COMPRESSIBLE_BYTES = 15;
/*     */   private static final int PREAMBLE_NOT_FULL = -1;
/*     */   private static final int NOT_ENOUGH_INPUT = -1;
/*     */   private static final int LITERAL = 0;
/*     */   private static final int COPY_1_BYTE_OFFSET = 1;
/*     */   private static final int COPY_2_BYTE_OFFSET = 2;
/*     */   private static final int COPY_4_BYTE_OFFSET = 3;
/*  41 */   private State state = State.READY;
/*     */   private byte tag;
/*     */   private int written;
/*     */   
/*     */   private enum State {
/*  46 */     READY,
/*  47 */     READING_PREAMBLE,
/*  48 */     READING_TAG,
/*  49 */     READING_LITERAL,
/*  50 */     READING_COPY;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  54 */     this.state = State.READY;
/*  55 */     this.tag = 0;
/*  56 */     this.written = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void encode(ByteBuf in, ByteBuf out, int length) {
/*  61 */     for (int i = 0;; i++) {
/*  62 */       int b = length >>> i * 7;
/*  63 */       if ((b & 0xFFFFFF80) != 0) {
/*  64 */         out.writeByte(b & 0x7F | 0x80);
/*     */       } else {
/*  66 */         out.writeByte(b);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  71 */     int inIndex = in.readerIndex();
/*  72 */     int baseIndex = inIndex;
/*     */     
/*  74 */     short[] table = getHashTable(length);
/*  75 */     int shift = Integer.numberOfLeadingZeros(table.length) + 1;
/*     */     
/*  77 */     int nextEmit = inIndex;
/*     */     
/*  79 */     if (length - inIndex >= 15) {
/*  80 */       int nextHash = hash(in, ++inIndex, shift);
/*     */       while (true) {
/*  82 */         int skip = 32;
/*     */ 
/*     */         
/*  85 */         int nextIndex = inIndex;
/*     */         while (true) {
/*  87 */           inIndex = nextIndex;
/*  88 */           int hash = nextHash;
/*  89 */           int bytesBetweenHashLookups = skip++ >> 5;
/*  90 */           nextIndex = inIndex + bytesBetweenHashLookups;
/*     */ 
/*     */           
/*  93 */           if (nextIndex > length - 4) {
/*     */             break;
/*     */           }
/*     */           
/*  97 */           nextHash = hash(in, nextIndex, shift);
/*     */           
/*  99 */           int candidate = baseIndex + table[hash];
/*     */           
/* 101 */           table[hash] = (short)(inIndex - baseIndex);
/*     */           
/* 103 */           if (in.getInt(inIndex) == in.getInt(candidate)) {
/*     */             
/* 105 */             encodeLiteral(in, out, inIndex - nextEmit);
/*     */ 
/*     */             
/*     */             while (true) {
/* 109 */               int base = inIndex;
/* 110 */               int matched = 4 + findMatchingLength(in, candidate + 4, inIndex + 4, length);
/* 111 */               inIndex += matched;
/* 112 */               int offset = base - candidate;
/* 113 */               encodeCopy(out, offset, matched);
/* 114 */               in.readerIndex(in.readerIndex() + matched);
/* 115 */               int insertTail = inIndex - 1;
/* 116 */               nextEmit = inIndex;
/* 117 */               if (inIndex >= length - 4) {
/*     */                 break;
/*     */               }
/*     */               
/* 121 */               int prevHash = hash(in, insertTail, shift);
/* 122 */               table[prevHash] = (short)(inIndex - baseIndex - 1);
/* 123 */               int currentHash = hash(in, insertTail + 1, shift);
/* 124 */               candidate = baseIndex + table[currentHash];
/* 125 */               table[currentHash] = (short)(inIndex - baseIndex);
/*     */               
/* 127 */               if (in.getInt(insertTail + 1) != in.getInt(candidate))
/*     */               
/* 129 */               { nextHash = hash(in, insertTail + 2, shift);
/* 130 */                 inIndex++; } 
/*     */             }  break;
/*     */           } 
/*     */         }  break;
/*     */       } 
/* 135 */     }  if (nextEmit < length) {
/* 136 */       encodeLiteral(in, out, length - nextEmit);
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
/*     */   private static int hash(ByteBuf in, int index, int shift) {
/* 151 */     return in.getInt(index) * 506832829 >>> shift;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static short[] getHashTable(int inputSize) {
/* 161 */     int htSize = 256;
/* 162 */     while (htSize < 16384 && htSize < inputSize) {
/* 163 */       htSize <<= 1;
/*     */     }
/* 165 */     return new short[htSize];
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
/*     */   private static int findMatchingLength(ByteBuf in, int minIndex, int inIndex, int maxIndex) {
/* 180 */     int matched = 0;
/*     */     
/* 182 */     while (inIndex <= maxIndex - 4 && in
/* 183 */       .getInt(inIndex) == in.getInt(minIndex + matched)) {
/* 184 */       inIndex += 4;
/* 185 */       matched += 4;
/*     */     } 
/*     */     
/* 188 */     while (inIndex < maxIndex && in.getByte(minIndex + matched) == in.getByte(inIndex)) {
/* 189 */       inIndex++;
/* 190 */       matched++;
/*     */     } 
/*     */     
/* 193 */     return matched;
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
/*     */   private static int bitsToEncode(int value) {
/* 205 */     int highestOneBit = Integer.highestOneBit(value);
/* 206 */     int bitLength = 0;
/* 207 */     while ((highestOneBit >>= 1) != 0) {
/* 208 */       bitLength++;
/*     */     }
/*     */     
/* 211 */     return bitLength;
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
/*     */   static void encodeLiteral(ByteBuf in, ByteBuf out, int length) {
/* 224 */     if (length < 61) {
/* 225 */       out.writeByte(length - 1 << 2);
/*     */     } else {
/* 227 */       int bitLength = bitsToEncode(length - 1);
/* 228 */       int bytesToEncode = 1 + bitLength / 8;
/* 229 */       out.writeByte(59 + bytesToEncode << 2);
/* 230 */       for (int i = 0; i < bytesToEncode; i++) {
/* 231 */         out.writeByte(length - 1 >> i * 8 & 0xFF);
/*     */       }
/*     */     } 
/*     */     
/* 235 */     out.writeBytes(in, length);
/*     */   }
/*     */   
/*     */   private static void encodeCopyWithOffset(ByteBuf out, int offset, int length) {
/* 239 */     if (length < 12 && offset < 2048) {
/* 240 */       out.writeByte(0x1 | length - 4 << 2 | offset >> 8 << 5);
/* 241 */       out.writeByte(offset & 0xFF);
/*     */     } else {
/* 243 */       out.writeByte(0x2 | length - 1 << 2);
/* 244 */       out.writeByte(offset & 0xFF);
/* 245 */       out.writeByte(offset >> 8 & 0xFF);
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
/*     */   private static void encodeCopy(ByteBuf out, int offset, int length) {
/* 257 */     while (length >= 68) {
/* 258 */       encodeCopyWithOffset(out, offset, 64);
/* 259 */       length -= 64;
/*     */     } 
/*     */     
/* 262 */     if (length > 64) {
/* 263 */       encodeCopyWithOffset(out, offset, 60);
/* 264 */       length -= 60;
/*     */     } 
/*     */     
/* 267 */     encodeCopyWithOffset(out, offset, length);
/*     */   }
/*     */   
/*     */   public void decode(ByteBuf in, ByteBuf out) {
/* 271 */     while (in.isReadable()) {
/* 272 */       int uncompressedLength; int literalWritten; int decodeWritten; switch (this.state) {
/*     */         case READY:
/* 274 */           this.state = State.READING_PREAMBLE;
/*     */         
/*     */         case READING_PREAMBLE:
/* 277 */           uncompressedLength = readPreamble(in);
/* 278 */           if (uncompressedLength == -1) {
/*     */             return;
/*     */           }
/*     */           
/* 282 */           if (uncompressedLength == 0) {
/*     */             
/* 284 */             this.state = State.READY;
/*     */             return;
/*     */           } 
/* 287 */           out.ensureWritable(uncompressedLength);
/* 288 */           this.state = State.READING_TAG;
/*     */         
/*     */         case READING_TAG:
/* 291 */           if (!in.isReadable()) {
/*     */             return;
/*     */           }
/* 294 */           this.tag = in.readByte();
/* 295 */           switch (this.tag & 0x3) {
/*     */             case 0:
/* 297 */               this.state = State.READING_LITERAL;
/*     */             
/*     */             case 1:
/*     */             case 2:
/*     */             case 3:
/* 302 */               this.state = State.READING_COPY;
/*     */           } 
/*     */         
/*     */         
/*     */         case READING_LITERAL:
/* 307 */           literalWritten = decodeLiteral(this.tag, in, out);
/* 308 */           if (literalWritten != -1) {
/* 309 */             this.state = State.READING_TAG;
/* 310 */             this.written += literalWritten;
/*     */             continue;
/*     */           } 
/*     */           return;
/*     */ 
/*     */ 
/*     */         
/*     */         case READING_COPY:
/* 318 */           switch (this.tag & 0x3) {
/*     */             case 1:
/* 320 */               decodeWritten = decodeCopyWith1ByteOffset(this.tag, in, out, this.written);
/* 321 */               if (decodeWritten != -1) {
/* 322 */                 this.state = State.READING_TAG;
/* 323 */                 this.written += decodeWritten;
/*     */                 continue;
/*     */               } 
/*     */               return;
/*     */ 
/*     */             
/*     */             case 2:
/* 330 */               decodeWritten = decodeCopyWith2ByteOffset(this.tag, in, out, this.written);
/* 331 */               if (decodeWritten != -1) {
/* 332 */                 this.state = State.READING_TAG;
/* 333 */                 this.written += decodeWritten;
/*     */                 continue;
/*     */               } 
/*     */               return;
/*     */ 
/*     */             
/*     */             case 3:
/* 340 */               decodeWritten = decodeCopyWith4ByteOffset(this.tag, in, out, this.written);
/* 341 */               if (decodeWritten != -1) {
/* 342 */                 this.state = State.READING_TAG;
/* 343 */                 this.written += decodeWritten;
/*     */                 continue;
/*     */               } 
/*     */               return;
/*     */           } 
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
/*     */   private static int readPreamble(ByteBuf in) {
/* 364 */     int length = 0;
/* 365 */     int byteIndex = 0;
/* 366 */     while (in.isReadable()) {
/* 367 */       int current = in.readUnsignedByte();
/* 368 */       length |= (current & 0x7F) << byteIndex++ * 7;
/* 369 */       if ((current & 0x80) == 0) {
/* 370 */         return length;
/*     */       }
/*     */       
/* 373 */       if (byteIndex >= 4) {
/* 374 */         throw new DecompressionException("Preamble is greater than 4 bytes");
/*     */       }
/*     */     } 
/*     */     
/* 378 */     return 0;
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
/*     */   static int decodeLiteral(byte tag, ByteBuf in, ByteBuf out) {
/*     */     int length;
/* 393 */     in.markReaderIndex();
/*     */     
/* 395 */     switch (tag >> 2 & 0x3F) {
/*     */       case 60:
/* 397 */         if (!in.isReadable()) {
/* 398 */           return -1;
/*     */         }
/* 400 */         length = in.readUnsignedByte();
/*     */         break;
/*     */       case 61:
/* 403 */         if (in.readableBytes() < 2) {
/* 404 */           return -1;
/*     */         }
/* 406 */         length = in.readUnsignedShortLE();
/*     */         break;
/*     */       case 62:
/* 409 */         if (in.readableBytes() < 3) {
/* 410 */           return -1;
/*     */         }
/* 412 */         length = in.readUnsignedMediumLE();
/*     */         break;
/*     */       case 63:
/* 415 */         if (in.readableBytes() < 4) {
/* 416 */           return -1;
/*     */         }
/* 418 */         length = in.readIntLE();
/*     */         break;
/*     */       default:
/* 421 */         length = tag >> 2 & 0x3F; break;
/*     */     } 
/* 423 */     length++;
/*     */     
/* 425 */     if (in.readableBytes() < length) {
/* 426 */       in.resetReaderIndex();
/* 427 */       return -1;
/*     */     } 
/*     */     
/* 430 */     out.writeBytes(in, length);
/* 431 */     return length;
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
/*     */   private static int decodeCopyWith1ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
/* 448 */     if (!in.isReadable()) {
/* 449 */       return -1;
/*     */     }
/*     */     
/* 452 */     int initialIndex = out.writerIndex();
/* 453 */     int length = 4 + ((tag & 0x1C) >> 2);
/* 454 */     int offset = (tag & 0xE0) << 8 >> 5 | in.readUnsignedByte();
/*     */     
/* 456 */     validateOffset(offset, writtenSoFar);
/*     */     
/* 458 */     out.markReaderIndex();
/* 459 */     if (offset < length) {
/* 460 */       int copies = length / offset;
/* 461 */       for (; copies > 0; copies--) {
/* 462 */         out.readerIndex(initialIndex - offset);
/* 463 */         out.readBytes(out, offset);
/*     */       } 
/* 465 */       if (length % offset != 0) {
/* 466 */         out.readerIndex(initialIndex - offset);
/* 467 */         out.readBytes(out, length % offset);
/*     */       } 
/*     */     } else {
/* 470 */       out.readerIndex(initialIndex - offset);
/* 471 */       out.readBytes(out, length);
/*     */     } 
/* 473 */     out.resetReaderIndex();
/*     */     
/* 475 */     return length;
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
/*     */   private static int decodeCopyWith2ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
/* 492 */     if (in.readableBytes() < 2) {
/* 493 */       return -1;
/*     */     }
/*     */     
/* 496 */     int initialIndex = out.writerIndex();
/* 497 */     int length = 1 + (tag >> 2 & 0x3F);
/* 498 */     int offset = in.readUnsignedShortLE();
/*     */     
/* 500 */     validateOffset(offset, writtenSoFar);
/*     */     
/* 502 */     out.markReaderIndex();
/* 503 */     if (offset < length) {
/* 504 */       int copies = length / offset;
/* 505 */       for (; copies > 0; copies--) {
/* 506 */         out.readerIndex(initialIndex - offset);
/* 507 */         out.readBytes(out, offset);
/*     */       } 
/* 509 */       if (length % offset != 0) {
/* 510 */         out.readerIndex(initialIndex - offset);
/* 511 */         out.readBytes(out, length % offset);
/*     */       } 
/*     */     } else {
/* 514 */       out.readerIndex(initialIndex - offset);
/* 515 */       out.readBytes(out, length);
/*     */     } 
/* 517 */     out.resetReaderIndex();
/*     */     
/* 519 */     return length;
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
/*     */   private static int decodeCopyWith4ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
/* 536 */     if (in.readableBytes() < 4) {
/* 537 */       return -1;
/*     */     }
/*     */     
/* 540 */     int initialIndex = out.writerIndex();
/* 541 */     int length = 1 + (tag >> 2 & 0x3F);
/* 542 */     int offset = in.readIntLE();
/*     */     
/* 544 */     validateOffset(offset, writtenSoFar);
/*     */     
/* 546 */     out.markReaderIndex();
/* 547 */     if (offset < length) {
/* 548 */       int copies = length / offset;
/* 549 */       for (; copies > 0; copies--) {
/* 550 */         out.readerIndex(initialIndex - offset);
/* 551 */         out.readBytes(out, offset);
/*     */       } 
/* 553 */       if (length % offset != 0) {
/* 554 */         out.readerIndex(initialIndex - offset);
/* 555 */         out.readBytes(out, length % offset);
/*     */       } 
/*     */     } else {
/* 558 */       out.readerIndex(initialIndex - offset);
/* 559 */       out.readBytes(out, length);
/*     */     } 
/* 561 */     out.resetReaderIndex();
/*     */     
/* 563 */     return length;
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
/*     */   private static void validateOffset(int offset, int chunkSizeSoFar) {
/* 576 */     if (offset == 0) {
/* 577 */       throw new DecompressionException("Offset is less than minimum permissible value");
/*     */     }
/*     */     
/* 580 */     if (offset < 0)
/*     */     {
/* 582 */       throw new DecompressionException("Offset is greater than maximum value supported by this implementation");
/*     */     }
/*     */     
/* 585 */     if (offset > chunkSizeSoFar) {
/* 586 */       throw new DecompressionException("Offset exceeds size of chunk");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int calculateChecksum(ByteBuf data) {
/* 597 */     return calculateChecksum(data, data.readerIndex(), data.readableBytes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int calculateChecksum(ByteBuf data, int offset, int length) {
/* 607 */     Crc32c crc32 = new Crc32c();
/*     */     try {
/* 609 */       crc32.update(data, offset, length);
/* 610 */       return maskChecksum((int)crc32.getValue());
/*     */     } finally {
/* 612 */       crc32.reset();
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
/*     */   static void validateChecksum(int expectedChecksum, ByteBuf data) {
/* 626 */     validateChecksum(expectedChecksum, data, data.readerIndex(), data.readableBytes());
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
/*     */   static void validateChecksum(int expectedChecksum, ByteBuf data, int offset, int length) {
/* 639 */     int actualChecksum = calculateChecksum(data, offset, length);
/* 640 */     if (actualChecksum != expectedChecksum) {
/* 641 */       throw new DecompressionException("mismatching checksum: " + 
/* 642 */           Integer.toHexString(actualChecksum) + " (expected: " + 
/* 643 */           Integer.toHexString(expectedChecksum) + ')');
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
/*     */   static int maskChecksum(int checksum) {
/* 659 */     return (checksum >> 15 | checksum << 17) + -1568478504;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Snappy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */