/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ import org.apache.commons.codec.binary.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLCodec
/*     */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*     */ {
/*     */   @Deprecated
/*     */   protected volatile String charset;
/*     */   protected static final byte ESCAPE_CHAR = 37;
/*     */   @Deprecated
/*     */   protected static final BitSet WWW_FORM_URL;
/*  72 */   private static final BitSet WWW_FORM_URL_SAFE = new BitSet(256);
/*     */ 
/*     */   
/*     */   static {
/*     */     int i;
/*  77 */     for (i = 97; i <= 122; i++) {
/*  78 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*  80 */     for (i = 65; i <= 90; i++) {
/*  81 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*     */     
/*  84 */     for (i = 48; i <= 57; i++) {
/*  85 */       WWW_FORM_URL_SAFE.set(i);
/*     */     }
/*     */     
/*  88 */     WWW_FORM_URL_SAFE.set(45);
/*  89 */     WWW_FORM_URL_SAFE.set(95);
/*  90 */     WWW_FORM_URL_SAFE.set(46);
/*  91 */     WWW_FORM_URL_SAFE.set(42);
/*     */     
/*  93 */     WWW_FORM_URL_SAFE.set(32);
/*     */ 
/*     */     
/*  96 */     WWW_FORM_URL = (BitSet)WWW_FORM_URL_SAFE.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLCodec() {
/* 104 */     this("UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLCodec(String charset) {
/* 114 */     this.charset = charset;
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
/*     */   public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
/* 127 */     if (bytes == null) {
/* 128 */       return null;
/*     */     }
/* 130 */     if (urlsafe == null) {
/* 131 */       urlsafe = WWW_FORM_URL_SAFE;
/*     */     }
/*     */     
/* 134 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 135 */     for (byte c : bytes) {
/* 136 */       int b = c;
/* 137 */       if (b < 0) {
/* 138 */         b = 256 + b;
/*     */       }
/* 140 */       if (urlsafe.get(b)) {
/* 141 */         if (b == 32) {
/* 142 */           b = 43;
/*     */         }
/* 144 */         buffer.write(b);
/*     */       } else {
/* 146 */         buffer.write(37);
/* 147 */         char hex1 = Utils.hexDigit(b >> 4);
/* 148 */         char hex2 = Utils.hexDigit(b);
/* 149 */         buffer.write(hex1);
/* 150 */         buffer.write(hex2);
/*     */       } 
/*     */     } 
/* 153 */     return buffer.toByteArray();
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
/*     */   public static final byte[] decodeUrl(byte[] bytes) throws DecoderException {
/* 167 */     if (bytes == null) {
/* 168 */       return null;
/*     */     }
/* 170 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 171 */     for (int i = 0; i < bytes.length; i++) {
/* 172 */       int b = bytes[i];
/* 173 */       if (b == 43) {
/* 174 */         buffer.write(32);
/* 175 */       } else if (b == 37) {
/*     */         try {
/* 177 */           int u = Utils.digit16(bytes[++i]);
/* 178 */           int l = Utils.digit16(bytes[++i]);
/* 179 */           buffer.write((char)((u << 4) + l));
/* 180 */         } catch (ArrayIndexOutOfBoundsException e) {
/* 181 */           throw new DecoderException("Invalid URL encoding: ", e);
/*     */         } 
/*     */       } else {
/* 184 */         buffer.write(b);
/*     */       } 
/*     */     } 
/* 187 */     return buffer.toByteArray();
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
/*     */   public byte[] encode(byte[] bytes) {
/* 199 */     return encodeUrl(WWW_FORM_URL_SAFE, bytes);
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
/*     */   public byte[] decode(byte[] bytes) throws DecoderException {
/* 215 */     return decodeUrl(bytes);
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
/*     */   public String encode(String str, String charset) throws UnsupportedEncodingException {
/* 230 */     if (str == null) {
/* 231 */       return null;
/*     */     }
/* 233 */     return StringUtils.newStringUsAscii(encode(str.getBytes(charset)));
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
/*     */   public String encode(String str) throws EncoderException {
/* 249 */     if (str == null) {
/* 250 */       return null;
/*     */     }
/*     */     try {
/* 253 */       return encode(str, getDefaultCharset());
/* 254 */     } catch (UnsupportedEncodingException e) {
/* 255 */       throw new EncoderException(e.getMessage(), e);
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
/*     */   public String decode(String str, String charset) throws DecoderException, UnsupportedEncodingException {
/* 275 */     if (str == null) {
/* 276 */       return null;
/*     */     }
/* 278 */     return new String(decode(StringUtils.getBytesUsAscii(str)), charset);
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
/*     */   public String decode(String str) throws DecoderException {
/* 294 */     if (str == null) {
/* 295 */       return null;
/*     */     }
/*     */     try {
/* 298 */       return decode(str, getDefaultCharset());
/* 299 */     } catch (UnsupportedEncodingException e) {
/* 300 */       throw new DecoderException(e.getMessage(), e);
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 315 */     if (obj == null)
/* 316 */       return null; 
/* 317 */     if (obj instanceof byte[])
/* 318 */       return encode((byte[])obj); 
/* 319 */     if (obj instanceof String) {
/* 320 */       return encode((String)obj);
/*     */     }
/* 322 */     throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be URL encoded");
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
/*     */   public Object decode(Object obj) throws DecoderException {
/* 340 */     if (obj == null)
/* 341 */       return null; 
/* 342 */     if (obj instanceof byte[])
/* 343 */       return decode((byte[])obj); 
/* 344 */     if (obj instanceof String) {
/* 345 */       return decode((String)obj);
/*     */     }
/* 347 */     throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be URL decoded");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultCharset() {
/* 358 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getEncoding() {
/* 370 */     return this.charset;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\net\URLCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */