/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ 
/*     */ public final class ZlibCodecFactory
/*     */ {
/*  27 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_JDK_MEM_LEVEL = 8;
/*     */ 
/*     */   
/*  37 */   private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.noJdkZlibDecoder", 
/*  38 */       (PlatformDependent.javaVersion() < 7)); static {
/*  39 */     logger.debug("-Dio.netty.noJdkZlibDecoder: {}", Boolean.valueOf(noJdkZlibDecoder));
/*     */   }
/*  41 */   private static final boolean noJdkZlibEncoder = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.noJdkZlibEncoder", false); static {
/*  42 */     logger.debug("-Dio.netty.noJdkZlibEncoder: {}", Boolean.valueOf(noJdkZlibEncoder));
/*     */   }
/*  44 */   private static final boolean supportsWindowSizeAndMemLevel = (noJdkZlibDecoder || PlatformDependent.javaVersion() >= 7);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSupportingWindowSizeAndMemLevel() {
/*  51 */     return supportsWindowSizeAndMemLevel;
/*     */   }
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(int compressionLevel) {
/*  55 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
/*  56 */       return new JZlibEncoder(compressionLevel);
/*     */     }
/*  58 */     return new JdkZlibEncoder(compressionLevel);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
/*  63 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
/*  64 */       return new JZlibEncoder(wrapper);
/*     */     }
/*  66 */     return new JdkZlibEncoder(wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
/*  71 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
/*  72 */       return new JZlibEncoder(wrapper, compressionLevel);
/*     */     }
/*  74 */     return new JdkZlibEncoder(wrapper, compressionLevel);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
/*  79 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder || windowBits != 15 || memLevel != 8)
/*     */     {
/*  81 */       return new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel);
/*     */     }
/*  83 */     return new JdkZlibEncoder(wrapper, compressionLevel);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
/*  88 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
/*  89 */       return new JZlibEncoder(dictionary);
/*     */     }
/*  91 */     return new JdkZlibEncoder(dictionary);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
/*  96 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder) {
/*  97 */       return new JZlibEncoder(compressionLevel, dictionary);
/*     */     }
/*  99 */     return new JdkZlibEncoder(compressionLevel, dictionary);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
/* 104 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibEncoder || windowBits != 15 || memLevel != 8)
/*     */     {
/* 106 */       return new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary);
/*     */     }
/* 108 */     return new JdkZlibEncoder(compressionLevel, dictionary);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibDecoder newZlibDecoder() {
/* 113 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
/* 114 */       return new JZlibDecoder();
/*     */     }
/* 116 */     return new JdkZlibDecoder(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
/* 121 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
/* 122 */       return new JZlibDecoder(wrapper);
/*     */     }
/* 124 */     return new JdkZlibDecoder(wrapper, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
/* 129 */     if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
/* 130 */       return new JZlibDecoder(dictionary);
/*     */     }
/* 132 */     return new JdkZlibDecoder(dictionary);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\ZlibCodecFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */