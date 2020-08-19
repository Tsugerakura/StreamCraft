/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public final class ApplicationProtocolConfig
/*     */ {
/*  34 */   public static final ApplicationProtocolConfig DISABLED = new ApplicationProtocolConfig();
/*     */ 
/*     */   
/*     */   private final List<String> supportedProtocols;
/*     */ 
/*     */   
/*     */   private final Protocol protocol;
/*     */ 
/*     */   
/*     */   private final SelectorFailureBehavior selectorBehavior;
/*     */ 
/*     */   
/*     */   private final SelectedListenerFailureBehavior selectedBehavior;
/*     */ 
/*     */   
/*     */   public ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, Iterable<String> supportedProtocols) {
/*  50 */     this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
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
/*     */   public ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, String... supportedProtocols) {
/*  62 */     this(protocol, selectorBehavior, selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
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
/*     */   private ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, List<String> supportedProtocols) {
/*  75 */     this.supportedProtocols = Collections.unmodifiableList((List<? extends String>)ObjectUtil.checkNotNull(supportedProtocols, "supportedProtocols"));
/*  76 */     this.protocol = (Protocol)ObjectUtil.checkNotNull(protocol, "protocol");
/*  77 */     this.selectorBehavior = (SelectorFailureBehavior)ObjectUtil.checkNotNull(selectorBehavior, "selectorBehavior");
/*  78 */     this.selectedBehavior = (SelectedListenerFailureBehavior)ObjectUtil.checkNotNull(selectedBehavior, "selectedBehavior");
/*     */     
/*  80 */     if (protocol == Protocol.NONE) {
/*  81 */       throw new IllegalArgumentException("protocol (" + Protocol.NONE + ") must not be " + Protocol.NONE + '.');
/*     */     }
/*  83 */     if (supportedProtocols.isEmpty()) {
/*  84 */       throw new IllegalArgumentException("supportedProtocols must be not empty");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ApplicationProtocolConfig() {
/*  92 */     this.supportedProtocols = Collections.emptyList();
/*  93 */     this.protocol = Protocol.NONE;
/*  94 */     this.selectorBehavior = SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
/*  95 */     this.selectedBehavior = SelectedListenerFailureBehavior.ACCEPT;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Protocol
/*     */   {
/* 102 */     NONE, NPN, ALPN, NPN_AND_ALPN;
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
/*     */   public enum SelectorFailureBehavior
/*     */   {
/* 116 */     FATAL_ALERT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 122 */     NO_ADVERTISE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     CHOOSE_MY_LAST_PROTOCOL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum SelectedListenerFailureBehavior
/*     */   {
/* 143 */     ACCEPT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     FATAL_ALERT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 155 */     CHOOSE_MY_LAST_PROTOCOL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> supportedProtocols() {
/* 162 */     return this.supportedProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Protocol protocol() {
/* 169 */     return this.protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelectorFailureBehavior selectorFailureBehavior() {
/* 176 */     return this.selectorBehavior;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
/* 183 */     return this.selectedBehavior;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\ApplicationProtocolConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */