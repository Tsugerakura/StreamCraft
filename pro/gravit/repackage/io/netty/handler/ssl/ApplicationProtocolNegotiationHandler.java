/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ApplicationProtocolNegotiationHandler
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*  65 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String fallbackProtocol;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ApplicationProtocolNegotiationHandler(String fallbackProtocol) {
/*  76 */     this.fallbackProtocol = (String)ObjectUtil.checkNotNull(fallbackProtocol, "fallbackProtocol");
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/*  81 */     if (evt instanceof SslHandshakeCompletionEvent) {
/*     */       
/*     */       try {
/*  84 */         SslHandshakeCompletionEvent handshakeEvent = (SslHandshakeCompletionEvent)evt;
/*  85 */         if (handshakeEvent.isSuccess()) {
/*  86 */           SslHandler sslHandler = (SslHandler)ctx.pipeline().get(SslHandler.class);
/*  87 */           if (sslHandler == null) {
/*  88 */             throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)");
/*     */           }
/*     */           
/*  91 */           String protocol = sslHandler.applicationProtocol();
/*  92 */           configurePipeline(ctx, (protocol != null) ? protocol : this.fallbackProtocol);
/*     */         } else {
/*  94 */           handshakeFailure(ctx, handshakeEvent.cause());
/*     */         } 
/*  96 */       } catch (Throwable cause) {
/*  97 */         exceptionCaught(ctx, cause);
/*     */       } finally {
/*  99 */         ChannelPipeline pipeline = ctx.pipeline();
/* 100 */         if (pipeline.context((ChannelHandler)this) != null) {
/* 101 */           pipeline.remove((ChannelHandler)this);
/*     */         }
/*     */       } 
/*     */     }
/* 105 */     ctx.fireUserEventTriggered(evt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void configurePipeline(ChannelHandlerContext paramChannelHandlerContext, String paramString) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handshakeFailure(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 122 */     logger.warn("{} TLS handshake failed:", ctx.channel(), cause);
/* 123 */     ctx.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 128 */     logger.warn("{} Failed to select the application-level protocol:", ctx.channel(), cause);
/* 129 */     ctx.fireExceptionCaught(cause);
/* 130 */     ctx.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ApplicationProtocolNegotiationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */