/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocket07FrameDecoder
/*     */   extends WebSocket08FrameDecoder
/*     */ {
/*     */   public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
/*  74 */     this(WebSocketDecoderConfig.newBuilder()
/*  75 */         .expectMaskedFrames(expectMaskedFrames)
/*  76 */         .allowExtensions(allowExtensions)
/*  77 */         .maxFramePayloadLength(maxFramePayloadLength)
/*  78 */         .build());
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
/*     */   public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
/*  98 */     this(WebSocketDecoderConfig.newBuilder()
/*  99 */         .expectMaskedFrames(expectMaskedFrames)
/* 100 */         .allowExtensions(allowExtensions)
/* 101 */         .maxFramePayloadLength(maxFramePayloadLength)
/* 102 */         .allowMaskMismatch(allowMaskMismatch)
/* 103 */         .build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocket07FrameDecoder(WebSocketDecoderConfig decoderConfig) {
/* 113 */     super(decoderConfig);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocket07FrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */