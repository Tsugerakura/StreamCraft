/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
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
/*     */ public final class WebSocketServerProtocolConfig
/*     */ {
/*  28 */   static final WebSocketServerProtocolConfig DEFAULT = new WebSocketServerProtocolConfig("/", null, false, 10000L, 0L, true, WebSocketCloseStatus.NORMAL_CLOSURE, true, WebSocketDecoderConfig.DEFAULT);
/*     */ 
/*     */   
/*     */   private final String websocketPath;
/*     */ 
/*     */   
/*     */   private final String subprotocols;
/*     */   
/*     */   private final boolean checkStartsWith;
/*     */   
/*     */   private final long handshakeTimeoutMillis;
/*     */   
/*     */   private final long forceCloseTimeoutMillis;
/*     */   
/*     */   private final boolean handleCloseFrames;
/*     */   
/*     */   private final WebSocketCloseStatus sendCloseFrame;
/*     */   
/*     */   private final boolean dropPongFrames;
/*     */   
/*     */   private final WebSocketDecoderConfig decoderConfig;
/*     */ 
/*     */   
/*     */   private WebSocketServerProtocolConfig(String websocketPath, String subprotocols, boolean checkStartsWith, long handshakeTimeoutMillis, long forceCloseTimeoutMillis, boolean handleCloseFrames, WebSocketCloseStatus sendCloseFrame, boolean dropPongFrames, WebSocketDecoderConfig decoderConfig) {
/*  52 */     this.websocketPath = websocketPath;
/*  53 */     this.subprotocols = subprotocols;
/*  54 */     this.checkStartsWith = checkStartsWith;
/*  55 */     this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
/*  56 */     this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/*  57 */     this.handleCloseFrames = handleCloseFrames;
/*  58 */     this.sendCloseFrame = sendCloseFrame;
/*  59 */     this.dropPongFrames = dropPongFrames;
/*  60 */     this.decoderConfig = (decoderConfig == null) ? WebSocketDecoderConfig.DEFAULT : decoderConfig;
/*     */   }
/*     */   
/*     */   public String websocketPath() {
/*  64 */     return this.websocketPath;
/*     */   }
/*     */   
/*     */   public String subprotocols() {
/*  68 */     return this.subprotocols;
/*     */   }
/*     */   
/*     */   public boolean checkStartsWith() {
/*  72 */     return this.checkStartsWith;
/*     */   }
/*     */   
/*     */   public long handshakeTimeoutMillis() {
/*  76 */     return this.handshakeTimeoutMillis;
/*     */   }
/*     */   
/*     */   public long forceCloseTimeoutMillis() {
/*  80 */     return this.forceCloseTimeoutMillis;
/*     */   }
/*     */   
/*     */   public boolean handleCloseFrames() {
/*  84 */     return this.handleCloseFrames;
/*     */   }
/*     */   
/*     */   public WebSocketCloseStatus sendCloseFrame() {
/*  88 */     return this.sendCloseFrame;
/*     */   }
/*     */   
/*     */   public boolean dropPongFrames() {
/*  92 */     return this.dropPongFrames;
/*     */   }
/*     */   
/*     */   public WebSocketDecoderConfig decoderConfig() {
/*  96 */     return this.decoderConfig;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 101 */     return "WebSocketServerProtocolConfig {websocketPath=" + this.websocketPath + ", subprotocols=" + this.subprotocols + ", checkStartsWith=" + this.checkStartsWith + ", handshakeTimeoutMillis=" + this.handshakeTimeoutMillis + ", forceCloseTimeoutMillis=" + this.forceCloseTimeoutMillis + ", handleCloseFrames=" + this.handleCloseFrames + ", sendCloseFrame=" + this.sendCloseFrame + ", dropPongFrames=" + this.dropPongFrames + ", decoderConfig=" + this.decoderConfig + "}";
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
/*     */   public Builder toBuilder() {
/* 115 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   public static Builder newBuilder() {
/* 119 */     return new Builder(DEFAULT);
/*     */   }
/*     */   
/*     */   public static final class Builder {
/*     */     private String websocketPath;
/*     */     private String subprotocols;
/*     */     private boolean checkStartsWith;
/*     */     private long handshakeTimeoutMillis;
/*     */     private long forceCloseTimeoutMillis;
/*     */     private boolean handleCloseFrames;
/*     */     private WebSocketCloseStatus sendCloseFrame;
/*     */     private boolean dropPongFrames;
/*     */     private WebSocketDecoderConfig decoderConfig;
/*     */     private WebSocketDecoderConfig.Builder decoderConfigBuilder;
/*     */     
/*     */     private Builder(WebSocketServerProtocolConfig serverConfig) {
/* 135 */       ObjectUtil.checkNotNull(serverConfig, "serverConfig");
/* 136 */       this.websocketPath = serverConfig.websocketPath();
/* 137 */       this.subprotocols = serverConfig.subprotocols();
/* 138 */       this.checkStartsWith = serverConfig.checkStartsWith();
/* 139 */       this.handshakeTimeoutMillis = serverConfig.handshakeTimeoutMillis();
/* 140 */       this.forceCloseTimeoutMillis = serverConfig.forceCloseTimeoutMillis();
/* 141 */       this.handleCloseFrames = serverConfig.handleCloseFrames();
/* 142 */       this.sendCloseFrame = serverConfig.sendCloseFrame();
/* 143 */       this.dropPongFrames = serverConfig.dropPongFrames();
/* 144 */       this.decoderConfig = serverConfig.decoderConfig();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder websocketPath(String websocketPath) {
/* 151 */       this.websocketPath = websocketPath;
/* 152 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder subprotocols(String subprotocols) {
/* 159 */       this.subprotocols = subprotocols;
/* 160 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder checkStartsWith(boolean checkStartsWith) {
/* 168 */       this.checkStartsWith = checkStartsWith;
/* 169 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder handshakeTimeoutMillis(long handshakeTimeoutMillis) {
/* 177 */       this.handshakeTimeoutMillis = handshakeTimeoutMillis;
/* 178 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder forceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
/* 185 */       this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
/* 186 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder handleCloseFrames(boolean handleCloseFrames) {
/* 193 */       this.handleCloseFrames = handleCloseFrames;
/* 194 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder sendCloseFrame(WebSocketCloseStatus sendCloseFrame) {
/* 201 */       this.sendCloseFrame = sendCloseFrame;
/* 202 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder dropPongFrames(boolean dropPongFrames) {
/* 209 */       this.dropPongFrames = dropPongFrames;
/* 210 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder decoderConfig(WebSocketDecoderConfig decoderConfig) {
/* 217 */       this.decoderConfig = (decoderConfig == null) ? WebSocketDecoderConfig.DEFAULT : decoderConfig;
/* 218 */       this.decoderConfigBuilder = null;
/* 219 */       return this;
/*     */     }
/*     */     
/*     */     private WebSocketDecoderConfig.Builder decoderConfigBuilder() {
/* 223 */       if (this.decoderConfigBuilder == null) {
/* 224 */         this.decoderConfigBuilder = this.decoderConfig.toBuilder();
/*     */       }
/* 226 */       return this.decoderConfigBuilder;
/*     */     }
/*     */     
/*     */     public Builder maxFramePayloadLength(int maxFramePayloadLength) {
/* 230 */       decoderConfigBuilder().maxFramePayloadLength(maxFramePayloadLength);
/* 231 */       return this;
/*     */     }
/*     */     
/*     */     public Builder expectMaskedFrames(boolean expectMaskedFrames) {
/* 235 */       decoderConfigBuilder().expectMaskedFrames(expectMaskedFrames);
/* 236 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowMaskMismatch(boolean allowMaskMismatch) {
/* 240 */       decoderConfigBuilder().allowMaskMismatch(allowMaskMismatch);
/* 241 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowExtensions(boolean allowExtensions) {
/* 245 */       decoderConfigBuilder().allowExtensions(allowExtensions);
/* 246 */       return this;
/*     */     }
/*     */     
/*     */     public Builder closeOnProtocolViolation(boolean closeOnProtocolViolation) {
/* 250 */       decoderConfigBuilder().closeOnProtocolViolation(closeOnProtocolViolation);
/* 251 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withUTF8Validator(boolean withUTF8Validator) {
/* 255 */       decoderConfigBuilder().withUTF8Validator(withUTF8Validator);
/* 256 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WebSocketServerProtocolConfig build() {
/* 263 */       return new WebSocketServerProtocolConfig(this.websocketPath, this.subprotocols, this.checkStartsWith, this.handshakeTimeoutMillis, this.forceCloseTimeoutMillis, this.handleCloseFrames, this.sendCloseFrame, this.dropPongFrames, (this.decoderConfigBuilder == null) ? this.decoderConfig : this.decoderConfigBuilder
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 272 */           .build());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketServerProtocolConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */