/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.concurrent.locks.Lock;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class OpenSslSessionStats
/*     */ {
/*     */   private final ReferenceCountedOpenSslContext context;
/*     */   
/*     */   OpenSslSessionStats(ReferenceCountedOpenSslContext context) {
/*  37 */     this.context = context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long number() {
/*  44 */     Lock readerLock = this.context.ctxLock.readLock();
/*  45 */     readerLock.lock();
/*     */     try {
/*  47 */       return SSLContext.sessionNumber(this.context.ctx);
/*     */     } finally {
/*  49 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long connect() {
/*  57 */     Lock readerLock = this.context.ctxLock.readLock();
/*  58 */     readerLock.lock();
/*     */     try {
/*  60 */       return SSLContext.sessionConnect(this.context.ctx);
/*     */     } finally {
/*  62 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long connectGood() {
/*  70 */     Lock readerLock = this.context.ctxLock.readLock();
/*  71 */     readerLock.lock();
/*     */     try {
/*  73 */       return SSLContext.sessionConnectGood(this.context.ctx);
/*     */     } finally {
/*  75 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long connectRenegotiate() {
/*  83 */     Lock readerLock = this.context.ctxLock.readLock();
/*  84 */     readerLock.lock();
/*     */     try {
/*  86 */       return SSLContext.sessionConnectRenegotiate(this.context.ctx);
/*     */     } finally {
/*  88 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long accept() {
/*  96 */     Lock readerLock = this.context.ctxLock.readLock();
/*  97 */     readerLock.lock();
/*     */     try {
/*  99 */       return SSLContext.sessionAccept(this.context.ctx);
/*     */     } finally {
/* 101 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long acceptGood() {
/* 109 */     Lock readerLock = this.context.ctxLock.readLock();
/* 110 */     readerLock.lock();
/*     */     try {
/* 112 */       return SSLContext.sessionAcceptGood(this.context.ctx);
/*     */     } finally {
/* 114 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long acceptRenegotiate() {
/* 122 */     Lock readerLock = this.context.ctxLock.readLock();
/* 123 */     readerLock.lock();
/*     */     try {
/* 125 */       return SSLContext.sessionAcceptRenegotiate(this.context.ctx);
/*     */     } finally {
/* 127 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long hits() {
/* 137 */     Lock readerLock = this.context.ctxLock.readLock();
/* 138 */     readerLock.lock();
/*     */     try {
/* 140 */       return SSLContext.sessionHits(this.context.ctx);
/*     */     } finally {
/* 142 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long cbHits() {
/* 150 */     Lock readerLock = this.context.ctxLock.readLock();
/* 151 */     readerLock.lock();
/*     */     try {
/* 153 */       return SSLContext.sessionCbHits(this.context.ctx);
/*     */     } finally {
/* 155 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long misses() {
/* 164 */     Lock readerLock = this.context.ctxLock.readLock();
/* 165 */     readerLock.lock();
/*     */     try {
/* 167 */       return SSLContext.sessionMisses(this.context.ctx);
/*     */     } finally {
/* 169 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long timeouts() {
/* 179 */     Lock readerLock = this.context.ctxLock.readLock();
/* 180 */     readerLock.lock();
/*     */     try {
/* 182 */       return SSLContext.sessionTimeouts(this.context.ctx);
/*     */     } finally {
/* 184 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long cacheFull() {
/* 192 */     Lock readerLock = this.context.ctxLock.readLock();
/* 193 */     readerLock.lock();
/*     */     try {
/* 195 */       return SSLContext.sessionCacheFull(this.context.ctx);
/*     */     } finally {
/* 197 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long ticketKeyFail() {
/* 205 */     Lock readerLock = this.context.ctxLock.readLock();
/* 206 */     readerLock.lock();
/*     */     try {
/* 208 */       return SSLContext.sessionTicketKeyFail(this.context.ctx);
/*     */     } finally {
/* 210 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long ticketKeyNew() {
/* 218 */     Lock readerLock = this.context.ctxLock.readLock();
/* 219 */     readerLock.lock();
/*     */     try {
/* 221 */       return SSLContext.sessionTicketKeyNew(this.context.ctx);
/*     */     } finally {
/* 223 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long ticketKeyRenew() {
/* 232 */     Lock readerLock = this.context.ctxLock.readLock();
/* 233 */     readerLock.lock();
/*     */     try {
/* 235 */       return SSLContext.sessionTicketKeyRenew(this.context.ctx);
/*     */     } finally {
/* 237 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long ticketKeyResume() {
/* 245 */     Lock readerLock = this.context.ctxLock.readLock();
/* 246 */     readerLock.lock();
/*     */     try {
/* 248 */       return SSLContext.sessionTicketKeyResume(this.context.ctx);
/*     */     } finally {
/* 250 */       readerLock.unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslSessionStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */