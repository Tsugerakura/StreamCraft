/*     */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RtspHeaderValues
/*     */ {
/*  29 */   public static final AsciiString APPEND = AsciiString.cached("append");
/*     */ 
/*     */ 
/*     */   
/*  33 */   public static final AsciiString AVP = AsciiString.cached("AVP");
/*     */ 
/*     */ 
/*     */   
/*  37 */   public static final AsciiString BYTES = HttpHeaderValues.BYTES;
/*     */ 
/*     */ 
/*     */   
/*  41 */   public static final AsciiString CHARSET = HttpHeaderValues.CHARSET;
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final AsciiString CLIENT_PORT = AsciiString.cached("client_port");
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final AsciiString CLOCK = AsciiString.cached("clock");
/*     */ 
/*     */ 
/*     */   
/*  53 */   public static final AsciiString CLOSE = HttpHeaderValues.CLOSE;
/*     */ 
/*     */ 
/*     */   
/*  57 */   public static final AsciiString COMPRESS = HttpHeaderValues.COMPRESS;
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final AsciiString CONTINUE = HttpHeaderValues.CONTINUE;
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final AsciiString DEFLATE = HttpHeaderValues.DEFLATE;
/*     */ 
/*     */ 
/*     */   
/*  69 */   public static final AsciiString DESTINATION = AsciiString.cached("destination");
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final AsciiString GZIP = HttpHeaderValues.GZIP;
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final AsciiString IDENTITY = HttpHeaderValues.IDENTITY;
/*     */ 
/*     */ 
/*     */   
/*  81 */   public static final AsciiString INTERLEAVED = AsciiString.cached("interleaved");
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final AsciiString KEEP_ALIVE = HttpHeaderValues.KEEP_ALIVE;
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final AsciiString LAYERS = AsciiString.cached("layers");
/*     */ 
/*     */ 
/*     */   
/*  93 */   public static final AsciiString MAX_AGE = HttpHeaderValues.MAX_AGE;
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final AsciiString MAX_STALE = HttpHeaderValues.MAX_STALE;
/*     */ 
/*     */ 
/*     */   
/* 101 */   public static final AsciiString MIN_FRESH = HttpHeaderValues.MIN_FRESH;
/*     */ 
/*     */ 
/*     */   
/* 105 */   public static final AsciiString MODE = AsciiString.cached("mode");
/*     */ 
/*     */ 
/*     */   
/* 109 */   public static final AsciiString MULTICAST = AsciiString.cached("multicast");
/*     */ 
/*     */ 
/*     */   
/* 113 */   public static final AsciiString MUST_REVALIDATE = HttpHeaderValues.MUST_REVALIDATE;
/*     */ 
/*     */ 
/*     */   
/* 117 */   public static final AsciiString NONE = HttpHeaderValues.NONE;
/*     */ 
/*     */ 
/*     */   
/* 121 */   public static final AsciiString NO_CACHE = HttpHeaderValues.NO_CACHE;
/*     */ 
/*     */ 
/*     */   
/* 125 */   public static final AsciiString NO_TRANSFORM = HttpHeaderValues.NO_TRANSFORM;
/*     */ 
/*     */ 
/*     */   
/* 129 */   public static final AsciiString ONLY_IF_CACHED = HttpHeaderValues.ONLY_IF_CACHED;
/*     */ 
/*     */ 
/*     */   
/* 133 */   public static final AsciiString PORT = AsciiString.cached("port");
/*     */ 
/*     */ 
/*     */   
/* 137 */   public static final AsciiString PRIVATE = HttpHeaderValues.PRIVATE;
/*     */ 
/*     */ 
/*     */   
/* 141 */   public static final AsciiString PROXY_REVALIDATE = HttpHeaderValues.PROXY_REVALIDATE;
/*     */ 
/*     */ 
/*     */   
/* 145 */   public static final AsciiString PUBLIC = HttpHeaderValues.PUBLIC;
/*     */ 
/*     */ 
/*     */   
/* 149 */   public static final AsciiString RTP = AsciiString.cached("RTP");
/*     */ 
/*     */ 
/*     */   
/* 153 */   public static final AsciiString RTPTIME = AsciiString.cached("rtptime");
/*     */ 
/*     */ 
/*     */   
/* 157 */   public static final AsciiString SEQ = AsciiString.cached("seq");
/*     */ 
/*     */ 
/*     */   
/* 161 */   public static final AsciiString SERVER_PORT = AsciiString.cached("server_port");
/*     */ 
/*     */ 
/*     */   
/* 165 */   public static final AsciiString SSRC = AsciiString.cached("ssrc");
/*     */ 
/*     */ 
/*     */   
/* 169 */   public static final AsciiString TCP = AsciiString.cached("TCP");
/*     */ 
/*     */ 
/*     */   
/* 173 */   public static final AsciiString TIME = AsciiString.cached("time");
/*     */ 
/*     */ 
/*     */   
/* 177 */   public static final AsciiString TIMEOUT = AsciiString.cached("timeout");
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final AsciiString TTL = AsciiString.cached("ttl");
/*     */ 
/*     */ 
/*     */   
/* 185 */   public static final AsciiString UDP = AsciiString.cached("UDP");
/*     */ 
/*     */ 
/*     */   
/* 189 */   public static final AsciiString UNICAST = AsciiString.cached("unicast");
/*     */ 
/*     */ 
/*     */   
/* 193 */   public static final AsciiString URL = AsciiString.cached("url");
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspHeaderValues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */