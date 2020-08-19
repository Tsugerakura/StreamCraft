/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseNCodec
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*     */   static final int EOF = -1;
/*     */   public static final int MIME_CHUNK_SIZE = 76;
/*     */   public static final int PEM_CHUNK_SIZE = 64;
/*     */   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
/*     */   private static final int DEFAULT_BUFFER_SIZE = 8192;
/*     */   protected static final int MASK_8BITS = 255;
/*     */   protected static final byte PAD_DEFAULT = 61;
/*     */   
/*     */   static class Context
/*     */   {
/*     */     int ibitWorkArea;
/*     */     long lbitWorkArea;
/*     */     byte[] buffer;
/*     */     int pos;
/*     */     int readPos;
/*     */     boolean eof;
/*     */     int currentLinePos;
/*     */     int modulus;
/*     */     
/*     */     public String toString() {
/* 103 */       return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", new Object[] {
/* 104 */             getClass().getSimpleName(), Arrays.toString(this.buffer), 
/* 105 */             Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos)
/*     */           });
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 159 */   protected final byte PAD = 61;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final byte pad;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int unencodedBlockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int encodedBlockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int lineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int chunkSeparatorLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
/* 192 */     this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte)61);
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
/*     */   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad) {
/* 206 */     this.unencodedBlockSize = unencodedBlockSize;
/* 207 */     this.encodedBlockSize = encodedBlockSize;
/* 208 */     boolean useChunking = (lineLength > 0 && chunkSeparatorLength > 0);
/* 209 */     this.lineLength = useChunking ? (lineLength / encodedBlockSize * encodedBlockSize) : 0;
/* 210 */     this.chunkSeparatorLength = chunkSeparatorLength;
/*     */     
/* 212 */     this.pad = pad;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasData(Context context) {
/* 222 */     return (context.buffer != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int available(Context context) {
/* 232 */     return (context.buffer != null) ? (context.pos - context.readPos) : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getDefaultBufferSize() {
/* 241 */     return 8192;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] resizeBuffer(Context context) {
/* 249 */     if (context.buffer == null) {
/* 250 */       context.buffer = new byte[getDefaultBufferSize()];
/* 251 */       context.pos = 0;
/* 252 */       context.readPos = 0;
/*     */     } else {
/* 254 */       byte[] b = new byte[context.buffer.length * 2];
/* 255 */       System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
/* 256 */       context.buffer = b;
/*     */     } 
/* 258 */     return context.buffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] ensureBufferSize(int size, Context context) {
/* 269 */     if (context.buffer == null || context.buffer.length < context.pos + size) {
/* 270 */       return resizeBuffer(context);
/*     */     }
/* 272 */     return context.buffer;
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
/*     */   int readResults(byte[] b, int bPos, int bAvail, Context context) {
/* 292 */     if (context.buffer != null) {
/* 293 */       int len = Math.min(available(context), bAvail);
/* 294 */       System.arraycopy(context.buffer, context.readPos, b, bPos, len);
/* 295 */       context.readPos += len;
/* 296 */       if (context.readPos >= context.pos) {
/* 297 */         context.buffer = null;
/*     */       }
/* 299 */       return len;
/*     */     } 
/* 301 */     return context.eof ? -1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean isWhiteSpace(byte byteToCheck) {
/* 312 */     switch (byteToCheck) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 13:
/*     */       case 32:
/* 317 */         return true;
/*     */     } 
/* 319 */     return false;
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 335 */     if (!(obj instanceof byte[])) {
/* 336 */       throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
/*     */     }
/* 338 */     return encode((byte[])obj);
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
/*     */   public String encodeToString(byte[] pArray) {
/* 350 */     return StringUtils.newStringUtf8(encode(pArray));
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
/*     */   public String encodeAsString(byte[] pArray) {
/* 363 */     return StringUtils.newStringUtf8(encode(pArray));
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
/*     */   public Object decode(Object obj) throws DecoderException {
/* 379 */     if (obj instanceof byte[])
/* 380 */       return decode((byte[])obj); 
/* 381 */     if (obj instanceof String) {
/* 382 */       return decode((String)obj);
/*     */     }
/* 384 */     throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
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
/*     */   public byte[] decode(String pArray) {
/* 396 */     return decode(StringUtils.getBytesUtf8(pArray));
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
/*     */   public byte[] decode(byte[] pArray) {
/* 408 */     if (pArray == null || pArray.length == 0) {
/* 409 */       return pArray;
/*     */     }
/* 411 */     Context context = new Context();
/* 412 */     decode(pArray, 0, pArray.length, context);
/* 413 */     decode(pArray, 0, -1, context);
/* 414 */     byte[] result = new byte[context.pos];
/* 415 */     readResults(result, 0, result.length, context);
/* 416 */     return result;
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
/*     */   public byte[] encode(byte[] pArray) {
/* 428 */     if (pArray == null || pArray.length == 0) {
/* 429 */       return pArray;
/*     */     }
/* 431 */     return encode(pArray, 0, pArray.length);
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
/*     */   public byte[] encode(byte[] pArray, int offset, int length) {
/* 448 */     if (pArray == null || pArray.length == 0) {
/* 449 */       return pArray;
/*     */     }
/* 451 */     Context context = new Context();
/* 452 */     encode(pArray, offset, length, context);
/* 453 */     encode(pArray, offset, -1, context);
/* 454 */     byte[] buf = new byte[context.pos - context.readPos];
/* 455 */     readResults(buf, 0, buf.length, context);
/* 456 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Context paramContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Context paramContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isInAlphabet(byte paramByte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
/* 486 */     for (byte octet : arrayOctet) {
/* 487 */       if (!isInAlphabet(octet) && (!allowWSPad || (octet != this.pad && 
/* 488 */         !isWhiteSpace(octet)))) {
/* 489 */         return false;
/*     */       }
/*     */     } 
/* 492 */     return true;
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
/*     */   public boolean isInAlphabet(String basen) {
/* 505 */     return isInAlphabet(StringUtils.getBytesUtf8(basen), true);
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
/*     */   protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
/* 518 */     if (arrayOctet == null) {
/* 519 */       return false;
/*     */     }
/* 521 */     for (byte element : arrayOctet) {
/* 522 */       if (this.pad == element || isInAlphabet(element)) {
/* 523 */         return true;
/*     */       }
/*     */     } 
/* 526 */     return false;
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
/*     */   public long getEncodedLength(byte[] pArray) {
/* 540 */     long len = ((pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize) * this.encodedBlockSize;
/* 541 */     if (this.lineLength > 0)
/*     */     {
/* 543 */       len += (len + this.lineLength - 1L) / this.lineLength * this.chunkSeparatorLength;
/*     */     }
/* 545 */     return len;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\binary\BaseNCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */