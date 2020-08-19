/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*     */ public abstract class DelegatingSslContext
/*     */   extends SslContext
/*     */ {
/*     */   private final SslContext ctx;
/*     */   
/*     */   protected DelegatingSslContext(SslContext ctx) {
/*  34 */     this.ctx = (SslContext)ObjectUtil.checkNotNull(ctx, "ctx");
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isClient() {
/*  39 */     return this.ctx.isClient();
/*     */   }
/*     */ 
/*     */   
/*     */   public final List<String> cipherSuites() {
/*  44 */     return this.ctx.cipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionCacheSize() {
/*  49 */     return this.ctx.sessionCacheSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionTimeout() {
/*  54 */     return this.ctx.sessionTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ApplicationProtocolNegotiator applicationProtocolNegotiator() {
/*  59 */     return this.ctx.applicationProtocolNegotiator();
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc) {
/*  64 */     SSLEngine engine = this.ctx.newEngine(alloc);
/*  65 */     initEngine(engine);
/*  66 */     return engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
/*  71 */     SSLEngine engine = this.ctx.newEngine(alloc, peerHost, peerPort);
/*  72 */     initEngine(engine);
/*  73 */     return engine;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
/*  78 */     SslHandler handler = this.ctx.newHandler(alloc, startTls);
/*  79 */     initHandler(handler);
/*  80 */     return handler;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
/*  85 */     SslHandler handler = this.ctx.newHandler(alloc, peerHost, peerPort, startTls);
/*  86 */     initHandler(handler);
/*  87 */     return handler;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
/*  92 */     SslHandler handler = this.ctx.newHandler(alloc, startTls, executor);
/*  93 */     initHandler(handler);
/*  94 */     return handler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor executor) {
/* 100 */     SslHandler handler = this.ctx.newHandler(alloc, peerHost, peerPort, startTls, executor);
/* 101 */     initHandler(handler);
/* 102 */     return handler;
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLSessionContext sessionContext() {
/* 107 */     return this.ctx.sessionContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void initEngine(SSLEngine paramSSLEngine);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initHandler(SslHandler handler) {
/* 120 */     initEngine(handler.engine());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\DelegatingSslContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */