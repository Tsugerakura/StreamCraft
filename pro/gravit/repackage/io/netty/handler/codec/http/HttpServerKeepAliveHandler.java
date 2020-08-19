/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServerKeepAliveHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*     */   private static final String MULTIPART_PREFIX = "multipart";
/*     */   private boolean persistentConnection = true;
/*     */   private int pendingResponses;
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  57 */     if (msg instanceof HttpRequest) {
/*  58 */       HttpRequest request = (HttpRequest)msg;
/*  59 */       if (this.persistentConnection) {
/*  60 */         this.pendingResponses++;
/*  61 */         this.persistentConnection = HttpUtil.isKeepAlive(request);
/*     */       } 
/*     */     } 
/*  64 */     super.channelRead(ctx, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/*  70 */     if (msg instanceof HttpResponse) {
/*  71 */       HttpResponse response = (HttpResponse)msg;
/*  72 */       trackResponse(response);
/*     */       
/*  74 */       if (!HttpUtil.isKeepAlive(response) || !isSelfDefinedMessageLength(response)) {
/*     */         
/*  76 */         this.pendingResponses = 0;
/*  77 */         this.persistentConnection = false;
/*     */       } 
/*     */       
/*  80 */       if (!shouldKeepAlive()) {
/*  81 */         HttpUtil.setKeepAlive(response, false);
/*     */       }
/*     */     } 
/*  84 */     if (msg instanceof LastHttpContent && !shouldKeepAlive()) {
/*  85 */       promise = promise.unvoid().addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*     */     }
/*  87 */     super.write(ctx, msg, promise);
/*     */   }
/*     */   
/*     */   private void trackResponse(HttpResponse response) {
/*  91 */     if (!isInformational(response)) {
/*  92 */       this.pendingResponses--;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean shouldKeepAlive() {
/*  97 */     return (this.pendingResponses != 0 || this.persistentConnection);
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
/*     */   private static boolean isSelfDefinedMessageLength(HttpResponse response) {
/* 115 */     return (HttpUtil.isContentLengthSet(response) || HttpUtil.isTransferEncodingChunked(response) || isMultipart(response) || 
/* 116 */       isInformational(response) || response.status().code() == HttpResponseStatus.NO_CONTENT.code());
/*     */   }
/*     */   
/*     */   private static boolean isInformational(HttpResponse response) {
/* 120 */     return (response.status().codeClass() == HttpStatusClass.INFORMATIONAL);
/*     */   }
/*     */   
/*     */   private static boolean isMultipart(HttpResponse response) {
/* 124 */     String contentType = response.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
/* 125 */     return (contentType != null && contentType
/* 126 */       .regionMatches(true, 0, "multipart", 0, "multipart".length()));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpServerKeepAliveHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */