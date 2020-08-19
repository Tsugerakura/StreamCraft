/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public final class OpenSslDefaultApplicationProtocolNegotiator
/*    */   implements OpenSslApplicationProtocolNegotiator
/*    */ {
/*    */   private final ApplicationProtocolConfig config;
/*    */   
/*    */   public OpenSslDefaultApplicationProtocolNegotiator(ApplicationProtocolConfig config) {
/* 31 */     this.config = (ApplicationProtocolConfig)ObjectUtil.checkNotNull(config, "config");
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> protocols() {
/* 36 */     return this.config.supportedProtocols();
/*    */   }
/*    */ 
/*    */   
/*    */   public ApplicationProtocolConfig.Protocol protocol() {
/* 41 */     return this.config.protocol();
/*    */   }
/*    */ 
/*    */   
/*    */   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
/* 46 */     return this.config.selectorFailureBehavior();
/*    */   }
/*    */ 
/*    */   
/*    */   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
/* 51 */     return this.config.selectedListenerFailureBehavior();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslDefaultApplicationProtocolNegotiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */