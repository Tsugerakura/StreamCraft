/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptionalSslHandler
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private final SslContext sslContext;
/*     */   
/*     */   public OptionalSslHandler(SslContext sslContext) {
/*  39 */     this.sslContext = (SslContext)ObjectUtil.checkNotNull(sslContext, "sslContext");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
/*  44 */     if (in.readableBytes() < 5) {
/*     */       return;
/*     */     }
/*  47 */     if (SslHandler.isEncrypted(in)) {
/*  48 */       handleSsl(context);
/*     */     } else {
/*  50 */       handleNonSsl(context);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleSsl(ChannelHandlerContext context) {
/*  55 */     SslHandler sslHandler = null;
/*     */     try {
/*  57 */       sslHandler = newSslHandler(context, this.sslContext);
/*  58 */       context.pipeline().replace((ChannelHandler)this, newSslHandlerName(), (ChannelHandler)sslHandler);
/*  59 */       sslHandler = null;
/*     */     }
/*     */     finally {
/*     */       
/*  63 */       if (sslHandler != null) {
/*  64 */         ReferenceCountUtil.safeRelease(sslHandler.engine());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleNonSsl(ChannelHandlerContext context) {
/*  70 */     ChannelHandler handler = newNonSslHandler(context);
/*  71 */     if (handler != null) {
/*  72 */       context.pipeline().replace((ChannelHandler)this, newNonSslHandlerName(), handler);
/*     */     } else {
/*  74 */       context.pipeline().remove((ChannelHandler)this);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String newSslHandlerName() {
/*  83 */     return null;
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
/*     */   protected SslHandler newSslHandler(ChannelHandlerContext context, SslContext sslContext) {
/*  97 */     return sslContext.newHandler(context.alloc());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String newNonSslHandlerName() {
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ChannelHandler newNonSslHandler(ChannelHandlerContext context) {
/* 115 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OptionalSslHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */