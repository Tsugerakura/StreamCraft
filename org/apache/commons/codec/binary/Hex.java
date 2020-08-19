/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.Charsets;
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
/*     */ public class Hex
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*  46 */   public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final char[] DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   private static final char[] DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeHex(String data) throws DecoderException {
/*  80 */     return decodeHex(data.toCharArray());
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
/*     */   public static byte[] decodeHex(char[] data) throws DecoderException {
/*  96 */     int len = data.length;
/*     */     
/*  98 */     if ((len & 0x1) != 0) {
/*  99 */       throw new DecoderException("Odd number of characters.");
/*     */     }
/*     */     
/* 102 */     byte[] out = new byte[len >> 1];
/*     */ 
/*     */     
/* 105 */     for (int i = 0, j = 0; j < len; i++) {
/* 106 */       int f = toDigit(data[j], j) << 4;
/* 107 */       j++;
/* 108 */       f |= toDigit(data[j], j);
/* 109 */       j++;
/* 110 */       out[i] = (byte)(f & 0xFF);
/*     */     } 
/*     */     
/* 113 */     return out;
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
/*     */   public static char[] encodeHex(byte[] data) {
/* 126 */     return encodeHex(data, true);
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
/*     */   public static char[] encodeHex(ByteBuffer data) {
/* 140 */     return encodeHex(data, true);
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
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase) {
/* 156 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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
/*     */   public static char[] encodeHex(ByteBuffer data, boolean toLowerCase) {
/* 172 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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
/*     */   protected static char[] encodeHex(byte[] data, char[] toDigits) {
/* 189 */     int l = data.length;
/* 190 */     char[] out = new char[l << 1];
/*     */     
/* 192 */     for (int i = 0, j = 0; i < l; i++) {
/* 193 */       out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
/* 194 */       out[j++] = toDigits[0xF & data[i]];
/*     */     } 
/* 196 */     return out;
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
/*     */   protected static char[] encodeHex(ByteBuffer data, char[] toDigits) {
/* 213 */     return encodeHex(data.array(), toDigits);
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
/*     */   public static String encodeHexString(byte[] data) {
/* 226 */     return new String(encodeHex(data));
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
/*     */   public static String encodeHexString(byte[] data, boolean toLowerCase) {
/* 241 */     return new String(encodeHex(data, toLowerCase));
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
/*     */   public static String encodeHexString(ByteBuffer data) {
/* 254 */     return new String(encodeHex(data));
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
/*     */   public static String encodeHexString(ByteBuffer data, boolean toLowerCase) {
/* 269 */     return new String(encodeHex(data, toLowerCase));
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
/*     */   protected static int toDigit(char ch, int index) throws DecoderException {
/* 284 */     int digit = Character.digit(ch, 16);
/* 285 */     if (digit == -1) {
/* 286 */       throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
/*     */     }
/* 288 */     return digit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Hex() {
/* 298 */     this.charset = DEFAULT_CHARSET;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Hex(Charset charset) {
/* 309 */     this.charset = charset;
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
/*     */   public Hex(String charsetName) {
/* 323 */     this(Charset.forName(charsetName));
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
/*     */   public byte[] decode(byte[] array) throws DecoderException {
/* 340 */     return decodeHex((new String(array, getCharset())).toCharArray());
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
/*     */   public byte[] decode(ByteBuffer buffer) throws DecoderException {
/* 357 */     return decodeHex((new String(buffer.array(), getCharset())).toCharArray());
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
/*     */   public Object decode(Object object) throws DecoderException {
/* 375 */     if (object instanceof String)
/* 376 */       return decode(((String)object).toCharArray()); 
/* 377 */     if (object instanceof byte[])
/* 378 */       return decode((byte[])object); 
/* 379 */     if (object instanceof ByteBuffer) {
/* 380 */       return decode((ByteBuffer)object);
/*     */     }
/*     */     try {
/* 383 */       return decodeHex((char[])object);
/* 384 */     } catch (ClassCastException e) {
/* 385 */       throw new DecoderException(e.getMessage(), e);
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
/*     */   public byte[] encode(byte[] array) {
/* 407 */     return encodeHexString(array).getBytes(getCharset());
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
/*     */   public byte[] encode(ByteBuffer array) {
/* 426 */     return encodeHexString(array).getBytes(getCharset());
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
/*     */   public Object encode(Object object) throws EncoderException {
/*     */     byte[] byteArray;
/* 448 */     if (object instanceof String) {
/* 449 */       byteArray = ((String)object).getBytes(getCharset());
/* 450 */     } else if (object instanceof ByteBuffer) {
/* 451 */       byteArray = ((ByteBuffer)object).array();
/*     */     } else {
/*     */       try {
/* 454 */         byteArray = (byte[])object;
/* 455 */       } catch (ClassCastException e) {
/* 456 */         throw new EncoderException(e.getMessage(), e);
/*     */       } 
/*     */     } 
/* 459 */     return encodeHex(byteArray);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 469 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharsetName() {
/* 479 */     return this.charset.name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 489 */     return super.toString() + "[charsetName=" + this.charset + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\binary\Hex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */