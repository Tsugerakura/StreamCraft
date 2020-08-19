/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.embedded.EmbeddedChannel;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibCodecFactory;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.compression.ZlibWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpContentCompressor
/*     */   extends HttpContentEncoder
/*     */ {
/*     */   private final int compressionLevel;
/*     */   private final int windowBits;
/*     */   private final int memLevel;
/*     */   private final int contentSizeThreshold;
/*     */   private ChannelHandlerContext ctx;
/*     */   
/*     */   public HttpContentCompressor() {
/*  43 */     this(6);
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
/*     */   public HttpContentCompressor(int compressionLevel) {
/*  56 */     this(compressionLevel, 15, 8, 0);
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
/*     */   public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
/*  79 */     this(compressionLevel, windowBits, memLevel, 0);
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
/*     */   public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel, int contentSizeThreshold) {
/* 106 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 107 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */ 
/*     */     
/* 111 */     if (windowBits < 9 || windowBits > 15) {
/* 112 */       throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
/*     */     }
/*     */     
/* 115 */     if (memLevel < 1 || memLevel > 9) {
/* 116 */       throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
/*     */     }
/*     */     
/* 119 */     if (contentSizeThreshold < 0) {
/* 120 */       throw new IllegalArgumentException("contentSizeThreshold: " + contentSizeThreshold + " (expected: non negative number)");
/*     */     }
/*     */     
/* 123 */     this.compressionLevel = compressionLevel;
/* 124 */     this.windowBits = windowBits;
/* 125 */     this.memLevel = memLevel;
/* 126 */     this.contentSizeThreshold = contentSizeThreshold;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 131 */     this.ctx = ctx;
/*     */   }
/*     */   
/*     */   protected HttpContentEncoder.Result beginEncode(HttpResponse httpResponse, String acceptEncoding) throws Exception {
/*     */     String targetContentEncoding;
/* 136 */     if (this.contentSizeThreshold > 0 && 
/* 137 */       httpResponse instanceof HttpContent && ((HttpContent)httpResponse)
/* 138 */       .content().readableBytes() < this.contentSizeThreshold) {
/* 139 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 143 */     String contentEncoding = httpResponse.headers().get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
/* 144 */     if (contentEncoding != null)
/*     */     {
/*     */       
/* 147 */       return null;
/*     */     }
/*     */     
/* 150 */     ZlibWrapper wrapper = determineWrapper(acceptEncoding);
/* 151 */     if (wrapper == null) {
/* 152 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 156 */     switch (wrapper) {
/*     */       case GZIP:
/* 158 */         targetContentEncoding = "gzip";
/*     */         break;
/*     */       case ZLIB:
/* 161 */         targetContentEncoding = "deflate";
/*     */         break;
/*     */       default:
/* 164 */         throw new Error();
/*     */     } 
/*     */     
/* 167 */     return new HttpContentEncoder.Result(targetContentEncoding, new EmbeddedChannel(this.ctx
/*     */           
/* 169 */           .channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx
/* 170 */           .channel().config(), new ChannelHandler[] { (ChannelHandler)ZlibCodecFactory.newZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel) }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ZlibWrapper determineWrapper(String acceptEncoding) {
/* 176 */     float starQ = -1.0F;
/* 177 */     float gzipQ = -1.0F;
/* 178 */     float deflateQ = -1.0F;
/* 179 */     for (String encoding : acceptEncoding.split(",")) {
/* 180 */       float q = 1.0F;
/* 181 */       int equalsPos = encoding.indexOf('=');
/* 182 */       if (equalsPos != -1) {
/*     */         try {
/* 184 */           q = Float.parseFloat(encoding.substring(equalsPos + 1));
/* 185 */         } catch (NumberFormatException e) {
/*     */           
/* 187 */           q = 0.0F;
/*     */         } 
/*     */       }
/* 190 */       if (encoding.contains("*")) {
/* 191 */         starQ = q;
/* 192 */       } else if (encoding.contains("gzip") && q > gzipQ) {
/* 193 */         gzipQ = q;
/* 194 */       } else if (encoding.contains("deflate") && q > deflateQ) {
/* 195 */         deflateQ = q;
/*     */       } 
/*     */     } 
/* 198 */     if (gzipQ > 0.0F || deflateQ > 0.0F) {
/* 199 */       if (gzipQ >= deflateQ) {
/* 200 */         return ZlibWrapper.GZIP;
/*     */       }
/* 202 */       return ZlibWrapper.ZLIB;
/*     */     } 
/*     */     
/* 205 */     if (starQ > 0.0F) {
/* 206 */       if (gzipQ == -1.0F) {
/* 207 */         return ZlibWrapper.GZIP;
/*     */       }
/* 209 */       if (deflateQ == -1.0F) {
/* 210 */         return ZlibWrapper.ZLIB;
/*     */       }
/*     */     } 
/* 213 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpContentCompressor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */