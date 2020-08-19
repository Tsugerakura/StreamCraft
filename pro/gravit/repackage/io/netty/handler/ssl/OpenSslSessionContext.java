/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SessionTicketKey;
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
/*     */ public abstract class OpenSslSessionContext
/*     */   implements SSLSessionContext
/*     */ {
/*  34 */   private static final Enumeration<byte[]> EMPTY = new EmptyEnumeration();
/*     */ 
/*     */ 
/*     */   
/*     */   private final OpenSslSessionStats stats;
/*     */ 
/*     */ 
/*     */   
/*     */   private final OpenSslKeyMaterialProvider provider;
/*     */ 
/*     */   
/*     */   final ReferenceCountedOpenSslContext context;
/*     */ 
/*     */ 
/*     */   
/*     */   OpenSslSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider) {
/*  50 */     this.context = context;
/*  51 */     this.provider = provider;
/*  52 */     this.stats = new OpenSslSessionStats(context);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSession(byte[] bytes) {
/*  57 */     ObjectUtil.checkNotNull(bytes, "bytes");
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration<byte[]> getIds() {
/*  63 */     return EMPTY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setTicketKeys(byte[] keys) {
/*  72 */     if (keys.length % 48 != 0) {
/*  73 */       throw new IllegalArgumentException("keys.length % 48 != 0");
/*     */     }
/*  75 */     SessionTicketKey[] tickets = new SessionTicketKey[keys.length / 48];
/*  76 */     for (int i = 0, a = 0; i < tickets.length; i++) {
/*  77 */       byte[] name = Arrays.copyOfRange(keys, a, 16);
/*  78 */       a += 16;
/*  79 */       byte[] hmacKey = Arrays.copyOfRange(keys, a, 16);
/*  80 */       i += 16;
/*  81 */       byte[] aesKey = Arrays.copyOfRange(keys, a, 16);
/*  82 */       a += 16;
/*  83 */       tickets[i] = new SessionTicketKey(name, hmacKey, aesKey);
/*     */     } 
/*  85 */     Lock writerLock = this.context.ctxLock.writeLock();
/*  86 */     writerLock.lock();
/*     */     try {
/*  88 */       SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
/*  89 */       SSLContext.setSessionTicketKeys(this.context.ctx, tickets);
/*     */     } finally {
/*  91 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTicketKeys(OpenSslSessionTicketKey... keys) {
/*  99 */     ObjectUtil.checkNotNull(keys, "keys");
/* 100 */     SessionTicketKey[] ticketKeys = new SessionTicketKey[keys.length];
/* 101 */     for (int i = 0; i < ticketKeys.length; i++) {
/* 102 */       ticketKeys[i] = (keys[i]).key;
/*     */     }
/* 104 */     Lock writerLock = this.context.ctxLock.writeLock();
/* 105 */     writerLock.lock();
/*     */     try {
/* 107 */       SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
/* 108 */       SSLContext.setSessionTicketKeys(this.context.ctx, ticketKeys);
/*     */     } finally {
/* 110 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setSessionCacheEnabled(boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isSessionCacheEnabled();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OpenSslSessionStats stats() {
/* 128 */     return this.stats;
/*     */   }
/*     */   
/*     */   final void destroy() {
/* 132 */     if (this.provider != null)
/* 133 */       this.provider.destroy(); 
/*     */   }
/*     */   
/*     */   private static final class EmptyEnumeration implements Enumeration<byte[]> {
/*     */     private EmptyEnumeration() {}
/*     */     
/*     */     public boolean hasMoreElements() {
/* 140 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] nextElement() {
/* 145 */       throw new NoSuchElementException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslSessionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */