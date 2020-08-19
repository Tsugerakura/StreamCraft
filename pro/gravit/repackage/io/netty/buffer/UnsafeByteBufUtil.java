/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ReadOnlyBufferException;
/*     */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
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
/*     */ final class UnsafeByteBufUtil
/*     */ {
/*  35 */   private static final boolean UNALIGNED = PlatformDependent.isUnaligned();
/*     */   private static final byte ZERO = 0;
/*     */   
/*     */   static byte getByte(long address) {
/*  39 */     return PlatformDependent.getByte(address);
/*     */   }
/*     */   
/*     */   static short getShort(long address) {
/*  43 */     if (UNALIGNED) {
/*  44 */       short v = PlatformDependent.getShort(address);
/*  45 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Short.reverseBytes(v);
/*     */     } 
/*  47 */     return (short)(PlatformDependent.getByte(address) << 8 | PlatformDependent.getByte(address + 1L) & 0xFF);
/*     */   }
/*     */   
/*     */   static short getShortLE(long address) {
/*  51 */     if (UNALIGNED) {
/*  52 */       short v = PlatformDependent.getShort(address);
/*  53 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(v) : v;
/*     */     } 
/*  55 */     return (short)(PlatformDependent.getByte(address) & 0xFF | PlatformDependent.getByte(address + 1L) << 8);
/*     */   }
/*     */   
/*     */   static int getUnsignedMedium(long address) {
/*  59 */     if (UNALIGNED) {
/*  60 */       return (PlatformDependent.getByte(address) & 0xFF) << 16 | (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/*  61 */         PlatformDependent.getShort(address + 1L) : 
/*  62 */         Short.reverseBytes(PlatformDependent.getShort(address + 1L))) & 0xFFFF;
/*     */     }
/*  64 */     return (PlatformDependent.getByte(address) & 0xFF) << 16 | (
/*  65 */       PlatformDependent.getByte(address + 1L) & 0xFF) << 8 | 
/*  66 */       PlatformDependent.getByte(address + 2L) & 0xFF;
/*     */   }
/*     */   
/*     */   static int getUnsignedMediumLE(long address) {
/*  70 */     if (UNALIGNED) {
/*  71 */       return PlatformDependent.getByte(address) & 0xFF | ((PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/*  72 */         Short.reverseBytes(PlatformDependent.getShort(address + 1L)) : 
/*  73 */         PlatformDependent.getShort(address + 1L)) & 0xFFFF) << 8;
/*     */     }
/*  75 */     return PlatformDependent.getByte(address) & 0xFF | (
/*  76 */       PlatformDependent.getByte(address + 1L) & 0xFF) << 8 | (
/*  77 */       PlatformDependent.getByte(address + 2L) & 0xFF) << 16;
/*     */   }
/*     */   
/*     */   static int getInt(long address) {
/*  81 */     if (UNALIGNED) {
/*  82 */       int v = PlatformDependent.getInt(address);
/*  83 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Integer.reverseBytes(v);
/*     */     } 
/*  85 */     return PlatformDependent.getByte(address) << 24 | (
/*  86 */       PlatformDependent.getByte(address + 1L) & 0xFF) << 16 | (
/*  87 */       PlatformDependent.getByte(address + 2L) & 0xFF) << 8 | 
/*  88 */       PlatformDependent.getByte(address + 3L) & 0xFF;
/*     */   }
/*     */   
/*     */   static int getIntLE(long address) {
/*  92 */     if (UNALIGNED) {
/*  93 */       int v = PlatformDependent.getInt(address);
/*  94 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(v) : v;
/*     */     } 
/*  96 */     return PlatformDependent.getByte(address) & 0xFF | (
/*  97 */       PlatformDependent.getByte(address + 1L) & 0xFF) << 8 | (
/*  98 */       PlatformDependent.getByte(address + 2L) & 0xFF) << 16 | 
/*  99 */       PlatformDependent.getByte(address + 3L) << 24;
/*     */   }
/*     */   
/*     */   static long getLong(long address) {
/* 103 */     if (UNALIGNED) {
/* 104 */       long v = PlatformDependent.getLong(address);
/* 105 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Long.reverseBytes(v);
/*     */     } 
/* 107 */     return PlatformDependent.getByte(address) << 56L | (
/* 108 */       PlatformDependent.getByte(address + 1L) & 0xFFL) << 48L | (
/* 109 */       PlatformDependent.getByte(address + 2L) & 0xFFL) << 40L | (
/* 110 */       PlatformDependent.getByte(address + 3L) & 0xFFL) << 32L | (
/* 111 */       PlatformDependent.getByte(address + 4L) & 0xFFL) << 24L | (
/* 112 */       PlatformDependent.getByte(address + 5L) & 0xFFL) << 16L | (
/* 113 */       PlatformDependent.getByte(address + 6L) & 0xFFL) << 8L | 
/* 114 */       PlatformDependent.getByte(address + 7L) & 0xFFL;
/*     */   }
/*     */   
/*     */   static long getLongLE(long address) {
/* 118 */     if (UNALIGNED) {
/* 119 */       long v = PlatformDependent.getLong(address);
/* 120 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(v) : v;
/*     */     } 
/* 122 */     return PlatformDependent.getByte(address) & 0xFFL | (
/* 123 */       PlatformDependent.getByte(address + 1L) & 0xFFL) << 8L | (
/* 124 */       PlatformDependent.getByte(address + 2L) & 0xFFL) << 16L | (
/* 125 */       PlatformDependent.getByte(address + 3L) & 0xFFL) << 24L | (
/* 126 */       PlatformDependent.getByte(address + 4L) & 0xFFL) << 32L | (
/* 127 */       PlatformDependent.getByte(address + 5L) & 0xFFL) << 40L | (
/* 128 */       PlatformDependent.getByte(address + 6L) & 0xFFL) << 48L | 
/* 129 */       PlatformDependent.getByte(address + 7L) << 56L;
/*     */   }
/*     */   
/*     */   static void setByte(long address, int value) {
/* 133 */     PlatformDependent.putByte(address, (byte)value);
/*     */   }
/*     */   
/*     */   static void setShort(long address, int value) {
/* 137 */     if (UNALIGNED) {
/* 138 */       PlatformDependent.putShort(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : 
/* 139 */           Short.reverseBytes((short)value));
/*     */     } else {
/* 141 */       PlatformDependent.putByte(address, (byte)(value >>> 8));
/* 142 */       PlatformDependent.putByte(address + 1L, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setShortLE(long address, int value) {
/* 147 */     if (UNALIGNED) {
/* 148 */       PlatformDependent.putShort(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/* 149 */           Short.reverseBytes((short)value) : (short)value);
/*     */     } else {
/* 151 */       PlatformDependent.putByte(address, (byte)value);
/* 152 */       PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setMedium(long address, int value) {
/* 157 */     PlatformDependent.putByte(address, (byte)(value >>> 16));
/* 158 */     if (UNALIGNED) {
/* 159 */       PlatformDependent.putShort(address + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : 
/* 160 */           Short.reverseBytes((short)value));
/*     */     } else {
/* 162 */       PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
/* 163 */       PlatformDependent.putByte(address + 2L, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setMediumLE(long address, int value) {
/* 168 */     PlatformDependent.putByte(address, (byte)value);
/* 169 */     if (UNALIGNED) {
/* 170 */       PlatformDependent.putShort(address + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)(value >>> 8)) : (short)(value >>> 8));
/*     */     } else {
/*     */       
/* 173 */       PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
/* 174 */       PlatformDependent.putByte(address + 2L, (byte)(value >>> 16));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setInt(long address, int value) {
/* 179 */     if (UNALIGNED) {
/* 180 */       PlatformDependent.putInt(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes(value));
/*     */     } else {
/* 182 */       PlatformDependent.putByte(address, (byte)(value >>> 24));
/* 183 */       PlatformDependent.putByte(address + 1L, (byte)(value >>> 16));
/* 184 */       PlatformDependent.putByte(address + 2L, (byte)(value >>> 8));
/* 185 */       PlatformDependent.putByte(address + 3L, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setIntLE(long address, int value) {
/* 190 */     if (UNALIGNED) {
/* 191 */       PlatformDependent.putInt(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(value) : value);
/*     */     } else {
/* 193 */       PlatformDependent.putByte(address, (byte)value);
/* 194 */       PlatformDependent.putByte(address + 1L, (byte)(value >>> 8));
/* 195 */       PlatformDependent.putByte(address + 2L, (byte)(value >>> 16));
/* 196 */       PlatformDependent.putByte(address + 3L, (byte)(value >>> 24));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setLong(long address, long value) {
/* 201 */     if (UNALIGNED) {
/* 202 */       PlatformDependent.putLong(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes(value));
/*     */     } else {
/* 204 */       PlatformDependent.putByte(address, (byte)(int)(value >>> 56L));
/* 205 */       PlatformDependent.putByte(address + 1L, (byte)(int)(value >>> 48L));
/* 206 */       PlatformDependent.putByte(address + 2L, (byte)(int)(value >>> 40L));
/* 207 */       PlatformDependent.putByte(address + 3L, (byte)(int)(value >>> 32L));
/* 208 */       PlatformDependent.putByte(address + 4L, (byte)(int)(value >>> 24L));
/* 209 */       PlatformDependent.putByte(address + 5L, (byte)(int)(value >>> 16L));
/* 210 */       PlatformDependent.putByte(address + 6L, (byte)(int)(value >>> 8L));
/* 211 */       PlatformDependent.putByte(address + 7L, (byte)(int)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setLongLE(long address, long value) {
/* 216 */     if (UNALIGNED) {
/* 217 */       PlatformDependent.putLong(address, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(value) : value);
/*     */     } else {
/* 219 */       PlatformDependent.putByte(address, (byte)(int)value);
/* 220 */       PlatformDependent.putByte(address + 1L, (byte)(int)(value >>> 8L));
/* 221 */       PlatformDependent.putByte(address + 2L, (byte)(int)(value >>> 16L));
/* 222 */       PlatformDependent.putByte(address + 3L, (byte)(int)(value >>> 24L));
/* 223 */       PlatformDependent.putByte(address + 4L, (byte)(int)(value >>> 32L));
/* 224 */       PlatformDependent.putByte(address + 5L, (byte)(int)(value >>> 40L));
/* 225 */       PlatformDependent.putByte(address + 6L, (byte)(int)(value >>> 48L));
/* 226 */       PlatformDependent.putByte(address + 7L, (byte)(int)(value >>> 56L));
/*     */     } 
/*     */   }
/*     */   
/*     */   static byte getByte(byte[] array, int index) {
/* 231 */     return PlatformDependent.getByte(array, index);
/*     */   }
/*     */   
/*     */   static short getShort(byte[] array, int index) {
/* 235 */     if (UNALIGNED) {
/* 236 */       short v = PlatformDependent.getShort(array, index);
/* 237 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Short.reverseBytes(v);
/*     */     } 
/* 239 */     return 
/* 240 */       (short)(PlatformDependent.getByte(array, index) << 8 | PlatformDependent.getByte(array, index + 1) & 0xFF);
/*     */   }
/*     */   
/*     */   static short getShortLE(byte[] array, int index) {
/* 244 */     if (UNALIGNED) {
/* 245 */       short v = PlatformDependent.getShort(array, index);
/* 246 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(v) : v;
/*     */     } 
/* 248 */     return 
/* 249 */       (short)(PlatformDependent.getByte(array, index) & 0xFF | PlatformDependent.getByte(array, index + 1) << 8);
/*     */   }
/*     */   
/*     */   static int getUnsignedMedium(byte[] array, int index) {
/* 253 */     if (UNALIGNED) {
/* 254 */       return (PlatformDependent.getByte(array, index) & 0xFF) << 16 | (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/* 255 */         PlatformDependent.getShort(array, index + 1) : 
/* 256 */         Short.reverseBytes(PlatformDependent.getShort(array, index + 1))) & 0xFFFF;
/*     */     }
/*     */     
/* 259 */     return (PlatformDependent.getByte(array, index) & 0xFF) << 16 | (
/* 260 */       PlatformDependent.getByte(array, index + 1) & 0xFF) << 8 | 
/* 261 */       PlatformDependent.getByte(array, index + 2) & 0xFF;
/*     */   }
/*     */   
/*     */   static int getUnsignedMediumLE(byte[] array, int index) {
/* 265 */     if (UNALIGNED) {
/* 266 */       return PlatformDependent.getByte(array, index) & 0xFF | ((PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/* 267 */         Short.reverseBytes(PlatformDependent.getShort(array, index + 1)) : 
/* 268 */         PlatformDependent.getShort(array, index + 1)) & 0xFFFF) << 8;
/*     */     }
/* 270 */     return PlatformDependent.getByte(array, index) & 0xFF | (
/* 271 */       PlatformDependent.getByte(array, index + 1) & 0xFF) << 8 | (
/* 272 */       PlatformDependent.getByte(array, index + 2) & 0xFF) << 16;
/*     */   }
/*     */   
/*     */   static int getInt(byte[] array, int index) {
/* 276 */     if (UNALIGNED) {
/* 277 */       int v = PlatformDependent.getInt(array, index);
/* 278 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Integer.reverseBytes(v);
/*     */     } 
/* 280 */     return PlatformDependent.getByte(array, index) << 24 | (
/* 281 */       PlatformDependent.getByte(array, index + 1) & 0xFF) << 16 | (
/* 282 */       PlatformDependent.getByte(array, index + 2) & 0xFF) << 8 | 
/* 283 */       PlatformDependent.getByte(array, index + 3) & 0xFF;
/*     */   }
/*     */   
/*     */   static int getIntLE(byte[] array, int index) {
/* 287 */     if (UNALIGNED) {
/* 288 */       int v = PlatformDependent.getInt(array, index);
/* 289 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(v) : v;
/*     */     } 
/* 291 */     return PlatformDependent.getByte(array, index) & 0xFF | (
/* 292 */       PlatformDependent.getByte(array, index + 1) & 0xFF) << 8 | (
/* 293 */       PlatformDependent.getByte(array, index + 2) & 0xFF) << 16 | 
/* 294 */       PlatformDependent.getByte(array, index + 3) << 24;
/*     */   }
/*     */   
/*     */   static long getLong(byte[] array, int index) {
/* 298 */     if (UNALIGNED) {
/* 299 */       long v = PlatformDependent.getLong(array, index);
/* 300 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? v : Long.reverseBytes(v);
/*     */     } 
/* 302 */     return PlatformDependent.getByte(array, index) << 56L | (
/* 303 */       PlatformDependent.getByte(array, index + 1) & 0xFFL) << 48L | (
/* 304 */       PlatformDependent.getByte(array, index + 2) & 0xFFL) << 40L | (
/* 305 */       PlatformDependent.getByte(array, index + 3) & 0xFFL) << 32L | (
/* 306 */       PlatformDependent.getByte(array, index + 4) & 0xFFL) << 24L | (
/* 307 */       PlatformDependent.getByte(array, index + 5) & 0xFFL) << 16L | (
/* 308 */       PlatformDependent.getByte(array, index + 6) & 0xFFL) << 8L | 
/* 309 */       PlatformDependent.getByte(array, index + 7) & 0xFFL;
/*     */   }
/*     */   
/*     */   static long getLongLE(byte[] array, int index) {
/* 313 */     if (UNALIGNED) {
/* 314 */       long v = PlatformDependent.getLong(array, index);
/* 315 */       return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(v) : v;
/*     */     } 
/* 317 */     return PlatformDependent.getByte(array, index) & 0xFFL | (
/* 318 */       PlatformDependent.getByte(array, index + 1) & 0xFFL) << 8L | (
/* 319 */       PlatformDependent.getByte(array, index + 2) & 0xFFL) << 16L | (
/* 320 */       PlatformDependent.getByte(array, index + 3) & 0xFFL) << 24L | (
/* 321 */       PlatformDependent.getByte(array, index + 4) & 0xFFL) << 32L | (
/* 322 */       PlatformDependent.getByte(array, index + 5) & 0xFFL) << 40L | (
/* 323 */       PlatformDependent.getByte(array, index + 6) & 0xFFL) << 48L | 
/* 324 */       PlatformDependent.getByte(array, index + 7) << 56L;
/*     */   }
/*     */   
/*     */   static void setByte(byte[] array, int index, int value) {
/* 328 */     PlatformDependent.putByte(array, index, (byte)value);
/*     */   }
/*     */   
/*     */   static void setShort(byte[] array, int index, int value) {
/* 332 */     if (UNALIGNED) {
/* 333 */       PlatformDependent.putShort(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : 
/* 334 */           Short.reverseBytes((short)value));
/*     */     } else {
/* 336 */       PlatformDependent.putByte(array, index, (byte)(value >>> 8));
/* 337 */       PlatformDependent.putByte(array, index + 1, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setShortLE(byte[] array, int index, int value) {
/* 342 */     if (UNALIGNED) {
/* 343 */       PlatformDependent.putShort(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/* 344 */           Short.reverseBytes((short)value) : (short)value);
/*     */     } else {
/* 346 */       PlatformDependent.putByte(array, index, (byte)value);
/* 347 */       PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setMedium(byte[] array, int index, int value) {
/* 352 */     PlatformDependent.putByte(array, index, (byte)(value >>> 16));
/* 353 */     if (UNALIGNED) {
/* 354 */       PlatformDependent.putShort(array, index + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)value : 
/*     */           
/* 356 */           Short.reverseBytes((short)value));
/*     */     } else {
/* 358 */       PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
/* 359 */       PlatformDependent.putByte(array, index + 2, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setMediumLE(byte[] array, int index, int value) {
/* 364 */     PlatformDependent.putByte(array, index, (byte)value);
/* 365 */     if (UNALIGNED) {
/* 366 */       PlatformDependent.putShort(array, index + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? 
/* 367 */           Short.reverseBytes((short)(value >>> 8)) : (short)(value >>> 8));
/*     */     } else {
/*     */       
/* 370 */       PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
/* 371 */       PlatformDependent.putByte(array, index + 2, (byte)(value >>> 16));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setInt(byte[] array, int index, int value) {
/* 376 */     if (UNALIGNED) {
/* 377 */       PlatformDependent.putInt(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Integer.reverseBytes(value));
/*     */     } else {
/* 379 */       PlatformDependent.putByte(array, index, (byte)(value >>> 24));
/* 380 */       PlatformDependent.putByte(array, index + 1, (byte)(value >>> 16));
/* 381 */       PlatformDependent.putByte(array, index + 2, (byte)(value >>> 8));
/* 382 */       PlatformDependent.putByte(array, index + 3, (byte)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setIntLE(byte[] array, int index, int value) {
/* 387 */     if (UNALIGNED) {
/* 388 */       PlatformDependent.putInt(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(value) : value);
/*     */     } else {
/* 390 */       PlatformDependent.putByte(array, index, (byte)value);
/* 391 */       PlatformDependent.putByte(array, index + 1, (byte)(value >>> 8));
/* 392 */       PlatformDependent.putByte(array, index + 2, (byte)(value >>> 16));
/* 393 */       PlatformDependent.putByte(array, index + 3, (byte)(value >>> 24));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setLong(byte[] array, int index, long value) {
/* 398 */     if (UNALIGNED) {
/* 399 */       PlatformDependent.putLong(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? value : Long.reverseBytes(value));
/*     */     } else {
/* 401 */       PlatformDependent.putByte(array, index, (byte)(int)(value >>> 56L));
/* 402 */       PlatformDependent.putByte(array, index + 1, (byte)(int)(value >>> 48L));
/* 403 */       PlatformDependent.putByte(array, index + 2, (byte)(int)(value >>> 40L));
/* 404 */       PlatformDependent.putByte(array, index + 3, (byte)(int)(value >>> 32L));
/* 405 */       PlatformDependent.putByte(array, index + 4, (byte)(int)(value >>> 24L));
/* 406 */       PlatformDependent.putByte(array, index + 5, (byte)(int)(value >>> 16L));
/* 407 */       PlatformDependent.putByte(array, index + 6, (byte)(int)(value >>> 8L));
/* 408 */       PlatformDependent.putByte(array, index + 7, (byte)(int)value);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setLongLE(byte[] array, int index, long value) {
/* 413 */     if (UNALIGNED) {
/* 414 */       PlatformDependent.putLong(array, index, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(value) : value);
/*     */     } else {
/* 416 */       PlatformDependent.putByte(array, index, (byte)(int)value);
/* 417 */       PlatformDependent.putByte(array, index + 1, (byte)(int)(value >>> 8L));
/* 418 */       PlatformDependent.putByte(array, index + 2, (byte)(int)(value >>> 16L));
/* 419 */       PlatformDependent.putByte(array, index + 3, (byte)(int)(value >>> 24L));
/* 420 */       PlatformDependent.putByte(array, index + 4, (byte)(int)(value >>> 32L));
/* 421 */       PlatformDependent.putByte(array, index + 5, (byte)(int)(value >>> 40L));
/* 422 */       PlatformDependent.putByte(array, index + 6, (byte)(int)(value >>> 48L));
/* 423 */       PlatformDependent.putByte(array, index + 7, (byte)(int)(value >>> 56L));
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setZero(byte[] array, int index, int length) {
/* 428 */     if (length == 0) {
/*     */       return;
/*     */     }
/* 431 */     PlatformDependent.setMemory(array, index, length, (byte)0);
/*     */   }
/*     */   
/*     */   static ByteBuf copy(AbstractByteBuf buf, long addr, int index, int length) {
/* 435 */     buf.checkIndex(index, length);
/* 436 */     ByteBuf copy = buf.alloc().directBuffer(length, buf.maxCapacity());
/* 437 */     if (length != 0) {
/* 438 */       if (copy.hasMemoryAddress()) {
/* 439 */         PlatformDependent.copyMemory(addr, copy.memoryAddress(), length);
/* 440 */         copy.setIndex(0, length);
/*     */       } else {
/* 442 */         copy.writeBytes(buf, index, length);
/*     */       } 
/*     */     }
/* 445 */     return copy;
/*     */   }
/*     */   
/*     */   static int setBytes(AbstractByteBuf buf, long addr, int index, InputStream in, int length) throws IOException {
/* 449 */     buf.checkIndex(index, length);
/* 450 */     ByteBuf tmpBuf = buf.alloc().heapBuffer(length);
/*     */     try {
/* 452 */       byte[] tmp = tmpBuf.array();
/* 453 */       int offset = tmpBuf.arrayOffset();
/* 454 */       int readBytes = in.read(tmp, offset, length);
/* 455 */       if (readBytes > 0) {
/* 456 */         PlatformDependent.copyMemory(tmp, offset, addr, readBytes);
/*     */       }
/* 458 */       return readBytes;
/*     */     } finally {
/* 460 */       tmpBuf.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuf dst, int dstIndex, int length) {
/* 465 */     buf.checkIndex(index, length);
/* 466 */     ObjectUtil.checkNotNull(dst, "dst");
/* 467 */     if (MathUtil.isOutOfBounds(dstIndex, length, dst.capacity())) {
/* 468 */       throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
/*     */     }
/*     */     
/* 471 */     if (dst.hasMemoryAddress()) {
/* 472 */       PlatformDependent.copyMemory(addr, dst.memoryAddress() + dstIndex, length);
/* 473 */     } else if (dst.hasArray()) {
/* 474 */       PlatformDependent.copyMemory(addr, dst.array(), dst.arrayOffset() + dstIndex, length);
/*     */     } else {
/* 476 */       dst.setBytes(dstIndex, buf, index, length);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void getBytes(AbstractByteBuf buf, long addr, int index, byte[] dst, int dstIndex, int length) {
/* 481 */     buf.checkIndex(index, length);
/* 482 */     ObjectUtil.checkNotNull(dst, "dst");
/* 483 */     if (MathUtil.isOutOfBounds(dstIndex, length, dst.length)) {
/* 484 */       throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
/*     */     }
/* 486 */     if (length != 0) {
/* 487 */       PlatformDependent.copyMemory(addr, dst, dstIndex, length);
/*     */     }
/*     */   }
/*     */   
/*     */   static void getBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer dst) {
/* 492 */     buf.checkIndex(index, dst.remaining());
/* 493 */     if (dst.remaining() == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 497 */     if (dst.isDirect()) {
/* 498 */       if (dst.isReadOnly())
/*     */       {
/* 500 */         throw new ReadOnlyBufferException();
/*     */       }
/*     */       
/* 503 */       long dstAddress = PlatformDependent.directBufferAddress(dst);
/* 504 */       PlatformDependent.copyMemory(addr, dstAddress + dst.position(), dst.remaining());
/* 505 */       dst.position(dst.position() + dst.remaining());
/* 506 */     } else if (dst.hasArray()) {
/*     */       
/* 508 */       PlatformDependent.copyMemory(addr, dst.array(), dst.arrayOffset() + dst.position(), dst.remaining());
/* 509 */       dst.position(dst.position() + dst.remaining());
/*     */     } else {
/* 511 */       dst.put(buf.nioBuffer());
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuf src, int srcIndex, int length) {
/* 516 */     buf.checkIndex(index, length);
/* 517 */     ObjectUtil.checkNotNull(src, "src");
/* 518 */     if (MathUtil.isOutOfBounds(srcIndex, length, src.capacity())) {
/* 519 */       throw new IndexOutOfBoundsException("srcIndex: " + srcIndex);
/*     */     }
/*     */     
/* 522 */     if (length != 0) {
/* 523 */       if (src.hasMemoryAddress()) {
/* 524 */         PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, addr, length);
/* 525 */       } else if (src.hasArray()) {
/* 526 */         PlatformDependent.copyMemory(src.array(), src.arrayOffset() + srcIndex, addr, length);
/*     */       } else {
/* 528 */         src.getBytes(srcIndex, buf, index, length);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static void setBytes(AbstractByteBuf buf, long addr, int index, byte[] src, int srcIndex, int length) {
/* 534 */     buf.checkIndex(index, length);
/* 535 */     if (length != 0) {
/* 536 */       PlatformDependent.copyMemory(src, srcIndex, addr, length);
/*     */     }
/*     */   }
/*     */   
/*     */   static void setBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src) {
/* 541 */     int length = src.remaining();
/* 542 */     if (length == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 546 */     if (src.isDirect()) {
/* 547 */       buf.checkIndex(index, length);
/*     */       
/* 549 */       long srcAddress = PlatformDependent.directBufferAddress(src);
/* 550 */       PlatformDependent.copyMemory(srcAddress + src.position(), addr, length);
/* 551 */       src.position(src.position() + length);
/* 552 */     } else if (src.hasArray()) {
/* 553 */       buf.checkIndex(index, length);
/*     */       
/* 555 */       PlatformDependent.copyMemory(src.array(), src.arrayOffset() + src.position(), addr, length);
/* 556 */       src.position(src.position() + length);
/*     */     }
/* 558 */     else if (length < 8) {
/* 559 */       setSingleBytes(buf, addr, index, src, length);
/*     */     } else {
/*     */       
/* 562 */       assert buf.nioBufferCount() == 1;
/* 563 */       ByteBuffer internalBuffer = buf.internalNioBuffer(index, length);
/* 564 */       internalBuffer.put(src);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void setSingleBytes(AbstractByteBuf buf, long addr, int index, ByteBuffer src, int length) {
/* 571 */     buf.checkIndex(index, length);
/* 572 */     int srcPosition = src.position();
/* 573 */     int srcLimit = src.limit();
/* 574 */     long dstAddr = addr;
/* 575 */     for (int srcIndex = srcPosition; srcIndex < srcLimit; srcIndex++) {
/* 576 */       byte value = src.get(srcIndex);
/* 577 */       PlatformDependent.putByte(dstAddr, value);
/* 578 */       dstAddr++;
/*     */     } 
/* 580 */     src.position(srcLimit);
/*     */   }
/*     */   
/*     */   static void getBytes(AbstractByteBuf buf, long addr, int index, OutputStream out, int length) throws IOException {
/* 584 */     buf.checkIndex(index, length);
/* 585 */     if (length != 0) {
/* 586 */       int len = Math.min(length, 8192);
/* 587 */       if (len <= 1024 || !buf.alloc().isDirectBufferPooled()) {
/* 588 */         getBytes(addr, ByteBufUtil.threadLocalTempArray(len), 0, len, out, length);
/*     */       } else {
/*     */         
/* 591 */         ByteBuf tmpBuf = buf.alloc().heapBuffer(len);
/*     */         try {
/* 593 */           byte[] tmp = tmpBuf.array();
/* 594 */           int offset = tmpBuf.arrayOffset();
/* 595 */           getBytes(addr, tmp, offset, len, out, length);
/*     */         } finally {
/* 597 */           tmpBuf.release();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void getBytes(long inAddr, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException {
/*     */     do {
/* 606 */       int len = Math.min(inLen, outLen);
/* 607 */       PlatformDependent.copyMemory(inAddr, in, inOffset, len);
/* 608 */       out.write(in, inOffset, len);
/* 609 */       outLen -= len;
/* 610 */       inAddr += len;
/* 611 */     } while (outLen > 0);
/*     */   }
/*     */   
/*     */   static void setZero(long addr, int length) {
/* 615 */     if (length == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 619 */     PlatformDependent.setMemory(addr, length, (byte)0);
/*     */   }
/*     */ 
/*     */   
/*     */   static UnpooledUnsafeDirectByteBuf newUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 624 */     if (PlatformDependent.useDirectBufferNoCleaner()) {
/* 625 */       return new UnpooledUnsafeNoCleanerDirectByteBuf(alloc, initialCapacity, maxCapacity);
/*     */     }
/* 627 */     return new UnpooledUnsafeDirectByteBuf(alloc, initialCapacity, maxCapacity);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnsafeByteBufUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */