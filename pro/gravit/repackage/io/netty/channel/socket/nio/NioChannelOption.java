/*     */ package pro.gravit.repackage.io.netty.channel.socket.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.NetworkChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
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
/*     */ @SuppressJava6Requirement(reason = "Usage explicit by the user")
/*     */ public final class NioChannelOption<T>
/*     */   extends ChannelOption<T>
/*     */ {
/*     */   private final SocketOption<T> option;
/*     */   
/*     */   private NioChannelOption(SocketOption<T> option) {
/*  40 */     super(option.name());
/*  41 */     this.option = option;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ChannelOption<T> of(SocketOption<T> option) {
/*  48 */     return new NioChannelOption<T>(option);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   static <T> boolean setOption(Channel jdkChannel, NioChannelOption<T> option, T value) {
/*  60 */     NetworkChannel channel = (NetworkChannel)jdkChannel;
/*  61 */     if (!channel.supportedOptions().contains(option.option)) {
/*  62 */       return false;
/*     */     }
/*  64 */     if (channel instanceof java.nio.channels.ServerSocketChannel && option.option == StandardSocketOptions.IP_TOS)
/*     */     {
/*     */       
/*  67 */       return false;
/*     */     }
/*     */     try {
/*  70 */       channel.setOption(option.option, value);
/*  71 */       return true;
/*  72 */     } catch (IOException e) {
/*  73 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   static <T> T getOption(Channel jdkChannel, NioChannelOption<T> option) {
/*  79 */     NetworkChannel channel = (NetworkChannel)jdkChannel;
/*     */     
/*  81 */     if (!channel.supportedOptions().contains(option.option)) {
/*  82 */       return null;
/*     */     }
/*  84 */     if (channel instanceof java.nio.channels.ServerSocketChannel && option.option == StandardSocketOptions.IP_TOS)
/*     */     {
/*     */       
/*  87 */       return null;
/*     */     }
/*     */     try {
/*  90 */       return channel.getOption(option.option);
/*  91 */     } catch (IOException e) {
/*  92 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   static ChannelOption[] getOptions(Channel jdkChannel) {
/*  99 */     NetworkChannel channel = (NetworkChannel)jdkChannel;
/* 100 */     Set<SocketOption<?>> supportedOpts = channel.supportedOptions();
/*     */     
/* 102 */     if (channel instanceof java.nio.channels.ServerSocketChannel) {
/* 103 */       List<ChannelOption<?>> extraOpts = new ArrayList<ChannelOption<?>>(supportedOpts.size());
/* 104 */       for (SocketOption<?> opt : supportedOpts) {
/* 105 */         if (opt == StandardSocketOptions.IP_TOS) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 110 */         extraOpts.add(new NioChannelOption(opt));
/*     */       } 
/* 112 */       return extraOpts.<ChannelOption>toArray(new ChannelOption[0]);
/*     */     } 
/* 114 */     ChannelOption[] arrayOfChannelOption = new ChannelOption[supportedOpts.size()];
/*     */     
/* 116 */     int i = 0;
/* 117 */     for (SocketOption<?> opt : supportedOpts) {
/* 118 */       arrayOfChannelOption[i++] = new NioChannelOption(opt);
/*     */     }
/* 120 */     return arrayOfChannelOption;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\socket\nio\NioChannelOption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */