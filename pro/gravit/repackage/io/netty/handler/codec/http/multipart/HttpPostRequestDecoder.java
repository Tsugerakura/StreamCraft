/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContent;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
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
/*     */ 
/*     */ 
/*     */ public class HttpPostRequestDecoder
/*     */   implements InterfaceHttpPostRequestDecoder
/*     */ {
/*     */   static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
/*     */   private final InterfaceHttpPostRequestDecoder decoder;
/*     */   
/*     */   public HttpPostRequestDecoder(HttpRequest request) {
/*  53 */     this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
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
/*     */   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) {
/*  69 */     this(factory, request, HttpConstants.DEFAULT_CHARSET);
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
/*     */   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
/*  87 */     ObjectUtil.checkNotNull(factory, "factory");
/*  88 */     ObjectUtil.checkNotNull(request, "request");
/*  89 */     ObjectUtil.checkNotNull(charset, "charset");
/*     */ 
/*     */     
/*  92 */     if (isMultipart(request)) {
/*  93 */       this.decoder = new HttpPostMultipartRequestDecoder(factory, request, charset);
/*     */     } else {
/*  95 */       this.decoder = new HttpPostStandardRequestDecoder(factory, request, charset);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected enum MultiPartStatus
/*     */   {
/* 130 */     NOTSTARTED, PREAMBLE, HEADERDELIMITER, DISPOSITION, FIELD, FILEUPLOAD, MIXEDPREAMBLE, MIXEDDELIMITER,
/* 131 */     MIXEDDISPOSITION, MIXEDFILEUPLOAD, MIXEDCLOSEDELIMITER, CLOSEDELIMITER, PREEPILOGUE, EPILOGUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMultipart(HttpRequest request) {
/* 139 */     String mimeType = request.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
/* 140 */     if (mimeType != null && mimeType.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())) {
/* 141 */       return (getMultipartDataBoundary(mimeType) != null);
/*     */     }
/* 143 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String[] getMultipartDataBoundary(String contentType) {
/* 153 */     String[] headerContentType = splitHeaderContentType(contentType);
/* 154 */     String multiPartHeader = HttpHeaderValues.MULTIPART_FORM_DATA.toString();
/* 155 */     if (headerContentType[0].regionMatches(true, 0, multiPartHeader, 0, multiPartHeader.length())) {
/*     */       int mrank, crank;
/*     */       
/* 158 */       String boundaryHeader = HttpHeaderValues.BOUNDARY.toString();
/* 159 */       if (headerContentType[1].regionMatches(true, 0, boundaryHeader, 0, boundaryHeader.length())) {
/* 160 */         mrank = 1;
/* 161 */         crank = 2;
/* 162 */       } else if (headerContentType[2].regionMatches(true, 0, boundaryHeader, 0, boundaryHeader.length())) {
/* 163 */         mrank = 2;
/* 164 */         crank = 1;
/*     */       } else {
/* 166 */         return null;
/*     */       } 
/* 168 */       String boundary = StringUtil.substringAfter(headerContentType[mrank], '=');
/* 169 */       if (boundary == null) {
/* 170 */         throw new ErrorDataDecoderException("Needs a boundary value");
/*     */       }
/* 172 */       if (boundary.charAt(0) == '"') {
/* 173 */         String bound = boundary.trim();
/* 174 */         int index = bound.length() - 1;
/* 175 */         if (bound.charAt(index) == '"') {
/* 176 */           boundary = bound.substring(1, index);
/*     */         }
/*     */       } 
/* 179 */       String charsetHeader = HttpHeaderValues.CHARSET.toString();
/* 180 */       if (headerContentType[crank].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
/* 181 */         String charset = StringUtil.substringAfter(headerContentType[crank], '=');
/* 182 */         if (charset != null) {
/* 183 */           return new String[] { "--" + boundary, charset };
/*     */         }
/*     */       } 
/* 186 */       return new String[] { "--" + boundary };
/*     */     } 
/* 188 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMultipart() {
/* 193 */     return this.decoder.isMultipart();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDiscardThreshold(int discardThreshold) {
/* 198 */     this.decoder.setDiscardThreshold(discardThreshold);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDiscardThreshold() {
/* 203 */     return this.decoder.getDiscardThreshold();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<InterfaceHttpData> getBodyHttpDatas() {
/* 208 */     return this.decoder.getBodyHttpDatas();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<InterfaceHttpData> getBodyHttpDatas(String name) {
/* 213 */     return this.decoder.getBodyHttpDatas(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData getBodyHttpData(String name) {
/* 218 */     return this.decoder.getBodyHttpData(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpPostRequestDecoder offer(HttpContent content) {
/* 223 */     return this.decoder.offer(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 228 */     return this.decoder.hasNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData next() {
/* 233 */     return this.decoder.next();
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData currentPartialHttpData() {
/* 238 */     return this.decoder.currentPartialHttpData();
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 243 */     this.decoder.destroy();
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanFiles() {
/* 248 */     this.decoder.cleanFiles();
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeHttpDataFromClean(InterfaceHttpData data) {
/* 253 */     this.decoder.removeHttpDataFromClean(data);
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
/*     */   private static String[] splitHeaderContentType(String sb) {
/* 268 */     int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
/* 269 */     int aEnd = sb.indexOf(';');
/* 270 */     if (aEnd == -1) {
/* 271 */       return new String[] { sb, "", "" };
/*     */     }
/* 273 */     int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
/* 274 */     if (sb.charAt(aEnd - 1) == ' ') {
/* 275 */       aEnd--;
/*     */     }
/* 277 */     int bEnd = sb.indexOf(';', bStart);
/* 278 */     if (bEnd == -1) {
/* 279 */       bEnd = HttpPostBodyUtil.findEndOfString(sb);
/* 280 */       return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), "" };
/*     */     } 
/* 282 */     int cStart = HttpPostBodyUtil.findNonWhitespace(sb, bEnd + 1);
/* 283 */     if (sb.charAt(bEnd - 1) == ' ') {
/* 284 */       bEnd--;
/*     */     }
/* 286 */     int cEnd = HttpPostBodyUtil.findEndOfString(sb);
/* 287 */     return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), sb.substring(cStart, cEnd) };
/*     */   }
/*     */ 
/*     */   
/*     */   public static class NotEnoughDataDecoderException
/*     */     extends DecoderException
/*     */   {
/*     */     private static final long serialVersionUID = -7846841864603865638L;
/*     */ 
/*     */     
/*     */     public NotEnoughDataDecoderException() {}
/*     */ 
/*     */     
/*     */     public NotEnoughDataDecoderException(String msg) {
/* 301 */       super(msg);
/*     */     }
/*     */     
/*     */     public NotEnoughDataDecoderException(Throwable cause) {
/* 305 */       super(cause);
/*     */     }
/*     */     
/*     */     public NotEnoughDataDecoderException(String msg, Throwable cause) {
/* 309 */       super(msg, cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class EndOfDataDecoderException
/*     */     extends DecoderException
/*     */   {
/*     */     private static final long serialVersionUID = 1336267941020800769L;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ErrorDataDecoderException
/*     */     extends DecoderException
/*     */   {
/*     */     private static final long serialVersionUID = 5020247425493164465L;
/*     */ 
/*     */     
/*     */     public ErrorDataDecoderException() {}
/*     */     
/*     */     public ErrorDataDecoderException(String msg) {
/* 330 */       super(msg);
/*     */     }
/*     */     
/*     */     public ErrorDataDecoderException(Throwable cause) {
/* 334 */       super(cause);
/*     */     }
/*     */     
/*     */     public ErrorDataDecoderException(String msg, Throwable cause) {
/* 338 */       super(msg, cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpPostRequestDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */