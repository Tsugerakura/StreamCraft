/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
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
/*     */ public class HttpVersion
/*     */   implements Comparable<HttpVersion>
/*     */ {
/*  35 */   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");
/*     */ 
/*     */   
/*     */   private static final String HTTP_1_0_STRING = "HTTP/1.0";
/*     */ 
/*     */   
/*     */   private static final String HTTP_1_1_STRING = "HTTP/1.1";
/*     */   
/*  43 */   public static final HttpVersion HTTP_1_0 = new HttpVersion("HTTP", 1, 0, false, true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final HttpVersion HTTP_1_1 = new HttpVersion("HTTP", 1, 1, true, true);
/*     */   
/*     */   private final String protocolName;
/*     */   
/*     */   private final int majorVersion;
/*     */   private final int minorVersion;
/*     */   private final String text;
/*     */   private final boolean keepAliveDefault;
/*     */   private final byte[] bytes;
/*     */   
/*     */   public static HttpVersion valueOf(String text) {
/*  59 */     ObjectUtil.checkNotNull(text, "text");
/*     */     
/*  61 */     text = text.trim();
/*     */     
/*  63 */     if (text.isEmpty()) {
/*  64 */       throw new IllegalArgumentException("text is empty (possibly HTTP/0.9)");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  75 */     HttpVersion version = version0(text);
/*  76 */     if (version == null) {
/*  77 */       version = new HttpVersion(text, true);
/*     */     }
/*  79 */     return version;
/*     */   }
/*     */   
/*     */   private static HttpVersion version0(String text) {
/*  83 */     if ("HTTP/1.1".equals(text)) {
/*  84 */       return HTTP_1_1;
/*     */     }
/*  86 */     if ("HTTP/1.0".equals(text)) {
/*  87 */       return HTTP_1_0;
/*     */     }
/*  89 */     return null;
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
/*     */   public HttpVersion(String text, boolean keepAliveDefault) {
/* 111 */     ObjectUtil.checkNotNull(text, "text");
/*     */     
/* 113 */     text = text.trim().toUpperCase();
/* 114 */     if (text.isEmpty()) {
/* 115 */       throw new IllegalArgumentException("empty text");
/*     */     }
/*     */     
/* 118 */     Matcher m = VERSION_PATTERN.matcher(text);
/* 119 */     if (!m.matches()) {
/* 120 */       throw new IllegalArgumentException("invalid version format: " + text);
/*     */     }
/*     */     
/* 123 */     this.protocolName = m.group(1);
/* 124 */     this.majorVersion = Integer.parseInt(m.group(2));
/* 125 */     this.minorVersion = Integer.parseInt(m.group(3));
/* 126 */     this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
/* 127 */     this.keepAliveDefault = keepAliveDefault;
/* 128 */     this.bytes = null;
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
/*     */   public HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
/* 145 */     this(protocolName, majorVersion, minorVersion, keepAliveDefault, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault, boolean bytes) {
/* 151 */     ObjectUtil.checkNotNull(protocolName, "protocolName");
/*     */     
/* 153 */     protocolName = protocolName.trim().toUpperCase();
/* 154 */     if (protocolName.isEmpty()) {
/* 155 */       throw new IllegalArgumentException("empty protocolName");
/*     */     }
/*     */     
/* 158 */     for (int i = 0; i < protocolName.length(); i++) {
/* 159 */       if (Character.isISOControl(protocolName.charAt(i)) || 
/* 160 */         Character.isWhitespace(protocolName.charAt(i))) {
/* 161 */         throw new IllegalArgumentException("invalid character in protocolName");
/*     */       }
/*     */     } 
/*     */     
/* 165 */     ObjectUtil.checkPositiveOrZero(majorVersion, "majorVersion");
/* 166 */     ObjectUtil.checkPositiveOrZero(minorVersion, "minorVersion");
/*     */     
/* 168 */     this.protocolName = protocolName;
/* 169 */     this.majorVersion = majorVersion;
/* 170 */     this.minorVersion = minorVersion;
/* 171 */     this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
/* 172 */     this.keepAliveDefault = keepAliveDefault;
/*     */     
/* 174 */     if (bytes) {
/* 175 */       this.bytes = this.text.getBytes(CharsetUtil.US_ASCII);
/*     */     } else {
/* 177 */       this.bytes = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String protocolName() {
/* 185 */     return this.protocolName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int majorVersion() {
/* 192 */     return this.majorVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int minorVersion() {
/* 199 */     return this.minorVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String text() {
/* 206 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isKeepAliveDefault() {
/* 214 */     return this.keepAliveDefault;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 222 */     return text();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 227 */     return (protocolName().hashCode() * 31 + majorVersion()) * 31 + 
/* 228 */       minorVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 233 */     if (!(o instanceof HttpVersion)) {
/* 234 */       return false;
/*     */     }
/*     */     
/* 237 */     HttpVersion that = (HttpVersion)o;
/* 238 */     return (minorVersion() == that.minorVersion() && 
/* 239 */       majorVersion() == that.majorVersion() && 
/* 240 */       protocolName().equals(that.protocolName()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(HttpVersion o) {
/* 245 */     int v = protocolName().compareTo(o.protocolName());
/* 246 */     if (v != 0) {
/* 247 */       return v;
/*     */     }
/*     */     
/* 250 */     v = majorVersion() - o.majorVersion();
/* 251 */     if (v != 0) {
/* 252 */       return v;
/*     */     }
/*     */     
/* 255 */     return minorVersion() - o.minorVersion();
/*     */   }
/*     */   
/*     */   void encode(ByteBuf buf) {
/* 259 */     if (this.bytes == null) {
/* 260 */       buf.writeCharSequence(this.text, CharsetUtil.US_ASCII);
/*     */     } else {
/* 262 */       buf.writeBytes(this.bytes);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */