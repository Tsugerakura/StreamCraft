/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.eclipse.jetty.npn.NextProtoNego;
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
/*     */ 
/*     */ final class JettyNpnSslEngine
/*     */   extends JdkSslEngine
/*     */ {
/*     */   private static boolean available;
/*     */   
/*     */   static boolean isAvailable() {
/*  38 */     updateAvailability();
/*  39 */     return available;
/*     */   }
/*     */   
/*     */   private static void updateAvailability() {
/*  43 */     if (available) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*  48 */       Class.forName("sun.security.ssl.NextProtoNegoExtension", true, null);
/*  49 */       available = true;
/*  50 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   JettyNpnSslEngine(SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator, boolean server) {
/*  56 */     super(engine);
/*  57 */     ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
/*     */     
/*  59 */     if (server) {
/*  60 */       final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(applicationNegotiator
/*  61 */           .protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
/*     */       
/*  63 */       NextProtoNego.put(engine, (NextProtoNego.Provider)new NextProtoNego.ServerProvider()
/*     */           {
/*     */             public void unsupported() {
/*  66 */               protocolListener.unsupported();
/*     */             }
/*     */ 
/*     */             
/*     */             public List<String> protocols() {
/*  71 */               return applicationNegotiator.protocols();
/*     */             }
/*     */ 
/*     */             
/*     */             public void protocolSelected(String protocol) {
/*     */               try {
/*  77 */                 protocolListener.selected(protocol);
/*  78 */               } catch (Throwable t) {
/*  79 */                 PlatformDependent.throwException(t);
/*     */               } 
/*     */             }
/*     */           });
/*     */     } else {
/*  84 */       final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory()
/*  85 */           .newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
/*     */       
/*  87 */       NextProtoNego.put(engine, (NextProtoNego.Provider)new NextProtoNego.ClientProvider()
/*     */           {
/*     */             public boolean supports() {
/*  90 */               return true;
/*     */             }
/*     */ 
/*     */             
/*     */             public void unsupported() {
/*  95 */               protocolSelector.unsupported();
/*     */             }
/*     */ 
/*     */             
/*     */             public String selectProtocol(List<String> protocols) {
/*     */               try {
/* 101 */                 return protocolSelector.select(protocols);
/* 102 */               } catch (Throwable t) {
/* 103 */                 PlatformDependent.throwException(t);
/* 104 */                 return null;
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeInbound() throws SSLException {
/* 113 */     NextProtoNego.remove(getWrappedEngine());
/* 114 */     super.closeInbound();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeOutbound() {
/* 119 */     NextProtoNego.remove(getWrappedEngine());
/* 120 */     super.closeOutbound();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JettyNpnSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */