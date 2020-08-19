/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpClientUpgradeHandler
/*     */   extends HttpObjectAggregator
/*     */   implements ChannelOutboundHandler
/*     */ {
/*     */   private final SourceCodec sourceCodec;
/*     */   private final UpgradeCodec upgradeCodec;
/*     */   private boolean upgradeRequested;
/*     */   
/*     */   public static interface UpgradeCodec
/*     */   {
/*     */     CharSequence protocol();
/*     */     
/*     */     Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext param1ChannelHandlerContext, HttpRequest param1HttpRequest);
/*     */     
/*     */     void upgradeTo(ChannelHandlerContext param1ChannelHandlerContext, FullHttpResponse param1FullHttpResponse) throws Exception;
/*     */   }
/*     */   
/*     */   public static interface SourceCodec
/*     */   {
/*     */     void prepareUpgradeFrom(ChannelHandlerContext param1ChannelHandlerContext);
/*     */     
/*     */     void upgradeFrom(ChannelHandlerContext param1ChannelHandlerContext);
/*     */   }
/*     */   
/*     */   public enum UpgradeEvent
/*     */   {
/*  48 */     UPGRADE_ISSUED,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  53 */     UPGRADE_SUCCESSFUL,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     UPGRADE_REJECTED;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClientUpgradeHandler(SourceCodec sourceCodec, UpgradeCodec upgradeCodec, int maxContentLength) {
/* 118 */     super(maxContentLength);
/* 119 */     this.sourceCodec = (SourceCodec)ObjectUtil.checkNotNull(sourceCodec, "sourceCodec");
/* 120 */     this.upgradeCodec = (UpgradeCodec)ObjectUtil.checkNotNull(upgradeCodec, "upgradeCodec");
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 125 */     ctx.bind(localAddress, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 131 */     ctx.connect(remoteAddress, localAddress, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 136 */     ctx.disconnect(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 141 */     ctx.close(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 146 */     ctx.deregister(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 151 */     ctx.read();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 157 */     if (!(msg instanceof HttpRequest)) {
/* 158 */       ctx.write(msg, promise);
/*     */       
/*     */       return;
/*     */     } 
/* 162 */     if (this.upgradeRequested) {
/* 163 */       promise.setFailure(new IllegalStateException("Attempting to write HTTP request with upgrade in progress"));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 168 */     this.upgradeRequested = true;
/* 169 */     setUpgradeRequestHeaders(ctx, (HttpRequest)msg);
/*     */ 
/*     */     
/* 172 */     ctx.write(msg, promise);
/*     */ 
/*     */     
/* 175 */     ctx.fireUserEventTriggered(UpgradeEvent.UPGRADE_ISSUED);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 181 */     ctx.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
/* 187 */     FullHttpResponse response = null;
/*     */     try {
/* 189 */       if (!this.upgradeRequested) {
/* 190 */         throw new IllegalStateException("Read HTTP response without requesting protocol switch");
/*     */       }
/*     */       
/* 193 */       if (msg instanceof HttpResponse) {
/* 194 */         HttpResponse rep = (HttpResponse)msg;
/* 195 */         if (!HttpResponseStatus.SWITCHING_PROTOCOLS.equals(rep.status())) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 200 */           ctx.fireUserEventTriggered(UpgradeEvent.UPGRADE_REJECTED);
/* 201 */           removeThisHandler(ctx);
/* 202 */           ctx.fireChannelRead(msg);
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 207 */       if (msg instanceof FullHttpResponse) {
/* 208 */         response = (FullHttpResponse)msg;
/*     */         
/* 210 */         response.retain();
/* 211 */         out.add(response);
/*     */       } else {
/*     */         
/* 214 */         super.decode(ctx, msg, out);
/* 215 */         if (out.isEmpty()) {
/*     */           return;
/*     */         }
/*     */ 
/*     */         
/* 220 */         assert out.size() == 1;
/* 221 */         response = (FullHttpResponse)out.get(0);
/*     */       } 
/*     */       
/* 224 */       CharSequence upgradeHeader = response.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
/* 225 */       if (upgradeHeader != null && !AsciiString.contentEqualsIgnoreCase(this.upgradeCodec.protocol(), upgradeHeader)) {
/* 226 */         throw new IllegalStateException("Switching Protocols response with unexpected UPGRADE protocol: " + upgradeHeader);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 231 */       this.sourceCodec.prepareUpgradeFrom(ctx);
/* 232 */       this.upgradeCodec.upgradeTo(ctx, response);
/*     */ 
/*     */       
/* 235 */       ctx.fireUserEventTriggered(UpgradeEvent.UPGRADE_SUCCESSFUL);
/*     */ 
/*     */ 
/*     */       
/* 239 */       this.sourceCodec.upgradeFrom(ctx);
/*     */ 
/*     */ 
/*     */       
/* 243 */       response.release();
/* 244 */       out.clear();
/* 245 */       removeThisHandler(ctx);
/* 246 */     } catch (Throwable t) {
/* 247 */       ReferenceCountUtil.release(response);
/* 248 */       ctx.fireExceptionCaught(t);
/* 249 */       removeThisHandler(ctx);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void removeThisHandler(ChannelHandlerContext ctx) {
/* 254 */     ctx.pipeline().remove(ctx.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setUpgradeRequestHeaders(ChannelHandlerContext ctx, HttpRequest request) {
/* 262 */     request.headers().set((CharSequence)HttpHeaderNames.UPGRADE, this.upgradeCodec.protocol());
/*     */ 
/*     */     
/* 265 */     Set<CharSequence> connectionParts = new LinkedHashSet<CharSequence>(2);
/* 266 */     connectionParts.addAll(this.upgradeCodec.setUpgradeHeaders(ctx, request));
/*     */ 
/*     */     
/* 269 */     StringBuilder builder = new StringBuilder();
/* 270 */     for (CharSequence part : connectionParts) {
/* 271 */       builder.append(part);
/* 272 */       builder.append(',');
/*     */     } 
/* 274 */     builder.append((CharSequence)HttpHeaderValues.UPGRADE);
/* 275 */     request.headers().add((CharSequence)HttpHeaderNames.CONNECTION, builder.toString());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpClientUpgradeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */