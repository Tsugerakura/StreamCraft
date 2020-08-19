/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CodecException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
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
/*     */ 
/*     */ public class WebSocketClientExtensionHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*     */   private final List<WebSocketClientExtensionHandshaker> extensionHandshakers;
/*     */   
/*     */   public WebSocketClientExtensionHandler(WebSocketClientExtensionHandshaker... extensionHandshakers) {
/*  54 */     ObjectUtil.checkNotNull(extensionHandshakers, "extensionHandshakers");
/*  55 */     if (extensionHandshakers.length == 0) {
/*  56 */       throw new IllegalArgumentException("extensionHandshakers must contains at least one handshaker");
/*     */     }
/*  58 */     this.extensionHandshakers = Arrays.asList(extensionHandshakers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/*  63 */     if (msg instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpRequest)msg).headers())) {
/*  64 */       HttpRequest request = (HttpRequest)msg;
/*  65 */       String headerValue = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
/*     */       
/*  67 */       for (WebSocketClientExtensionHandshaker extensionHandshaker : this.extensionHandshakers) {
/*  68 */         WebSocketExtensionData extensionData = extensionHandshaker.newRequestData();
/*  69 */         headerValue = WebSocketExtensionUtil.appendExtension(headerValue, extensionData
/*  70 */             .name(), extensionData.parameters());
/*     */       } 
/*     */       
/*  73 */       request.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, headerValue);
/*     */     } 
/*     */     
/*  76 */     super.write(ctx, msg, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/*  82 */     if (msg instanceof HttpResponse) {
/*  83 */       HttpResponse response = (HttpResponse)msg;
/*     */       
/*  85 */       if (WebSocketExtensionUtil.isWebsocketUpgrade(response.headers())) {
/*  86 */         String extensionsHeader = response.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
/*     */         
/*  88 */         if (extensionsHeader != null) {
/*     */           
/*  90 */           List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
/*     */           
/*  92 */           List<WebSocketClientExtension> validExtensions = new ArrayList<WebSocketClientExtension>(extensions.size());
/*  93 */           int rsv = 0;
/*     */           
/*  95 */           for (WebSocketExtensionData extensionData : extensions) {
/*     */             
/*  97 */             Iterator<WebSocketClientExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
/*  98 */             WebSocketClientExtension validExtension = null;
/*     */             
/* 100 */             while (validExtension == null && extensionHandshakersIterator.hasNext()) {
/*     */               
/* 102 */               WebSocketClientExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
/* 103 */               validExtension = extensionHandshaker.handshakeExtension(extensionData);
/*     */             } 
/*     */             
/* 106 */             if (validExtension != null && (validExtension.rsv() & rsv) == 0) {
/* 107 */               rsv |= validExtension.rsv();
/* 108 */               validExtensions.add(validExtension); continue;
/*     */             } 
/* 110 */             throw new CodecException("invalid WebSocket Extension handshake for \"" + extensionsHeader + '"');
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 115 */           for (WebSocketClientExtension validExtension : validExtensions) {
/* 116 */             WebSocketExtensionDecoder decoder = validExtension.newExtensionDecoder();
/* 117 */             WebSocketExtensionEncoder encoder = validExtension.newExtensionEncoder();
/* 118 */             ctx.pipeline().addAfter(ctx.name(), decoder.getClass().getName(), (ChannelHandler)decoder);
/* 119 */             ctx.pipeline().addAfter(ctx.name(), encoder.getClass().getName(), (ChannelHandler)encoder);
/*     */           } 
/*     */         } 
/*     */         
/* 123 */         ctx.pipeline().remove(ctx.name());
/*     */       } 
/*     */     } 
/*     */     
/* 127 */     super.channelRead(ctx, msg);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\WebSocketClientExtensionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */