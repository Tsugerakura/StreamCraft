/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public interface JdkApplicationProtocolNegotiator
/*    */   extends ApplicationProtocolNegotiator
/*    */ {
/*    */   SslEngineWrapperFactory wrapperFactory();
/*    */   
/*    */   ProtocolSelectorFactory protocolSelectorFactory();
/*    */   
/*    */   ProtocolSelectionListenerFactory protocolListenerFactory();
/*    */   
/*    */   public static interface ProtocolSelectionListenerFactory
/*    */   {
/*    */     JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine param1SSLEngine, List<String> param1List);
/*    */   }
/*    */   
/*    */   public static interface ProtocolSelectorFactory
/*    */   {
/*    */     JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine param1SSLEngine, Set<String> param1Set);
/*    */   }
/*    */   
/*    */   public static interface ProtocolSelectionListener
/*    */   {
/*    */     void unsupported();
/*    */     
/*    */     void selected(String param1String) throws Exception;
/*    */   }
/*    */   
/*    */   public static interface ProtocolSelector
/*    */   {
/*    */     void unsupported();
/*    */     
/*    */     String select(List<String> param1List) throws Exception;
/*    */   }
/*    */   
/*    */   public static abstract class AllocatorAwareSslEngineWrapperFactory
/*    */     implements SslEngineWrapperFactory
/*    */   {
/*    */     public final SSLEngine wrapSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
/* 54 */       return wrapSslEngine(engine, ByteBufAllocator.DEFAULT, applicationNegotiator, isServer);
/*    */     }
/*    */     
/*    */     abstract SSLEngine wrapSslEngine(SSLEngine param1SSLEngine, ByteBufAllocator param1ByteBufAllocator, JdkApplicationProtocolNegotiator param1JdkApplicationProtocolNegotiator, boolean param1Boolean);
/*    */   }
/*    */   
/*    */   public static interface SslEngineWrapperFactory {
/*    */     SSLEngine wrapSslEngine(SSLEngine param1SSLEngine, JdkApplicationProtocolNegotiator param1JdkApplicationProtocolNegotiator, boolean param1Boolean);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\JdkApplicationProtocolNegotiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */