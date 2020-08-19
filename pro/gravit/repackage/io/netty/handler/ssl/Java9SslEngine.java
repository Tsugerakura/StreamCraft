/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.function.BiFunction;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ final class Java9SslEngine
/*     */   extends JdkSslEngine
/*     */ {
/*     */   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener selectionListener;
/*     */   private final AlpnSelector alpnSelector;
/*     */   
/*     */   private final class AlpnSelector
/*     */     implements BiFunction<SSLEngine, List<String>, String>
/*     */   {
/*     */     private final JdkApplicationProtocolNegotiator.ProtocolSelector selector;
/*     */     private boolean called;
/*     */     
/*     */     AlpnSelector(JdkApplicationProtocolNegotiator.ProtocolSelector selector) {
/*  44 */       this.selector = selector;
/*     */     }
/*     */ 
/*     */     
/*     */     public String apply(SSLEngine sslEngine, List<String> strings) {
/*  49 */       assert !this.called;
/*  50 */       this.called = true;
/*     */       
/*     */       try {
/*  53 */         String selected = this.selector.select(strings);
/*  54 */         return (selected == null) ? "" : selected;
/*  55 */       } catch (Exception cause) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  60 */         return null;
/*     */       } 
/*     */     }
/*     */     
/*     */     void checkUnsupported() {
/*  65 */       if (this.called) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  72 */       String protocol = Java9SslEngine.this.getApplicationProtocol();
/*  73 */       assert protocol != null;
/*     */       
/*  75 */       if (protocol.isEmpty())
/*     */       {
/*  77 */         this.selector.unsupported();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   Java9SslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
/*  83 */     super(engine);
/*  84 */     if (isServer) {
/*  85 */       this.selectionListener = null;
/*  86 */       this
/*  87 */         .alpnSelector = new AlpnSelector(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())));
/*  88 */       Java9SslUtils.setHandshakeApplicationProtocolSelector(engine, this.alpnSelector);
/*     */     } else {
/*  90 */       this
/*  91 */         .selectionListener = applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols());
/*  92 */       this.alpnSelector = null;
/*  93 */       Java9SslUtils.setApplicationProtocols(engine, applicationNegotiator.protocols());
/*     */     } 
/*     */   }
/*     */   
/*     */   private SSLEngineResult verifyProtocolSelection(SSLEngineResult result) throws SSLException {
/*  98 */     if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
/*  99 */       if (this.alpnSelector == null) {
/*     */         
/*     */         try {
/* 102 */           String protocol = getApplicationProtocol();
/* 103 */           assert protocol != null;
/* 104 */           if (protocol.isEmpty()) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 109 */             this.selectionListener.unsupported();
/*     */           } else {
/* 111 */             this.selectionListener.selected(protocol);
/*     */           } 
/* 113 */         } catch (Throwable e) {
/* 114 */           throw SslUtils.toSSLHandshakeException(e);
/*     */         } 
/*     */       } else {
/* 117 */         assert this.selectionListener == null;
/* 118 */         this.alpnSelector.checkUnsupported();
/*     */       } 
/*     */     }
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/* 126 */     return verifyProtocolSelection(super.wrap(src, dst));
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
/* 131 */     return verifyProtocolSelection(super.wrap(srcs, dst));
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int len, ByteBuffer dst) throws SSLException {
/* 136 */     return verifyProtocolSelection(super.wrap(srcs, offset, len, dst));
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
/* 141 */     return verifyProtocolSelection(super.unwrap(src, dst));
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
/* 146 */     return verifyProtocolSelection(super.unwrap(src, dsts));
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dst, int offset, int len) throws SSLException {
/* 151 */     return verifyProtocolSelection(super.unwrap(src, dst, offset, len));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void setNegotiatedApplicationProtocol(String applicationProtocol) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNegotiatedApplicationProtocol() {
/* 161 */     String protocol = getApplicationProtocol();
/* 162 */     if (protocol != null) {
/* 163 */       return protocol.isEmpty() ? null : protocol;
/*     */     }
/* 165 */     return protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getApplicationProtocol() {
/* 171 */     return Java9SslUtils.getApplicationProtocol(getWrappedEngine());
/*     */   }
/*     */   
/*     */   public String getHandshakeApplicationProtocol() {
/* 175 */     return Java9SslUtils.getHandshakeApplicationProtocol(getWrappedEngine());
/*     */   }
/*     */   
/*     */   public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> selector) {
/* 179 */     Java9SslUtils.setHandshakeApplicationProtocolSelector(getWrappedEngine(), selector);
/*     */   }
/*     */   
/*     */   public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
/* 183 */     return Java9SslUtils.getHandshakeApplicationProtocolSelector(getWrappedEngine());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\Java9SslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */