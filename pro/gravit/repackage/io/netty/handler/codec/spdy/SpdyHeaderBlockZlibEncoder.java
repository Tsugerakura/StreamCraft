/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.zip.Deflater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ class SpdyHeaderBlockZlibEncoder
/*     */   extends SpdyHeaderBlockRawEncoder
/*     */ {
/*     */   private final Deflater compressor;
/*     */   private boolean finished;
/*     */   
/*     */   SpdyHeaderBlockZlibEncoder(SpdyVersion spdyVersion, int compressionLevel) {
/*  35 */     super(spdyVersion);
/*  36 */     if (compressionLevel < 0 || compressionLevel > 9) {
/*  37 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/*  40 */     this.compressor = new Deflater(compressionLevel);
/*  41 */     this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
/*     */   }
/*     */   
/*     */   private int setInput(ByteBuf decompressed) {
/*  45 */     int len = decompressed.readableBytes();
/*     */     
/*  47 */     if (decompressed.hasArray()) {
/*  48 */       this.compressor.setInput(decompressed.array(), decompressed.arrayOffset() + decompressed.readerIndex(), len);
/*     */     } else {
/*  50 */       byte[] in = new byte[len];
/*  51 */       decompressed.getBytes(decompressed.readerIndex(), in);
/*  52 */       this.compressor.setInput(in, 0, in.length);
/*     */     } 
/*     */     
/*  55 */     return len;
/*     */   }
/*     */   
/*     */   private ByteBuf encode(ByteBufAllocator alloc, int len) {
/*  59 */     ByteBuf compressed = alloc.heapBuffer(len);
/*  60 */     boolean release = true;
/*     */     try {
/*  62 */       while (compressInto(compressed))
/*     */       {
/*  64 */         compressed.ensureWritable(compressed.capacity() << 1);
/*     */       }
/*  66 */       release = false;
/*  67 */       return compressed;
/*     */     } finally {
/*  69 */       if (release)
/*  70 */         compressed.release(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Guarded by java version check")
/*     */   private boolean compressInto(ByteBuf compressed) {
/*     */     int numBytes;
/*  77 */     byte[] out = compressed.array();
/*  78 */     int off = compressed.arrayOffset() + compressed.writerIndex();
/*  79 */     int toWrite = compressed.writableBytes();
/*     */     
/*  81 */     if (PlatformDependent.javaVersion() >= 7) {
/*  82 */       numBytes = this.compressor.deflate(out, off, toWrite, 2);
/*     */     } else {
/*  84 */       numBytes = this.compressor.deflate(out, off, toWrite);
/*     */     } 
/*  86 */     compressed.writerIndex(compressed.writerIndex() + numBytes);
/*  87 */     return (numBytes == toWrite);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
/*  92 */     if (frame == null) {
/*  93 */       throw new IllegalArgumentException("frame");
/*     */     }
/*     */     
/*  96 */     if (this.finished) {
/*  97 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/*     */     
/* 100 */     ByteBuf decompressed = super.encode(alloc, frame);
/*     */     try {
/* 102 */       if (!decompressed.isReadable()) {
/* 103 */         return Unpooled.EMPTY_BUFFER;
/*     */       }
/*     */       
/* 106 */       int len = setInput(decompressed);
/* 107 */       return encode(alloc, len);
/*     */     } finally {
/* 109 */       decompressed.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void end() {
/* 115 */     if (this.finished) {
/*     */       return;
/*     */     }
/* 118 */     this.finished = true;
/* 119 */     this.compressor.end();
/* 120 */     super.end();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHeaderBlockZlibEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */