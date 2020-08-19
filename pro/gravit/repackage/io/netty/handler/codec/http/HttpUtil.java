/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.NetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HttpUtil
/*     */ {
/*  37 */   private static final AsciiString CHARSET_EQUALS = AsciiString.of(HttpHeaderValues.CHARSET + "=");
/*  38 */   private static final AsciiString SEMICOLON = AsciiString.cached(";");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOriginForm(URI uri) {
/*  47 */     return (uri.getScheme() == null && uri.getSchemeSpecificPart() == null && uri
/*  48 */       .getHost() == null && uri.getAuthority() == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAsteriskForm(URI uri) {
/*  56 */     return ("*".equals(uri.getPath()) && uri
/*  57 */       .getScheme() == null && uri.getSchemeSpecificPart() == null && uri
/*  58 */       .getHost() == null && uri.getAuthority() == null && uri.getQuery() == null && uri
/*  59 */       .getFragment() == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isKeepAlive(HttpMessage message) {
/*  70 */     return (!message.headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.CLOSE, true) && (message
/*  71 */       .protocolVersion().isKeepAliveDefault() || message
/*  72 */       .headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.KEEP_ALIVE, true)));
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
/*     */   public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
/*  96 */     setKeepAlive(message.headers(), message.protocolVersion(), keepAlive);
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
/*     */   public static void setKeepAlive(HttpHeaders h, HttpVersion httpVersion, boolean keepAlive) {
/* 119 */     if (httpVersion.isKeepAliveDefault()) {
/* 120 */       if (keepAlive) {
/* 121 */         h.remove((CharSequence)HttpHeaderNames.CONNECTION);
/*     */       } else {
/* 123 */         h.set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
/*     */       }
/*     */     
/* 126 */     } else if (keepAlive) {
/* 127 */       h.set((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
/*     */     } else {
/* 129 */       h.remove((CharSequence)HttpHeaderNames.CONNECTION);
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
/*     */   public static long getContentLength(HttpMessage message) {
/* 147 */     String value = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/* 148 */     if (value != null) {
/* 149 */       return Long.parseLong(value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 154 */     long webSocketContentLength = getWebSocketContentLength(message);
/* 155 */     if (webSocketContentLength >= 0L) {
/* 156 */       return webSocketContentLength;
/*     */     }
/*     */ 
/*     */     
/* 160 */     throw new NumberFormatException("header not found: " + HttpHeaderNames.CONTENT_LENGTH);
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
/*     */   public static long getContentLength(HttpMessage message, long defaultValue) {
/* 174 */     String value = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/* 175 */     if (value != null) {
/* 176 */       return Long.parseLong(value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 181 */     long webSocketContentLength = getWebSocketContentLength(message);
/* 182 */     if (webSocketContentLength >= 0L) {
/* 183 */       return webSocketContentLength;
/*     */     }
/*     */ 
/*     */     
/* 187 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getContentLength(HttpMessage message, int defaultValue) {
/* 198 */     return (int)Math.min(2147483647L, getContentLength(message, defaultValue));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getWebSocketContentLength(HttpMessage message) {
/* 207 */     HttpHeaders h = message.headers();
/* 208 */     if (message instanceof HttpRequest) {
/* 209 */       HttpRequest req = (HttpRequest)message;
/* 210 */       if (HttpMethod.GET.equals(req.method()) && h
/* 211 */         .contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1) && h
/* 212 */         .contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2)) {
/* 213 */         return 8;
/*     */       }
/* 215 */     } else if (message instanceof HttpResponse) {
/* 216 */       HttpResponse res = (HttpResponse)message;
/* 217 */       if (res.status().code() == 101 && h
/* 218 */         .contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN) && h
/* 219 */         .contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION)) {
/* 220 */         return 16;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 225 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setContentLength(HttpMessage message, long length) {
/* 232 */     message.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, Long.valueOf(length));
/*     */   }
/*     */   
/*     */   public static boolean isContentLengthSet(HttpMessage m) {
/* 236 */     return m.headers().contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
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
/*     */   public static boolean is100ContinueExpected(HttpMessage message) {
/* 249 */     return (isExpectHeaderValid(message) && message
/*     */       
/* 251 */       .headers().contains((CharSequence)HttpHeaderNames.EXPECT, (CharSequence)HttpHeaderValues.CONTINUE, true));
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
/*     */   static boolean isUnsupportedExpectation(HttpMessage message) {
/* 263 */     if (!isExpectHeaderValid(message)) {
/* 264 */       return false;
/*     */     }
/*     */     
/* 267 */     String expectValue = message.headers().get((CharSequence)HttpHeaderNames.EXPECT);
/* 268 */     return (expectValue != null && !HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(expectValue));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isExpectHeaderValid(HttpMessage message) {
/* 277 */     return (message instanceof HttpRequest && message
/* 278 */       .protocolVersion().compareTo(HttpVersion.HTTP_1_1) >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void set100ContinueExpected(HttpMessage message, boolean expected) {
/* 289 */     if (expected) {
/* 290 */       message.headers().set((CharSequence)HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE);
/*     */     } else {
/* 292 */       message.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isTransferEncodingChunked(HttpMessage message) {
/* 303 */     return message.headers().contains((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (CharSequence)HttpHeaderValues.CHUNKED, true);
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
/*     */   public static void setTransferEncodingChunked(HttpMessage m, boolean chunked) {
/* 315 */     if (chunked) {
/* 316 */       m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
/* 317 */       m.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/*     */     } else {
/* 319 */       List<String> encodings = m.headers().getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/* 320 */       if (encodings.isEmpty()) {
/*     */         return;
/*     */       }
/* 323 */       List<CharSequence> values = new ArrayList<CharSequence>((Collection)encodings);
/* 324 */       Iterator<CharSequence> valuesIt = values.iterator();
/* 325 */       while (valuesIt.hasNext()) {
/* 326 */         CharSequence value = valuesIt.next();
/* 327 */         if (HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(value)) {
/* 328 */           valuesIt.remove();
/*     */         }
/*     */       } 
/* 331 */       if (values.isEmpty()) {
/* 332 */         m.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/*     */       } else {
/* 334 */         m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, values);
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
/*     */   public static Charset getCharset(HttpMessage message) {
/* 347 */     return getCharset(message, CharsetUtil.ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Charset getCharset(CharSequence contentTypeValue) {
/* 358 */     if (contentTypeValue != null) {
/* 359 */       return getCharset(contentTypeValue, CharsetUtil.ISO_8859_1);
/*     */     }
/* 361 */     return CharsetUtil.ISO_8859_1;
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
/*     */   public static Charset getCharset(HttpMessage message, Charset defaultCharset) {
/* 374 */     CharSequence contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
/* 375 */     if (contentTypeValue != null) {
/* 376 */       return getCharset(contentTypeValue, defaultCharset);
/*     */     }
/* 378 */     return defaultCharset;
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
/*     */   public static Charset getCharset(CharSequence contentTypeValue, Charset defaultCharset) {
/* 391 */     if (contentTypeValue != null) {
/* 392 */       CharSequence charsetCharSequence = getCharsetAsSequence(contentTypeValue);
/* 393 */       if (charsetCharSequence != null) {
/*     */         try {
/* 395 */           return Charset.forName(charsetCharSequence.toString());
/* 396 */         } catch (IllegalCharsetNameException illegalCharsetNameException) {
/*     */         
/* 398 */         } catch (UnsupportedCharsetException unsupportedCharsetException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 403 */     return defaultCharset;
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
/*     */   public static CharSequence getCharsetAsString(HttpMessage message) {
/* 419 */     return getCharsetAsSequence(message);
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
/*     */   public static CharSequence getCharsetAsSequence(HttpMessage message) {
/* 432 */     CharSequence contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
/* 433 */     if (contentTypeValue != null) {
/* 434 */       return getCharsetAsSequence(contentTypeValue);
/*     */     }
/* 436 */     return null;
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
/*     */   public static CharSequence getCharsetAsSequence(CharSequence contentTypeValue) {
/* 452 */     ObjectUtil.checkNotNull(contentTypeValue, "contentTypeValue");
/*     */     
/* 454 */     int indexOfCharset = AsciiString.indexOfIgnoreCaseAscii(contentTypeValue, (CharSequence)CHARSET_EQUALS, 0);
/* 455 */     if (indexOfCharset == -1) {
/* 456 */       return null;
/*     */     }
/*     */     
/* 459 */     int indexOfEncoding = indexOfCharset + CHARSET_EQUALS.length();
/* 460 */     if (indexOfEncoding < contentTypeValue.length()) {
/* 461 */       CharSequence charsetCandidate = contentTypeValue.subSequence(indexOfEncoding, contentTypeValue.length());
/* 462 */       int indexOfSemicolon = AsciiString.indexOfIgnoreCaseAscii(charsetCandidate, (CharSequence)SEMICOLON, 0);
/* 463 */       if (indexOfSemicolon == -1) {
/* 464 */         return charsetCandidate;
/*     */       }
/*     */       
/* 467 */       return charsetCandidate.subSequence(0, indexOfSemicolon);
/*     */     } 
/*     */     
/* 470 */     return null;
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
/*     */   public static CharSequence getMimeType(HttpMessage message) {
/* 485 */     CharSequence contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
/* 486 */     if (contentTypeValue != null) {
/* 487 */       return getMimeType(contentTypeValue);
/*     */     }
/* 489 */     return null;
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
/*     */   public static CharSequence getMimeType(CharSequence contentTypeValue) {
/* 506 */     ObjectUtil.checkNotNull(contentTypeValue, "contentTypeValue");
/*     */     
/* 508 */     int indexOfSemicolon = AsciiString.indexOfIgnoreCaseAscii(contentTypeValue, (CharSequence)SEMICOLON, 0);
/* 509 */     if (indexOfSemicolon != -1) {
/* 510 */       return contentTypeValue.subSequence(0, indexOfSemicolon);
/*     */     }
/* 512 */     return (contentTypeValue.length() > 0) ? contentTypeValue : null;
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
/*     */   public static String formatHostnameForHttp(InetSocketAddress addr) {
/* 524 */     String hostString = NetUtil.getHostname(addr);
/* 525 */     if (NetUtil.isValidIpV6Address(hostString)) {
/* 526 */       if (!addr.isUnresolved()) {
/* 527 */         hostString = NetUtil.toAddressString(addr.getAddress());
/*     */       }
/* 529 */       return '[' + hostString + ']';
/*     */     } 
/* 531 */     return hostString;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */