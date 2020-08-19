/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*     */ @Deprecated
/*     */ public final class JdkAlpnApplicationProtocolNegotiator
/*     */   extends JdkBaseApplicationProtocolNegotiator
/*     */ {
/*  30 */   private static final boolean AVAILABLE = (Conscrypt.isAvailable() || 
/*  31 */     jdkAlpnSupported() || 
/*  32 */     JettyAlpnSslEngine.isAvailable());
/*     */   
/*  34 */   private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory ALPN_WRAPPER = AVAILABLE ? new AlpnWrapper() : new FailureWrapper();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(Iterable<String> protocols) {
/*  41 */     this(false, protocols);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(String... protocols) {
/*  49 */     this(false, protocols);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, Iterable<String> protocols) {
/*  58 */     this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, String... protocols) {
/*  67 */     this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, Iterable<String> protocols) {
/*  78 */     this(serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, protocols);
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
/*     */   public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, String... protocols) {
/*  91 */     this(serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY, clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY, protocols);
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
/*     */   public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
/* 104 */     super(ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String... protocols) {
/* 115 */     super(ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
/*     */   }
/*     */   
/*     */   private static final class FailureWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory {
/*     */     private FailureWrapper() {}
/*     */     
/*     */     public SSLEngine wrapSslEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
/* 122 */       throw new RuntimeException("ALPN unsupported. Is your classpath configured correctly? For Conscrypt, add the appropriate Conscrypt JAR to classpath and set the security provider. For Jetty-ALPN, see http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-starting");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class AlpnWrapper
/*     */     extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory
/*     */   {
/*     */     private AlpnWrapper() {}
/*     */     
/*     */     public SSLEngine wrapSslEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
/* 133 */       if (Conscrypt.isEngineSupported(engine)) {
/* 134 */         return isServer ? ConscryptAlpnSslEngine.newServerEngine(engine, alloc, applicationNegotiator) : 
/* 135 */           ConscryptAlpnSslEngine.newClientEngine(engine, alloc, applicationNegotiator);
/*     */       }
/* 137 */       if (JdkAlpnApplicationProtocolNegotiator.jdkAlpnSupported()) {
/* 138 */         return new Java9SslEngine(engine, applicationNegotiator, isServer);
/*     */       }
/* 140 */       if (JettyAlpnSslEngine.isAvailable()) {
/* 141 */         return isServer ? JettyAlpnSslEngine.newServerEngine(engine, applicationNegotiator) : 
/* 142 */           JettyAlpnSslEngine.newClientEngine(engine, applicationNegotiator);
/*     */       }
/* 144 */       throw new RuntimeException("Unable to wrap SSLEngine of type " + engine.getClass().getName());
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean jdkAlpnSupported() {
/* 149 */     return (PlatformDependent.javaVersion() >= 9 && Java9SslUtils.supportsAlpn());
/*     */   }
/*     */   
/*     */   static boolean isAlpnSupported() {
/* 153 */     return AVAILABLE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkAlpnApplicationProtocolNegotiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */