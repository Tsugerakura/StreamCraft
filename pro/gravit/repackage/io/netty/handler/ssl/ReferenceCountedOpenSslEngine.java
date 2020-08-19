/*      */ package pro.gravit.repackage.io.netty.handler.ssl;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ReadOnlyBufferException;
/*      */ import java.security.Principal;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLEngineResult;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLHandshakeException;
/*      */ import javax.net.ssl.SSLParameters;
/*      */ import javax.net.ssl.SSLPeerUnverifiedException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSessionBindingEvent;
/*      */ import javax.net.ssl.SSLSessionBindingListener;
/*      */ import javax.net.ssl.SSLSessionContext;
/*      */ import javax.security.cert.X509Certificate;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.internal.tcnative.Buffer;
/*      */ import pro.gravit.repackage.io.netty.internal.tcnative.SSL;
/*      */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetectorFactory;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*      */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ReferenceCountedOpenSslEngine
/*      */   extends SSLEngine
/*      */   implements ReferenceCounted, ApplicationProtocolAccessor
/*      */ {
/*  100 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
/*      */ 
/*      */   
/*  103 */   private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
/*      */   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_3 = 5;
/*  110 */   private static final int[] OPENSSL_OP_NO_PROTOCOLS = new int[] { SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2, SSL.SSL_OP_NO_TLSv1_3 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  122 */   static final int MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
/*      */ 
/*      */ 
/*      */   
/*  126 */   private static final int MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
/*      */   
/*  128 */   private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*  129 */   private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
/*  130 */   private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
/*  131 */   private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0); private long ssl; private long networkBIO; private HandshakeState handshakeState; private boolean receivedShutdown; private volatile boolean destroyed; private volatile String applicationProtocol; private volatile boolean needTask; private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak; private final AbstractReferenceCounted refCnt; private volatile ClientAuth clientAuth; private volatile Certificate[] localCertificateChain; private volatile long lastAccessed; private String endPointIdentificationAlgorithm; private Object algorithmConstraints; private List<String> sniHostNames; private volatile Collection<?> matchers;
/*  132 */   private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0); private boolean isInboundDone; private boolean outboundClosed; final boolean jdkCompatibilityMode; private final boolean clientMode; final ByteBufAllocator alloc; private final OpenSslEngineMap engineMap; private final OpenSslApplicationProtocolNegotiator apn;
/*      */   private final ReferenceCountedOpenSslContext parentContext;
/*      */   private final OpenSslSession session;
/*      */   private final ByteBuffer[] singleSrcBuffer;
/*      */   private final ByteBuffer[] singleDstBuffer;
/*      */   private final boolean enableOcsp;
/*      */   private int maxWrapOverhead;
/*      */   private int maxWrapBufferSize;
/*      */   private Throwable handshakeException;
/*      */   
/*  142 */   private enum HandshakeState { NOT_STARTED,
/*      */ 
/*      */ 
/*      */     
/*  146 */     STARTED_IMPLICITLY,
/*      */ 
/*      */ 
/*      */     
/*  150 */     STARTED_EXPLICITLY,
/*      */ 
/*      */ 
/*      */     
/*  154 */     FINISHED; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode, boolean leakDetection) {
/*  233 */     super(peerHost, peerPort); long finalSsl; this.handshakeState = HandshakeState.NOT_STARTED; this.refCnt = new AbstractReferenceCounted() { public ReferenceCounted touch(Object hint) { if (ReferenceCountedOpenSslEngine.this.leak != null) ReferenceCountedOpenSslEngine.this.leak.record(hint);  return ReferenceCountedOpenSslEngine.this; } protected void deallocate() { ReferenceCountedOpenSslEngine.this.shutdown(); if (ReferenceCountedOpenSslEngine.this.leak != null) { boolean closed = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this); assert closed; }  ReferenceCountedOpenSslEngine.this.parentContext.release(); } }
/*  234 */       ; this.clientAuth = ClientAuth.NONE; this.lastAccessed = -1L; this.singleSrcBuffer = new ByteBuffer[1]; this.singleDstBuffer = new ByteBuffer[1]; OpenSsl.ensureAvailability();
/*  235 */     this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull(alloc, "alloc");
/*  236 */     this.apn = (OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator();
/*  237 */     this.clientMode = context.isClient();
/*  238 */     if (PlatformDependent.javaVersion() >= 7) {
/*  239 */       this.session = new ExtendedOpenSslSession(new DefaultOpenSslSession(context.sessionContext()))
/*      */         {
/*      */           private String[] peerSupportedSignatureAlgorithms;
/*      */           private List requestedServerNames;
/*      */           
/*      */           public List getRequestedServerNames() {
/*  245 */             if (ReferenceCountedOpenSslEngine.this.clientMode) {
/*  246 */               return Java8SslUtils.getSniHostNames(ReferenceCountedOpenSslEngine.this.sniHostNames);
/*      */             }
/*  248 */             synchronized (ReferenceCountedOpenSslEngine.this) {
/*  249 */               if (this.requestedServerNames == null) {
/*  250 */                 if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/*  251 */                   this.requestedServerNames = Collections.emptyList();
/*      */                 } else {
/*  253 */                   String name = SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl);
/*  254 */                   if (name == null) {
/*  255 */                     this.requestedServerNames = Collections.emptyList();
/*      */                   }
/*      */                   else {
/*      */                     
/*  259 */                     this
/*  260 */                       .requestedServerNames = Java8SslUtils.getSniHostName(
/*  261 */                         SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl).getBytes(CharsetUtil.UTF_8));
/*      */                   } 
/*      */                 } 
/*      */               }
/*  265 */               return this.requestedServerNames;
/*      */             } 
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*      */           public String[] getPeerSupportedSignatureAlgorithms() {
/*  272 */             synchronized (ReferenceCountedOpenSslEngine.this) {
/*  273 */               if (this.peerSupportedSignatureAlgorithms == null) {
/*  274 */                 if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/*  275 */                   this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
/*      */                 } else {
/*  277 */                   String[] algs = SSL.getSigAlgs(ReferenceCountedOpenSslEngine.this.ssl);
/*  278 */                   if (algs == null) {
/*  279 */                     this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
/*      */                   } else {
/*  281 */                     Set<String> algorithmList = new LinkedHashSet<String>(algs.length);
/*  282 */                     for (String alg : algs) {
/*  283 */                       String converted = SignatureAlgorithmConverter.toJavaName(alg);
/*      */                       
/*  285 */                       if (converted != null) {
/*  286 */                         algorithmList.add(converted);
/*      */                       }
/*      */                     } 
/*  289 */                     this.peerSupportedSignatureAlgorithms = algorithmList.<String>toArray(new String[0]);
/*      */                   } 
/*      */                 } 
/*      */               }
/*  293 */               return (String[])this.peerSupportedSignatureAlgorithms.clone();
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/*      */           public List<byte[]> getStatusResponses() {
/*  299 */             byte[] ocspResponse = null;
/*  300 */             if (ReferenceCountedOpenSslEngine.this.enableOcsp && ReferenceCountedOpenSslEngine.this.clientMode) {
/*  301 */               synchronized (ReferenceCountedOpenSslEngine.this) {
/*  302 */                 if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/*  303 */                   ocspResponse = SSL.getOcspResponse(ReferenceCountedOpenSslEngine.this.ssl);
/*      */                 }
/*      */               } 
/*      */             }
/*  307 */             return (ocspResponse == null) ? 
/*  308 */               (List)Collections.<byte[]>emptyList() : (List)Collections.<byte[]>singletonList(ocspResponse);
/*      */           }
/*      */         };
/*      */     } else {
/*  312 */       this.session = new DefaultOpenSslSession(context.sessionContext());
/*      */     } 
/*  314 */     this.engineMap = context.engineMap;
/*  315 */     this.enableOcsp = context.enableOcsp;
/*      */ 
/*      */     
/*  318 */     this.localCertificateChain = context.keyCertChain;
/*      */     
/*  320 */     this.jdkCompatibilityMode = jdkCompatibilityMode;
/*  321 */     Lock readerLock = context.ctxLock.readLock();
/*  322 */     readerLock.lock();
/*      */     
/*      */     try {
/*  325 */       finalSsl = SSL.newSSL(context.ctx, !context.isClient());
/*      */     } finally {
/*  327 */       readerLock.unlock();
/*      */     } 
/*  329 */     synchronized (this) {
/*  330 */       this.ssl = finalSsl;
/*      */       try {
/*  332 */         this.networkBIO = SSL.bioNewByteBuffer(this.ssl, context.getBioNonApplicationBufferSize());
/*      */ 
/*      */ 
/*      */         
/*  336 */         setClientAuth(this.clientMode ? ClientAuth.NONE : context.clientAuth);
/*      */         
/*  338 */         if (context.protocols != null) {
/*  339 */           setEnabledProtocols(context.protocols);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  344 */         if (this.clientMode && SslUtils.isValidHostNameForSNI(peerHost)) {
/*  345 */           SSL.setTlsExtHostName(this.ssl, peerHost);
/*  346 */           this.sniHostNames = Collections.singletonList(peerHost);
/*      */         } 
/*      */         
/*  349 */         if (this.enableOcsp) {
/*  350 */           SSL.enableOcsp(this.ssl);
/*      */         }
/*      */         
/*  353 */         if (!jdkCompatibilityMode) {
/*  354 */           SSL.setMode(this.ssl, SSL.getMode(this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE);
/*      */         }
/*      */ 
/*      */         
/*  358 */         calculateMaxWrapOverhead();
/*  359 */       } catch (Throwable cause) {
/*      */ 
/*      */         
/*  362 */         shutdown();
/*      */         
/*  364 */         PlatformDependent.throwException(cause);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  370 */     this.parentContext = context;
/*  371 */     this.parentContext.retain();
/*      */ 
/*      */ 
/*      */     
/*  375 */     this.leak = leakDetection ? leakDetector.track(this) : null;
/*      */   }
/*      */   
/*      */   final synchronized String[] authMethods() {
/*  379 */     if (isDestroyed()) {
/*  380 */       return EmptyArrays.EMPTY_STRINGS;
/*      */     }
/*  382 */     return SSL.authenticationMethods(this.ssl);
/*      */   }
/*      */   
/*      */   final boolean setKeyMaterial(OpenSslKeyMaterial keyMaterial) throws Exception {
/*  386 */     synchronized (this) {
/*  387 */       if (isDestroyed()) {
/*  388 */         return false;
/*      */       }
/*  390 */       SSL.setKeyMaterial(this.ssl, keyMaterial.certificateChainAddress(), keyMaterial.privateKeyAddress());
/*      */     } 
/*  392 */     this.localCertificateChain = (Certificate[])keyMaterial.certificateChain();
/*  393 */     return true;
/*      */   }
/*      */   
/*      */   final synchronized SecretKeySpec masterKey() {
/*  397 */     if (isDestroyed()) {
/*  398 */       return null;
/*      */     }
/*  400 */     return new SecretKeySpec(SSL.getMasterKey(this.ssl), "AES");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOcspResponse(byte[] response) {
/*  408 */     if (!this.enableOcsp) {
/*  409 */       throw new IllegalStateException("OCSP stapling is not enabled");
/*      */     }
/*      */     
/*  412 */     if (this.clientMode) {
/*  413 */       throw new IllegalStateException("Not a server SSLEngine");
/*      */     }
/*      */     
/*  416 */     synchronized (this) {
/*  417 */       if (!isDestroyed()) {
/*  418 */         SSL.setOcspResponse(this.ssl, response);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getOcspResponse() {
/*  428 */     if (!this.enableOcsp) {
/*  429 */       throw new IllegalStateException("OCSP stapling is not enabled");
/*      */     }
/*      */     
/*  432 */     if (!this.clientMode) {
/*  433 */       throw new IllegalStateException("Not a client SSLEngine");
/*      */     }
/*      */     
/*  436 */     synchronized (this) {
/*  437 */       if (isDestroyed()) {
/*  438 */         return EmptyArrays.EMPTY_BYTES;
/*      */       }
/*  440 */       return SSL.getOcspResponse(this.ssl);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final int refCnt() {
/*  446 */     return this.refCnt.refCnt();
/*      */   }
/*      */ 
/*      */   
/*      */   public final ReferenceCounted retain() {
/*  451 */     this.refCnt.retain();
/*  452 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ReferenceCounted retain(int increment) {
/*  457 */     this.refCnt.retain(increment);
/*  458 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ReferenceCounted touch() {
/*  463 */     this.refCnt.touch();
/*  464 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ReferenceCounted touch(Object hint) {
/*  469 */     this.refCnt.touch(hint);
/*  470 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean release() {
/*  475 */     return this.refCnt.release();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean release(int decrement) {
/*  480 */     return this.refCnt.release(decrement);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized SSLSession getHandshakeSession() {
/*  489 */     switch (this.handshakeState) {
/*      */       case NONE:
/*      */       case ALPN:
/*  492 */         return null;
/*      */     } 
/*  494 */     return this.session;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized long sslPointer() {
/*  504 */     return this.ssl;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized void shutdown() {
/*  511 */     if (!this.destroyed) {
/*  512 */       this.destroyed = true;
/*  513 */       this.engineMap.remove(this.ssl);
/*  514 */       SSL.freeSSL(this.ssl);
/*  515 */       this.ssl = this.networkBIO = 0L;
/*      */       
/*  517 */       this.isInboundDone = this.outboundClosed = true;
/*      */     } 
/*      */ 
/*      */     
/*  521 */     SSL.clearError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int writePlaintextData(ByteBuffer src, int len) {
/*  530 */     int sslWrote, pos = src.position();
/*  531 */     int limit = src.limit();
/*      */ 
/*      */     
/*  534 */     if (src.isDirect()) {
/*  535 */       sslWrote = SSL.writeToSSL(this.ssl, bufferAddress(src) + pos, len);
/*  536 */       if (sslWrote > 0) {
/*  537 */         src.position(pos + sslWrote);
/*      */       }
/*      */     } else {
/*  540 */       ByteBuf buf = this.alloc.directBuffer(len);
/*      */       try {
/*  542 */         src.limit(pos + len);
/*      */         
/*  544 */         buf.setBytes(0, src);
/*  545 */         src.limit(limit);
/*      */         
/*  547 */         sslWrote = SSL.writeToSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
/*  548 */         if (sslWrote > 0) {
/*  549 */           src.position(pos + sslWrote);
/*      */         } else {
/*  551 */           src.position(pos);
/*      */         } 
/*      */       } finally {
/*  554 */         buf.release();
/*      */       } 
/*      */     } 
/*  557 */     return sslWrote;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteBuf writeEncryptedData(ByteBuffer src, int len) {
/*  564 */     int pos = src.position();
/*  565 */     if (src.isDirect()) {
/*  566 */       SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(src) + pos, len, false);
/*      */     } else {
/*  568 */       ByteBuf buf = this.alloc.directBuffer(len);
/*      */       try {
/*  570 */         int limit = src.limit();
/*  571 */         src.limit(pos + len);
/*  572 */         buf.writeBytes(src);
/*      */         
/*  574 */         src.position(pos);
/*  575 */         src.limit(limit);
/*      */         
/*  577 */         SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(buf), len, false);
/*  578 */         return buf;
/*  579 */       } catch (Throwable cause) {
/*  580 */         buf.release();
/*  581 */         PlatformDependent.throwException(cause);
/*      */       } 
/*      */     } 
/*  584 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int readPlaintextData(ByteBuffer dst) {
/*  592 */     int sslRead, pos = dst.position();
/*  593 */     if (dst.isDirect()) {
/*  594 */       sslRead = SSL.readFromSSL(this.ssl, bufferAddress(dst) + pos, dst.limit() - pos);
/*  595 */       if (sslRead > 0) {
/*  596 */         dst.position(pos + sslRead);
/*      */       }
/*      */     } else {
/*  599 */       int limit = dst.limit();
/*  600 */       int len = Math.min(maxEncryptedPacketLength0(), limit - pos);
/*  601 */       ByteBuf buf = this.alloc.directBuffer(len);
/*      */       try {
/*  603 */         sslRead = SSL.readFromSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
/*  604 */         if (sslRead > 0) {
/*  605 */           dst.limit(pos + sslRead);
/*  606 */           buf.getBytes(buf.readerIndex(), dst);
/*  607 */           dst.limit(limit);
/*      */         } 
/*      */       } finally {
/*  610 */         buf.release();
/*      */       } 
/*      */     } 
/*      */     
/*  614 */     return sslRead;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final synchronized int maxWrapOverhead() {
/*  621 */     return this.maxWrapOverhead;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final synchronized int maxEncryptedPacketLength() {
/*  628 */     return maxEncryptedPacketLength0();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int maxEncryptedPacketLength0() {
/*  636 */     return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int calculateMaxLengthForWrap(int plaintextLength, int numComponents) {
/*  645 */     return (int)Math.min(this.maxWrapBufferSize, plaintextLength + this.maxWrapOverhead * numComponents);
/*      */   }
/*      */   
/*      */   final synchronized int sslPending() {
/*  649 */     return sslPending0();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void calculateMaxWrapOverhead() {
/*  656 */     this.maxWrapOverhead = SSL.getMaxWrapOverhead(this.ssl);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  661 */     this.maxWrapBufferSize = this.jdkCompatibilityMode ? maxEncryptedPacketLength0() : (maxEncryptedPacketLength0() << 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int sslPending0() {
/*  669 */     return (this.handshakeState != HandshakeState.FINISHED) ? 0 : SSL.sslPending(this.ssl);
/*      */   }
/*      */   
/*      */   private boolean isBytesAvailableEnoughForWrap(int bytesAvailable, int plaintextLength, int numComponents) {
/*  673 */     return (bytesAvailable - this.maxWrapOverhead * numComponents >= plaintextLength);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
/*  680 */     if (srcs == null) {
/*  681 */       throw new IllegalArgumentException("srcs is null");
/*      */     }
/*  683 */     if (dst == null) {
/*  684 */       throw new IllegalArgumentException("dst is null");
/*      */     }
/*      */     
/*  687 */     if (offset >= srcs.length || offset + length > srcs.length) {
/*  688 */       throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  693 */     if (dst.isReadOnly()) {
/*  694 */       throw new ReadOnlyBufferException();
/*      */     }
/*      */     
/*  697 */     synchronized (this) {
/*  698 */       if (isOutboundDone())
/*      */       {
/*  700 */         return (isInboundDone() || isDestroyed()) ? CLOSED_NOT_HANDSHAKING : NEED_UNWRAP_CLOSED;
/*      */       }
/*      */       
/*  703 */       int bytesProduced = 0;
/*  704 */       ByteBuf bioReadCopyBuf = null;
/*      */       
/*      */       try {
/*  707 */         if (dst.isDirect()) {
/*  708 */           SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(dst) + dst.position(), dst.remaining(), true);
/*      */         } else {
/*      */           
/*  711 */           bioReadCopyBuf = this.alloc.directBuffer(dst.remaining());
/*  712 */           SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(bioReadCopyBuf), bioReadCopyBuf.writableBytes(), true);
/*      */         } 
/*      */ 
/*      */         
/*  716 */         int bioLengthBefore = SSL.bioLengthByteBuffer(this.networkBIO);
/*      */ 
/*      */         
/*  719 */         if (this.outboundClosed) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  725 */           if (!isBytesAvailableEnoughForWrap(dst.remaining(), 2, 1)) {
/*  726 */             return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  731 */           bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
/*  732 */           if (bytesProduced <= 0) {
/*  733 */             return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  738 */           if (!doSSLShutdown()) {
/*  739 */             return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, bytesProduced);
/*      */           }
/*  741 */           bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
/*  742 */           return newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
/*      */         } 
/*      */ 
/*      */         
/*  746 */         SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*      */         
/*  748 */         if (this.handshakeState != HandshakeState.FINISHED) {
/*  749 */           if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY)
/*      */           {
/*  751 */             this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
/*      */           }
/*      */ 
/*      */           
/*  755 */           bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
/*      */           
/*  757 */           if (this.handshakeException != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  769 */             if (bytesProduced > 0) {
/*  770 */               return newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  775 */             return newResult(handshakeException(), 0, 0);
/*      */           } 
/*      */           
/*  778 */           status = handshake();
/*      */ 
/*      */ 
/*      */           
/*  782 */           bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
/*      */           
/*  784 */           if (status == SSLEngineResult.HandshakeStatus.NEED_TASK) {
/*  785 */             return newResult(status, 0, bytesProduced);
/*      */           }
/*      */           
/*  788 */           if (bytesProduced > 0)
/*      */           {
/*      */ 
/*      */             
/*  792 */             return newResult(mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? ((bytesProduced == bioLengthBefore) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : 
/*      */                   
/*  794 */                   getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED), 0, bytesProduced);
/*      */           }
/*      */ 
/*      */           
/*  798 */           if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)
/*      */           {
/*  800 */             return isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  805 */           if (this.outboundClosed) {
/*  806 */             bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
/*  807 */             return newResultMayFinishHandshake(status, 0, bytesProduced);
/*      */           } 
/*      */         } 
/*      */         
/*  811 */         int endOffset = offset + length;
/*  812 */         if (this.jdkCompatibilityMode) {
/*  813 */           int srcsLen = 0;
/*  814 */           for (int i = offset; i < endOffset; i++) {
/*  815 */             ByteBuffer src = srcs[i];
/*  816 */             if (src == null) {
/*  817 */               throw new IllegalArgumentException("srcs[" + i + "] is null");
/*      */             }
/*  819 */             if (srcsLen != MAX_PLAINTEXT_LENGTH) {
/*      */ 
/*      */ 
/*      */               
/*  823 */               srcsLen += src.remaining();
/*  824 */               if (srcsLen > MAX_PLAINTEXT_LENGTH || srcsLen < 0)
/*      */               {
/*      */ 
/*      */                 
/*  828 */                 srcsLen = MAX_PLAINTEXT_LENGTH;
/*      */               }
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/*  834 */           if (!isBytesAvailableEnoughForWrap(dst.remaining(), srcsLen, 1)) {
/*  835 */             return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0);
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  840 */         int bytesConsumed = 0;
/*      */         
/*  842 */         bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
/*  843 */         for (; offset < endOffset; offset++) {
/*  844 */           ByteBuffer src = srcs[offset];
/*  845 */           int remaining = src.remaining();
/*  846 */           if (remaining != 0) {
/*      */             int bytesWritten;
/*      */ 
/*      */ 
/*      */             
/*  851 */             if (this.jdkCompatibilityMode) {
/*      */ 
/*      */ 
/*      */               
/*  855 */               bytesWritten = writePlaintextData(src, Math.min(remaining, MAX_PLAINTEXT_LENGTH - bytesConsumed));
/*      */             
/*      */             }
/*      */             else {
/*      */               
/*  860 */               int availableCapacityForWrap = dst.remaining() - bytesProduced - this.maxWrapOverhead;
/*  861 */               if (availableCapacityForWrap <= 0) {
/*  862 */                 return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), bytesConsumed, bytesProduced);
/*      */               }
/*      */               
/*  865 */               bytesWritten = writePlaintextData(src, Math.min(remaining, availableCapacityForWrap));
/*      */             } 
/*      */             
/*  868 */             if (bytesWritten > 0) {
/*  869 */               bytesConsumed += bytesWritten;
/*      */ 
/*      */               
/*  872 */               int pendingNow = SSL.bioLengthByteBuffer(this.networkBIO);
/*  873 */               bytesProduced += bioLengthBefore - pendingNow;
/*  874 */               bioLengthBefore = pendingNow;
/*      */               
/*  876 */               if (this.jdkCompatibilityMode || bytesProduced == dst.remaining()) {
/*  877 */                 return newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
/*      */               }
/*      */             } else {
/*  880 */               int sslError = SSL.getError(this.ssl, bytesWritten);
/*  881 */               if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
/*      */                 
/*  883 */                 if (!this.receivedShutdown) {
/*  884 */                   closeAll();
/*      */                   
/*  886 */                   bytesProduced += bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*  891 */                   SSLEngineResult.HandshakeStatus hs = mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? (
/*  892 */                       (bytesProduced == dst.remaining()) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : 
/*  893 */                       getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED);
/*      */                   
/*  895 */                   return newResult(hs, bytesConsumed, bytesProduced);
/*      */                 } 
/*      */                 
/*  898 */                 return newResult(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, bytesConsumed, bytesProduced);
/*  899 */               }  if (sslError == SSL.SSL_ERROR_WANT_READ)
/*      */               {
/*      */ 
/*      */                 
/*  903 */                 return newResult(SSLEngineResult.HandshakeStatus.NEED_UNWRAP, bytesConsumed, bytesProduced); } 
/*  904 */               if (sslError == SSL.SSL_ERROR_WANT_WRITE)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  917 */                 return newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced); } 
/*  918 */               if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION)
/*      */               {
/*      */ 
/*      */                 
/*  922 */                 return newResult(SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced);
/*      */               }
/*      */               
/*  925 */               throw shutdownWithError("SSL_write", sslError);
/*      */             } 
/*      */           } 
/*      */         } 
/*  929 */         return newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
/*      */       } finally {
/*  931 */         SSL.bioClearByteBuffer(this.networkBIO);
/*  932 */         if (bioReadCopyBuf == null) {
/*  933 */           dst.position(dst.position() + bytesProduced);
/*      */         } else {
/*  935 */           assert bioReadCopyBuf.readableBytes() <= dst.remaining() : "The destination buffer " + dst + " didn't have enough remaining space to hold the encrypted content in " + bioReadCopyBuf;
/*      */           
/*  937 */           dst.put(bioReadCopyBuf.internalNioBuffer(bioReadCopyBuf.readerIndex(), bytesProduced));
/*  938 */           bioReadCopyBuf.release();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private SSLEngineResult newResult(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
/*  945 */     return newResult(SSLEngineResult.Status.OK, hs, bytesConsumed, bytesProduced);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult newResult(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
/*  953 */     if (isOutboundDone()) {
/*  954 */       if (isInboundDone()) {
/*      */         
/*  956 */         hs = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*      */ 
/*      */         
/*  959 */         shutdown();
/*      */       } 
/*  961 */       return new SSLEngineResult(SSLEngineResult.Status.CLOSED, hs, bytesConsumed, bytesProduced);
/*      */     } 
/*  963 */     if (hs == SSLEngineResult.HandshakeStatus.NEED_TASK)
/*      */     {
/*  965 */       this.needTask = true;
/*      */     }
/*  967 */     return new SSLEngineResult(status, hs, bytesConsumed, bytesProduced);
/*      */   }
/*      */ 
/*      */   
/*      */   private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
/*  972 */     return newResult(mayFinishHandshake((hs != SSLEngineResult.HandshakeStatus.FINISHED) ? getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED), bytesConsumed, bytesProduced);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
/*  979 */     return newResult(status, mayFinishHandshake((hs != SSLEngineResult.HandshakeStatus.FINISHED) ? getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED), bytesConsumed, bytesProduced);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLException shutdownWithError(String operations, int sslError) {
/*  987 */     return shutdownWithError(operations, sslError, SSL.getLastErrorNumber());
/*      */   }
/*      */   
/*      */   private SSLException shutdownWithError(String operation, int sslError, int error) {
/*  991 */     String errorString = SSL.getErrorString(error);
/*  992 */     if (logger.isDebugEnabled()) {
/*  993 */       logger.debug("{} failed with {}: OpenSSL error: {} {}", new Object[] { operation, 
/*  994 */             Integer.valueOf(sslError), Integer.valueOf(error), errorString });
/*      */     }
/*      */ 
/*      */     
/*  998 */     shutdown();
/*  999 */     if (this.handshakeState == HandshakeState.FINISHED) {
/* 1000 */       return new SSLException(errorString);
/*      */     }
/*      */     
/* 1003 */     SSLHandshakeException exception = new SSLHandshakeException(errorString);
/*      */     
/* 1005 */     if (this.handshakeException != null) {
/* 1006 */       exception.initCause(this.handshakeException);
/* 1007 */       this.handshakeException = null;
/*      */     } 
/* 1009 */     return exception;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SSLEngineResult unwrap(ByteBuffer[] srcs, int srcsOffset, int srcsLength, ByteBuffer[] dsts, int dstsOffset, int dstsLength) throws SSLException {
/* 1017 */     ObjectUtil.checkNotNull(srcs, "srcs");
/* 1018 */     if (srcsOffset >= srcs.length || srcsOffset + srcsLength > srcs.length)
/*      */     {
/* 1020 */       throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
/*      */     }
/*      */ 
/*      */     
/* 1024 */     if (dsts == null) {
/* 1025 */       throw new IllegalArgumentException("dsts is null");
/*      */     }
/* 1027 */     if (dstsOffset >= dsts.length || dstsOffset + dstsLength > dsts.length) {
/* 1028 */       throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
/*      */     }
/*      */ 
/*      */     
/* 1032 */     long capacity = 0L;
/* 1033 */     int dstsEndOffset = dstsOffset + dstsLength;
/* 1034 */     for (int i = dstsOffset; i < dstsEndOffset; i++) {
/* 1035 */       ByteBuffer dst = dsts[i];
/* 1036 */       if (dst == null) {
/* 1037 */         throw new IllegalArgumentException("dsts[" + i + "] is null");
/*      */       }
/* 1039 */       if (dst.isReadOnly()) {
/* 1040 */         throw new ReadOnlyBufferException();
/*      */       }
/* 1042 */       capacity += dst.remaining();
/*      */     } 
/*      */     
/* 1045 */     int srcsEndOffset = srcsOffset + srcsLength;
/* 1046 */     long len = 0L;
/* 1047 */     for (int j = srcsOffset; j < srcsEndOffset; j++) {
/* 1048 */       ByteBuffer src = srcs[j];
/* 1049 */       if (src == null) {
/* 1050 */         throw new IllegalArgumentException("srcs[" + j + "] is null");
/*      */       }
/* 1052 */       len += src.remaining();
/*      */     } 
/*      */     
/* 1055 */     synchronized (this) {
/* 1056 */       int packetLength; if (isInboundDone()) {
/* 1057 */         return (isOutboundDone() || isDestroyed()) ? CLOSED_NOT_HANDSHAKING : NEED_WRAP_CLOSED;
/*      */       }
/*      */       
/* 1060 */       SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*      */       
/* 1062 */       if (this.handshakeState != HandshakeState.FINISHED) {
/* 1063 */         if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY)
/*      */         {
/* 1065 */           this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
/*      */         }
/*      */         
/* 1068 */         status = handshake();
/*      */         
/* 1070 */         if (status == SSLEngineResult.HandshakeStatus.NEED_TASK) {
/* 1071 */           return newResult(status, 0, 0);
/*      */         }
/*      */         
/* 1074 */         if (status == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
/* 1075 */           return NEED_WRAP_OK;
/*      */         }
/*      */         
/* 1078 */         if (this.isInboundDone) {
/* 1079 */           return NEED_WRAP_CLOSED;
/*      */         }
/*      */       } 
/*      */       
/* 1083 */       int sslPending = sslPending0();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1089 */       if (this.jdkCompatibilityMode) {
/* 1090 */         if (len < 5L) {
/* 1091 */           return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
/*      */         }
/*      */         
/* 1094 */         packetLength = SslUtils.getEncryptedPacketLength(srcs, srcsOffset);
/* 1095 */         if (packetLength == -2) {
/* 1096 */           throw new NotSslRecordException("not an SSL/TLS record");
/*      */         }
/*      */         
/* 1099 */         int packetLengthDataOnly = packetLength - 5;
/* 1100 */         if (packetLengthDataOnly > capacity) {
/*      */ 
/*      */           
/* 1103 */           if (packetLengthDataOnly > MAX_RECORD_SIZE)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1109 */             throw new SSLException("Illegal packet length: " + packetLengthDataOnly + " > " + this.session
/* 1110 */                 .getApplicationBufferSize());
/*      */           }
/* 1112 */           this.session.tryExpandApplicationBufferSize(packetLengthDataOnly);
/*      */           
/* 1114 */           return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
/*      */         } 
/*      */         
/* 1117 */         if (len < packetLength)
/*      */         {
/*      */           
/* 1120 */           return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0); } 
/*      */       } else {
/* 1122 */         if (len == 0L && sslPending <= 0)
/* 1123 */           return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0); 
/* 1124 */         if (capacity == 0L) {
/* 1125 */           return newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
/*      */         }
/* 1127 */         packetLength = (int)Math.min(2147483647L, len);
/*      */       } 
/*      */ 
/*      */       
/* 1131 */       assert srcsOffset < srcsEndOffset;
/*      */ 
/*      */       
/* 1134 */       assert capacity > 0L;
/*      */ 
/*      */       
/* 1137 */       int bytesProduced = 0;
/* 1138 */       int bytesConsumed = 0; try {
/*      */         while (true) {
/*      */           ByteBuf bioWriteCopyBuf;
/*      */           int pendingEncryptedBytes;
/* 1142 */           ByteBuffer src = srcs[srcsOffset];
/* 1143 */           int remaining = src.remaining();
/*      */ 
/*      */           
/* 1146 */           if (remaining == 0) {
/* 1147 */             if (sslPending <= 0) {
/*      */ 
/*      */               
/* 1150 */               if (++srcsOffset >= srcsEndOffset) {
/*      */                 break;
/*      */               }
/*      */               continue;
/*      */             } 
/* 1155 */             bioWriteCopyBuf = null;
/* 1156 */             pendingEncryptedBytes = SSL.bioLengthByteBuffer(this.networkBIO);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 1161 */             pendingEncryptedBytes = Math.min(packetLength, remaining);
/* 1162 */             bioWriteCopyBuf = writeEncryptedData(src, pendingEncryptedBytes);
/*      */           } 
/*      */           
/*      */           while (true) {
/* 1166 */             ByteBuffer dst = dsts[dstsOffset];
/* 1167 */             if (!dst.hasRemaining())
/*      */             
/* 1169 */             { if (++dstsOffset >= dstsEndOffset)
/*      */               
/*      */               { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1231 */                 if (bioWriteCopyBuf != null)
/* 1232 */                   bioWriteCopyBuf.release();  break; }  continue; }  int bytesRead = readPlaintextData(dst); int localBytesConsumed = pendingEncryptedBytes - SSL.bioLengthByteBuffer(this.networkBIO); bytesConsumed += localBytesConsumed; packetLength -= localBytesConsumed; pendingEncryptedBytes -= localBytesConsumed; src.position(src.position() + localBytesConsumed); if (bytesRead > 0) { bytesProduced += bytesRead; if (!dst.hasRemaining()) { sslPending = sslPending0(); if (++dstsOffset >= dstsEndOffset) { SSLEngineResult sSLEngineResult1 = (sslPending > 0) ? newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced) : newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced); if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  return sSLEngineResult1; }  continue; }  if (packetLength == 0 || this.jdkCompatibilityMode) { if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  break; }  continue; }  int sslError = SSL.getError(this.ssl, bytesRead); if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE) { if (++srcsOffset >= srcsEndOffset) { if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  break; }  if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  continue; }  if (sslError == SSL.SSL_ERROR_ZERO_RETURN) { if (!this.receivedShutdown) closeAll();  SSLEngineResult sSLEngineResult1 = newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced); if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  return sSLEngineResult1; }  if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) { SSLEngineResult sSLEngineResult1 = newResult(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced); if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  return sSLEngineResult1; }  SSLEngineResult sSLEngineResult = sslReadErrorResult(sslError, SSL.getLastErrorNumber(), bytesConsumed, bytesProduced); if (bioWriteCopyBuf != null) bioWriteCopyBuf.release();  return sSLEngineResult;
/*      */           } 
/*      */           break;
/*      */         } 
/*      */       } finally {
/* 1237 */         SSL.bioClearByteBuffer(this.networkBIO);
/* 1238 */         rejectRemoteInitiatedRenegotiation();
/*      */       } 
/*      */ 
/*      */       
/* 1242 */       if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN) {
/* 1243 */         closeAll();
/*      */       }
/*      */       
/* 1246 */       return newResultMayFinishHandshake(isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult sslReadErrorResult(int error, int stackError, int bytesConsumed, int bytesProduced) throws SSLException {
/* 1256 */     if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
/* 1257 */       if (this.handshakeException == null && this.handshakeState != HandshakeState.FINISHED)
/*      */       {
/*      */         
/* 1260 */         this.handshakeException = new SSLHandshakeException(SSL.getErrorString(stackError));
/*      */       }
/*      */ 
/*      */       
/* 1264 */       SSL.clearError();
/* 1265 */       return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced);
/*      */     } 
/* 1267 */     throw shutdownWithError("SSL_read", error, stackError);
/*      */   }
/*      */   
/*      */   private void closeAll() throws SSLException {
/* 1271 */     this.receivedShutdown = true;
/* 1272 */     closeOutbound();
/* 1273 */     closeInbound();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
/* 1280 */     if (!isDestroyed() && SSL.getHandshakeCount(this.ssl) > 1 && 
/*      */ 
/*      */       
/* 1283 */       !"TLSv1.3".equals(this.session.getProtocol()) && this.handshakeState == HandshakeState.FINISHED) {
/*      */ 
/*      */       
/* 1286 */       shutdown();
/* 1287 */       throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
/*      */     } 
/*      */   }
/*      */   
/*      */   public final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dsts) throws SSLException {
/* 1292 */     return unwrap(srcs, 0, srcs.length, dsts, 0, dsts.length);
/*      */   }
/*      */   
/*      */   private ByteBuffer[] singleSrcBuffer(ByteBuffer src) {
/* 1296 */     this.singleSrcBuffer[0] = src;
/* 1297 */     return this.singleSrcBuffer;
/*      */   }
/*      */   
/*      */   private void resetSingleSrcBuffer() {
/* 1301 */     this.singleSrcBuffer[0] = null;
/*      */   }
/*      */   
/*      */   private ByteBuffer[] singleDstBuffer(ByteBuffer src) {
/* 1305 */     this.singleDstBuffer[0] = src;
/* 1306 */     return this.singleDstBuffer;
/*      */   }
/*      */   
/*      */   private void resetSingleDstBuffer() {
/* 1310 */     this.singleDstBuffer[0] = null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
/*      */     try {
/* 1317 */       return unwrap(singleSrcBuffer(src), 0, 1, dsts, offset, length);
/*      */     } finally {
/* 1319 */       resetSingleSrcBuffer();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*      */     try {
/* 1326 */       return wrap(singleSrcBuffer(src), dst);
/*      */     } finally {
/* 1328 */       resetSingleSrcBuffer();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/*      */     try {
/* 1335 */       return unwrap(singleSrcBuffer(src), singleDstBuffer(dst));
/*      */     } finally {
/* 1337 */       resetSingleSrcBuffer();
/* 1338 */       resetSingleDstBuffer();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
/*      */     try {
/* 1345 */       return unwrap(singleSrcBuffer(src), dsts);
/*      */     } finally {
/* 1347 */       resetSingleSrcBuffer();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized Runnable getDelegatedTask() {
/* 1353 */     if (isDestroyed()) {
/* 1354 */       return null;
/*      */     }
/* 1356 */     final Runnable task = SSL.getTask(this.ssl);
/* 1357 */     if (task == null) {
/* 1358 */       return null;
/*      */     }
/* 1360 */     return new Runnable()
/*      */       {
/*      */         public void run() {
/* 1363 */           if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/*      */             return;
/*      */           }
/*      */           
/*      */           try {
/* 1368 */             task.run();
/*      */           } finally {
/*      */             
/* 1371 */             ReferenceCountedOpenSslEngine.this.needTask = false;
/*      */           } 
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized void closeInbound() throws SSLException {
/* 1379 */     if (this.isInboundDone) {
/*      */       return;
/*      */     }
/*      */     
/* 1383 */     this.isInboundDone = true;
/*      */     
/* 1385 */     if (isOutboundDone())
/*      */     {
/*      */       
/* 1388 */       shutdown();
/*      */     }
/*      */     
/* 1391 */     if (this.handshakeState != HandshakeState.NOT_STARTED && !this.receivedShutdown) {
/* 1392 */       throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized boolean isInboundDone() {
/* 1399 */     return this.isInboundDone;
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized void closeOutbound() {
/* 1404 */     if (this.outboundClosed) {
/*      */       return;
/*      */     }
/*      */     
/* 1408 */     this.outboundClosed = true;
/*      */     
/* 1410 */     if (this.handshakeState != HandshakeState.NOT_STARTED && !isDestroyed()) {
/* 1411 */       int mode = SSL.getShutdown(this.ssl);
/* 1412 */       if ((mode & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN) {
/* 1413 */         doSSLShutdown();
/*      */       }
/*      */     } else {
/*      */       
/* 1417 */       shutdown();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean doSSLShutdown() {
/* 1426 */     if (SSL.isInInit(this.ssl) != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */       
/* 1431 */       return false;
/*      */     }
/* 1433 */     int err = SSL.shutdownSSL(this.ssl);
/* 1434 */     if (err < 0) {
/* 1435 */       int sslErr = SSL.getError(this.ssl, err);
/* 1436 */       if (sslErr == SSL.SSL_ERROR_SYSCALL || sslErr == SSL.SSL_ERROR_SSL) {
/* 1437 */         if (logger.isDebugEnabled()) {
/* 1438 */           int error = SSL.getLastErrorNumber();
/* 1439 */           logger.debug("SSL_shutdown failed: OpenSSL error: {} {}", Integer.valueOf(error), SSL.getErrorString(error));
/*      */         } 
/*      */         
/* 1442 */         shutdown();
/* 1443 */         return false;
/*      */       } 
/* 1445 */       SSL.clearError();
/*      */     } 
/* 1447 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized boolean isOutboundDone() {
/* 1454 */     return (this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication(this.networkBIO) == 0));
/*      */   }
/*      */ 
/*      */   
/*      */   public final String[] getSupportedCipherSuites() {
/* 1459 */     return OpenSsl.AVAILABLE_CIPHER_SUITES.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */   
/*      */   public final String[] getEnabledCipherSuites() {
/*      */     String[] enabled;
/* 1465 */     synchronized (this) {
/* 1466 */       if (!isDestroyed()) {
/* 1467 */         enabled = SSL.getCiphers(this.ssl);
/*      */       } else {
/* 1469 */         return EmptyArrays.EMPTY_STRINGS;
/*      */       } 
/*      */     } 
/* 1472 */     if (enabled == null) {
/* 1473 */       return EmptyArrays.EMPTY_STRINGS;
/*      */     }
/* 1475 */     List<String> enabledList = new ArrayList<String>();
/* 1476 */     synchronized (this) {
/* 1477 */       for (int i = 0; i < enabled.length; i++) {
/* 1478 */         String mapped = toJavaCipherSuite(enabled[i]);
/* 1479 */         String cipher = (mapped == null) ? enabled[i] : mapped;
/* 1480 */         if (OpenSsl.isTlsv13Supported() || !SslUtils.isTLSv13Cipher(cipher))
/*      */         {
/*      */           
/* 1483 */           enabledList.add(cipher); } 
/*      */       } 
/*      */     } 
/* 1486 */     return enabledList.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setEnabledCipherSuites(String[] cipherSuites) {
/* 1492 */     ObjectUtil.checkNotNull(cipherSuites, "cipherSuites");
/*      */     
/* 1494 */     StringBuilder buf = new StringBuilder();
/* 1495 */     StringBuilder bufTLSv13 = new StringBuilder();
/*      */     
/* 1497 */     CipherSuiteConverter.convertToCipherStrings(Arrays.asList(cipherSuites), buf, bufTLSv13, OpenSsl.isBoringSSL());
/* 1498 */     String cipherSuiteSpec = buf.toString();
/* 1499 */     String cipherSuiteSpecTLSv13 = bufTLSv13.toString();
/*      */     
/* 1501 */     if (!OpenSsl.isTlsv13Supported() && !cipherSuiteSpecTLSv13.isEmpty()) {
/* 1502 */       throw new IllegalArgumentException("TLSv1.3 is not supported by this java version.");
/*      */     }
/* 1504 */     synchronized (this) {
/* 1505 */       if (!isDestroyed()) {
/*      */ 
/*      */         
/*      */         try {
/*      */           
/* 1510 */           SSL.setCipherSuites(this.ssl, cipherSuiteSpec, false);
/*      */           
/* 1512 */           if (OpenSsl.isTlsv13Supported())
/*      */           {
/* 1514 */             SSL.setCipherSuites(this.ssl, cipherSuiteSpecTLSv13, true);
/*      */           }
/*      */         }
/* 1517 */         catch (Exception e) {
/* 1518 */           throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec, e);
/*      */         } 
/*      */       } else {
/* 1521 */         throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final String[] getSupportedProtocols() {
/* 1528 */     return OpenSsl.SUPPORTED_PROTOCOLS_SET.<String>toArray(new String[0]);
/*      */   }
/*      */   
/*      */   public final String[] getEnabledProtocols() {
/*      */     int opts;
/* 1533 */     List<String> enabled = new ArrayList<String>(6);
/*      */     
/* 1535 */     enabled.add("SSLv2Hello");
/*      */ 
/*      */     
/* 1538 */     synchronized (this) {
/* 1539 */       if (!isDestroyed()) {
/* 1540 */         opts = SSL.getOptions(this.ssl);
/*      */       } else {
/* 1542 */         return enabled.<String>toArray(new String[0]);
/*      */       } 
/*      */     } 
/* 1545 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1, "TLSv1")) {
/* 1546 */       enabled.add("TLSv1");
/*      */     }
/* 1548 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1")) {
/* 1549 */       enabled.add("TLSv1.1");
/*      */     }
/* 1551 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2")) {
/* 1552 */       enabled.add("TLSv1.2");
/*      */     }
/* 1554 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
/* 1555 */       enabled.add("TLSv1.3");
/*      */     }
/* 1557 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv2, "SSLv2")) {
/* 1558 */       enabled.add("SSLv2");
/*      */     }
/* 1560 */     if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv3, "SSLv3")) {
/* 1561 */       enabled.add("SSLv3");
/*      */     }
/* 1563 */     return enabled.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isProtocolEnabled(int opts, int disableMask, String protocolString) {
/* 1569 */     return ((opts & disableMask) == 0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(protocolString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setEnabledProtocols(String[] protocols) {
/* 1583 */     if (protocols == null)
/*      */     {
/* 1585 */       throw new IllegalArgumentException();
/*      */     }
/* 1587 */     int minProtocolIndex = OPENSSL_OP_NO_PROTOCOLS.length;
/* 1588 */     int maxProtocolIndex = 0;
/* 1589 */     for (String p : protocols) {
/* 1590 */       if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(p)) {
/* 1591 */         throw new IllegalArgumentException("Protocol " + p + " is not supported.");
/*      */       }
/* 1593 */       if (p.equals("SSLv2")) {
/* 1594 */         if (minProtocolIndex > 0) {
/* 1595 */           minProtocolIndex = 0;
/*      */         }
/* 1597 */         if (maxProtocolIndex < 0) {
/* 1598 */           maxProtocolIndex = 0;
/*      */         }
/* 1600 */       } else if (p.equals("SSLv3")) {
/* 1601 */         if (minProtocolIndex > 1) {
/* 1602 */           minProtocolIndex = 1;
/*      */         }
/* 1604 */         if (maxProtocolIndex < 1) {
/* 1605 */           maxProtocolIndex = 1;
/*      */         }
/* 1607 */       } else if (p.equals("TLSv1")) {
/* 1608 */         if (minProtocolIndex > 2) {
/* 1609 */           minProtocolIndex = 2;
/*      */         }
/* 1611 */         if (maxProtocolIndex < 2) {
/* 1612 */           maxProtocolIndex = 2;
/*      */         }
/* 1614 */       } else if (p.equals("TLSv1.1")) {
/* 1615 */         if (minProtocolIndex > 3) {
/* 1616 */           minProtocolIndex = 3;
/*      */         }
/* 1618 */         if (maxProtocolIndex < 3) {
/* 1619 */           maxProtocolIndex = 3;
/*      */         }
/* 1621 */       } else if (p.equals("TLSv1.2")) {
/* 1622 */         if (minProtocolIndex > 4) {
/* 1623 */           minProtocolIndex = 4;
/*      */         }
/* 1625 */         if (maxProtocolIndex < 4) {
/* 1626 */           maxProtocolIndex = 4;
/*      */         }
/* 1628 */       } else if (p.equals("TLSv1.3")) {
/* 1629 */         if (minProtocolIndex > 5) {
/* 1630 */           minProtocolIndex = 5;
/*      */         }
/* 1632 */         if (maxProtocolIndex < 5) {
/* 1633 */           maxProtocolIndex = 5;
/*      */         }
/*      */       } 
/*      */     } 
/* 1637 */     synchronized (this) {
/* 1638 */       if (!isDestroyed()) {
/*      */         
/* 1640 */         SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2 | SSL.SSL_OP_NO_TLSv1_3);
/*      */ 
/*      */         
/* 1643 */         int opts = 0; int i;
/* 1644 */         for (i = 0; i < minProtocolIndex; i++) {
/* 1645 */           opts |= OPENSSL_OP_NO_PROTOCOLS[i];
/*      */         }
/* 1647 */         assert maxProtocolIndex != Integer.MAX_VALUE;
/* 1648 */         for (i = maxProtocolIndex + 1; i < OPENSSL_OP_NO_PROTOCOLS.length; i++) {
/* 1649 */           opts |= OPENSSL_OP_NO_PROTOCOLS[i];
/*      */         }
/*      */ 
/*      */         
/* 1653 */         SSL.setOptions(this.ssl, opts);
/*      */       } else {
/* 1655 */         throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(protocols));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final SSLSession getSession() {
/* 1662 */     return this.session;
/*      */   }
/*      */ 
/*      */   
/*      */   public final synchronized void beginHandshake() throws SSLException {
/* 1667 */     switch (this.handshakeState) {
/*      */       case NPN:
/* 1669 */         checkEngineClosed();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1677 */         this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
/* 1678 */         calculateMaxWrapOverhead();
/*      */ 
/*      */       
/*      */       case NPN_AND_ALPN:
/*      */         return;
/*      */       
/*      */       case ALPN:
/* 1685 */         throw new SSLException("renegotiation unsupported");
/*      */       case NONE:
/* 1687 */         this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
/* 1688 */         if (handshake() == SSLEngineResult.HandshakeStatus.NEED_TASK)
/*      */         {
/* 1690 */           this.needTask = true;
/*      */         }
/* 1692 */         calculateMaxWrapOverhead();
/*      */     } 
/*      */     
/* 1695 */     throw new Error();
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkEngineClosed() throws SSLException {
/* 1700 */     if (isDestroyed()) {
/* 1701 */       throw new SSLException("engine closed");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static SSLEngineResult.HandshakeStatus pendingStatus(int pendingStatus) {
/* 1707 */     return (pendingStatus > 0) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
/*      */   }
/*      */   
/*      */   private static boolean isEmpty(Object[] arr) {
/* 1711 */     return (arr == null || arr.length == 0);
/*      */   }
/*      */   
/*      */   private static boolean isEmpty(byte[] cert) {
/* 1715 */     return (cert == null || cert.length == 0);
/*      */   }
/*      */   
/*      */   private SSLEngineResult.HandshakeStatus handshakeException() throws SSLException {
/* 1719 */     if (SSL.bioLengthNonApplication(this.networkBIO) > 0)
/*      */     {
/* 1721 */       return SSLEngineResult.HandshakeStatus.NEED_WRAP;
/*      */     }
/*      */     
/* 1724 */     Throwable exception = this.handshakeException;
/* 1725 */     assert exception != null;
/* 1726 */     this.handshakeException = null;
/* 1727 */     shutdown();
/* 1728 */     if (exception instanceof SSLHandshakeException) {
/* 1729 */       throw (SSLHandshakeException)exception;
/*      */     }
/* 1731 */     SSLHandshakeException e = new SSLHandshakeException("General OpenSslEngine problem");
/* 1732 */     e.initCause(exception);
/* 1733 */     throw e;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void initHandshakeException(Throwable cause) {
/* 1741 */     assert this.handshakeException == null;
/* 1742 */     this.handshakeException = cause;
/*      */   }
/*      */   
/*      */   private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
/* 1746 */     if (this.needTask) {
/* 1747 */       return SSLEngineResult.HandshakeStatus.NEED_TASK;
/*      */     }
/* 1749 */     if (this.handshakeState == HandshakeState.FINISHED) {
/* 1750 */       return SSLEngineResult.HandshakeStatus.FINISHED;
/*      */     }
/*      */     
/* 1753 */     checkEngineClosed();
/*      */     
/* 1755 */     if (this.handshakeException != null) {
/*      */ 
/*      */       
/* 1758 */       if (SSL.doHandshake(this.ssl) <= 0)
/*      */       {
/* 1760 */         SSL.clearError();
/*      */       }
/* 1762 */       return handshakeException();
/*      */     } 
/*      */ 
/*      */     
/* 1766 */     this.engineMap.add(this);
/* 1767 */     if (this.lastAccessed == -1L) {
/* 1768 */       this.lastAccessed = System.currentTimeMillis();
/*      */     }
/*      */     
/* 1771 */     int code = SSL.doHandshake(this.ssl);
/* 1772 */     if (code <= 0) {
/* 1773 */       int sslError = SSL.getError(this.ssl, code);
/* 1774 */       if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE) {
/* 1775 */         return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
/*      */       }
/*      */       
/* 1778 */       if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION)
/*      */       {
/*      */         
/* 1781 */         return SSLEngineResult.HandshakeStatus.NEED_TASK;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1786 */       if (this.handshakeException != null) {
/* 1787 */         return handshakeException();
/*      */       }
/*      */ 
/*      */       
/* 1791 */       throw shutdownWithError("SSL_do_handshake", sslError);
/*      */     } 
/*      */     
/* 1794 */     if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
/* 1795 */       return SSLEngineResult.HandshakeStatus.NEED_WRAP;
/*      */     }
/*      */     
/* 1798 */     this.session.handshakeFinished();
/* 1799 */     return SSLEngineResult.HandshakeStatus.FINISHED;
/*      */   }
/*      */ 
/*      */   
/*      */   private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus status) throws SSLException {
/* 1804 */     if (status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && this.handshakeState != HandshakeState.FINISHED)
/*      */     {
/*      */       
/* 1807 */       return handshake();
/*      */     }
/* 1809 */     return status;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 1815 */     if (needPendingStatus()) {
/* 1816 */       if (this.needTask)
/*      */       {
/* 1818 */         return SSLEngineResult.HandshakeStatus.NEED_TASK;
/*      */       }
/* 1820 */       return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
/*      */     } 
/* 1822 */     return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*      */   }
/*      */ 
/*      */   
/*      */   private SSLEngineResult.HandshakeStatus getHandshakeStatus(int pending) {
/* 1827 */     if (needPendingStatus()) {
/* 1828 */       if (this.needTask)
/*      */       {
/* 1830 */         return SSLEngineResult.HandshakeStatus.NEED_TASK;
/*      */       }
/* 1832 */       return pendingStatus(pending);
/*      */     } 
/* 1834 */     return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
/*      */   }
/*      */   
/*      */   private boolean needPendingStatus() {
/* 1838 */     return (this.handshakeState != HandshakeState.NOT_STARTED && !isDestroyed() && (this.handshakeState != HandshakeState.FINISHED || 
/* 1839 */       isInboundDone() || isOutboundDone()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String toJavaCipherSuite(String openSslCipherSuite) {
/* 1846 */     if (openSslCipherSuite == null) {
/* 1847 */       return null;
/*      */     }
/*      */     
/* 1850 */     String version = SSL.getVersion(this.ssl);
/* 1851 */     String prefix = toJavaCipherSuitePrefix(version);
/* 1852 */     return CipherSuiteConverter.toJava(openSslCipherSuite, prefix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String toJavaCipherSuitePrefix(String protocolVersion) {
/*      */     char c;
/* 1860 */     if (protocolVersion == null || protocolVersion.isEmpty()) {
/* 1861 */       c = Character.MIN_VALUE;
/*      */     } else {
/* 1863 */       c = protocolVersion.charAt(0);
/*      */     } 
/*      */     
/* 1866 */     switch (c) {
/*      */       case 'T':
/* 1868 */         return "TLS";
/*      */       case 'S':
/* 1870 */         return "SSL";
/*      */     } 
/* 1872 */     return "UNKNOWN";
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setUseClientMode(boolean clientMode) {
/* 1878 */     if (clientMode != this.clientMode) {
/* 1879 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean getUseClientMode() {
/* 1885 */     return this.clientMode;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void setNeedClientAuth(boolean b) {
/* 1890 */     setClientAuth(b ? ClientAuth.REQUIRE : ClientAuth.NONE);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean getNeedClientAuth() {
/* 1895 */     return (this.clientAuth == ClientAuth.REQUIRE);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void setWantClientAuth(boolean b) {
/* 1900 */     setClientAuth(b ? ClientAuth.OPTIONAL : ClientAuth.NONE);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean getWantClientAuth() {
/* 1905 */     return (this.clientAuth == ClientAuth.OPTIONAL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final synchronized void setVerify(int verifyMode, int depth) {
/* 1914 */     SSL.setVerify(this.ssl, verifyMode, depth);
/*      */   }
/*      */   
/*      */   private void setClientAuth(ClientAuth mode) {
/* 1918 */     if (this.clientMode) {
/*      */       return;
/*      */     }
/* 1921 */     synchronized (this) {
/* 1922 */       if (this.clientAuth == mode) {
/*      */         return;
/*      */       }
/*      */       
/* 1926 */       switch (mode) {
/*      */         case NONE:
/* 1928 */           SSL.setVerify(this.ssl, 0, 10);
/*      */           break;
/*      */         case ALPN:
/* 1931 */           SSL.setVerify(this.ssl, 2, 10);
/*      */           break;
/*      */         case NPN:
/* 1934 */           SSL.setVerify(this.ssl, 1, 10);
/*      */           break;
/*      */         default:
/* 1937 */           throw new Error(mode.toString());
/*      */       } 
/* 1939 */       this.clientAuth = mode;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void setEnableSessionCreation(boolean b) {
/* 1945 */     if (b) {
/* 1946 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean getEnableSessionCreation() {
/* 1952 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*      */   public final synchronized SSLParameters getSSLParameters() {
/* 1958 */     SSLParameters sslParameters = super.getSSLParameters();
/*      */     
/* 1960 */     int version = PlatformDependent.javaVersion();
/* 1961 */     if (version >= 7) {
/* 1962 */       sslParameters.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
/* 1963 */       Java7SslParametersUtils.setAlgorithmConstraints(sslParameters, this.algorithmConstraints);
/* 1964 */       if (version >= 8) {
/* 1965 */         if (this.sniHostNames != null) {
/* 1966 */           Java8SslUtils.setSniHostNames(sslParameters, this.sniHostNames);
/*      */         }
/* 1968 */         if (!isDestroyed()) {
/* 1969 */           Java8SslUtils.setUseCipherSuitesOrder(sslParameters, 
/* 1970 */               ((SSL.getOptions(this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0));
/*      */         }
/*      */         
/* 1973 */         Java8SslUtils.setSNIMatchers(sslParameters, this.matchers);
/*      */       } 
/*      */     } 
/* 1976 */     return sslParameters;
/*      */   }
/*      */ 
/*      */   
/*      */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*      */   public final synchronized void setSSLParameters(SSLParameters sslParameters) {
/* 1982 */     int version = PlatformDependent.javaVersion();
/* 1983 */     if (version >= 7) {
/* 1984 */       if (sslParameters.getAlgorithmConstraints() != null) {
/* 1985 */         throw new IllegalArgumentException("AlgorithmConstraints are not supported.");
/*      */       }
/*      */       
/* 1988 */       if (version >= 8) {
/* 1989 */         if (!isDestroyed()) {
/* 1990 */           if (this.clientMode) {
/* 1991 */             List<String> sniHostNames = Java8SslUtils.getSniHostNames(sslParameters);
/* 1992 */             for (String name : sniHostNames) {
/* 1993 */               SSL.setTlsExtHostName(this.ssl, name);
/*      */             }
/* 1995 */             this.sniHostNames = sniHostNames;
/*      */           } 
/* 1997 */           if (Java8SslUtils.getUseCipherSuitesOrder(sslParameters)) {
/* 1998 */             SSL.setOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
/*      */           } else {
/* 2000 */             SSL.clearOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
/*      */           } 
/*      */         } 
/* 2003 */         this.matchers = sslParameters.getSNIMatchers();
/*      */       } 
/*      */       
/* 2006 */       String endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
/* 2007 */       boolean endPointVerificationEnabled = isEndPointVerificationEnabled(endPointIdentificationAlgorithm);
/*      */ 
/*      */ 
/*      */       
/* 2011 */       if (this.clientMode && endPointVerificationEnabled) {
/* 2012 */         SSL.setVerify(this.ssl, 2, -1);
/*      */       }
/*      */       
/* 2015 */       this.endPointIdentificationAlgorithm = endPointIdentificationAlgorithm;
/* 2016 */       this.algorithmConstraints = sslParameters.getAlgorithmConstraints();
/*      */     } 
/* 2018 */     super.setSSLParameters(sslParameters);
/*      */   }
/*      */   
/*      */   private static boolean isEndPointVerificationEnabled(String endPointIdentificationAlgorithm) {
/* 2022 */     return (endPointIdentificationAlgorithm != null && !endPointIdentificationAlgorithm.isEmpty());
/*      */   }
/*      */   
/*      */   private boolean isDestroyed() {
/* 2026 */     return this.destroyed;
/*      */   }
/*      */   
/*      */   final boolean checkSniHostnameMatch(byte[] hostname) {
/* 2030 */     return Java8SslUtils.checkSniHostnameMatch(this.matchers, hostname);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getNegotiatedApplicationProtocol() {
/* 2035 */     return this.applicationProtocol;
/*      */   }
/*      */   
/*      */   private static long bufferAddress(ByteBuffer b) {
/* 2039 */     assert b.isDirect();
/* 2040 */     if (PlatformDependent.hasUnsafe()) {
/* 2041 */       return PlatformDependent.directBufferAddress(b);
/*      */     }
/* 2043 */     return Buffer.address(b);
/*      */   }
/*      */ 
/*      */   
/*      */   private final class DefaultOpenSslSession
/*      */     implements OpenSslSession
/*      */   {
/*      */     private final OpenSslSessionContext sessionContext;
/*      */     
/*      */     private X509Certificate[] x509PeerCerts;
/*      */     private Certificate[] peerCerts;
/*      */     private String protocol;
/*      */     private String cipher;
/*      */     private byte[] id;
/*      */     private long creationTime;
/* 2058 */     private volatile int applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
/*      */     
/*      */     private Map<String, Object> values;
/*      */ 
/*      */     
/*      */     DefaultOpenSslSession(OpenSslSessionContext sessionContext) {
/* 2064 */       this.sessionContext = sessionContext;
/*      */     }
/*      */     
/*      */     private SSLSessionBindingEvent newSSLSessionBindingEvent(String name) {
/* 2068 */       return new SSLSessionBindingEvent(ReferenceCountedOpenSslEngine.this.session, name);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] getId() {
/* 2073 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2074 */         if (this.id == null) {
/* 2075 */           return EmptyArrays.EMPTY_BYTES;
/*      */         }
/* 2077 */         return (byte[])this.id.clone();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public SSLSessionContext getSessionContext() {
/* 2083 */       return this.sessionContext;
/*      */     }
/*      */ 
/*      */     
/*      */     public long getCreationTime() {
/* 2088 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2089 */         if (this.creationTime == 0L && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/* 2090 */           this.creationTime = SSL.getTime(ReferenceCountedOpenSslEngine.this.ssl) * 1000L;
/*      */         }
/*      */       } 
/* 2093 */       return this.creationTime;
/*      */     }
/*      */ 
/*      */     
/*      */     public long getLastAccessedTime() {
/* 2098 */       long lastAccessed = ReferenceCountedOpenSslEngine.this.lastAccessed;
/*      */       
/* 2100 */       return (lastAccessed == -1L) ? getCreationTime() : lastAccessed;
/*      */     }
/*      */ 
/*      */     
/*      */     public void invalidate() {
/* 2105 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2106 */         if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/* 2107 */           SSL.setTimeout(ReferenceCountedOpenSslEngine.this.ssl, 0L);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isValid() {
/* 2114 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2115 */         if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/* 2116 */           return (System.currentTimeMillis() - SSL.getTimeout(ReferenceCountedOpenSslEngine.this.ssl) * 1000L < SSL.getTime(ReferenceCountedOpenSslEngine.this.ssl) * 1000L);
/*      */         }
/*      */       } 
/* 2119 */       return false;
/*      */     }
/*      */     
/*      */     public void putValue(String name, Object value) {
/*      */       Object old;
/* 2124 */       ObjectUtil.checkNotNull(name, "name");
/* 2125 */       ObjectUtil.checkNotNull(value, "value");
/*      */ 
/*      */       
/* 2128 */       synchronized (this) {
/* 2129 */         Map<String, Object> values = this.values;
/* 2130 */         if (values == null)
/*      */         {
/* 2132 */           values = this.values = new HashMap<String, Object>(2);
/*      */         }
/* 2134 */         old = values.put(name, value);
/*      */       } 
/*      */       
/* 2137 */       if (value instanceof SSLSessionBindingListener)
/*      */       {
/* 2139 */         ((SSLSessionBindingListener)value).valueBound(newSSLSessionBindingEvent(name));
/*      */       }
/* 2141 */       notifyUnbound(old, name);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getValue(String name) {
/* 2146 */       ObjectUtil.checkNotNull(name, "name");
/* 2147 */       synchronized (this) {
/* 2148 */         if (this.values == null) {
/* 2149 */           return null;
/*      */         }
/* 2151 */         return this.values.get(name);
/*      */       } 
/*      */     }
/*      */     
/*      */     public void removeValue(String name) {
/*      */       Object old;
/* 2157 */       ObjectUtil.checkNotNull(name, "name");
/*      */ 
/*      */       
/* 2160 */       synchronized (this) {
/* 2161 */         Map<String, Object> values = this.values;
/* 2162 */         if (values == null) {
/*      */           return;
/*      */         }
/* 2165 */         old = values.remove(name);
/*      */       } 
/*      */       
/* 2168 */       notifyUnbound(old, name);
/*      */     }
/*      */ 
/*      */     
/*      */     public String[] getValueNames() {
/* 2173 */       synchronized (this) {
/* 2174 */         Map<String, Object> values = this.values;
/* 2175 */         if (values == null || values.isEmpty()) {
/* 2176 */           return EmptyArrays.EMPTY_STRINGS;
/*      */         }
/* 2178 */         return (String[])values.keySet().toArray((Object[])new String[0]);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void notifyUnbound(Object value, String name) {
/* 2183 */       if (value instanceof SSLSessionBindingListener)
/*      */       {
/* 2185 */         ((SSLSessionBindingListener)value).valueUnbound(newSSLSessionBindingEvent(name));
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handshakeFinished() throws SSLException {
/* 2195 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2196 */         if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/* 2197 */           this.id = SSL.getSessionId(ReferenceCountedOpenSslEngine.this.ssl);
/* 2198 */           this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(SSL.getCipherForSSL(ReferenceCountedOpenSslEngine.this.ssl));
/* 2199 */           this.protocol = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
/*      */           
/* 2201 */           initPeerCerts();
/* 2202 */           selectApplicationProtocol();
/* 2203 */           ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
/*      */           
/* 2205 */           ReferenceCountedOpenSslEngine.this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.FINISHED;
/*      */         } else {
/* 2207 */           throw new SSLException("Already closed");
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void initPeerCerts() {
/* 2218 */       byte[][] chain = SSL.getPeerCertChain(ReferenceCountedOpenSslEngine.this.ssl);
/* 2219 */       if (ReferenceCountedOpenSslEngine.this.clientMode) {
/* 2220 */         if (ReferenceCountedOpenSslEngine.isEmpty((Object[])chain)) {
/* 2221 */           this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
/* 2222 */           this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
/*      */         } else {
/* 2224 */           this.peerCerts = new Certificate[chain.length];
/* 2225 */           this.x509PeerCerts = new X509Certificate[chain.length];
/* 2226 */           initCerts(chain, 0);
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 2234 */         byte[] clientCert = SSL.getPeerCertificate(ReferenceCountedOpenSslEngine.this.ssl);
/* 2235 */         if (ReferenceCountedOpenSslEngine.isEmpty(clientCert)) {
/* 2236 */           this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
/* 2237 */           this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
/*      */         }
/* 2239 */         else if (ReferenceCountedOpenSslEngine.isEmpty((Object[])chain)) {
/* 2240 */           this.peerCerts = new Certificate[] { new OpenSslX509Certificate(clientCert) };
/* 2241 */           this.x509PeerCerts = new X509Certificate[] { new OpenSslJavaxX509Certificate(clientCert) };
/*      */         } else {
/* 2243 */           this.peerCerts = new Certificate[chain.length + 1];
/* 2244 */           this.x509PeerCerts = new X509Certificate[chain.length + 1];
/* 2245 */           this.peerCerts[0] = new OpenSslX509Certificate(clientCert);
/* 2246 */           this.x509PeerCerts[0] = new OpenSslJavaxX509Certificate(clientCert);
/* 2247 */           initCerts(chain, 1);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void initCerts(byte[][] chain, int startPos) {
/* 2254 */       for (int i = 0; i < chain.length; i++) {
/* 2255 */         int certPos = startPos + i;
/* 2256 */         this.peerCerts[certPos] = new OpenSslX509Certificate(chain[i]);
/* 2257 */         this.x509PeerCerts[certPos] = new OpenSslJavaxX509Certificate(chain[i]);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void selectApplicationProtocol() throws SSLException {
/*      */       String applicationProtocol;
/* 2265 */       ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior = ReferenceCountedOpenSslEngine.this.apn.selectedListenerFailureBehavior();
/* 2266 */       List<String> protocols = ReferenceCountedOpenSslEngine.this.apn.protocols();
/*      */       
/* 2268 */       switch (ReferenceCountedOpenSslEngine.this.apn.protocol()) {
/*      */         case NONE:
/*      */           return;
/*      */ 
/*      */         
/*      */         case ALPN:
/* 2274 */           applicationProtocol = SSL.getAlpnSelected(ReferenceCountedOpenSslEngine.this.ssl);
/* 2275 */           if (applicationProtocol != null) {
/* 2276 */             ReferenceCountedOpenSslEngine.this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
/*      */           }
/*      */ 
/*      */         
/*      */         case NPN:
/* 2281 */           applicationProtocol = SSL.getNextProtoNegotiated(ReferenceCountedOpenSslEngine.this.ssl);
/* 2282 */           if (applicationProtocol != null) {
/* 2283 */             ReferenceCountedOpenSslEngine.this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
/*      */           }
/*      */ 
/*      */         
/*      */         case NPN_AND_ALPN:
/* 2288 */           applicationProtocol = SSL.getAlpnSelected(ReferenceCountedOpenSslEngine.this.ssl);
/* 2289 */           if (applicationProtocol == null) {
/* 2290 */             applicationProtocol = SSL.getNextProtoNegotiated(ReferenceCountedOpenSslEngine.this.ssl);
/*      */           }
/* 2292 */           if (applicationProtocol != null) {
/* 2293 */             ReferenceCountedOpenSslEngine.this.applicationProtocol = selectApplicationProtocol(protocols, behavior, applicationProtocol);
/*      */           }
/*      */       } 
/*      */ 
/*      */       
/* 2298 */       throw new Error();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private String selectApplicationProtocol(List<String> protocols, ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior, String applicationProtocol) throws SSLException {
/* 2305 */       if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
/* 2306 */         return applicationProtocol;
/*      */       }
/* 2308 */       int size = protocols.size();
/* 2309 */       assert size > 0;
/* 2310 */       if (protocols.contains(applicationProtocol)) {
/* 2311 */         return applicationProtocol;
/*      */       }
/* 2313 */       if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
/* 2314 */         return protocols.get(size - 1);
/*      */       }
/* 2316 */       throw new SSLException("unknown protocol " + applicationProtocol);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
/* 2324 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2325 */         if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.peerCerts)) {
/* 2326 */           throw new SSLPeerUnverifiedException("peer not verified");
/*      */         }
/* 2328 */         return (Certificate[])this.peerCerts.clone();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Certificate[] getLocalCertificates() {
/* 2334 */       Certificate[] localCerts = ReferenceCountedOpenSslEngine.this.localCertificateChain;
/* 2335 */       if (localCerts == null) {
/* 2336 */         return null;
/*      */       }
/* 2338 */       return (Certificate[])localCerts.clone();
/*      */     }
/*      */ 
/*      */     
/*      */     public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
/* 2343 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2344 */         if (ReferenceCountedOpenSslEngine.isEmpty((Object[])this.x509PeerCerts)) {
/* 2345 */           throw new SSLPeerUnverifiedException("peer not verified");
/*      */         }
/* 2347 */         return (X509Certificate[])this.x509PeerCerts.clone();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
/* 2353 */       Certificate[] peer = getPeerCertificates();
/*      */ 
/*      */       
/* 2356 */       return ((X509Certificate)peer[0]).getSubjectX500Principal();
/*      */     }
/*      */ 
/*      */     
/*      */     public Principal getLocalPrincipal() {
/* 2361 */       Certificate[] local = ReferenceCountedOpenSslEngine.this.localCertificateChain;
/* 2362 */       if (local == null || local.length == 0) {
/* 2363 */         return null;
/*      */       }
/* 2365 */       return ((X509Certificate)local[0]).getIssuerX500Principal();
/*      */     }
/*      */ 
/*      */     
/*      */     public String getCipherSuite() {
/* 2370 */       synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2371 */         if (this.cipher == null) {
/* 2372 */           return "SSL_NULL_WITH_NULL_NULL";
/*      */         }
/* 2374 */         return this.cipher;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public String getProtocol() {
/* 2380 */       String protocol = this.protocol;
/* 2381 */       if (protocol == null) {
/* 2382 */         synchronized (ReferenceCountedOpenSslEngine.this) {
/* 2383 */           if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
/* 2384 */             protocol = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
/*      */           } else {
/* 2386 */             protocol = "";
/*      */           } 
/*      */         } 
/*      */       }
/* 2390 */       return protocol;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getPeerHost() {
/* 2395 */       return ReferenceCountedOpenSslEngine.this.getPeerHost();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPeerPort() {
/* 2400 */       return ReferenceCountedOpenSslEngine.this.getPeerPort();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPacketBufferSize() {
/* 2405 */       return ReferenceCountedOpenSslEngine.this.maxEncryptedPacketLength();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getApplicationBufferSize() {
/* 2410 */       return this.applicationBufferSize;
/*      */     }
/*      */ 
/*      */     
/*      */     public void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
/* 2415 */       if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE)
/* 2416 */         this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE; 
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ReferenceCountedOpenSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */