/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.SocketAddress;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ final class ChannelHandlerMask
/*     */ {
/*  36 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelHandlerMask.class);
/*     */   
/*     */   static final int MASK_EXCEPTION_CAUGHT = 1;
/*     */   
/*     */   static final int MASK_CHANNEL_REGISTERED = 2;
/*     */   
/*     */   static final int MASK_CHANNEL_UNREGISTERED = 4;
/*     */   
/*     */   static final int MASK_CHANNEL_ACTIVE = 8;
/*     */   
/*     */   static final int MASK_CHANNEL_INACTIVE = 16;
/*     */   
/*     */   static final int MASK_CHANNEL_READ = 32;
/*     */   static final int MASK_CHANNEL_READ_COMPLETE = 64;
/*     */   static final int MASK_USER_EVENT_TRIGGERED = 128;
/*     */   static final int MASK_CHANNEL_WRITABILITY_CHANGED = 256;
/*     */   static final int MASK_BIND = 512;
/*     */   static final int MASK_CONNECT = 1024;
/*     */   static final int MASK_DISCONNECT = 2048;
/*     */   static final int MASK_CLOSE = 4096;
/*     */   static final int MASK_DEREGISTER = 8192;
/*     */   static final int MASK_READ = 16384;
/*     */   static final int MASK_WRITE = 32768;
/*     */   static final int MASK_FLUSH = 65536;
/*     */   private static final int MASK_ALL_INBOUND = 511;
/*     */   private static final int MASK_ALL_OUTBOUND = 130561;
/*     */   
/*  63 */   private static final FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>> MASKS = new FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>>()
/*     */     {
/*     */       protected Map<Class<? extends ChannelHandler>, Integer> initialValue()
/*     */       {
/*  67 */         return new WeakHashMap<Class<? extends ChannelHandler>, Integer>(32);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int mask(Class<? extends ChannelHandler> clazz) {
/*  77 */     Map<Class<? extends ChannelHandler>, Integer> cache = (Map<Class<? extends ChannelHandler>, Integer>)MASKS.get();
/*  78 */     Integer mask = cache.get(clazz);
/*  79 */     if (mask == null) {
/*  80 */       mask = Integer.valueOf(mask0(clazz));
/*  81 */       cache.put(clazz, mask);
/*     */     } 
/*  83 */     return mask.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int mask0(Class<? extends ChannelHandler> handlerType) {
/*  90 */     int mask = 1;
/*     */     try {
/*  92 */       if (ChannelInboundHandler.class.isAssignableFrom(handlerType)) {
/*  93 */         mask |= 0x1FF;
/*     */         
/*  95 */         if (isSkippable(handlerType, "channelRegistered", new Class[] { ChannelHandlerContext.class })) {
/*  96 */           mask &= 0xFFFFFFFD;
/*     */         }
/*  98 */         if (isSkippable(handlerType, "channelUnregistered", new Class[] { ChannelHandlerContext.class })) {
/*  99 */           mask &= 0xFFFFFFFB;
/*     */         }
/* 101 */         if (isSkippable(handlerType, "channelActive", new Class[] { ChannelHandlerContext.class })) {
/* 102 */           mask &= 0xFFFFFFF7;
/*     */         }
/* 104 */         if (isSkippable(handlerType, "channelInactive", new Class[] { ChannelHandlerContext.class })) {
/* 105 */           mask &= 0xFFFFFFEF;
/*     */         }
/* 107 */         if (isSkippable(handlerType, "channelRead", new Class[] { ChannelHandlerContext.class, Object.class })) {
/* 108 */           mask &= 0xFFFFFFDF;
/*     */         }
/* 110 */         if (isSkippable(handlerType, "channelReadComplete", new Class[] { ChannelHandlerContext.class })) {
/* 111 */           mask &= 0xFFFFFFBF;
/*     */         }
/* 113 */         if (isSkippable(handlerType, "channelWritabilityChanged", new Class[] { ChannelHandlerContext.class })) {
/* 114 */           mask &= 0xFFFFFEFF;
/*     */         }
/* 116 */         if (isSkippable(handlerType, "userEventTriggered", new Class[] { ChannelHandlerContext.class, Object.class })) {
/* 117 */           mask &= 0xFFFFFF7F;
/*     */         }
/*     */       } 
/*     */       
/* 121 */       if (ChannelOutboundHandler.class.isAssignableFrom(handlerType)) {
/* 122 */         mask |= 0x1FE01;
/*     */         
/* 124 */         if (isSkippable(handlerType, "bind", new Class[] { ChannelHandlerContext.class, SocketAddress.class, ChannelPromise.class }))
/*     */         {
/* 126 */           mask &= 0xFFFFFDFF;
/*     */         }
/* 128 */         if (isSkippable(handlerType, "connect", new Class[] { ChannelHandlerContext.class, SocketAddress.class, SocketAddress.class, ChannelPromise.class }))
/*     */         {
/* 130 */           mask &= 0xFFFFFBFF;
/*     */         }
/* 132 */         if (isSkippable(handlerType, "disconnect", new Class[] { ChannelHandlerContext.class, ChannelPromise.class })) {
/* 133 */           mask &= 0xFFFFF7FF;
/*     */         }
/* 135 */         if (isSkippable(handlerType, "close", new Class[] { ChannelHandlerContext.class, ChannelPromise.class })) {
/* 136 */           mask &= 0xFFFFEFFF;
/*     */         }
/* 138 */         if (isSkippable(handlerType, "deregister", new Class[] { ChannelHandlerContext.class, ChannelPromise.class })) {
/* 139 */           mask &= 0xFFFFDFFF;
/*     */         }
/* 141 */         if (isSkippable(handlerType, "read", new Class[] { ChannelHandlerContext.class })) {
/* 142 */           mask &= 0xFFFFBFFF;
/*     */         }
/* 144 */         if (isSkippable(handlerType, "write", new Class[] { ChannelHandlerContext.class, Object.class, ChannelPromise.class }))
/*     */         {
/* 146 */           mask &= 0xFFFF7FFF;
/*     */         }
/* 148 */         if (isSkippable(handlerType, "flush", new Class[] { ChannelHandlerContext.class })) {
/* 149 */           mask &= 0xFFFEFFFF;
/*     */         }
/*     */       } 
/*     */       
/* 153 */       if (isSkippable(handlerType, "exceptionCaught", new Class[] { ChannelHandlerContext.class, Throwable.class })) {
/* 154 */         mask &= 0xFFFFFFFE;
/*     */       }
/* 156 */     } catch (Exception e) {
/*     */       
/* 158 */       PlatformDependent.throwException(e);
/*     */     } 
/*     */     
/* 161 */     return mask;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isSkippable(final Class<?> handlerType, final String methodName, Class<?>... paramTypes) throws Exception {
/* 167 */     return ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedExceptionAction<Boolean>()
/*     */         {
/*     */           public Boolean run() throws Exception {
/*     */             Method m;
/*     */             try {
/* 172 */               m = handlerType.getMethod(methodName, paramTypes);
/* 173 */             } catch (NoSuchMethodException e) {
/* 174 */               ChannelHandlerMask.logger.debug("Class {} missing method {}, assume we can not skip execution", new Object[] { this.val$handlerType, this.val$methodName, e });
/*     */               
/* 176 */               return Boolean.valueOf(false);
/*     */             } 
/* 178 */             return Boolean.valueOf((m != null && m.isAnnotationPresent((Class)ChannelHandlerMask.Skip.class)));
/*     */           }
/*     */         })).booleanValue();
/*     */   }
/*     */   
/*     */   @Target({ElementType.METHOD})
/*     */   @Retention(RetentionPolicy.RUNTIME)
/*     */   static @interface Skip {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelHandlerMask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */