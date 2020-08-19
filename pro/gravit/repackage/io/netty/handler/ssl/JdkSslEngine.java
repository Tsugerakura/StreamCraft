/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLSession;
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
/*     */ class JdkSslEngine
/*     */   extends SSLEngine
/*     */   implements ApplicationProtocolAccessor
/*     */ {
/*     */   private final SSLEngine engine;
/*     */   private volatile String applicationProtocol;
/*     */   
/*     */   JdkSslEngine(SSLEngine engine) {
/*  34 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNegotiatedApplicationProtocol() {
/*  39 */     return this.applicationProtocol;
/*     */   }
/*     */   
/*     */   void setNegotiatedApplicationProtocol(String applicationProtocol) {
/*  43 */     this.applicationProtocol = applicationProtocol;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSession() {
/*  48 */     return this.engine.getSession();
/*     */   }
/*     */   
/*     */   public SSLEngine getWrappedEngine() {
/*  52 */     return this.engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeInbound() throws SSLException {
/*  57 */     this.engine.closeInbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeOutbound() {
/*  62 */     this.engine.closeOutbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPeerHost() {
/*  67 */     return this.engine.getPeerHost();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPeerPort() {
/*  72 */     return this.engine.getPeerPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
/*  77 */     return this.engine.wrap(byteBuffer, byteBuffer2);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, ByteBuffer byteBuffer) throws SSLException {
/*  82 */     return this.engine.wrap(byteBuffers, byteBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i2, ByteBuffer byteBuffer) throws SSLException {
/*  87 */     return this.engine.wrap(byteBuffers, i, i2, byteBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
/*  92 */     return this.engine.unwrap(byteBuffer, byteBuffer2);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers) throws SSLException {
/*  97 */     return this.engine.unwrap(byteBuffer, byteBuffers);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i2) throws SSLException {
/* 102 */     return this.engine.unwrap(byteBuffer, byteBuffers, i, i2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Runnable getDelegatedTask() {
/* 107 */     return this.engine.getDelegatedTask();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInboundDone() {
/* 112 */     return this.engine.isInboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOutboundDone() {
/* 117 */     return this.engine.isOutboundDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedCipherSuites() {
/* 122 */     return this.engine.getSupportedCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledCipherSuites() {
/* 127 */     return this.engine.getEnabledCipherSuites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledCipherSuites(String[] strings) {
/* 132 */     this.engine.setEnabledCipherSuites(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSupportedProtocols() {
/* 137 */     return this.engine.getSupportedProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getEnabledProtocols() {
/* 142 */     return this.engine.getEnabledProtocols();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledProtocols(String[] strings) {
/* 147 */     this.engine.setEnabledProtocols(strings);
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Can only be called when running on JDK7+")
/*     */   public SSLSession getHandshakeSession() {
/* 153 */     return this.engine.getHandshakeSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public void beginHandshake() throws SSLException {
/* 158 */     this.engine.beginHandshake();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
/* 163 */     return this.engine.getHandshakeStatus();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseClientMode(boolean b) {
/* 168 */     this.engine.setUseClientMode(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseClientMode() {
/* 173 */     return this.engine.getUseClientMode();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNeedClientAuth(boolean b) {
/* 178 */     this.engine.setNeedClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getNeedClientAuth() {
/* 183 */     return this.engine.getNeedClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWantClientAuth(boolean b) {
/* 188 */     this.engine.setWantClientAuth(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWantClientAuth() {
/* 193 */     return this.engine.getWantClientAuth();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnableSessionCreation(boolean b) {
/* 198 */     this.engine.setEnableSessionCreation(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getEnableSessionCreation() {
/* 203 */     return this.engine.getEnableSessionCreation();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLParameters getSSLParameters() {
/* 208 */     return this.engine.getSSLParameters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSSLParameters(SSLParameters sslParameters) {
/* 213 */     this.engine.setSSLParameters(sslParameters);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */