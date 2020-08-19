/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*    */ public class Utf8FrameValidator
/*    */   extends ChannelInboundHandlerAdapter
/*    */ {
/*    */   private int fragmentedFramesCount;
/*    */   private Utf8Validator utf8Validator;
/*    */   
/*    */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 35 */     if (msg instanceof WebSocketFrame) {
/* 36 */       WebSocketFrame frame = (WebSocketFrame)msg;
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 41 */         if (((WebSocketFrame)msg).isFinalFragment()) {
/*    */ 
/*    */           
/* 44 */           if (!(frame instanceof PingWebSocketFrame)) {
/* 45 */             this.fragmentedFramesCount = 0;
/*    */ 
/*    */             
/* 48 */             if (frame instanceof TextWebSocketFrame || (this.utf8Validator != null && this.utf8Validator
/* 49 */               .isChecking()))
/*    */             {
/* 51 */               checkUTF8String(frame.content());
/*    */ 
/*    */ 
/*    */               
/* 55 */               this.utf8Validator.finish();
/*    */             }
/*    */           
/*    */           } 
/*    */         } else {
/*    */           
/* 61 */           if (this.fragmentedFramesCount == 0) {
/*    */             
/* 63 */             if (frame instanceof TextWebSocketFrame) {
/* 64 */               checkUTF8String(frame.content());
/*    */             
/*    */             }
/*    */           }
/* 68 */           else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
/* 69 */             checkUTF8String(frame.content());
/*    */           } 
/*    */ 
/*    */ 
/*    */           
/* 74 */           this.fragmentedFramesCount++;
/*    */         } 
/* 76 */       } catch (CorruptedWebSocketFrameException e) {
/* 77 */         frame.release();
/* 78 */         throw e;
/*    */       } 
/*    */     } 
/*    */     
/* 82 */     super.channelRead(ctx, msg);
/*    */   }
/*    */   
/*    */   private void checkUTF8String(ByteBuf buffer) {
/* 86 */     if (this.utf8Validator == null) {
/* 87 */       this.utf8Validator = new Utf8Validator();
/*    */     }
/* 89 */     this.utf8Validator.check(buffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 94 */     if (cause instanceof pro.gravit.repackage.io.netty.handler.codec.CorruptedFrameException && ctx.channel().isOpen()) {
/* 95 */       ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
/*    */     }
/* 97 */     super.exceptionCaught(ctx, cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\Utf8FrameValidator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */