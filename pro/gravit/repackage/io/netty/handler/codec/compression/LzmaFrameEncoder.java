/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import lzma.sdk.lzma.Encoder;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufInputStream;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufOutputStream;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToByteEncoder;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LzmaFrameEncoder
/*     */   extends MessageToByteEncoder<ByteBuf>
/*     */ {
/*  41 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(LzmaFrameEncoder.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MEDIUM_DICTIONARY_SIZE = 65536;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MIN_FAST_BYTES = 5;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MEDIUM_FAST_BYTES = 32;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MAX_FAST_BYTES = 273;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_MATCH_FINDER = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_LC = 3;
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_LP = 0;
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_PB = 2;
/*     */ 
/*     */   
/*     */   private final Encoder encoder;
/*     */ 
/*     */   
/*     */   private final byte properties;
/*     */ 
/*     */   
/*     */   private final int littleEndianDictionarySize;
/*     */ 
/*     */   
/*     */   private static boolean warningLogged;
/*     */ 
/*     */ 
/*     */   
/*     */   public LzmaFrameEncoder() {
/*  88 */     this(65536);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LzmaFrameEncoder(int lc, int lp, int pb) {
/*  96 */     this(lc, lp, pb, 65536);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LzmaFrameEncoder(int dictionarySize) {
/* 106 */     this(3, 0, 2, dictionarySize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LzmaFrameEncoder(int lc, int lp, int pb, int dictionarySize) {
/* 113 */     this(lc, lp, pb, dictionarySize, false, 32);
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
/*     */   public LzmaFrameEncoder(int lc, int lp, int pb, int dictionarySize, boolean endMarkerMode, int numFastBytes) {
/* 138 */     if (lc < 0 || lc > 8) {
/* 139 */       throw new IllegalArgumentException("lc: " + lc + " (expected: 0-8)");
/*     */     }
/* 141 */     if (lp < 0 || lp > 4) {
/* 142 */       throw new IllegalArgumentException("lp: " + lp + " (expected: 0-4)");
/*     */     }
/* 144 */     if (pb < 0 || pb > 4) {
/* 145 */       throw new IllegalArgumentException("pb: " + pb + " (expected: 0-4)");
/*     */     }
/* 147 */     if (lc + lp > 4 && 
/* 148 */       !warningLogged) {
/* 149 */       logger.warn("The latest versions of LZMA libraries (for example, XZ Utils) has an additional requirement: lc + lp <= 4. Data which don't follow this requirement cannot be decompressed with this libraries.");
/*     */ 
/*     */       
/* 152 */       warningLogged = true;
/*     */     } 
/*     */     
/* 155 */     if (dictionarySize < 0) {
/* 156 */       throw new IllegalArgumentException("dictionarySize: " + dictionarySize + " (expected: 0+)");
/*     */     }
/* 158 */     if (numFastBytes < 5 || numFastBytes > 273) {
/* 159 */       throw new IllegalArgumentException(String.format("numFastBytes: %d (expected: %d-%d)", new Object[] {
/* 160 */               Integer.valueOf(numFastBytes), Integer.valueOf(5), Integer.valueOf(273)
/*     */             }));
/*     */     }
/*     */     
/* 164 */     this.encoder = new Encoder();
/* 165 */     this.encoder.setDictionarySize(dictionarySize);
/* 166 */     this.encoder.setEndMarkerMode(endMarkerMode);
/* 167 */     this.encoder.setMatchFinder(1);
/* 168 */     this.encoder.setNumFastBytes(numFastBytes);
/* 169 */     this.encoder.setLcLpPb(lc, lp, pb);
/*     */     
/* 171 */     this.properties = (byte)((pb * 5 + lp) * 9 + lc);
/* 172 */     this.littleEndianDictionarySize = Integer.reverseBytes(dictionarySize);
/*     */   }
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
/*     */     ByteBufInputStream byteBufInputStream;
/* 177 */     int length = in.readableBytes();
/* 178 */     InputStream bbIn = null;
/* 179 */     ByteBufOutputStream bbOut = null;
/*     */     try {
/* 181 */       byteBufInputStream = new ByteBufInputStream(in);
/* 182 */       bbOut = new ByteBufOutputStream(out);
/* 183 */       bbOut.writeByte(this.properties);
/* 184 */       bbOut.writeInt(this.littleEndianDictionarySize);
/* 185 */       bbOut.writeLong(Long.reverseBytes(length));
/* 186 */       this.encoder.code((InputStream)byteBufInputStream, (OutputStream)bbOut, -1L, -1L, null);
/*     */     } finally {
/* 188 */       if (byteBufInputStream != null) {
/* 189 */         byteBufInputStream.close();
/*     */       }
/* 191 */       if (bbOut != null) {
/* 192 */         bbOut.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf in, boolean preferDirect) throws Exception {
/* 199 */     int length = in.readableBytes();
/* 200 */     int maxOutputLength = maxOutputBufferLength(length);
/* 201 */     return ctx.alloc().ioBuffer(maxOutputLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int maxOutputBufferLength(int inputLength) {
/*     */     double factor;
/* 209 */     if (inputLength < 200) {
/* 210 */       factor = 1.5D;
/* 211 */     } else if (inputLength < 500) {
/* 212 */       factor = 1.2D;
/* 213 */     } else if (inputLength < 1000) {
/* 214 */       factor = 1.1D;
/* 215 */     } else if (inputLength < 10000) {
/* 216 */       factor = 1.05D;
/*     */     } else {
/* 218 */       factor = 1.02D;
/*     */     } 
/* 220 */     return 13 + (int)(inputLength * factor);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\LzmaFrameEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */