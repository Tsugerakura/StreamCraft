/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
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
/*     */ 
/*     */ public final class DeflateFrameServerExtensionHandshaker
/*     */   implements WebSocketServerExtensionHandshaker
/*     */ {
/*     */   static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
/*     */   static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
/*     */   private final int compressionLevel;
/*     */   private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */   
/*     */   public DeflateFrameServerExtensionHandshaker() {
/*  45 */     this(6);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeflateFrameServerExtensionHandshaker(int compressionLevel) {
/*  55 */     this(compressionLevel, WebSocketExtensionFilterProvider.DEFAULT);
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
/*     */   public DeflateFrameServerExtensionHandshaker(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider) {
/*  68 */     if (compressionLevel < 0 || compressionLevel > 9) {
/*  69 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/*  72 */     this.compressionLevel = compressionLevel;
/*  73 */     this.extensionFilterProvider = (WebSocketExtensionFilterProvider)ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
/*  78 */     if (!"x-webkit-deflate-frame".equals(extensionData.name()) && 
/*  79 */       !"deflate-frame".equals(extensionData.name())) {
/*  80 */       return null;
/*     */     }
/*     */     
/*  83 */     if (extensionData.parameters().isEmpty()) {
/*  84 */       return new DeflateFrameServerExtension(this.compressionLevel, extensionData.name(), this.extensionFilterProvider);
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class DeflateFrameServerExtension
/*     */     implements WebSocketServerExtension
/*     */   {
/*     */     private final String extensionName;
/*     */     private final int compressionLevel;
/*     */     private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */     
/*     */     DeflateFrameServerExtension(int compressionLevel, String extensionName, WebSocketExtensionFilterProvider extensionFilterProvider) {
/*  98 */       this.extensionName = extensionName;
/*  99 */       this.compressionLevel = compressionLevel;
/* 100 */       this.extensionFilterProvider = extensionFilterProvider;
/*     */     }
/*     */ 
/*     */     
/*     */     public int rsv() {
/* 105 */       return 4;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionEncoder newExtensionEncoder() {
/* 110 */       return new PerFrameDeflateEncoder(this.compressionLevel, 15, false, this.extensionFilterProvider
/* 111 */           .encoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionDecoder newExtensionDecoder() {
/* 116 */       return new PerFrameDeflateDecoder(false, this.extensionFilterProvider.decoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionData newReponseData() {
/* 121 */       return new WebSocketExtensionData(this.extensionName, Collections.emptyMap());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\DeflateFrameServerExtensionHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */