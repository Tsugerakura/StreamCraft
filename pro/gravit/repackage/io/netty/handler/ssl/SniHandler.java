/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderException;
/*     */ import pro.gravit.repackage.io.netty.util.AsyncMapping;
/*     */ import pro.gravit.repackage.io.netty.util.DomainNameMapping;
/*     */ import pro.gravit.repackage.io.netty.util.Mapping;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SniHandler
/*     */   extends AbstractSniHandler<SslContext>
/*     */ {
/*  38 */   private static final Selection EMPTY_SELECTION = new Selection(null, null);
/*     */   
/*     */   protected final AsyncMapping<String, SslContext> mapping;
/*     */   
/*  42 */   private volatile Selection selection = EMPTY_SELECTION;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SniHandler(Mapping<? super String, ? extends SslContext> mapping) {
/*  51 */     this(new AsyncMappingAdapter(mapping, null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SniHandler(DomainNameMapping<? extends SslContext> mapping) {
/*  61 */     this((Mapping)mapping);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping) {
/*  72 */     this.mapping = (AsyncMapping<String, SslContext>)ObjectUtil.checkNotNull(mapping, "mapping");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String hostname() {
/*  79 */     return this.selection.hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SslContext sslContext() {
/*  86 */     return this.selection.context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname) throws Exception {
/*  97 */     return this.mapping.map(hostname, ctx.executor().newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void onLookupComplete(ChannelHandlerContext ctx, String hostname, Future<SslContext> future) throws Exception {
/* 103 */     if (!future.isSuccess()) {
/* 104 */       Throwable cause = future.cause();
/* 105 */       if (cause instanceof Error) {
/* 106 */         throw (Error)cause;
/*     */       }
/* 108 */       throw new DecoderException("failed to get the SslContext for " + hostname, cause);
/*     */     } 
/*     */     
/* 111 */     SslContext sslContext = (SslContext)future.getNow();
/* 112 */     this.selection = new Selection(sslContext, hostname);
/*     */     try {
/* 114 */       replaceHandler(ctx, hostname, sslContext);
/* 115 */     } catch (Throwable cause) {
/* 116 */       this.selection = EMPTY_SELECTION;
/* 117 */       PlatformDependent.throwException(cause);
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
/*     */   protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext) throws Exception {
/* 131 */     SslHandler sslHandler = null;
/*     */     try {
/* 133 */       sslHandler = newSslHandler(sslContext, ctx.alloc());
/* 134 */       ctx.pipeline().replace((ChannelHandler)this, SslHandler.class.getName(), (ChannelHandler)sslHandler);
/* 135 */       sslHandler = null;
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 140 */       if (sslHandler != null) {
/* 141 */         ReferenceCountUtil.safeRelease(sslHandler.engine());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SslHandler newSslHandler(SslContext context, ByteBufAllocator allocator) {
/* 151 */     return context.newHandler(allocator);
/*     */   }
/*     */   
/*     */   private static final class AsyncMappingAdapter implements AsyncMapping<String, SslContext> {
/*     */     private final Mapping<? super String, ? extends SslContext> mapping;
/*     */     
/*     */     private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> mapping) {
/* 158 */       this.mapping = (Mapping<? super String, ? extends SslContext>)ObjectUtil.checkNotNull(mapping, "mapping");
/*     */     }
/*     */ 
/*     */     
/*     */     public Future<SslContext> map(String input, Promise<SslContext> promise) {
/*     */       SslContext context;
/*     */       try {
/* 165 */         context = (SslContext)this.mapping.map(input);
/* 166 */       } catch (Throwable cause) {
/* 167 */         return (Future<SslContext>)promise.setFailure(cause);
/*     */       } 
/* 169 */       return (Future<SslContext>)promise.setSuccess(context);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Selection {
/*     */     final SslContext context;
/*     */     final String hostname;
/*     */     
/*     */     Selection(SslContext context, String hostname) {
/* 178 */       this.context = context;
/* 179 */       this.hostname = hostname;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SniHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */