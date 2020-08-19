/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.PrivateKey;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509KeyManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.CertificateVerifier;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLContext;
/*     */ import pro.gravit.repackage.io.netty.internal.tcnative.SSLPrivateKeyMethod;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetectorFactory;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ public abstract class ReferenceCountedOpenSslContext
/*     */   extends SslContext
/*     */   implements ReferenceCounted
/*     */ {
/*  81 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
/*     */   
/*  83 */   private static final int DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE = Math.max(1, 
/*  84 */       SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.handler.ssl.openssl.bioNonApplicationBufferSize", 2048));
/*     */ 
/*     */   
/*  87 */   static final boolean USE_TASKS = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.handler.ssl.openssl.useTasks", false);
/*     */   
/*     */   private static final Integer DH_KEY_LENGTH;
/*  90 */   private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
/*     */   
/*     */   protected static final int VERIFY_DEPTH = 10;
/*     */   
/*     */   protected long ctx;
/*     */   
/*     */   private final List<String> unmodifiableCiphers;
/*     */   
/*     */   private final long sessionCacheSize;
/*     */   
/*     */   private final long sessionTimeout;
/*     */   
/*     */   private final OpenSslApplicationProtocolNegotiator apn;
/*     */   
/*     */   private final int mode;
/*     */   
/*     */   private final ResourceLeakTracker<ReferenceCountedOpenSslContext> leak;
/*     */ 
/*     */   
/* 109 */   private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted()
/*     */     {
/*     */       public ReferenceCounted touch(Object hint) {
/* 112 */         if (ReferenceCountedOpenSslContext.this.leak != null) {
/* 113 */           ReferenceCountedOpenSslContext.this.leak.record(hint);
/*     */         }
/*     */         
/* 116 */         return ReferenceCountedOpenSslContext.this;
/*     */       }
/*     */ 
/*     */       
/*     */       protected void deallocate() {
/* 121 */         ReferenceCountedOpenSslContext.this.destroy();
/* 122 */         if (ReferenceCountedOpenSslContext.this.leak != null) {
/* 123 */           boolean closed = ReferenceCountedOpenSslContext.this.leak.close(ReferenceCountedOpenSslContext.this);
/* 124 */           assert closed;
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   final Certificate[] keyCertChain;
/*     */   final ClientAuth clientAuth;
/*     */   final String[] protocols;
/*     */   final boolean enableOcsp;
/* 133 */   final OpenSslEngineMap engineMap = new DefaultOpenSslEngineMap();
/* 134 */   final ReadWriteLock ctxLock = new ReentrantReadWriteLock();
/*     */   
/* 136 */   private volatile int bioNonApplicationBufferSize = DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
/*     */ 
/*     */   
/* 139 */   static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator()
/*     */     {
/*     */       public ApplicationProtocolConfig.Protocol protocol()
/*     */       {
/* 143 */         return ApplicationProtocolConfig.Protocol.NONE;
/*     */       }
/*     */ 
/*     */       
/*     */       public List<String> protocols() {
/* 148 */         return Collections.emptyList();
/*     */       }
/*     */ 
/*     */       
/*     */       public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
/* 153 */         return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
/*     */       }
/*     */ 
/*     */       
/*     */       public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
/* 158 */         return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
/*     */       }
/*     */     };
/*     */   
/*     */   static {
/* 163 */     Integer dhLen = null;
/*     */     
/*     */     try {
/* 166 */       String dhKeySize = SystemPropertyUtil.get("jdk.tls.ephemeralDHKeySize");
/* 167 */       if (dhKeySize != null) {
/*     */         try {
/* 169 */           dhLen = Integer.valueOf(dhKeySize);
/* 170 */         } catch (NumberFormatException e) {
/* 171 */           logger.debug("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + dhKeySize);
/*     */         }
/*     */       
/*     */       }
/* 175 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/* 178 */     DH_KEY_LENGTH = dhLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apnCfg, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, boolean leakDetection) throws SSLException {
/* 185 */     this(ciphers, cipherFilter, toNegotiator(apnCfg), sessionCacheSize, sessionTimeout, mode, keyCertChain, clientAuth, protocols, startTls, enableOcsp, leakDetection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, boolean leakDetection) throws SSLException {
/* 194 */     super(startTls);
/*     */     
/* 196 */     OpenSsl.ensureAvailability();
/*     */     
/* 198 */     if (enableOcsp && !OpenSsl.isOcspSupported()) {
/* 199 */       throw new IllegalStateException("OCSP is not supported.");
/*     */     }
/*     */     
/* 202 */     if (mode != 1 && mode != 0) {
/* 203 */       throw new IllegalArgumentException("mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
/*     */     }
/* 205 */     this.leak = leakDetection ? leakDetector.track(this) : null;
/* 206 */     this.mode = mode;
/* 207 */     this.clientAuth = isServer() ? (ClientAuth)ObjectUtil.checkNotNull(clientAuth, "clientAuth") : ClientAuth.NONE;
/* 208 */     this.protocols = protocols;
/* 209 */     this.enableOcsp = enableOcsp;
/*     */     
/* 211 */     this.keyCertChain = (keyCertChain == null) ? null : (Certificate[])keyCertChain.clone();
/*     */     
/* 213 */     this.unmodifiableCiphers = Arrays.asList(((CipherSuiteFilter)ObjectUtil.checkNotNull(cipherFilter, "cipherFilter")).filterCipherSuites(ciphers, OpenSsl.DEFAULT_CIPHERS, 
/* 214 */           OpenSsl.availableJavaCipherSuites()));
/*     */     
/* 216 */     this.apn = (OpenSslApplicationProtocolNegotiator)ObjectUtil.checkNotNull(apn, "apn");
/*     */ 
/*     */     
/* 219 */     boolean success = false;
/*     */     try {
/*     */       try {
/* 222 */         int protocolOpts = 30;
/*     */         
/* 224 */         if (OpenSsl.isTlsv13Supported()) {
/* 225 */           protocolOpts |= 0x20;
/*     */         }
/* 227 */         this.ctx = SSLContext.make(protocolOpts, mode);
/* 228 */       } catch (Exception e) {
/* 229 */         throw new SSLException("failed to create an SSL_CTX", e);
/*     */       } 
/*     */       
/* 232 */       boolean tlsv13Supported = OpenSsl.isTlsv13Supported();
/* 233 */       StringBuilder cipherBuilder = new StringBuilder();
/* 234 */       StringBuilder cipherTLSv13Builder = new StringBuilder();
/*     */ 
/*     */       
/*     */       try {
/* 238 */         if (this.unmodifiableCiphers.isEmpty()) {
/*     */           
/* 240 */           SSLContext.setCipherSuite(this.ctx, "", false);
/* 241 */           if (tlsv13Supported)
/*     */           {
/* 243 */             SSLContext.setCipherSuite(this.ctx, "", true);
/*     */           }
/*     */         } else {
/* 246 */           CipherSuiteConverter.convertToCipherStrings(this.unmodifiableCiphers, cipherBuilder, cipherTLSv13Builder, 
/* 247 */               OpenSsl.isBoringSSL());
/*     */ 
/*     */           
/* 250 */           SSLContext.setCipherSuite(this.ctx, cipherBuilder.toString(), false);
/* 251 */           if (tlsv13Supported)
/*     */           {
/* 253 */             SSLContext.setCipherSuite(this.ctx, cipherTLSv13Builder.toString(), true);
/*     */           }
/*     */         } 
/* 256 */       } catch (SSLException e) {
/* 257 */         throw e;
/* 258 */       } catch (Exception e) {
/* 259 */         throw new SSLException("failed to set cipher suite: " + this.unmodifiableCiphers, e);
/*     */       } 
/*     */       
/* 262 */       int options = SSLContext.getOptions(this.ctx) | SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1_3 | SSL.SSL_OP_CIPHER_SERVER_PREFERENCE | SSL.SSL_OP_NO_COMPRESSION | SSL.SSL_OP_NO_TICKET;
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
/* 280 */       if (cipherBuilder.length() == 0)
/*     */       {
/* 282 */         options |= SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2;
/*     */       }
/*     */ 
/*     */       
/* 286 */       SSLContext.setOptions(this.ctx, options);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 291 */       SSLContext.setMode(this.ctx, SSLContext.getMode(this.ctx) | SSL.SSL_MODE_ACCEPT_MOVING_WRITE_BUFFER);
/*     */       
/* 293 */       if (DH_KEY_LENGTH != null) {
/* 294 */         SSLContext.setTmpDHLength(this.ctx, DH_KEY_LENGTH.intValue());
/*     */       }
/*     */       
/* 297 */       List<String> nextProtoList = apn.protocols();
/*     */       
/* 299 */       if (!nextProtoList.isEmpty()) {
/* 300 */         String[] appProtocols = nextProtoList.<String>toArray(new String[0]);
/* 301 */         int selectorBehavior = opensslSelectorFailureBehavior(apn.selectorFailureBehavior());
/*     */         
/* 303 */         switch (apn.protocol()) {
/*     */           case CHOOSE_MY_LAST_PROTOCOL:
/* 305 */             SSLContext.setNpnProtos(this.ctx, appProtocols, selectorBehavior);
/*     */             break;
/*     */           case ACCEPT:
/* 308 */             SSLContext.setAlpnProtos(this.ctx, appProtocols, selectorBehavior);
/*     */             break;
/*     */           case null:
/* 311 */             SSLContext.setNpnProtos(this.ctx, appProtocols, selectorBehavior);
/* 312 */             SSLContext.setAlpnProtos(this.ctx, appProtocols, selectorBehavior);
/*     */             break;
/*     */           default:
/* 315 */             throw new Error();
/*     */         } 
/*     */ 
/*     */       
/*     */       } 
/* 320 */       if (sessionCacheSize <= 0L)
/*     */       {
/* 322 */         sessionCacheSize = SSLContext.setSessionCacheSize(this.ctx, 20480L);
/*     */       }
/* 324 */       this.sessionCacheSize = sessionCacheSize;
/* 325 */       SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
/*     */ 
/*     */       
/* 328 */       if (sessionTimeout <= 0L)
/*     */       {
/* 330 */         sessionTimeout = SSLContext.setSessionCacheTimeout(this.ctx, 300L);
/*     */       }
/* 332 */       this.sessionTimeout = sessionTimeout;
/* 333 */       SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
/*     */       
/* 335 */       if (enableOcsp) {
/* 336 */         SSLContext.enableOcsp(this.ctx, isClient());
/*     */       }
/*     */       
/* 339 */       SSLContext.setUseTasks(this.ctx, USE_TASKS);
/* 340 */       success = true;
/*     */     } finally {
/* 342 */       if (!success) {
/* 343 */         release();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int opensslSelectorFailureBehavior(ApplicationProtocolConfig.SelectorFailureBehavior behavior) {
/* 349 */     switch (behavior) {
/*     */       case CHOOSE_MY_LAST_PROTOCOL:
/* 351 */         return 0;
/*     */       case ACCEPT:
/* 353 */         return 1;
/*     */     } 
/* 355 */     throw new Error();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final List<String> cipherSuites() {
/* 361 */     return this.unmodifiableCiphers;
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionCacheSize() {
/* 366 */     return this.sessionCacheSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public final long sessionTimeout() {
/* 371 */     return this.sessionTimeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
/* 376 */     return this.apn;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isClient() {
/* 381 */     return (this.mode == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
/* 386 */     return newEngine0(alloc, peerHost, peerPort, true);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
/* 391 */     return new SslHandler(newEngine0(alloc, null, -1, false), startTls);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
/* 396 */     return new SslHandler(newEngine0(alloc, peerHost, peerPort, false), startTls);
/*     */   }
/*     */ 
/*     */   
/*     */   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
/* 401 */     return new SslHandler(newEngine0(alloc, null, -1, false), startTls, executor);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor executor) {
/* 407 */     return new SslHandler(newEngine0(alloc, peerHost, peerPort, false), executor);
/*     */   }
/*     */   
/*     */   SSLEngine newEngine0(ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
/* 411 */     return new ReferenceCountedOpenSslEngine(this, alloc, peerHost, peerPort, jdkCompatibilityMode, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SSLEngine newEngine(ByteBufAllocator alloc) {
/* 419 */     return newEngine(alloc, null, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final long context() {
/* 431 */     return sslCtxPointer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final OpenSslSessionStats stats() {
/* 441 */     return sessionContext().stats();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setRejectRemoteInitiatedRenegotiation(boolean rejectRemoteInitiatedRenegotiation) {
/* 451 */     if (!rejectRemoteInitiatedRenegotiation) {
/* 452 */       throw new UnsupportedOperationException("Renegotiation is not supported");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean getRejectRemoteInitiatedRenegotiation() {
/* 462 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBioNonApplicationBufferSize(int bioNonApplicationBufferSize) {
/* 470 */     this
/* 471 */       .bioNonApplicationBufferSize = ObjectUtil.checkPositiveOrZero(bioNonApplicationBufferSize, "bioNonApplicationBufferSize");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBioNonApplicationBufferSize() {
/* 478 */     return this.bioNonApplicationBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final void setTicketKeys(byte[] keys) {
/* 488 */     sessionContext().setTicketKeys(keys);
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
/*     */   @Deprecated
/*     */   public final long sslCtxPointer() {
/* 503 */     Lock readerLock = this.ctxLock.readLock();
/* 504 */     readerLock.lock();
/*     */     try {
/* 506 */       return SSLContext.getSslCtx(this.ctx);
/*     */     } finally {
/* 508 */       readerLock.unlock();
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
/*     */   public final void setPrivateKeyMethod(OpenSslPrivateKeyMethod method) {
/* 522 */     ObjectUtil.checkNotNull(method, "method");
/* 523 */     Lock writerLock = this.ctxLock.writeLock();
/* 524 */     writerLock.lock();
/*     */     try {
/* 526 */       SSLContext.setPrivateKeyMethod(this.ctx, new PrivateKeyMethod(this.engineMap, method));
/*     */     } finally {
/* 528 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   final void setUseTasks(boolean useTasks) {
/* 534 */     Lock writerLock = this.ctxLock.writeLock();
/* 535 */     writerLock.lock();
/*     */     try {
/* 537 */       SSLContext.setUseTasks(this.ctx, useTasks);
/*     */     } finally {
/* 539 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void destroy() {
/* 547 */     Lock writerLock = this.ctxLock.writeLock();
/* 548 */     writerLock.lock();
/*     */     try {
/* 550 */       if (this.ctx != 0L) {
/* 551 */         if (this.enableOcsp) {
/* 552 */           SSLContext.disableOcsp(this.ctx);
/*     */         }
/*     */         
/* 555 */         SSLContext.free(this.ctx);
/* 556 */         this.ctx = 0L;
/*     */         
/* 558 */         OpenSslSessionContext context = sessionContext();
/* 559 */         if (context != null) {
/* 560 */           context.destroy();
/*     */         }
/*     */       } 
/*     */     } finally {
/* 564 */       writerLock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected static X509Certificate[] certificates(byte[][] chain) {
/* 569 */     X509Certificate[] peerCerts = new X509Certificate[chain.length];
/* 570 */     for (int i = 0; i < peerCerts.length; i++) {
/* 571 */       peerCerts[i] = new OpenSslX509Certificate(chain[i]);
/*     */     }
/* 573 */     return peerCerts;
/*     */   }
/*     */   
/*     */   protected static X509TrustManager chooseTrustManager(TrustManager[] managers) {
/* 577 */     for (TrustManager m : managers) {
/* 578 */       if (m instanceof X509TrustManager) {
/* 579 */         if (PlatformDependent.javaVersion() >= 7) {
/* 580 */           return OpenSslX509TrustManagerWrapper.wrapIfNeeded((X509TrustManager)m);
/*     */         }
/* 582 */         return (X509TrustManager)m;
/*     */       } 
/*     */     } 
/* 585 */     throw new IllegalStateException("no X509TrustManager found");
/*     */   }
/*     */   
/*     */   protected static X509KeyManager chooseX509KeyManager(KeyManager[] kms) {
/* 589 */     for (KeyManager km : kms) {
/* 590 */       if (km instanceof X509KeyManager) {
/* 591 */         return (X509KeyManager)km;
/*     */       }
/*     */     } 
/* 594 */     throw new IllegalStateException("no X509KeyManager found");
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
/*     */   static OpenSslApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config) {
/* 606 */     if (config == null) {
/* 607 */       return NONE_PROTOCOL_NEGOTIATOR;
/*     */     }
/*     */     
/* 610 */     switch (config.protocol()) {
/*     */       case null:
/* 612 */         return NONE_PROTOCOL_NEGOTIATOR;
/*     */       case CHOOSE_MY_LAST_PROTOCOL:
/*     */       case ACCEPT:
/*     */       case null:
/* 616 */         switch (config.selectedListenerFailureBehavior()) {
/*     */           case CHOOSE_MY_LAST_PROTOCOL:
/*     */           case ACCEPT:
/* 619 */             switch (config.selectorFailureBehavior()) {
/*     */               case CHOOSE_MY_LAST_PROTOCOL:
/*     */               case ACCEPT:
/* 622 */                 return new OpenSslDefaultApplicationProtocolNegotiator(config);
/*     */             } 
/*     */             
/* 625 */             throw new UnsupportedOperationException("OpenSSL provider does not support " + config
/*     */                 
/* 627 */                 .selectorFailureBehavior() + " behavior");
/*     */         } 
/*     */ 
/*     */         
/* 631 */         throw new UnsupportedOperationException("OpenSSL provider does not support " + config
/*     */             
/* 633 */             .selectedListenerFailureBehavior() + " behavior");
/*     */     } 
/*     */ 
/*     */     
/* 637 */     throw new Error();
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Guarded by java version check")
/*     */   static boolean useExtendedTrustManager(X509TrustManager trustManager) {
/* 643 */     return (PlatformDependent.javaVersion() >= 7 && trustManager instanceof javax.net.ssl.X509ExtendedTrustManager);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int refCnt() {
/* 648 */     return this.refCnt.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ReferenceCounted retain() {
/* 653 */     this.refCnt.retain();
/* 654 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ReferenceCounted retain(int increment) {
/* 659 */     this.refCnt.retain(increment);
/* 660 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ReferenceCounted touch() {
/* 665 */     this.refCnt.touch();
/* 666 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ReferenceCounted touch(Object hint) {
/* 671 */     this.refCnt.touch(hint);
/* 672 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean release() {
/* 677 */     return this.refCnt.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean release(int decrement) {
/* 682 */     return this.refCnt.release(decrement);
/*     */   }
/*     */   
/*     */   static abstract class AbstractCertificateVerifier extends CertificateVerifier {
/*     */     private final OpenSslEngineMap engineMap;
/*     */     
/*     */     AbstractCertificateVerifier(OpenSslEngineMap engineMap) {
/* 689 */       this.engineMap = engineMap;
/*     */     }
/*     */ 
/*     */     
/*     */     public final int verify(long ssl, byte[][] chain, String auth) {
/* 694 */       ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
/* 695 */       if (engine == null)
/*     */       {
/* 697 */         return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
/*     */       }
/* 699 */       X509Certificate[] peerCerts = ReferenceCountedOpenSslContext.certificates(chain);
/*     */       try {
/* 701 */         verify(engine, peerCerts, auth);
/* 702 */         return CertificateVerifier.X509_V_OK;
/* 703 */       } catch (Throwable cause) {
/* 704 */         ReferenceCountedOpenSslContext.logger.debug("verification of certificate failed", cause);
/* 705 */         engine.initHandshakeException(cause);
/*     */ 
/*     */         
/* 708 */         if (cause instanceof OpenSslCertificateException)
/*     */         {
/*     */           
/* 711 */           return ((OpenSslCertificateException)cause).errorCode();
/*     */         }
/* 713 */         if (cause instanceof java.security.cert.CertificateExpiredException) {
/* 714 */           return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
/*     */         }
/* 716 */         if (cause instanceof java.security.cert.CertificateNotYetValidException) {
/* 717 */           return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
/*     */         }
/* 719 */         if (PlatformDependent.javaVersion() >= 7) {
/* 720 */           return translateToError(cause);
/*     */         }
/*     */ 
/*     */         
/* 724 */         return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
/*     */       } 
/*     */     }
/*     */     
/*     */     @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */     private static int translateToError(Throwable cause) {
/* 730 */       if (cause instanceof java.security.cert.CertificateRevokedException) {
/* 731 */         return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 737 */       Throwable wrapped = cause.getCause();
/* 738 */       while (wrapped != null) {
/* 739 */         if (wrapped instanceof CertPathValidatorException) {
/* 740 */           CertPathValidatorException ex = (CertPathValidatorException)wrapped;
/* 741 */           CertPathValidatorException.Reason reason = ex.getReason();
/* 742 */           if (reason == CertPathValidatorException.BasicReason.EXPIRED) {
/* 743 */             return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
/*     */           }
/* 745 */           if (reason == CertPathValidatorException.BasicReason.NOT_YET_VALID) {
/* 746 */             return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
/*     */           }
/* 748 */           if (reason == CertPathValidatorException.BasicReason.REVOKED) {
/* 749 */             return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
/*     */           }
/*     */         } 
/* 752 */         wrapped = wrapped.getCause();
/*     */       } 
/* 754 */       return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
/*     */     }
/*     */     
/*     */     abstract void verify(ReferenceCountedOpenSslEngine param1ReferenceCountedOpenSslEngine, X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws Exception;
/*     */   }
/*     */   
/*     */   private static final class DefaultOpenSslEngineMap
/*     */     implements OpenSslEngineMap {
/* 762 */     private final Map<Long, ReferenceCountedOpenSslEngine> engines = PlatformDependent.newConcurrentHashMap();
/*     */ 
/*     */     
/*     */     public ReferenceCountedOpenSslEngine remove(long ssl) {
/* 766 */       return this.engines.remove(Long.valueOf(ssl));
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(ReferenceCountedOpenSslEngine engine) {
/* 771 */       this.engines.put(Long.valueOf(engine.sslPointer()), engine);
/*     */     }
/*     */ 
/*     */     
/*     */     public ReferenceCountedOpenSslEngine get(long ssl) {
/* 776 */       return this.engines.get(Long.valueOf(ssl));
/*     */     }
/*     */     
/*     */     private DefaultOpenSslEngineMap() {}
/*     */   }
/*     */   
/*     */   static void setKeyMaterial(long ctx, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword) throws SSLException {
/* 783 */     long keyBio = 0L;
/* 784 */     long keyCertChainBio = 0L;
/* 785 */     long keyCertChainBio2 = 0L;
/* 786 */     PemEncoded encoded = null;
/*     */     
/*     */     try {
/* 789 */       encoded = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, keyCertChain);
/* 790 */       keyCertChainBio = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
/* 791 */       keyCertChainBio2 = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
/*     */       
/* 793 */       if (key != null) {
/* 794 */         keyBio = toBIO(ByteBufAllocator.DEFAULT, key);
/*     */       }
/*     */       
/* 797 */       SSLContext.setCertificateBio(ctx, keyCertChainBio, keyBio, (keyPassword == null) ? "" : keyPassword);
/*     */ 
/*     */ 
/*     */       
/* 801 */       SSLContext.setCertificateChainBio(ctx, keyCertChainBio2, true);
/* 802 */     } catch (SSLException e) {
/* 803 */       throw e;
/* 804 */     } catch (Exception e) {
/* 805 */       throw new SSLException("failed to set certificate and key", e);
/*     */     } finally {
/* 807 */       freeBio(keyBio);
/* 808 */       freeBio(keyCertChainBio);
/* 809 */       freeBio(keyCertChainBio2);
/* 810 */       if (encoded != null) {
/* 811 */         encoded.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   static void freeBio(long bio) {
/* 817 */     if (bio != 0L) {
/* 818 */       SSL.freeBIO(bio);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long toBIO(ByteBufAllocator allocator, PrivateKey key) throws Exception {
/* 827 */     if (key == null) {
/* 828 */       return 0L;
/*     */     }
/*     */     
/* 831 */     PemEncoded pem = PemPrivateKey.toPEM(allocator, true, key);
/*     */     try {
/* 833 */       return toBIO(allocator, pem.retain());
/*     */     } finally {
/* 835 */       pem.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long toBIO(ByteBufAllocator allocator, X509Certificate... certChain) throws Exception {
/* 844 */     if (certChain == null) {
/* 845 */       return 0L;
/*     */     }
/*     */     
/* 848 */     if (certChain.length == 0) {
/* 849 */       throw new IllegalArgumentException("certChain can't be empty");
/*     */     }
/*     */     
/* 852 */     PemEncoded pem = PemX509Certificate.toPEM(allocator, true, certChain);
/*     */     try {
/* 854 */       return toBIO(allocator, pem.retain());
/*     */     } finally {
/* 856 */       pem.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static long toBIO(ByteBufAllocator allocator, PemEncoded pem) throws Exception {
/*     */     try {
/* 864 */       ByteBuf content = pem.content();
/*     */       
/* 866 */       if (content.isDirect()) {
/* 867 */         return newBIO(content.retainedSlice());
/*     */       }
/*     */       
/* 870 */       ByteBuf buffer = allocator.directBuffer(content.readableBytes());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 886 */       pem.release();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static long newBIO(ByteBuf buffer) throws Exception {
/*     */     try {
/* 892 */       long bio = SSL.newMemBIO();
/* 893 */       int readable = buffer.readableBytes();
/* 894 */       if (SSL.bioWrite(bio, OpenSsl.memoryAddress(buffer) + buffer.readerIndex(), readable) != readable) {
/* 895 */         SSL.freeBIO(bio);
/* 896 */         throw new IllegalStateException("Could not write data to memory BIO");
/*     */       } 
/* 898 */       return bio;
/*     */     } finally {
/* 900 */       buffer.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static OpenSslKeyMaterialProvider providerFor(KeyManagerFactory factory, String password) {
/* 910 */     if (factory instanceof OpenSslX509KeyManagerFactory) {
/* 911 */       return ((OpenSslX509KeyManagerFactory)factory).newProvider();
/*     */     }
/*     */     
/* 914 */     if (factory instanceof OpenSslCachingX509KeyManagerFactory)
/*     */     {
/* 916 */       return ((OpenSslCachingX509KeyManagerFactory)factory).newProvider(password);
/*     */     }
/*     */     
/* 919 */     return new OpenSslKeyMaterialProvider(chooseX509KeyManager(factory.getKeyManagers()), password);
/*     */   }
/*     */   
/*     */   public abstract OpenSslSessionContext sessionContext();
/*     */   
/*     */   private static final class PrivateKeyMethod implements SSLPrivateKeyMethod { private final OpenSslEngineMap engineMap;
/*     */     
/*     */     PrivateKeyMethod(OpenSslEngineMap engineMap, OpenSslPrivateKeyMethod keyMethod) {
/* 927 */       this.engineMap = engineMap;
/* 928 */       this.keyMethod = keyMethod;
/*     */     }
/*     */     private final OpenSslPrivateKeyMethod keyMethod;
/*     */     private ReferenceCountedOpenSslEngine retrieveEngine(long ssl) throws SSLException {
/* 932 */       ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
/* 933 */       if (engine == null) {
/* 934 */         throw new SSLException("Could not find a " + 
/* 935 */             StringUtil.simpleClassName(ReferenceCountedOpenSslEngine.class) + " for sslPointer " + ssl);
/*     */       }
/* 937 */       return engine;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] sign(long ssl, int signatureAlgorithm, byte[] digest) throws Exception {
/* 942 */       ReferenceCountedOpenSslEngine engine = retrieveEngine(ssl);
/*     */       try {
/* 944 */         return verifyResult(this.keyMethod.sign(engine, signatureAlgorithm, digest));
/* 945 */       } catch (Exception e) {
/* 946 */         engine.initHandshakeException(e);
/* 947 */         throw e;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] decrypt(long ssl, byte[] input) throws Exception {
/* 953 */       ReferenceCountedOpenSslEngine engine = retrieveEngine(ssl);
/*     */       try {
/* 955 */         return verifyResult(this.keyMethod.decrypt(engine, input));
/* 956 */       } catch (Exception e) {
/* 957 */         engine.initHandshakeException(e);
/* 958 */         throw e;
/*     */       } 
/*     */     }
/*     */     
/*     */     private static byte[] verifyResult(byte[] result) throws SignatureException {
/* 963 */       if (result == null) {
/* 964 */         throw new SignatureException();
/*     */       }
/* 966 */       return result;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ReferenceCountedOpenSslContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */