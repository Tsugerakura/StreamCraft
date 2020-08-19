/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.net.URI;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.EmptyHttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
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
/*     */ public final class WebSocketClientProtocolConfig
/*     */ {
/*  32 */   static final WebSocketClientProtocolConfig DEFAULT = new WebSocketClientProtocolConfig(
/*  33 */       URI.create("https://localhost/"), null, WebSocketVersion.V13, false, (HttpHeaders)EmptyHttpHeaders.INSTANCE, 65536, true, false, true, WebSocketCloseStatus.NORMAL_CLOSURE, true, 10000L, -1L, false);
/*     */ 
/*     */   
/*     */   private final URI webSocketUri;
/*     */ 
/*     */   
/*     */   private final String subprotocol;
/*     */   
/*     */   private final WebSocketVersion version;
/*     */   
/*     */   private final boolean allowExtensions;
/*     */   
/*     */   private final HttpHeaders customHeaders;
/*     */   
/*     */   private final int maxFramePayloadLength;
/*     */   
/*     */   private final boolean performMasking;
/*     */   
/*     */   private final boolean allowMaskMismatch;
/*     */   
/*     */   private final boolean handleCloseFrames;
/*     */   
/*     */   private final WebSocketCloseStatus sendCloseFrame;
/*     */   
/*     */   private final boolean dropPongFrames;
/*     */   
/*     */   private final long handshakeTimeoutMillis;
/*     */   
/*     */   private final long forceCloseTimeoutMillis;
/*     */   
/*     */   private final boolean absoluteUpgradeUrl;
/*     */ 
/*     */   
/*     */   private WebSocketClientProtocolConfig(URI webSocketUri, String subprotocol, WebSocketVersion version, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, boolean handleCloseFrames, WebSocketCloseStatus sendCloseFrame, boolean dropPongFrames, long handshakeTimeoutMillis, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
/*  67 */     this.webSocketUri = webSocketUri;
/*  68 */     this.subprotocol = subprotocol;
/*  69 */     this.version = version;
/*  70 */     this.allowExtensions = allowExtensions;
/*  71 */     this.customHeaders = customHeaders;
/*  72 */     this.maxFramePayloadLength = maxFramePayloadLength;
/*  73 */     this.performMasking = performMasking;
/*  74 */     this.allowMaskMismatch = allowMaskMismatch;
/*  75 */     this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/*  76 */     this.handleCloseFrames = handleCloseFrames;
/*  77 */     this.sendCloseFrame = sendCloseFrame;
/*  78 */     this.dropPongFrames = dropPongFrames;
/*  79 */     this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
/*  80 */     this.absoluteUpgradeUrl = absoluteUpgradeUrl;
/*     */   }
/*     */   
/*     */   public URI webSocketUri() {
/*  84 */     return this.webSocketUri;
/*     */   }
/*     */   
/*     */   public String subprotocol() {
/*  88 */     return this.subprotocol;
/*     */   }
/*     */   
/*     */   public WebSocketVersion version() {
/*  92 */     return this.version;
/*     */   }
/*     */   
/*     */   public boolean allowExtensions() {
/*  96 */     return this.allowExtensions;
/*     */   }
/*     */   
/*     */   public HttpHeaders customHeaders() {
/* 100 */     return this.customHeaders;
/*     */   }
/*     */   
/*     */   public int maxFramePayloadLength() {
/* 104 */     return this.maxFramePayloadLength;
/*     */   }
/*     */   
/*     */   public boolean performMasking() {
/* 108 */     return this.performMasking;
/*     */   }
/*     */   
/*     */   public boolean allowMaskMismatch() {
/* 112 */     return this.allowMaskMismatch;
/*     */   }
/*     */   
/*     */   public boolean handleCloseFrames() {
/* 116 */     return this.handleCloseFrames;
/*     */   }
/*     */   
/*     */   public WebSocketCloseStatus sendCloseFrame() {
/* 120 */     return this.sendCloseFrame;
/*     */   }
/*     */   
/*     */   public boolean dropPongFrames() {
/* 124 */     return this.dropPongFrames;
/*     */   }
/*     */   
/*     */   public long handshakeTimeoutMillis() {
/* 128 */     return this.handshakeTimeoutMillis;
/*     */   }
/*     */   
/*     */   public long forceCloseTimeoutMillis() {
/* 132 */     return this.forceCloseTimeoutMillis;
/*     */   }
/*     */   
/*     */   public boolean absoluteUpgradeUrl() {
/* 136 */     return this.absoluteUpgradeUrl;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 141 */     return "WebSocketClientProtocolConfig {webSocketUri=" + this.webSocketUri + ", subprotocol=" + this.subprotocol + ", version=" + this.version + ", allowExtensions=" + this.allowExtensions + ", customHeaders=" + this.customHeaders + ", maxFramePayloadLength=" + this.maxFramePayloadLength + ", performMasking=" + this.performMasking + ", allowMaskMismatch=" + this.allowMaskMismatch + ", handleCloseFrames=" + this.handleCloseFrames + ", sendCloseFrame=" + this.sendCloseFrame + ", dropPongFrames=" + this.dropPongFrames + ", handshakeTimeoutMillis=" + this.handshakeTimeoutMillis + ", forceCloseTimeoutMillis=" + this.forceCloseTimeoutMillis + ", absoluteUpgradeUrl=" + this.absoluteUpgradeUrl + "}";
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
/*     */   public Builder toBuilder() {
/* 160 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   public static Builder newBuilder() {
/* 164 */     return new Builder(DEFAULT);
/*     */   }
/*     */   
/*     */   public static final class Builder {
/*     */     private URI webSocketUri;
/*     */     private String subprotocol;
/*     */     private WebSocketVersion version;
/*     */     private boolean allowExtensions;
/*     */     private HttpHeaders customHeaders;
/*     */     private int maxFramePayloadLength;
/*     */     private boolean performMasking;
/*     */     private boolean allowMaskMismatch;
/*     */     private boolean handleCloseFrames;
/*     */     private WebSocketCloseStatus sendCloseFrame;
/*     */     private boolean dropPongFrames;
/*     */     private long handshakeTimeoutMillis;
/*     */     private long forceCloseTimeoutMillis;
/*     */     private boolean absoluteUpgradeUrl;
/*     */     
/*     */     private Builder(WebSocketClientProtocolConfig clientConfig) {
/* 184 */       ObjectUtil.checkNotNull(clientConfig, "clientConfig");
/*     */       
/* 186 */       this.webSocketUri = clientConfig.webSocketUri();
/* 187 */       this.subprotocol = clientConfig.subprotocol();
/* 188 */       this.version = clientConfig.version();
/* 189 */       this.allowExtensions = clientConfig.allowExtensions();
/* 190 */       this.customHeaders = clientConfig.customHeaders();
/* 191 */       this.maxFramePayloadLength = clientConfig.maxFramePayloadLength();
/* 192 */       this.performMasking = clientConfig.performMasking();
/* 193 */       this.allowMaskMismatch = clientConfig.allowMaskMismatch();
/* 194 */       this.handleCloseFrames = clientConfig.handleCloseFrames();
/* 195 */       this.sendCloseFrame = clientConfig.sendCloseFrame();
/* 196 */       this.dropPongFrames = clientConfig.dropPongFrames();
/* 197 */       this.handshakeTimeoutMillis = clientConfig.handshakeTimeoutMillis();
/* 198 */       this.forceCloseTimeoutMillis = clientConfig.forceCloseTimeoutMillis();
/* 199 */       this.absoluteUpgradeUrl = clientConfig.absoluteUpgradeUrl();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder webSocketUri(String webSocketUri) {
/* 207 */       return webSocketUri(URI.create(webSocketUri));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder webSocketUri(URI webSocketUri) {
/* 215 */       this.webSocketUri = webSocketUri;
/* 216 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder subprotocol(String subprotocol) {
/* 223 */       this.subprotocol = subprotocol;
/* 224 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder version(WebSocketVersion version) {
/* 231 */       this.version = version;
/* 232 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowExtensions(boolean allowExtensions) {
/* 239 */       this.allowExtensions = allowExtensions;
/* 240 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder customHeaders(HttpHeaders customHeaders) {
/* 247 */       this.customHeaders = customHeaders;
/* 248 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder maxFramePayloadLength(int maxFramePayloadLength) {
/* 255 */       this.maxFramePayloadLength = maxFramePayloadLength;
/* 256 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder performMasking(boolean performMasking) {
/* 265 */       this.performMasking = performMasking;
/* 266 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder allowMaskMismatch(boolean allowMaskMismatch) {
/* 273 */       this.allowMaskMismatch = allowMaskMismatch;
/* 274 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder handleCloseFrames(boolean handleCloseFrames) {
/* 281 */       this.handleCloseFrames = handleCloseFrames;
/* 282 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder sendCloseFrame(WebSocketCloseStatus sendCloseFrame) {
/* 289 */       this.sendCloseFrame = sendCloseFrame;
/* 290 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder dropPongFrames(boolean dropPongFrames) {
/* 297 */       this.dropPongFrames = dropPongFrames;
/* 298 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder handshakeTimeoutMillis(long handshakeTimeoutMillis) {
/* 306 */       this.handshakeTimeoutMillis = handshakeTimeoutMillis;
/* 307 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder forceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 314 */       this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/* 315 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder absoluteUpgradeUrl(boolean absoluteUpgradeUrl) {
/* 322 */       this.absoluteUpgradeUrl = absoluteUpgradeUrl;
/* 323 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WebSocketClientProtocolConfig build() {
/* 330 */       return new WebSocketClientProtocolConfig(this.webSocketUri, this.subprotocol, this.version, this.allowExtensions, this.customHeaders, this.maxFramePayloadLength, this.performMasking, this.allowMaskMismatch, this.handleCloseFrames, this.sendCloseFrame, this.dropPongFrames, this.handshakeTimeoutMillis, this.forceCloseTimeoutMillis, this.absoluteUpgradeUrl);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketClientProtocolConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */