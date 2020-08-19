/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.eclipse.jetty.alpn.ALPN;
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
/*     */ 
/*     */ abstract class JettyAlpnSslEngine
/*     */   extends JdkSslEngine
/*     */ {
/*  34 */   private static final boolean available = initAvailable();
/*     */   
/*     */   static boolean isAvailable() {
/*  37 */     return available;
/*     */   }
/*     */   
/*     */   private static boolean initAvailable() {
/*  41 */     if (PlatformDependent.javaVersion() <= 8) {
/*     */       
/*     */       try {
/*  44 */         Class.forName("sun.security.ssl.ALPNExtension", true, null);
/*  45 */         return true;
/*  46 */       } catch (Throwable throwable) {}
/*     */     }
/*     */ 
/*     */     
/*  50 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static JettyAlpnSslEngine newClientEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
/*  55 */     return new ClientEngine(engine, applicationNegotiator);
/*     */   }
/*     */ 
/*     */   
/*     */   static JettyAlpnSslEngine newServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
/*  60 */     return new ServerEngine(engine, applicationNegotiator);
/*     */   }
/*     */   
/*     */   private JettyAlpnSslEngine(SSLEngine engine) {
/*  64 */     super(engine);
/*     */   }
/*     */   
/*     */   private static final class ClientEngine extends JettyAlpnSslEngine {
/*     */     ClientEngine(SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
/*  69 */       super(engine);
/*  70 */       ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
/*  71 */       final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(applicationNegotiator
/*  72 */           .protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
/*     */       
/*  74 */       ALPN.put(engine, (ALPN.Provider)new ALPN.ClientProvider()
/*     */           {
/*     */             public List<String> protocols() {
/*  77 */               return applicationNegotiator.protocols();
/*     */             }
/*     */ 
/*     */             
/*     */             public void selected(String protocol) throws SSLException {
/*     */               try {
/*  83 */                 protocolListener.selected(protocol);
/*  84 */               } catch (Throwable t) {
/*  85 */                 throw SslUtils.toSSLHandshakeException(t);
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/*     */             public void unsupported() {
/*  91 */               protocolListener.unsupported();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public void closeInbound() throws SSLException {
/*     */       try {
/*  99 */         ALPN.remove(getWrappedEngine());
/*     */       } finally {
/* 101 */         super.closeInbound();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void closeOutbound() {
/*     */       try {
/* 108 */         ALPN.remove(getWrappedEngine());
/*     */       } finally {
/* 110 */         super.closeOutbound();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ServerEngine extends JettyAlpnSslEngine {
/*     */     ServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
/* 117 */       super(engine);
/* 118 */       ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
/* 119 */       final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory()
/* 120 */           .newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
/*     */       
/* 122 */       ALPN.put(engine, (ALPN.Provider)new ALPN.ServerProvider()
/*     */           {
/*     */             public String select(List<String> protocols) throws SSLException {
/*     */               try {
/* 126 */                 return protocolSelector.select(protocols);
/* 127 */               } catch (Throwable t) {
/* 128 */                 throw SslUtils.toSSLHandshakeException(t);
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/*     */             public void unsupported() {
/* 134 */               protocolSelector.unsupported();
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public void closeInbound() throws SSLException {
/*     */       try {
/* 142 */         ALPN.remove(getWrappedEngine());
/*     */       } finally {
/* 144 */         super.closeInbound();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void closeOutbound() {
/*     */       try {
/* 151 */         ALPN.remove(getWrappedEngine());
/*     */       } finally {
/* 153 */         super.closeOutbound();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JettyAlpnSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */