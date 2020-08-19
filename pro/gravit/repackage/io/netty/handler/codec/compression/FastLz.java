/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FastLz
/*     */ {
/*     */   private static final int MAX_DISTANCE = 8191;
/*     */   private static final int MAX_FARDISTANCE = 73725;
/*     */   private static final int HASH_LOG = 13;
/*     */   private static final int HASH_SIZE = 8192;
/*     */   private static final int HASH_MASK = 8191;
/*     */   private static final int MAX_COPY = 32;
/*     */   private static final int MAX_LEN = 264;
/*     */   private static final int MIN_RECOMENDED_LENGTH_FOR_LEVEL_2 = 65536;
/*     */   static final int MAGIC_NUMBER = 4607066;
/*     */   static final byte BLOCK_TYPE_NON_COMPRESSED = 0;
/*     */   static final byte BLOCK_TYPE_COMPRESSED = 1;
/*     */   static final byte BLOCK_WITHOUT_CHECKSUM = 0;
/*     */   static final byte BLOCK_WITH_CHECKSUM = 16;
/*     */   static final int OPTIONS_OFFSET = 3;
/*     */   static final int CHECKSUM_OFFSET = 4;
/*     */   static final int MAX_CHUNK_LENGTH = 65535;
/*     */   static final int MIN_LENGTH_TO_COMPRESSION = 32;
/*     */   static final int LEVEL_AUTO = 0;
/*     */   static final int LEVEL_1 = 1;
/*     */   static final int LEVEL_2 = 2;
/*     */   
/*     */   static int calculateOutputBufferLength(int inputLength) {
/*  83 */     int outputLength = (int)(inputLength * 1.06D);
/*  84 */     return Math.max(outputLength, 66);
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
/*     */   static int compress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int proposedLevel) {
/*     */     int level;
/*  97 */     if (proposedLevel == 0) {
/*  98 */       level = (inLength < 65536) ? 1 : 2;
/*     */     } else {
/* 100 */       level = proposedLevel;
/*     */     } 
/*     */     
/* 103 */     int ip = 0;
/* 104 */     int ipBound = ip + inLength - 2;
/* 105 */     int ipLimit = ip + inLength - 12;
/*     */     
/* 107 */     int op = 0;
/*     */ 
/*     */     
/* 110 */     int[] htab = new int[8192];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     if (inLength < 4) {
/* 122 */       if (inLength != 0) {
/*     */         
/* 124 */         output[outOffset + op++] = (byte)(inLength - 1);
/* 125 */         ipBound++;
/* 126 */         while (ip <= ipBound) {
/* 127 */           output[outOffset + op++] = input[inOffset + ip++];
/*     */         }
/* 129 */         return inLength + 1;
/*     */       } 
/*     */       
/* 132 */       return 0;
/*     */     } 
/*     */     
/*     */     int hslot;
/*     */     
/* 137 */     for (hslot = 0; hslot < 8192; hslot++)
/*     */     {
/* 139 */       htab[hslot] = ip;
/*     */     }
/*     */ 
/*     */     
/* 143 */     int copy = 2;
/* 144 */     output[outOffset + op++] = 31;
/* 145 */     output[outOffset + op++] = input[inOffset + ip++];
/* 146 */     output[outOffset + op++] = input[inOffset + ip++];
/*     */ 
/*     */     
/* 149 */     while (ip < ipLimit) {
/* 150 */       int ref = 0;
/*     */       
/* 152 */       long distance = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 157 */       int len = 3;
/*     */ 
/*     */       
/* 160 */       int anchor = ip;
/*     */       
/* 162 */       boolean matchLabel = false;
/*     */ 
/*     */       
/* 165 */       if (level == 2)
/*     */       {
/* 167 */         if (input[inOffset + ip] == input[inOffset + ip - 1] && 
/* 168 */           readU16(input, inOffset + ip - 1) == readU16(input, inOffset + ip + 1)) {
/* 169 */           distance = 1L;
/* 170 */           ip += 3;
/* 171 */           ref = anchor + 2;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 176 */           matchLabel = true;
/*     */         } 
/*     */       }
/* 179 */       if (!matchLabel) {
/*     */ 
/*     */         
/* 182 */         int i = hashFunction(input, inOffset + ip);
/*     */         
/* 184 */         hslot = i;
/*     */         
/* 186 */         ref = htab[i];
/*     */ 
/*     */         
/* 189 */         distance = (anchor - ref);
/*     */ 
/*     */ 
/*     */         
/* 193 */         htab[hslot] = anchor;
/*     */ 
/*     */         
/* 196 */         if (distance == 0L || ((level == 1) ? (distance >= 8191L) : (distance >= 73725L)) || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++]) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 204 */           output[outOffset + op++] = input[inOffset + anchor++];
/* 205 */           ip = anchor;
/* 206 */           copy++;
/* 207 */           if (copy == 32) {
/* 208 */             copy = 0;
/* 209 */             output[outOffset + op++] = 31;
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/* 214 */         if (level == 2)
/*     */         {
/* 216 */           if (distance >= 8191L) {
/* 217 */             if (input[inOffset + ip++] != input[inOffset + ref++] || input[inOffset + ip++] != input[inOffset + ref++]) {
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 222 */               output[outOffset + op++] = input[inOffset + anchor++];
/* 223 */               ip = anchor;
/* 224 */               copy++;
/* 225 */               if (copy == 32) {
/* 226 */                 copy = 0;
/* 227 */                 output[outOffset + op++] = 31;
/*     */               } 
/*     */               continue;
/*     */             } 
/* 231 */             len += 2;
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 239 */       ip = anchor + len;
/*     */ 
/*     */       
/* 242 */       distance--;
/*     */       
/* 244 */       if (distance == 0L) {
/*     */ 
/*     */         
/* 247 */         byte x = input[inOffset + ip - 1];
/* 248 */         while (ip < ipBound && 
/* 249 */           input[inOffset + ref++] == x)
/*     */         {
/*     */           
/* 252 */           ip++;
/*     */ 
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 258 */       else if (input[inOffset + ref++] == input[inOffset + ip++]) {
/*     */ 
/*     */         
/* 261 */         if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */         {
/*     */           
/* 264 */           if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */           {
/*     */             
/* 267 */             if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */             {
/*     */               
/* 270 */               if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */               {
/*     */                 
/* 273 */                 if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */                 {
/*     */                   
/* 276 */                   if (input[inOffset + ref++] == input[inOffset + ip++])
/*     */                   {
/*     */                     
/* 279 */                     if (input[inOffset + ref++] == input[inOffset + ip++]) {
/*     */                       do {
/*     */                       
/* 282 */                       } while (ip < ipBound && 
/* 283 */                         input[inOffset + ref++] == input[inOffset + ip++]);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       } 
/* 292 */       if (copy != 0) {
/*     */ 
/*     */         
/* 295 */         output[outOffset + op - copy - 1] = (byte)(copy - 1);
/*     */       } else {
/*     */         
/* 298 */         op--;
/*     */       } 
/*     */ 
/*     */       
/* 302 */       copy = 0;
/*     */ 
/*     */       
/* 305 */       ip -= 3;
/* 306 */       len = ip - anchor;
/*     */ 
/*     */       
/* 309 */       if (level == 2) {
/* 310 */         if (distance < 8191L) {
/* 311 */           if (len < 7) {
/* 312 */             output[outOffset + op++] = (byte)(int)((len << 5) + (distance >>> 8L));
/* 313 */             output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */           } else {
/* 315 */             output[outOffset + op++] = (byte)(int)(224L + (distance >>> 8L));
/* 316 */             for (len -= 7; len >= 255; len -= 255) {
/* 317 */               output[outOffset + op++] = -1;
/*     */             }
/* 319 */             output[outOffset + op++] = (byte)len;
/* 320 */             output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */           }
/*     */         
/*     */         }
/* 324 */         else if (len < 7) {
/* 325 */           distance -= 8191L;
/* 326 */           output[outOffset + op++] = (byte)((len << 5) + 31);
/* 327 */           output[outOffset + op++] = -1;
/* 328 */           output[outOffset + op++] = (byte)(int)(distance >>> 8L);
/* 329 */           output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */         } else {
/* 331 */           distance -= 8191L;
/* 332 */           output[outOffset + op++] = -1;
/* 333 */           for (len -= 7; len >= 255; len -= 255) {
/* 334 */             output[outOffset + op++] = -1;
/*     */           }
/* 336 */           output[outOffset + op++] = (byte)len;
/* 337 */           output[outOffset + op++] = -1;
/* 338 */           output[outOffset + op++] = (byte)(int)(distance >>> 8L);
/* 339 */           output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */         } 
/*     */       } else {
/*     */         
/* 343 */         if (len > 262) {
/* 344 */           while (len > 262) {
/* 345 */             output[outOffset + op++] = (byte)(int)(224L + (distance >>> 8L));
/* 346 */             output[outOffset + op++] = -3;
/* 347 */             output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/* 348 */             len -= 262;
/*     */           } 
/*     */         }
/*     */         
/* 352 */         if (len < 7) {
/* 353 */           output[outOffset + op++] = (byte)(int)((len << 5) + (distance >>> 8L));
/* 354 */           output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */         } else {
/* 356 */           output[outOffset + op++] = (byte)(int)(224L + (distance >>> 8L));
/* 357 */           output[outOffset + op++] = (byte)(len - 7);
/* 358 */           output[outOffset + op++] = (byte)(int)(distance & 0xFFL);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 364 */       int hval = hashFunction(input, inOffset + ip);
/* 365 */       htab[hval] = ip++;
/*     */ 
/*     */       
/* 368 */       hval = hashFunction(input, inOffset + ip);
/* 369 */       htab[hval] = ip++;
/*     */ 
/*     */       
/* 372 */       output[outOffset + op++] = 31;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 391 */     ipBound++;
/* 392 */     while (ip <= ipBound) {
/* 393 */       output[outOffset + op++] = input[inOffset + ip++];
/* 394 */       copy++;
/* 395 */       if (copy == 32) {
/* 396 */         copy = 0;
/* 397 */         output[outOffset + op++] = 31;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 402 */     if (copy != 0) {
/*     */       
/* 404 */       output[outOffset + op - copy - 1] = (byte)(copy - 1);
/*     */     } else {
/* 406 */       op--;
/*     */     } 
/*     */     
/* 409 */     if (level == 2)
/*     */     {
/* 411 */       output[outOffset] = (byte)(output[outOffset] | 0x20);
/*     */     }
/*     */     
/* 414 */     return op;
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
/*     */   static int decompress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int outLength) {
/* 428 */     int level = (input[inOffset] >> 5) + 1;
/* 429 */     if (level != 1 && level != 2) {
/* 430 */       throw new DecompressionException(String.format("invalid level: %d (expected: %d or %d)", new Object[] {
/* 431 */               Integer.valueOf(level), Integer.valueOf(1), Integer.valueOf(2)
/*     */             }));
/*     */     }
/*     */ 
/*     */     
/* 436 */     int ip = 0;
/*     */     
/* 438 */     int op = 0;
/*     */     
/* 440 */     long ctrl = (input[inOffset + ip++] & 0x1F);
/*     */     
/* 442 */     int loop = 1;
/*     */     
/*     */     do {
/* 445 */       int ref = op;
/*     */       
/* 447 */       long len = ctrl >> 5L;
/*     */       
/* 449 */       long ofs = (ctrl & 0x1FL) << 8L;
/*     */       
/* 451 */       if (ctrl >= 32L) {
/* 452 */         len--;
/*     */         
/* 454 */         ref = (int)(ref - ofs);
/*     */ 
/*     */         
/* 457 */         if (len == 6L) {
/* 458 */           if (level == 1) {
/*     */             
/* 460 */             len += (input[inOffset + ip++] & 0xFF);
/*     */           } else {
/*     */             int code; do {
/* 463 */               code = input[inOffset + ip++] & 0xFF;
/* 464 */               len += code;
/* 465 */             } while (code == 255);
/*     */           } 
/*     */         }
/* 468 */         if (level == 1) {
/*     */           
/* 470 */           ref -= input[inOffset + ip++] & 0xFF;
/*     */         } else {
/* 472 */           int code = input[inOffset + ip++] & 0xFF;
/* 473 */           ref -= code;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 478 */           if (code == 255 && ofs == 7936L) {
/* 479 */             ofs = ((input[inOffset + ip++] & 0xFF) << 8);
/* 480 */             ofs += (input[inOffset + ip++] & 0xFF);
/*     */             
/* 482 */             ref = (int)(op - ofs - 8191L);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 487 */         if (op + len + 3L > outLength) {
/* 488 */           return 0;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 494 */         if (ref - 1 < 0) {
/* 495 */           return 0;
/*     */         }
/*     */         
/* 498 */         if (ip < inLength) {
/* 499 */           ctrl = (input[inOffset + ip++] & 0xFF);
/*     */         } else {
/* 501 */           loop = 0;
/*     */         } 
/*     */         
/* 504 */         if (ref == op) {
/*     */ 
/*     */           
/* 507 */           byte b = output[outOffset + ref - 1];
/* 508 */           output[outOffset + op++] = b;
/* 509 */           output[outOffset + op++] = b;
/* 510 */           output[outOffset + op++] = b;
/* 511 */           while (len != 0L) {
/* 512 */             output[outOffset + op++] = b;
/* 513 */             len--;
/*     */           } 
/*     */         } else {
/*     */           
/* 517 */           ref--;
/*     */ 
/*     */           
/* 520 */           output[outOffset + op++] = output[outOffset + ref++];
/* 521 */           output[outOffset + op++] = output[outOffset + ref++];
/* 522 */           output[outOffset + op++] = output[outOffset + ref++];
/*     */           
/* 524 */           while (len != 0L) {
/* 525 */             output[outOffset + op++] = output[outOffset + ref++];
/* 526 */             len--;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 530 */         ctrl++;
/*     */         
/* 532 */         if (op + ctrl > outLength) {
/* 533 */           return 0;
/*     */         }
/* 535 */         if (ip + ctrl > inLength) {
/* 536 */           return 0;
/*     */         }
/*     */ 
/*     */         
/* 540 */         output[outOffset + op++] = input[inOffset + ip++];
/*     */         
/* 542 */         ctrl--; for (; ctrl != 0L; ctrl--)
/*     */         {
/* 544 */           output[outOffset + op++] = input[inOffset + ip++];
/*     */         }
/*     */         
/* 547 */         loop = (ip < inLength) ? 1 : 0;
/* 548 */         if (loop != 0)
/*     */         {
/* 550 */           ctrl = (input[inOffset + ip++] & 0xFF);
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 555 */     while (loop != 0);
/*     */ 
/*     */     
/* 558 */     return op;
/*     */   }
/*     */   
/*     */   private static int hashFunction(byte[] p, int offset) {
/* 562 */     int v = readU16(p, offset);
/* 563 */     v ^= readU16(p, offset + 1) ^ v >> 3;
/* 564 */     v &= 0x1FFF;
/* 565 */     return v;
/*     */   }
/*     */   
/*     */   private static int readU16(byte[] data, int offset) {
/* 569 */     if (offset + 1 >= data.length) {
/* 570 */       return data[offset] & 0xFF;
/*     */     }
/* 572 */     return (data[offset + 1] & 0xFF) << 8 | data[offset] & 0xFF;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\FastLz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */