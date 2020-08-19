/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.conscrypt.AllocatedBuffer;
/*     */ import org.conscrypt.BufferAllocator;
/*     */ import org.conscrypt.Conscrypt;
/*     */ import org.conscrypt.HandshakeListener;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ abstract class ConscryptAlpnSslEngine
/*     */   extends JdkSslEngine
/*     */ {
/*  44 */   private static final boolean USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.handler.ssl.conscrypt.useBufferAllocator", true);
/*     */ 
/*     */ 
/*     */   
/*     */   static ConscryptAlpnSslEngine newClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
/*  49 */     return new ClientEngine(engine, alloc, applicationNegotiator);
/*     */   }
/*     */ 
/*     */   
/*     */   static ConscryptAlpnSslEngine newServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
/*  54 */     return new ServerEngine(engine, alloc, applicationNegotiator);
/*     */   }
/*     */   
/*     */   private ConscryptAlpnSslEngine(SSLEngine engine, ByteBufAllocator alloc, List<String> protocols) {
/*  58 */     super(engine);
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
/*  69 */     if (USE_BUFFER_ALLOCATOR) {
/*  70 */       Conscrypt.setBufferAllocator(engine, new BufferAllocatorAdapter(alloc));
/*     */     }
/*     */ 
/*     */     
/*  74 */     Conscrypt.setApplicationProtocols(engine, protocols.<String>toArray(new String[0]));
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
/*     */   final int calculateOutNetBufSize(int plaintextBytes, int numBuffers) {
/*  87 */     long maxOverhead = Conscrypt.maxSealOverhead(getWrappedEngine()) * numBuffers;
/*     */     
/*  89 */     return (int)Math.min(2147483647L, plaintextBytes + maxOverhead);
/*     */   }
/*     */   
/*     */   final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dests) throws SSLException {
/*  93 */     return Conscrypt.unwrap(getWrappedEngine(), srcs, dests);
/*     */   }
/*     */   
/*     */   private static final class ClientEngine
/*     */     extends ConscryptAlpnSslEngine {
/*     */     private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener;
/*     */     
/*     */     ClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
/* 101 */       super(engine, alloc, applicationNegotiator.protocols());
/*     */       
/* 103 */       Conscrypt.setHandshakeListener(engine, new HandshakeListener()
/*     */           {
/*     */             public void onHandshakeFinished() throws SSLException {
/* 106 */               ConscryptAlpnSslEngine.ClientEngine.this.selectProtocol();
/*     */             }
/*     */           });
/*     */       
/* 110 */       this.protocolListener = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(applicationNegotiator
/* 111 */           .protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
/*     */     }
/*     */ 
/*     */     
/*     */     private void selectProtocol() throws SSLException {
/* 116 */       String protocol = Conscrypt.getApplicationProtocol(getWrappedEngine());
/*     */       try {
/* 118 */         this.protocolListener.selected(protocol);
/* 119 */       } catch (Throwable e) {
/* 120 */         throw SslUtils.toSSLHandshakeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ServerEngine
/*     */     extends ConscryptAlpnSslEngine {
/*     */     private final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector;
/*     */     
/*     */     ServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
/* 130 */       super(engine, alloc, applicationNegotiator.protocols());
/*     */ 
/*     */       
/* 133 */       Conscrypt.setHandshakeListener(engine, new HandshakeListener()
/*     */           {
/*     */             public void onHandshakeFinished() throws SSLException {
/* 136 */               ConscryptAlpnSslEngine.ServerEngine.this.selectProtocol();
/*     */             }
/*     */           });
/*     */       
/* 140 */       this.protocolSelector = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory()
/* 141 */           .newSelector(this, new LinkedHashSet<String>(applicationNegotiator
/* 142 */               .protocols())), "protocolSelector");
/*     */     }
/*     */ 
/*     */     
/*     */     private void selectProtocol() throws SSLException {
/*     */       try {
/* 148 */         String protocol = Conscrypt.getApplicationProtocol(getWrappedEngine());
/* 149 */         this.protocolSelector.select((protocol != null) ? Collections.<String>singletonList(protocol) : 
/* 150 */             Collections.<String>emptyList());
/* 151 */       } catch (Throwable e) {
/* 152 */         throw SslUtils.toSSLHandshakeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class BufferAllocatorAdapter extends BufferAllocator {
/*     */     private final ByteBufAllocator alloc;
/*     */     
/*     */     BufferAllocatorAdapter(ByteBufAllocator alloc) {
/* 161 */       this.alloc = alloc;
/*     */     }
/*     */ 
/*     */     
/*     */     public AllocatedBuffer allocateDirectBuffer(int capacity) {
/* 166 */       return new ConscryptAlpnSslEngine.BufferAdapter(this.alloc.directBuffer(capacity));
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class BufferAdapter extends AllocatedBuffer {
/*     */     private final ByteBuf nettyBuffer;
/*     */     private final ByteBuffer buffer;
/*     */     
/*     */     BufferAdapter(ByteBuf nettyBuffer) {
/* 175 */       this.nettyBuffer = nettyBuffer;
/* 176 */       this.buffer = nettyBuffer.nioBuffer(0, nettyBuffer.capacity());
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuffer nioBuffer() {
/* 181 */       return this.buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     public AllocatedBuffer retain() {
/* 186 */       this.nettyBuffer.retain();
/* 187 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public AllocatedBuffer release() {
/* 192 */       this.nettyBuffer.release();
/* 193 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ConscryptAlpnSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */