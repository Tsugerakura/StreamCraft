/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpRequestDecoder
/*    */   extends HttpObjectDecoder
/*    */ {
/*    */   public HttpRequestDecoder() {}
/*    */   
/*    */   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
/* 70 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
/* 75 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
/* 81 */     super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
/*    */   }
/*    */ 
/*    */   
/*    */   protected HttpMessage createMessage(String[] initialLine) throws Exception {
/* 86 */     return new DefaultHttpRequest(
/* 87 */         HttpVersion.valueOf(initialLine[2]), 
/* 88 */         HttpMethod.valueOf(initialLine[0]), initialLine[1], this.validateHeaders);
/*    */   }
/*    */ 
/*    */   
/*    */   protected HttpMessage createInvalidMessage() {
/* 93 */     return new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request", this.validateHeaders);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isDecodingRequest() {
/* 98 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpRequestDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */