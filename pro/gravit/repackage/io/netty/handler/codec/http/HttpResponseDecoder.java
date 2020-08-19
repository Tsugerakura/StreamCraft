/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpResponseDecoder
/*     */   extends HttpObjectDecoder
/*     */ {
/*  86 */   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponseDecoder() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
/* 101 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
/* 106 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
/* 112 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpMessage createMessage(String[] initialLine) {
/* 117 */     return new DefaultHttpResponse(
/* 118 */         HttpVersion.valueOf(initialLine[0]), 
/* 119 */         HttpResponseStatus.valueOf(Integer.parseInt(initialLine[1]), initialLine[2]), this.validateHeaders);
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpMessage createInvalidMessage() {
/* 124 */     return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, UNKNOWN_STATUS, this.validateHeaders);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isDecodingRequest() {
/* 129 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpResponseDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */