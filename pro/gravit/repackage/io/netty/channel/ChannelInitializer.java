/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ @Sharable
/*     */ public abstract class ChannelInitializer<C extends Channel>
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*  56 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
/*     */ 
/*     */   
/*  59 */   private final Set<ChannelHandlerContext> initMap = Collections.newSetFromMap(new ConcurrentHashMap<ChannelHandlerContext, Boolean>());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/*  78 */     if (initChannel(ctx)) {
/*     */ 
/*     */       
/*  81 */       ctx.pipeline().fireChannelRegistered();
/*     */ 
/*     */       
/*  84 */       removeState(ctx);
/*     */     } else {
/*     */       
/*  87 */       ctx.fireChannelRegistered();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/*  96 */     if (logger.isWarnEnabled()) {
/*  97 */       logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
/*     */     }
/*  99 */     ctx.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 107 */     if (ctx.channel().isRegistered())
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 112 */       if (initChannel(ctx))
/*     */       {
/*     */         
/* 115 */         removeState(ctx);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 122 */     this.initMap.remove(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
/* 127 */     if (this.initMap.add(ctx)) {
/*     */       try {
/* 129 */         initChannel((C)ctx.channel());
/* 130 */       } catch (Throwable cause) {
/*     */ 
/*     */         
/* 133 */         exceptionCaught(ctx, cause);
/*     */       } finally {
/* 135 */         ChannelPipeline pipeline = ctx.pipeline();
/* 136 */         if (pipeline.context(this) != null) {
/* 137 */           pipeline.remove(this);
/*     */         }
/*     */       } 
/* 140 */       return true;
/*     */     } 
/* 142 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeState(final ChannelHandlerContext ctx) {
/* 147 */     if (ctx.isRemoved()) {
/* 148 */       this.initMap.remove(ctx);
/*     */     }
/*     */     else {
/*     */       
/* 152 */       ctx.executor().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 155 */               ChannelInitializer.this.initMap.remove(ctx);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract void initChannel(C paramC) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */