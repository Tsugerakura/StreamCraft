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
/*     */ public final class WebSocketDecoderConfig
/*     */ {
/*  25 */   static final WebSocketDecoderConfig DEFAULT = new WebSocketDecoderConfig(65536, true, false, false, true, true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int maxFramePayloadLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean expectMaskedFrames;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean allowMaskMismatch;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean allowExtensions;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean closeOnProtocolViolation;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean withUTF8Validator;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private WebSocketDecoderConfig(int maxFramePayloadLength, boolean expectMaskedFrames, boolean allowMaskMismatch, boolean allowExtensions, boolean closeOnProtocolViolation, boolean withUTF8Validator) {
/*  59 */     this.maxFramePayloadLength = maxFramePayloadLength;
/*  60 */     this.expectMaskedFrames = expectMaskedFrames;
/*  61 */     this.allowMaskMismatch = allowMaskMismatch;
/*  62 */     this.allowExtensions = allowExtensions;
/*  63 */     this.closeOnProtocolViolation = closeOnProtocolViolation;
/*  64 */     this.withUTF8Validator = withUTF8Validator;
/*     */   }
/*     */   
/*     */   public int maxFramePayloadLength() {
/*  68 */     return this.maxFramePayloadLength;
/*     */   }
/*     */   
/*     */   public boolean expectMaskedFrames() {
/*  72 */     return this.expectMaskedFrames;
/*     */   }
/*     */   
/*     */   public boolean allowMaskMismatch() {
/*  76 */     return this.allowMaskMismatch;
/*     */   }
/*     */   
/*     */   public boolean allowExtensions() {
/*  80 */     return this.allowExtensions;
/*     */   }
/*     */   
/*     */   public boolean closeOnProtocolViolation() {
/*  84 */     return this.closeOnProtocolViolation;
/*     */   }
/*     */   
/*     */   public boolean withUTF8Validator() {
/*  88 */     return this.withUTF8Validator;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  93 */     return "WebSocketDecoderConfig [maxFramePayloadLength=" + this.maxFramePayloadLength + ", expectMaskedFrames=" + this.expectMaskedFrames + ", allowMaskMismatch=" + this.allowMaskMismatch + ", allowExtensions=" + this.allowExtensions + ", closeOnProtocolViolation=" + this.closeOnProtocolViolation + ", withUTF8Validator=" + this.withUTF8Validator + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Builder toBuilder() {
/* 104 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   public static Builder newBuilder() {
/* 108 */     return new Builder(DEFAULT);
/*     */   }
/*     */   
/*     */   public static final class Builder {
/*     */     private int maxFramePayloadLength;
/*     */     private boolean expectMaskedFrames;
/*     */     private boolean allowMaskMismatch;
/*     */     private boolean allowExtensions;
/*     */     private boolean closeOnProtocolViolation;
/*     */     private boolean withUTF8Validator;
/*     */     
/*     */     private Builder(WebSocketDecoderConfig decoderConfig) {
/* 120 */       ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
/* 121 */       this.maxFramePayloadLength = decoderConfig.maxFramePayloadLength();
/* 122 */       this.expectMaskedFrames = decoderConfig.expectMaskedFrames();
/* 123 */       this.allowMaskMismatch = decoderConfig.allowMaskMismatch();
/* 124 */       this.allowExtensions = decoderConfig.allowExtensions();
/* 125 */       this.closeOnProtocolViolation = decoderConfig.closeOnProtocolViolation();
/* 126 */       this.withUTF8Validator = decoderConfig.withUTF8Validator();
/*     */     }
/*     */     
/*     */     public Builder maxFramePayloadLength(int maxFramePayloadLength) {
/* 130 */       this.maxFramePayloadLength = maxFramePayloadLength;
/* 131 */       return this;
/*     */     }
/*     */     
/*     */     public Builder expectMaskedFrames(boolean expectMaskedFrames) {
/* 135 */       this.expectMaskedFrames = expectMaskedFrames;
/* 136 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowMaskMismatch(boolean allowMaskMismatch) {
/* 140 */       this.allowMaskMismatch = allowMaskMismatch;
/* 141 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowExtensions(boolean allowExtensions) {
/* 145 */       this.allowExtensions = allowExtensions;
/* 146 */       return this;
/*     */     }
/*     */     
/*     */     public Builder closeOnProtocolViolation(boolean closeOnProtocolViolation) {
/* 150 */       this.closeOnProtocolViolation = closeOnProtocolViolation;
/* 151 */       return this;
/*     */     }
/*     */     
/*     */     public Builder withUTF8Validator(boolean withUTF8Validator) {
/* 155 */       this.withUTF8Validator = withUTF8Validator;
/* 156 */       return this;
/*     */     }
/*     */     
/*     */     public WebSocketDecoderConfig build() {
/* 160 */       return new WebSocketDecoderConfig(this.maxFramePayloadLength, this.expectMaskedFrames, this.allowMaskMismatch, this.allowExtensions, this.closeOnProtocolViolation, this.withUTF8Validator);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketDecoderConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */