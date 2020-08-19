/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.embedded.EmbeddedChannel;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CodecException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpContentDecoder
/*     */   extends MessageToMessageDecoder<HttpObject>
/*     */ {
/*  49 */   static final String IDENTITY = HttpHeaderValues.IDENTITY.toString();
/*     */   
/*     */   protected ChannelHandlerContext ctx;
/*     */   
/*     */   private EmbeddedChannel decoder;
/*     */   private boolean continueResponse;
/*     */   private boolean needRead = true;
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
/*     */     try {
/*  59 */       if (msg instanceof HttpResponse && ((HttpResponse)msg).status().code() == 100) {
/*     */         
/*  61 */         if (!(msg instanceof LastHttpContent)) {
/*  62 */           this.continueResponse = true;
/*     */         }
/*     */         
/*  65 */         out.add(ReferenceCountUtil.retain(msg));
/*     */         
/*     */         return;
/*     */       } 
/*  69 */       if (this.continueResponse) {
/*  70 */         if (msg instanceof LastHttpContent) {
/*  71 */           this.continueResponse = false;
/*     */         }
/*     */         
/*  74 */         out.add(ReferenceCountUtil.retain(msg));
/*     */         
/*     */         return;
/*     */       } 
/*  78 */       if (msg instanceof HttpMessage) {
/*  79 */         cleanup();
/*  80 */         HttpMessage message = (HttpMessage)msg;
/*  81 */         HttpHeaders headers = message.headers();
/*     */ 
/*     */         
/*  84 */         String contentEncoding = headers.get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
/*  85 */         if (contentEncoding != null) {
/*  86 */           contentEncoding = contentEncoding.trim();
/*     */         } else {
/*  88 */           contentEncoding = IDENTITY;
/*     */         } 
/*  90 */         this.decoder = newContentDecoder(contentEncoding);
/*     */         
/*  92 */         if (this.decoder == null) {
/*  93 */           if (message instanceof HttpContent) {
/*  94 */             ((HttpContent)message).retain();
/*     */           }
/*  96 */           out.add(message);
/*     */ 
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */ 
/*     */         
/* 104 */         if (headers.contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH)) {
/* 105 */           headers.remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/* 106 */           headers.set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 112 */         CharSequence targetContentEncoding = getTargetContentEncoding(contentEncoding);
/* 113 */         if (HttpHeaderValues.IDENTITY.contentEquals(targetContentEncoding)) {
/*     */ 
/*     */           
/* 116 */           headers.remove((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
/*     */         } else {
/* 118 */           headers.set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, targetContentEncoding);
/*     */         } 
/*     */         
/* 121 */         if (message instanceof HttpContent) {
/*     */           HttpMessage copy;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 127 */           if (message instanceof HttpRequest) {
/* 128 */             HttpRequest r = (HttpRequest)message;
/* 129 */             copy = new DefaultHttpRequest(r.protocolVersion(), r.method(), r.uri());
/* 130 */           } else if (message instanceof HttpResponse) {
/* 131 */             HttpResponse r = (HttpResponse)message;
/* 132 */             copy = new DefaultHttpResponse(r.protocolVersion(), r.status());
/*     */           } else {
/* 134 */             throw new CodecException("Object of class " + message.getClass().getName() + " is not an HttpRequest or HttpResponse");
/*     */           } 
/*     */           
/* 137 */           copy.headers().set(message.headers());
/* 138 */           copy.setDecoderResult(message.decoderResult());
/* 139 */           out.add(copy);
/*     */         } else {
/* 141 */           out.add(message);
/*     */         } 
/*     */       } 
/*     */       
/* 145 */       if (msg instanceof HttpContent) {
/* 146 */         HttpContent c = (HttpContent)msg;
/* 147 */         if (this.decoder == null) {
/* 148 */           out.add(c.retain());
/*     */         } else {
/* 150 */           decodeContent(c, out);
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 154 */       this.needRead = out.isEmpty();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void decodeContent(HttpContent c, List<Object> out) {
/* 159 */     ByteBuf content = c.content();
/*     */     
/* 161 */     decode(content, out);
/*     */     
/* 163 */     if (c instanceof LastHttpContent) {
/* 164 */       finishDecode(out);
/*     */       
/* 166 */       LastHttpContent last = (LastHttpContent)c;
/*     */ 
/*     */       
/* 169 */       HttpHeaders headers = last.trailingHeaders();
/* 170 */       if (headers.isEmpty()) {
/* 171 */         out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/*     */       } else {
/* 173 */         out.add(new ComposedLastHttpContent(headers, DecoderResult.SUCCESS));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 180 */     boolean needRead = this.needRead;
/* 181 */     this.needRead = true;
/*     */     
/*     */     try {
/* 184 */       ctx.fireChannelReadComplete();
/*     */     } finally {
/* 186 */       if (needRead && !ctx.channel().config().isAutoRead()) {
/* 187 */         ctx.read();
/*     */       }
/*     */     } 
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
/*     */   protected String getTargetContentEncoding(String contentEncoding) throws Exception {
/* 213 */     return IDENTITY;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 218 */     cleanupSafely(ctx);
/* 219 */     super.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 224 */     cleanupSafely(ctx);
/* 225 */     super.channelInactive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 230 */     this.ctx = ctx;
/* 231 */     super.handlerAdded(ctx);
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 235 */     if (this.decoder != null) {
/*     */       
/* 237 */       this.decoder.finishAndReleaseAll();
/* 238 */       this.decoder = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void cleanupSafely(ChannelHandlerContext ctx) {
/*     */     try {
/* 244 */       cleanup();
/* 245 */     } catch (Throwable cause) {
/*     */ 
/*     */       
/* 248 */       ctx.fireExceptionCaught(cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void decode(ByteBuf in, List<Object> out) {
/* 254 */     this.decoder.writeInbound(new Object[] { in.retain() });
/* 255 */     fetchDecoderOutput(out);
/*     */   }
/*     */   
/*     */   private void finishDecode(List<Object> out) {
/* 259 */     if (this.decoder.finish()) {
/* 260 */       fetchDecoderOutput(out);
/*     */     }
/* 262 */     this.decoder = null;
/*     */   }
/*     */   
/*     */   private void fetchDecoderOutput(List<Object> out) {
/*     */     while (true) {
/* 267 */       ByteBuf buf = (ByteBuf)this.decoder.readInbound();
/* 268 */       if (buf == null) {
/*     */         break;
/*     */       }
/* 271 */       if (!buf.isReadable()) {
/* 272 */         buf.release();
/*     */         continue;
/*     */       } 
/* 275 */       out.add(new DefaultHttpContent(buf));
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract EmbeddedChannel newContentDecoder(String paramString) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpContentDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */