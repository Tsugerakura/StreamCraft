/*    */ package pro.gravit.repackage.io.netty.handler.ssl.ocsp;
/*    */ 
/*    */ import javax.net.ssl.SSLHandshakeException;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*    */ import pro.gravit.repackage.io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
/*    */ import pro.gravit.repackage.io.netty.handler.ssl.SslHandshakeCompletionEvent;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class OcspClientHandler
/*    */   extends ChannelInboundHandlerAdapter
/*    */ {
/*    */   private final ReferenceCountedOpenSslEngine engine;
/*    */   
/*    */   protected OcspClientHandler(ReferenceCountedOpenSslEngine engine) {
/* 40 */     this.engine = (ReferenceCountedOpenSslEngine)ObjectUtil.checkNotNull(engine, "engine");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract boolean verify(ChannelHandlerContext paramChannelHandlerContext, ReferenceCountedOpenSslEngine paramReferenceCountedOpenSslEngine) throws Exception;
/*    */ 
/*    */ 
/*    */   
/*    */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 50 */     if (evt instanceof SslHandshakeCompletionEvent) {
/* 51 */       ctx.pipeline().remove((ChannelHandler)this);
/*    */       
/* 53 */       SslHandshakeCompletionEvent event = (SslHandshakeCompletionEvent)evt;
/* 54 */       if (event.isSuccess() && !verify(ctx, this.engine)) {
/* 55 */         throw new SSLHandshakeException("Bad OCSP response");
/*    */       }
/*    */     } 
/*    */     
/* 59 */     ctx.fireUserEventTriggered(evt);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ocsp\OcspClientHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */