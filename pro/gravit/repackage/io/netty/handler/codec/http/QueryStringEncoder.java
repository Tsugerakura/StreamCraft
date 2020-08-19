/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QueryStringEncoder
/*     */ {
/*     */   private final Charset charset;
/*     */   private final StringBuilder uriBuilder;
/*     */   private boolean hasParams;
/*     */   private static final byte WRITE_UTF_UNKNOWN = 63;
/*  46 */   private static final char[] CHAR_MAP = "0123456789ABCDEF".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringEncoder(String uri) {
/*  53 */     this(uri, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QueryStringEncoder(String uri, Charset charset) {
/*  61 */     ObjectUtil.checkNotNull(charset, "charset");
/*  62 */     this.uriBuilder = new StringBuilder(uri);
/*  63 */     this.charset = CharsetUtil.UTF_8.equals(charset) ? null : charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addParam(String name, String value) {
/*  70 */     ObjectUtil.checkNotNull(name, "name");
/*  71 */     if (this.hasParams) {
/*  72 */       this.uriBuilder.append('&');
/*     */     } else {
/*  74 */       this.uriBuilder.append('?');
/*  75 */       this.hasParams = true;
/*     */     } 
/*     */     
/*  78 */     encodeComponent(name);
/*  79 */     if (value != null) {
/*  80 */       this.uriBuilder.append('=');
/*  81 */       encodeComponent(value);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void encodeComponent(CharSequence s) {
/*  86 */     if (this.charset == null) {
/*  87 */       encodeUtf8Component(s);
/*     */     } else {
/*  89 */       encodeNonUtf8Component(s);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI toUri() throws URISyntaxException {
/*  99 */     return new URI(toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return this.uriBuilder.toString();
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
/*     */   private void encodeNonUtf8Component(CharSequence s) {
/* 124 */     char[] buf = null;
/*     */     
/* 126 */     for (int i = 0, len = s.length(); i < len; ) {
/* 127 */       char c = s.charAt(i);
/* 128 */       if (dontNeedEncoding(c)) {
/* 129 */         this.uriBuilder.append(c);
/* 130 */         i++; continue;
/*     */       } 
/* 132 */       int index = 0;
/* 133 */       if (buf == null) {
/* 134 */         buf = new char[s.length() - i];
/*     */       }
/*     */       
/*     */       do {
/* 138 */         buf[index] = c;
/* 139 */         index++;
/* 140 */         ++i;
/* 141 */       } while (i < s.length() && !dontNeedEncoding(c = s.charAt(i)));
/*     */       
/* 143 */       byte[] bytes = (new String(buf, 0, index)).getBytes(this.charset);
/*     */       
/* 145 */       for (byte b : bytes) {
/* 146 */         appendEncoded(b);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void encodeUtf8Component(CharSequence s) {
/* 156 */     for (int i = 0, len = s.length(); i < len; i++) {
/* 157 */       char c = s.charAt(i);
/* 158 */       if (!dontNeedEncoding(c)) {
/* 159 */         encodeUtf8Component(s, i, len);
/*     */         return;
/*     */       } 
/*     */     } 
/* 163 */     this.uriBuilder.append(s);
/*     */   }
/*     */   
/*     */   private void encodeUtf8Component(CharSequence s, int encodingStart, int len) {
/* 167 */     if (encodingStart > 0)
/*     */     {
/* 169 */       this.uriBuilder.append(s, 0, encodingStart);
/*     */     }
/* 171 */     encodeUtf8ComponentSlow(s, encodingStart, len);
/*     */   }
/*     */   
/*     */   private void encodeUtf8ComponentSlow(CharSequence s, int start, int len) {
/* 175 */     for (int i = start; i < len; i++) {
/* 176 */       char c = s.charAt(i);
/* 177 */       if (c < '')
/* 178 */       { if (dontNeedEncoding(c)) {
/* 179 */           this.uriBuilder.append(c);
/*     */         } else {
/* 181 */           appendEncoded(c);
/*     */         }  }
/* 183 */       else if (c < 'ࠀ')
/* 184 */       { appendEncoded(0xC0 | c >> 6);
/* 185 */         appendEncoded(0x80 | c & 0x3F); }
/* 186 */       else if (StringUtil.isSurrogate(c))
/* 187 */       { if (!Character.isHighSurrogate(c)) {
/* 188 */           appendEncoded(63);
/*     */         }
/*     */         else {
/*     */           
/* 192 */           if (++i == s.length()) {
/* 193 */             appendEncoded(63);
/*     */             
/*     */             break;
/*     */           } 
/* 197 */           writeUtf8Surrogate(c, s.charAt(i));
/*     */         }  }
/* 199 */       else { appendEncoded(0xE0 | c >> 12);
/* 200 */         appendEncoded(0x80 | c >> 6 & 0x3F);
/* 201 */         appendEncoded(0x80 | c & 0x3F); }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeUtf8Surrogate(char c, char c2) {
/* 207 */     if (!Character.isLowSurrogate(c2)) {
/* 208 */       appendEncoded(63);
/* 209 */       appendEncoded(Character.isHighSurrogate(c2) ? 63 : c2);
/*     */       return;
/*     */     } 
/* 212 */     int codePoint = Character.toCodePoint(c, c2);
/*     */     
/* 214 */     appendEncoded(0xF0 | codePoint >> 18);
/* 215 */     appendEncoded(0x80 | codePoint >> 12 & 0x3F);
/* 216 */     appendEncoded(0x80 | codePoint >> 6 & 0x3F);
/* 217 */     appendEncoded(0x80 | codePoint & 0x3F);
/*     */   }
/*     */   
/*     */   private void appendEncoded(int b) {
/* 221 */     this.uriBuilder.append('%').append(forDigit(b >> 4)).append(forDigit(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static char forDigit(int digit) {
/* 232 */     return CHAR_MAP[digit & 0xF];
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
/*     */   private static boolean dontNeedEncoding(char ch) {
/* 247 */     return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '-' || ch == '_' || ch == '.' || ch == '*');
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\QueryStringEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */