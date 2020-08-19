/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.PromiseCombiner;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.TypeParameterMatcher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MessageToMessageEncoder<I>
/*     */   extends ChannelOutboundHandlerAdapter
/*     */ {
/*     */   private final TypeParameterMatcher matcher;
/*     */   
/*     */   protected MessageToMessageEncoder() {
/*  60 */     this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
/*  69 */     this.matcher = TypeParameterMatcher.get(outboundMessageType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/*  77 */     return this.matcher.match(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/*  82 */     CodecOutputList out = null;
/*     */     try {
/*  84 */       if (acceptOutboundMessage(msg)) {
/*  85 */         out = CodecOutputList.newInstance();
/*     */         
/*  87 */         I cast = (I)msg;
/*     */         try {
/*  89 */           encode(ctx, cast, out);
/*     */         } finally {
/*  91 */           ReferenceCountUtil.release(cast);
/*     */         } 
/*     */         
/*  94 */         if (out.isEmpty()) {
/*  95 */           out.recycle();
/*  96 */           out = null;
/*     */           
/*  98 */           throw new EncoderException(
/*  99 */               StringUtil.simpleClassName(this) + " must produce at least one message.");
/*     */         } 
/*     */       } else {
/* 102 */         ctx.write(msg, promise);
/*     */       } 
/* 104 */     } catch (EncoderException e) {
/* 105 */       throw e;
/* 106 */     } catch (Throwable t) {
/* 107 */       throw new EncoderException(t);
/*     */     } finally {
/* 109 */       if (out != null) {
/* 110 */         int sizeMinusOne = out.size() - 1;
/* 111 */         if (sizeMinusOne == 0) {
/* 112 */           ctx.write(out.getUnsafe(0), promise);
/* 113 */         } else if (sizeMinusOne > 0) {
/*     */ 
/*     */           
/* 116 */           if (promise == ctx.voidPromise()) {
/* 117 */             writeVoidPromise(ctx, out);
/*     */           } else {
/* 119 */             writePromiseCombiner(ctx, out, promise);
/*     */           } 
/*     */         } 
/* 122 */         out.recycle();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeVoidPromise(ChannelHandlerContext ctx, CodecOutputList out) {
/* 128 */     ChannelPromise voidPromise = ctx.voidPromise();
/* 129 */     for (int i = 0; i < out.size(); i++) {
/* 130 */       ctx.write(out.getUnsafe(i), voidPromise);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void writePromiseCombiner(ChannelHandlerContext ctx, CodecOutputList out, ChannelPromise promise) {
/* 135 */     PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
/* 136 */     for (int i = 0; i < out.size(); i++) {
/* 137 */       combiner.add((Future)ctx.write(out.getUnsafe(i)));
/*     */     }
/* 139 */     combiner.finish((Promise)promise);
/*     */   }
/*     */   
/*     */   protected abstract void encode(ChannelHandlerContext paramChannelHandlerContext, I paramI, List<Object> paramList) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\MessageToMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */