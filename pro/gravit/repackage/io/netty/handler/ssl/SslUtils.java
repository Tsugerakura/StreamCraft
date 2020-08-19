/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.SSLHandshakeException;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.base64.Base64;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.base64.Base64Dialect;
/*     */ import pro.gravit.repackage.io.netty.util.NetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
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
/*     */ final class SslUtils
/*     */ {
/*  45 */   static final Set<String> TLSV13_CIPHERS = Collections.unmodifiableSet(new LinkedHashSet<String>(
/*  46 */         Arrays.asList(new String[] { "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "TLS_AES_128_GCM_SHA256", "TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_SHA256" })));
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_SSL_V2 = "SSLv2";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_SSL_V3 = "SSLv3";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_TLS_V1 = "TLSv1";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";
/*     */ 
/*     */   
/*     */   static final String PROTOCOL_TLS_V1_3 = "TLSv1.3";
/*     */ 
/*     */   
/*     */   static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
/*     */ 
/*     */   
/*     */   static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
/*     */ 
/*     */   
/*     */   static final int SSL_CONTENT_TYPE_ALERT = 21;
/*     */ 
/*     */   
/*     */   static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
/*     */ 
/*     */   
/*     */   static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
/*     */ 
/*     */   
/*     */   static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
/*     */ 
/*     */   
/*     */   static final int SSL_RECORD_HEADER_LENGTH = 5;
/*     */ 
/*     */   
/*     */   static final int NOT_ENOUGH_DATA = -1;
/*     */ 
/*     */   
/*     */   static final int NOT_ENCRYPTED = -2;
/*     */ 
/*     */   
/*     */   static final String[] DEFAULT_CIPHER_SUITES;
/*     */ 
/*     */   
/*     */   static final String[] DEFAULT_TLSV13_CIPHER_SUITES;
/*     */   
/* 102 */   static final String[] TLSV13_CIPHER_SUITES = new String[] { "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384" };
/*     */   
/*     */   static {
/* 105 */     if (PlatformDependent.javaVersion() >= 11) {
/* 106 */       DEFAULT_TLSV13_CIPHER_SUITES = TLSV13_CIPHER_SUITES;
/*     */     } else {
/* 108 */       DEFAULT_TLSV13_CIPHER_SUITES = EmptyArrays.EMPTY_STRINGS;
/*     */     } 
/*     */     
/* 111 */     List<String> defaultCiphers = new ArrayList<String>();
/*     */     
/* 113 */     defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
/* 114 */     defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
/* 115 */     defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
/* 116 */     defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
/* 117 */     defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
/*     */     
/* 119 */     defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
/*     */     
/* 121 */     defaultCiphers.add("TLS_RSA_WITH_AES_128_GCM_SHA256");
/* 122 */     defaultCiphers.add("TLS_RSA_WITH_AES_128_CBC_SHA");
/*     */     
/* 124 */     defaultCiphers.add("TLS_RSA_WITH_AES_256_CBC_SHA");
/*     */     
/* 126 */     Collections.addAll(defaultCiphers, DEFAULT_TLSV13_CIPHER_SUITES);
/*     */     
/* 128 */     DEFAULT_CIPHER_SUITES = defaultCiphers.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void addIfSupported(Set<String> supported, List<String> enabled, String... names) {
/* 135 */     for (String n : names) {
/* 136 */       if (supported.contains(n)) {
/* 137 */         enabled.add(n);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, Iterable<String> fallbackCiphers) {
/* 143 */     if (defaultCiphers.isEmpty()) {
/* 144 */       for (String cipher : fallbackCiphers) {
/* 145 */         if (cipher.startsWith("SSL_") || cipher.contains("_RC4_")) {
/*     */           continue;
/*     */         }
/* 148 */         defaultCiphers.add(cipher);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, String... fallbackCiphers) {
/* 154 */     useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, Arrays.asList(fallbackCiphers));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static SSLHandshakeException toSSLHandshakeException(Throwable e) {
/* 161 */     if (e instanceof SSLHandshakeException) {
/* 162 */       return (SSLHandshakeException)e;
/*     */     }
/*     */     
/* 165 */     return (SSLHandshakeException)(new SSLHandshakeException(e.getMessage())).initCause(e);
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
/*     */   static int getEncryptedPacketLength(ByteBuf buffer, int offset) {
/*     */     boolean tls;
/* 186 */     int packetLength = 0;
/*     */ 
/*     */ 
/*     */     
/* 190 */     switch (buffer.getUnsignedByte(offset)) {
/*     */       case 20:
/*     */       case 21:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/* 196 */         tls = true;
/*     */         break;
/*     */       
/*     */       default:
/* 200 */         tls = false;
/*     */         break;
/*     */     } 
/* 203 */     if (tls) {
/*     */       
/* 205 */       int majorVersion = buffer.getUnsignedByte(offset + 1);
/* 206 */       if (majorVersion == 3) {
/*     */         
/* 208 */         packetLength = unsignedShortBE(buffer, offset + 3) + 5;
/* 209 */         if (packetLength <= 5)
/*     */         {
/* 211 */           tls = false;
/*     */         }
/*     */       } else {
/*     */         
/* 215 */         tls = false;
/*     */       } 
/*     */     } 
/*     */     
/* 219 */     if (!tls) {
/*     */       
/* 221 */       int headerLength = ((buffer.getUnsignedByte(offset) & 0x80) != 0) ? 2 : 3;
/* 222 */       int majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
/* 223 */       if (majorVersion == 2 || majorVersion == 3) {
/*     */ 
/*     */         
/* 226 */         packetLength = (headerLength == 2) ? ((shortBE(buffer, offset) & Short.MAX_VALUE) + 2) : ((shortBE(buffer, offset) & 0x3FFF) + 3);
/* 227 */         if (packetLength <= headerLength) {
/* 228 */           return -1;
/*     */         }
/*     */       } else {
/* 231 */         return -2;
/*     */       } 
/*     */     } 
/* 234 */     return packetLength;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int unsignedShortBE(ByteBuf buffer, int offset) {
/* 240 */     return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer
/* 241 */       .getUnsignedShort(offset) : buffer.getUnsignedShortLE(offset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static short shortBE(ByteBuf buffer, int offset) {
/* 247 */     return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer
/* 248 */       .getShort(offset) : buffer.getShortLE(offset);
/*     */   }
/*     */   
/*     */   private static short unsignedByte(byte b) {
/* 252 */     return (short)(b & 0xFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int unsignedShortBE(ByteBuffer buffer, int offset) {
/* 257 */     return shortBE(buffer, offset) & 0xFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   private static short shortBE(ByteBuffer buffer, int offset) {
/* 262 */     return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer
/* 263 */       .getShort(offset) : ByteBufUtil.swapShort(buffer.getShort(offset));
/*     */   }
/*     */   
/*     */   static int getEncryptedPacketLength(ByteBuffer[] buffers, int offset) {
/* 267 */     ByteBuffer buffer = buffers[offset];
/*     */ 
/*     */     
/* 270 */     if (buffer.remaining() >= 5) {
/* 271 */       return getEncryptedPacketLength(buffer);
/*     */     }
/*     */ 
/*     */     
/* 275 */     ByteBuffer tmp = ByteBuffer.allocate(5);
/*     */     
/*     */     do {
/* 278 */       buffer = buffers[offset++].duplicate();
/* 279 */       if (buffer.remaining() > tmp.remaining()) {
/* 280 */         buffer.limit(buffer.position() + tmp.remaining());
/*     */       }
/* 282 */       tmp.put(buffer);
/* 283 */     } while (tmp.hasRemaining());
/*     */ 
/*     */     
/* 286 */     tmp.flip();
/* 287 */     return getEncryptedPacketLength(tmp);
/*     */   }
/*     */   private static int getEncryptedPacketLength(ByteBuffer buffer) {
/*     */     boolean tls;
/* 291 */     int packetLength = 0;
/* 292 */     int pos = buffer.position();
/*     */ 
/*     */     
/* 295 */     switch (unsignedByte(buffer.get(pos))) {
/*     */       case 20:
/*     */       case 21:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/* 301 */         tls = true;
/*     */         break;
/*     */       
/*     */       default:
/* 305 */         tls = false;
/*     */         break;
/*     */     } 
/* 308 */     if (tls) {
/*     */       
/* 310 */       int majorVersion = unsignedByte(buffer.get(pos + 1));
/* 311 */       if (majorVersion == 3) {
/*     */         
/* 313 */         packetLength = unsignedShortBE(buffer, pos + 3) + 5;
/* 314 */         if (packetLength <= 5)
/*     */         {
/* 316 */           tls = false;
/*     */         }
/*     */       } else {
/*     */         
/* 320 */         tls = false;
/*     */       } 
/*     */     } 
/*     */     
/* 324 */     if (!tls) {
/*     */       
/* 326 */       int headerLength = ((unsignedByte(buffer.get(pos)) & 0x80) != 0) ? 2 : 3;
/* 327 */       int majorVersion = unsignedByte(buffer.get(pos + headerLength + 1));
/* 328 */       if (majorVersion == 2 || majorVersion == 3) {
/*     */ 
/*     */         
/* 331 */         packetLength = (headerLength == 2) ? ((shortBE(buffer, pos) & Short.MAX_VALUE) + 2) : ((shortBE(buffer, pos) & 0x3FFF) + 3);
/* 332 */         if (packetLength <= headerLength) {
/* 333 */           return -1;
/*     */         }
/*     */       } else {
/* 336 */         return -2;
/*     */       } 
/*     */     } 
/* 339 */     return packetLength;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void handleHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean notify) {
/* 345 */     ctx.flush();
/* 346 */     if (notify) {
/* 347 */       ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
/*     */     }
/* 349 */     ctx.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void zeroout(ByteBuf buffer) {
/* 356 */     if (!buffer.isReadOnly()) {
/* 357 */       buffer.setZero(0, buffer.capacity());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void zerooutAndRelease(ByteBuf buffer) {
/* 365 */     zeroout(buffer);
/* 366 */     buffer.release();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static ByteBuf toBase64(ByteBufAllocator allocator, ByteBuf src) {
/* 375 */     ByteBuf dst = Base64.encode(src, src.readerIndex(), src
/* 376 */         .readableBytes(), true, Base64Dialect.STANDARD, allocator);
/* 377 */     src.readerIndex(src.writerIndex());
/* 378 */     return dst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isValidHostNameForSNI(String hostname) {
/* 385 */     return (hostname != null && hostname
/* 386 */       .indexOf('.') > 0 && 
/* 387 */       !hostname.endsWith(".") && 
/* 388 */       !NetUtil.isValidIpV4Address(hostname) && 
/* 389 */       !NetUtil.isValidIpV6Address(hostname));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isTLSv13Cipher(String cipher) {
/* 397 */     return TLSV13_CIPHERS.contains(cipher);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */