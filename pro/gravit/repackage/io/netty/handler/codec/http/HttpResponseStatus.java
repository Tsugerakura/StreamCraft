/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpResponseStatus
/*     */   implements Comparable<HttpResponseStatus>
/*     */ {
/*  39 */   public static final HttpResponseStatus CONTINUE = newStatus(100, "Continue");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final HttpResponseStatus SWITCHING_PROTOCOLS = newStatus(101, "Switching Protocols");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final HttpResponseStatus PROCESSING = newStatus(102, "Processing");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public static final HttpResponseStatus OK = newStatus(200, "OK");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final HttpResponseStatus CREATED = newStatus(201, "Created");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final HttpResponseStatus ACCEPTED = newStatus(202, "Accepted");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION = newStatus(203, "Non-Authoritative Information");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final HttpResponseStatus NO_CONTENT = newStatus(204, "No Content");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final HttpResponseStatus RESET_CONTENT = newStatus(205, "Reset Content");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final HttpResponseStatus PARTIAL_CONTENT = newStatus(206, "Partial Content");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  90 */   public static final HttpResponseStatus MULTI_STATUS = newStatus(207, "Multi-Status");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   public static final HttpResponseStatus MULTIPLE_CHOICES = newStatus(300, "Multiple Choices");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final HttpResponseStatus MOVED_PERMANENTLY = newStatus(301, "Moved Permanently");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   public static final HttpResponseStatus FOUND = newStatus(302, "Found");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   public static final HttpResponseStatus SEE_OTHER = newStatus(303, "See Other");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final HttpResponseStatus NOT_MODIFIED = newStatus(304, "Not Modified");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 120 */   public static final HttpResponseStatus USE_PROXY = newStatus(305, "Use Proxy");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 125 */   public static final HttpResponseStatus TEMPORARY_REDIRECT = newStatus(307, "Temporary Redirect");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 130 */   public static final HttpResponseStatus PERMANENT_REDIRECT = newStatus(308, "Permanent Redirect");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   public static final HttpResponseStatus BAD_REQUEST = newStatus(400, "Bad Request");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 140 */   public static final HttpResponseStatus UNAUTHORIZED = newStatus(401, "Unauthorized");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 145 */   public static final HttpResponseStatus PAYMENT_REQUIRED = newStatus(402, "Payment Required");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 150 */   public static final HttpResponseStatus FORBIDDEN = newStatus(403, "Forbidden");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   public static final HttpResponseStatus NOT_FOUND = newStatus(404, "Not Found");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 160 */   public static final HttpResponseStatus METHOD_NOT_ALLOWED = newStatus(405, "Method Not Allowed");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 165 */   public static final HttpResponseStatus NOT_ACCEPTABLE = newStatus(406, "Not Acceptable");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED = newStatus(407, "Proxy Authentication Required");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 176 */   public static final HttpResponseStatus REQUEST_TIMEOUT = newStatus(408, "Request Timeout");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final HttpResponseStatus CONFLICT = newStatus(409, "Conflict");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 186 */   public static final HttpResponseStatus GONE = newStatus(410, "Gone");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 191 */   public static final HttpResponseStatus LENGTH_REQUIRED = newStatus(411, "Length Required");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 196 */   public static final HttpResponseStatus PRECONDITION_FAILED = newStatus(412, "Precondition Failed");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 202 */   public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE = newStatus(413, "Request Entity Too Large");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 207 */   public static final HttpResponseStatus REQUEST_URI_TOO_LONG = newStatus(414, "Request-URI Too Long");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 212 */   public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE = newStatus(415, "Unsupported Media Type");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 218 */   public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE = newStatus(416, "Requested Range Not Satisfiable");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 223 */   public static final HttpResponseStatus EXPECTATION_FAILED = newStatus(417, "Expectation Failed");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 230 */   public static final HttpResponseStatus MISDIRECTED_REQUEST = newStatus(421, "Misdirected Request");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 235 */   public static final HttpResponseStatus UNPROCESSABLE_ENTITY = newStatus(422, "Unprocessable Entity");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 240 */   public static final HttpResponseStatus LOCKED = newStatus(423, "Locked");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 245 */   public static final HttpResponseStatus FAILED_DEPENDENCY = newStatus(424, "Failed Dependency");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 250 */   public static final HttpResponseStatus UNORDERED_COLLECTION = newStatus(425, "Unordered Collection");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 255 */   public static final HttpResponseStatus UPGRADE_REQUIRED = newStatus(426, "Upgrade Required");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 260 */   public static final HttpResponseStatus PRECONDITION_REQUIRED = newStatus(428, "Precondition Required");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 265 */   public static final HttpResponseStatus TOO_MANY_REQUESTS = newStatus(429, "Too Many Requests");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 271 */   public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE = newStatus(431, "Request Header Fields Too Large");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 276 */   public static final HttpResponseStatus INTERNAL_SERVER_ERROR = newStatus(500, "Internal Server Error");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 281 */   public static final HttpResponseStatus NOT_IMPLEMENTED = newStatus(501, "Not Implemented");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 286 */   public static final HttpResponseStatus BAD_GATEWAY = newStatus(502, "Bad Gateway");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 291 */   public static final HttpResponseStatus SERVICE_UNAVAILABLE = newStatus(503, "Service Unavailable");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 296 */   public static final HttpResponseStatus GATEWAY_TIMEOUT = newStatus(504, "Gateway Timeout");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 302 */   public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED = newStatus(505, "HTTP Version Not Supported");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 307 */   public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES = newStatus(506, "Variant Also Negotiates");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 312 */   public static final HttpResponseStatus INSUFFICIENT_STORAGE = newStatus(507, "Insufficient Storage");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 317 */   public static final HttpResponseStatus NOT_EXTENDED = newStatus(510, "Not Extended");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 323 */   public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED = newStatus(511, "Network Authentication Required");
/*     */   
/*     */   private static HttpResponseStatus newStatus(int statusCode, String reasonPhrase) {
/* 326 */     return new HttpResponseStatus(statusCode, reasonPhrase, true);
/*     */   }
/*     */   private final int code;
/*     */   private final AsciiString codeAsText;
/*     */   private HttpStatusClass codeClass;
/*     */   private final String reasonPhrase;
/*     */   private final byte[] bytes;
/*     */   
/*     */   public static HttpResponseStatus valueOf(int code) {
/* 335 */     HttpResponseStatus status = valueOf0(code);
/* 336 */     return (status != null) ? status : new HttpResponseStatus(code);
/*     */   }
/*     */   
/*     */   private static HttpResponseStatus valueOf0(int code) {
/* 340 */     switch (code) {
/*     */       case 100:
/* 342 */         return CONTINUE;
/*     */       case 101:
/* 344 */         return SWITCHING_PROTOCOLS;
/*     */       case 102:
/* 346 */         return PROCESSING;
/*     */       case 200:
/* 348 */         return OK;
/*     */       case 201:
/* 350 */         return CREATED;
/*     */       case 202:
/* 352 */         return ACCEPTED;
/*     */       case 203:
/* 354 */         return NON_AUTHORITATIVE_INFORMATION;
/*     */       case 204:
/* 356 */         return NO_CONTENT;
/*     */       case 205:
/* 358 */         return RESET_CONTENT;
/*     */       case 206:
/* 360 */         return PARTIAL_CONTENT;
/*     */       case 207:
/* 362 */         return MULTI_STATUS;
/*     */       case 300:
/* 364 */         return MULTIPLE_CHOICES;
/*     */       case 301:
/* 366 */         return MOVED_PERMANENTLY;
/*     */       case 302:
/* 368 */         return FOUND;
/*     */       case 303:
/* 370 */         return SEE_OTHER;
/*     */       case 304:
/* 372 */         return NOT_MODIFIED;
/*     */       case 305:
/* 374 */         return USE_PROXY;
/*     */       case 307:
/* 376 */         return TEMPORARY_REDIRECT;
/*     */       case 308:
/* 378 */         return PERMANENT_REDIRECT;
/*     */       case 400:
/* 380 */         return BAD_REQUEST;
/*     */       case 401:
/* 382 */         return UNAUTHORIZED;
/*     */       case 402:
/* 384 */         return PAYMENT_REQUIRED;
/*     */       case 403:
/* 386 */         return FORBIDDEN;
/*     */       case 404:
/* 388 */         return NOT_FOUND;
/*     */       case 405:
/* 390 */         return METHOD_NOT_ALLOWED;
/*     */       case 406:
/* 392 */         return NOT_ACCEPTABLE;
/*     */       case 407:
/* 394 */         return PROXY_AUTHENTICATION_REQUIRED;
/*     */       case 408:
/* 396 */         return REQUEST_TIMEOUT;
/*     */       case 409:
/* 398 */         return CONFLICT;
/*     */       case 410:
/* 400 */         return GONE;
/*     */       case 411:
/* 402 */         return LENGTH_REQUIRED;
/*     */       case 412:
/* 404 */         return PRECONDITION_FAILED;
/*     */       case 413:
/* 406 */         return REQUEST_ENTITY_TOO_LARGE;
/*     */       case 414:
/* 408 */         return REQUEST_URI_TOO_LONG;
/*     */       case 415:
/* 410 */         return UNSUPPORTED_MEDIA_TYPE;
/*     */       case 416:
/* 412 */         return REQUESTED_RANGE_NOT_SATISFIABLE;
/*     */       case 417:
/* 414 */         return EXPECTATION_FAILED;
/*     */       case 421:
/* 416 */         return MISDIRECTED_REQUEST;
/*     */       case 422:
/* 418 */         return UNPROCESSABLE_ENTITY;
/*     */       case 423:
/* 420 */         return LOCKED;
/*     */       case 424:
/* 422 */         return FAILED_DEPENDENCY;
/*     */       case 425:
/* 424 */         return UNORDERED_COLLECTION;
/*     */       case 426:
/* 426 */         return UPGRADE_REQUIRED;
/*     */       case 428:
/* 428 */         return PRECONDITION_REQUIRED;
/*     */       case 429:
/* 430 */         return TOO_MANY_REQUESTS;
/*     */       case 431:
/* 432 */         return REQUEST_HEADER_FIELDS_TOO_LARGE;
/*     */       case 500:
/* 434 */         return INTERNAL_SERVER_ERROR;
/*     */       case 501:
/* 436 */         return NOT_IMPLEMENTED;
/*     */       case 502:
/* 438 */         return BAD_GATEWAY;
/*     */       case 503:
/* 440 */         return SERVICE_UNAVAILABLE;
/*     */       case 504:
/* 442 */         return GATEWAY_TIMEOUT;
/*     */       case 505:
/* 444 */         return HTTP_VERSION_NOT_SUPPORTED;
/*     */       case 506:
/* 446 */         return VARIANT_ALSO_NEGOTIATES;
/*     */       case 507:
/* 448 */         return INSUFFICIENT_STORAGE;
/*     */       case 510:
/* 450 */         return NOT_EXTENDED;
/*     */       case 511:
/* 452 */         return NETWORK_AUTHENTICATION_REQUIRED;
/*     */     } 
/* 454 */     return null;
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
/*     */   public static HttpResponseStatus valueOf(int code, String reasonPhrase) {
/* 466 */     HttpResponseStatus responseStatus = valueOf0(code);
/* 467 */     return (responseStatus != null && responseStatus.reasonPhrase().contentEquals(reasonPhrase)) ? responseStatus : new HttpResponseStatus(code, reasonPhrase);
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
/*     */   public static HttpResponseStatus parseLine(CharSequence line) {
/* 481 */     return (line instanceof AsciiString) ? parseLine((AsciiString)line) : parseLine(line.toString());
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
/*     */   public static HttpResponseStatus parseLine(String line) {
/*     */     try {
/* 495 */       int space = line.indexOf(' ');
/* 496 */       return (space == -1) ? valueOf(Integer.parseInt(line)) : 
/* 497 */         valueOf(Integer.parseInt(line.substring(0, space)), line.substring(space + 1));
/* 498 */     } catch (Exception e) {
/* 499 */       throw new IllegalArgumentException("malformed status line: " + line, e);
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
/*     */   public static HttpResponseStatus parseLine(AsciiString line) {
/*     */     try {
/* 514 */       int space = line.forEachByte(ByteProcessor.FIND_ASCII_SPACE);
/* 515 */       return (space == -1) ? valueOf(line.parseInt()) : valueOf(line.parseInt(0, space), line.toString(space + 1));
/* 516 */     } catch (Exception e) {
/* 517 */       throw new IllegalArgumentException("malformed status line: " + line, e);
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
/*     */   private HttpResponseStatus(int code) {
/* 532 */     this(code, HttpStatusClass.valueOf(code).defaultReasonPhrase() + " (" + code + ')', false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponseStatus(int code, String reasonPhrase) {
/* 539 */     this(code, reasonPhrase, false);
/*     */   }
/*     */   
/*     */   private HttpResponseStatus(int code, String reasonPhrase, boolean bytes) {
/* 543 */     ObjectUtil.checkPositiveOrZero(code, "code");
/* 544 */     ObjectUtil.checkNotNull(reasonPhrase, "reasonPhrase");
/*     */     
/* 546 */     for (int i = 0; i < reasonPhrase.length(); i++) {
/* 547 */       char c = reasonPhrase.charAt(i);
/*     */       
/* 549 */       switch (c) { case '\n':
/*     */         case '\r':
/* 551 */           throw new IllegalArgumentException("reasonPhrase contains one of the following prohibited characters: \\r\\n: " + reasonPhrase); }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 557 */     this.code = code;
/* 558 */     String codeString = Integer.toString(code);
/* 559 */     this.codeAsText = new AsciiString(codeString);
/* 560 */     this.reasonPhrase = reasonPhrase;
/* 561 */     if (bytes) {
/* 562 */       this.bytes = (codeString + ' ' + reasonPhrase).getBytes(CharsetUtil.US_ASCII);
/*     */     } else {
/* 564 */       this.bytes = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int code() {
/* 572 */     return this.code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsciiString codeAsText() {
/* 579 */     return this.codeAsText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String reasonPhrase() {
/* 586 */     return this.reasonPhrase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatusClass codeClass() {
/* 593 */     HttpStatusClass type = this.codeClass;
/* 594 */     if (type == null) {
/* 595 */       this.codeClass = type = HttpStatusClass.valueOf(this.code);
/*     */     }
/* 597 */     return type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 602 */     return code();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 611 */     if (!(o instanceof HttpResponseStatus)) {
/* 612 */       return false;
/*     */     }
/*     */     
/* 615 */     return (code() == ((HttpResponseStatus)o).code());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(HttpResponseStatus o) {
/* 624 */     return code() - o.code();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 629 */     return (new StringBuilder(this.reasonPhrase.length() + 4))
/* 630 */       .append((CharSequence)this.codeAsText)
/* 631 */       .append(' ')
/* 632 */       .append(this.reasonPhrase)
/* 633 */       .toString();
/*     */   }
/*     */   
/*     */   void encode(ByteBuf buf) {
/* 637 */     if (this.bytes == null) {
/* 638 */       ByteBufUtil.copy(this.codeAsText, buf);
/* 639 */       buf.writeByte(32);
/* 640 */       buf.writeCharSequence(this.reasonPhrase, CharsetUtil.US_ASCII);
/*     */     } else {
/* 642 */       buf.writeBytes(this.bytes);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpResponseStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */