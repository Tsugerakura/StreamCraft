/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WebSocketClientHandshakerFactory
/*     */ {
/*     */   public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders) {
/*  53 */     return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, 65536);
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
/*     */   public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
/*  77 */     return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
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
/*     */   public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
/* 110 */     return newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, -1L);
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
/*     */ 
/*     */   
/*     */   public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
/* 145 */     if (version == WebSocketVersion.V13) {
/* 146 */       return new WebSocketClientHandshaker13(webSocketURL, WebSocketVersion.V13, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis);
/*     */     }
/*     */ 
/*     */     
/* 150 */     if (version == WebSocketVersion.V08) {
/* 151 */       return new WebSocketClientHandshaker08(webSocketURL, WebSocketVersion.V08, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis);
/*     */     }
/*     */ 
/*     */     
/* 155 */     if (version == WebSocketVersion.V07) {
/* 156 */       return new WebSocketClientHandshaker07(webSocketURL, WebSocketVersion.V07, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis);
/*     */     }
/*     */ 
/*     */     
/* 160 */     if (version == WebSocketVersion.V00) {
/* 161 */       return new WebSocketClientHandshaker00(webSocketURL, WebSocketVersion.V00, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis);
/*     */     }
/*     */ 
/*     */     
/* 165 */     throw new WebSocketHandshakeException("Protocol version " + version + " not supported.");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/* 202 */     if (version == WebSocketVersion.V13) {
/* 203 */       return new WebSocketClientHandshaker13(webSocketURL, WebSocketVersion.V13, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     }
/*     */ 
/*     */     
/* 207 */     if (version == WebSocketVersion.V08) {
/* 208 */       return new WebSocketClientHandshaker08(webSocketURL, WebSocketVersion.V08, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     }
/*     */ 
/*     */     
/* 212 */     if (version == WebSocketVersion.V07) {
/* 213 */       return new WebSocketClientHandshaker07(webSocketURL, WebSocketVersion.V07, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     }
/*     */ 
/*     */     
/* 217 */     if (version == WebSocketVersion.V00) {
/* 218 */       return new WebSocketClientHandshaker00(webSocketURL, WebSocketVersion.V00, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 223 */     throw new WebSocketHandshakeException("Protocol version " + version + " not supported.");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientHandshakerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */