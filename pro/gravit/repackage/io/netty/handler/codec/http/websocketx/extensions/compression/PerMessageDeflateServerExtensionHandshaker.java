/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibCodecFactory;
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
/*     */ 
/*     */ public final class PerMessageDeflateServerExtensionHandshaker
/*     */   implements WebSocketServerExtensionHandshaker
/*     */ {
/*     */   public static final int MIN_WINDOW_SIZE = 8;
/*     */   public static final int MAX_WINDOW_SIZE = 15;
/*     */   static final String PERMESSAGE_DEFLATE_EXTENSION = "permessage-deflate";
/*     */   static final String CLIENT_MAX_WINDOW = "client_max_window_bits";
/*     */   static final String SERVER_MAX_WINDOW = "server_max_window_bits";
/*     */   static final String CLIENT_NO_CONTEXT = "client_no_context_takeover";
/*     */   static final String SERVER_NO_CONTEXT = "server_no_context_takeover";
/*     */   private final int compressionLevel;
/*     */   private final boolean allowServerWindowSize;
/*     */   private final int preferredClientWindowSize;
/*     */   private final boolean allowServerNoContext;
/*     */   private final boolean preferredClientNoContext;
/*     */   private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */   
/*     */   public PerMessageDeflateServerExtensionHandshaker() {
/*  58 */     this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
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
/*     */   public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext) {
/*  81 */     this(compressionLevel, allowServerWindowSize, preferredClientWindowSize, allowServerNoContext, preferredClientNoContext, WebSocketExtensionFilterProvider.DEFAULT);
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
/*     */   public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext, WebSocketExtensionFilterProvider extensionFilterProvider) {
/* 108 */     if (preferredClientWindowSize > 15 || preferredClientWindowSize < 8) {
/* 109 */       throw new IllegalArgumentException("preferredServerWindowSize: " + preferredClientWindowSize + " (expected: 8-15)");
/*     */     }
/*     */     
/* 112 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 113 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/* 116 */     this.compressionLevel = compressionLevel;
/* 117 */     this.allowServerWindowSize = allowServerWindowSize;
/* 118 */     this.preferredClientWindowSize = preferredClientWindowSize;
/* 119 */     this.allowServerNoContext = allowServerNoContext;
/* 120 */     this.preferredClientNoContext = preferredClientNoContext;
/* 121 */     this.extensionFilterProvider = (WebSocketExtensionFilterProvider)ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
/* 126 */     if (!"permessage-deflate".equals(extensionData.name())) {
/* 127 */       return null;
/*     */     }
/*     */     
/* 130 */     boolean deflateEnabled = true;
/* 131 */     int clientWindowSize = 15;
/* 132 */     int serverWindowSize = 15;
/* 133 */     boolean serverNoContext = false;
/* 134 */     boolean clientNoContext = false;
/*     */ 
/*     */     
/* 137 */     Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
/* 138 */     while (deflateEnabled && parametersIterator.hasNext()) {
/* 139 */       Map.Entry<String, String> parameter = parametersIterator.next();
/*     */       
/* 141 */       if ("client_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 143 */         clientWindowSize = this.preferredClientWindowSize; continue;
/* 144 */       }  if ("server_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 146 */         if (this.allowServerWindowSize) {
/* 147 */           serverWindowSize = Integer.parseInt(parameter.getValue());
/* 148 */           if (serverWindowSize > 15 || serverWindowSize < 8)
/* 149 */             deflateEnabled = false; 
/*     */           continue;
/*     */         } 
/* 152 */         deflateEnabled = false; continue;
/*     */       } 
/* 154 */       if ("client_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 156 */         clientNoContext = this.preferredClientNoContext; continue;
/* 157 */       }  if ("server_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 159 */         if (this.allowServerNoContext) {
/* 160 */           serverNoContext = true; continue;
/*     */         } 
/* 162 */         deflateEnabled = false;
/*     */         
/*     */         continue;
/*     */       } 
/* 166 */       deflateEnabled = false;
/*     */     } 
/*     */ 
/*     */     
/* 170 */     if (deflateEnabled) {
/* 171 */       return new PermessageDeflateExtension(this.compressionLevel, serverNoContext, serverWindowSize, clientNoContext, clientWindowSize, this.extensionFilterProvider);
/*     */     }
/*     */     
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class PermessageDeflateExtension
/*     */     implements WebSocketServerExtension
/*     */   {
/*     */     private final int compressionLevel;
/*     */     
/*     */     private final boolean serverNoContext;
/*     */     private final int serverWindowSize;
/*     */     private final boolean clientNoContext;
/*     */     private final int clientWindowSize;
/*     */     private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */     
/*     */     PermessageDeflateExtension(int compressionLevel, boolean serverNoContext, int serverWindowSize, boolean clientNoContext, int clientWindowSize, WebSocketExtensionFilterProvider extensionFilterProvider) {
/* 190 */       this.compressionLevel = compressionLevel;
/* 191 */       this.serverNoContext = serverNoContext;
/* 192 */       this.serverWindowSize = serverWindowSize;
/* 193 */       this.clientNoContext = clientNoContext;
/* 194 */       this.clientWindowSize = clientWindowSize;
/* 195 */       this.extensionFilterProvider = extensionFilterProvider;
/*     */     }
/*     */ 
/*     */     
/*     */     public int rsv() {
/* 200 */       return 4;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionEncoder newExtensionEncoder() {
/* 205 */       return new PerMessageDeflateEncoder(this.compressionLevel, this.serverWindowSize, this.serverNoContext, this.extensionFilterProvider
/* 206 */           .encoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionDecoder newExtensionDecoder() {
/* 211 */       return new PerMessageDeflateDecoder(this.clientNoContext, this.extensionFilterProvider.decoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionData newReponseData() {
/* 216 */       HashMap<String, String> parameters = new HashMap<String, String>(4);
/* 217 */       if (this.serverNoContext) {
/* 218 */         parameters.put("server_no_context_takeover", null);
/*     */       }
/* 220 */       if (this.clientNoContext) {
/* 221 */         parameters.put("client_no_context_takeover", null);
/*     */       }
/* 223 */       if (this.serverWindowSize != 15) {
/* 224 */         parameters.put("server_max_window_bits", Integer.toString(this.serverWindowSize));
/*     */       }
/* 226 */       if (this.clientWindowSize != 15) {
/* 227 */         parameters.put("client_max_window_bits", Integer.toString(this.clientWindowSize));
/*     */       }
/* 229 */       return new WebSocketExtensionData("permessage-deflate", parameters);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerMessageDeflateServerExtensionHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */