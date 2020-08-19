/*     */ package pro.gravit.repackage.io.netty.handler.codec.base64;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Base64
/*     */ {
/*     */   private static final int MAX_LINE_LENGTH = 76;
/*     */   private static final byte EQUALS_SIGN = 61;
/*     */   private static final byte NEW_LINE = 10;
/*     */   private static final byte WHITE_SPACE_ENC = -5;
/*     */   private static final byte EQUALS_SIGN_ENC = -1;
/*     */   
/*     */   private static byte[] alphabet(Base64Dialect dialect) {
/*  54 */     return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).alphabet;
/*     */   }
/*     */   
/*     */   private static byte[] decodabet(Base64Dialect dialect) {
/*  58 */     return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).decodabet;
/*     */   }
/*     */   
/*     */   private static boolean breakLines(Base64Dialect dialect) {
/*  62 */     return ((Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect")).breakLinesByDefault;
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src) {
/*  66 */     return encode(src, Base64Dialect.STANDARD);
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, Base64Dialect dialect) {
/*  70 */     return encode(src, breakLines(dialect), dialect);
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, boolean breakLines) {
/*  74 */     return encode(src, breakLines, Base64Dialect.STANDARD);
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect) {
/*  78 */     ObjectUtil.checkNotNull(src, "src");
/*     */     
/*  80 */     ByteBuf dest = encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect);
/*  81 */     src.readerIndex(src.writerIndex());
/*  82 */     return dest;
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, int off, int len) {
/*  86 */     return encode(src, off, len, Base64Dialect.STANDARD);
/*     */   }
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, int off, int len, Base64Dialect dialect) {
/*  90 */     return encode(src, off, len, breakLines(dialect), dialect);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines) {
/*  95 */     return encode(src, off, len, breakLines, Base64Dialect.STANDARD);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect) {
/* 100 */     return encode(src, off, len, breakLines, dialect, src.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect, ByteBufAllocator allocator) {
/* 105 */     ObjectUtil.checkNotNull(src, "src");
/* 106 */     ObjectUtil.checkNotNull(dialect, "dialect");
/*     */     
/* 108 */     ByteBuf dest = allocator.buffer(encodedBufferSize(len, breakLines)).order(src.order());
/* 109 */     byte[] alphabet = alphabet(dialect);
/* 110 */     int d = 0;
/* 111 */     int e = 0;
/* 112 */     int len2 = len - 2;
/* 113 */     int lineLength = 0;
/* 114 */     for (; d < len2; d += 3, e += 4) {
/* 115 */       encode3to4(src, d + off, 3, dest, e, alphabet);
/*     */       
/* 117 */       lineLength += 4;
/*     */       
/* 119 */       if (breakLines && lineLength == 76) {
/* 120 */         dest.setByte(e + 4, 10);
/* 121 */         e++;
/* 122 */         lineLength = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 126 */     if (d < len) {
/* 127 */       encode3to4(src, d + off, len - d, dest, e, alphabet);
/* 128 */       e += 4;
/*     */     } 
/*     */ 
/*     */     
/* 132 */     if (e > 1 && dest.getByte(e - 1) == 10) {
/* 133 */       e--;
/*     */     }
/*     */     
/* 136 */     return dest.slice(0, e);
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
/*     */   private static void encode3to4(ByteBuf src, int srcOffset, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
/* 152 */     if (src.order() == ByteOrder.BIG_ENDIAN) {
/*     */       int inBuff;
/* 154 */       switch (numSigBytes) {
/*     */         case 1:
/* 156 */           inBuff = toInt(src.getByte(srcOffset));
/*     */           break;
/*     */         case 2:
/* 159 */           inBuff = toIntBE(src.getShort(srcOffset));
/*     */           break;
/*     */         default:
/* 162 */           inBuff = (numSigBytes <= 0) ? 0 : toIntBE(src.getMedium(srcOffset));
/*     */           break;
/*     */       } 
/* 165 */       encode3to4BigEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
/*     */     } else {
/*     */       int inBuff;
/* 168 */       switch (numSigBytes) {
/*     */         case 1:
/* 170 */           inBuff = toInt(src.getByte(srcOffset));
/*     */           break;
/*     */         case 2:
/* 173 */           inBuff = toIntLE(src.getShort(srcOffset));
/*     */           break;
/*     */         default:
/* 176 */           inBuff = (numSigBytes <= 0) ? 0 : toIntLE(src.getMedium(srcOffset));
/*     */           break;
/*     */       } 
/* 179 */       encode3to4LittleEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static int encodedBufferSize(int len, boolean breakLines) {
/* 186 */     long len43 = (len << 2L) / 3L;
/*     */ 
/*     */     
/* 189 */     long ret = len43 + 3L & 0xFFFFFFFFFFFFFFFCL;
/*     */     
/* 191 */     if (breakLines) {
/* 192 */       ret += len43 / 76L;
/*     */     }
/*     */     
/* 195 */     return (ret < 2147483647L) ? (int)ret : Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */   private static int toInt(byte value) {
/* 199 */     return (value & 0xFF) << 16;
/*     */   }
/*     */   
/*     */   private static int toIntBE(short value) {
/* 203 */     return (value & 0xFF00) << 8 | (value & 0xFF) << 8;
/*     */   }
/*     */   
/*     */   private static int toIntLE(short value) {
/* 207 */     return (value & 0xFF) << 16 | value & 0xFF00;
/*     */   }
/*     */   
/*     */   private static int toIntBE(int mediumValue) {
/* 211 */     return mediumValue & 0xFF0000 | mediumValue & 0xFF00 | mediumValue & 0xFF;
/*     */   }
/*     */   
/*     */   private static int toIntLE(int mediumValue) {
/* 215 */     return (mediumValue & 0xFF) << 16 | mediumValue & 0xFF00 | (mediumValue & 0xFF0000) >>> 16;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void encode3to4BigEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
/* 221 */     switch (numSigBytes) {
/*     */       case 3:
/* 223 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | alphabet[inBuff & 0x3F]);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 2:
/* 229 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | 0x3D);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 1:
/* 235 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | 0x3D00 | 0x3D);
/*     */         break;
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
/*     */   private static void encode3to4LittleEndian(int inBuff, int numSigBytes, ByteBuf dest, int destOffset, byte[] alphabet) {
/* 249 */     switch (numSigBytes) {
/*     */       case 3:
/* 251 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | alphabet[inBuff & 0x3F] << 24);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 2:
/* 257 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | 0x3D000000);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 1:
/* 263 */         dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | 0x3D0000 | 0x3D000000);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ByteBuf decode(ByteBuf src) {
/* 275 */     return decode(src, Base64Dialect.STANDARD);
/*     */   }
/*     */   
/*     */   public static ByteBuf decode(ByteBuf src, Base64Dialect dialect) {
/* 279 */     ObjectUtil.checkNotNull(src, "src");
/*     */     
/* 281 */     ByteBuf dest = decode(src, src.readerIndex(), src.readableBytes(), dialect);
/* 282 */     src.readerIndex(src.writerIndex());
/* 283 */     return dest;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf decode(ByteBuf src, int off, int len) {
/* 288 */     return decode(src, off, len, Base64Dialect.STANDARD);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect) {
/* 293 */     return decode(src, off, len, dialect, src.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect, ByteBufAllocator allocator) {
/* 298 */     ObjectUtil.checkNotNull(src, "src");
/* 299 */     ObjectUtil.checkNotNull(dialect, "dialect");
/*     */ 
/*     */     
/* 302 */     return (new Decoder()).decode(src, off, len, allocator, dialect);
/*     */   }
/*     */ 
/*     */   
/*     */   static int decodedBufferSize(int len) {
/* 307 */     return len - (len >>> 2);
/*     */   }
/*     */   
/*     */   private static final class Decoder implements ByteProcessor {
/* 311 */     private final byte[] b4 = new byte[4];
/*     */     private int b4Posn;
/*     */     private byte[] decodabet;
/*     */     private int outBuffPosn;
/*     */     private ByteBuf dest;
/*     */     
/*     */     ByteBuf decode(ByteBuf src, int off, int len, ByteBufAllocator allocator, Base64Dialect dialect) {
/* 318 */       this.dest = allocator.buffer(Base64.decodedBufferSize(len)).order(src.order());
/*     */       
/* 320 */       this.decodabet = Base64.decodabet(dialect);
/*     */       try {
/* 322 */         src.forEachByte(off, len, this);
/* 323 */         return this.dest.slice(0, this.outBuffPosn);
/* 324 */       } catch (Throwable cause) {
/* 325 */         this.dest.release();
/* 326 */         PlatformDependent.throwException(cause);
/* 327 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean process(byte value) throws Exception {
/* 333 */       if (value > 0) {
/* 334 */         byte sbiDecode = this.decodabet[value];
/* 335 */         if (sbiDecode >= -5) {
/* 336 */           if (sbiDecode >= -1) {
/* 337 */             this.b4[this.b4Posn++] = value;
/* 338 */             if (this.b4Posn > 3) {
/* 339 */               this.outBuffPosn += decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
/* 340 */               this.b4Posn = 0;
/*     */ 
/*     */               
/* 343 */               return (value != 61);
/*     */             } 
/*     */           } 
/* 346 */           return true;
/*     */         } 
/*     */       } 
/* 349 */       throw new IllegalArgumentException("invalid Base64 input character: " + (short)(value & 0xFF) + " (decimal)");
/*     */     }
/*     */     
/*     */     private static int decode4to3(byte[] src, ByteBuf dest, int destOffset, byte[] decodabet) {
/*     */       int decodedValue;
/* 354 */       byte src0 = src[0];
/* 355 */       byte src1 = src[1];
/* 356 */       byte src2 = src[2];
/*     */       
/* 358 */       if (src2 == 61) {
/*     */         
/*     */         try {
/* 361 */           decodedValue = (decodabet[src0] & 0xFF) << 2 | (decodabet[src1] & 0xFF) >>> 4;
/* 362 */         } catch (IndexOutOfBoundsException ignored) {
/* 363 */           throw new IllegalArgumentException("not encoded in Base64");
/*     */         } 
/* 365 */         dest.setByte(destOffset, decodedValue);
/* 366 */         return 1;
/*     */       } 
/*     */       
/* 369 */       byte src3 = src[3];
/* 370 */       if (src3 == 61) {
/*     */         
/* 372 */         byte b1 = decodabet[src1];
/*     */         
/*     */         try {
/* 375 */           if (dest.order() == ByteOrder.BIG_ENDIAN)
/*     */           {
/*     */             
/* 378 */             decodedValue = ((decodabet[src0] & 0x3F) << 2 | (b1 & 0xF0) >> 4) << 8 | (b1 & 0xF) << 4 | (decodabet[src2] & 0xFC) >>> 2;
/*     */           }
/*     */           else
/*     */           {
/* 382 */             decodedValue = (decodabet[src0] & 0x3F) << 2 | (b1 & 0xF0) >> 4 | ((b1 & 0xF) << 4 | (decodabet[src2] & 0xFC) >>> 2) << 8;
/*     */           }
/*     */         
/* 385 */         } catch (IndexOutOfBoundsException ignored) {
/* 386 */           throw new IllegalArgumentException("not encoded in Base64");
/*     */         } 
/* 388 */         dest.setShort(destOffset, decodedValue);
/* 389 */         return 2;
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 394 */         if (dest.order() == ByteOrder.BIG_ENDIAN) {
/* 395 */           decodedValue = (decodabet[src0] & 0x3F) << 18 | (decodabet[src1] & 0xFF) << 12 | (decodabet[src2] & 0xFF) << 6 | decodabet[src3] & 0xFF;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 400 */           byte b1 = decodabet[src1];
/* 401 */           byte b2 = decodabet[src2];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 407 */           decodedValue = (decodabet[src0] & 0x3F) << 2 | (b1 & 0xF) << 12 | (b1 & 0xF0) >>> 4 | (b2 & 0x3) << 22 | (b2 & 0xFC) << 6 | (decodabet[src3] & 0xFF) << 16;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 418 */       catch (IndexOutOfBoundsException ignored) {
/* 419 */         throw new IllegalArgumentException("not encoded in Base64");
/*     */       } 
/* 421 */       dest.setMedium(destOffset, decodedValue);
/* 422 */       return 3;
/*     */     }
/*     */     
/*     */     private Decoder() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\base64\Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */