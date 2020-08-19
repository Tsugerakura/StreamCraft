/*      */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*      */ 
/*      */ import java.text.ParseException;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.DateFormatter;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.HeadersUtils;
/*      */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*      */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class HttpHeaders
/*      */   implements Iterable<Map.Entry<String, String>>
/*      */ {
/*      */   @Deprecated
/*   54 */   public static final HttpHeaders EMPTY_HEADERS = EmptyHttpHeaders.instance();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static final class Names
/*      */   {
/*      */     public static final String ACCEPT = "Accept";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCEPT_CHARSET = "Accept-Charset";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCEPT_ENCODING = "Accept-Encoding";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCEPT_LANGUAGE = "Accept-Language";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCEPT_RANGES = "Accept-Ranges";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCEPT_PATCH = "Accept-Patch";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String AGE = "Age";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ALLOW = "Allow";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String AUTHORIZATION = "Authorization";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CACHE_CONTROL = "Cache-Control";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONNECTION = "Connection";
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_BASE = "Content-Base";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_ENCODING = "Content-Encoding";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_LANGUAGE = "Content-Language";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_LENGTH = "Content-Length";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_LOCATION = "Content-Location";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_MD5 = "Content-MD5";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_RANGE = "Content-Range";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTENT_TYPE = "Content-Type";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String COOKIE = "Cookie";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String DATE = "Date";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ETAG = "ETag";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String EXPECT = "Expect";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String EXPIRES = "Expires";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String FROM = "From";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String HOST = "Host";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IF_MATCH = "If-Match";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IF_NONE_MATCH = "If-None-Match";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IF_RANGE = "If-Range";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String LAST_MODIFIED = "Last-Modified";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String LOCATION = "Location";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MAX_FORWARDS = "Max-Forwards";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ORIGIN = "Origin";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PRAGMA = "Pragma";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String RANGE = "Range";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String REFERER = "Referer";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String RETRY_AFTER = "Retry-After";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_KEY1 = "Sec-WebSocket-Key1";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_KEY2 = "Sec-WebSocket-Key2";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SERVER = "Server";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SET_COOKIE = "Set-Cookie";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String SET_COOKIE2 = "Set-Cookie2";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String TE = "TE";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String TRAILER = "Trailer";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String TRANSFER_ENCODING = "Transfer-Encoding";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String UPGRADE = "Upgrade";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String USER_AGENT = "User-Agent";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String VARY = "Vary";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String VIA = "Via";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WARNING = "Warning";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WEBSOCKET_LOCATION = "WebSocket-Location";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WEBSOCKET_ORIGIN = "WebSocket-Origin";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WEBSOCKET_PROTOCOL = "WebSocket-Protocol";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static final class Values
/*      */   {
/*      */     public static final String APPLICATION_JSON = "application/json";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String BASE64 = "base64";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String BINARY = "binary";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String BOUNDARY = "boundary";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String BYTES = "bytes";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CHARSET = "charset";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CHUNKED = "chunked";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CLOSE = "close";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String COMPRESS = "compress";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String CONTINUE = "100-continue";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String DEFLATE = "deflate";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String GZIP = "gzip";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String GZIP_DEFLATE = "gzip,deflate";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String IDENTITY = "identity";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String KEEP_ALIVE = "keep-alive";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MAX_AGE = "max-age";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MAX_STALE = "max-stale";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MIN_FRESH = "min-fresh";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MULTIPART_FORM_DATA = "multipart/form-data";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String MUST_REVALIDATE = "must-revalidate";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String NO_CACHE = "no-cache";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String NO_STORE = "no-store";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String NO_TRANSFORM = "no-transform";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String NONE = "none";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String ONLY_IF_CACHED = "only-if-cached";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PRIVATE = "private";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PROXY_REVALIDATE = "proxy-revalidate";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String PUBLIC = "public";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String QUOTED_PRINTABLE = "quoted-printable";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String S_MAXAGE = "s-maxage";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String TRAILERS = "trailers";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String UPGRADE = "Upgrade";
/*      */ 
/*      */ 
/*      */     
/*      */     public static final String WEBSOCKET = "WebSocket";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isKeepAlive(HttpMessage message) {
/*  519 */     return HttpUtil.isKeepAlive(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
/*  545 */     HttpUtil.setKeepAlive(message, keepAlive);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHeader(HttpMessage message, String name) {
/*  553 */     return message.headers().get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHeader(HttpMessage message, CharSequence name) {
/*  567 */     return message.headers().get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHeader(HttpMessage message, String name, String defaultValue) {
/*  577 */     return message.headers().get(name, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHeader(HttpMessage message, CharSequence name, String defaultValue) {
/*  592 */     return message.headers().get(name, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHeader(HttpMessage message, String name, Object value) {
/*  602 */     message.headers().set(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHeader(HttpMessage message, CharSequence name, Object value) {
/*  617 */     message.headers().set(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHeader(HttpMessage message, String name, Iterable<?> values) {
/*  627 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHeader(HttpMessage message, CharSequence name, Iterable<?> values) {
/*  648 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addHeader(HttpMessage message, String name, Object value) {
/*  658 */     message.headers().add(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addHeader(HttpMessage message, CharSequence name, Object value) {
/*  672 */     message.headers().add(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void removeHeader(HttpMessage message, String name) {
/*  682 */     message.headers().remove(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void removeHeader(HttpMessage message, CharSequence name) {
/*  692 */     message.headers().remove(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void clearHeaders(HttpMessage message) {
/*  702 */     message.headers().clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int getIntHeader(HttpMessage message, String name) {
/*  712 */     return getIntHeader(message, name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int getIntHeader(HttpMessage message, CharSequence name) {
/*  728 */     String value = message.headers().get(name);
/*  729 */     if (value == null) {
/*  730 */       throw new NumberFormatException("header not found: " + name);
/*      */     }
/*  732 */     return Integer.parseInt(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int getIntHeader(HttpMessage message, String name, int defaultValue) {
/*  742 */     return message.headers().getInt(name, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int getIntHeader(HttpMessage message, CharSequence name, int defaultValue) {
/*  757 */     return message.headers().getInt(name, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setIntHeader(HttpMessage message, String name, int value) {
/*  767 */     message.headers().setInt(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setIntHeader(HttpMessage message, CharSequence name, int value) {
/*  778 */     message.headers().setInt(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setIntHeader(HttpMessage message, String name, Iterable<Integer> values) {
/*  788 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setIntHeader(HttpMessage message, CharSequence name, Iterable<Integer> values) {
/*  799 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addIntHeader(HttpMessage message, String name, int value) {
/*  809 */     message.headers().add(name, Integer.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addIntHeader(HttpMessage message, CharSequence name, int value) {
/*  819 */     message.headers().addInt(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDateHeader(HttpMessage message, String name) throws ParseException {
/*  829 */     return getDateHeader(message, name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDateHeader(HttpMessage message, CharSequence name) throws ParseException {
/*  845 */     String value = message.headers().get(name);
/*  846 */     if (value == null) {
/*  847 */       throw new ParseException("header not found: " + name, 0);
/*      */     }
/*  849 */     Date date = DateFormatter.parseHttpDate(value);
/*  850 */     if (date == null) {
/*  851 */       throw new ParseException("header can't be parsed into a Date: " + value, 0);
/*      */     }
/*  853 */     return date;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDateHeader(HttpMessage message, String name, Date defaultValue) {
/*  863 */     return getDateHeader(message, name, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDateHeader(HttpMessage message, CharSequence name, Date defaultValue) {
/*  878 */     String value = getHeader(message, name);
/*  879 */     Date date = DateFormatter.parseHttpDate(value);
/*  880 */     return (date != null) ? date : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setDateHeader(HttpMessage message, String name, Date value) {
/*  890 */     setDateHeader(message, name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setDateHeader(HttpMessage message, CharSequence name, Date value) {
/*  903 */     if (value != null) {
/*  904 */       message.headers().set(name, DateFormatter.format(value));
/*      */     } else {
/*  906 */       message.headers().set(name, (Iterable<?>)null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setDateHeader(HttpMessage message, String name, Iterable<Date> values) {
/*  917 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setDateHeader(HttpMessage message, CharSequence name, Iterable<Date> values) {
/*  930 */     message.headers().set(name, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addDateHeader(HttpMessage message, String name, Date value) {
/*  940 */     message.headers().add(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void addDateHeader(HttpMessage message, CharSequence name, Date value) {
/*  952 */     message.headers().add(name, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static long getContentLength(HttpMessage message) {
/*  971 */     return HttpUtil.getContentLength(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static long getContentLength(HttpMessage message, long defaultValue) {
/*  988 */     return HttpUtil.getContentLength(message, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setContentLength(HttpMessage message, long length) {
/*  996 */     HttpUtil.setContentLength(message, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHost(HttpMessage message) {
/* 1006 */     return message.headers().get((CharSequence)HttpHeaderNames.HOST);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getHost(HttpMessage message, String defaultValue) {
/* 1017 */     return message.headers().get((CharSequence)HttpHeaderNames.HOST, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHost(HttpMessage message, String value) {
/* 1027 */     message.headers().set((CharSequence)HttpHeaderNames.HOST, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setHost(HttpMessage message, CharSequence value) {
/* 1037 */     message.headers().set((CharSequence)HttpHeaderNames.HOST, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDate(HttpMessage message) throws ParseException {
/* 1050 */     return getDateHeader(message, (CharSequence)HttpHeaderNames.DATE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Date getDate(HttpMessage message, Date defaultValue) {
/* 1062 */     return getDateHeader(message, (CharSequence)HttpHeaderNames.DATE, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setDate(HttpMessage message, Date value) {
/* 1072 */     message.headers().set((CharSequence)HttpHeaderNames.DATE, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean is100ContinueExpected(HttpMessage message) {
/* 1083 */     return HttpUtil.is100ContinueExpected(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void set100ContinueExpected(HttpMessage message) {
/* 1095 */     HttpUtil.set100ContinueExpected(message, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void set100ContinueExpected(HttpMessage message, boolean set) {
/* 1109 */     HttpUtil.set100ContinueExpected(message, set);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isTransferEncodingChunked(HttpMessage message) {
/* 1122 */     return HttpUtil.isTransferEncodingChunked(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void removeTransferEncodingChunked(HttpMessage m) {
/* 1130 */     HttpUtil.setTransferEncodingChunked(m, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void setTransferEncodingChunked(HttpMessage m) {
/* 1138 */     HttpUtil.setTransferEncodingChunked(m, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isContentLengthSet(HttpMessage m) {
/* 1146 */     return HttpUtil.isContentLengthSet(m);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean equalsIgnoreCase(CharSequence name1, CharSequence name2) {
/* 1154 */     return AsciiString.contentEqualsIgnoreCase(name1, name2);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static void encodeAscii(CharSequence seq, ByteBuf buf) {
/* 1159 */     if (seq instanceof AsciiString) {
/* 1160 */       ByteBufUtil.copy((AsciiString)seq, 0, buf, seq.length());
/*      */     } else {
/* 1162 */       buf.writeCharSequence(seq, CharsetUtil.US_ASCII);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static CharSequence newEntity(String name) {
/* 1174 */     return (CharSequence)new AsciiString(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String get(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String get(CharSequence name) {
/* 1193 */     return get(name.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String get(CharSequence name, String defaultValue) {
/* 1204 */     String value = get(name);
/* 1205 */     if (value == null) {
/* 1206 */       return defaultValue;
/*      */     }
/* 1208 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Integer getInt(CharSequence paramCharSequence);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int getInt(CharSequence paramCharSequence, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Short getShort(CharSequence paramCharSequence);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract short getShort(CharSequence paramCharSequence, short paramShort);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Long getTimeMillis(CharSequence paramCharSequence);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long getTimeMillis(CharSequence paramCharSequence, long paramLong);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract List<String> getAll(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAll(CharSequence name) {
/* 1288 */     return getAll(name.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract List<Map.Entry<String, String>> entries();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean contains(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public abstract Iterator<Map.Entry<String, String>> iterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator<String> valueStringIterator(CharSequence name) {
/* 1323 */     return getAll(name).iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator<? extends CharSequence> valueCharSequenceIterator(CharSequence name) {
/* 1332 */     return (Iterator)valueStringIterator(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(CharSequence name) {
/* 1342 */     return contains(name.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isEmpty();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int size();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Set<String> names();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders add(String paramString, Object paramObject);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders add(CharSequence name, Object value) {
/* 1381 */     return add(name.toString(), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders add(String paramString, Iterable<?> paramIterable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders add(CharSequence name, Iterable<?> values) {
/* 1407 */     return add(name.toString(), values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders add(HttpHeaders headers) {
/* 1416 */     ObjectUtil.checkNotNull(headers, "headers");
/* 1417 */     for (Map.Entry<String, String> e : (Iterable<Map.Entry<String, String>>)headers) {
/* 1418 */       add(e.getKey(), e.getValue());
/*      */     }
/* 1420 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders addInt(CharSequence paramCharSequence, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders addShort(CharSequence paramCharSequence, short paramShort);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders set(String paramString, Object paramObject);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders set(CharSequence name, Object value) {
/* 1458 */     return set(name.toString(), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders set(String paramString, Iterable<?> paramIterable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders set(CharSequence name, Iterable<?> values) {
/* 1486 */     return set(name.toString(), values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders set(HttpHeaders headers) {
/* 1495 */     ObjectUtil.checkNotNull(headers, "headers");
/*      */     
/* 1497 */     clear();
/*      */     
/* 1499 */     if (headers.isEmpty()) {
/* 1500 */       return this;
/*      */     }
/*      */     
/* 1503 */     for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)headers) {
/* 1504 */       add(entry.getKey(), entry.getValue());
/*      */     }
/* 1506 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders setAll(HttpHeaders headers) {
/* 1516 */     ObjectUtil.checkNotNull(headers, "headers");
/*      */     
/* 1518 */     if (headers.isEmpty()) {
/* 1519 */       return this;
/*      */     }
/*      */     
/* 1522 */     for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)headers) {
/* 1523 */       set(entry.getKey(), entry.getValue());
/*      */     }
/* 1525 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders setInt(CharSequence paramCharSequence, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders setShort(CharSequence paramCharSequence, short paramShort);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders remove(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders remove(CharSequence name) {
/* 1556 */     return remove(name.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HttpHeaders clear();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(String name, String value, boolean ignoreCase) {
/* 1570 */     Iterator<String> valueIterator = valueStringIterator(name);
/* 1571 */     if (ignoreCase) {
/* 1572 */       while (valueIterator.hasNext()) {
/* 1573 */         if (((String)valueIterator.next()).equalsIgnoreCase(value)) {
/* 1574 */           return true;
/*      */         }
/*      */       } 
/*      */     } else {
/* 1578 */       while (valueIterator.hasNext()) {
/* 1579 */         if (((String)valueIterator.next()).equals(value)) {
/* 1580 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/* 1584 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
/* 1598 */     Iterator<? extends CharSequence> itr = valueCharSequenceIterator(name);
/* 1599 */     while (itr.hasNext()) {
/* 1600 */       if (containsCommaSeparatedTrimmed(itr.next(), value, ignoreCase)) {
/* 1601 */         return true;
/*      */       }
/*      */     } 
/* 1604 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean containsCommaSeparatedTrimmed(CharSequence rawNext, CharSequence expected, boolean ignoreCase) {
/* 1609 */     int begin = 0;
/*      */     
/* 1611 */     if (ignoreCase) {
/* 1612 */       int end; if ((end = AsciiString.indexOf(rawNext, ',', begin)) == -1) {
/* 1613 */         if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(rawNext), expected)) {
/* 1614 */           return true;
/*      */         }
/*      */       } else {
/*      */         do {
/* 1618 */           if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(rawNext.subSequence(begin, end)), expected)) {
/* 1619 */             return true;
/*      */           }
/* 1621 */           begin = end + 1;
/* 1622 */         } while ((end = AsciiString.indexOf(rawNext, ',', begin)) != -1);
/*      */         
/* 1624 */         if (begin < rawNext.length() && 
/* 1625 */           AsciiString.contentEqualsIgnoreCase(AsciiString.trim(rawNext.subSequence(begin, rawNext.length())), expected)) {
/* 1626 */           return true;
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       int end;
/* 1631 */       if ((end = AsciiString.indexOf(rawNext, ',', begin)) == -1) {
/* 1632 */         if (AsciiString.contentEquals(AsciiString.trim(rawNext), expected)) {
/* 1633 */           return true;
/*      */         }
/*      */       } else {
/*      */         do {
/* 1637 */           if (AsciiString.contentEquals(AsciiString.trim(rawNext.subSequence(begin, end)), expected)) {
/* 1638 */             return true;
/*      */           }
/* 1640 */           begin = end + 1;
/* 1641 */         } while ((end = AsciiString.indexOf(rawNext, ',', begin)) != -1);
/*      */         
/* 1643 */         if (begin < rawNext.length() && 
/* 1644 */           AsciiString.contentEquals(AsciiString.trim(rawNext.subSequence(begin, rawNext.length())), expected)) {
/* 1645 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1650 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getAsString(CharSequence name) {
/* 1659 */     return get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final List<String> getAllAsString(CharSequence name) {
/* 1668 */     return getAll(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Iterator<Map.Entry<String, String>> iteratorAsString() {
/* 1675 */     return iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
/* 1688 */     return contains(name.toString(), value.toString(), ignoreCase);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1693 */     return HeadersUtils.toString(getClass(), iteratorCharSequence(), size());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders copy() {
/* 1700 */     return (new DefaultHttpHeaders()).set(this);
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */