/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibCodecFactory;
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
/*     */ public final class PerMessageDeflateClientExtensionHandshaker
/*     */   implements WebSocketClientExtensionHandshaker
/*     */ {
/*     */   private final int compressionLevel;
/*     */   private final boolean allowClientWindowSize;
/*     */   private final int requestedServerWindowSize;
/*     */   private final boolean allowClientNoContext;
/*     */   private final boolean requestedServerNoContext;
/*     */   private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */   
/*     */   public PerMessageDeflateClientExtensionHandshaker() {
/*  50 */     this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
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
/*     */   public PerMessageDeflateClientExtensionHandshaker(int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext) {
/*  73 */     this(compressionLevel, allowClientWindowSize, requestedServerWindowSize, allowClientNoContext, requestedServerNoContext, WebSocketExtensionFilterProvider.DEFAULT);
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
/*     */   public PerMessageDeflateClientExtensionHandshaker(int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext, WebSocketExtensionFilterProvider extensionFilterProvider) {
/* 101 */     if (requestedServerWindowSize > 15 || requestedServerWindowSize < 8) {
/* 102 */       throw new IllegalArgumentException("requestedServerWindowSize: " + requestedServerWindowSize + " (expected: 8-15)");
/*     */     }
/*     */     
/* 105 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 106 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/* 109 */     this.compressionLevel = compressionLevel;
/* 110 */     this.allowClientWindowSize = allowClientWindowSize;
/* 111 */     this.requestedServerWindowSize = requestedServerWindowSize;
/* 112 */     this.allowClientNoContext = allowClientNoContext;
/* 113 */     this.requestedServerNoContext = requestedServerNoContext;
/* 114 */     this.extensionFilterProvider = (WebSocketExtensionFilterProvider)ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketExtensionData newRequestData() {
/* 119 */     HashMap<String, String> parameters = new HashMap<String, String>(4);
/* 120 */     if (this.requestedServerWindowSize != 15) {
/* 121 */       parameters.put("server_no_context_takeover", null);
/*     */     }
/* 123 */     if (this.allowClientNoContext) {
/* 124 */       parameters.put("client_no_context_takeover", null);
/*     */     }
/* 126 */     if (this.requestedServerWindowSize != 15) {
/* 127 */       parameters.put("server_max_window_bits", Integer.toString(this.requestedServerWindowSize));
/*     */     }
/* 129 */     if (this.allowClientWindowSize) {
/* 130 */       parameters.put("client_max_window_bits", null);
/*     */     }
/* 132 */     return new WebSocketExtensionData("permessage-deflate", parameters);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
/* 137 */     if (!"permessage-deflate".equals(extensionData.name())) {
/* 138 */       return null;
/*     */     }
/*     */     
/* 141 */     boolean succeed = true;
/* 142 */     int clientWindowSize = 15;
/* 143 */     int serverWindowSize = 15;
/* 144 */     boolean serverNoContext = false;
/* 145 */     boolean clientNoContext = false;
/*     */ 
/*     */     
/* 148 */     Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
/* 149 */     while (succeed && parametersIterator.hasNext()) {
/* 150 */       Map.Entry<String, String> parameter = parametersIterator.next();
/*     */       
/* 152 */       if ("client_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 154 */         if (this.allowClientWindowSize) {
/* 155 */           clientWindowSize = Integer.parseInt(parameter.getValue()); continue;
/*     */         } 
/* 157 */         succeed = false; continue;
/*     */       } 
/* 159 */       if ("server_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 161 */         serverWindowSize = Integer.parseInt(parameter.getValue());
/* 162 */         if (clientWindowSize > 15 || clientWindowSize < 8)
/* 163 */           succeed = false;  continue;
/*     */       } 
/* 165 */       if ("client_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 167 */         if (this.allowClientNoContext) {
/* 168 */           clientNoContext = true; continue;
/*     */         } 
/* 170 */         succeed = false; continue;
/*     */       } 
/* 172 */       if ("server_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
/*     */         
/* 174 */         if (this.requestedServerNoContext) {
/* 175 */           serverNoContext = true; continue;
/*     */         } 
/* 177 */         succeed = false;
/*     */         
/*     */         continue;
/*     */       } 
/* 181 */       succeed = false;
/*     */     } 
/*     */ 
/*     */     
/* 185 */     if ((this.requestedServerNoContext && !serverNoContext) || this.requestedServerWindowSize != serverWindowSize)
/*     */     {
/* 187 */       succeed = false;
/*     */     }
/*     */     
/* 190 */     if (succeed) {
/* 191 */       return new PermessageDeflateExtension(serverNoContext, serverWindowSize, clientNoContext, clientWindowSize, this.extensionFilterProvider);
/*     */     }
/*     */     
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private final class PermessageDeflateExtension
/*     */     implements WebSocketClientExtension
/*     */   {
/*     */     private final boolean serverNoContext;
/*     */     private final int serverWindowSize;
/*     */     private final boolean clientNoContext;
/*     */     private final int clientWindowSize;
/*     */     private final WebSocketExtensionFilterProvider extensionFilterProvider;
/*     */     
/*     */     public int rsv() {
/* 208 */       return 4;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     PermessageDeflateExtension(boolean serverNoContext, int serverWindowSize, boolean clientNoContext, int clientWindowSize, WebSocketExtensionFilterProvider extensionFilterProvider) {
/* 214 */       this.serverNoContext = serverNoContext;
/* 215 */       this.serverWindowSize = serverWindowSize;
/* 216 */       this.clientNoContext = clientNoContext;
/* 217 */       this.clientWindowSize = clientWindowSize;
/* 218 */       this.extensionFilterProvider = extensionFilterProvider;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionEncoder newExtensionEncoder() {
/* 223 */       return new PerMessageDeflateEncoder(PerMessageDeflateClientExtensionHandshaker.this.compressionLevel, this.clientWindowSize, this.clientNoContext, this.extensionFilterProvider
/* 224 */           .encoderFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketExtensionDecoder newExtensionDecoder() {
/* 229 */       return new PerMessageDeflateDecoder(this.serverNoContext, this.extensionFilterProvider.decoderFilter());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\PerMessageDeflateClientExtensionHandshaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */