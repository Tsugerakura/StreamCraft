/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLHandshakeException;
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
/*     */ class JdkBaseApplicationProtocolNegotiator
/*     */   implements JdkApplicationProtocolNegotiator
/*     */ {
/*     */   private final List<String> protocols;
/*     */   private final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory;
/*     */   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory;
/*     */   private final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory;
/*     */   
/*     */   JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
/*  47 */     this(wrapperFactory, selectorFactory, listenerFactory, ApplicationProtocolUtil.toList(protocols));
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
/*     */   JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String... protocols) {
/*  60 */     this(wrapperFactory, selectorFactory, listenerFactory, ApplicationProtocolUtil.toList(protocols));
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
/*     */   private JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, List<String> protocols) {
/*  73 */     this.wrapperFactory = (JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)ObjectUtil.checkNotNull(wrapperFactory, "wrapperFactory");
/*  74 */     this.selectorFactory = (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)ObjectUtil.checkNotNull(selectorFactory, "selectorFactory");
/*  75 */     this.listenerFactory = (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)ObjectUtil.checkNotNull(listenerFactory, "listenerFactory");
/*  76 */     this.protocols = Collections.unmodifiableList((List<? extends String>)ObjectUtil.checkNotNull(protocols, "protocols"));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> protocols() {
/*  81 */     return this.protocols;
/*     */   }
/*     */ 
/*     */   
/*     */   public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
/*  86 */     return this.selectorFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
/*  91 */     return this.listenerFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
/*  96 */     return this.wrapperFactory;
/*     */   }
/*     */   
/*  99 */   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory()
/*     */     {
/*     */       public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine engine, Set<String> supportedProtocols) {
/* 102 */         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelector((JdkSslEngine)engine, supportedProtocols);
/*     */       }
/*     */     };
/*     */   
/* 106 */   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory NO_FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory()
/*     */     {
/*     */       public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine engine, Set<String> supportedProtocols) {
/* 109 */         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelector((JdkSslEngine)engine, supportedProtocols);
/*     */       }
/*     */     };
/*     */   
/* 113 */   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory()
/*     */     {
/*     */       public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine engine, List<String> supportedProtocols)
/*     */       {
/* 117 */         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelectionListener((JdkSslEngine)engine, supportedProtocols);
/*     */       }
/*     */     };
/*     */   
/* 121 */   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory NO_FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory()
/*     */     {
/*     */       public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine engine, List<String> supportedProtocols)
/*     */       {
/* 125 */         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelectionListener((JdkSslEngine)engine, supportedProtocols);
/*     */       }
/*     */     };
/*     */   
/*     */   static class NoFailProtocolSelector implements JdkApplicationProtocolNegotiator.ProtocolSelector {
/*     */     private final JdkSslEngine engineWrapper;
/*     */     private final Set<String> supportedProtocols;
/*     */     
/*     */     NoFailProtocolSelector(JdkSslEngine engineWrapper, Set<String> supportedProtocols) {
/* 134 */       this.engineWrapper = engineWrapper;
/* 135 */       this.supportedProtocols = supportedProtocols;
/*     */     }
/*     */ 
/*     */     
/*     */     public void unsupported() {
/* 140 */       this.engineWrapper.setNegotiatedApplicationProtocol(null);
/*     */     }
/*     */ 
/*     */     
/*     */     public String select(List<String> protocols) throws Exception {
/* 145 */       for (String p : this.supportedProtocols) {
/* 146 */         if (protocols.contains(p)) {
/* 147 */           this.engineWrapper.setNegotiatedApplicationProtocol(p);
/* 148 */           return p;
/*     */         } 
/*     */       } 
/* 151 */       return noSelectMatchFound();
/*     */     }
/*     */     
/*     */     public String noSelectMatchFound() throws Exception {
/* 155 */       this.engineWrapper.setNegotiatedApplicationProtocol(null);
/* 156 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class FailProtocolSelector extends NoFailProtocolSelector {
/*     */     FailProtocolSelector(JdkSslEngine engineWrapper, Set<String> supportedProtocols) {
/* 162 */       super(engineWrapper, supportedProtocols);
/*     */     }
/*     */ 
/*     */     
/*     */     public String noSelectMatchFound() throws Exception {
/* 167 */       throw new SSLHandshakeException("Selected protocol is not supported");
/*     */     }
/*     */   }
/*     */   
/*     */   private static class NoFailProtocolSelectionListener implements JdkApplicationProtocolNegotiator.ProtocolSelectionListener {
/*     */     private final JdkSslEngine engineWrapper;
/*     */     private final List<String> supportedProtocols;
/*     */     
/*     */     NoFailProtocolSelectionListener(JdkSslEngine engineWrapper, List<String> supportedProtocols) {
/* 176 */       this.engineWrapper = engineWrapper;
/* 177 */       this.supportedProtocols = supportedProtocols;
/*     */     }
/*     */ 
/*     */     
/*     */     public void unsupported() {
/* 182 */       this.engineWrapper.setNegotiatedApplicationProtocol(null);
/*     */     }
/*     */ 
/*     */     
/*     */     public void selected(String protocol) throws Exception {
/* 187 */       if (this.supportedProtocols.contains(protocol)) {
/* 188 */         this.engineWrapper.setNegotiatedApplicationProtocol(protocol);
/*     */       } else {
/* 190 */         noSelectedMatchFound(protocol);
/*     */       } 
/*     */     }
/*     */     
/*     */     protected void noSelectedMatchFound(String protocol) throws Exception {}
/*     */   }
/*     */   
/*     */   private static final class FailProtocolSelectionListener
/*     */     extends NoFailProtocolSelectionListener
/*     */   {
/*     */     FailProtocolSelectionListener(JdkSslEngine engineWrapper, List<String> supportedProtocols) {
/* 201 */       super(engineWrapper, supportedProtocols);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void noSelectedMatchFound(String protocol) throws Exception {
/* 206 */       throw new SSLHandshakeException("No compatible protocols found");
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkBaseApplicationProtocolNegotiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */