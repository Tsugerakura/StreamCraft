/*     */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RtspHeaderNames
/*     */ {
/*  32 */   public static final AsciiString ACCEPT = HttpHeaderNames.ACCEPT;
/*     */ 
/*     */ 
/*     */   
/*  36 */   public static final AsciiString ACCEPT_ENCODING = HttpHeaderNames.ACCEPT_ENCODING;
/*     */ 
/*     */ 
/*     */   
/*  40 */   public static final AsciiString ACCEPT_LANGUAGE = HttpHeaderNames.ACCEPT_LANGUAGE;
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final AsciiString ALLOW = AsciiString.cached("allow");
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final AsciiString AUTHORIZATION = HttpHeaderNames.AUTHORIZATION;
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final AsciiString BANDWIDTH = AsciiString.cached("bandwidth");
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final AsciiString BLOCKSIZE = AsciiString.cached("blocksize");
/*     */ 
/*     */ 
/*     */   
/*  60 */   public static final AsciiString CACHE_CONTROL = HttpHeaderNames.CACHE_CONTROL;
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final AsciiString CONFERENCE = AsciiString.cached("conference");
/*     */ 
/*     */ 
/*     */   
/*  68 */   public static final AsciiString CONNECTION = HttpHeaderNames.CONNECTION;
/*     */ 
/*     */ 
/*     */   
/*  72 */   public static final AsciiString CONTENT_BASE = HttpHeaderNames.CONTENT_BASE;
/*     */ 
/*     */ 
/*     */   
/*  76 */   public static final AsciiString CONTENT_ENCODING = HttpHeaderNames.CONTENT_ENCODING;
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final AsciiString CONTENT_LANGUAGE = HttpHeaderNames.CONTENT_LANGUAGE;
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static final AsciiString CONTENT_LENGTH = HttpHeaderNames.CONTENT_LENGTH;
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static final AsciiString CONTENT_LOCATION = HttpHeaderNames.CONTENT_LOCATION;
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final AsciiString CONTENT_TYPE = HttpHeaderNames.CONTENT_TYPE;
/*     */ 
/*     */ 
/*     */   
/*  96 */   public static final AsciiString CSEQ = AsciiString.cached("cseq");
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final AsciiString DATE = HttpHeaderNames.DATE;
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final AsciiString EXPIRES = HttpHeaderNames.EXPIRES;
/*     */ 
/*     */ 
/*     */   
/* 108 */   public static final AsciiString FROM = HttpHeaderNames.FROM;
/*     */ 
/*     */ 
/*     */   
/* 112 */   public static final AsciiString HOST = HttpHeaderNames.HOST;
/*     */ 
/*     */ 
/*     */   
/* 116 */   public static final AsciiString IF_MATCH = HttpHeaderNames.IF_MATCH;
/*     */ 
/*     */ 
/*     */   
/* 120 */   public static final AsciiString IF_MODIFIED_SINCE = HttpHeaderNames.IF_MODIFIED_SINCE;
/*     */ 
/*     */ 
/*     */   
/* 124 */   public static final AsciiString KEYMGMT = AsciiString.cached("keymgmt");
/*     */ 
/*     */ 
/*     */   
/* 128 */   public static final AsciiString LAST_MODIFIED = HttpHeaderNames.LAST_MODIFIED;
/*     */ 
/*     */ 
/*     */   
/* 132 */   public static final AsciiString PROXY_AUTHENTICATE = HttpHeaderNames.PROXY_AUTHENTICATE;
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static final AsciiString PROXY_REQUIRE = AsciiString.cached("proxy-require");
/*     */ 
/*     */ 
/*     */   
/* 140 */   public static final AsciiString PUBLIC = AsciiString.cached("public");
/*     */ 
/*     */ 
/*     */   
/* 144 */   public static final AsciiString RANGE = HttpHeaderNames.RANGE;
/*     */ 
/*     */ 
/*     */   
/* 148 */   public static final AsciiString REFERER = HttpHeaderNames.REFERER;
/*     */ 
/*     */ 
/*     */   
/* 152 */   public static final AsciiString REQUIRE = AsciiString.cached("require");
/*     */ 
/*     */ 
/*     */   
/* 156 */   public static final AsciiString RETRT_AFTER = HttpHeaderNames.RETRY_AFTER;
/*     */ 
/*     */ 
/*     */   
/* 160 */   public static final AsciiString RTP_INFO = AsciiString.cached("rtp-info");
/*     */ 
/*     */ 
/*     */   
/* 164 */   public static final AsciiString SCALE = AsciiString.cached("scale");
/*     */ 
/*     */ 
/*     */   
/* 168 */   public static final AsciiString SESSION = AsciiString.cached("session");
/*     */ 
/*     */ 
/*     */   
/* 172 */   public static final AsciiString SERVER = HttpHeaderNames.SERVER;
/*     */ 
/*     */ 
/*     */   
/* 176 */   public static final AsciiString SPEED = AsciiString.cached("speed");
/*     */ 
/*     */ 
/*     */   
/* 180 */   public static final AsciiString TIMESTAMP = AsciiString.cached("timestamp");
/*     */ 
/*     */ 
/*     */   
/* 184 */   public static final AsciiString TRANSPORT = AsciiString.cached("transport");
/*     */ 
/*     */ 
/*     */   
/* 188 */   public static final AsciiString UNSUPPORTED = AsciiString.cached("unsupported");
/*     */ 
/*     */ 
/*     */   
/* 192 */   public static final AsciiString USER_AGENT = HttpHeaderNames.USER_AGENT;
/*     */ 
/*     */ 
/*     */   
/* 196 */   public static final AsciiString VARY = HttpHeaderNames.VARY;
/*     */ 
/*     */ 
/*     */   
/* 200 */   public static final AsciiString VIA = HttpHeaderNames.VIA;
/*     */ 
/*     */ 
/*     */   
/* 204 */   public static final AsciiString WWW_AUTHENTICATE = HttpHeaderNames.WWW_AUTHENTICATE;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspHeaderNames.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */