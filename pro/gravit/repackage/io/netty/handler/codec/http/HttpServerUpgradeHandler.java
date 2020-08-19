/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServerUpgradeHandler
/*     */   extends HttpObjectAggregator
/*     */ {
/*     */   private final SourceCodec sourceCodec;
/*     */   private final UpgradeCodecFactory upgradeCodecFactory;
/*     */   private boolean handlingUpgrade;
/*     */   
/*     */   public static final class UpgradeEvent
/*     */     implements ReferenceCounted
/*     */   {
/*     */     private final CharSequence protocol;
/*     */     private final FullHttpRequest upgradeRequest;
/*     */     
/*     */     UpgradeEvent(CharSequence protocol, FullHttpRequest upgradeRequest) {
/* 107 */       this.protocol = protocol;
/* 108 */       this.upgradeRequest = upgradeRequest;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CharSequence protocol() {
/* 115 */       return this.protocol;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public FullHttpRequest upgradeRequest() {
/* 122 */       return this.upgradeRequest;
/*     */     }
/*     */ 
/*     */     
/*     */     public int refCnt() {
/* 127 */       return this.upgradeRequest.refCnt();
/*     */     }
/*     */ 
/*     */     
/*     */     public UpgradeEvent retain() {
/* 132 */       this.upgradeRequest.retain();
/* 133 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public UpgradeEvent retain(int increment) {
/* 138 */       this.upgradeRequest.retain(increment);
/* 139 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public UpgradeEvent touch() {
/* 144 */       this.upgradeRequest.touch();
/* 145 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public UpgradeEvent touch(Object hint) {
/* 150 */       this.upgradeRequest.touch(hint);
/* 151 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean release() {
/* 156 */       return this.upgradeRequest.release();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean release(int decrement) {
/* 161 */       return this.upgradeRequest.release(decrement);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 166 */       return "UpgradeEvent [protocol=" + this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
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
/*     */   public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory) {
/* 189 */     this(sourceCodec, upgradeCodecFactory, 0);
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
/*     */   public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory, int maxContentLength) {
/* 202 */     super(maxContentLength);
/*     */     
/* 204 */     this.sourceCodec = (SourceCodec)ObjectUtil.checkNotNull(sourceCodec, "sourceCodec");
/* 205 */     this.upgradeCodecFactory = (UpgradeCodecFactory)ObjectUtil.checkNotNull(upgradeCodecFactory, "upgradeCodecFactory");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
/*     */     FullHttpRequest fullRequest;
/* 212 */     this.handlingUpgrade |= isUpgradeRequest(msg);
/* 213 */     if (!this.handlingUpgrade) {
/*     */       
/* 215 */       ReferenceCountUtil.retain(msg);
/* 216 */       out.add(msg);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 221 */     if (msg instanceof FullHttpRequest) {
/* 222 */       fullRequest = (FullHttpRequest)msg;
/* 223 */       ReferenceCountUtil.retain(msg);
/* 224 */       out.add(msg);
/*     */     } else {
/*     */       
/* 227 */       super.decode(ctx, msg, out);
/* 228 */       if (out.isEmpty()) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 234 */       assert out.size() == 1;
/* 235 */       this.handlingUpgrade = false;
/* 236 */       fullRequest = (FullHttpRequest)out.get(0);
/*     */     } 
/*     */     
/* 239 */     if (upgrade(ctx, fullRequest))
/*     */     {
/*     */ 
/*     */       
/* 243 */       out.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isUpgradeRequest(HttpObject msg) {
/* 254 */     return (msg instanceof HttpRequest && ((HttpRequest)msg).headers().get((CharSequence)HttpHeaderNames.UPGRADE) != null);
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
/*     */   private boolean upgrade(ChannelHandlerContext ctx, FullHttpRequest request) {
/* 267 */     List<CharSequence> requestedProtocols = splitHeader(request.headers().get((CharSequence)HttpHeaderNames.UPGRADE));
/* 268 */     int numRequestedProtocols = requestedProtocols.size();
/* 269 */     UpgradeCodec upgradeCodec = null;
/* 270 */     CharSequence upgradeProtocol = null;
/* 271 */     for (int i = 0; i < numRequestedProtocols; i++) {
/* 272 */       CharSequence p = requestedProtocols.get(i);
/* 273 */       UpgradeCodec c = this.upgradeCodecFactory.newUpgradeCodec(p);
/* 274 */       if (c != null) {
/* 275 */         upgradeProtocol = p;
/* 276 */         upgradeCodec = c;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 281 */     if (upgradeCodec == null)
/*     */     {
/* 283 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 287 */     List<String> connectionHeaderValues = request.headers().getAll((CharSequence)HttpHeaderNames.CONNECTION);
/*     */     
/* 289 */     if (connectionHeaderValues == null) {
/* 290 */       return false;
/*     */     }
/*     */     
/* 293 */     StringBuilder concatenatedConnectionValue = new StringBuilder(connectionHeaderValues.size() * 10);
/* 294 */     for (CharSequence connectionHeaderValue : connectionHeaderValues) {
/* 295 */       concatenatedConnectionValue.append(connectionHeaderValue).append(',');
/*     */     }
/* 297 */     concatenatedConnectionValue.setLength(concatenatedConnectionValue.length() - 1);
/*     */ 
/*     */     
/* 300 */     Collection<CharSequence> requiredHeaders = upgradeCodec.requiredUpgradeHeaders();
/* 301 */     List<CharSequence> values = splitHeader(concatenatedConnectionValue);
/* 302 */     if (!AsciiString.containsContentEqualsIgnoreCase(values, (CharSequence)HttpHeaderNames.UPGRADE) || 
/* 303 */       !AsciiString.containsAllContentEqualsIgnoreCase(values, requiredHeaders)) {
/* 304 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 308 */     for (CharSequence requiredHeader : requiredHeaders) {
/* 309 */       if (!request.headers().contains(requiredHeader)) {
/* 310 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 316 */     FullHttpResponse upgradeResponse = createUpgradeResponse(upgradeProtocol);
/* 317 */     if (!upgradeCodec.prepareUpgradeResponse(ctx, request, upgradeResponse.headers())) {
/* 318 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 322 */     UpgradeEvent event = new UpgradeEvent(upgradeProtocol, request);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 329 */       ChannelFuture writeComplete = ctx.writeAndFlush(upgradeResponse);
/*     */       
/* 331 */       this.sourceCodec.upgradeFrom(ctx);
/* 332 */       upgradeCodec.upgradeTo(ctx, request);
/*     */ 
/*     */       
/* 335 */       ctx.pipeline().remove((ChannelHandler)this);
/*     */ 
/*     */ 
/*     */       
/* 339 */       ctx.fireUserEventTriggered(event.retain());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 344 */       writeComplete.addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
/*     */     } finally {
/*     */       
/* 347 */       event.release();
/*     */     } 
/* 349 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static FullHttpResponse createUpgradeResponse(CharSequence upgradeProtocol) {
/* 356 */     DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, false);
/*     */     
/* 358 */     res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
/* 359 */     res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, upgradeProtocol);
/* 360 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<CharSequence> splitHeader(CharSequence header) {
/* 368 */     StringBuilder builder = new StringBuilder(header.length());
/* 369 */     List<CharSequence> protocols = new ArrayList<CharSequence>(4);
/* 370 */     for (int i = 0; i < header.length(); i++) {
/* 371 */       char c = header.charAt(i);
/* 372 */       if (!Character.isWhitespace(c))
/*     */       {
/*     */ 
/*     */         
/* 376 */         if (c == ',') {
/*     */           
/* 378 */           protocols.add(builder.toString());
/* 379 */           builder.setLength(0);
/*     */         } else {
/* 381 */           builder.append(c);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 386 */     if (builder.length() > 0) {
/* 387 */       protocols.add(builder.toString());
/*     */     }
/*     */     
/* 390 */     return protocols;
/*     */   }
/*     */   
/*     */   public static interface UpgradeCodecFactory {
/*     */     HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence param1CharSequence);
/*     */   }
/*     */   
/*     */   public static interface UpgradeCodec {
/*     */     Collection<CharSequence> requiredUpgradeHeaders();
/*     */     
/*     */     boolean prepareUpgradeResponse(ChannelHandlerContext param1ChannelHandlerContext, FullHttpRequest param1FullHttpRequest, HttpHeaders param1HttpHeaders);
/*     */     
/*     */     void upgradeTo(ChannelHandlerContext param1ChannelHandlerContext, FullHttpRequest param1FullHttpRequest);
/*     */   }
/*     */   
/*     */   public static interface SourceCodec {
/*     */     void upgradeFrom(ChannelHandlerContext param1ChannelHandlerContext);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpServerUpgradeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */