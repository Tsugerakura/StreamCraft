/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
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
/*     */ public final class DeflateFrameClientExtensionHandshaker
/*     */   implements WebSocketClientExtensionHandshaker
/*     */ {
/*     */   private final int compressionLevel;
/*     */   private final boolean useWebkitExtensionName;
/*     */   private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */   
/*     */   public DeflateFrameClientExtensionHandshaker(boolean useWebkitExtensionName) {
/*  44 */     this(6, useWebkitExtensionName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName) {
/*  54 */     this(compressionLevel, useWebkitExtensionName, WebSocketExtensionFilterProvider.DEFAULT);
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
/*     */   public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName, WebSocketExtensionFilterProvider extensionFilterProvider) {
/*  67 */     if (compressionLevel < 0 || compressionLevel > 9) {
/*  68 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/*  71 */     this.compressionLevel = compressionLevel;
/*  72 */     this.useWebkitExtensionName = useWebkitExtensionName;
/*  73 */     this.extensionFilterProvider = (WebSocketExtensionFilterProvider)ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketExtensionData newRequestData() {
/*  78 */     return new WebSocketExtensionData(this.useWebkitExtensionName ? "x-webkit-deflate-frame" : "deflate-frame", 
/*     */         
/*  80 */         Collections.emptyMap());
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
/*  85 */     if (!"x-webkit-deflate-frame".equals(extensionData.name()) && 
/*  86 */       !"deflate-frame".equals(extensionData.name())) {
/*  87 */       return null;
/*     */     }
/*     */     
/*  90 */     if (extensionData.parameters().isEmpty()) {
/*  91 */       return new DeflateFrameClientExtension(this.compressionLevel, this.extensionFilterProvider);
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */   
/*     */   private static class DeflateFrameClientExtension
/*     */     implements WebSocketClientExtension
/*     */   {
/*     */     private final int compressionLevel;
/*     */     private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */     
/*     */     DeflateFrameClientExtension(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider) {
/* 103 */       this.compressionLevel = compressionLevel;
/* 104 */       this.extensionFilterProvider = extensionFilterProvider;
/*     */     }
/*     */ 
/*     */     
/*     */     public int rsv() {
/* 109 */       return 4;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionEncoder newExtensionEncoder() {
/* 114 */       return new PerFrameDeflateEncoder(this.compressionLevel, 15, false, this.extensionFilterProvider
/* 115 */           .encoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionDecoder newExtensionDecoder() {
/* 120 */       return new PerFrameDeflateDecoder(false, this.extensionFilterProvider.decoderFilter());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\DeflateFrameClientExtensionHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */