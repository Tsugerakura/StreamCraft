/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketServerExtensionHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*     */   private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
/*     */   private List<WebSocketServerExtension> validExtensions;
/*     */   
/*     */   public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker... extensionHandshakers) {
/*  57 */     ObjectUtil.checkNotNull(extensionHandshakers, "extensionHandshakers");
/*  58 */     if (extensionHandshakers.length == 0) {
/*  59 */       throw new IllegalArgumentException("extensionHandshakers must contains at least one handshaker");
/*     */     }
/*  61 */     this.extensionHandshakers = Arrays.asList(extensionHandshakers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  67 */     if (msg instanceof HttpRequest) {
/*  68 */       HttpRequest request = (HttpRequest)msg;
/*     */       
/*  70 */       if (WebSocketExtensionUtil.isWebsocketUpgrade(request.headers())) {
/*  71 */         String extensionsHeader = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
/*     */         
/*  73 */         if (extensionsHeader != null) {
/*     */           
/*  75 */           List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
/*  76 */           int rsv = 0;
/*     */           
/*  78 */           for (WebSocketExtensionData extensionData : extensions) {
/*     */             
/*  80 */             Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
/*  81 */             WebSocketServerExtension validExtension = null;
/*     */             
/*  83 */             while (validExtension == null && extensionHandshakersIterator.hasNext()) {
/*     */               
/*  85 */               WebSocketServerExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
/*  86 */               validExtension = extensionHandshaker.handshakeExtension(extensionData);
/*     */             } 
/*     */             
/*  89 */             if (validExtension != null && (validExtension.rsv() & rsv) == 0) {
/*  90 */               if (this.validExtensions == null) {
/*  91 */                 this.validExtensions = new ArrayList<WebSocketServerExtension>(1);
/*     */               }
/*  93 */               rsv |= validExtension.rsv();
/*  94 */               this.validExtensions.add(validExtension);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     super.channelRead(ctx, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 106 */     if (msg instanceof HttpResponse && 
/* 107 */       WebSocketExtensionUtil.isWebsocketUpgrade(((HttpResponse)msg).headers()) && this.validExtensions != null) {
/* 108 */       HttpResponse response = (HttpResponse)msg;
/* 109 */       String headerValue = response.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
/*     */       
/* 111 */       for (WebSocketServerExtension extension : this.validExtensions) {
/* 112 */         WebSocketExtensionData extensionData = extension.newReponseData();
/* 113 */         headerValue = WebSocketExtensionUtil.appendExtension(headerValue, extensionData
/* 114 */             .name(), extensionData.parameters());
/*     */       } 
/*     */       
/* 117 */       promise.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */           {
/*     */             public void operationComplete(ChannelFuture future) throws Exception {
/* 120 */               if (future.isSuccess()) {
/* 121 */                 for (WebSocketServerExtension extension : WebSocketServerExtensionHandler.this.validExtensions) {
/* 122 */                   WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
/* 123 */                   WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
/* 124 */                   ctx.pipeline().addAfter(ctx.name(), decoder.getClass().getName(), (ChannelHandler)decoder);
/* 125 */                   ctx.pipeline().addAfter(ctx.name(), encoder.getClass().getName(), (ChannelHandler)encoder);
/*     */                 } 
/*     */               }
/*     */               
/* 129 */               ctx.pipeline().remove(ctx.name());
/*     */             }
/*     */           });
/*     */       
/* 133 */       if (headerValue != null) {
/* 134 */         response.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, headerValue);
/*     */       }
/*     */     } 
/*     */     
/* 138 */     super.write(ctx, msg, promise);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\WebSocketServerExtensionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */