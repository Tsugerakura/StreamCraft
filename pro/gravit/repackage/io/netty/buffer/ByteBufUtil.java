/*      */ package pro.gravit.repackage.io.netty.buffer;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.CharacterCodingException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetDecoder;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.CoderResult;
/*      */ import java.nio.charset.CodingErrorAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*      */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*      */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ByteBufUtil
/*      */ {
/*      */   static {
/*      */     ByteBufAllocator alloc;
/*      */   }
/*      */   
/*   58 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
/*   59 */   private static final FastThreadLocal<byte[]> BYTE_ARRAYS = new FastThreadLocal<byte[]>()
/*      */     {
/*      */       protected byte[] initialValue() throws Exception {
/*   62 */         return PlatformDependent.allocateUninitializedArray(1024);
/*      */       }
/*      */     };
/*      */ 
/*      */   
/*      */   private static final byte WRITE_UTF_UNKNOWN = 63;
/*      */   private static final int MAX_CHAR_BUFFER_SIZE;
/*      */   private static final int THREAD_LOCAL_BUFFER_SIZE;
/*   70 */   private static final int MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
/*      */   static final int WRITE_CHUNK_SIZE = 8192;
/*      */   static final ByteBufAllocator DEFAULT_ALLOCATOR;
/*      */   static final int MAX_TL_ARRAY_LEN = 1024;
/*      */   private static final ByteProcessor FIND_NON_ASCII;
/*      */   
/*   76 */   static { String allocType = SystemPropertyUtil.get("pro.gravit.repackage.io.netty.allocator.type", 
/*   77 */         PlatformDependent.isAndroid() ? "unpooled" : "pooled");
/*   78 */     allocType = allocType.toLowerCase(Locale.US).trim();
/*      */ 
/*      */     
/*   81 */     if ("unpooled".equals(allocType)) {
/*   82 */       alloc = UnpooledByteBufAllocator.DEFAULT;
/*   83 */       logger.debug("-Dio.netty.allocator.type: {}", allocType);
/*   84 */     } else if ("pooled".equals(allocType)) {
/*   85 */       alloc = PooledByteBufAllocator.DEFAULT;
/*   86 */       logger.debug("-Dio.netty.allocator.type: {}", allocType);
/*      */     } else {
/*   88 */       alloc = PooledByteBufAllocator.DEFAULT;
/*   89 */       logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
/*      */     } 
/*      */     
/*   92 */     DEFAULT_ALLOCATOR = alloc;
/*      */     
/*   94 */     THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.threadLocalDirectBufferSize", 0);
/*   95 */     logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", Integer.valueOf(THREAD_LOCAL_BUFFER_SIZE));
/*      */     
/*   97 */     MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.maxThreadLocalCharBufferSize", 16384);
/*   98 */     logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", Integer.valueOf(MAX_CHAR_BUFFER_SIZE));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1298 */     FIND_NON_ASCII = new ByteProcessor()
/*      */       {
/*      */         public boolean process(byte value) {
/* 1301 */           return (value >= 0);
/*      */         }
/*      */       }; } static byte[] threadLocalTempArray(int minLength) { return (minLength <= 1024) ? (byte[])BYTE_ARRAYS.get() : PlatformDependent.allocateUninitializedArray(minLength); } public static String hexDump(ByteBuf buffer) { return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes()); } public static String hexDump(ByteBuf buffer, int fromIndex, int length) { return HexUtil.hexDump(buffer, fromIndex, length); } public static String hexDump(byte[] array) { return hexDump(array, 0, array.length); } public static String hexDump(byte[] array, int fromIndex, int length) { return HexUtil.hexDump(array, fromIndex, length); } public static byte decodeHexByte(CharSequence s, int pos) { return StringUtil.decodeHexByte(s, pos); } public static byte[] decodeHexDump(CharSequence hexDump) { return StringUtil.decodeHexDump(hexDump, 0, hexDump.length()); }
/*      */   public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) { return StringUtil.decodeHexDump(hexDump, fromIndex, length); }
/*      */   public static boolean ensureWritableSuccess(int ensureWritableResult) { return (ensureWritableResult == 0 || ensureWritableResult == 2); }
/*      */   public static int hashCode(ByteBuf buffer) { int aLen = buffer.readableBytes(); int intCount = aLen >>> 2; int byteCount = aLen & 0x3; int hashCode = 1; int arrayIndex = buffer.readerIndex(); if (buffer.order() == ByteOrder.BIG_ENDIAN) { for (int j = intCount; j > 0; j--) { hashCode = 31 * hashCode + buffer.getInt(arrayIndex); arrayIndex += 4; }  } else { for (int j = intCount; j > 0; j--) { hashCode = 31 * hashCode + swapInt(buffer.getInt(arrayIndex)); arrayIndex += 4; }  }  for (int i = byteCount; i > 0; i--) hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);  if (hashCode == 0) hashCode = 1;  return hashCode; }
/*      */   public static int indexOf(ByteBuf needle, ByteBuf haystack) { int attempts = haystack.readableBytes() - needle.readableBytes() + 1; for (int i = 0; i < attempts; i++) { if (equals(needle, needle.readerIndex(), haystack, haystack.readerIndex() + i, needle.readableBytes())) return haystack.readerIndex() + i;  }  return -1; }
/*      */   public static boolean equals(ByteBuf a, int aStartIndex, ByteBuf b, int bStartIndex, int length) { if (aStartIndex < 0 || bStartIndex < 0 || length < 0) throw new IllegalArgumentException("All indexes and lengths must be non-negative");  if (a.writerIndex() - length < aStartIndex || b.writerIndex() - length < bStartIndex) return false;  int longCount = length >>> 3; int byteCount = length & 0x7; if (a.order() == b.order()) { for (int j = longCount; j > 0; j--) { if (a.getLong(aStartIndex) != b.getLong(bStartIndex)) return false;  aStartIndex += 8; bStartIndex += 8; }  } else { for (int j = longCount; j > 0; j--) { if (a.getLong(aStartIndex) != swapLong(b.getLong(bStartIndex))) return false;  aStartIndex += 8; bStartIndex += 8; }  }  for (int i = byteCount; i > 0; i--) { if (a.getByte(aStartIndex) != b.getByte(bStartIndex)) return false;  aStartIndex++; bStartIndex++; }  return true; }
/*      */   public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) { int aLen = bufferA.readableBytes(); if (aLen != bufferB.readableBytes()) return false;  return equals(bufferA, bufferA.readerIndex(), bufferB, bufferB.readerIndex(), aLen); }
/*      */   public static int compare(ByteBuf bufferA, ByteBuf bufferB) { int aLen = bufferA.readableBytes(); int bLen = bufferB.readableBytes(); int minLength = Math.min(aLen, bLen); int uintCount = minLength >>> 2; int byteCount = minLength & 0x3; int aIndex = bufferA.readerIndex(); int bIndex = bufferB.readerIndex(); if (uintCount > 0) { long res; boolean bufferAIsBigEndian = (bufferA.order() == ByteOrder.BIG_ENDIAN); int uintCountIncrement = uintCount << 2; if (bufferA.order() == bufferB.order()) { res = bufferAIsBigEndian ? compareUintBigEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : compareUintLittleEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement); } else { res = bufferAIsBigEndian ? compareUintBigEndianA(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : compareUintBigEndianB(bufferA, bufferB, aIndex, bIndex, uintCountIncrement); }  if (res != 0L) return (int)Math.min(2147483647L, Math.max(-2147483648L, res));  aIndex += uintCountIncrement; bIndex += uintCountIncrement; }  for (int aEnd = aIndex + byteCount; aIndex < aEnd; aIndex++, bIndex++) { int comp = bufferA.getUnsignedByte(aIndex) - bufferB.getUnsignedByte(bIndex); if (comp != 0) return comp;  }  return aLen - bLen; }
/*      */   private static long compareUintBigEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) { for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) { long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedInt(bIndex); if (comp != 0L) return comp;  }  return 0L; }
/*      */   private static long compareUintLittleEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) { for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) { long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedIntLE(bIndex); if (comp != 0L) return comp;  }  return 0L; }
/*      */   private static long compareUintBigEndianA(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) { for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) { long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedIntLE(bIndex); if (comp != 0L) return comp;  }  return 0L; }
/* 1314 */   private static boolean isAscii(ByteBuf buf, int index, int length) { return (buf.forEachByte(index, length, FIND_NON_ASCII) == -1); }
/*      */   private static long compareUintBigEndianB(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) { for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) {
/*      */       long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedInt(bIndex); if (comp != 0L)
/*      */         return comp; 
/*      */     }  return 0L; }
/*      */   public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) { if (fromIndex <= toIndex)
/*      */       return firstIndexOf(buffer, fromIndex, toIndex, value); 
/*      */     return lastIndexOf(buffer, fromIndex, toIndex, value); }
/*      */   public static short swapShort(short value) { return Short.reverseBytes(value); }
/*      */   public static int swapMedium(int value) { int swapped = value << 16 & 0xFF0000 | value & 0xFF00 | value >>> 16 & 0xFF;
/*      */     if ((swapped & 0x800000) != 0)
/*      */       swapped |= 0xFF000000; 
/*      */     return swapped; }
/*      */   public static int swapInt(int value) { return Integer.reverseBytes(value); }
/*      */   public static long swapLong(long value) { return Long.reverseBytes(value); }
/*      */   public static ByteBuf writeShortBE(ByteBuf buf, int shortValue) { return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.writeShort(shortValue) : buf.writeShortLE(shortValue); }
/*      */   public static ByteBuf setShortBE(ByteBuf buf, int index, int shortValue) { return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.setShort(index, shortValue) : buf.setShortLE(index, shortValue); }
/*      */   public static ByteBuf writeMediumBE(ByteBuf buf, int mediumValue) { return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.writeMedium(mediumValue) : buf.writeMediumLE(mediumValue); }
/*      */   public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) { boolean release = true;
/*      */     ByteBuf dst = alloc.buffer(length);
/*      */     try {
/*      */       buffer.readBytes(dst);
/*      */       release = false;
/*      */       return dst;
/*      */     } finally {
/*      */       if (release)
/*      */         dst.release(); 
/*      */     }  }
/*      */   private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) { fromIndex = Math.max(fromIndex, 0);
/*      */     if (fromIndex >= toIndex || buffer.capacity() == 0)
/*      */       return -1; 
/*      */     return buffer.forEachByte(fromIndex, toIndex - fromIndex, (ByteProcessor)new ByteProcessor.IndexOfProcessor(value)); }
/*      */   private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) { int capacity = buffer.capacity();
/*      */     fromIndex = Math.min(fromIndex, capacity);
/*      */     if (fromIndex < 0 || capacity == 0)
/*      */       return -1; 
/*      */     return buffer.forEachByteDesc(toIndex, fromIndex - toIndex, (ByteProcessor)new ByteProcessor.IndexOfProcessor(value)); }
/*      */   private static CharSequence checkCharSequenceBounds(CharSequence seq, int start, int end) { if (MathUtil.isOutOfBounds(start, end - start, seq.length()))
/*      */       throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= seq.length(" + seq.length() + ')'); 
/*      */     return seq; }
/*      */   public static ByteBuf writeUtf8(ByteBufAllocator alloc, CharSequence seq) { ByteBuf buf = alloc.buffer(utf8MaxBytes(seq));
/*      */     writeUtf8(buf, seq);
/*      */     return buf; }
/*      */   public static int writeUtf8(ByteBuf buf, CharSequence seq) { int seqLength = seq.length();
/*      */     return reserveAndWriteUtf8Seq(buf, seq, 0, seqLength, utf8MaxBytes(seqLength)); }
/*      */   public static int writeUtf8(ByteBuf buf, CharSequence seq, int start, int end) { checkCharSequenceBounds(seq, start, end);
/*      */     return reserveAndWriteUtf8Seq(buf, seq, start, end, utf8MaxBytes(end - start)); }
/* 1361 */   public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int reserveBytes) { return reserveAndWriteUtf8Seq(buf, seq, 0, seq.length(), reserveBytes); } private static boolean isUtf8(ByteBuf buf, int index, int length) { int endIndex = index + length;
/* 1362 */     while (index < endIndex) {
/* 1363 */       byte b1 = buf.getByte(index++);
/*      */       
/* 1365 */       if ((b1 & 0x80) == 0) {
/*      */         continue;
/*      */       }
/*      */       
/* 1369 */       if ((b1 & 0xE0) == 192) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1375 */         if (index >= endIndex) {
/* 1376 */           return false;
/*      */         }
/* 1378 */         byte b2 = buf.getByte(index++);
/* 1379 */         if ((b2 & 0xC0) != 128) {
/* 1380 */           return false;
/*      */         }
/* 1382 */         if ((b1 & 0xFF) < 194)
/* 1383 */           return false;  continue;
/*      */       } 
/* 1385 */       if ((b1 & 0xF0) == 224) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1394 */         if (index > endIndex - 2) {
/* 1395 */           return false;
/*      */         }
/* 1397 */         byte b2 = buf.getByte(index++);
/* 1398 */         byte b3 = buf.getByte(index++);
/* 1399 */         if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128) {
/* 1400 */           return false;
/*      */         }
/* 1402 */         if ((b1 & 0xF) == 0 && (b2 & 0xFF) < 160) {
/* 1403 */           return false;
/*      */         }
/* 1405 */         if ((b1 & 0xF) == 13 && (b2 & 0xFF) > 159)
/* 1406 */           return false;  continue;
/*      */       } 
/* 1408 */       if ((b1 & 0xF8) == 240) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1416 */         if (index > endIndex - 3) {
/* 1417 */           return false;
/*      */         }
/* 1419 */         byte b2 = buf.getByte(index++);
/* 1420 */         byte b3 = buf.getByte(index++);
/* 1421 */         byte b4 = buf.getByte(index++);
/* 1422 */         if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128 || (b4 & 0xC0) != 128)
/*      */         {
/* 1424 */           return false;
/*      */         }
/* 1426 */         if ((b1 & 0xFF) > 244 || ((b1 & 0xFF) == 240 && (b2 & 0xFF) < 144) || ((b1 & 0xFF) == 244 && (b2 & 0xFF) > 143))
/*      */         {
/*      */           
/* 1429 */           return false; } 
/*      */         continue;
/*      */       } 
/* 1432 */       return false;
/*      */     } 
/*      */     
/* 1435 */     return true; }
/*      */   public static int reserveAndWriteUtf8(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) { return reserveAndWriteUtf8Seq(buf, checkCharSequenceBounds(seq, start, end), start, end, reserveBytes); }
/*      */   private static int reserveAndWriteUtf8Seq(ByteBuf buf, CharSequence seq, int start, int end, int reserveBytes) { while (true) { while (buf instanceof WrappedCompositeByteBuf) buf = buf.unwrap();  if (buf instanceof AbstractByteBuf) { AbstractByteBuf byteBuf = (AbstractByteBuf)buf; byteBuf.ensureWritable0(reserveBytes); int written = writeUtf8(byteBuf, byteBuf.writerIndex, seq, start, end); byteBuf.writerIndex += written; return written; }  if (buf instanceof WrappedByteBuf) { buf = buf.unwrap(); continue; }  break; }  byte[] bytes = seq.subSequence(start, end).toString().getBytes(CharsetUtil.UTF_8); buf.writeBytes(bytes); return bytes.length; }
/*      */   static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) { return writeUtf8(buffer, writerIndex, seq, 0, len); }
/*      */   static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int start, int end) { int oldWriterIndex = writerIndex; for (int i = start; i < end; i++) { char c = seq.charAt(i); if (c < '') { buffer._setByte(writerIndex++, (byte)c); } else if (c < 'ࠀ') { buffer._setByte(writerIndex++, (byte)(0xC0 | c >> 6)); buffer._setByte(writerIndex++, (byte)(0x80 | c & 0x3F)); } else if (StringUtil.isSurrogate(c)) { if (!Character.isHighSurrogate(c)) { buffer._setByte(writerIndex++, 63); } else { if (++i == end) { buffer._setByte(writerIndex++, 63); break; }  writerIndex = writeUtf8Surrogate(buffer, writerIndex, c, seq.charAt(i)); }  } else { buffer._setByte(writerIndex++, (byte)(0xE0 | c >> 12)); buffer._setByte(writerIndex++, (byte)(0x80 | c >> 6 & 0x3F)); buffer._setByte(writerIndex++, (byte)(0x80 | c & 0x3F)); }  }  return writerIndex - oldWriterIndex; }
/*      */   private static int writeUtf8Surrogate(AbstractByteBuf buffer, int writerIndex, char c, char c2) { if (!Character.isLowSurrogate(c2)) { buffer._setByte(writerIndex++, 63); buffer._setByte(writerIndex++, Character.isHighSurrogate(c2) ? 63 : c2); return writerIndex; }  int codePoint = Character.toCodePoint(c, c2); buffer._setByte(writerIndex++, (byte)(0xF0 | codePoint >> 18)); buffer._setByte(writerIndex++, (byte)(0x80 | codePoint >> 12 & 0x3F)); buffer._setByte(writerIndex++, (byte)(0x80 | codePoint >> 6 & 0x3F)); buffer._setByte(writerIndex++, (byte)(0x80 | codePoint & 0x3F)); return writerIndex; }
/*      */   public static int utf8MaxBytes(int seqLength) { return seqLength * MAX_BYTES_PER_CHAR_UTF8; }
/*      */   public static int utf8MaxBytes(CharSequence seq) { return utf8MaxBytes(seq.length()); }
/*      */   public static int utf8Bytes(CharSequence seq) { return utf8ByteCount(seq, 0, seq.length()); }
/* 1444 */   public static int utf8Bytes(CharSequence seq, int start, int end) { return utf8ByteCount(checkCharSequenceBounds(seq, start, end), start, end); } private static int utf8ByteCount(CharSequence seq, int start, int end) { if (seq instanceof AsciiString) return end - start;  int i = start; while (i < end && seq.charAt(i) < '') i++;  return (i < end) ? (i - start + utf8BytesNonAscii(seq, i, end)) : (i - start); } private static int utf8BytesNonAscii(CharSequence seq, int start, int end) { int encodedLength = 0; for (int i = start; i < end; i++) { char c = seq.charAt(i); if (c < 'ࠀ') { encodedLength += (127 - c >>> 31) + 1; } else if (StringUtil.isSurrogate(c)) { if (!Character.isHighSurrogate(c)) { encodedLength++; } else { if (++i == end) { encodedLength++; break; }  if (!Character.isLowSurrogate(seq.charAt(i))) { encodedLength += 2; } else { encodedLength += 4; }  }  } else { encodedLength += 3; }  }  return encodedLength; } public static ByteBuf writeAscii(ByteBufAllocator alloc, CharSequence seq) { ByteBuf buf = alloc.buffer(seq.length()); writeAscii(buf, seq); return buf; } public static int writeAscii(ByteBuf buf, CharSequence seq) { int len = seq.length(); if (seq instanceof AsciiString) { AsciiString asciiString = (AsciiString)seq; buf.writeBytes(asciiString.array(), asciiString.arrayOffset(), len); } else { while (true) { while (buf instanceof WrappedCompositeByteBuf) buf = buf.unwrap();  if (buf instanceof AbstractByteBuf) { AbstractByteBuf byteBuf = (AbstractByteBuf)buf; byteBuf.ensureWritable0(len); int written = writeAscii(byteBuf, byteBuf.writerIndex, seq, len); byteBuf.writerIndex += written; return written; }  if (buf instanceof WrappedByteBuf) { buf = buf.unwrap(); continue; }  break; }  byte[] bytes = seq.toString().getBytes(CharsetUtil.US_ASCII); buf.writeBytes(bytes); return bytes.length; }  return len; } static int writeAscii(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) { for (int i = 0; i < len; i++) buffer._setByte(writerIndex++, AsciiString.c2b(seq.charAt(i)));  return len; } public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) { return encodeString0(alloc, false, src, charset, 0); } public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset, int extraCapacity) { return encodeString0(alloc, false, src, charset, extraCapacity); } static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset, int extraCapacity) { ByteBuf dst; CharsetEncoder encoder = CharsetUtil.encoder(charset); int length = (int)(src.remaining() * encoder.maxBytesPerChar()) + extraCapacity; boolean release = true; if (enforceHeap) { dst = alloc.heapBuffer(length); } else { dst = alloc.buffer(length); }  try { ByteBuffer dstBuf = dst.internalNioBuffer(dst.readerIndex(), length); int pos = dstBuf.position(); CoderResult cr = encoder.encode(src, dstBuf, true); if (!cr.isUnderflow()) cr.throwException();  cr = encoder.flush(dstBuf); if (!cr.isUnderflow()) cr.throwException();  dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos); release = false; return dst; } catch (CharacterCodingException x) { throw new IllegalStateException(x); } finally { if (release) dst.release();  }  } static String decodeString(ByteBuf src, int readerIndex, int len, Charset charset) { byte[] array; int offset; if (len == 0) return "";  if (src.hasArray()) { array = src.array(); offset = src.arrayOffset() + readerIndex; } else { array = threadLocalTempArray(len); offset = 0; src.getBytes(readerIndex, array, 0, len); }  if (CharsetUtil.US_ASCII.equals(charset)) return new String(array, 0, offset, len);  return new String(array, offset, len, charset); } public static ByteBuf threadLocalDirectBuffer() { if (THREAD_LOCAL_BUFFER_SIZE <= 0) return null;  if (PlatformDependent.hasUnsafe()) return ThreadLocalUnsafeDirectByteBuf.newInstance();  return ThreadLocalDirectByteBuf.newInstance(); } public static byte[] getBytes(ByteBuf buf) { return getBytes(buf, buf.readerIndex(), buf.readableBytes()); } public static byte[] getBytes(ByteBuf buf, int start, int length) { return getBytes(buf, start, length, true); } public static byte[] getBytes(ByteBuf buf, int start, int length, boolean copy) { int capacity = buf.capacity(); if (MathUtil.isOutOfBounds(start, length, capacity)) throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + capacity + ')');  if (buf.hasArray()) { if (copy || start != 0 || length != capacity) { int baseOffset = buf.arrayOffset() + start; return Arrays.copyOfRange(buf.array(), baseOffset, baseOffset + length); }  return buf.array(); }  byte[] v = PlatformDependent.allocateUninitializedArray(length); buf.getBytes(start, v); return v; } public static void copy(AsciiString src, ByteBuf dst) { copy(src, 0, dst, src.length()); } public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int dstIdx, int length) { if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');  ((ByteBuf)ObjectUtil.checkNotNull(dst, "dst")).setBytes(dstIdx, src.array(), srcIdx + src.arrayOffset(), length); } public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int length) { if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');  ((ByteBuf)ObjectUtil.checkNotNull(dst, "dst")).writeBytes(src.array(), srcIdx + src.arrayOffset(), length); } static void readBytes(ByteBufAllocator allocator, ByteBuffer buffer, int position, int length, OutputStream out) throws IOException { if (buffer.hasArray())
/* 1445 */     { out.write(buffer.array(), position + buffer.arrayOffset(), length); }
/*      */     else
/* 1447 */     { int chunkLen = Math.min(length, 8192);
/* 1448 */       buffer.clear().position(position);
/*      */       
/* 1450 */       if (length <= 1024 || !allocator.isDirectBufferPooled())
/* 1451 */       { getBytes(buffer, threadLocalTempArray(chunkLen), 0, chunkLen, out, length); }
/*      */       else
/*      */       
/* 1454 */       { ByteBuf tmpBuf = allocator.heapBuffer(chunkLen);
/*      */         
/* 1456 */         try { byte[] tmp = tmpBuf.array();
/* 1457 */           int offset = tmpBuf.arrayOffset();
/* 1458 */           getBytes(buffer, tmp, offset, chunkLen, out, length); }
/*      */         finally
/* 1460 */         { tmpBuf.release(); }  }  }  } public static String prettyHexDump(ByteBuf buffer) { return prettyHexDump(buffer, buffer.readerIndex(), buffer.readableBytes()); } public static String prettyHexDump(ByteBuf buffer, int offset, int length) { return HexUtil.prettyHexDump(buffer, offset, length); } public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf) { appendPrettyHexDump(dump, buf, buf.readerIndex(), buf.readableBytes()); } public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) { HexUtil.appendPrettyHexDump(dump, buf, offset, length); } private static final class HexUtil {
/*      */     private static final char[] BYTE2CHAR = new char[256]; private static final char[] HEXDUMP_TABLE = new char[1024]; private static final String[] HEXPADDING = new String[16]; private static final String[] HEXDUMP_ROWPREFIXES = new String[4096]; private static final String[] BYTE2HEX = new String[256]; private static final String[] BYTEPADDING = new String[16]; static { char[] DIGITS = "0123456789abcdef".toCharArray(); int i; for (i = 0; i < 256; i++) { HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0xF]; HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0xF]; }  for (i = 0; i < HEXPADDING.length; i++) { int padding = HEXPADDING.length - i; StringBuilder buf = new StringBuilder(padding * 3); for (int j = 0; j < padding; j++) buf.append("   ");  HEXPADDING[i] = buf.toString(); }  for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i++) { StringBuilder buf = new StringBuilder(12); buf.append(StringUtil.NEWLINE); buf.append(Long.toHexString((i << 4) & 0xFFFFFFFFL | 0x100000000L)); buf.setCharAt(buf.length() - 9, '|'); buf.append('|'); HEXDUMP_ROWPREFIXES[i] = buf.toString(); }  for (i = 0; i < BYTE2HEX.length; i++) BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);  for (i = 0; i < BYTEPADDING.length; i++) { int padding = BYTEPADDING.length - i; StringBuilder buf = new StringBuilder(padding); for (int j = 0; j < padding; j++) buf.append(' ');  BYTEPADDING[i] = buf.toString(); }  for (i = 0; i < BYTE2CHAR.length; i++) { if (i <= 31 || i >= 127) { BYTE2CHAR[i] = '.'; } else { BYTE2CHAR[i] = (char)i; }  }  } private static String hexDump(ByteBuf buffer, int fromIndex, int length) { ObjectUtil.checkPositiveOrZero(length, "length"); if (length == 0) return "";  int endIndex = fromIndex + length; char[] buf = new char[length << 1]; int srcIdx = fromIndex; int dstIdx = 0; for (; srcIdx < endIndex; srcIdx++, dstIdx += 2) System.arraycopy(HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);  return new String(buf); } private static String hexDump(byte[] array, int fromIndex, int length) { ObjectUtil.checkPositiveOrZero(length, "length"); if (length == 0) return "";  int endIndex = fromIndex + length; char[] buf = new char[length << 1]; int srcIdx = fromIndex; int dstIdx = 0; for (; srcIdx < endIndex; srcIdx++, dstIdx += 2) System.arraycopy(HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1, buf, dstIdx, 2);  return new String(buf); } private static String prettyHexDump(ByteBuf buffer, int offset, int length) { if (length == 0) return "";  int rows = length / 16 + (((length & 0xF) == 0) ? 0 : 1) + 4; StringBuilder buf = new StringBuilder(rows * 80); appendPrettyHexDump(buf, buffer, offset, length); return buf.toString(); } private static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) { if (MathUtil.isOutOfBounds(offset, length, buf.capacity())) throw new IndexOutOfBoundsException("expected: 0 <= offset(" + offset + ") <= offset + length(" + length + ") <= buf.capacity(" + buf.capacity() + ')');  if (length == 0) return;  dump.append("         +-------------------------------------------------+" + StringUtil.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+"); int startIndex = offset; int fullRows = length >>> 4; int remainder = length & 0xF; for (int row = 0; row < fullRows; row++) { int rowStartIndex = (row << 4) + startIndex; appendHexDumpRowPrefix(dump, row, rowStartIndex); int rowEndIndex = rowStartIndex + 16; int j; for (j = rowStartIndex; j < rowEndIndex; j++) dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);  dump.append(" |"); for (j = rowStartIndex; j < rowEndIndex; j++) dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);  dump.append('|'); }  if (remainder != 0) { int rowStartIndex = (fullRows << 4) + startIndex; appendHexDumpRowPrefix(dump, fullRows, rowStartIndex); int rowEndIndex = rowStartIndex + remainder; int j; for (j = rowStartIndex; j < rowEndIndex; j++) dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);  dump.append(HEXPADDING[remainder]); dump.append(" |"); for (j = rowStartIndex; j < rowEndIndex; j++) dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);  dump.append(BYTEPADDING[remainder]); dump.append('|'); }  dump.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+"); } private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) { if (row < HEXDUMP_ROWPREFIXES.length) { dump.append(HEXDUMP_ROWPREFIXES[row]); } else { dump.append(StringUtil.NEWLINE); dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L)); dump.setCharAt(dump.length() - 9, '|'); dump.append('|'); }  } } static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
/*      */     private static final ObjectPool<ThreadLocalUnsafeDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<ThreadLocalUnsafeDirectByteBuf>() { public ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newObject(ObjectPool.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle) { return new ByteBufUtil.ThreadLocalUnsafeDirectByteBuf(handle); } }
/*      */       ); private final ObjectPool.Handle<ThreadLocalUnsafeDirectByteBuf> handle; static ThreadLocalUnsafeDirectByteBuf newInstance() { ThreadLocalUnsafeDirectByteBuf buf = (ThreadLocalUnsafeDirectByteBuf)RECYCLER.get(); buf.resetRefCnt(); return buf; } private ThreadLocalUnsafeDirectByteBuf(ObjectPool.Handle<ThreadLocalUnsafeDirectByteBuf> handle) { super(UnpooledByteBufAllocator.DEFAULT, 256, 2147483647); this.handle = handle; } protected void deallocate() { if (capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) { super.deallocate(); } else { clear(); this.handle.recycle(this); }  } } static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf {
/*      */     private static final ObjectPool<ThreadLocalDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<ThreadLocalDirectByteBuf>() { public ByteBufUtil.ThreadLocalDirectByteBuf newObject(ObjectPool.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> handle) { return new ByteBufUtil.ThreadLocalDirectByteBuf(handle); } }
/*      */       ); private final ObjectPool.Handle<ThreadLocalDirectByteBuf> handle; static ThreadLocalDirectByteBuf newInstance() { ThreadLocalDirectByteBuf buf = (ThreadLocalDirectByteBuf)RECYCLER.get(); buf.resetRefCnt(); return buf; } private ThreadLocalDirectByteBuf(ObjectPool.Handle<ThreadLocalDirectByteBuf> handle) { super(UnpooledByteBufAllocator.DEFAULT, 256, 2147483647); this.handle = handle; }
/*      */     protected void deallocate() { if (capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) { super.deallocate(); } else { clear(); this.handle.recycle(this); }  } }
/*      */   public static boolean isText(ByteBuf buf, Charset charset) { return isText(buf, buf.readerIndex(), buf.readableBytes(), charset); }
/*      */   public static boolean isText(ByteBuf buf, int index, int length, Charset charset) { ObjectUtil.checkNotNull(buf, "buf"); ObjectUtil.checkNotNull(charset, "charset"); int maxIndex = buf.readerIndex() + buf.readableBytes(); if (index < 0 || length < 0 || index > maxIndex - length) throw new IndexOutOfBoundsException("index: " + index + " length: " + length);  if (charset.equals(CharsetUtil.UTF_8)) return isUtf8(buf, index, length);  if (charset.equals(CharsetUtil.US_ASCII)) return isAscii(buf, index, length);  CharsetDecoder decoder = CharsetUtil.decoder(charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT); try { if (buf.nioBufferCount() == 1) { decoder.decode(buf.nioBuffer(index, length)); } else { ByteBuf heapBuffer = buf.alloc().heapBuffer(length); try { heapBuffer.writeBytes(buf, index, length); decoder.decode(heapBuffer.internalNioBuffer(heapBuffer.readerIndex(), length)); } finally { heapBuffer.release(); }  }  return true; } catch (CharacterCodingException ignore) { return false; }  }
/* 1469 */   private static void getBytes(ByteBuffer inBuffer, byte[] in, int inOffset, int inLen, OutputStream out, int outLen) throws IOException { do { int len = Math.min(inLen, outLen);
/* 1470 */       inBuffer.get(in, inOffset, len);
/* 1471 */       out.write(in, inOffset, len);
/* 1472 */       outLen -= len; }
/* 1473 */     while (outLen > 0); }
/*      */ 
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */