/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class OpenSslServerSessionContext
/*     */   extends OpenSslSessionContext
/*     */ {
/*     */   OpenSslServerSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider) {
/*  29 */     super(context, provider);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionTimeout(int seconds) {
/*  34 */     if (seconds < 0) {
/*  35 */       throw new IllegalArgumentException();
/*     */     }
/*  37 */     Lock writerLock = this.context.ctxLock.writeLock();
/*  38 */     writerLock.lock();
/*     */     try {
/*  40 */       SSLContext.setSessionCacheTimeout(this.context.ctx, seconds);
/*     */     } finally {
/*  42 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSessionTimeout() {
/*  48 */     Lock readerLock = this.context.ctxLock.readLock();
/*  49 */     readerLock.lock();
/*     */     try {
/*  51 */       return (int)SSLContext.getSessionCacheTimeout(this.context.ctx);
/*     */     } finally {
/*  53 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionCacheSize(int size) {
/*  59 */     if (size < 0) {
/*  60 */       throw new IllegalArgumentException();
/*     */     }
/*  62 */     Lock writerLock = this.context.ctxLock.writeLock();
/*  63 */     writerLock.lock();
/*     */     try {
/*  65 */       SSLContext.setSessionCacheSize(this.context.ctx, size);
/*     */     } finally {
/*  67 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSessionCacheSize() {
/*  73 */     Lock readerLock = this.context.ctxLock.readLock();
/*  74 */     readerLock.lock();
/*     */     try {
/*  76 */       return (int)SSLContext.getSessionCacheSize(this.context.ctx);
/*     */     } finally {
/*  78 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionCacheEnabled(boolean enabled) {
/*  84 */     long mode = enabled ? SSL.SSL_SESS_CACHE_SERVER : SSL.SSL_SESS_CACHE_OFF;
/*     */     
/*  86 */     Lock writerLock = this.context.ctxLock.writeLock();
/*  87 */     writerLock.lock();
/*     */     try {
/*  89 */       SSLContext.setSessionCacheMode(this.context.ctx, mode);
/*     */     } finally {
/*  91 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSessionCacheEnabled() {
/*  97 */     Lock readerLock = this.context.ctxLock.readLock();
/*  98 */     readerLock.lock();
/*     */     try {
/* 100 */       return (SSLContext.getSessionCacheMode(this.context.ctx) == SSL.SSL_SESS_CACHE_SERVER);
/*     */     } finally {
/* 102 */       readerLock.unlock();
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
/*     */   public boolean setSessionIdContext(byte[] sidCtx) {
/* 116 */     Lock writerLock = this.context.ctxLock.writeLock();
/* 117 */     writerLock.lock();
/*     */     try {
/* 119 */       return SSLContext.setSessionIdContext(this.context.ctx, sidCtx);
/*     */     } finally {
/* 121 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslServerSessionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */