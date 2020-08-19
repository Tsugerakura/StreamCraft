/*     */ package pro.gravit.repackage.io.netty.bootstrap;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.util.AttributeKey;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public abstract class AbstractBootstrapConfig<B extends AbstractBootstrap<B, C>, C extends Channel>
/*     */ {
/*     */   protected final B bootstrap;
/*     */   
/*     */   protected AbstractBootstrapConfig(B bootstrap) {
/*  37 */     this.bootstrap = (B)ObjectUtil.checkNotNull(bootstrap, "bootstrap");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SocketAddress localAddress() {
/*  44 */     return this.bootstrap.localAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ChannelFactory<? extends C> channelFactory() {
/*  52 */     return this.bootstrap.channelFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ChannelHandler handler() {
/*  59 */     return this.bootstrap.handler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Map<ChannelOption<?>, Object> options() {
/*  66 */     return this.bootstrap.options();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Map<AttributeKey<?>, Object> attrs() {
/*  73 */     return this.bootstrap.attrs();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final EventLoopGroup group() {
/*  81 */     return this.bootstrap.group();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  88 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append('(');
/*  89 */     EventLoopGroup group = group();
/*  90 */     if (group != null) {
/*  91 */       buf.append("group: ")
/*  92 */         .append(StringUtil.simpleClassName(group))
/*  93 */         .append(", ");
/*     */     }
/*     */     
/*  96 */     ChannelFactory<? extends C> factory = channelFactory();
/*  97 */     if (factory != null) {
/*  98 */       buf.append("channelFactory: ")
/*  99 */         .append(factory)
/* 100 */         .append(", ");
/*     */     }
/* 102 */     SocketAddress localAddress = localAddress();
/* 103 */     if (localAddress != null) {
/* 104 */       buf.append("localAddress: ")
/* 105 */         .append(localAddress)
/* 106 */         .append(", ");
/*     */     }
/*     */     
/* 109 */     Map<ChannelOption<?>, Object> options = options();
/* 110 */     if (!options.isEmpty()) {
/* 111 */       buf.append("options: ")
/* 112 */         .append(options)
/* 113 */         .append(", ");
/*     */     }
/* 115 */     Map<AttributeKey<?>, Object> attrs = attrs();
/* 116 */     if (!attrs.isEmpty()) {
/* 117 */       buf.append("attrs: ")
/* 118 */         .append(attrs)
/* 119 */         .append(", ");
/*     */     }
/* 121 */     ChannelHandler handler = handler();
/* 122 */     if (handler != null) {
/* 123 */       buf.append("handler: ")
/* 124 */         .append(handler)
/* 125 */         .append(", ");
/*     */     }
/* 127 */     if (buf.charAt(buf.length() - 1) == '(') {
/* 128 */       buf.append(')');
/*     */     } else {
/* 130 */       buf.setCharAt(buf.length() - 2, ')');
/* 131 */       buf.setLength(buf.length() - 1);
/*     */     } 
/* 133 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\bootstrap\AbstractBootstrapConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */