/*     */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*     */ 
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultHttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultHttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponseStatus;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RtspDecoder
/*     */   extends HttpObjectDecoder
/*     */ {
/*  61 */   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isDecodingRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   private static final Pattern versionPattern = Pattern.compile("RTSP/\\d\\.\\d");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RtspDecoder() {
/*  95 */     this(4096, 8192, 8192);
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
/*     */   public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
/* 109 */     super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false);
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
/*     */   public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
/* 123 */     super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false, validateHeaders);
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
/*     */   protected HttpMessage createMessage(String[] initialLine) throws Exception {
/* 135 */     if (versionPattern.matcher(initialLine[0]).matches()) {
/* 136 */       this.isDecodingRequest = false;
/* 137 */       return (HttpMessage)new DefaultHttpResponse(RtspVersions.valueOf(initialLine[0]), new HttpResponseStatus(
/* 138 */             Integer.parseInt(initialLine[1]), initialLine[2]), this.validateHeaders);
/*     */     } 
/*     */ 
/*     */     
/* 142 */     this.isDecodingRequest = true;
/* 143 */     return (HttpMessage)new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), 
/* 144 */         RtspMethods.valueOf(initialLine[0]), initialLine[1], this.validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
/* 154 */     return (super.isContentAlwaysEmpty(msg) || !msg.headers().contains((CharSequence)RtspHeaderNames.CONTENT_LENGTH));
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpMessage createInvalidMessage() {
/* 159 */     if (this.isDecodingRequest) {
/* 160 */       return (HttpMessage)new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request", this.validateHeaders);
/*     */     }
/*     */     
/* 163 */     return (HttpMessage)new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, UNKNOWN_STATUS, this.validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isDecodingRequest() {
/* 171 */     return this.isDecodingRequest;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */