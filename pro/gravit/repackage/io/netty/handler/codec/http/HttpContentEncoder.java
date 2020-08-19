/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.embedded.EmbeddedChannel;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageCodec;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpContentEncoder
/*     */   extends MessageToMessageCodec<HttpRequest, HttpObject>
/*     */ {
/*     */   private enum State
/*     */   {
/*  59 */     PASS_THROUGH,
/*  60 */     AWAIT_HEADERS,
/*  61 */     AWAIT_CONTENT;
/*     */   }
/*     */   
/*  64 */   private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
/*  65 */   private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
/*  66 */   private static final int CONTINUE_CODE = HttpResponseStatus.CONTINUE.code();
/*     */   
/*  68 */   private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque<CharSequence>();
/*     */   private EmbeddedChannel encoder;
/*  70 */   private State state = State.AWAIT_HEADERS;
/*     */ 
/*     */   
/*     */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/*  74 */     return (msg instanceof HttpContent || msg instanceof HttpResponse);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
/*     */     CharSequence acceptEncoding;
/*  80 */     List<String> acceptEncodingHeaders = msg.headers().getAll((CharSequence)HttpHeaderNames.ACCEPT_ENCODING);
/*  81 */     switch (acceptEncodingHeaders.size()) {
/*     */       case 0:
/*  83 */         acceptEncoding = HttpContentDecoder.IDENTITY;
/*     */         break;
/*     */       case 1:
/*  86 */         acceptEncoding = acceptEncodingHeaders.get(0);
/*     */         break;
/*     */       
/*     */       default:
/*  90 */         acceptEncoding = StringUtil.join(",", acceptEncodingHeaders);
/*     */         break;
/*     */     } 
/*     */     
/*  94 */     HttpMethod method = msg.method();
/*  95 */     if (HttpMethod.HEAD.equals(method)) {
/*  96 */       acceptEncoding = ZERO_LENGTH_HEAD;
/*  97 */     } else if (HttpMethod.CONNECT.equals(method)) {
/*  98 */       acceptEncoding = ZERO_LENGTH_CONNECT;
/*     */     } 
/*     */     
/* 101 */     this.acceptEncodingQueue.add(acceptEncoding);
/* 102 */     out.add(ReferenceCountUtil.retain(msg)); } protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
/*     */     HttpResponse res;
/*     */     int code;
/*     */     CharSequence acceptEncoding;
/*     */     Result result;
/* 107 */     boolean isFull = (msg instanceof HttpResponse && msg instanceof LastHttpContent);
/* 108 */     switch (this.state) {
/*     */       case AWAIT_HEADERS:
/* 110 */         ensureHeaders(msg);
/* 111 */         assert this.encoder == null;
/*     */         
/* 113 */         res = (HttpResponse)msg;
/* 114 */         code = res.status().code();
/*     */         
/* 116 */         if (code == CONTINUE_CODE) {
/*     */ 
/*     */           
/* 119 */           acceptEncoding = null;
/*     */         } else {
/*     */           
/* 122 */           acceptEncoding = this.acceptEncodingQueue.poll();
/* 123 */           if (acceptEncoding == null) {
/* 124 */             throw new IllegalStateException("cannot send more responses than requests");
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 141 */         if (isPassthru(res.protocolVersion(), code, acceptEncoding)) {
/* 142 */           if (isFull) {
/* 143 */             out.add(ReferenceCountUtil.retain(res)); break;
/*     */           } 
/* 145 */           out.add(res);
/*     */           
/* 147 */           this.state = State.PASS_THROUGH;
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 152 */         if (isFull)
/*     */         {
/* 154 */           if (!((ByteBufHolder)res).content().isReadable()) {
/* 155 */             out.add(ReferenceCountUtil.retain(res));
/*     */             
/*     */             break;
/*     */           } 
/*     */         }
/*     */         
/* 161 */         result = beginEncode(res, acceptEncoding.toString());
/*     */ 
/*     */         
/* 164 */         if (result == null) {
/* 165 */           if (isFull) {
/* 166 */             out.add(ReferenceCountUtil.retain(res)); break;
/*     */           } 
/* 168 */           out.add(res);
/*     */           
/* 170 */           this.state = State.PASS_THROUGH;
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 175 */         this.encoder = result.contentEncoder();
/*     */ 
/*     */ 
/*     */         
/* 179 */         res.headers().set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, result.targetContentEncoding());
/*     */ 
/*     */         
/* 182 */         if (isFull) {
/*     */           
/* 184 */           HttpResponse newRes = new DefaultHttpResponse(res.protocolVersion(), res.status());
/* 185 */           newRes.headers().set(res.headers());
/* 186 */           out.add(newRes);
/*     */           
/* 188 */           ensureContent(res);
/* 189 */           encodeFullResponse(newRes, (HttpContent)res, out);
/*     */           
/*     */           break;
/*     */         } 
/* 193 */         res.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/* 194 */         res.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
/*     */         
/* 196 */         out.add(res);
/* 197 */         this.state = State.AWAIT_CONTENT;
/* 198 */         if (!(msg instanceof HttpContent)) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case AWAIT_CONTENT:
/* 207 */         ensureContent(msg);
/* 208 */         if (encodeContent((HttpContent)msg, out)) {
/* 209 */           this.state = State.AWAIT_HEADERS;
/*     */         }
/*     */         break;
/*     */       
/*     */       case PASS_THROUGH:
/* 214 */         ensureContent(msg);
/* 215 */         out.add(ReferenceCountUtil.retain(msg));
/*     */         
/* 217 */         if (msg instanceof LastHttpContent) {
/* 218 */           this.state = State.AWAIT_HEADERS;
/*     */         }
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void encodeFullResponse(HttpResponse newRes, HttpContent content, List<Object> out) {
/* 226 */     int existingMessages = out.size();
/* 227 */     encodeContent(content, out);
/*     */     
/* 229 */     if (HttpUtil.isContentLengthSet(newRes)) {
/*     */       
/* 231 */       int messageSize = 0;
/* 232 */       for (int i = existingMessages; i < out.size(); i++) {
/* 233 */         Object item = out.get(i);
/* 234 */         if (item instanceof HttpContent) {
/* 235 */           messageSize += ((HttpContent)item).content().readableBytes();
/*     */         }
/*     */       } 
/* 238 */       HttpUtil.setContentLength(newRes, messageSize);
/*     */     } else {
/* 240 */       newRes.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isPassthru(HttpVersion version, int code, CharSequence httpMethod) {
/* 245 */     return (code < 200 || code == 204 || code == 304 || httpMethod == ZERO_LENGTH_HEAD || (httpMethod == ZERO_LENGTH_CONNECT && code == 200) || version == HttpVersion.HTTP_1_0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void ensureHeaders(HttpObject msg) {
/* 251 */     if (!(msg instanceof HttpResponse)) {
/* 252 */       throw new IllegalStateException("unexpected message type: " + msg
/*     */           
/* 254 */           .getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
/*     */     }
/*     */   }
/*     */   
/*     */   private static void ensureContent(HttpObject msg) {
/* 259 */     if (!(msg instanceof HttpContent)) {
/* 260 */       throw new IllegalStateException("unexpected message type: " + msg
/*     */           
/* 262 */           .getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean encodeContent(HttpContent c, List<Object> out) {
/* 267 */     ByteBuf content = c.content();
/*     */     
/* 269 */     encode(content, out);
/*     */     
/* 271 */     if (c instanceof LastHttpContent) {
/* 272 */       finishEncode(out);
/* 273 */       LastHttpContent last = (LastHttpContent)c;
/*     */ 
/*     */ 
/*     */       
/* 277 */       HttpHeaders headers = last.trailingHeaders();
/* 278 */       if (headers.isEmpty()) {
/* 279 */         out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/*     */       } else {
/* 281 */         out.add(new ComposedLastHttpContent(headers, DecoderResult.SUCCESS));
/*     */       } 
/* 283 */       return true;
/*     */     } 
/* 285 */     return false;
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
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 306 */     cleanupSafely(ctx);
/* 307 */     super.handlerRemoved(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 312 */     cleanupSafely(ctx);
/* 313 */     super.channelInactive(ctx);
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 317 */     if (this.encoder != null) {
/*     */       
/* 319 */       this.encoder.finishAndReleaseAll();
/* 320 */       this.encoder = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void cleanupSafely(ChannelHandlerContext ctx) {
/*     */     try {
/* 326 */       cleanup();
/* 327 */     } catch (Throwable cause) {
/*     */ 
/*     */       
/* 330 */       ctx.fireExceptionCaught(cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void encode(ByteBuf in, List<Object> out) {
/* 336 */     this.encoder.writeOutbound(new Object[] { in.retain() });
/* 337 */     fetchEncoderOutput(out);
/*     */   }
/*     */   
/*     */   private void finishEncode(List<Object> out) {
/* 341 */     if (this.encoder.finish()) {
/* 342 */       fetchEncoderOutput(out);
/*     */     }
/* 344 */     this.encoder = null;
/*     */   }
/*     */   
/*     */   private void fetchEncoderOutput(List<Object> out) {
/*     */     while (true) {
/* 349 */       ByteBuf buf = (ByteBuf)this.encoder.readOutbound();
/* 350 */       if (buf == null) {
/*     */         break;
/*     */       }
/* 353 */       if (!buf.isReadable()) {
/* 354 */         buf.release();
/*     */         continue;
/*     */       } 
/* 357 */       out.add(new DefaultHttpContent(buf));
/*     */     } 
/*     */   }
/*     */   protected abstract Result beginEncode(HttpResponse paramHttpResponse, String paramString) throws Exception;
/*     */   
/*     */   public static final class Result { private final String targetContentEncoding;
/*     */     private final EmbeddedChannel contentEncoder;
/*     */     
/*     */     public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
/* 366 */       this.targetContentEncoding = (String)ObjectUtil.checkNotNull(targetContentEncoding, "targetContentEncoding");
/* 367 */       this.contentEncoder = (EmbeddedChannel)ObjectUtil.checkNotNull(contentEncoder, "contentEncoder");
/*     */     }
/*     */     
/*     */     public String targetContentEncoding() {
/* 371 */       return this.targetContentEncoding;
/*     */     }
/*     */     
/*     */     public EmbeddedChannel contentEncoder() {
/* 375 */       return this.contentEncoder;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpContentEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */