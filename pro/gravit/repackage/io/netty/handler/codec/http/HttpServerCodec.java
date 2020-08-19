/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.CombinedChannelDuplexHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HttpServerCodec
/*     */   extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder>
/*     */   implements HttpServerUpgradeHandler.SourceCodec
/*     */ {
/*  36 */   private final Queue<HttpMethod> queue = new ArrayDeque<HttpMethod>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerCodec() {
/*  44 */     this(4096, 8192, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
/*  51 */     init((ChannelInboundHandler)new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize), (ChannelOutboundHandler)new HttpServerResponseEncoder());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
/*  59 */     init((ChannelInboundHandler)new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), (ChannelOutboundHandler)new HttpServerResponseEncoder());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
/*  68 */     init((ChannelInboundHandler)new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize), (ChannelOutboundHandler)new HttpServerResponseEncoder());
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
/*     */   public void upgradeFrom(ChannelHandlerContext ctx) {
/*  80 */     ctx.pipeline().remove((ChannelHandler)this);
/*     */   }
/*     */   
/*     */   private final class HttpServerRequestDecoder
/*     */     extends HttpRequestDecoder {
/*     */     HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
/*  86 */       super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
/*     */     }
/*     */ 
/*     */     
/*     */     HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
/*  91 */       super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
/*  97 */       super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
/* 102 */       int oldSize = out.size();
/* 103 */       super.decode(ctx, buffer, out);
/* 104 */       int size = out.size();
/* 105 */       for (int i = oldSize; i < size; i++) {
/* 106 */         Object obj = out.get(i);
/* 107 */         if (obj instanceof HttpRequest)
/* 108 */           HttpServerCodec.this.queue.add(((HttpRequest)obj).method()); 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private final class HttpServerResponseEncoder
/*     */     extends HttpResponseEncoder {
/*     */     private HttpMethod method;
/*     */     
/*     */     private HttpServerResponseEncoder() {}
/*     */     
/*     */     protected void sanitizeHeadersBeforeEncode(HttpResponse msg, boolean isAlwaysEmpty) {
/* 120 */       if (!isAlwaysEmpty && HttpMethod.CONNECT.equals(this.method) && msg
/* 121 */         .status().codeClass() == HttpStatusClass.SUCCESS) {
/*     */ 
/*     */         
/* 124 */         msg.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/*     */         
/*     */         return;
/*     */       } 
/* 128 */       super.sanitizeHeadersBeforeEncode(msg, isAlwaysEmpty);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isContentAlwaysEmpty(HttpResponse msg) {
/* 133 */       this.method = HttpServerCodec.this.queue.poll();
/* 134 */       return (HttpMethod.HEAD.equals(this.method) || super.isContentAlwaysEmpty(msg));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpServerCodec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */